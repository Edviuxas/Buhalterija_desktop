package ds;

import java.io.Serializable;
import java.util.ArrayList;

public class Category implements Serializable {
    private String name = "--";
    private String description;
    private User creator = null;
    private ArrayList<User> responsiblePeople = new ArrayList<>();
    private ArrayList<Category> subCategories = new ArrayList<>();
    private Category parentCategory = null;
    private String parentName = null;
    private ArrayList<Income> incomes = new ArrayList<>();
    private ArrayList<Expense> expenses = new ArrayList<>();
    private double sumOfExpenses = 0;
    private double sumOfIncomes = 0;
    private String path = "";

    public Category(String name, String description, double sumOfExpenses, double sumOfIncomes) {
        this.name = name;
        this.description = description;
        this.sumOfExpenses = sumOfExpenses;
        this.sumOfIncomes = sumOfIncomes;
    }

//    public Category(String name, String parentName, Double sumOfExpenses, Double sumOfIncomes) {
//        this.name = name;
//        this.parentName = parentName;
//        this.sumOfExpenses = sumOfExpenses;
//        this.sumOfIncomes = sumOfIncomes;
//    }

    public Category(String name, String description, String parentName, double sumOfExpenses, double sumOfIncomes) {
        this.name = name;
        this.description = description;
        this.parentName = parentName;
        this.sumOfExpenses = sumOfExpenses;
        this.sumOfIncomes = sumOfIncomes;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public double getSumOfExpenses() {
        return sumOfExpenses;
    }

    public void setSumOfExpenses(double sumOfExpenses) {
        this.sumOfExpenses = sumOfExpenses;
    }

    public double getSumOfIncomes() {
        return sumOfIncomes;
    }

    public void setSumOfIncomes(double sumOfIncomes) {
        this.sumOfIncomes = sumOfIncomes;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public ArrayList<Income> getIncomes() {
        return incomes;
    }

    public void setIncomes(ArrayList<Income> incomes) {
        this.incomes = incomes;
    }

    public ArrayList<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(ArrayList<Expense> expenses) {
        this.expenses = expenses;
    }

    public Category() {
    }

    public Category(String name, String description, User creator, ArrayList<User> responsiblePeople, ArrayList<Category> subCategories, Category parentCategory) {
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.responsiblePeople = responsiblePeople;
        this.subCategories = subCategories;
        this.parentCategory = parentCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<User> getResponsiblePeople() {
        return responsiblePeople;
    }

    public void setResponsiblePeople(ArrayList<User> responsiblePeople) {
        this.responsiblePeople = responsiblePeople;
    }

    public ArrayList<Category> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(ArrayList<Category> subCategories) {
        this.subCategories = subCategories;
    }

    public Category getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
    }

    @Override
    public String toString() {
        return "Category " + this.getName();
    }

    public Category(String name, String description, ArrayList<User> responsiblePeople, ArrayList<Category> subCategories, Category parentCategory) {
        this.name = name;
        this.description = description;
        this.responsiblePeople = responsiblePeople;
        this.subCategories = subCategories;
        this.parentCategory = parentCategory;
    }
}
