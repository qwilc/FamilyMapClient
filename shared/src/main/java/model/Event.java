package model;

import java.util.Objects;
import java.util.UUID;

/**
 * The Event class stores the data for a row of the event table in the database.
 * Contains fields for eventID, associated username, personID, latitude, longitude, country, city, event type, and year, as well as the corresponding getters and setters
 */
public class Event extends Model {
    /**
     * The unique identifier
     */
    private String eventID;
    /**
     * The username of the associated user
     */
    private String associatedUsername;
    /**
     * The identifier of the associated person
     */
    private String personID;
    /**
     * The latitude
     */
    private double latitude;
    /**
     * The longitude
     */
    private double longitude;
    /**
     * The country
     */
    private String country;
    /**
     * The city
     */
    private String city;
    /**
     * The event type
     */
    private String eventType;
    /**
     * The event year
     */
    private int year;

    /**
     * Creates an Event object
     *
     * @param eventID the event's identifier
     * @param username the username of the associated user
     * @param personID the ID of the associated person
     * @param latitude the event's latitude
     * @param longitude the event's longitude
     * @param country the event's country
     * @param city the event's city
     * @param eventType the event's type
     * @param year the event year
     */
    public Event(String eventID, String username, String personID, double latitude, double longitude,
                 String country, String city, String eventType, int year) {
        this.eventID = eventID;
        this.associatedUsername = username;
        this.personID = personID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = eventType;
        this.year = year;
    }

    public Event(String username, String personID, double latitude, double longitude,
                 String country, String city, String eventType, int year) {
        this.eventID = UUID.randomUUID().toString();
        this.associatedUsername = username;
        this.personID = personID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = eventType;
        this.year = year;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    @Override
    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Double.compare(event.latitude, latitude) == 0
                && Double.compare(event.longitude, longitude) == 0
                && year == event.year
                && Objects.equals(eventID, event.eventID)
                && Objects.equals(associatedUsername, event.associatedUsername)
                && Objects.equals(personID, event.personID)
                && Objects.equals(country, event.country)
                && Objects.equals(city, event.city)
                && Objects.equals(eventType, event.eventType);
    }
}
