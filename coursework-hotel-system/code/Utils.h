#pragma once
#include <string>

using namespace std;

string InputLine();
string InputStr();
string InputPassword();
string InputTelephone();
string InputPassport();

int InputInt();
int InputInt(int min, int max);

double InputDouble();
double InputDouble(double min, double max);

bool Confirmation(string question);

void Initialize();

string Encrypt(string str);
string Decrypt(string str);

string Uppercase(string str);
string Lowercase(string str);

char Uppercase(char ch);
char Lowercase(char ch);