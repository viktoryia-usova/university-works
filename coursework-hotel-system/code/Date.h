#pragma once
#include <iostream>

using namespace std;

struct Date
{
	int day;
	int month;
	int year;
};

Date Today();

Date InputDate();
void PrintDate(Date date);

string DateToString(Date date);

int CompareDate(Date date1, Date date2);

