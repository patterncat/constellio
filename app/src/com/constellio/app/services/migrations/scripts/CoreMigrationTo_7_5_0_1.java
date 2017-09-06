package com.constellio.app.services.migrations.scripts;

import com.constellio.app.entities.modules.MetadataSchemasAlterationHelper;
import com.constellio.app.entities.modules.MigrationResourcesProvider;
import com.constellio.app.entities.modules.MigrationScript;
import com.constellio.app.modules.rm.wrappers.Printable;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.services.schemas.builders.MetadataBuilder;
import com.constellio.model.services.schemas.builders.MetadataSchemaTypesBuilder;
import com.constellio.model.services.schemas.validators.JasperFilePrintableValidator;

public class CoreMigrationTo_7_5_0_1 implements MigrationScript {
    @Override
    public String getVersion() {
        return "7.5.0.1";
    }

    @Override
    public void migrate(String collection, MigrationResourcesProvider migrationResourcesProvider, AppLayerFactory appLayerFactory) throws Exception {
        new SchemaAlterationFor7_5_0_1(collection, migrationResourcesProvider, appLayerFactory).migrate();
    }

    class SchemaAlterationFor7_5_0_1 extends MetadataSchemasAlterationHelper {

        protected SchemaAlterationFor7_5_0_1(String collection, MigrationResourcesProvider migrationResourcesProvider,
                                         AppLayerFactory appLayerFactory) {
            super(collection, migrationResourcesProvider, appLayerFactory);
        }

        @Override
        protected void migrate(MetadataSchemaTypesBuilder typesBuilder) {
            MetadataBuilder jasperMetadata = typesBuilder.getSchemaType(Printable.SCHEMA_TYPE).getDefaultSchema().getMetadata(Printable.JASPERFILE);
            if(!jasperMetadata.getOriginalMetadata().getValidators().contains(JasperFilePrintableValidator.class)) {
                jasperMetadata.addValidator(JasperFilePrintableValidator.class);
            }
        }
    }
}
