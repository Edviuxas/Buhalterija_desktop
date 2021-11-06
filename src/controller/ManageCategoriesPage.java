package controller;

import ds.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.orm.jpa.vendor.Database;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ManageCategoriesPage implements Initializable {
    @FXML
    public ListView listCategories;
    @FXML
    public TextField textName;
    @FXML
    public TextArea textDescription;
    @FXML
    public Label lblAddCategory;
    @FXML
    public Button btnClearTextBoxes;
    @FXML
    public Label lblTotalIncome;
    @FXML
    public Label lblTotalExpense;
    @FXML
    public DatePicker dateFrom;
    @FXML
    public DatePicker dateUntil;
    private Category currentCategory = null;
    private Category categoryWeAreUpdating = null;
    private User currentUser;
    private FinanceManagementIS fmis;

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void setFmis(FinanceManagementIS fmis) {
        this.fmis = fmis;
        fmis.setCategories(DatabaseController.getAllCategories());
        showCategories(fmis.getCategories());

        dateUntil.valueProperty().addListener((observable, oldDate, newDate) -> {
            setTotalIncomesExpensesLabels();
        });
        dateFrom.valueProperty().addListener((observable, oldDate, newDate) -> {
            setTotalIncomesExpensesLabels();
        });

        setTotalIncomesExpensesLabels();
    }

    private void setTotalIncomesExpensesLabels() {
        if (dateFrom.getValue() != null && dateUntil.getValue() != null) {
            lblTotalIncome.setText(Double.toString(DatabaseController.getTotalIncomesByDate(dateFrom.getValue(), dateUntil.getValue())));
            lblTotalExpense.setText(Double.toString(DatabaseController.getTotalExpensesByDate(dateFrom.getValue(), dateUntil.getValue())));
        } else {
            lblTotalIncome.setText(Double.toString(DatabaseController.getTotalIncomesByDate(null, null)));
            lblTotalExpense.setText(Double.toString(DatabaseController.getTotalExpensesByDate(null, null)));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void getCategoryDetails(ActionEvent actionEvent) {
        if (listCategories.getSelectionModel().getSelectedItem() != null) {
            Category selectedCategory = getCategory(fmis.getCategories(), listCategories.getSelectionModel().getSelectedItem().toString());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Name: " + selectedCategory.getName() + "\nDescription: " + selectedCategory.getDescription() + "\nTotal income: " + selectedCategory.getSumOfIncomes() + "\nTotal expense: " + selectedCategory.getSumOfExpenses());
            alert.showAndWait();
        }
    }

    public void btnSavePress(ActionEvent actionEvent) throws IOException {
        if (textDescription.getText() != "" && textName.getText() != null) {
            if (lblAddCategory.getText().equals("ADD CATEGORY")) {
                if (currentCategory != null) {
                    if (currentCategory.getResponsiblePeople().contains(currentUser) || (currentCategory.getCreator() != null && currentCategory.getCreator().equals(currentUser)) || currentUser.getUserName().equals("admin")) {
                        if (getCategory(fmis.getCategories(), textName.getText()) == null) {
                            ArrayList<User> responsiblePeople = currentCategory.getResponsiblePeople();
//                            if (currentCategory.getCreator() != null) {
//                                responsiblePeople.add(currentCategory.getCreator());
//                            }
                            Category addingCategory = new Category(textName.getText(), textDescription.getText(), currentUser, responsiblePeople, new ArrayList<Category>(), currentCategory);
                            DatabaseController.addCategory(addingCategory);
                            currentCategory.getSubCategories().add(addingCategory);
                            clearTextBoxes();
                            showCategories(currentCategory.getSubCategories());
                            btnClearTextBoxes.setText("Clear");
                        } else {
                            AlertBoxes.showErrorBox("Category with this name already exists");
                        }
                    } else {
                        AlertBoxes.showErrorBox("Since you are not responsible for this category you can not create sub categories");
                    }
                } else {
                    if (getCategory(fmis.getCategories(), textName.getText()) == null) {
                        Category addingCategory = new Category(textName.getText(), textDescription.getText(), currentUser, new ArrayList<User>(), new ArrayList<Category>(), null);
                        DatabaseController.addCategory(addingCategory);
                        fmis.getCategories().add(addingCategory);
                        showCategories(fmis.getCategories());
                        clearTextBoxes();
                        btnClearTextBoxes.setText("Clear");
                    } else {
                        AlertBoxes.showErrorBox("Category with this name already exists");
                    }
                }
            } else {
                if (getCategory(fmis.getCategories(), textName.getText()) == null || getCategory(fmis.getCategories(), textName.getText()) == categoryWeAreUpdating) {
                    String oldCategoryName = categoryWeAreUpdating.getName();
                    categoryWeAreUpdating.setName(textName.getText());
                    categoryWeAreUpdating.setDescription(textDescription.getText());
                    DatabaseController.updateCategory(oldCategoryName, categoryWeAreUpdating);
                    for (Income income : categoryWeAreUpdating.getIncomes()) {
                        DatabaseController.updateIncomeBelongsTo(income.getName(), income.getBelongsTo());
                    }
                    for (Expense expense : categoryWeAreUpdating.getExpenses()) {
                        DatabaseController.updateIncomeBelongsTo(expense.getName(), expense.getBelongsTo());
                    }
                    clearTextBoxes();
                    lblAddCategory.setText("ADD CATEGORY");
                    categoryWeAreUpdating = null;
                    if (currentCategory == null || currentCategory.getParentCategory() == null) {
                        showCategories(fmis.getCategories());
                    } else {
                        showCategories(currentCategory.getParentCategory().getSubCategories());
                    }
                    btnClearTextBoxes.setText("Clear");
                } else {
                    AlertBoxes.showErrorBox("Category with this name already exists");
                }
            }
        }
    }

    private void clearTextBoxes() {
        textDescription.clear();
        textName.clear();
    }

    private void preparePageForCategoryUpdate() {
        lblAddCategory.setText("UPDATE CATEGORY");
        textName.setText(categoryWeAreUpdating.getName());
        textDescription.setText(categoryWeAreUpdating.getDescription());
        btnClearTextBoxes.setText("Cancel");
    }

    public void updateCategory(ActionEvent actionEvent) {
        if (listCategories.getSelectionModel().getSelectedItem() != null) {
            categoryWeAreUpdating = getCategory(fmis.getCategories(), listCategories.getSelectionModel().getSelectedItem().toString());
            preparePageForCategoryUpdate();
        }
    }

    public static Category getCategory(ArrayList<Category> layerOfCategories, String categoryName) {
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

    public void goDeeperCategories(ActionEvent actionEvent) {
        if (listCategories.getSelectionModel().getSelectedItem() != null) {
            String value = listCategories.getSelectionModel().getSelectedItem().toString();
            Category category = getCategory(fmis.getCategories(), value);
            currentCategory = category;
            if (category != null) {
                showCategories(category.getSubCategories());
            }
        }
    }

    public void goOutCategories(ActionEvent actionEvent) {
        if (currentCategory != null) {
            if (currentCategory.getParentCategory() == null) {
                showCategories(fmis.getCategories());
                currentCategory = null;
            } else {
                showCategories(currentCategory.getParentCategory().getSubCategories());
                currentCategory = currentCategory.getParentCategory();
            }
        }
    }

    private void showCategories(ArrayList<Category> layerOfCategories) {
        //ArrayList<Category> allCat = DatabaseController.getAllCategories();
        listCategories.getItems().clear();
        for (Category category : layerOfCategories) {
            listCategories.getItems().add(category.getName());
        }
    }

    public void close(ActionEvent actionEvent) throws IOException {
        loadMainWindow();
    }

    private void loadMainWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
        Parent root = loader.load();

        MainPage mainPage = loader.getController();
        mainPage.setFmis(fmis);
        mainPage.setCurrentUser(currentUser);

        Stage stage = (Stage) listCategories.getScene().getWindow();
        stage.setTitle("Main Page");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void deleteCategory(ActionEvent actionEvent) {
        if (listCategories.getSelectionModel().getSelectedItem() != null) {
            Category category = getCategory(fmis.getCategories(), listCategories.getSelectionModel().getSelectedItem().toString());
            boolean isResponsible = false;
            if (!category.getResponsiblePeople().isEmpty()) {
                for (User responsiblePerson : category.getResponsiblePeople()) {
                    if (responsiblePerson.getId() == currentUser.getId()) {
                        isResponsible = true;
                    }
                }
            }
            if (isResponsible || (category.getCreator() != null && category.getCreator().equals(currentUser)) || currentUser.getUserName().equals("admin")) {
                if (category != null) {
                    if (category.getParentCategory() == null) {
                        fmis.getCategories().remove(category);
                        DatabaseController.removeCategory(fmis.getCategories());
                        showCategories(fmis.getCategories());
                    } else {
                        category.getParentCategory().getSubCategories().remove(category);
                        DatabaseController.removeCategory(fmis.getCategories());
                        showCategories(category.getParentCategory().getSubCategories());
                    }
                    setTotalIncomesExpensesLabels();
                }
            } else {
                AlertBoxes.showErrorBox("Category not found");
            }
        } else {
            AlertBoxes.showErrorBox("Since you are not responsible for this category you can not delete it");
        }
    }

    public void addResponsiblePerson(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AddResponsiblePersonPage.fxml"));
        Parent root = loader.load();

        AddResponsiblePersonPage addResponsiblePersonPage = loader.getController();
        addResponsiblePersonPage.setFmis(fmis);
        addResponsiblePersonPage.setCurrentUser(currentUser);

        Stage stage = (Stage) listCategories.getScene().getWindow();
        stage.setTitle("Add responsible person");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void btnClearPress(ActionEvent actionEvent) {
        if (btnClearTextBoxes.getText().equals("Cancel")) {
            lblAddCategory.setText("ADD CATEGORY");
            btnClearTextBoxes.setText("Clear");
        }
        textName.clear();
        textDescription.clear();
        categoryWeAreUpdating = null;
    }

    public void manageIncomesExpenses(ActionEvent actionEvent) throws IOException {
        Category category = getCategory(fmis.getCategories(), listCategories.getSelectionModel().getSelectedItem().toString());
        if (listCategories.getSelectionModel().getSelectedItem() != null) {
            for (User responsiblePerson : category.getResponsiblePeople()) {
                if (responsiblePerson.getId() == currentUser.getId()) {
                    loadManageIncomesExpensesPage(category);
                    return;
                }
            }
            if (category.getCreator() != null && category.getCreator().equals(currentUser)) {
                loadManageIncomesExpensesPage(category);
            } else if (currentUser.getUserName().equals("admin")) {
                loadManageIncomesExpensesPage(category);
            } else {
                AlertBoxes.showErrorBox("Since you are not responsible for this category, you can not manage incomes/expenses");
            }

            //((category.getCreator() != null && category.getCreator().equals(currentUser)) || currentUser.getUserName().equals("admin"))
//            if (category.getResponsiblePeople().contains(currentUser)) {
//                loadManageIncomesExpensesPage(category);
//            }
        } else {
            AlertBoxes.showErrorBox("Since you are not responsible for this category, you can not manage incomes/expenses");
        }
    }

    private void loadManageIncomesExpensesPage(Category category) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ManageIncomesExpensesPage.fxml"));
        Parent root = loader.load();

        ManageIncomesExpensesPage manageIncomesExpensesPage = loader.getController();
        manageIncomesExpensesPage.setCategory(category);
        manageIncomesExpensesPage.setFmis(fmis);
        manageIncomesExpensesPage.setCurrentUser(currentUser);

        Stage stage = (Stage) listCategories.getScene().getWindow();
        stage.setTitle("Manage incomes and expenses");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
