package com.constellio.app.ui.pages.search.batchProcessing.entities;

import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.schemas.MetadataSchemaType;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;

import java.util.*;

public class BatchProcessRequest {

	private List<String> ids = new ArrayList<>();

	private LogicalSearchQuery query;

	private Map<String, Object> modifiedMetadatas = new HashMap<>();

	private User user;

	private MetadataSchemaType schemaType;

	public BatchProcessRequest(List<String> ids, LogicalSearchQuery query, User user,
							   MetadataSchemaType schemaType, Map<String, Object> modifiedMetadatas) {
		if(ids != null) {
			this.ids = Collections.unmodifiableList(ids);
		} else {
			this.ids = Collections.unmodifiableList(new ArrayList<String>());
		}

		this.query = query;
		this.user = user;
		this.schemaType = schemaType;
		this.modifiedMetadatas = Collections.unmodifiableMap(modifiedMetadatas);
	}

	public BatchProcessRequest() {
	}

	public List<String> getIds() {
		return ids;
	}

	public User getUser() {
		return user;
	}

	public MetadataSchemaType getSchemaType() {
		return schemaType;
	}

	public Map<String, Object> getModifiedMetadatas() {
		return modifiedMetadatas;
	}

	public BatchProcessRequest setIds(List<String> ids) {
		this.ids = ids;
		return this;
	}

	public BatchProcessRequest setModifiedMetadatas(Map<String, Object> modifiedMetadatas) {
		this.modifiedMetadatas = modifiedMetadatas;
		return this;
	}

	public BatchProcessRequest setUser(User user) {
		this.user = user;
		return this;
	}

	public BatchProcessRequest addModifiedMetadata(String metadataCode, Object value) {
		modifiedMetadatas.put(metadataCode, value);
		return this;
	}

	public BatchProcessRequest setSchemaType(MetadataSchemaType schemaType) {
		this.schemaType = schemaType;
		return this;
	}

	public LogicalSearchQuery getQuery() {
		return query;
	}

	public BatchProcessRequest setQuery(LogicalSearchQuery query) {
		this.query = query;
		return this;
	}

	@Override
	public String toString() {
		return "BatchProcessRequest{" +
				"modifiedMetadatas=" + modifiedMetadatas +
				", ids=" + ids +
				'}';
	}
}
