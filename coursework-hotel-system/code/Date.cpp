#define _CRT_SECURE_NO_WARNINGS

#include "Date.h"
#include "Utils.h"
#include <ctime>

Date Today()
{
    time_t tim = time(0);
    tm* now = localtime(&tim);

    Date date;
    date.day = now->tm_mday;
    date.month = now->tm_mon + 1;
    date.year = now->tm_year + 1900;

    return date;
}

Date InputDate()
{
    Date date;

    bool isCorrect;
    do
    {
        isCorrect = true;
        string str = InputStr();

        if (str.length() != 10)
        {
            isCorrect = false;
            cout << "Некорректный ввод" << endl;
            continue;
        }

        for (int i = 0; i < str.length(); ++i)
        {
            if (str[i] == '.' && (i == 2 || i == 5))
                continue;

            if ((str[i] >= '0' && str[i] <= '9') && (i != 2 && i != 5))
                continue;

            isCorrect = false;
            break;
        }

        if (!isCorrect)
        {
            cout << "Некорректный ввод" << endl;
            continue;
        }

        date.day = stoi(str.substr(0, 2));
        date.month = stoi(str.substr(3, 2));
        date.year = stoi(str.substr(6, 4));

        if (date.day < 1 || date.day > 31)
        {
            isCorrect = false;
            cout << "Некорректный ввод" << endl;
            continue;
        }

        if (date.month < 1 || date.month > 12)
        {
            isCorrect = false;
            cout << "Некорректный ввод" << endl;
            continue;
        }

        if (date.year < 0)
        {
            isCorrect = false;
            cout << "Некорректный ввод" << endl;
            continue;
        }

        if (date.day > 30 && (date.month == 4 || date.month == 6 || date.month == 9 || date.month == 11))
        {
            isCorrect = false;
            cout << "Некорректный ввод" << endl;
            continue;
        }

        if (date.day > 29 && date.month == 2)
        {
            isCorrect = false;
            cout << "Некорректный ввод" << endl;
            continue;
        }

        if (date.day > 28 && date.month == 2 && (date.year % 4 != 0 || date.year % 100 == 0))
        {
            isCorrect = false;
            cout << "Некорректный ввод" << endl;
            continue;
        }

    } while (!isCorrect);

    return date;
}

void PrintDate(Date date)
{
    cout << DateToString(date);
}

string DateToString(Date date)
{
    string result = "";

    result += (date.day < 10 ? "0" : "") + to_string(date.day);
    result += ".";
    result += (date.month < 10 ? "0" : "") + to_string(date.month);
    result += ".";
    result += to_string(date.year);

    return result;
}

int CompareDate(Date date1, Date date2)
{
    if (date1.year == date2.year)
    {
        if (date1.month == date2.month)
        {
            if (date1.day == date2.day)
                return 0;
            else 
                return date1.day > date2.day ? 1 : -1;
        }
        else
            return date1.month > date2.month ? 1 : -1;
    }
    else
        return date1.year > date2.year ? 1 : -1;
}
