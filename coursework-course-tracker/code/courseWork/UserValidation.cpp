#ifndef auth_C
#define auth_C
#include "UserValidation.h"
#include <regex>

// Метод для генерации случайной соли
std::string UserValidation::generateSalt() {
	const int SALT_LENGTH = 16; // Задаем длину соли
	std::string salt_chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	std::string salt;
	for (int i = 0; i < SALT_LENGTH; ++i) {
		salt += salt_chars[std::rand() % salt_chars.size()];
	}
	return salt;
}

// Функция для входа в систему
void UserValidation::login(std::vector<Student>& students, std::vector<Admin>& admins, std::vector<Course>& courses, std::vector<Teacher>& teachers, std::vector<Certificate>& certificates) {
	std::string userName, password;

	std::cout << std::endl << "Введите имя пользователя: ";
	if (ValidationManager::setStringWithOutSpace(userName)) {
		system("cls");
		return;
	}

	std::cout << std::endl << "Введите пароль: ";
	password = getPassword();

	// Проверка имени пользователя и пароля
	checkUsernameAndPassword(students, admins, teachers, userName, password, courses, certificates);
}


// Функция для проверки имени пользователя и пароля
void UserValidation::checkUsernameAndPassword(std::vector<Student>& students, std::vector<Admin>& admins,
	std::vector<Teacher>& teachers, const std::string& userName,
	const std::string& password, std::vector<Course>& courses, std::vector<Certificate>& certificates) {
	// Проверка среди администраторов
	for (Admin& admin : admins) {
		if (admin.getUserName() == userName && admin.getHashedPassword() == hashPassword(password + admin.getSalt())) {
			if (admin.getUserAccess() == Accepted) {
				handleUserAccess(admin, students,teachers, courses,certificates);
				return;
			} else if (admin.getUserAccess() == Pending) {
				system("cls");
				ValidationManager::consoleIO("Ваш аккаунт ожидает подтверждения администратора", LightBlue);
				return;
			} else {
				system("cls");
				ValidationManager::consoleIO("Ваш аккаунт заблокирован", LightRed);
				return;
			}
		}
	}

	// Проверка среди студентов
	for (Student& student : students) {
		if (student.getUserName() == userName && student.getHashedPassword() == hashPassword(password + student.getSalt())) {
			if (student.getUserAccess() == Accepted) {
				handleUserAccess(student, courses, certificates, students);
				return;
			} else if (student.getUserAccess() == Pending) {
				system("cls");
				ValidationManager::consoleIO("Ваш аккаунт ожидает подтверждения администратора", LightBlue);
				return;
			} else {
				system("cls");
				ValidationManager::consoleIO("Ваш аккаунт заблокирован", LightRed);
				return;
			}
		}
	}

	// Проверка среди преподавателей
	for (Teacher& teacher : teachers) {
		if (teacher.getUserName() == userName && teacher.getHashedPassword() == hashPassword(password + teacher.getSalt())) {
			if (teacher.getUserAccess() == Accepted) {
				handleUserAccess(teacher, students, courses, certificates, teachers);
				return;
			} else if (teacher.getUserAccess() == Pending) {
				system("cls");
				ValidationManager::consoleIO("Ваш аккаунт ожидает подтверждения администратора", LightBlue);
				return;
			} else {
				system("cls");
				ValidationManager::consoleIO("Ваш аккаунт заблокирован", LightRed);
				return;
			}
		}
	}

	// Если пользователь не найден
	system("cls");
	ValidationManager::consoleIO("Неверное имя пользователя или пароль.", LightRed);
}

// Функция для обработки доступа пользователя
void UserValidation::handleUserAccess(Student& currentStudent, std::vector<Course>& courses, std::vector<Certificate>& certificates, const std::vector<Student>& students) {
	system("cls");
	ValidationManager::consoleIO("Вход выполнен успешно!", LightGreen);

	currentStudent.openStudentMenu(courses, certificates, students);
}

void UserValidation::handleUserAccess(Admin& currentAdmin, std::vector<Student>& students,
	std::vector<Teacher>& teachers, std::vector<Course>& courses, std::vector<Certificate>& certificates) {
	system("cls");
	ValidationManager::consoleIO("Вход выполнен успешно!", LightGreen);

	currentAdmin.openAdminMenu(students, teachers, courses, certificates);
}

// Функция для обработки доступа преподавателя
void UserValidation::handleUserAccess(Teacher& currentTeacher, std::vector<Student>& students, std::vector<Course>& courses, std::vector<Certificate>& certificates, std::vector<Teacher>& teachers) {
	system("cls");
	ValidationManager::consoleIO("Вход выполнен успешно!", LightGreen);

	currentTeacher.openTeacherMenu(students,courses, certificates, teachers);
}

void UserValidation::registerUser(std::vector<Student>& students, std::vector<Teacher>& teachers) {
	std::string userName, password;

	bool isUnique = false;
	bool isTeacher = false;

	// Определение типа пользователя (учитель или студент)
	std::cout << "Вы преподаватель? (Y/N): ";
	char ch;
	while (ch = _getch()) {
		if (ch == 'y' || ch == 'Y') {
			std::cout << ch << std::endl;
			isTeacher = true;
			break;
		}
		if (ch == 'n' || ch == 'N') { 
			std::cout << ch << std::endl;
			break; 
		}
	}

	// Ввод имени пользователя и проверка на уникальность
	do {
		std::cout << "Введите имя пользователя: ";
		if (ValidationManager::setStringWithOutSpace(userName)) {
			system("cls");
			return;
		}

		// Проверка уникальности имени пользователя
		isUnique = isUsernameUnique(students, teachers, userName);


		if (!isUnique) {
			ValidationManager::consoleIO("Пользователь с таким именем уже существует.", LightMagenta);
			userName.clear();
		}
	} while (!isUnique);

	// Получение пароля от пользователя
	password = getPasswordFromUser();

	// Общие поля для обоих типов пользователей
	std::cout << "Введите ваш номер телефона: +375";
	std::string phoneNumber = "+375" + ValidationManager::getPhone();
	std::cout << std::endl;

	std::string mail;
	do {
		std::cout << "Введите вашу почту: ";
		std::cin >> mail;
		if (!ValidationManager::isValidEmail(mail)) {
			ValidationManager::consoleIO("Невалидная почта. Пример: example@gmail.com", LightBlue);
		}
	} while (!ValidationManager::isValidEmail(mail));

	if (isTeacher) {
		// Ввод дополнительных данных для преподавателя
		std::string name, surname, middleName;
		int languageChoice;

		// Ввод ФИО преподавателя
		std::cout << "Введите ваше имя: ";
		ValidationManager::setStringWithOutSpace(name);
		std::cout << "Введите вашу фамилию: ";
		ValidationManager::setStringWithOutSpace(surname);
		std::cout << "Введите ваше отчество: ";
		ValidationManager::setStringWithOutSpace(middleName);

		// Выбор языка преподавания
		std::cout << "Выберите язык преподавания:\n";
		std::cout << "1. English\n2. Spanish\n3. French\n4. German\n5. Italian\n6. Chinese\n7. Japanese\n8. Russian\n9. Arabic\n10. Portuguese\n";
		do {
			std::cout << "Введите номер языка: ";
			languageChoice = ValidationManager::numberControl();
		} while (languageChoice < 1 || languageChoice > 10);

		Language language = static_cast<Language>(languageChoice - 1);

		// Создание преподавателя и добавление его в вектор
		createTeacherAndAddToVector(teachers, userName, password, name, surname, middleName, phoneNumber, mail, language);
		FileService::writeTeachersToFile("teachers.txt", teachers);

	} else {
		// Создание студента и добавление его в вектор
		createUserAndAddToVector(students, userName, password, phoneNumber, mail);
		FileService::writeStudentsToFile("students.txt", students);
	}
}


// Функция для проверки уникальности имени пользователя
bool UserValidation::isUsernameUnique(const std::vector<Student>& students, const std::vector<Teacher>& teachers, const std::string& userName) {
	for (const Student& student : students) {
		if (student.getUserName() == userName) {
			return false;
		}
	}
	for (const Teacher& teacher : teachers) {
		if (teacher.getUserName() == userName) {
			return false;
		}
	}
	return true;
}

// Функция для получения пароля от пользователя
std::string UserValidation::getPasswordFromUser() {
	std::string password;

	do {
		std::cout << std::endl << "Введите пароль: ";
		password = getPassword();
		if (password.size() < 6) {
			ValidationManager::consoleIO("Пароль должен состоять из 6 и более символов", LightBlue);

		}
	} while (password.size() < 6);

	return password;
}

void UserValidation::createTeacherAndAddToVector(std::vector<Teacher>& teachers, const std::string& userName, const std::string& password,
	const std::string& name, const std::string& surname, const std::string& middleName, const std::string& phoneNumber,
	const std::string& mail, Language language) {

	std::string salt = generateSalt();
	std::string hashedPassword = hashPassword(password + salt);
	Acess access = Pending; // По умолчанию доступ пользователя ожидает подтверждения администратора
	std::vector<int> courseIDs;

	// Генерация уникального ID преподавателя
	int teacherID = Teacher::generateID();

	// Создание нового объекта Teacher
	teachers.emplace_back(userName, hashedPassword, salt, access, courseIDs, name, surname, middleName, phoneNumber, mail, language);

	system("cls");
	ValidationManager::consoleIO("Преподаватель успешно зарегистрирован!", LightGreen);
}


// Функция для создания нового пользователя и добавления его в вектор
void UserValidation::createUserAndAddToVector(std::vector<Student>& students, const std::string& userName, const std::string& password,
	const std::string& phoneNumber, const std::string& mail) {
	std::string salt = generateSalt();
	std::string hashedPassword = hashPassword(password + salt);
	Acess access = Pending; // По умолчанию доступ пользователя ожидает подтверждения администратора
	std::vector<int> r;
	students.emplace_back(userName, hashedPassword, salt, access,r,phoneNumber,mail);
	system("cls");
	ValidationManager::consoleIO("Студент успешно зарегистрирован!", LightGreen);
}

//Функция дял сакрыитя пароля на *
std::string UserValidation::getPassword() {
	const char BACKSPACE = 8;
	const char ENTER = 13;
	std::string password;
	char ch;

	while ((ch = _getch()) != ENTER) {
		if (ch == BACKSPACE) {
			if (password.length() != 0) {
				std::cout << "\b \b";
				password.pop_back();
			}
		} else {
			if (ch != ' ') {
				std::cout << '*';
				password.push_back(ch);
			}
		}
	}
	std::cout << std::endl;
	return password;
}

int ValidationManager::numberControl() {
	const char BACKSPACE = 8;
	const char ENTER = 13;
	std::string str = "";
	char symbol;


	while ((symbol = _getch()) != ENTER || str == "") {
		if (symbol == BACKSPACE && !str.empty()) {
			str.pop_back();
			std::cout << "\b \b";
		}
		if (symbol >= '0' && symbol <= '9' && str.size() < std::to_string(INT_MAX).size() - 1) {
			str += symbol;
			std::cout << symbol;
		}
	}
	std::cout << std::endl;
	return std::stoi(str);
}

void ValidationManager::consoleIO(std::string err, ConsoleColor color) {
	SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), color);
	std::cout << err << std::endl;
	SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), LightGray);
}

std::string ValidationManager::languageLV() {
	const char BACKSPACE = 8;
	const char ENTER = 13;
	std::string str = "";
	char symbol;


	while ((symbol = _getch()) != ENTER || str == "") {
		if (symbol == BACKSPACE && !str.empty()) {
			str.pop_back();
			std::cout << "\b \b";
		}
		if (symbol >= 'A' && symbol <= 'C' && str.size() == 0) {
			str += symbol;
			std::cout << symbol;
		}
		if (symbol >= '1' && symbol <= '2' && str.size() == 1) {
			str += symbol;
			std::cout << symbol;
		}
	}
	std::cout << std::endl;
	return str;
}

std::string ValidationManager::enter_time() {
	std::string timeInput;
	std::regex timeFormat(R"(^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$)"); // Регулярное выражение для формата HH:MM

	do {
		std::cout << "Введите время (в формате HH:MM): ";
		std::getline(std::cin, timeInput);

		if (std::regex_match(timeInput, timeFormat)) {
			return timeInput; // Возвращаем, если формат верный
		} else {
			std::cout << "Ошибка: неверный формат времени. Попробуйте еще раз.\n";
		}
	} while (true);
}


//функция хеширования пароля
std::string UserValidation::hashPassword(const std::string& password) {
	std::hash<std::string> hasher;
	size_t hash = hasher(password);

	// Преобразование хеша в строку
	return std::to_string(hash);
}


bool ValidationManager::setStringWithOutSpace(std::string& str) {
	char ch;
	const char BACKSPACE = 8;
	const char ENTER = 13;
	const char ESC = 27;
	while ((ch = _getch()) != ENTER || str.length() == 0) {
		if (ch == ESC) return true;
		else if (ch == BACKSPACE) {
			if (str.length() != 0) {
				std::cout << "\b \b";
				str.pop_back();
			}
		} else {
			if (ch != ' ' && ((ch >= '0' && ch <= '9') ||
				(ch >= 'A' && ch <= 'z') || ch == '!' || ch == '_' || ch == '-')) {
				std::cout << ch;
				str.push_back(ch);
			}
		}
	}
	std:: cout << std::endl;
	return false;
}

bool ValidationManager::setStringWithSpace(std::string& str) {
	char ch;
	const char BACKSPACE = 8;
	const char ENTER = 13;
	const char ESC = 27;
	while ((ch = _getch()) != ENTER || str.length() == 0) {
		if (ch == ESC) return true;
		else if (ch == BACKSPACE) {
			if (str.length() != 0) {
				std::cout << "\b \b";
				str.pop_back();
			}
		} else {
			if (((ch >= '0' && ch <= '9') ||
				(ch >= 'A' && ch <= 'z') || ch == '!' || ch == '_' || ch == '-' || ch == ' ')) {
				std::cout << ch;
				str.push_back(ch);
			}
		}
	}
	std::cout << std::endl;
	return false;
}

std::string ValidationManager::getPhone() {
	char ch;
	const char BACKSPACE = 8;
	const char ENTER = 13;
	std::string str;
	while ((ch = _getch()) != ENTER || str.length() < 9) {
		 if (ch == BACKSPACE) {
			if (str.length() != 0) {
				std::cout << "\b \b";
				str.pop_back();
			}
		} else {
			if (ch != ' ' && (ch >= '0' && ch <= '9') && str.length() < 9) {
				std::cout << ch;
				str.push_back(ch);
			}
		}
	}
	std::cout << std::endl;
	return str;
}

bool ValidationManager::isValidEmail(const std::string& email) {
	// Регулярное выражение для проверки email
	const std::regex pattern(R"((^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$))");

	// Проверяем соответствие строки регулярному выражению
	return std::regex_match(email, pattern);
}

//функция дял создания файла с пользователем админ
void FileService::createAdminUser(const std::string fileName) {
	std::ofstream file1(fileName);
	if (file1.is_open()) {
		std::string salt = UserValidation::generateSalt();
		file1 << "admin" << ',' << UserValidation::hashPassword("admin" + salt) << ',' << salt << ",1" << std::endl;
		file1.close();
	}

	else {
		std::cout << "Ошибка открытия файла с данными об пользователях." << std::endl;
	}
}


void FileService::writeTeachersToFile(const std::string& filename, const std::vector<Teacher>& teachers) {
	std::ofstream file(filename);

	if (file.is_open()) {
		for (const auto& teacher : teachers) {
			file << teacher.getUserName() << ','
				<< teacher.getHashedPassword() << ','
				<< teacher.getSalt() << ','
				<< static_cast<int>(teacher.getUserAccess()) << ','
				<< teacher.getID() << ','
				<< teacher.getName() << ','
				<< teacher.getSurname() << ','
				<< teacher.getMiddleName() << ','
				<< teacher.getPhoneNumber() << ','
				<< teacher.getMail() << ',';

			// Записываем все courseIDs, разделенные запятыми
			for (size_t i = 0; i < teacher.getCoursesID().size(); ++i) {
				file << teacher.getCoursesID()[i];
				if (i != teacher.getCoursesID().size() - 1) {
					file << '|'; // Используем '|' как разделитель
				}
			}

			file << ',' << static_cast<int>(teacher.getLanguage()) << '\n';
		}
		file.close();
	}
}

void FileService::readTeachersFromFile(const std::string& filename, std::vector<Teacher>& teachers) {
	std::ifstream file(filename);
	std::string line;

	if (file.is_open()) {
		while (std::getline(file, line)) {
			std::stringstream ss(line);
			std::string uname, hashedPass, salt, name, surname, middleName, phone, mail, courseIDsStr;
			int access, teacherID, lang;

			// Читаем данные из строки
			std::getline(ss, uname, ',');
			std::getline(ss, hashedPass, ',');
			std::getline(ss, salt, ',');
			ss >> access;
			ss.ignore(1); // Пропускаем запятую

			ss >> teacherID;
			ss.ignore(1); // Пропускаем запятую

			std::getline(ss, name, ',');
			std::getline(ss, surname, ',');
			std::getline(ss, middleName, ',');
			std::getline(ss, phone, ',');
			std::getline(ss, mail, ',');

			std::getline(ss, courseIDsStr, ',');
			ss >> lang;

			// Преобразуем access и language в соответствующие enum значения
			Acess ac = static_cast<Acess>(access);
			Language language = static_cast<Language>(lang);

			// Разбираем строку courseIDsStr на вектор целых чисел
			std::vector<int> courseIDs;
			std::stringstream courseStream(courseIDsStr);
			std::string courseID;
			while (std::getline(courseStream, courseID, '|')) {
				courseIDs.push_back(std::stoi(courseID));
			}

			// Создаем объект Teacher и добавляем его в вектор
			teachers.emplace_back(uname, hashedPass, salt, ac, teacherID, name, surname, middleName, phone, mail, courseIDs, language);
		}
		file.close();
	}
}

void FileService::writeStudentsToFile(const std::string& filename, const std::vector<Student>& students) {
	std::ofstream file(filename);

	if (file.is_open()) {
		for (const auto& student : students) {
			// Записываем основные данные
			file << student.getUserName() << ','
				<< student.getHashedPassword() << ','
				<< student.getSalt() << ','
				<< static_cast<int>(student.getUserAccess()) << ','
				<< student.getID() << ','
				<< student.getPhoneNumber() << ','
				<< student.getMail() << ',';

			// Записываем все courseIDs, разделённые '|'
			for (size_t i = 0; i < student.getCoursesID().size(); ++i) {
				file << student.getCoursesID()[i];
				if (i != student.getCoursesID().size() - 1) {
					file << '|'; // Разделитель для идентификаторов курсов
				}
			}
			file << ','; // Переход к заявкам

			// Записываем заявки (requests)
			for (size_t i = 0; i < student.getRequests().size(); ++i) {
				const Request& request = student.getRequests()[i];
				file << request.getRequestID() << ':'
					<< request.getCourseID() << ':'
					<< static_cast<int>(request.getStatus());
				if (i != student.getRequests().size() - 1) {
					file << '|'; // Разделитель для заявок
				}
			}
			file << '\n';
		}
		file.close();
	}
}

void FileService::readStudentsFromFile(const std::string& filename, std::vector<Student>& students) {
	std::ifstream file(filename);
	std::string line;

	if (file.is_open()) {
		while (std::getline(file, line)) {
			std::stringstream ss(line);
			std::string uname, hashedPass, salt, phone, mail, coursesStr, requestsStr;
			int access, studentID;

			// Чтение основных данных
			std::getline(ss, uname, ',');
			std::getline(ss, hashedPass, ',');
			std::getline(ss, salt, ',');
			ss >> access;
			ss.ignore(1); // Пропуск запятой
			ss >> studentID;
			ss.ignore(1); // Пропуск запятой
			std::getline(ss, phone, ',');
			std::getline(ss, mail, ',');

			// Чтение списка идентификаторов курсов
			std::vector<int> coursesID;
			std::getline(ss, coursesStr, ',');
			std::stringstream coursesStream(coursesStr);
			std::string courseID;
			while (std::getline(coursesStream, courseID, '|')) {
				coursesID.push_back(std::stoi(courseID));
			}

			// Чтение заявок (requests)
			std::vector<Request> requests;
			std::getline(ss, requestsStr);
			std::stringstream requestsStream(requestsStr);
			std::string requestPart;
			while (std::getline(requestsStream, requestPart, '|')) {
				std::stringstream requestStream(requestPart);
				std::string reqID, courseID, status;

				std::getline(requestStream, reqID, ':');
				std::getline(requestStream, courseID, ':');
				std::getline(requestStream, status, ':');

				// Создание объекта Request
				requests.emplace_back(
					std::stoi(reqID),
					std::stoi(courseID),
					static_cast<RequestStatus>(std::stoi(status))
				);
			}

			// Создание объекта Student и добавление его в вектор
			students.emplace_back(
				uname, hashedPass, salt, static_cast<Acess>(access),
				studentID, coursesID, phone, mail, requests
			);
		}
		file.close();
	}
}

// Функция для записи вектора админов в файл
void FileService::writeAdminsToFile(const std::vector<Admin>& admins, const std::string& filename) {
	std::ofstream file(filename);

	if (file.is_open()) {
		for (const auto& admin : admins) {
			file << admin.getUserName() << ","
				<< admin.getHashedPassword() << ","
				<< admin.getSalt() << ","
				<< admin.getUserAccess() << "\n";  // Пишем в CSV формате
		}
		file.close();
	}
}

// Функция для чтения вектора админов из файла
void FileService::readAdminsFromFile(const std::string& filename, std::vector<Admin>& admins) {
	std::ifstream file(filename);
	std::string line;

	if (file.is_open()) {
		while (std::getline(file, line)) {
			std::stringstream ss(line);
			std::string uname, hashedPass, salt;
			short access;

			std::getline(ss, uname, ',');
			std::getline(ss, hashedPass, ',');
			std::getline(ss, salt, ',');
			ss >> access;
			Acess ac = Acess(access);
			admins.emplace_back(uname, hashedPass, salt, ac);
		}
		file.close();
	}
}

void FileService::writeCoursesToFile(const std::string& filename, const std::vector<Course>& courses) {
	std::ofstream file(filename);

	if (file.is_open()) {
		for (const auto& course : courses) {
			// Записываем основные данные курса
			file << course.getCourseID() << ','
				<< course.getName() << ','
				<< course.getLevel() << ','
				<< course.getID() << ','
				<< course.getTotalTopics() << ','
				<< course.getCompletedTopics() << ','
				<< course.getIsCompleted() << ','
				<< course.getTotalStudents() << ','
				<< static_cast<int>(course.getLanguage()) << ',';

			// Записываем список студентов, разделённый '|'
			for (size_t i = 0; i < course.getStudentsEnrolledID().size(); ++i) {
				file << course.getStudentsEnrolledID()[i];
				if (i != course.getStudentsEnrolledID().size() - 1) {
					file << '|';
				}
			}
			file << ',';

			// Записываем расписание
			const Schedule& schedule = course.getSchedule();

			// Преобразуем дни недели в строку
			const auto& days = schedule.getDay();
			for (size_t i = 0; i < days.size(); ++i) {
				file << days[i];
				if (i != days.size() - 1) {
					file << '|';
				}
			}
			file << ',' << schedule.getStartTime() << ','
				<< schedule.getEndTime() << ','
				<< schedule.getRoom() << '\n';
		}
		file.close();
	}
}

void FileService::readCoursesFromFile(const std::string& filename, std::vector<Course>& courses) {
	std::ifstream file(filename);
	std::string line;

	if (file.is_open()) {
		while (std::getline(file, line)) {
			std::stringstream ss(line);
			std::string name, level, studentsStr, daysStr, startTime, endTime, room;
			int courseID, teacherID, totalTopics, completedTopics, totalStudents, languageInt;
			bool isCompleted;

			// Чтение основных данных курса
			ss >> courseID;
			ss.ignore(1);
			std::getline(ss, name, ',');
			std::getline(ss, level, ',');
			ss >> teacherID;
			ss.ignore(1);
			ss >> totalTopics;
			ss.ignore(1);
			ss >> completedTopics;
			ss.ignore(1);
			ss >> isCompleted;
			ss.ignore(1);
			ss >> totalStudents;
			ss.ignore(1);
			ss >> languageInt;
			ss.ignore(1);

			// Чтение списка студентов, записанных на курс
			std::vector<int> studentsEnrolledID;
			std::getline(ss, studentsStr, ',');
			std::stringstream studentsStream(studentsStr);
			std::string studentID;
			while (std::getline(studentsStream, studentID, '|')) {
				studentsEnrolledID.push_back(std::stoi(studentID));
			}

			// Чтение расписания
			std::getline(ss, daysStr, ',');
			std::vector<DayOfWeek> days;
			std::stringstream daysStream(daysStr);
			std::string day;
			while (std::getline(daysStream, day, '|')) {
				days.push_back(static_cast<DayOfWeek>(std::stoi(day)));
			}

			std::getline(ss, startTime, ',');
			std::getline(ss, endTime, ',');
			std::getline(ss, room);

			// Создание расписания
			Schedule schedule(days, startTime, endTime, room);

			// Создание объекта Course и добавление его в вектор
			courses.emplace_back(
				courseID, name, level, teacherID, totalTopics, completedTopics, isCompleted,
				totalStudents, studentsEnrolledID, static_cast<Language>(languageInt), schedule
			);
		}
		file.close();
	}
}

void FileService::writeCertificatesToFile(const std::string& filename, const std::vector<Certificate>& certificates) {
	std::ofstream file(filename);

	if (file.is_open()) {
		for (const auto& certificate : certificates) {
			file <<certificate.getCourseID() <<','
				<< certificate.getTeacherName() << ','
				<< certificate.getCourseName() << ','
				<< static_cast<int>(certificate.getLanguage()) << ','
				<< certificate.getLanguageLevel() << '\n';
		}
		file.close();
	}
}

void FileService::readCertificatesFromFile(const std::string& filename, std::vector<Certificate>& certificates) {
	std::ifstream file(filename);
	std::string line;

	if (file.is_open()) {
		while (std::getline(file, line)) {
			std::stringstream ss(line);
			std::string teacherName, courseName, languageLevel;
			int languageInt, courseID;

			// Чтение данных
			ss >> courseID;
			ss.ignore(1); // Пропуск запятой
			std::getline(ss, teacherName, ',');
			std::getline(ss, courseName, ',');
			ss >> languageInt;
			ss.ignore(1); // Пропуск запятой
			std::getline(ss, languageLevel);

			// Создание объекта Certificate и добавление его в вектор
			certificates.emplace_back(courseID,teacherName, courseName, static_cast<Language>(languageInt), languageLevel);
		}
		file.close();
	}
}

#endif 