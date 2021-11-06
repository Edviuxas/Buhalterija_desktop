package webControllers;

import GSONSerializable.ExpenseGSONSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import controller.DatabaseController;
import ds.Expense;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Controller
public class WebExpenseController {
    @RequestMapping(value = "/expenses/update", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String updateExpense(@RequestBody String request) {
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        String oldName = data.getProperty("oldName");
        String oldDescription = data.getProperty("oldDescription");
        String newName = data.getProperty("newName");
        String newDescription = data.getProperty("newDescription");
        double newAmount = Double.parseDouble(data.getProperty("newAmount"));
        try {
            DatabaseController.updateExpense(oldName, oldDescription, new Expense(newName, newDescription, newAmount));
        } catch (Exception e) {
            return "Error";
        }
        return "Successfully updated expense";
    }

    @RequestMapping(value = "expenses/addExpense", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String addExpense(@RequestBody String request) throws SQLException, ClassNotFoundException {
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        String categoryName = data.getProperty("categoryName");
        String name = data.getProperty("name");
        String description = data.getProperty("description");
        double amount = Double.parseDouble(data.getProperty("amount"));
        if (DatabaseController.categoryExists(categoryName)) {
            try {
                DatabaseController.addExpense(new Expense(name, description, amount), categoryName);
            } catch (Exception e) {
                return "Error";
            }
        }
        return "Successfully added";
    }

    @RequestMapping(value = "expenses/deleteExpense", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String deleteExpense(@RequestBody String request) {
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        try {
            DatabaseController.deleteExpense(new Expense(data.getProperty("belongsTo"), data.getProperty("name"), data.getProperty("description"), Double.parseDouble(data.getProperty("amount"))));
        } catch (Exception e) {
            return "Error";
        }
        return "Successfully deleted expense";
    }

    @RequestMapping(value = "/expenses/getAll", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String getAllExpenses() throws SQLException, ClassNotFoundException {

        ArrayList<Expense> allExpenses = DatabaseController.getAllExpenses();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Expense.class, new ExpenseGSONSerializer());
        Gson parser = gsonBuilder.create();
        parser.toJson(allExpenses.get(0));

        Type expenseList = new TypeToken<List<Expense>>() {
        }.getType();
        gsonBuilder.registerTypeAdapter(expenseList, new ExpenseGSONSerializer());
        parser = gsonBuilder.create();

        return parser.toJson(allExpenses);
    }

    @RequestMapping(value = "/expenses/getExpenses", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String getExpenses(@RequestBody String request) {
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        ArrayList<Expense> allExpenses;
        ArrayList<Expense> finalAllExpenses = new ArrayList<>();
        if (data.getProperty("dateFrom") != null && data.getProperty("dateUntil") != null) {
            LocalDate dateFrom = LocalDate.parse(data.getProperty("dateFrom").replace('/', '-'));
            LocalDate dateUntil = LocalDate.parse(data.getProperty("dateUntil").replace('/', '-'));
            allExpenses = DatabaseController.getExpensesByDate(dateFrom, dateUntil);
            for (Expense expense : allExpenses) {
                if (expense.getBelongsTo().equals(data.getProperty("belongsTo"))) {
                    finalAllExpenses.add(expense);
                }
            }
        } else {
            finalAllExpenses = DatabaseController.getExpenses(data.getProperty("belongsTo"));
        }

        if (!finalAllExpenses.isEmpty()) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Expense.class, new ExpenseGSONSerializer());
            parser = gsonBuilder.create();
            parser.toJson(finalAllExpenses.get(0));

            Type incomeList = new TypeToken<List<Expense>>() {
            }.getType();
            gsonBuilder.registerTypeAdapter(incomeList, new ExpenseGSONSerializer());
            parser = gsonBuilder.create();

            return parser.toJson(finalAllExpenses);
        } else {
            return "No data";
        }


    }

    @RequestMapping(value = "/expenses/getTotalExpenses", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String getTotalIncome(@RequestBody String request) {
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        double sum = DatabaseController.getTotalExpenses(Integer.parseInt(data.getProperty("userID")));
        try {
            return Double.toString(sum);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }
}
