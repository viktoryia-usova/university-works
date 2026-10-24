#include "BorrowRecord.h"
#include "Room.h"
#include "ClientInfo.h"
#include <fstream>
#include <string>
#include <iomanip>

bool IsActive(BorrowRecord record)
{
    return record.state == STATE_DEFAULT && CompareDate(record.end, Today()) >= 0;
}

vector<BorrowRecord> LoadBorrowRecords()
{
    ifstream fin(FILE_BORROW_RECORD, ios_base::in);

    vector<BorrowRecord> result;

    if (!fin.is_open())
    {
        return result;
    }

    int size = 0;
    fin >> size;

    for (int i = 0; i < size; ++i)
    {
        BorrowRecord record;
        fin >> record.roomId;
        fin >> record.clientInfoId;
        fin >> record.state;
        fin >> record.start.day;
        fin >> record.start.month;
        fin >> record.start.year;
        fin >> record.end.day;
        fin >> record.end.month;
        fin >> record.end.year;

        result.push_back(record);
    }

    fin.close();

    return result;
}

void SaveBorrowRecords(vector<BorrowRecord> records)
{
    ofstream fout(FILE_BORROW_RECORD, ios_base::trunc);

    if (!fout.is_open())
    {
        return;
    }

    fout << records.size() << endl;

    for (int i = 0; i < records.size(); ++i)
    {
        fout << records[i].roomId << endl;
        fout << records[i].clientInfoId << endl;
        fout << records[i].state << endl;
        fout << records[i].start.day << endl;
        fout << records[i].start.month << endl;
        fout << records[i].start.year << endl;
        fout << records[i].end.day << endl;
        fout << records[i].end.month << endl;
        fout << records[i].end.year << endl;
    }

    fout.close();
}

void PrintBorrowRecords(vector<BorrowRecord> records)
{
    if (records.empty())
    {
        cout << "Список пуст" << endl;
        return;
    }

    cout << "**********************************************************************************" << endl;
    cout << "|  №  |     Номер     |       Клиент       |Дата начала|Дата окончания|  Статус  |" << endl;
    cout << "**********************************************************************************" << endl;

    for (int i = 0; i < records.size(); i++)
    {
        string state = " Завершен ";

        if (IsActive(records[i]))
            state = " Активен  ";
        else if (records[i].state == STATE_CANCELLED)
            state = " Отменен  ";

        cout << "|";
        cout << setw(5) << i + 1;
        cout << "|";
        cout << setw(15) << FindRoomById(records[i].roomId).number;
        cout << "|";
        cout << setw(20) << CredentialsToStr(FindClientInfoById(records[i].clientInfoId).credentials, false);
        cout << "|";
        cout << setw(11) << DateToString(records[i].start);
        cout << "|";
        cout << setw(14) << DateToString(records[i].end);
        cout << "|";
        cout << setw(10) << state;
        cout << "|";
        cout << endl;

        cout << "**********************************************************************************" << endl;
    }
}

vector<BorrowRecord> FindBorrowRecordByRoomId(int roomId)
{
    vector<BorrowRecord> result;

    vector<BorrowRecord> records = LoadBorrowRecords();
    for (int i = 0; i < records.size(); i++)
    {
        if (roomId == records[i].roomId)
        {
            result.push_back(records[i]);
        }
    }

    return result;
}

vector<BorrowRecord> FindBorrowRecordByRoomId(int roomId, bool isActive)
{
    vector<BorrowRecord> result;

    vector<BorrowRecord> records = FindBorrowRecordByRoomId(roomId);
    for (int i = 0; i < records.size(); i++)
    {
        if(IsActive(records[i]) == isActive)
        {
            result.push_back(records[i]);
        }
    }
	return result;
}
