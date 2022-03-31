package handler;

import service.AllPeopleService;
import service.PersonService;

public class PersonHandler extends GetDataHandler {
    @Override
    protected void initializeAllModelsService() {
        service = new AllPeopleService();
    }

    @Override
    protected void initializeSingleModelService() {
        service = new PersonService();
    }
}
