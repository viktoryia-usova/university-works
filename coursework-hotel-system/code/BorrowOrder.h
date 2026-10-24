#pragma once
#include <iostream>
#include <vector>
#include "Date.h"

using namespace std;

#define FILE_BORROW_ORDER "borrow_orders.txt"

struct BorrowOrder
{
	int roomTypeId;
	int clientInfoId;
	string comment;
	Date ordered;
	Date start;
	Date end;
};

BorrowOrder InputBorrowOrder(int roomTypeId, int clientInfoId);

vector<BorrowOrder> LoadBorrowOrders();
void SaveBorrowOrders(vector<BorrowOrder> orders);

vector<BorrowOrder> LoadCancelledBorrowOrders(string username);
void SaveCancelledBorrowOrders(vector<BorrowOrder> orders, string username);
void CancelOrder(BorrowOrder order);

void PrintCancelledBorrowOrder(BorrowOrder order);
void PrintBorrowOrders(vector<BorrowOrder> orders);