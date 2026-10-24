#pragma once
#ifndef auth_H
#define auth_H
#include <string>
#include <iostream>
#include <Windows.h>
#include <fstream>
#include <conio.h>
#include <sstream>
#include "User.h"
#include "Student.h"
#include "Teacher.h"

//класс предоставляет 
// статические методы для работы с файлами, 
// связанными с администраторами, курсами, сертификатами, учителями и студентами
class FileService {
public:
	static void writeAdminsToFile(const std::vector<Admin>& admins, const std::string& filename);
	static void readAdminsFromFile(const std::string& filename, std::vector<Admin>& admins);

	static void writeCoursesToFile(const std::string& filename, const std::vector<Course>& courses);
	static void readCoursesFromFile(const std::string& filename, std::vector<Course>& courses);

	static void writeCertificatesToFile(const std::string& filename, const std::vector<Certificate>& certificates);
	static void readCertificatesFromFile(const std::string& filename, std::vector<Certificate>& certificates);

   
	
	static void createAdminUser(const std::string fileName);

	static void writeTeachersToFile(const std::string& filename, const std::vector<Teacher>& teachers);
	static void readTeachersFromFile(const std::string& filename, std::vector<Teacher>& teachers);

	static void writeStudentsToFile(const std::string& filename, const std::vector<Student>& students);
	static void readStudentsFromFile(const std::string& filename, std::vector<Student>& students);
};

//класс содержит 
// статические методы для проверки данных пользователей 
// (например, логин), управления доступом и регистрации новых пользователей
class UserValidation {
public:
	static void login(std::vector<Student>& students, std::vector<Admin>& admins, std::vector<Course>& courses, std::vector<Teacher>& teachers, std::vector<Certificate>& certificates);
	static void checkUsernameAndPassword(std::vector<Student>& students, std::vector<Admin>& admins,
		std::vector<Teacher>& teachers, const std::string& userName,const std::string& password,
		std::vector<Course>& courses, std::vector<Certificate>& certificates);
	static void handleUserAccess(Student& currentStudent, std::vector<Course>& courses, std::vector<Certificate>& certificates, const std::vector<Student>& students);
	static void handleUserAccess(Admin& currentAdmin, std::vector<Student>& students, std::vector<Teacher>& teachers, std::vector<Course>& courses, std::vector<Certificate>& certificates);
	static void handleUserAccess(Teacher& currentTeacher, std::vector<Student>& students, std::vector<Course>& courses, std::vector<Certificate>& certificates, std::vector<Teacher>& teachers);
	static void registerUser(std::vector<Student>& students, std::vector<Teacher>& teachers);

	static bool isUsernameUnique(const std::vector<Student>& students, const std::vector<Teacher>& teachers, const std::string& userName);

	static std::string getPasswordFromUser();

	static void createTeacherAndAddToVector(std::vector<Teacher>& teachers, const std::string& userName,
		const std::string& password, const std::string& name, const std::string& surname, const std::string& middleName,
		const std::string& phoneNumber, const std::string& mail, Language language);

	static void createUserAndAddToVector(std::vector<Student>& students, const std::string& userName, const std::string& password,
		const std::string& phoneNumber, const std::string& mail);

	static std::string generateSalt();
	static std::string hashPassword(const std::string& password);

	static std::string getPassword();

};

//набор цветов для консольного вывода
enum ConsoleColor {
	Black = 0,
	Blue = 1,
	Green = 2,
	Cyan = 3,
	Red = 4,
	Magenta = 5,
	Brown = 6,
	LightGray = 7,
	DarkGray = 8,
	LightBlue = 9,
	LightGreen = 10,
	LightCyan = 11,
	LightRed = 12,
	LightMagenta = 13,
	Yellow = 14,
	White = 15
};


//класс предоставляет методы для валидации данных ввода,
// такие как проверка строк на наличие пробелов, 
// получение номера телефона и проверка корректности адреса электронной почты.
class ValidationManager {
public:
	
	static bool setStringWithOutSpace(std::string& str);
	static bool setStringWithSpace(std::string& str);
	static std::string getPhone();
	static bool isValidEmail(const std::string& email);
	static int numberControl();
	static void consoleIO(std::string err, ConsoleColor color);
	static std::string languageLV();
	static std::string enter_time();

};


#endif