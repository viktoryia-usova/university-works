#pragma once //используются для предотвращения многократного включения заголовочного файла.
#ifndef TEACHER_H
#define TEACHER_H

#include "user.h"
#include <string>
#include <unordered_set>

//определен класс Teacher, который наследуется от класса Account.
class Teacher : public Account {
private:
	int teacherID;
	std::string name;
	std::string surname;
	std::string middleName;
	std::string phoneNumber;
	std::string mail;
	std::vector<int> courseIDs;
	Language language;

	static std::unordered_set<unsigned int> IDs;
	// Статическая переменная для генерации уникальных ID
public:
	// Конструктор
	Teacher(const std::string& name, const std::string& surname, const std::string& middleName,
		const std::string& phoneNumber, const std::string& mail);

	Teacher(const std::string& userName, const std::string& hashedPassword, const std::string& salt,
		Acess access, int teacherID, const std::string& name, const std::string& surname,
		const std::string& middleName, const std::string& phoneNumber, const std::string& mail, const std::vector<int>& courseIDs, Language language);

	Teacher(const std::string& userName, const std::string& hashedPassword, const std::string& salt,
		Acess access, const std::vector<int>& courseIDs, const std::string& name, const std::string& surname,
		const std::string& middleName, const std::string& phoneNumber, const std::string& mail, Language language);

	// Геттеры и сеттеры
	int getID() const;
	void setTeacherID(int id);

	std::string getUserName() const override; 

	std::string getName() const;
	void setName(const std::string& name);

	std::string getSurname() const;
	void setSurname(const std::string& surname);

	std::string getMiddleName() const;
	void setMiddleName(const std::string& middleName);

	std::string getPhoneNumber() const;
	void setPhoneNumber(const std::string& phoneNumber);

	std::string getMail() const;
	void setMail(const std::string& mail);

	Language getLanguage() const;

	std::vector<int> getCoursesID() const;

	std::string getFullName();

	void addCourse(int id);

	bool hasCourse(int courseID) const;

	static int generateID();

	void removeCourse(int courseID);

	void openTeacherMenu(std::vector<Student>& students, std::vector<Course>& courses, std::vector<Certificate>& certificates, std::vector<Teacher>& teachers);
	void viewMyCourses(const std::vector<Course>& courses) const;
	void viewStudentsForCourses(const std::vector<Student>& students, const std::vector<Course>& courses);
	void editCourseSchedule(std::vector<Course>& courses);
	void conductLesson(std::vector<Course>& courses);
	void issueCertificatesForCourse(std::vector<Course>& courses, std::vector<Certificate>& certificates, std::vector<Student>& students);
	void viewSchedule(const std::vector<Course>& courses) const;
	void removeStudentFromCourse(std::vector<Student>& students, std::vector<Course>& courses);

	void editProfile();




	void changePassword();
};

#endif
