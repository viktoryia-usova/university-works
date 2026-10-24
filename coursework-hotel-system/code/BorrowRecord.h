#pragma once
#include <iostream>
#include <vector>
#include "Date.h"

using namespace std;

#define FILE_BORROW_RECORD "borrow_records.txt"

#define STATE_DEFAULT   0
#define STATE_CANCELLED 1

struct BorrowRecord
{
	int roomId;
	int clientInfoId;
	Date start;
	Date end;
	int state;
};

bool IsActive(BorrowRecord record);

vector<BorrowRecord> LoadBorrowRecords();
void SaveBorrowRecords(vector<BorrowRecord> records);

void PrintBorrowRecords(vector<BorrowRecord> records);

vector<BorrowRecord> FindBorrowRecordByRoomId(int roomId);
vector<BorrowRecord> FindBorrowRecordByRoomId(int roomId, bool isActive);

