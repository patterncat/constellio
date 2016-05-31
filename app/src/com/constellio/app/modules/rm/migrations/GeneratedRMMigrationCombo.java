package com.constellio.app.modules.rm.migrations;

import static java.util.Arrays.asList;

import com.constellio.app.entities.modules.MigrationResourcesProvider;
import com.constellio.app.entities.schemasDisplay.SchemaTypesDisplayConfig;
import com.constellio.app.entities.schemasDisplay.enums.MetadataInputType;
import com.constellio.app.modules.rm.model.CopyRetentionRuleFactory;
import com.constellio.app.modules.rm.model.CopyRetentionRuleInRuleFactory;
import com.constellio.app.modules.rm.model.calculators.AdministrativeUnitAncestorsCalculator;
import com.constellio.app.modules.rm.model.calculators.CategoryIsLinkableCalculator;
import com.constellio.app.modules.rm.model.calculators.ContainerRecordTreeVisibilityCalculator;
import com.constellio.app.modules.rm.model.calculators.ContainerTitleCalculator;
import com.constellio.app.modules.rm.model.calculators.FolderActiveRetentionTypeCalculator;
import com.constellio.app.modules.rm.model.calculators.FolderApplicableCopyRuleCalculator;
import com.constellio.app.modules.rm.model.calculators.FolderCalendarYearCalculator;
import com.constellio.app.modules.rm.model.calculators.FolderClosingDateCalculator;
import com.constellio.app.modules.rm.model.calculators.FolderCopyRulesExpectedDepositDatesCalculator;
import com.constellio.app.modules.rm.model.calculators.FolderCopyRulesExpectedDestructionDatesCalculator;
import com.constellio.app.modules.rm.model.calculators.FolderCopyRulesExpectedTransferDatesCalculator;
import com.constellio.app.modules.rm.model.calculators.FolderCopyStatusCalculator3;
import com.constellio.app.modules.rm.model.calculators.FolderDecommissioningDateCalculator;
import com.constellio.app.modules.rm.model.calculators.FolderExpectedDepositDateCalculator;
import com.constellio.app.modules.rm.model.calculators.FolderExpectedDestructionDateCalculator;
import com.constellio.app.modules.rm.model.calculators.FolderExpectedTransferDateCalculator;
import com.constellio.app.modules.rm.model.calculators.FolderInactiveDisposalTypeCalculator;
import com.constellio.app.modules.rm.model.calculators.FolderSemiActiveRetentionTypeCalculator;
import com.constellio.app.modules.rm.model.calculators.FolderStatusCalculator;
import com.constellio.app.modules.rm.model.calculators.FolderTreeVisibilityCalculator;
import com.constellio.app.modules.rm.model.calculators.category.CategoryCopyRetentionRulesOnDocumentTypesCalculator;
import com.constellio.app.modules.rm.model.calculators.category.CategoryLevelCalculator;
import com.constellio.app.modules.rm.model.calculators.decommissioningList.DecomListContainersCalculator;
import com.constellio.app.modules.rm.model.calculators.decommissioningList.DecomListFoldersCalculator;
import com.constellio.app.modules.rm.model.calculators.decommissioningList.DecomListHasAnalogicalMediumTypesCalculator;
import com.constellio.app.modules.rm.model.calculators.decommissioningList.DecomListHasElectronicMediumTypesCalculator;
import com.constellio.app.modules.rm.model.calculators.decommissioningList.DecomListIsUniform;
import com.constellio.app.modules.rm.model.calculators.decommissioningList.DecomListStatusCalculator2;
import com.constellio.app.modules.rm.model.calculators.decommissioningList.DecomListUniformCategoryCalculator2;
import com.constellio.app.modules.rm.model.calculators.decommissioningList.DecomListUniformCopyRuleCalculator2;
import com.constellio.app.modules.rm.model.calculators.decommissioningList.DecomListUniformCopyTypeCalculator2;
import com.constellio.app.modules.rm.model.calculators.decommissioningList.DecomListUniformRuleCalculator2;
import com.constellio.app.modules.rm.model.calculators.decommissioningList.PendingValidationCalculator;
import com.constellio.app.modules.rm.model.calculators.document.DocumentActualDepositDateCalculator;
import com.constellio.app.modules.rm.model.calculators.document.DocumentActualDestructionDateCalculator;
import com.constellio.app.modules.rm.model.calculators.document.DocumentActualTransferDateCalculator;
import com.constellio.app.modules.rm.model.calculators.document.DocumentApplicableCopyRulesCalculator;
import com.constellio.app.modules.rm.model.calculators.document.DocumentArchivisticStatusCalculator;
import com.constellio.app.modules.rm.model.calculators.document.DocumentCalendarYearCalculator;
import com.constellio.app.modules.rm.model.calculators.document.DocumentExpectedDepositDateCalculator;
import com.constellio.app.modules.rm.model.calculators.document.DocumentExpectedDestructionDateCalculator;
import com.constellio.app.modules.rm.model.calculators.document.DocumentExpectedTransferDateCalculator;
import com.constellio.app.modules.rm.model.calculators.document.DocumentIsSameInactiveFateAsFolderCalculator;
import com.constellio.app.modules.rm.model.calculators.document.DocumentIsSameSemiActiveFateAsFolderCalculator;
import com.constellio.app.modules.rm.model.calculators.document.DocumentMainCopyRuleCalculator2;
import com.constellio.app.modules.rm.model.calculators.document.DocumentRetentionRuleCalculator;
import com.constellio.app.modules.rm.model.calculators.document.DocumentVersionCalculator;
import com.constellio.app.modules.rm.model.calculators.folder.FolderApplicableCategoryCalculator;
import com.constellio.app.modules.rm.model.calculators.folder.FolderAppliedAdministrativeUnitCalculator;
import com.constellio.app.modules.rm.model.calculators.folder.FolderAppliedFilingSpaceCalculator;
import com.constellio.app.modules.rm.model.calculators.folder.FolderAppliedRetentionRuleCalculator;
import com.constellio.app.modules.rm.model.calculators.folder.FolderAppliedUniformSubdivisionCalculator;
import com.constellio.app.modules.rm.model.calculators.folder.FolderMainCopyRuleCalculator2;
import com.constellio.app.modules.rm.model.calculators.folder.FolderMediaTypesCalculator;
import com.constellio.app.modules.rm.model.calculators.folder.FolderRetentionPeriodCodeCalculator;
import com.constellio.app.modules.rm.model.calculators.rule.RuleDocumentTypesCalculator2;
import com.constellio.app.modules.rm.model.calculators.rule.RuleFolderTypesCalculator;
import com.constellio.app.modules.rm.model.enums.CopyType;
import com.constellio.app.modules.rm.model.enums.DecomListStatus;
import com.constellio.app.modules.rm.model.enums.DecommissioningListType;
import com.constellio.app.modules.rm.model.enums.DecommissioningMonth;
import com.constellio.app.modules.rm.model.enums.DecommissioningType;
import com.constellio.app.modules.rm.model.enums.DisposalType;
import com.constellio.app.modules.rm.model.enums.FolderMediaType;
import com.constellio.app.modules.rm.model.enums.FolderStatus;
import com.constellio.app.modules.rm.model.enums.OriginStatus;
import com.constellio.app.modules.rm.model.enums.RetentionRuleScope;
import com.constellio.app.modules.rm.model.enums.RetentionType;
import com.constellio.app.modules.rm.model.validators.FolderValidator;
import com.constellio.app.modules.rm.model.validators.RetentionRuleValidator;
import com.constellio.app.modules.rm.services.borrowingServices.BorrowingType;
import com.constellio.app.modules.rm.wrappers.structures.CommentFactory;
import com.constellio.app.modules.rm.wrappers.structures.DecomListContainerDetailFactory;
import com.constellio.app.modules.rm.wrappers.structures.DecomListFolderDetailFactory;
import com.constellio.app.modules.rm.wrappers.structures.DecomListValidationFactory;
import com.constellio.app.modules.rm.wrappers.structures.RetentionRuleDocumentTypeFactory;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.app.services.schemasDisplay.SchemaTypesDisplayTransactionBuilder;
import com.constellio.app.services.schemasDisplay.SchemasDisplayManager;
import com.constellio.model.entities.schemas.MetadataValueType;
import com.constellio.model.entities.security.Role;
import com.constellio.model.services.contents.ContentFactory;
import com.constellio.model.services.schemas.builders.MetadataBuilder;
import com.constellio.model.services.schemas.builders.MetadataSchemaBuilder;
import com.constellio.model.services.schemas.builders.MetadataSchemaTypeBuilder;
import com.constellio.model.services.schemas.builders.MetadataSchemaTypesBuilder;
import com.constellio.model.services.schemas.calculators.AllAuthorizationsCalculator;
import com.constellio.model.services.schemas.calculators.InheritedAuthorizationsCalculator;
import com.constellio.model.services.schemas.calculators.ParentPathCalculator;
import com.constellio.model.services.schemas.calculators.PathCalculator;
import com.constellio.model.services.schemas.calculators.PathPartsCalculator;
import com.constellio.model.services.schemas.calculators.PrincipalPathCalculator;
import com.constellio.model.services.schemas.calculators.TokensCalculator2;
import com.constellio.model.services.schemas.validators.ManualTokenValidator;
import com.constellio.model.services.schemas.validators.metadatas.IntegerStringValidator;
import com.constellio.model.services.security.roles.RolesManager;

public final class GeneratedRMMigrationCombo {
	String collection;

	AppLayerFactory appLayerFactory;

	MigrationResourcesProvider resourcesProvider;

	GeneratedRMMigrationCombo(String collection, AppLayerFactory appLayerFactory, MigrationResourcesProvider resourcesProvider) {
		this.collection = collection;
		this.appLayerFactory = appLayerFactory;
		this.resourcesProvider = resourcesProvider;
	}

	public void applyGeneratedSchemaAlteration(MetadataSchemaTypesBuilder typesBuilder) {
		MetadataSchemaTypeBuilder collectionSchemaType = typesBuilder.getSchemaType("collection");
		MetadataSchemaBuilder collectionSchema = collectionSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder groupSchemaType = typesBuilder.getSchemaType("group");
		MetadataSchemaBuilder groupSchema = groupSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder userSchemaType = typesBuilder.getSchemaType("user");
		MetadataSchemaBuilder userSchema = userSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder ddvTaskStatusSchemaType = typesBuilder.getSchemaType("ddvTaskStatus");
		MetadataSchemaBuilder ddvTaskStatusSchema = ddvTaskStatusSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder ddvTaskTypeSchemaType = typesBuilder.getSchemaType("ddvTaskType");
		MetadataSchemaBuilder ddvTaskTypeSchema = ddvTaskTypeSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder emailToSendSchemaType = typesBuilder.getSchemaType("emailToSend");
		MetadataSchemaBuilder emailToSendSchema = emailToSendSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder eventSchemaType = typesBuilder.getSchemaType("event");
		MetadataSchemaBuilder eventSchema = eventSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder facetSchemaType = typesBuilder.getSchemaType("facet");
		MetadataSchemaBuilder facet_fieldSchema = facetSchemaType.getCustomSchema("field");
		MetadataSchemaBuilder facet_querySchema = facetSchemaType.getCustomSchema("query");
		MetadataSchemaBuilder facetSchema = facetSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder reportSchemaType = typesBuilder.getSchemaType("report");
		MetadataSchemaBuilder reportSchema = reportSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder savedSearchSchemaType = typesBuilder.getSchemaType("savedSearch");
		MetadataSchemaBuilder savedSearchSchema = savedSearchSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder taskSchemaType = typesBuilder.getSchemaType("task");
		MetadataSchemaBuilder task_approvalSchema = taskSchemaType.getCustomSchema("approval");
		MetadataSchemaBuilder taskSchema = taskSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder userDocumentSchemaType = typesBuilder.getSchemaType("userDocument");
		MetadataSchemaBuilder userDocumentSchema = userDocumentSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder userTaskSchemaType = typesBuilder.getSchemaType("userTask");
		MetadataSchemaBuilder userTaskSchema = userTaskSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder workflowSchemaType = typesBuilder.getSchemaType("workflow");
		MetadataSchemaBuilder workflowSchema = workflowSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder workflowInstanceSchemaType = typesBuilder.getSchemaType("workflowInstance");
		MetadataSchemaBuilder workflowInstanceSchema = workflowInstanceSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder administrativeUnitSchemaType = typesBuilder.createNewSchemaType("administrativeUnit");
		MetadataSchemaBuilder administrativeUnitSchema = administrativeUnitSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder cartSchemaType = typesBuilder.createNewSchemaType("cart").setSecurity(false);
		MetadataSchemaBuilder cartSchema = cartSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder categorySchemaType = typesBuilder.createNewSchemaType("category").setSecurity(false);
		MetadataSchemaBuilder categorySchema = categorySchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder containerRecordSchemaType = typesBuilder.createNewSchemaType("containerRecord");
		MetadataSchemaBuilder containerRecordSchema = containerRecordSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder ddvContainerRecordTypeSchemaType = typesBuilder.createNewSchemaType("ddvContainerRecordType")
				.setSecurity(false);
		MetadataSchemaBuilder ddvContainerRecordTypeSchema = ddvContainerRecordTypeSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder ddvDocumentTypeSchemaType = typesBuilder.createNewSchemaType("ddvDocumentType")
				.setSecurity(false);
		MetadataSchemaBuilder ddvDocumentTypeSchema = ddvDocumentTypeSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder ddvFolderTypeSchemaType = typesBuilder.createNewSchemaType("ddvFolderType").setSecurity(false);
		MetadataSchemaBuilder ddvFolderTypeSchema = ddvFolderTypeSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder ddvMediumTypeSchemaType = typesBuilder.createNewSchemaType("ddvMediumType").setSecurity(false);
		MetadataSchemaBuilder ddvMediumTypeSchema = ddvMediumTypeSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder ddvStorageSpaceTypeSchemaType = typesBuilder.createNewSchemaType("ddvStorageSpaceType")
				.setSecurity(false);
		MetadataSchemaBuilder ddvStorageSpaceTypeSchema = ddvStorageSpaceTypeSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder ddvVariablePeriodSchemaType = typesBuilder.createNewSchemaType("ddvVariablePeriod")
				.setSecurity(false);
		MetadataSchemaBuilder ddvVariablePeriodSchema = ddvVariablePeriodSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder decommissioningListSchemaType = typesBuilder.createNewSchemaType("decommissioningList")
				.setSecurity(false);
		MetadataSchemaBuilder decommissioningListSchema = decommissioningListSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder documentSchemaType = typesBuilder.createNewSchemaType("document");
		MetadataSchemaBuilder document_emailSchema = documentSchemaType.createCustomSchema("email");
		MetadataSchemaBuilder documentSchema = documentSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder filingSpaceSchemaType = typesBuilder.createNewSchemaType("filingSpace").setSecurity(false);
		MetadataSchemaBuilder filingSpaceSchema = filingSpaceSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder folderSchemaType = typesBuilder.createNewSchemaType("folder");
		MetadataSchemaBuilder folderSchema = folderSchemaType.getDefaultSchema();
		folderSchema.defineValidators().add(FolderValidator.class);
		MetadataSchemaTypeBuilder retentionRuleSchemaType = typesBuilder.createNewSchemaType("retentionRule").setSecurity(false);
		MetadataSchemaBuilder retentionRuleSchema = retentionRuleSchemaType.getDefaultSchema();
		retentionRuleSchema.defineValidators().add(RetentionRuleValidator.class);
		MetadataSchemaTypeBuilder storageSpaceSchemaType = typesBuilder.createNewSchemaType("storageSpace").setSecurity(false);
		MetadataSchemaBuilder storageSpaceSchema = storageSpaceSchemaType.getDefaultSchema();
		MetadataSchemaTypeBuilder uniformSubdivisionSchemaType = typesBuilder.createNewSchemaType("uniformSubdivision")
				.setSecurity(false);
		MetadataSchemaBuilder uniformSubdivisionSchema = uniformSubdivisionSchemaType.getDefaultSchema();
		MetadataBuilder administrativeUnit_adress = administrativeUnitSchema.create("adress").setType(MetadataValueType.STRING);
		administrativeUnit_adress.setUndeletable(true);
		MetadataBuilder administrativeUnit_allauthorizations = administrativeUnitSchema.get("allauthorizations");
		administrativeUnit_allauthorizations.setMultivalue(true);
		administrativeUnit_allauthorizations.setSystemReserved(true);
		administrativeUnit_allauthorizations.setUndeletable(true);
		administrativeUnit_allauthorizations.setEssential(true);
		MetadataBuilder administrativeUnit_authorizations = administrativeUnitSchema.get("authorizations");
		administrativeUnit_authorizations.setMultivalue(true);
		administrativeUnit_authorizations.setSystemReserved(true);
		administrativeUnit_authorizations.setUndeletable(true);
		administrativeUnit_authorizations.setEssential(true);
		MetadataBuilder administrativeUnit_code = administrativeUnitSchema.create("code").setType(MetadataValueType.STRING);
		administrativeUnit_code.setDefaultRequirement(true);
		administrativeUnit_code.setUndeletable(true);
		administrativeUnit_code.setEssential(true);
		administrativeUnit_code.setSchemaAutocomplete(true);
		administrativeUnit_code.setSearchable(true);
		administrativeUnit_code.setUniqueValue(true);
		MetadataBuilder administrativeUnit_createdBy = administrativeUnitSchema.get("createdBy");
		administrativeUnit_createdBy.setSystemReserved(true);
		administrativeUnit_createdBy.setUndeletable(true);
		administrativeUnit_createdBy.setEssential(true);
		MetadataBuilder administrativeUnit_createdOn = administrativeUnitSchema.get("createdOn");
		administrativeUnit_createdOn.setSystemReserved(true);
		administrativeUnit_createdOn.setUndeletable(true);
		administrativeUnit_createdOn.setEssential(true);
		administrativeUnit_createdOn.setSortable(true);
		MetadataBuilder administrativeUnit_decommissioningMonth = administrativeUnitSchema.create("decommissioningMonth")
				.setType(MetadataValueType.ENUM);
		administrativeUnit_decommissioningMonth.setUndeletable(true);
		administrativeUnit_decommissioningMonth.defineAsEnum(DecommissioningMonth.class);
		MetadataBuilder administrativeUnit_deleted = administrativeUnitSchema.get("deleted");
		administrativeUnit_deleted.setSystemReserved(true);
		administrativeUnit_deleted.setUndeletable(true);
		administrativeUnit_deleted.setEssential(true);
		MetadataBuilder administrativeUnit_denyTokens = administrativeUnitSchema.get("denyTokens");
		administrativeUnit_denyTokens.setMultivalue(true);
		administrativeUnit_denyTokens.setSystemReserved(true);
		administrativeUnit_denyTokens.setUndeletable(true);
		administrativeUnit_denyTokens.setEssential(true);
		administrativeUnit_denyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder administrativeUnit_description = administrativeUnitSchema.create("description")
				.setType(MetadataValueType.STRING);
		administrativeUnit_description.setUndeletable(true);
		administrativeUnit_description.setEssentialInSummary(true);
		administrativeUnit_description.setSearchable(true);
		MetadataBuilder administrativeUnit_detachedauthorizations = administrativeUnitSchema.get("detachedauthorizations");
		administrativeUnit_detachedauthorizations.setSystemReserved(true);
		administrativeUnit_detachedauthorizations.setUndeletable(true);
		administrativeUnit_detachedauthorizations.setEssential(true);
		MetadataBuilder administrativeUnit_filingSpaces = administrativeUnitSchema.create("filingSpaces")
				.setType(MetadataValueType.REFERENCE);
		administrativeUnit_filingSpaces.setMultivalue(true);
		administrativeUnit_filingSpaces.setUndeletable(true);
		administrativeUnit_filingSpaces.defineReferencesTo(filingSpaceSchemaType);
		MetadataBuilder administrativeUnit_filingSpacesAdmins = administrativeUnitSchema.create("filingSpacesAdmins")
				.setType(MetadataValueType.REFERENCE);
		administrativeUnit_filingSpacesAdmins.setMultivalue(true);
		administrativeUnit_filingSpacesAdmins.setUndeletable(true);
		administrativeUnit_filingSpacesAdmins.defineReferencesTo(userSchemaType);
		MetadataBuilder administrativeUnit_filingSpacesUsers = administrativeUnitSchema.create("filingSpacesUsers")
				.setType(MetadataValueType.REFERENCE);
		administrativeUnit_filingSpacesUsers.setMultivalue(true);
		administrativeUnit_filingSpacesUsers.setUndeletable(true);
		administrativeUnit_filingSpacesUsers.defineReferencesTo(userSchemaType);
		MetadataBuilder administrativeUnit_followers = administrativeUnitSchema.get("followers");
		administrativeUnit_followers.setMultivalue(true);
		administrativeUnit_followers.setSystemReserved(true);
		administrativeUnit_followers.setUndeletable(true);
		administrativeUnit_followers.setEssential(true);
		administrativeUnit_followers.setSearchable(true);
		MetadataBuilder administrativeUnit_id = administrativeUnitSchema.get("id");
		administrativeUnit_id.setDefaultRequirement(true);
		administrativeUnit_id.setSystemReserved(true);
		administrativeUnit_id.setUndeletable(true);
		administrativeUnit_id.setEssential(true);
		administrativeUnit_id.setSearchable(true);
		administrativeUnit_id.setSortable(true);
		administrativeUnit_id.setUniqueValue(true);
		administrativeUnit_id.setUnmodifiable(true);
		MetadataBuilder administrativeUnit_inheritedauthorizations = administrativeUnitSchema.get("inheritedauthorizations");
		administrativeUnit_inheritedauthorizations.setMultivalue(true);
		administrativeUnit_inheritedauthorizations.setSystemReserved(true);
		administrativeUnit_inheritedauthorizations.setUndeletable(true);
		administrativeUnit_inheritedauthorizations.setEssential(true);
		MetadataBuilder administrativeUnit_legacyIdentifier = administrativeUnitSchema.get("legacyIdentifier");
		administrativeUnit_legacyIdentifier.setDefaultRequirement(true);
		administrativeUnit_legacyIdentifier.setSystemReserved(true);
		administrativeUnit_legacyIdentifier.setUndeletable(true);
		administrativeUnit_legacyIdentifier.setEssential(true);
		administrativeUnit_legacyIdentifier.setSearchable(true);
		administrativeUnit_legacyIdentifier.setUniqueValue(true);
		administrativeUnit_legacyIdentifier.setUnmodifiable(true);
		MetadataBuilder administrativeUnit_manualTokens = administrativeUnitSchema.get("manualTokens");
		administrativeUnit_manualTokens.setMultivalue(true);
		administrativeUnit_manualTokens.setSystemReserved(true);
		administrativeUnit_manualTokens.setUndeletable(true);
		administrativeUnit_manualTokens.setEssential(true);
		administrativeUnit_manualTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder administrativeUnit_markedForPreviewConversion = administrativeUnitSchema
				.get("markedForPreviewConversion");
		administrativeUnit_markedForPreviewConversion.setSystemReserved(true);
		administrativeUnit_markedForPreviewConversion.setUndeletable(true);
		administrativeUnit_markedForPreviewConversion.setEssential(true);
		MetadataBuilder administrativeUnit_modifiedBy = administrativeUnitSchema.get("modifiedBy");
		administrativeUnit_modifiedBy.setSystemReserved(true);
		administrativeUnit_modifiedBy.setUndeletable(true);
		administrativeUnit_modifiedBy.setEssential(true);
		MetadataBuilder administrativeUnit_modifiedOn = administrativeUnitSchema.get("modifiedOn");
		administrativeUnit_modifiedOn.setSystemReserved(true);
		administrativeUnit_modifiedOn.setUndeletable(true);
		administrativeUnit_modifiedOn.setEssential(true);
		administrativeUnit_modifiedOn.setSortable(true);
		MetadataBuilder administrativeUnit_parent = administrativeUnitSchema.create("parent")
				.setType(MetadataValueType.REFERENCE);
		administrativeUnit_parent.setUndeletable(true);
		administrativeUnit_parent.setEssential(true);
		administrativeUnit_parent.defineChildOfRelationshipToType(administrativeUnitSchemaType);
		MetadataBuilder administrativeUnit_parentpath = administrativeUnitSchema.get("parentpath");
		administrativeUnit_parentpath.setMultivalue(true);
		administrativeUnit_parentpath.setSystemReserved(true);
		administrativeUnit_parentpath.setUndeletable(true);
		administrativeUnit_parentpath.setEssential(true);
		MetadataBuilder administrativeUnit_path = administrativeUnitSchema.get("path");
		administrativeUnit_path.setMultivalue(true);
		administrativeUnit_path.setSystemReserved(true);
		administrativeUnit_path.setUndeletable(true);
		administrativeUnit_path.setEssential(true);
		MetadataBuilder administrativeUnit_pathParts = administrativeUnitSchema.get("pathParts");
		administrativeUnit_pathParts.setMultivalue(true);
		administrativeUnit_pathParts.setSystemReserved(true);
		administrativeUnit_pathParts.setUndeletable(true);
		administrativeUnit_pathParts.setEssential(true);
		MetadataBuilder administrativeUnit_principalpath = administrativeUnitSchema.get("principalpath");
		administrativeUnit_principalpath.setSystemReserved(true);
		administrativeUnit_principalpath.setUndeletable(true);
		administrativeUnit_principalpath.setEssential(true);
		MetadataBuilder administrativeUnit_removedauthorizations = administrativeUnitSchema.get("removedauthorizations");
		administrativeUnit_removedauthorizations.setMultivalue(true);
		administrativeUnit_removedauthorizations.setSystemReserved(true);
		administrativeUnit_removedauthorizations.setUndeletable(true);
		administrativeUnit_removedauthorizations.setEssential(true);
		MetadataBuilder administrativeUnit_schema = administrativeUnitSchema.get("schema");
		administrativeUnit_schema.setDefaultRequirement(true);
		administrativeUnit_schema.setSystemReserved(true);
		administrativeUnit_schema.setUndeletable(true);
		administrativeUnit_schema.setEssential(true);
		MetadataBuilder administrativeUnit_searchable = administrativeUnitSchema.get("searchable");
		administrativeUnit_searchable.setSystemReserved(true);
		administrativeUnit_searchable.setUndeletable(true);
		administrativeUnit_searchable.setEssential(true);
		MetadataBuilder administrativeUnit_shareDenyTokens = administrativeUnitSchema.get("shareDenyTokens");
		administrativeUnit_shareDenyTokens.setMultivalue(true);
		administrativeUnit_shareDenyTokens.setSystemReserved(true);
		administrativeUnit_shareDenyTokens.setUndeletable(true);
		administrativeUnit_shareDenyTokens.setEssential(true);
		administrativeUnit_shareDenyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder administrativeUnit_shareTokens = administrativeUnitSchema.get("shareTokens");
		administrativeUnit_shareTokens.setMultivalue(true);
		administrativeUnit_shareTokens.setSystemReserved(true);
		administrativeUnit_shareTokens.setUndeletable(true);
		administrativeUnit_shareTokens.setEssential(true);
		administrativeUnit_shareTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder administrativeUnit_title = administrativeUnitSchema.get("title");
		administrativeUnit_title.setDefaultRequirement(true);
		administrativeUnit_title.setUndeletable(true);
		administrativeUnit_title.setEssential(true);
		administrativeUnit_title.setSchemaAutocomplete(true);
		administrativeUnit_title.setSearchable(true);
		MetadataBuilder administrativeUnit_tokens = administrativeUnitSchema.get("tokens");
		administrativeUnit_tokens.setMultivalue(true);
		administrativeUnit_tokens.setSystemReserved(true);
		administrativeUnit_tokens.setUndeletable(true);
		administrativeUnit_tokens.setEssential(true);
		MetadataBuilder administrativeUnit_unitAncestors = administrativeUnitSchema.create("unitAncestors")
				.setType(MetadataValueType.REFERENCE);
		administrativeUnit_unitAncestors.setMultivalue(true);
		administrativeUnit_unitAncestors.setUndeletable(true);
		administrativeUnit_unitAncestors.defineReferencesTo(administrativeUnitSchemaType);
		MetadataBuilder administrativeUnit_visibleInTrees = administrativeUnitSchema.get("visibleInTrees");
		administrativeUnit_visibleInTrees.setSystemReserved(true);
		administrativeUnit_visibleInTrees.setUndeletable(true);
		administrativeUnit_visibleInTrees.setEssential(true);
		MetadataBuilder cart_allauthorizations = cartSchema.get("allauthorizations");
		cart_allauthorizations.setMultivalue(true);
		cart_allauthorizations.setSystemReserved(true);
		cart_allauthorizations.setUndeletable(true);
		MetadataBuilder cart_authorizations = cartSchema.get("authorizations");
		cart_authorizations.setMultivalue(true);
		cart_authorizations.setSystemReserved(true);
		cart_authorizations.setUndeletable(true);
		MetadataBuilder cart_containers = cartSchema.create("containers").setType(MetadataValueType.REFERENCE);
		cart_containers.setMultivalue(true);
		cart_containers.setUndeletable(true);
		cart_containers.defineReferencesTo(containerRecordSchemaType);
		MetadataBuilder cart_createdBy = cartSchema.get("createdBy");
		cart_createdBy.setSystemReserved(true);
		cart_createdBy.setUndeletable(true);
		MetadataBuilder cart_createdOn = cartSchema.get("createdOn");
		cart_createdOn.setSystemReserved(true);
		cart_createdOn.setUndeletable(true);
		cart_createdOn.setSortable(true);
		MetadataBuilder cart_deleted = cartSchema.get("deleted");
		cart_deleted.setSystemReserved(true);
		cart_deleted.setUndeletable(true);
		MetadataBuilder cart_denyTokens = cartSchema.get("denyTokens");
		cart_denyTokens.setMultivalue(true);
		cart_denyTokens.setSystemReserved(true);
		cart_denyTokens.setUndeletable(true);
		cart_denyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder cart_detachedauthorizations = cartSchema.get("detachedauthorizations");
		cart_detachedauthorizations.setSystemReserved(true);
		cart_detachedauthorizations.setUndeletable(true);
		MetadataBuilder cart_documents = cartSchema.create("documents").setType(MetadataValueType.REFERENCE);
		cart_documents.setMultivalue(true);
		cart_documents.setUndeletable(true);
		cart_documents.defineReferencesTo(documentSchemaType);
		MetadataBuilder cart_folders = cartSchema.create("folders").setType(MetadataValueType.REFERENCE);
		cart_folders.setMultivalue(true);
		cart_folders.setUndeletable(true);
		cart_folders.defineReferencesTo(folderSchemaType);
		MetadataBuilder cart_followers = cartSchema.get("followers");
		cart_followers.setMultivalue(true);
		cart_followers.setSystemReserved(true);
		cart_followers.setUndeletable(true);
		cart_followers.setSearchable(true);
		MetadataBuilder cart_id = cartSchema.get("id");
		cart_id.setDefaultRequirement(true);
		cart_id.setSystemReserved(true);
		cart_id.setUndeletable(true);
		cart_id.setSearchable(true);
		cart_id.setSortable(true);
		cart_id.setUniqueValue(true);
		cart_id.setUnmodifiable(true);
		MetadataBuilder cart_inheritedauthorizations = cartSchema.get("inheritedauthorizations");
		cart_inheritedauthorizations.setMultivalue(true);
		cart_inheritedauthorizations.setSystemReserved(true);
		cart_inheritedauthorizations.setUndeletable(true);
		MetadataBuilder cart_legacyIdentifier = cartSchema.get("legacyIdentifier");
		cart_legacyIdentifier.setDefaultRequirement(true);
		cart_legacyIdentifier.setSystemReserved(true);
		cart_legacyIdentifier.setUndeletable(true);
		cart_legacyIdentifier.setSearchable(true);
		cart_legacyIdentifier.setUniqueValue(true);
		cart_legacyIdentifier.setUnmodifiable(true);
		MetadataBuilder cart_manualTokens = cartSchema.get("manualTokens");
		cart_manualTokens.setMultivalue(true);
		cart_manualTokens.setSystemReserved(true);
		cart_manualTokens.setUndeletable(true);
		cart_manualTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder cart_markedForPreviewConversion = cartSchema.get("markedForPreviewConversion");
		cart_markedForPreviewConversion.setSystemReserved(true);
		cart_markedForPreviewConversion.setUndeletable(true);
		MetadataBuilder cart_modifiedBy = cartSchema.get("modifiedBy");
		cart_modifiedBy.setSystemReserved(true);
		cart_modifiedBy.setUndeletable(true);
		MetadataBuilder cart_modifiedOn = cartSchema.get("modifiedOn");
		cart_modifiedOn.setSystemReserved(true);
		cart_modifiedOn.setUndeletable(true);
		cart_modifiedOn.setSortable(true);
		MetadataBuilder cart_owner = cartSchema.create("owner").setType(MetadataValueType.REFERENCE);
		cart_owner.setDefaultRequirement(true);
		cart_owner.setUndeletable(true);
		cart_owner.defineReferencesTo(userSchemaType);
		MetadataBuilder cart_parentpath = cartSchema.get("parentpath");
		cart_parentpath.setMultivalue(true);
		cart_parentpath.setSystemReserved(true);
		cart_parentpath.setUndeletable(true);
		MetadataBuilder cart_path = cartSchema.get("path");
		cart_path.setMultivalue(true);
		cart_path.setSystemReserved(true);
		cart_path.setUndeletable(true);
		MetadataBuilder cart_pathParts = cartSchema.get("pathParts");
		cart_pathParts.setMultivalue(true);
		cart_pathParts.setSystemReserved(true);
		cart_pathParts.setUndeletable(true);
		MetadataBuilder cart_principalpath = cartSchema.get("principalpath");
		cart_principalpath.setSystemReserved(true);
		cart_principalpath.setUndeletable(true);
		MetadataBuilder cart_removedauthorizations = cartSchema.get("removedauthorizations");
		cart_removedauthorizations.setMultivalue(true);
		cart_removedauthorizations.setSystemReserved(true);
		cart_removedauthorizations.setUndeletable(true);
		MetadataBuilder cart_schema = cartSchema.get("schema");
		cart_schema.setDefaultRequirement(true);
		cart_schema.setSystemReserved(true);
		cart_schema.setUndeletable(true);
		MetadataBuilder cart_searchable = cartSchema.get("searchable");
		cart_searchable.setSystemReserved(true);
		cart_searchable.setUndeletable(true);
		MetadataBuilder cart_shareDenyTokens = cartSchema.get("shareDenyTokens");
		cart_shareDenyTokens.setMultivalue(true);
		cart_shareDenyTokens.setSystemReserved(true);
		cart_shareDenyTokens.setUndeletable(true);
		cart_shareDenyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder cart_shareTokens = cartSchema.get("shareTokens");
		cart_shareTokens.setMultivalue(true);
		cart_shareTokens.setSystemReserved(true);
		cart_shareTokens.setUndeletable(true);
		cart_shareTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder cart_sharedWithUsers = cartSchema.create("sharedWithUsers").setType(MetadataValueType.REFERENCE);
		cart_sharedWithUsers.setMultivalue(true);
		cart_sharedWithUsers.setUndeletable(true);
		cart_sharedWithUsers.defineReferencesTo(userSchemaType);
		MetadataBuilder cart_title = cartSchema.get("title");
		cart_title.setUndeletable(true);
		cart_title.setSchemaAutocomplete(true);
		cart_title.setSearchable(true);
		MetadataBuilder cart_tokens = cartSchema.get("tokens");
		cart_tokens.setMultivalue(true);
		cart_tokens.setSystemReserved(true);
		cart_tokens.setUndeletable(true);
		MetadataBuilder cart_visibleInTrees = cartSchema.get("visibleInTrees");
		cart_visibleInTrees.setSystemReserved(true);
		cart_visibleInTrees.setUndeletable(true);
		MetadataBuilder category_allauthorizations = categorySchema.get("allauthorizations");
		category_allauthorizations.setMultivalue(true);
		category_allauthorizations.setSystemReserved(true);
		category_allauthorizations.setUndeletable(true);
		category_allauthorizations.setEssential(true);
		MetadataBuilder category_authorizations = categorySchema.get("authorizations");
		category_authorizations.setMultivalue(true);
		category_authorizations.setSystemReserved(true);
		category_authorizations.setUndeletable(true);
		category_authorizations.setEssential(true);
		MetadataBuilder category_code = categorySchema.create("code").setType(MetadataValueType.STRING);
		category_code.setDefaultRequirement(true);
		category_code.setUndeletable(true);
		category_code.setEssential(true);
		category_code.setSchemaAutocomplete(true);
		category_code.setSearchable(true);
		category_code.setUniqueValue(true);
		MetadataBuilder category_comments = categorySchema.create("comments").setType(MetadataValueType.STRUCTURE);
		category_comments.setMultivalue(true);
		category_comments.setUndeletable(true);
		category_comments.defineStructureFactory(CommentFactory.class);
		MetadataBuilder category_copyRetentionRulesOnDocumentTypes = categorySchema.create("copyRetentionRulesOnDocumentTypes")
				.setType(MetadataValueType.STRUCTURE);
		category_copyRetentionRulesOnDocumentTypes.setMultivalue(true);
		category_copyRetentionRulesOnDocumentTypes.defineStructureFactory(CopyRetentionRuleInRuleFactory.class);
		MetadataBuilder category_createdBy = categorySchema.get("createdBy");
		category_createdBy.setSystemReserved(true);
		category_createdBy.setUndeletable(true);
		category_createdBy.setEssential(true);
		MetadataBuilder category_createdOn = categorySchema.get("createdOn");
		category_createdOn.setSystemReserved(true);
		category_createdOn.setUndeletable(true);
		category_createdOn.setEssential(true);
		category_createdOn.setSortable(true);
		MetadataBuilder category_deleted = categorySchema.get("deleted");
		category_deleted.setSystemReserved(true);
		category_deleted.setUndeletable(true);
		category_deleted.setEssential(true);
		MetadataBuilder category_denyTokens = categorySchema.get("denyTokens");
		category_denyTokens.setMultivalue(true);
		category_denyTokens.setSystemReserved(true);
		category_denyTokens.setUndeletable(true);
		category_denyTokens.setEssential(true);
		category_denyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder category_description = categorySchema.create("description").setType(MetadataValueType.STRING);
		category_description.setUndeletable(true);
		category_description.setEssentialInSummary(true);
		category_description.setSearchable(true);
		MetadataBuilder category_detachedauthorizations = categorySchema.get("detachedauthorizations");
		category_detachedauthorizations.setSystemReserved(true);
		category_detachedauthorizations.setUndeletable(true);
		category_detachedauthorizations.setEssential(true);
		MetadataBuilder category_followers = categorySchema.get("followers");
		category_followers.setMultivalue(true);
		category_followers.setSystemReserved(true);
		category_followers.setUndeletable(true);
		category_followers.setEssential(true);
		category_followers.setSearchable(true);
		MetadataBuilder category_id = categorySchema.get("id");
		category_id.setDefaultRequirement(true);
		category_id.setSystemReserved(true);
		category_id.setUndeletable(true);
		category_id.setEssential(true);
		category_id.setSearchable(true);
		category_id.setSortable(true);
		category_id.setUniqueValue(true);
		category_id.setUnmodifiable(true);
		MetadataBuilder category_inheritedauthorizations = categorySchema.get("inheritedauthorizations");
		category_inheritedauthorizations.setMultivalue(true);
		category_inheritedauthorizations.setSystemReserved(true);
		category_inheritedauthorizations.setUndeletable(true);
		category_inheritedauthorizations.setEssential(true);
		MetadataBuilder category_keywords = categorySchema.create("keywords").setType(MetadataValueType.STRING);
		category_keywords.setMultivalue(true);
		category_keywords.setUndeletable(true);
		category_keywords.setSchemaAutocomplete(true);
		category_keywords.setSearchable(true);
		MetadataBuilder category_legacyIdentifier = categorySchema.get("legacyIdentifier");
		category_legacyIdentifier.setDefaultRequirement(true);
		category_legacyIdentifier.setSystemReserved(true);
		category_legacyIdentifier.setUndeletable(true);
		category_legacyIdentifier.setEssential(true);
		category_legacyIdentifier.setSearchable(true);
		category_legacyIdentifier.setUniqueValue(true);
		category_legacyIdentifier.setUnmodifiable(true);
		MetadataBuilder category_level = categorySchema.create("level").setType(MetadataValueType.NUMBER);
		MetadataBuilder category_linkable = categorySchema.create("linkable").setType(MetadataValueType.BOOLEAN);
		category_linkable.setUndeletable(true);
		category_linkable.setEssential(true);
		MetadataBuilder category_manualTokens = categorySchema.get("manualTokens");
		category_manualTokens.setMultivalue(true);
		category_manualTokens.setSystemReserved(true);
		category_manualTokens.setUndeletable(true);
		category_manualTokens.setEssential(true);
		category_manualTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder category_markedForPreviewConversion = categorySchema.get("markedForPreviewConversion");
		category_markedForPreviewConversion.setSystemReserved(true);
		category_markedForPreviewConversion.setUndeletable(true);
		category_markedForPreviewConversion.setEssential(true);
		MetadataBuilder category_modifiedBy = categorySchema.get("modifiedBy");
		category_modifiedBy.setSystemReserved(true);
		category_modifiedBy.setUndeletable(true);
		category_modifiedBy.setEssential(true);
		MetadataBuilder category_modifiedOn = categorySchema.get("modifiedOn");
		category_modifiedOn.setSystemReserved(true);
		category_modifiedOn.setUndeletable(true);
		category_modifiedOn.setEssential(true);
		category_modifiedOn.setSortable(true);
		MetadataBuilder category_parent = categorySchema.create("parent").setType(MetadataValueType.REFERENCE);
		category_parent.setUndeletable(true);
		category_parent.setEssential(true);
		category_parent.defineChildOfRelationshipToType(categorySchemaType);
		MetadataBuilder category_parentpath = categorySchema.get("parentpath");
		category_parentpath.setMultivalue(true);
		category_parentpath.setSystemReserved(true);
		category_parentpath.setUndeletable(true);
		category_parentpath.setEssential(true);
		MetadataBuilder category_path = categorySchema.get("path");
		category_path.setMultivalue(true);
		category_path.setSystemReserved(true);
		category_path.setUndeletable(true);
		category_path.setEssential(true);
		MetadataBuilder category_pathParts = categorySchema.get("pathParts");
		category_pathParts.setMultivalue(true);
		category_pathParts.setSystemReserved(true);
		category_pathParts.setUndeletable(true);
		category_pathParts.setEssential(true);
		MetadataBuilder category_principalpath = categorySchema.get("principalpath");
		category_principalpath.setSystemReserved(true);
		category_principalpath.setUndeletable(true);
		category_principalpath.setEssential(true);
		MetadataBuilder category_removedauthorizations = categorySchema.get("removedauthorizations");
		category_removedauthorizations.setMultivalue(true);
		category_removedauthorizations.setSystemReserved(true);
		category_removedauthorizations.setUndeletable(true);
		category_removedauthorizations.setEssential(true);
		MetadataBuilder category_retentionRules = categorySchema.create("retentionRules").setType(MetadataValueType.REFERENCE);
		category_retentionRules.setMultivalue(true);
		category_retentionRules.setUndeletable(true);
		category_retentionRules.setEssential(true);
		category_retentionRules.defineReferencesTo(retentionRuleSchemaType);
		MetadataBuilder category_schema = categorySchema.get("schema");
		category_schema.setDefaultRequirement(true);
		category_schema.setSystemReserved(true);
		category_schema.setUndeletable(true);
		category_schema.setEssential(true);
		MetadataBuilder category_searchable = categorySchema.get("searchable");
		category_searchable.setSystemReserved(true);
		category_searchable.setUndeletable(true);
		category_searchable.setEssential(true);
		MetadataBuilder category_shareDenyTokens = categorySchema.get("shareDenyTokens");
		category_shareDenyTokens.setMultivalue(true);
		category_shareDenyTokens.setSystemReserved(true);
		category_shareDenyTokens.setUndeletable(true);
		category_shareDenyTokens.setEssential(true);
		category_shareDenyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder category_shareTokens = categorySchema.get("shareTokens");
		category_shareTokens.setMultivalue(true);
		category_shareTokens.setSystemReserved(true);
		category_shareTokens.setUndeletable(true);
		category_shareTokens.setEssential(true);
		category_shareTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder category_title = categorySchema.get("title");
		category_title.setDefaultRequirement(true);
		category_title.setUndeletable(true);
		category_title.setEssential(true);
		category_title.setSchemaAutocomplete(true);
		category_title.setSearchable(true);
		MetadataBuilder category_tokens = categorySchema.get("tokens");
		category_tokens.setMultivalue(true);
		category_tokens.setSystemReserved(true);
		category_tokens.setUndeletable(true);
		category_tokens.setEssential(true);
		MetadataBuilder category_visibleInTrees = categorySchema.get("visibleInTrees");
		category_visibleInTrees.setSystemReserved(true);
		category_visibleInTrees.setUndeletable(true);
		category_visibleInTrees.setEssential(true);
		MetadataBuilder containerRecord_administrativeUnit = containerRecordSchema.create("administrativeUnit")
				.setType(MetadataValueType.REFERENCE);
		containerRecord_administrativeUnit.setUndeletable(true);
		containerRecord_administrativeUnit.setEssential(true);
		containerRecord_administrativeUnit.defineTaxonomyRelationshipToType(administrativeUnitSchemaType);
		MetadataBuilder containerRecord_allauthorizations = containerRecordSchema.get("allauthorizations");
		containerRecord_allauthorizations.setMultivalue(true);
		containerRecord_allauthorizations.setSystemReserved(true);
		containerRecord_allauthorizations.setUndeletable(true);
		containerRecord_allauthorizations.setEssential(true);
		MetadataBuilder containerRecord_authorizations = containerRecordSchema.get("authorizations");
		containerRecord_authorizations.setMultivalue(true);
		containerRecord_authorizations.setSystemReserved(true);
		containerRecord_authorizations.setUndeletable(true);
		containerRecord_authorizations.setEssential(true);
		MetadataBuilder containerRecord_borrowDate = containerRecordSchema.create("borrowDate").setType(MetadataValueType.DATE);
		containerRecord_borrowDate.setUndeletable(true);
		containerRecord_borrowDate.setEssential(true);
		MetadataBuilder containerRecord_borrowed = containerRecordSchema.create("borrowed").setType(MetadataValueType.BOOLEAN);
		containerRecord_borrowed.setUndeletable(true);
		MetadataBuilder containerRecord_borrower = containerRecordSchema.create("borrower").setType(MetadataValueType.REFERENCE);
		containerRecord_borrower.setUndeletable(true);
		containerRecord_borrower.setEssential(true);
		containerRecord_borrower.defineReferencesTo(userSchemaType);
		MetadataBuilder containerRecord_capacity = containerRecordSchema.create("capacity").setType(MetadataValueType.NUMBER);
		containerRecord_capacity.setUndeletable(true);
		MetadataBuilder containerRecord_comments = containerRecordSchema.create("comments").setType(MetadataValueType.STRUCTURE);
		containerRecord_comments.setMultivalue(true);
		containerRecord_comments.setUndeletable(true);
		containerRecord_comments.defineStructureFactory(CommentFactory.class);
		MetadataBuilder containerRecord_completionDate = containerRecordSchema.create("completionDate")
				.setType(MetadataValueType.DATE);
		containerRecord_completionDate.setUndeletable(true);
		containerRecord_completionDate.setEssential(true);
		MetadataBuilder containerRecord_createdBy = containerRecordSchema.get("createdBy");
		containerRecord_createdBy.setSystemReserved(true);
		containerRecord_createdBy.setUndeletable(true);
		containerRecord_createdBy.setEssential(true);
		MetadataBuilder containerRecord_createdOn = containerRecordSchema.get("createdOn");
		containerRecord_createdOn.setSystemReserved(true);
		containerRecord_createdOn.setUndeletable(true);
		containerRecord_createdOn.setEssential(true);
		containerRecord_createdOn.setSortable(true);
		MetadataBuilder containerRecord_decommissioningType = containerRecordSchema.create("decommissioningType")
				.setType(MetadataValueType.ENUM);
		containerRecord_decommissioningType.setUndeletable(true);
		containerRecord_decommissioningType.setEssential(true);
		containerRecord_decommissioningType.defineAsEnum(DecommissioningType.class);
		MetadataBuilder containerRecord_deleted = containerRecordSchema.get("deleted");
		containerRecord_deleted.setSystemReserved(true);
		containerRecord_deleted.setUndeletable(true);
		containerRecord_deleted.setEssential(true);
		MetadataBuilder containerRecord_denyTokens = containerRecordSchema.get("denyTokens");
		containerRecord_denyTokens.setMultivalue(true);
		containerRecord_denyTokens.setSystemReserved(true);
		containerRecord_denyTokens.setUndeletable(true);
		containerRecord_denyTokens.setEssential(true);
		containerRecord_denyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder containerRecord_description = containerRecordSchema.create("description")
				.setType(MetadataValueType.STRING);
		containerRecord_description.setUndeletable(true);
		containerRecord_description.setEssentialInSummary(true);
		containerRecord_description.setSearchable(true);
		MetadataBuilder containerRecord_detachedauthorizations = containerRecordSchema.get("detachedauthorizations");
		containerRecord_detachedauthorizations.setSystemReserved(true);
		containerRecord_detachedauthorizations.setUndeletable(true);
		containerRecord_detachedauthorizations.setEssential(true);
		MetadataBuilder containerRecord_filingSpace = containerRecordSchema.create("filingSpace")
				.setType(MetadataValueType.REFERENCE);
		containerRecord_filingSpace.setUndeletable(true);
		containerRecord_filingSpace.setEssential(true);
		containerRecord_filingSpace.defineReferencesTo(filingSpaceSchemaType);
		MetadataBuilder containerRecord_fillRatioEntered = containerRecordSchema.create("fillRatioEntered")
				.setType(MetadataValueType.NUMBER);
		containerRecord_fillRatioEntered.setUndeletable(true);
		MetadataBuilder containerRecord_followers = containerRecordSchema.get("followers");
		containerRecord_followers.setMultivalue(true);
		containerRecord_followers.setSystemReserved(true);
		containerRecord_followers.setUndeletable(true);
		containerRecord_followers.setEssential(true);
		containerRecord_followers.setSearchable(true);
		MetadataBuilder containerRecord_full = containerRecordSchema.create("full").setType(MetadataValueType.BOOLEAN);
		containerRecord_full.setUndeletable(true);
		containerRecord_full.setEssential(true);
		MetadataBuilder containerRecord_id = containerRecordSchema.get("id");
		containerRecord_id.setDefaultRequirement(true);
		containerRecord_id.setSystemReserved(true);
		containerRecord_id.setUndeletable(true);
		containerRecord_id.setEssential(true);
		containerRecord_id.setSearchable(true);
		containerRecord_id.setSortable(true);
		containerRecord_id.setUniqueValue(true);
		containerRecord_id.setUnmodifiable(true);
		MetadataBuilder containerRecord_identifier = containerRecordSchema.create("identifier").setType(MetadataValueType.STRING);
		containerRecord_identifier.setUndeletable(true);
		containerRecord_identifier.setEssential(true);
		containerRecord_identifier.setSearchable(true);
		MetadataBuilder containerRecord_inheritedauthorizations = containerRecordSchema.get("inheritedauthorizations");
		containerRecord_inheritedauthorizations.setMultivalue(true);
		containerRecord_inheritedauthorizations.setSystemReserved(true);
		containerRecord_inheritedauthorizations.setUndeletable(true);
		containerRecord_inheritedauthorizations.setEssential(true);
		MetadataBuilder containerRecord_legacyIdentifier = containerRecordSchema.get("legacyIdentifier");
		containerRecord_legacyIdentifier.setDefaultRequirement(true);
		containerRecord_legacyIdentifier.setSystemReserved(true);
		containerRecord_legacyIdentifier.setUndeletable(true);
		containerRecord_legacyIdentifier.setEssential(true);
		containerRecord_legacyIdentifier.setSearchable(true);
		containerRecord_legacyIdentifier.setUniqueValue(true);
		containerRecord_legacyIdentifier.setUnmodifiable(true);
		MetadataBuilder containerRecord_manualTokens = containerRecordSchema.get("manualTokens");
		containerRecord_manualTokens.setMultivalue(true);
		containerRecord_manualTokens.setSystemReserved(true);
		containerRecord_manualTokens.setUndeletable(true);
		containerRecord_manualTokens.setEssential(true);
		containerRecord_manualTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder containerRecord_markedForPreviewConversion = containerRecordSchema.get("markedForPreviewConversion");
		containerRecord_markedForPreviewConversion.setSystemReserved(true);
		containerRecord_markedForPreviewConversion.setUndeletable(true);
		containerRecord_markedForPreviewConversion.setEssential(true);
		MetadataBuilder containerRecord_modifiedBy = containerRecordSchema.get("modifiedBy");
		containerRecord_modifiedBy.setSystemReserved(true);
		containerRecord_modifiedBy.setUndeletable(true);
		containerRecord_modifiedBy.setEssential(true);
		MetadataBuilder containerRecord_modifiedOn = containerRecordSchema.get("modifiedOn");
		containerRecord_modifiedOn.setSystemReserved(true);
		containerRecord_modifiedOn.setUndeletable(true);
		containerRecord_modifiedOn.setEssential(true);
		containerRecord_modifiedOn.setSortable(true);
		MetadataBuilder containerRecord_parentpath = containerRecordSchema.get("parentpath");
		containerRecord_parentpath.setMultivalue(true);
		containerRecord_parentpath.setSystemReserved(true);
		containerRecord_parentpath.setUndeletable(true);
		containerRecord_parentpath.setEssential(true);
		MetadataBuilder containerRecord_path = containerRecordSchema.get("path");
		containerRecord_path.setMultivalue(true);
		containerRecord_path.setSystemReserved(true);
		containerRecord_path.setUndeletable(true);
		containerRecord_path.setEssential(true);
		MetadataBuilder containerRecord_pathParts = containerRecordSchema.get("pathParts");
		containerRecord_pathParts.setMultivalue(true);
		containerRecord_pathParts.setSystemReserved(true);
		containerRecord_pathParts.setUndeletable(true);
		containerRecord_pathParts.setEssential(true);
		MetadataBuilder containerRecord_planifiedReturnDate = containerRecordSchema.create("planifiedReturnDate")
				.setType(MetadataValueType.DATE);
		containerRecord_planifiedReturnDate.setUndeletable(true);
		containerRecord_planifiedReturnDate.setEssential(true);
		MetadataBuilder containerRecord_position = containerRecordSchema.create("position").setType(MetadataValueType.STRING);
		containerRecord_position.setUndeletable(true);
		containerRecord_position.setSearchable(true);
		MetadataBuilder containerRecord_principalpath = containerRecordSchema.get("principalpath");
		containerRecord_principalpath.setSystemReserved(true);
		containerRecord_principalpath.setUndeletable(true);
		containerRecord_principalpath.setEssential(true);
		MetadataBuilder containerRecord_realDepositDate = containerRecordSchema.create("realDepositDate")
				.setType(MetadataValueType.DATE);
		containerRecord_realDepositDate.setUndeletable(true);
		containerRecord_realDepositDate.setEssential(true);
		MetadataBuilder containerRecord_realReturnDate = containerRecordSchema.create("realReturnDate")
				.setType(MetadataValueType.DATE);
		containerRecord_realReturnDate.setUndeletable(true);
		containerRecord_realReturnDate.setEssential(true);
		MetadataBuilder containerRecord_realTransferDate = containerRecordSchema.create("realTransferDate")
				.setType(MetadataValueType.DATE);
		containerRecord_realTransferDate.setUndeletable(true);
		containerRecord_realTransferDate.setEssential(true);
		MetadataBuilder containerRecord_removedauthorizations = containerRecordSchema.get("removedauthorizations");
		containerRecord_removedauthorizations.setMultivalue(true);
		containerRecord_removedauthorizations.setSystemReserved(true);
		containerRecord_removedauthorizations.setUndeletable(true);
		containerRecord_removedauthorizations.setEssential(true);
		MetadataBuilder containerRecord_schema = containerRecordSchema.get("schema");
		containerRecord_schema.setDefaultRequirement(true);
		containerRecord_schema.setSystemReserved(true);
		containerRecord_schema.setUndeletable(true);
		containerRecord_schema.setEssential(true);
		MetadataBuilder containerRecord_searchable = containerRecordSchema.get("searchable");
		containerRecord_searchable.setSystemReserved(true);
		containerRecord_searchable.setUndeletable(true);
		containerRecord_searchable.setEssential(true);
		MetadataBuilder containerRecord_shareDenyTokens = containerRecordSchema.get("shareDenyTokens");
		containerRecord_shareDenyTokens.setMultivalue(true);
		containerRecord_shareDenyTokens.setSystemReserved(true);
		containerRecord_shareDenyTokens.setUndeletable(true);
		containerRecord_shareDenyTokens.setEssential(true);
		containerRecord_shareDenyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder containerRecord_shareTokens = containerRecordSchema.get("shareTokens");
		containerRecord_shareTokens.setMultivalue(true);
		containerRecord_shareTokens.setSystemReserved(true);
		containerRecord_shareTokens.setUndeletable(true);
		containerRecord_shareTokens.setEssential(true);
		containerRecord_shareTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder containerRecord_storageSpace = containerRecordSchema.create("storageSpace")
				.setType(MetadataValueType.REFERENCE);
		containerRecord_storageSpace.setUndeletable(true);
		containerRecord_storageSpace.setEssential(true);
		containerRecord_storageSpace.defineTaxonomyRelationshipToType(storageSpaceSchemaType);
		MetadataBuilder containerRecord_temporaryIdentifier = containerRecordSchema.create("temporaryIdentifier")
				.setType(MetadataValueType.STRING);
		containerRecord_temporaryIdentifier.setDefaultRequirement(true);
		containerRecord_temporaryIdentifier.setUndeletable(true);
		containerRecord_temporaryIdentifier.setEssential(true);
		containerRecord_temporaryIdentifier.setSearchable(true);
		MetadataBuilder containerRecord_title = containerRecordSchema.get("title");
		containerRecord_title.setDefaultRequirement(true);
		containerRecord_title.setUndeletable(true);
		containerRecord_title.setEssential(true);
		containerRecord_title.setSchemaAutocomplete(true);
		containerRecord_title.setSearchable(true);
		MetadataBuilder containerRecord_tokens = containerRecordSchema.get("tokens");
		containerRecord_tokens.setMultivalue(true);
		containerRecord_tokens.setSystemReserved(true);
		containerRecord_tokens.setUndeletable(true);
		containerRecord_tokens.setEssential(true);
		MetadataBuilder containerRecord_type = containerRecordSchema.create("type").setType(MetadataValueType.REFERENCE);
		containerRecord_type.setDefaultRequirement(true);
		containerRecord_type.setUndeletable(true);
		containerRecord_type.setEssential(true);
		containerRecord_type.defineReferencesTo(ddvContainerRecordTypeSchemaType);
		MetadataBuilder containerRecord_visibleInTrees = containerRecordSchema.get("visibleInTrees");
		containerRecord_visibleInTrees.setSystemReserved(true);
		containerRecord_visibleInTrees.setUndeletable(true);
		containerRecord_visibleInTrees.setEssential(true);
		MetadataBuilder ddvContainerRecordType_allauthorizations = ddvContainerRecordTypeSchema.get("allauthorizations");
		ddvContainerRecordType_allauthorizations.setMultivalue(true);
		ddvContainerRecordType_allauthorizations.setSystemReserved(true);
		ddvContainerRecordType_allauthorizations.setUndeletable(true);
		MetadataBuilder ddvContainerRecordType_authorizations = ddvContainerRecordTypeSchema.get("authorizations");
		ddvContainerRecordType_authorizations.setMultivalue(true);
		ddvContainerRecordType_authorizations.setSystemReserved(true);
		ddvContainerRecordType_authorizations.setUndeletable(true);
		MetadataBuilder ddvContainerRecordType_code = ddvContainerRecordTypeSchema.create("code")
				.setType(MetadataValueType.STRING);
		ddvContainerRecordType_code.setDefaultRequirement(true);
		ddvContainerRecordType_code.setUndeletable(true);
		ddvContainerRecordType_code.setSchemaAutocomplete(true);
		ddvContainerRecordType_code.setSearchable(true);
		ddvContainerRecordType_code.setUniqueValue(true);
		MetadataBuilder ddvContainerRecordType_comments = ddvContainerRecordTypeSchema.create("comments")
				.setType(MetadataValueType.STRUCTURE);
		ddvContainerRecordType_comments.setMultivalue(true);
		ddvContainerRecordType_comments.defineStructureFactory(CommentFactory.class);
		MetadataBuilder ddvContainerRecordType_createdBy = ddvContainerRecordTypeSchema.get("createdBy");
		ddvContainerRecordType_createdBy.setSystemReserved(true);
		ddvContainerRecordType_createdBy.setUndeletable(true);
		MetadataBuilder ddvContainerRecordType_createdOn = ddvContainerRecordTypeSchema.get("createdOn");
		ddvContainerRecordType_createdOn.setSystemReserved(true);
		ddvContainerRecordType_createdOn.setUndeletable(true);
		ddvContainerRecordType_createdOn.setSortable(true);
		MetadataBuilder ddvContainerRecordType_deleted = ddvContainerRecordTypeSchema.get("deleted");
		ddvContainerRecordType_deleted.setSystemReserved(true);
		ddvContainerRecordType_deleted.setUndeletable(true);
		MetadataBuilder ddvContainerRecordType_denyTokens = ddvContainerRecordTypeSchema.get("denyTokens");
		ddvContainerRecordType_denyTokens.setMultivalue(true);
		ddvContainerRecordType_denyTokens.setSystemReserved(true);
		ddvContainerRecordType_denyTokens.setUndeletable(true);
		ddvContainerRecordType_denyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder ddvContainerRecordType_description = ddvContainerRecordTypeSchema.create("description")
				.setType(MetadataValueType.TEXT);
		ddvContainerRecordType_description.setUndeletable(true);
		ddvContainerRecordType_description.setEssentialInSummary(true);
		ddvContainerRecordType_description.setSearchable(true);
		MetadataBuilder ddvContainerRecordType_detachedauthorizations = ddvContainerRecordTypeSchema
				.get("detachedauthorizations");
		ddvContainerRecordType_detachedauthorizations.setSystemReserved(true);
		ddvContainerRecordType_detachedauthorizations.setUndeletable(true);
		MetadataBuilder ddvContainerRecordType_followers = ddvContainerRecordTypeSchema.get("followers");
		ddvContainerRecordType_followers.setMultivalue(true);
		ddvContainerRecordType_followers.setSystemReserved(true);
		ddvContainerRecordType_followers.setUndeletable(true);
		ddvContainerRecordType_followers.setSearchable(true);
		MetadataBuilder ddvContainerRecordType_id = ddvContainerRecordTypeSchema.get("id");
		ddvContainerRecordType_id.setDefaultRequirement(true);
		ddvContainerRecordType_id.setSystemReserved(true);
		ddvContainerRecordType_id.setUndeletable(true);
		ddvContainerRecordType_id.setSearchable(true);
		ddvContainerRecordType_id.setSortable(true);
		ddvContainerRecordType_id.setUniqueValue(true);
		ddvContainerRecordType_id.setUnmodifiable(true);
		MetadataBuilder ddvContainerRecordType_inheritedauthorizations = ddvContainerRecordTypeSchema
				.get("inheritedauthorizations");
		ddvContainerRecordType_inheritedauthorizations.setMultivalue(true);
		ddvContainerRecordType_inheritedauthorizations.setSystemReserved(true);
		ddvContainerRecordType_inheritedauthorizations.setUndeletable(true);
		MetadataBuilder ddvContainerRecordType_legacyIdentifier = ddvContainerRecordTypeSchema.get("legacyIdentifier");
		ddvContainerRecordType_legacyIdentifier.setDefaultRequirement(true);
		ddvContainerRecordType_legacyIdentifier.setSystemReserved(true);
		ddvContainerRecordType_legacyIdentifier.setUndeletable(true);
		ddvContainerRecordType_legacyIdentifier.setSearchable(true);
		ddvContainerRecordType_legacyIdentifier.setUniqueValue(true);
		ddvContainerRecordType_legacyIdentifier.setUnmodifiable(true);
		MetadataBuilder ddvContainerRecordType_linkedSchema = ddvContainerRecordTypeSchema.create("linkedSchema")
				.setType(MetadataValueType.STRING);
		MetadataBuilder ddvContainerRecordType_manualTokens = ddvContainerRecordTypeSchema.get("manualTokens");
		ddvContainerRecordType_manualTokens.setMultivalue(true);
		ddvContainerRecordType_manualTokens.setSystemReserved(true);
		ddvContainerRecordType_manualTokens.setUndeletable(true);
		ddvContainerRecordType_manualTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder ddvContainerRecordType_markedForPreviewConversion = ddvContainerRecordTypeSchema
				.get("markedForPreviewConversion");
		ddvContainerRecordType_markedForPreviewConversion.setSystemReserved(true);
		ddvContainerRecordType_markedForPreviewConversion.setUndeletable(true);
		MetadataBuilder ddvContainerRecordType_modifiedBy = ddvContainerRecordTypeSchema.get("modifiedBy");
		ddvContainerRecordType_modifiedBy.setSystemReserved(true);
		ddvContainerRecordType_modifiedBy.setUndeletable(true);
		MetadataBuilder ddvContainerRecordType_modifiedOn = ddvContainerRecordTypeSchema.get("modifiedOn");
		ddvContainerRecordType_modifiedOn.setSystemReserved(true);
		ddvContainerRecordType_modifiedOn.setUndeletable(true);
		ddvContainerRecordType_modifiedOn.setSortable(true);
		MetadataBuilder ddvContainerRecordType_parentpath = ddvContainerRecordTypeSchema.get("parentpath");
		ddvContainerRecordType_parentpath.setMultivalue(true);
		ddvContainerRecordType_parentpath.setSystemReserved(true);
		ddvContainerRecordType_parentpath.setUndeletable(true);
		MetadataBuilder ddvContainerRecordType_path = ddvContainerRecordTypeSchema.get("path");
		ddvContainerRecordType_path.setMultivalue(true);
		ddvContainerRecordType_path.setSystemReserved(true);
		ddvContainerRecordType_path.setUndeletable(true);
		MetadataBuilder ddvContainerRecordType_pathParts = ddvContainerRecordTypeSchema.get("pathParts");
		ddvContainerRecordType_pathParts.setMultivalue(true);
		ddvContainerRecordType_pathParts.setSystemReserved(true);
		ddvContainerRecordType_pathParts.setUndeletable(true);
		MetadataBuilder ddvContainerRecordType_principalpath = ddvContainerRecordTypeSchema.get("principalpath");
		ddvContainerRecordType_principalpath.setSystemReserved(true);
		ddvContainerRecordType_principalpath.setUndeletable(true);
		MetadataBuilder ddvContainerRecordType_removedauthorizations = ddvContainerRecordTypeSchema.get("removedauthorizations");
		ddvContainerRecordType_removedauthorizations.setMultivalue(true);
		ddvContainerRecordType_removedauthorizations.setSystemReserved(true);
		ddvContainerRecordType_removedauthorizations.setUndeletable(true);
		MetadataBuilder ddvContainerRecordType_schema = ddvContainerRecordTypeSchema.get("schema");
		ddvContainerRecordType_schema.setDefaultRequirement(true);
		ddvContainerRecordType_schema.setSystemReserved(true);
		ddvContainerRecordType_schema.setUndeletable(true);
		MetadataBuilder ddvContainerRecordType_searchable = ddvContainerRecordTypeSchema.get("searchable");
		ddvContainerRecordType_searchable.setSystemReserved(true);
		ddvContainerRecordType_searchable.setUndeletable(true);
		MetadataBuilder ddvContainerRecordType_shareDenyTokens = ddvContainerRecordTypeSchema.get("shareDenyTokens");
		ddvContainerRecordType_shareDenyTokens.setMultivalue(true);
		ddvContainerRecordType_shareDenyTokens.setSystemReserved(true);
		ddvContainerRecordType_shareDenyTokens.setUndeletable(true);
		ddvContainerRecordType_shareDenyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder ddvContainerRecordType_shareTokens = ddvContainerRecordTypeSchema.get("shareTokens");
		ddvContainerRecordType_shareTokens.setMultivalue(true);
		ddvContainerRecordType_shareTokens.setSystemReserved(true);
		ddvContainerRecordType_shareTokens.setUndeletable(true);
		ddvContainerRecordType_shareTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder ddvContainerRecordType_title = ddvContainerRecordTypeSchema.get("title");
		ddvContainerRecordType_title.setDefaultRequirement(true);
		ddvContainerRecordType_title.setUndeletable(true);
		ddvContainerRecordType_title.setSchemaAutocomplete(true);
		ddvContainerRecordType_title.setSearchable(true);
		ddvContainerRecordType_title.setUniqueValue(true);
		MetadataBuilder ddvContainerRecordType_tokens = ddvContainerRecordTypeSchema.get("tokens");
		ddvContainerRecordType_tokens.setMultivalue(true);
		ddvContainerRecordType_tokens.setSystemReserved(true);
		ddvContainerRecordType_tokens.setUndeletable(true);
		MetadataBuilder ddvContainerRecordType_visibleInTrees = ddvContainerRecordTypeSchema.get("visibleInTrees");
		ddvContainerRecordType_visibleInTrees.setSystemReserved(true);
		ddvContainerRecordType_visibleInTrees.setUndeletable(true);
		MetadataBuilder ddvDocumentType_allauthorizations = ddvDocumentTypeSchema.get("allauthorizations");
		ddvDocumentType_allauthorizations.setMultivalue(true);
		ddvDocumentType_allauthorizations.setSystemReserved(true);
		ddvDocumentType_allauthorizations.setUndeletable(true);
		MetadataBuilder ddvDocumentType_authorizations = ddvDocumentTypeSchema.get("authorizations");
		ddvDocumentType_authorizations.setMultivalue(true);
		ddvDocumentType_authorizations.setSystemReserved(true);
		ddvDocumentType_authorizations.setUndeletable(true);
		MetadataBuilder ddvDocumentType_code = ddvDocumentTypeSchema.create("code").setType(MetadataValueType.STRING);
		ddvDocumentType_code.setDefaultRequirement(true);
		ddvDocumentType_code.setUndeletable(true);
		ddvDocumentType_code.setSchemaAutocomplete(true);
		ddvDocumentType_code.setSearchable(true);
		ddvDocumentType_code.setUniqueValue(true);
		MetadataBuilder ddvDocumentType_comments = ddvDocumentTypeSchema.create("comments").setType(MetadataValueType.STRUCTURE);
		ddvDocumentType_comments.setMultivalue(true);
		ddvDocumentType_comments.defineStructureFactory(CommentFactory.class);
		MetadataBuilder ddvDocumentType_createdBy = ddvDocumentTypeSchema.get("createdBy");
		ddvDocumentType_createdBy.setSystemReserved(true);
		ddvDocumentType_createdBy.setUndeletable(true);
		MetadataBuilder ddvDocumentType_createdOn = ddvDocumentTypeSchema.get("createdOn");
		ddvDocumentType_createdOn.setSystemReserved(true);
		ddvDocumentType_createdOn.setUndeletable(true);
		ddvDocumentType_createdOn.setSortable(true);
		MetadataBuilder ddvDocumentType_deleted = ddvDocumentTypeSchema.get("deleted");
		ddvDocumentType_deleted.setSystemReserved(true);
		ddvDocumentType_deleted.setUndeletable(true);
		MetadataBuilder ddvDocumentType_denyTokens = ddvDocumentTypeSchema.get("denyTokens");
		ddvDocumentType_denyTokens.setMultivalue(true);
		ddvDocumentType_denyTokens.setSystemReserved(true);
		ddvDocumentType_denyTokens.setUndeletable(true);
		ddvDocumentType_denyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder ddvDocumentType_description = ddvDocumentTypeSchema.create("description").setType(MetadataValueType.TEXT);
		ddvDocumentType_description.setUndeletable(true);
		ddvDocumentType_description.setEssentialInSummary(true);
		ddvDocumentType_description.setSearchable(true);
		MetadataBuilder ddvDocumentType_detachedauthorizations = ddvDocumentTypeSchema.get("detachedauthorizations");
		ddvDocumentType_detachedauthorizations.setSystemReserved(true);
		ddvDocumentType_detachedauthorizations.setUndeletable(true);
		MetadataBuilder ddvDocumentType_followers = ddvDocumentTypeSchema.get("followers");
		ddvDocumentType_followers.setMultivalue(true);
		ddvDocumentType_followers.setSystemReserved(true);
		ddvDocumentType_followers.setUndeletable(true);
		ddvDocumentType_followers.setSearchable(true);
		MetadataBuilder ddvDocumentType_id = ddvDocumentTypeSchema.get("id");
		ddvDocumentType_id.setDefaultRequirement(true);
		ddvDocumentType_id.setSystemReserved(true);
		ddvDocumentType_id.setUndeletable(true);
		ddvDocumentType_id.setSearchable(true);
		ddvDocumentType_id.setSortable(true);
		ddvDocumentType_id.setUniqueValue(true);
		ddvDocumentType_id.setUnmodifiable(true);
		MetadataBuilder ddvDocumentType_inheritedauthorizations = ddvDocumentTypeSchema.get("inheritedauthorizations");
		ddvDocumentType_inheritedauthorizations.setMultivalue(true);
		ddvDocumentType_inheritedauthorizations.setSystemReserved(true);
		ddvDocumentType_inheritedauthorizations.setUndeletable(true);
		MetadataBuilder ddvDocumentType_legacyIdentifier = ddvDocumentTypeSchema.get("legacyIdentifier");
		ddvDocumentType_legacyIdentifier.setDefaultRequirement(true);
		ddvDocumentType_legacyIdentifier.setSystemReserved(true);
		ddvDocumentType_legacyIdentifier.setUndeletable(true);
		ddvDocumentType_legacyIdentifier.setSearchable(true);
		ddvDocumentType_legacyIdentifier.setUniqueValue(true);
		ddvDocumentType_legacyIdentifier.setUnmodifiable(true);
		MetadataBuilder ddvDocumentType_linkedSchema = ddvDocumentTypeSchema.create("linkedSchema")
				.setType(MetadataValueType.STRING);
		MetadataBuilder ddvDocumentType_manualTokens = ddvDocumentTypeSchema.get("manualTokens");
		ddvDocumentType_manualTokens.setMultivalue(true);
		ddvDocumentType_manualTokens.setSystemReserved(true);
		ddvDocumentType_manualTokens.setUndeletable(true);
		ddvDocumentType_manualTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder ddvDocumentType_markedForPreviewConversion = ddvDocumentTypeSchema.get("markedForPreviewConversion");
		ddvDocumentType_markedForPreviewConversion.setSystemReserved(true);
		ddvDocumentType_markedForPreviewConversion.setUndeletable(true);
		MetadataBuilder ddvDocumentType_modifiedBy = ddvDocumentTypeSchema.get("modifiedBy");
		ddvDocumentType_modifiedBy.setSystemReserved(true);
		ddvDocumentType_modifiedBy.setUndeletable(true);
		MetadataBuilder ddvDocumentType_modifiedOn = ddvDocumentTypeSchema.get("modifiedOn");
		ddvDocumentType_modifiedOn.setSystemReserved(true);
		ddvDocumentType_modifiedOn.setUndeletable(true);
		ddvDocumentType_modifiedOn.setSortable(true);
		MetadataBuilder ddvDocumentType_parentpath = ddvDocumentTypeSchema.get("parentpath");
		ddvDocumentType_parentpath.setMultivalue(true);
		ddvDocumentType_parentpath.setSystemReserved(true);
		ddvDocumentType_parentpath.setUndeletable(true);
		MetadataBuilder ddvDocumentType_path = ddvDocumentTypeSchema.get("path");
		ddvDocumentType_path.setMultivalue(true);
		ddvDocumentType_path.setSystemReserved(true);
		ddvDocumentType_path.setUndeletable(true);
		MetadataBuilder ddvDocumentType_pathParts = ddvDocumentTypeSchema.get("pathParts");
		ddvDocumentType_pathParts.setMultivalue(true);
		ddvDocumentType_pathParts.setSystemReserved(true);
		ddvDocumentType_pathParts.setUndeletable(true);
		MetadataBuilder ddvDocumentType_principalpath = ddvDocumentTypeSchema.get("principalpath");
		ddvDocumentType_principalpath.setSystemReserved(true);
		ddvDocumentType_principalpath.setUndeletable(true);
		MetadataBuilder ddvDocumentType_removedauthorizations = ddvDocumentTypeSchema.get("removedauthorizations");
		ddvDocumentType_removedauthorizations.setMultivalue(true);
		ddvDocumentType_removedauthorizations.setSystemReserved(true);
		ddvDocumentType_removedauthorizations.setUndeletable(true);
		MetadataBuilder ddvDocumentType_schema = ddvDocumentTypeSchema.get("schema");
		ddvDocumentType_schema.setDefaultRequirement(true);
		ddvDocumentType_schema.setSystemReserved(true);
		ddvDocumentType_schema.setUndeletable(true);
		MetadataBuilder ddvDocumentType_searchable = ddvDocumentTypeSchema.get("searchable");
		ddvDocumentType_searchable.setSystemReserved(true);
		ddvDocumentType_searchable.setUndeletable(true);
		MetadataBuilder ddvDocumentType_shareDenyTokens = ddvDocumentTypeSchema.get("shareDenyTokens");
		ddvDocumentType_shareDenyTokens.setMultivalue(true);
		ddvDocumentType_shareDenyTokens.setSystemReserved(true);
		ddvDocumentType_shareDenyTokens.setUndeletable(true);
		ddvDocumentType_shareDenyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder ddvDocumentType_shareTokens = ddvDocumentTypeSchema.get("shareTokens");
		ddvDocumentType_shareTokens.setMultivalue(true);
		ddvDocumentType_shareTokens.setSystemReserved(true);
		ddvDocumentType_shareTokens.setUndeletable(true);
		ddvDocumentType_shareTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder ddvDocumentType_templates = ddvDocumentTypeSchema.create("templates").setType(MetadataValueType.CONTENT);
		ddvDocumentType_templates.setMultivalue(true);
		ddvDocumentType_templates.setUndeletable(true);
		ddvDocumentType_templates.defineStructureFactory(ContentFactory.class);
		MetadataBuilder ddvDocumentType_title = ddvDocumentTypeSchema.get("title");
		ddvDocumentType_title.setDefaultRequirement(true);
		ddvDocumentType_title.setUndeletable(true);
		ddvDocumentType_title.setSchemaAutocomplete(true);
		ddvDocumentType_title.setSearchable(true);
		ddvDocumentType_title.setUniqueValue(true);
		MetadataBuilder ddvDocumentType_tokens = ddvDocumentTypeSchema.get("tokens");
		ddvDocumentType_tokens.setMultivalue(true);
		ddvDocumentType_tokens.setSystemReserved(true);
		ddvDocumentType_tokens.setUndeletable(true);
		MetadataBuilder ddvDocumentType_visibleInTrees = ddvDocumentTypeSchema.get("visibleInTrees");
		ddvDocumentType_visibleInTrees.setSystemReserved(true);
		ddvDocumentType_visibleInTrees.setUndeletable(true);
		MetadataBuilder ddvFolderType_allauthorizations = ddvFolderTypeSchema.get("allauthorizations");
		ddvFolderType_allauthorizations.setMultivalue(true);
		ddvFolderType_allauthorizations.setSystemReserved(true);
		ddvFolderType_allauthorizations.setUndeletable(true);
		MetadataBuilder ddvFolderType_authorizations = ddvFolderTypeSchema.get("authorizations");
		ddvFolderType_authorizations.setMultivalue(true);
		ddvFolderType_authorizations.setSystemReserved(true);
		ddvFolderType_authorizations.setUndeletable(true);
		MetadataBuilder ddvFolderType_code = ddvFolderTypeSchema.create("code").setType(MetadataValueType.STRING);
		ddvFolderType_code.setDefaultRequirement(true);
		ddvFolderType_code.setUndeletable(true);
		ddvFolderType_code.setSchemaAutocomplete(true);
		ddvFolderType_code.setSearchable(true);
		ddvFolderType_code.setUniqueValue(true);
		MetadataBuilder ddvFolderType_comments = ddvFolderTypeSchema.create("comments").setType(MetadataValueType.STRUCTURE);
		ddvFolderType_comments.setMultivalue(true);
		ddvFolderType_comments.defineStructureFactory(CommentFactory.class);
		MetadataBuilder ddvFolderType_createdBy = ddvFolderTypeSchema.get("createdBy");
		ddvFolderType_createdBy.setSystemReserved(true);
		ddvFolderType_createdBy.setUndeletable(true);
		MetadataBuilder ddvFolderType_createdOn = ddvFolderTypeSchema.get("createdOn");
		ddvFolderType_createdOn.setSystemReserved(true);
		ddvFolderType_createdOn.setUndeletable(true);
		ddvFolderType_createdOn.setSortable(true);
		MetadataBuilder ddvFolderType_deleted = ddvFolderTypeSchema.get("deleted");
		ddvFolderType_deleted.setSystemReserved(true);
		ddvFolderType_deleted.setUndeletable(true);
		MetadataBuilder ddvFolderType_denyTokens = ddvFolderTypeSchema.get("denyTokens");
		ddvFolderType_denyTokens.setMultivalue(true);
		ddvFolderType_denyTokens.setSystemReserved(true);
		ddvFolderType_denyTokens.setUndeletable(true);
		ddvFolderType_denyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder ddvFolderType_description = ddvFolderTypeSchema.create("description").setType(MetadataValueType.TEXT);
		ddvFolderType_description.setUndeletable(true);
		ddvFolderType_description.setEssentialInSummary(true);
		ddvFolderType_description.setSearchable(true);
		MetadataBuilder ddvFolderType_detachedauthorizations = ddvFolderTypeSchema.get("detachedauthorizations");
		ddvFolderType_detachedauthorizations.setSystemReserved(true);
		ddvFolderType_detachedauthorizations.setUndeletable(true);
		MetadataBuilder ddvFolderType_followers = ddvFolderTypeSchema.get("followers");
		ddvFolderType_followers.setMultivalue(true);
		ddvFolderType_followers.setSystemReserved(true);
		ddvFolderType_followers.setUndeletable(true);
		ddvFolderType_followers.setSearchable(true);
		MetadataBuilder ddvFolderType_id = ddvFolderTypeSchema.get("id");
		ddvFolderType_id.setDefaultRequirement(true);
		ddvFolderType_id.setSystemReserved(true);
		ddvFolderType_id.setUndeletable(true);
		ddvFolderType_id.setSearchable(true);
		ddvFolderType_id.setSortable(true);
		ddvFolderType_id.setUniqueValue(true);
		ddvFolderType_id.setUnmodifiable(true);
		MetadataBuilder ddvFolderType_inheritedauthorizations = ddvFolderTypeSchema.get("inheritedauthorizations");
		ddvFolderType_inheritedauthorizations.setMultivalue(true);
		ddvFolderType_inheritedauthorizations.setSystemReserved(true);
		ddvFolderType_inheritedauthorizations.setUndeletable(true);
		MetadataBuilder ddvFolderType_legacyIdentifier = ddvFolderTypeSchema.get("legacyIdentifier");
		ddvFolderType_legacyIdentifier.setDefaultRequirement(true);
		ddvFolderType_legacyIdentifier.setSystemReserved(true);
		ddvFolderType_legacyIdentifier.setUndeletable(true);
		ddvFolderType_legacyIdentifier.setSearchable(true);
		ddvFolderType_legacyIdentifier.setUniqueValue(true);
		ddvFolderType_legacyIdentifier.setUnmodifiable(true);
		MetadataBuilder ddvFolderType_linkedSchema = ddvFolderTypeSchema.create("linkedSchema").setType(MetadataValueType.STRING);
		MetadataBuilder ddvFolderType_manualTokens = ddvFolderTypeSchema.get("manualTokens");
		ddvFolderType_manualTokens.setMultivalue(true);
		ddvFolderType_manualTokens.setSystemReserved(true);
		ddvFolderType_manualTokens.setUndeletable(true);
		ddvFolderType_manualTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder ddvFolderType_markedForPreviewConversion = ddvFolderTypeSchema.get("markedForPreviewConversion");
		ddvFolderType_markedForPreviewConversion.setSystemReserved(true);
		ddvFolderType_markedForPreviewConversion.setUndeletable(true);
		MetadataBuilder ddvFolderType_modifiedBy = ddvFolderTypeSchema.get("modifiedBy");
		ddvFolderType_modifiedBy.setSystemReserved(true);
		ddvFolderType_modifiedBy.setUndeletable(true);
		MetadataBuilder ddvFolderType_modifiedOn = ddvFolderTypeSchema.get("modifiedOn");
		ddvFolderType_modifiedOn.setSystemReserved(true);
		ddvFolderType_modifiedOn.setUndeletable(true);
		ddvFolderType_modifiedOn.setSortable(true);
		MetadataBuilder ddvFolderType_parentpath = ddvFolderTypeSchema.get("parentpath");
		ddvFolderType_parentpath.setMultivalue(true);
		ddvFolderType_parentpath.setSystemReserved(true);
		ddvFolderType_parentpath.setUndeletable(true);
		MetadataBuilder ddvFolderType_path = ddvFolderTypeSchema.get("path");
		ddvFolderType_path.setMultivalue(true);
		ddvFolderType_path.setSystemReserved(true);
		ddvFolderType_path.setUndeletable(true);
		MetadataBuilder ddvFolderType_pathParts = ddvFolderTypeSchema.get("pathParts");
		ddvFolderType_pathParts.setMultivalue(true);
		ddvFolderType_pathParts.setSystemReserved(true);
		ddvFolderType_pathParts.setUndeletable(true);
		MetadataBuilder ddvFolderType_principalpath = ddvFolderTypeSchema.get("principalpath");
		ddvFolderType_principalpath.setSystemReserved(true);
		ddvFolderType_principalpath.setUndeletable(true);
		MetadataBuilder ddvFolderType_removedauthorizations = ddvFolderTypeSchema.get("removedauthorizations");
		ddvFolderType_removedauthorizations.setMultivalue(true);
		ddvFolderType_removedauthorizations.setSystemReserved(true);
		ddvFolderType_removedauthorizations.setUndeletable(true);
		MetadataBuilder ddvFolderType_schema = ddvFolderTypeSchema.get("schema");
		ddvFolderType_schema.setDefaultRequirement(true);
		ddvFolderType_schema.setSystemReserved(true);
		ddvFolderType_schema.setUndeletable(true);
		MetadataBuilder ddvFolderType_searchable = ddvFolderTypeSchema.get("searchable");
		ddvFolderType_searchable.setSystemReserved(true);
		ddvFolderType_searchable.setUndeletable(true);
		MetadataBuilder ddvFolderType_shareDenyTokens = ddvFolderTypeSchema.get("shareDenyTokens");
		ddvFolderType_shareDenyTokens.setMultivalue(true);
		ddvFolderType_shareDenyTokens.setSystemReserved(true);
		ddvFolderType_shareDenyTokens.setUndeletable(true);
		ddvFolderType_shareDenyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder ddvFolderType_shareTokens = ddvFolderTypeSchema.get("shareTokens");
		ddvFolderType_shareTokens.setMultivalue(true);
		ddvFolderType_shareTokens.setSystemReserved(true);
		ddvFolderType_shareTokens.setUndeletable(true);
		ddvFolderType_shareTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder ddvFolderType_title = ddvFolderTypeSchema.get("title");
		ddvFolderType_title.setDefaultRequirement(true);
		ddvFolderType_title.setUndeletable(true);
		ddvFolderType_title.setSchemaAutocomplete(true);
		ddvFolderType_title.setSearchable(true);
		ddvFolderType_title.setUniqueValue(true);
		MetadataBuilder ddvFolderType_tokens = ddvFolderTypeSchema.get("tokens");
		ddvFolderType_tokens.setMultivalue(true);
		ddvFolderType_tokens.setSystemReserved(true);
		ddvFolderType_tokens.setUndeletable(true);
		MetadataBuilder ddvFolderType_visibleInTrees = ddvFolderTypeSchema.get("visibleInTrees");
		ddvFolderType_visibleInTrees.setSystemReserved(true);
		ddvFolderType_visibleInTrees.setUndeletable(true);
		MetadataBuilder ddvMediumType_allauthorizations = ddvMediumTypeSchema.get("allauthorizations");
		ddvMediumType_allauthorizations.setMultivalue(true);
		ddvMediumType_allauthorizations.setSystemReserved(true);
		ddvMediumType_allauthorizations.setUndeletable(true);
		MetadataBuilder ddvMediumType_analogical = ddvMediumTypeSchema.create("analogical").setType(MetadataValueType.BOOLEAN);
		ddvMediumType_analogical.setDefaultRequirement(true);
		MetadataBuilder ddvMediumType_authorizations = ddvMediumTypeSchema.get("authorizations");
		ddvMediumType_authorizations.setMultivalue(true);
		ddvMediumType_authorizations.setSystemReserved(true);
		ddvMediumType_authorizations.setUndeletable(true);
		MetadataBuilder ddvMediumType_code = ddvMediumTypeSchema.create("code").setType(MetadataValueType.STRING);
		ddvMediumType_code.setDefaultRequirement(true);
		ddvMediumType_code.setUndeletable(true);
		ddvMediumType_code.setSchemaAutocomplete(true);
		ddvMediumType_code.setSearchable(true);
		ddvMediumType_code.setUniqueValue(true);
		MetadataBuilder ddvMediumType_comments = ddvMediumTypeSchema.create("comments").setType(MetadataValueType.STRUCTURE);
		ddvMediumType_comments.setMultivalue(true);
		ddvMediumType_comments.defineStructureFactory(CommentFactory.class);
		MetadataBuilder ddvMediumType_createdBy = ddvMediumTypeSchema.get("createdBy");
		ddvMediumType_createdBy.setSystemReserved(true);
		ddvMediumType_createdBy.setUndeletable(true);
		MetadataBuilder ddvMediumType_createdOn = ddvMediumTypeSchema.get("createdOn");
		ddvMediumType_createdOn.setSystemReserved(true);
		ddvMediumType_createdOn.setUndeletable(true);
		ddvMediumType_createdOn.setSortable(true);
		MetadataBuilder ddvMediumType_deleted = ddvMediumTypeSchema.get("deleted");
		ddvMediumType_deleted.setSystemReserved(true);
		ddvMediumType_deleted.setUndeletable(true);
		MetadataBuilder ddvMediumType_denyTokens = ddvMediumTypeSchema.get("denyTokens");
		ddvMediumType_denyTokens.setMultivalue(true);
		ddvMediumType_denyTokens.setSystemReserved(true);
		ddvMediumType_denyTokens.setUndeletable(true);
		ddvMediumType_denyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder ddvMediumType_description = ddvMediumTypeSchema.create("description").setType(MetadataValueType.TEXT);
		ddvMediumType_description.setUndeletable(true);
		ddvMediumType_description.setEssentialInSummary(true);
		ddvMediumType_description.setSearchable(true);
		MetadataBuilder ddvMediumType_detachedauthorizations = ddvMediumTypeSchema.get("detachedauthorizations");
		ddvMediumType_detachedauthorizations.setSystemReserved(true);
		ddvMediumType_detachedauthorizations.setUndeletable(true);
		MetadataBuilder ddvMediumType_followers = ddvMediumTypeSchema.get("followers");
		ddvMediumType_followers.setMultivalue(true);
		ddvMediumType_followers.setSystemReserved(true);
		ddvMediumType_followers.setUndeletable(true);
		ddvMediumType_followers.setSearchable(true);
		MetadataBuilder ddvMediumType_id = ddvMediumTypeSchema.get("id");
		ddvMediumType_id.setDefaultRequirement(true);
		ddvMediumType_id.setSystemReserved(true);
		ddvMediumType_id.setUndeletable(true);
		ddvMediumType_id.setSearchable(true);
		ddvMediumType_id.setSortable(true);
		ddvMediumType_id.setUniqueValue(true);
		ddvMediumType_id.setUnmodifiable(true);
		MetadataBuilder ddvMediumType_inheritedauthorizations = ddvMediumTypeSchema.get("inheritedauthorizations");
		ddvMediumType_inheritedauthorizations.setMultivalue(true);
		ddvMediumType_inheritedauthorizations.setSystemReserved(true);
		ddvMediumType_inheritedauthorizations.setUndeletable(true);
		MetadataBuilder ddvMediumType_legacyIdentifier = ddvMediumTypeSchema.get("legacyIdentifier");
		ddvMediumType_legacyIdentifier.setDefaultRequirement(true);
		ddvMediumType_legacyIdentifier.setSystemReserved(true);
		ddvMediumType_legacyIdentifier.setUndeletable(true);
		ddvMediumType_legacyIdentifier.setSearchable(true);
		ddvMediumType_legacyIdentifier.setUniqueValue(true);
		ddvMediumType_legacyIdentifier.setUnmodifiable(true);
		MetadataBuilder ddvMediumType_manualTokens = ddvMediumTypeSchema.get("manualTokens");
		ddvMediumType_manualTokens.setMultivalue(true);
		ddvMediumType_manualTokens.setSystemReserved(true);
		ddvMediumType_manualTokens.setUndeletable(true);
		ddvMediumType_manualTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder ddvMediumType_markedForPreviewConversion = ddvMediumTypeSchema.get("markedForPreviewConversion");
		ddvMediumType_markedForPreviewConversion.setSystemReserved(true);
		ddvMediumType_markedForPreviewConversion.setUndeletable(true);
		MetadataBuilder ddvMediumType_modifiedBy = ddvMediumTypeSchema.get("modifiedBy");
		ddvMediumType_modifiedBy.setSystemReserved(true);
		ddvMediumType_modifiedBy.setUndeletable(true);
		MetadataBuilder ddvMediumType_modifiedOn = ddvMediumTypeSchema.get("modifiedOn");
		ddvMediumType_modifiedOn.setSystemReserved(true);
		ddvMediumType_modifiedOn.setUndeletable(true);
		ddvMediumType_modifiedOn.setSortable(true);
		MetadataBuilder ddvMediumType_parentpath = ddvMediumTypeSchema.get("parentpath");
		ddvMediumType_parentpath.setMultivalue(true);
		ddvMediumType_parentpath.setSystemReserved(true);
		ddvMediumType_parentpath.setUndeletable(true);
		MetadataBuilder ddvMediumType_path = ddvMediumTypeSchema.get("path");
		ddvMediumType_path.setMultivalue(true);
		ddvMediumType_path.setSystemReserved(true);
		ddvMediumType_path.setUndeletable(true);
		MetadataBuilder ddvMediumType_pathParts = ddvMediumTypeSchema.get("pathParts");
		ddvMediumType_pathParts.setMultivalue(true);
		ddvMediumType_pathParts.setSystemReserved(true);
		ddvMediumType_pathParts.setUndeletable(true);
		MetadataBuilder ddvMediumType_principalpath = ddvMediumTypeSchema.get("principalpath");
		ddvMediumType_principalpath.setSystemReserved(true);
		ddvMediumType_principalpath.setUndeletable(true);
		MetadataBuilder ddvMediumType_removedauthorizations = ddvMediumTypeSchema.get("removedauthorizations");
		ddvMediumType_removedauthorizations.setMultivalue(true);
		ddvMediumType_removedauthorizations.setSystemReserved(true);
		ddvMediumType_removedauthorizations.setUndeletable(true);
		MetadataBuilder ddvMediumType_schema = ddvMediumTypeSchema.get("schema");
		ddvMediumType_schema.setDefaultRequirement(true);
		ddvMediumType_schema.setSystemReserved(true);
		ddvMediumType_schema.setUndeletable(true);
		MetadataBuilder ddvMediumType_searchable = ddvMediumTypeSchema.get("searchable");
		ddvMediumType_searchable.setSystemReserved(true);
		ddvMediumType_searchable.setUndeletable(true);
		MetadataBuilder ddvMediumType_shareDenyTokens = ddvMediumTypeSchema.get("shareDenyTokens");
		ddvMediumType_shareDenyTokens.setMultivalue(true);
		ddvMediumType_shareDenyTokens.setSystemReserved(true);
		ddvMediumType_shareDenyTokens.setUndeletable(true);
		ddvMediumType_shareDenyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder ddvMediumType_shareTokens = ddvMediumTypeSchema.get("shareTokens");
		ddvMediumType_shareTokens.setMultivalue(true);
		ddvMediumType_shareTokens.setSystemReserved(true);
		ddvMediumType_shareTokens.setUndeletable(true);
		ddvMediumType_shareTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder ddvMediumType_title = ddvMediumTypeSchema.get("title");
		ddvMediumType_title.setDefaultRequirement(true);
		ddvMediumType_title.setUndeletable(true);
		ddvMediumType_title.setSchemaAutocomplete(true);
		ddvMediumType_title.setSearchable(true);
		ddvMediumType_title.setUniqueValue(true);
		MetadataBuilder ddvMediumType_tokens = ddvMediumTypeSchema.get("tokens");
		ddvMediumType_tokens.setMultivalue(true);
		ddvMediumType_tokens.setSystemReserved(true);
		ddvMediumType_tokens.setUndeletable(true);
		MetadataBuilder ddvMediumType_visibleInTrees = ddvMediumTypeSchema.get("visibleInTrees");
		ddvMediumType_visibleInTrees.setSystemReserved(true);
		ddvMediumType_visibleInTrees.setUndeletable(true);
		MetadataBuilder ddvStorageSpaceType_allauthorizations = ddvStorageSpaceTypeSchema.get("allauthorizations");
		ddvStorageSpaceType_allauthorizations.setMultivalue(true);
		ddvStorageSpaceType_allauthorizations.setSystemReserved(true);
		ddvStorageSpaceType_allauthorizations.setUndeletable(true);
		MetadataBuilder ddvStorageSpaceType_authorizations = ddvStorageSpaceTypeSchema.get("authorizations");
		ddvStorageSpaceType_authorizations.setMultivalue(true);
		ddvStorageSpaceType_authorizations.setSystemReserved(true);
		ddvStorageSpaceType_authorizations.setUndeletable(true);
		MetadataBuilder ddvStorageSpaceType_code = ddvStorageSpaceTypeSchema.create("code").setType(MetadataValueType.STRING);
		ddvStorageSpaceType_code.setDefaultRequirement(true);
		ddvStorageSpaceType_code.setUndeletable(true);
		ddvStorageSpaceType_code.setSchemaAutocomplete(true);
		ddvStorageSpaceType_code.setSearchable(true);
		ddvStorageSpaceType_code.setUniqueValue(true);
		MetadataBuilder ddvStorageSpaceType_comments = ddvStorageSpaceTypeSchema.create("comments")
				.setType(MetadataValueType.STRUCTURE);
		ddvStorageSpaceType_comments.setMultivalue(true);
		ddvStorageSpaceType_comments.defineStructureFactory(CommentFactory.class);
		MetadataBuilder ddvStorageSpaceType_createdBy = ddvStorageSpaceTypeSchema.get("createdBy");
		ddvStorageSpaceType_createdBy.setSystemReserved(true);
		ddvStorageSpaceType_createdBy.setUndeletable(true);
		MetadataBuilder ddvStorageSpaceType_createdOn = ddvStorageSpaceTypeSchema.get("createdOn");
		ddvStorageSpaceType_createdOn.setSystemReserved(true);
		ddvStorageSpaceType_createdOn.setUndeletable(true);
		ddvStorageSpaceType_createdOn.setSortable(true);
		MetadataBuilder ddvStorageSpaceType_deleted = ddvStorageSpaceTypeSchema.get("deleted");
		ddvStorageSpaceType_deleted.setSystemReserved(true);
		ddvStorageSpaceType_deleted.setUndeletable(true);
		MetadataBuilder ddvStorageSpaceType_denyTokens = ddvStorageSpaceTypeSchema.get("denyTokens");
		ddvStorageSpaceType_denyTokens.setMultivalue(true);
		ddvStorageSpaceType_denyTokens.setSystemReserved(true);
		ddvStorageSpaceType_denyTokens.setUndeletable(true);
		ddvStorageSpaceType_denyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder ddvStorageSpaceType_description = ddvStorageSpaceTypeSchema.create("description")
				.setType(MetadataValueType.TEXT);
		ddvStorageSpaceType_description.setUndeletable(true);
		ddvStorageSpaceType_description.setEssentialInSummary(true);
		ddvStorageSpaceType_description.setSearchable(true);
		MetadataBuilder ddvStorageSpaceType_detachedauthorizations = ddvStorageSpaceTypeSchema.get("detachedauthorizations");
		ddvStorageSpaceType_detachedauthorizations.setSystemReserved(true);
		ddvStorageSpaceType_detachedauthorizations.setUndeletable(true);
		MetadataBuilder ddvStorageSpaceType_followers = ddvStorageSpaceTypeSchema.get("followers");
		ddvStorageSpaceType_followers.setMultivalue(true);
		ddvStorageSpaceType_followers.setSystemReserved(true);
		ddvStorageSpaceType_followers.setUndeletable(true);
		ddvStorageSpaceType_followers.setSearchable(true);
		MetadataBuilder ddvStorageSpaceType_id = ddvStorageSpaceTypeSchema.get("id");
		ddvStorageSpaceType_id.setDefaultRequirement(true);
		ddvStorageSpaceType_id.setSystemReserved(true);
		ddvStorageSpaceType_id.setUndeletable(true);
		ddvStorageSpaceType_id.setSearchable(true);
		ddvStorageSpaceType_id.setSortable(true);
		ddvStorageSpaceType_id.setUniqueValue(true);
		ddvStorageSpaceType_id.setUnmodifiable(true);
		MetadataBuilder ddvStorageSpaceType_inheritedauthorizations = ddvStorageSpaceTypeSchema.get("inheritedauthorizations");
		ddvStorageSpaceType_inheritedauthorizations.setMultivalue(true);
		ddvStorageSpaceType_inheritedauthorizations.setSystemReserved(true);
		ddvStorageSpaceType_inheritedauthorizations.setUndeletable(true);
		MetadataBuilder ddvStorageSpaceType_legacyIdentifier = ddvStorageSpaceTypeSchema.get("legacyIdentifier");
		ddvStorageSpaceType_legacyIdentifier.setDefaultRequirement(true);
		ddvStorageSpaceType_legacyIdentifier.setSystemReserved(true);
		ddvStorageSpaceType_legacyIdentifier.setUndeletable(true);
		ddvStorageSpaceType_legacyIdentifier.setSearchable(true);
		ddvStorageSpaceType_legacyIdentifier.setUniqueValue(true);
		ddvStorageSpaceType_legacyIdentifier.setUnmodifiable(true);
		MetadataBuilder ddvStorageSpaceType_linkedSchema = ddvStorageSpaceTypeSchema.create("linkedSchema")
				.setType(MetadataValueType.STRING);
		MetadataBuilder ddvStorageSpaceType_manualTokens = ddvStorageSpaceTypeSchema.get("manualTokens");
		ddvStorageSpaceType_manualTokens.setMultivalue(true);
		ddvStorageSpaceType_manualTokens.setSystemReserved(true);
		ddvStorageSpaceType_manualTokens.setUndeletable(true);
		ddvStorageSpaceType_manualTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder ddvStorageSpaceType_markedForPreviewConversion = ddvStorageSpaceTypeSchema
				.get("markedForPreviewConversion");
		ddvStorageSpaceType_markedForPreviewConversion.setSystemReserved(true);
		ddvStorageSpaceType_markedForPreviewConversion.setUndeletable(true);
		MetadataBuilder ddvStorageSpaceType_modifiedBy = ddvStorageSpaceTypeSchema.get("modifiedBy");
		ddvStorageSpaceType_modifiedBy.setSystemReserved(true);
		ddvStorageSpaceType_modifiedBy.setUndeletable(true);
		MetadataBuilder ddvStorageSpaceType_modifiedOn = ddvStorageSpaceTypeSchema.get("modifiedOn");
		ddvStorageSpaceType_modifiedOn.setSystemReserved(true);
		ddvStorageSpaceType_modifiedOn.setUndeletable(true);
		ddvStorageSpaceType_modifiedOn.setSortable(true);
		MetadataBuilder ddvStorageSpaceType_parentpath = ddvStorageSpaceTypeSchema.get("parentpath");
		ddvStorageSpaceType_parentpath.setMultivalue(true);
		ddvStorageSpaceType_parentpath.setSystemReserved(true);
		ddvStorageSpaceType_parentpath.setUndeletable(true);
		MetadataBuilder ddvStorageSpaceType_path = ddvStorageSpaceTypeSchema.get("path");
		ddvStorageSpaceType_path.setMultivalue(true);
		ddvStorageSpaceType_path.setSystemReserved(true);
		ddvStorageSpaceType_path.setUndeletable(true);
		MetadataBuilder ddvStorageSpaceType_pathParts = ddvStorageSpaceTypeSchema.get("pathParts");
		ddvStorageSpaceType_pathParts.setMultivalue(true);
		ddvStorageSpaceType_pathParts.setSystemReserved(true);
		ddvStorageSpaceType_pathParts.setUndeletable(true);
		MetadataBuilder ddvStorageSpaceType_principalpath = ddvStorageSpaceTypeSchema.get("principalpath");
		ddvStorageSpaceType_principalpath.setSystemReserved(true);
		ddvStorageSpaceType_principalpath.setUndeletable(true);
		MetadataBuilder ddvStorageSpaceType_removedauthorizations = ddvStorageSpaceTypeSchema.get("removedauthorizations");
		ddvStorageSpaceType_removedauthorizations.setMultivalue(true);
		ddvStorageSpaceType_removedauthorizations.setSystemReserved(true);
		ddvStorageSpaceType_removedauthorizations.setUndeletable(true);
		MetadataBuilder ddvStorageSpaceType_schema = ddvStorageSpaceTypeSchema.get("schema");
		ddvStorageSpaceType_schema.setDefaultRequirement(true);
		ddvStorageSpaceType_schema.setSystemReserved(true);
		ddvStorageSpaceType_schema.setUndeletable(true);
		MetadataBuilder ddvStorageSpaceType_searchable = ddvStorageSpaceTypeSchema.get("searchable");
		ddvStorageSpaceType_searchable.setSystemReserved(true);
		ddvStorageSpaceType_searchable.setUndeletable(true);
		MetadataBuilder ddvStorageSpaceType_shareDenyTokens = ddvStorageSpaceTypeSchema.get("shareDenyTokens");
		ddvStorageSpaceType_shareDenyTokens.setMultivalue(true);
		ddvStorageSpaceType_shareDenyTokens.setSystemReserved(true);
		ddvStorageSpaceType_shareDenyTokens.setUndeletable(true);
		ddvStorageSpaceType_shareDenyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder ddvStorageSpaceType_shareTokens = ddvStorageSpaceTypeSchema.get("shareTokens");
		ddvStorageSpaceType_shareTokens.setMultivalue(true);
		ddvStorageSpaceType_shareTokens.setSystemReserved(true);
		ddvStorageSpaceType_shareTokens.setUndeletable(true);
		ddvStorageSpaceType_shareTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder ddvStorageSpaceType_title = ddvStorageSpaceTypeSchema.get("title");
		ddvStorageSpaceType_title.setDefaultRequirement(true);
		ddvStorageSpaceType_title.setUndeletable(true);
		ddvStorageSpaceType_title.setSchemaAutocomplete(true);
		ddvStorageSpaceType_title.setSearchable(true);
		ddvStorageSpaceType_title.setUniqueValue(true);
		MetadataBuilder ddvStorageSpaceType_tokens = ddvStorageSpaceTypeSchema.get("tokens");
		ddvStorageSpaceType_tokens.setMultivalue(true);
		ddvStorageSpaceType_tokens.setSystemReserved(true);
		ddvStorageSpaceType_tokens.setUndeletable(true);
		MetadataBuilder ddvStorageSpaceType_visibleInTrees = ddvStorageSpaceTypeSchema.get("visibleInTrees");
		ddvStorageSpaceType_visibleInTrees.setSystemReserved(true);
		ddvStorageSpaceType_visibleInTrees.setUndeletable(true);
		MetadataBuilder ddvVariablePeriod_allauthorizations = ddvVariablePeriodSchema.get("allauthorizations");
		ddvVariablePeriod_allauthorizations.setMultivalue(true);
		ddvVariablePeriod_allauthorizations.setSystemReserved(true);
		ddvVariablePeriod_allauthorizations.setUndeletable(true);
		MetadataBuilder ddvVariablePeriod_authorizations = ddvVariablePeriodSchema.get("authorizations");
		ddvVariablePeriod_authorizations.setMultivalue(true);
		ddvVariablePeriod_authorizations.setSystemReserved(true);
		ddvVariablePeriod_authorizations.setUndeletable(true);
		MetadataBuilder ddvVariablePeriod_code = ddvVariablePeriodSchema.create("code").setType(MetadataValueType.STRING);
		ddvVariablePeriod_code.setDefaultRequirement(true);
		ddvVariablePeriod_code.setUndeletable(true);
		ddvVariablePeriod_code.setSchemaAutocomplete(true);
		ddvVariablePeriod_code.setSearchable(true);
		ddvVariablePeriod_code.setUniqueValue(true);
		ddvVariablePeriod_code.setUnmodifiable(true);
		ddvVariablePeriod_code.defineValidators().add(IntegerStringValidator.class);
		MetadataBuilder ddvVariablePeriod_comments = ddvVariablePeriodSchema.create("comments")
				.setType(MetadataValueType.STRUCTURE);
		ddvVariablePeriod_comments.setMultivalue(true);
		ddvVariablePeriod_comments.defineStructureFactory(CommentFactory.class);
		MetadataBuilder ddvVariablePeriod_createdBy = ddvVariablePeriodSchema.get("createdBy");
		ddvVariablePeriod_createdBy.setSystemReserved(true);
		ddvVariablePeriod_createdBy.setUndeletable(true);
		MetadataBuilder ddvVariablePeriod_createdOn = ddvVariablePeriodSchema.get("createdOn");
		ddvVariablePeriod_createdOn.setSystemReserved(true);
		ddvVariablePeriod_createdOn.setUndeletable(true);
		ddvVariablePeriod_createdOn.setSortable(true);
		MetadataBuilder ddvVariablePeriod_deleted = ddvVariablePeriodSchema.get("deleted");
		ddvVariablePeriod_deleted.setSystemReserved(true);
		ddvVariablePeriod_deleted.setUndeletable(true);
		MetadataBuilder ddvVariablePeriod_denyTokens = ddvVariablePeriodSchema.get("denyTokens");
		ddvVariablePeriod_denyTokens.setMultivalue(true);
		ddvVariablePeriod_denyTokens.setSystemReserved(true);
		ddvVariablePeriod_denyTokens.setUndeletable(true);
		ddvVariablePeriod_denyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder ddvVariablePeriod_description = ddvVariablePeriodSchema.create("description")
				.setType(MetadataValueType.TEXT);
		ddvVariablePeriod_description.setUndeletable(true);
		ddvVariablePeriod_description.setEssentialInSummary(true);
		ddvVariablePeriod_description.setSearchable(true);
		MetadataBuilder ddvVariablePeriod_detachedauthorizations = ddvVariablePeriodSchema.get("detachedauthorizations");
		ddvVariablePeriod_detachedauthorizations.setSystemReserved(true);
		ddvVariablePeriod_detachedauthorizations.setUndeletable(true);
		MetadataBuilder ddvVariablePeriod_followers = ddvVariablePeriodSchema.get("followers");
		ddvVariablePeriod_followers.setMultivalue(true);
		ddvVariablePeriod_followers.setSystemReserved(true);
		ddvVariablePeriod_followers.setUndeletable(true);
		ddvVariablePeriod_followers.setSearchable(true);
		MetadataBuilder ddvVariablePeriod_id = ddvVariablePeriodSchema.get("id");
		ddvVariablePeriod_id.setDefaultRequirement(true);
		ddvVariablePeriod_id.setSystemReserved(true);
		ddvVariablePeriod_id.setUndeletable(true);
		ddvVariablePeriod_id.setSearchable(true);
		ddvVariablePeriod_id.setSortable(true);
		ddvVariablePeriod_id.setUniqueValue(true);
		ddvVariablePeriod_id.setUnmodifiable(true);
		MetadataBuilder ddvVariablePeriod_inheritedauthorizations = ddvVariablePeriodSchema.get("inheritedauthorizations");
		ddvVariablePeriod_inheritedauthorizations.setMultivalue(true);
		ddvVariablePeriod_inheritedauthorizations.setSystemReserved(true);
		ddvVariablePeriod_inheritedauthorizations.setUndeletable(true);
		MetadataBuilder ddvVariablePeriod_legacyIdentifier = ddvVariablePeriodSchema.get("legacyIdentifier");
		ddvVariablePeriod_legacyIdentifier.setDefaultRequirement(true);
		ddvVariablePeriod_legacyIdentifier.setSystemReserved(true);
		ddvVariablePeriod_legacyIdentifier.setUndeletable(true);
		ddvVariablePeriod_legacyIdentifier.setSearchable(true);
		ddvVariablePeriod_legacyIdentifier.setUniqueValue(true);
		ddvVariablePeriod_legacyIdentifier.setUnmodifiable(true);
		MetadataBuilder ddvVariablePeriod_manualTokens = ddvVariablePeriodSchema.get("manualTokens");
		ddvVariablePeriod_manualTokens.setMultivalue(true);
		ddvVariablePeriod_manualTokens.setSystemReserved(true);
		ddvVariablePeriod_manualTokens.setUndeletable(true);
		ddvVariablePeriod_manualTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder ddvVariablePeriod_markedForPreviewConversion = ddvVariablePeriodSchema.get("markedForPreviewConversion");
		ddvVariablePeriod_markedForPreviewConversion.setSystemReserved(true);
		ddvVariablePeriod_markedForPreviewConversion.setUndeletable(true);
		MetadataBuilder ddvVariablePeriod_modifiedBy = ddvVariablePeriodSchema.get("modifiedBy");
		ddvVariablePeriod_modifiedBy.setSystemReserved(true);
		ddvVariablePeriod_modifiedBy.setUndeletable(true);
		MetadataBuilder ddvVariablePeriod_modifiedOn = ddvVariablePeriodSchema.get("modifiedOn");
		ddvVariablePeriod_modifiedOn.setSystemReserved(true);
		ddvVariablePeriod_modifiedOn.setUndeletable(true);
		ddvVariablePeriod_modifiedOn.setSortable(true);
		MetadataBuilder ddvVariablePeriod_parentpath = ddvVariablePeriodSchema.get("parentpath");
		ddvVariablePeriod_parentpath.setMultivalue(true);
		ddvVariablePeriod_parentpath.setSystemReserved(true);
		ddvVariablePeriod_parentpath.setUndeletable(true);
		MetadataBuilder ddvVariablePeriod_path = ddvVariablePeriodSchema.get("path");
		ddvVariablePeriod_path.setMultivalue(true);
		ddvVariablePeriod_path.setSystemReserved(true);
		ddvVariablePeriod_path.setUndeletable(true);
		MetadataBuilder ddvVariablePeriod_pathParts = ddvVariablePeriodSchema.get("pathParts");
		ddvVariablePeriod_pathParts.setMultivalue(true);
		ddvVariablePeriod_pathParts.setSystemReserved(true);
		ddvVariablePeriod_pathParts.setUndeletable(true);
		MetadataBuilder ddvVariablePeriod_principalpath = ddvVariablePeriodSchema.get("principalpath");
		ddvVariablePeriod_principalpath.setSystemReserved(true);
		ddvVariablePeriod_principalpath.setUndeletable(true);
		MetadataBuilder ddvVariablePeriod_removedauthorizations = ddvVariablePeriodSchema.get("removedauthorizations");
		ddvVariablePeriod_removedauthorizations.setMultivalue(true);
		ddvVariablePeriod_removedauthorizations.setSystemReserved(true);
		ddvVariablePeriod_removedauthorizations.setUndeletable(true);
		MetadataBuilder ddvVariablePeriod_schema = ddvVariablePeriodSchema.get("schema");
		ddvVariablePeriod_schema.setDefaultRequirement(true);
		ddvVariablePeriod_schema.setSystemReserved(true);
		ddvVariablePeriod_schema.setUndeletable(true);
		MetadataBuilder ddvVariablePeriod_searchable = ddvVariablePeriodSchema.get("searchable");
		ddvVariablePeriod_searchable.setSystemReserved(true);
		ddvVariablePeriod_searchable.setUndeletable(true);
		MetadataBuilder ddvVariablePeriod_shareDenyTokens = ddvVariablePeriodSchema.get("shareDenyTokens");
		ddvVariablePeriod_shareDenyTokens.setMultivalue(true);
		ddvVariablePeriod_shareDenyTokens.setSystemReserved(true);
		ddvVariablePeriod_shareDenyTokens.setUndeletable(true);
		ddvVariablePeriod_shareDenyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder ddvVariablePeriod_shareTokens = ddvVariablePeriodSchema.get("shareTokens");
		ddvVariablePeriod_shareTokens.setMultivalue(true);
		ddvVariablePeriod_shareTokens.setSystemReserved(true);
		ddvVariablePeriod_shareTokens.setUndeletable(true);
		ddvVariablePeriod_shareTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder ddvVariablePeriod_title = ddvVariablePeriodSchema.get("title");
		ddvVariablePeriod_title.setDefaultRequirement(true);
		ddvVariablePeriod_title.setUndeletable(true);
		ddvVariablePeriod_title.setSchemaAutocomplete(true);
		ddvVariablePeriod_title.setSearchable(true);
		ddvVariablePeriod_title.setUniqueValue(true);
		MetadataBuilder ddvVariablePeriod_tokens = ddvVariablePeriodSchema.get("tokens");
		ddvVariablePeriod_tokens.setMultivalue(true);
		ddvVariablePeriod_tokens.setSystemReserved(true);
		ddvVariablePeriod_tokens.setUndeletable(true);
		MetadataBuilder ddvVariablePeriod_visibleInTrees = ddvVariablePeriodSchema.get("visibleInTrees");
		ddvVariablePeriod_visibleInTrees.setSystemReserved(true);
		ddvVariablePeriod_visibleInTrees.setUndeletable(true);
		MetadataBuilder decommissioningList_administrativeUnit = decommissioningListSchema.create("administrativeUnit")
				.setType(MetadataValueType.REFERENCE);
		decommissioningList_administrativeUnit.setUndeletable(true);
		decommissioningList_administrativeUnit.setEssential(true);
		decommissioningList_administrativeUnit.defineReferencesTo(administrativeUnitSchemaType);
		MetadataBuilder decommissioningList_allauthorizations = decommissioningListSchema.get("allauthorizations");
		decommissioningList_allauthorizations.setMultivalue(true);
		decommissioningList_allauthorizations.setSystemReserved(true);
		decommissioningList_allauthorizations.setUndeletable(true);
		decommissioningList_allauthorizations.setEssential(true);
		MetadataBuilder decommissioningList_analogicalMedium = decommissioningListSchema.create("analogicalMedium")
				.setType(MetadataValueType.BOOLEAN);
		decommissioningList_analogicalMedium.setUndeletable(true);
		decommissioningList_analogicalMedium.setEssential(true);
		MetadataBuilder decommissioningList_approvalDate = decommissioningListSchema.create("approvalDate")
				.setType(MetadataValueType.DATE);
		decommissioningList_approvalDate.setUndeletable(true);
		decommissioningList_approvalDate.setEssential(true);
		MetadataBuilder decommissioningList_approvalRequest = decommissioningListSchema.create("approvalRequest")
				.setType(MetadataValueType.REFERENCE);
		decommissioningList_approvalRequest.setUndeletable(true);
		decommissioningList_approvalRequest.setEssential(true);
		decommissioningList_approvalRequest.defineReferencesTo(userSchemaType);
		MetadataBuilder decommissioningList_approvalRequestDate = decommissioningListSchema.create("approvalRequestDate")
				.setType(MetadataValueType.DATE);
		decommissioningList_approvalRequestDate.setUndeletable(true);
		decommissioningList_approvalRequestDate.setEssential(true);
		MetadataBuilder decommissioningList_approvalUser = decommissioningListSchema.create("approvalUser")
				.setType(MetadataValueType.REFERENCE);
		decommissioningList_approvalUser.setUndeletable(true);
		decommissioningList_approvalUser.setEssential(true);
		decommissioningList_approvalUser.defineReferencesTo(userSchemaType);
		MetadataBuilder decommissioningList_authorizations = decommissioningListSchema.get("authorizations");
		decommissioningList_authorizations.setMultivalue(true);
		decommissioningList_authorizations.setSystemReserved(true);
		decommissioningList_authorizations.setUndeletable(true);
		decommissioningList_authorizations.setEssential(true);
		MetadataBuilder decommissioningList_comments = decommissioningListSchema.create("comments")
				.setType(MetadataValueType.STRUCTURE);
		decommissioningList_comments.setMultivalue(true);
		decommissioningList_comments.setUndeletable(true);
		decommissioningList_comments.defineStructureFactory(CommentFactory.class);
		MetadataBuilder decommissioningList_containerDetails = decommissioningListSchema.create("containerDetails")
				.setType(MetadataValueType.STRUCTURE);
		decommissioningList_containerDetails.setMultivalue(true);
		decommissioningList_containerDetails.setUndeletable(true);
		decommissioningList_containerDetails.setEssential(true);
		decommissioningList_containerDetails.defineStructureFactory(DecomListContainerDetailFactory.class);
		MetadataBuilder decommissioningList_containers = decommissioningListSchema.create("containers")
				.setType(MetadataValueType.REFERENCE);
		decommissioningList_containers.setMultivalue(true);
		decommissioningList_containers.setUndeletable(true);
		decommissioningList_containers.setEssential(true);
		decommissioningList_containers.defineReferencesTo(containerRecordSchemaType);
		MetadataBuilder decommissioningList_createdBy = decommissioningListSchema.get("createdBy");
		decommissioningList_createdBy.setSystemReserved(true);
		decommissioningList_createdBy.setUndeletable(true);
		decommissioningList_createdBy.setEssential(true);
		MetadataBuilder decommissioningList_createdOn = decommissioningListSchema.get("createdOn");
		decommissioningList_createdOn.setSystemReserved(true);
		decommissioningList_createdOn.setUndeletable(true);
		decommissioningList_createdOn.setEssential(true);
		decommissioningList_createdOn.setSortable(true);
		MetadataBuilder decommissioningList_deleted = decommissioningListSchema.get("deleted");
		decommissioningList_deleted.setSystemReserved(true);
		decommissioningList_deleted.setUndeletable(true);
		decommissioningList_deleted.setEssential(true);
		MetadataBuilder decommissioningList_denyTokens = decommissioningListSchema.get("denyTokens");
		decommissioningList_denyTokens.setMultivalue(true);
		decommissioningList_denyTokens.setSystemReserved(true);
		decommissioningList_denyTokens.setUndeletable(true);
		decommissioningList_denyTokens.setEssential(true);
		decommissioningList_denyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder decommissioningList_description = decommissioningListSchema.create("description")
				.setType(MetadataValueType.TEXT);
		decommissioningList_description.setUndeletable(true);
		decommissioningList_description.setEssentialInSummary(true);
		decommissioningList_description.setSearchable(true);
		MetadataBuilder decommissioningList_detachedauthorizations = decommissioningListSchema.get("detachedauthorizations");
		decommissioningList_detachedauthorizations.setSystemReserved(true);
		decommissioningList_detachedauthorizations.setUndeletable(true);
		decommissioningList_detachedauthorizations.setEssential(true);
		MetadataBuilder decommissioningList_documents = decommissioningListSchema.create("documents")
				.setType(MetadataValueType.REFERENCE);
		decommissioningList_documents.setMultivalue(true);
		decommissioningList_documents.setUndeletable(true);
		decommissioningList_documents.defineReferencesTo(documentSchemaType);
		MetadataBuilder decommissioningList_documentsReportContent = decommissioningListSchema.create("documentsReportContent")
				.setType(MetadataValueType.CONTENT);
		decommissioningList_documentsReportContent.setUndeletable(true);
		decommissioningList_documentsReportContent.defineStructureFactory(ContentFactory.class);
		MetadataBuilder decommissioningList_electronicMedium = decommissioningListSchema.create("electronicMedium")
				.setType(MetadataValueType.BOOLEAN);
		decommissioningList_electronicMedium.setUndeletable(true);
		decommissioningList_electronicMedium.setEssential(true);
		MetadataBuilder decommissioningList_filingSpace = decommissioningListSchema.create("filingSpace")
				.setType(MetadataValueType.REFERENCE);
		decommissioningList_filingSpace.setUndeletable(true);
		decommissioningList_filingSpace.setEssential(true);
		decommissioningList_filingSpace.defineReferencesTo(filingSpaceSchemaType);
		MetadataBuilder decommissioningList_folderDetails = decommissioningListSchema.create("folderDetails")
				.setType(MetadataValueType.STRUCTURE);
		decommissioningList_folderDetails.setMultivalue(true);
		decommissioningList_folderDetails.setUndeletable(true);
		decommissioningList_folderDetails.setEssential(true);
		decommissioningList_folderDetails.defineStructureFactory(DecomListFolderDetailFactory.class);
		MetadataBuilder decommissioningList_folders = decommissioningListSchema.create("folders")
				.setType(MetadataValueType.REFERENCE);
		decommissioningList_folders.setMultivalue(true);
		decommissioningList_folders.setUndeletable(true);
		decommissioningList_folders.setEssential(true);
		decommissioningList_folders.defineReferencesTo(folderSchemaType);
		MetadataBuilder decommissioningList_foldersMediaTypes = decommissioningListSchema.create("foldersMediaTypes")
				.setType(MetadataValueType.ENUM);
		decommissioningList_foldersMediaTypes.setMultivalue(true);
		decommissioningList_foldersMediaTypes.setUndeletable(true);
		decommissioningList_foldersMediaTypes.setEssential(true);
		decommissioningList_foldersMediaTypes.defineAsEnum(FolderMediaType.class);
		MetadataBuilder decommissioningList_foldersReportContent = decommissioningListSchema.create("foldersReportContent")
				.setType(MetadataValueType.CONTENT);
		decommissioningList_foldersReportContent.setUndeletable(true);
		decommissioningList_foldersReportContent.defineStructureFactory(ContentFactory.class);
		MetadataBuilder decommissioningList_followers = decommissioningListSchema.get("followers");
		decommissioningList_followers.setMultivalue(true);
		decommissioningList_followers.setSystemReserved(true);
		decommissioningList_followers.setUndeletable(true);
		decommissioningList_followers.setEssential(true);
		decommissioningList_followers.setSearchable(true);
		MetadataBuilder decommissioningList_id = decommissioningListSchema.get("id");
		decommissioningList_id.setDefaultRequirement(true);
		decommissioningList_id.setSystemReserved(true);
		decommissioningList_id.setUndeletable(true);
		decommissioningList_id.setEssential(true);
		decommissioningList_id.setSearchable(true);
		decommissioningList_id.setSortable(true);
		decommissioningList_id.setUniqueValue(true);
		decommissioningList_id.setUnmodifiable(true);
		MetadataBuilder decommissioningList_inheritedauthorizations = decommissioningListSchema.get("inheritedauthorizations");
		decommissioningList_inheritedauthorizations.setMultivalue(true);
		decommissioningList_inheritedauthorizations.setSystemReserved(true);
		decommissioningList_inheritedauthorizations.setUndeletable(true);
		decommissioningList_inheritedauthorizations.setEssential(true);
		MetadataBuilder decommissioningList_legacyIdentifier = decommissioningListSchema.get("legacyIdentifier");
		decommissioningList_legacyIdentifier.setDefaultRequirement(true);
		decommissioningList_legacyIdentifier.setSystemReserved(true);
		decommissioningList_legacyIdentifier.setUndeletable(true);
		decommissioningList_legacyIdentifier.setEssential(true);
		decommissioningList_legacyIdentifier.setSearchable(true);
		decommissioningList_legacyIdentifier.setUniqueValue(true);
		decommissioningList_legacyIdentifier.setUnmodifiable(true);
		MetadataBuilder decommissioningList_manualTokens = decommissioningListSchema.get("manualTokens");
		decommissioningList_manualTokens.setMultivalue(true);
		decommissioningList_manualTokens.setSystemReserved(true);
		decommissioningList_manualTokens.setUndeletable(true);
		decommissioningList_manualTokens.setEssential(true);
		decommissioningList_manualTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder decommissioningList_markedForPreviewConversion = decommissioningListSchema
				.get("markedForPreviewConversion");
		decommissioningList_markedForPreviewConversion.setSystemReserved(true);
		decommissioningList_markedForPreviewConversion.setUndeletable(true);
		decommissioningList_markedForPreviewConversion.setEssential(true);
		MetadataBuilder decommissioningList_modifiedBy = decommissioningListSchema.get("modifiedBy");
		decommissioningList_modifiedBy.setSystemReserved(true);
		decommissioningList_modifiedBy.setUndeletable(true);
		decommissioningList_modifiedBy.setEssential(true);
		MetadataBuilder decommissioningList_modifiedOn = decommissioningListSchema.get("modifiedOn");
		decommissioningList_modifiedOn.setSystemReserved(true);
		decommissioningList_modifiedOn.setUndeletable(true);
		decommissioningList_modifiedOn.setEssential(true);
		decommissioningList_modifiedOn.setSortable(true);
		MetadataBuilder decommissioningList_originArchivisticStatus = decommissioningListSchema.create("originArchivisticStatus")
				.setType(MetadataValueType.ENUM);
		decommissioningList_originArchivisticStatus.setSystemReserved(true);
		decommissioningList_originArchivisticStatus.setUndeletable(true);
		decommissioningList_originArchivisticStatus.setEssential(true);
		decommissioningList_originArchivisticStatus.defineAsEnum(OriginStatus.class);
		MetadataBuilder decommissioningList_parentpath = decommissioningListSchema.get("parentpath");
		decommissioningList_parentpath.setMultivalue(true);
		decommissioningList_parentpath.setSystemReserved(true);
		decommissioningList_parentpath.setUndeletable(true);
		decommissioningList_parentpath.setEssential(true);
		MetadataBuilder decommissioningList_path = decommissioningListSchema.get("path");
		decommissioningList_path.setMultivalue(true);
		decommissioningList_path.setSystemReserved(true);
		decommissioningList_path.setUndeletable(true);
		decommissioningList_path.setEssential(true);
		MetadataBuilder decommissioningList_pathParts = decommissioningListSchema.get("pathParts");
		decommissioningList_pathParts.setMultivalue(true);
		decommissioningList_pathParts.setSystemReserved(true);
		decommissioningList_pathParts.setUndeletable(true);
		decommissioningList_pathParts.setEssential(true);
		MetadataBuilder decommissioningList_pendingValidations = decommissioningListSchema.create("pendingValidations")
				.setType(MetadataValueType.REFERENCE);
		decommissioningList_pendingValidations.setMultivalue(true);
		decommissioningList_pendingValidations.setUndeletable(true);
		decommissioningList_pendingValidations.defineReferencesTo(asList(userSchema));
		MetadataBuilder decommissioningList_principalpath = decommissioningListSchema.get("principalpath");
		decommissioningList_principalpath.setSystemReserved(true);
		decommissioningList_principalpath.setUndeletable(true);
		decommissioningList_principalpath.setEssential(true);
		MetadataBuilder decommissioningList_processingDate = decommissioningListSchema.create("processingDate")
				.setType(MetadataValueType.DATE);
		decommissioningList_processingDate.setUndeletable(true);
		decommissioningList_processingDate.setEssential(true);
		MetadataBuilder decommissioningList_processingUser = decommissioningListSchema.create("processingUser")
				.setType(MetadataValueType.REFERENCE);
		decommissioningList_processingUser.setUndeletable(true);
		decommissioningList_processingUser.setEssential(true);
		decommissioningList_processingUser.defineReferencesTo(userSchemaType);
		MetadataBuilder decommissioningList_removedauthorizations = decommissioningListSchema.get("removedauthorizations");
		decommissioningList_removedauthorizations.setMultivalue(true);
		decommissioningList_removedauthorizations.setSystemReserved(true);
		decommissioningList_removedauthorizations.setUndeletable(true);
		decommissioningList_removedauthorizations.setEssential(true);
		MetadataBuilder decommissioningList_schema = decommissioningListSchema.get("schema");
		decommissioningList_schema.setDefaultRequirement(true);
		decommissioningList_schema.setSystemReserved(true);
		decommissioningList_schema.setUndeletable(true);
		decommissioningList_schema.setEssential(true);
		MetadataBuilder decommissioningList_searchable = decommissioningListSchema.get("searchable");
		decommissioningList_searchable.setSystemReserved(true);
		decommissioningList_searchable.setUndeletable(true);
		decommissioningList_searchable.setEssential(true);
		MetadataBuilder decommissioningList_shareDenyTokens = decommissioningListSchema.get("shareDenyTokens");
		decommissioningList_shareDenyTokens.setMultivalue(true);
		decommissioningList_shareDenyTokens.setSystemReserved(true);
		decommissioningList_shareDenyTokens.setUndeletable(true);
		decommissioningList_shareDenyTokens.setEssential(true);
		decommissioningList_shareDenyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder decommissioningList_shareTokens = decommissioningListSchema.get("shareTokens");
		decommissioningList_shareTokens.setMultivalue(true);
		decommissioningList_shareTokens.setSystemReserved(true);
		decommissioningList_shareTokens.setUndeletable(true);
		decommissioningList_shareTokens.setEssential(true);
		decommissioningList_shareTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder decommissioningList_status = decommissioningListSchema.create("status").setType(MetadataValueType.ENUM);
		decommissioningList_status.setUndeletable(true);
		decommissioningList_status.setEssential(true);
		decommissioningList_status.defineAsEnum(DecomListStatus.class);
		MetadataBuilder decommissioningList_title = decommissioningListSchema.get("title");
		decommissioningList_title.setDefaultRequirement(true);
		decommissioningList_title.setUndeletable(true);
		decommissioningList_title.setEssential(true);
		decommissioningList_title.setSchemaAutocomplete(true);
		decommissioningList_title.setSearchable(true);
		MetadataBuilder decommissioningList_tokens = decommissioningListSchema.get("tokens");
		decommissioningList_tokens.setMultivalue(true);
		decommissioningList_tokens.setSystemReserved(true);
		decommissioningList_tokens.setUndeletable(true);
		decommissioningList_tokens.setEssential(true);
		MetadataBuilder decommissioningList_type = decommissioningListSchema.create("type").setType(MetadataValueType.ENUM);
		decommissioningList_type.setDefaultRequirement(true);
		decommissioningList_type.setSystemReserved(true);
		decommissioningList_type.setUndeletable(true);
		decommissioningList_type.setEssential(true);
		decommissioningList_type.setSearchable(true);
		decommissioningList_type.defineAsEnum(DecommissioningListType.class);
		MetadataBuilder decommissioningList_uniform = decommissioningListSchema.create("uniform")
				.setType(MetadataValueType.BOOLEAN);
		decommissioningList_uniform.setUndeletable(true);
		decommissioningList_uniform.setEssential(true);
		MetadataBuilder decommissioningList_uniformCategory = decommissioningListSchema.create("uniformCategory")
				.setType(MetadataValueType.REFERENCE);
		decommissioningList_uniformCategory.setUndeletable(true);
		decommissioningList_uniformCategory.setEssential(true);
		decommissioningList_uniformCategory.defineReferencesTo(categorySchemaType);
		MetadataBuilder decommissioningList_uniformCopyRule = decommissioningListSchema.create("uniformCopyRule")
				.setType(MetadataValueType.STRUCTURE);
		decommissioningList_uniformCopyRule.setUndeletable(true);
		decommissioningList_uniformCopyRule.setEssential(true);
		decommissioningList_uniformCopyRule.defineStructureFactory(CopyRetentionRuleFactory.class);
		MetadataBuilder decommissioningList_uniformCopyType = decommissioningListSchema.create("uniformCopyType")
				.setType(MetadataValueType.ENUM);
		decommissioningList_uniformCopyType.setUndeletable(true);
		decommissioningList_uniformCopyType.setEssential(true);
		decommissioningList_uniformCopyType.defineAsEnum(CopyType.class);
		MetadataBuilder decommissioningList_uniformRule = decommissioningListSchema.create("uniformRule")
				.setType(MetadataValueType.REFERENCE);
		decommissioningList_uniformRule.setUndeletable(true);
		decommissioningList_uniformRule.setEssential(true);
		decommissioningList_uniformRule.defineReferencesTo(retentionRuleSchemaType);
		MetadataBuilder decommissioningList_validationDate = decommissioningListSchema.create("validationDate")
				.setType(MetadataValueType.DATE);
		decommissioningList_validationDate.setUndeletable(true);
		decommissioningList_validationDate.setEnabled(false);
		MetadataBuilder decommissioningList_validationUser = decommissioningListSchema.create("validationUser")
				.setType(MetadataValueType.REFERENCE);
		decommissioningList_validationUser.setUndeletable(true);
		decommissioningList_validationUser.setEnabled(false);
		decommissioningList_validationUser.defineReferencesTo(userSchemaType);
		MetadataBuilder decommissioningList_validations = decommissioningListSchema.create("validations")
				.setType(MetadataValueType.STRUCTURE);
		decommissioningList_validations.setMultivalue(true);
		decommissioningList_validations.setUndeletable(true);
		decommissioningList_validations.defineStructureFactory(DecomListValidationFactory.class);
		MetadataBuilder decommissioningList_visibleInTrees = decommissioningListSchema.get("visibleInTrees");
		decommissioningList_visibleInTrees.setSystemReserved(true);
		decommissioningList_visibleInTrees.setUndeletable(true);
		decommissioningList_visibleInTrees.setEssential(true);
		MetadataBuilder document_email_emailAttachmentsList = document_emailSchema.create("emailAttachmentsList")
				.setType(MetadataValueType.STRING);
		document_email_emailAttachmentsList.setMultivalue(true);
		document_email_emailAttachmentsList.setUndeletable(true);
		document_email_emailAttachmentsList.setEssential(true);
		MetadataBuilder document_email_emailBCCTo = document_emailSchema.create("emailBCCTo").setType(MetadataValueType.STRING);
		document_email_emailBCCTo.setMultivalue(true);
		document_email_emailBCCTo.setUndeletable(true);
		document_email_emailBCCTo.setEssential(true);
		document_email_emailBCCTo.getPopulateConfigsBuilder().setProperties(asList("bcc"));
		MetadataBuilder document_email_emailCCTo = document_emailSchema.create("emailCCTo").setType(MetadataValueType.STRING);
		document_email_emailCCTo.setMultivalue(true);
		document_email_emailCCTo.setUndeletable(true);
		document_email_emailCCTo.setEssential(true);
		document_email_emailCCTo.getPopulateConfigsBuilder().setProperties(asList("cc"));
		MetadataBuilder document_email_emailCompany = document_emailSchema.create("emailCompany")
				.setType(MetadataValueType.STRING);
		document_email_emailCompany.setUndeletable(true);
		document_email_emailCompany.setEssential(true);
		MetadataBuilder document_email_emailContent = document_emailSchema.create("emailContent").setType(MetadataValueType.TEXT);
		document_email_emailContent.setUndeletable(true);
		document_email_emailContent.setEssential(true);
		MetadataBuilder document_email_emailFrom = document_emailSchema.create("emailFrom").setType(MetadataValueType.STRING);
		document_email_emailFrom.setUndeletable(true);
		document_email_emailFrom.setEssential(true);
		document_email_emailFrom.getPopulateConfigsBuilder().setProperties(asList("from"));
		MetadataBuilder document_email_emailInNameOf = document_emailSchema.create("emailInNameOf")
				.setType(MetadataValueType.STRING);
		document_email_emailInNameOf.setUndeletable(true);
		document_email_emailInNameOf.setEssential(true);
		MetadataBuilder document_email_emailObject = document_emailSchema.create("emailObject").setType(MetadataValueType.STRING);
		document_email_emailObject.setUndeletable(true);
		document_email_emailObject.setEssential(true);
		document_email_emailObject.getPopulateConfigsBuilder().setProperties(asList("subject"));
		MetadataBuilder document_email_emailReceivedOn = document_emailSchema.create("emailReceivedOn")
				.setType(MetadataValueType.DATE_TIME);
		document_email_emailReceivedOn.setUndeletable(true);
		document_email_emailReceivedOn.setEssential(true);
		MetadataBuilder document_email_emailSentOn = document_emailSchema.create("emailSentOn")
				.setType(MetadataValueType.DATE_TIME);
		document_email_emailSentOn.setUndeletable(true);
		document_email_emailSentOn.setEssential(true);
		MetadataBuilder document_email_emailTo = document_emailSchema.create("emailTo").setType(MetadataValueType.STRING);
		document_email_emailTo.setMultivalue(true);
		document_email_emailTo.setUndeletable(true);
		document_email_emailTo.setEssential(true);
		document_email_emailTo.getPopulateConfigsBuilder().setProperties(asList("to"));
		MetadataBuilder document_email_subjectToBroadcastRule = document_emailSchema.create("subjectToBroadcastRule")
				.setType(MetadataValueType.BOOLEAN);
		document_email_subjectToBroadcastRule.setUndeletable(true);
		document_email_subjectToBroadcastRule.setEssential(true);
		MetadataBuilder document_actualDepositDate = documentSchema.create("actualDepositDate").setType(MetadataValueType.DATE);
		document_actualDepositDate.setUndeletable(true);
		document_actualDepositDate.setEssential(true);
		MetadataBuilder document_actualDepositDateEntered = documentSchema.create("actualDepositDateEntered")
				.setType(MetadataValueType.DATE);
		document_actualDepositDateEntered.setUndeletable(true);
		MetadataBuilder document_actualDestructionDate = documentSchema.create("actualDestructionDate")
				.setType(MetadataValueType.DATE);
		document_actualDestructionDate.setUndeletable(true);
		document_actualDestructionDate.setEssential(true);
		MetadataBuilder document_actualDestructionDateEntered = documentSchema.create("actualDestructionDateEntered")
				.setType(MetadataValueType.DATE);
		document_actualDestructionDateEntered.setUndeletable(true);
		MetadataBuilder document_actualTransferDate = documentSchema.create("actualTransferDate").setType(MetadataValueType.DATE);
		document_actualTransferDate.setUndeletable(true);
		document_actualTransferDate.setEssential(true);
		MetadataBuilder document_actualTransferDateEntered = documentSchema.create("actualTransferDateEntered")
				.setType(MetadataValueType.DATE);
		document_actualTransferDateEntered.setUndeletable(true);
		MetadataBuilder document_administrativeUnit = documentSchema.create("administrativeUnit")
				.setType(MetadataValueType.REFERENCE);
		document_administrativeUnit.setUndeletable(true);
		document_administrativeUnit.setEssential(true);
		document_administrativeUnit.defineReferencesTo(administrativeUnitSchemaType);
		MetadataBuilder document_alertUsersWhenAvailable = documentSchema.create("alertUsersWhenAvailable")
				.setType(MetadataValueType.REFERENCE);
		document_alertUsersWhenAvailable.setMultivalue(true);
		document_alertUsersWhenAvailable.setUndeletable(true);
		document_alertUsersWhenAvailable.defineReferencesTo(userSchemaType);
		MetadataBuilder document_allauthorizations = documentSchema.get("allauthorizations");
		document_allauthorizations.setMultivalue(true);
		document_allauthorizations.setSystemReserved(true);
		document_allauthorizations.setUndeletable(true);
		document_allauthorizations.setEssential(true);
		MetadataBuilder document_applicableCopyRule = documentSchema.create("applicableCopyRule")
				.setType(MetadataValueType.STRUCTURE);
		document_applicableCopyRule.setMultivalue(true);
		document_applicableCopyRule.setUndeletable(true);
		document_applicableCopyRule.defineStructureFactory(CopyRetentionRuleInRuleFactory.class);
		MetadataBuilder document_archivisticStatus = documentSchema.create("archivisticStatus").setType(MetadataValueType.ENUM);
		document_archivisticStatus.setUndeletable(true);
		document_archivisticStatus.setEssential(true);
		document_archivisticStatus.defineAsEnum(FolderStatus.class);
		MetadataBuilder document_author = documentSchema.create("author").setType(MetadataValueType.STRING);
		document_author.setUndeletable(true);
		document_author.setEssential(true);
		document_author.getPopulateConfigsBuilder().setProperties(asList("author"));
		MetadataBuilder document_authorizations = documentSchema.get("authorizations");
		document_authorizations.setMultivalue(true);
		document_authorizations.setSystemReserved(true);
		document_authorizations.setUndeletable(true);
		document_authorizations.setEssential(true);
		MetadataBuilder document_borrowed = documentSchema.create("borrowed").setType(MetadataValueType.BOOLEAN);
		document_borrowed.setUndeletable(true);
		MetadataBuilder document_calendarYear = documentSchema.create("calendarYear").setType(MetadataValueType.DATE);
		document_calendarYear.setUndeletable(true);
		MetadataBuilder document_calendarYearEntered = documentSchema.create("calendarYearEntered")
				.setType(MetadataValueType.STRING);
		document_calendarYearEntered.setUndeletable(true);
		MetadataBuilder document_category = documentSchema.create("category").setType(MetadataValueType.REFERENCE);
		document_category.setUndeletable(true);
		document_category.setEssential(true);
		document_category.defineReferencesTo(categorySchemaType);
		MetadataBuilder document_closingDate = documentSchema.create("closingDate").setType(MetadataValueType.DATE);
		document_closingDate.setUndeletable(true);
		document_closingDate.setEssential(true);
		MetadataBuilder document_comments = documentSchema.create("comments").setType(MetadataValueType.STRUCTURE);
		document_comments.setMultivalue(true);
		document_comments.setUndeletable(true);
		document_comments.defineStructureFactory(CommentFactory.class);
		MetadataBuilder document_company = documentSchema.create("company").setType(MetadataValueType.STRING);
		document_company.setUndeletable(true);
		document_company.setEssential(true);
		document_company.getPopulateConfigsBuilder().setProperties(asList("company"));
		MetadataBuilder document_content = documentSchema.create("content").setType(MetadataValueType.CONTENT);
		document_content.setUndeletable(true);
		document_content.setEssential(true);
		document_content.setEssentialInSummary(true);
		document_content.setSearchable(true);
		document_content.defineStructureFactory(ContentFactory.class);
		MetadataBuilder document_copyStatus = documentSchema.create("copyStatus").setType(MetadataValueType.ENUM);
		document_copyStatus.defineAsEnum(CopyType.class);
		MetadataBuilder document_createdBy = documentSchema.get("createdBy");
		document_createdBy.setSystemReserved(true);
		document_createdBy.setUndeletable(true);
		document_createdBy.setEssential(true);
		MetadataBuilder document_createdOn = documentSchema.get("createdOn");
		document_createdOn.setSystemReserved(true);
		document_createdOn.setUndeletable(true);
		document_createdOn.setEssential(true);
		document_createdOn.setSortable(true);
		MetadataBuilder document_deleted = documentSchema.get("deleted");
		document_deleted.setSystemReserved(true);
		document_deleted.setUndeletable(true);
		document_deleted.setEssential(true);
		MetadataBuilder document_denyTokens = documentSchema.get("denyTokens");
		document_denyTokens.setMultivalue(true);
		document_denyTokens.setSystemReserved(true);
		document_denyTokens.setUndeletable(true);
		document_denyTokens.setEssential(true);
		document_denyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder document_description = documentSchema.create("description").setType(MetadataValueType.TEXT);
		document_description.setUndeletable(true);
		document_description.setEssentialInSummary(true);
		document_description.setSearchable(true);
		MetadataBuilder document_detachedauthorizations = documentSchema.get("detachedauthorizations");
		document_detachedauthorizations.setSystemReserved(true);
		document_detachedauthorizations.setUndeletable(true);
		document_detachedauthorizations.setEssential(true);
		MetadataBuilder document_documentType = documentSchema.create("documentType").setType(MetadataValueType.STRING);
		document_documentType.setSystemReserved(true);
		document_documentType.setUndeletable(true);
		MetadataBuilder document_expectedDepositDate = documentSchema.create("expectedDepositDate")
				.setType(MetadataValueType.DATE);
		document_expectedDepositDate.setUndeletable(true);
		document_expectedDepositDate.setEssential(true);
		MetadataBuilder document_expectedDestructionDate = documentSchema.create("expectedDestructionDate")
				.setType(MetadataValueType.DATE);
		document_expectedDestructionDate.setUndeletable(true);
		document_expectedDestructionDate.setEssential(true);
		MetadataBuilder document_expectedTransferDate = documentSchema.create("expectedTransferDate")
				.setType(MetadataValueType.DATE);
		document_expectedTransferDate.setUndeletable(true);
		document_expectedTransferDate.setEssential(true);
		MetadataBuilder document_filingSpace = documentSchema.create("filingSpace").setType(MetadataValueType.REFERENCE);
		document_filingSpace.setUndeletable(true);
		document_filingSpace.setEssential(true);
		document_filingSpace.defineReferencesTo(filingSpaceSchemaType);
		MetadataBuilder document_folder = documentSchema.create("folder").setType(MetadataValueType.REFERENCE);
		document_folder.setDefaultRequirement(true);
		document_folder.setUndeletable(true);
		document_folder.setEssential(true);
		document_folder.defineChildOfRelationshipToType(folderSchemaType);
		MetadataBuilder document_followers = documentSchema.get("followers");
		document_followers.setMultivalue(true);
		document_followers.setSystemReserved(true);
		document_followers.setUndeletable(true);
		document_followers.setEssential(true);
		document_followers.setSearchable(true);
		MetadataBuilder document_formCreatedBy = documentSchema.create("formCreatedBy").setType(MetadataValueType.REFERENCE);
		document_formCreatedBy.setSystemReserved(true);
		document_formCreatedBy.setUndeletable(true);
		document_formCreatedBy.defineReferencesTo(userSchemaType);
		MetadataBuilder document_formCreatedOn = documentSchema.create("formCreatedOn").setType(MetadataValueType.DATE_TIME);
		document_formCreatedOn.setSystemReserved(true);
		document_formCreatedOn.setUndeletable(true);
		MetadataBuilder document_formModifiedBy = documentSchema.create("formModifiedBy").setType(MetadataValueType.REFERENCE);
		document_formModifiedBy.setSystemReserved(true);
		document_formModifiedBy.setUndeletable(true);
		document_formModifiedBy.defineReferencesTo(userSchemaType);
		MetadataBuilder document_formModifiedOn = documentSchema.create("formModifiedOn").setType(MetadataValueType.DATE_TIME);
		document_formModifiedOn.setSystemReserved(true);
		document_formModifiedOn.setUndeletable(true);
		MetadataBuilder document_id = documentSchema.get("id");
		document_id.setDefaultRequirement(true);
		document_id.setSystemReserved(true);
		document_id.setUndeletable(true);
		document_id.setEssential(true);
		document_id.setSearchable(true);
		document_id.setSortable(true);
		document_id.setUniqueValue(true);
		document_id.setUnmodifiable(true);
		MetadataBuilder document_inheritedRetentionRule = documentSchema.create("inheritedRetentionRule")
				.setType(MetadataValueType.REFERENCE);
		document_inheritedRetentionRule.defineReferencesTo(retentionRuleSchemaType);
		MetadataBuilder document_inheritedauthorizations = documentSchema.get("inheritedauthorizations");
		document_inheritedauthorizations.setMultivalue(true);
		document_inheritedauthorizations.setSystemReserved(true);
		document_inheritedauthorizations.setUndeletable(true);
		document_inheritedauthorizations.setEssential(true);
		MetadataBuilder document_keywords = documentSchema.create("keywords").setType(MetadataValueType.STRING);
		document_keywords.setMultivalue(true);
		document_keywords.setUndeletable(true);
		document_keywords.setSearchable(true);
		document_keywords.getPopulateConfigsBuilder().setProperties(asList("keywords"));
		MetadataBuilder document_legacyIdentifier = documentSchema.get("legacyIdentifier");
		document_legacyIdentifier.setDefaultRequirement(true);
		document_legacyIdentifier.setSystemReserved(true);
		document_legacyIdentifier.setUndeletable(true);
		document_legacyIdentifier.setEssential(true);
		document_legacyIdentifier.setSearchable(true);
		document_legacyIdentifier.setUniqueValue(true);
		document_legacyIdentifier.setUnmodifiable(true);
		MetadataBuilder document_mainCopyRule = documentSchema.create("mainCopyRule").setType(MetadataValueType.STRUCTURE);
		document_mainCopyRule.setDefaultRequirement(true);
		document_mainCopyRule.setUndeletable(true);
		document_mainCopyRule.defineStructureFactory(CopyRetentionRuleFactory.class);
		MetadataBuilder document_mainCopyRuleIdEntered = documentSchema.create("mainCopyRuleIdEntered")
				.setType(MetadataValueType.STRING);
		document_mainCopyRuleIdEntered.setUndeletable(true);
		MetadataBuilder document_manualTokens = documentSchema.get("manualTokens");
		document_manualTokens.setMultivalue(true);
		document_manualTokens.setSystemReserved(true);
		document_manualTokens.setUndeletable(true);
		document_manualTokens.setEssential(true);
		document_manualTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder document_markedForPreviewConversion = documentSchema.get("markedForPreviewConversion");
		document_markedForPreviewConversion.setSystemReserved(true);
		document_markedForPreviewConversion.setUndeletable(true);
		document_markedForPreviewConversion.setEssential(true);
		MetadataBuilder document_modifiedBy = documentSchema.get("modifiedBy");
		document_modifiedBy.setSystemReserved(true);
		document_modifiedBy.setUndeletable(true);
		document_modifiedBy.setEssential(true);
		MetadataBuilder document_modifiedOn = documentSchema.get("modifiedOn");
		document_modifiedOn.setSystemReserved(true);
		document_modifiedOn.setUndeletable(true);
		document_modifiedOn.setEssential(true);
		document_modifiedOn.setSortable(true);
		MetadataBuilder document_openingDate = documentSchema.create("openingDate").setType(MetadataValueType.DATE);
		document_openingDate.setUndeletable(true);
		document_openingDate.setEssential(true);
		MetadataBuilder document_parentpath = documentSchema.get("parentpath");
		document_parentpath.setMultivalue(true);
		document_parentpath.setSystemReserved(true);
		document_parentpath.setUndeletable(true);
		document_parentpath.setEssential(true);
		MetadataBuilder document_path = documentSchema.get("path");
		document_path.setMultivalue(true);
		document_path.setSystemReserved(true);
		document_path.setUndeletable(true);
		document_path.setEssential(true);
		MetadataBuilder document_pathParts = documentSchema.get("pathParts");
		document_pathParts.setMultivalue(true);
		document_pathParts.setSystemReserved(true);
		document_pathParts.setUndeletable(true);
		document_pathParts.setEssential(true);
		MetadataBuilder document_principalpath = documentSchema.get("principalpath");
		document_principalpath.setSystemReserved(true);
		document_principalpath.setUndeletable(true);
		document_principalpath.setEssential(true);
		MetadataBuilder document_published = documentSchema.create("published").setType(MetadataValueType.BOOLEAN);
		document_published.setUndeletable(true);
		document_published.setDefaultValue(false);
		MetadataBuilder document_removedauthorizations = documentSchema.get("removedauthorizations");
		document_removedauthorizations.setMultivalue(true);
		document_removedauthorizations.setSystemReserved(true);
		document_removedauthorizations.setUndeletable(true);
		document_removedauthorizations.setEssential(true);
		MetadataBuilder document_retentionRule = documentSchema.create("retentionRule").setType(MetadataValueType.REFERENCE);
		document_retentionRule.setUndeletable(true);
		document_retentionRule.setEssential(true);
		document_retentionRule.defineReferencesTo(retentionRuleSchemaType);
		MetadataBuilder document_sameInactiveFateAsFolder = documentSchema.create("sameInactiveFateAsFolder")
				.setType(MetadataValueType.BOOLEAN);
		document_sameInactiveFateAsFolder.setUndeletable(true);
		MetadataBuilder document_sameSemiActiveFateAsFolder = documentSchema.create("sameSemiActiveFateAsFolder")
				.setType(MetadataValueType.BOOLEAN);
		document_sameSemiActiveFateAsFolder.setUndeletable(true);
		MetadataBuilder document_schema = documentSchema.get("schema");
		document_schema.setDefaultRequirement(true);
		document_schema.setSystemReserved(true);
		document_schema.setUndeletable(true);
		document_schema.setEssential(true);
		MetadataBuilder document_searchable = documentSchema.get("searchable");
		document_searchable.setSystemReserved(true);
		document_searchable.setUndeletable(true);
		document_searchable.setEssential(true);
		MetadataBuilder document_shareDenyTokens = documentSchema.get("shareDenyTokens");
		document_shareDenyTokens.setMultivalue(true);
		document_shareDenyTokens.setSystemReserved(true);
		document_shareDenyTokens.setUndeletable(true);
		document_shareDenyTokens.setEssential(true);
		document_shareDenyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder document_shareTokens = documentSchema.get("shareTokens");
		document_shareTokens.setMultivalue(true);
		document_shareTokens.setSystemReserved(true);
		document_shareTokens.setUndeletable(true);
		document_shareTokens.setEssential(true);
		document_shareTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder document_subject = documentSchema.create("subject").setType(MetadataValueType.STRING);
		document_subject.setUndeletable(true);
		document_subject.setEssential(true);
		document_subject.getPopulateConfigsBuilder().setProperties(asList("subject"));
		MetadataBuilder document_title = documentSchema.get("title");
		document_title.setDefaultRequirement(true);
		document_title.setUndeletable(true);
		document_title.setEssential(true);
		document_title.setSchemaAutocomplete(true);
		document_title.setSearchable(true);
		document_title.getPopulateConfigsBuilder().setProperties(asList("title"));
		MetadataBuilder document_tokens = documentSchema.get("tokens");
		document_tokens.setMultivalue(true);
		document_tokens.setSystemReserved(true);
		document_tokens.setUndeletable(true);
		document_tokens.setEssential(true);
		MetadataBuilder document_type = documentSchema.create("type").setType(MetadataValueType.REFERENCE);
		document_type.setUndeletable(true);
		document_type.setEssential(true);
		document_type.defineReferencesTo(ddvDocumentTypeSchemaType);
		MetadataBuilder document_version = documentSchema.create("version").setType(MetadataValueType.STRING);
		document_version.setUndeletable(true);
		MetadataBuilder document_visibleInTrees = documentSchema.get("visibleInTrees");
		document_visibleInTrees.setSystemReserved(true);
		document_visibleInTrees.setUndeletable(true);
		document_visibleInTrees.setEssential(true);
		MetadataBuilder filingSpace_administrators = filingSpaceSchema.create("administrators")
				.setType(MetadataValueType.REFERENCE);
		filingSpace_administrators.setMultivalue(true);
		filingSpace_administrators.setUndeletable(true);
		filingSpace_administrators.setEssential(true);
		filingSpace_administrators.defineReferencesTo(userSchemaType);
		MetadataBuilder filingSpace_allauthorizations = filingSpaceSchema.get("allauthorizations");
		filingSpace_allauthorizations.setMultivalue(true);
		filingSpace_allauthorizations.setSystemReserved(true);
		filingSpace_allauthorizations.setUndeletable(true);
		filingSpace_allauthorizations.setEssential(true);
		MetadataBuilder filingSpace_authorizations = filingSpaceSchema.get("authorizations");
		filingSpace_authorizations.setMultivalue(true);
		filingSpace_authorizations.setSystemReserved(true);
		filingSpace_authorizations.setUndeletable(true);
		filingSpace_authorizations.setEssential(true);
		MetadataBuilder filingSpace_code = filingSpaceSchema.create("code").setType(MetadataValueType.STRING);
		filingSpace_code.setDefaultRequirement(true);
		filingSpace_code.setUndeletable(true);
		filingSpace_code.setEssential(true);
		filingSpace_code.setSchemaAutocomplete(true);
		filingSpace_code.setSearchable(true);
		MetadataBuilder filingSpace_createdBy = filingSpaceSchema.get("createdBy");
		filingSpace_createdBy.setSystemReserved(true);
		filingSpace_createdBy.setUndeletable(true);
		filingSpace_createdBy.setEssential(true);
		MetadataBuilder filingSpace_createdOn = filingSpaceSchema.get("createdOn");
		filingSpace_createdOn.setSystemReserved(true);
		filingSpace_createdOn.setUndeletable(true);
		filingSpace_createdOn.setEssential(true);
		filingSpace_createdOn.setSortable(true);
		MetadataBuilder filingSpace_deleted = filingSpaceSchema.get("deleted");
		filingSpace_deleted.setSystemReserved(true);
		filingSpace_deleted.setUndeletable(true);
		filingSpace_deleted.setEssential(true);
		MetadataBuilder filingSpace_denyTokens = filingSpaceSchema.get("denyTokens");
		filingSpace_denyTokens.setMultivalue(true);
		filingSpace_denyTokens.setSystemReserved(true);
		filingSpace_denyTokens.setUndeletable(true);
		filingSpace_denyTokens.setEssential(true);
		filingSpace_denyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder filingSpace_description = filingSpaceSchema.create("description").setType(MetadataValueType.STRING);
		filingSpace_description.setUndeletable(true);
		filingSpace_description.setEssentialInSummary(true);
		filingSpace_description.setSearchable(true);
		MetadataBuilder filingSpace_detachedauthorizations = filingSpaceSchema.get("detachedauthorizations");
		filingSpace_detachedauthorizations.setSystemReserved(true);
		filingSpace_detachedauthorizations.setUndeletable(true);
		filingSpace_detachedauthorizations.setEssential(true);
		MetadataBuilder filingSpace_followers = filingSpaceSchema.get("followers");
		filingSpace_followers.setMultivalue(true);
		filingSpace_followers.setSystemReserved(true);
		filingSpace_followers.setUndeletable(true);
		filingSpace_followers.setEssential(true);
		filingSpace_followers.setSearchable(true);
		MetadataBuilder filingSpace_id = filingSpaceSchema.get("id");
		filingSpace_id.setDefaultRequirement(true);
		filingSpace_id.setSystemReserved(true);
		filingSpace_id.setUndeletable(true);
		filingSpace_id.setEssential(true);
		filingSpace_id.setSearchable(true);
		filingSpace_id.setSortable(true);
		filingSpace_id.setUniqueValue(true);
		filingSpace_id.setUnmodifiable(true);
		MetadataBuilder filingSpace_inheritedauthorizations = filingSpaceSchema.get("inheritedauthorizations");
		filingSpace_inheritedauthorizations.setMultivalue(true);
		filingSpace_inheritedauthorizations.setSystemReserved(true);
		filingSpace_inheritedauthorizations.setUndeletable(true);
		filingSpace_inheritedauthorizations.setEssential(true);
		MetadataBuilder filingSpace_legacyIdentifier = filingSpaceSchema.get("legacyIdentifier");
		filingSpace_legacyIdentifier.setDefaultRequirement(true);
		filingSpace_legacyIdentifier.setSystemReserved(true);
		filingSpace_legacyIdentifier.setUndeletable(true);
		filingSpace_legacyIdentifier.setEssential(true);
		filingSpace_legacyIdentifier.setSearchable(true);
		filingSpace_legacyIdentifier.setUniqueValue(true);
		filingSpace_legacyIdentifier.setUnmodifiable(true);
		MetadataBuilder filingSpace_manualTokens = filingSpaceSchema.get("manualTokens");
		filingSpace_manualTokens.setMultivalue(true);
		filingSpace_manualTokens.setSystemReserved(true);
		filingSpace_manualTokens.setUndeletable(true);
		filingSpace_manualTokens.setEssential(true);
		filingSpace_manualTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder filingSpace_markedForPreviewConversion = filingSpaceSchema.get("markedForPreviewConversion");
		filingSpace_markedForPreviewConversion.setSystemReserved(true);
		filingSpace_markedForPreviewConversion.setUndeletable(true);
		filingSpace_markedForPreviewConversion.setEssential(true);
		MetadataBuilder filingSpace_modifiedBy = filingSpaceSchema.get("modifiedBy");
		filingSpace_modifiedBy.setSystemReserved(true);
		filingSpace_modifiedBy.setUndeletable(true);
		filingSpace_modifiedBy.setEssential(true);
		MetadataBuilder filingSpace_modifiedOn = filingSpaceSchema.get("modifiedOn");
		filingSpace_modifiedOn.setSystemReserved(true);
		filingSpace_modifiedOn.setUndeletable(true);
		filingSpace_modifiedOn.setEssential(true);
		filingSpace_modifiedOn.setSortable(true);
		MetadataBuilder filingSpace_parentpath = filingSpaceSchema.get("parentpath");
		filingSpace_parentpath.setMultivalue(true);
		filingSpace_parentpath.setSystemReserved(true);
		filingSpace_parentpath.setUndeletable(true);
		filingSpace_parentpath.setEssential(true);
		MetadataBuilder filingSpace_path = filingSpaceSchema.get("path");
		filingSpace_path.setMultivalue(true);
		filingSpace_path.setSystemReserved(true);
		filingSpace_path.setUndeletable(true);
		filingSpace_path.setEssential(true);
		MetadataBuilder filingSpace_pathParts = filingSpaceSchema.get("pathParts");
		filingSpace_pathParts.setMultivalue(true);
		filingSpace_pathParts.setSystemReserved(true);
		filingSpace_pathParts.setUndeletable(true);
		filingSpace_pathParts.setEssential(true);
		MetadataBuilder filingSpace_principalpath = filingSpaceSchema.get("principalpath");
		filingSpace_principalpath.setSystemReserved(true);
		filingSpace_principalpath.setUndeletable(true);
		filingSpace_principalpath.setEssential(true);
		MetadataBuilder filingSpace_removedauthorizations = filingSpaceSchema.get("removedauthorizations");
		filingSpace_removedauthorizations.setMultivalue(true);
		filingSpace_removedauthorizations.setSystemReserved(true);
		filingSpace_removedauthorizations.setUndeletable(true);
		filingSpace_removedauthorizations.setEssential(true);
		MetadataBuilder filingSpace_schema = filingSpaceSchema.get("schema");
		filingSpace_schema.setDefaultRequirement(true);
		filingSpace_schema.setSystemReserved(true);
		filingSpace_schema.setUndeletable(true);
		filingSpace_schema.setEssential(true);
		MetadataBuilder filingSpace_searchable = filingSpaceSchema.get("searchable");
		filingSpace_searchable.setSystemReserved(true);
		filingSpace_searchable.setUndeletable(true);
		filingSpace_searchable.setEssential(true);
		MetadataBuilder filingSpace_shareDenyTokens = filingSpaceSchema.get("shareDenyTokens");
		filingSpace_shareDenyTokens.setMultivalue(true);
		filingSpace_shareDenyTokens.setSystemReserved(true);
		filingSpace_shareDenyTokens.setUndeletable(true);
		filingSpace_shareDenyTokens.setEssential(true);
		filingSpace_shareDenyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder filingSpace_shareTokens = filingSpaceSchema.get("shareTokens");
		filingSpace_shareTokens.setMultivalue(true);
		filingSpace_shareTokens.setSystemReserved(true);
		filingSpace_shareTokens.setUndeletable(true);
		filingSpace_shareTokens.setEssential(true);
		filingSpace_shareTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder filingSpace_title = filingSpaceSchema.get("title");
		filingSpace_title.setDefaultRequirement(true);
		filingSpace_title.setUndeletable(true);
		filingSpace_title.setEssential(true);
		filingSpace_title.setSchemaAutocomplete(true);
		filingSpace_title.setSearchable(true);
		MetadataBuilder filingSpace_tokens = filingSpaceSchema.get("tokens");
		filingSpace_tokens.setMultivalue(true);
		filingSpace_tokens.setSystemReserved(true);
		filingSpace_tokens.setUndeletable(true);
		filingSpace_tokens.setEssential(true);
		MetadataBuilder filingSpace_users = filingSpaceSchema.create("users").setType(MetadataValueType.REFERENCE);
		filingSpace_users.setMultivalue(true);
		filingSpace_users.setUndeletable(true);
		filingSpace_users.setEssential(true);
		filingSpace_users.defineReferencesTo(userSchemaType);
		MetadataBuilder filingSpace_visibleInTrees = filingSpaceSchema.get("visibleInTrees");
		filingSpace_visibleInTrees.setSystemReserved(true);
		filingSpace_visibleInTrees.setUndeletable(true);
		filingSpace_visibleInTrees.setEssential(true);
		MetadataBuilder folder_activeRetentionPeriodCode = folderSchema.create("activeRetentionPeriodCode")
				.setType(MetadataValueType.STRING);
		folder_activeRetentionPeriodCode.setUndeletable(true);
		MetadataBuilder folder_activeRetentionType = folderSchema.create("activeRetentionType").setType(MetadataValueType.ENUM);
		folder_activeRetentionType.setUndeletable(true);
		folder_activeRetentionType.setEssential(true);
		folder_activeRetentionType.defineAsEnum(RetentionType.class);
		MetadataBuilder folder_actualDepositDate = folderSchema.create("actualDepositDate").setType(MetadataValueType.DATE);
		folder_actualDepositDate.setUndeletable(true);
		folder_actualDepositDate.setEssential(true);
		MetadataBuilder folder_actualDestructionDate = folderSchema.create("actualDestructionDate")
				.setType(MetadataValueType.DATE);
		folder_actualDestructionDate.setUndeletable(true);
		folder_actualDestructionDate.setEssential(true);
		MetadataBuilder folder_actualTransferDate = folderSchema.create("actualTransferDate").setType(MetadataValueType.DATE);
		folder_actualTransferDate.setUndeletable(true);
		folder_actualTransferDate.setEssential(true);
		MetadataBuilder folder_administrativeUnit = folderSchema.create("administrativeUnit")
				.setType(MetadataValueType.REFERENCE);
		folder_administrativeUnit.setDefaultRequirement(true);
		folder_administrativeUnit.setUndeletable(true);
		folder_administrativeUnit.setEssential(true);
		folder_administrativeUnit.defineReferencesTo(administrativeUnitSchemaType);
		MetadataBuilder folder_administrativeUnitAncestors = folderSchema.create("administrativeUnitAncestors")
				.setType(MetadataValueType.REFERENCE);
		folder_administrativeUnitAncestors.setMultivalue(true);
		folder_administrativeUnitAncestors.setUndeletable(true);
		folder_administrativeUnitAncestors.setEssential(true);
		folder_administrativeUnitAncestors.defineReferencesTo(administrativeUnitSchemaType);
		MetadataBuilder folder_administrativeUnitEntered = folderSchema.create("administrativeUnitEntered")
				.setType(MetadataValueType.REFERENCE);
		folder_administrativeUnitEntered.setUndeletable(true);
		folder_administrativeUnitEntered.setEssential(true);
		folder_administrativeUnitEntered.defineTaxonomyRelationshipToType(administrativeUnitSchemaType);
		MetadataBuilder folder_alertUsersWhenAvailable = folderSchema.create("alertUsersWhenAvailable")
				.setType(MetadataValueType.REFERENCE);
		folder_alertUsersWhenAvailable.setMultivalue(true);
		folder_alertUsersWhenAvailable.setUndeletable(true);
		folder_alertUsersWhenAvailable.defineReferencesTo(userSchemaType);
		MetadataBuilder folder_allauthorizations = folderSchema.get("allauthorizations");
		folder_allauthorizations.setMultivalue(true);
		folder_allauthorizations.setSystemReserved(true);
		folder_allauthorizations.setUndeletable(true);
		folder_allauthorizations.setEssential(true);
		MetadataBuilder folder_applicableCopyRule = folderSchema.create("applicableCopyRule")
				.setType(MetadataValueType.STRUCTURE);
		folder_applicableCopyRule.setMultivalue(true);
		folder_applicableCopyRule.setUndeletable(true);
		folder_applicableCopyRule.setEssential(true);
		folder_applicableCopyRule.defineStructureFactory(CopyRetentionRuleFactory.class);
		MetadataBuilder folder_archivisticStatus = folderSchema.create("archivisticStatus").setType(MetadataValueType.ENUM);
		folder_archivisticStatus.setUndeletable(true);
		folder_archivisticStatus.setEssential(true);
		folder_archivisticStatus.setEssentialInSummary(true);
		folder_archivisticStatus.defineAsEnum(FolderStatus.class);
		MetadataBuilder folder_authorizations = folderSchema.get("authorizations");
		folder_authorizations.setMultivalue(true);
		folder_authorizations.setSystemReserved(true);
		folder_authorizations.setUndeletable(true);
		folder_authorizations.setEssential(true);
		MetadataBuilder folder_borrowDate = folderSchema.create("borrowDate").setType(MetadataValueType.DATE_TIME);
		folder_borrowDate.setUndeletable(true);
		MetadataBuilder folder_borrowPreviewReturnDate = folderSchema.create("borrowPreviewReturnDate")
				.setType(MetadataValueType.DATE);
		folder_borrowPreviewReturnDate.setUndeletable(true);
		MetadataBuilder folder_borrowReturnDate = folderSchema.create("borrowReturnDate").setType(MetadataValueType.DATE_TIME);
		folder_borrowReturnDate.setUndeletable(true);
		MetadataBuilder folder_borrowUser = folderSchema.create("borrowUser").setType(MetadataValueType.REFERENCE);
		folder_borrowUser.setUndeletable(true);
		folder_borrowUser.defineReferencesTo(userSchemaType);
		MetadataBuilder folder_borrowUserEntered = folderSchema.create("borrowUserEntered").setType(MetadataValueType.REFERENCE);
		folder_borrowUserEntered.setSystemReserved(true);
		folder_borrowUserEntered.setUndeletable(true);
		folder_borrowUserEntered.defineReferencesTo(userSchemaType);
		MetadataBuilder folder_borrowed = folderSchema.create("borrowed").setType(MetadataValueType.BOOLEAN);
		folder_borrowed.setUndeletable(true);
		MetadataBuilder folder_borrowingType = folderSchema.create("borrowingType").setType(MetadataValueType.ENUM);
		folder_borrowingType.setUndeletable(true);
		folder_borrowingType.defineAsEnum(BorrowingType.class);
		MetadataBuilder folder_calendarYear = folderSchema.create("calendarYear").setType(MetadataValueType.DATE);
		folder_calendarYear.setUndeletable(true);
		MetadataBuilder folder_calendarYearEntered = folderSchema.create("calendarYearEntered").setType(MetadataValueType.STRING);
		folder_calendarYearEntered.setUndeletable(true);
		MetadataBuilder folder_category = folderSchema.create("category").setType(MetadataValueType.REFERENCE);
		folder_category.setDefaultRequirement(true);
		folder_category.setUndeletable(true);
		folder_category.setEssential(true);
		folder_category.defineReferencesTo(categorySchemaType);
		MetadataBuilder folder_categoryCode = folderSchema.create("categoryCode").setType(MetadataValueType.STRING);
		folder_categoryCode.setUndeletable(true);
		folder_categoryCode.setEssential(true);
		MetadataBuilder folder_categoryEntered = folderSchema.create("categoryEntered").setType(MetadataValueType.REFERENCE);
		folder_categoryEntered.setUndeletable(true);
		folder_categoryEntered.setEssential(true);
		folder_categoryEntered.defineTaxonomyRelationshipToType(categorySchemaType);
		MetadataBuilder folder_closingDate = folderSchema.create("closingDate").setType(MetadataValueType.DATE);
		folder_closingDate.setUndeletable(true);
		folder_closingDate.setEssential(true);
		MetadataBuilder folder_comments = folderSchema.create("comments").setType(MetadataValueType.STRUCTURE);
		folder_comments.setMultivalue(true);
		folder_comments.setUndeletable(true);
		folder_comments.defineStructureFactory(CommentFactory.class);
		MetadataBuilder folder_container = folderSchema.create("container").setType(MetadataValueType.REFERENCE);
		folder_container.setUndeletable(true);
		folder_container.setEssential(true);
		folder_container.defineReferencesTo(containerRecordSchemaType);
		MetadataBuilder folder_copyRulesExpectedDepositDates = folderSchema.create("copyRulesExpectedDepositDates")
				.setType(MetadataValueType.DATE);
		folder_copyRulesExpectedDepositDates.setMultivalue(true);
		folder_copyRulesExpectedDepositDates.setUndeletable(true);
		folder_copyRulesExpectedDepositDates.setEssential(true);
		MetadataBuilder folder_copyRulesExpectedDestructionDates = folderSchema.create("copyRulesExpectedDestructionDates")
				.setType(MetadataValueType.DATE);
		folder_copyRulesExpectedDestructionDates.setMultivalue(true);
		folder_copyRulesExpectedDestructionDates.setUndeletable(true);
		folder_copyRulesExpectedDestructionDates.setEssential(true);
		MetadataBuilder folder_copyRulesExpectedTransferDates = folderSchema.create("copyRulesExpectedTransferDates")
				.setType(MetadataValueType.DATE);
		folder_copyRulesExpectedTransferDates.setMultivalue(true);
		folder_copyRulesExpectedTransferDates.setUndeletable(true);
		folder_copyRulesExpectedTransferDates.setEssential(true);
		MetadataBuilder folder_copyStatus = folderSchema.create("copyStatus").setType(MetadataValueType.ENUM);
		folder_copyStatus.setDefaultRequirement(true);
		folder_copyStatus.setUndeletable(true);
		folder_copyStatus.setEssential(true);
		folder_copyStatus.defineAsEnum(CopyType.class);
		MetadataBuilder folder_copyStatusEntered = folderSchema.create("copyStatusEntered").setType(MetadataValueType.ENUM);
		folder_copyStatusEntered.setUndeletable(true);
		folder_copyStatusEntered.setEssential(true);
		folder_copyStatusEntered.defineAsEnum(CopyType.class);
		MetadataBuilder folder_createdBy = folderSchema.get("createdBy");
		folder_createdBy.setSystemReserved(true);
		folder_createdBy.setUndeletable(true);
		folder_createdBy.setEssential(true);
		MetadataBuilder folder_createdOn = folderSchema.get("createdOn");
		folder_createdOn.setSystemReserved(true);
		folder_createdOn.setUndeletable(true);
		folder_createdOn.setEssential(true);
		folder_createdOn.setSortable(true);
		MetadataBuilder folder_decommissioningDate = folderSchema.create("decommissioningDate").setType(MetadataValueType.DATE);
		folder_decommissioningDate.setUndeletable(true);
		folder_decommissioningDate.setEssential(true);
		MetadataBuilder folder_deleted = folderSchema.get("deleted");
		folder_deleted.setSystemReserved(true);
		folder_deleted.setUndeletable(true);
		folder_deleted.setEssential(true);
		MetadataBuilder folder_denyTokens = folderSchema.get("denyTokens");
		folder_denyTokens.setMultivalue(true);
		folder_denyTokens.setSystemReserved(true);
		folder_denyTokens.setUndeletable(true);
		folder_denyTokens.setEssential(true);
		folder_denyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder folder_description = folderSchema.create("description").setType(MetadataValueType.TEXT);
		folder_description.setUndeletable(true);
		folder_description.setEssentialInSummary(true);
		folder_description.setSearchable(true);
		MetadataBuilder folder_detachedauthorizations = folderSchema.get("detachedauthorizations");
		folder_detachedauthorizations.setSystemReserved(true);
		folder_detachedauthorizations.setUndeletable(true);
		folder_detachedauthorizations.setEssential(true);
		MetadataBuilder folder_enteredClosingDate = folderSchema.create("enteredClosingDate").setType(MetadataValueType.DATE);
		folder_enteredClosingDate.setUndeletable(true);
		folder_enteredClosingDate.setEssential(true);
		MetadataBuilder folder_expectedDepositDate = folderSchema.create("expectedDepositDate").setType(MetadataValueType.DATE);
		folder_expectedDepositDate.setUndeletable(true);
		folder_expectedDepositDate.setEssential(true);
		MetadataBuilder folder_expectedDestructionDate = folderSchema.create("expectedDestructionDate")
				.setType(MetadataValueType.DATE);
		folder_expectedDestructionDate.setUndeletable(true);
		folder_expectedDestructionDate.setEssential(true);
		MetadataBuilder folder_expectedTransferDate = folderSchema.create("expectedTransferDate").setType(MetadataValueType.DATE);
		folder_expectedTransferDate.setUndeletable(true);
		folder_expectedTransferDate.setEssential(true);
		MetadataBuilder folder_filingSpace = folderSchema.create("filingSpace").setType(MetadataValueType.REFERENCE);
		folder_filingSpace.setUndeletable(true);
		folder_filingSpace.defineReferencesTo(filingSpaceSchemaType);
		MetadataBuilder folder_filingSpaceCode = folderSchema.create("filingSpaceCode").setType(MetadataValueType.STRING);
		folder_filingSpaceCode.setUndeletable(true);
		folder_filingSpaceCode.setEssential(true);
		MetadataBuilder folder_filingSpaceEntered = folderSchema.create("filingSpaceEntered")
				.setType(MetadataValueType.REFERENCE);
		folder_filingSpaceEntered.setUndeletable(true);
		folder_filingSpaceEntered.setEnabled(false);
		folder_filingSpaceEntered.defineReferencesTo(filingSpaceSchemaType);
		MetadataBuilder folder_folderType = folderSchema.create("folderType").setType(MetadataValueType.STRING);
		folder_folderType.setSystemReserved(true);
		folder_folderType.setUndeletable(true);
		MetadataBuilder folder_followers = folderSchema.get("followers");
		folder_followers.setMultivalue(true);
		folder_followers.setSystemReserved(true);
		folder_followers.setUndeletable(true);
		folder_followers.setEssential(true);
		folder_followers.setSearchable(true);
		MetadataBuilder folder_formCreatedBy = folderSchema.create("formCreatedBy").setType(MetadataValueType.REFERENCE);
		folder_formCreatedBy.setSystemReserved(true);
		folder_formCreatedBy.setUndeletable(true);
		folder_formCreatedBy.defineReferencesTo(userSchemaType);
		MetadataBuilder folder_formCreatedOn = folderSchema.create("formCreatedOn").setType(MetadataValueType.DATE_TIME);
		folder_formCreatedOn.setSystemReserved(true);
		folder_formCreatedOn.setUndeletable(true);
		MetadataBuilder folder_formModifiedBy = folderSchema.create("formModifiedBy").setType(MetadataValueType.REFERENCE);
		folder_formModifiedBy.setSystemReserved(true);
		folder_formModifiedBy.setUndeletable(true);
		folder_formModifiedBy.defineReferencesTo(userSchemaType);
		MetadataBuilder folder_formModifiedOn = folderSchema.create("formModifiedOn").setType(MetadataValueType.DATE_TIME);
		folder_formModifiedOn.setSystemReserved(true);
		folder_formModifiedOn.setUndeletable(true);
		MetadataBuilder folder_id = folderSchema.get("id");
		folder_id.setDefaultRequirement(true);
		folder_id.setSystemReserved(true);
		folder_id.setUndeletable(true);
		folder_id.setEssential(true);
		folder_id.setSearchable(true);
		folder_id.setSortable(true);
		folder_id.setUniqueValue(true);
		folder_id.setUnmodifiable(true);
		MetadataBuilder folder_inactiveDisposalType = folderSchema.create("inactiveDisposalType").setType(MetadataValueType.ENUM);
		folder_inactiveDisposalType.setUndeletable(true);
		folder_inactiveDisposalType.setEssential(true);
		folder_inactiveDisposalType.defineAsEnum(DisposalType.class);
		MetadataBuilder folder_inheritedauthorizations = folderSchema.get("inheritedauthorizations");
		folder_inheritedauthorizations.setMultivalue(true);
		folder_inheritedauthorizations.setSystemReserved(true);
		folder_inheritedauthorizations.setUndeletable(true);
		folder_inheritedauthorizations.setEssential(true);
		MetadataBuilder folder_keywords = folderSchema.create("keywords").setType(MetadataValueType.STRING);
		folder_keywords.setMultivalue(true);
		folder_keywords.setUndeletable(true);
		folder_keywords.setSearchable(true);
		MetadataBuilder folder_legacyIdentifier = folderSchema.get("legacyIdentifier");
		folder_legacyIdentifier.setDefaultRequirement(true);
		folder_legacyIdentifier.setSystemReserved(true);
		folder_legacyIdentifier.setUndeletable(true);
		folder_legacyIdentifier.setEssential(true);
		folder_legacyIdentifier.setSearchable(true);
		folder_legacyIdentifier.setUniqueValue(true);
		folder_legacyIdentifier.setUnmodifiable(true);
		MetadataBuilder folder_linearSize = folderSchema.create("linearSize").setType(MetadataValueType.NUMBER);
		folder_linearSize.setUndeletable(true);
		folder_linearSize.setEssential(true);
		MetadataBuilder folder_mainCopyRule = folderSchema.create("mainCopyRule").setType(MetadataValueType.STRUCTURE);
		folder_mainCopyRule.setUndeletable(true);
		folder_mainCopyRule.setEssential(true);
		folder_mainCopyRule.defineStructureFactory(CopyRetentionRuleFactory.class);
		MetadataBuilder folder_mainCopyRuleIdEntered = folderSchema.create("mainCopyRuleIdEntered")
				.setType(MetadataValueType.STRING);
		folder_mainCopyRuleIdEntered.setUndeletable(true);
		MetadataBuilder folder_manualTokens = folderSchema.get("manualTokens");
		folder_manualTokens.setMultivalue(true);
		folder_manualTokens.setSystemReserved(true);
		folder_manualTokens.setUndeletable(true);
		folder_manualTokens.setEssential(true);
		folder_manualTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder folder_markedForPreviewConversion = folderSchema.get("markedForPreviewConversion");
		folder_markedForPreviewConversion.setSystemReserved(true);
		folder_markedForPreviewConversion.setUndeletable(true);
		folder_markedForPreviewConversion.setEssential(true);
		MetadataBuilder folder_mediaType = folderSchema.create("mediaType").setType(MetadataValueType.ENUM);
		folder_mediaType.setUndeletable(true);
		folder_mediaType.setEssential(true);
		folder_mediaType.defineAsEnum(FolderMediaType.class);
		MetadataBuilder folder_mediumTypes = folderSchema.create("mediumTypes").setType(MetadataValueType.REFERENCE);
		folder_mediumTypes.setMultivalue(true);
		folder_mediumTypes.setUndeletable(true);
		folder_mediumTypes.setEssential(true);
		folder_mediumTypes.defineReferencesTo(ddvMediumTypeSchemaType);
		MetadataBuilder folder_modifiedBy = folderSchema.get("modifiedBy");
		folder_modifiedBy.setSystemReserved(true);
		folder_modifiedBy.setUndeletable(true);
		folder_modifiedBy.setEssential(true);
		MetadataBuilder folder_modifiedOn = folderSchema.get("modifiedOn");
		folder_modifiedOn.setSystemReserved(true);
		folder_modifiedOn.setUndeletable(true);
		folder_modifiedOn.setEssential(true);
		folder_modifiedOn.setSortable(true);
		MetadataBuilder folder_openingDate = folderSchema.create("openingDate").setType(MetadataValueType.DATE);
		folder_openingDate.setDefaultRequirement(true);
		folder_openingDate.setUndeletable(true);
		folder_openingDate.setEssential(true);
		MetadataBuilder folder_parentFolder = folderSchema.create("parentFolder").setType(MetadataValueType.REFERENCE);
		folder_parentFolder.setUndeletable(true);
		folder_parentFolder.setEssential(true);
		folder_parentFolder.defineChildOfRelationshipToType(folderSchemaType);
		MetadataBuilder folder_parentpath = folderSchema.get("parentpath");
		folder_parentpath.setMultivalue(true);
		folder_parentpath.setSystemReserved(true);
		folder_parentpath.setUndeletable(true);
		folder_parentpath.setEssential(true);
		MetadataBuilder folder_path = folderSchema.get("path");
		folder_path.setMultivalue(true);
		folder_path.setSystemReserved(true);
		folder_path.setUndeletable(true);
		folder_path.setEssential(true);
		MetadataBuilder folder_pathParts = folderSchema.get("pathParts");
		folder_pathParts.setMultivalue(true);
		folder_pathParts.setSystemReserved(true);
		folder_pathParts.setUndeletable(true);
		folder_pathParts.setEssential(true);
		MetadataBuilder folder_permissionStatus = folderSchema.create("permissionStatus").setType(MetadataValueType.ENUM);
		folder_permissionStatus.setSystemReserved(true);
		folder_permissionStatus.setUndeletable(true);
		folder_permissionStatus.defineAsEnum(FolderStatus.class);
		MetadataBuilder folder_principalpath = folderSchema.get("principalpath");
		folder_principalpath.setSystemReserved(true);
		folder_principalpath.setUndeletable(true);
		folder_principalpath.setEssential(true);
		MetadataBuilder folder_removedauthorizations = folderSchema.get("removedauthorizations");
		folder_removedauthorizations.setMultivalue(true);
		folder_removedauthorizations.setSystemReserved(true);
		folder_removedauthorizations.setUndeletable(true);
		folder_removedauthorizations.setEssential(true);
		MetadataBuilder folder_retentionRule = folderSchema.create("retentionRule").setType(MetadataValueType.REFERENCE);
		folder_retentionRule.setDefaultRequirement(true);
		folder_retentionRule.setUndeletable(true);
		folder_retentionRule.setEssential(true);
		folder_retentionRule.defineReferencesTo(retentionRuleSchemaType);
		MetadataBuilder folder_retentionRuleEntered = folderSchema.create("retentionRuleEntered")
				.setType(MetadataValueType.REFERENCE);
		folder_retentionRuleEntered.setUndeletable(true);
		folder_retentionRuleEntered.setEssential(true);
		folder_retentionRuleEntered.defineReferencesTo(retentionRuleSchemaType);
		MetadataBuilder folder_ruleAdminUnit = folderSchema.create("ruleAdminUnit").setType(MetadataValueType.REFERENCE);
		folder_ruleAdminUnit.setMultivalue(true);
		folder_ruleAdminUnit.setUndeletable(true);
		folder_ruleAdminUnit.setEssential(true);
		folder_ruleAdminUnit.defineReferencesTo(administrativeUnitSchemaType);
		MetadataBuilder folder_schema = folderSchema.get("schema");
		folder_schema.setDefaultRequirement(true);
		folder_schema.setSystemReserved(true);
		folder_schema.setUndeletable(true);
		folder_schema.setEssential(true);
		MetadataBuilder folder_searchable = folderSchema.get("searchable");
		folder_searchable.setSystemReserved(true);
		folder_searchable.setUndeletable(true);
		folder_searchable.setEssential(true);
		MetadataBuilder folder_semiactiveRetentionPeriodCode = folderSchema.create("semiactiveRetentionPeriodCode")
				.setType(MetadataValueType.STRING);
		folder_semiactiveRetentionPeriodCode.setUndeletable(true);
		MetadataBuilder folder_semiactiveRetentionType = folderSchema.create("semiactiveRetentionType")
				.setType(MetadataValueType.ENUM);
		folder_semiactiveRetentionType.setUndeletable(true);
		folder_semiactiveRetentionType.setEssential(true);
		folder_semiactiveRetentionType.defineAsEnum(RetentionType.class);
		MetadataBuilder folder_shareDenyTokens = folderSchema.get("shareDenyTokens");
		folder_shareDenyTokens.setMultivalue(true);
		folder_shareDenyTokens.setSystemReserved(true);
		folder_shareDenyTokens.setUndeletable(true);
		folder_shareDenyTokens.setEssential(true);
		folder_shareDenyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder folder_shareTokens = folderSchema.get("shareTokens");
		folder_shareTokens.setMultivalue(true);
		folder_shareTokens.setSystemReserved(true);
		folder_shareTokens.setUndeletable(true);
		folder_shareTokens.setEssential(true);
		folder_shareTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder folder_title = folderSchema.get("title");
		folder_title.setDefaultRequirement(true);
		folder_title.setUndeletable(true);
		folder_title.setEssential(true);
		folder_title.setSchemaAutocomplete(true);
		folder_title.setSearchable(true);
		MetadataBuilder folder_tokens = folderSchema.get("tokens");
		folder_tokens.setMultivalue(true);
		folder_tokens.setSystemReserved(true);
		folder_tokens.setUndeletable(true);
		folder_tokens.setEssential(true);
		MetadataBuilder folder_type = folderSchema.create("type").setType(MetadataValueType.REFERENCE);
		folder_type.setUndeletable(true);
		folder_type.setEssential(true);
		folder_type.defineReferencesTo(ddvFolderTypeSchemaType);
		MetadataBuilder folder_uniformSubdivision = folderSchema.create("uniformSubdivision")
				.setType(MetadataValueType.REFERENCE);
		folder_uniformSubdivision.setUndeletable(true);
		folder_uniformSubdivision.setEssential(true);
		folder_uniformSubdivision.defineReferencesTo(uniformSubdivisionSchemaType);
		MetadataBuilder folder_uniformSubdivisionEntered = folderSchema.create("uniformSubdivisionEntered")
				.setType(MetadataValueType.REFERENCE);
		folder_uniformSubdivisionEntered.setUndeletable(true);
		folder_uniformSubdivisionEntered.setEssential(true);
		folder_uniformSubdivisionEntered.defineReferencesTo(uniformSubdivisionSchemaType);
		MetadataBuilder folder_visibleInTrees = folderSchema.get("visibleInTrees");
		folder_visibleInTrees.setSystemReserved(true);
		folder_visibleInTrees.setUndeletable(true);
		folder_visibleInTrees.setEssential(true);
		MetadataBuilder retentionRule_administrativeUnits = retentionRuleSchema.create("administrativeUnits")
				.setType(MetadataValueType.REFERENCE);
		retentionRule_administrativeUnits.setMultivalue(true);
		retentionRule_administrativeUnits.setUndeletable(true);
		retentionRule_administrativeUnits.setEssential(true);
		retentionRule_administrativeUnits.defineReferencesTo(administrativeUnitSchemaType);
		MetadataBuilder retentionRule_allauthorizations = retentionRuleSchema.get("allauthorizations");
		retentionRule_allauthorizations.setMultivalue(true);
		retentionRule_allauthorizations.setSystemReserved(true);
		retentionRule_allauthorizations.setUndeletable(true);
		retentionRule_allauthorizations.setEssential(true);
		MetadataBuilder retentionRule_approvalDate = retentionRuleSchema.create("approvalDate").setType(MetadataValueType.DATE);
		retentionRule_approvalDate.setUndeletable(true);
		retentionRule_approvalDate.setEssential(true);
		MetadataBuilder retentionRule_approved = retentionRuleSchema.create("approved").setType(MetadataValueType.BOOLEAN);
		retentionRule_approved.setUndeletable(true);
		retentionRule_approved.setEssential(true);
		MetadataBuilder retentionRule_authorizations = retentionRuleSchema.get("authorizations");
		retentionRule_authorizations.setMultivalue(true);
		retentionRule_authorizations.setSystemReserved(true);
		retentionRule_authorizations.setUndeletable(true);
		retentionRule_authorizations.setEssential(true);
		MetadataBuilder retentionRule_code = retentionRuleSchema.create("code").setType(MetadataValueType.STRING);
		retentionRule_code.setDefaultRequirement(true);
		retentionRule_code.setUndeletable(true);
		retentionRule_code.setEssential(true);
		retentionRule_code.setSchemaAutocomplete(true);
		retentionRule_code.setSearchable(true);
		retentionRule_code.setUniqueValue(true);
		MetadataBuilder retentionRule_confidentialDocuments = retentionRuleSchema.create("confidentialDocuments")
				.setType(MetadataValueType.BOOLEAN);
		retentionRule_confidentialDocuments.setUndeletable(true);
		MetadataBuilder retentionRule_copyRetentionRules = retentionRuleSchema.create("copyRetentionRules")
				.setType(MetadataValueType.STRUCTURE);
		retentionRule_copyRetentionRules.setMultivalue(true);
		retentionRule_copyRetentionRules.setUndeletable(true);
		retentionRule_copyRetentionRules.setEssential(true);
		retentionRule_copyRetentionRules.defineStructureFactory(CopyRetentionRuleFactory.class);
		MetadataBuilder retentionRule_copyRulesComment = retentionRuleSchema.create("copyRulesComment")
				.setType(MetadataValueType.TEXT);
		retentionRule_copyRulesComment.setMultivalue(true);
		retentionRule_copyRulesComment.setUndeletable(true);
		retentionRule_copyRulesComment.setEssential(true);
		MetadataBuilder retentionRule_corpus = retentionRuleSchema.create("corpus").setType(MetadataValueType.STRING);
		retentionRule_corpus.setUndeletable(true);
		MetadataBuilder retentionRule_corpusRuleNumber = retentionRuleSchema.create("corpusRuleNumber")
				.setType(MetadataValueType.TEXT);
		retentionRule_corpusRuleNumber.setUndeletable(true);
		MetadataBuilder retentionRule_createdBy = retentionRuleSchema.get("createdBy");
		retentionRule_createdBy.setSystemReserved(true);
		retentionRule_createdBy.setUndeletable(true);
		retentionRule_createdBy.setEssential(true);
		MetadataBuilder retentionRule_createdOn = retentionRuleSchema.get("createdOn");
		retentionRule_createdOn.setSystemReserved(true);
		retentionRule_createdOn.setUndeletable(true);
		retentionRule_createdOn.setEssential(true);
		retentionRule_createdOn.setSortable(true);
		MetadataBuilder retentionRule_deleted = retentionRuleSchema.get("deleted");
		retentionRule_deleted.setSystemReserved(true);
		retentionRule_deleted.setUndeletable(true);
		retentionRule_deleted.setEssential(true);
		MetadataBuilder retentionRule_denyTokens = retentionRuleSchema.get("denyTokens");
		retentionRule_denyTokens.setMultivalue(true);
		retentionRule_denyTokens.setSystemReserved(true);
		retentionRule_denyTokens.setUndeletable(true);
		retentionRule_denyTokens.setEssential(true);
		retentionRule_denyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder retentionRule_description = retentionRuleSchema.create("description").setType(MetadataValueType.TEXT);
		retentionRule_description.setUndeletable(true);
		retentionRule_description.setEssentialInSummary(true);
		retentionRule_description.setSchemaAutocomplete(true);
		MetadataBuilder retentionRule_detachedauthorizations = retentionRuleSchema.get("detachedauthorizations");
		retentionRule_detachedauthorizations.setSystemReserved(true);
		retentionRule_detachedauthorizations.setUndeletable(true);
		retentionRule_detachedauthorizations.setEssential(true);
		MetadataBuilder retentionRule_documentCopyRetentionRules = retentionRuleSchema.create("documentCopyRetentionRules")
				.setType(MetadataValueType.STRUCTURE);
		retentionRule_documentCopyRetentionRules.setMultivalue(true);
		retentionRule_documentCopyRetentionRules.setUndeletable(true);
		retentionRule_documentCopyRetentionRules.defineStructureFactory(CopyRetentionRuleFactory.class);
		MetadataBuilder retentionRule_documentTypes = retentionRuleSchema.create("documentTypes")
				.setType(MetadataValueType.REFERENCE);
		retentionRule_documentTypes.setMultivalue(true);
		retentionRule_documentTypes.setUndeletable(true);
		retentionRule_documentTypes.setEssential(true);
		retentionRule_documentTypes.defineReferencesTo(ddvDocumentTypeSchemaType);
		MetadataBuilder retentionRule_documentTypesDetails = retentionRuleSchema.create("documentTypesDetails")
				.setType(MetadataValueType.STRUCTURE);
		retentionRule_documentTypesDetails.setMultivalue(true);
		retentionRule_documentTypesDetails.setUndeletable(true);
		retentionRule_documentTypesDetails.setEssential(true);
		retentionRule_documentTypesDetails.defineStructureFactory(RetentionRuleDocumentTypeFactory.class);
		MetadataBuilder retentionRule_essentialDocuments = retentionRuleSchema.create("essentialDocuments")
				.setType(MetadataValueType.BOOLEAN);
		retentionRule_essentialDocuments.setUndeletable(true);
		MetadataBuilder retentionRule_folderTypes = retentionRuleSchema.create("folderTypes")
				.setType(MetadataValueType.REFERENCE);
		retentionRule_folderTypes.setMultivalue(true);
		retentionRule_folderTypes.defineReferencesTo(ddvFolderTypeSchemaType);
		MetadataBuilder retentionRule_followers = retentionRuleSchema.get("followers");
		retentionRule_followers.setMultivalue(true);
		retentionRule_followers.setSystemReserved(true);
		retentionRule_followers.setUndeletable(true);
		retentionRule_followers.setEssential(true);
		retentionRule_followers.setSearchable(true);
		MetadataBuilder retentionRule_generalComment = retentionRuleSchema.create("generalComment")
				.setType(MetadataValueType.TEXT);
		retentionRule_generalComment.setUndeletable(true);
		MetadataBuilder retentionRule_history = retentionRuleSchema.create("history").setType(MetadataValueType.TEXT);
		retentionRule_history.setUndeletable(true);
		MetadataBuilder retentionRule_id = retentionRuleSchema.get("id");
		retentionRule_id.setDefaultRequirement(true);
		retentionRule_id.setSystemReserved(true);
		retentionRule_id.setUndeletable(true);
		retentionRule_id.setEssential(true);
		retentionRule_id.setSearchable(true);
		retentionRule_id.setSortable(true);
		retentionRule_id.setUniqueValue(true);
		retentionRule_id.setUnmodifiable(true);
		MetadataBuilder retentionRule_inheritedauthorizations = retentionRuleSchema.get("inheritedauthorizations");
		retentionRule_inheritedauthorizations.setMultivalue(true);
		retentionRule_inheritedauthorizations.setSystemReserved(true);
		retentionRule_inheritedauthorizations.setUndeletable(true);
		retentionRule_inheritedauthorizations.setEssential(true);
		MetadataBuilder retentionRule_juridicReference = retentionRuleSchema.create("juridicReference")
				.setType(MetadataValueType.TEXT);
		retentionRule_juridicReference.setUndeletable(true);
		MetadataBuilder retentionRule_keywords = retentionRuleSchema.create("keywords").setType(MetadataValueType.STRING);
		retentionRule_keywords.setMultivalue(true);
		retentionRule_keywords.setUndeletable(true);
		retentionRule_keywords.setSearchable(true);
		MetadataBuilder retentionRule_legacyIdentifier = retentionRuleSchema.get("legacyIdentifier");
		retentionRule_legacyIdentifier.setDefaultRequirement(true);
		retentionRule_legacyIdentifier.setSystemReserved(true);
		retentionRule_legacyIdentifier.setUndeletable(true);
		retentionRule_legacyIdentifier.setEssential(true);
		retentionRule_legacyIdentifier.setSearchable(true);
		retentionRule_legacyIdentifier.setUniqueValue(true);
		retentionRule_legacyIdentifier.setUnmodifiable(true);
		MetadataBuilder retentionRule_manualTokens = retentionRuleSchema.get("manualTokens");
		retentionRule_manualTokens.setMultivalue(true);
		retentionRule_manualTokens.setSystemReserved(true);
		retentionRule_manualTokens.setUndeletable(true);
		retentionRule_manualTokens.setEssential(true);
		retentionRule_manualTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder retentionRule_markedForPreviewConversion = retentionRuleSchema.get("markedForPreviewConversion");
		retentionRule_markedForPreviewConversion.setSystemReserved(true);
		retentionRule_markedForPreviewConversion.setUndeletable(true);
		retentionRule_markedForPreviewConversion.setEssential(true);
		MetadataBuilder retentionRule_modifiedBy = retentionRuleSchema.get("modifiedBy");
		retentionRule_modifiedBy.setSystemReserved(true);
		retentionRule_modifiedBy.setUndeletable(true);
		retentionRule_modifiedBy.setEssential(true);
		MetadataBuilder retentionRule_modifiedOn = retentionRuleSchema.get("modifiedOn");
		retentionRule_modifiedOn.setSystemReserved(true);
		retentionRule_modifiedOn.setUndeletable(true);
		retentionRule_modifiedOn.setEssential(true);
		retentionRule_modifiedOn.setSortable(true);
		MetadataBuilder retentionRule_parentpath = retentionRuleSchema.get("parentpath");
		retentionRule_parentpath.setMultivalue(true);
		retentionRule_parentpath.setSystemReserved(true);
		retentionRule_parentpath.setUndeletable(true);
		retentionRule_parentpath.setEssential(true);
		MetadataBuilder retentionRule_path = retentionRuleSchema.get("path");
		retentionRule_path.setMultivalue(true);
		retentionRule_path.setSystemReserved(true);
		retentionRule_path.setUndeletable(true);
		retentionRule_path.setEssential(true);
		MetadataBuilder retentionRule_pathParts = retentionRuleSchema.get("pathParts");
		retentionRule_pathParts.setMultivalue(true);
		retentionRule_pathParts.setSystemReserved(true);
		retentionRule_pathParts.setUndeletable(true);
		retentionRule_pathParts.setEssential(true);
		MetadataBuilder retentionRule_principalDefaultDocumentCopyRetentionRule = retentionRuleSchema
				.create("principalDefaultDocumentCopyRetentionRule").setType(MetadataValueType.STRUCTURE);
		retentionRule_principalDefaultDocumentCopyRetentionRule.setUndeletable(true);
		retentionRule_principalDefaultDocumentCopyRetentionRule.defineStructureFactory(CopyRetentionRuleFactory.class);
		MetadataBuilder retentionRule_principalpath = retentionRuleSchema.get("principalpath");
		retentionRule_principalpath.setSystemReserved(true);
		retentionRule_principalpath.setUndeletable(true);
		retentionRule_principalpath.setEssential(true);
		MetadataBuilder retentionRule_removedauthorizations = retentionRuleSchema.get("removedauthorizations");
		retentionRule_removedauthorizations.setMultivalue(true);
		retentionRule_removedauthorizations.setSystemReserved(true);
		retentionRule_removedauthorizations.setUndeletable(true);
		retentionRule_removedauthorizations.setEssential(true);
		MetadataBuilder retentionRule_responsibleAdministrativeUnits = retentionRuleSchema
				.create("responsibleAdministrativeUnits").setType(MetadataValueType.BOOLEAN);
		retentionRule_responsibleAdministrativeUnits.setUndeletable(true);
		retentionRule_responsibleAdministrativeUnits.setEssential(true);
		MetadataBuilder retentionRule_schema = retentionRuleSchema.get("schema");
		retentionRule_schema.setDefaultRequirement(true);
		retentionRule_schema.setSystemReserved(true);
		retentionRule_schema.setUndeletable(true);
		retentionRule_schema.setEssential(true);
		MetadataBuilder retentionRule_scope = retentionRuleSchema.create("scope").setType(MetadataValueType.ENUM);
		retentionRule_scope.setUndeletable(true);
		retentionRule_scope.defineAsEnum(RetentionRuleScope.class);
		MetadataBuilder retentionRule_searchable = retentionRuleSchema.get("searchable");
		retentionRule_searchable.setSystemReserved(true);
		retentionRule_searchable.setUndeletable(true);
		retentionRule_searchable.setEssential(true);
		MetadataBuilder retentionRule_secondaryDefaultDocumentCopyRetentionRule = retentionRuleSchema
				.create("secondaryDefaultDocumentCopyRetentionRule").setType(MetadataValueType.STRUCTURE);
		retentionRule_secondaryDefaultDocumentCopyRetentionRule.setUndeletable(true);
		retentionRule_secondaryDefaultDocumentCopyRetentionRule.defineStructureFactory(CopyRetentionRuleFactory.class);
		MetadataBuilder retentionRule_shareDenyTokens = retentionRuleSchema.get("shareDenyTokens");
		retentionRule_shareDenyTokens.setMultivalue(true);
		retentionRule_shareDenyTokens.setSystemReserved(true);
		retentionRule_shareDenyTokens.setUndeletable(true);
		retentionRule_shareDenyTokens.setEssential(true);
		retentionRule_shareDenyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder retentionRule_shareTokens = retentionRuleSchema.get("shareTokens");
		retentionRule_shareTokens.setMultivalue(true);
		retentionRule_shareTokens.setSystemReserved(true);
		retentionRule_shareTokens.setUndeletable(true);
		retentionRule_shareTokens.setEssential(true);
		retentionRule_shareTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder retentionRule_title = retentionRuleSchema.get("title");
		retentionRule_title.setDefaultRequirement(true);
		retentionRule_title.setUndeletable(true);
		retentionRule_title.setEssential(true);
		retentionRule_title.setSchemaAutocomplete(true);
		retentionRule_title.setSearchable(true);
		MetadataBuilder retentionRule_tokens = retentionRuleSchema.get("tokens");
		retentionRule_tokens.setMultivalue(true);
		retentionRule_tokens.setSystemReserved(true);
		retentionRule_tokens.setUndeletable(true);
		retentionRule_tokens.setEssential(true);
		MetadataBuilder retentionRule_visibleInTrees = retentionRuleSchema.get("visibleInTrees");
		retentionRule_visibleInTrees.setSystemReserved(true);
		retentionRule_visibleInTrees.setUndeletable(true);
		retentionRule_visibleInTrees.setEssential(true);
		MetadataBuilder storageSpace_allauthorizations = storageSpaceSchema.get("allauthorizations");
		storageSpace_allauthorizations.setMultivalue(true);
		storageSpace_allauthorizations.setSystemReserved(true);
		storageSpace_allauthorizations.setUndeletable(true);
		storageSpace_allauthorizations.setEssential(true);
		MetadataBuilder storageSpace_authorizations = storageSpaceSchema.get("authorizations");
		storageSpace_authorizations.setMultivalue(true);
		storageSpace_authorizations.setSystemReserved(true);
		storageSpace_authorizations.setUndeletable(true);
		storageSpace_authorizations.setEssential(true);
		MetadataBuilder storageSpace_capacity = storageSpaceSchema.create("capacity").setType(MetadataValueType.NUMBER);
		storageSpace_capacity.setUndeletable(true);
		storageSpace_capacity.setEssential(true);
		MetadataBuilder storageSpace_code = storageSpaceSchema.create("code").setType(MetadataValueType.STRING);
		storageSpace_code.setDefaultRequirement(true);
		storageSpace_code.setUndeletable(true);
		storageSpace_code.setEssential(true);
		storageSpace_code.setSchemaAutocomplete(true);
		storageSpace_code.setSearchable(true);
		storageSpace_code.setUniqueValue(true);
		MetadataBuilder storageSpace_comments = storageSpaceSchema.create("comments").setType(MetadataValueType.STRUCTURE);
		storageSpace_comments.setMultivalue(true);
		storageSpace_comments.setUndeletable(true);
		storageSpace_comments.defineStructureFactory(CommentFactory.class);
		MetadataBuilder storageSpace_createdBy = storageSpaceSchema.get("createdBy");
		storageSpace_createdBy.setSystemReserved(true);
		storageSpace_createdBy.setUndeletable(true);
		storageSpace_createdBy.setEssential(true);
		MetadataBuilder storageSpace_createdOn = storageSpaceSchema.get("createdOn");
		storageSpace_createdOn.setSystemReserved(true);
		storageSpace_createdOn.setUndeletable(true);
		storageSpace_createdOn.setEssential(true);
		storageSpace_createdOn.setSortable(true);
		MetadataBuilder storageSpace_decommissioningType = storageSpaceSchema.create("decommissioningType")
				.setType(MetadataValueType.ENUM);
		storageSpace_decommissioningType.setUndeletable(true);
		storageSpace_decommissioningType.setEssential(true);
		storageSpace_decommissioningType.defineAsEnum(DecommissioningType.class);
		MetadataBuilder storageSpace_deleted = storageSpaceSchema.get("deleted");
		storageSpace_deleted.setSystemReserved(true);
		storageSpace_deleted.setUndeletable(true);
		storageSpace_deleted.setEssential(true);
		MetadataBuilder storageSpace_denyTokens = storageSpaceSchema.get("denyTokens");
		storageSpace_denyTokens.setMultivalue(true);
		storageSpace_denyTokens.setSystemReserved(true);
		storageSpace_denyTokens.setUndeletable(true);
		storageSpace_denyTokens.setEssential(true);
		storageSpace_denyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder storageSpace_description = storageSpaceSchema.create("description").setType(MetadataValueType.STRING);
		storageSpace_description.setUndeletable(true);
		storageSpace_description.setEssentialInSummary(true);
		storageSpace_description.setSearchable(true);
		MetadataBuilder storageSpace_detachedauthorizations = storageSpaceSchema.get("detachedauthorizations");
		storageSpace_detachedauthorizations.setSystemReserved(true);
		storageSpace_detachedauthorizations.setUndeletable(true);
		storageSpace_detachedauthorizations.setEssential(true);
		MetadataBuilder storageSpace_followers = storageSpaceSchema.get("followers");
		storageSpace_followers.setMultivalue(true);
		storageSpace_followers.setSystemReserved(true);
		storageSpace_followers.setUndeletable(true);
		storageSpace_followers.setEssential(true);
		storageSpace_followers.setSearchable(true);
		MetadataBuilder storageSpace_id = storageSpaceSchema.get("id");
		storageSpace_id.setDefaultRequirement(true);
		storageSpace_id.setSystemReserved(true);
		storageSpace_id.setUndeletable(true);
		storageSpace_id.setEssential(true);
		storageSpace_id.setSearchable(true);
		storageSpace_id.setSortable(true);
		storageSpace_id.setUniqueValue(true);
		storageSpace_id.setUnmodifiable(true);
		MetadataBuilder storageSpace_inheritedauthorizations = storageSpaceSchema.get("inheritedauthorizations");
		storageSpace_inheritedauthorizations.setMultivalue(true);
		storageSpace_inheritedauthorizations.setSystemReserved(true);
		storageSpace_inheritedauthorizations.setUndeletable(true);
		storageSpace_inheritedauthorizations.setEssential(true);
		MetadataBuilder storageSpace_legacyIdentifier = storageSpaceSchema.get("legacyIdentifier");
		storageSpace_legacyIdentifier.setDefaultRequirement(true);
		storageSpace_legacyIdentifier.setSystemReserved(true);
		storageSpace_legacyIdentifier.setUndeletable(true);
		storageSpace_legacyIdentifier.setEssential(true);
		storageSpace_legacyIdentifier.setSearchable(true);
		storageSpace_legacyIdentifier.setUniqueValue(true);
		storageSpace_legacyIdentifier.setUnmodifiable(true);
		MetadataBuilder storageSpace_manualTokens = storageSpaceSchema.get("manualTokens");
		storageSpace_manualTokens.setMultivalue(true);
		storageSpace_manualTokens.setSystemReserved(true);
		storageSpace_manualTokens.setUndeletable(true);
		storageSpace_manualTokens.setEssential(true);
		storageSpace_manualTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder storageSpace_markedForPreviewConversion = storageSpaceSchema.get("markedForPreviewConversion");
		storageSpace_markedForPreviewConversion.setSystemReserved(true);
		storageSpace_markedForPreviewConversion.setUndeletable(true);
		storageSpace_markedForPreviewConversion.setEssential(true);
		MetadataBuilder storageSpace_modifiedBy = storageSpaceSchema.get("modifiedBy");
		storageSpace_modifiedBy.setSystemReserved(true);
		storageSpace_modifiedBy.setUndeletable(true);
		storageSpace_modifiedBy.setEssential(true);
		MetadataBuilder storageSpace_modifiedOn = storageSpaceSchema.get("modifiedOn");
		storageSpace_modifiedOn.setSystemReserved(true);
		storageSpace_modifiedOn.setUndeletable(true);
		storageSpace_modifiedOn.setEssential(true);
		storageSpace_modifiedOn.setSortable(true);
		MetadataBuilder storageSpace_parentStorageSpace = storageSpaceSchema.create("parentStorageSpace")
				.setType(MetadataValueType.REFERENCE);
		storageSpace_parentStorageSpace.setUndeletable(true);
		storageSpace_parentStorageSpace.setEssential(true);
		storageSpace_parentStorageSpace.defineChildOfRelationshipToType(storageSpaceSchemaType);
		MetadataBuilder storageSpace_parentpath = storageSpaceSchema.get("parentpath");
		storageSpace_parentpath.setMultivalue(true);
		storageSpace_parentpath.setSystemReserved(true);
		storageSpace_parentpath.setUndeletable(true);
		storageSpace_parentpath.setEssential(true);
		MetadataBuilder storageSpace_path = storageSpaceSchema.get("path");
		storageSpace_path.setMultivalue(true);
		storageSpace_path.setSystemReserved(true);
		storageSpace_path.setUndeletable(true);
		storageSpace_path.setEssential(true);
		MetadataBuilder storageSpace_pathParts = storageSpaceSchema.get("pathParts");
		storageSpace_pathParts.setMultivalue(true);
		storageSpace_pathParts.setSystemReserved(true);
		storageSpace_pathParts.setUndeletable(true);
		storageSpace_pathParts.setEssential(true);
		MetadataBuilder storageSpace_principalpath = storageSpaceSchema.get("principalpath");
		storageSpace_principalpath.setSystemReserved(true);
		storageSpace_principalpath.setUndeletable(true);
		storageSpace_principalpath.setEssential(true);
		MetadataBuilder storageSpace_removedauthorizations = storageSpaceSchema.get("removedauthorizations");
		storageSpace_removedauthorizations.setMultivalue(true);
		storageSpace_removedauthorizations.setSystemReserved(true);
		storageSpace_removedauthorizations.setUndeletable(true);
		storageSpace_removedauthorizations.setEssential(true);
		MetadataBuilder storageSpace_schema = storageSpaceSchema.get("schema");
		storageSpace_schema.setDefaultRequirement(true);
		storageSpace_schema.setSystemReserved(true);
		storageSpace_schema.setUndeletable(true);
		storageSpace_schema.setEssential(true);
		MetadataBuilder storageSpace_searchable = storageSpaceSchema.get("searchable");
		storageSpace_searchable.setSystemReserved(true);
		storageSpace_searchable.setUndeletable(true);
		storageSpace_searchable.setEssential(true);
		MetadataBuilder storageSpace_shareDenyTokens = storageSpaceSchema.get("shareDenyTokens");
		storageSpace_shareDenyTokens.setMultivalue(true);
		storageSpace_shareDenyTokens.setSystemReserved(true);
		storageSpace_shareDenyTokens.setUndeletable(true);
		storageSpace_shareDenyTokens.setEssential(true);
		storageSpace_shareDenyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder storageSpace_shareTokens = storageSpaceSchema.get("shareTokens");
		storageSpace_shareTokens.setMultivalue(true);
		storageSpace_shareTokens.setSystemReserved(true);
		storageSpace_shareTokens.setUndeletable(true);
		storageSpace_shareTokens.setEssential(true);
		storageSpace_shareTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder storageSpace_title = storageSpaceSchema.get("title");
		storageSpace_title.setDefaultRequirement(true);
		storageSpace_title.setUndeletable(true);
		storageSpace_title.setEssential(true);
		storageSpace_title.setSchemaAutocomplete(true);
		storageSpace_title.setSearchable(true);
		MetadataBuilder storageSpace_tokens = storageSpaceSchema.get("tokens");
		storageSpace_tokens.setMultivalue(true);
		storageSpace_tokens.setSystemReserved(true);
		storageSpace_tokens.setUndeletable(true);
		storageSpace_tokens.setEssential(true);
		MetadataBuilder storageSpace_type = storageSpaceSchema.create("type").setType(MetadataValueType.REFERENCE);
		storageSpace_type.setUndeletable(true);
		storageSpace_type.setEssential(true);
		storageSpace_type.defineReferencesTo(ddvStorageSpaceTypeSchemaType);
		MetadataBuilder storageSpace_visibleInTrees = storageSpaceSchema.get("visibleInTrees");
		storageSpace_visibleInTrees.setSystemReserved(true);
		storageSpace_visibleInTrees.setUndeletable(true);
		storageSpace_visibleInTrees.setEssential(true);
		MetadataBuilder uniformSubdivision_allauthorizations = uniformSubdivisionSchema.get("allauthorizations");
		uniformSubdivision_allauthorizations.setMultivalue(true);
		uniformSubdivision_allauthorizations.setSystemReserved(true);
		uniformSubdivision_allauthorizations.setUndeletable(true);
		uniformSubdivision_allauthorizations.setEssential(true);
		MetadataBuilder uniformSubdivision_authorizations = uniformSubdivisionSchema.get("authorizations");
		uniformSubdivision_authorizations.setMultivalue(true);
		uniformSubdivision_authorizations.setSystemReserved(true);
		uniformSubdivision_authorizations.setUndeletable(true);
		uniformSubdivision_authorizations.setEssential(true);
		MetadataBuilder uniformSubdivision_code = uniformSubdivisionSchema.create("code").setType(MetadataValueType.STRING);
		uniformSubdivision_code.setDefaultRequirement(true);
		uniformSubdivision_code.setUndeletable(true);
		uniformSubdivision_code.setEssential(true);
		uniformSubdivision_code.setSearchable(true);
		uniformSubdivision_code.setUniqueValue(true);
		MetadataBuilder uniformSubdivision_comments = uniformSubdivisionSchema.create("comments")
				.setType(MetadataValueType.STRUCTURE);
		uniformSubdivision_comments.setMultivalue(true);
		uniformSubdivision_comments.setUndeletable(true);
		uniformSubdivision_comments.defineStructureFactory(CommentFactory.class);
		MetadataBuilder uniformSubdivision_createdBy = uniformSubdivisionSchema.get("createdBy");
		uniformSubdivision_createdBy.setSystemReserved(true);
		uniformSubdivision_createdBy.setUndeletable(true);
		uniformSubdivision_createdBy.setEssential(true);
		MetadataBuilder uniformSubdivision_createdOn = uniformSubdivisionSchema.get("createdOn");
		uniformSubdivision_createdOn.setSystemReserved(true);
		uniformSubdivision_createdOn.setUndeletable(true);
		uniformSubdivision_createdOn.setEssential(true);
		uniformSubdivision_createdOn.setSortable(true);
		MetadataBuilder uniformSubdivision_deleted = uniformSubdivisionSchema.get("deleted");
		uniformSubdivision_deleted.setSystemReserved(true);
		uniformSubdivision_deleted.setUndeletable(true);
		uniformSubdivision_deleted.setEssential(true);
		MetadataBuilder uniformSubdivision_denyTokens = uniformSubdivisionSchema.get("denyTokens");
		uniformSubdivision_denyTokens.setMultivalue(true);
		uniformSubdivision_denyTokens.setSystemReserved(true);
		uniformSubdivision_denyTokens.setUndeletable(true);
		uniformSubdivision_denyTokens.setEssential(true);
		uniformSubdivision_denyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder uniformSubdivision_description = uniformSubdivisionSchema.create("description")
				.setType(MetadataValueType.STRING);
		uniformSubdivision_description.setUndeletable(true);
		uniformSubdivision_description.setEssentialInSummary(true);
		uniformSubdivision_description.setSearchable(true);
		MetadataBuilder uniformSubdivision_detachedauthorizations = uniformSubdivisionSchema.get("detachedauthorizations");
		uniformSubdivision_detachedauthorizations.setSystemReserved(true);
		uniformSubdivision_detachedauthorizations.setUndeletable(true);
		uniformSubdivision_detachedauthorizations.setEssential(true);
		MetadataBuilder uniformSubdivision_followers = uniformSubdivisionSchema.get("followers");
		uniformSubdivision_followers.setMultivalue(true);
		uniformSubdivision_followers.setSystemReserved(true);
		uniformSubdivision_followers.setUndeletable(true);
		uniformSubdivision_followers.setEssential(true);
		uniformSubdivision_followers.setSearchable(true);
		MetadataBuilder uniformSubdivision_id = uniformSubdivisionSchema.get("id");
		uniformSubdivision_id.setDefaultRequirement(true);
		uniformSubdivision_id.setSystemReserved(true);
		uniformSubdivision_id.setUndeletable(true);
		uniformSubdivision_id.setEssential(true);
		uniformSubdivision_id.setSearchable(true);
		uniformSubdivision_id.setSortable(true);
		uniformSubdivision_id.setUniqueValue(true);
		uniformSubdivision_id.setUnmodifiable(true);
		MetadataBuilder uniformSubdivision_inheritedauthorizations = uniformSubdivisionSchema.get("inheritedauthorizations");
		uniformSubdivision_inheritedauthorizations.setMultivalue(true);
		uniformSubdivision_inheritedauthorizations.setSystemReserved(true);
		uniformSubdivision_inheritedauthorizations.setUndeletable(true);
		uniformSubdivision_inheritedauthorizations.setEssential(true);
		MetadataBuilder uniformSubdivision_legacyIdentifier = uniformSubdivisionSchema.get("legacyIdentifier");
		uniformSubdivision_legacyIdentifier.setDefaultRequirement(true);
		uniformSubdivision_legacyIdentifier.setSystemReserved(true);
		uniformSubdivision_legacyIdentifier.setUndeletable(true);
		uniformSubdivision_legacyIdentifier.setEssential(true);
		uniformSubdivision_legacyIdentifier.setSearchable(true);
		uniformSubdivision_legacyIdentifier.setUniqueValue(true);
		uniformSubdivision_legacyIdentifier.setUnmodifiable(true);
		MetadataBuilder uniformSubdivision_manualTokens = uniformSubdivisionSchema.get("manualTokens");
		uniformSubdivision_manualTokens.setMultivalue(true);
		uniformSubdivision_manualTokens.setSystemReserved(true);
		uniformSubdivision_manualTokens.setUndeletable(true);
		uniformSubdivision_manualTokens.setEssential(true);
		uniformSubdivision_manualTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder uniformSubdivision_markedForPreviewConversion = uniformSubdivisionSchema
				.get("markedForPreviewConversion");
		uniformSubdivision_markedForPreviewConversion.setSystemReserved(true);
		uniformSubdivision_markedForPreviewConversion.setUndeletable(true);
		uniformSubdivision_markedForPreviewConversion.setEssential(true);
		MetadataBuilder uniformSubdivision_modifiedBy = uniformSubdivisionSchema.get("modifiedBy");
		uniformSubdivision_modifiedBy.setSystemReserved(true);
		uniformSubdivision_modifiedBy.setUndeletable(true);
		uniformSubdivision_modifiedBy.setEssential(true);
		MetadataBuilder uniformSubdivision_modifiedOn = uniformSubdivisionSchema.get("modifiedOn");
		uniformSubdivision_modifiedOn.setSystemReserved(true);
		uniformSubdivision_modifiedOn.setUndeletable(true);
		uniformSubdivision_modifiedOn.setEssential(true);
		uniformSubdivision_modifiedOn.setSortable(true);
		MetadataBuilder uniformSubdivision_parentpath = uniformSubdivisionSchema.get("parentpath");
		uniformSubdivision_parentpath.setMultivalue(true);
		uniformSubdivision_parentpath.setSystemReserved(true);
		uniformSubdivision_parentpath.setUndeletable(true);
		uniformSubdivision_parentpath.setEssential(true);
		MetadataBuilder uniformSubdivision_path = uniformSubdivisionSchema.get("path");
		uniformSubdivision_path.setMultivalue(true);
		uniformSubdivision_path.setSystemReserved(true);
		uniformSubdivision_path.setUndeletable(true);
		uniformSubdivision_path.setEssential(true);
		MetadataBuilder uniformSubdivision_pathParts = uniformSubdivisionSchema.get("pathParts");
		uniformSubdivision_pathParts.setMultivalue(true);
		uniformSubdivision_pathParts.setSystemReserved(true);
		uniformSubdivision_pathParts.setUndeletable(true);
		uniformSubdivision_pathParts.setEssential(true);
		MetadataBuilder uniformSubdivision_principalpath = uniformSubdivisionSchema.get("principalpath");
		uniformSubdivision_principalpath.setSystemReserved(true);
		uniformSubdivision_principalpath.setUndeletable(true);
		uniformSubdivision_principalpath.setEssential(true);
		MetadataBuilder uniformSubdivision_removedauthorizations = uniformSubdivisionSchema.get("removedauthorizations");
		uniformSubdivision_removedauthorizations.setMultivalue(true);
		uniformSubdivision_removedauthorizations.setSystemReserved(true);
		uniformSubdivision_removedauthorizations.setUndeletable(true);
		uniformSubdivision_removedauthorizations.setEssential(true);
		MetadataBuilder uniformSubdivision_retentionRule = uniformSubdivisionSchema.create("retentionRule")
				.setType(MetadataValueType.REFERENCE);
		uniformSubdivision_retentionRule.setMultivalue(true);
		uniformSubdivision_retentionRule.setUndeletable(true);
		uniformSubdivision_retentionRule.setEssential(true);
		uniformSubdivision_retentionRule.defineReferencesTo(retentionRuleSchemaType);
		MetadataBuilder uniformSubdivision_schema = uniformSubdivisionSchema.get("schema");
		uniformSubdivision_schema.setDefaultRequirement(true);
		uniformSubdivision_schema.setSystemReserved(true);
		uniformSubdivision_schema.setUndeletable(true);
		uniformSubdivision_schema.setEssential(true);
		MetadataBuilder uniformSubdivision_searchable = uniformSubdivisionSchema.get("searchable");
		uniformSubdivision_searchable.setSystemReserved(true);
		uniformSubdivision_searchable.setUndeletable(true);
		uniformSubdivision_searchable.setEssential(true);
		MetadataBuilder uniformSubdivision_shareDenyTokens = uniformSubdivisionSchema.get("shareDenyTokens");
		uniformSubdivision_shareDenyTokens.setMultivalue(true);
		uniformSubdivision_shareDenyTokens.setSystemReserved(true);
		uniformSubdivision_shareDenyTokens.setUndeletable(true);
		uniformSubdivision_shareDenyTokens.setEssential(true);
		uniformSubdivision_shareDenyTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder uniformSubdivision_shareTokens = uniformSubdivisionSchema.get("shareTokens");
		uniformSubdivision_shareTokens.setMultivalue(true);
		uniformSubdivision_shareTokens.setSystemReserved(true);
		uniformSubdivision_shareTokens.setUndeletable(true);
		uniformSubdivision_shareTokens.setEssential(true);
		uniformSubdivision_shareTokens.defineValidators().add(ManualTokenValidator.class);
		MetadataBuilder uniformSubdivision_title = uniformSubdivisionSchema.get("title");
		uniformSubdivision_title.setDefaultRequirement(true);
		uniformSubdivision_title.setUndeletable(true);
		uniformSubdivision_title.setEssential(true);
		uniformSubdivision_title.setSchemaAutocomplete(true);
		uniformSubdivision_title.setSearchable(true);
		MetadataBuilder uniformSubdivision_tokens = uniformSubdivisionSchema.get("tokens");
		uniformSubdivision_tokens.setMultivalue(true);
		uniformSubdivision_tokens.setSystemReserved(true);
		uniformSubdivision_tokens.setUndeletable(true);
		uniformSubdivision_tokens.setEssential(true);
		MetadataBuilder uniformSubdivision_visibleInTrees = uniformSubdivisionSchema.get("visibleInTrees");
		uniformSubdivision_visibleInTrees.setSystemReserved(true);
		uniformSubdivision_visibleInTrees.setUndeletable(true);
		uniformSubdivision_visibleInTrees.setEssential(true);
		MetadataBuilder userDocument_folder = userDocumentSchema.create("folder").setType(MetadataValueType.REFERENCE);
		userDocument_folder.defineReferencesTo(asList(folderSchema));
		MetadataBuilder userTask_administrativeUnit = userTaskSchema.create("administrativeUnit")
				.setType(MetadataValueType.REFERENCE);
		userTask_administrativeUnit.defineTaxonomyRelationshipToType(administrativeUnitSchemaType);
		MetadataBuilder userTask_linkedDocuments = userTaskSchema.create("linkedDocuments").setType(MetadataValueType.REFERENCE);
		userTask_linkedDocuments.setMultivalue(true);
		userTask_linkedDocuments.defineReferencesTo(documentSchemaType);
		MetadataBuilder userTask_linkedFolders = userTaskSchema.create("linkedFolders").setType(MetadataValueType.REFERENCE);
		userTask_linkedFolders.setMultivalue(true);
		userTask_linkedFolders.defineReferencesTo(folderSchemaType);
		administrativeUnit_allauthorizations.defineDataEntry().asCalculated(AllAuthorizationsCalculator.class);
		administrativeUnit_filingSpacesAdmins.defineDataEntry()
				.asCopied(administrativeUnit_filingSpaces, filingSpace_administrators);
		administrativeUnit_filingSpacesUsers.defineDataEntry().asCopied(administrativeUnit_filingSpaces, filingSpace_users);
		administrativeUnit_inheritedauthorizations.defineDataEntry().asCalculated(InheritedAuthorizationsCalculator.class);
		administrativeUnit_parentpath.defineDataEntry().asCalculated(ParentPathCalculator.class);
		administrativeUnit_path.defineDataEntry().asCalculated(PathCalculator.class);
		administrativeUnit_pathParts.defineDataEntry().asCalculated(PathPartsCalculator.class);
		administrativeUnit_principalpath.defineDataEntry().asCalculated(PrincipalPathCalculator.class);
		administrativeUnit_tokens.defineDataEntry().asCalculated(TokensCalculator2.class);
		administrativeUnit_unitAncestors.defineDataEntry().asCalculated(AdministrativeUnitAncestorsCalculator.class);
		cart_allauthorizations.defineDataEntry().asCalculated(AllAuthorizationsCalculator.class);
		cart_inheritedauthorizations.defineDataEntry().asCalculated(InheritedAuthorizationsCalculator.class);
		cart_parentpath.defineDataEntry().asCalculated(ParentPathCalculator.class);
		cart_path.defineDataEntry().asCalculated(PathCalculator.class);
		cart_pathParts.defineDataEntry().asCalculated(PathPartsCalculator.class);
		cart_principalpath.defineDataEntry().asCalculated(PrincipalPathCalculator.class);
		cart_tokens.defineDataEntry().asCalculated(TokensCalculator2.class);
		category_allauthorizations.defineDataEntry().asCalculated(AllAuthorizationsCalculator.class);
		category_copyRetentionRulesOnDocumentTypes.defineDataEntry()
				.asCalculated(CategoryCopyRetentionRulesOnDocumentTypesCalculator.class);
		category_inheritedauthorizations.defineDataEntry().asCalculated(InheritedAuthorizationsCalculator.class);
		category_level.defineDataEntry().asCalculated(CategoryLevelCalculator.class);
		category_linkable.defineDataEntry().asCalculated(CategoryIsLinkableCalculator.class);
		category_parentpath.defineDataEntry().asCalculated(ParentPathCalculator.class);
		category_path.defineDataEntry().asCalculated(PathCalculator.class);
		category_pathParts.defineDataEntry().asCalculated(PathPartsCalculator.class);
		category_principalpath.defineDataEntry().asCalculated(PrincipalPathCalculator.class);
		category_tokens.defineDataEntry().asCalculated(TokensCalculator2.class);
		containerRecord_allauthorizations.defineDataEntry().asCalculated(AllAuthorizationsCalculator.class);
		containerRecord_inheritedauthorizations.defineDataEntry().asCalculated(InheritedAuthorizationsCalculator.class);
		containerRecord_parentpath.defineDataEntry().asCalculated(ParentPathCalculator.class);
		containerRecord_path.defineDataEntry().asCalculated(PathCalculator.class);
		containerRecord_pathParts.defineDataEntry().asCalculated(PathPartsCalculator.class);
		containerRecord_principalpath.defineDataEntry().asCalculated(PrincipalPathCalculator.class);
		containerRecord_title.defineDataEntry().asCalculated(ContainerTitleCalculator.class);
		containerRecord_tokens.defineDataEntry().asCalculated(TokensCalculator2.class);
		containerRecord_visibleInTrees.defineDataEntry().asCalculated(ContainerRecordTreeVisibilityCalculator.class);
		ddvContainerRecordType_allauthorizations.defineDataEntry().asCalculated(AllAuthorizationsCalculator.class);
		ddvContainerRecordType_inheritedauthorizations.defineDataEntry().asCalculated(InheritedAuthorizationsCalculator.class);
		ddvContainerRecordType_parentpath.defineDataEntry().asCalculated(ParentPathCalculator.class);
		ddvContainerRecordType_path.defineDataEntry().asCalculated(PathCalculator.class);
		ddvContainerRecordType_pathParts.defineDataEntry().asCalculated(PathPartsCalculator.class);
		ddvContainerRecordType_principalpath.defineDataEntry().asCalculated(PrincipalPathCalculator.class);
		ddvContainerRecordType_tokens.defineDataEntry().asCalculated(TokensCalculator2.class);
		ddvDocumentType_allauthorizations.defineDataEntry().asCalculated(AllAuthorizationsCalculator.class);
		ddvDocumentType_inheritedauthorizations.defineDataEntry().asCalculated(InheritedAuthorizationsCalculator.class);
		ddvDocumentType_parentpath.defineDataEntry().asCalculated(ParentPathCalculator.class);
		ddvDocumentType_path.defineDataEntry().asCalculated(PathCalculator.class);
		ddvDocumentType_pathParts.defineDataEntry().asCalculated(PathPartsCalculator.class);
		ddvDocumentType_principalpath.defineDataEntry().asCalculated(PrincipalPathCalculator.class);
		ddvDocumentType_tokens.defineDataEntry().asCalculated(TokensCalculator2.class);
		ddvFolderType_allauthorizations.defineDataEntry().asCalculated(AllAuthorizationsCalculator.class);
		ddvFolderType_inheritedauthorizations.defineDataEntry().asCalculated(InheritedAuthorizationsCalculator.class);
		ddvFolderType_parentpath.defineDataEntry().asCalculated(ParentPathCalculator.class);
		ddvFolderType_path.defineDataEntry().asCalculated(PathCalculator.class);
		ddvFolderType_pathParts.defineDataEntry().asCalculated(PathPartsCalculator.class);
		ddvFolderType_principalpath.defineDataEntry().asCalculated(PrincipalPathCalculator.class);
		ddvFolderType_tokens.defineDataEntry().asCalculated(TokensCalculator2.class);
		ddvMediumType_allauthorizations.defineDataEntry().asCalculated(AllAuthorizationsCalculator.class);
		ddvMediumType_inheritedauthorizations.defineDataEntry().asCalculated(InheritedAuthorizationsCalculator.class);
		ddvMediumType_parentpath.defineDataEntry().asCalculated(ParentPathCalculator.class);
		ddvMediumType_path.defineDataEntry().asCalculated(PathCalculator.class);
		ddvMediumType_pathParts.defineDataEntry().asCalculated(PathPartsCalculator.class);
		ddvMediumType_principalpath.defineDataEntry().asCalculated(PrincipalPathCalculator.class);
		ddvMediumType_tokens.defineDataEntry().asCalculated(TokensCalculator2.class);
		ddvStorageSpaceType_allauthorizations.defineDataEntry().asCalculated(AllAuthorizationsCalculator.class);
		ddvStorageSpaceType_inheritedauthorizations.defineDataEntry().asCalculated(InheritedAuthorizationsCalculator.class);
		ddvStorageSpaceType_parentpath.defineDataEntry().asCalculated(ParentPathCalculator.class);
		ddvStorageSpaceType_path.defineDataEntry().asCalculated(PathCalculator.class);
		ddvStorageSpaceType_pathParts.defineDataEntry().asCalculated(PathPartsCalculator.class);
		ddvStorageSpaceType_principalpath.defineDataEntry().asCalculated(PrincipalPathCalculator.class);
		ddvStorageSpaceType_tokens.defineDataEntry().asCalculated(TokensCalculator2.class);
		ddvVariablePeriod_allauthorizations.defineDataEntry().asCalculated(AllAuthorizationsCalculator.class);
		ddvVariablePeriod_inheritedauthorizations.defineDataEntry().asCalculated(InheritedAuthorizationsCalculator.class);
		ddvVariablePeriod_parentpath.defineDataEntry().asCalculated(ParentPathCalculator.class);
		ddvVariablePeriod_path.defineDataEntry().asCalculated(PathCalculator.class);
		ddvVariablePeriod_pathParts.defineDataEntry().asCalculated(PathPartsCalculator.class);
		ddvVariablePeriod_principalpath.defineDataEntry().asCalculated(PrincipalPathCalculator.class);
		ddvVariablePeriod_tokens.defineDataEntry().asCalculated(TokensCalculator2.class);
		decommissioningList_allauthorizations.defineDataEntry().asCalculated(AllAuthorizationsCalculator.class);
		decommissioningList_analogicalMedium.defineDataEntry().asCalculated(DecomListHasAnalogicalMediumTypesCalculator.class);
		decommissioningList_containers.defineDataEntry().asCalculated(DecomListContainersCalculator.class);
		decommissioningList_electronicMedium.defineDataEntry().asCalculated(DecomListHasElectronicMediumTypesCalculator.class);
		decommissioningList_folders.defineDataEntry().asCalculated(DecomListFoldersCalculator.class);
		decommissioningList_foldersMediaTypes.defineDataEntry().asCopied(decommissioningList_folders, folder_mediaType);
		decommissioningList_inheritedauthorizations.defineDataEntry().asCalculated(InheritedAuthorizationsCalculator.class);
		decommissioningList_parentpath.defineDataEntry().asCalculated(ParentPathCalculator.class);
		decommissioningList_path.defineDataEntry().asCalculated(PathCalculator.class);
		decommissioningList_pathParts.defineDataEntry().asCalculated(PathPartsCalculator.class);
		decommissioningList_pendingValidations.defineDataEntry().asCalculated(PendingValidationCalculator.class);
		decommissioningList_principalpath.defineDataEntry().asCalculated(PrincipalPathCalculator.class);
		decommissioningList_status.defineDataEntry().asCalculated(DecomListStatusCalculator2.class);
		decommissioningList_tokens.defineDataEntry().asCalculated(TokensCalculator2.class);
		decommissioningList_uniform.defineDataEntry().asCalculated(DecomListIsUniform.class);
		decommissioningList_uniformCategory.defineDataEntry().asCalculated(DecomListUniformCategoryCalculator2.class);
		decommissioningList_uniformCopyRule.defineDataEntry().asCalculated(DecomListUniformCopyRuleCalculator2.class);
		decommissioningList_uniformCopyType.defineDataEntry().asCalculated(DecomListUniformCopyTypeCalculator2.class);
		decommissioningList_uniformRule.defineDataEntry().asCalculated(DecomListUniformRuleCalculator2.class);
		document_actualDepositDate.defineDataEntry().asCalculated(DocumentActualDepositDateCalculator.class);
		document_actualDestructionDate.defineDataEntry().asCalculated(DocumentActualDestructionDateCalculator.class);
		document_actualTransferDate.defineDataEntry().asCalculated(DocumentActualTransferDateCalculator.class);
		document_administrativeUnit.defineDataEntry().asCopied(document_folder, folder_administrativeUnit);
		document_allauthorizations.defineDataEntry().asCalculated(AllAuthorizationsCalculator.class);
		document_applicableCopyRule.defineDataEntry().asCalculated(DocumentApplicableCopyRulesCalculator.class);
		document_archivisticStatus.defineDataEntry().asCalculated(DocumentArchivisticStatusCalculator.class);
		document_calendarYear.defineDataEntry().asCalculated(DocumentCalendarYearCalculator.class);
		document_category.defineDataEntry().asCopied(document_folder, folder_category);
		document_closingDate.defineDataEntry().asCopied(document_folder, folder_closingDate);
		document_copyStatus.defineDataEntry().asCopied(document_folder, folder_copyStatus);
		document_documentType.defineDataEntry().asCopied(document_type, ddvDocumentType_title);
		document_expectedDepositDate.defineDataEntry().asCalculated(DocumentExpectedDepositDateCalculator.class);
		document_expectedDestructionDate.defineDataEntry().asCalculated(DocumentExpectedDestructionDateCalculator.class);
		document_expectedTransferDate.defineDataEntry().asCalculated(DocumentExpectedTransferDateCalculator.class);
		document_filingSpace.defineDataEntry().asCopied(document_folder, folder_filingSpace);
		document_inheritedRetentionRule.defineDataEntry().asCopied(document_folder, folder_retentionRule);
		document_inheritedauthorizations.defineDataEntry().asCalculated(InheritedAuthorizationsCalculator.class);
		document_mainCopyRule.defineDataEntry().asCalculated(DocumentMainCopyRuleCalculator2.class);
		document_openingDate.defineDataEntry().asCopied(document_folder, folder_openingDate);
		document_parentpath.defineDataEntry().asCalculated(ParentPathCalculator.class);
		document_path.defineDataEntry().asCalculated(PathCalculator.class);
		document_pathParts.defineDataEntry().asCalculated(PathPartsCalculator.class);
		document_principalpath.defineDataEntry().asCalculated(PrincipalPathCalculator.class);
		document_retentionRule.defineDataEntry().asCalculated(DocumentRetentionRuleCalculator.class);
		document_sameInactiveFateAsFolder.defineDataEntry().asCalculated(DocumentIsSameInactiveFateAsFolderCalculator.class);
		document_sameSemiActiveFateAsFolder.defineDataEntry().asCalculated(DocumentIsSameSemiActiveFateAsFolderCalculator.class);
		document_tokens.defineDataEntry().asCalculated(TokensCalculator2.class);
		document_version.defineDataEntry().asCalculated(DocumentVersionCalculator.class);
		document_visibleInTrees.defineDataEntry().asCopied(document_folder, folder_visibleInTrees);
		filingSpace_allauthorizations.defineDataEntry().asCalculated(AllAuthorizationsCalculator.class);
		filingSpace_inheritedauthorizations.defineDataEntry().asCalculated(InheritedAuthorizationsCalculator.class);
		filingSpace_parentpath.defineDataEntry().asCalculated(ParentPathCalculator.class);
		filingSpace_path.defineDataEntry().asCalculated(PathCalculator.class);
		filingSpace_pathParts.defineDataEntry().asCalculated(PathPartsCalculator.class);
		filingSpace_principalpath.defineDataEntry().asCalculated(PrincipalPathCalculator.class);
		filingSpace_tokens.defineDataEntry().asCalculated(TokensCalculator2.class);
		folder_activeRetentionPeriodCode.defineDataEntry()
				.asCalculated(FolderRetentionPeriodCodeCalculator.FolderActiveRetentionPeriodCodeCalculator.class);
		folder_activeRetentionType.defineDataEntry().asCalculated(FolderActiveRetentionTypeCalculator.class);
		folder_administrativeUnit.defineDataEntry().asCalculated(FolderAppliedAdministrativeUnitCalculator.class);
		folder_administrativeUnitAncestors.defineDataEntry()
				.asCopied(folder_administrativeUnit, administrativeUnit_unitAncestors);
		folder_allauthorizations.defineDataEntry().asCalculated(AllAuthorizationsCalculator.class);
		folder_applicableCopyRule.defineDataEntry().asCalculated(FolderApplicableCopyRuleCalculator.class);
		folder_archivisticStatus.defineDataEntry().asCalculated(FolderStatusCalculator.class);
		folder_calendarYear.defineDataEntry().asCalculated(FolderCalendarYearCalculator.class);
		folder_category.defineDataEntry().asCalculated(FolderApplicableCategoryCalculator.class);
		folder_categoryCode.defineDataEntry().asCopied(folder_category, category_code);
		folder_closingDate.defineDataEntry().asCalculated(FolderClosingDateCalculator.class);
		folder_copyRulesExpectedDepositDates.defineDataEntry().asCalculated(FolderCopyRulesExpectedDepositDatesCalculator.class);
		folder_copyRulesExpectedDestructionDates.defineDataEntry()
				.asCalculated(FolderCopyRulesExpectedDestructionDatesCalculator.class);
		folder_copyRulesExpectedTransferDates.defineDataEntry()
				.asCalculated(FolderCopyRulesExpectedTransferDatesCalculator.class);
		folder_copyStatus.defineDataEntry().asCalculated(FolderCopyStatusCalculator3.class);
		folder_decommissioningDate.defineDataEntry().asCalculated(FolderDecommissioningDateCalculator.class);
		folder_expectedDepositDate.defineDataEntry().asCalculated(FolderExpectedDepositDateCalculator.class);
		folder_expectedDestructionDate.defineDataEntry().asCalculated(FolderExpectedDestructionDateCalculator.class);
		folder_expectedTransferDate.defineDataEntry().asCalculated(FolderExpectedTransferDateCalculator.class);
		folder_filingSpace.defineDataEntry().asCalculated(FolderAppliedFilingSpaceCalculator.class);
		folder_filingSpaceCode.defineDataEntry().asCopied(folder_filingSpace, filingSpace_code);
		folder_folderType.defineDataEntry().asCopied(folder_type, ddvFolderType_title);
		folder_inactiveDisposalType.defineDataEntry().asCalculated(FolderInactiveDisposalTypeCalculator.class);
		folder_inheritedauthorizations.defineDataEntry().asCalculated(InheritedAuthorizationsCalculator.class);
		folder_mainCopyRule.defineDataEntry().asCalculated(FolderMainCopyRuleCalculator2.class);
		folder_mediaType.defineDataEntry().asCalculated(FolderMediaTypesCalculator.class);
		folder_parentpath.defineDataEntry().asCalculated(ParentPathCalculator.class);
		folder_path.defineDataEntry().asCalculated(PathCalculator.class);
		folder_pathParts.defineDataEntry().asCalculated(PathPartsCalculator.class);
		folder_principalpath.defineDataEntry().asCalculated(PrincipalPathCalculator.class);
		folder_retentionRule.defineDataEntry().asCalculated(FolderAppliedRetentionRuleCalculator.class);
		folder_ruleAdminUnit.defineDataEntry().asCopied(folder_retentionRule, retentionRule_administrativeUnits);
		folder_semiactiveRetentionPeriodCode.defineDataEntry()
				.asCalculated(FolderRetentionPeriodCodeCalculator.FolderSemiActiveRetentionPeriodCodeCalculator.class);
		folder_semiactiveRetentionType.defineDataEntry().asCalculated(FolderSemiActiveRetentionTypeCalculator.class);
		folder_tokens.defineDataEntry().asCalculated(TokensCalculator2.class);
		folder_uniformSubdivision.defineDataEntry().asCalculated(FolderAppliedUniformSubdivisionCalculator.class);
		folder_visibleInTrees.defineDataEntry().asCalculated(FolderTreeVisibilityCalculator.class);
		retentionRule_allauthorizations.defineDataEntry().asCalculated(AllAuthorizationsCalculator.class);
		retentionRule_documentTypes.defineDataEntry().asCalculated(RuleDocumentTypesCalculator2.class);
		retentionRule_folderTypes.defineDataEntry().asCalculated(RuleFolderTypesCalculator.class);
		retentionRule_inheritedauthorizations.defineDataEntry().asCalculated(InheritedAuthorizationsCalculator.class);
		retentionRule_parentpath.defineDataEntry().asCalculated(ParentPathCalculator.class);
		retentionRule_path.defineDataEntry().asCalculated(PathCalculator.class);
		retentionRule_pathParts.defineDataEntry().asCalculated(PathPartsCalculator.class);
		retentionRule_principalpath.defineDataEntry().asCalculated(PrincipalPathCalculator.class);
		retentionRule_tokens.defineDataEntry().asCalculated(TokensCalculator2.class);
		storageSpace_allauthorizations.defineDataEntry().asCalculated(AllAuthorizationsCalculator.class);
		storageSpace_inheritedauthorizations.defineDataEntry().asCalculated(InheritedAuthorizationsCalculator.class);
		storageSpace_parentpath.defineDataEntry().asCalculated(ParentPathCalculator.class);
		storageSpace_path.defineDataEntry().asCalculated(PathCalculator.class);
		storageSpace_pathParts.defineDataEntry().asCalculated(PathPartsCalculator.class);
		storageSpace_principalpath.defineDataEntry().asCalculated(PrincipalPathCalculator.class);
		storageSpace_tokens.defineDataEntry().asCalculated(TokensCalculator2.class);
		uniformSubdivision_allauthorizations.defineDataEntry().asCalculated(AllAuthorizationsCalculator.class);
		uniformSubdivision_inheritedauthorizations.defineDataEntry().asCalculated(InheritedAuthorizationsCalculator.class);
		uniformSubdivision_parentpath.defineDataEntry().asCalculated(ParentPathCalculator.class);
		uniformSubdivision_path.defineDataEntry().asCalculated(PathCalculator.class);
		uniformSubdivision_pathParts.defineDataEntry().asCalculated(PathPartsCalculator.class);
		uniformSubdivision_principalpath.defineDataEntry().asCalculated(PrincipalPathCalculator.class);
		uniformSubdivision_tokens.defineDataEntry().asCalculated(TokensCalculator2.class);
	}

	public void applySchemasDisplay(SchemasDisplayManager manager) {
		SchemaTypesDisplayTransactionBuilder transaction = manager.newTransactionBuilderFor(collection);
		SchemaTypesDisplayConfig typesConfig = manager.getTypes(collection);
		transaction.setModifiedCollectionTypes(manager.getTypes(collection).withFacetMetadataCodes(
				asList("folder_default_administrativeUnit", "folder_default_archivisticStatus", "folder_default_category",
						"folder_default_copyStatus", "folder_default_filingSpace", "folder_default_mediumTypes",
						"folder_default_schema")));
		transaction.add(manager.getType(collection, "administrativeUnit").withSimpleSearchStatus(false)
				.withAdvancedSearchStatus(false).withManageableStatus(false)
				.withMetadataGroup(resourcesProvider.getLanguageMapWithKeys(asList("defaultGroupLabel"))));
		transaction.add(manager.getMetadata(collection, "administrativeUnit_default_adress").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "administrativeUnit_default_code").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "administrativeUnit_default_createdBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "administrativeUnit_default_createdOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "administrativeUnit_default_decommissioningMonth").withMetadataGroup("")
				.withInputType(MetadataInputType.RADIO_BUTTONS).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "administrativeUnit_default_description").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "administrativeUnit_default_modifiedBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "administrativeUnit_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "administrativeUnit_default_parent").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "administrativeUnit_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "cart_default_containers").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "cart_default_createdBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "cart_default_createdOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "cart_default_documents").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "cart_default_folders").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "cart_default_modifiedBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "cart_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "cart_default_owner").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "cart_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getType(collection, "category").withSimpleSearchStatus(false).withAdvancedSearchStatus(false)
				.withManageableStatus(false)
				.withMetadataGroup(resourcesProvider.getLanguageMapWithKeys(asList("defaultGroupLabel"))));
		transaction.add(manager.getMetadata(collection, "category_default_code").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "category_default_comments").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "category_default_createdBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "category_default_createdOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "category_default_description").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "category_default_keywords").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "category_default_modifiedBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "category_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "category_default_parent").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "category_default_retentionRules").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "category_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getType(collection, "collection").withSimpleSearchStatus(false).withAdvancedSearchStatus(false)
				.withManageableStatus(false)
				.withMetadataGroup(resourcesProvider.getLanguageMapWithKeys(asList("defaultGroupLabel"))));
		transaction.add(manager.getMetadata(collection, "collection_default_code").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "collection_default_createdOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "collection_default_languages").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "collection_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "collection_default_name").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "collection_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getType(collection, "containerRecord").withSimpleSearchStatus(true).withAdvancedSearchStatus(true)
				.withManageableStatus(false)
				.withMetadataGroup(resourcesProvider.getLanguageMapWithKeys(asList("defaultGroupLabel"))));
		transaction.add(manager.getMetadata(collection, "containerRecord_default_administrativeUnit").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "containerRecord_default_borrowDate").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "containerRecord_default_borrowed").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "containerRecord_default_borrower").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "containerRecord_default_capacity").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "containerRecord_default_completionDate").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "containerRecord_default_decommissioningType").withMetadataGroup("")
				.withInputType(MetadataInputType.DROPDOWN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "containerRecord_default_description").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "containerRecord_default_filingSpace").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "containerRecord_default_fillRatioEntered").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "containerRecord_default_full").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "containerRecord_default_id").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "containerRecord_default_identifier").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "containerRecord_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "containerRecord_default_planifiedReturnDate").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "containerRecord_default_position").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "containerRecord_default_realDepositDate").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "containerRecord_default_realReturnDate").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "containerRecord_default_realTransferDate").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "containerRecord_default_storageSpace").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "containerRecord_default_temporaryIdentifier").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "containerRecord_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "containerRecord_default_type").withMetadataGroup("")
				.withInputType(MetadataInputType.DROPDOWN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getType(collection, "ddvContainerRecordType").withSimpleSearchStatus(false)
				.withAdvancedSearchStatus(false).withManageableStatus(false)
				.withMetadataGroup(resourcesProvider.getLanguageMapWithKeys(asList("defaultGroupLabel"))));
		transaction.add(manager.getMetadata(collection, "ddvContainerRecordType_default_code").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvContainerRecordType_default_description").withMetadataGroup("")
				.withInputType(MetadataInputType.TEXTAREA).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvContainerRecordType_default_linkedSchema").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvContainerRecordType_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvContainerRecordType_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction
				.add(manager.getType(collection, "ddvDocumentType").withSimpleSearchStatus(false).withAdvancedSearchStatus(false)
						.withManageableStatus(false)
						.withMetadataGroup(resourcesProvider.getLanguageMapWithKeys(asList("defaultGroupLabel"))));
		transaction.add(manager.getMetadata(collection, "ddvDocumentType_default_code").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvDocumentType_default_description").withMetadataGroup("")
				.withInputType(MetadataInputType.TEXTAREA).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvDocumentType_default_linkedSchema").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvDocumentType_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvDocumentType_default_templates").withMetadataGroup("")
				.withInputType(MetadataInputType.CONTENT).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvDocumentType_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getType(collection, "ddvFolderType").withSimpleSearchStatus(false).withAdvancedSearchStatus(false)
				.withManageableStatus(false)
				.withMetadataGroup(resourcesProvider.getLanguageMapWithKeys(asList("defaultGroupLabel"))));
		transaction.add(manager.getMetadata(collection, "ddvFolderType_default_code").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvFolderType_default_description").withMetadataGroup("")
				.withInputType(MetadataInputType.TEXTAREA).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvFolderType_default_linkedSchema").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvFolderType_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvFolderType_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getType(collection, "ddvMediumType").withSimpleSearchStatus(false).withAdvancedSearchStatus(false)
				.withManageableStatus(false)
				.withMetadataGroup(resourcesProvider.getLanguageMapWithKeys(asList("defaultGroupLabel"))));
		transaction.add(manager.getMetadata(collection, "ddvMediumType_default_analogical").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvMediumType_default_code").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvMediumType_default_description").withMetadataGroup("")
				.withInputType(MetadataInputType.TEXTAREA).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvMediumType_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvMediumType_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getType(collection, "ddvStorageSpaceType").withSimpleSearchStatus(false)
				.withAdvancedSearchStatus(false).withManageableStatus(false)
				.withMetadataGroup(resourcesProvider.getLanguageMapWithKeys(asList("defaultGroupLabel"))));
		transaction.add(manager.getMetadata(collection, "ddvStorageSpaceType_default_code").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvStorageSpaceType_default_description").withMetadataGroup("")
				.withInputType(MetadataInputType.TEXTAREA).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvStorageSpaceType_default_linkedSchema").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvStorageSpaceType_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvStorageSpaceType_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvTaskStatus_default_code").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvTaskStatus_default_comments").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvTaskStatus_default_createdBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvTaskStatus_default_createdOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvTaskStatus_default_description").withMetadataGroup("")
				.withInputType(MetadataInputType.TEXTAREA).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvTaskStatus_default_modifiedBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvTaskStatus_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvTaskStatus_default_statusType").withMetadataGroup("")
				.withInputType(MetadataInputType.DROPDOWN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "ddvTaskStatus_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvTaskType_default_code").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvTaskType_default_comments").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvTaskType_default_createdBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvTaskType_default_createdOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvTaskType_default_description").withMetadataGroup("")
				.withInputType(MetadataInputType.TEXTAREA).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvTaskType_default_linkedSchema").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvTaskType_default_modifiedBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvTaskType_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvTaskType_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvVariablePeriod_default_code").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvVariablePeriod_default_comments").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvVariablePeriod_default_createdBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvVariablePeriod_default_createdOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvVariablePeriod_default_description").withMetadataGroup("")
				.withInputType(MetadataInputType.TEXTAREA).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvVariablePeriod_default_modifiedBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvVariablePeriod_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "ddvVariablePeriod_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getType(collection, "decommissioningList").withSimpleSearchStatus(false)
				.withAdvancedSearchStatus(false).withManageableStatus(false)
				.withMetadataGroup(resourcesProvider.getLanguageMapWithKeys(asList("defaultGroupLabel"))));
		transaction.add(manager.getMetadata(collection, "decommissioningList_default_administrativeUnit").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "decommissioningList_default_approvalDate").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "decommissioningList_default_approvalRequest").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "decommissioningList_default_approvalRequestDate").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "decommissioningList_default_approvalUser").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "decommissioningList_default_containerDetails").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "decommissioningList_default_createdBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "decommissioningList_default_createdOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "decommissioningList_default_description").withMetadataGroup("")
				.withInputType(MetadataInputType.TEXTAREA).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "decommissioningList_default_filingSpace").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "decommissioningList_default_folderDetails").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "decommissioningList_default_modifiedBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "decommissioningList_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "decommissioningList_default_processingDate").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "decommissioningList_default_processingUser").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "decommissioningList_default_status").withMetadataGroup("")
				.withInputType(MetadataInputType.RADIO_BUTTONS).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "decommissioningList_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "decommissioningList_default_type").withMetadataGroup("")
				.withInputType(MetadataInputType.RADIO_BUTTONS).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "decommissioningList_default_uniformCategory").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "decommissioningList_default_uniformRule").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "decommissioningList_default_validationDate").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "decommissioningList_default_validationUser").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getType(collection, "document").withSimpleSearchStatus(true).withAdvancedSearchStatus(true)
				.withManageableStatus(false).withMetadataGroup(
						resourcesProvider.getLanguageMapWithKeys(asList("defaultGroupLabel", "classifiedInGroupLabel"))));
		transaction.add(manager.getMetadata(collection, "document_email_actualDepositDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_email_actualDestructionDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_email_actualTransferDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_email_archivisticStatus").withMetadataGroup("")
				.withInputType(MetadataInputType.RADIO_BUTTONS).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_email_author").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_email_category").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_email_comments").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "document_email_company").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_email_content").withMetadataGroup("")
				.withInputType(MetadataInputType.CONTENT).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "document_email_copyStatus").withMetadataGroup("")
				.withInputType(MetadataInputType.RADIO_BUTTONS).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "document_email_description").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_email_emailAttachmentsList").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_email_emailBCCTo").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_email_emailCCTo").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_email_emailCompany").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_email_emailContent").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_email_emailFrom").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_email_emailInNameOf").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_email_emailObject").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_email_emailReceivedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_email_emailSentOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_email_emailTo").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_email_expectedDepositDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_email_expectedDestructionDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_email_expectedTransferDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_email_folder").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_email_formCreatedBy").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "document_email_formCreatedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "document_email_formModifiedBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "document_email_formModifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "document_email_keywords").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_email_mainCopyRule").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "document_email_mainCopyRuleIdEntered").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "document_email_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "document_email_retentionRule").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_email_subject").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "document_email_subjectToBroadcastRule").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_email_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_email_type").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_default_actualDepositDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_default_actualDestructionDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_default_actualTransferDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_default_administrativeUnit").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_default_archivisticStatus").withMetadataGroup("")
				.withInputType(MetadataInputType.RADIO_BUTTONS).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_default_author").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_default_borrowed").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_default_category").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_default_closingDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_default_comments").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "document_default_company").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_default_content").withMetadataGroup("")
				.withInputType(MetadataInputType.CONTENT).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "document_default_copyStatus").withMetadataGroup("")
				.withInputType(MetadataInputType.RADIO_BUTTONS).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "document_default_description").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_default_expectedDepositDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_default_expectedDestructionDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_default_expectedTransferDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_default_filingSpace").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_default_folder").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_default_formCreatedBy").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "document_default_formCreatedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "document_default_formModifiedBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "document_default_formModifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "document_default_id").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_default_keywords").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_default_mainCopyRule").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "document_default_mainCopyRuleIdEntered").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "document_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "document_default_openingDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_default_retentionRule").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_default_subject").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "document_default_type").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "emailToSend_default_BCC").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "emailToSend_default_CC").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "emailToSend_default_createdBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "emailToSend_default_createdOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "emailToSend_default_error").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "emailToSend_default_from").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "emailToSend_default_modifiedBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "emailToSend_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "emailToSend_default_parameters").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "emailToSend_default_sendOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "emailToSend_default_subject").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "emailToSend_default_template").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "emailToSend_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "emailToSend_default_to").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "emailToSend_default_tryingCount").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getType(collection, "event").withSimpleSearchStatus(false).withAdvancedSearchStatus(false)
				.withManageableStatus(false)
				.withMetadataGroup(resourcesProvider.getLanguageMapWithKeys(asList("defaultGroupLabel"))));
		transaction.add(manager.getMetadata(collection, "event_default_createdBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "event_default_createdOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "event_default_delta").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "event_default_eventPrincipalPath").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "event_default_ip").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "event_default_modifiedBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "event_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "event_default_permissionDateRange").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "event_default_permissionRoles").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "event_default_permissionUsers").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "event_default_reason").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "event_default_recordIdentifier").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "event_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "event_default_type").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "event_default_userRoles").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "event_default_username").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getType(collection, "facet").withSimpleSearchStatus(false).withAdvancedSearchStatus(false)
				.withManageableStatus(false).withMetadataGroup(resourcesProvider.getLanguageMapWithKeys(
						asList("init.facetConfiguration.values", "init.facetConfiguration.query",
								"init.facetConfiguration.configuration"))));
		transaction.add(manager.getMetadata(collection, "facet_field_active")
				.withMetadataGroup("init.facetConfiguration.configuration").withInputType(MetadataInputType.FIELD)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_field_createdBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_field_createdOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_field_elementPerPage")
				.withMetadataGroup("init.facetConfiguration.configuration").withInputType(MetadataInputType.FIELD)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_field_facetType")
				.withMetadataGroup("init.facetConfiguration.configuration").withInputType(MetadataInputType.RADIO_BUTTONS)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_field_fieldDatastoreCode")
				.withMetadataGroup("init.facetConfiguration.configuration").withInputType(MetadataInputType.FIELD)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_field_fieldValuesLabel")
				.withMetadataGroup("init.facetConfiguration.values").withInputType(MetadataInputType.FIELD)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_field_modifiedBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_field_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_field_openByDefault")
				.withMetadataGroup("init.facetConfiguration.configuration").withInputType(MetadataInputType.FIELD)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_field_order").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_field_orderResult")
				.withMetadataGroup("init.facetConfiguration.configuration").withInputType(MetadataInputType.RADIO_BUTTONS)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_field_pages").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_field_title")
				.withMetadataGroup("init.facetConfiguration.configuration").withInputType(MetadataInputType.FIELD)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_query_active")
				.withMetadataGroup("init.facetConfiguration.configuration").withInputType(MetadataInputType.FIELD)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_query_createdBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_query_createdOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_query_elementPerPage")
				.withMetadataGroup("init.facetConfiguration.configuration").withInputType(MetadataInputType.FIELD)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_query_facetType")
				.withMetadataGroup("init.facetConfiguration.configuration").withInputType(MetadataInputType.RADIO_BUTTONS)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_query_fieldDatastoreCode")
				.withMetadataGroup("init.facetConfiguration.configuration").withInputType(MetadataInputType.FIELD)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction
				.add(manager.getMetadata(collection, "facet_query_listQueries").withMetadataGroup("init.facetConfiguration.query")
						.withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
						.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_query_modifiedBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_query_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_query_openByDefault")
				.withMetadataGroup("init.facetConfiguration.configuration").withInputType(MetadataInputType.FIELD)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_query_order").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_query_orderResult")
				.withMetadataGroup("init.facetConfiguration.configuration").withInputType(MetadataInputType.RADIO_BUTTONS)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_query_pages").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_query_title")
				.withMetadataGroup("init.facetConfiguration.configuration").withInputType(MetadataInputType.FIELD)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_default_active")
				.withMetadataGroup("init.facetConfiguration.configuration").withInputType(MetadataInputType.FIELD)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_default_createdBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_default_createdOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_default_elementPerPage")
				.withMetadataGroup("init.facetConfiguration.configuration").withInputType(MetadataInputType.FIELD)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_default_facetType")
				.withMetadataGroup("init.facetConfiguration.configuration").withInputType(MetadataInputType.RADIO_BUTTONS)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_default_fieldDatastoreCode")
				.withMetadataGroup("init.facetConfiguration.configuration").withInputType(MetadataInputType.FIELD)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_default_modifiedBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_default_openByDefault")
				.withMetadataGroup("init.facetConfiguration.configuration").withInputType(MetadataInputType.FIELD)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_default_order").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_default_orderResult")
				.withMetadataGroup("init.facetConfiguration.configuration").withInputType(MetadataInputType.RADIO_BUTTONS)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_default_pages").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "facet_default_title")
				.withMetadataGroup("init.facetConfiguration.configuration").withInputType(MetadataInputType.FIELD)
				.withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getType(collection, "filingSpace").withSimpleSearchStatus(false).withAdvancedSearchStatus(false)
				.withManageableStatus(false)
				.withMetadataGroup(resourcesProvider.getLanguageMapWithKeys(asList("defaultGroupLabel"))));
		transaction.add(manager.getMetadata(collection, "filingSpace_default_administrators").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "filingSpace_default_code").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "filingSpace_default_description").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "filingSpace_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "filingSpace_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "filingSpace_default_users").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getType(collection, "folder").withSimpleSearchStatus(true).withAdvancedSearchStatus(true)
				.withManageableStatus(false).withMetadataGroup(
						resourcesProvider.getLanguageMapWithKeys(asList("defaultGroupLabel", "classifiedInGroupLabel"))));
		transaction.add(manager.getMetadata(collection, "folder_default_activeRetentionType").withMetadataGroup("")
				.withInputType(MetadataInputType.RADIO_BUTTONS).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_actualDepositDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_actualDestructionDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_actualTransferDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_administrativeUnit").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_administrativeUnitEntered").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "folder_default_archivisticStatus").withMetadataGroup("")
				.withInputType(MetadataInputType.RADIO_BUTTONS).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_borrowDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_borrowPreviewReturnDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_borrowReturnDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_borrowUser").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_borrowUserEntered").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "folder_default_borrowed").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_borrowingType").withMetadataGroup("")
				.withInputType(MetadataInputType.RADIO_BUTTONS).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "folder_default_category").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_categoryCode").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_categoryEntered").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "folder_default_closingDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_comments").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "folder_default_container").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_copyRulesExpectedDepositDates").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_copyRulesExpectedDestructionDates").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_copyRulesExpectedTransferDates").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_copyStatus").withMetadataGroup("")
				.withInputType(MetadataInputType.RADIO_BUTTONS).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_copyStatusEntered").withMetadataGroup("")
				.withInputType(MetadataInputType.RADIO_BUTTONS).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "folder_default_decommissioningDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_description").withMetadataGroup("")
				.withInputType(MetadataInputType.TEXTAREA).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_enteredClosingDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "folder_default_expectedDepositDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_expectedDestructionDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_expectedTransferDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_filingSpace").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_filingSpaceCode").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_followers").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "folder_default_formCreatedBy").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "folder_default_formCreatedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "folder_default_formModifiedBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "folder_default_formModifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "folder_default_id").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_inactiveDisposalType").withMetadataGroup("")
				.withInputType(MetadataInputType.RADIO_BUTTONS).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_keywords").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_linearSize").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "folder_default_mainCopyRule").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "folder_default_mainCopyRuleIdEntered").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "folder_default_mediaType").withMetadataGroup("")
				.withInputType(MetadataInputType.RADIO_BUTTONS).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_mediumTypes").withMetadataGroup("")
				.withInputType(MetadataInputType.CHECKBOXES).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "folder_default_openingDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_parentFolder").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_retentionRule").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_retentionRuleEntered").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "folder_default_ruleAdminUnit").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_schema").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "folder_default_semiactiveRetentionType").withMetadataGroup("")
				.withInputType(MetadataInputType.RADIO_BUTTONS).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_type").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_uniformSubdivision").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "folder_default_uniformSubdivisionEntered").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getType(collection, "group").withSimpleSearchStatus(false).withAdvancedSearchStatus(false)
				.withManageableStatus(false)
				.withMetadataGroup(resourcesProvider.getLanguageMapWithKeys(asList("defaultGroupLabel"))));
		transaction.add(manager.getMetadata(collection, "group_default_code").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "group_default_createdOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "group_default_isGlobal").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "group_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "group_default_parent").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "group_default_roles").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "group_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "report_default_columnsCount").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "report_default_createdBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "report_default_createdOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "report_default_linesCount").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "report_default_modifiedBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "report_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "report_default_reportedMetadata").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "report_default_schemaTypeCode").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "report_default_separator").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "report_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "report_default_username").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getType(collection, "retentionRule").withSimpleSearchStatus(false).withAdvancedSearchStatus(false)
				.withManageableStatus(false)
				.withMetadataGroup(resourcesProvider.getLanguageMapWithKeys(asList("defaultGroupLabel"))));
		transaction.add(manager.getMetadata(collection, "retentionRule_default_administrativeUnits").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "retentionRule_default_approvalDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "retentionRule_default_approved").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "retentionRule_default_code").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "retentionRule_default_confidentialDocuments").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "retentionRule_default_copyRetentionRules").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "retentionRule_default_copyRulesComment").withMetadataGroup("")
				.withInputType(MetadataInputType.TEXTAREA).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "retentionRule_default_corpus").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "retentionRule_default_corpusRuleNumber").withMetadataGroup("")
				.withInputType(MetadataInputType.TEXTAREA).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "retentionRule_default_description").withMetadataGroup("")
				.withInputType(MetadataInputType.TEXTAREA).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "retentionRule_default_documentCopyRetentionRules").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "retentionRule_default_documentTypesDetails").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "retentionRule_default_essentialDocuments").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "retentionRule_default_generalComment").withMetadataGroup("")
				.withInputType(MetadataInputType.TEXTAREA).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "retentionRule_default_history").withMetadataGroup("")
				.withInputType(MetadataInputType.TEXTAREA).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "retentionRule_default_juridicReference").withMetadataGroup("")
				.withInputType(MetadataInputType.TEXTAREA).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "retentionRule_default_keywords").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "retentionRule_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "retentionRule_default_principalDefaultDocumentCopyRetentionRule")
				.withMetadataGroup("").withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction
				.add(manager.getMetadata(collection, "retentionRule_default_responsibleAdministrativeUnits").withMetadataGroup("")
						.withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
						.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "retentionRule_default_scope").withMetadataGroup("")
				.withInputType(MetadataInputType.RADIO_BUTTONS).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "retentionRule_default_secondaryDefaultDocumentCopyRetentionRule")
				.withMetadataGroup("").withInputType(MetadataInputType.FIELD).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "retentionRule_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "savedSearch_default_advancedSearch").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "savedSearch_default_createdBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "savedSearch_default_createdOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "savedSearch_default_facetSelections").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "savedSearch_default_freeTextSearch").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "savedSearch_default_modifiedBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "savedSearch_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "savedSearch_default_pageNumber").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "savedSearch_default_public").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "savedSearch_default_schemaFilter").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "savedSearch_default_searchType").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "savedSearch_default_sortField").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "savedSearch_default_sortOrder").withMetadataGroup("")
				.withInputType(MetadataInputType.RADIO_BUTTONS).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "savedSearch_default_temporary").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "savedSearch_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "savedSearch_default_user").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getType(collection, "storageSpace").withSimpleSearchStatus(false).withAdvancedSearchStatus(false)
				.withManageableStatus(false)
				.withMetadataGroup(resourcesProvider.getLanguageMapWithKeys(asList("defaultGroupLabel"))));
		transaction.add(manager.getMetadata(collection, "storageSpace_default_capacity").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "storageSpace_default_code").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "storageSpace_default_createdBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "storageSpace_default_createdOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "storageSpace_default_decommissioningType").withMetadataGroup("")
				.withInputType(MetadataInputType.RADIO_BUTTONS).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "storageSpace_default_description").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "storageSpace_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "storageSpace_default_parentStorageSpace").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "storageSpace_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "storageSpace_default_type").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getType(collection, "task").withSimpleSearchStatus(false).withAdvancedSearchStatus(false)
				.withManageableStatus(false)
				.withMetadataGroup(resourcesProvider.getLanguageMapWithKeys(asList("defaultGroupLabel"))));
		transaction.add(manager.getMetadata(collection, "task_approval_assignCandidates").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "task_approval_assignedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "task_approval_assignedTo").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "task_approval_createdBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "task_approval_createdOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "task_approval_decision").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "task_approval_dueDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "task_approval_finishedBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "task_approval_finishedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "task_approval_modifiedBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "task_approval_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "task_approval_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "task_approval_workflowIdentifier").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "task_approval_workflowRecordIdentifiers").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "task_default_assignCandidates").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "task_default_assignedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "task_default_assignedTo").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "task_default_createdBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "task_default_createdOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "task_default_dueDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "task_default_finishedBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "task_default_finishedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "task_default_modifiedBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "task_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "task_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "task_default_workflowIdentifier").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "task_default_workflowRecordIdentifiers").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getType(collection, "uniformSubdivision").withSimpleSearchStatus(false)
				.withAdvancedSearchStatus(false).withManageableStatus(false)
				.withMetadataGroup(resourcesProvider.getLanguageMapWithKeys(asList("defaultGroupLabel"))));
		transaction.add(manager.getMetadata(collection, "uniformSubdivision_default_code").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "uniformSubdivision_default_description").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "uniformSubdivision_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "uniformSubdivision_default_retentionRule").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "uniformSubdivision_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getType(collection, "user").withSimpleSearchStatus(false).withAdvancedSearchStatus(false)
				.withManageableStatus(false)
				.withMetadataGroup(resourcesProvider.getLanguageMapWithKeys(asList("defaultGroupLabel"))));
		transaction.add(manager.getMetadata(collection, "user_default_allroles").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "user_default_collectionDeleteAccess").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "user_default_collectionReadAccess").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "user_default_collectionWriteAccess").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "user_default_createdOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "user_default_defaultTabInFolderDisplay").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "user_default_defaultTaxonomy").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "user_default_email").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "user_default_firstname").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "user_default_groups").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "user_default_jobTitle").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "user_default_lastname").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "user_default_loginLanguageCode").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "user_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "user_default_phone").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "user_default_signature").withMetadataGroup("")
				.withInputType(MetadataInputType.TEXTAREA).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "user_default_startTab").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "user_default_status").withMetadataGroup("")
				.withInputType(MetadataInputType.RADIO_BUTTONS).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "user_default_systemAdmin").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "user_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "user_default_username").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "user_default_userroles").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getType(collection, "userDocument").withSimpleSearchStatus(false).withAdvancedSearchStatus(false)
				.withManageableStatus(false)
				.withMetadataGroup(resourcesProvider.getLanguageMapWithKeys(asList("defaultGroupLabel"))));
		transaction.add(manager.getMetadata(collection, "userDocument_default_content").withMetadataGroup("")
				.withInputType(MetadataInputType.CONTENT).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "userDocument_default_createdOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "userDocument_default_folder").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "userDocument_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "userDocument_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "userDocument_default_user").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getType(collection, "userTask").withSimpleSearchStatus(true).withAdvancedSearchStatus(true)
				.withManageableStatus(false).withMetadataGroup(resourcesProvider.getLanguageMapWithKeys(
						asList("init.userTask.followersTab", "init.userTask.definition", "init.userTask.assignment",
								"init.userTask.details", "init.userTask.remindersTab"))));
		transaction.add(manager.getMetadata(collection, "userTask_default_administrativeUnit").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "userTask_default_assignedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "userTask_default_assignee").withMetadataGroup(" Assignation")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "userTask_default_assigneeGroupsCandidates")
				.withMetadataGroup(" Assignation").withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "userTask_default_assigneeUsersCandidates")
				.withMetadataGroup(" Assignation").withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "userTask_default_assigner").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "userTask_default_comments").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "userTask_default_contents").withMetadataGroup(" Fichiers")
				.withInputType(MetadataInputType.CONTENT).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "userTask_default_createdOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "userTask_default_decision").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "userTask_default_description").withMetadataGroup("")
				.withInputType(MetadataInputType.RICHTEXT).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "userTask_default_dueDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "userTask_default_endDate").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "userTask_default_linkedDocuments").withMetadataGroup(" Fichiers")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "userTask_default_linkedFolders").withMetadataGroup(" Fichiers")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "userTask_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "userTask_default_nextReminderOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "userTask_default_parentTask").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "userTask_default_parentTaskDueDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "userTask_default_progressPercentage").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "userTask_default_relativeDueDate").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "userTask_default_reminders").withMetadataGroup(" Rappels")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "userTask_default_startDate").withMetadataGroup("")
				.withInputType(MetadataInputType.HIDDEN).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "userTask_default_status").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "userTask_default_taskFollowers").withMetadataGroup(" Suivre")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "userTask_default_taskFollowersIds").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "userTask_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "userTask_default_type").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(true));
		transaction.add(manager.getMetadata(collection, "userTask_default_workflow").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "userTask_default_workflowInstance").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "workflow_default_code").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "workflow_default_createdBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "workflow_default_createdOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "workflow_default_modifiedBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "workflow_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "workflow_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "workflowInstance_default_createdBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "workflowInstance_default_createdOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "workflowInstance_default_extraFields").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "workflowInstance_default_modifiedBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "workflowInstance_default_modifiedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "workflowInstance_default_startedBy").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "workflowInstance_default_startedOn").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "workflowInstance_default_status").withMetadataGroup("")
				.withInputType(MetadataInputType.RADIO_BUTTONS).withHighlightStatus(false)
				.withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "workflowInstance_default_title").withMetadataGroup("")
				.withInputType(MetadataInputType.FIELD).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		transaction.add(manager.getMetadata(collection, "workflowInstance_default_workflow").withMetadataGroup("")
				.withInputType(MetadataInputType.LOOKUP).withHighlightStatus(false).withVisibleInAdvancedSearchStatus(false));
		manager.execute(transaction.build());
	}

	public void applyGeneratedRoles() {
		RolesManager rolesManager = appLayerFactory.getModelLayerFactory().getRolesManager();
		;
		rolesManager.updateRole(new Role(collection, "ADM", "Administrateur",
				asList("core.deleteContentVersion", "rm.modifySemiActiveBorrowedFolder", "rm.shareFolders", "core.manageSecurity",
						"rm.createSubFolders", "rm.manageReports", "core.manageMetadataExtractor", "rm.createDocuments",
						"rm.shareInactiveDocuments", "rm.deleteInactiveFolders", "core.manageEmailServer", "core.viewEvents",
						"core.manageSearchReports", "rm.modifyInactiveBorrowedFolder", "rm.deleteInactiveDocuments",
						"rm.duplicateSemiActiveFolders", "rm.modifyInactiveFolders", "rm.borrowFolder",
						"rm.modifyInactiveDocuments", "rm.shareSemiActiveDocuments", "rm.createSubFoldersInSemiActiveFolders",
						"rm.editDecommissioningList", "rm.manageRetentionRule", "rm.duplicateInactiveFolders",
						"core.manageSystemGroups", "core.ldapConfigurationManagement", "core.manageSystemServers",
						"core.manageTaxonomies", "rm.createInactiveDocuments", "core.manageSystemModules", "rm.decommissioning",
						"rm.deleteSemiActiveFolders", "rm.createSubFoldersInInactiveFolders", "core.manageTrash",
						"rm.manageClassificationPlan", "rm.processDecommissioningList", "rm.manageFolderAuthorizations",
						"core.manageSearchEngine", "rm.shareDocuments", "rm.manageUniformSubdivisions",
						"rm.returnOtherUsersDocuments", "rm.modifySemiActiveFolders", "rm.manageStorageSpaces",
						"core.manageSystemConfiguration", "rm.createSemiActiveDocuments", "rm.modifyFolderDecomDate",
						"rm.uploadInactiveDocuments", "core.manageSystemCollections", "rm.modifyOpeningDateFolder",
						"core.manageSystemDataImports", "core.manageConnectors", "core.manageFacets",
						"rm.deleteSemiActiveDocuments", "rm.uploadSemiActiveDocuments", "rm.shareInactiveFolders",
						"core.manageSystemUsers", "core.manageValueList", "rm.manageContainers", "tasks.manageWorkflows",
						"core.manageMetadataSchemas", "rm.manageDocumentAuthorizations", "rm.modifySemiActiveDocuments",
						"rm.createFolders", "core.manageSystemUpdates", "rm.shareSemiActiveFolders")));
		rolesManager.addRole(new Role(collection, "U", "Utilisateur",
				asList("rm.modifySemiActiveBorrowedFolder", "rm.shareFolders", "rm.createSubFolders", "rm.borrowFolder",
						"rm.deleteSemiActiveDocuments", "rm.createDocuments", "rm.uploadSemiActiveDocuments",
						"rm.shareSemiActiveDocuments", "rm.shareDocuments", "rm.createFolders", "rm.shareSemiActiveFolders",
						"rm.deleteSemiActiveFolders")));
		rolesManager.addRole(new Role(collection, "M", "Gestionnaire",
				asList("rm.modifySemiActiveBorrowedFolder", "rm.shareFolders", "rm.manageFolderAuthorizations",
						"rm.createSubFolders", "rm.deleteSemiActiveDocuments", "rm.borrowFolder", "rm.uploadSemiActiveDocuments",
						"rm.createDocuments", "rm.shareDocuments", "rm.shareSemiActiveDocuments", "rm.manageContainers",
						"rm.manageDocumentAuthorizations", "rm.createFolders", "rm.modifyOpeningDateFolder",
						"rm.shareSemiActiveFolders", "rm.deleteSemiActiveFolders", "rm.decommissioning")));
		rolesManager.addRole(new Role(collection, "RGD", "Responsable de la gestion documentaire",
				asList("core.deleteContentVersion", "rm.modifySemiActiveBorrowedFolder", "rm.shareFolders", "core.manageSecurity",
						"rm.createSubFolders", "core.manageMetadataExtractor", "rm.manageReports", "rm.createDocuments",
						"rm.shareInactiveDocuments", "rm.deleteInactiveFolders", "core.manageEmailServer", "core.viewEvents",
						"core.manageSearchReports", "rm.modifyInactiveBorrowedFolder", "rm.deleteInactiveDocuments",
						"rm.duplicateSemiActiveFolders", "rm.modifyInactiveFolders", "rm.borrowFolder",
						"rm.modifyInactiveDocuments", "rm.shareSemiActiveDocuments", "rm.createSubFoldersInSemiActiveFolders",
						"rm.editDecommissioningList", "core.manageSystemGroups", "rm.manageRetentionRule",
						"rm.duplicateInactiveFolders", "core.ldapConfigurationManagement", "core.manageSystemServers",
						"core.manageTaxonomies", "rm.createInactiveDocuments", "core.manageSystemModules", "core.manageTrash",
						"rm.decommissioning", "rm.deleteSemiActiveFolders", "rm.createSubFoldersInInactiveFolders",
						"rm.manageClassificationPlan", "rm.processDecommissioningList", "rm.manageFolderAuthorizations",
						"core.manageSearchEngine", "rm.shareDocuments", "rm.manageUniformSubdivisions",
						"rm.returnOtherUsersDocuments", "rm.modifySemiActiveFolders", "core.manageSystemConfiguration",
						"rm.manageStorageSpaces", "rm.createSemiActiveDocuments", "rm.modifyFolderDecomDate",
						"core.manageSystemCollections", "rm.uploadInactiveDocuments", "rm.modifyOpeningDateFolder",
						"core.manageSystemDataImports", "core.manageConnectors", "core.manageFacets",
						"rm.deleteSemiActiveDocuments", "rm.uploadSemiActiveDocuments", "rm.shareInactiveFolders",
						"core.manageSystemUsers", "core.manageValueList", "rm.manageContainers", "core.manageMetadataSchemas",
						"rm.manageDocumentAuthorizations", "rm.modifySemiActiveDocuments", "core.manageSystemUpdates",
						"rm.createFolders", "rm.shareSemiActiveFolders")));
	}
}
