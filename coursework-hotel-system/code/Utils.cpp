#include "Utils.h"
#include <iostream>
#include <iomanip>
#include <Windows.h>
#include <conio.h>
#include "Account.h"

string InputLine()
{
	string result;
	do
	{
		getline(cin, result);

	} while (result.empty());

	return result;
}

string InputStr()
{
	string result;
	do
	{
		result = InputLine();
		if (result.find(' ') != -1)
		{
			std::cout << "Значение не должно содержать пробелы" << std::endl;
		}
	} while (result.find(' ') != -1);

	return result;
}

string InputPassword()
{
	string password;

	do {
		char input;
		do
		{
			input = _getch();
			if (input == '\b' && !password.empty())
			{
				cout << "\b \b";
				password = password.substr(0, password.length() - 1);
			}
			else if (input != '\b' && input != '\r')
			{
				password += input;
				cout << "*";
			}
		} while (input != '\r');

		cout << endl;

		if (password.length() < 4 || password.length() > 16)
			cout << "Пароль должен содержать от 4 до 16 символов" << endl;
	} while (password.length() < 4 || password.length() > 16);

	return password;
}

string InputTelephone()
{
	string telephone;
	
	bool isCorrect;
	do
	{
		isCorrect = true;
		cout << "+375";
		telephone = InputStr();

		if (telephone.length() != 9)
		{
			isCorrect = false;
			std::cout << "Некорректный ввод" << std::endl;
			continue;
		}

		for (int i = 0; i < telephone.length(); ++i)
		{
			if (telephone[i] >= '0' && telephone[i] <= '9')
				continue;

			isCorrect = false;
			break;
		}

		if (!isCorrect)
		{
			std::cout << "Некорректный ввод" << std::endl;
		}
	} while (!isCorrect);

	return telephone;
}

string InputPassport()
{
	string avaliableChars = "ABCEHKMOPTXАВСЕНКМОРТХ";
	string passport;

	bool isCorrect;
	do
	{
		isCorrect = true;
		passport = InputStr();

		if (passport.length() != 9)
		{
			cout << "Некоррентный ввод! Номер паспорта должен быть в формате АВ1234567" << endl;
			isCorrect = false;
			continue;
		}

		passport = Uppercase(passport);

		for (int i = 0; i < passport.length(); ++i)
		{
			if ((passport[i] >= '0' && passport[i] <= '9') && (i >= 2))
				continue;

			if (i <= 1)
			{
				bool isFound = false;
				for (int j = 0; j < avaliableChars.length(); ++j)
				{
					if (passport[i] == avaliableChars[j])
					{
						isFound = true;
						break;
					}
				}

				if (isFound)
					continue;
			}

			isCorrect = false;
			break;
		}

		if (!isCorrect)
		{
			cout << "Некоррентный ввод! Номер паспорта должен быть в формате АВ1234567" << endl;
		}

	} while (!isCorrect);

	return passport;
}

int InputInt()
{
	string str;
	bool isCorrect;
	do
	{
		str = InputStr();

		if (str.length() == 1 && (str[0] == '-' || str[0] == '+'))
		{
			std::cout << "Некорректный ввод" << std::endl;
			continue;
		}

		isCorrect = true;
		for (int i = 0; i < str.length(); ++i)
		{
			if ((str[i] == '-' || str[i] == '+') && i == 0)
				continue;

			if (str[i] >= '0' && str[i] <= '9')
				continue;

			isCorrect = false;
			break;
		}

		if (!isCorrect)
		{
			std::cout << "Некорректный ввод" << std::endl;
		}

	} while (!isCorrect);

	return stoi(str);
}

int InputInt(int min, int max)
{
	int value;
	do
	{
		value = InputInt();
		if (value < min || value > max)
		{
			std::cout << "Некорректный ввод" << std::endl;
		}
	} while (value < min || value > max);
	return value;
}

double InputDouble()
{
	std::string str;

	bool isCorrect;
	do
	{
		isCorrect = true;

		str = InputStr();

		int countOfDots = 0;
		for (int i = 0; i < str.length(); ++i)
		{
			if (str[i] >= '0' && str[i] <= '9')
				continue;

			if (str[i] == '-' && i == 0)
				continue;

			if (str[i] == '.' && countOfDots == 0)
			{
				countOfDots++;
				continue;
			}

			isCorrect = false;
			break;
		}

		if (!isCorrect)
		{
			std::cout << "Некорректный ввод" << std::endl;
		}

	} while (!isCorrect);

	return std::stof(str);
}

double InputDouble(double min, double max)
{
	double value;
	do
	{
		value = InputDouble();
		if (value < min || value > max)
		{
			std::cout << "Некорректный ввод" << std::endl;
		}
	} while (value < min || value > max);
	return value;
}

bool Confirmation(string question)
{
	cout << question << "(1 - Да, 0 - Нет)" << endl;
	return InputInt(0,1) == 1;
}

void Initialize()
{
	SetConsoleCP(1251);
	SetConsoleOutputCP(1251);

	cout << setprecision(2) << fixed;

	InitAccounts();
}

string Encrypt(string str)
{
	for (int i = 0; i < str.length(); ++i)
		str[i] += 5;

	return str;
}

string Decrypt(string str)
{
	for (int i = 0; i < str.length(); ++i)
		str[i] -= 5;

	return str;
}

string Uppercase(string str)
{
	for (int i = 0; i < str.length(); ++i)
		str[i] = Uppercase(str[i]);

	return str;
}

string Lowercase(string str)
{
	for (int i = 0; i < str.length(); ++i)
		str[i] = Lowercase(str[i]);

	return str;
}

char Uppercase(char ch)
{
	if (ch >= 'a' && ch <= 'z')
		ch += 'A' - 'a';

	if (ch >= 'а' && ch <= 'я')
		ch += 'А' - 'а';

	return ch;
}

char Lowercase(char ch)
{
	if (ch >= 'A' && ch <= 'Z')
		ch += 'a' - 'A';

	if (ch >= 'А' && ch <= 'Я')
		ch += 'а' - 'А';

	return ch;
}
