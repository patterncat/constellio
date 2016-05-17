package com.constellio.app.ui.pages.search;

import static com.constellio.app.ui.i18n.i18n.$;
import static java.util.Arrays.asList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constellio.app.entities.schemasDisplay.MetadataDisplayConfig;
import com.constellio.app.modules.rm.model.labelTemplate.LabelTemplate;
import com.constellio.app.modules.rm.reports.builders.search.stats.StatsReportBuilderFactory;
import com.constellio.app.modules.rm.reports.factories.ExampleReportFactory;
import com.constellio.app.modules.rm.wrappers.RMObject;
import com.constellio.app.services.factories.ConstellioFactories;
import com.constellio.app.services.schemasDisplay.SchemasDisplayManager;
import com.constellio.app.ui.application.ConstellioUI;
import com.constellio.app.ui.entities.FacetVO;
import com.constellio.app.ui.entities.MetadataVO;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.framework.builders.MetadataToVOBuilder;
import com.constellio.app.ui.framework.builders.RecordToVOBuilder;
import com.constellio.app.ui.framework.components.ReportPresenter;
import com.constellio.app.ui.framework.data.SearchResultVODataProvider;
import com.constellio.app.ui.framework.reports.ReportBuilderFactory;
import com.constellio.app.ui.pages.base.BasePresenter;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.app.ui.pages.search.batchProcessing.entities.BatchProcessRequest;
import com.constellio.data.utils.KeySetMap;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.Facet;
import com.constellio.model.entities.records.wrappers.SavedSearch;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.records.wrappers.structure.FacetType;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchema;
import com.constellio.model.entities.schemas.MetadataSchemaType;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.entities.schemas.entries.DataEntryType;
import com.constellio.model.services.records.RecordServicesException;
import com.constellio.model.services.records.RecordServicesRuntimeException;
import com.constellio.model.services.records.SchemasRecordsServices;
import com.constellio.model.services.schemas.SchemaUtils;
import com.constellio.model.services.schemas.builders.CommonMetadataBuilder;
import com.constellio.model.services.search.SPEQueryResponse;
import com.constellio.model.services.search.SearchBoostManager;
import com.constellio.model.services.search.StatusFilter;
import com.constellio.model.services.search.query.ReturnedMetadatasFilter;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;
import com.constellio.model.services.search.query.logical.LogicalSearchQueryFacetFilters;
import com.constellio.model.services.search.query.logical.condition.LogicalSearchCondition;
import com.constellio.model.services.search.zipContents.ZipContentsService;
import com.constellio.model.services.search.zipContents.ZipContentsService.NoContentToZipRuntimeException;
import com.vaadin.server.StreamResource.StreamSource;

public abstract class SearchPresenter<T extends SearchView> extends BasePresenter<T> implements ReportPresenter {
	private static final String ZIP_CONTENT_RESOURCE = "zipContentsFolder";

	public enum SortOrder {ASCENDING, DESCENDING}

	private static Logger LOGGER = LoggerFactory.getLogger(SearchPresenter.class);

	private Map<String, String[]> extraSolrParams = new HashMap<>();
	KeySetMap<String, String> facetSelections = new KeySetMap<>();
	Map<String, Boolean> facetStatus = new HashMap<>();
	List<String> suggestions;
	String sortCriterion;
	SortOrder sortOrder;
	String collection;
	transient SchemasDisplayManager schemasDisplayManager;
	transient SearchPresenterService service;
	boolean highlighter = true;

	public SearchPresenter(T view) {
		super(view);
		init(view.getConstellioFactories(), view.getSessionContext());
	}

	public void setExtraSolrParams(Map<String, String[]> extraSolrParams) {
		this.extraSolrParams = extraSolrParams;
	}

	public Map<String, String[]> getExtraSolrParams() {
		return extraSolrParams;
	}

	private void readObject(java.io.ObjectInputStream stream)
			throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		ConstellioFactories constellioFactories = ConstellioFactories.getInstance();
		SessionContext sessionContext = ConstellioUI.getCurrentSessionContext();
		init(constellioFactories, sessionContext);
	}

	private void init(ConstellioFactories constellioFactories, SessionContext sessionContext) {
		collection = sessionContext.getCurrentCollection();
		service = new SearchPresenterService(collection, constellioFactories.getModelLayerFactory());
		schemasDisplayManager = constellioFactories.getAppLayerFactory().getMetadataSchemasDisplayManager();
	}

	public void resetFacetAndOrder() {
		resetFacetSelection();
		sortOrder = SortOrder.ASCENDING;
	}

	public abstract Record getTemporarySearchRecord();

	public abstract SearchPresenter<T> forRequestParameters(String params);

	public abstract boolean mustDisplayResults();

	public abstract int getPageNumber();

	public abstract void setPageNumber(int pageNumber);

	public List<FacetVO> getFacets() {
		return service.getFacets(getSearchQuery(), facetStatus);
	}

	public String getSortCriterion() {
		return sortCriterion;
	}

	public SortOrder getSortOrder() {
		return sortOrder;
	}

	public String getUserSearchExpression() {
		return null;
	}

	public boolean mustDisplaySuggestions() {
		if (searchServices().getResultsCount(getSearchQuery()) != 0) {
			return false;
		}
		SPEQueryResponse suggestionsResponse = searchServices()
				.query(getSearchQuery().setNumberOfRows(0).setSpellcheck(true));
		if (suggestionsResponse.isCorrectlySpelt()) {
			return false;
		}
		suggestions = suggestionsResponse.getSpellCheckerSuggestions();
		return !suggestions.isEmpty();
	}

	public List<String> getSuggestions() {
		return suggestions;
	}

	public SearchResultVODataProvider getSearchResults() {
		return new SearchResultVODataProvider(new RecordToVOBuilder(), modelLayerFactory,
				view.getSessionContext()) {
			@Override
			protected LogicalSearchQuery getQuery() {
				LogicalSearchQuery query = getSearchQuery().setHighlighting(highlighter).setOverridedQueryParams(extraSolrParams);
				if (sortCriterion == null) {
					if (StringUtils.isNotBlank(getUserSearchExpression())) {
						query.setFieldBoosts(searchBoostManager().getAllSearchBoostsByMetadataType(view.getCollection()));
						query.setQueryBoosts(searchBoostManager().getAllSearchBoostsByQueryType(view.getCollection()));
					}
					return query;
				}
				Metadata metadata = getMetadata(sortCriterion);
				return sortOrder == SortOrder.ASCENDING ? query.sortAsc(metadata) : query.sortDesc(metadata);
			}
		};
	}

	public void facetValueSelected(String facetId, String facetValue) {
		facetSelections.get(facetId).add(facetValue);
		view.refreshSearchResultsAndFacets();
	}

	public void facetValueDeselected(String facetId, String facetValue) {
		facetSelections.get(facetId).remove(facetValue);
		view.refreshSearchResultsAndFacets();
	}

	public void facetDeselected(String facetId) {
		facetSelections.get(facetId).clear();
		view.refreshSearchResultsAndFacets();
	}

	public void facetOpened(String facetId) {
		facetStatus.put(facetId, true);
	}

	public void facetClosed(String facetId) {
		facetStatus.put(facetId, false);
	}

	public KeySetMap<String, String> getFacetSelections() {
		return facetSelections;
	}

	public void setFacetSelections(Map<String, Set<String>> facetSelections) {
		this.facetSelections.putAll(facetSelections);
	}

	public void sortCriterionSelected(String sortCriterion, SortOrder sortOrder) {
		this.sortCriterion = sortCriterion;
		this.sortOrder = sortOrder;
		view.refreshSearchResults(true);
	}

	@Override
	public List<String> getSupportedReports() {
		List<String> supportedReports = new ArrayList<>();
		if (view.computeStatistics()) {
			supportedReports.add("Reports.FolderLinearMeasureStats");
		}
		return supportedReports;
	}

	@Override
	public ReportBuilderFactory getReport(String report) {
		switch (report) {
		case "Reports.fakeReport":
			return new ExampleReportFactory(view.getSelectedRecordIds());
		case "Reports.FolderLinearMeasureStats":
			return new StatsReportBuilderFactory(view.getCollection(), modelLayerFactory, getSearchQuery());
		}
		throw new UnknownReportRuntimeException("BUG: Unknown report " + report);
	}

	public String getZippedContentsFilename() {
		return $("SearchView.contentZip");
	}

	public StreamSource getZippedContents() {
		return new StreamSource() {
			@Override
			public InputStream getStream() {
				File folder = modelLayerFactory.getDataLayerFactory().getIOServicesFactory().newFileService()
						.newTemporaryFolder(ZIP_CONTENT_RESOURCE);
				File file = new File(folder, getZippedContentsFilename());
				try {
					new ZipContentsService(modelLayerFactory, collection)
							.zipContentsOfRecords(view.getSelectedRecordIds(), file);
					return new FileInputStream(file);
				} catch (NoContentToZipRuntimeException e) {
					LOGGER.error("Error while zipping", e);
					view.showErrorMessage($("SearchView.noContentInSelectedRecords"));
					return null;
				} catch (Exception e) {
					LOGGER.error("Error while zipping", e);
					view.showErrorMessage($("SearchView.zipContentsError"));
					return null;
				}
			}
		};
	}

	public abstract void suggestionSelected(String suggestion);

	public abstract List<MetadataVO> getMetadataAllowedInSort();

	protected abstract LogicalSearchCondition getSearchCondition();

	protected LogicalSearchQuery getSearchQuery() {
		LogicalSearchQuery query = new LogicalSearchQuery(getSearchCondition())
				.setOverridedQueryParams(extraSolrParams)
				.setFreeTextQuery(getUserSearchExpression())
				.filteredWithUser(getCurrentUser())
				.filteredByStatus(StatusFilter.ACTIVES)
				.setPreferAnalyzedFields(true);

		query.setReturnedMetadatas(ReturnedMetadatasFilter.onlyFields(
				schemasDisplayManager.getReturnedFieldsForSearch(collection)));

		SchemasRecordsServices schemas = new SchemasRecordsServices(collection, modelLayerFactory);
		LogicalSearchQueryFacetFilters filters = query.getFacetFilters();
		filters.clear();
		for (Entry<String, Set<String>> selection : facetSelections.getMapEntries()) {
			try {
				Facet facet = schemas.getFacet(selection.getKey());
				if (!selection.getValue().isEmpty()) {
					if (facet.getFacetType() == FacetType.FIELD) {
						filters.selectedFieldFacetValues(facet.getFieldDataStoreCode(), selection.getValue());
					} else if (facet.getFacetType() == FacetType.QUERY) {
						filters.selectedQueryFacetValues(facet.getId(), selection.getValue());
					}
				}
			} catch (RecordServicesRuntimeException.NoSuchRecordWithId id) {
				LOGGER.warn("Facet '" + id + "' has been deleted");
			}
		}
		return query;
	}

	public void setHighlighter(boolean highlighter) {
		this.highlighter = highlighter;
	}

	protected void resetFacetSelection() {
		facetSelections.clear();
	}

	protected SavedSearch getSavedSearch(String id) {
		Record record = recordServices().getDocumentById(id);
		return new SavedSearch(record, types());
	}

	Metadata getMetadata(String code) {
		SchemaUtils utils = new SchemaUtils();
		String schemaCode = utils.getSchemaCode(code);
		return schema(schemaCode).getMetadata(utils.getLocalCode(code, schemaCode));
	}

	protected List<MetadataVO> getMetadataAllowedInAdvancedSearch(String schemaTypeCode) {
		MetadataToVOBuilder builder = new MetadataToVOBuilder();
		MetadataSchemaType schemaType = schemaType(schemaTypeCode);

		List<MetadataVO> result = new ArrayList<>();
		for (Metadata metadata : schemaType.getAllMetadatas()) {
			MetadataDisplayConfig config = schemasDisplayManager().getMetadata(view.getCollection(), metadata.getCode());
			if (config.isVisibleInAdvancedSearch()) {
				result.add(builder.build(metadata, view.getSessionContext()));
			}
		}
		return result;
	}

	protected MetadataVO getMetadataVO(String metadataCode) {
		return presenterService().getMetadataVO(metadataCode, view.getSessionContext());
	}

	protected List<MetadataVO> getMetadataAllowedInSort(String schemaTypeCode) {
		MetadataSchemaType schemaType = schemaType(schemaTypeCode);
		return getMetadataAllowedInSort(schemaType);
	}

	protected List<MetadataVO> getMetadataAllowedInSort(MetadataSchemaType schemaType) {
		MetadataToVOBuilder builder = new MetadataToVOBuilder();

		List<MetadataVO> result = new ArrayList<>();
		result.add(builder.build(schemaType.getMetadataWithAtomicCode(CommonMetadataBuilder.PATH), view.getSessionContext()));
		for (Metadata metadata : schemaType.getAllMetadatas()) {
			if (metadata.isSortable()) {
				result.add(builder.build(metadata, view.getSessionContext()));
			}
		}
		return result;
	}

	protected List<LabelTemplate> getTemplates() {
		return appLayerFactory.getLabelTemplateManager().listTemplates(null);
	}

	protected boolean saveSearch(String title, boolean publicAccess) {
		SavedSearch search = new SavedSearch(recordServices().newRecordWithSchema(schema(SavedSearch.DEFAULT_SCHEMA)), types())
				.setTitle(title)
				.setUser(getCurrentUser().getId())
				.setPublic(publicAccess)
				.setSortField(sortCriterion)
				.setSortOrder(SavedSearch.SortOrder.valueOf(sortOrder.name()))
				.setSelectedFacets(facetSelections.getNestedMap())
				.setTemporary(false);
		try {
			recordServices().add(prepareSavedSearch(search));
		} catch (RecordServicesException e) {
			view.showErrorMessage($("SearchView.errorSavingSearch"));
			return false;
		}
		view.showMessage($("SearchView.searchSaved"));
		return true;
	}

	protected abstract void saveTemporarySearch();

	protected SavedSearch prepareSavedSearch(SavedSearch search) {
		return search;
	}

	private SearchBoostManager searchBoostManager() {
		return modelLayerFactory.getSearchBoostManager();
	}

	private static List<String> excludedMetadatas = asList(Schemas.IDENTIFIER.getLocalCode(), Schemas.CREATED_ON.getLocalCode(),
			Schemas.MODIFIED_ON.getLocalCode(), RMObject.FORM_CREATED_ON, RMObject.FORM_MODIFIED_ON);

	public BatchProcessRequest toRequest(List<String> selectedRecord, RecordVO formVO) {

		String typeCode = new SchemaUtils().getSchemaTypeCode(formVO.getSchema().getCode());
		MetadataSchemaType type = coreSchemas().getTypes().getSchemaType(typeCode);
		MetadataSchema schema = coreSchemas().getTypes().getSchema(formVO.getSchema().getCode());
		Map<String, Object> fieldsModifications = new HashMap<>();
		for (MetadataVO metadataVO : formVO.getMetadatas()) {
			Metadata metadata = schema.get(metadataVO.getLocalCode());
			Object value = formVO.get(metadataVO);
			if (metadata.getDataEntry().getType() == DataEntryType.MANUAL
					&& value != null
					&& !metadata.isSystemReserved()
					&& !excludedMetadatas.contains(metadata.getLocalCode())) {
				fieldsModifications.put(metadataVO.getCode(), value);
			}
		}
		User user = getCurrentUser();

		return new BatchProcessRequest(selectedRecord, user, type, fieldsModifications);
	}
}
