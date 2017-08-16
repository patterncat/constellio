package com.constellio.app.modules.rm.model.SIPArchivedGenerator.constellio.sip.data.intelligid;

import com.constellio.app.modules.rm.model.CopyRetentionRule;
import com.constellio.app.modules.rm.model.SIPArchivedGenerator.constellio.filter.SIPFilter;
import com.constellio.app.modules.rm.model.SIPArchivedGenerator.constellio.sip.data.SIPObjectsProvider;
import com.constellio.app.modules.rm.model.SIPArchivedGenerator.constellio.sip.ead.EADArchdesc;
import com.constellio.app.modules.rm.model.SIPArchivedGenerator.constellio.sip.model.SIPObject;
import com.constellio.app.modules.rm.model.SIPArchivedGenerator.constellio.sip.model.intelligid.EntityRetriever;
import com.constellio.app.modules.rm.model.SIPArchivedGenerator.constellio.sip.model.intelligid.IntelliGIDSIPDocument;
import com.constellio.app.modules.rm.model.SIPArchivedGenerator.constellio.sip.model.intelligid.IntelliGIDSIPFolder;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.wrappers.*;
import com.constellio.app.modules.rm.wrappers.type.DocumentType;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchemaType;
import com.constellio.model.entities.schemas.MetadataSchemaTypes;
import com.constellio.model.services.schemas.MetadataListFilter;
import com.constellio.model.services.schemas.MetadataSchemasManager;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;
import com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators;
import com.constellio.model.services.search.query.logical.condition.LogicalSearchCondition;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class IntelliGIDSIPObjectsProvider implements SIPObjectsProvider {
    public static final String CLE_FICHIERS_JOINTS = "fichiersJoints";

    private List<Metadata> metadonneesDossier;

    private List<Metadata> metadonneesDocument;

    private Long idRubrique;

    private Long idUniteAdministrative;

    private List<Document> fichesDocuments;

    private String collection;

    private AppLayerFactory factory;

    private RMSchemasRecordsServices rm;

    private SIPFilter filter;

    public IntelliGIDSIPObjectsProvider(String collection, AppLayerFactory factory, SIPFilter filter) {
        this.collection = collection;
        this.factory = factory;
        this.filter = filter;
        this.rm = new RMSchemasRecordsServices(collection, factory);
        init();
    }

    @Override
    public int getStartIndex() {
        return filter.getStartIndex();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        if (filter.getAdministrativeUnit() != null) {
            idUniteAdministrative = Long.parseLong(this.filter.getAdministrativeUnit().getId());
        }
        if (filter.getRubriqueCode() != null) {
            idRubrique = Long.parseLong(filter.getRubriqueCode().getId());
        }

        MetadataSchemaTypes types = factory.getModelLayerFactory().getMetadataSchemasManager().getSchemaTypes(collection);
        metadonneesDossier = types.getSchema(Folder.DEFAULT_SCHEMA).getMetadatas().only(new SearchableMetadataListFilter());
        metadonneesDocument = types.getSchema(Document.DEFAULT_SCHEMA).getMetadatas().only(new SearchableMetadataListFilter());

        System.out.println("Obtention de la liste des documents");
        fichesDocuments = rm.searchDocuments(filter.getSearchQuery());
        System.out.println("Liste de documents obtenue (" + fichesDocuments.size() + ")");
    }

    @Override
    public List<String> getMetadataIds(SIPObject sipObject) {
        List<String> metadataIds = new ArrayList<String>();
        if (sipObject instanceof IntelliGIDSIPFolder) {
            metadataIds.add("numeroRegleConservation");
            metadataIds.add("regleConservation");
        } else if (sipObject instanceof IntelliGIDSIPDocument) {
            IntelliGIDSIPDocument sipDocument = (IntelliGIDSIPDocument) sipObject;
            Document ficheDocument = sipDocument.getFicheMetadonnees();
            boolean isCourriel = ficheDocument.getSchema().getCode().equals(Email.SCHEMA);
            if (isCourriel) {
                metadataIds.add("courrielA");
                metadataIds.add("courrielDe");
                metadataIds.add("courrielAuNomDe");
                metadataIds.add("courrielCc");
                metadataIds.add("courrielCci");
                metadataIds.add("courrielObjet");
            } else {
                metadataIds.add("typeDocument");
            }
        }
        return metadataIds;
    }

    @Override
    public List<String> getMetadataValues(SIPObject sipObject, String metadataId) {
        List<String> metadataValues = new ArrayList<String>();
        if (sipObject instanceof IntelliGIDSIPFolder) {
            IntelliGIDSIPFolder sipFolder = (IntelliGIDSIPFolder) sipObject;
            Folder ficheDossier = sipFolder.getFicheMetadonnees();
            Folder ficheDossierCourante = ficheDossier;
            while (ficheDossierCourante.getParentFolder() != null) {
                ficheDossierCourante = rm.getFolder(ficheDossierCourante.getParentFolder());
            }
            RetentionRule regleConservation = rm.getRetentionRule(ficheDossierCourante.getRetentionRule());
            CopyRetentionRule delai = regleConservation.getCopyRetentionRules().get(0);
            if ("numeroRegleConservation".equals(metadataId)) {
                metadataValues.add(delai.getCode());
            } else if ("regleConservation".equals(metadataId)) {
                metadataValues.add(delai.getTitle());
            }
        } else if (sipObject instanceof IntelliGIDSIPDocument) {
            IntelliGIDSIPDocument sipDocument = (IntelliGIDSIPDocument) sipObject;
            Document ficheDocument = sipDocument.getFicheMetadonnees();
            boolean isCourriel = ficheDocument.getSchema().getCode().equals(Email.SCHEMA);
            if (isCourriel) {
                String nomMetadonnee;
                if ("emailTo".equals(metadataId)) {
                    nomMetadonnee = Email.EMAIL_TO;
                } else if ("emailFrom".equals(metadataId)) {
                    nomMetadonnee = Email.EMAIL_FROM;
                } else if ("emailInNameOf".equals(metadataId)) {
                    nomMetadonnee = Email.EMAIL_IN_NAME_OF;
                } else if ("emailCCTo".equals(metadataId)) {
                    nomMetadonnee = Email.EMAIL_CC_TO;
                } else if ("emailBCCTo".equals(metadataId)) {
                    nomMetadonnee = Email.EMAIL_BCC_TO;
                } else if ("emailObject".equals(metadataId)) {
                    nomMetadonnee = Email.EMAIL_OBJECT;
                } else {
                    nomMetadonnee = null;
                }
                if (nomMetadonnee != null) {
                    String metadataValue = ficheDocument.get(nomMetadonnee);
                    if (StringUtils.isNotBlank(metadataValue)) {
                        metadataValues.add(metadataValue);
                    }
                }
            } else {
                if ("typeDocument".equals(metadataId)) {
                    DocumentType typeDocument = rm.getDocumentType(ficheDocument.<String>get(Document.TYPE));
                    if (typeDocument != null) {
                        metadataValues.add(typeDocument.getCode());
                    }
                }
            }
        }
        return metadataValues;
    }

    @Override
    public List<SIPObject> list() {
        return new AbstractList<SIPObject>() {
            @Override
            public SIPObject get(int index) {
                System.out.println("Document " + (index + 1) + " de " + fichesDocuments.size());
                Document ficheDocument = fichesDocuments.get(index);
                return new IntelliGIDSIPDocument(ficheDocument, metadonneesDocument, metadonneesDossier, new EntityRetriever(collection, factory));
            }

            @Override
            public int size() {
                return fichesDocuments.size();
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, byte[]> getExtraFiles(SIPObject sipObject) {
        Map<String, byte[]> result;
        if (sipObject instanceof IntelliGIDSIPDocument) {
            IntelliGIDSIPDocument sipDocument = (IntelliGIDSIPDocument) sipObject;
            Document ficheDocument = sipDocument.getFicheMetadonnees();
            boolean isCourriel = ficheDocument.getSchema().getCode().equals(Email.SCHEMA);
            if (isCourriel && ficheDocument.getContent() != null) {
                File sipDocumentFile = sipDocument.getFile();
                String nomFichier = sipDocument.getFilename();
                Map<String, Object> parsedMessage;
                try {
                    InputStream in = new FileInputStream(sipDocumentFile);
                    parsedMessage = rm.parseEmail(nomFichier, in);
                    if (parsedMessage != null) {
                        result = new LinkedHashMap<String, byte[]>();
                        Map<String, InputStream> streamMap = (Map<String, InputStream>) parsedMessage.get(CLE_FICHIERS_JOINTS);
                        for (Entry<String, InputStream> entry : streamMap.entrySet()) {
                            InputStream fichierJointIn = entry.getValue();
                            byte[] fichierJointBytes = IOUtils.toByteArray(fichierJointIn);
                            result.put(entry.getKey(), fichierJointBytes);
                        }
                    } else {
                        result = null;
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                    result = null;
                }
            } else {
                result = null;
            }
        } else {
            result = null;
        }
        return result;
    }

    private String formatDate(LocalDate date) {
        return date != null ? new SimpleDateFormat("yyyyMMdd").format(date.toDate()) : null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public EADArchdesc getEADArchdesc(SIPObject sipObject) {
        EADArchdesc archdesc;
        if (sipObject instanceof IntelliGIDSIPDocument) {
            IntelliGIDSIPDocument sipDocument = (IntelliGIDSIPDocument) sipObject;
            Document ficheDocument = sipDocument.getFicheMetadonnees();
            Folder ficheDossier = rm.getFolder(ficheDocument.getFolder());

            archdesc = new EADArchdesc();

            String dateCreation = formatDate(ficheDocument.getCreatedOn().toLocalDate());
            if (dateCreation != null) {
                archdesc.getDidUnitDates().put("creation", dateCreation);
            }

            String datePublication = formatDate(ficheDocument.getFolderActualDepositDate());
            if (datePublication != null) {
                archdesc.getDidUnitDates().put("publication", datePublication);
            }

//			List<Language> langues = ficheDocument.get();
//			for (Language langue : langues) {
//				archdesc.getDidLangmaterials().add(langue.getDescription());
//			}

            String resume = ficheDocument.getDescription();
            if (StringUtils.isNotBlank(resume)) {
                archdesc.getDidAbstracts().add(resume);
            }

            List<String> motsCles = ficheDocument.getKeywords();
            if (motsCles != null) {
                for (String motCle : motsCles) {
                    archdesc.getControlAccessSubjects().add(motCle);
                }
            }
            //archdesc.setAccessRestrictLegalStatus(ficheDossier.get(Folder.IS_RESTRICTED_ACCESS).toString());

        } else if (sipObject instanceof IntelliGIDSIPFolder) {
            IntelliGIDSIPFolder sipFolder = (IntelliGIDSIPFolder) sipObject;
            Folder ficheDossier = sipFolder.getFicheMetadonnees();

            archdesc = new EADArchdesc();

            Category processusActivite = rm.getCategory(ficheDossier.getCategory());
            archdesc.getFileplanPs().add(processusActivite.getTitle());

            String dateOuverture = formatDate(ficheDossier.getOpenDate());
            if (dateOuverture != null) {
                archdesc.getDidUnitDates().put("creation", dateOuverture);
            }

            String dateFermeture = formatDate(ficheDossier.getCloseDate());
            if (dateFermeture != null) {
                archdesc.getDidUnitDates().put("closure", dateFermeture);
            }

            String resume = ficheDossier.getDescription();
            if (StringUtils.isNotBlank(resume)) {
                archdesc.getDidAbstracts().add(resume);
            }

            AdministrativeUnit posteClassement = rm.getAdministrativeUnit(ficheDossier.<String>get(Folder.ADMINISTRATIVE_UNIT));
            AdministrativeUnit uniteAdministrative = rm.getAdministrativeUnit(posteClassement.<String>get(AdministrativeUnit.PARENT));

            archdesc.setDidOriginationCorpname(uniteAdministrative.getCode());

//			Boolean exemplairePrincipal = ficheDossier.getMainCopyRule();
//			if (exemplairePrincipal != null) {
//				String libelleStatutExemplaire = exemplairePrincipal ? "Exemplaire principal" : "Exemplaire secondaire";
//				archdesc.getAltformavailPs().add(libelleStatutExemplaire);
//			}
            MetadataSchemasManager manager = factory.getModelLayerFactory().getMetadataSchemasManager();
            MetadataSchemaType documentSchemaType = manager.getSchemaTypes(collection).getSchemaType(Document.SCHEMA_TYPE);
            LogicalSearchCondition conditionDocument = LogicalSearchQueryOperators.from(documentSchemaType).where(documentSchemaType.getDefaultSchema().getMetadata(Document.FOLDER)).isEqualTo(ficheDossier.getId());
            List<Document> documentsLies = rm.wrapDocuments(factory.getModelLayerFactory().newSearchServices().search(new LogicalSearchQuery(conditionDocument)));
            factory.getModelLayerFactory().newSearchServices().ite
            if (documentsLies != null) {
                for (Document documentLie : documentsLies) {
                    List<String> relatedmaterialList = new ArrayList<String>();
                    relatedmaterialList.add(documentLie.getId() + " " + documentLie.getTitle());
                    archdesc.getRelatedmaterialLists().add(relatedmaterialList);
                }
            }

            MetadataSchemaType folderSchemaType = manager.getSchemaTypes(collection).getSchemaType(Folder.SCHEMA_TYPE);
            LogicalSearchCondition conditionFolder = LogicalSearchQueryOperators.from(folderSchemaType).where(folderSchemaType.getDefaultSchema().getMetadata(Folder.PARENT_FOLDER)).isEqualTo(ficheDossier.getId());
            List<Folder> dossiersLies = rm.wrapFolders(factory.getModelLayerFactory().newSearchServices().search(new LogicalSearchQuery(conditionFolder)));
            if (dossiersLies != null) {
                for (Folder dossierLie : dossiersLies) {
                    List<String> relatedmaterialList = new ArrayList<String>();
                    relatedmaterialList.add(dossierLie.getId() + " " + dossierLie.getTitle());
                    archdesc.getRelatedmaterialLists().add(relatedmaterialList);
                }
            }


            //TODO
            List<String> motsCles = ficheDossier.getKeywords();
            for (String motCle : motsCles) {
                archdesc.getControlAccessSubjects().add(motCle);
            }
            //archdesc.setAccessRestrictLegalStatus(ficheDossier.get(Folder.IS_RESTRICTED_ACCESS).toString());
        } else {
            archdesc = null;
        }
        return archdesc;
    }

    private class SearchableMetadataListFilter implements MetadataListFilter {
        @Override
        public boolean isReturned(Metadata metadata) {
            return metadata.isSearchable();
        }
    }

}
