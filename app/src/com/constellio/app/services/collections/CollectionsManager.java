package com.constellio.app.services.collections;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.common.params.ModifiableSolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constellio.app.services.collections.CollectionsManagerRuntimeException.CollectionsManagerRuntimeException_CannotCreateCollectionRecord;
import com.constellio.app.services.collections.CollectionsManagerRuntimeException.CollectionsManagerRuntimeException_CannotMigrateCollection;
import com.constellio.app.services.collections.CollectionsManagerRuntimeException.CollectionsManagerRuntimeException_CannotRemoveCollection;
import com.constellio.app.services.collections.CollectionsManagerRuntimeException.CollectionsManagerRuntimeException_CollectionLanguageMustIncludeSystemMainDataLanguage;
import com.constellio.app.services.collections.CollectionsManagerRuntimeException.CollectionsManagerRuntimeException_CollectionNotFound;
import com.constellio.app.services.collections.CollectionsManagerRuntimeException.CollectionsManagerRuntimeException_CollectionWithGivenCodeAlreadyExists;
import com.constellio.app.services.collections.CollectionsManagerRuntimeException.CollectionsManagerRuntimeException_InvalidCode;
import com.constellio.app.services.collections.CollectionsManagerRuntimeException.CollectionsManagerRuntimeException_InvalidLanguage;
import com.constellio.app.services.extensions.ConstellioModulesManagerImpl;
import com.constellio.app.services.migrations.MigrationServices;
import com.constellio.app.services.systemSetup.SystemGlobalConfigsManager;
import com.constellio.data.dao.dto.records.RecordsFlushing;
import com.constellio.data.dao.dto.records.TransactionDTO;
import com.constellio.data.dao.managers.StatefulService;
import com.constellio.data.dao.managers.config.ConfigManager;
import com.constellio.data.dao.managers.config.ConfigManagerException.OptimisticLockingConfiguration;
import com.constellio.data.dao.services.bigVault.RecordDaoException.OptimisticLocking;
import com.constellio.data.dao.services.factories.DataLayerFactory;
import com.constellio.data.utils.Delayed;
import com.constellio.model.entities.Language;
import com.constellio.model.entities.Taxonomy;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.Collection;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchema;
import com.constellio.model.entities.schemas.MetadataSchemaTypes;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.entities.security.global.SolrUserCredential;
import com.constellio.model.services.collections.CollectionsListManager;
import com.constellio.model.services.factories.ModelLayerFactory;
import com.constellio.model.services.factories.SystemCollectionListener;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.records.RecordServicesException;
import com.constellio.model.services.records.RecordServicesRuntimeException;
import com.constellio.model.services.records.SchemasRecordsServices;
import com.constellio.model.services.records.cache.CacheConfig;
import com.constellio.model.services.records.cache.RecordsCache;
import com.constellio.model.services.taxonomies.TaxonomiesManager;

public class CollectionsManager implements StatefulService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CollectionsManager.class);

	private final Delayed<MigrationServices> migrationServicesDelayed;

	private final CollectionsListManager collectionsListManager;

	private final ConstellioModulesManagerImpl constellioModulesManager;

	private final ModelLayerFactory modelLayerFactory;

	private final DataLayerFactory dataLayerFactory;

	private final SystemGlobalConfigsManager systemGlobalConfigsManager;

	private Map<String, List<String>> collectionLanguagesCache = new HashMap<>();

	private List<String> newDisabledCollections = new ArrayList<>();

	public CollectionsManager(ModelLayerFactory modelLayerFactory, ConstellioModulesManagerImpl constellioModulesManager,
			Delayed<MigrationServices> migrationServicesDelayed, SystemGlobalConfigsManager systemGlobalConfigsManager) {
		this.modelLayerFactory = modelLayerFactory;
		this.constellioModulesManager = constellioModulesManager;
		this.collectionsListManager = modelLayerFactory.getCollectionsListManager();
		this.dataLayerFactory = modelLayerFactory.getDataLayerFactory();
		this.migrationServicesDelayed = migrationServicesDelayed;
		this.systemGlobalConfigsManager = systemGlobalConfigsManager;
	}

	@Override
	public void initialize() {
		if (!collectionsListManager.getCollections().contains(Collection.SYSTEM_COLLECTION)) {
			createSystemCollection();

		}
		if (!modelLayerFactory.getMetadataSchemasManager().getSchemaTypes(Collection.SYSTEM_COLLECTION)
				.hasType(SolrUserCredential.SCHEMA_TYPE)) {
			initializeSystemCollection();
		}

		disableCollectionsWithoutSchemas();

		SchemasRecordsServices schemas = new SchemasRecordsServices(Collection.SYSTEM_COLLECTION, modelLayerFactory);
		if (!schemas.getTypes().hasType(SolrUserCredential.SCHEMA_TYPE)) {
			for (SystemCollectionListener listener : modelLayerFactory.getSystemCollectionListeners()) {
				listener.systemCollectionCreated();
			}
		}

		schemas = new SchemasRecordsServices(Collection.SYSTEM_COLLECTION, modelLayerFactory);
		RecordsCache cache = modelLayerFactory.getRecordsCaches().getCache(Collection.SYSTEM_COLLECTION);

		if (schemas.getTypes().hasType(SolrUserCredential.SCHEMA_TYPE)) {
			cache.configureCache(CacheConfig.permanentCache(schemas.credentialSchemaType()));
			cache.configureCache(CacheConfig.permanentCache(schemas.globalGroupSchemaType()));
		}

	}

	private void disableCollectionsWithoutSchemas() {

		for (String collection : collectionsListManager.getCollections()) {
			try {
				MetadataSchemaTypes types = modelLayerFactory.getMetadataSchemasManager().getSchemaTypes(collection);
				types.getSchemaType(User.SCHEMA_TYPE);

			} catch (Exception e) {
				collectionsListManager.remove(collection);
				newDisabledCollections.add(collection);
				LOGGER.warn("Collection '" + collection + "' has been disabled since it have no schemas "
						+ "(probably a problem during the creation of the collection)");
			}
		}
	}

	private void createSystemCollection() {
		String mainDataLanguage = modelLayerFactory.getConfiguration().getMainDataLanguage();
		List<String> languages = asList(mainDataLanguage);
		createCollectionInCurrentVersion(Collection.SYSTEM_COLLECTION, languages);
	}

	private void initializeSystemCollection() {
		for (SystemCollectionListener listener : modelLayerFactory.getSystemCollectionListeners()) {
			listener.systemCollectionCreated();
		}
	}

	public List<String> getCollectionCodes() {
		return collectionsListManager.getCollections();
	}

	public List<String> getCollectionCodesExcludingSystem() {
		return collectionsListManager.getCollectionsExcludingSystem();
	}

	void addGlobalGroupsInCollection(String code) {
		modelLayerFactory.newUserServices().addGlobalGroupsInCollection(code);
	}

	public Record createCollectionRecordWithCode(String code, String name, List<String> languages) {
		RecordServices recordServices = modelLayerFactory.newRecordServices();
		Record record = recordServices.newRecordWithSchema(collectionSchema(code));
		record.set(collectionCodeMetadata(code), code);
		record.set(collectionNameMetadata(code), name);
		record.set(Schemas.TITLE, name);
		record.set(collectionLanguages(code), languages);
		try {
			recordServices.add(record);
			return record;
		} catch (RecordServicesException e) {
			throw new CollectionsManagerRuntimeException_CannotCreateCollectionRecord(code, e);
		}
	}

	Record createCollectionRecordWithCode(String code, List<String> languages) {
		return createCollectionRecordWithCode(code, code, languages);
	}

	MetadataSchema collectionSchema(String collection) {
		MetadataSchemaTypes types = modelLayerFactory.getMetadataSchemasManager().getSchemaTypes(collection);
		return types.getSchema(Collection.SCHEMA_TYPE + "_default");
	}

	private Metadata collectionCodeMetadata(String collection) {
		return collectionSchema(collection).getMetadata(Collection.CODE);
	}

	private Metadata collectionNameMetadata(String collection) {
		return collectionSchema(collection).getMetadata(Collection.NAME);
	}

	private Metadata collectionLanguages(String collection) {
		return collectionSchema(collection).getMetadata(Collection.LANGUAGES);
	}

	public void createCollectionConfigs(String code) {
		modelLayerFactory.getMetadataSchemasManager().createCollectionSchemas(code);
		modelLayerFactory.getTaxonomiesManager().createCollectionTaxonomies(code);
		modelLayerFactory.getAuthorizationDetailsManager().createCollectionAuthorizationDetail(code);
		modelLayerFactory.getRolesManager().createCollectionRole(code);
		modelLayerFactory.getSearchBoostManager().createCollectionSearchBoost(code);
	}

	public Collection getCollection(String code) {
		try {
			Record record = modelLayerFactory.newRecordServices().getDocumentById(code);
			MetadataSchemaTypes types = modelLayerFactory.getMetadataSchemasManager().getSchemaTypes(code);
			return new Collection(record, types);
		} catch (RecordServicesRuntimeException.NoSuchRecordWithId e) {
			throw new CollectionsManagerRuntimeException_CollectionNotFound(code, e);
		}
	}

	public void deleteCollection(final String collection) {
		ConfigManager configManager = dataLayerFactory.getConfigManager();
		removeCollectionFromUserCredentials(collection);
		removeCollectionFromGlobalGroups(collection);
		removeFromCollectionsListManager(collection);
		removeCollectionFromBigVault(collection);
		removeCollectionFromVersionProperties(collection, configManager);
		removeRemoveAllConfigsOfCollection(collection, configManager);
		removeCollectionFromCache(collection);
	}

	private void removeCollectionFromCache(String collection) {
		modelLayerFactory.getRecordsCaches().invalidate(collection);
	}

	private void removeRemoveAllConfigsOfCollection(final String collection, ConfigManager configManager) {
		configManager.deleteAllConfigsIn("/" + collection);
	}

	private void removeCollectionFromBigVault(final String collection) {
		TransactionDTO transactionDTO = newTransactionDTO();
		ModifiableSolrParams params = newModifiableSolrParams();
		params.set("q", "collection_s:" + collection);
		transactionDTO = transactionDTO.withDeletedByQueries(params);
		try {
			dataLayerFactory.newRecordDao().execute(transactionDTO);
		} catch (OptimisticLocking optimisticLocking) {
			throw new CollectionsManagerRuntimeException_CannotRemoveCollection(collection, optimisticLocking);
		}
	}

	private void removeCollectionFromVersionProperties(final String collection, ConfigManager configManager) {
		constellioModulesManager.removeCollectionFromVersionProperties(collection, configManager);
	}

	private void removeCollectionFromUserCredentials(final String collection) {
		modelLayerFactory.getUserCredentialsManager().removeCollection(collection);
	}

	private void removeCollectionFromGlobalGroups(final String collection) {
		modelLayerFactory.getGlobalGroupsManager().removeCollection(collection);
	}

	private void removeFromCollectionsListManager(final String collection) {
		collectionsListManager.remove(collection);
	}

	// private PropertiesAlteration newRemoveCollectionPropertiesAlteration(final String collection) {
	// return new PropertiesAlteration() {
	// @Override
	// public void alter(Map<String, String> properties) {
	// if (properties.containsKey(collection + "_version")) {
	// properties.remove(collection + "_version");
	// }
	// }
	// };
	// }

	TransactionDTO newTransactionDTO() {
		return new TransactionDTO(RecordsFlushing.NOW);
	}

	ModifiableSolrParams newModifiableSolrParams() {
		return new ModifiableSolrParams();
	}

	public List<String> getCollectionLanguages(final String collection) {

		if (Collection.SYSTEM_COLLECTION.equals(collection)) {
			return asList(modelLayerFactory.getConfiguration().getMainDataLanguage());
		}

		List<String> collectionLanguages = collectionLanguagesCache.get(collection);

		if (collectionLanguages == null) {
			try {
				collectionLanguages = getCollection(collection).getLanguages();
				collectionLanguagesCache.put(collection, collectionLanguages);
			} catch (CollectionsManagerRuntimeException_CollectionNotFound e) {
				LOGGER.debug("Collection '" + collection + "' not found.", e);
				return Collections.emptyList();
			}
		}
		return collectionLanguages;
	}

	@Override
	public void close() {
		// No finalization required.
	}

	public Record createCollectionInCurrentVersion(String code, String name, List<String> languages) {
		return createCollectionInVersion(code, name, languages, null);
	}

	public Record createCollectionInCurrentVersion(String code, List<String> languages) {
		return createCollectionInVersion(code, languages, null);
	}

	public Record createCollectionInVersion(String code, List<String> languages, String version) {
		return createCollectionInVersion(code, code, languages, version);
	}

	public Record createCollectionInVersion(String code, String name, List<String> languages, String version) {
		prepareCollectionCreationAndGetInvalidModules(code, name, languages, version);
		return crateCollectionAfterPrepare(code, name, languages);
	}

	private Record crateCollectionAfterPrepare(String code, String name, List<String> languages) {
		Record collectionRecord = createCollectionRecordWithCode(code, name, languages);
		if (!code.equals(Collection.SYSTEM_COLLECTION)) {
			addGlobalGroupsInCollection(code);
		}
		initializeCollection(code);
		return collectionRecord;
	}

	private Set<String> prepareCollectionCreationAndGetInvalidModules(String code, String name,
			List<String> languages, String version) {
		validateCode(code);

		boolean reindexingRequired = systemGlobalConfigsManager.isReindexingRequired();
		if (collectionsListManager.getCollections().contains(code)) {
			throw new CollectionsManagerRuntimeException_CollectionWithGivenCodeAlreadyExists(code);
		}

		String mainDataLanguage = modelLayerFactory.getConfiguration().getMainDataLanguage();
		if (!languages.contains(mainDataLanguage)) {
			throw new CollectionsManagerRuntimeException_CollectionLanguageMustIncludeSystemMainDataLanguage(
					mainDataLanguage);
		}

		for (String language : languages) {
			if (!Language.isSupported(language)) {
				throw new CollectionsManagerRuntimeException_InvalidLanguage(language);
			}
		}

		collectionLanguagesCache.put(code, languages);
		createCollectionConfigs(code);
		collectionsListManager.addCollection(code, languages);
		Set<String> returnList = new HashSet<>();
		try {
			returnList.addAll(migrationServicesDelayed.get().migrate(code, version, true));
		} catch (OptimisticLockingConfiguration optimisticLockingConfiguration) {
			throw new CollectionsManagerRuntimeException_CannotMigrateCollection(code, version, optimisticLockingConfiguration);
		} finally {
			systemGlobalConfigsManager.setReindexingRequired(reindexingRequired);
		}
		return returnList;
	}

	public void validateCode(String code) {
		if (!Collection.SYSTEM_COLLECTION.equals(code)) {
			String pattern = "[a-zA-Z]([a-zA-Z0-9])+";
			if (code == null || !code.matches(pattern)) {
				throw new CollectionsManagerRuntimeException_InvalidCode(code);
			}
		}
	}

	public Set<String> initializeCollectionsAndGetInvalidModules() {
		Set<String> returnList = new HashSet<>();
		for (String collection : getCollectionCodes()) {
			returnList.addAll(constellioModulesManager.startModules(collection));
			initializeCollection(collection);
		}
		return returnList;
	}

	public void initializeModulesResources() {
		Set<String> returnList = new HashSet<>();
		for (String collection : getCollectionCodes()) {
			constellioModulesManager.initializePluginResources(collection);
		}
	}

	void initializeCollection(String collection) {
		RecordsCache cache = modelLayerFactory.getRecordsCaches().getCache(collection);
		SchemasRecordsServices core = new SchemasRecordsServices(collection, modelLayerFactory);
		cache.configureCache(CacheConfig.permanentCache(core.userSchemaType()));
		cache.configureCache(CacheConfig.permanentCache(core.groupSchemaType()));
		cache.configureCache(CacheConfig.permanentCache(core.collectionSchemaType()));
		cache.configureCache(CacheConfig.permanentCache(core.facetSchemaType()));

		TaxonomiesManager taxonomiesManager = modelLayerFactory.getTaxonomiesManager();
		MetadataSchemaTypes types = modelLayerFactory.getMetadataSchemasManager().getSchemaTypes(collection);
		for (Taxonomy taxonomy : taxonomiesManager.getEnabledTaxonomies(collection)) {
			for (String schemaType : taxonomy.getSchemaTypes()) {
				cache.configureCache(CacheConfig.permanentCache(types.getSchemaType(schemaType)));
			}
		}
	}
}
