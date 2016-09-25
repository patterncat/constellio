package com.constellio.app.services.schemas.bulkImport;

import static com.constellio.app.services.schemas.bulkImport.BulkImportParams.ImportErrorsBehavior.CONTINUE_FOR_RECORD_OF_SAME_TYPE;
import static com.constellio.app.services.schemas.bulkImport.BulkImportParams.ImportErrorsBehavior.STOP_ON_FIRST_ERROR;
import static com.constellio.app.services.schemas.bulkImport.Resolver.toResolver;
import static com.constellio.data.utils.LangUtils.replacingLiteral;
import static com.constellio.data.utils.ThreadUtils.iterateOverRunningTaskInParallel;
import static com.constellio.model.entities.schemas.MetadataValueType.STRING;
import static com.constellio.model.entities.schemas.Schemas.LEGACY_ID;
import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.from;
import static java.io.File.separator;
import static java.util.Arrays.asList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constellio.app.services.schemas.bulkImport.BulkImportParams.ImportErrorsBehavior;
import com.constellio.app.services.schemas.bulkImport.BulkImportParams.ImportValidationErrorsBehavior;
import com.constellio.app.services.schemas.bulkImport.data.ImportData;
import com.constellio.app.services.schemas.bulkImport.data.ImportDataIterator;
import com.constellio.app.services.schemas.bulkImport.data.ImportDataOptions;
import com.constellio.app.services.schemas.bulkImport.data.ImportDataProvider;
import com.constellio.data.dao.dto.records.RecordsFlushing;
import com.constellio.data.io.services.facades.IOServices;
import com.constellio.data.io.streamFactories.StreamFactory;
import com.constellio.data.utils.BatchBuilderIterator;
import com.constellio.data.utils.Factory;
import com.constellio.data.utils.LangUtils.StringReplacer;
import com.constellio.data.utils.ThreadUtils.IteratorElementTask;
import com.constellio.model.entities.Language;
import com.constellio.model.entities.records.Content;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.Transaction;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchema;
import com.constellio.model.entities.schemas.MetadataSchemaType;
import com.constellio.model.entities.schemas.MetadataSchemaTypes;
import com.constellio.model.entities.schemas.MetadataSchemasRuntimeException.NoSuchSchemaType;
import com.constellio.model.entities.schemas.MetadataValueType;
import com.constellio.model.extensions.ModelLayerCollectionExtensions;
import com.constellio.model.extensions.events.recordsImport.BuildParams;
import com.constellio.model.extensions.events.recordsImport.ValidationParams;
import com.constellio.model.frameworks.validation.DecoratedValidationsErrors;
import com.constellio.model.frameworks.validation.ValidationError;
import com.constellio.model.frameworks.validation.ValidationErrors;
import com.constellio.model.frameworks.validation.ValidationException;
import com.constellio.model.services.contents.BulkUploader;
import com.constellio.model.services.contents.BulkUploaderRuntimeException;
import com.constellio.model.services.contents.ContentManager;
import com.constellio.model.services.contents.ContentVersionDataSummary;
import com.constellio.model.services.factories.ModelLayerFactory;
import com.constellio.model.services.records.ContentImport;
import com.constellio.model.services.records.ContentImportVersion;
import com.constellio.model.services.records.RecordCachesServices;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.records.RecordServicesException;
import com.constellio.model.services.records.bulkImport.ProgressionHandler;
import com.constellio.model.services.schemas.MetadataSchemasManager;
import com.constellio.model.services.search.SearchServices;
import com.constellio.model.utils.EnumWithSmallCodeUtils;

public class RecordsImportServicesExecutor {

	public static StringReplacer IMPORTED_FILEPATH_CLEANER = replacingLiteral("/", separator).replacingLiteral("\\", separator);
	public static final String INVALID_SCHEMA_TYPE_CODE = "invalidSchemaTypeCode";
	public static final String LEGACY_ID_LOCAL_CODE = LEGACY_ID.getLocalCode();
	static final List<String> ALL_BOOLEAN_YES = asList("o", "y", "t", "oui", "vrai", "yes", "true", "1");
	static final List<String> ALL_BOOLEAN_NO = asList("n", "f", "non", "faux", "no", "false", "0");
	private static final String IMPORT_URL_INPUTSTREAM_NAME = "RecordsImportServices-ImportURL";
	private static final Logger LOGGER = LoggerFactory.getLogger(RecordsImportServices.class);
	private static final String CYCLIC_DEPENDENCIES_ERROR = "cyclicDependencies";
	private static final String CONTENT_NOT_FOUND_ERROR = "contentNotFound";
	private static final String CONTENT_NOT_IMPORTED_ERROR = "contentNotImported";

	private ModelLayerFactory modelLayerFactory;
	private MetadataSchemasManager schemasManager;
	private RecordServices recordServices;
	private ContentManager contentManager;
	private IOServices ioServices;
	private SearchServices searchServices;
	private URLResolver urlResolver;
	private final ImportDataProvider importDataProvider;
	private final BulkImportProgressionListener bulkImportProgressionListener;
	private final User user;
	private final List<String> collections;
	private final BulkImportParams params;
	private final ProgressionHandler progressionHandler;
	private final String collection;
	private ModelLayerCollectionExtensions extensions;
	private MetadataSchemaTypes types;
	private Language language;
	private BulkImportResults importResults;
	private Map<String, Factory<ContentVersionDataSummary>> importedFilesMap;
	private SkippedRecordsImport skippedRecordsImport;
	ResolverCache resolverCache;

	private static class TypeImportContext {
		List<String> uniqueMetadatas;
		String schemaType;
		AtomicInteger addUpdateCount;
		boolean recordsBeforeImport;
	}

	private static class TypeBatchImportContext {
		List<ImportData> batch;
		Transaction transaction;
		ImportDataOptions options;
		BulkUploader bulkUploader;
	}

	//
	public RecordsImportServicesExecutor(ModelLayerFactory modelLayerFactory, URLResolver urlResolver,
			ImportDataProvider importDataProvider, final BulkImportProgressionListener bulkImportProgressionListener,
			final User user, List<String> collections, BulkImportParams params) {
		this.modelLayerFactory = modelLayerFactory;
		this.importDataProvider = importDataProvider;
		this.bulkImportProgressionListener = bulkImportProgressionListener;
		this.user = user;
		this.collections = collections;
		this.params = params;
		schemasManager = modelLayerFactory.getMetadataSchemasManager();
		recordServices = modelLayerFactory.newRecordServices();
		searchServices = modelLayerFactory.newSearchServices();
		contentManager = modelLayerFactory.getContentManager();
		ioServices = modelLayerFactory.getIOServicesFactory().newIOServices();
		this.skippedRecordsImport = new SkippedRecordsImport();
		this.urlResolver = urlResolver;
		this.progressionHandler = new ProgressionHandler(bulkImportProgressionListener);
		this.collection = user.getCollection();
		this.importResults = new BulkImportResults();
	}

	public BulkImportResults bulkImport()
			throws RecordsImportServicesRuntimeException, ValidationException {

		try {
			initialize();
			ValidationErrors errors = new ValidationErrors();
			validate(errors);
			return run(errors);
		} finally {
			importDataProvider.close();
		}
	}

	RecordsImportServicesExecutor initialize() {
		language = Language.withCode(user.getLoginLanguageCode());
		if (language == null) {
			language = Language.withCode(modelLayerFactory.getConfiguration().getMainDataLanguage());
		}

		new RecordCachesServices(modelLayerFactory).loadCachesIn(collection);
		importDataProvider.initialize();
		extensions = modelLayerFactory.getExtensions().forCollection(collection);
		types = schemasManager.getSchemaTypes(collection);
		resolverCache = new ResolverCache(modelLayerFactory.newRecordServices(), searchServices, types, importDataProvider);

		return this;
	}

	private BulkImportResults run(ValidationErrors errors)
			throws ValidationException {

		importedFilesMap = new HashMap<>();
		for (Map.Entry<String, Factory<ContentVersionDataSummary>> entry : contentManager.getImportedFilesMap().entrySet()) {
			importedFilesMap.put(entry.getKey(), entry.getValue());
		}
		for (String schemaType : getImportedSchemaTypes()) {

			TypeImportContext context = new TypeImportContext();
			context.schemaType = schemaType;
			context.recordsBeforeImport = searchServices.hasResults(from(types.getSchemaType(schemaType)).returnAll());
			progressionHandler.beforeImportOf(schemaType);
			context.uniqueMetadatas = types.getSchemaType(schemaType).getAllMetadatas().onlyWithType(STRING).onlyUniques()
					.toLocalCodesList();
			int previouslySkipped = 0;
			context.addUpdateCount = new AtomicInteger();
			boolean typeImportFinished = false;

			while (!typeImportFinished) {

				ImportDataIterator importDataIterator = importDataProvider.newDataIterator(schemaType);
				int skipped;
				if (params.getThreads() == 1) {
					skipped = bulkImport(context, importDataIterator, errors);
				} else {
					skipped = bulkImportInParallel(context, importDataIterator, errors);
				}
				if (skipped > 0 && skipped == previouslySkipped) {

					if (errors.isEmpty()) {
						Set<String> cyclicDependentIds = resolverCache.getNotYetImportedLegacyIds(schemaType);
						addCyclicDependenciesValidationError(errors, types.getSchemaType(schemaType), cyclicDependentIds);

					}

					throwIfNonEmpty(errors);
				}
				if (skipped == 0) {
					typeImportFinished = true;
				}
				previouslySkipped = skipped;
			}
			if (params.importErrorsBehavior == STOP_ON_FIRST_ERROR
					|| params.importErrorsBehavior == CONTINUE_FOR_RECORD_OF_SAME_TYPE) {
				throwIfNonEmpty(errors);
			}
		}

		progressionHandler.onImportFinished();
		throwIfNonEmptyErrorOrWarnings(errors);

		return importResults;
	}

	private void throwIfNonEmptyErrorOrWarnings(ValidationErrors errors)
			throws ValidationException {
		if (!errors.isEmptyErrorAndWarnings()) {
			skippedRecordsImport.addWarningForSkippedRecordsBecauseOfDependencies(language, types, errors);
			errors.throwIfNonEmptyErrorOrWarnings();
		}
	}

	private void throwIfNonEmpty(ValidationErrors errors)
			throws ValidationException {
		if (!errors.isEmpty()) {
			skippedRecordsImport.addWarningForSkippedRecordsBecauseOfDependencies(language, types, errors);
			errors.throwIfNonEmpty();
		}
	}

	private void addCyclicDependenciesValidationError(ValidationErrors errors, MetadataSchemaType schemaType,
			Set<String> cyclicDependentIds) {

		List<String> ids = new ArrayList<>(cyclicDependentIds);
		Collections.sort(ids);

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("prefix", schemaType.getLabel(language) + " : ");

		parameters.put("cyclicDependentIds", StringUtils.join(ids, ", "));
		parameters.put("schemaType", schemaType.getCode());

		errors.add(RecordsImportServices.class, CYCLIC_DEPENDENCIES_ERROR, parameters);
	}

	int bulkImport(TypeImportContext typeImportContext, ImportDataIterator importDataIterator, ValidationErrors errors)
			throws ValidationException {

		int skipped = 0;
		Iterator<List<ImportData>> importDataBatches = new BatchBuilderIterator<>(importDataIterator, params.getBatchSize());
		while (importDataBatches.hasNext()) {
			try {
				TypeBatchImportContext typeBatchImportContext = newTypeBatchImportContext(typeImportContext,
						importDataIterator.getOptions(), importDataBatches.next());
				skipped += importBatch(typeImportContext, typeBatchImportContext, errors);
				recordServices.executeHandlingImpactsAsync(typeBatchImportContext.transaction);
			} catch (RecordServicesException e) {
				while (importDataBatches.hasNext()) {
					importDataBatches.next();
				}
				throw new RuntimeException(e);
			}

		}

		return skipped;
	}

	private TypeBatchImportContext newTypeBatchImportContext(TypeImportContext typeImportContext,
			ImportDataOptions options, List<ImportData> batch) {
		TypeBatchImportContext typeBatchImportContext = new TypeBatchImportContext();
		typeBatchImportContext.batch = batch;
		typeBatchImportContext.transaction = new Transaction();
		typeBatchImportContext.transaction.getRecordUpdateOptions().setSkipReferenceValidation(true);
		typeBatchImportContext.options = options;
		if (!typeImportContext.recordsBeforeImport) {
			typeBatchImportContext.transaction.getRecordUpdateOptions().setUnicityValidationsEnabled(false);
		}
		return typeBatchImportContext;
	}

	int bulkImportInParallel(final TypeImportContext typeImportContext, ImportDataIterator importDataIterator,
			final ValidationErrors errors)
			throws ValidationException {

		final AtomicInteger skipped = new AtomicInteger();

		Iterator<List<ImportData>> importDataBatches = new BatchBuilderIterator<>(importDataIterator, params.getBatchSize());
		final ImportDataOptions options = importDataIterator.getOptions();
		try {
			iterateOverRunningTaskInParallel(importDataBatches, params.threads, new IteratorElementTask<List<ImportData>>() {
				@Override
				public void executeTask(List<ImportData> value)
						throws Exception {
					TypeBatchImportContext typeBatchImportContext = newTypeBatchImportContext(typeImportContext, options, value);
					skipped.addAndGet(importBatch(typeImportContext, typeBatchImportContext, errors));
					recordServices.executeHandlingImpactsAsync(typeBatchImportContext.transaction);

				}
			});
		} catch (ValidationException e) {
			throw e;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		while (importDataIterator.hasNext()) {
			importDataIterator.next();
		}

		return skipped.get();
	}

	private int importBatch(final TypeImportContext typeImportContext, TypeBatchImportContext typeBatchImportContext,
			ValidationErrors errors)
			throws ValidationException {

		preuploadContents(typeBatchImportContext);

		int skipped = 0;
		int errorsCount = 0;
		List<ImportData> batch = typeBatchImportContext.batch;
		for (int i = 0; i < batch.size(); i++) {
			final ImportData toImport = batch.get(i);
			final String schemaTypeLabel = types.getSchemaType(typeImportContext.schemaType).getLabel(language);

			DecoratedValidationsErrors decoratedValidationsErrors = new DecoratedValidationsErrors(errors) {
				@Override
				public void buildExtraParams(Map<String, Object> parameters) {
					if (toImport.getValue("code") != null) {
						parameters.put("prefix", schemaTypeLabel + " " + toImport.getValue("code") + " : ");
					} else {
						parameters.put("prefix", schemaTypeLabel + " " + toImport.getLegacyId() + " : ");
					}

					parameters.put("index", "" + (toImport.getIndex() + 1));
					parameters.put("legacyId", toImport.getLegacyId());
					parameters.put("schemaType", typeImportContext.schemaType);
				}
			};
			String legacyId = toImport.getLegacyId();
			try {

				if (!this.skippedRecordsImport.isSkipped(typeImportContext.schemaType, legacyId)) {
					importRecord(typeImportContext, typeBatchImportContext, toImport, decoratedValidationsErrors);
				}

			} catch (PostponedRecordException e) {
				progressionHandler.onRecordImportPostponed(legacyId);
				skipped++;
			}

			if (decoratedValidationsErrors.hasDecoratedErrors()) {
				errorsCount++;
			}

		}

		//		LOGGER.info("Splitter cache object count : " + SchemaUtils.underscoreSplitCache.size());
		//		LOGGER.info("Record cache object count : " + modelLayerFactory.getRecordsCaches().getCacheObjectsCount());
		//		LOGGER.info("Import cache object count : " + resolverCache.getCacheTotalSize());

		String firstId = batch.get(0).getLegacyId();
		String lastId = batch.get(batch.size() - 1).getLegacyId();

		progressionHandler.afterRecordImports(firstId, lastId, batch.size(), errorsCount);

		contentManager.deleteUnreferencedContents(RecordsFlushing.LATER());
		return skipped;
	}

	private void importRecord(TypeImportContext typeImportContext, TypeBatchImportContext typeBatchImportContext,
			ImportData toImport, DecoratedValidationsErrors errors)
			throws ValidationException, PostponedRecordException {

		String legacyId = toImport.getLegacyId();
		if (resolverCache.getNotYetImportedLegacyIds(typeImportContext.schemaType).contains(legacyId)) {

			extensions.callRecordImportValidate(typeImportContext.schemaType, new ValidationParams(errors, toImport));

			String title = (String) toImport.getFields().get("title");

			try {
				Record record = buildRecord(typeImportContext, typeBatchImportContext, toImport, errors);

				typeBatchImportContext.transaction.add(record);
				typeImportContext.addUpdateCount.incrementAndGet();

				try {
					recordServices.validateRecordInTransaction(record, typeBatchImportContext.transaction);

				} catch (RecordServicesException.ValidationException e) {
					for (ValidationError error : e.getErrors().getValidationErrors()) {
						errors.add(error, error.getParameters());
					}
				}

				if (errors.hasDecoratedErrors()) {
					if (params.getImportErrorsBehavior() == ImportErrorsBehavior.STOP_ON_FIRST_ERROR) {
						throw new ValidationException(errors);

					} else {
						skippedRecordsImport.markAsSkippedBecauseOfFailure(typeImportContext.schemaType, legacyId);
						typeBatchImportContext.transaction.remove(record);
					}

				} else {
					resolverCache.mapIds(typeImportContext.schemaType, LEGACY_ID_LOCAL_CODE, legacyId, record.getId());
					for (String uniqueMetadata : typeImportContext.uniqueMetadatas) {
						String value = (String) toImport.getFields().get(uniqueMetadata);
						if (value != null) {
							resolverCache.mapIds(typeImportContext.schemaType, uniqueMetadata, value, record.getId());
						}
					}
				}

			} catch (SkippedBecauseOfFailedDependency e) {
				skippedRecordsImport.markAsSkippedBecauseOfDependencyFailure(typeImportContext.schemaType, legacyId);
			}
			progressionHandler.incrementProgression();
		}
	}

	private void preuploadContents(TypeBatchImportContext typeBatchImportContext) {
		List<Metadata> contentMetadatas = types.getAllContentMetadatas();
		if (!contentMetadatas.isEmpty()) {

			for (ImportData toImport : typeBatchImportContext.batch) {

				for (Metadata contentMetadata : contentMetadatas) {
					if (toImport.getFields().containsKey(contentMetadata.getLocalCode())) {
						List<ContentImport> contentImports = new ArrayList<>();
						if (contentMetadata.isMultivalue()) {
							contentImports.addAll((List) toImport.getFields().get(contentMetadata.getLocalCode()));
						} else {
							contentImports.add((ContentImport) toImport.getFields().get(contentMetadata.getLocalCode()));

						}
						for (ContentImport contentImport : contentImports) {
							for (Iterator<ContentImportVersion> iterator = contentImport.getVersions().iterator(); iterator
									.hasNext(); ) {
								ContentImportVersion version = iterator.next();
								String url = version.getUrl();
								if (!url.toLowerCase().startsWith("imported://")) {
									StreamFactory<InputStream> inputStreamStreamFactory = urlResolver
											.resolve(url, version.getFileName());

									if (typeBatchImportContext.bulkUploader == null) {
										typeBatchImportContext.bulkUploader = new BulkUploader(modelLayerFactory);
										typeBatchImportContext.bulkUploader.setHandleDeletionOfUnreferencedHashes(false);
									}

									if (iterator.hasNext()) {
										typeBatchImportContext.bulkUploader
												.uploadAsyncWithoutParsing(url, inputStreamStreamFactory, version.getFileName());
									} else {
										typeBatchImportContext.bulkUploader.uploadAsync(url, inputStreamStreamFactory);
									}
								}
							}

						}
					}
				}

			}
			if (typeBatchImportContext.bulkUploader != null) {
				typeBatchImportContext.bulkUploader.close();
			}
		}
	}

	Record buildRecord(TypeImportContext typeImportContext, TypeBatchImportContext typeBatchImportContext,
			ImportData toImport, ValidationErrors errors)
			throws PostponedRecordException, SkippedBecauseOfFailedDependency {
		MetadataSchemaType schemaType = getMetadataSchemaType(typeImportContext.schemaType);
		MetadataSchema newSchema = getMetadataSchema(typeImportContext.schemaType + "_" + toImport.getSchema());

		Record record;
		String legacyId = toImport.getLegacyId();
		if (resolverCache.isRecordUpdate(typeImportContext.schemaType, legacyId)) {
			record = modelLayerFactory.newSearchServices()
					.searchSingleResult(from(schemaType).where(LEGACY_ID).isEqualTo(legacyId));
		} else {
			if (typeBatchImportContext.options.isImportAsLegacyId()) {
				record = recordServices.newRecordWithSchema(newSchema);
			} else {
				record = recordServices.newRecordWithSchema(newSchema, legacyId);
			}
		}

		if (!newSchema.getCode().equals(record.getSchemaCode())) {
			MetadataSchema wasSchema = getMetadataSchema(record.getSchemaCode());
			record.changeSchema(wasSchema, newSchema);
		}
		record.set(LEGACY_ID, legacyId);
		for (Entry<String, Object> field : toImport.getFields().entrySet()) {
			Metadata metadata = newSchema.getMetadata(field.getKey());
			if (metadata.getType() != MetadataValueType.STRUCTURE) {
				Object value = field.getValue();
				Object convertedValue =
						value == null ? null : convertValue(typeBatchImportContext, metadata, value, errors);
				record.set(metadata, convertedValue);
			}
		}

		extensions.callRecordImportBuild(typeImportContext.schemaType, new BuildParams(record, types, toImport));

		return record;
	}

	Object convertScalarValue(TypeBatchImportContext typeBatchImportContext, Metadata metadata, Object value,
			ValidationErrors errors)
			throws PostponedRecordException, SkippedBecauseOfFailedDependency {
		switch (metadata.getType()) {

		case NUMBER:
			return Double.valueOf((String) value);

		case BOOLEAN:
			return value == null ? null : ALL_BOOLEAN_YES.contains(((String) value).toLowerCase());

		case CONTENT:
			return convertContent(typeBatchImportContext, value, errors);

		case REFERENCE:
			return convertReference(metadata, (String) value);

		case ENUM:
			return EnumWithSmallCodeUtils.toEnum(metadata.getEnumClass(), (String) value);

		default:
			return value;
		}
	}

	private Content convertContent(TypeBatchImportContext typeBatchImportContext, Object value, ValidationErrors errors) {
		ContentImport contentImport = (ContentImport) value;
		List<ContentImportVersion> versions = contentImport.getVersions();
		Content content = null;

		for (int i = 0; i < versions.size(); i++) {
			ContentImportVersion version = versions.get(i);
			try {

				ContentVersionDataSummary contentVersionDataSummary;
				if (version.getUrl().toLowerCase().startsWith("imported://")) {
					String importedFilePath = IMPORTED_FILEPATH_CLEANER.replaceOn(
							version.getUrl().substring("imported://".length()));
					Factory<ContentVersionDataSummary> factory = importedFilesMap.get(importedFilePath);

					if (factory == null) {
						Map<String, Object> parameters = new HashMap<>();
						parameters.put("fileName", contentImport.getFileName());
						parameters.put("filePath", importedFilePath);
						errors.add(RecordsImportServices.class, CONTENT_NOT_IMPORTED_ERROR, parameters);
						return null;
					} else {
						contentVersionDataSummary = factory.get();
					}

				} else {
					contentVersionDataSummary = typeBatchImportContext.bulkUploader.get(version.getUrl());
				}

				if (content == null) {
					try {

						if (version.isMajor()) {
							content = contentManager.createMajor(user, version.getFileName(), contentVersionDataSummary);
						} else {
							content = contentManager.createMinor(user, version.getFileName(), contentVersionDataSummary);
						}
					} catch (Exception e) {
						e.printStackTrace();
						Map<String, Object> parameters = new HashMap<>();
						parameters.put("fileName", contentImport.getFileName());
						errors.add(RecordsImportServices.class, CONTENT_NOT_IMPORTED_ERROR, parameters);
						return null;
					}
				} else {
					content.updateContentWithName(user, contentVersionDataSummary, version.isMajor(), version.getFileName());
				}
				if (version.getLastModification() != null) {
					content.setVersionModificationDatetime(version.getLastModification());
				}
				if (version.getComment() != null) {
					content.setVersionComment(version.getComment());
				}
			} catch (BulkUploaderRuntimeException e) {
				e.getCause().printStackTrace();
				LOGGER.warn("Could not retrieve content with url '" + contentImport.getUrl() + "'");

				Map<String, Object> params = new HashMap<>();
				params.put("url", e.getKey());
				errors.addWarning(RecordsImportServices.class, CONTENT_NOT_FOUND_ERROR, params);
				return null;
			}
		}
		return content;
	}

	private Object convertReference(Metadata metadata, String value)
			throws PostponedRecordException, SkippedBecauseOfFailedDependency {

		Resolver resolver = toResolver(value);
		String referenceType = metadata.getAllowedReferences().getTypeWithAllowedSchemas();

		if (LEGACY_ID_LOCAL_CODE.equals(resolver.metadata) && resolver.value != null) {
			if (this.skippedRecordsImport.isSkipped(referenceType, resolver.value)) {
				throw new SkippedBecauseOfFailedDependency();
			}
		}

		if (!resolverCache.isAvailable(referenceType, resolver.metadata, resolver.value)) {
			throw new PostponedRecordException();
		}
		return resolverCache.resolve(referenceType, value);
	}

	Object convertValue(TypeBatchImportContext typeBatchImportContext, Metadata metadata, Object value, ValidationErrors errors)
			throws PostponedRecordException, SkippedBecauseOfFailedDependency {

		if (metadata.isMultivalue()) {

			List<Object> convertedValues = new ArrayList<>();

			if (value != null) {
				List<Object> rawValues = (List<Object>) value;
				for (Object item : rawValues) {
					Object convertedValue = convertScalarValue(typeBatchImportContext, metadata, item, errors);
					if (convertedValue != null) {
						convertedValues.add(convertedValue);
					}
				}
			}

			return convertedValues;

		} else {
			return convertScalarValue(typeBatchImportContext, metadata, value, errors);
		}

	}

	List<String> getImportedSchemaTypes()
			throws ValidationException {
		List<String> importedSchemaTypes = new ArrayList<>();
		List<String> availableSchemaTypes = importDataProvider.getAvailableSchemaTypes();

		validateAvailableSchemaTypes(availableSchemaTypes);

		for (String schemaType : types.getSchemaTypesSortedByDependency()) {
			if (availableSchemaTypes.contains(schemaType)) {
				importedSchemaTypes.add(schemaType);
			}
		}

		return importedSchemaTypes;
	}

	private void validateAvailableSchemaTypes(List<String> availableSchemaTypes)
			throws ValidationException {
		ValidationErrors errors = new ValidationErrors();

		for (String availableSchemaType : availableSchemaTypes) {
			try {
				types.getSchemaType(availableSchemaType);
			} catch (NoSuchSchemaType e) {
				Map<String, Object> parameters = new HashMap<>();
				parameters.put("schemaType", availableSchemaType);

				errors.add(RecordsImportServices.class, INVALID_SCHEMA_TYPE_CODE, parameters);
			}
		}

		if (!errors.getValidationErrors().isEmpty()) {
			throw new ValidationException(errors);
		}
	}

	void validate(ValidationErrors errors)
			throws com.constellio.model.frameworks.validation.ValidationException {
		List<String> importedSchemaTypes = getImportedSchemaTypes();
		for (String schemaType : importedSchemaTypes) {
			new RecordsImportValidator(schemaType, progressionHandler, importDataProvider, types, resolverCache, extensions,
					language, skippedRecordsImport).validate(errors);

			if (params.getImportValidationErrorsBehavior() == ImportValidationErrorsBehavior.STOP_IMPORT) {
				errors.throwIfNonEmpty();
			}
		}
	}

	MetadataSchema getMetadataSchema(String schema) {
		return schemasManager.getSchemaTypes(collection).getSchema(schema);
	}

	MetadataSchemaType getMetadataSchemaType(String schemaType) {
		return schemasManager.getSchemaTypes(collection).getSchemaType(schemaType);
	}

	private static class PostponedRecordException extends Exception {
	}

	private static class SkippedBecauseOfFailedDependency extends Exception {
	}

}
