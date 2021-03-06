package com.constellio.app.services.schemas.bulkImport;

import com.constellio.app.services.schemas.bulkImport.data.ImportDataProvider;
import com.constellio.data.utils.KeySetMap;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchemaType;
import com.constellio.model.entities.schemas.MetadataSchemaTypes;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.search.SearchServices;
import com.constellio.model.services.search.query.ReturnedMetadatasFilter;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;

import java.util.*;

import static com.constellio.model.entities.schemas.Schemas.LEGACY_ID;
import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.from;

public class ResolverCache {

	private static final int MAX_NUMBER_OF_RECORDS_BEFORE_LOADING_ALL_LEGACY_IDS = 1000;

	Map<String, Long> typesRecordsCount = new HashMap<>();

	Map<String, Map<String, SchemaTypeUniqueMetadataMappingCache>> cache = new HashMap<>();

	MetadataSchemaTypes types;

	RecordServices recordServices;

	SearchServices searchServices;

	ImportDataProvider importDataProvider;

	public ResolverCache(RecordServices recordServices, SearchServices searchServices, MetadataSchemaTypes types,
			ImportDataProvider importDataProvider) {
		this.recordServices = recordServices;
		this.types = types;
		this.searchServices = searchServices;
		this.importDataProvider = importDataProvider;
	}

	public int getCacheTotalSize() {
		int nbElements = 0;
		for (Map.Entry<String, Map<String, SchemaTypeUniqueMetadataMappingCache>> element : cache.entrySet()) {
			for (SchemaTypeUniqueMetadataMappingCache cache : element.getValue().values()) {
				nbElements += cache.getItemsCount();
			}
		}
		return nbElements;
	}

	synchronized SchemaTypeUniqueMetadataMappingCache getSchemaTypeCache(String schemaType, String metadata) {
		if (!cache.containsKey(schemaType)) {
			cache.put(schemaType, new HashMap<String, SchemaTypeUniqueMetadataMappingCache>());
		}
		Map<String, SchemaTypeUniqueMetadataMappingCache> mapping = cache.get(schemaType);
		if (!mapping.containsKey(metadata)) {
			mapping.put(metadata, new SchemaTypeUniqueMetadataMappingCache(schemaType, metadata));
		}

		return mapping.get(metadata);
	}

	public List<MetadataSchemaType> getCachedSchemaTypes() {
		List<MetadataSchemaType> returnedTypes = new ArrayList<>();
		for (String schemaType : cache.keySet()) {
			MetadataSchemaType type = types.getSchemaType(schemaType);
			returnedTypes.add(type);
		}
		return returnedTypes;
	}

	public void mapIds(String schemaType, String metadata, String legacyId, String id) {
		getSchemaTypeCache(schemaType, metadata).mapIds(legacyId, id);
	}

	public void markAsRecordInFile(String schemaType, String metadata, String legacyId) {
		getSchemaTypeCache(schemaType, metadata).markAsRecordInFile(legacyId);
	}

	public boolean isAvailable(String schemaType, String metadata, String legacyId) {
		return !getSchemaTypeCache(schemaType, metadata).recordsInFile.contains(legacyId);
	}

	public synchronized boolean isRecordUpdate(String schemaType, String legacyId, boolean importAsLegacyId) {
		if (importAsLegacyId) {
			if (!typesRecordsCount.containsKey(schemaType)) {
				MetadataSchemaType type = types.getSchemaType(schemaType);
				typesRecordsCount.put(schemaType, searchServices.getResultsCount(from(type).where(LEGACY_ID).isNotNull()));
			}
			return typesRecordsCount.get(schemaType) > 0 &&
					getSchemaTypeCache(schemaType, LEGACY_ID.getLocalCode()).isRecordUpdate(legacyId);
		} else {
			if (!typesRecordsCount.containsKey(schemaType)) {
				MetadataSchemaType type = types.getSchemaType(schemaType);
				typesRecordsCount
						.put(schemaType, searchServices.getResultsCount(from(type).where(Schemas.IDENTIFIER).isNotNull()));
			}
			return typesRecordsCount.get(schemaType) > 0 &&
					getSchemaTypeCache(schemaType, Schemas.IDENTIFIER.getLocalCode()).isRecordUpdate(legacyId);
		}
	}

	public String resolve(String schemaType, String resolver) {
		if (resolver == null) {
			return null;
		} else {
			int colonIndex = resolver.indexOf(":");
			if (colonIndex != -1) {
				String resolverMetadata = resolver.substring(0, colonIndex);
				String resolverValue = resolver.substring(colonIndex + 1);
				String id;
				id = getSchemaTypeCache(schemaType, resolverMetadata).searchMapping.get(resolverValue);

				if (id == null) {
					MetadataSchemaType type = types.getSchemaType(schemaType);
					Metadata metadata = type.getAllMetadatas().getMetadataWithLocalCode(resolverMetadata);
					Record result = recordServices.getRecordByMetadata(metadata, resolverValue);
					id = result == null ? null : result.getId();
					getSchemaTypeCache(schemaType, resolverMetadata).mapSearch(resolver, id);
				}
				return id;
			} else {
				return getSchemaTypeCache(schemaType, LEGACY_ID.getLocalCode()).idsMapping.get(resolver);
			}
		}
	}

	public void markUniqueValueAsRequired(String schemaType, String metadata, String uniqueValue, String usedByMetadata,
			String usedByLegacyId) {
		getSchemaTypeCache(schemaType, metadata).markLegacyIdAsRequiredBy(uniqueValue, usedByMetadata, usedByLegacyId);
	}

	public KeySetMap<String, String> getUnresolvableUniqueValues(String schemaType, String metadata) {
		return getSchemaTypeCache(schemaType, metadata).getUnresolvableLegacyIds();
	}

	public Set<String> getNotYetImportedLegacyIds(String schemaType, boolean importAsLegacyId) {
		if (importAsLegacyId) {
			return getSchemaTypeCache(schemaType, LEGACY_ID.getLocalCode()).recordsInFile;
		} else {
			return getSchemaTypeCache(schemaType, Schemas.IDENTIFIER.getLocalCode()).recordsInFile;
		}
	}

	public boolean isNewUniqueValue(String schemaType, String metadata, String legacyId) {
		return getSchemaTypeCache(schemaType, metadata).isNewLegacyId(legacyId);
	}

	class SchemaTypeUniqueMetadataMappingCache {

		int importDataSize = -1;

		String metadata;

		String schemaType;

		Map<String, String> idsMapping = new HashMap<>();

		Map<String, String> searchMapping = new HashMap<>();

		Set<String> recordsInFile = new HashSet<>();

		KeySetMap<String, String> unresolvedLegacyIds = new KeySetMap<>();

		Set<String> legacyIds = null;

		private SchemaTypeUniqueMetadataMappingCache(String schemaType, String metadata) {
			this.schemaType = schemaType;
			this.metadata = metadata;
		}

		public synchronized void mapIds(String legacyId, String id) {
			idsMapping.put(legacyId, id);
			mapSearch(legacyId, id);
			recordsInFile.remove(legacyId);
			unresolvedLegacyIds.remove(legacyId);
		}

		public synchronized void mapSearch(String search, String id) {
			searchMapping.put(search, id);
		}

		public synchronized void markLegacyIdAsRequiredBy(String legacyId, String usedByMetadata, String usedByLegacyId) {
			if (!idsMapping.containsKey(legacyId) && !recordsInFile.contains(legacyId)) {
				unresolvedLegacyIds.add(legacyId, usedByMetadata + ":" + usedByLegacyId);
			}
		}

		public synchronized KeySetMap<String, String> getUnresolvableLegacyIds() {

			if (unresolvedLegacyIds.getNestedMap().size() > MAX_NUMBER_OF_RECORDS_BEFORE_LOADING_ALL_LEGACY_IDS) {
				LogicalSearchQuery query = new LogicalSearchQuery();
				MetadataSchemaType type = types.getSchemaType(schemaType);
				Metadata resolverMetadata = type.getDefaultSchema().get(this.metadata);
				query.setCondition(from(type).where(resolverMetadata).isNotNull());
				query.setReturnedMetadatas(ReturnedMetadatasFilter.onlyMetadatas(resolverMetadata));
				Iterator<Record> recordsIterator = searchServices.recordsIterator(query, 10000);
				SchemaTypeUniqueMetadataMappingCache cache = getSchemaTypeCache(schemaType, metadata);
				while (recordsIterator.hasNext()) {
					Record record = recordsIterator.next();
					cache.mapSearch(record.<String>get(resolverMetadata), record.getId());
				}
			}

			for (String requiredLegacyId : new HashSet<>(unresolvedLegacyIds.getNestedMap().keySet())) {
				String id = resolve(schemaType, metadata + ":" + requiredLegacyId);
				if (id != null) {
					mapIds(requiredLegacyId, id);
				}
			}
			return new KeySetMap<>(unresolvedLegacyIds);
		}

		public synchronized void markAsRecordInFile(String legacyId) {
			recordsInFile.add(legacyId);

			unresolvedLegacyIds.remove(legacyId);

		}

		public synchronized boolean isNewLegacyId(String legacyId) {
			return !idsMapping.containsKey(legacyId) && !recordsInFile.contains(legacyId);
		}

		public synchronized boolean isRecordUpdate(String legacyId) {

			if (importDataSize == -1) {
				importDataSize = importDataProvider.size(schemaType);
			}
			if (importDataSize <= MAX_NUMBER_OF_RECORDS_BEFORE_LOADING_ALL_LEGACY_IDS) {
				MetadataSchemaType type = types.getSchemaType(schemaType);
				return searchServices.hasResults(from(type).where(LEGACY_ID).isEqualTo(legacyId));

			} else {
				if (legacyIds == null) {

					MetadataSchemaType type = types.getSchemaType(schemaType);
					//List<String> ids = searchServices.searchRecordIds(from(type).where(Schemas.LEGACY_ID).isNotNull());
					legacyIds = new HashSet<>();
					Iterator<Record> iterators = searchServices.recordsIterator(new LogicalSearchQuery()
							.setCondition(from(type).where(LEGACY_ID).isNotNull())
							.setReturnedMetadatas(ReturnedMetadatasFilter.onlyMetadatas(LEGACY_ID)), 5000);
					while (iterators.hasNext()) {
						String aLegacyId = iterators.next().get(LEGACY_ID);
						legacyIds.add(aLegacyId);
					}
				}

				return legacyIds.contains(legacyId);
			}
		}

		public int getItemsCount() {
			int size = 0;
			if (idsMapping != null) {
				size += idsMapping.size();
			}
			if (searchMapping != null) {
				size += searchMapping.size();
			}
			if (recordsInFile != null) {
				size += recordsInFile.size();
			}
			if (unresolvedLegacyIds != null) {
				size += unresolvedLegacyIds.getNestedMap().size();
			}
			if (legacyIds != null) {
				size += legacyIds.size();
			}

			return size;
		}
	}

}
