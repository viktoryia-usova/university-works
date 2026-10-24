#ifndef ORDER_C
#define ORDER_C
#include "Course.h"
#include <iostream>
#include <iomanip>


// Инициализация статической переменной для генерации ID
std::unordered_set<unsigned int> Course::IDs = {};

// Конструктор класса Course
Course::Course(const std::string& name, const std::string& level, int teacherID, int totalTopics, Language language, const Schedule& schedule, int totalStudent)
	: courseID(generateID()), name(name), level(level), teacherID(teacherID),language(language),
	totalTopics(totalTopics), completedTopics(0), isCompleted(false), totalStudents(totalStudent), schedule(schedule) {
}

Course::Course(const std::string& name, const std::string& level, int teacherID, int totalTopics,
	int completedTopics, bool isCompleted, int totalStudents,
	const std::vector<int>& studentsEnrolledID, Language language, const Schedule& schedule)
	: courseID(generateID()), name(name), level(level), teacherID(teacherID),
	totalTopics(totalTopics), completedTopics(completedTopics), isCompleted(isCompleted),
	totalStudents(totalStudents), studentsEnrolledID(studentsEnrolledID),
	language(language), schedule(schedule) {
}

Course::Course(const int& courseID,const std::string& name, const std::string& level, int teacherID, int totalTopics,
	int completedTopics, bool isCompleted, int totalStudents,
	const std::vector<int>& studentsEnrolledID, Language language, const Schedule& schedule)
	: courseID(courseID), name(name), level(level), teacherID(teacherID),
	totalTopics(totalTopics), completedTopics(completedTopics), isCompleted(isCompleted),
	totalStudents(totalStudents), studentsEnrolledID(studentsEnrolledID),
	language(language), schedule(schedule) {
	IDs.insert(courseID);
}


// Геттеры и сеттеры

// Получить ID курса
int Course::getCourseID() const {
	return courseID;
}

// Установить ID курса
void Course::setCourseID(int id) {
	courseID = id;
}

// Получить название курса
std::string Course::getName() const {
	return name;
}

// Установить название курса
void Course::setName(const std::string& name) {
	this->name = name;
}

// Получить язык курса
Language Course::getLanguage() const {
	return language;
}

// Установить язык курса
void Course::setLanguage(Language language) {
	this->language = language;
}

// Получить уровень курса
std::string Course::getLevel() const {
	return level;
}

// Установить уровень курса
void Course::setLevel(const std::string& level) {
	this->level = level;
}

// Получить ID преподавателя курса
int Course::getID() const {
	return teacherID;
}

// Установить ID преподавателя курса
void Course::setTeacherID(int teacherID) {
	this->teacherID = teacherID;
}

// Получить общее количество тем на курсе
int Course::getTotalTopics() const {
	return totalTopics;
}

// Установить общее количество тем
void Course::setTotalTopics(int totalTopics) {
	this->totalTopics = totalTopics;
}

// Получить количество завершенных тем
int Course::getCompletedTopics() const {
	return completedTopics;
}

// Установить количество завершенных тем
void Course::setCompletedTopics(int completedTopics) {
	this->completedTopics = completedTopics;
}

// Проверить, завершен ли курс
bool Course::getIsCompleted() const {
	return isCompleted;
}

// Установить статус завершенности курса
void Course::setIsCompleted(bool isCompleted) {
	this->isCompleted = isCompleted;
}

// Получить количество студентов на курсе
int Course::getTotalStudents() const {
	return totalStudents;
}

// Установить количество студентов
void Course::setTotalStudents(int totalStudents) {
	this->totalStudents = totalStudents;
}

// Получить список ID студентов, записанных на курс
std::vector<int> Course::getStudentsEnrolledID() const {
	return studentsEnrolledID;
}

std::vector<int>& Course::getStudentsEnrolledID() {
	return studentsEnrolledID;
}

// Добавить студента на курс
void Course::addStudent(int studentID) {
	studentsEnrolledID.push_back(studentID);
	totalStudents++;
}

const Schedule& Course::getSchedule() const { return schedule; }

void Course::setSchedule(const Schedule& schedule) {
	this->schedule = schedule;
}

// Удалить студента с курса
void Course::removeStudent(int studentID) {
	auto it = std::find(studentsEnrolledID.begin(), studentsEnrolledID.end(), studentID);
	if (it != studentsEnrolledID.end()) {
		studentsEnrolledID.erase(it);
		totalStudents--;
	}
}

// Метод для генерации уникального ID
int Course::generateID() {
	int newID = 1000;
	do {
		++newID;   // Генерируем новый ID
	} while (IDs.find(newID) != IDs.end());
	IDs.insert(newID);
	return newID;
}

std::string Course::languageToString(Language language) {
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


void Course::incrementCompletedTopics() {
	if (completedTopics < totalTopics) {
		++completedTopics;
	}
}

void Course::markAsCompleted() {
	isCompleted = true;
}

Schedule::Schedule() {
}

std::vector<DayOfWeek> Schedule::getDay() const {
   return days;
}

std::string Schedule::getStartTime() const {
	return start_time;
}

std::string Schedule::getEndTime() const {
	return end_time;
}

std::string Schedule::getRoom() const {
	return room;
}

void Schedule::displaySchedule() const {
}

std::string Schedule::getDayAsString(DayOfWeek day) {
	switch (day) {
	case DayOfWeek::Monday: return "Понедельник";
	case DayOfWeek::Tuesday: return "Вторник";
	case DayOfWeek::Wednesday: return "Среда";
	case DayOfWeek::Thursday: return "Четверг";
	case DayOfWeek::Friday: return "Пятница";
	case DayOfWeek::Saturday: return "Суббота";
	case DayOfWeek::Sunday: return "Воскресенье";
	default: return "Неизвестный день";
	}
}

#endif
