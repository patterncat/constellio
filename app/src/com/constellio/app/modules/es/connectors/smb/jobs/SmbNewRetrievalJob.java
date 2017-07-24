package com.constellio.app.modules.es.connectors.smb.jobs;

import com.constellio.app.modules.es.connectors.smb.ConnectorSmb;
import com.constellio.app.modules.es.connectors.smb.cache.SmbConnectorContext;
import com.constellio.app.modules.es.connectors.smb.jobmanagement.SmbConnectorJob;
import com.constellio.app.modules.es.connectors.smb.jobmanagement.SmbJobFactoryImpl.SmbJobCategory;
import com.constellio.app.modules.es.connectors.smb.jobmanagement.SmbJobFactoryImpl.SmbJobType;
import com.constellio.app.modules.es.connectors.smb.service.SmbFileDTO;
import com.constellio.app.modules.es.connectors.smb.service.SmbModificationIndicator;
import com.constellio.app.modules.es.connectors.smb.service.SmbRecordService;
import com.constellio.app.modules.es.connectors.spi.Connector;
import com.constellio.app.modules.es.model.connectors.ConnectorDocument;
import com.constellio.app.modules.es.model.connectors.smb.ConnectorSmbDocument;
import com.constellio.app.modules.es.model.connectors.smb.ConnectorSmbFolder;
import com.constellio.model.entities.schemas.Schemas;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;

public class SmbNewRetrievalJob extends SmbConnectorJob {
    private static final String jobName = SmbNewRetrievalJob.class.getSimpleName();
    private final boolean folder;
    private final JobParams jobParams;
    private final SmbModificationIndicator shareIndicator;


    public SmbNewRetrievalJob(JobParams params, SmbModificationIndicator shareIndicator, boolean folder) {
        super(params.getConnector(), jobName);
        this.jobParams = params;
        this.shareIndicator = shareIndicator;
        this.folder = folder;
    }

    @Override
    public void execute(Connector connector) {
        String url = jobParams.getUrl();

        boolean seed = jobParams.getConnectorInstance().getSeeds().contains(url);

        SmbConnectorContext context = jobParams.getConnector().getContext();
        SmbFileDTO smbFileDTO = jobParams.getSmbShareService().getSmbFileDTO(url);

        switch (smbFileDTO.getStatus()) {
            case FULL_DTO:
                try {
                    final ConnectorDocument connectorDocument = jobParams.getSmbRecordService().newConnectorDocument(url);

                    String parentId = context.getId(jobParams.getParentUrl());
                    if (seed || parentId != null) {
                        jobParams.getUpdater().updateDocumentOrFolder(smbFileDTO, connectorDocument, parentId, seed);
                        jobParams.getEventObserver().push(Arrays.asList((ConnectorDocument) connectorDocument));

                        SmbModificationIndicator smbModificationIndicator = new SmbModificationIndicator(smbFileDTO.getPermissionsHash(), smbFileDTO.getLength(), smbFileDTO.getLastModified());
                        context.traverseModified(url, smbModificationIndicator, jobParams.getConnectorInstance().getTraversalCode());
                    }
                } catch (Exception e) {
                    this.connector.getLogger().errorUnexpected(e);
                }
                break;
            case FAILED_DTO:
                try {
                    final ConnectorDocument connectorDocument = jobParams.getSmbRecordService().newConnectorDocument(url);
                    String parentId = context.getId(jobParams.getParentUrl());
                    if (parentId != null) {
                        jobParams.getUpdater().updateFailedDocumentOrFolder(smbFileDTO, connectorDocument, parentId);
                        jobParams.getEventObserver().push(Arrays.asList((ConnectorDocument) connectorDocument));
                    }
                } catch (Exception e) {
                    this.connector.getLogger().errorUnexpected(e);
                }
                break;
            case DELETE_DTO:
                try {
                    if (jobParams.getConnector().getContext().getId(url) != null) {
                        SmbConnectorJob deleteJob = jobParams.getJobFactory().get(SmbJobCategory.DELETE, url, jobParams.getParentUrl());
                        ConnectorSmb connectorSmb = (ConnectorSmb) connector;
                        connectorSmb.queueJob(deleteJob);
                    }
                } catch (Exception e) {
                    this.connector.getLogger().errorUnexpected(e);
                }
                break;
            default:
                this.connector.getLogger()
                        .error("Unexpected DTO type for : " + url, "", new LinkedHashMap<String, String>());
                break;
        }
    }

    @Override
    public String getUrl() {
        return jobParams.getUrl();
    }

    @Override
    public String toString() {
        return jobName + '@' + Integer.toHexString(hashCode()) + " - " + jobParams.getSmbUtils();
    }

    @Override
    public SmbJobType getType() {
        return SmbJobType.NEW_DOCUMENT_JOB;
    }
}