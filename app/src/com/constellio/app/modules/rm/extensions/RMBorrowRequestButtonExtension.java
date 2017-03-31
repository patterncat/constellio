package com.constellio.app.modules.rm.extensions;

import com.constellio.app.api.extensions.PagesComponentsExtension;
import com.constellio.app.api.extensions.params.DecorateMainComponentAfterInitExtensionParams;
import com.constellio.app.modules.rm.constants.RMPermissionsTo;
import com.constellio.app.modules.rm.model.enums.FolderMediaType;
import com.constellio.app.modules.rm.model.enums.FolderStatus;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.ui.pages.containers.DisplayContainerView;
import com.constellio.app.modules.rm.ui.pages.containers.DisplayContainerViewImpl;
import com.constellio.app.modules.rm.ui.pages.folder.DisplayFolderView;
import com.constellio.app.modules.rm.ui.pages.folder.DisplayFolderViewImpl;
import com.constellio.app.modules.rm.wrappers.ContainerRecord;
import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.app.modules.tasks.TasksPermissionsTo;
import com.constellio.app.modules.tasks.model.wrappers.Task;
import com.constellio.app.modules.tasks.services.TasksSchemasRecordsServices;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.app.ui.framework.builders.RecordToVOBuilder;
import com.constellio.app.ui.framework.buttons.BaseButton;
import com.constellio.app.ui.framework.buttons.ConfirmDialogButton;
import com.constellio.app.ui.framework.buttons.WindowButton;
import com.constellio.app.ui.framework.components.BaseForm;
import com.constellio.app.ui.framework.decorators.base.ActionMenuButtonsDecorator;
import com.constellio.app.ui.pages.base.BasePresenterUtils;
import com.constellio.app.ui.pages.base.BaseView;
import com.constellio.app.ui.pages.base.BaseViewImpl;
import com.constellio.model.entities.CorePermissions;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.frameworks.validation.ValidationException;
import com.constellio.model.services.factories.ModelLayerFactory;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.records.RecordServicesException;
import com.vaadin.data.Container;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.ui.*;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.vaadin.dialogs.ConfirmDialog;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.constellio.app.ui.i18n.i18n.$;

/**
 * Created by Constellio on 2017-03-16.
 */
public class RMBorrowRequestButtonExtension extends PagesComponentsExtension {

    enum RequestType {
        EXTENSION, BORROW
    }

    String collection;
    AppLayerFactory appLayerFactory;
    ModelLayerFactory modelLayerFactory;
    TasksSchemasRecordsServices taskSchemas;
    Record record;
    boolean borrowed;
    boolean reactivable;

    public RMBorrowRequestButtonExtension(String collection, AppLayerFactory appLayerFactory) {
        this.collection = collection;
        this.appLayerFactory = appLayerFactory;
        this.modelLayerFactory = appLayerFactory.getModelLayerFactory();
        this.taskSchemas = new TasksSchemasRecordsServices(collection, appLayerFactory);
    }

    @Override
    public void decorateMainComponentBeforeViewAssembledOnViewEntered(DecorateMainComponentAfterInitExtensionParams params) {
        super.decorateMainComponentAfterViewAssembledOnViewEntered(params);
        Component mainComponent = params.getMainComponent();
        BaseViewImpl view = (BaseViewImpl) mainComponent;
        if (mainComponent instanceof DisplayFolderViewImpl) {
            this.record = new RMSchemasRecordsServices(view.getCollection(), appLayerFactory).getFolder(((DisplayFolderViewImpl) view).getRecord().getId()).getWrappedRecord();
            this.reactivable = ((Folder) this.record).getArchivisticStatus().isActiveOrSemiActive() && ((Folder) this.record).getMediaType().equals(FolderMediaType.ANALOG) && getCurrentUser(view).has(RMPermissionsTo.BORROW_FOLDER).on(this.record);
            this.borrowed = Boolean.TRUE.equals(((Folder) this.record).getBorrowed()) && ((Folder) this.record).getBorrowUser().equals(getCurrentUser(view).getId()) && getCurrentUser(view).has(RMPermissionsTo.BORROW_FOLDER).on(this.record);
        } else if (mainComponent instanceof DisplayContainerViewImpl) {
            this.record = new RMSchemasRecordsServices(view.getCollection(), appLayerFactory).getContainerRecord(((DisplayContainerViewImpl) view).getPresenter().getContainerId()).getWrappedRecord();
            this.borrowed = Boolean.TRUE.equals(((ContainerRecord) this.record).getBorrowed()) && getCurrentUser(view).has(RMPermissionsTo.BORROW_FOLDER).on(this.record);
        } else throw new UnsupportedOperationException();

        view.addActionMenuButtonsDecorator(new ActionMenuButtonsDecorator() {
            @Override
            public void decorate(final BaseViewImpl view, List<Button> actionMenuButtons) {
                actionMenuButtons.add(buildRequestBorrowButton(view));
                actionMenuButtons.add(buildRequestReturnButton(view));
                actionMenuButtons.add(buildRequestReactivationButton(view));
                actionMenuButtons.add(buildRequestBorrowExtensionButton(view));
            }
        });
    }

    private User getCurrentUser(BaseView view) {
        BasePresenterUtils basePresenterUtils = new BasePresenterUtils(view.getConstellioFactories(), view.getSessionContext());
        return basePresenterUtils.getCurrentUser();
    }

    private Button buildRequestBorrowButton(final BaseViewImpl view) {
        if (this.record instanceof Folder) {
            return new BaseButton($("DisplayFolderView.borrowRequest")) {
                @Override
                protected void buttonClick(ClickEvent event) {
                    ConfirmDialog.show(
                            UI.getCurrent(),
                            $("DisplayFolderViewImpl.requestBorrowButtonTitle"),
                            $("DisplayFolderViewImpl.requestBorrowButtonMessage"),
                            $("cancel"),
                            $("DisplayFolderView.borrowContainerInstead"),
                            $("DisplayFolderView.confirmBorrowFolder"),
                            new ConfirmDialog.Listener() {
                                public void onClose(ConfirmDialog dialog) {
                                    if (dialog.isConfirmed()) {
                                        borrowRequest(view);
                                    } else if (dialog.isCanceled()) {
                                        return;
                                    } else {
                                        borrowRequest(view);
                                    }
                                }
                            }).setWidth("55%");

                }

                @Override
                public boolean isVisible() {
                    return !RMBorrowRequestButtonExtension.this.borrowed;
                }
            };
        } else if (this.record instanceof ContainerRecord) {
            return new ConfirmDialogButton($("DisplayFolderView.borrowRequest")) {

                @Override
                protected String getConfirmDialogMessage() {
                    return $("DisplayContainerViewImpl.confirmBorrowContainer");
                }

                @Override
                protected void confirmButtonClick(ConfirmDialog dialog) {
                    borrowRequest(view);
                }

                @Override
                public boolean isVisible() {
                    return !RMBorrowRequestButtonExtension.this.borrowed;
                }
            };
        } else throw new UnsupportedOperationException();
    }

    private Button buildRequestReturnButton(final BaseViewImpl view) {
        return new ConfirmDialogButton($("DocumentActionsComponent.checkIn")) {

            @Override
            protected String getConfirmDialogMessage() {
                return $("DisplayFolderView.confirmReturnMessage");
            }

            @Override
            protected void confirmButtonClick(ConfirmDialog dialog) {
                returnRequest(view);
            }

            @Override
            public boolean isVisible() {
                return RMBorrowRequestButtonExtension.this.borrowed;
            }
        };
    }

    private Button buildRequestReactivationButton(final BaseViewImpl view) {
        return new ConfirmDialogButton($("DisplayFolderView.reactivation")) {

            @Override
            protected String getConfirmDialogMessage() {
                return $("DisplayFolderView.confirmReactivationMessage");
            }

            @Override
            protected void confirmButtonClick(ConfirmDialog dialog) {
                reactivationRequested(view);
            }

            @Override
            public boolean isVisible() {
                return RMBorrowRequestButtonExtension.this.reactivable;
            }
        };
    }

    private Button buildRequestBorrowExtensionButton(final BaseViewImpl view) {
        return new WindowButton($("DisplayFolderView.extension"), $("Extension")) {
            @PropertyId("value")
            private InlineDateField datefield;

            @Override
            protected Component buildWindowContent() {
                Label dateLabel = new Label($("DisplayFolderView.chooseDateToExtends"));

                datefield = new InlineDateField();
                List<BaseForm.FieldAndPropertyId> fields = Collections.singletonList(new BaseForm.FieldAndPropertyId(datefield, "value"));
                Request req = new Request(new Date(), RequestType.EXTENSION);
                BaseForm form = new BaseForm<Request>(req, this, datefield) {

                    @Override
                    protected void saveButtonClick(Request viewObject) throws ValidationException {
                        viewObject.setValue(datefield.getValue());
                        borrowExtensionRequested(view, viewObject);
                        getWindow().close();
                    }

                    @Override
                    protected void cancelButtonClick(Request viewObject) {
                        getWindow().close();
                    }
                };
                return form;
            }

            public void setDatefield(InlineDateField datefield) {
                this.datefield = datefield;
            }

            public InlineDateField getDatefield() {
                return this.datefield;
            }

            @Override
            public boolean isVisible() {
                return RMBorrowRequestButtonExtension.this.borrowed;
            }
        };
    }

    public void borrowRequest(BaseViewImpl view) {
        try {
            Task borrowRequest;
            if (this.record instanceof Folder) {
                borrowRequest = taskSchemas.newBorrowFolderRequestTask(getCurrentUser(view).getId(), this.record.getId());
            } else if (this.record instanceof ContainerRecord) {
                borrowRequest = taskSchemas.newBorrowContainerRequestTask(getCurrentUser(view).getId(), this.record.getId());
            } else throw new UnsupportedOperationException("invalid item : " + this.record.getClass().getName());
            modelLayerFactory.newRecordServices().add(borrowRequest);
        } catch (RecordServicesException e) {
            e.printStackTrace();
        }
    }

    public void returnRequest(BaseViewImpl view) {
        try {
            Task returnRequest;
            if (this.record instanceof Folder) {
                returnRequest = taskSchemas.newReturnFolderRequestTask(getCurrentUser(view).getId(), this.record.getId());
            } else if (view instanceof DisplayContainerViewImpl) {
                returnRequest = taskSchemas.newReturnContainerRequestTask(getCurrentUser(view).getId(), this.record.getId());
                modelLayerFactory.newRecordServices().add(returnRequest);
            } else throw new UnsupportedOperationException("invalid item : " + this.record.getClass().getName());
            modelLayerFactory.newRecordServices().add(returnRequest);
        } catch (RecordServicesException e) {
            e.printStackTrace();
        }
    }

    public void reactivationRequested(BaseViewImpl view) {
        try {
            Task reactivationRequest;
            if (this.record instanceof Folder) {
                reactivationRequest = taskSchemas.newReactivateFolderRequestTask(getCurrentUser(view).getId(), this.record.getId());
            } else if (view instanceof DisplayContainerViewImpl) {
                reactivationRequest = taskSchemas.newReactivationContainerRequestTask(getCurrentUser(view).getId(), this.record.getId());
            } else throw new UnsupportedOperationException("invalid view : " + view.getClass().getCanonicalName());
            modelLayerFactory.newRecordServices().add(reactivationRequest);
        } catch (RecordServicesException e) {
            e.printStackTrace();
        }
    }

    public void borrowExtensionRequested(BaseViewImpl view, Request req) {
        try {
            Task borrowExtensionRequest;
            if (this.record instanceof Folder) {
                borrowExtensionRequest = taskSchemas.newBorrowFolderExtensionRequestTask(getCurrentUser(view).getId(), this.record.getId(), new LocalDate(req.getValue()));
            } else if (this.record instanceof ContainerRecord) {
                borrowExtensionRequest = taskSchemas.newBorrowContainerExtensionRequestTask(getCurrentUser(view).getId(), this.record.getId(), new LocalDate(req.getValue()));
            } else throw new UnsupportedOperationException("invalid item : " + this.record.getClass().getName());
            modelLayerFactory.newRecordServices().add(borrowExtensionRequest);
        } catch (RecordServicesException e) {
            e.printStackTrace();
        }
    }

    public class Request {

        @PropertyId("value")
        public Object value;

        @PropertyId("type")
        public RequestType type;

        public Request() {

        }

        public Request(Object value, RequestType requestType) {
            this.type = requestType;
            this.value = value;
        }

        public Object getValue() {
            return value;
        }

        public Request setValue(Object value) {
            this.value = value;
            return this;
        }

        public RequestType getType() {
            return type;
        }

        public Request setType(RequestType type) {
            this.type = type;
            return this;
        }
    }
}
