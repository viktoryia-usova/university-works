#pragma once
#include "User.h"
#include "Certificate.h"

enum RequestStatus {
	Rejected = -1,//(Отклонен) — значение -1
	Expected, //(Ожидается) — значение 0
	Approved //(Одобрен) — значение 1.
};

class Request;

//Класс Student наследует от класса Account
class Student:public Account {
private:
	int studentID;
	std::vector<int> coursesID;
	std::string phoneNumber;
	std::string mail;
	std::vector<Request> requests;
	std::vector<Certificate> studentCertificate;

	static std::unordered_set<unsigned int> IDs;

public:
	Student();
	Student(const std::string& uname, const std::string& pass, const std::string& slt, Acess& acesses, std::vector<int> coursesID, const std::string& phoneNumber,const std::string& mail);
	Student(const std::string& uname, const std::string& hashedPass, const std::string& salt, Acess ac, int id, const std::vector<int>& courses, const std::string& phone, const std::string& mail, const std::vector<Request>& reqs);
	// Геттеры
	std::vector<int> getCoursesID() const;
	std::vector<int>& getCoursesID();
	int getID()const;
	std::string getPhoneNumber() const;
	std::string getMail() const;
	const std::vector<Request>& getRequests() const;
	std::vector<Request>& getRequests();
	// Сеттеры
	std::string getUserName() const override;
	void setCoursesID(const std::vector<int>& coursesID);
	void setPhoneNumber(std::string& phoneNumber);
	void setMail(std::string& mail);

	bool hasCourse(int courseID) const;

	int generateID();

	const std::vector<Certificate>& getStudentCertificate()const;

	std::vector<Certificate>& getStudentCertificate();

	void removeCourse(int courseID);


	void openStudentMenu(std::vector<Course>& courses, std::vector<Certificate>& certificates, const std::vector<Student>& students);
	void changePassword();
	void displayCourseTable(const std::vector<Course>& courses) const; // Таблица с форматированием
	void sortCourses(std::vector<Course>& courses, int category, bool ascending); // Сортировка курсов
	void displayForFind(const std::vector<Course>& courses, std::string& str);
	void viewAvailableCourses(std::vector<Course>& courses); // Просмотр курсов с сортировкой

	void findCourse(const std::vector<Course>& courses);

	void enrollInCourse(std::vector<Request>& requests, const std::vector<Course>& courses);
	void viewMyCourses(const std::vector<Course>& courses) const;
	void viewSchedule(const std::vector<Course>& courses) const;
	void viewRequests(const std::vector<Course>& courses) const;
	void viewCertificates() const;
	void editProfile();

};

//Класс Request представляет запрос студента на курс
class Request {
private:
	int requestId;
	int courseID;      
	RequestStatus status;

	static int nextID;
  
public:

	Request(int reqId, const int courseId, RequestStatus stat);
	Request(const int courseId, RequestStatus stat);

	RequestStatus getStatus() const;
	int getRequestID() const;
	int getCourseID() const;

	void setStatus(RequestStatus newStatus);

	static std::string statusToString(RequestStatus status);


};

