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

public class JsonLoader {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>)
                    (json, type, context) -> LocalDate.parse(json.getAsString()))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                    (json, type, context) -> LocalDateTime.parse(json.getAsString()))
            .create();

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