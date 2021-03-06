package com.constellio.app.ui.pages.home;

import static com.constellio.app.ui.i18n.i18n.$;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constellio.app.entities.navigation.PageItem;
import com.constellio.app.entities.navigation.PageItem.RecordTree;
import com.constellio.app.modules.es.model.connectors.smb.ConnectorSmbDocument;
import com.constellio.app.modules.rm.RMConfigs;
import com.constellio.app.modules.rm.constants.RMTaxonomies;
import com.constellio.app.modules.rm.navigation.RMViews;
import com.constellio.app.modules.rm.ui.util.ConstellioAgentUtils;
import com.constellio.app.modules.rm.wrappers.ContainerRecord;
import com.constellio.app.modules.rm.wrappers.Document;
import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.app.ui.framework.components.breadcrumb.BaseBreadcrumbTrail;
import com.constellio.app.ui.framework.data.RecordLazyTreeDataProvider;
import com.constellio.app.ui.pages.base.BasePresenter;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.app.ui.params.ParamUtils;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchema;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.services.configs.SystemConfigurationsManager;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.records.RecordServicesRuntimeException.NoSuchRecordWithId;
import com.constellio.model.services.schemas.MetadataSchemasManager;
import com.constellio.model.services.schemas.SchemaUtils;
import com.constellio.model.services.search.SearchServices;
import com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators;

public class HomePresenter extends BasePresenter<HomeView> {

	private static Logger LOGGER = LoggerFactory.getLogger(HomePresenter.class);

	private String currentTab;
	
	private List<PageItem> tabItems;

	public HomePresenter(HomeView view) {
		super(view);
		tabItems = navigationConfig().getFragments(HomeView.TABS);
	}

	public HomePresenter forParams(String params) {
		if (getCurrentUser() != null) {
			Map<String, String> paramsMap = ParamUtils.getParamsMap(params);
			String tabParam = paramsMap.get("tab");
			String taxonomyCodeParam = paramsMap.get("taxonomyCode");
			String taxonomyMetadataParam = paramsMap.get("taxonomyMetadata");
			String expandedRecordIdParam = paramsMap.get("expandedRecordId");
			
			if (tabParam == null) {
				currentTab = getDefaultTab();
			} else {
				currentTab = tabParam;
			}
			
			SessionContext sessionContext = view.getSessionContext();
			if (taxonomyCodeParam != null) {
				// Looking for a tree tab matching current tab 
				loop1 : for (PageItem tabItem : tabItems) {
					if ((tabItem instanceof RecordTree) && currentTab.equals(tabItem.getCode())) {
						RecordTree recordTree = (RecordTree) tabItem;
						List<RecordLazyTreeDataProvider> dataProviders = recordTree.getDataProviders(appLayerFactory, sessionContext);
						for (int i = 0; i < dataProviders.size(); i++) {
							RecordLazyTreeDataProvider dataProvider = dataProviders.get(i);
							String dataProviderTaxonomyCode = dataProvider.getTaxonomyCode();
							if (taxonomyCodeParam.equals(dataProviderTaxonomyCode)) {
								recordTree.setDefaultDataProvider(i);

								if (expandedRecordIdParam != null) {
									Record expandedRecord = getRecord(expandedRecordIdParam);
									
									List<String> expandedRecordIds = new ArrayList<>();
									expandedRecordIds.add(0, expandedRecordIdParam);
									
									Record lastAddedParent = null;
									String currentParentId = expandedRecord.getParentId();
									while (currentParentId != null) {
										lastAddedParent = getRecord(currentParentId);
										expandedRecordIds.add(0, currentParentId);
										currentParentId = lastAddedParent.getParentId();
									}
									
									String taxonomyRecordId;
									if (taxonomyMetadataParam != null) {
										Record recordWithTaxonomyMetadata;
										if (lastAddedParent != null) {
											recordWithTaxonomyMetadata = lastAddedParent;
										} else {
											recordWithTaxonomyMetadata = expandedRecord;
										}
										MetadataSchemasManager schemasManager = modelLayerFactory.getMetadataSchemasManager();
										MetadataSchema expandedRecordSchema = schemasManager.getSchemaOf(recordWithTaxonomyMetadata);
										Metadata taxonomyMetadata = expandedRecordSchema.get(taxonomyMetadataParam);
										taxonomyRecordId = expandedRecord.get(taxonomyMetadata);
									} else {
										taxonomyRecordId = expandedRecordIdParam;
									}
									if (!expandedRecordIds.contains(taxonomyRecordId)) {
										expandedRecordIds.add(0, taxonomyRecordId);
									}
									
									Record taxonomyRecord = getRecord(taxonomyRecordId);
									String currentTaxonomyRecordParentId = taxonomyRecord.getParentId();
									while (currentTaxonomyRecordParentId != null) {
										Record taxonomyRecordParent = getRecord(currentTaxonomyRecordParentId);
										expandedRecordIds.add(0, currentTaxonomyRecordParentId);
										currentTaxonomyRecordParentId = taxonomyRecordParent.getParentId();
									}
									
									recordTree.setExpandedRecordIds(expandedRecordIds);
								}
								
								break loop1;
							}
						}
					}
				}
			}
		} else {
			view.updateUI();
		}
		return this;
	}

	public List<PageItem> getTabs() {
		return tabItems;
	}

	public String getDefaultTab() {
		String startTab = getCurrentUser().getStartTab();
		if (startTab == null) {
			startTab = presenterService().getSystemConfigs().getDefaultStartTab();
		}
		return startTab;
	}

	public String getCurrentTab() {
		return currentTab;
	}

	public void tabSelected(String tabCode) {
		currentTab = tabCode;
	}

	public void recordClicked(String id, String taxonomyCode) {
		if (id != null && !id.startsWith("dummy")) {
			try {
				// Recent folders or documents
				if (taxonomyCode == null) {
					taxonomyCode = RMTaxonomies.CLASSIFICATION_PLAN;
				}
				Record record = getRecord(id);
				String schemaCode = record.getSchemaCode();
				String schemaTypeCode = SchemaUtils.getSchemaTypeCode(schemaCode);
				if (Folder.SCHEMA_TYPE.equals(schemaTypeCode)) {
					view.getUIContext().setAttribute(BaseBreadcrumbTrail.TAXONOMY_CODE, taxonomyCode);
					view.navigate().to(RMViews.class).displayFolder(id);
				} else if (Document.SCHEMA_TYPE.equals(schemaTypeCode)) {
					view.getUIContext().setAttribute(BaseBreadcrumbTrail.TAXONOMY_CODE, taxonomyCode);
					view.navigate().to(RMViews.class).displayDocument(id);
				} else if (ContainerRecord.SCHEMA_TYPE.equals(schemaTypeCode)) {
					view.navigate().to(RMViews.class).displayContainer(id);
				} else if (ConstellioAgentUtils.isAgentSupported()) {
					String smbMetadataCode;
					if (ConnectorSmbDocument.SCHEMA_TYPE.equals(schemaTypeCode)) {
						smbMetadataCode = ConnectorSmbDocument.URL;
//					} else if (ConnectorSmbFolder.SCHEMA_TYPE.equals(schemaTypeCode)) {
//						smbMetadataCode = ConnectorSmbFolder.URL;
                    } else {
                        smbMetadataCode = null;
                    }
                    if (smbMetadataCode != null) {
                        Metadata smbUrlMetadata = types().getMetadata(schemaTypeCode + "_default_" + smbMetadataCode);
                        String smbPath = record.get(smbUrlMetadata);
                        SystemConfigurationsManager systemConfigurationsManager = modelLayerFactory.getSystemConfigurationsManager();
                        RMConfigs rmConfigs = new RMConfigs(systemConfigurationsManager);
                        if (rmConfigs.isAgentEnabled()) {
                            String agentSmbPath = ConstellioAgentUtils.getAgentSmbURL(smbPath);
                            view.openURL(agentSmbPath);
                        } else {
							String path = smbPath;
							if (StringUtils.startsWith(path, "smb://")) {
								path = "file://" + StringUtils.removeStart(path, "smb://");
							}
							view.openURL(path);
                        }
                    }
                }
            } catch (NoSuchRecordWithId e) {
                view.showErrorMessage($("HomeView.noSuchRecord"));
                LOGGER.warn("Error while clicking on record id " + id, e);
            }
        }
    }

	@Override
	protected boolean hasPageAccess(String params, User user) {
		return true;
	}

	private Record getRecord(String id) {
		RecordServices recordServices = modelLayerFactory.newRecordServices();
		return recordServices.getDocumentById(id);
	}

	boolean isSelected(String recordId) {
		SessionContext sessionContext = view.getSessionContext();
		return sessionContext.getSelectedRecordIds().contains(recordId);
	}

	void selectionChanged(String recordId, Boolean selected) {
		SessionContext sessionContext = view.getSessionContext();
		SearchServices searchServices = modelLayerFactory.newSearchServices();
		Record record = searchServices.searchSingleResult(LogicalSearchQueryOperators.fromAllSchemasIn(sessionContext.getCurrentCollection())
				.where(Schemas.IDENTIFIER).isEqualTo(recordId));
		String schemaTypeCode = record == null? null:record.getTypeCode();
		if (selected) {
			sessionContext.addSelectedRecordId(recordId, schemaTypeCode);
		} else {
			sessionContext.removeSelectedRecordId(recordId, schemaTypeCode);
		}
	}
	
}
