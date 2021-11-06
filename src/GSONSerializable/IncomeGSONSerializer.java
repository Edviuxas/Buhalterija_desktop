package GSONSerializable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ds.Income;

import java.lang.reflect.Type;

public class IncomeGSONSerializer implements JsonSerializer<Income> {
    @Override
    public JsonElement serialize(Income income, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("belongsTo", income.getName());
        jsonObject.addProperty("name", income.getName());
        jsonObject.addProperty("description", income.getDescription());
        jsonObject.addProperty("amount", income.getAmount());
        return jsonObject;
    }
}
