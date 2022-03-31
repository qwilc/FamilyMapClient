package result;

import model.Event;
import model.Model;

/**
 * The EventResult class stores the data for the result body for event requests where an event ID was specified
 */
public class EventResult extends Result {
    /**
     * The username of the associated user
     */
    private String associatedUsername;
    /**
     * The event ID
     */
    private String eventID;
    /**
     * The ID of the person whose event this is
     */
    private String personID;
    /**
     * The event's latitude
     */
    private double latitude;
    /**
     * The event's longitude
     */
    private double longitude;
    /**
     * The country where the event took place
     */
    private String country;
    /**
     * The city where the event took place
     */
    private String city;
    /**
     * The type of event
     */
    private String eventType;
    /**
     * The event year
     */
    private int year;

    /**
     * Creates an EventResult Object
     *
     * @param message the message to return in case of an error
     * @param success indicates whether the request was successful
     * @param associatedUsername the associated username
     * @param eventID the event ID
     * @param personID the person ID
     * @param latitude the event latitude
     * @param longitude the event longitude
     * @param country the event country
     * @param city the event city
     * @param eventType the event type
     * @param year the event year
     */
    public EventResult(String message, boolean success, String associatedUsername, String eventID, String personID,
                       double latitude, double longitude, String country, String city, String eventType, int year) {
        super(message, success);
        this.associatedUsername = associatedUsername;
        this.eventID = eventID;
        this.personID = personID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = eventType;
        this.year = year;
    }

    @Override
    public void setDataFromModel(Model model) {
        Event event = (Event) model;
        this.associatedUsername = event.getAssociatedUsername();
        this.eventID = event.getEventID();
        this.personID = event.getPersonID();
        this.latitude = event.getLatitude();
        this.longitude = event.getLongitude();
        this.country = event.getCountry();
        this.city = event.getCity();
        this.eventType = event.getEventType();
        this.year = event.getYear();
    }

    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

}
