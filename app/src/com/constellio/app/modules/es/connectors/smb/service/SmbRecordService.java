package com.constellio.app.modules.es.connectors.smb.service;

import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.where;

import java.util.*;

import com.constellio.app.modules.es.connectors.smb.cache.ContextUtils;
import com.constellio.app.modules.es.connectors.smb.cache.SmbConnectorContext;
import com.constellio.app.modules.es.constants.ESTaxonomies;
import com.constellio.data.dao.dto.records.FacetValue;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.services.search.SPEQueryResponse;
import com.constellio.model.services.search.query.ReturnedMetadatasFilter;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

import com.constellio.app.modules.es.connectors.smb.utils.ConnectorSmbUtils;
import com.constellio.app.modules.es.model.connectors.ConnectorDocument;
import com.constellio.app.modules.es.model.connectors.smb.ConnectorSmbDocument;
import com.constellio.app.modules.es.model.connectors.smb.ConnectorSmbFolder;
import com.constellio.app.modules.es.model.connectors.smb.ConnectorSmbInstance;
import com.constellio.app.modules.es.services.ESSchemasRecordsServices;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;
import org.joda.time.LocalDateTime;

public class SmbRecordService {
	private ESSchemasRecordsServices es;
	private ConnectorSmbInstance connectorInstance;
	private ConnectorSmbUtils smbUtils;

	public SmbRecordService(ESSchemasRecordsServices es, ConnectorSmbInstance connectorInstance) {
		this.es = es;
		this.connectorInstance = connectorInstance;
		this.smbUtils = new ConnectorSmbUtils();
	}

	public static String getSafeId(ConnectorSmbFolder folder) {
		String folderId = null;
		if (folder != null) {
			folderId = folder.getId();
		}
		return folderId;
	}

	public List<ConnectorSmbDocument> getDocuments(String url) {
		return es.searchConnectorSmbDocuments(es.fromConnectorSmbDocumentWhereConnectorIs(connectorInstance)
				.andWhere(es.connectorSmbDocument.url())
				.isEqualTo(url));
	}

	public List<ConnectorSmbFolder> getFolders(String url) {
		return es.searchConnectorSmbFolders(es.fromConnectorSmbFolderWhereConnectorIs(connectorInstance)
				.andWhere(es.connectorSmbFolder.url())
				.isEqualTo(url));
	}

	public synchronized ConnectorSmbDocument newConnectorSmbDocument(String url) {
		ConnectorSmbDocument document = es.newConnectorSmbDocument(connectorInstance);
		return document;
	}

	public ConnectorSmbDocument convertToSmbDocumentOrNull(ConnectorDocument document) {
		ConnectorSmbDocument result = null;

		String documentSchemaCode = document.getSchemaCode();
		String smbDocumentSchemaTypeCode = es.connectorSmbDocument.schemaType()
				.getCode();

		if (documentSchemaCode.contains(smbDocumentSchemaTypeCode)) {
			result = es.wrapConnectorSmbDocument(document.getWrappedRecord());
		}
		return result;
	}

	public ConnectorSmbFolder convertToSmbFolderOrNull(ConnectorDocument document) {
		ConnectorSmbFolder result = null;

		String documentSchemaCode = document.getSchemaCode();
		String smbFolderSchemaTypeCode = es.connectorSmbFolder.schemaType()
				.getCode();

		if (documentSchemaCode.contains(smbFolderSchemaTypeCode)) {
			result = es.wrapConnectorSmbFolder(document.getWrappedRecord());
		}
		return result;
	}

	public synchronized ConnectorSmbFolder newConnectorSmbFolder(String url) {
		ConnectorSmbFolder folder = es.newConnectorSmbFolder(connectorInstance);
		return folder;
	}

	public ConnectorSmbFolder getFolder(String url) {
		if (StringUtils.isBlank(url)) {
			return null;
		} else {
			List<ConnectorSmbFolder> folders = getFolders(url);
			if (folders.isEmpty()) {
				return null;
			}
			return folders.get(0);
		}
	}

	public ConnectorSmbDocument getDocument(String url) {
		if (StringUtils.isBlank(url)) {
			return null;
		} else {
			List<ConnectorSmbDocument> documents = getDocuments(url);
			if (documents.isEmpty()) {
				return null;
			}
			return documents.get(0);
		}
	}

	public void updateResumeUrl(String url) {
		connectorInstance.setResumeUrl(url);
	}


	public Set<String> duplicateDocuments() {
		LogicalSearchQuery query = new LogicalSearchQuery(es.fromAllDocumentsOf(connectorInstance.getId()))
			.addFieldFacet(Schemas.URL.getDataStoreCode())
			.setFieldFacetLimit(10_000)
			.setReturnedMetadatas(ReturnedMetadatasFilter.onlyMetadatas(Schemas.URL))
			.setNumberOfRows(0);
		Map<String, String[]> solrParams = new HashMap<>();
		solrParams.put("facet.mincount", new String[]{"2"});
		query.setOverridedQueryParams(solrParams);

		Set<String> urls = new HashSet<>();
		SPEQueryResponse response = es.getAppLayerFactory().getModelLayerFactory().newSearchServices().query(query);
		for (FacetValue facetValue : response.getFieldFacetValues(Schemas.URL.getDataStoreCode())) {
			urls.add(facetValue.getValue());
		}

		return urls;
	}

	public void syncContext(SmbConnectorContext context) {
		Map<String, SmbModificationIndicator> urlsFromDb = urlsFromDb();
		String traversalCode = UUID.randomUUID().toString();;
		for (Map.Entry<String, SmbModificationIndicator> databaseEntry : urlsFromDb.entrySet()) {
			context.traverseModified(databaseEntry.getKey(), databaseEntry.getValue(), databaseEntry.getValue().getParentId(), traversalCode);
		}
		for (String url : context.staleUrls(traversalCode)) {
			context.delete(url);
		}
	}

	private Map<String, SmbModificationIndicator> urlsFromDb() {
		Map<String, SmbModificationIndicator> urls = new HashMap<>();
		Metadata url = Schemas.URL;
		Metadata permissionHash = es.connectorSmbDocument.permissionsHash();
		Metadata size = es.connectorSmbDocument.size();
		Metadata lastModified = es.connectorSmbDocument.lastModified();
		Metadata parent = es.connectorSmbDocument.parent();
		Metadata parentFolder = es.connectorSmbFolder.parent();

		LogicalSearchQuery query = new LogicalSearchQuery(es.fromAllDocumentsOf(connectorInstance.getId()));
		query.setReturnedMetadatas(ReturnedMetadatasFilter.onlyMetadatas(url, permissionHash, size, lastModified, parent, parentFolder));
		int startRow = 0;
		query.setNumberOfRows(10_000);
		while(true) {
			SPEQueryResponse response = es.getAppLayerFactory().getModelLayerFactory().newSearchServices().query(query);
			List<Record> records = response.getRecords();
			if (records.isEmpty()) {
				break;
			}
			for (Record record : response.getRecords()) {
				String urlValue = record.get(url);
				String permissionHashValue = record.get(permissionHash);
				permissionHashValue = StringUtils.defaultString(permissionHashValue);
				Double sizeDouble = record.get(size);
				String parentValue = (String) record.get(parent);
				if (parentValue == null) {
					parentValue = (String) record.get(parentFolder);
				}
				double sizeValue = 0;
				if (sizeDouble != null) {
					sizeValue = sizeDouble;
				}
				LocalDateTime lastModifiedDateTime = record.get(lastModified);
				long lastModifiedValue = -1;
				if (lastModifiedDateTime != null) {
					lastModifiedValue = lastModifiedDateTime.toDate().getTime();
				}

				SmbModificationIndicator databaseIndicator = new SmbModificationIndicator(permissionHashValue, sizeValue, lastModifiedValue);
				databaseIndicator.setParentId(parentValue);
				urls.put(urlValue, databaseIndicator);
				startRow++;
			}
			query.setStartRow(startRow);
		}

		return urls;
	}
}