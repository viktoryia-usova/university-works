#include "RoomType.h"
#include "Utils.h"
#include <fstream>
#include <string>
#include <iomanip>
#include "Room.h"

RoomType InputRoomType()
{
    RoomType roomType;

    roomType.id = FindRoomTypeMaxId() + 1;

    cout << "Введите название:" << endl;
    roomType.name = InputLine();

    cout << "Введите описание:" << endl;
    roomType.description = InputLine();

    cout << "Введите стоимость аренды в день" << endl;
    roomType.cost = InputDouble(0, 1000000);

    return roomType;
}

int FindRoomTypeMaxId()
{
    vector<RoomType> types = LoadRoomTypes();

    int maxId = 0;
    for (int i = 0; i < types.size(); i++)
        if (maxId < types[i].id)
            maxId = types[i].id;

    return maxId;
}

vector<RoomType> LoadRoomTypes()
{
    ifstream fin(FILE_ROOM_TYPE, ios_base::in);

    vector<RoomType> result;

    if (!fin.is_open())
    {
        return result;
    }

    int size = 0;
    fin >> size;

    for (int i = 0; i < size; ++i)
    {
        RoomType info;
        fin >> info.id;
        fin >> info.cost;
        getline(fin, info.name);
        getline(fin, info.name);
        getline(fin, info.description);

        result.push_back(info);
    }

    fin.close();

    return result;
}

void SaveRoomTypes(vector<RoomType> roomTypes)
{
    ofstream fout(FILE_ROOM_TYPE, ios_base::trunc);

    if (!fout.is_open())
    {
        return;
    }

    fout << roomTypes.size() << endl;

    for (int i = 0; i < roomTypes.size(); ++i)
    {
        fout << roomTypes[i].id << endl;
        fout << roomTypes[i].cost << endl;
        fout << roomTypes[i].name << endl;
        fout << roomTypes[i].description << endl;
    }

    fout.close();
}

void PrintRoomType(RoomType roomType)
{
    cout << "Название: " << roomType.name << endl;
    cout << "Стоимость в сутки: " << roomType.cost << endl;
    cout << "Описание: " << roomType.description << endl;
}

void PrintRoomTypes(vector<RoomType> roomTypes)
{
    if (roomTypes.empty())
    {
        cout << "Список пуст" << endl;
        return;
    }

    cout << "********************************************************************************************************************" << endl;
    cout << "|  №  |      Название      |   Стоимость   |                Описание                |Всего номеров|Свободно номеров|" << endl;
    cout << "********************************************************************************************************************" << endl;
    
    for (int i = 0; i < roomTypes.size(); i++)
    {
        cout << "|";
        cout << setw(5) << i + 1;
        cout << "|";
        cout << setw(20) << roomTypes[i].name;
        cout << "|";
        cout << setw(7) << roomTypes[i].cost << " руб/сут";
        cout << "|";
        cout << setw(40) << roomTypes[i].description;
        cout << "|";
        cout << setw(13) << GetCountOfRooms(roomTypes[i].id);
        cout << "|";
        cout << setw(16) << GetCountOfFreeRooms(roomTypes[i].id);
        cout << "|";
        cout << endl;
        cout << "********************************************************************************************************************" << endl;
    }
}

int GetCountOfRooms(int roomTypeId)
{
    int result = 0;

    vector<Room> rooms = LoadRooms();
    for (int i = 0; i < rooms.size(); i++)
    {
        if (rooms[i].roomTypeId == roomTypeId)
            result++;
    }

    return result;
}

int GetCountOfFreeRooms(int roomTypeId)
{
    int result = 0;

    vector<Room> rooms = LoadRooms();
    for (int i = 0; i < rooms.size(); i++)
    {
        if (rooms[i].roomTypeId == roomTypeId && IsRoomFree(rooms[i].id))
            result++;
    }

    return result;
}

RoomType FindRoomTypeById(int id)
{
    RoomType result;

    vector<RoomType> types = LoadRoomTypes();
    for (int i = 0; i < types.size(); i++)
    {
        if (id == types[i].id)
        {
            result = types[i];
            break;
        }
    }

    return result;
}
