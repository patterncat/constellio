package com.constellio.app.ui.framework.components.fields.record;

import java.util.List;

import com.constellio.app.ui.application.ConstellioUI;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.framework.components.converters.RecordIdToCaptionConverter;
import com.constellio.app.ui.framework.components.fields.BaseComboBox;
import com.constellio.app.ui.framework.data.RecordVODataProvider;
import com.constellio.app.ui.pages.base.SessionContext;

public class RecordComboBox extends BaseComboBox implements RecordOptionField {
	
	private RecordIdToCaptionConverter captionConverter = new RecordIdToCaptionConverter();
	
	private RecordOptionFieldPresenter presenter;
	
	public RecordComboBox(String schemaCode) {
		super();
		this.presenter = new RecordOptionFieldPresenter(this);
		this.presenter.forSchemaCode(schemaCode);
	}

	@Override
	public void setDataProvider(RecordVODataProvider dataProvider) {
		int size = dataProvider.size();
		List<RecordVO> records = dataProvider.listRecordVOs(0, size);
		for (RecordVO recordVO : records) {
			String recordId = recordVO.getId();
			String itemCaption = captionConverter.convertToPresentation(recordId, String.class, getLocale());
			addItem(recordId);
			setItemCaption(recordId, itemCaption);
		}
	}
	
	@Override
	public SessionContext getSessionContext() {
		return ConstellioUI.getCurrentSessionContext();
	}

}
