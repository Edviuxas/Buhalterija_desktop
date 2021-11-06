package webControllers;

import GSONSerializable.IncomeGSONSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import controller.DatabaseController;
import ds.Income;
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
public class WebIncomeController {

    @RequestMapping(value = "/incomes/update", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String updateIncome(@RequestBody String request) {
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        String oldName = data.getProperty("oldName");
        String oldDescription = data.getProperty("oldDescription");
        String newName = data.getProperty("newName");
        String newDescription = data.getProperty("newDescription");
        double newAmount = Double.parseDouble(data.getProperty("newAmount"));
        try {
            DatabaseController.updateIncome(oldName, oldDescription, new Income(newName, newDescription, newAmount));
        } catch (Exception e) {
            return "Error";
        }
        return "Successfully updated income";
    }

    @RequestMapping(value = "incomes/addIncome", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String addIncome(@RequestBody String request) throws SQLException, ClassNotFoundException {
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        String categoryName = data.getProperty("categoryName");
        String name = data.getProperty("name");
        String description = data.getProperty("description");
        double amount = Double.parseDouble(data.getProperty("amount"));
        if (DatabaseController.categoryExists(categoryName)) {
            try {
                DatabaseController.addIncome(new Income(name, description, amount), categoryName);
            } catch (Exception e) {
                return "Error";
            }
        }
        return "Successfully added";
    }

    @RequestMapping(value = "incomes/deleteIncome", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String deleteIncome(@RequestBody String request) {
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        try {
            DatabaseController.deleteIncome(new Income(data.getProperty("belongsTo"), data.getProperty("name"), data.getProperty("description"), Double.parseDouble(data.getProperty("amount"))));
        } catch (Exception e) {
            return "Error";
        }
        return "Successfully deleted income";
    }

    @RequestMapping(value = "/incomes/getAll", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String getAllIncomes() throws SQLException, ClassNotFoundException {

        ArrayList<Income> allIncomes = DatabaseController.getAllIncomes();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Income.class, new IncomeGSONSerializer());
        Gson parser = gsonBuilder.create();
        parser.toJson(allIncomes.get(0));

        Type incomeList = new TypeToken<List<Income>>() {
        }.getType();
        gsonBuilder.registerTypeAdapter(incomeList, new IncomeGSONSerializer());
        parser = gsonBuilder.create();

        return parser.toJson(allIncomes);
    }

    @RequestMapping(value = "/incomes/getIncomes", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String getIncomes(@RequestBody String request) {
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        ArrayList<Income> allIncomes = DatabaseController.getIncomes(data.getProperty("belongsTo"));
        ArrayList<Income> finalAllIncomes = new ArrayList<>();
        if (data.getProperty("dateFrom") != null && data.getProperty("dateUntil") != null) {
            LocalDate dateFrom = LocalDate.parse(data.getProperty("dateFrom").replace('/', '-'));
            LocalDate dateUntil = LocalDate.parse(data.getProperty("dateUntil").replace('/', '-'));
            allIncomes = DatabaseController.getIncomesByDate(dateFrom, dateUntil);
            for (Income income : allIncomes) {
                if (income.getBelongsTo().equals(data.getProperty("belongsTo"))) {
                    finalAllIncomes.add(income);
                }
            }
        } else {
            finalAllIncomes = DatabaseController.getIncomes(data.getProperty("belongsTo"));
        }
        if (!finalAllIncomes.isEmpty()) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Income.class, new IncomeGSONSerializer());
            parser = gsonBuilder.create();
            parser.toJson(finalAllIncomes.get(0));

            Type incomeList = new TypeToken<List<Income>>() {
            }.getType();
            gsonBuilder.registerTypeAdapter(incomeList, new IncomeGSONSerializer());
            parser = gsonBuilder.create();

            return parser.toJson(finalAllIncomes);
        } else {
            return "No data";
        }

    }

    @RequestMapping(value = "/incomes/getTotalIncomes", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String getTotalIncome(@RequestBody String request) {
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        double sum = DatabaseController.getTotalIncomes(Integer.parseInt(data.getProperty("userID")));
        try {
            return Double.toString(sum);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }
}
