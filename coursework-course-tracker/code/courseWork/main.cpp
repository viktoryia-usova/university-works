#include <iostream>
#include <vector>
#include <locale>

using namespace std;

#include "UserValidation.h"

int main() {
	setlocale(LC_ALL, "Ru");
	system("chcp 1251 > nul");
	std::vector<Course> courses;
	std::vector<Student> students;
	std::vector<Teacher> teachers;
	std::vector<Admin> admins;
	std::vector<Certificate> certificates;

	//чтение данных из текстовых файлов и 
	// данные загружаются в соответствующие векторы.
	FileService::readStudentsFromFile("students.txt", students);
	FileService::readTeachersFromFile("teachers.txt", teachers);
	FileService::readCoursesFromFile("courses.txt", courses);
	FileService::readCertificatesFromFile("certificate.txt", certificates);

	FileService::readAdminsFromFile("admins.txt", admins);

	//создаем админа, если такого нет
	if (admins.empty()) {
		FileService::createAdminUser("admins.txt");
	     FileService::readAdminsFromFile("admins.txt", admins);
	}

	// Основной цикл программы
	while (true) {
		int choice;
		std::cout << "1. Войти" << std::endl << std::endl;
		std::cout << "2. Зарегистрироваться" << std::endl << std::endl;
		std::cout << "3. Выйти из системы" << std::endl << std::endl;
		std::cout << "Выберите действие: ";
		choice = ValidationManager::numberControl();
		
		 //Выполнение выбранного действия
		if (choice == 1) {
			system("cls");
			UserValidation::login(students,admins, courses, teachers, certificates); // Вход пользователя
		}
		else if (choice == 2) {
			system("cls");
			UserValidation::registerUser(students, teachers); // Регистрация нового пользователя
		}
		else if (choice == 3) {
			system("cls");
			break; // Выход из программы
		}
		else {
			system("cls");
			SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), LightBlue);
			std::cout << "Некорректный ввод. Попробуйте еще раз." << std::endl<<std::endl;
			SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), LightGray);
		}
	}
	// Перед завершением программы происходит 
	// запись всех измененных данных обратно в соответствующие файлы
	FileService::writeAdminsToFile(admins, "admins.txt");
	FileService::writeTeachersToFile("teachers.txt", teachers);
	FileService::writeStudentsToFile("students.txt", students);
	FileService::writeCoursesToFile("courses.txt", courses);
	FileService::writeCertificatesToFile("certificate.txt", certificates);

	return 0;
}