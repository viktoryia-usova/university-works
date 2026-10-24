#include "ClientInfo.h"
#include <fstream>
#include <string>
#include "Utils.h"

ClientInfo InputClientInfo(int accountId)
{
    ClientInfo clientInfo;

    clientInfo.id = FindClientInfoMaxId() + 1;
    clientInfo.accountId = accountId;

	cout << "Введите ваше ФИО:" << endl;
	clientInfo.credentials = InputCredentials();

	cout << "Введите номер паспорта" << endl;
	clientInfo.passport = InputPassport();

	cout << "Введите номер телефона" << endl;
	clientInfo.telephone = InputTelephone();

    return clientInfo;
}

int FindClientInfoMaxId()
{
    vector<ClientInfo> infos = LoadClientInfos();

    int maxId = 0;
    for (int i = 0; i < infos.size(); i++)
        if (maxId < infos[i].id)
            maxId = infos[i].id;

    return maxId;
}

vector<ClientInfo> LoadClientInfos()
{
	ifstream fin(FILE_CLIENT_INFO, ios_base::in);

	vector<ClientInfo> result;

	if (!fin.is_open())
	{
		return result;
	}

	int size = 0;
	fin >> size;

	for (int i = 0; i < size; ++i)
	{
		ClientInfo info;
		fin >> info.id;
		fin >> info.accountId;
		getline(fin, info.telephone);
		getline(fin, info.telephone);
		getline(fin, info.passport);
		getline(fin, info.credentials.firstname);
		getline(fin, info.credentials.secondname);
		getline(fin, info.credentials.lastname);

		result.push_back(info);
	}

	fin.close();

	return result;
}

void SaveClientInfos(vector<ClientInfo> infos)
{
	ofstream fout(FILE_CLIENT_INFO, ios_base::trunc);

	if (!fout.is_open())
	{
		return;
	}

	fout << infos.size() << endl;

	for (int i = 0; i < infos.size(); ++i)
	{
		fout << infos[i].id << endl;
		fout << infos[i].accountId << endl;
		fout << infos[i].telephone << endl;
		fout << infos[i].passport << endl;
		fout << infos[i].credentials.firstname << endl;
		fout << infos[i].credentials.secondname << endl;
		fout << infos[i].credentials.lastname << endl;
	}

	fout.close();
}

void PrintClientInfo(ClientInfo info)
{
	cout << "ФИО: " << CredentialsToStr(info.credentials, true) << endl;
	cout << "Телефон: " << "+375" << info.telephone << endl;
	cout << "Паспорт: " << info.passport;
}

void UpdateClientInfo(ClientInfo info)
{
	vector<ClientInfo> infos = LoadClientInfos();
	for (int i = 0; i < infos.size(); i++)
	{
		if (info.id == infos[i].id)
		{
			infos[i] = info;
			break;
		}
	}

	SaveClientInfos(infos);
}

ClientInfo FindClientInfoById(int id)
{
	ClientInfo result;

	vector<ClientInfo> infos = LoadClientInfos();
	for (int i = 0; i < infos.size(); i++)
	{
		if (id == infos[i].id)
		{
			result = infos[i];
			break;
		}
	}

    return result;
}

ClientInfo FindClientInfoByAccountId(int accountId)
{
	ClientInfo result;

	vector<ClientInfo> infos = LoadClientInfos();
	for (int i = 0; i < infos.size(); i++)
	{
		if (accountId == infos[i].accountId)
		{
			result = infos[i];
			break;
		}
	}

	return result;
}
