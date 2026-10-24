#include "Account.h"
#include "ClientInfo.h"
#include "Utils.h"
#include <fstream>
#include <iomanip>

void InitAccounts()
{
	vector<Account> accounts = LoadAccounts();

	if (!accounts.empty())
		return;

	cout << "Это первый вход. Зарегистрируйте администратора:" << endl;
	accounts.push_back(InputAccount(ROLE_ADMIN, true));

	SaveAccounts(accounts);
}

Account InputAccount(int role, bool isActive)
{
	Account account;
	account.role = role;
	account.isActive = isActive;

	cout << "Введите логин:" << endl;
	account.username = InputUsername();

	cout << "Введите пароль:" << endl;
	account.password = InputPassword();

	account.id = FindAccountMaxId() + 1;

	return account;
}

string InputUsername()
{
	vector<Account> accounts = LoadAccounts();

	string username;

	bool isCorrect = false;
	while (!isCorrect)
	{
		username = InputStr();
		isCorrect = true;

		for (int i = 0; i < accounts.size(); ++i)
		{
			if (username == accounts[i].username)
			{
				cout << "Данный логин уже занят" << endl;
				isCorrect = false;
				break;
			}
		}
	}

	return username;
}

int FindAccountMaxId()
{
	vector<Account> accounts = LoadAccounts();

	int maxId = 0;
	for (int i = 0; i < accounts.size(); i++)
		if (maxId < accounts[i].id)
			maxId = accounts[i].id;

	return maxId;
}

vector<Account> LoadAccounts()
{
	ifstream fin(FILE_ACCOUNT, ios_base::in);

	vector<Account> result;

	if (!fin.is_open())
	{
		return result;
	}

	int size = 0;
	fin >> size;

	for (int i = 0; i < size; ++i)
	{
		Account account;
		int temp = 0;
		fin >> account.id;
		fin >> account.username;
		fin >> account.password;
		fin >> account.role;
		fin >> temp;

		account.isActive = temp == 1;
		account.password = Decrypt(account.password);

		result.push_back(account);
	}

	fin.close();

	return result;
}

void SaveAccounts(vector<Account> accounts)
{
	ofstream fout(FILE_ACCOUNT, ios_base::trunc);

	if (!fout.is_open())
	{
		return;
	}

	fout << accounts.size() << endl;

	for (int i = 0; i < accounts.size(); ++i)
	{
		fout << accounts[i].id << endl;
		fout << accounts[i].username << endl;
		fout << Encrypt(accounts[i].password) << endl;
		fout << accounts[i].role << endl;
		fout << (accounts[i].isActive ? 1 : 0) << endl;
	}

	fout.close();
}

void PrintAccounts(vector<Account> accounts)
{
	if (accounts.empty())
	{
		cout << "Список пуст" << endl;
		return;
	}

	cout << "*********************************************************************************" << endl;
	cout << "|  №  |        Логин        |         ФИО         |   Телефон   | Паспорт | Бан |" << endl;
	cout << "*********************************************************************************" << endl;

	for (int i = 0; i < accounts.size(); i++)
	{
		ClientInfo clientInfo = FindClientInfoByAccountId(accounts[i].id);

		cout << "|";
		cout << setw(4) << i + 1 << ".";
		cout << "|";
		cout << setw(21) << accounts[i].username;
		cout << "|";
		cout << setw(21) << CredentialsToStr(clientInfo.credentials, false);
		cout << "|";
		cout << "+375" << clientInfo.telephone;
		cout << "|";
		cout << clientInfo.passport;
		cout << "|";
		cout << (accounts[i].isActive ? "     " : " Бан ");
		cout << "|";
		cout << endl;
		cout << "*********************************************************************************" << endl;
	}
}

Account FindAccountById(int id)
{
	Account result;

	vector<Account> accounts = LoadAccounts();
	for (int i = 0; i < accounts.size(); i++)
	{
		if (accounts[i].id == id)
		{
			result = accounts[i];
			break;
		}
	}
	return result;
}
