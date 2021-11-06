package controller;

import ds.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class AddResponsiblePersonPage{
    @FXML
    public ComboBox comboCategory;
    @FXML
    public ComboBox comboUser;
    private FinanceManagementIS fmis;
    private User currentUser;

    public void setFmis(FinanceManagementIS fmis) {
        this.fmis = fmis;
        populateComboBoxes();
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void addResponsiblePerson(ActionEvent actionEvent) throws IOException {
        if (comboUser.getSelectionModel().getSelectedItem() != null && comboCategory.getSelectionModel().getSelectedItem() != null) {
            Category category = getCategory(fmis.getCategories(), comboCategory.getSelectionModel().getSelectedItem().toString());
            if (((category.getCreator() != null && category.getCreator().equals(currentUser)) || currentUser.getUserName().equals("admin"))) {
                User addingUser = fmis.getAllUsers().get(comboUser.getSelectionModel().getSelectedIndex());
                if (!category.getResponsiblePeople().contains(addingUser)) {
                    category.getResponsiblePeople().add(addingUser);
                    DatabaseController.addResponsiblePerson(addingUser, category);
                    addResponsiblePersonToSubcategories(addingUser, category.getSubCategories());
                }
                loadManageCategories();
            }
        }
    }

    private static void addResponsiblePersonToSubcategories(User addingUser, ArrayList<Category> layerOfCategories) {
        for (Category category : layerOfCategories) {
            DatabaseController.addResponsiblePerson(addingUser, category);
            category.getResponsiblePeople().add(addingUser);
            if (!category.getSubCategories().isEmpty()) {
                addResponsiblePersonToSubcategories(addingUser, category.getSubCategories());
            }
        }
    }

    private static Category getCategory(ArrayList<Category> layerOfCategories, String categoryName) {
        Category result;
        for (Category category : layerOfCategories) {
            if (category.getName().equals(categoryName)) {
                result = category;
            } else {
                result = getCategory(category.getSubCategories(), categoryName);
            }
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public void cancel(ActionEvent actionEvent) throws IOException {
        loadManageCategories();
    }

    private void loadManageCategories() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ManageCategoriesPage.fxml"));
        Parent root = loader.load();

        ManageCategoriesPage manageCategoriesPage = loader.getController();
        manageCategoriesPage.setFmis(fmis);
        manageCategoriesPage.setCurrentUser(currentUser);

        Stage stage = (Stage) comboUser.getScene().getWindow();
        stage.setTitle("Manage Categories Page");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void populateComboBoxes() {
        comboCategory.getItems().clear();
        comboUser.getItems().clear();
        for (Category category : fmis.getCategories()) {
            comboCategory.getItems().add(category.getName());
        }
        for (User user : fmis.getAllUsers()) {
            if (user instanceof IndividualPersonUser) {
                comboUser.getItems().add("Individual user " + ((IndividualPersonUser) user).getName());
            } else {
                comboUser.getItems().add("Company user " + ((CompanyUser)user).getName());
            }
        }
    }
}
