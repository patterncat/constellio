package com.constellio.app.ui.pages.management.schemaRecords;

import static com.constellio.app.ui.i18n.i18n.$;
import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.from;

import com.constellio.app.ui.entities.MetadataSchemaVO;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.entities.RecordVO.VIEW_MODE;
import com.constellio.app.ui.framework.builders.MetadataSchemaToVOBuilder;
import com.constellio.app.ui.framework.builders.RecordToVOBuilder;
import com.constellio.app.ui.framework.data.RecordVODataProvider;
import com.constellio.app.ui.pages.base.SingleSchemaBasePresenter;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.services.records.RecordServicesRuntimeException.RecordServicesRuntimeException_CannotPhysicallyDeleteRecord;
import com.constellio.model.services.schemas.SchemaUtils;
import com.constellio.model.services.search.StatusFilter;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;

public class ListSchemaRecordsPresenter extends SingleSchemaBasePresenter<ListSchemaRecordsView> {

	private String schemaCode;

	public ListSchemaRecordsPresenter(ListSchemaRecordsView view) {
		super(view);
	}

	public void forSchema(String parameters) {
		schemaCode = parameters;
		setSchemaCode(schemaCode);
	}

	public RecordVODataProvider getDataProvider() {
		MetadataSchemaVO schemaVO = new MetadataSchemaToVOBuilder()
				.build(defaultSchema(), VIEW_MODE.TABLE, view.getSessionContext());
		RecordToVOBuilder voBuilder = new RecordToVOBuilder();
		RecordVODataProvider dataProvider = new RecordVODataProvider(
				schemaVO, voBuilder, modelLayerFactory, view.getSessionContext()) {
			@Override
			protected LogicalSearchQuery getQuery() {
				return new LogicalSearchQuery(from(defaultSchema()).returnAll())
						.filteredByStatus(StatusFilter.ACTIVES).sortAsc(Schemas.TITLE);
			}
		};
		return dataProvider;
	}

	public void displayButtonClicked(RecordVO recordVO) {
		view.navigate().to().displaySchemaRecord(recordVO.getId());
	}

	public void editButtonClicked(RecordVO recordVO) {
		String schemaCode = getSchemaCode();
		view.navigate().to().editSchemaRecord(schemaCode, recordVO.getId());
	}

	public void addLinkClicked() {
		String schemaCode = getSchemaCode();
		view.navigate().to().addSchemaRecord(schemaCode);
	}

	public void deleteButtonClicked(RecordVO recordVO) {
		if (isDeletable(recordVO)) {
			Record record = getRecord(recordVO.getId());
			try {
				delete(record, false);
			} catch (RecordServicesRuntimeException_CannotPhysicallyDeleteRecord error) {
			/*
				This catch happens to avoid presenting a message in the UI
				which wrongly tells the user that the deletion completely failed
				while it really succeeded, but only logically.
			 */
			}
			view.refreshTable();
		} else {
			view.showErrorMessage($("ListSchemaRecordsView.cannotDelete"));
		}

	}

	@Override
	protected boolean hasPageAccess(String params, final User user) {
		String schemaTypeCode = new SchemaUtils().getSchemaTypeCode(params);
		return new SchemaRecordsPresentersServices(appLayerFactory).canManageSchemaType(schemaTypeCode, user);
	}

	public void search(String freeText) {
		view.navigate().to().searchSchemaRecords(getSchemaCode(), freeText);
	}
}
