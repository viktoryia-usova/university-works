#pragma once
#ifndef user_H
#define user_H
#include <iostream>
#include <vector>
#include <fstream>
#include <iomanip>
#include <algorithm>
#include "Course.h"
enum Acess {
	Blocked = -1, //заблокированный доступ.
	Pending = 0, //ожидающий доступ.
	Accepted = 1 //разрешенный доступ
};

class Student;
class Teacher;
class Certificate;

class Account { //базовый класс для всех учетных записей пользователей
protected:
	std::string userName;
	std::string hashedPassword;
	std::string salt;
	Acess access;
public:
	Account();
	Account(const std::string& uname, const std::string& pass, const std::string& slt, Acess& acesses);

	virtual std::string getUserName() const; //абстракция
	std::string getSalt() const; // соль для хеширования пароля
	std::string getHashedPassword() const;//хешированный пароль.
	Acess getUserAccess() const;
	void setUserAccess(Acess acesses);
	void setHashedPassword(const std::string& hashedPassword);
	std::string accessLevelToString(Acess access) const;
};

//Класс Admin наследует от класса Account
class Admin :public Account {
public:
	Admin();
	Admin(const std::string& uname, const std::string& pass, const std::string& slt, Acess& acesses);
	std::string getUserName() const override;
	void openAdminMenu(std::vector<Student>& students, std::vector<Teacher>& teachers, std::vector<Course>& courses, std::vector<Certificate>& certificates);
	void openCourseManagementMenu(std::vector<Course>& courses, std::vector<Teacher>& teachers, std::vector<Student>& students, std::vector<Certificate>& certificates);
	void viewCourseList(const std::vector<Course>& courses) const;
	void addCourse(std::vector<Course>& courses, std::vector<Teacher>& teachers, std::vector<Certificate>& certificates);
	static Schedule inputSchedule();
	void editCourse(std::vector<Course>& courses, std::vector<Teacher>& teachers, std::vector<Certificate>& certificates);
	void deleteCourse(std::vector<Course>& courses, std::vector<Teacher>& teachers, std::vector<Student>& students, std::vector<Certificate>& certificates);
	static void filterCoursesByLevel(const std::vector<Course>& courses);
	void processOrders(std::vector<Student>& students, std::vector<Course>& courses
	);
	template<class T>
	void changeAccessUser(std::vector<T>& students);
	void openUserAccountManagement(std::vector<Student>& students);
	void openUserAccountManagement(std::vector<Teacher>& users);
	template<class T>
	void displayUsers(const std::vector<T>& students);
	template<class T>
	void viewUsers(const std::vector<T>& users);

	void displayUserDetails(const Student& student) const;
	void displayUserDetails(const Teacher& teacher) const;
	template<class T>//шаблонные методы
	void grantSystemAccess(std::vector<T>& students);
	template<class T>
	void grantSystemAccessToAll(std::vector<T>& students);
	template<class T>
	void blockUser(std::vector<T>& students);
	template<class T>
	void deleteUser(std::vector<T>& students);

	
};

#endif