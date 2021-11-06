package GSONSerializable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ds.Category;

import java.lang.reflect.Type;

public class CategoryGSONSerializer implements JsonSerializer<Category> {

    @Override
    public JsonElement serialize(Category category, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", category.getName());
        jsonObject.addProperty("description", category.getDescription());
        jsonObject.addProperty("parentName", category.getParentName());
        jsonObject.addProperty("expenses", category.getSumOfExpenses());
        jsonObject.addProperty("incomes", category.getSumOfIncomes());
        jsonObject.addProperty("path", category.getPath());
        return jsonObject;
    }
}
