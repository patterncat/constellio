package com.constellio.app.modules.rm.ui.pages.decommissioning;

import static com.constellio.app.ui.i18n.i18n.$;
import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.allConditions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.constellio.app.modules.rm.constants.RMPermissionsTo;
import com.constellio.app.modules.rm.navigation.RMViews;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.services.decommissioning.DecommissioningListParams;
import com.constellio.app.modules.rm.services.decommissioning.DecommissioningSearchConditionFactory;
import com.constellio.app.modules.rm.services.decommissioning.DecommissioningService;
import com.constellio.app.modules.rm.services.decommissioning.SearchType;
import com.constellio.app.modules.rm.wrappers.DecommissioningList;
import com.constellio.app.modules.rm.wrappers.Document;
import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.app.ui.entities.MetadataVO;
import com.constellio.app.ui.pages.search.AdvancedSearchCriteriaComponent.SearchCriteriaPresenter;
import com.constellio.app.ui.pages.search.SearchPresenter;
import com.constellio.app.ui.pages.search.criteria.ConditionBuilder;
import com.constellio.app.ui.pages.search.criteria.ConditionException;
import com.constellio.app.ui.pages.search.criteria.ConditionException.ConditionException_EmptyCondition;
import com.constellio.app.ui.pages.search.criteria.ConditionException.ConditionException_TooManyClosedParentheses;
import com.constellio.app.ui.pages.search.criteria.ConditionException.ConditionException_UnclosedParentheses;
import com.constellio.app.ui.pages.search.criteria.Criterion;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.schemas.MetadataSchemaType;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.services.search.query.logical.condition.LogicalSearchCondition;

public class DecommissioningBuilderPresenter extends SearchPresenter<DecommissioningBuilderView>
		implements SearchCriteriaPresenter {
	private transient LogicalSearchCondition condition;
	private transient RMSchemasRecordsServices rmRecordServices;
	private transient DecommissioningService decommissioningService;

	SearchType searchType;
	String adminUnitId;

	public DecommissioningBuilderPresenter(DecommissioningBuilderView view) {
		super(view);
	}

	@Override
	public DecommissioningBuilderPresenter forRequestParameters(String params) {
		searchType = SearchType.valueOf(params);
		view.setCriteriaSchemaType(getSchemaType());
		view.addEmptyCriterion();
		view.addEmptyCriterion();
		return this;
	}

	@Override
	protected boolean hasPageAccess(String params, User user) {
		return user.has(RMPermissionsTo.PROCESS_DECOMMISSIONING_LIST).onSomething();
	}

	@Override
	public boolean mustDisplayResults() {
		return false;
	}

	@Override
	public int getPageNumber() {
		return 1;
	}

	@Override
	public void suggestionSelected(String suggestion) {
		// Do nothing
	}

	public SearchType getSearchType() {
		return searchType;
	}

	public void searchRequested() {
		try {
			buildSearchCondition();
			resetFacetSelection();
			view.refreshSearchResultsAndFacets();
		} catch (ConditionException_EmptyCondition e) {
			view.showErrorMessage($("AdvancedSearchView.emptyCondition"));
		} catch (ConditionException_TooManyClosedParentheses e) {
			view.showErrorMessage($("AdvancedSearchView.tooManyClosedParentheses"));
		} catch (ConditionException_UnclosedParentheses e) {
			view.showErrorMessage($("AdvancedSearchView.unclosedParentheses"));
		} catch (ConditionException e) {
			throw new RuntimeException("BUG: Uncaught ConditionException", e);
		}
	}

	public void decommissioningListCreationRequested(DecommissioningListParams params) {
		DecommissioningService decommissioningService = new DecommissioningService(view.getCollection(), modelLayerFactory);
		params.setAdministrativeUnit(adminUnitId);
		params.setSearchType(searchType);
		try {
			DecommissioningList decommissioningList = decommissioningService.createDecommissioningList(params, getCurrentUser());
			if (decommissioningList.getDecommissioningListType().isFolderList()) {
				view.navigate().to(RMViews.class).displayDecommissioningList(decommissioningList.getId());
			} else {
				view.navigate().to(RMViews.class).displayDocumentDecommissioningList(decommissioningList.getId());
			}
		} catch (Exception e) {
			view.showErrorMessage($("DecommissioningBuilderView.unableToSave"));
		}
	}

	public List<SelectItemVO> getAdministrativeUnits() {
		User currentUser = presenterService().getCurrentUser(view.getSessionContext());
		List<SelectItemVO> results = new ArrayList<>();
		List<String> conceptIds = decommissioningService().getAdministrativeUnitsForUser(currentUser);
		List<Record> concepts = recordServices().getRecordsById(currentUser.getCollection(), conceptIds);
		for (Record record : concepts) {
			results.add(new SelectItemVO(record.getId(), (String) record.get(Schemas.TITLE)));
		}
		return results;
	}

	@Override
	public void addCriterionRequested() {
		view.addEmptyCriterion();
	}

	public void administrativeUnitSelected(String adminUnitId) {
		this.adminUnitId = adminUnitId;
	}

	@Override
	public List<MetadataVO> getMetadataAllowedInCriteria() {
		return getMetadataAllowedInAdvancedSearch(getSchemaType());
	}

	@Override
	public MetadataVO getMetadataVO(String metadataCode) {
		return super.getMetadataVO(metadataCode);
	}

	@Override
	protected boolean saveSearch(String title, boolean publicAccess) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<MetadataVO> getMetadataAllowedInSort() {
		return getMetadataAllowedInSort(getSchemaType());
	}

	@Override
	protected LogicalSearchCondition getSearchCondition() {
		if (condition == null) {
			try {
				buildSearchCondition();
			} catch (ConditionException e) {
				throw new RuntimeException("Unexpected exception (should be unreachable)", e);
			}
		}
		return condition;
	}

	void buildSearchCondition()
			throws ConditionException {
		List<Criterion> criteria = view.getSearchCriteria();
		if (criteria.isEmpty()) {
			condition = selectByDecommissioningStatus();
		} else {
			condition = allConditions(selectByDecommissioningStatus(), selectByAdvancedSearchCriteria(criteria));
		}
	}

	private String getSchemaType() {
		return searchType.isFolderSearch() ? Folder.SCHEMA_TYPE : Document.SCHEMA_TYPE;
	}

	private LogicalSearchCondition selectByDecommissioningStatus() {
		return new DecommissioningSearchConditionFactory(view.getCollection(), modelLayerFactory)
				.bySearchType(searchType, adminUnitId);
	}

	private LogicalSearchCondition selectByAdvancedSearchCriteria(List<Criterion> criteria)
			throws ConditionException {
		MetadataSchemaType type = searchType.isFolderSearch() ?
				rmRecordServices().folderSchemaType() : rmRecordServices().documentSchemaType();
		return new ConditionBuilder(type).build(criteria);
	}

	private DecommissioningService decommissioningService() {
		if (decommissioningService == null) {
			decommissioningService = new DecommissioningService(view.getCollection(), modelLayerFactory);
		}
		return decommissioningService;
	}

	private RMSchemasRecordsServices rmRecordServices() {
		if (rmRecordServices == null) {
			rmRecordServices = new RMSchemasRecordsServices(view.getCollection(), modelLayerFactory);
		}
		return rmRecordServices;
	}

	public static class SelectItemVO implements Serializable {
		private final String id;
		private final String label;

		public SelectItemVO(String id, String label) {
			this.id = id;
			this.label = label;
		}

		public String getId() {
			return id;
		}

		public String getLabel() {
			return label;
		}
	}
}
