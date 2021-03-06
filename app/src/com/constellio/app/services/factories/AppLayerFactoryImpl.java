package com.constellio.app.services.factories;

import static com.constellio.model.services.records.reindexing.ReindexationParams.recalculateAndRewriteSchemaTypesInBackground;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constellio.app.api.cmis.binding.global.CmisCacheManager;
import com.constellio.app.conf.AppLayerConfiguration;
import com.constellio.app.extensions.AppLayerExtensions;
import com.constellio.app.extensions.api.scripts.Scripts;
import com.constellio.app.extensions.impl.DefaultPagesComponentsExtension;
import com.constellio.app.modules.complementary.ESRMRobotsModule;
import com.constellio.app.modules.es.ConstellioESModule;
import com.constellio.app.modules.rm.ConstellioRMModule;
import com.constellio.app.modules.rm.model.labelTemplate.LabelTemplateManager;
import com.constellio.app.modules.robots.ConstellioRobotsModule;
import com.constellio.app.modules.tasks.TaskModule;
import com.constellio.app.services.appManagement.AppManagementService;
import com.constellio.app.services.appManagement.AppManagementServiceException;
import com.constellio.app.services.collections.CollectionsManager;
import com.constellio.app.services.extensions.ConstellioModulesManagerImpl;
import com.constellio.app.services.extensions.plugins.ConstellioPluginConfigurationManager;
import com.constellio.app.services.extensions.plugins.ConstellioPluginManager;
import com.constellio.app.services.extensions.plugins.JSPFConstellioPluginManager;
import com.constellio.app.services.metadata.AppSchemasServices;
import com.constellio.app.services.migrations.ConstellioEIM;
import com.constellio.app.services.migrations.MigrationServices;
import com.constellio.app.services.records.SystemCheckManager;
import com.constellio.app.services.recovery.UpgradeAppRecoveryService;
import com.constellio.app.services.recovery.UpgradeAppRecoveryServiceImpl;
import com.constellio.app.services.schemasDisplay.SchemasDisplayManager;
import com.constellio.app.services.systemSetup.SystemGlobalConfigsManager;
import com.constellio.app.ui.application.NavigatorConfigurationService;
import com.constellio.app.ui.framework.containers.ContainerButtonListener;
import com.constellio.app.ui.i18n.i18n;
import com.constellio.app.ui.pages.base.EnterViewListener;
import com.constellio.app.ui.pages.base.InitUIListener;
import com.constellio.app.ui.pages.base.PresenterService;
import com.constellio.data.dao.managers.StatefulService;
import com.constellio.data.dao.managers.StatefullServiceDecorator;
import com.constellio.data.dao.managers.config.ConfigManagerException.OptimisticLockingConfiguration;
import com.constellio.data.dao.services.factories.DataLayerFactory;
import com.constellio.data.dao.services.factories.LayerFactoryImpl;
import com.constellio.data.io.IOServicesFactory;
import com.constellio.data.io.services.facades.IOServices;
import com.constellio.data.utils.Delayed;
import com.constellio.data.utils.ImpossibleRuntimeException;
import com.constellio.data.utils.dev.Toggle;
import com.constellio.model.conf.FoldersLocator;
import com.constellio.model.entities.records.RecordMigrationScript;
import com.constellio.model.entities.schemas.MetadataSchemaType;
import com.constellio.model.services.configs.SystemConfigurationsManager;
import com.constellio.model.services.extensions.ConstellioModulesManager;
import com.constellio.model.services.factories.ModelLayerFactory;
import com.constellio.model.services.migrations.ConstellioEIMConfigs;
import com.constellio.model.services.records.reindexing.ReindexationMode;
import com.constellio.model.services.records.reindexing.ReindexingServices;
import com.constellio.model.services.schemas.MetadataSchemasManager;

public class AppLayerFactoryImpl extends LayerFactoryImpl implements AppLayerFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppLayerFactoryImpl.class);

	private FoldersLocator foldersLocator;

	private ConstellioPluginManager pluginManager;

	private ConstellioModulesManagerImpl modulesManager;

	private ModelLayerFactory modelLayerFactory;

	private DataLayerFactory dataLayerFactory;

	private CmisCacheManager cmisRepositoriesManager;

	private List<EnterViewListener> enterViewListeners;

	private List<InitUIListener> initUIListeners;

	private SystemGlobalConfigsManager systemGlobalConfigsManager;

	private List<ContainerButtonListener> containerButtonListeners;
	private SchemasDisplayManager metadataSchemasDisplayManager;

	private final AppLayerExtensions appLayerExtensions;

	private final CollectionsManager collectionsManager;

	private final LabelTemplateManager labelTemplateManager;

	private final AppLayerConfiguration appLayerConfiguration;

	private final Map<String, StatefulService> moduleManagers = new HashMap<>();
	final private NavigatorConfigurationService navigatorConfigService;

	private final SystemCheckManager systemCheckManager;

	public AppLayerFactoryImpl(AppLayerConfiguration appLayerConfiguration, ModelLayerFactory modelLayerFactory,
			DataLayerFactory dataLayerFactory, StatefullServiceDecorator statefullServiceDecorator, String instanceName) {
		super(modelLayerFactory, statefullServiceDecorator, instanceName);

		this.appLayerExtensions = new AppLayerExtensions();
		this.modelLayerFactory = modelLayerFactory;
		this.dataLayerFactory = dataLayerFactory;
		this.enterViewListeners = new ArrayList<>();
		this.initUIListeners = new ArrayList<>();
		this.containerButtonListeners = new ArrayList<>();
		this.foldersLocator = new FoldersLocator();
		this.appLayerConfiguration = appLayerConfiguration;
		this.setDefaultLocale();
		this.metadataSchemasDisplayManager = add(new SchemasDisplayManager(dataLayerFactory.getConfigManager(),
				modelLayerFactory.getCollectionsListManager(), modelLayerFactory.getMetadataSchemasManager(),
				dataLayerFactory.getSettingsCacheManager()));

		IOServices ioServices = modelLayerFactory.getIOServicesFactory().newIOServices();
		pluginManager = add(new JSPFConstellioPluginManager(appLayerConfiguration.getPluginsFolder(),
				appLayerConfiguration.getPluginsManagementOnStartupFile(), ioServices,
				new ConstellioPluginConfigurationManager(dataLayerFactory.getConfigManager())));
		pluginManager.registerModule(new ConstellioRMModule());

		pluginManager.registerModule(new ConstellioESModule());
		pluginManager.registerModule(new TaskModule());
		pluginManager.registerModule(new ConstellioRobotsModule());
		pluginManager.registerModule(new ESRMRobotsModule());

		Delayed<MigrationServices> migrationServicesDelayed = new Delayed<>();
		this.modulesManager = add(new ConstellioModulesManagerImpl(this, pluginManager, migrationServicesDelayed));

		this.systemGlobalConfigsManager = add(
				new SystemGlobalConfigsManager(modelLayerFactory.getDataLayerFactory().getConfigManager()));
		this.collectionsManager = add(
				new CollectionsManager(modelLayerFactory, modulesManager, migrationServicesDelayed, systemGlobalConfigsManager));
		migrationServicesDelayed.set(newMigrationServices());
		try {
			newMigrationServices().migrate(null, false);
		} catch (OptimisticLockingConfiguration optimisticLockingConfiguration) {
			throw new RuntimeException(optimisticLockingConfiguration);
		}
		labelTemplateManager = new LabelTemplateManager(dataLayerFactory.getConfigManager(), this);
		this.navigatorConfigService = new NavigatorConfigurationService();
		this.systemCheckManager = add(new SystemCheckManager(this));

	}

	private void setDefaultLocale() {
		Locale locale;
		String mainDataLanguage = modelLayerFactory.getConfiguration().getMainDataLanguage();
		if ("en".equals(mainDataLanguage)) {
			locale = Locale.ENGLISH;
		} else if ("fr".equals(mainDataLanguage)) {
			locale = Locale.FRENCH;
		} else {
			throw new ImpossibleRuntimeException("Invalid language " + mainDataLanguage);
		}
		i18n.setLocale(locale);

	}

	public void registerSystemWideManager(String module, String id, StatefulService manager) {
		String key = module + "-" + id;
		add(manager);
		moduleManagers.put(key, manager);
	}

	public void registerManager(String collection, String module, String id, StatefulService manager) {
		String key = collection + "-" + module + "-" + id;
		add(manager);
		moduleManagers.put(key, manager);
	}

	public <T> T getRegisteredManager(String collection, String module, String id) {
		String key = collection + "-" + module + "-" + id;
		return (T) moduleManagers.get(key);
	}

	public <T> T getSystemWideRegisteredManager(String module, String id) {
		String key = module + "-" + id;
		return (T) moduleManagers.get(key);
	}

	public AppLayerExtensions getExtensions() {
		return appLayerExtensions;
	}

	public AppManagementService newApplicationService() {
		IOServicesFactory ioServicesFactory = dataLayerFactory.getIOServicesFactory();
		return new AppManagementService(this, foldersLocator);
	}

	public UpgradeAppRecoveryService newUpgradeAppRecoveryService() {
		return new UpgradeAppRecoveryServiceImpl(this,
				dataLayerFactory.getIOServicesFactory().newIOServices());
	}

	public SystemCheckManager getSystemCheckManager() {
		return systemCheckManager;
	}

	@Override
	public void initialize() {
		UpgradeAppRecoveryServiceImpl upgradeAppRecoveryService = new UpgradeAppRecoveryServiceImpl(this,
				dataLayerFactory.getIOServicesFactory().newIOServices());
		upgradeAppRecoveryService.deletePreviousWarCausingFailure();
		SystemConfigurationsManager configManager = modelLayerFactory
				.getSystemConfigurationsManager();
		configManager.initialize();
		ConstellioEIMConfigs constellioConfigs = new ConstellioEIMConfigs(configManager);
		boolean recoveryModeActive = constellioConfigs.isInUpdateProcess();
		if (Toggle.FORCE_ROLLBACK.isEnabled() || recoveryModeActive) {
			startupWithPossibleRecovery(upgradeAppRecoveryService);
		} else {
			normalStartup();
		}
		if (dataLayerFactory.getDataLayerConfiguration().isBackgroundThreadsEnabled()) {
			dataLayerFactory.getBackgroundThreadsManager().onSystemStarted();
		}
		upgradeAppRecoveryService.close();
	}

	private void startupWithPossibleRecovery(UpgradeAppRecoveryServiceImpl recoveryService) {
		if (dataLayerFactory.getSecondTransactionLogManager() != null) {
			recoveryService.startRollbackMode();
			try {
				normalStartup();
				recoveryService.stopRollbackMode();
			} catch (Throwable exception) {
				if (recoveryService.isInRollbackMode()) {
					LOGGER.error("Error when trying to start application", exception);
					recoveryService.rollback(exception);
					//this.appLayerFactory.getModelLayerFactory().getDataLayerFactory().close(false);
					try {
						newApplicationService().restart();
					} catch (AppManagementServiceException e) {
						throw new RuntimeException(e);
					}
				} else {
					LOGGER.info("No more in rollback mode...");
					throw exception;
				}
			}
		} else {
			//rare case in tests
			normalStartup();
		}

	}

	private void normalStartup() {
		appLayerExtensions.getSystemWideExtensions().pagesComponentsExtensions.add(new DefaultPagesComponentsExtension(this));
		this.pluginManager.detectPlugins();

		Set<String> invalidPlugins = new HashSet<>();
		super.initialize();

		String warVersion = newApplicationService().getWarVersion();
		if (warVersion != null && !"5.0.0".equals(warVersion)) {
			LOGGER.info("----------- STARTING APPLICATION IN VERSION " + warVersion + " -----------");
		}

		try {
			collectionsManager.initializeModulesResources();
			invalidPlugins.addAll(newMigrationServices().migrate(null, false));
		} catch (OptimisticLockingConfiguration optimisticLockingConfiguration) {
			throw new RuntimeException(optimisticLockingConfiguration);
		}

		LOGGER.info("initializeCollectionsAndGetInvalidModules");

		invalidPlugins.addAll(collectionsManager.initializeCollectionsAndGetInvalidModules());
		getModulesManager().enableComplementaryModules();

		if (!invalidPlugins.isEmpty()) {
			LOGGER.warn("System is restarting because of invalid modules \n\t" + StringUtils.join(invalidPlugins, "\n\t"));
			try {
				restart();
			} catch (AppManagementServiceException e) {
				throw new RuntimeException(e);
			}
		}

		Map<String, Set<String>> typesWithNewScriptsInCollections = new HashMap<>();
		for (String collection : getModelLayerFactory().getCollectionsListManager().getCollectionsExcludingSystem()) {
			List<RecordMigrationScript> scripts = newMigrationServices().getAllRecordMigrationScripts(collection);
			//TODO Check if master node
			Set<String> typesWithNewScripts = getModelLayerFactory().getRecordMigrationsManager()
					.registerReturningTypesWithNewScripts(collection, scripts, true);
			typesWithNewScriptsInCollections.put(collection, typesWithNewScripts);
		}

		modelLayerFactory.getBatchProcessesController().start();

		ReindexingServices reindexingServices = modelLayerFactory.newReindexingServices();
		MetadataSchemasManager schemasManager = modelLayerFactory.getMetadataSchemasManager();
		for (Map.Entry<String, Set<String>> entry : typesWithNewScriptsInCollections.entrySet()) {
			List<MetadataSchemaType> types = schemasManager.getSchemaTypes(entry.getKey(), new ArrayList<>(entry.getValue()));
			reindexingServices.reindexCollections(recalculateAndRewriteSchemaTypesInBackground(types));
		}

	}

	public void postInitialization() {
		pluginManager.configure();
		if (systemGlobalConfigsManager.isMarkedForReindexing()) {
			try {
				modelLayerFactory.newReindexingServices().reindexCollections(ReindexationMode.RECALCULATE_AND_REWRITE);
				systemGlobalConfigsManager.setMarkedForReindexing(false);
				systemGlobalConfigsManager.setReindexingRequired(false);
				systemGlobalConfigsManager.setLastReindexingFailed(false);
			} catch (Exception e) {
				LOGGER.error("Reindexing failed", e);
				systemGlobalConfigsManager.setMarkedForReindexing(false);
				systemGlobalConfigsManager.setReindexingRequired(true);
				systemGlobalConfigsManager.setLastReindexingFailed(true);
			}
		}
		systemGlobalConfigsManager.setRestartRequired(false);
	}

	public void restart()
			throws AppManagementServiceException {
		newApplicationService().restart();
	}

	@Override
	public void close() {
		Scripts.removeScripts();
		super.close();
	}

	public ConstellioPluginManager getPluginManager() {
		return pluginManager;
	}

	public ModelLayerFactory getModelLayerFactory() {
		return modelLayerFactory;
	}

	public NavigatorConfigurationService getNavigatorConfigurationService() {
		return navigatorConfigService;
	}

	public PresenterService newPresenterService() {
		return new PresenterService(modelLayerFactory);
	}

	public CmisCacheManager getConstellioCmisRepositoriesManager() {
		if (cmisRepositoriesManager == null) {
			cmisRepositoriesManager = add(new CmisCacheManager(this));
			cmisRepositoriesManager.initialize();
		}
		return cmisRepositoriesManager;
	}

	public List<EnterViewListener> getEnterViewListeners() {
		return enterViewListeners;
	}

	public List<InitUIListener> getInitUIListeners() {
		return initUIListeners;
	}

	public List<ContainerButtonListener> getContainerButtonListeners() {
		return containerButtonListeners;
	}

	public SchemasDisplayManager getMetadataSchemasDisplayManager() {
		return metadataSchemasDisplayManager;
	}

	public SystemGlobalConfigsManager getSystemGlobalConfigsManager() {
		return this.systemGlobalConfigsManager;
	}

	public CollectionsManager getCollectionsManager() {
		return collectionsManager;
	}

	public MigrationServices newMigrationServices() {
		return new MigrationServices(new ConstellioEIM(), this, modulesManager, pluginManager);
	}

	public ConstellioModulesManager getModulesManager() {
		return modulesManager;
	}

	public LabelTemplateManager getLabelTemplateManager() {
		return labelTemplateManager;
	}

	public AppLayerConfiguration getAppLayerConfiguration() {
		return appLayerConfiguration;
	}

	public AppSchemasServices newSchemasServices() {
		return new AppSchemasServices(this);
	}

}
