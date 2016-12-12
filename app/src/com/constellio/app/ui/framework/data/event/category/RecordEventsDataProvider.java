package com.constellio.app.ui.framework.data.event.category;

import com.constellio.app.modules.rm.services.events.RMEventsSearchServices;
import com.constellio.app.services.factories.ConstellioFactories;
import com.constellio.app.ui.framework.data.event.EventStatistics;
import com.constellio.app.ui.pages.events.EventsCategoryDataProvider;
import com.constellio.model.entities.records.wrappers.EventType;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.services.factories.ModelLayerFactory;
import com.constellio.model.services.search.SearchServices;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.constellio.app.ui.i18n.i18n.$;

public class RecordEventsDataProvider implements EventsCategoryDataProvider {

    transient List<EventStatistics> events;

    private String collection;

    private String currentUserName;

    private String recordID;

    public RecordEventsDataProvider(ModelLayerFactory modelLayerFactory, String collection,
                                                        String currentUserName, String recordID) {
        this.collection = collection;
        this.currentUserName = currentUserName;
        this.recordID = recordID;
        init(modelLayerFactory);
    }

    private void readObject(java.io.ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        ConstellioFactories constellioFactories = ConstellioFactories.getInstance();
        init(constellioFactories.getModelLayerFactory());
    }

    void init(ModelLayerFactory modelLayerFactory) {
        SearchServices searchServices = modelLayerFactory.newSearchServices();
        RMEventsSearchServices rmSchemasRecordsServices = new RMEventsSearchServices(modelLayerFactory, collection);
        events = new ArrayList<>();
        EventStatistics eventStatistics = new EventStatistics();
        eventStatistics.setLabel($("ListEventsView.foldersCreation"));
        User currentUser = modelLayerFactory.newUserServices().getUserInCollection(currentUserName, collection);
        LogicalSearchQuery query = rmSchemasRecordsServices
                .newFindEventByRecordIDQuery(currentUser, recordID);
        eventStatistics.setValue((float) searchServices.getResultsCount(query));
        events.add(eventStatistics);
    }

    @Override
    public String getEventType(Integer index) {
        if (index == 0) {
            return EventType.CREATE_FOLDER;
        } else if (index == 1) {
            return EventType.CREATE_DOCUMENT;
        } else {
            return EventType.CREATE_TASK;
        }
    }

    public int size() {
        return 3;
    }

    @Override
    public String getDataTitle() {
        return $("ListEventsView.foldersAndDocumentsCreation");
    }

    @Override
    public String getDataReportTitle() {
        return $("ListEventsView.foldersAndDocumentsCreation.reportTitle");
    }

    @Override
    public List<EventStatistics> getEvents() {
        if (events == null) {
            ConstellioFactories constellioFactories = ConstellioFactories.getInstance();
            init(constellioFactories.getModelLayerFactory());
        }
        return events;
    }

    @Override
    public EventStatistics getEventStatistics(Integer index) {
        return getEvents().get(index);
    }
}
