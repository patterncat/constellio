package com.constellio.app.modules.rm.ui.pages.document;

import com.constellio.app.modules.rm.ui.components.RMMetadataDisplayFactory;
import com.constellio.app.modules.rm.ui.components.breadcrumb.FolderDocumentBreadcrumbTrail;
import com.constellio.app.modules.rm.ui.entities.DocumentVO;
import com.constellio.app.modules.rm.wrappers.Document;
import com.constellio.app.modules.tasks.model.wrappers.Task;
import com.constellio.app.modules.tasks.ui.components.fields.StarredFieldImpl;
import com.constellio.app.ui.entities.ContentVersionVO;
import com.constellio.app.ui.entities.MetadataVO;
import com.constellio.app.ui.entities.MetadataValueVO;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.framework.buttons.*;
import com.constellio.app.ui.framework.buttons.ConfirmDialogButton.DialogMode;
import com.constellio.app.ui.framework.buttons.WindowButton.WindowConfiguration;
import com.constellio.app.ui.framework.buttons.report.ReportGeneratorButton;
import com.constellio.app.ui.framework.components.BaseForm;
import com.constellio.app.ui.framework.components.ComponentState;
import com.constellio.app.ui.framework.components.RecordDisplay;
import com.constellio.app.ui.framework.components.breadcrumb.BaseBreadcrumbTrail;
import com.constellio.app.ui.framework.components.content.UpdateContentVersionWindowImpl;
import com.constellio.app.ui.framework.components.fields.BaseTextField;
import com.constellio.app.ui.framework.components.table.ContentVersionVOTable;
import com.constellio.app.ui.framework.components.table.RecordVOTable;
import com.constellio.app.ui.framework.components.table.columns.EventVOTableColumnsManager;
import com.constellio.app.ui.framework.components.table.columns.RecordVOTableColumnsManager;
import com.constellio.app.ui.framework.components.table.columns.TableColumnsManager;
import com.constellio.app.ui.framework.components.viewers.ContentViewer;
import com.constellio.app.ui.framework.containers.RecordVOLazyContainer;
import com.constellio.app.ui.framework.data.RecordVODataProvider;
import com.constellio.app.ui.framework.decorators.tabs.TabSheetDecorator;
import com.constellio.app.ui.framework.items.RecordVOItem;
import com.constellio.app.ui.pages.base.BaseViewImpl;
import com.constellio.app.ui.pages.management.Report.PrintableReportListPossibleType;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.*;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.dialogs.ConfirmDialog;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import static com.constellio.app.ui.framework.buttons.WindowButton.WindowConfiguration.modalDialog;
import static com.constellio.app.ui.i18n.i18n.$;

public class DisplayDocumentViewImpl extends BaseViewImpl implements DisplayDocumentView, DropHandler {
	private VerticalLayout mainLayout;
	private Label borrowedLabel;
	private DocumentVO documentVO;
	private String taxonomyCode;
	private TabSheet tabSheet;
	private ContentViewer contentViewer;
	private RecordDisplay recordDisplay;
	private ContentVersionVOTable versionTable;
	private Component tasksComponent;
	private Component eventsComponent;
	private UpdateContentVersionWindowImpl uploadWindow;
	private EditButton editDocumentButton;
	private DeleteButton deleteDocumentButton;
	private Button copyContentButton;
	private WindowButton renameContentButton;
	private WindowButton signButton;
	private WindowButton startWorkflowButton;

	private Button linkToDocumentButton, addAuthorizationButton, uploadButton, checkInButton, checkOutButton, finalizeButton,
			shareDocumentButton, createPDFAButton, alertWhenAvailableButton, addToCartButton, addToOrRemoveFromSelectionButton, publishButton, unpublishButton,
			publicLinkButton, reportGeneratorButton;
	
	private List<TabSheetDecorator> tabSheetDecorators = new ArrayList<>();

	private DisplayDocumentPresenter presenter;

	public DisplayDocumentViewImpl() {
		presenter = new DisplayDocumentPresenter(this);
	}

	@Override
	protected void initBeforeCreateComponents(ViewChangeEvent event) {
		presenter.forParams(event.getParameters());
	}

	@Override
	protected void afterViewAssembled(ViewChangeEvent event) {
		presenter.viewAssembled();
	}

	@Override
	public void setDocumentVO(DocumentVO documentVO) {
		this.documentVO = documentVO;
		if (recordDisplay != null) {
			recordDisplay.setRecordVO(documentVO);
		}
	}

	@Override
	public void setContentVersions(List<ContentVersionVO> contentVersions) {
		versionTable.setContentVersions(contentVersions);
	}

	@Override
	protected String getTitle() {
		return null;
	}

	@Override
	protected Component buildMainComponent(ViewChangeEvent event) {
		mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();

		borrowedLabel = new Label();
		borrowedLabel.setVisible(false);
		borrowedLabel.addStyleName(ValoTheme.LABEL_COLORED);
		borrowedLabel.addStyleName(ValoTheme.LABEL_BOLD);

		contentViewer = new ContentViewer(documentVO, Document.CONTENT, documentVO.getContent());

		tabSheet = new TabSheet();

		recordDisplay = new RecordDisplay(documentVO, new RMMetadataDisplayFactory());
		versionTable = new ContentVersionVOTable(presenter.getAppLayerFactory(), presenter.hasCurrentUserPermissionToViewFileSystemName()) {
			@Override
			protected boolean isDeleteColumn() {
				return presenter.isDeleteContentVersionPossible();
			}

			@Override
			protected boolean isDeletePossible(ContentVersionVO contentVersionVO) {
				return presenter.isDeleteContentVersionPossible(contentVersionVO);
			}

			@Override
			protected void deleteButtonClick(ContentVersionVO contentVersionVO) {
				presenter.deleteContentVersionButtonClicked(contentVersionVO);
			}
		};
		tasksComponent = new CustomComponent();
		versionTable.setSizeFull();

		tabSheet.addTab(recordDisplay, $("DisplayDocumentView.tabs.metadata"));
		tabSheet.addTab(versionTable, $("DisplayDocumentView.tabs.versions"));
		tabSheet.addTab(tasksComponent, $("DisplayDocumentView.tabs.tasks", presenter.getTaskCount()));

		eventsComponent = new CustomComponent();
		tabSheet.addTab(eventsComponent, $("DisplayDocumentView.tabs.logs"));
		if(presenter.hasCurrentUserPermissionToViewEvents()) {
			tabSheet.getTab(eventsComponent).setEnabled(true);
		}
		else {
			tabSheet.getTab(eventsComponent).setEnabled(false);
		}

		tabSheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
			@Override
			public void selectedTabChange(TabSheet.SelectedTabChangeEvent event) {
				if (event.getTabSheet().getSelectedTab() == eventsComponent) {
					presenter.refreshEvents();
				}
			}
		});

		mainLayout.addComponents(borrowedLabel, contentViewer, tabSheet);
		
		for (TabSheetDecorator tabSheetDecorator : tabSheetDecorators) {
			tabSheetDecorator.decorate(this, tabSheet);
		}
		
		return mainLayout;
	}

	@Override
	protected BaseBreadcrumbTrail buildBreadcrumbTrail() {
		return new FolderDocumentBreadcrumbTrail(documentVO.getId(), taxonomyCode, this);
	}

	@Override
	public void refreshMetadataDisplay() {
		recordDisplay.refresh();
	}

	@Override
	protected boolean isBackgroundViewMonitor() {
		return true;
	}

	@Override
	protected void onBackgroundViewMonitor() {
		presenter.backgroundViewMonitor();
	}

//	@Override
//	protected ClickListener getBackButtonClickListener() {
//		return new ClickListener() {
//			@Override
//			public void buttonClick(ClickEvent event) {
//				presenter.backButtonClicked();
//			}
//		};
//	}

	@Override
	public void setTasks(final RecordVODataProvider dataProvider) {
		Table tasksTable = new RecordVOTable(dataProvider) {
			@Override
			protected Component buildMetadataComponent(MetadataValueVO metadataValue, RecordVO recordVO) {
				if(Task.STARRED_BY_USERS.equals(metadataValue.getMetadata().getLocalCode())) {
					return new StarredFieldImpl(recordVO.getId(), (List<String>)metadataValue.getValue(), getSessionContext().getCurrentUser().getId()) {
						@Override
						public void updateTaskStarred(boolean isStarred, String taskId) {
							presenter.updateTaskStarred(isStarred, taskId, dataProvider);
						}
					};
				} else {
					return super.buildMetadataComponent(metadataValue, recordVO);
				}
			}

			@Override
			protected TableColumnsManager newColumnsManager() {
				return new RecordVOTableColumnsManager() {
					@Override
					protected String toColumnId(Object propertyId) {
						if(propertyId instanceof MetadataVO) {
							if(Task.STARRED_BY_USERS.equals(((MetadataVO) propertyId).getLocalCode())) {
								setColumnHeader(propertyId, "");
								setColumnWidth(propertyId, 60);
							}
						}
						return super.toColumnId(propertyId);
					}
				};
			}

			@Override
			public Collection<?> getSortableContainerPropertyIds() {
				Collection<?> sortableContainerPropertyIds = super.getSortableContainerPropertyIds();
				Iterator<?> iterator = sortableContainerPropertyIds.iterator();
				while (iterator.hasNext()) {
					Object property = iterator.next();
					if(property != null && property instanceof MetadataVO && Task.STARRED_BY_USERS.equals(((MetadataVO) property).getLocalCode())) {
						iterator.remove();
					}
				}
				return sortableContainerPropertyIds;
			}
		};
		tasksTable.setSizeFull();
		tasksTable.addItemClickListener(new ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				RecordVOItem item = (RecordVOItem) event.getItem();
				RecordVO recordVO = item.getRecord();
				presenter.taskClicked(recordVO);
			}
		});
		Component oldTasksComponent = tasksComponent;
		tasksComponent = tasksTable;
		tabSheet.replaceComponent(oldTasksComponent, tasksComponent);
	}

	@Override
	public void setEvents(RecordVODataProvider dataProvider) {
		RecordVOTable table = new RecordVOTable($("DisplayDocumentView.tabs.logs"), new RecordVOLazyContainer(dataProvider)) {
			@Override
			protected TableColumnsManager newColumnsManager() {
				return new EventVOTableColumnsManager();
			}
		};
		table.setSizeFull();

		tabSheet.replaceComponent(eventsComponent, table);
		eventsComponent = table;
	}

	@Override
	protected List<Button> buildActionMenuButtons(ViewChangeEvent event) {
		List<Button> actionMenuButtons = new ArrayList<>();

		editDocumentButton = new EditButton($("DisplayDocumentView.editDocument")) {
			@Override
			protected void buttonClick(ClickEvent event) {
				presenter.editDocumentButtonClicked();
			}
		};

		deleteDocumentButton = new DeleteButton($("DisplayDocumentView.deleteDocument")) {
			@Override
			protected void confirmButtonClick(ConfirmDialog dialog) {
				presenter.deleteDocumentButtonClicked();
			}
		};

		linkToDocumentButton = new LinkButton($("DocumentActionsComponent.linkToDocument")) {
			@Override
			protected void buttonClick(ClickEvent event) {
				presenter.linkToDocumentButtonClicked();
			}
		};
		linkToDocumentButton.setVisible(false);

		addAuthorizationButton = new LinkButton($("DocumentActionsComponent.addAuthorization")) {
			@Override
			protected void buttonClick(ClickEvent event) {
				presenter.addAuthorizationButtonClicked();
			}
		};

		createPDFAButton = new ConfirmDialogButton($("DocumentActionsComponent.createPDFA")) {
			@Override
			protected void confirmButtonClick(ConfirmDialog dialog) { presenter.createPDFAButtonClicked(); }

			@Override
			protected String getConfirmDialogMessage() {
				return $("ConfirmDialog.confirmCreatePDFA");
			}
		};
		((ConfirmDialogButton) createPDFAButton).setDialogMode(DialogMode.WARNING);

		shareDocumentButton = new LinkButton($("DocumentActionsComponent.shareDocument")) {
			@Override
			protected void buttonClick(ClickEvent event) {
				presenter.shareDocumentButtonClicked();
			}
		};

		addToCartButton = buildAddToCartButton();
		
		addToOrRemoveFromSelectionButton = new AddToOrRemoveFromSelectionButton(documentVO, getSessionContext().getSelectedRecordIds().contains(documentVO.getId()));

		uploadButton = new LinkButton($("DocumentActionsComponent.upload")) {
			@Override
			protected void buttonClick(ClickEvent event) {
				presenter.uploadButtonClicked();
			}
		};

		checkInButton = new LinkButton($("DocumentActionsComponent.checkIn")) {
			@Override
			protected void buttonClick(ClickEvent event) {
				presenter.checkInButtonClicked();
			}
		};

		alertWhenAvailableButton = new LinkButton($("RMObject.alertWhenAvailable")) {
			@Override
			protected void buttonClick(ClickEvent event) {
				presenter.alertWhenAvailableClicked();
			}
		};

		checkOutButton = new LinkButton($("DocumentActionsComponent.checkOut")) {
			@Override
			protected void buttonClick(ClickEvent event) {
				presenter.checkOutButtonClicked();
			}
		};

		finalizeButton = new ConfirmDialogButton(null, $("DocumentActionsComponent.finalize")) {
			@Override
			protected String getConfirmDialogMessage() {
				return $("DocumentActionsComponent.finalize.confirm");
			}

			@Override
			protected void confirmButtonClick(ConfirmDialog dialog) {
				presenter.finalizeButtonClicked();
			}
		};
		finalizeButton.addStyleName(ValoTheme.BUTTON_LINK);

		actionMenuButtons.add(editDocumentButton);

		copyContentButton = new LinkButton($("DocumentContextMenu.copyContent")) {
			@Override
			protected void buttonClick(ClickEvent event) {
				presenter.copyContentButtonClicked();
			}
		};

		actionMenuButtons.add(copyContentButton);

		reportGeneratorButton = new ReportGeneratorButton($("ReportGeneratorButton.buttonText"), $("ReportGeneratorButton.windowText"), this, getConstellioFactories().getAppLayerFactory(), getCollection(), PrintableReportListPossibleType.DOCUMENT,  getDocumentVO());

		if (presenter.hasContent()) {
			renameContentButton = new WindowButton($("DocumentContextMenu.renameContent"), $("DocumentContextMenu.renameContent"),
					WindowConfiguration.modalDialog("40%", "100px")) {
				@Override
				protected Component buildWindowContent() {
					final TextField title = new BaseTextField();
					title.setValue(presenter.getContentTitle());
					title.setWidth("100%");

					Button save = new BaseButton($("DisplayDocumentView.renameContentConfirm")) {
						@Override
						protected void buttonClick(ClickEvent event) {
							presenter.renameContentButtonClicked(title.getValue());
							getWindow().close();
						}
					};
					save.addStyleName(ValoTheme.BUTTON_PRIMARY);
					save.addStyleName(BaseForm.SAVE_BUTTON);

					Button cancel = new BaseButton($("DisplayDocumentView.renameContentCancel")) {
						@Override
						protected void buttonClick(ClickEvent event) {
							getWindow().close();
						}
					};

					HorizontalLayout form = new HorizontalLayout(title, save, cancel);
					form.setExpandRatio(title, 1);
					form.setSpacing(true);
					form.setWidth("95%");

					VerticalLayout layout = new VerticalLayout(form);
					layout.setSizeFull();

					return layout;
				}
			};

			signButton = new WindowButton($("DocumentContextMenu.sign"), $("DocumentContextMenu.sign"),
					WindowConfiguration.modalDialog("40%", "300px")) {
				@Override
				protected Component buildWindowContent() {
					final ByteArrayOutputStream stream = new ByteArrayOutputStream();

					final Upload certificate = new Upload($("DisplayDocumentWindow.sign.certificate"), new Receiver() {
						@Override
						public OutputStream receiveUpload(String filename, String mimeType) {
							return stream;
						}
					});
					certificate.addSucceededListener(new SucceededListener() {
						@Override
						public void uploadSucceeded(SucceededEvent event) {

						}
					});

					final PasswordField password = new PasswordField($("DisplayDocumentWindow.sign.password"));

					Button sign = new Button($("DisplayDocumentViewImpl.sign.sign"));
					sign.addClickListener(new ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {
							getWindow().close();
						}
					});

					FileDownloader downloader = new FileDownloader(new StreamResource(new StreamSource() {
						@Override
						public InputStream getStream() {
							return presenter.getSignatureInputStream(stream.toString(), password.getValue());
						}
					}, "signature.pdf"));
					downloader.extend(sign);

					VerticalLayout layout = new VerticalLayout(certificate, password, sign);
					layout.setSpacing(true);
					return layout;
				}
			};

			actionMenuButtons.add(actionMenuButtons.indexOf(copyContentButton), renameContentButton);

			publishButton = new LinkButton($("DocumentContextMenu.publish")) {
				@Override
				protected void buttonClick(ClickEvent event) {
					presenter.publishButtonClicked();
				}
			};
			if(presenter.hasCurrentUserPermissionToPublishOnCurrentDocument()) {
				actionMenuButtons.add(publishButton);
			}

			unpublishButton = new LinkButton($("DocumentContextMenu.unpublish")) {
				@Override
				protected void buttonClick(ClickEvent event) {
					presenter.unpublishButtonClicked();
				}
			};
			if(presenter.hasCurrentUserPermissionToPublishOnCurrentDocument()) {
				actionMenuButtons.add(unpublishButton);
			}

			WindowButton.WindowConfiguration publicLinkConfig = new WindowConfiguration(true, false, "75%", "125px");
			publicLinkButton = new WindowButton(
					$("DocumentContextMenu.publicLink"), $("DocumentContextMenu.publicLink"), publicLinkConfig) {
				@Override
				protected Component buildWindowContent() {
					Label link = new Label(presenter.getPublicLink());
					Label message = new Label($("DocumentContextMenu.publicLinkInfo"));
					message.addStyleName(ValoTheme.LABEL_BOLD);
					return new VerticalLayout(message, link);
				}
			};
			actionMenuButtons.add(publicLinkButton);

			//actionMenuButtons.add(sign);
		}
		startWorkflowButton = new StartWorkflowButton();
		startWorkflowButton.setVisible(presenter.hasPermissionToStartWorkflow());

		actionMenuButtons.add(deleteDocumentButton);
		actionMenuButtons.add(linkToDocumentButton);
		actionMenuButtons.add(addAuthorizationButton);
		actionMenuButtons.add(createPDFAButton);
		actionMenuButtons.add(shareDocumentButton);
		if (presenter.hasCurrentUserPermissionToUseCart()) {
			actionMenuButtons.add(addToCartButton);
		}
		actionMenuButtons.add(addToOrRemoveFromSelectionButton);
		actionMenuButtons.add(uploadButton);
		actionMenuButtons.add(checkInButton);
		actionMenuButtons.add(alertWhenAvailableButton);
		actionMenuButtons.add(checkOutButton);

		if(presenter.hasWritePermission()) {
			actionMenuButtons.add(finalizeButton);
		}

		if (presenter.hasPermissionToStartWorkflow()) {
			actionMenuButtons.add(startWorkflowButton);
		}
		actionMenuButtons.add(reportGeneratorButton);
		return actionMenuButtons;
	}

	private WindowButton buildAddToCartButton() {
		return new WindowButton($("DisplayFolderView.addToCart"),$("DisplayFolderView.selectCart")) {
			@Override
			protected Component buildWindowContent() {
				VerticalLayout layout = new VerticalLayout();

				HorizontalLayout newCartLayout = new HorizontalLayout();
				newCartLayout.setSpacing(true);
				newCartLayout.addComponent(new Label($("CartView.newCart")));
				final BaseTextField newCartTitleField;
				newCartLayout.addComponent(newCartTitleField = new BaseTextField());
				BaseButton saveButton;
				newCartLayout.addComponent(saveButton = new BaseButton($("save")) {
					@Override
					protected void buttonClick(ClickEvent event) {
						presenter.createNewCartAndAddToItRequested(newCartTitleField.getValue());
						getWindow().close();
					}
				});
				saveButton.addStyleName(ValoTheme.BUTTON_PRIMARY);

				TabSheet tabSheet = new TabSheet();
				final RecordVOLazyContainer ownedCartsContainer = new RecordVOLazyContainer(presenter.getOwnedCartsDataProvider());
				RecordVOTable ownedCartsTable = new RecordVOTable($("CartView.ownedCarts"), ownedCartsContainer);
				ownedCartsTable.addItemClickListener(new ItemClickListener() {
					@Override
					public void itemClick(ItemClickEvent event) {
						presenter.addToCartRequested(ownedCartsContainer.getRecordVO((int)event.getItemId()));
						getWindow().close();
					}
				});

				ownedCartsTable.setPageLength(Math.min(15, ownedCartsContainer.size()));
				ownedCartsTable.setWidth("100%");

				final RecordVOLazyContainer sharedCartsContainer = new RecordVOLazyContainer(presenter.getSharedCartsDataProvider());
				RecordVOTable sharedCartsTable = new RecordVOTable($("CartView.sharedCarts"), sharedCartsContainer);
				sharedCartsTable.addItemClickListener(new ItemClickListener() {
					@Override
					public void itemClick(ItemClickEvent event) {
						presenter.addToCartRequested(sharedCartsContainer.getRecordVO((int)event.getItemId()));
						getWindow().close();
					}
				});

				sharedCartsTable.setPageLength(Math.min(15, ownedCartsContainer.size()));
				sharedCartsTable.setWidth("100%");
				tabSheet.addTab(ownedCartsTable);
				tabSheet.addTab(sharedCartsTable);
				layout.addComponents(newCartLayout,tabSheet);
				return layout;
			}
		};
	}

	private void initUploadWindow() {
		if (uploadWindow == null) {
			if (documentVO != null) {
				Map<RecordVO, MetadataVO> record = new HashMap<>();
				record.put(documentVO, documentVO.getMetadata(Document.CONTENT));
				uploadWindow = new UpdateContentVersionWindowImpl(record) {
					@Override
					public void close() {
						super.close();
						presenter.updateWindowClosed();
						presenter.updateContentVersions();
						versionTable.refreshRowCache();
					}
				};
			}
		}
	}

	@Override
	public void drop(DragAndDropEvent event) {
		openUploadWindow(false);
		uploadWindow.drop(event);
	}

	@Override
	public AcceptCriterion getAcceptCriterion() {
		initUploadWindow();
		if (uploadWindow != null) {
			return uploadWindow.getAcceptCriterion();
		} else {
			return AcceptAll.get();
		}

	}

	@Override
	public void openUploadWindow(boolean checkingIn) {
		uploadWindow = null;
		initUploadWindow();
		uploadWindow.open(checkingIn);
	}

	@Override
	public void setStartWorkflowButtonState(ComponentState state) {
		startWorkflowButton.setVisible(state.isVisible());
		startWorkflowButton.setEnabled(state.isEnabled());
	}

	@Override
	public void setUploadButtonState(ComponentState state) {
		uploadButton.setVisible(state.isVisible());
		uploadButton.setEnabled(state.isEnabled());
	}

	@Override
	public void setCheckInButtonState(ComponentState state) {
		checkInButton.setVisible(state.isVisible());
		checkInButton.setEnabled(state.isEnabled());
	}

	@Override
	public void setAlertWhenAvailableButtonState(ComponentState state) {
		alertWhenAvailableButton.setVisible(state.isVisible());
		alertWhenAvailableButton.setEnabled(state.isEnabled());
	}

	@Override
	public void setCheckOutButtonState(ComponentState state) {
		checkOutButton.setVisible(state.isVisible());
		checkOutButton.setEnabled(state.isEnabled());
	}

	@Override
	public void setGenerateMetadataButtonState(ComponentState state) {
		reportGeneratorButton.setVisible(state.isVisible());
		reportGeneratorButton.setEnabled(state.isEnabled());
	}

	@Override
	public void setFinalizeButtonVisible(boolean visible) {
		finalizeButton.setVisible(visible);
	}

	@Override
	public void setEditDocumentButtonState(ComponentState state) {
		editDocumentButton.setVisible(state.isVisible());
		editDocumentButton.setEnabled(state.isEnabled());
		if (renameContentButton != null) {
			renameContentButton.setVisible(state.isVisible());
			renameContentButton.setEnabled(state.isEnabled());
		}
	}

	@Override
	public void setAddDocumentButtonState(ComponentState state) {
		//nothing to set only from context
		if (copyContentButton != null) {
			copyContentButton.setVisible(state.isVisible());
			copyContentButton.setEnabled(state.isEnabled());
		}
	}

	@Override
	public void setDeleteDocumentButtonState(ComponentState state) {
		deleteDocumentButton.setVisible(state.isVisible());
		deleteDocumentButton.setEnabled(state.isEnabled());
	}

	@Override
	public void setAddAuthorizationButtonState(ComponentState state) {
		addAuthorizationButton.setVisible(state.isVisible());
		addAuthorizationButton.setEnabled(state.isEnabled());
	}

	@Override
	public void setShareDocumentButtonState(ComponentState state) {
		shareDocumentButton.setVisible(state.isVisible());
		shareDocumentButton.setEnabled(state.isEnabled());
	}

	@Override
	public void setCreatePDFAButtonState(ComponentState state) {
		createPDFAButton.setVisible(state.isVisible());
		createPDFAButton.setEnabled(state.isEnabled());
	}

	@Override
	public void setBorrowedMessage(String borrowedMessageKey, String... args) {
		if (StringUtils.isNotBlank(borrowedMessageKey)) {
			borrowedLabel.setVisible(true);
			borrowedLabel.setValue($(borrowedMessageKey, (Object[]) args));
		} else {
			borrowedLabel.setVisible(false);
			borrowedLabel.setValue(null);
		}
	}

	@Override
	public void setPublishButtons(boolean published) {
		if (publishButton != null && presenter.hasCurrentUserPermissionToPublishOnCurrentDocument()) {
			publishButton.setVisible(!published);
		}
		if (unpublishButton != null && presenter.hasCurrentUserPermissionToPublishOnCurrentDocument()) {
			unpublishButton.setVisible(published);
		}
		if (publicLinkButton != null) {
			publicLinkButton.setVisible(published);
		}
	}

	@Override
	public void openAgentURL(String agentURL) {
		Page.getCurrent().open(agentURL, null);
	}

	@Override
	public void setTaxonomyCode(String taxonomyCode) {
		this.taxonomyCode = taxonomyCode;
	}

	public void addTabSheetDecorator(TabSheetDecorator decorator) {
		this.tabSheetDecorators.add(decorator);
	}
	
	public List<TabSheetDecorator> getTabSheetDecorators() {
		return this.tabSheetDecorators;
	}
	
	public void removeTabSheetDecorator(TabSheetDecorator decorator) {
		this.tabSheetDecorators.remove(decorator);
	}
	
	@Override
	public DocumentVO getDocumentVO() {
		return documentVO;
	}

	@Override
	public void refreshParent() {
		// No parent
	}

	private class StartWorkflowButton extends WindowButton {
		public StartWorkflowButton() {
			super($("TasksManagementView.startWorkflow"), $("TasksManagementView.startWorkflow"), modalDialog("75%", "75%"));
		}

		@Override
		protected Component buildWindowContent() {
			RecordVOTable table = new RecordVOTable(presenter.getWorkflows());
			table.setWidth("98%");
			table.addItemClickListener(new ItemClickListener() {
				@Override
				public void itemClick(ItemClickEvent event) {
					RecordVOItem item = (RecordVOItem) event.getItem();
					presenter.workflowStartRequested(item.getRecord());
					getWindow().close();
				}
			});
			return table;
		}
	}
}
