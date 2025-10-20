package com.luinel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Utility class for loading JSON resources into Java objects.
 * <p>
 * This class uses Gson for JSON deserialization and supports
 * automatic conversion for {@link LocalDate} and {@link LocalDateTime}.
 * </p>
 */
public class JsonLoader {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class,
                    (JsonDeserializer<LocalDate>) (json, type, context) -> LocalDate.parse(json.getAsString()))
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonDeserializer<LocalDateTime>) (json, type, context) -> LocalDateTime.parse(json.getAsString()))
            .create();

    /**
     * Loads a JSON file from the classpath and deserializes it into a list of
     * objects of the specified type.
     *
     * @param resourcePath the path to the JSON resource on the classpath
     * @param clazz        the class of the objects to deserialize
     * @param <T>          the type of objects in the resulting list
     * @return a list of deserialized objects
     * @throws IllegalArgumentException if the resource cannot be found
     * @throws RuntimeException         if an error occurs during JSON parsing
     */
    public static <T> List<T> load(String resourcePath, Class<T> clazz) {
        InputStream inputStream = Optional
                .ofNullable(Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath))
                .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + resourcePath));

        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            Type listType = TypeToken.getParameterized(List.class, clazz).getType();
            return gson.fromJson(reader, listType);
        } catch (Exception e) {
            throw new RuntimeException("Error while loading json file: " + resourcePath, e);
        }
    }
}
