package com.constellio.app.modules.rm.migrations;

import java.io.File;

import com.constellio.app.entities.modules.MigrationResourcesProvider;
import com.constellio.app.entities.modules.MigrationScript;
import com.constellio.app.services.appManagement.AppManagementService;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.data.io.services.zip.ZipService;
import com.constellio.data.io.services.zip.ZipServiceException;
import com.constellio.model.conf.FoldersLocator;

public class RMMigrationTo7_3_java implements MigrationScript {

    @Override
    public String getVersion() {
        return "7.3";
    }

    @Override
    public void migrate(String collection, MigrationResourcesProvider migrationResourcesProvider,
            AppLayerFactory appLayerFactory) {

        AppManagementService updateService = appLayerFactory.newApplicationService();
        FoldersLocator foldersLocator = new FoldersLocator();
        File jdkZip = new File(foldersLocator.getConstellioWebappFolder().getAbsoluteFile() + File.separator + "jdk-1.8.zip");
        File jdkFolder = new File(foldersLocator.getWrapperInstallationFolder().getAbsolutePath() + File.separator + "jdk");
        ZipService zipService = appLayerFactory.getModelLayerFactory().getIOServicesFactory().newZipService();
        File jdkBin = new File(foldersLocator.getWrapperInstallationFolder().getAbsolutePath() + File.separator + "jdk" + File.separator + "jdk1.8" + File.separator + "bin");

        if (!jdkFolder.exists()) {
            jdkFolder.mkdir();
        }

        try {
            zipService.unzip(jdkZip, jdkFolder);
        } catch (ZipServiceException ze) {
            ze.printStackTrace();
        }

        updateService.updateJDKWrapperConf(jdkBin);

    }
}
