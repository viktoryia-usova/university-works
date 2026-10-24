#include "BorrowOrder.h"
#include "Utils.h"
#include "Account.h"
#include "ClientInfo.h"
#include "RoomType.h"
#include <fstream>
#include <string>
#include <iomanip>

BorrowOrder InputBorrowOrder(int roomTypeId, int clientInfoId)
{
    BorrowOrder borrowOrder;
    borrowOrder.roomTypeId = roomTypeId;
    borrowOrder.clientInfoId = clientInfoId;

    borrowOrder.ordered = Today();

    cout << "Введите дату начала брони:";
    do
    {
        borrowOrder.start = InputDate();

        if (CompareDate(borrowOrder.start, Today()) == -1)
            cout << "Дата началa должна быть не раньше сегодняшней" << endl;
    } while (CompareDate(borrowOrder.start, Today()) == -1);

    cout << "Введите дату окончания брони:";
    do
    {
        borrowOrder.end = InputDate();

        if (CompareDate(borrowOrder.end, borrowOrder.start) <= 0)
            cout << "Дата окончания должна быть не раньше даты начала" << endl;
    } while (CompareDate(borrowOrder.end, borrowOrder.start) <= 0);

    cout << "Введите комментарий, если необъодимо или 0 для пропуска:" << endl;
    borrowOrder.comment = InputLine();

    return borrowOrder;
}

vector<BorrowOrder> LoadBorrowOrders()
{
    ifstream fin(FILE_BORROW_ORDER, ios_base::in);

    vector<BorrowOrder> result;

    if (!fin.is_open())
    {
        return result;
    }

    int size = 0;
    fin >> size;

    for (int i = 0; i < size; ++i)
    {
        BorrowOrder order;
        fin >> order.clientInfoId;
        fin >> order.roomTypeId;
        fin >> order.ordered.day;
        fin >> order.ordered.month;
        fin >> order.ordered.year;
        fin >> order.start.day;
        fin >> order.start.month;
        fin >> order.start.year;
        fin >> order.end.day;
        fin >> order.end.month;
        fin >> order.end.year;
        getline(fin, order.comment);
        getline(fin, order.comment);

        result.push_back(order);
    }

    fin.close();

    return result;
}

vector<BorrowOrder> LoadCancelledBorrowOrders(string username)
{
    ifstream fin(username + ".txt", ios_base::in);

    vector<BorrowOrder> result;

    if (!fin.is_open())
        return result;

    int size = 0;
    fin >> size;

    for (int i = 0; i < size; ++i)
    {
        BorrowOrder order;
        fin >> order.clientInfoId;
        fin >> order.roomTypeId;
        fin >> order.ordered.day;
        fin >> order.ordered.month;
        fin >> order.ordered.year;
        fin >> order.start.day;
        fin >> order.start.month;
        fin >> order.start.year;
        fin >> order.end.day;
        fin >> order.end.month;
        fin >> order.end.year;
        getline(fin, order.comment);
        getline(fin, order.comment);

        result.push_back(order);
    }

    fin.close();

    return result;
}

void SaveCancelledBorrowOrders(vector<BorrowOrder> orders, string username)
{
    ofstream fout(username + ".txt", ios_base::trunc);

    if (!fout.is_open())
        return;

    fout << orders.size() << endl;

    for (int i = 0; i < orders.size(); ++i)
    {
        fout << orders[i].clientInfoId << endl;
        fout << orders[i].roomTypeId << endl;
        fout << orders[i].ordered.day << endl;
        fout << orders[i].ordered.month << endl;
        fout << orders[i].ordered.year << endl;
        fout << orders[i].start.day << endl;
        fout << orders[i].start.month << endl;
        fout << orders[i].start.year << endl;
        fout << orders[i].end.day << endl;
        fout << orders[i].end.month << endl;
        fout << orders[i].end.year << endl;
        fout << orders[i].comment << endl;
    }

    fout.close();
}

void SaveBorrowOrders(vector<BorrowOrder> orders)
{
    ofstream fout(FILE_BORROW_ORDER, ios_base::trunc);

    if (!fout.is_open())
    {
        return;
    }

    fout << orders.size() << endl;

    for (int i = 0; i < orders.size(); ++i)
    {
        fout << orders[i].clientInfoId << endl;
        fout << orders[i].roomTypeId << endl;
        fout << orders[i].ordered.day << endl;
        fout << orders[i].ordered.month << endl;
        fout << orders[i].ordered.year << endl;
        fout << orders[i].start.day << endl;
        fout << orders[i].start.month << endl;
        fout << orders[i].start.year << endl;
        fout << orders[i].end.day << endl;
        fout << orders[i].end.month << endl;
        fout << orders[i].end.year << endl;
        fout << orders[i].comment << endl;
    }

    fout.close();
}

void CancelOrder(BorrowOrder order)
{
    string username = FindAccountById(FindClientInfoById(order.clientInfoId).accountId).username;

    vector<BorrowOrder> cancelledOrders = LoadCancelledBorrowOrders(username);
    cancelledOrders.push_back(order);
    SaveCancelledBorrowOrders(cancelledOrders, username);
}

void PrintCancelledBorrowOrder(BorrowOrder order)
{
    cout << "Тип комнаты: " << FindRoomTypeById(order.roomTypeId).name << endl;
    cout << "С " << DateToString(order.start) << " по " << DateToString(order.end) << endl;
    cout << "Дата заявки: " << DateToString(order.ordered) << endl;
    cout << "Причина: " << order.comment << endl;
}

void PrintBorrowOrders(vector<BorrowOrder> orders)
{
    if (orders.empty())
    {
        cout << "Список пуст" << endl;
        return;
    }

    cout << "*********************************************************************************************************************************" << endl;
    cout << "|  №  |       Клиент       |     Тип номера     |Дата заявки|Дата начала|Дата окончания|               Комментарий              |" << endl;
    cout << "*********************************************************************************************************************************" << endl;

    for (int i = 0; i < orders.size(); i++)
    {
        cout << "|";
        cout << setw(5) << i + 1;
        cout << "|";
        cout << setw(20) << CredentialsToStr(FindClientInfoById(orders[i].clientInfoId).credentials, false);
        cout << "|";
        cout << setw(20) << FindRoomTypeById(orders[i].roomTypeId).name;
        cout << "|";
        cout << setw(11) << DateToString(orders[i].ordered);
        cout << "|";
        cout << setw(11) << DateToString(orders[i].start);
        cout << "|";
        cout << setw(14) << DateToString(orders[i].end);
        cout << "|";
        cout << setw(40) << orders[i].comment;
        cout << "|";
        cout << endl;

        cout << "*********************************************************************************************************************************" << endl;
    }
}
