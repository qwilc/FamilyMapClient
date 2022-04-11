package model;

public class FamilyMember {
    private String fullName;
    private String relation;
    private String personID;

    public FamilyMember(String fullName, String relation, String personID) {
        this.fullName = fullName;
        this.relation = relation;
        this.personID = personID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }
}
