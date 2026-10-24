#pragma once
#include <iostream>
#include <vector>
using namespace std;

#define ROLE_UNDEFINED 0
#define ROLE_ADMIN	   1
#define ROLE_CLIENT    2

#define FILE_ACCOUNT "accounts.txt"

struct Account 
{
	int id;
	string username;
	string password;
	int role;
	bool isActive;
};

void InitAccounts();

Account InputAccount(int role, bool isActive);
string InputUsername();

int FindAccountMaxId();

vector<Account> LoadAccounts();
void SaveAccounts(vector<Account> accounts);

void PrintAccounts(vector<Account> accounts);

Account FindAccountById(int id);