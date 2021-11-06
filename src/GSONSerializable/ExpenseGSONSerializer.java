package GSONSerializable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ds.Expense;

import java.lang.reflect.Type;

public class ExpenseGSONSerializer implements JsonSerializer<Expense> {
    @Override
    public JsonElement serialize(Expense expense, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("belongsTo", expense.getName());
        jsonObject.addProperty("name", expense.getName());
        jsonObject.addProperty("description", expense.getDescription());
        jsonObject.addProperty("amount", expense.getAmount());
        return jsonObject;
    }
}
