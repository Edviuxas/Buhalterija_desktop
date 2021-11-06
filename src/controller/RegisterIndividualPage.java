package controller;

import ds.FinanceManagementIS;
import ds.IndividualPersonUser;
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
import java.io.IOException;

public class RegisterIndividualPage {
    @FXML
    public Button btnSave;
    private boolean cameFromMainPage = false;
    private FinanceManagementIS fmis;
    private User currentUser = null;
    private User editingUser = null;
    @FXML
    public TextField textLogin;
    @FXML
    public TextField textName;
    @FXML
    public PasswordField textPassword;
    @FXML
    public TextField textSurname;
    @FXML
    public TextField textEmail;

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

    public void registerIndividualUser(ActionEvent actionEvent) throws IOException {
        if (textLogin.getText() != "" && textPassword.getText() != "" && textEmail.getText() != "" && textName.getText() != "" && textSurname.getText() != "") {
            if (!textLogin.getText().equals("admin")) {
                if (btnSave.getText().equals("Save updates")) {
                    int id = editingUser.getId();
                    editingUser.setUserName(textLogin.getText());
                    editingUser.setPassword(textPassword.getText());
                    ((IndividualPersonUser) editingUser).setName(textName.getText());
                    ((IndividualPersonUser) editingUser).setEmailAddress(textEmail.getText());
                    ((IndividualPersonUser) editingUser).setSurname(textSurname.getText());
                    DatabaseController.updateUser(id, editingUser);
                    if (!cameFromMainPage) {
                        loadManageUsers();
                    } else {
                        loadMainPage();
                    }
                } else {
                    addIndividualUser(textLogin.getText(), textPassword.getText(), textEmail.getText(), textName.getText(), textSurname.getText());
                    loadLoginPage();
                }
            } else {
                AlertBoxes.showErrorBox("Username can not be admin");
            }
        }
    }

    private void addIndividualUser(String login, String password, String email, String name, String surname) {
        for (User user : fmis.getAllUsers()) {
            if (user instanceof IndividualPersonUser) {
                IndividualPersonUser individualPersonUser = (IndividualPersonUser)user;
                if (individualPersonUser.getName() == name && individualPersonUser.getSurname() == surname && individualPersonUser.getUserName() == login) {
                    AlertBoxes.showErrorBox("User already exists");
                    return;
                }
            }
        }
        int id = 0;
        if (fmis.getAllUsers().stream().count() > 0) {
            id = fmis.getAllUsers().get((int) fmis.getAllUsers().stream().count() - 1).getId();
        }
        IndividualPersonUser individualPersonUser = new IndividualPersonUser(login, password, id+1, name, surname, email);
        DatabaseController.addUser(individualPersonUser);
        fmis.getAllUsers().add(individualPersonUser);
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

    public void setFmis(FinanceManagementIS fmis) {
        this.fmis = fmis;
    }

    private void loadLoginPage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginPage.fxml"));
        Parent root = loader.load();

        LoginPage loginPage = loader.getController();
        loginPage.setFmis(fmis);

        Stage stage = (Stage) textEmail.getScene().getWindow();
        stage.setTitle("Login Page");
        stage.setScene(new Scene(root));
        stage.show();
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
