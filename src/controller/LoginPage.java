package controller;

import ds.FinanceManagementIS;
import ds.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginPage implements Initializable {

    private FinanceManagementIS fmis = null;
    @FXML
    public TextField textfieldLogin;
    @FXML
    public PasswordField textfieldPassword;
    @FXML
    public Button btnSignIn;

    boolean isCompanyUser, isIndividualUser;

    public void validateUser(ActionEvent actionEvent) throws IOException {
        fmis.setAllUsers(DatabaseController.getAllUsers());
        /*User loginUser = DatabaseController.getUser(textfieldLogin.getText(), textfieldPassword.getText());
        if (loginUser != null) {
            loadMainWindow(loginUser);
            return;
        }*/
        for (User user : fmis.getAllUsers()) {
            if (user.getUserName().equals(textfieldLogin.getText()) && user.getPassword().equals(textfieldPassword.getText())) {
                loadMainWindow(user);
                return;
            }
        }
        AlertBoxes.showErrorBox("Validation failed.");
    }

    private void loadMainWindow(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
        Parent root = loader.load();

        MainPage mainPage = loader.getController();
        mainPage.setFmis(fmis);
        mainPage.setCurrentUser(user);

        Stage stage = (Stage) btnSignIn.getScene().getWindow();
        stage.setTitle("Main Page");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setFmis(FinanceManagementIS fmis) {
        this.fmis = fmis;
    }

    public void registerUser(ActionEvent actionEvent) throws IOException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Finance management system");
        alert.setHeaderText("What type of user are you.");
        alert.setContentText("Please choose");

        ButtonType buttonTypeCompanyUser = new ButtonType("Company");
        ButtonType buttonTypeIndividualUser = new ButtonType("Individual");

        alert.getButtonTypes().setAll(buttonTypeCompanyUser, buttonTypeIndividualUser);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeCompanyUser) {
            isCompanyUser = true;
        } else if (result.get() == buttonTypeIndividualUser) {
            isIndividualUser = true;
        }

        FXMLLoader loader = new FXMLLoader();
        Parent root = null;
        if (isCompanyUser) {
            loader.setLocation(getClass().getResource("RegisterCompanyPage.fxml"));
            root = loader.load();
            RegisterCompanyPage registerCompanyPage = loader.getController();
            registerCompanyPage.setFmis(fmis);
        } else {
            loader.setLocation(getClass().getResource("RegisterIndividualPage.fxml"));
            root = loader.load();
            RegisterIndividualPage registerIndividualPage = loader.getController();
            registerIndividualPage.setFmis(fmis);
        }

        Stage stage = (Stage) btnSignIn.getScene().getWindow();
        stage.setTitle("Register new user");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
