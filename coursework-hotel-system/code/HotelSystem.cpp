#include <iostream>
#include "Menu.h"
#include "Utils.h"
using namespace std;

int main()
{
	Initialize();

	bool work = true;
	while (work)
	{
		system("cls");
		cout << "Введите:" << endl;
		cout << "1.Вход" << endl;
		cout << "2.Регистрация" << endl;
		cout << "3.Выход" << endl;

		switch (InputInt(1, 3))
		{
		case 1:
		{
			Menu(Authorization());
			break;
		}
		case 2:
		{
			Menu(Registration());
			break;
		}
		case 3:
		{
			work = false;
			break;
		}
		}
	}

	return 0;
}