package com.constellio.app.modules.tasks.ui.pages;

import static com.constellio.app.ui.entities.RecordVO.VIEW_MODE.FORM;
import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.from;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.constellio.app.modules.tasks.model.wrappers.Task;
import com.constellio.app.modules.tasks.navigation.TasksNavigationConfiguration;
import com.constellio.app.modules.tasks.services.TasksSchemasRecordsServices;
import com.constellio.app.modules.tasks.ui.entities.TaskVO;
import com.constellio.app.modules.tasks.ui.pages.tasks.AddEditTaskPresenter;
import com.constellio.app.modules.tasks.ui.pages.tasks.AddEditTaskView;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.framework.builders.RecordToVOBuilder;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.app.ui.params.ParamUtils;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.search.SearchServices;
import com.constellio.sdk.tests.ConstellioTest;
import com.constellio.sdk.tests.FakeSessionContext;
import com.constellio.sdk.tests.SDKViewNavigation;
import com.constellio.sdk.tests.setups.Users;

public class AddEditTaskPresenterAcceptanceTest extends ConstellioTest {
	Users users = new Users();
	@Mock
	AddEditTaskView view;
	SessionContext sessionContext;
	private RecordServices recordServices;
	private SearchServices searchServices;
	private TasksSchemasRecordsServices tasksSchemas;
	AddEditTaskPresenter presenter;
	private LocalDate shishDate = LocalDate.now().minusDays(1);

	@Before
	public void setUp()
			throws Exception {
		prepareSystem(withZeCollection().withTasksModule().withAllTest(users));
		givenTimeIs(shishDate);

		recordServices = getModelLayerFactory().newRecordServices();
		searchServices = getModelLayerFactory().newSearchServices();
		tasksSchemas = new TasksSchemasRecordsServices(zeCollection, getAppLayerFactory());

		sessionContext = FakeSessionContext.chuckNorrisInCollection(zeCollection);
		sessionContext.setCurrentLocale(Locale.FRENCH);
		sessionContext.setCurrentCollection(zeCollection);
		recordServices.add(users.chuckNorrisIn(zeCollection).setCollectionWriteAccess(true).setCollectionReadAccess(true));

		when(view.getSessionContext()).thenReturn(sessionContext);
		when(view.getCollection()).thenReturn(zeCollection);
		when(view.getConstellioFactories()).thenReturn(getConstellioFactories());
		new SDKViewNavigation(view);

		presenter = new AddEditTaskPresenter(view);
	}

	@Test
	public void whenAddSubTaskThenTaskParentIdSetCorrectly()
			throws Exception {
		Map<String, String> params = new HashMap<>();
		String parentTaskId = "zeParentTask";
		params.put("parentId", parentTaskId);
		String viewPath = ParamUtils.addParams(TasksNavigationConfiguration.ADD_TASK, params);
		presenter.initTaskVO(viewPath);
		assertThat(presenter.getTask().get(Task.PARENT_TASK)).isEqualTo(parentTaskId);
	}

	@Test
	public void whenEditTaskThenTaskLoadedCorrectly()
			throws Exception {
		Task zeTask = tasksSchemas.newTask().setTitle("zeTask");
		recordServices.add(zeTask.getWrappedRecord());
		zeTask = tasksSchemas.getTask(zeTask.getId());

		Map<String, String> parameters = new HashMap<>();
		parameters.put("id", zeTask.getId());
		String viewPath = ParamUtils.addParams(TasksNavigationConfiguration.EDIT_TASK, parameters);
		presenter.initTaskVO(viewPath);
		TaskVO taskVO = presenter.getTask();
		assertThat(taskVO.getAssignee()).isEqualTo(null);
		assertThat(taskVO.getTitle()).isEqualTo("zeTask");
	}

	@Test
	public void whenAddTaskThenAssigneeSetToCurrentUserAndDueDateSetByDefaultToCurrentDate()
			throws Exception {
		String viewPath = ParamUtils.addParams(TasksNavigationConfiguration.ADD_TASK, new HashMap<String, String>());
		presenter.initTaskVO(viewPath);

		TaskVO taskVO = presenter.getTask();
		assertThat(taskVO.getAssignee()).isEqualTo(getSessionCurrentUserId());
		assertThat(taskVO.getDueDate()).isEqualTo(shishDate);
	}

	@Test
	public void givenTaskWithModifiedAssigneeAndAssignationDateWhenSaveTaskThenAssigneeAndAssignationDateSaved()
			throws Exception {
		String bobId = users.bobIn(zeCollection).getId();
		String aliceId = users.aliceIn(zeCollection).getId();
		Task zeTask = tasksSchemas.newTask().setTitle("zeTask").setAssignee(aliceId).setAssignationDate(shishDate.minusDays(1))
				.setAssigner(bobId);

		RecordVO taskVO = new RecordToVOBuilder().build(zeTask.getWrappedRecord(), FORM, sessionContext);
		presenter.saveButtonClicked(taskVO);

		Task reloadedTask = tasksSchemas
				.wrapTask(searchServices.searchSingleResult(from(tasksSchemas.userTask.schema()).returnAll()));
		assertThat(reloadedTask.getAssignee()).isEqualTo(aliceId);
		assertThat(reloadedTask.getAssigner()).isEqualTo(getSessionCurrentUserId());
		assertThat(reloadedTask.getAssignedOn()).isEqualTo(shishDate);
	}

	@Test
	public void givenTaskWithUnmodifiedAssigneeWhenSaveTaskThenAssignerAndAssignationDateNotSet()
			throws Exception {
		String bobId = users.bobIn(zeCollection).getId();
		String aliceId = users.aliceIn(zeCollection).getId();
		Task zeTask = tasksSchemas.newTask().setTitle("zeTask").setAssignee(aliceId).setAssignationDate(shishDate.minusDays(1))
				.setAssigner(bobId);
		recordServices.add(zeTask);
		zeTask = tasksSchemas.getTask(zeTask.getId());

		RecordVO taskVO = new RecordToVOBuilder().build(zeTask.getWrappedRecord(), FORM, sessionContext);
		presenter.saveButtonClicked(taskVO);

		Task reloadedTask = tasksSchemas.getTask(zeTask.getId());
		assertThat(reloadedTask.getAssignee()).isEqualTo(aliceId);
		assertThat(reloadedTask.getAssigner()).isEqualTo(bobId);
		assertThat(reloadedTask.getAssignedOn()).isEqualTo(shishDate.minusDays(1));
	}

	@Test
	public void givenTaskWithoutAssigneeWhenSavedThenAssignationDateAndAssignerAreNull()
			throws Exception {
		Task zeTask = tasksSchemas.newTask().setTitle("zeTask");
		RecordVO taskVO = new RecordToVOBuilder().build(zeTask.getWrappedRecord(), FORM, sessionContext);
		presenter.saveButtonClicked(taskVO);
		Task reloadedTask = tasksSchemas
				.wrapTask(searchServices.searchSingleResult(from(tasksSchemas.userTask.schema()).returnAll()));
		assertThat(reloadedTask.getTitle()).isEqualTo("zeTask");
		assertThat(reloadedTask.getAssignee()).isNull();
		assertThat(reloadedTask.getAssignedOn()).isNull();
		assertThat(reloadedTask.getAssigner()).isNull();
	}

	@Test
	public void givenTaskWithAssigneeWhenSavedThenAssignationDateAndAssignerAreSetCorrectly()
			throws Exception {
		Task zeTask = tasksSchemas.newTask().setTitle("zeTask").setAssignee(users.aliceIn(zeCollection).getId());
		RecordVO taskVO = new RecordToVOBuilder().build(zeTask.getWrappedRecord(), FORM, sessionContext);
		presenter.saveButtonClicked(taskVO);
		Task reloadedTask = tasksSchemas
				.wrapTask(searchServices.searchSingleResult(from(tasksSchemas.userTask.schema()).returnAll()));
		assertThat(reloadedTask.getTitle()).isEqualTo("zeTask");
		assertThat(reloadedTask.getAssignee()).isEqualTo(users.aliceIn(zeCollection).getId());
		assertThat(reloadedTask.getAssignedOn()).isEqualTo(shishDate);
		assertThat(reloadedTask.getAssigner()).isEqualTo(getSessionCurrentUserId());

	}

	@Test
	public void whenCancelThenTaskNotSaved()
			throws Exception {
		String viewPath = ParamUtils.addParams(TasksNavigationConfiguration.ADD_TASK, new HashMap<String, String>());
		presenter.initTaskVO(viewPath);
		presenter.cancelButtonClicked();
		assertThat(searchServices.getResultsCount(from(tasksSchemas.userTask.schema()).returnAll())).isEqualTo(0);
	}

	protected String getSessionCurrentUserId() {
		//sessionContext.getCurrentUser().getid() dos not work because user reloaded
		return tasksSchemas
				.wrapUser(searchServices.searchSingleResult(
						from(tasksSchemas.userSchema()).where(tasksSchemas.userSchema().getMetadata(User.USERNAME))
								.isEqualTo(sessionContext.getCurrentUser().getUsername()))).getId();
	}

}
