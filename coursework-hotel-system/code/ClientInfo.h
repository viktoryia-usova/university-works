#pragma once
#include <vector>
#include "Credentials.h"

#define FILE_CLIENT_INFO "client_infos.txt"

using namespace std;

struct ClientInfo
{
	int id;
	int accountId;
	Credentials credentials;
	string telephone;
	string passport;
};

ClientInfo InputClientInfo(int accountId);

int FindClientInfoMaxId();

vector<ClientInfo> LoadClientInfos();
void SaveClientInfos(vector<ClientInfo> infos);

void PrintClientInfo(ClientInfo info);

void UpdateClientInfo(ClientInfo info);

ClientInfo FindClientInfoById(int id);
ClientInfo FindClientInfoByAccountId(int accountId);