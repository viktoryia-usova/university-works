#pragma once
#include <iostream>
#include <vector>

using namespace std;

#define FILE_ROOM_TYPE "room_types.txt"

struct RoomType
{
	int id;
	string name;
	string description;
	double cost;
};

RoomType InputRoomType();

int FindRoomTypeMaxId();

vector<RoomType> LoadRoomTypes();
void SaveRoomTypes(vector<RoomType> roomTypes);

void PrintRoomType(RoomType roomType);
void PrintRoomTypes(vector<RoomType> roomTypes);

int GetCountOfRooms(int roomTypeId);
int GetCountOfFreeRooms(int roomTypeId);

RoomType FindRoomTypeById(int id);