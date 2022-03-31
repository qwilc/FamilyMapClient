package treeGenerator;

import dao.DataAccessException;
import dao.EventDAO;
import dao.PersonDAO;
import helperData.DataReader;
import helperData.Location;
import logger.LoggerConfig;
import model.Event;
import model.Person;
import model.User;

import java.sql.Connection;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TreeGenerator {
    private final User user;
    private int numPeopleAdded = 0;
    private int numEventsAdded = 0;
    private final Connection conn;
    private final String associatedUsername;
    private final Logger logger = Logger.getLogger("TreeGenerator");
    private final Random random = new Random();

    public TreeGenerator(Connection conn, User user) {
        this.conn = conn;
        this.user = user;
        this.associatedUsername = user.getUsername();
        LoggerConfig.configureLogger(logger, Level.INFO);
    }

    public void generateTree(int numGenerations) throws DataAccessException {
        Person userPerson = generateUserPerson();
        if(numGenerations > 0) {
            generateAncestors(userPerson, numGenerations);
        }
        else {
            addPersonToDatabase(userPerson);
        }

        PersonDAO personDAO = new PersonDAO(conn);

        logger.finer("Does userPerson exist? " + (personDAO.query(user.getPersonID() ) != null) );
        logger.finer("User's fatherID: " + userPerson.getFatherID());
        logger.finer(personDAO.query(userPerson.getFatherID()).getPersonID());
    }

    public void generateAncestors(Person person, int numGenerations) {
        int childBirthYear = getBirthYear(person.getPersonID());

        Person mother = generatePerson("f", childBirthYear);
        Person father = generatePerson("m", childBirthYear);

        generateMarriageEvent(mother, father);
        mother.setSpouseID(father.getPersonID());
        father.setSpouseID(mother.getPersonID());

        person.setMotherID(mother.getPersonID());
        person.setFatherID(father.getPersonID());

        logger.finest("Person's fatherID: " + person.getFatherID() + " Father's personID: " + father.getPersonID());

        addPersonToDatabase(person);

        if(numGenerations > 1) {
            numGenerations--;
            generateAncestors(mother, numGenerations);
            generateAncestors(father, numGenerations);
        }
        else {
            addPersonToDatabase(mother);
            addPersonToDatabase(father);
        }
    }

    private void addPersonToDatabase(Person person) {
        try {
            numPeopleAdded++;
            PersonDAO dao = new PersonDAO(conn);
            dao.insert(person);
        }
        catch (DataAccessException ex) {
            logger.severe("Error inserting randomly generated person into database");
            ex.printStackTrace();
        }
    }

    public Person generateUserPerson() {
        Person person = new Person(
                user.getPersonID(),
                associatedUsername,
                user.getFirstName(),
                user.getLastName(),
                user.getGender(),
                null,
                null,
                null);

        generateUserBirthEvent(person.getPersonID());

        return person;
    }

    public Person generatePerson(String gender, int childBirthYear) {
        Person person = new Person(associatedUsername, null, null, gender, null, null, null);
        setRandomName(person);

        generateBirthEvent(person.getPersonID(), childBirthYear);
        generateDeathEvent(person.getPersonID());

        return person;
    }

    private void generateMarriageEvent(Person mother, Person father) {
        int marriageYear = getBirthYear(mother.getPersonID()) + 25;
        Event marriage = new Event(associatedUsername, null, 0, 0, null, null, "marriage", marriageYear);
        setRandomLocation(marriage);

        marriage.setPersonID(mother.getPersonID());
        addEventToDatabase(marriage);

        marriage.setEventID(UUID.randomUUID().toString());
        marriage.setPersonID(father.getPersonID());
        addEventToDatabase(marriage);
    }

    private void generateUserBirthEvent(String personID) {
        Event birth = new Event(associatedUsername, personID, 0, 0, null, null, "birth", 2000);
        setRandomLocation(birth);
        addEventToDatabase(birth);
    }

    private void generateBirthEvent(String personID, int childBirthYear) {
        int birthYear = childBirthYear - 30;
        Event birth = new Event(associatedUsername, personID, 0, 0, null, null, "birth", birthYear);
        setRandomLocation(birth);
        addEventToDatabase(birth);
    }

    private void generateDeathEvent(String personID) {
        int deathYear = getBirthYear(personID) + 70;
        Event death = new Event(associatedUsername, personID, 0, 0, null, null, "death", deathYear);
        setRandomLocation(death);
        addEventToDatabase(death);
    }

    private void addEventToDatabase(Event event) {
        try {
            EventDAO dao = new EventDAO(conn);
            dao.insert(event);
            numEventsAdded++;
        }
        catch (DataAccessException ex) {
            logger.severe("Error inserting randomly generated event into database");
            ex.printStackTrace();
        }
    }

    private void setRandomLocation(Event event) {
        Location location = selectLocation();
        event.setCountry(location.getCountry());
        event.setCity(location.getCity());
        event.setLatitude(location.getLatitude());
        event.setLongitude(location.getLongitude());
    }

    private Location selectLocation() {
        Location[] possibleLocations = DataReader.getLocations().getData();

        assert possibleLocations != null;
        int index = random.nextInt(possibleLocations.length);
        return possibleLocations[index];
    }

    private void setRandomName(Person person) {
        person.setFirstName(selectFirstName(person.getGender()));
        person.setLastName(selectLastName());
    }

    private String selectFirstName(String gender) {
        String[] possibleNames = null;

        if(Objects.equals(gender, "f")) {
            possibleNames = DataReader.getFemaleNames().getData();
        }
        else if(Objects.equals(gender, "m")) {
            possibleNames = DataReader.getMaleNames().getData();
        }

        assert possibleNames != null;
        int index = random.nextInt(possibleNames.length);
        return possibleNames[index];
    }

    private String selectLastName() {
        String[] possibleNames = DataReader.getSurnames().getData();

        assert possibleNames != null;
        int index = random.nextInt(possibleNames.length);
        return possibleNames[index];
    }

    private int getBirthYear(String personID) {
        try {
            EventDAO dao = new EventDAO(conn);
            Event[] queryResult = dao.queryByPersonAndType(personID, "birth");
            assert (queryResult.length == 1);

            Event birth = queryResult[0];
            return birth.getYear();
        }
        catch (DataAccessException ex) {
            logger.severe("Error getting birth year");
            ex.printStackTrace();
        }
        return -1;
    }

    public int getNumPeopleAdded() {
        return numPeopleAdded;
    }

    public int getNumEventsAdded() {
        return numEventsAdded;
    }
}
