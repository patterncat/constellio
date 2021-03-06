package com.constellio.app.ui.framework.components.fields.taxonomy;

import com.constellio.app.entities.schemasDisplay.enums.MetadataDisplayType;
import com.constellio.app.ui.application.ConstellioUI;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.framework.components.fields.ListOptionGroup;
import com.constellio.app.ui.pages.base.SessionContext;

import java.util.List;

public class TaxonomyOptionGroup extends ListOptionGroup implements TaxonomyField {

	private TaxonomyFieldPresenter presenter;

	private MetadataDisplayType metadataDisplayType;

	public TaxonomyOptionGroup(String taxonomyCode, String schemaTypeCode, MetadataDisplayType metadataDisplayType) {
		super();
		this.metadataDisplayType = metadataDisplayType;
		presenter = new TaxonomyFieldPresenter(this);
		presenter.forTaxonomyAndSchemaTypeCodes(taxonomyCode, schemaTypeCode);

	}

	@Override
	public void setOptions(List<RecordVO> recordVOs) {
		initStyleName();
		for (RecordVO recordVO : recordVOs) {
			addItem(recordVO.getId());
			setItemCaption(recordVO.getId(), recordVO.getTitle());
		}
	}

	@Override
	public SessionContext getSessionContext() {
		return ConstellioUI.getCurrentSessionContext();
	}

	public void initStyleName() {
		if(metadataDisplayType != null && metadataDisplayType.equals(MetadataDisplayType.HORIZONTAL)) {
			this.addStyleName("horizontal");
		}
		else {
			this.addStyleName("vertical");
		}
	};

}
