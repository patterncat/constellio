package com.constellio.app.modules.es;

import static com.constellio.app.extensions.api.scripts.Scripts.registerScript;
import static com.constellio.app.modules.es.model.connectors.ConnectorType.CODE_HTTP;
import static com.constellio.app.modules.es.model.connectors.ConnectorType.CODE_LDAP;
import static com.constellio.app.modules.es.model.connectors.ConnectorType.CODE_SMB;
import static com.constellio.model.services.records.cache.CacheConfig.permanentCache;
import static com.constellio.model.services.records.cache.CacheConfig.permanentCacheNotLoadedInitially;
import static com.constellio.model.services.records.cache.CacheConfig.permanentEssentialMetadatasCacheNotLoadedInitially;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.constellio.app.entities.modules.ComboMigrationScript;
import com.constellio.app.entities.modules.InstallableSystemModule;
import com.constellio.app.entities.modules.MigrationScript;
import com.constellio.app.entities.modules.ModuleWithComboMigration;
import com.constellio.app.entities.navigation.NavigationConfig;
import com.constellio.app.extensions.AppLayerCollectionExtensions;
import com.constellio.app.extensions.treenode.TreeNodeAppExtension;
import com.constellio.app.modules.es.connectors.http.ConnectorHttpUtilsServices;
import com.constellio.app.modules.es.connectors.ldap.ConnectorLDAPUtilsServices;
import com.constellio.app.modules.es.connectors.smb.SMBConnectorUtilsServices;
import com.constellio.app.modules.es.constants.ESPermissionsTo;
import com.constellio.app.modules.es.extensions.ESRecordAppExtension;
import com.constellio.app.modules.es.extensions.ESRecordExtension;
import com.constellio.app.modules.es.extensions.ESRecordNavigationExtension;
import com.constellio.app.modules.es.extensions.ESSMBParentConnectorUrlCriterionExtension;
import com.constellio.app.modules.es.extensions.ESSearchPageExtension;
import com.constellio.app.modules.es.extensions.ESTaxonomyPageExtension;
import com.constellio.app.modules.es.extensions.api.ESModuleExtensions;
import com.constellio.app.modules.es.migrations.*;
import com.constellio.app.modules.es.migrations.ESMigrationTo7_4_1;
import com.constellio.app.modules.es.migrations.ESMigrationTo7_4_2;
import com.constellio.app.modules.es.model.connectors.http.ConnectorHttpInstance;
import com.constellio.app.modules.es.model.connectors.ldap.ConnectorLDAPInstance;
import com.constellio.app.modules.es.model.connectors.smb.ConnectorSmbFolder;
import com.constellio.app.modules.es.model.connectors.smb.ConnectorSmbInstance;
import com.constellio.app.modules.es.navigation.ESNavigationConfiguration;
import com.constellio.app.modules.es.scripts.RestoreConnectorTypes;
import com.constellio.app.modules.es.services.ConnectorManager;
import com.constellio.app.modules.es.services.ESSchemasRecordsServices;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.model.entities.configs.SystemConfiguration;
import com.constellio.model.entities.records.wrappers.Facet;
import com.constellio.model.extensions.ModelLayerCollectionExtensions;
import com.constellio.model.services.factories.ModelLayerFactory;
import com.constellio.model.services.records.cache.CacheConfig;
import com.constellio.model.services.records.cache.RecordsCache;

public class ConstellioESModule implements InstallableSystemModule, ModuleWithComboMigration {
	public static final String ID = "es";
	public static final String NAME = "Constellio Enterprise Search (beta)";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getPublisher() {
		return "Constellio";
	}

	@Override
	public List<MigrationScript> getMigrationScripts() {
		return Arrays.asList(
				new ESMigrationTo5_1_6(),
				new ESMigrationTo6_1(),
				new ESMigrationTo6_2(),
				new ESMigrationTo6_4(),
				new ESMigrationTo6_5_42(),
				new ESMigrationTo6_5_58(),
				new ESMigrationTo7_1_3(),
				new ESMigrationTo7_4_1(),
				new ESMigrationTo7_4_2(),
				new ESMigrationTo7_4_3(),
				new ESMigrationTo7_5()
		);
	}

	@Override
	public List<SystemConfiguration> getConfigurations() {
		return Collections.unmodifiableList(ESConfigs.configurations);
	}

	@Override
	public Map<String, List<String>> getPermissions() {
		return ESPermissionsTo.PERMISSIONS.getGrouped();
	}

	@Override
	public List<String> getRolesForCreator() {
		return new ArrayList<>();
	}

	@Override
	public boolean isComplementary() {
		return false;
	}

	@Override
	public List<String> getDependencies() {
		return new ArrayList<>();
	}

	@Override
	public void configureNavigation(NavigationConfig config) {
		ESNavigationConfiguration.configureNavigation(config);
	}

	@Override
	public void start(String collection, AppLayerFactory appLayerFactory) {

		registerManagers(collection, appLayerFactory);
		registerScript(new RestoreConnectorTypes(appLayerFactory, collection));

		setupModelLayerExtensions(collection, appLayerFactory);
		setupAppLayerExtensions(collection, appLayerFactory);

	}

	private void registerManagers(String collection, AppLayerFactory appLayerFactory) {
		ESSchemasRecordsServices es = new ESSchemasRecordsServices(collection, appLayerFactory);
		ConnectorManager connectorManager = new ConnectorManager(es);
		appLayerFactory.registerManager(collection, ConstellioESModule.ID, ConnectorManager.ID, connectorManager);

		connectorManager.register(CODE_HTTP, ConnectorHttpInstance.SCHEMA_CODE,
				new ConnectorHttpUtilsServices(collection, appLayerFactory));
		connectorManager
				.register(CODE_SMB, ConnectorSmbInstance.SCHEMA_CODE, new SMBConnectorUtilsServices(collection, appLayerFactory));
		connectorManager.register(CODE_LDAP, ConnectorLDAPInstance.SCHEMA_CODE,
				new ConnectorLDAPUtilsServices(collection, appLayerFactory));
	}

	@Override
	public void stop(String collection, AppLayerFactory appLayerFactory) {
	}

	@Override
	public void addDemoData(String collection, AppLayerFactory appLayerFactory) {
		// ES provides no demo data for now
	}

	private void setupAppLayerExtensions(String collection, AppLayerFactory appLayerFactory) {
		AppLayerCollectionExtensions extensions = appLayerFactory.getExtensions()
				.forCollection(collection);
		extensions.moduleExtensionsMap.put(ID, new ESModuleExtensions());
		extensions.taxonomyAccessExtensions.add(new ESTaxonomyPageExtension(collection));
		extensions.recordAppExtensions.add(new ESRecordAppExtension(collection, appLayerFactory));
		extensions.recordNavigationExtensions.add(new ESRecordNavigationExtension(collection, appLayerFactory));
		extensions.searchPageExtensions.add(new ESSearchPageExtension(appLayerFactory));
		extensions.treeNodeAppExtension.add(new TreeNodeAppExtension());
		extensions.searchCriterionExtensions.add(new ESSMBParentConnectorUrlCriterionExtension(appLayerFactory, collection));
	}

	private void setupModelLayerExtensions(String collection, AppLayerFactory appLayerFactory) {
		ModelLayerCollectionExtensions extensions = appLayerFactory.getModelLayerFactory()
				.getExtensions()
				.forCollection(collection);
		ESSchemasRecordsServices es = new ESSchemasRecordsServices(collection, appLayerFactory);
		ModelLayerFactory modelLayerFactory = appLayerFactory.getModelLayerFactory();
		RecordsCache recordsCache = modelLayerFactory.getRecordsCaches()
				.getCache(collection);

		recordsCache.removeCache(ConnectorSmbFolder.SCHEMA_TYPE);
		recordsCache.configureCache(permanentCache(es.connectorInstance.schemaType()));
		recordsCache.configureCache(permanentCacheNotLoadedInitially(ConnectorSmbFolder.SCHEMA_TYPE,
				es.connectorSmbFolder.schemaType().getAllMetadatas()));
		recordsCache.configureCache(permanentEssentialMetadatasCacheNotLoadedInitially(es.connectorSmbDocument.schemaType()));
		recordsCache.configureCache(permanentCache(es.connectorType.schemaType()));

		if (!recordsCache.isConfigured(es.authorizationDetails.schemaType())) {
			recordsCache.configureCache(CacheConfig.permanentCache(es.authorizationDetails.schemaType()));
		}

		if (!recordsCache.isConfigured(Facet.SCHEMA_TYPE)) {
			recordsCache.configureCache(CacheConfig.permanentCache(es.facet.schemaType()));
		}

		extensions.recordExtensions.add(new ESRecordExtension(es));
	}

	@Override
	public void start(AppLayerFactory appLayerFactory) {
		ESNavigationConfiguration.configureNavigation(appLayerFactory.getNavigatorConfigurationService());

	}

	@Override
	public void stop(AppLayerFactory appLayerFactory) {

	}

	@Override
	public ComboMigrationScript getComboMigrationScript() {
		return new ESMigrationCombo();
	}
}
