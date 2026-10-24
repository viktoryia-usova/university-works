#pragma once
#include <iostream>

using namespace std;

struct Credentials
{
	string firstname;
	string secondname;
	string lastname;
};

Credentials InputCredentials();

string CredentialsToStr(Credentials credentials, bool isFull);
