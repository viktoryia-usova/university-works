#pragma once
#ifndef CERTIFICATE_H
#define CERTIFICATE_H

#include <string>
#include "Course.h"

class Certificate {
private:
    int courseID;
    std::string teacherName;       // ФИО преподавателя
    std::string courseName;        // Название курса
    Language language;             // Язык курса (enum class Language)
    std::string languageLevel;     // Уровень языка (A1 - C2)

public:
    // Конструкторы
    Certificate() = default;
    Certificate(const int courseID,const std::string& teacherName, const std::string& courseName,
        Language language, const std::string& languageLevel);

    // Геттеры
    int getCourseID() const;
    std::string getTeacherName() const;
    std::string getCourseName() const;
    Language getLanguage() const;
    std::string getLanguageLevel() const;

    // Сеттеры
    void setCourceID(const int id);
    void setTeacherName(const std::string& teacherName);
    void setCourseName(const std::string& courseName);
    void setLanguage(Language language);
    void setLanguageLevel(const std::string& languageLevel);

    // Метод для преобразования Language в строку
    static std::string languageToString(Language language);

    // Метод для генерации текста сертификата
    std::string generateCertificateText() const;

    // Метод для отображения информации о сертификате
    void displayCertificate() const;
};

#endif // CERTIFICATE_H

