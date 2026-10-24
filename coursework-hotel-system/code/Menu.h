#pragma once
#include "Account.h"

void Menu(Account account);
void MenuAdmin(Account account);
void MenuClient(Account account);

Account Authorization();
Account Registration();

void UserManagement();
void RoomTypeManagement();
void RoomManagement();
void OrderManagement();
void RecordManagement();
void HistoryManagement();

void ManageProfile(Account account);
void ViewAvaliableNumbers();
void CreateOrder(Account account);
void ViewClientOrders(Account account);
void ViewClientRecords(Account account);
void ViewClientHistory(Account account);