#include "Credentials.h"
#include "Utils.h"

Credentials InputCredentials()
{
	Credentials credentials;

	cout << "¬ведите фамилию:" << endl;
	credentials.firstname = InputStr();

	cout << "¬ведите им€:" << endl;
	credentials.secondname = InputStr();

	cout << "¬ведите отчество:" << endl;
	credentials.lastname = InputStr();

	return credentials;
}

string CredentialsToStr(Credentials credentials, bool isFull)
{
	string result = credentials.firstname;
	result += " ";

	if (isFull)
	{
		result += credentials.secondname;
		result += " ";
		result += credentials.lastname;
	}
	else
	{
		result += credentials.secondname[0];
		result += +".";
		result += credentials.lastname[0];
		result += ".";
	}
	

	return result;
}
