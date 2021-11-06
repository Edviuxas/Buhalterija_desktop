package ds;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

public class FinanceManagementIS implements Serializable {

    private ArrayList<Category> categories = new ArrayList<>();
    private ArrayList<User> allUsers = new ArrayList<>();
    private String companyName;
    private LocalDate systemCreated;
    private String currentVersion;

    public LocalDate getSystemCreated() {
        return systemCreated;
    }

    public void setSystemCreated(LocalDate systemCreated) {
        this.systemCreated = systemCreated;
    }

    public FinanceManagementIS() {
    }

    public FinanceManagementIS(String companyName, LocalDate systemCreated, String currentVersion) {
        this.companyName = companyName;
        this.systemCreated = systemCreated;
        this.currentVersion = currentVersion;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    public ArrayList<User> getAllUsers() {
        return allUsers;
    }

    public void setAllUsers(ArrayList<User> allUsers) {
        this.allUsers = allUsers;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }


}
