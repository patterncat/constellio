package com.constellio.app.modules.tasks.ui.pages;

import static com.constellio.app.modules.tasks.model.wrappers.Task.*;
import static com.constellio.app.modules.tasks.model.wrappers.Task.ASSIGNEE;
import static com.constellio.app.modules.tasks.model.wrappers.Task.ASSIGNER;
import static com.constellio.app.modules.tasks.model.wrappers.Task.DUE_DATE;
import static com.constellio.app.ui.i18n.i18n.$;
import static com.constellio.model.entities.records.wrappers.RecordWrapper.TITLE;

import java.io.IOException;
import java.util.*;

import com.constellio.app.api.extensions.params.UpdateComponentExtensionParams;
import com.constellio.app.modules.rm.RMConfigs;
import com.constellio.app.modules.tasks.TasksPermissionsTo;
import com.constellio.app.modules.tasks.model.wrappers.BetaWorkflow;
import com.constellio.app.modules.tasks.model.wrappers.BetaWorkflowInstance;
import com.constellio.app.modules.tasks.model.wrappers.Task;
import com.constellio.app.modules.tasks.model.wrappers.request.RequestTask;
import com.constellio.app.modules.tasks.model.wrappers.types.TaskStatus;
import com.constellio.app.modules.tasks.navigation.TaskViews;
import com.constellio.app.modules.tasks.services.TaskPresenterServices;
import com.constellio.app.modules.tasks.services.TasksSchemasRecordsServices;
import com.constellio.app.modules.tasks.services.TasksSearchServices;
import com.constellio.app.modules.tasks.services.BetaWorkflowServices;
import com.constellio.app.modules.tasks.ui.builders.TaskToVOBuilder;
import com.constellio.app.modules.tasks.ui.components.TaskFieldFactory;
import com.constellio.app.modules.tasks.ui.components.TaskTable.TaskPresenter;
import com.constellio.app.modules.tasks.ui.components.WorkflowTable.WorkflowPresenter;
import com.constellio.app.modules.tasks.ui.components.fields.TaskDecisionField;
import com.constellio.app.modules.tasks.ui.components.window.QuickCompleteWindow;
import com.constellio.app.modules.tasks.ui.entities.TaskVO;
import com.constellio.app.ui.application.ConstellioUI;
import com.constellio.app.ui.entities.MetadataSchemaVO;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.entities.RecordVO.VIEW_MODE;
import com.constellio.app.ui.framework.builders.MetadataSchemaToVOBuilder;
import com.constellio.app.ui.framework.builders.RecordToVOBuilder;
import com.constellio.app.ui.framework.buttons.report.ReportGeneratorButton;
import com.constellio.app.ui.framework.data.RecordVODataProvider;
import com.constellio.app.ui.pages.base.BaseView;
import com.constellio.app.ui.pages.base.BaseViewImpl;
import com.constellio.app.ui.pages.base.SingleSchemaBasePresenter;
import com.constellio.app.ui.pages.management.Report.PrintableReportListPossibleType;
import com.constellio.model.entities.Language;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.structures.MapStringStringStructure;
import com.constellio.model.services.records.RecordServicesException;
import com.constellio.model.services.schemas.SchemaUtils;
import com.constellio.model.services.records.RecordServicesException;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;
import com.constellio.model.services.search.query.logical.LogicalSearchQuerySort;
import com.vaadin.ui.Component;
import com.vaadin.data.Validator;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.joda.time.LocalDate;

public class TaskManagementPresenter extends SingleSchemaBasePresenter<TaskManagementView>
		implements TaskPresenter, WorkflowPresenter {
	public static final String TASKS_ASSIGNED_BY_CURRENT_USER = "tasksAssignedByCurrentUser";
	public static final String TASKS_NOT_ASSIGNED = "nonAssignedTasks";
	public static final String TASKS_ASSIGNED_TO_CURRENT_USER = "tasksAssignedToCurrentUser";
	public static final String TASKS_RECENTLY_COMPLETED = "recentlyCompletedTasks";
	public static final String WORKFLOWS_STARTED = "startedWorkflows";

	private transient TasksSearchServices tasksSearchServices;
	private transient TaskPresenterServices taskPresenterServices;
	private transient BetaWorkflowServices workflowServices;
	private RecordVODataProvider provider;

	public TaskManagementPresenter(TaskManagementView view) {
		super(view, DEFAULT_SCHEMA);
		initTransientObjects();
	}

	@Override
	protected boolean hasPageAccess(String params, User user) {
		return true;
	}

	public List<String> getTabs() {
		List<String> tabs = new ArrayList<>();
		tabs.add(TASKS_ASSIGNED_TO_CURRENT_USER);
		tabs.add(TASKS_ASSIGNED_BY_CURRENT_USER);
		tabs.add(TASKS_NOT_ASSIGNED);
		tabs.add(TASKS_RECENTLY_COMPLETED);

		if (areWorkflowsEnabled() && getCurrentUser().has(TasksPermissionsTo.MANAGE_WORKFLOWS).globally()) {
			tabs.add(WORKFLOWS_STARTED);
		}

		return tabs;
	}

	public void tabSelected(String tabId) {
		if (isWorkflowTab(tabId)) {
			provider = getWorkflowInstances(tabId);
			view.displayWorkflows(provider);
		} else if (isTaskTab(tabId)) {
			provider = getTasks(tabId);
			view.displayTasks(provider);
		} else {
			UpdateComponentExtensionParams params = new UpdateComponentExtensionParams((Component) view, view.getSelectedTab());
			appCollectionExtentions.updateComponent(params);
		}
	}

	public void addTaskButtonClicked() {
		view.navigate().to(TaskViews.class).addTask();
	}

	public String getTabCaption(String tabId) {
		return $("TasksManagementView.tab." + tabId);
	}

	private void refreshCurrentTab() {
		view.reloadCurrentTab();
	}

	@Override
	public void displayButtonClicked(RecordVO record) {
		view.navigate().to(TaskViews.class).displayTask(record.getId());
	}

	@Override
	public void editButtonClicked(RecordVO record) {
		view.navigate().to().editTask(record.getId());
	}

	@Override
	public void deleteButtonClicked(RecordVO record) {
		taskPresenterServices.deleteTask(toRecord(record), getCurrentUser());
		view.reloadCurrentTab();
	}

	@Override
	public void completeButtonClicked(RecordVO record) {
		view.navigate().to().editTask(record.getId(), true);
	}

	@Override
	public void closeButtonClicked(RecordVO record) {
		taskPresenterServices.closeTask(toRecord(record), getCurrentUser());
		refreshCurrentTab();
	}

	@Override
	public boolean isTaskOverdue(TaskVO taskVO) {
		return taskPresenterServices.isTaskOverdue(taskVO);
	}

	@Override
	public boolean isFinished(TaskVO taskVO) {
		return taskPresenterServices.isFinished(taskVO);
	}

	@Override
	public void autoAssignButtonClicked(RecordVO recordVO) {
		taskPresenterServices.autoAssignTask(toRecord(recordVO), getCurrentUser());
		refreshCurrentTab();
	}

	@Override
	public boolean isAutoAssignButtonEnabled(RecordVO recordVO) {
		return taskPresenterServices.isAutoAssignButtonEnabled(toRecord(recordVO), getCurrentUser());
	}

	@Override
	public boolean isEditButtonEnabled(RecordVO recordVO) {
		return taskPresenterServices.isEditTaskButtonVisible(toRecord(recordVO), getCurrentUser());
	}

	@Override
	public boolean isCompleteButtonEnabled(RecordVO recordVO) {
		return taskPresenterServices.isCompleteTaskButtonVisible(toRecord(recordVO), getCurrentUser());
	}

	@Override
	public boolean isCloseButtonEnabled(RecordVO recordVO) {
		return taskPresenterServices.isCloseTaskButtonVisible(toRecord(recordVO), getCurrentUser());
	}

	@Override
	public boolean isDeleteButtonEnabled(RecordVO recordVO) {
		return taskPresenterServices.isDeleteTaskButtonVisible(toRecord(recordVO), getCurrentUser());
	}

	@Override
	public boolean isDeleteButtonVisible(RecordVO entity) {
		return taskPresenterServices.isDeleteTaskButtonVisible(toRecord(entity), getCurrentUser());
	}

	@Override
	public void displayWorkflowInstanceRequested(RecordVO recordVO) {
		view.navigate().to(TaskViews.class).displayWorkflowInstance(recordVO.getId());
	}

	@Override
	public void cancelWorkflowInstanceRequested(RecordVO record) {
		BetaWorkflowInstance instance = new TasksSchemasRecordsServices(view.getCollection(), appLayerFactory)
				.getBetaWorkflowInstance(record.getId());
		workflowServices.cancel(instance);
		refreshCurrentTab();
	}

	@Override
	public void generateReportButtonClicked(RecordVO recordVO) {
		ReportGeneratorButton button = new ReportGeneratorButton($("ReportGeneratorButton.buttonText"), $("Générer un rapport de métadonnées"), view, appLayerFactory, collection, PrintableReportListPossibleType.TASK, recordVO);
		button.click();
	}

	public RecordVODataProvider getWorkflows() {
		MetadataSchemaVO schemaVO = new MetadataSchemaToVOBuilder().build(
				schema(BetaWorkflow.DEFAULT_SCHEMA), VIEW_MODE.TABLE, view.getSessionContext());

		return new RecordVODataProvider(schemaVO, new RecordToVOBuilder(), modelLayerFactory, view.getSessionContext()) {
			@Override
			protected LogicalSearchQuery getQuery() {
				return workflowServices.getWorkflowsQuery();
			}
		};
	}

	public void workflowStartRequested(RecordVO record) {
		BetaWorkflow workflow = new TasksSchemasRecordsServices(view.getCollection(), appLayerFactory).getBetaWorkflow(record.getId());
		Map<String, List<String>> parameters = new HashMap<>();
		workflowServices.start(workflow, getCurrentUser(), parameters);
		refreshCurrentTab();
	}

	private RecordVODataProvider getTasks(String tabId) {
		MetadataSchemaVO schemaVO = new MetadataSchemaToVOBuilder()
				.build(defaultSchema(), VIEW_MODE.TABLE, getMetadataForTab(tabId), view.getSessionContext());

		switch (tabId) {
		case TASKS_ASSIGNED_TO_CURRENT_USER:
			return new RecordVODataProvider(schemaVO, new TaskToVOBuilder(), modelLayerFactory, view.getSessionContext()) {
				@Override
				protected LogicalSearchQuery getQuery() {
					LogicalSearchQuery query = tasksSearchServices.getTasksAssignedToUserQuery(getCurrentUser());
					addTimeStampToQuery(query);
					addStarredSortToQuery(query);
					return query;
				}

				@Override
				protected void clearSort(LogicalSearchQuery query) {
					super.clearSort(query);
					addStarredSortToQuery(query);
				}
			};
		case TASKS_ASSIGNED_BY_CURRENT_USER:
			return new RecordVODataProvider(schemaVO, new TaskToVOBuilder(), modelLayerFactory, view.getSessionContext()) {
				@Override
				protected LogicalSearchQuery getQuery() {
					LogicalSearchQuery query = tasksSearchServices.getTasksAssignedByUserQuery(getCurrentUser());
					addTimeStampToQuery(query);
					addStarredSortToQuery(query);
					return query;
				}

				@Override
				protected void clearSort(LogicalSearchQuery query) {
					super.clearSort(query);
					addStarredSortToQuery(query);
				}
			};
		case TASKS_NOT_ASSIGNED:
			return new RecordVODataProvider(schemaVO, new TaskToVOBuilder(), modelLayerFactory, view.getSessionContext()) {
				@Override
				protected LogicalSearchQuery getQuery() {
					LogicalSearchQuery query = tasksSearchServices.getUnassignedTasksQuery(getCurrentUser());
					addTimeStampToQuery(query);
					addStarredSortToQuery(query);
					return query;
				}

				@Override
				protected void clearSort(LogicalSearchQuery query) {
					super.clearSort(query);
					addStarredSortToQuery(query);
				}
			};
		case TASKS_RECENTLY_COMPLETED:
			return new RecordVODataProvider(schemaVO, new TaskToVOBuilder(), modelLayerFactory, view.getSessionContext()) {
				@Override
				protected LogicalSearchQuery getQuery() {
					LogicalSearchQuery query = tasksSearchServices.getRecentlyCompletedTasks(getCurrentUser());
					addTimeStampToQuery(query);
					addStarredSortToQuery(query);
					return query;
				}

				@Override
				protected void clearSort(LogicalSearchQuery query) {
					super.clearSort(query);
					addStarredSortToQuery(query);
				}
			};
		default:
			throw new RuntimeException("BUG: Unknown tabId + " + tabId);
		}
	}

	private void addTimeStampToQuery(LogicalSearchQuery query) {
		TaskManagementViewImpl.Timestamp timestamp = view.getTimestamp();
		switch (timestamp) {
			case ALL:
				break;
			case TODAY:
				tasksSearchServices.addDateFilterToQuery(query, LocalDate.now());
				break;
			case WEEK:
				tasksSearchServices.addDateFilterToQuery(query, LocalDate.now().plusWeeks(1));
				break;
			case MONTH:
				tasksSearchServices.addDateFilterToQuery(query, LocalDate.now().plusMonths(1));
				break;
		}
	}

	private RecordVODataProvider getWorkflowInstances(String tabId) {
		MetadataSchemaVO schemaVO = new MetadataSchemaToVOBuilder()
				.build(schema(BetaWorkflowInstance.DEFAULT_SCHEMA), VIEW_MODE.TABLE, view.getSessionContext());

		switch (tabId) {
		case WORKFLOWS_STARTED:
			return new RecordVODataProvider(schemaVO, new RecordToVOBuilder(), modelLayerFactory, view.getSessionContext()) {
				@Override
				protected LogicalSearchQuery getQuery() {
					return workflowServices.getCurrentWorkflowInstancesQuery();
				}
			};
		default:
			throw new RuntimeException("BUG: Unknown tabId + " + tabId);
		}
	}

	private List<String> getMetadataForTab(String tabId) {
		switch (tabId) {
		case TASKS_ASSIGNED_TO_CURRENT_USER:
			return Arrays.asList(STARRED_BY_USERS, TITLE, ASSIGNER, DUE_DATE, STATUS);
		case TASKS_ASSIGNED_BY_CURRENT_USER:
			return Arrays.asList(STARRED_BY_USERS, TITLE, ASSIGNEE, DUE_DATE, STATUS);
		case TASKS_NOT_ASSIGNED:
			return Arrays.asList(STARRED_BY_USERS, TITLE, DUE_DATE, STATUS);
		default:
			return Arrays.asList(STARRED_BY_USERS, TITLE, ASSIGNER, ASSIGNEE, DUE_DATE, STATUS);
		}
	}

	private boolean isWorkflowTab(String tabId) {
		return WORKFLOWS_STARTED.equals(tabId);
	}

	private boolean isTaskTab(String tabId) {
		switch (tabId) {
			case TASKS_ASSIGNED_TO_CURRENT_USER:
			case TASKS_ASSIGNED_BY_CURRENT_USER:
			case TASKS_NOT_ASSIGNED:
			case TASKS_RECENTLY_COMPLETED:
				return true;
			default:
				return false;
		}
	}

	private void readObject(java.io.ObjectInputStream stream)
			throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		initTransientObjects();
	}

	private void initTransientObjects() {
		TasksSchemasRecordsServices schemas = new TasksSchemasRecordsServices(collection, appLayerFactory);
		workflowServices = new BetaWorkflowServices(collection, appLayerFactory);
		tasksSearchServices = new TasksSearchServices(schemas);
		taskPresenterServices = new TaskPresenterServices(
				schemas, recordServices(), tasksSearchServices, modelLayerFactory.newLoggingServices());
	}

	public boolean areWorkflowsEnabled() {
		RMConfigs configs = new RMConfigs(modelLayerFactory.getSystemConfigurationsManager());
		return configs.areWorkflowsEnabled();
	}

	public boolean hasPermissionToStartWorkflow() {
		return getCurrentUser().has(TasksPermissionsTo.START_WORKFLOWS).globally();
	}

	public boolean isMetadataReportAllowed(RecordVO recordVO) {
		return true;
	}

	@Override
	public void completeQuicklyButtonClicked(RecordVO recordVO) {
		TasksSchemasRecordsServices tasksSchemas = new TasksSchemasRecordsServices(collection, appLayerFactory);
		Task task = tasksSchemas.getTask(recordVO.getId());
		Object decisions = task.get(Task.BETA_NEXT_TASKS_DECISIONS);
		if((task.getModelTask() != null && decisions != null && !((MapStringStringStructure)decisions).isEmpty()) || tasksSchemas.isRequestTask(task)) {
			QuickCompleteWindow quickCompleteWindow = new QuickCompleteWindow(this, appLayerFactory, recordVO);
			quickCompleteWindow.show();
		} else {
			QuickCompleteWindow.quickCompleteTask(appLayerFactory, task, null, null, null, null);
			refreshCurrentTab();
		}
	}

	@Override
	public BaseView getView() {
		return view;
	}

	@Override
	public void reloadTaskModified(Task task) {
		view.reloadCurrentTab();
	}

	@Override
	public String getCurrentUserId() {
		return getCurrentUser().getId();
	}

	@Override
	public void updateTaskStarred(boolean isStarred, String taskId) {
		TasksSchemasRecordsServices taskSchemas = new TasksSchemasRecordsServices(collection, appLayerFactory);
		Task task = taskSchemas.getTask(taskId);
		if(isStarred) {
			task.addStarredBy(getCurrentUser().getId());
		} else {
			task.removeStarredBy(getCurrentUser().getId());
		}
		try {
			recordServices().update(task);
		} catch (RecordServicesException e) {
			e.printStackTrace();
		}
		provider.fireDataRefreshEvent();
	}

	public String getDueDateCaption() {
		return modelLayerFactory.getMetadataSchemasManager().getSchemaTypes(collection)
				.getDefaultSchema(SCHEMA_TYPE).getMetadata(DUE_DATE)
				.getLabel(Language.withLocale(view.getSessionContext().getCurrentLocale()));
	}

	private void addStarredSortToQuery(LogicalSearchQuery query) {
		Metadata metadata = types().getSchema(Task.DEFAULT_SCHEMA).getMetadata(Task.STARRED_BY_USERS);
		LogicalSearchQuerySort sortField
				= new LogicalSearchQuerySort("termfreq(" + metadata.getDataStoreCode() + ",\'" + getCurrentUserId() + "\')", false);
		query.sortFirstOn(sortField);
	}
}
