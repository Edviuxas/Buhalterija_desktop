package webControllers;

import GSONSerializable.AllCategoriesGSONSerializer;
import GSONSerializable.CategoryGSONSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import controller.DatabaseController;
import ds.Category;
import ds.Expense;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Controller
public class WebCategoryController {

    @RequestMapping(value = "category/getCategories", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String getCategories(@RequestBody String request) {
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        int userID = Integer.parseInt(data.getProperty("userID"));
        String categoryName = null;
        if (!data.getProperty("categoryName").equals("")) {
            categoryName = data.getProperty("categoryName");
        }

        ArrayList<Category> categories = DatabaseController.getCategories(userID, categoryName);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Category.class, new CategoryGSONSerializer());
        parser.toJson(categories.get(0));

        Type categoryList = new TypeToken<ArrayList<Category>>() {
        }.getType();
        gsonBuilder.registerTypeAdapter(categoryList, new CategoryGSONSerializer());
        parser = gsonBuilder.create();

        return parser.toJson(categories);
    }

    @RequestMapping(value = "category/getAllCategories", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String getAllCategories(@RequestBody String request) {
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        int userID = Integer.parseInt(data.getProperty("userID"));

        ArrayList<Category> categories = DatabaseController.getAllCategories(userID);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Category.class, new CategoryGSONSerializer());
        parser.toJson(categories.get(0));

        Type categoryList = new TypeToken<ArrayList<Category>>() {
        }.getType();
        gsonBuilder.registerTypeAdapter(categoryList, new CategoryGSONSerializer());
        parser = gsonBuilder.create();

        return parser.toJson(categories);
    }

    @RequestMapping(value = "category/add", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String addCategory(@RequestBody String request) {
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        String name = data.getProperty("name");
        String description = data.getProperty("description");
        int creatorId = Integer.parseInt(data.getProperty("creatorId"));
        String parentName = null;
        if (!data.getProperty("parentName").equals("")) {
            parentName = data.getProperty("parentName");
        }

        try {
            DatabaseController.addCategory(name, description, creatorId, parentName);
        } catch (Exception e) {
            return "Error";
        }
        return "Successfully added category";
    }

    @RequestMapping(value = "category/update", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String updateCategory(@RequestBody String request) {
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        String oldName = data.getProperty("oldName");
        String newName = data.getProperty("newName");
        String newDescription = data.getProperty("newDescription");
        double sumOfIncomes = Double.parseDouble(data.getProperty("newIncomesSum"));
        double sumOfExpenses = Double.parseDouble(data.getProperty("newExpensesSum"));

        try {
            DatabaseController.updateCategory(oldName, newName, newDescription, sumOfExpenses, sumOfIncomes);
        } catch (Exception e) {
            return "Error";
        }
        return "Successfully updated category";
    }

    @RequestMapping(value = "category/delete", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String deleteCategory(@RequestBody String request) {
        try {
            Gson parser = new Gson();
            Properties data = parser.fromJson(request, Properties.class);
            DatabaseController.removeCategory(data.getProperty("categoryName"));
        } catch (Exception e) {
            return "Error";
        }
        return "Successfully deleted category";
    }
}
