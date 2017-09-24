package com.constellio.app.services.metadata;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.Test;

import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.wrappers.AdministrativeUnit;
import com.constellio.model.conf.PropertiesModelLayerConfiguration.InMemoryModelLayerConfiguration;
import com.constellio.model.entities.schemas.entries.DataEntryType;
import com.constellio.model.services.schemas.builders.MetadataBuilderRuntimeException;
import com.constellio.sdk.tests.ConstellioTest;
import com.constellio.sdk.tests.ModelLayerConfigurationAlteration;

public class InexistentMetadataCalculatorsAcceptanceTest extends ConstellioTest {

	@Test(expected = MetadataBuilderRuntimeException.CannotInstanciateClass.class)
	public void givenLostCalculatorConfigDisabledThenSystemDoesNotStart()
			throws Exception {

		givenDisabledAfterTestValidations();
		givenTransactionLogIsEnabled();
		File state = getTestResourceFile("givenSystemWithInexistentMetadata.zip");

		getCurrentTestSession().getFactoriesTestFeatures().givenSystemInState(state);
		getAppLayerFactory();
	}

	@Test
	public void givenLostCalculatorConfigEnabledThenSystemStartAndRemoveValueWhenUpdatingRecords()
			throws Exception {

		configure(new ModelLayerConfigurationAlteration() {
			@Override
			public void alter(InMemoryModelLayerConfiguration configuration) {
				configuration.setExceptionWhenCalculatorOrValidatorNotFound(false);
			}
		});
		givenDisabledAfterTestValidations();
		givenTransactionLogIsEnabled();
		File state = getTestResourceFile("givenSystemWithInexistentMetadata.zip");

		getCurrentTestSession().getFactoriesTestFeatures().givenSystemInState(state);

		RMSchemasRecordsServices rm = new RMSchemasRecordsServices(zeCollection, getAppLayerFactory());
		AdministrativeUnit unit = rm.getAdministrativeUnit("unitId");
		assertThat(unit.<String>get("metadataWithInexistentCalculator")).isEqualTo("This is ze title : title");

		getModelLayerFactory().newRecordServices().update(unit.setTitle("New title"));
		//Not updated since it is now a manual field
		assertThat(unit.<String>get("metadataWithInexistentCalculator")).isEqualTo("This is ze title : title");
		assertThat(rm.administrativeUnit.schema().getMetadata("metadataWithInexistentCalculator").getDataEntry().getType())
				.isEqualTo(DataEntryType.MANUAL);
	}
}
