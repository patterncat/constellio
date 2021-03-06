package com.constellio.app.modules.tasks.ui.components;

import com.constellio.app.entities.schemasDisplay.enums.MetadataInputType;
import com.constellio.app.modules.tasks.model.wrappers.request.BorrowRequest;
import com.constellio.app.modules.tasks.ui.components.fields.*;
import com.constellio.app.modules.tasks.ui.components.fields.list.*;
import com.constellio.app.ui.entities.MetadataVO;
import com.constellio.app.ui.framework.components.MetadataFieldFactory;
import com.vaadin.ui.Field;

import static com.constellio.app.modules.rm.wrappers.Document.TYPE;
import static com.constellio.app.modules.tasks.model.wrappers.Task.*;

public class TaskFieldFactory extends MetadataFieldFactory {

	public static final String INCLUSIVE_DECISION = "inclusiveDecision";

	public TaskFieldFactory(boolean isViewOnly) {
		super(isViewOnly);
	}

	@Override
	public Field<?> build(MetadataVO metadata) {
		Field<?> field;
		MetadataInputType inputType = metadata.getMetadataInputType();
		switch (metadata.getLocalCode()) {
		case TYPE:
			if (MetadataInputType.LOOKUP.equals(inputType)) {
				field = new TaskTypeFieldLookupImpl();
			} else if (MetadataInputType.RADIO_BUTTONS.equals(inputType)) {
				field = new TaskTypeFieldOptionGroupImpl();
			} else {
				field = new TaskTypeFieldComboBoxImpl();
			}
			break;
		case TASK_FOLLOWERS:
			field = new ListAddRemoveTaskFollowerField();
			break;
		case REMINDERS:
			field = new ListAddRemoveTaskReminderField();
			break;
		case PROGRESS_PERCENTAGE:
			field = new TaskProgressPercentageFieldImpl();
			break;
		case RELATIVE_DUE_DATE:
			field = new TaskRelativeDueDateFieldImpl();
			break;
		case DECISION:
			field = new TaskDecisionFieldImpl();
			break;
		case BorrowRequest.ACCEPTED:
			field = new TaskAcceptedFieldImpl();
			break;
		case BorrowRequest.REASON:
			field = new TaskReasonFieldImpl();
			break;
		case LINKED_FOLDERS:
			field = new TaskListAddRemoveLinkedFoldersLookupField();
			postBuild(field, metadata);
			break;
        case LINKED_DOCUMENTS:
            field = new TaskListAddRemoveLinkedDocumentsLookupField();
			postBuild(field, metadata);
            break;
        case INCLUSIVE_DECISION:
        	field = new ListAddRemoveWorkflowInclusiveDecisionFieldImpl();
        	break;
		case REMINDER_FREQUENCY:
			field = new TaskReminderFrequencyFieldImpl();
			break;
		default:
			field = super.build(metadata);
		}
		if (field instanceof CustomTaskField) {
			postBuild(field, metadata);
		}
		return field;
	}
}
