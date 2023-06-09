package fr.boul2gom.cerberus.common.serializer;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

public class DataSerializer<T> implements JsonSerializer<T>, JsonDeserializer<T> {

    private static final String TYPE_KEY = "type";
    private static final String DATA_KEY = "data";
    private static final Gson GSON = new Gson();

    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject json = new JsonObject();

        json.addProperty(TYPE_KEY, src.getClass().getName());
        json.addProperty(DATA_KEY, GSON.toJson(src));

        return json;
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = json.getAsJsonObject();
        final String type = object.get(TYPE_KEY).getAsString();
        final String data = object.get(DATA_KEY).getAsString();

        try {
            final Class<?> clazz = Class.forName(type);

            return GSON.fromJson(data, (Type) clazz);
        } catch (ClassNotFoundException ignored) {}

        return null;
    }
}
