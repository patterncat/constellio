package com.constellio.app.entities.schemasDisplay;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SchemaDisplayConfig {

	private final String schemaCode;

	private final String collection;

	private final List<String> displayMetadataCodes;

	private final List<String> formMetadataCodes;

	private final List<String> searchResultsMetadataCodes;

	private final List<String> tableMetadataCodes;

	public SchemaDisplayConfig(String collection, String schemaCode, List<String> displayMetadataCodes,
			List<String> formMetadataCodes, List<String> searchResultsMetadataCodes, List<String> tableMetadataCodes) {
		this.collection = collection;
		this.schemaCode = schemaCode;
		this.displayMetadataCodes = Collections.unmodifiableList(displayMetadataCodes);
		this.formMetadataCodes = Collections.unmodifiableList(formMetadataCodes);
		this.searchResultsMetadataCodes = Collections.unmodifiableList(searchResultsMetadataCodes);
		this.tableMetadataCodes = Collections.unmodifiableList(tableMetadataCodes);
	}

	public List<String> getDisplayMetadataCodes() {
		return displayMetadataCodes;
	}

	public List<String> getFormMetadataCodes() {
		return formMetadataCodes;
	}

	public List<String> getSearchResultsMetadataCodes() {
		return searchResultsMetadataCodes;
	}

	public List<String> getTableMetadataCodes() {
		return tableMetadataCodes;
	}

	public String getSchemaCode() {
		return schemaCode;
	}

	public String getCollection() {
		return collection;
	}

	public SchemaDisplayConfig withDisplayMetadataCodes(List<String> displayMetadataCodes) {
		return new SchemaDisplayConfig(collection, schemaCode, displayMetadataCodes, formMetadataCodes,
				searchResultsMetadataCodes, tableMetadataCodes);
	}

	public SchemaDisplayConfig withFormMetadataCodes(List<String> formMetadataCodes) {
		return new SchemaDisplayConfig(collection, schemaCode, displayMetadataCodes, formMetadataCodes,
				searchResultsMetadataCodes, tableMetadataCodes);
	}

	public SchemaDisplayConfig withSearchResultsMetadataCodes(List<String> searchResultsMetadataCodes) {
		return new SchemaDisplayConfig(collection, schemaCode, displayMetadataCodes, formMetadataCodes,
				searchResultsMetadataCodes, tableMetadataCodes);
	}

	public SchemaDisplayConfig withTableMetadataCodes(List<String> tableMetadataCodes) {
		return new SchemaDisplayConfig(collection, schemaCode, displayMetadataCodes, formMetadataCodes,
				searchResultsMetadataCodes, tableMetadataCodes);
	}

	public SchemaDisplayConfig withNewSearchResultMetadataCode(String code) {
		List<String> result = new ArrayList<>(searchResultsMetadataCodes);
		result.add(code);
		return withSearchResultsMetadataCodes(result);
	}

	public SchemaDisplayConfig withNewDisplayMetadataBefore(String metadataCode, String before) {
		int index = displayMetadataCodes.indexOf(before);
		List<String> displayMetadataCodes = new ArrayList<>();
		displayMetadataCodes.addAll(this.displayMetadataCodes);
		displayMetadataCodes.add(index, metadataCode);
		return withDisplayMetadataCodes(displayMetadataCodes);
	}

	public SchemaDisplayConfig withNewFormMetadata(String metadataCode) {
		List<String> formMetadatas = new ArrayList<>();
		formMetadatas.addAll(this.formMetadataCodes);
		formMetadatas.add(metadataCode);
		return withFormMetadataCodes(formMetadatas);
	}

	public SchemaDisplayConfig withNewFormAndDisplayMetadatas(String... metadataCodes) {

		List<String> displayMetadatas = new ArrayList<>();
		displayMetadatas.addAll(this.displayMetadataCodes);
		displayMetadatas.addAll(asList(metadataCodes));

		List<String> formMetadatas = new ArrayList<>();
		formMetadatas.addAll(this.formMetadataCodes);
		formMetadatas.addAll(asList(metadataCodes));
		return withFormMetadataCodes(formMetadatas).withDisplayMetadataCodes(displayMetadatas);
	}

	public SchemaDisplayConfig withNewFormMetadatas(String... metadataCodes) {

		List<String> formMetadatas = new ArrayList<>();
		formMetadatas.addAll(this.formMetadataCodes);
		formMetadatas.addAll(asList(metadataCodes));
		return withFormMetadataCodes(formMetadatas);
	}

	public SchemaDisplayConfig withRemovedDisplayMetadatas(String... metadataCodes) {

		List<String> displayMetadatas = new ArrayList<>();
		displayMetadatas.addAll(this.displayMetadataCodes);
		displayMetadatas.removeAll(asList(metadataCodes));
		return withDisplayMetadataCodes(displayMetadatas);
	}

	public SchemaDisplayConfig withRemovedFormMetadatas(String... metadataCodes) {

		List<String> formMetadatas = new ArrayList<>();
		formMetadatas.addAll(this.formMetadataCodes);
		formMetadatas.removeAll(asList(metadataCodes));
		return withFormMetadataCodes(formMetadatas);
	}

	public SchemaDisplayConfig withNewFormMetadataBefore(String metadataCode, String before) {
		int index = formMetadataCodes.indexOf(before);
		List<String> formMetadatas = new ArrayList<>();
		formMetadatas.addAll(this.formMetadataCodes);
		formMetadatas.add(index, metadataCode);
		return withFormMetadataCodes(formMetadatas);
	}

}

