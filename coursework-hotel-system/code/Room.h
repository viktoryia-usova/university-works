#pragma once
#include <iostream>
#include <vector>

using namespace std;

#define FILE_ROOM "rooms.txt"

struct Room
{
	int id;
	int roomTypeId;
	string number;
};

Room InputRoom();

int FindRoomMaxId();

int ChooseRoomType();

vector<Room> LoadRooms();
void SaveRooms(vector<Room> rooms);

void PrintRoom(Room room);
void PrintRooms(vector<Room> rooms);

Room FindRoomById(int roomId);
vector<Room> FindRoomByRoomTypeId(int roomTypeId);

bool IsRoomFree(int roomId);