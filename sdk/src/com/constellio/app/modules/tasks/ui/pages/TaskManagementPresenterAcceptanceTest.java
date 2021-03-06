package com.constellio.app.modules.tasks.ui.pages;

import com.constellio.app.modules.tasks.model.wrappers.Task;
import com.constellio.app.modules.tasks.model.wrappers.structures.TaskFollower;
import com.constellio.app.modules.tasks.model.wrappers.types.TaskStatus;
import com.constellio.app.modules.tasks.services.TasksSchemasRecordsServices;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.framework.data.RecordVODataProvider;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.search.SearchServices;
import com.constellio.sdk.tests.ConstellioTest;
import com.constellio.sdk.tests.FakeSessionContext;
import com.constellio.sdk.tests.MockedNavigation;
import com.constellio.sdk.tests.setups.Users;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.Locale;

import static com.constellio.sdk.tests.TestUtils.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TaskManagementPresenterAcceptanceTest extends ConstellioTest {
	Users users = new Users();
	@Mock TaskManagementView view;
	MockedNavigation navigator;
	SessionContext sessionContext;
	private RecordServices recordServices;
	private Task zeTask;
	private TasksSchemasRecordsServices tasksSchemas;
	private SearchServices searchServices;
	TaskManagementPresenter presenter;

	private User aliceHasWriteAccessOnZeTask;
	private User bobHasReadAccessOnTask;
	private User chuckNorrisHasDeleteAccessOnTask;
	private User charlesWithNoAccessOnTask;

	@Before
	public void setUp()
			throws Exception {
		prepareSystem(withZeCollection().withTasksModule().withAllTest(users));

		recordServices = getModelLayerFactory().newRecordServices();
		searchServices = getModelLayerFactory().newSearchServices();

		sessionContext = FakeSessionContext.chuckNorrisInCollection(zeCollection);
		sessionContext.setCurrentLocale(Locale.FRENCH);

		when(view.getSessionContext()).thenReturn(sessionContext);
		when(view.getCollection()).thenReturn(zeCollection);
		when(view.getConstellioFactories()).thenReturn(getConstellioFactories());
		when(view.navigate()).thenReturn(navigator);
		when(view.getTimestamp()).thenReturn(TaskManagementViewImpl.Timestamp.ALL);

		bobHasReadAccessOnTask = users.bobIn(zeCollection);
		aliceHasWriteAccessOnZeTask = users.aliceIn(zeCollection);
		chuckNorrisHasDeleteAccessOnTask = users.chuckNorrisIn(zeCollection);
		charlesWithNoAccessOnTask = users.charlesIn(zeCollection);

		presenter = new TaskManagementPresenter(view);

		tasksSchemas = new TasksSchemasRecordsServices(zeCollection, getAppLayerFactory());
		zeTask = tasksSchemas.newTask();
		recordServices.add(zeTask.setTitle("zeTitle").setStatus(CLOSED())
				.setTaskFollowers(asList(new TaskFollower().setFollowerId(bobHasReadAccessOnTask.getId())))
				.setAssignee(aliceHasWriteAccessOnZeTask.getId()).setAssigner(chuckNorrisHasDeleteAccessOnTask.getId())
				.setAssignationDate(LocalDate.now())
				.setCreatedBy(chuckNorrisHasDeleteAccessOnTask.getId()));
	}

	@Test
	public void givenFolderIdMetadataWhenIsRecordIdMetadataThenReturnTrue()
			throws Exception {

		//assertThat(presenter.isRecordIdMetadata(metadataValueVO)).isTrue();
	}

	@Test
	public void givenWorkflowsAreActivatedThenOnlyUsersWithNeededPermissionCanSeeTheTab()
			throws Exception {
		sessionContext = FakeSessionContext.adminInCollection(zeCollection);
		when(view.getSessionContext()).thenReturn(sessionContext);
		TaskManagementPresenter presenter = Mockito.spy(new TaskManagementPresenter(view));
		doReturn(true).when(presenter).areWorkflowsEnabled();

		assertThat(presenter.getTabs()).contains(presenter.WORKFLOWS_STARTED);

		sessionContext = FakeSessionContext.aliceInCollection(zeCollection);
		presenter = Mockito.spy(new TaskManagementPresenter(view));
		assertThat(presenter.getTabs()).doesNotContain(presenter.WORKFLOWS_STARTED);
	}

	@Test
	public void givenUserCreatedClosedTaskThenVisibleInRecentlyClosedTask ()
			throws Exception {
		ArgumentCaptor<RecordVODataProvider> argumentCaptor = ArgumentCaptor.forClass(RecordVODataProvider.class);
		doNothing().when(view).displayTasks(argumentCaptor.capture());

		recordServices.add(zeTask.setStatus(CLOSED()).setAssignee(null).setAssignationDate(null)
				.setAssigneeGroupsCandidates(null).setAssigneeUsersCandidates(null).setAssigner(null));

		presenter.tabSelected(presenter.TASKS_RECENTLY_COMPLETED);

		RecordVODataProvider recordVODataProvider = argumentCaptor.getValue();
		List<RecordVO> recordVOList = recordVODataProvider.listRecordVOs(0, recordVODataProvider.size());

		assertThat(recordVOList).hasSize(1);
		assertThat(recordVOList.get(0).getId()).isEqualTo(zeTask.getId());
	}

	@Test
	public void givenUserAssignedToClosedTaskThenVisibleInRecentlyClosedTask ()
			throws Exception {
		sessionContext = FakeSessionContext.aliceInCollection(zeCollection);
		sessionContext.setCurrentLocale(Locale.FRENCH);

		ArgumentCaptor<RecordVODataProvider> argumentCaptor = ArgumentCaptor.forClass(RecordVODataProvider.class);
		doNothing().when(view).displayTasks(argumentCaptor.capture());

		recordServices.add(zeTask.setStatus(CLOSED()).setAssignee(null).setAssignationDate(null)
				.setAssigneeGroupsCandidates(null).setAssigneeUsersCandidates(null).setAssigner(null));

		presenter.tabSelected(presenter.TASKS_RECENTLY_COMPLETED);

		RecordVODataProvider recordVODataProvider = argumentCaptor.getValue();
		List<RecordVO> recordVOList = recordVODataProvider.listRecordVOs(0, recordVODataProvider.size());

		assertThat(recordVOList).hasSize(1);
		assertThat(recordVOList.get(0).getId()).isEqualTo(zeTask.getId());
	}

	@Test
	public void givenUserNotAssignedToClosedTaskAndIsNotTheCreatorThenInvisibleInRecentlyClosedTask ()
			throws Exception {
		sessionContext = FakeSessionContext.chuckNorrisInCollection(zeCollection);
		sessionContext.setCurrentLocale(Locale.FRENCH);

		ArgumentCaptor<RecordVODataProvider> argumentCaptor = ArgumentCaptor.forClass(RecordVODataProvider.class);
		doNothing().when(view).displayTasks(argumentCaptor.capture());

		recordServices.add(zeTask.setStatus(CLOSED()).setAssignee(null).setAssignationDate(null)
				.setAssigneeGroupsCandidates(null).setAssigneeUsersCandidates(null).setAssigner(null));

		presenter.tabSelected(presenter.TASKS_RECENTLY_COMPLETED);

		RecordVODataProvider recordVODataProvider = argumentCaptor.getValue();
		List<RecordVO> recordVOList = recordVODataProvider.listRecordVOs(0, recordVODataProvider.size());

		assertThat(recordVOList).hasSize(1);
		assertThat(recordVOList.get(0).getId()).isEqualTo(zeTask.getId());
	}

	public String CLOSED() {
		TaskStatus frenchType = tasksSchemas.getTaskStatusWithCode(TaskStatus.CLOSED_CODE);
		assertThat(frenchType.isAfterFinished()).isTrue();
		return frenchType.getId();
	}
}
