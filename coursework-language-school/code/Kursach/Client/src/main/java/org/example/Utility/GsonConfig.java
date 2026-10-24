package org.example.Utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class GsonConfig {

    public static Gson createGson() {
        DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", new Locale("ru")); // Устанавливаем формат с русскими месяцами
        return new GsonBuilder()
                .setDateFormat(((SimpleDateFormat) dateFormat).toPattern()) // Устанавливаем формат для Gson
                .create();
    }
}
