package com.constellio.model.entities.configs;

public interface ConfigsProvider {

    <T> T getValue(SystemConfiguration configuration);

}
