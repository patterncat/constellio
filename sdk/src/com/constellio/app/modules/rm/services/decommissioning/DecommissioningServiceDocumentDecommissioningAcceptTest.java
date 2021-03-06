package com.constellio.app.modules.rm.services.decommissioning;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import com.constellio.app.modules.rm.RMConfigs;
import com.constellio.app.modules.rm.RMConfigs.DecommissioningPhase;
import com.constellio.app.modules.rm.RMTestRecords;
import com.constellio.app.modules.rm.model.enums.DecommissioningListType;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.wrappers.DecommissioningList;
import com.constellio.app.modules.rm.wrappers.Document;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.services.configs.SystemConfigurationsManager;
import com.constellio.model.services.records.RecordServices;
import com.constellio.sdk.tests.ConstellioTest;
import com.constellio.sdk.tests.annotations.SlowTest;

public class DecommissioningServiceDocumentDecommissioningAcceptTest extends ConstellioTest {
	RMTestRecords records = new RMTestRecords(zeCollection);
	DecommissioningService service;
	RMSchemasRecordsServices rm;
	RecordServices recordServices;

	@Before
	public void setUp()
			throws Exception {
		prepareSystem(
				withZeCollection().withConstellioRMModule().withAllTestUsers().withRMTest(records)
						.withFoldersAndContainersOfEveryStatus().withDocumentsDecommissioningList()
		);

		rm = new RMSchemasRecordsServices(zeCollection, getAppLayerFactory());
		recordServices = getModelLayerFactory().newRecordServices();

		service = new DecommissioningService(zeCollection, getAppLayerFactory());
	}

	@Test
	public void givenTransferSearchThenCreateListToTransfer() {
		DecommissioningListParams params = new DecommissioningListParams();
		params.setTitle("Ze title");
		params.setDescription("Ze description");
		params.setAdministrativeUnit(records.unitId_10a);
		params.setSearchType(SearchType.documentTransfer);
		params.setSelectedRecordIds(Arrays.asList(records.folder_A10 + "_paperContractWithDifferentCopy"));

		DecommissioningList decommissioningList = service.createDecommissioningList(params, records.getChuckNorris());
		assertThat(decommissioningList.getTitle()).isEqualTo("Ze title");
		assertThat(decommissioningList.getDescription()).isEqualTo("Ze description");
		assertThat(decommissioningList.getAdministrativeUnit()).isEqualTo(records.unitId_10a);
		assertThat(decommissioningList.getDecommissioningListType()).isEqualTo(DecommissioningListType.DOCUMENTS_TO_TRANSFER);
		assertThat(decommissioningList.getDocuments()).containsExactly(records.folder_A10 + "_paperContractWithDifferentCopy");
		assertThat(decommissioningList.getFolderDetails()).isEmpty();
		assertThat(decommissioningList.getContainerDetails()).isEmpty();
	}

	@Test
	public void givenActiveToDepositSearchThenCreateListToDeposit() {
		DecommissioningListParams params = new DecommissioningListParams();
		params.setTitle("Ze title");
		params.setDescription("Ze description");
		params.setAdministrativeUnit(records.unitId_10a);
		params.setSearchType(SearchType.documentActiveToDeposit);
		params.setSelectedRecordIds(Arrays.asList(records.folder_A10 + "_paperContractWithDifferentCopy"));

		DecommissioningList decommissioningList = service.createDecommissioningList(params, records.getChuckNorris());
		assertThat(decommissioningList.getTitle()).isEqualTo("Ze title");
		assertThat(decommissioningList.getDescription()).isEqualTo("Ze description");
		assertThat(decommissioningList.getAdministrativeUnit()).isEqualTo(records.unitId_10a);
		assertThat(decommissioningList.getDecommissioningListType()).isEqualTo(DecommissioningListType.DOCUMENTS_TO_DEPOSIT);
		assertThat(decommissioningList.getDocuments()).containsExactly(records.folder_A10 + "_paperContractWithDifferentCopy");
		assertThat(decommissioningList.getFolderDetails()).isEmpty();
		assertThat(decommissioningList.getContainerDetails()).isEmpty();
	}

	@Test
	public void givenActiveToDestroySearchThenCreateListToDestroy() {
		DecommissioningListParams params = new DecommissioningListParams();
		params.setTitle("Ze title");
		params.setDescription("Ze description");
		params.setAdministrativeUnit(records.unitId_10a);
		params.setSearchType(SearchType.documentActiveToDestroy);
		params.setSelectedRecordIds(Arrays.asList(records.folder_A10 + "_paperContractWithDifferentCopy"));

		DecommissioningList decommissioningList = service.createDecommissioningList(params, records.getChuckNorris());
		assertThat(decommissioningList.getTitle()).isEqualTo("Ze title");
		assertThat(decommissioningList.getDescription()).isEqualTo("Ze description");
		assertThat(decommissioningList.getAdministrativeUnit()).isEqualTo(records.unitId_10a);
		assertThat(decommissioningList.getDecommissioningListType()).isEqualTo(DecommissioningListType.DOCUMENTS_TO_DESTROY);
		assertThat(decommissioningList.getDocuments()).containsExactly(records.folder_A10 + "_paperContractWithDifferentCopy");
		assertThat(decommissioningList.getFolderDetails()).isEmpty();
		assertThat(decommissioningList.getContainerDetails()).isEmpty();
	}

	@Test
	public void givenSemiActiveToDepositSearchThenCreateListToDeposit() {
		DecommissioningListParams params = new DecommissioningListParams();
		params.setTitle("Ze title");
		params.setDescription("Ze description");
		params.setAdministrativeUnit(records.unitId_10a);
		params.setSearchType(SearchType.documentSemiActiveToDeposit);
		params.setSelectedRecordIds(Arrays.asList(records.folder_A42 + "_paperContractWithDifferentCopy"));

		DecommissioningList decommissioningList = service.createDecommissioningList(params, records.getChuckNorris());
		assertThat(decommissioningList.getTitle()).isEqualTo("Ze title");
		assertThat(decommissioningList.getDescription()).isEqualTo("Ze description");
		assertThat(decommissioningList.getAdministrativeUnit()).isEqualTo(records.unitId_10a);
		assertThat(decommissioningList.getDecommissioningListType()).isEqualTo(DecommissioningListType.DOCUMENTS_TO_DEPOSIT);
		assertThat(decommissioningList.getDocuments()).containsExactly(records.folder_A42 + "_paperContractWithDifferentCopy");
		assertThat(decommissioningList.getFolderDetails()).isEmpty();
		assertThat(decommissioningList.getContainerDetails()).isEmpty();
	}

	@Test
	public void givenSemiActiveToDestroySearchThenCreateListToDestroy() {
		DecommissioningListParams params = new DecommissioningListParams();
		params.setTitle("Ze title");
		params.setDescription("Ze description");
		params.setAdministrativeUnit(records.unitId_10a);
		params.setSearchType(SearchType.documentSemiActiveToDestroy);
		params.setSelectedRecordIds(Arrays.asList(records.folder_A42 + "_paperContractWithDifferentCopy"));

		DecommissioningList decommissioningList = service.createDecommissioningList(params, records.getChuckNorris());
		assertThat(decommissioningList.getTitle()).isEqualTo("Ze title");
		assertThat(decommissioningList.getDescription()).isEqualTo("Ze description");
		assertThat(decommissioningList.getAdministrativeUnit()).isEqualTo(records.unitId_10a);
		assertThat(decommissioningList.getDecommissioningListType()).isEqualTo(DecommissioningListType.DOCUMENTS_TO_DESTROY);
		assertThat(decommissioningList.getDocuments()).containsExactly(records.folder_A42 + "_paperContractWithDifferentCopy");
		assertThat(decommissioningList.getFolderDetails()).isEmpty();
		assertThat(decommissioningList.getContainerDetails()).isEmpty();
	}

	@Test
	public void givenListToTransferThenAllDocumentsAreTransferred() {
		User processingUser = records.getChuckNorris();
		LocalDate processingDate = new LocalDate();
		givenTimeIs(processingDate);

		service.decommission(records.getList31(), processingUser);
		verifyProcessed(processingDate, processingUser, records.getList31());
		for (Document document : getListDocuments(records.getList31())) {
			assertThat(document.getActualTransferDateEntered()).isEqualTo(processingDate);
		}
	}

	@Test
	public void givenListOfActiveToDepositThenAllDocumentsAreDeposited() {
		User processingUser = records.getChuckNorris();
		LocalDate processingDate = new LocalDate();
		givenTimeIs(processingDate);

		service.decommission(records.getList34(), processingUser);
		verifyProcessed(processingDate, processingUser, records.getList34());
		for (Document document : getListDocuments(records.getList34())) {
			assertThat(document.getActualDepositDateEntered()).isEqualTo(processingDate);
		}
	}

	@Test
	public void givenListOfSemiActiveToDepositThenAllDocumentsAreDeposited() {
		User processingUser = records.getChuckNorris();
		LocalDate processingDate = new LocalDate();
		givenTimeIs(processingDate);

		service.decommission(records.getList33(), processingUser);
		verifyProcessed(processingDate, processingUser, records.getList33());
		for (Document document : getListDocuments(records.getList33())) {
			assertThat(document.getActualDepositDateEntered()).isEqualTo(processingDate);
		}
	}

	@Test
	public void givenListOfActiveToDestroyThenAllDocumentsAreDestroyed() {
		User processingUser = records.getChuckNorris();
		LocalDate processingDate = new LocalDate();
		givenTimeIs(processingDate);

		service.decommission(records.getList35(), processingUser);
		verifyProcessed(processingDate, processingUser, records.getList35());
		for (Document document : getListDocuments(records.getList35())) {
			assertThat(document.getActualDestructionDateEntered()).isEqualTo(processingDate);
		}
	}

	@Test
	public void givenListOfActiveToDestroyWhenDeletionEnabledThenAllDocumentsAreDeleted() {
		User processingUser = records.getChuckNorris();
		LocalDate processingDate = new LocalDate();
		givenTimeIs(processingDate);

		getConfigurationManager().setValue(RMConfigs.DELETE_DOCUMENT_RECORDS_WITH_DESTRUCTION, true);

		service.decommission(records.getList35(), processingUser);
		verifyProcessed(processingDate, processingUser, records.getList35());
		for (Document document : getListDocuments(records.getList35())) {
			assertThat(document.getActualDestructionDateEntered()).isEqualTo(processingDate);
			assertThat(document.isLogicallyDeletedStatus()).isTrue();
		}
	}

	@Test
	public void givenListOfSemiActiveToDestroyThenAllDocumentsAreDestroyed() {
		User processingUser = records.getChuckNorris();
		LocalDate processingDate = new LocalDate();
		givenTimeIs(processingDate);

		service.decommission(records.getList36(), processingUser);
		verifyProcessed(processingDate, processingUser, records.getList36());
		for (Document document : getListDocuments(records.getList36())) {
			assertThat(document.getActualDestructionDateEntered()).isEqualTo(processingDate);
		}
	}

	@Test
	public void givenListOfSemiActiveToDestroyWhenDeletionEnabledThenAllDocumentsAreDeleted() {
		User processingUser = records.getChuckNorris();
		LocalDate processingDate = new LocalDate();
		givenTimeIs(processingDate);

		getConfigurationManager().setValue(RMConfigs.DELETE_DOCUMENT_RECORDS_WITH_DESTRUCTION, true);

		service.decommission(records.getList36(), processingUser);
		verifyProcessed(processingDate, processingUser, records.getList36());
		for (Document document : getListDocuments(records.getList36())) {
			assertThat(document.getActualDestructionDateEntered()).isEqualTo(processingDate);
			assertThat(document.isLogicallyDeletedStatus()).isTrue();
		}
	}

	@Test
	@SlowTest
	public void givenListToTransferWhenCreatePDFaOnTransferThenPDFaCreated() {
		getConfigurationManager().setValue(RMConfigs.PDFA_CREATED_ON, DecommissioningPhase.ON_TRANSFER_OR_DEPOSIT);
		givenDisabledAfterTestValidations();
		service.decommission(records.getList31(), records.getChuckNorris());
		for (Document document : getListDocuments(records.getList31())) {
			if (document.getContent() != null) {
				assertThat(document.getContent().getCurrentVersion().getMimetype()).isEqualTo("application/pdf");
			}
		}
	}

	@Test
	@SlowTest
	public void givenListToDepositWhenCreatePDFaOnTransferThenPDFaCreated() {
		getConfigurationManager().setValue(RMConfigs.PDFA_CREATED_ON, DecommissioningPhase.ON_TRANSFER_OR_DEPOSIT);
		givenDisabledAfterTestValidations();
		service.decommission(records.getList34(), records.getChuckNorris());
		for (Document document : getListDocuments(records.getList34())) {
			if (document.getContent() != null) {
				assertThat(document.getContent().getCurrentVersion().getMimetype()).isEqualTo("application/pdf");
			}
		}
	}

	@Test
	@SlowTest
	public void givenListToDepositWhenCreatePDFaOnDepositThenPDFaCreated() {
		getConfigurationManager().setValue(RMConfigs.PDFA_CREATED_ON, DecommissioningPhase.ON_DEPOSIT);
		givenDisabledAfterTestValidations();
		service.decommission(records.getList33(), records.getChuckNorris());
		for (Document document : getListDocuments(records.getList33())) {
			if (document.getContent() != null) {
				assertThat(document.getContent().getCurrentVersion().getMimetype()).isEqualTo("application/pdf");
			}
		}
	}

	private void verifyProcessed(LocalDate processingDate, User processingUser, DecommissioningList decommissioningList) {
		assertThat(decommissioningList.getProcessingDate()).isEqualTo(processingDate);
		assertThat(decommissioningList.getProcessingUser()).isEqualTo(processingUser.getId());
	}

	private List<Document> getListDocuments(DecommissioningList list) {
		return rm.wrapDocuments(recordServices.getRecordsById(zeCollection, list.getDocuments()));
	}

	private SystemConfigurationsManager getConfigurationManager() {
		return getModelLayerFactory().getSystemConfigurationsManager();
	}
}
