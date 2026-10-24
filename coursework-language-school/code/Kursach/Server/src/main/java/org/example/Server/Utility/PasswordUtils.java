package org.example.Server.Utility;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    // Метод для хеширования пароля
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt()); // Генерация хеша с солью
    }

    // Метод для проверки соответствия пароля и хеша
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword); // Сравнение пароля с хешем
    }
}

