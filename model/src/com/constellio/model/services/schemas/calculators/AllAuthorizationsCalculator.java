package com.constellio.model.services.schemas.calculators;

import static com.constellio.model.entities.schemas.MetadataValueType.STRING;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.constellio.model.entities.calculators.CalculatorParameters;
import com.constellio.model.entities.calculators.MetadataValueCalculator;
import com.constellio.model.entities.calculators.dependencies.Dependency;
import com.constellio.model.entities.calculators.dependencies.LocalDependency;
import com.constellio.model.entities.schemas.MetadataValueType;

public class AllAuthorizationsCalculator implements MetadataValueCalculator<List<String>> {

	LocalDependency<List<String>> authorizationsParam = LocalDependency.toAStringList("authorizations");
	LocalDependency<List<String>> inheritedAuthorizationsParam = LocalDependency.toAStringList("inheritedauthorizations");

	@Override
	public List<String> calculate(CalculatorParameters parameters) {
		Set<String> calculatedAuthorizations = new HashSet<>();
		List<String> authorizations = parameters.get(authorizationsParam);
		List<String> inheritedAuthorizations = parameters.get(inheritedAuthorizationsParam);

		calculatedAuthorizations.addAll(authorizations);
		calculatedAuthorizations.addAll(inheritedAuthorizations);
		return new ArrayList<>(calculatedAuthorizations);
	}

	@Override
	public List<String> getDefaultValue() {
		return Collections.emptyList();
	}

	@Override
	public MetadataValueType getReturnType() {
		return STRING;
	}

	@Override
	public boolean isMultiValue() {
		return true;
	}

	@Override
	public List<? extends Dependency> getDependencies() {
		return Arrays.asList(authorizationsParam, inheritedAuthorizationsParam);
	}
}
