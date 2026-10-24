#include "Certificate.h"
#include <iostream>

// Конструктор
Certificate::Certificate(const int courseID,const std::string& teacherName, const std::string& courseName,
    Language language, const std::string& languageLevel)
    : courseID(courseID), teacherName(teacherName), courseName(courseName), language(language), languageLevel(languageLevel) {
}

int Certificate::getCourseID() const {
    return courseID;
}

// Геттеры
std::string Certificate::getTeacherName() const {
    return teacherName;
}

std::string Certificate::getCourseName() const {
    return courseName;
}

Language Certificate::getLanguage() const {
    return language;
}

std::string Certificate::getLanguageLevel() const {
    return languageLevel;
}

void Certificate::setCourceID(const int id) {
    courseID = id;
}

// Сеттеры
void Certificate::setTeacherName(const std::string& teacherName) {
    this->teacherName = teacherName;
}

void Certificate::setCourseName(const std::string& courseName) {
    this->courseName = courseName;
}

void Certificate::setLanguage(Language language) {
    this->language = language;
}

void Certificate::setLanguageLevel(const std::string& languageLevel) {
    this->languageLevel = languageLevel;
}

// Метод для преобразования Language в строку
std::string Certificate::languageToString(Language language) {
    switch (language) {
    case Language::English: return "Английский";
    case Language::Spanish: return "Испанский";
    case Language::French:  return "Французский";
    case Language::German:  return "Немецкий";
    case Language::Italian: return "Итальянский";
    case Language::Chinese: return "Китайский";
    case Language::Japanese: return "Японский";
    case Language::Russian: return "Русский";
    case Language::Arabic: return "Арабский";
    case Language::Portuguese: return "Португальский";
    default: return "Неизвестный язык";
    }
}

// Метод для генерации текста сертификата
std::string Certificate::generateCertificateText() const {
    return "Сертификат подтверждает успешное прохождение курса \"" + courseName +
        "\" на уровне " + languageLevel + " по языку " + languageToString(language) +
        ". Курс преподавал(а): " + teacherName + ".";
}

// Метод для отображения информации о сертификате
void Certificate::displayCertificate() const {
    std::cout << "=== Сертификат ===" << std::endl;
    std::cout << generateCertificateText() << std::endl;
}
