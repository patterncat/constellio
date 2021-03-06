package com.constellio.app.ui.pages.imports;

import java.io.File;

import com.constellio.app.services.schemas.bulkImport.SchemaTypeImportServices;
import com.constellio.app.services.schemas.bulkImport.data.ImportDataProvider;
import com.constellio.app.services.schemas.bulkImport.data.ImportServices;
import com.constellio.app.services.schemas.bulkImport.data.xml.XMLImportSchemaTypesDataProvider;
import com.constellio.model.services.factories.ModelLayerFactory;

public class ImportSchemaTypesFilePresenter extends ImportFilePresenter {

	public ImportSchemaTypesFilePresenter(ImportFileView view) {
		super(view);
	}

	@Override
	protected ImportServices newImportServices(ModelLayerFactory modelLayerFactory) {
		return new SchemaTypeImportServices(appLayerFactory, view.getCollection());
	}

	@Override
	protected ImportDataProvider getXMLImportDataProviderForSingleXMLFile(ModelLayerFactory modelLayerFactory, File file,
			String fileName) {
		return XMLImportSchemaTypesDataProvider.forSingleXMLFile(modelLayerFactory, file, fileName);
	}

	@Override
	protected ImportDataProvider getXMLImportDataProviderForZipFile(ModelLayerFactory modelLayerFactory, File file) {
		return XMLImportSchemaTypesDataProvider.forZipFile(modelLayerFactory, file);
	}

	@Override
	protected ImportDataProvider getExcelImportDataProviderFromFile(File file) {
		return null;
	}
}
