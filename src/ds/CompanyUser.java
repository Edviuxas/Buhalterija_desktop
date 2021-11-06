package ds;

import java.io.Serializable;

public class CompanyUser extends User implements Serializable {
    private String name;
    private String contactPerson;

    @Override
    public String toString() {
        return "COMPANY: Name='" + name + '\'' +
                ", ID=" + id +
                ", Contact person: " + contactPerson;
    }

    public CompanyUser(String userName, String password, int id, String name, String contactPerson) {
        super(userName, password, id);
        this.name = name;
        this.contactPerson = contactPerson;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
}
