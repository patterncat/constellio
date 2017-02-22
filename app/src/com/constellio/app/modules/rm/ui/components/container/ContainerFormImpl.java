package com.constellio.app.modules.rm.ui.components.container;

import com.constellio.app.modules.rm.ui.pages.containers.edit.AddEditContainerPresenter;
import com.constellio.app.modules.rm.wrappers.ContainerRecord;
import com.constellio.app.ui.entities.MetadataVO;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.framework.buttons.WindowButton;
import com.constellio.app.ui.framework.components.RecordFieldFactory;
import com.constellio.app.ui.framework.components.RecordForm;
import com.constellio.app.ui.framework.components.fields.number.BaseIntegerField;
import com.vaadin.ui.*;

import java.util.ArrayList;
import java.util.List;

import static com.constellio.app.ui.i18n.i18n.$;

/**
 * Created by Constellio on 2017-01-11.
 */
public abstract class ContainerFormImpl extends RecordForm implements ContainerForm {

    public ContainerFormImpl(RecordVO record, final AddEditContainerPresenter presenter) {
        this(record, new ContainerFieldFactory((String) record.get(ContainerRecord.TYPE), presenter));
        if(presenter.isMultipleMode()) {
            WindowButton newSaveButton = new WindowButton(saveButton.getCaption(), saveButton.getCaption()) {
                @Override
                public void buttonClick(ClickEvent event) {
                    super.buttonClick(event);
                }

                @Override
                protected Component buildWindowContent() {
                    VerticalLayout mainLayout = new VerticalLayout();
                    mainLayout.setSpacing(true);

                    final BaseIntegerField integerField = new BaseIntegerField($("AddEditContainerView.numberOfContainer"));

                    HorizontalLayout buttonLayout = new HorizontalLayout();
                    buttonLayout.setSpacing(true);
                    Button newLayoutSaveButton = new Button(saveButton.getCaption());
                    newLayoutSaveButton.addClickListener(new ClickListener() {
                        @Override
                        public void buttonClick(ClickEvent event) {
                            presenter.setNumberOfContainer(Integer.parseInt(integerField.getValue()));
                            callTrySave();
                            getWindow().close();
                        }
                    });
                    Button newLayoutCancelButton = new Button(cancelButton.getCaption());
                    newLayoutCancelButton.addClickListener(new ClickListener() {
                        @Override
                        public void buttonClick(ClickEvent event) {
                            getWindow().close();
                        }
                    });
                    buttonLayout.addComponents(newLayoutCancelButton, newLayoutSaveButton);

                    mainLayout.addComponents(integerField, buttonLayout);
                    return mainLayout;
                }
            };
            buttonsLayout.replaceComponent(saveButton, newSaveButton);
        }
    }

    public ContainerFormImpl(final RecordVO recordVO, RecordFieldFactory formFieldFactory) {
        super(recordVO, buildFields(recordVO, formFieldFactory), formFieldFactory);
    }

    private static List<FieldAndPropertyId> buildFields(RecordVO recordVO, RecordFieldFactory formFieldFactory) {
        List<FieldAndPropertyId> fieldsAndPropertyIds = new ArrayList<FieldAndPropertyId>();
        for (MetadataVO metadataVO : recordVO.getFormMetadatas()) {
            Field<?> field = formFieldFactory.build(recordVO, metadataVO);
            if (field != null) {
                field.addStyleName(STYLE_FIELD);
                field.addStyleName(STYLE_FIELD + "-" + metadataVO.getCode());
                fieldsAndPropertyIds.add(new FieldAndPropertyId(field, metadataVO));
            }
        }
        return fieldsAndPropertyIds;
    }

    @SuppressWarnings("unchecked")
    public Field<String> getTypeField() {
        return (Field<String>) getField(ContainerRecord.TYPE);
    }

    @SuppressWarnings("unchecked")
    public Field<String> getDecommissioningTypeField() {
        return (Field<String>) getField(ContainerRecord.DECOMMISSIONING_TYPE);
    }

    @SuppressWarnings("unchecked")
    public Field<String> getAdministrativeUnitField() {
        return (Field<String>) getField(ContainerRecord.ADMINISTRATIVE_UNIT);
    }
}