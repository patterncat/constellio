package com.constellio.app.modules.rm.ui.pages.userDocuments;

import static com.constellio.app.ui.i18n.i18n.$;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constellio.app.modules.rm.navigation.RMNavigationConfiguration;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.services.decommissioning.DecommissioningService;
import com.constellio.app.modules.rm.wrappers.Document;
import com.constellio.app.modules.rm.wrappers.RMUserFolder;
import com.constellio.app.services.factories.ConstellioFactories;
import com.constellio.app.ui.entities.ContentVersionVO;
import com.constellio.app.ui.entities.ContentVersionVO.InputStreamProvider;
import com.constellio.app.ui.entities.MetadataSchemaVO;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.entities.RecordVO.VIEW_MODE;
import com.constellio.app.ui.entities.UserDocumentVO;
import com.constellio.app.ui.entities.UserVO;
import com.constellio.app.ui.framework.builders.MetadataSchemaToVOBuilder;
import com.constellio.app.ui.framework.builders.UserDocumentToVOBuilder;
import com.constellio.app.ui.framework.builders.UserFolderToVOBuilder;
import com.constellio.app.ui.framework.data.RecordVODataProvider;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.app.ui.pages.base.SingleSchemaBasePresenter;
import com.constellio.app.ui.util.MessageUtils;
import com.constellio.data.io.services.facades.IOServices;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.records.wrappers.UserDocument;
import com.constellio.model.entities.records.wrappers.UserFolder;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchema;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.frameworks.validation.ValidationException;
import com.constellio.model.services.contents.ContentFactory;
import com.constellio.model.services.contents.icap.IcapException;
import com.constellio.model.services.migrations.ConstellioEIMConfigs;
import com.constellio.model.services.search.SearchServices;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;
import com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators;
import com.constellio.model.services.users.UserServices;

public class ListUserDocumentsPresenter extends SingleSchemaBasePresenter<ListUserDocumentsView> {

	Boolean allItemsSelected = false;

	Boolean allItemsDeselected = false;

	private static Logger LOGGER = LoggerFactory.getLogger(ListUserDocumentsPresenter.class);

	private UserDocumentToVOBuilder voBuilder = new UserDocumentToVOBuilder();

	private RecordVODataProvider userFoldersDataProvider;

	private RecordVODataProvider userDocumentsDataProvider;

	private ConstellioEIMConfigs eimConfigs;

	public ListUserDocumentsPresenter(ListUserDocumentsView view) {
		super(view, UserDocument.DEFAULT_SCHEMA);
		this.eimConfigs = new ConstellioEIMConfigs(modelLayerFactory.getSystemConfigurationsManager());
	}

	@Override
	protected boolean hasPageAccess(String params, User user) {
		return true;
	}

	void forParams(String params) {
		SessionContext sessionContext = view.getSessionContext();
		MetadataSchemaToVOBuilder schemaVOBuilder = new MetadataSchemaToVOBuilder();
		UserDocumentToVOBuilder userDocumentVOBuilder = new UserDocumentToVOBuilder();
		UserFolderToVOBuilder userFolderVOBuilder = new UserFolderToVOBuilder();

		MetadataSchema userFolderSchema = schema(UserFolder.DEFAULT_SCHEMA);
		MetadataSchemaVO userFolderSchemaVO = schemaVOBuilder.build(userFolderSchema, VIEW_MODE.TABLE, sessionContext);
		userFoldersDataProvider = new RecordVODataProvider(userFolderSchemaVO, userFolderVOBuilder, modelLayerFactory,
				view.getSessionContext()) {
			@Override
			protected LogicalSearchQuery getQuery() {
				return getUserFoldersQuery();
			}
		};

		MetadataSchema userDocumentSchema = schema(UserDocument.DEFAULT_SCHEMA);
		MetadataSchemaVO userDocumentSchemaVO = schemaVOBuilder.build(userDocumentSchema, VIEW_MODE.TABLE, sessionContext);
		userDocumentsDataProvider = new RecordVODataProvider(userDocumentSchemaVO, userDocumentVOBuilder, modelLayerFactory,
				view.getSessionContext()) {
			@Override
			protected LogicalSearchQuery getQuery() {
				return getUserDocumentsQuery();
			}
		};

		computeAllItemsSelected();
		view.setUserContent(Arrays.asList(userFoldersDataProvider, userDocumentsDataProvider));
	}

	private LogicalSearchQuery getUserFoldersQuery() {
		User currentUser = getCurrentUser();
		MetadataSchema userFolderSchema = schema(UserFolder.DEFAULT_SCHEMA);
		Metadata userMetadata = userFolderSchema.getMetadata(UserFolder.USER);
		Metadata parentUserFolderMetadata = userFolderSchema.getMetadata(UserFolder.PARENT_USER_FOLDER);

		LogicalSearchQuery query = new LogicalSearchQuery();
		query.setCondition(
				LogicalSearchQueryOperators.from(userFolderSchema).where(userMetadata).is(currentUser.getWrappedRecord())
						.andWhere(parentUserFolderMetadata).isNull());
		query.sortAsc(Schemas.IDENTIFIER);

		return query;
	}

	private LogicalSearchQuery getUserDocumentsQuery() {
		User currentUser = getCurrentUser();
		MetadataSchema userDocumentSchema = schema(UserDocument.DEFAULT_SCHEMA);
		Metadata userMetadata = userDocumentSchema.getMetadata(UserDocument.USER);
		Metadata userFolderMetadata = userDocumentSchema.getMetadata(UserDocument.USER_FOLDER);

		LogicalSearchQuery query = new LogicalSearchQuery();
		query.setCondition(
				LogicalSearchQueryOperators.from(userDocumentSchema).where(userMetadata).is(currentUser.getWrappedRecord())
						.andWhere(userFolderMetadata).isNull());
		query.sortAsc(Schemas.IDENTIFIER);

		return query;
	}

	boolean isSelected(RecordVO recordVO) {
		SessionContext sessionContext = view.getSessionContext();
		return sessionContext.getSelectedRecordIds().contains(recordVO.getId());
	}

	void selectionChanged(RecordVO recordVO, boolean selected) {
		allItemsSelected = false;
		allItemsDeselected = false;

		String recordId = recordVO.getId();
		String schemaTypeCode = recordVO.getSchema().getTypeCode();
		SessionContext sessionContext = view.getSessionContext();
		List<String> selectedRecordIds = sessionContext.getSelectedRecordIds();
		if (selected && !selectedRecordIds.contains(recordId)) {
			sessionContext.addSelectedRecordId(recordId, schemaTypeCode);
		} else if (!selected) {
			sessionContext.removeSelectedRecordId(recordId, schemaTypeCode);
		}
		view.refresh();
	}

	void handleFile(final File file, String fileName, String mimeType, long length) {
		MetadataSchema userDocumentSchema = schema(UserDocument.DEFAULT_SCHEMA);
		Record newRecord = recordServices().newRecordWithSchema(userDocumentSchema);

		SessionContext sessionContext = view.getSessionContext();
		UserVO currentUserVO = sessionContext.getCurrentUser();
		String collection = sessionContext.getCurrentCollection();

		UserServices userServices = modelLayerFactory.newUserServices();

		User currentUser = userServices.getUserInCollection(currentUserVO.getUsername(), collection);

		InputStreamProvider inputStreamProvider = new InputStreamProvider() {
			@Override
			public InputStream getInputStream(String streamName) {
				IOServices ioServices = ConstellioFactories.getInstance().getIoServicesFactory().newIOServices();
				try {
					return ioServices.newFileInputStream(file, streamName);
				} catch (FileNotFoundException e) {
					return null;
				}
			}

			@Override
			public void deleteTemp() {
				FileUtils.deleteQuietly(file);
				file.deleteOnExit();
			}
		};
		UserDocumentVO newUserDocumentVO = (UserDocumentVO) voBuilder.build(newRecord, VIEW_MODE.FORM, view.getSessionContext());
		ContentVersionVO contentVersionVO = new ContentVersionVO(null, null, fileName, mimeType, length, null, null, null,
				null, null, null, inputStreamProvider);
		contentVersionVO.setMajorVersion(true);
		newUserDocumentVO.set(UserDocument.USER, currentUser.getWrappedRecord());
		newUserDocumentVO.set(UserDocument.CONTENT, contentVersionVO);

		try {
			// TODO More elegant way to achieve this
			newRecord = toRecord(newUserDocumentVO);

			addOrUpdate(newRecord);
			contentVersionVO.getInputStreamProvider().deleteTemp();
			if (Boolean.TRUE.equals(contentVersionVO.hasFoundDuplicate())) {
				RMSchemasRecordsServices rm = new RMSchemasRecordsServices(collection, appLayerFactory);
				LogicalSearchQuery duplicateDocumentsQuery = new LogicalSearchQuery()
						.setCondition(LogicalSearchQueryOperators.from(rm.documentSchemaType())
								.where(rm.document.content()).is(ContentFactory.isHash(contentVersionVO.getDuplicatedHash()))
								.andWhere(Schemas.LOGICALLY_DELETED_STATUS).isFalseOrNull()
						)
						.filteredWithUser(getCurrentUser());
				List<Document> duplicateDocuments = rm.searchDocuments(duplicateDocumentsQuery);
				LogicalSearchQuery duplicateUserDocumentsQuery = new LogicalSearchQuery()
						.setCondition(LogicalSearchQueryOperators.from(rm.userDocumentSchemaType())
								.where(rm.userDocument.content()).is(ContentFactory.isHash(contentVersionVO.getDuplicatedHash()))
								.andWhere(Schemas.LOGICALLY_DELETED_STATUS).isFalseOrNull()
								.andWhere(Schemas.IDENTIFIER).isNotEqual(newRecord.getId()))
						.filteredWithUser(getCurrentUser());
				List<UserDocument> duplicateUserDocuments = rm.searchUserDocuments(duplicateUserDocumentsQuery);
				if (duplicateDocuments.size() > 0 || duplicateUserDocuments.size() > 0) {
					StringBuilder message = new StringBuilder($("ContentManager.hasFoundDuplicateWithConfirmation", StringUtils.defaultIfBlank(contentVersionVO.getFileName(), "")));
					message.append("<br>");
					for (Document document : duplicateDocuments) {
						message.append("<br>-");
						message.append(document.getTitle());
						message.append(": ");
						message.append(generateDisplayLink(document));
					}
					for (UserDocument userDocument : duplicateUserDocuments) {
						message.append("<br>-");
						message.append(userDocument.getTitle());
						message.append(": ");
						message.append(generateDisplayLink(userDocument));
					}
					view.showClickableMessage(message.toString());
				}
			}
			userDocumentsDataProvider.fireDataRefreshEvent();
		} catch (final IcapException e) {
			view.showErrorMessage(e.getMessage());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			Throwable cause = e.getCause();
			if (cause != null && StringUtils.isNotBlank(cause.getMessage()) && cause instanceof ValidationException) {
				view.showErrorMessage(cause.getMessage());
			} else {
				view.showErrorMessage(MessageUtils.toMessage(e));
			}
		}
	}

	public void deleteButtonClicked(RecordVO userContentVO) {
		User currentUser = getCurrentUser();
		String schemaTypeCode = userContentVO.getSchema().getTypeCode();
		Record record;
		if (UserFolder.SCHEMA_TYPE.equals(schemaTypeCode)) {
			this.setSchemaCode(UserFolder.DEFAULT_SCHEMA);
			record = toRecord(userContentVO);
			this.setSchemaCode(UserDocument.DEFAULT_SCHEMA);
		} else {
			record = toRecord(userContentVO);
		}

		try {
			if (UserFolder.SCHEMA_TYPE.equals(schemaTypeCode)) {
				RMSchemasRecordsServices rm = new RMSchemasRecordsServices(collection, appLayerFactory);
				RMUserFolder userFolder = rm.wrapUserFolder(record);
				DecommissioningService decommissioningService = new DecommissioningService(collection, appLayerFactory);
				decommissioningService.deleteUserFolder(userFolder, currentUser);
				userFoldersDataProvider.fireDataRefreshEvent();
			} else {
				delete(record);
				userDocumentsDataProvider.fireDataRefreshEvent();
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			view.showErrorMessage(MessageUtils.toMessage(e));
		}
	}

	private int secondsSinceLastRefresh = 0;

	private long lastKnownUserFoldersCount = -1;

	private long lastKnownUserDocumentsCount = -1;

	void backgroundViewMonitor() {
		secondsSinceLastRefresh++;
		if (secondsSinceLastRefresh >= 10) {
			SearchServices searchServices = modelLayerFactory.newSearchServices();

			secondsSinceLastRefresh = 0;
			long userFoldersCount = searchServices.getResultsCount(getUserFoldersQuery());
			if (lastKnownUserFoldersCount != userFoldersCount) {
				lastKnownUserFoldersCount = userFoldersCount;
				userFoldersDataProvider.fireDataRefreshEvent();
			}
			long userDocumentsCount = searchServices.getResultsCount(getUserDocumentsQuery());
			if (lastKnownUserDocumentsCount != userDocumentsCount) {
				lastKnownUserDocumentsCount = userDocumentsCount;
				userDocumentsDataProvider.fireDataRefreshEvent();
			}
		}
	}

	void computeAllItemsSelected() {
		SessionContext sessionContext = view.getSessionContext();
		List<String> selectedRecordIds = sessionContext.getSelectedRecordIds();
		SearchServices searchServices = modelLayerFactory.newSearchServices();

		List<String> userFolderIds = searchServices.searchRecordIds(getUserFoldersQuery());
		for (String userFolderId : userFolderIds) {
			if (!selectedRecordIds.contains(userFolderId)) {
				allItemsSelected = false;
				return;
			}
		}
		List<String> userDocumentIds = searchServices.searchRecordIds(getUserDocumentsQuery());
		for (String userDocumentId : userDocumentIds) {
			if (!selectedRecordIds.contains(userDocumentId)) {
				allItemsSelected = false;
				return;
			}
		}
		allItemsSelected = !userFolderIds.isEmpty() || !userDocumentIds.isEmpty();
	}

	boolean isAllItemsSelected() {
		return allItemsSelected;
	}

	boolean isAllItemsDeselected() {
		return allItemsDeselected;
	}

	void selectAllClicked() {
		allItemsSelected = true;
		allItemsDeselected = false;

		SessionContext sessionContext = view.getSessionContext();
		SearchServices searchServices = modelLayerFactory.newSearchServices();
		List<String> userFolderIds = searchServices.searchRecordIds(getUserFoldersQuery());
		for (String userFolderId : userFolderIds) {
			sessionContext.addSelectedRecordId(userFolderId, UserFolder.SCHEMA_TYPE);
		}
		List<String> userDocumentIds = searchServices.searchRecordIds(getUserDocumentsQuery());
		for (String userDocumentId : userDocumentIds) {
			sessionContext.addSelectedRecordId(userDocumentId, UserDocument.SCHEMA_TYPE);
		}
	}

	void deselectAllClicked() {
		allItemsSelected = false;
		allItemsDeselected = true;

		SessionContext sessionContext = view.getSessionContext();
		SearchServices searchServices = modelLayerFactory.newSearchServices();
		List<String> userFolderIds = searchServices.searchRecordIds(getUserFoldersQuery());
		for (String userFolderId : userFolderIds) {
			sessionContext.removeSelectedRecordId(userFolderId, UserFolder.SCHEMA_TYPE);
		}
		List<String> userDocumentIds = searchServices.searchRecordIds(getUserDocumentsQuery());
		for (String userDocumentId : userDocumentIds) {
			sessionContext.removeSelectedRecordId(userDocumentId, UserDocument.SCHEMA_TYPE);
		}
	}

	String generateDisplayLink(Document document) {
		String constellioUrl = eimConfigs.getConstellioUrl();
		String displayURL = RMNavigationConfiguration.DISPLAY_DOCUMENT;
		String url = constellioUrl + "#!" + displayURL + "/" + document.getId();
		return "<a href=\"" + url + "\">" + url + "</a>";
	}

	String generateDisplayLink(UserDocument userDocument) {
		String constellioUrl = eimConfigs.getConstellioUrl();
		String displayURL = RMNavigationConfiguration.LIST_USER_DOCUMENTS;
		String url = constellioUrl + "#!" + displayURL;
		return "<a href=\"" + url + "\">" + url + "</a>";
	}
}
