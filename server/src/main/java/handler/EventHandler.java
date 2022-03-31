package handler;

import service.AllEventsService;
import service.EventService;

public class EventHandler extends GetDataHandler {
    @Override
    protected void initializeAllModelsService() {
        service = new AllEventsService();
    }

    @Override
    protected void initializeSingleModelService() {
        service = new EventService();
    }
}
