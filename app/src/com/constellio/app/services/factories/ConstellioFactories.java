package com.constellio.app.services.factories;

import java.io.File;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constellio.app.conf.AppLayerConfiguration;
import com.constellio.app.conf.PropertiesAppLayerConfiguration;
import com.constellio.data.conf.DataLayerConfiguration;
import com.constellio.data.conf.PropertiesDataLayerConfiguration;
import com.constellio.data.dao.services.factories.DataLayerFactory;
import com.constellio.data.io.IOServicesFactory;
import com.constellio.data.utils.Delayed;
import com.constellio.data.utils.Factory;
import com.constellio.data.utils.PropertyFileUtils;
import com.constellio.data.utils.dev.Toggle;
import com.constellio.model.conf.FoldersLocator;
import com.constellio.model.conf.ModelLayerConfiguration;
import com.constellio.model.conf.PropertiesModelLayerConfiguration;
import com.constellio.model.services.extensions.ConstellioModulesManager;
import com.constellio.model.services.factories.ModelLayerFactory;
import com.constellio.model.services.factories.ModelLayerFactoryImpl;

public class ConstellioFactories {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConstellioFactories.class);

	public static ConstellioFactoriesInstanceProvider instanceProvider = new SingletonConstellioFactoriesInstanceProvider();

	private File propertyFile;
	private ConstellioFactoriesDecorator decorator;
	private DataLayerConfiguration dataLayerConfiguration;
	private ModelLayerConfiguration modelLayerConfiguration;
	private AppLayerConfiguration appLayerConfiguration;

	private FoldersLocator foldersLocator;

	private IOServicesFactory ioServicesFactory;

	private DataLayerFactory dataLayerFactory;

	private ModelLayerFactory modelLayerFactory;

	private AppLayerFactory appLayerFactory;

	private ThreadLocal<AppLayerFactory> requestCachedFactories = new ThreadLocal<>();

	private ConstellioFactories() {

	}

	public static ConstellioFactories getInstanceIfAlreadyStarted() {
		return instanceProvider.getInstance(null);
	}

	public static boolean isInitialized() {
		return instanceProvider.isInitialized();
	}

	public static ConstellioFactories getInstance() {
		File propertyFile = new FoldersLocator().getConstellioProperties();
		ConstellioFactoriesDecorator constellioFactoriesDecorator = new ConstellioFactoriesDecorator();
		return getInstance(propertyFile, constellioFactoriesDecorator);
	}

	public static ConstellioFactories getInstance(ConstellioFactoriesDecorator constellioFactoriesDecorator) {
		File propertyFile = new FoldersLocator().getConstellioProperties();
		return getInstance(propertyFile, constellioFactoriesDecorator);
	}

	public static ConstellioFactories getInstance(final File propertyFile,
			final ConstellioFactoriesDecorator decorator) {
		return instanceProvider.getInstance(new Factory<ConstellioFactories>() {
			@Override
			public ConstellioFactories get() {
				ConstellioFactories instance = buildFor(propertyFile, decorator, null);
				return instance;
			}
		});
	}

	public static void start() {
		//getInstance().getAppLayerFactory().initialize();
		//instance.appLayerFactory.initialize();
	}

	public static void clear() {
		instanceProvider.clear();
		//		if (instance != null) {
		//			instance.appLayerFactory.close();
		//			instance = null;
		//		}
	}
	//
	//	public static void restart() {
	//		File propertyFile = getInstance().propertyFile;
	//		ConstellioFactoriesDecorator decorator = getInstance().decorator;
	//		clear();
	//		getInstance(propertyFile, decorator);
	//	}

	public static ConstellioFactories buildFor(File propertyFile, ConstellioFactoriesDecorator decorator, String instanceName) {
		ConstellioFactories factories = new ConstellioFactories();

		factories.propertyFile = propertyFile;
		Map<String, String> configs = PropertyFileUtils.loadKeyValues(propertyFile);

		factories.decorator = decorator;
		factories.foldersLocator = decorator.decorateFoldersLocator(new FoldersLocator());
		factories.buildConfiguration(propertyFile, configs);
		factories.buildLayers(instanceName);
		return factories;
	}

	private void buildLayers(String instanceName) {
		Delayed<ConstellioModulesManager> modulesManager = new Delayed<>();
		ioServicesFactory = new IOServicesFactory(dataLayerConfiguration.getTempFolder());

		dataLayerFactory = decorator.decorateDataLayerFactory(new DataLayerFactory(ioServicesFactory, dataLayerConfiguration,
				decorator.getStatefullServiceDecorator(), instanceName));

		modelLayerFactory = decorator.decorateModelServicesFactory(new ModelLayerFactoryImpl(dataLayerFactory, foldersLocator,
				modelLayerConfiguration, decorator.getStatefullServiceDecorator(), modulesManager, instanceName,
				new ModelLayerFactoryFactory()));

		appLayerFactory = decorator.decorateAppServicesFactory(new AppLayerFactoryImpl(appLayerConfiguration, modelLayerFactory,
				dataLayerFactory, decorator.getStatefullServiceDecorator(), instanceName));

		modulesManager.set(appLayerFactory.getModulesManager());

	}

	private void buildConfiguration(File propertyFile, Map<String, String> configs) {
		File defaultTempFolder = foldersLocator.getDefaultTempFolder();
		File defaultFileSystemBaseFolder = new File(foldersLocator.getConfFolder(), "settings");

		this.dataLayerConfiguration = decorator
				.decorateDataLayerConfiguration(new PropertiesDataLayerConfiguration(configs, defaultTempFolder,
						defaultFileSystemBaseFolder, propertyFile));
		this.modelLayerConfiguration = decorator
				.decorateModelLayerConfiguration(
						new PropertiesModelLayerConfiguration(configs, dataLayerConfiguration, foldersLocator, propertyFile));
		this.appLayerConfiguration = decorator
				.decorateAppLayerConfiguration(
						new PropertiesAppLayerConfiguration(configs, modelLayerConfiguration, foldersLocator, propertyFile));
	}

	private static AtomicInteger factoryIdSeq = new AtomicInteger();

	public void onRequestStarted() {

		long factoryId = factoryIdSeq.incrementAndGet();

		AppLayerFactoryImpl appLayerFactory = (AppLayerFactoryImpl) getAppLayerFactory();
		AppLayerFactoryWithRequestCacheImpl requestCachedAppLayerFactory = new AppLayerFactoryWithRequestCacheImpl(
				appLayerFactory, "" + factoryId);

		requestCachedFactories.set(requestCachedAppLayerFactory);
		if (Toggle.LOG_REQUEST_CACHE.isEnabled()) {
			LOGGER.info("onRequestStarted() - " + requestCachedAppLayerFactory.toString());
		}
	}

	public void onRequestEnded() {

		AppLayerFactory appLayerFactory = requestCachedFactories.get();
		if (appLayerFactory != null && appLayerFactory instanceof AppLayerFactoryWithRequestCacheImpl) {
			((AppLayerFactoryWithRequestCacheImpl) appLayerFactory).disconnect();
			if (Toggle.LOG_REQUEST_CACHE.isEnabled()) {
				LOGGER.info("onRequestEnded() - " + appLayerFactory.toString());
			}
		}
		requestCachedFactories.set(null);
	}

	public IOServicesFactory getIoServicesFactory() {
		return ioServicesFactory;
	}

	public DataLayerFactory getDataLayerFactory() {
		return getModelLayerFactory().getDataLayerFactory();
	}

	public ModelLayerFactory getModelLayerFactory() {
		return getAppLayerFactory().getModelLayerFactory();
	}

	public DataLayerConfiguration getDataLayerConfiguration() {
		return dataLayerConfiguration;
	}

	public ModelLayerConfiguration getModelLayerConfiguration() {
		return modelLayerConfiguration;
	}

	public AppLayerConfiguration getAppLayerConfiguration() {
		return appLayerConfiguration;
	}

	public FoldersLocator getFoldersLocator() {
		return foldersLocator;
	}

	public AppLayerFactory getAppLayerFactory() {
		AppLayerFactory requestCachedAppLayerFactory = requestCachedFactories.get();
		return requestCachedAppLayerFactory == null ? appLayerFactory : requestCachedAppLayerFactory;
	}
}
