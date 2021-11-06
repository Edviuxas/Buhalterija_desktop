package GSONSerializable;

import com.google.gson.*;
import ds.Category;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AllCategoriesGSONSerializer implements JsonSerializer<ArrayList<Category>> {
    @Override
    public JsonElement serialize(ArrayList<Category> allCategories, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonArray jsonArray = new JsonArray();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Category.class, new CategoryGSONSerializer());
        Gson parser = gsonBuilder.create();

        for (Category category : allCategories) {
            jsonArray.add(parser.toJson(category));
        }
        System.out.println(jsonArray);
        return jsonArray;
    }
}
