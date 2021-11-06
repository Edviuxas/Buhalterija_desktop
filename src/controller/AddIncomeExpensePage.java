package controller;

import ds.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.xml.crypto.Data;
import java.io.IOException;

public class AddIncomeExpensePage {
    @FXML
    public TextArea textDescription;
    @FXML
    public TextField textAmmount;
    @FXML
    public Button btnAdd;
    private FinanceManagementIS fmis;
    private Category selectedCategory;
    private User currentUser;

    public void setSelectedCategory(Category selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public void setFmis(FinanceManagementIS fmis) {
        this.fmis = fmis;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    private void addMoneyToCategoriesAbove(String typeOfMoney, double amount) {
        Category cat = selectedCategory;
        while (cat.getParentCategory() != null) {
            cat = cat.getParentCategory();
            if (typeOfMoney == "Expenses") {
                double currentSum = cat.getSumOfExpenses();
                cat.setSumOfExpenses(currentSum + amount);
            } else {
                double currentSum = cat.getSumOfIncomes();
                cat.setSumOfIncomes(currentSum + amount);
            }
            DatabaseController.updateCategory(cat);
        }
    }

    public void addIncomeExpense(ActionEvent actionEvent) throws IOException {
        if (textAmmount.getText() != "" && textDescription.getText() != "") {
            String description = textDescription.getText();
            double amount = Double.parseDouble(textAmmount.getText());
            if (btnAdd.getText() == "Add income") {
                addIncome(amount, description);
            } else {
                addExpense(amount, description);
            }
            loadManageCategories();
        }
    }

    private void addIncome(double amount, String description) {
        Income income = new Income(description, amount);
        DatabaseController.addIncome(income, selectedCategory.getName());
        selectedCategory.getIncomes().add(income);
        double currentSum = selectedCategory.getSumOfIncomes();
        selectedCategory.setSumOfIncomes(currentSum + amount);
        DatabaseController.updateCategory(selectedCategory);
        addMoneyToCategoriesAbove("Incomes", amount);
    }

    private void addExpense(double amount, String description) {
        Expense expense = new Expense(description, amount);
        DatabaseController.addExpense(expense, selectedCategory.getName());
        selectedCategory.getExpenses().add(expense);
        double currentSum = selectedCategory.getSumOfExpenses();
        selectedCategory.setSumOfExpenses(currentSum + amount);
        DatabaseController.updateCategory(selectedCategory);
        addMoneyToCategoriesAbove("Expenses", amount);
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

        Stage stage = (Stage) btnAdd.getScene().getWindow();
        stage.setTitle("Manage Categories Page");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
