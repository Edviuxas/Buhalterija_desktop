package ds;

import java.io.Serializable;

public class IndividualPersonUser extends User implements Serializable {
    private String name;
    private String surname;
    private String emailAddress;

    public IndividualPersonUser() {

    }

    @Override
    public String toString() {
        return "PERSON: Name='" + name + '\'' +
                ", Surname='" + surname + '\'' +
                ", Email='" + emailAddress + '\'' +
                ", ID=" + id;
    }

    public IndividualPersonUser(String userName, String password, int id, String vardas, String pavarde, String elPastas) {
        super(userName, password, id);
        this.name = vardas;
        this.surname = pavarde;
        this.emailAddress = elPastas;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
