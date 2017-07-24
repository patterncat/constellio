package com.constellio.app.modules.es.connectors.smb.service;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;

import java.io.Serializable;

public class SmbModificationIndicator implements Serializable {
    private String id;
    private final String permissionsHash;
    private final double size;
    private final long lastModified;
    private String traversalCode;

    public SmbModificationIndicator(String permissionsHash, double size, long lastModified) {
        this.permissionsHash = permissionsHash;
        this.size = size;
        this.lastModified = lastModified;
    }

    @Override
    public boolean equals(Object other) {
        if (other != null && other instanceof SmbModificationIndicator) {
            SmbModificationIndicator smbModificationIndicator = (SmbModificationIndicator) other;
            if (StringUtils.equals(permissionsHash, smbModificationIndicator.permissionsHash) &&
                    size == smbModificationIndicator.size &&
                    lastModified == smbModificationIndicator.lastModified) {
                return true;
            }
        }
        return false;
    }

    public void setTraversalCode(String traversalCode) {
        this.traversalCode = traversalCode;
    }

    public String getTraversalCode() {
        return traversalCode;
    }

    public String getPermissionsHash() {
        return permissionsHash;
    }

    public double getSize() {
        return size;
    }

    public long getLastModified() {
        return lastModified;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }
}