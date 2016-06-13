package com.constellio.app.services.trash;

import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.from;
import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.fromAllSchemasIn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.app.ui.framework.data.RecordVODataProvider;
import com.constellio.data.dao.dto.records.FacetValue;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.schemas.MetadataSchemaType;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.services.records.RecordDeleteOptions;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.search.SPEQueryResponse;
import com.constellio.model.services.search.SearchServices;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;
import com.constellio.model.services.search.query.logical.condition.LogicalSearchCondition;

public class TrashServices {
	private final AppLayerFactory appLayerFactory;
	private final String collection;
	private RecordServices recordServices;

	public TrashServices(AppLayerFactory appLayerFactory, String collection) {
		this.appLayerFactory = appLayerFactory;
		this.collection = collection;
	}

	public LogicalSearchQuery getTrashRecordsQueryForType(String selectedType, User currentUser) {
		MetadataSchemaType schema = appLayerFactory.getModelLayerFactory()
				.getMetadataSchemasManager().getSchemaTypes(collection).getSchemaType(selectedType);
		LogicalSearchCondition condition = from(schema).where(Schemas.LOGICALLY_DELETED_STATUS).isTrue();
		return new LogicalSearchQuery(condition).filteredWithUserDelete(currentUser).sortAsc(Schemas.TITLE);
	}

	public LogicalSearchQuery getTrashRecordsQueryForCollection(String collection, User currentUser) {
		LogicalSearchCondition condition = fromAllSchemasIn(collection).where(Schemas.LOGICALLY_DELETED_STATUS).isTrue();
		return new LogicalSearchQuery(condition).filteredWithUserDelete(currentUser).sortAsc(Schemas.TITLE);
	}

	public List<String> restoreSelection(Set<String> selectedRecords, User currentUser) {
		List<String> returnList = new ArrayList<>();
		for (String recordId : selectedRecords) {
			Record record = recordServices().getDocumentById(recordId);
			if(recordServices().isRestorable(record, currentUser)){
				recordServices().restore(record, currentUser);
			}else{
				returnList.add(recordId);
			}
		}
		return returnList;
	}

	private RecordServices recordServices() {
		if (recordServices == null) {
			recordServices = appLayerFactory.getModelLayerFactory().newRecordServices();
		}
		return recordServices;
	}

	public void deleteSelection(Set<String> selectedRecords, User currentUser) {
		for (String recordId : selectedRecords) {
			Record record = recordServices().getDocumentById(recordId);
			recordServices().physicallyDelete(record, currentUser);//, RecordDeleteOptions.
		}
	}

	public Set<String> getTypesWithLogicallyDeletedRecords(String collection, User currentUser) {
		SearchServices searchServices = appLayerFactory.getModelLayerFactory()
				.newSearchServices();
		LogicalSearchQuery query = getTrashRecordsQueryForCollection(collection, currentUser);
		query = query.addFieldFacet("schema_s").setNumberOfRows(0);

		SPEQueryResponse response = searchServices.query(query);
		List<String> schemasCodes = response.getFieldFacetValuesWithResults("schema_s");
		Set<String> returnSet = new HashSet<>();
		for (String schemaCode : schemasCodes) {
			String schemaType = StringUtils.substringBefore(schemaCode, "_");
			returnSet.add(schemaType);
		}
		return returnSet;
	}

	public List<String> getRelatedRecords(String recordId) {
		return new ArrayList<>();
	}
}
