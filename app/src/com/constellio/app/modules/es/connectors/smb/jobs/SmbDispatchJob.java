package com.constellio.app.modules.es.connectors.smb.jobs;

import com.constellio.app.modules.es.connectors.smb.ConnectorSmb;
import com.constellio.app.modules.es.connectors.smb.jobmanagement.SmbConnectorJob;
import com.constellio.app.modules.es.connectors.smb.jobmanagement.SmbJobFactoryImpl.SmbJobCategory;
import com.constellio.app.modules.es.connectors.smb.jobmanagement.SmbJobFactoryImpl.SmbJobType;
import com.constellio.app.modules.es.connectors.smb.service.SmbModificationIndicator;
import com.constellio.app.modules.es.connectors.spi.Connector;
import com.constellio.app.modules.es.connectors.spi.ConnectorJob;
import com.constellio.app.modules.es.model.connectors.smb.ConnectorSmbFolder;

import java.util.List;

public class SmbDispatchJob extends SmbConnectorJob {
	private static final String jobName = SmbDispatchJob.class.getSimpleName();
	private final JobParams jobParams;

	public SmbDispatchJob(JobParams jobParams) {
		super(jobParams.getConnector(), jobName);
		this.jobParams = jobParams;
	}

	@Override
	public void execute(Connector connector) {
		ConnectorSmb connectorSmb = (ConnectorSmb) connector;

		String url = jobParams.getUrl();

		SmbConnectorJob smbRetrievalJob = jobParams.getJobFactory().get(SmbJobCategory.RETRIEVAL, url, jobParams.getParentUrl());
		connectorSmb.queueJob(smbRetrievalJob);

		if (!(smbRetrievalJob instanceof SmbDeleteJob)) {
			if (jobParams.getSmbUtils().isFolder(url)) {
				if (connectorSmb.getContext().getId(url) != null) {
					List<String> childrenUrls = jobParams.getSmbShareService().getChildrenUrlsFor(url);

					for (String childUrl : childrenUrls) {
						if (jobParams.getSmbUtils().isFolder(childUrl)) {
							SmbConnectorJob smbChildFolderRetrievalJob = jobParams.getJobFactory().get(SmbJobCategory.DISPATCH, childUrl, url);
							connectorSmb.queueJob(smbChildFolderRetrievalJob);
						} else {
							SmbConnectorJob smbChildDocumentRetrievalJob = jobParams.getJobFactory().get(SmbJobCategory.RETRIEVAL, childUrl, url);
							connectorSmb.queueJob(smbChildDocumentRetrievalJob);
						}
					}
				} else {
					SmbModificationIndicator modificationIndicator = connectorSmb.getContext().getModificationIndicator(url);
					if (modificationIndicator != null) {
						ConnectorSmbFolder connectorSmbFolder = jobParams.getSmbRecordService().getFolder(url);
						if (connectorSmbFolder != null) {
							modificationIndicator.setId(connectorSmbFolder.getId());
						}
					}
				}
			}
		}

	}

	@Override
	public String toString() {
		return jobName + '@' + Integer.toHexString(hashCode()) + " - " + jobParams.getUrl();
	}

	@Override
	public String getUrl() {
		return jobParams.getUrl();
	}

	@Override
	public SmbJobType getType() {
		return SmbJobType.DISPATCH_JOB;
	}
}