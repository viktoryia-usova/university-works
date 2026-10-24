#include "Teacher.h"
#include "UserValidation.h"

std::unordered_set<unsigned int> Teacher::IDs = {};

// Конструктор класса Teacher
Teacher::Teacher(const std::string& name, const std::string& surname, const std::string& middleName,
	const std::string& phoneNumber, const std::string& mail)
	: teacherID(generateID()), name(name), surname(surname), middleName(middleName), phoneNumber(phoneNumber), mail(mail) {
}

Teacher::Teacher(const std::string& userName, const std::string& hashedPassword, const std::string& salt,
	Acess access, int teacherID, const std::string& name, const std::string& surname,
	const std::string& middleName, const std::string& phoneNumber, const std::string& mail, const std::vector<int>& courseIDs, Language language)
	: Account(userName, hashedPassword, salt, access), // Инициализация полей базового класса
	name(name), surname(surname), middleName(middleName),
	phoneNumber(phoneNumber), mail(mail), courseIDs(courseIDs),
	language(language), teacherID(teacherID) {
	IDs.insert(teacherID);
}

Teacher::Teacher(const std::string& userName, const std::string& hashedPassword, const std::string& salt,
	Acess access, const std::vector<int>& courseIDs, const std::string& name, const std::string& surname,
	const std::string& middleName, const std::string& phoneNumber, const std::string& mail, Language language) : Account(userName, hashedPassword, salt, access), // Инициализация полей базового класса
	name(name), surname(surname), middleName(middleName),
	phoneNumber(phoneNumber), mail(mail), courseIDs(courseIDs),
	language(language), teacherID(generateID()) {
}

// Геттеры и сеттеры

// Получить ID преподавателя
int Teacher::getID() const {
	return teacherID;
}

// Установить ID преподавателя
void Teacher::setTeacherID(int id) {
	teacherID = id;
}

std::string Teacher::getUserName() const
{
	return userName;
}

// Получить имя преподавателя
std::string Teacher::getName() const {
	return name;
}

// Установить имя преподавателя
void Teacher::setName(const std::string& name) {
	this->name = name;
}

// Получить фамилию преподавателя
std::string Teacher::getSurname() const {
	return surname;
}

// Установить фамилию преподавателя
void Teacher::setSurname(const std::string& surname) {
	this->surname = surname;
}

// Получить отчество преподавателя
std::string Teacher::getMiddleName() const {
	return middleName;
}

// Установить отчество преподавателя
void Teacher::setMiddleName(const std::string& middleName) {
	this->middleName = middleName;
}

// Получить номер телефона преподавателя
std::string Teacher::getPhoneNumber() const {
	return phoneNumber;
}

// Установить номер телефона преподавателя
void Teacher::setPhoneNumber(const std::string& phoneNumber) {
	this->phoneNumber = phoneNumber;
}

// Получить электронную почту преподавателя
std::string Teacher::getMail() const {
	return mail;
}

// Установить электронную почту преподавателя
void Teacher::setMail(const std::string& mail) {
	this->mail = mail;
}

Language Teacher::getLanguage() const {
	return language;
}

// Получить список ID курсов, которые ведет преподаватель
std::vector<int> Teacher::getCoursesID() const {
	return courseIDs;
}

std::string Teacher::getFullName() {
	return name+' ' + surname +' '+ middleName;
}

void Teacher::addCourse(int id) {
	courseIDs.push_back(id);
}

bool Teacher::hasCourse(int courseID) const {
	return std::find(courseIDs.begin(), courseIDs.end(), courseID) != courseIDs.end();
}

int Teacher::generateID() {
	int newID = 1000;
		do {
			++newID;   // Генерируем новый ID
		} while (IDs.find(newID) != IDs.end());
		IDs.insert(newID);
		return newID;
}

void Teacher::removeCourse(int courseID) {
	auto it = std::find(courseIDs.begin(), courseIDs.end(), courseID);
	if (it != courseIDs.end()) {
		courseIDs.erase(it);
	}
}


// Открытие меню пользователя
void Teacher::openTeacherMenu(std::vector<Student>& students, std::vector<Course>& courses, std::vector<Certificate>& certificates, std::vector<Teacher>& teachers) {
	int choice;

	do {
		std::cout << "Меню учителя:\n";
		std::cout << "1. Просмотреть свои курсы\n";
		std::cout << "2. Просмотреть записанных студентов\n";
		std::cout << "3. Редактировать расписание\n";
		std::cout << "4. Провести занятие\n";
		std::cout << "5. Выдать сертификаты курсу\n";
		std::cout << "6. Просмотреть расписание курса\n";
		std::cout << "7. Удалить студента из курса\n";
		std::cout << "8. Редактировать профиль" << std::endl;
		std::cout << "0. Выйти\n";
		std::cout << "Ваш выбор: ";
		choice = ValidationManager::numberControl();

		switch (choice) {
		case 1:
			system("cls");
			viewMyCourses(courses); 
			break;
		case 2:
			system("cls");
			viewStudentsForCourses(students, courses); 
			break;
		case 3:
			system("cls");
			editCourseSchedule(courses);
			FileService::writeTeachersToFile("teachers.txt", teachers);
			FileService::writeCoursesToFile("courses.txt", courses);
			break;
		case 4:
			system("cls");
			conductLesson(courses); 
			FileService::writeTeachersToFile("teachers.txt", teachers);
			FileService::writeCoursesToFile("courses.txt", courses);
			break;
		case 5:
			system("cls");
			issueCertificatesForCourse(courses, certificates, students); 
			FileService::writeStudentsToFile("students.txt", students);
			FileService::writeTeachersToFile("teachers.txt", teachers);
			FileService::writeCoursesToFile("courses.txt", courses);
			FileService::writeCertificatesToFile("certificate.txt", certificates);
			break;
		case 6:
			system("cls");
			viewSchedule(courses); 
			break;
		case 7:
			system("cls");
			removeStudentFromCourse(students, courses); 
			FileService::writeStudentsToFile("students.txt", students);
			FileService::writeTeachersToFile("teachers.txt", teachers);
			FileService::writeCoursesToFile("courses.txt", courses); break;
		case 8:
			system("cls");
			editProfile();
			FileService::writeTeachersToFile("teachers.txt", teachers);
			break;
		case 0:
			system("cls");
			break;
		default:
			system("cls");
			ValidationManager::consoleIO("Некорректный выбор. Попробуйте снова.", LightBlue);
			break;
		}

	} while (choice != 0);
}

void Teacher::viewMyCourses(const std::vector<Course>& courses) const {
	std::cout << "————————————————————————————————————————————————————————————————————————————-—-———" << std::endl;
	std::cout << "| ID курса |         Название          |     Язык     |   Уровень   | Кол-во тем |" << std::endl;
	std::cout << "————————————————————————————————————————————————————————————————————————————-——-——" << std::endl;

	bool hasCourses = false;

	for (const auto& courseID : courseIDs) {
		auto it = std::find_if(courses.begin(), courses.end(), [courseID](const Course& course) {
			return course.getCourseID() == courseID;		
			});

		if (it != courses.end()) {
			hasCourses = true;
			std::cout << "| " << std::setw(8) << it->getCourseID()
				<< " | " << std::setw(25) << it->getName()
				<< " | " << std::setw(12) << Course::languageToString(it->getLanguage())
				<< " | " << std::setw(11) << it->getLevel()
				<< " | " << std::setw(10) << it->getTotalTopics() << " |" << std::endl;
		}
	}

	if (!hasCourses) {
		std::cout << "|                                Вы ещё не записаны на курсы.                    |" << std::endl;
	}

	std::cout << "—————————————————————————————————————————————————————————————————————————————-—-——" << std::endl;
}

// Прототипы методов в классе Teacher
void Teacher::viewStudentsForCourses(const std::vector<Student>& students, const std::vector<Course>& courses) {
	// Отображение курсов преподавателя
	viewMyCourses(courses);

	int courseID;
	std::cout << "\nВведите ID курса, чтобы просмотреть студентов(0 для выхода): ";
	courseID = ValidationManager::numberControl();
	if (courseID == 0) {
		system("cls");
		return;
	}

	// Поиск выбранного курса
	auto it = std::find_if(courses.begin(), courses.end(), [courseID](const Course& c) {
		return c.getCourseID() == courseID;
		});

	if (it == courses.end() || it->getID() != this->getID()) {
		system("cls");
		ValidationManager::consoleIO("Курс не найден или вы не являетесь его преподавателем.", LightRed);
		return;
	}

	// Список ID студентов, записанных на курс
	const auto& studentIDs = it->getStudentsEnrolledID();

	if (studentIDs.empty()) {
		system("cls");
		ValidationManager::consoleIO("На данный курс ещё никто не записался.", Yellow);
		return;
	}

	// Вывод списка студентов
	std::cout << "\nСписок студентов на курсе \"" << it->getName() << "\":\n";
	std::cout << "+—————————————+——————————————————+—————————----------+\n";
	std::cout << "| ID студента |        Имя       |       Email       |\n";
	std::cout << "+—————————————+——————————————————+—————————----------+\n";

	for (const int studentID : studentIDs) {
		auto studentIt = std::find_if(students.begin(), students.end(), [studentID](const Student& s) {
			return s.getID() == studentID;
			});

		if (studentIt != students.end()) {
			std::cout << "| " << std::setw(11) << studentIt->getID()
				<< " | " << std::setw(16) << studentIt->getUserName()
				<< " | " << std::setw(17) << studentIt->getMail() << " |\n";
		}
	}
	std::cout << "+—————————————+——————————————————+—————————----------+\n";
}


void Teacher::editCourseSchedule(std::vector<Course>& courses) {
	if (courses.empty()) {
		system("cls");
		ValidationManager::consoleIO("Список курсов пуст. Нечего редактировать.", Yellow);
		return;
	}

	viewMyCourses(courses);
	int courseID;
	do {
		std::cout << "Введите ID курса для редактирования(0 для выхода): ";
		courseID = ValidationManager::numberControl();
		if (courseID == 0) {
			system("cls");
			return;
		}
		if (courseID < 1000 || courseID > 9999) {
			ValidationManager::consoleIO("Курс не найден или вы не являетесь его преподавателем.", LightRed);
			return;
		}
	} while (courseID < 1000 || courseID > 9999);

	auto it = std::find_if(courses.begin(), courses.end(),
		[courseID](const Course& course) { return course.getCourseID() == courseID; });
	bool flag = false;
	for (int id : courseIDs) {
		if (id == courseID)flag = true;
	}
	if (it == courses.end() || !flag) {
		ValidationManager::consoleIO("Курс не найден или вы не являетесь его преподавателем.", LightRed);
		return;
	}

	it->setSchedule(Admin::inputSchedule());
}

void Teacher::conductLesson(std::vector<Course>& courses) {
	viewMyCourses(courses);
	
	int courseID;
	std::cout << "\nВведите ID курса для проведения занятия(0 для выхода): ";
	courseID = ValidationManager::numberControl();
	if (courseID == 0) {
		system("cls");
		return;
	}
	auto it = std::find_if(courses.begin(), courses.end(), [courseID](const Course& c) {
		return c.getCourseID() == courseID;
		});

	if (it == courses.end() || it->getID() != this->getID()) {
		ValidationManager::consoleIO("Курс не найден или вы не являетесь его преподавателем.", LightRed);
		return;
	}

	if (it->getStudentsEnrolledID().size() <= 0) {
		ValidationManager::consoleIO("На данный курс не записан ни один студент.", Yellow);
		return;
	}

	if (it->getCompletedTopics() >= it->getTotalTopics()) {
		system("cls");
		ValidationManager::consoleIO("Все темы в курсе \"" + it->getName() + "\" уже завершены.\n", Yellow);
		return;
	}

	it->incrementCompletedTopics();
	
	system("cls");
	std::cout << "Занятие проведено. Темы завершены: " << it->getCompletedTopics()
		<< "/" << it->getTotalTopics() << ".\n\n";

	if (it->getCompletedTopics() == it->getTotalTopics()) {
		it->markAsCompleted();
		ValidationManager::consoleIO("Курс \"" + (it->getName()) + "\" завершен!\n",LightGreen);
	}
}



void Teacher::issueCertificatesForCourse(std::vector<Course>& courses, std::vector<Certificate>& certificates, std::vector<Student>& students) {
	// Отображаем список курсов преподавателя
	viewMyCourses(courses);

	int courseID;
	std::cout << "\nВведите ID курса, для которого хотите выдать сертификаты: ";
	courseID = ValidationManager::numberControl();

	// Поиск курса
	auto courseIt = std::find_if(courses.begin(), courses.end(), [courseID](const Course& c) {
		return c.getCourseID() == courseID;
		});

	if (courseIt == courses.end() || courseIt->getID() != this->getID()) {
		ValidationManager::consoleIO("Курс не найден или вы не являетесь его преподавателем.", LightRed);
		return;
	}

	if (!courseIt->getIsCompleted()) {
		ValidationManager::consoleIO("Курс ещё не завершён. Сертификаты нельзя выдать.", Yellow);
		return;
	}

	int studentId = courseIt->getStudentsEnrolledID()[0];
	auto studentIT = std::find_if(students.begin(), students.end(), [studentId](const Student& s) {
		return s.getID() == studentId;
		});

	for (Certificate cer : studentIT->getStudentCertificate()) {
		if (cer.getCourseID() == courseID) {
			system("cls");
			ValidationManager::consoleIO("Вы не можете выдать сертификаты дважды", LightRed);
			return;
		}
	}

	// Проверяем, есть ли студенты, записанные на курс
	const auto& studentIDs = courseIt->getStudentsEnrolledID();
	if (studentIDs.empty()) {
		ValidationManager::consoleIO("На данный курс не записан ни один студент.", Yellow);
		return;
	}

	// Выдача сертификатов

		// Создание сертификата
	Certificate cert(courseID, this->getFullName(), courseIt->getName(),  courseIt->getLanguage(),courseIt->getLevel());
	certificates.push_back(cert);
	
	std::vector<Student*> studentsIt;
	for (Student& student : students) {
		for (int& studentCourseID : student.getCoursesID()) {
			if (studentCourseID == courseID) {
				student.getStudentCertificate().push_back(cert);
			}
		}
	}

	ValidationManager::consoleIO("Сертификаты успешно выданы всем студентам курса.", LightGreen);
}


void Teacher::viewSchedule(const std::vector<Course>& courses) const {
	if (courseIDs.empty()) {
		ValidationManager::consoleIO("Вы ещё не записаны на курсы.\n", LightRed);
		return;
	}

	// Вывод списка курсов
	system("cls");
	viewMyCourses(courses);

	std::vector<const Course*> enrolledCourses; // Список курсов, на которые записан студент
	for (const auto& courseID : courseIDs) {
		auto it = std::find_if(courses.begin(), courses.end(), [courseID](const Course& course) {
			return course.getCourseID() == courseID;
			});

		if (it != courses.end()) {
			enrolledCourses.push_back(&(*it));
		}
	}

	std::cout << "+----------------------------------+" << std::endl;
	std::cout << "Введите ID курса, чтобы увидеть расписание: ";
	int selectedID = ValidationManager::numberControl();

	auto selectedCourseIt = std::find_if(enrolledCourses.begin(), enrolledCourses.end(), [selectedID](const Course* course) {
		return course->getCourseID() == selectedID;
		});

	if (selectedCourseIt == enrolledCourses.end()) {
		ValidationManager::consoleIO("Некорректный выбор ID!\n", LightRed);
		return;
	}

	const Course* selectedCourse = *selectedCourseIt;

	// Вывод расписания курса
	system("cls");
	std::cout << "+----------------------------+" << std::endl;
	std::cout << "| Расписание курса           |" << std::endl;
	std::cout << "+----------------------------+" << std::endl;
	std::cout << "| Курс: " << selectedCourse->getName() << std::endl;
	std::cout << "| Преподаватель: " << selectedCourse->getID() << std::endl;
	std::cout << "| Количество пройденных тем тем: " << selectedCourse->getCompletedTopics() << '/' << selectedCourse->getTotalTopics() << std::endl;
	std::cout << "+-------------+---------------+--------------+" << std::endl;
	std::cout << "| День        | Время         | Кабинет      |" << std::endl;
	std::cout << "+-------------+---------------+--------------+" << std::endl;

	const Schedule& schedule = selectedCourse->getSchedule();
	for (const auto& day : schedule.getDay()) {
		std::cout << "| " << std::setw(11) << Schedule::getDayAsString(day)
			<< " | " << std::setw(13) << schedule.getStartTime() + " - " + schedule.getEndTime()
			<< " | " << std::setw(12) << schedule.getRoom()
			<< " |" << std::endl;
	}

	std::cout << "+-------------+---------------+--------------+" << std::endl;
}


void Teacher::removeStudentFromCourse(std::vector<Student>& students, std::vector<Course>& courses) {
	viewMyCourses(courses);

	int courseID;

	std::cout << "\nВведите ID курса, чтобы удалить студента: ";
	courseID = ValidationManager::numberControl();

	// Поиск выбранного курса
	auto courseIt = std::find_if(courses.begin(), courses.end(), [courseID](const Course& c) {
		return c.getCourseID() == courseID;
		});

	if (courseIt == courses.end() || courseIt->getID() != this->getID()) {
		ValidationManager::consoleIO("Курс не найден или вы не являетесь его преподавателем.", LightRed);
		return;
	}

	// Список ID студентов, записанных на курс
	auto& studentIDs = courseIt->getStudentsEnrolledID();
	if (studentIDs.empty()) {
		ValidationManager::consoleIO("На данный курс ещё никто не записался.", Yellow);
		return;
	}

	std::cout << "\nСписок студентов на курсе \"" << courseIt->getName() << "\":\n";
	std::cout << "+—————————————+——————————————————+—————————----------+\n";
	std::cout << "| ID студента |        Имя       |       Email       |\n";
	std::cout << "+—————————————+——————————————————+—————————----------+\n";

	for (const int studentID : studentIDs) {
		auto studentIt = std::find_if(students.begin(), students.end(), [studentID](const Student& s) {
			return s.getID() == studentID;
			});

		if (studentIt != students.end()) {
			std::cout << "| " << std::setw(11) << studentIt->getID()
				<< " | " << std::setw(16) << studentIt->getUserName()
				<< " | " << std::setw(17) << studentIt->getMail() << " |\n";
		}
	}
	std::cout << "+—————————————+——————————————————+—————————----------+\n";

	int studentID;
	std::cout << "\nВведите ID студента, которого хотите удалить: ";
	studentID = ValidationManager::numberControl();

	// Проверяем, записан ли студент на курс
	auto studentIDIt = std::find(studentIDs.begin(), studentIDs.end(), studentID);
	if (studentIDIt == studentIDs.end()) {
		system("cls");
		ValidationManager::consoleIO("Студент с указанным ID не найден на этом курсе.", LightRed);
		return;
	}

	// Удаляем студента из курса
	studentIDs.erase(studentIDIt);

	// Удаляем курс из списка курсов студента
	auto studentIt = std::find_if(students.begin(), students.end(), [studentID](const Student& s) {
		return s.getID() == studentID;
		});

	if (studentIt != students.end()) {
		studentIt->removeCourse(courseID);
		system("cls");
		ValidationManager::consoleIO("Студент успешно удалён с курса.", LightGreen);
	} else {
		system("cls");
		ValidationManager::consoleIO("Ошибка: студент с данным ID не найден в системе.", LightRed);
	}
}

void Teacher::editProfile() {
	int choice;

	do {
		// Меню изменения профиля
		std::cout << "        Изменение профиля          " << std::endl;
		std::cout << "1 - Изменить номер телефона " << std::endl;
		std::cout << "2 - Изменить электронную почту" << std::endl;
		std::cout << "3 - Изменить пароль" << std::endl;
		std::cout << "4 - Изменить ФИО" << std::endl;
		std::cout << "0 - Выйти" << std::endl;
		std::cout << "Выберите действие: ";
		choice = ValidationManager::numberControl();

		switch (choice) {
		case 1: { // Изменить номер телефона
			system("cls");
			std::cout << "Введите новый номер телефона: +375";
			std::string newPhone = ValidationManager::getPhone();
			this->setPhoneNumber(newPhone);
			system("cls");
			ValidationManager::consoleIO("Номер телефона успешно изменён!\n", LightGreen);
			break;
		}
		case 2: { // Изменить электронную почту
			system("cls");
			std::string newEmail;
			std::cout << "Введите новую электронную почту: ";
			std::cin >> newEmail;

			if (ValidationManager::isValidEmail(newEmail)) {
				this->setMail(newEmail);
				system("cls");
				ValidationManager::consoleIO("Электронная почта успешно изменена!\n", LightGreen);
			} else {
				system("cls");
				ValidationManager::consoleIO("Неверный формат электронной почты!\n", LightRed);
			}
			break;
		}
		case 3: { // Изменить пароль
			this->changePassword();
			break;
		}
		case 4: { // Изменить ФИО
			system("cls");
			std::string newFirstName, newMiddleName, newLastName;
			std::cout << "Введите новую фамилию: ";
			ValidationManager::setStringWithOutSpace(newLastName);
			std::cout << "Введите новое имя: ";
			ValidationManager::setStringWithOutSpace(newFirstName);
			std::cout << "Введите новое отчество: ";
			ValidationManager::setStringWithOutSpace(newMiddleName);
			

			this->setName(newFirstName);
			this->setMiddleName(newMiddleName);
			this->setSurname(newLastName);

			system("cls");
			ValidationManager::consoleIO("ФИО успешно изменено!\n", LightGreen);
			break;
		}
		case 0: { // Выйти
			system("cls");
			return;
		}
		default: {
			system("cls");
			ValidationManager::consoleIO("Некорректный выбор. Попробуйте ещё раз.\n", LightBlue);
			break;
		}
		}
	} while (choice != 0);
}


void Teacher::changePassword() {
	std::cout << "Введите старый пароль: ";
	std::string pass;
	pass = UserValidation::getPassword();
	if (UserValidation::hashPassword(pass + getSalt()) != getHashedPassword()) {
		system("cls");
		ValidationManager::consoleIO("Пароль не верен\n", LightRed);
		return;
	}
	std::cout << "Введите новый пароль: ";
	std::string newPass;
	newPass = UserValidation::getPassword();
	if (UserValidation::hashPassword(newPass + getSalt()) == getHashedPassword()) {
		system("cls");
		ValidationManager::consoleIO("Новый пароль не должен совподась со старым\n", Magenta);
		return;
	}
	setHashedPassword(UserValidation::hashPassword(newPass + getSalt()));
	system("cls");
	ValidationManager::consoleIO("Установлен новый пароль\n", LightGreen);
}