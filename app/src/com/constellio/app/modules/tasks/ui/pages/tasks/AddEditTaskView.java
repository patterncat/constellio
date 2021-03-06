package com.constellio.app.modules.tasks.ui.pages.tasks;

import com.constellio.app.modules.tasks.ui.components.fields.TaskForm;
import com.constellio.app.modules.tasks.ui.entities.TaskVO;
import com.constellio.app.modules.tasks.ui.pages.viewGroups.TasksViewGroup;
import com.constellio.app.ui.pages.base.BaseView;
import com.constellio.model.entities.records.Record;

public interface AddEditTaskView extends BaseView, TasksViewGroup {

	TaskForm getForm();

	void setRecord(TaskVO taskVO);

	void adjustAcceptedField(boolean b);

	void navigateToWorkflow(String workflowId);

	Record getWorkflow(String workflowId);
}
