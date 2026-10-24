#include "Room.h"
#include "RoomType.h"
#include "BorrowRecord.h"
#include "Utils.h"
#include <fstream>
#include <string>
#include <iomanip>

Room InputRoom()
{
    Room room;

    room.id = FindRoomMaxId() + 1;
    room.roomTypeId = ChooseRoomType();

    cout << "Введите номер комнаты:" << endl;
    room.number = InputLine();

    return room;
}

int FindRoomMaxId()
{
    vector<Room> rooms = LoadRooms();

    int maxId = 0;
    for (int i = 0; i < rooms.size(); i++)
        if (maxId < rooms[i].id)
            maxId = rooms[i].id;

    return maxId;
}

int ChooseRoomType()
{
    vector<RoomType> types = LoadRoomTypes();
    if (types.empty())
        return 0;

    PrintRoomTypes(types);
    cout << "Выберите тип:" << endl;
    int index = InputInt(1, types.size()) - 1;

    return types[index].id;
}

vector<Room> LoadRooms()
{
    ifstream fin(FILE_ROOM, ios_base::in);

    vector<Room> result;

    if (!fin.is_open())
    {
        return result;
    }

    int size = 0;
    fin >> size;

    for (int i = 0; i < size; ++i)
    {
        Room room;
        fin >> room.id;
        fin >> room.roomTypeId;
        getline(fin, room.number);
        getline(fin, room.number);

        result.push_back(room);
    }

    fin.close();

    return result;
}

void SaveRooms(vector<Room> rooms)
{
    ofstream fout(FILE_ROOM, ios_base::trunc);

    if (!fout.is_open())
    {
        return;
    }

    fout << rooms.size() << endl;

    for (int i = 0; i < rooms.size(); ++i)
    {
        fout << rooms[i].id << endl;
        fout << rooms[i].roomTypeId << endl;
        fout << rooms[i].number << endl;
    }

    fout.close();
}

void PrintRoom(Room room)
{
    cout << "Номер комнаты: " << room.number << endl;
    cout << "Тип комнаты:" << FindRoomTypeById(room.roomTypeId).name << endl;
}

void PrintRooms(vector<Room> rooms)
{
    if (rooms.empty())
    {
        cout << "Список пуст" << endl;
        return;
    }

    cout << "***********************************************" << endl;
    cout << "|  №  |  Номер  |     Тип номера     | Статус |" << endl;
    cout << "***********************************************" << endl;

    for (int i = 0; i < rooms.size(); i++)
    {
        cout << "|";
        cout << setw(5) << i + 1;
        cout << "|";
        cout << setw(9) << rooms[i].number;
        cout << "|";
        cout << setw(20) << FindRoomTypeById(rooms[i].roomTypeId).name;
        cout << "|";
        cout << (IsRoomFree(rooms[i].id) ? "Свободен" : " Занят  ");
        cout << "|";
        cout << endl;
        cout << "***********************************************" << endl;
    }
}

Room FindRoomById(int roomId)
{
    Room result;

    vector<Room> rooms = LoadRooms();
    for (int i = 0; i < rooms.size(); i++)
    {
        if (roomId == rooms[i].roomTypeId)
        {
            result = rooms[i];
            break;
        }
    }

    return result;
}

vector<Room> FindRoomByRoomTypeId(int roomTypeId)
{
    vector<Room> result;

    vector<Room> rooms = LoadRooms();
    for (int i = 0; i < rooms.size(); i++)
    {
        if (roomTypeId == rooms[i].roomTypeId)
        {
            result.push_back(rooms[i]);
        }
    }

    return result;
}

bool IsRoomFree(int roomId)
{
    return FindBorrowRecordByRoomId(roomId, true).empty();
}
