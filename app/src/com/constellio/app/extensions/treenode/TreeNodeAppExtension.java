package com.constellio.app.extensions.treenode;

import com.constellio.app.modules.es.constants.ESTaxonomies;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.app.ui.framework.data.trees.RecordTreeNodesDataProvider;
import com.constellio.app.ui.framework.data.trees.SmbRecordTreeNodesDataProvider;

public class TreeNodeAppExtension implements TreeNodeExtension {
    @Override
    public RecordTreeNodesDataProvider getTreeNodeFor(String codeToxonomie, AppLayerFactory appLayerFactory) {
        RecordTreeNodesDataProvider recordTreeNode = null;

        if(codeToxonomie.equals(ESTaxonomies.SMB_FOLDERS)) {
            recordTreeNode = new SmbRecordTreeNodesDataProvider(codeToxonomie, appLayerFactory);
        }

        return recordTreeNode;
    }
}
