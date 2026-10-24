#include "Student.h"
#include "UserValidation.h"

// Конструктор по умолчанию
std::unordered_set<unsigned int> Student::IDs = {};

Student::Student():studentID(generateID()) {}

// Конструктор с параметрами
Student::Student(const std::string& uname, const std::string& pass, const std::string& slt, Acess& acesses,
	std::vector<int> coursesID, const std::string& phoneNumber, const std::string& mail)
	: Account(uname, pass, slt, acesses), coursesID(coursesID), phoneNumber(phoneNumber),
	mail(mail), studentID(generateID()) {
}

Student::Student(const std::string& uname, const std::string& hashedPass, const std::string& salt, Acess ac,
	int id, const std::vector<int>& courses, const std::string& phone, const std::string& mail,
	const std::vector<Request>& reqs)
	: Account(uname, hashedPass, salt, ac), studentID(id), coursesID(courses),
	phoneNumber(phone), mail(mail), requests(reqs) {
	IDs.insert(id);
}


// Геттеры

std::vector<int> Student::getCoursesID() const {
	return coursesID;
}

std::vector<int>& Student::getCoursesID() {
	return coursesID;
}

int Student::getID() const {
	return studentID;
}

std::string Student::getPhoneNumber() const {
	return phoneNumber;
}

std::string Student::getMail() const {
	return mail;
}

const std::vector<Request>& Student::getRequests() const { return requests; }
std::vector<Request>& Student::getRequests() { return requests; }

// Сеттеры

std::string Student::getUserName() const
{
	return userName;
}

void Student::setCoursesID(const std::vector<int>& coursesID) {
	this->coursesID = coursesID;
}

void Student::setPhoneNumber(std::string& phoneNumber) {
	this->phoneNumber = phoneNumber;
}

void Student::setMail(std::string& mail) {
	this->mail = mail;
}

bool Student::hasCourse(int courseID) const {
	return std::find(coursesID.begin(), coursesID.end(), courseID) != coursesID.end();
}

int Student::generateID() {
	int newID = 1000;
	do {
		++newID;   // Генерируем новый ID
	} while (IDs.find(newID) != IDs.end());
	IDs.insert(newID);
	return newID;
}

const std::vector<Certificate>& Student::getStudentCertificate() const {
	return studentCertificate;
}

std::vector<Certificate>& Student::getStudentCertificate() {
	return studentCertificate;
}

void Student::removeCourse(int courseID) {
	auto it = std::find(coursesID.begin(), coursesID.end(), courseID);
	if (it != coursesID.end()) {
		coursesID.erase(it);
	} 
}


// Открытие меню пользователя
void Student::openStudentMenu(std::vector<Course>& courses, std::vector<Certificate>& certificates, const std::vector<Student>& students) {
	std::cout << "Добро пожадовать " << this->userName << std::endl;
	std::cout << "Ваша почта: " << this->mail << std::endl;
	std::cout << "Ваш номер телефона: " << this->phoneNumber << std::endl;

	int choice;
	do {
		std::cout << "——————————————Меню студента————————————————" << std::endl;
		std::cout << "1. Просмотреть доступные курсы" << std::endl;
		std::cout << "2. Поиск курса" << std::endl;
		std::cout << "3. Фильтровать курсы по уровню" << std::endl;
		std::cout << "4. Записаться на курс" << std::endl;
		std::cout << "5. Просмотреть свои курсы" << std::endl;
		std::cout << "6. Просмотреть свои заявки" << std::endl;
		std::cout << "7. Просмотреть сертификаты" << std::endl;
		std::cout << "8. Просмотреть расписание" << std::endl;
		std::cout << "9. Редактировать профиль" << std::endl;
		std::cout << "0. Выйти из меню" << std::endl;
		std::cout << "Выберите действие: ";
		choice = ValidationManager::numberControl();

		switch (choice) {
		case 1:
			system("cls");
			viewAvailableCourses(courses);
			system("cls");
			break;
		case 2:
			system("cls");
			findCourse(courses);
			system("cls");
			break;
		case 3:
			system("cls");
			Admin::filterCoursesByLevel(courses);
			break;
		case 4:

			system("cls");
			enrollInCourse(requests, courses);
			FileService::writeStudentsToFile("students.txt", students);
			break;
		case 5:
			system("cls");
			viewMyCourses(courses);
			break;
		case 6:
			system("cls");
			viewRequests(courses);
			break;
		case 7:
			system("cls");
			viewCertificates();
			break;
		case 8:
			system("cls");
			viewSchedule(courses);
			break;
		case 9:
			system("cls");
			editProfile();
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

void Student::viewAvailableCourses(std::vector<Course>& courses) {
	bool ascending = true; // Флаг для чередования направления сортировки
	char choice;

	do {
		// Очистка экрана и отображение таблицы курсов
		system("cls");
		displayCourseTable(courses);

		// Меню сортировки
		std::cout << "\nВыберите сортировку:" << std::endl;
		std::cout << "1 - Сортировать по названию" << std::endl;
		std::cout << "2 - Сортировать по языку" << std::endl;
		std::cout << "3 - Сортировать по уровню языка" << std::endl;
		std::cout << "4 - Сортировать по количеству тем" << std::endl;
		std::cout << "5 - Сортировать по количеству занятых мест" << std::endl;
		std::cout << "ESC - Выход" << std::endl;


		// Ждем нажатие клавиши
		choice = _getch();

		// Если ESC (код 27), то выходим из цикла
		if (choice == 27) {
			break;
		}

		// Преобразуем выбор в индекс сортировки
		choice -= '0';

		// Выполняем сортировку, если выбор корректный
		if (choice >= 1 && choice <= 5) {
			sortCourses(courses, choice, ascending);
			ascending = !ascending; // Меняем направление сортировки
		}
	} while (true);
}

void Student::displayCourseTable(const std::vector<Course>& courses) const {
	std::cout << "+-------+------------------------+--------------------+----------------+------------+-------------+" << std::endl;
	std::cout << "|  ID   |        Название        |       Язык         |  Уровень языка | Кол-во тем | Занято мест |" << std::endl;
	std::cout << "+-------+------------------------+--------------------+----------------+------------+-------------+" << std::endl;

	for (const auto& course : courses) {
		if (!course.getIsCompleted()) {
			std::cout << "| " << std::setw(5) << course.getCourseID()
				<< " | " << std::setw(22) << course.getName()
				<< " | " << std::setw(18) << Course::languageToString(course.getLanguage())
				<< " | " << std::setw(14) << course.getLevel()
				<< " | " << std::setw(10) << course.getTotalTopics()
				<< " | " << std::setw(11) << std::to_string(course.getStudentsEnrolledID().size()) + " / " + std::to_string(course.getTotalStudents())
				<< " |" << std::endl;
		}
	}
	std::cout << "+-------+------------------------+--------------------+----------------+------------+-------------+" << std::endl;
}


void Student::sortCourses(std::vector<Course>& courses, int category, bool ascending) {
	switch (category) {
	case 1: // Сортировка по названию
		std::sort(courses.begin(), courses.end(), [ascending](const Course& a, const Course& b) {
			return ascending ? a.getName() < b.getName() : a.getName() > b.getName();
			});
		break;
	case 2: // Сортировка по языку
		std::sort(courses.begin(), courses.end(), [ascending](const Course& a, const Course& b) {
			return ascending ? Course::languageToString(a.getLanguage()) < Course::languageToString(b.getLanguage())
				: Course::languageToString(a.getLanguage()) > Course::languageToString(b.getLanguage());
			});
		break;
	case 3: // Сортировка по уровню языка
		std::sort(courses.begin(), courses.end(), [ascending](const Course& a, const Course& b) {
			return ascending ? a.getLevel() < b.getLevel() : a.getLevel() > b.getLevel();
			});
		break;
	case 4: // Сортировка по количеству тем
		std::sort(courses.begin(), courses.end(), [ascending](const Course& a, const Course& b) {
			return ascending ? a.getTotalTopics() < b.getTotalTopics() : a.getTotalTopics() > b.getTotalTopics();
			});
		break;
	case 5: // Сортировка по количеству занятых мест
		std::sort(courses.begin(), courses.end(), [ascending](const Course& a, const Course& b) {
			return ascending ? a.getStudentsEnrolledID().size() < b.getStudentsEnrolledID().size()
				: a.getStudentsEnrolledID().size() > b.getStudentsEnrolledID().size();
			});
		break;
	default:
		std::cout << "Некорректная категория для сортировки!" << std::endl;
		break;
	}
}

void Student::displayForFind(const std::vector<Course>& courses, std::string& str) {
	std::cout << "+-------+------------------------+--------------------+----------------+------------+-------------+" << std::endl;
	std::cout << "|  ID   |        Название        |       Язык         |  Уровень языка | Кол-во тем | Занято мест |" << std::endl;
	std::cout << "+-------+------------------------+--------------------+----------------+------------+-------------+" << std::endl;

	for (const auto& course : courses) {
		if (!course.getIsCompleted() && str == course.getName().substr(0, str.size())) {
			std::cout << "| " << std::setw(5) << course.getID()
				<< " | " << std::setw(22) << course.getName()
				<< " | " << std::setw(18) << Course::languageToString(course.getLanguage())
				<< " | " << std::setw(14) << course.getLevel()
				<< " | " << std::setw(10) << course.getTotalTopics()
				<< " | " << std::setw(11) << std::to_string(course.getStudentsEnrolledID().size()) + " / " + std::to_string(course.getTotalStudents())
				<< " |" << std::endl;
		}
	}
	std::cout << "+-------+------------------------+--------------------+----------------+------------+-------------+" << std::endl;
}

void Student::findCourse(const std::vector<Course>& courses) {
	std::cout << "Введите название: ";
	char ch;
	std::string str;
	const char BACKSPACE = 8;
	const char ENTER = 13;
	const char ESC = 27;
	while ((ch = _getch()) != ENTER || str.length() == 0) {
		if (ch == ESC) return;
		else if (ch == BACKSPACE) {
			if (str.length() != 0) {
				system("cls");
				str.pop_back();
				std::cout << "Введите название: " << str << std::endl << std::endl;
				std::cout << "\b \b";
				displayForFind(courses, str);

			}
		} else {
			if (ch == ' ' || ((ch >= '0' && ch <= '9') ||
				(ch >= 'A' && ch <= 'z') || ch == '!' || ch == '_' || ch == '-')) {
				system("cls");
				str.push_back(ch);
				std::cout <<"Введите название: " << str << std::endl << std::endl;

				displayForFind(courses, str);
			}
		}
	}
}

void Student::enrollInCourse(std::vector<Request>& requests, const std::vector<Course>& courses) {
	if (this->getCoursesID().size() >= 5) {
		ValidationManager::consoleIO("Вы можете записаться лишь на 5 курсов за раз", Magenta);
		return;
	}
	displayCourseTable(courses);
	std::cout << "Введите ID курса, на который хотите подать заявку: ";
	int courseID = ValidationManager::numberControl();

	auto it = std::find_if(courses.begin(), courses.end(), [courseID](const Course& course) {
		return course.getCourseID() == courseID;
		});

	for (int id : this->getCoursesID()) {
		if (id == courseID) {
			system("cls");	
			ValidationManager::consoleIO("Вы не можете записаться на один курс дважды", Red);
			return;
		}
	}

	if (it != courses.end()) {
		requests.emplace_back(courseID, RequestStatus::Expected);
		system("cls");
		ValidationManager::consoleIO("Заявка на курс успешно подана.", LightGreen);
	} else {
		system("cls");
		ValidationManager::consoleIO("Курс с таким ID не найден.", LightBlue);
	}
}


void Student::viewMyCourses(const std::vector<Course>& courses) const {
	std::cout << "————————————————————————————————————————————————————————————————————————————-—-———" << std::endl;
	std::cout << "| ID курса |         Название          |     Язык     |   Уровень   | Кол-во тем |" << std::endl;
	std::cout << "————————————————————————————————————————————————————————————————————————————-——-——" << std::endl;

	bool hasCourses = false;

	for (const auto& courseID : coursesID) {
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

void Student::viewSchedule(const std::vector<Course>& courses) const {
	if (coursesID.empty()) {
		ValidationManager::consoleIO("Вы ещё не записаны на курсы.\n", LightRed);
		return;
	}

	// Вывод списка курсов
	system("cls");
	viewMyCourses(courses);

	std::vector<const Course*> enrolledCourses; // Список курсов, на которые записан студент
	for (const auto& courseID : coursesID) {
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
	std::cout << "| Количество пройденных тем: " << selectedCourse->getCompletedTopics()<< '/' << selectedCourse->getTotalTopics() << std::endl;
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


void Student::viewRequests(const std::vector<Course>& courses) const {
	std::cout << "——————————————————————————————————————————————————————————————————————————————————————-----" << std::endl;
	std::cout << "| ID заявки | ID курса |        Статус         |         Название курса         | Уровень |" << std::endl;
	std::cout << "——————————————————————————————————————————————————————————————————————————————————————-----" << std::endl;

	if (requests.empty()) {
		std::cout << "|                                    У вас нет активных заявок.                       |" << std::endl;
	} else {
		for (const auto& request : requests) {
			auto it = std::find_if(courses.begin(), courses.end(), [&request](const Course& course) {
				return course.getCourseID() == request.getCourseID();
				});

			std::string courseName = "Не найден";
			std::string courseLevel = "—";

			if (it != courses.end()) {
				courseName = it->getName();
				courseLevel = it->getLevel();
			}

			std::cout << "| " << std::setw(9) << request.getRequestID()
				<< " | " << std::setw(8) << request.getCourseID()
				<< " | " << std::setw(21) << Request::statusToString(request.getStatus())
				<< " | " << std::setw(30) << courseName
				<< " | " << std::setw(7) << courseLevel << " |" << std::endl;
		}
	}

	std::cout << "——————————————————————————————————————————————————————————————————————————————————————-----" << std::endl;
}


void Student::viewCertificates() const {
	std::cout << "+------------------------+------------------------+--------------------+----------------+" << std::endl;
	std::cout << "|        Курс            |     Преподаватель      |        Язык        |     Уровень    |" << std::endl;
	std::cout << "+------------------------+------------------------+--------------------+----------------+" << std::endl;

	if (studentCertificate.empty()) {
		std::cout << "|                          У вас пока нет сертификатов.                                 |" << std::endl;
	} else {
		for (const auto& cert : studentCertificate) {
			std::cout << "| " << std::setw(22) << cert.getCourseName()
				<< " | " << std::setw(22) << cert.getTeacherName()
				<< " | " << std::setw(18) << Certificate::languageToString(cert.getLanguage())
				<< " | " << std::setw(14) << cert.getLanguageLevel()
				<< " |" << std::endl;
		}
	}

	std::cout << "+------------------------+------------------------+--------------------+----------------+" << std::endl;
}


void Student::editProfile() {
	int choice;

	do {
		// Меню изменения профиля
		std::cout << "        Изменение профиля          " << std::endl;
		std::cout << "1 - Изменить номер телефона " << std::endl;
		std::cout << "2 - Изменить электронную почту" << std::endl;
		std::cout << "3 - Изменить пароль" << std::endl;
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
			std::cin>>newEmail;
			
			
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

void Student::changePassword() {
	std::cout << "Введите старый пароль: ";
	std::string pass;
	pass = UserValidation::getPassword();
	if (UserValidation::hashPassword(pass + this->getSalt()) != this->getHashedPassword()) {
		system("cls");
		ValidationManager::consoleIO("Пароль не верен\n", LightRed);
		return;
	}
	std::cout << "Введите новый пароль: ";
	std::string newPass;
	newPass = UserValidation::getPassword();
	if (UserValidation::hashPassword(newPass + this->getSalt()) == this->getHashedPassword()) {
		system("cls");
		ValidationManager::consoleIO("Новый пароль не должен совподась со старым\n", Magenta);
		return;
	} else if (newPass.size() < 6) {
		ValidationManager::consoleIO("Пароль должен состоять из 6 и более символов\n", LightBlue);
		return;
	}
	this->setHashedPassword(UserValidation::hashPassword(newPass + this->getSalt()));
	system("cls");
	ValidationManager::consoleIO("Установлен новый пароль\n", LightGreen);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

int Request::nextID = 1000;

Request::Request(int reqId, const int courseId, RequestStatus stat)
	: requestId(nextID++), courseID(courseId), status(stat) {
}

Request::Request(const int courseId, RequestStatus stat) : requestId(nextID++), courseID(courseId), status(stat) {
}


int Request::getRequestID() const { return Request::requestId; }

int Request::getCourseID() const { return courseID; }

RequestStatus Request::getStatus() const { return status; }

void Request::setStatus(RequestStatus newStatus) { status = newStatus; }

std::string Request::statusToString(RequestStatus status) {
	switch (status) {
	case RequestStatus::Expected:
		return "Ожидает рассмотрения";
	case RequestStatus::Approved:
		return "Одобрено";
	case RequestStatus::Rejected:
		return "Отклонено";
	default:
		return "Неизвестный статус";
	}
}