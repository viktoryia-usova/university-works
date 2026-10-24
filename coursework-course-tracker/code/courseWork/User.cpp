#ifndef user_C
#define user_C
#include "User.h"
#include "UserValidation.h"
#include "Student.h"

Account::Account() {
	access = Pending;
}

// Конструктор класса Account
Account::Account(const std::string& uname, const std::string& pass, const std::string& slt, Acess& acesses)
	: userName(uname), hashedPassword(pass), salt(slt), access(acesses) {
}

// Геттеры и сеттеры класса Account
std::string Account::getUserName() const { return userName; }

std::string Account::getSalt() const { return salt; }

std::string Account::getHashedPassword() const { return hashedPassword; }

Acess Account::getUserAccess() const { return access; }

void Account::setUserAccess(Acess accesses) { access = accesses; }

void Account::setHashedPassword(const std::string& hashedPassword) { this->hashedPassword = hashedPassword; }

std::string Account::accessLevelToString(Acess access) const {
	switch (access) {
	case Acess::Accepted: return "Разрешен";
	case Acess::Pending: return "Ожидает";
	case Acess::Blocked: return "Заблокирован";
	default: return "Неизвестно";
	}
}

Admin::Admin() :Account() {
}

Admin::Admin(const std::string& uname, const std::string& pass, const std::string& slt, Acess& acesses)
	:Account(uname, pass, slt, acesses) {
}

std::string Admin::getUserName() const
{
	return userName;
}


// Открытие меню администратора
void Admin::openAdminMenu(std::vector<Student>& students, std::vector<Teacher>& teachers, std::vector<Course>& courses, std::vector<Certificate>& certificates) {
	int choice;
	do {
		std::cout << "——————————————Меню администратора—————————————————" << std::endl;
		std::cout << "1. Управление учетными записями учеников" << std::endl;
		std::cout << "2. Управление учетными записями преподавателей" << std::endl;
		std::cout << "3. Режим редактирования курсов" << std::endl;
		std::cout << "4. Обработка заявок учеников" << std::endl;
		std::cout << "5. Выйти из меню администратора" << std::endl;
		std::cout << "Выберите действие: ";
		choice = ValidationManager::numberControl();
		switch (choice) {
		case 1:
			system("cls");
			openUserAccountManagement(students); // Вызов функции управления учетными записями учеников
			FileService::writeStudentsToFile("students.txt", students);
			break;
		case 2:
			system("cls");
			openUserAccountManagement(teachers); // Вызов функции управления учетными записями препдавателей
			FileService::writeTeachersToFile("teachers.txt", teachers);
			break;
		case 3:
			system("cls");
			openCourseManagementMenu(courses, teachers, students, certificates);
			FileService::writeTeachersToFile("teachers.txt", teachers);
			FileService::writeStudentsToFile("students.txt", students);
			FileService::writeCoursesToFile("courses.txt", courses);
			FileService::writeCertificatesToFile("certificate.txt", certificates); break;
		case 4:
			system("cls");
			processOrders(students, courses);
			FileService::writeStudentsToFile("students.txt", students);
			FileService::writeTeachersToFile("teachers.txt", teachers);
			FileService::writeCoursesToFile("courses.txt", courses);
			FileService::writeCertificatesToFile("certificate.txt", certificates);
			break;
		case 5:
			system("cls");
			break;
		default:
			//
			system("cls");
			ValidationManager::consoleIO("Некорректный ввод. Попробуйте еще раз.", LightBlue);
			break;
		}
	} while (choice != 5);
}

// Открытие меню управления учетными записями пользователей
void Admin::openUserAccountManagement(std::vector<Student>& users) {
	int choice;
	do {
		std::cout << "Управление учетными записями учеников:" << std::endl;
		std::cout << "1. Просмотреть список учеников" << std::endl;
		std::cout << "2. Изменить права доступа к системе" << std::endl;
		std::cout << "3. Удаление учетной записи" << std::endl;
		std::cout << "4. Вернуться" << std::endl;
		std::cout << "Выберите действие: ";

		choice = ValidationManager::numberControl();;

		switch (choice) {
		case 1:
			system("cls");
			viewUsers(users); // Вызов функции просмотра списка пользователей
			break;
		case 2:
			system("cls");
			changeAccessUser(users); // Вызов функции изменения доступа к системе
			FileService::writeStudentsToFile("students.txt", users);
			break;
		case 3:
			system("cls");
			deleteUser(users); // Вызов функции удаления учетной записи
			FileService::writeStudentsToFile("students.txt", users);
			break;
		case 4:
			FileService::writeStudentsToFile("students.txt", users);
			system("cls");
			return;
			break;
		default:
			system("cls");
			ValidationManager::consoleIO("Некорректный ввод. Попробуйте еще раз.", LightBlue);
			break;
		}
	} while (choice != 4);
}

void Admin::openUserAccountManagement(std::vector<Teacher>& users) {
	int choice;
	do {
		std::cout << "Управление учетными записями преподавателей:" << std::endl;
		std::cout << "1. Просмотреть список преподавателей" << std::endl;
		std::cout << "2. Изменить права доступа к системе" << std::endl;
		std::cout << "3. Удаление учетной записи" << std::endl;
		std::cout << "4. Вернуться" << std::endl;
		std::cout << "Выберите действие: ";

		choice = ValidationManager::numberControl();;

		switch (choice) {
		case 1:
			system("cls");
			viewUsers(users); // Вызов функции просмотра списка пользователей
			break;
		case 2:
			system("cls");
			changeAccessUser(users); // Вызов функции изменения доступа к системе
			FileService::writeTeachersToFile("teachers.txt", users);
			break;
		case 3:
			system("cls");
			deleteUser(users); // Вызов функции удаления учетной записи
			FileService::writeTeachersToFile("teachers.txt", users);
			break;
		case 4:
			system("cls");
			FileService::writeTeachersToFile("teachers.txt", users);
			return;
			break;
		default:
			system("cls");
			ValidationManager::consoleIO("Некорректный ввод. Попробуйте еще раз.", LightBlue);
			break;
		}
	} while (choice != 4);
}

// Просмотр списка пользователей
template<class T>
void Admin::displayUsers(const std::vector<T>& users) {
	if (users.empty()) {
		ValidationManager::consoleIO("Список студентов пуст.", LightBlue);
		return;
	}

	std::cout << "——————————————————————————————————————————————————————-----------" << std::endl;
	std::cout << "| ID   | Имя пользователя   |   Доступ      | Количество курсов |" << std::endl;
	std::cout << "——————————————————————————————————————————————————————-----------" << std::endl;

	for (const auto& user : users) {
		std::cout << "| " << std::setw(4) << user.getID()
			<< " | " << std::setw(18) << user.getUserName()
			<< " | " << std::setw(13) << accessLevelToString(user.getUserAccess())
			<< " | " << std::setw(17) << user.getCoursesID().size()
			<< " |" << std::endl;
	}

	std::cout << "——————————————————————————————————————————————————————-----------" << std::endl;
}

template<class T>
void Admin::viewUsers(const std::vector<T>& users){

	displayUsers(users);

	std::cout << "Выберите ID пользователя для просмотра подробной информации (0 для выхода): ";
	int ID = ValidationManager::numberControl();

	if (ID == 0) {
		return;
	}

	auto it = std::find_if(users.begin(), users.end(), [ID](const T& user) {
		return user.getID() == ID;
		});

	if (it != users.end()) {
		system("cls");
		displayUserDetails(*it);
	} else {
		system("cls");
		ValidationManager::consoleIO("Пользователь с указанным ID не найден.", LightRed);
	}
}

// Метод для детального просмотра информации о студенте
void Admin::displayUserDetails(const Student& student) const {
	std::cout << "—————————————————— Подробная информация о студенте —————————————————" << std::endl;
	std::cout << "ID: " << student.getID() << std::endl;
	std::cout << "Имя пользователя: " << student.getUserName() << std::endl;
	std::cout << "Уровень доступа: " << accessLevelToString(student.getUserAccess()) << std::endl;
	std::cout << "Телефон: " << student.getPhoneNumber() << std::endl;
	std::cout << "Почта: " << student.getMail() << std::endl;

	std::cout << "Курсы:" << std::endl;
	if (!student.getCoursesID().empty()) {
		for (const auto& courseID : student.getCoursesID()) {
			std::cout << "- ID курса: " << courseID << std::endl;
		}
	} else {
		std::cout << "Нет записанных курсов." << std::endl;
	}

	std::cout << "Сертификаты:" << std::endl;
	if (!student.getStudentCertificate().empty()) {
		for (const auto& certificate : student.getStudentCertificate()) {
			std::cout << "- Курс: " << certificate.getCourseName()
				<< ", Преподаватель: " << certificate.getTeacherName()
				<< ", Язык: " << Certificate::languageToString(certificate.getLanguage()) // Преобразуем enum в int или используем метод для отображения
				<< ", Уровень: " << certificate.getLanguageLevel() << std::endl;
		}
	} else {
		std::cout << "Сертификатов нет." << std::endl;
	}

	std::cout << "Заявки:" << std::endl;
	if (!student.getRequests().empty()) {
		for (const auto& request : student.getRequests()) {
			std::cout << "- ID заявки: " << request.getRequestID()
				<< ", ID курса: " << request.getCourseID()
				<< ", Статус: " << static_cast<int>(request.getStatus()) << std::endl;
		}
	} else {
		std::cout << "Заявок нет." << std::endl;
	}

	std::cout << "———————————————————————————————————————————————————————————————" << std::endl;
}
	
void Admin::displayUserDetails(const Teacher& teacher) const {
	std::cout << "—————————————————— Подробная информация о преподавателе —————————————————" << std::endl;
	std::cout << "ID: " << teacher.getID() << std::endl;
	std::cout << "Уровень доступа: " << accessLevelToString(teacher.getUserAccess()) << std::endl;
	std::cout << "ФИО: " << teacher.getSurname() << " "
		<< teacher.getName() << " "
		<< teacher.getMiddleName() << std::endl;
	std::cout << "Телефон: " << teacher.getPhoneNumber() << std::endl;
	std::cout << "Почта: " << teacher.getMail() << std::endl;

	std::cout << "Курсы, которые ведёт преподаватель:" << std::endl;
	if (!teacher.getCoursesID().empty()) {
		for (const auto& courseID : teacher.getCoursesID()) {
			std::cout << "- ID курса: " << courseID << std::endl;
		}
	} else {
		std::cout << "Преподаватель не ведёт курсов." << std::endl;
	}

	std::cout << "Язык преподавания: " << Certificate::languageToString(teacher.getLanguage()) << std::endl;
	std::cout << "——————————————————————————————————————————————————————————————————————" << std::endl;
}


// Изменение доступа к системе для пользователей
template<class T>
void Admin::changeAccessUser(std::vector<T>& users) {
	std::string userName;
	int choice;

	do {
		std::cout << "1) Разблокировать пользователя" << std::endl;
		std::cout << "2) Разрешить доступ к системе всем новым пользователям" << std::endl;
		std::cout << "3) Заблокировать пользователя" << std::endl;
		std::cout << "4) Вернуться" << std::endl;
		std::cout << "Выберите действие: ";

		choice = ValidationManager::numberControl();;


		switch (choice) {
		case 1:
			system("cls");
			grantSystemAccess(users); // Разрешаем доступ к системе пользователю
			break;
		case 2:
			system("cls");
			grantSystemAccessToAll(users); // Разрешаем доступ к системе всем новым пользователям
			break;
		case 3:
			system("cls");
			blockUser(users); // Блокируем пользователя
			break;
		case 4:
			system("cls");
			return; // Возвращаемся в предыдущее меню
		default:
			system("cls");
			ValidationManager::consoleIO("Некорректный ввод. Попробуйте еще раз.", LightBlue);
			break;
		}
	} while (choice != 4);
}

// Предоставление доступа к системе пользователю
template<class T>
void Admin::grantSystemAccess(std::vector<T>& users) {
	displayUsers(users);
	std::string userName;
	std::cout << "Введите имя пользователя, которому необходимо предоставить доступ к системе: ";
	std::cin >> userName;

	for (T& user : users) {
		if (user.getUserName() == userName) {
			if (user.getUserAccess() == Blocked) {
				user.setUserAccess(Accepted); // Предоставляем доступ к системе

				system("cls");
				ValidationManager::consoleIO("Пользователь " + userName + " получил доступ к системе", LightGreen);
				return;
			} else {
				system("cls");
				ValidationManager::consoleIO("Пользователь не заблокирован.", LightBlue);
				return;
			}
		}
	}
	system("cls");
	ValidationManager::consoleIO("Пользователь с именем " + userName + " не найден.", Magenta);
}

// Предоставление доступа к системе всем новым пользователям
template<class T>
void Admin::grantSystemAccessToAll(std::vector<T>& users) {
	for (T& user : users) {
		if (user.getUserAccess() == Pending) {
			user.setUserAccess(Accepted); // Предоставляем доступ к системе всем новым пользователям
		}
	}
	system("cls");
	ValidationManager::consoleIO("Все зарегистрированные пользователи получили доступ к системе", LightGreen);
}

// Блокировка пользователя
template<class T>
void Admin::blockUser(std::vector<T>& users) {
	displayUsers(users);
	std::string userName;
	std::cout << "Введите имя пользователя, которого необходимо заблокировать: ";
	std::cin >> userName;

	for (T& user : users) {
		if (user.getUserName() == userName) {
			if (user.getUserAccess() != Blocked) {
				user.setUserAccess(Blocked); // Блокируем пользователя
				system("cls");
				ValidationManager::consoleIO("Пользователь " + userName + " заблокирован.", LightGreen);
				return;
			} else {
				system("cls");
				ValidationManager::consoleIO("Пользователь уже заблокирован.", LightBlue);
				return;
			}
		}
	}
	system("cls");

	ValidationManager::consoleIO("Пользователь с именем " + userName + " не найден.", Magenta);
}

// Удаление учетной записи
template<class T>
void Admin::deleteUser(std::vector<T>& users) {
	displayUsers(users);
	std::string userName;
	std::cout << "Введите имя пользователя, которого необходимо удалить: ";
	std::cin >> userName;

	for (auto it = users.begin(); it != users.end(); ++it) {
		if (userName == it->getUserName()) {
			it = users.erase(it); // Удаляем учетную запись
			system("cls");
			ValidationManager::consoleIO("Пользователь " + userName + " удален.", LightGreen);
			return;
		}	
	}
	//
	system("cls");
	ValidationManager::consoleIO("Пользователь с именем " + userName + " не найден.", Magenta);
}

// Открытие меню управления курсами
void Admin::openCourseManagementMenu(std::vector<Course>& courses, std::vector<Teacher>& teachers, std::vector<Student>& students, std::vector<Certificate>& certificates) {
	int choice;

	do {
		std::cout << "——————————————Управление курсами—————————————————" << std::endl;
		std::cout << "1. Просмотреть список курсов" << std::endl;
		std::cout << "2. Добавить курс" << std::endl;
		std::cout << "3. Редактировать курс" << std::endl;
		std::cout << "4. Удалить курс" << std::endl;
		std::cout << "5. Фильтровать курсы по уровню" << std::endl;
		std::cout << "6. Вернуться в главное меню администратора" << std::endl;
		std::cout << "Выберите действие: ";
		choice = ValidationManager::numberControl();

		switch (choice) {
		case 1:
			system("cls");
			viewCourseList(courses);
			break;
		case 2:
			system("cls");
			addCourse(courses, teachers, certificates);
			FileService::writeCoursesToFile("courses.txt", courses);
			FileService::writeCertificatesToFile("certificate.txt", certificates);
			break;
		case 3:
			system("cls");
			editCourse(courses, teachers, certificates);
			FileService::writeCoursesToFile("courses.txt", courses);
			FileService::writeCertificatesToFile("certificate.txt", certificates);
			break;
		case 4:
			system("cls");
			deleteCourse(courses, teachers, students, certificates);
			FileService::writeCoursesToFile("courses.txt", courses);
			FileService::writeCertificatesToFile("certificate.txt", certificates);
			break;
		case 5:
			system("cls");
			filterCoursesByLevel(courses);
			break;
		case 6:
			system("cls");
			return;
		default:
			system("cls");
			ValidationManager::consoleIO("Некорректный ввод. Попробуйте еще раз.", LightBlue);
			break;
		}
	} while (choice != 6);
}


// Просмотр списка курсов в табличном виде
void Admin::viewCourseList(const std::vector<Course>& courses) const {
	if (courses.empty()) {
		ValidationManager::consoleIO("Список курсов пуст.", Yellow);
		return;
	}

	std::cout << std::left << std::setw(10) << "ID"
		<< std::setw(30) << "Название курса"
		<< std::setw(20) << "Язык"
		<< std::setw(15) << "Уровень"
		<< std::setw(10) << "Темы"
		<< std::setw(10) << "Студенты" << std::endl;
	std::cout << std::string(95, '-') << std::endl;

	for (const auto& course : courses) {
		std::cout << std::left << std::setw(10) << course.getCourseID()
			<< std::setw(30) << course.getName()
			<< std::setw(20) << Course::languageToString(course.getLanguage())
			<< std::setw(15) << course.getLevel()
			<< std::setw(10) << course.getTotalTopics()
			<< std::setw(10) << course.getTotalStudents() << std::endl;
	}
}

// Добавление нового курса
void Admin::addCourse(std::vector<Course>& courses, std::vector<Teacher>& teachers, std::vector<Certificate>& certificates) {
	std::string name, level;
	int totalTopics;
	Language language;

	std::cout << "Введите новое название курса: ";
	if (ValidationManager::setStringWithSpace(name)) {
		system("cls");
		return;
	}

	std::cout << "Выберите язык:\n";
	for (int i = 0; i < 10; ++i) {
		std::cout << i + 1 << ". " << Course::languageToString((static_cast<Language>(i))) << std::endl;
	}
	int languageChoice;
	do {
		std::cout << "Введите номер языка: ";
		languageChoice = ValidationManager::numberControl();
	} while (languageChoice < 1 || languageChoice > 10);

	language = static_cast<Language>(languageChoice - 1);

	// Выбор преподавателя
	std::cout << "Список преподавателей для выбранного языка:\n";
	std::vector<std::reference_wrapper<Teacher>> suitableTeachers; // Используем ссылки
	for (auto& teacher : teachers) {
		if (teacher.getLanguage() == language) {
			suitableTeachers.push_back(teacher);
			std::cout << suitableTeachers.size() << ". " << teacher.getFullName() << std::endl;
		}
	}

	if (suitableTeachers.empty()) {
		ValidationManager::consoleIO("Нет доступных преподавателей для выбранного языка.", LightRed);
		return;
	}

	int teacherChoice;
	do {
		std::cout << "Выберите преподавателя(0 для выхода): ";
		teacherChoice = ValidationManager::numberControl();
		if (teacherChoice < 1 || teacherChoice > suitableTeachers.size()) {
			ValidationManager::consoleIO("Неправильный ввод.", LightRed);
		}
		if (teacherChoice == 0) {
			system("cls");
			return;
		}
	} while (teacherChoice < 1 || teacherChoice > suitableTeachers.size());
	Teacher& selectedTeacher = suitableTeachers[teacherChoice - 1]; // Получаем ссылку на преподавателя

	std::cout << "Введите уровень курса (например, A1, B2): ";
	level = ValidationManager::languageLV();

	std::cout << "Введите количество тем: ";
	totalTopics = ValidationManager::numberControl();

	int totalStudent;
	std::cout << "Введите максимально количество учеников: ";
	totalStudent = ValidationManager::numberControl();

	Schedule schedule = inputSchedule();

	// Создание курса
	Course newCourse(name, level, selectedTeacher.getID(), totalTopics, language, schedule, totalStudent);

	// Добавление курса в список курсов и к преподавателю
	courses.push_back(newCourse);
	selectedTeacher.addCourse(newCourse.getCourseID()); // Прямое изменение через ссылку

	// Создание сертификата для курса
	Certificate cer(newCourse.getID(), selectedTeacher.getFullName(), name, language, level);
	certificates.push_back(cer);

	ValidationManager::consoleIO("Курс успешно добавлен!", LightGreen);
}


Schedule Admin::inputSchedule() {
	std::vector<DayOfWeek> days;
	std::string start_time, end_time, room;
	bool flag;
	do {
		flag = 0;
		std::cout << "Введите дни недели для курса (введите номера через пробел, завершите ввод нажатием Enter):\n";
		std::cout << "1 - Понедельник\n";
		std::cout << "2 - Вторник\n";
		std::cout << "3 - Среда\n";
		std::cout << "4 - Четверг\n";
		std::cout << "5 - Пятница\n";
		std::cout << "6 - Суббота\n";
		std::cout << "7 - Воскресенье\n";
	

		std::string input;
		ValidationManager::setStringWithSpace(input);
		std::istringstream iss(input);
		int day;
		while (iss >> day) {
			if (day >= 1 && day <= 7) {
				days.push_back(static_cast<DayOfWeek>(day - 1));
			} else {
				std::cout << "Неверный номер дня: " << day << ". Повторите ввод." << std::endl;
				flag = 1;
				days.clear();
			}
		}
		if(!flag)break;

	} while (true);
	int h1, h2,m1,m2;
	do {

	std::cout << "Введите время начала занятия (например, 10:00): ";
	start_time = ValidationManager::enter_time();
	h1 = std::stoi(start_time.substr(0, 2));
	m1 = std::stoi(start_time.substr(3,2));
	std::cout << "Введите время окончания занятия (например, 12:00): ";

	end_time = ValidationManager::enter_time();
	h2 = std::stoi(end_time.substr(0, 2));
	m2 = std::stoi(start_time.substr(3, 2));
	if (h2 < h1 || (h2==h1 && m2 < m1)) {
		ValidationManager::consoleIO("Время окончания не может быть меньше чем время окончания", LightRed);
	}
	} while (h2 < h1 || (h2 == h1 && m2 < m1));

	std::cout << "Введите кабинет: ";
	ValidationManager::setStringWithOutSpace(room);

	return Schedule(days, start_time, end_time, room);
}


// Редактирование курса
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
void Admin::editCourse(std::vector<Course>& courses, std::vector<Teacher>& teachers, std::vector<Certificate>& certificates) {
	if (courses.empty()) {
		system("cls");
		ValidationManager::consoleIO("Список курсов пуст. Нечего редактировать.", Yellow);
		return;
	}

	viewCourseList(courses);
	int courseID;
	do {
		std::cout << "Введите ID курса для редактирования: ";
		courseID = ValidationManager::numberControl();
	} while (courseID < 1000 || courseID > courses.size()+1000);

	auto it = std::find_if(courses.begin(), courses.end(),
		[courseID](const Course& course) { return course.getCourseID() == courseID; });

	auto itCer = std::find_if(certificates.begin(), certificates.end(),
		[courseID](const Certificate& certificate) { return certificate.getCourseID() == courseID; });

	if (it == courses.end()) {
		ValidationManager::consoleIO("Курс с указанным ID не найден.", LightRed);
		return;
	}

	Course& course = *it;
	std::vector<Teacher*> suitableTeachers;

	int choice;
	do {
		std::cout << "Что вы хотите отредактировать?\n";
		std::cout << "1. Название\n2. Язык\n3. Уровень\n4. Количество тем\n5. Расписание\n6. Назад\nВыберите действие: ";

		do {
			std::cout << "Ваш выбор: ";
			choice = ValidationManager::numberControl();
		} while (choice < 1 || choice > 6);

		std::string name;
		std::string lv;
		int languageChoice;
		Schedule schedule;
		switch (choice) {
		case 1:
			std::cout << "Введите новое название курса: ";
			if (ValidationManager::setStringWithSpace(name)) {
				system("cls");
				return;
			}
			course.setName(name);
			itCer->setCourseName(name);
			break;

		case 2: 
			std::cout << "Выберите новый язык:\n";
			for (int i = 0; i < 10; ++i) {
				std::cout << i + 1 << ". " << Course::languageToString((static_cast<Language>(i))) << std::endl;
			}

			do {
				std::cout << "Введите номер языка: ";
				languageChoice = ValidationManager::numberControl();
			} while (languageChoice < 1 || languageChoice > 10);
			
			std::cout << "Список преподавателей для выбранного языка:\n";

			for (auto& teacher : teachers) {
				if (teacher.getLanguage() == languageChoice) {
					suitableTeachers.push_back(&teacher);
					std::cout << suitableTeachers.size() << ". " << teacher.getFullName() << std::endl;
				}
			}

			if (suitableTeachers.empty()) {
				system("cls");
				ValidationManager::consoleIO("Нет доступных преподавателей для выбранного языка.", LightRed);
				return;
			}

			int teacherChoice;
			do {
				std::cout << "Выберите преподавателя: ";
				teacherChoice = ValidationManager::numberControl();
			} while (teacherChoice < 1 || teacherChoice > teachers.size());

			course.setTeacherID(suitableTeachers[teacherChoice - 1]->getID());
			course.setLanguage(static_cast<Language>(languageChoice - 1));

			itCer->setTeacherName(suitableTeachers[teacherChoice - 1]->getFullName());
			itCer->setLanguage(static_cast<Language>(languageChoice - 1));

			break;
		case 3:
			std::cout << "Введите уровень курса (А1 - С2): ";
			lv = ValidationManager::languageLV();;
			course.setLevel(lv);
			itCer->setLanguageLevel(lv);
			break;
		case 4:
			std::cout << "Введите новое количество тем: ";
			course.setTotalTopics(ValidationManager::numberControl());

			break; 
		case 5:
			schedule = inputSchedule();
			course.setSchedule(schedule);
			break;
		case 6:
			return;
		default:
			system("cls");
			ValidationManager::consoleIO("Некорректный ввод. Попробуйте снова.", LightBlue);
			break;
		}
	} while (choice != 6);
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Удаление курса
void Admin::deleteCourse(std::vector<Course>& courses, std::vector<Teacher>& teachers, std::vector<Student>& students, std::vector<Certificate>& certificates) {
	if (courses.empty()) {
		system("cls");
		ValidationManager::consoleIO("Список курсов пуст. Нечего удалять.", Yellow);
		return;
	}

	// Отобразить список курсов
	viewCourseList(courses);

	// Получение ID курса для удаления
	int courseID;
	do {
		std::cout << "Введите ID курса для удаления: ";
		courseID = ValidationManager::numberControl();
	} while (courseID <= 0);

	// Найти и удалить курс из списка курсов
	auto it = std::find_if(courses.begin(), courses.end(),
		[courseID](const Course& course) { return course.getCourseID() == courseID; });

	auto itCer = std::find_if(certificates.begin(), certificates.end(),
		[courseID](const Certificate& certificate) { return certificate.getCourseID() == courseID; });

	if (it != courses.end()) {
		// Удаление курса у преподавателей
		for (auto& teacher : teachers) {
			if (teacher.hasCourse(courseID)) { // Проверяем, есть ли курс у преподавателя
				teacher.removeCourse(courseID);
			}
		}

		// Удаление курса у студентов
		if (it->getStudentsEnrolledID().size() > 0) {
			for (auto& student : students) {
				if (student.hasCourse(courseID)) { // Проверяем, есть ли курс у ученика
					student.removeCourse(courseID);
				}
			}
		}
		// Удаляем курс из общего списка
		certificates.erase(itCer);
		courses.erase(it);

		system("cls");
		ValidationManager::consoleIO("Курс успешно удален!", LightGreen);
	} else {
		system("cls");
		ValidationManager::consoleIO("Курс с указанным ID не найден.", LightRed);
	}
}

void Admin::filterCoursesByLevel(const std::vector<Course>& courses) {
	std::string minLevel, maxLevel;
	std::cout << "Введите минимальный уровень (например, A1): ";
	minLevel = ValidationManager::languageLV();
	std::cout << "Введите максимальный уровень (например, B2): ";
	maxLevel = ValidationManager::languageLV();

	// Уровни для сравнения, упорядоченные в массиве
	const std::vector<std::string> levels = { "A1", "A2", "B1", "B2", "C1", "C2" };

	// Проверка, что введенные уровни существуют в массиве
	auto minIt = std::find(levels.begin(), levels.end(), minLevel);
	auto maxIt = std::find(levels.begin(), levels.end(), maxLevel);

	if (minIt == levels.end() || maxIt == levels.end() || minIt > maxIt) {
		ValidationManager::consoleIO("Ошибка: указанный диапазон уровней некорректен.\n", LightRed);
		return;
	}

	std::cout << "\nКурсы с уровнями от " << minLevel << " до " << maxLevel << ":\n";
	std::cout << "——————————————————————————————————————————————————-——————————————————————————-—-———" << std::endl;
	std::cout << "| ID курса |         Название          |     Язык      |   Уровень   | Кол-во тем |" << std::endl;
	std::cout << "———————————————————————————————————————————————————-—————————————————————————-——-——" << std::endl;

	bool found = false;
	for (const auto& course : courses) {
		auto courseIt = std::find(levels.begin(), levels.end(), course.getLevel());
		if (courseIt != levels.end() && courseIt >= minIt && courseIt <= maxIt) {
			std::cout << "| " << std::setw(8) << course.getID()
				<< " | " << std::setw(25) << course.getName()
				<< " | " << std::setw(13) << Course::languageToString(course.getLanguage())
				<< " | " << std::setw(11) << course.getLevel()
				<< " | " << std::setw(10) << course.getTotalTopics() << " |" << std::endl;
			found = true;
		}
	}

	if (!found) {
		std::cout << "Нет курсов в указанном диапазоне уровней.\n";
	}

	std::cout << "———————————————————————————-—————————————————————————————————————————————————-——-——" << std::endl;
}


void Admin::processOrders(std::vector<Student>& students, std::vector<Course>& courses) {
	std::vector<Student*> studentsWithRequests;

	std::cout << "Студенты с ожидающими запросами на курсы:" << std::endl;
	for (auto& student : students) {
		// Проверяем, есть ли у студента ожидающие запросы
		bool hasPendingRequest = std::any_of(student.getRequests().begin(), student.getRequests().end(), [](const Request& request) {
			return request.getStatus() == RequestStatus::Expected;
			});

		if (hasPendingRequest) {
			studentsWithRequests.push_back(&student);
			std::cout << studentsWithRequests.size() << ". " << student.getUserName() << std::endl;
		}
	}

	// Если нет студентов с ожидающими запросами
	if (studentsWithRequests.empty()) {
		system("cls");
		ValidationManager::consoleIO("Нет студентов с ожидающими запросами.", LightMagenta);
		return;
	}

	std::cout << "Выберите номер студента для обработки его запросов (0 для выхода): ";
	int studentChoice = ValidationManager::numberControl();
	if (studentChoice == 0 || studentChoice > studentsWithRequests.size()) {
		return;
	}

	Student& selectedStudent = *studentsWithRequests[studentChoice - 1];

	std::cout << "Запросы студента: " << selectedStudent.getUserName() << std::endl;
	for (const Request& request : selectedStudent.getRequests()) {
		if (request.getStatus() == RequestStatus::Expected) {
			std::cout << "ID Запроса: " << request.getRequestID()
				<< ", ID Курса: " << request.getCourseID()
				<< ", Статус: Ожидает подтверждения" << std::endl;
		}
	}

	std::cout << "Введите ID запроса для обработки (0 для выхода): ";
	int requestID = ValidationManager::numberControl();
	if (requestID == 0) {
		return;
	}

	std::vector<Request>& requests = selectedStudent.getRequests();
	std::vector<Request>::iterator it = std::find_if(requests.begin(), requests.end(), [requestID](const Request& request) {
		return request.getRequestID() == requestID;
		});

	if (it != requests.end()) {
		std::cout << "1. Подтвердить запрос" << std::endl;
		std::cout << "2. Отклонить запрос" << std::endl;

		std::cout << "Выберите действие: ";
		int action = ValidationManager::numberControl();

		if (action == 1) {
			it->setStatus(Approved); // Подтверждение запроса
			system("cls");
			int courseID = it->getCourseID();
			auto it = std::find_if(courses.begin(), courses.end(), [courseID](Course course) {return course.getCourseID() == courseID; });
			it->getStudentsEnrolledID().push_back(selectedStudent.getID());
			ValidationManager::consoleIO("Запрос подтвержден. Студент записан на курс.", LightGreen);
			
			if (it->getStudentsEnrolledID().size() >= it->getTotalStudents()) it->setIsCompleted(true);

			// Добавляем курс в список курсов студента
			selectedStudent.getCoursesID().push_back(it->getCourseID());
		} else if (action == 2) {
			it->setStatus(RequestStatus::Rejected); // Отклонение запроса
			system("cls");
			ValidationManager::consoleIO("Запрос отклонен.", LightRed);
		} else {
			system("cls");
			ValidationManager::consoleIO("Некорректное действие.", LightBlue);
		}

		// Сохраняем данные студентов
		FileService::writeStudentsToFile("students.txt", students);
		FileService::writeCoursesToFile("courses.txt", courses);
	} else {
		system("cls");
		ValidationManager::consoleIO("Запрос с данным ID не найден.", LightMagenta);
	}
}


#endif


