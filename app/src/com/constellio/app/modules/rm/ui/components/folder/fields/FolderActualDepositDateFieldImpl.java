package com.constellio.app.modules.rm.ui.components.folder.fields;

import java.util.Date;

import org.joda.time.LocalDate;

import com.constellio.app.ui.framework.components.fields.date.JodaDateField;

public class FolderActualDepositDateFieldImpl extends JodaDateField implements FolderActualDepositDateField {

	@Override
	public LocalDate getFieldValue() {
		return (LocalDate) getConvertedValue();
	}

	@Override
	public void setFieldValue(Object value) {
		setInternalValue((Date) value);
	}

}
