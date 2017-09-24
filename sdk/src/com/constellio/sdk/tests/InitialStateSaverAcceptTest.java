package com.constellio.sdk.tests;

import org.junit.Test;

import com.constellio.app.modules.rm.DemoTestRecords;
import com.constellio.app.modules.rm.RMConfigs;
import com.constellio.app.modules.rm.RMTestRecords;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.wrappers.AdministrativeUnit;
import com.constellio.model.entities.records.Transaction;
import com.constellio.model.entities.schemas.MetadataValueType;
import com.constellio.model.services.factories.ModelLayerFactory;
import com.constellio.model.services.schemas.MetadataSchemaTypesAlteration;
import com.constellio.model.services.schemas.builders.MetadataSchemaTypesBuilder;
import com.constellio.sdk.tests.annotations.MainTest;
import com.constellio.sdk.tests.annotations.UiTest;

@UiTest
@MainTest
public class InitialStateSaverAcceptTest extends ConstellioTest {

	@Test
	public void saveCurrentInitialState()
			throws Exception {

		givenTransactionLogIsEnabled();
		givenCollection(zeCollection).withConstellioRMModule();

		getModelLayerFactory().getMetadataSchemasManager().modify(zeCollection, new MetadataSchemaTypesAlteration() {
			@Override
			public void alter(MetadataSchemaTypesBuilder types) {
				types.getSchemaType(AdministrativeUnit.SCHEMA_TYPE).getDefaultSchema().create("metadataWithInexistentCalculator")
						.setType(MetadataValueType.STRING).defineDataEntry().asCalculated(AnInexistentCalculatortest.class);
			}
		});

		Transaction tx = new Transaction();

		RMSchemasRecordsServices rm = new RMSchemasRecordsServices(zeCollection, getAppLayerFactory());
		AdministrativeUnit unit = tx.add(rm.newAdministrativeUnitWithId("unitId").setCode("code").setTitle("title"));

		getModelLayerFactory().newRecordServices().execute(tx);

		getSaveStateFeature().saveCurrentStateToInitialStatesFolder();
	}

	@Test
	public void saveModifiedState()
			throws Exception {

		givenTransactionLogIsEnabled();
		givenCollection(zeCollection).withConstellioRMModule().withAllTestUsers();

		getSaveStateFeature().saveStateAfterTestWithTitle("with_manual_modifications");

		ModelLayerFactory modelLayerFactory = getModelLayerFactory();
		//modelLayerFactory.getSystemConfigurationsManager().setValue(RMConfigs.DOCUMENT_RETENTION_RULES, true);

		newWebDriver(loggedAsUserInCollection(admin, zeCollection));
		waitUntilICloseTheBrowsers();

	}

	@Test
	public void saveWithEnterpriseSearchModule()
			throws Exception {

		givenTransactionLogIsEnabled();
		givenCollection(zeCollection).withConstellioESModule().withAllTestUsers();

		getSaveStateFeature().saveStateAfterTestWithTitle("with_enterprise_search_module");

		newWebDriver(loggedAsUserInCollection(admin, zeCollection));
		waitUntilICloseTheBrowsers();

	}

	@Test
	public void saveModifiedStateStartingFromNewSystem()
			throws Exception {

		givenTransactionLogIsEnabled();
		//		givenCollection(zeCollection).withConstellioRMModule().withAllTestUsers();
		//
		getSaveStateFeature().saveStateAfterTestWithTitle("from_new_system");

		newWebDriver();
		waitUntilICloseTheBrowsers();

	}

	@Test
	public void saveStateWithTestRecords()
			throws Exception {

		givenTransactionLogIsEnabled();
		givenCollection(zeCollection).withConstellioRMModule().withAllTestUsers();
		RMTestRecords records = new RMTestRecords(zeCollection);
		records.setup(getAppLayerFactory()).withFoldersAndContainersOfEveryStatus();
		givenCollection("zeDeuxiemeCollection").withConstellioRMModule().withAllTestUsers();
		DemoTestRecords records2 = new DemoTestRecords("zeDeuxiemeCollection");
		records2.setup(getAppLayerFactory()).withFoldersAndContainersOfEveryStatus();

		getSaveStateFeature().saveStateAfterTestWithTitle("with_document_rules");

		ModelLayerFactory modelLayerFactory = getModelLayerFactory();
		modelLayerFactory.getSystemConfigurationsManager().setValue(RMConfigs.DOCUMENT_RETENTION_RULES, true);

		newWebDriver(loggedAsUserInCollection(admin, zeCollection));
		waitUntilICloseTheBrowsers();

	}

}
