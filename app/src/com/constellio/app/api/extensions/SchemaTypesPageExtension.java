package com.constellio.app.api.extensions;

import java.util.ArrayList;
import java.util.List;

import com.constellio.app.api.extensions.params.GetAvailableExtraMetadataAttributesParam;
import com.constellio.app.api.extensions.params.IsBuiltInMetadataAttributeModifiableParam;
import com.constellio.data.frameworks.extensions.ExtensionBooleanResult;

public class SchemaTypesPageExtension {

	/**
	 * Return available custom attributes for a given metadata
	 * A custom attribute will appear has a checkbox in the metadata form
	 * Then it can be used to filter metadatas
	 *
	 * @param param
	 * @return
	 */
	public List<String> getAvailableExtraMetadataAttributes(GetAvailableExtraMetadataAttributesParam param) {
		return new ArrayList<>();
	}

	/**
	 * Specify if the given metadata attribute is modifiable
	 *
	 * @param param
	 * @return
	 */
	public ExtensionBooleanResult isBuiltInMetadataAttributeModifiable(IsBuiltInMetadataAttributeModifiableParam param) {
		return ExtensionBooleanResult.NOT_APPLICABLE;
	}
}
