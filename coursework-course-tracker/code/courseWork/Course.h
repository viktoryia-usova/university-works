#ifndef COURSE_H
#define COURSE_H

#include <string>
#include <vector>
#include <unordered_set>

enum DayOfWeek {
	Monday,
	Tuesday,
	Wednesday,
	Thursday,
	Friday,
	Saturday,
	Sunday
};

class Schedule {
private:
	std::vector<DayOfWeek> days;// Дни недели
	std::string start_time;  // Время начала занятия
	std::string end_time;    // Время окончания занятия
	std::string room;        // Кабинет

public:
	// Конструктор
	Schedule();
	Schedule(std::vector<DayOfWeek> day, const std::string& start_time, const std::string& end_time, const std::string& room)
		: days(day), start_time(start_time), end_time(end_time), room(room) {
	}

	// Получить день недели
	std::vector<DayOfWeek> getDay() const;

	// Получить время начала занятия
	std::string getStartTime() const;

	// Получить время окончания занятия
	std::string getEndTime() const;

	// Получить кабинет
	std::string getRoom() const;

	// Отобразить расписание
	void displaySchedule() const;

	// Преобразование дня недели из enum в строку
	static std::string getDayAsString(DayOfWeek day);
};

enum Language {
	English,
	Spanish,
	French,
	German,
	Italian,
	Chinese,
	Japanese,
	Russian,
	Arabic,
	Portuguese
};
class Course {
private:
	int courseID;
	std::string name;
	std::string level; // от A1 - C2
	int teacherID;
	int totalTopics;
	int completedTopics;
	bool isCompleted;
	int totalStudents;
	//std::vector<std::string> nameTopic;
	std::vector<int> studentsEnrolledID; // Вектор ID студентов, записанных на курс
	Language language;
	Schedule schedule;
	// Статическая переменная для генерации уникальных ID
	static std::unordered_set<unsigned int> IDs;


public:
	// Конструктор
	Course(const std::string& name, const std::string& level, int teacherID, int totalTopics, Language language, const Schedule& schedule, int totalStudent);

	Course(const std::string& name, const std::string& level, int teacherID, int totalTopics,
		int completedTopics, bool isCompleted, int totalStudents, const std::vector<int>& studentsEnrolledID,
		Language language, const Schedule& schedule);

	Course(const int& courseID, const std::string& name, const std::string& level, int teacherID, int totalTopics,
		int completedTopics, bool isCompleted, int totalStudents, const std::vector<int>& studentsEnrolledID,
		Language language, const Schedule& schedule);

	// Геттеры и сеттеры
	int getCourseID() const;
	void setCourseID(int id);

	std::string getName() const;
	void setName(const std::string& name);

	Language getLanguage() const;
	void setLanguage(Language language);

	std::string getLevel() const;
	void setLevel(const std::string& level);

	int getID() const;
	void setTeacherID(int teacherID);

	int getTotalTopics() const;
	void setTotalTopics(int totalTopics);

	int getCompletedTopics() const;
	void setCompletedTopics(int completedTopics);

	bool getIsCompleted() const;
	void setIsCompleted(bool isCompleted);

	int getTotalStudents() const;
	void setTotalStudents(int totalStudents);

	std::vector<int> getStudentsEnrolledID() const;

	std::vector<int>& getStudentsEnrolledID();

	const Schedule& getSchedule() const;
	void setSchedule(const Schedule& schedule);

	void addStudent(int studentID);
	void removeStudent(int studentID);

	// Метод для генерации уникального ID
	static int generateID();
	static std::string languageToString(Language language);
	void incrementCompletedTopics();
	void markAsCompleted();
};

#endif // ORDER_H