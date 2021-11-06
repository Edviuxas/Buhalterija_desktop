package controller;

import ds.CompanyUser;
import ds.FinanceManagementIS;
import ds.IndividualPersonUser;
import ds.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainPage implements Initializable {

    private User currentUser = null;
    private FinanceManagementIS fmis = null;
    @FXML
    public Button btnManageUsers;
    @FXML
    public Button btnManageCategories;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        if (!(currentUser.getUserName().equals("admin") && currentUser.getPassword().equals("admin"))) {
//            btnManageUsers.setDisable(true);
            btnManageUsers.setText("Change personal information");
        }
    }

    public void setFmis(FinanceManagementIS fmis) {
        this.fmis = fmis;
    }

    public void manageCategories(ActionEvent actionEvent) throws IOException {
        loadManageCategories();
    }

    public void manageUsersClick(ActionEvent actionEvent) throws IOException {
        if (btnManageUsers.getText().equals("Change personal information")) {
            FXMLLoader loader = new FXMLLoader();
            Parent root = null;
            if (currentUser instanceof CompanyUser) {
                loader = new FXMLLoader(getClass().getResource("RegisterCompanyPage.fxml"));
                root = loader.load();
                CompanyUser companyUser = (CompanyUser) currentUser;
                RegisterCompanyPage registerCompanyPage = loader.getController();
                registerCompanyPage.setFmis(fmis);
                registerCompanyPage.textContactPerson.setText(companyUser.getContactPerson());
                registerCompanyPage.textPassword.setText(companyUser.getPassword());
                registerCompanyPage.textCompanyName.setText(companyUser.getName());
                registerCompanyPage.textLogin.setText(companyUser.getUserName());
                registerCompanyPage.btnSave.setText("Save updates");
                registerCompanyPage.setCurrentUser(currentUser);
                registerCompanyPage.setEditingUser(companyUser);
                registerCompanyPage.setCameFromMainPage(true);

                Stage stage = (Stage) btnManageUsers.getScene().getWindow();
                stage.setTitle("Update user");
                stage.setScene(new Scene(root));
                stage.show();
            } else {
                loader = new FXMLLoader(getClass().getResource("RegisterIndividualPage.fxml"));
                root = loader.load();
                IndividualPersonUser individualPersonUser = (IndividualPersonUser) currentUser;
                RegisterIndividualPage registerIndividualPage = loader.getController();
                registerIndividualPage.setFmis(fmis);
                registerIndividualPage.textSurname.setText(individualPersonUser.getSurname());
                registerIndividualPage.textName.setText(individualPersonUser.getName());
                registerIndividualPage.textEmail.setText(individualPersonUser.getEmailAddress());
                registerIndividualPage.textLogin.setText(individualPersonUser.getUserName());
                registerIndividualPage.textPassword.setText(individualPersonUser.getPassword());
                registerIndividualPage.btnSave.setText("Save updates");
                registerIndividualPage.setCurrentUser(currentUser);
                registerIndividualPage.setEditingUser(individualPersonUser);
                registerIndividualPage.setCameFromMainPage(true);

                Stage stage = (Stage) btnManageUsers.getScene().getWindow();
                stage.setTitle("Update user");
                stage.setScene(new Scene(root));
                stage.show();
            }
        } else {
            loadManageUsers();
        }
    }

    private void loadManageCategories() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ManageCategoriesPage.fxml"));
        Parent root = loader.load();

        ManageCategoriesPage manageCategoriesPage = loader.getController();
        manageCategoriesPage.setFmis(fmis);
        manageCategoriesPage.setCurrentUser(currentUser);

        Stage stage = (Stage) btnManageCategories.getScene().getWindow();
        stage.setTitle("Manage Categories Page");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void loadManageUsers() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ManageUsersPage.fxml"));
        Parent root = loader.load();

        ManageUsersPage manageUsersPage = loader.getController();
        manageUsersPage.setFmis(fmis);
        manageUsersPage.setCurrentUser(currentUser);

        Stage stage = (Stage) btnManageCategories.getScene().getWindow();
        stage.setTitle("Manage Users");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void logOff(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginPage.fxml"));
        Parent root = loader.load();

        LoginPage manageUsersPage = loader.getController();
        manageUsersPage.setFmis(fmis);

        Stage stage = (Stage) btnManageCategories.getScene().getWindow();
        stage.setTitle("Login");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
