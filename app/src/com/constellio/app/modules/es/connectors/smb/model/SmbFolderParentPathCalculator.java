package com.constellio.app.modules.es.connectors.smb.model;

import com.constellio.app.modules.es.connectors.smb.LastFetchedStatus;
import com.constellio.app.modules.es.model.connectors.smb.ConnectorSmbFolder;
import com.constellio.model.entities.calculators.CalculatorParameters;
import com.constellio.model.entities.calculators.MetadataValueCalculator;
import com.constellio.model.entities.calculators.dependencies.*;
import com.constellio.model.services.schemas.calculators.ParentPathCalculator;
import com.constellio.model.services.schemas.calculators.PathPartsCalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SmbFolderParentPathCalculator extends ParentPathCalculator implements MetadataValueCalculator<List<String>> {

	LocalDependency<LastFetchedStatus> statusDependency = LocalDependency.toAnEnum(ConnectorSmbFolder.LAST_FETCHED_STATUS);

	@Override
	public List<String> calculate(CalculatorParameters parameters) {
		LastFetchedStatus lastFetchedStatus = parameters.get(statusDependency);
		if (lastFetchedStatus != null && LastFetchedStatus.OK.equals(lastFetchedStatus)) {
			return super.calculate(parameters);
		}
		return new ArrayList<>();
	}

	@Override
	public List<? extends Dependency> getDependencies() {
		return Arrays.asList(taxonomiesParam, statusDependency);
	}
}
