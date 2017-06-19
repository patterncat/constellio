package com.constellio.app.modules.es.connectors.smb.cache;

import com.constellio.app.modules.es.connectors.smb.service.SmbModificationIndicator;

/**
 * Created by admin on 2017-06-19.
 */
public class ContextUtils {

    public static boolean equals(SmbModificationIndicator firstIndicator, SmbModificationIndicator secondIndicator, boolean folder) {
        if (folder) {
            if (firstIndicator.getLastModified() == secondIndicator.getLastModified()) {
                return true;
            }
        } else {
            if (firstIndicator.equals(secondIndicator)) {
                return true;
            }
        }
        return false;
    }
}
