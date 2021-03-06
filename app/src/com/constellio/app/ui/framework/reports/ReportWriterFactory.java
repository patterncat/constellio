package com.constellio.app.ui.framework.reports;

import java.io.Serializable;

import com.constellio.model.services.factories.ModelLayerFactory;

public interface ReportWriterFactory extends Serializable {
	ReportWriter getReportBuilder(ModelLayerFactory modelLayerFactory);

	String getFilename();
}
