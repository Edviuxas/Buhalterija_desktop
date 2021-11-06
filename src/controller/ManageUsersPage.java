package controller;

import ds.CompanyUser;
import ds.FinanceManagementIS;
import ds.IndividualPersonUser;
import ds.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import javax.xml.crypto.Data;
import java.io.IOException;

public class ManageUsersPage {
    @FXML
    public ListView listUsers;
    private FinanceManagementIS fmis;
    private User currentUser;

    public void setFmis(FinanceManagementIS fmis) {
        this.fmis = fmis;
        populateListView();
    }

    private void populateListView() {
        listUsers.getItems().clear();
        for (User user : fmis.getAllUsers()) {
            listUsers.getItems().add("Username: " + user.getUserName() + " ID: " + user.getId());
        }
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void updateUser(ActionEvent actionEvent) throws IOException {
        if (listUsers.getSelectionModel().getSelectedItem() != null) {
            String[] values = listUsers.getSelectionModel().getSelectedItem().toString().split(" ");
            User user = getUser(Integer.parseInt(values[3]), fmis);

            FXMLLoader loader = new FXMLLoader();
            Parent root = null;

            if (user instanceof CompanyUser) {
                loader = new FXMLLoader(getClass().getResource("RegisterCompanyPage.fxml"));
                root = loader.load();
                CompanyUser companyUser = (CompanyUser) user;
                RegisterCompanyPage registerCompanyPage = loader.getController();
                registerCompanyPage.setFmis(fmis);
                registerCompanyPage.textContactPerson.setText(companyUser.getContactPerson());
                registerCompanyPage.textPassword.setText(companyUser.getPassword());
                registerCompanyPage.textCompanyName.setText(companyUser.getName());
                registerCompanyPage.textLogin.setText(companyUser.getUserName());
                registerCompanyPage.btnSave.setText("Save updates");
                registerCompanyPage.setCurrentUser(currentUser);
                registerCompanyPage.setEditingUser(companyUser);
            } else {
                loader = new FXMLLoader(getClass().getResource("RegisterIndividualPage.fxml"));
                root = loader.load();
                IndividualPersonUser individualPersonUser = (IndividualPersonUser) user;
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
            }

            Stage stage = (Stage) listUsers.getScene().getWindow();
            stage.setTitle("Update user");
            stage.setScene(new Scene(root));
            stage.show();
        }
    }

    public void moreUserInformation(ActionEvent actionEvent) {
        if (listUsers.getSelectionModel().getSelectedItem() != null) {
            String[] values = listUsers.getSelectionModel().getSelectedItem().toString().split(" ");
            User user = getUser(Integer.parseInt(values[3]), fmis);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText(user.toString());
            alert.showAndWait();
        }
    }

    public void deleteUser(ActionEvent actionEvent) {
        if (listUsers.getSelectionModel().getSelectedItem() != null) {
            String[] values = listUsers.getSelectionModel().getSelectedItem().toString().split(" ");
            User userWeAreRemoving = getUser(Integer.parseInt(values[3]), fmis);
            if (!userWeAreRemoving.getUserName().equals("admin")) {
                DatabaseController.deleteUser(userWeAreRemoving.getId());
                fmis.getAllUsers().remove(userWeAreRemoving);
            } else {
                AlertBoxes.showErrorBox("Can not remove admin user");
            }
        }
        populateListView();
    }

    public static User getUser(int id, FinanceManagementIS fmis) {
        for (User user : fmis.getAllUsers()) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    public void exitPage(ActionEvent actionEvent) throws IOException {
        loadMainWindow(currentUser);
    }

    private void loadMainWindow(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
        Parent root = loader.load();

        MainPage mainPage = loader.getController();
        mainPage.setFmis(fmis);
        mainPage.setCurrentUser(user);

        Stage stage = (Stage) listUsers.getScene().getWindow();
        stage.setTitle("Main Page");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
