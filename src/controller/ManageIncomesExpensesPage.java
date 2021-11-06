package controller;

import ds.*;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;
import javafx.stage.Stage;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

public class ManageIncomesExpensesPage {
    Category category;
    FinanceManagementIS fmis;
    User currentUser;
    Income incomeWeAreUpdating = null;
    Expense expenseWeAreUpdating = null;
    @FXML
    public Label lblTotalCategoryExpense;
    @FXML
    public Label lblTotalCategoryIncome;
    @FXML
    public DatePicker dateCreated;
    @FXML
    public DatePicker dateFrom;
    @FXML
    public DatePicker dateUntil;
    @FXML
    public Label lblTotalIncome;
    @FXML
    public Label lblTotalExpense;
    @FXML
    public Label lblAdd;
    @FXML
    public TextField textAmount;
    @FXML
    public TextArea textDescription;
    @FXML
    public ComboBox comboType;
    @FXML
    public TextField textName;
    @FXML
    public ListView list;
    @FXML
    public Button btnDelete;
    @FXML
    public Button btnUpdate;
    @FXML
    public Button btnSave;
    @FXML
    public Button btnCancel;

    public void setCategory(Category category) {
        this.category = category;
        lblTotalIncome.setText(String.valueOf(category.getSumOfIncomes()));
        lblTotalExpense.setText(String.valueOf(category.getSumOfExpenses()));
    }

    public void setFmis(FinanceManagementIS fmis) {
        this.fmis = fmis;
        populateListView(category.getIncomes(), category.getExpenses());
        populateComboBox();
        dateUntil.setValue(LocalDate.now());
        dateFrom.setValue(LocalDate.now());
        dateUntil.valueProperty().addListener((observable, oldDate, newDate) -> {
            filterByDate();
        });
        dateFrom.valueProperty().addListener((observable, oldDate, newDate) -> {
            filterByDate();
        });
        setIncomeSumLabelText(category.getIncomes());
        setExpenseSumLabelText(category.getExpenses());
    }

    private void filterByDate() {

//        ArrayList<Income> incomes = new ArrayList<>();
//        ArrayList<Expense> expenses = new ArrayList<>();

//        for (Income income : category.getIncomes()) {
//            if (income.getDateCreated().isBefore(dateUntil.getValue()) && income.getDateCreated().isAfter(dateFrom.getValue())) {
//                incomes.add(income);
//            }
//        }
//
//        for (Expense expense : category.getExpenses()) {
//            if (expense.getDateCreated().isBefore(dateUntil.getValue()) && expense.getDateCreated().isAfter(dateFrom.getValue())) {
//                expenses.add(expense);
//            }
//        }

        ArrayList<Income> incomes = DatabaseController.getIncomesByDate(dateFrom.getValue(), dateUntil.getValue());
        ArrayList<Expense> expenses = DatabaseController.getExpensesByDate(dateFrom.getValue(), dateUntil.getValue());
        populateListView(incomes, expenses);
        setIncomeSumLabelText(incomes);
        setExpenseSumLabelText(expenses);
    }

    private void setExpenseSumLabelText(ArrayList<Expense> expenses) {
        double sum = 0.0;
        for (Expense expense : expenses) {
            sum += expense.getAmount();
        }
        lblTotalCategoryExpense.setText(String.valueOf(sum));
    }

    private void setIncomeSumLabelText(ArrayList<Income> incomes) {
        double sum = 0.0;
        for (Income income : incomes) {
            sum += income.getAmount();
        }
        lblTotalCategoryIncome.setText(String.valueOf(sum));
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    private void populateComboBox() {
        comboType.getItems().add("Expense");
        comboType.getItems().add("Income");
        comboType.getSelectionModel().selectFirst();
    }

    private void populateListView(ArrayList<Income> incomes, ArrayList<Expense> expenses) {
        list.getItems().clear();
        for (Expense expense : expenses) {
            list.getItems().add("Expense " + expense.getName() + " amount: " + expense.getAmount());
        }
        for (Income income : incomes) {
            list.getItems().add("Income " + income.getName() + " amount: " + income.getAmount());
        }
    }

    public void delete(ActionEvent actionEvent) {
        if (list.getSelectionModel().getSelectedItem() != null) {
            String listText = list.getSelectionModel().getSelectedItem().toString();
            String type = listText.split(" ")[0];
            String name = listText.split(" ")[1];
            if (type.equals("Expense")) {
                Iterator<Expense> iter = category.getExpenses().iterator();
                while (iter.hasNext()) {
                    Expense expense = iter.next();
                    if (expense.getName().equals(name)) {
                        removeExpense(expense, iter);
                    }
                }
            } else {
                Iterator<Income> iter = category.getIncomes().iterator();
                while (iter.hasNext()) {
                    Income income = iter.next();
                    if (income.getName().equals(name)) {
                        removeIncome(income, iter);
                    }
                }
            }
        }
        populateListView(category.getIncomes(), category.getExpenses());
        updateTotalLabels(category.getSumOfIncomes(), category.getSumOfExpenses());
    }

    private void updateTotalLabels(double incomeSum, double expenseSum) {
        lblTotalExpense.setText(String.valueOf(expenseSum));
        lblTotalIncome.setText(String.valueOf(incomeSum));
    }

    public void update(ActionEvent actionEvent) {
        if (list.getSelectionModel().getSelectedItem() != null) {
            String listText = list.getSelectionModel().getSelectedItem().toString();
            String type = listText.split(" ")[0];
            String name = listText.split(" ")[1];
            lblAdd.setText("UPDATE INCOMES/EXPENSES");
            comboType.setDisable(true);
            if (type.equals("Expense")) {
                for (Expense expense : category.getExpenses()) {
                    if (expense.getName().equals(name)) {
                        expenseWeAreUpdating = expense;
                        textName.setText(expense.getName());
                        textAmount.setText(String.valueOf(expense.getAmount()));
                        textDescription.setText(expense.getDescription());
                    }
                }
            } else {
                for (Income income : category.getIncomes()) {
                    if (income.getName().equals(name)) {
                        incomeWeAreUpdating = income;
                        textName.setText(income.getName());
                        textAmount.setText(String.valueOf(income.getAmount()));
                        textDescription.setText(income.getDescription());
                    }
                }
            }
        }
    }

    public void save(ActionEvent actionEvent) {
        if (textName.getText() != "" && textAmount.getText() != "" && textDescription.getText() != "") {
            if (lblAdd.getText().equals("UPDATE INCOMES/EXPENSES")) {
                if (incomeWeAreUpdating != null) {
                    double moneyDifference = Double.parseDouble(textAmount.getText()) - incomeWeAreUpdating.getAmount();
                    addMoneyToCategoriesAbove("Incomes", moneyDifference);
                    updateIncome(incomeWeAreUpdating, textName.getText(), textDescription.getText(), Double.parseDouble(textAmount.getText()), moneyDifference);
                } else {
                    double moneyDifference = Double.parseDouble(textAmount.getText()) - expenseWeAreUpdating.getAmount();
                    addMoneyToCategoriesAbove("Expenses", moneyDifference);
                    updateExpense(expenseWeAreUpdating, textName.getText(), textDescription.getText(), Double.parseDouble(textAmount.getText()), moneyDifference);
                }
                incomeWeAreUpdating = null;
                expenseWeAreUpdating = null;
                comboType.setDisable(false);
                lblAdd.setText("ADD INCOMES/EXPENSES");
            } else {
                if (comboType.getSelectionModel().getSelectedItem().toString() == "Expense") {
                    addExpense(textName.getText(), Double.parseDouble(textAmount.getText()), textDescription.getText(), dateCreated.getValue());
                } else {
                    addIncome(textName.getText(), Double.parseDouble(textAmount.getText()), textDescription.getText(), dateCreated.getValue());
                }
            }
            textDescription.setText("");
            textName.setText("");
            textAmount.setText("");
            updateTotalLabels(category.getSumOfIncomes(), category.getSumOfExpenses());
            populateListView(category.getIncomes(), category.getExpenses());
        }
    }

    private void updateExpense(Expense expenseWeAreUpdating, String nameText, String description, double amount, double moneyDifference) {
        String oldName = expenseWeAreUpdating.getName();
        String oldDescription = expenseWeAreUpdating.getDescription();
        expenseWeAreUpdating.setName(nameText);
        expenseWeAreUpdating.setDescription(description);
        expenseWeAreUpdating.setAmount(amount);
        double currentSum = category.getSumOfExpenses();
        category.setSumOfExpenses(currentSum + moneyDifference);
        DatabaseController.updateExpense(oldName, oldDescription, expenseWeAreUpdating);
        DatabaseController.updateCategory(category);
    }

    private void updateIncome(Income incomeWeAreUpdating, String name, String description, double amount, double moneyDifference) {
        String oldName = incomeWeAreUpdating.getName();
        String oldDescription = incomeWeAreUpdating.getDescription();
        incomeWeAreUpdating.setName(name);
        incomeWeAreUpdating.setDescription(description);
        incomeWeAreUpdating.setAmount(amount);
        double currentSum = category.getSumOfIncomes();
        category.setSumOfIncomes(currentSum + moneyDifference);
        DatabaseController.updateIncome(oldName, oldDescription, incomeWeAreUpdating);
        DatabaseController.updateCategory(category);
    }

    private void addIncome(String name, double amount, String description, LocalDate dateCreated) {
        Income income = new Income(category.getName(), name, description, amount, dateCreated);
        DatabaseController.addIncome(income, category.getName());
        category.getIncomes().add(income);
        double currentSum = category.getSumOfIncomes();
        category.setSumOfIncomes(currentSum + amount);
        DatabaseController.updateCategory(category);
        addMoneyToCategoriesAbove("Incomes", amount);

    }

    private void addExpense(String name, double amount, String description, LocalDate dateCreated) {
        Expense expense = new Expense(category.getName(), name, description, amount, dateCreated);
        DatabaseController.addExpense(expense, category.getName());
        category.getExpenses().add(expense);
        double currentSum = category.getSumOfExpenses();
        category.setSumOfExpenses(currentSum + amount);
        DatabaseController.updateCategory(category);
        addMoneyToCategoriesAbove("Expenses", amount);
    }

    private void removeIncome(Income income, Iterator<Income> iter) {
        iter.remove();
        DatabaseController.deleteIncome(income);
        double currentSum = category.getSumOfIncomes();
        category.setSumOfIncomes(currentSum - income.getAmount());
        DatabaseController.updateCategory(category);
        addMoneyToCategoriesAbove("Incomes", income.getAmount() * -1);
    }

    private void removeExpense(Expense expense, Iterator<Expense> iter) {
        iter.remove();
        DatabaseController.deleteExpense(expense);
        double currentSum = category.getSumOfExpenses();
        category.setSumOfExpenses(currentSum - expense.getAmount());
        DatabaseController.updateCategory(category);
        addMoneyToCategoriesAbove("Expenses", expense.getAmount() * -1);
    }

    private void addMoneyToCategoriesAbove(String typeOfMoney, double amount) {
        Category cat = category;
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

    public void cancel(ActionEvent actionEvent) throws IOException {
        loadManageCategories();
    }

    private void loadManageCategories() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ManageCategoriesPage.fxml"));
        Parent root = loader.load();

        ManageCategoriesPage manageCategoriesPage = loader.getController();
        manageCategoriesPage.setFmis(fmis);
        manageCategoriesPage.setCurrentUser(currentUser);

        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.setTitle("Manage Categories Page");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
