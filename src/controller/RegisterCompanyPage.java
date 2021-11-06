package controller;

import ds.CompanyUser;
import ds.FinanceManagementIS;
import ds.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sun.applet.Main;

import javax.xml.crypto.Data;
import java.io.IOException;

public class RegisterCompanyPage {
    @FXML
    public Button btnSave;
    private boolean cameFromMainPage = false;
    private FinanceManagementIS fmis;
    private User currentUser = null;
    private User editingUser = null;
    @FXML
    public TextField textLogin;
    @FXML
    public TextField textCompanyName;
    @FXML
    public TextField textContactPerson;
    @FXML
    public PasswordField textPassword;

    public boolean getCameFromMainPage() {
        return cameFromMainPage;
    }

    public void setCameFromMainPage(boolean cameFromMainPage) {
        this.cameFromMainPage = cameFromMainPage;
    }

    public void setEditingUser(User editingUser) {
        this.editingUser = editingUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void registerCompanyUser(ActionEvent actionEvent) throws IOException {
        if (textLogin.getText() != "" && textCompanyName.getText() != "" && textPassword.getText() != "" && textContactPerson.getText() != "") {
            if (!textLogin.getText().equals("admin")) {
                if (btnSave.getText().equals("Save updates")) {
                    int id = editingUser.getId();
                    editingUser.setUserName(textLogin.getText());
                    editingUser.setPassword(textPassword.getText());
                    ((CompanyUser)editingUser).setName(textCompanyName.getText());
                    ((CompanyUser)editingUser).setContactPerson(textContactPerson.getText());
                    DatabaseController.updateUser(id, editingUser);
                    if (!cameFromMainPage) {
                        loadManageUsers();
                    } else {
                        loadMainPage();
                    }
                } else {
                    addCompanyUser(textLogin.getText(), textPassword.getText(), textCompanyName.getText(), textContactPerson.getText());
                    loadLoginPage();
                }
            } else {
                AlertBoxes.showErrorBox("Username can not be admin");
            }
        }
    }

    public void cancelRegistration(ActionEvent actionEvent) throws IOException {
        if (!cameFromMainPage) {
            if (btnSave.getText().equals("Save")) {
                loadLoginPage();
            } else {
                loadManageUsers();
            }
        } else {
            loadMainPage();
        }
    }

    private void addCompanyUser(String login, String password, String companyName, String contactPerson) {
        for (User user : fmis.getAllUsers()) {
            if (user instanceof CompanyUser) {
                if (((CompanyUser) user).getName() == companyName) {
                    AlertBoxes.showErrorBox("User already exists");
                    return;
                }
            }
        }
        int id = 0;
        if (fmis.getAllUsers().stream().count() > 0) {
            id = fmis.getAllUsers().get((int) fmis.getAllUsers().stream().count() - 1).getId();
        }
        CompanyUser companyUser = new CompanyUser(login, password, id+1, companyName, contactPerson);
        DatabaseController.addUser(companyUser);
        fmis.getAllUsers().add(companyUser);
    }

    private void loadLoginPage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginPage.fxml"));
        Parent root = loader.load();

        LoginPage loginPage = loader.getController();
        loginPage.setFmis(fmis);

        Stage stage = (Stage) textCompanyName.getScene().getWindow();
        stage.setTitle("Login Page");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void setFmis(FinanceManagementIS fmis) {
        this.fmis = fmis;
    }

    private void loadManageUsers() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ManageUsersPage.fxml"));
        Parent root = loader.load();

        ManageUsersPage manageUsersPage = loader.getController();
        manageUsersPage.setFmis(fmis);
        manageUsersPage.setCurrentUser(currentUser);

        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.setTitle("Manage Users");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void loadMainPage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
        Parent root = loader.load();

        MainPage mainPage = loader.getController();
        mainPage.setFmis(fmis);
        mainPage.setCurrentUser(currentUser);

        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.setTitle("Manage Users");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
