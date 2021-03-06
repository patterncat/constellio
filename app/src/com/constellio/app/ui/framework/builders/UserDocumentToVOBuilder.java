package com.constellio.app.ui.framework.builders;

import java.util.List;

import com.constellio.app.ui.entities.MetadataValueVO;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.entities.RecordVO.VIEW_MODE;
import com.constellio.app.ui.entities.UserDocumentVO;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.model.entities.records.Record;

public class UserDocumentToVOBuilder extends RecordToVOBuilder {

	@Override
	protected RecordVO newRecordVO(String id, List<MetadataValueVO> metadataValueVOs, VIEW_MODE viewMode) {
		return new UserDocumentVO(id, metadataValueVOs, viewMode);
	}

	@Override
	public UserDocumentVO build(Record record, VIEW_MODE viewMode, SessionContext sessionContext) {
		return (UserDocumentVO) super.build(record, viewMode, sessionContext);
	}

}
