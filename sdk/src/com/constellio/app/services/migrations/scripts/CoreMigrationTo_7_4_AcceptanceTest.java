package com.constellio.app.services.migrations.scripts;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.model.entities.calculators.JEXLMetadataValueCalculator;
import com.constellio.model.entities.calculators.MetadataValueCalculator;
import com.constellio.model.entities.schemas.entries.CalculatedDataEntry;
import com.constellio.model.entities.schemas.entries.DataEntryType;
import com.constellio.model.services.records.extractions.RecordPopulateServices;
import com.constellio.model.services.schemas.MetadataSchemasManager;
import com.constellio.sdk.tests.ConstellioTest;

public class CoreMigrationTo_7_4_AcceptanceTest extends ConstellioTest {

	@Test
	public void startApplicationWithSaveState() {
		RecordPopulateServices.LOG_CONTENT_MISSING = false;
		givenTransactionLogIsEnabled();

		getCurrentTestSession().getFactoriesTestFeatures()
				.givenSystemInState(getTestResourceFile("saveStateWithTemporaryMetadata.zip")).withPasswordsReset()
				.withFakeEncryptionServices();
		MetadataSchemasManager manager = getModelLayerFactory().getMetadataSchemasManager();
		assertThat(manager.getSchemaTypes(zeCollection).getSchema(Folder.DEFAULT_SCHEMA).getMetadata("temporaryMetadata"))
				.isNotNull();
		assertThat(manager.getSchemaTypes(zeCollection).getSchema(Folder.DEFAULT_SCHEMA).getMetadata("temporaryMetadata")
				.getDataEntry().getType()).isEqualTo(DataEntryType.CALCULATED);

		MetadataValueCalculator<?> calculator = ((CalculatedDataEntry) manager.getSchemaTypes(zeCollection)
				.getSchema(Folder.DEFAULT_SCHEMA).getMetadata("temporaryMetadata").getDataEntry()).getCalculator();
		String currentScript = ((JEXLMetadataValueCalculator) calculator).getExpression();
		assertThat(currentScript).isEqualTo("#STRICT:title + '-test'");
	}
}
