package model;

public class FamilyMember {
    private final String fullName;
    private final String relation;
    private final String personID;

    public FamilyMember(String fullName, String relation, String personID) {
        this.fullName = fullName;
        this.relation = relation;
        this.personID = personID;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRelation() {
        return relation;
    }

    public String getPersonID() {
        return personID;
    }
}
