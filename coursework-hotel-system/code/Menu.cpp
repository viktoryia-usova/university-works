#include "Menu.h"
#include "Utils.h"
#include "ClientInfo.h"
#include "RoomType.h"
#include "Room.h"
#include "BorrowOrder.h"
#include "BorrowRecord.h"

void Menu(Account account)
{
	if (!account.isActive)
	{
		system("cls");
		cout << "Ваш аккаунт был заблокирован" << endl;
		system("pause");
		return;
	}

	switch (account.role)
	{
	case ROLE_ADMIN:
	{
		MenuAdmin(account);
		break;
	}
	case ROLE_CLIENT:
	{
		MenuClient(account);
		break;
	}
	case ROLE_UNDEFINED:
	default:
		system("cls");
		cout << "Неверный логин или пароль" << endl;
		system("pause");
		break;
	}
}

void MenuAdmin(Account account)
{
	bool work = true;
	while (work)
	{
		system("cls");
		cout << "1. Управление пользователями" << endl;
		cout << "2. Управление типами номеров" << endl;
		cout << "3. Управление номерами" << endl;
		cout << "4. Управление заявками" << endl;
		cout << "5. Управление записями" << endl;
		cout << "6. Управление историей" << endl;
		cout << "7. Выход" << endl;

		switch (InputInt(1, 7))
		{
		case 1:
		{
			UserManagement();
			break;
		}
		case 2:
		{
			RoomTypeManagement();
			break;
		}
		case 3:
		{
			RoomManagement();
			break;
		}
		case 4:
		{
			OrderManagement();
			break;
		}
		case 5:
		{
			RecordManagement();
			break;
		}
		case 6:
		{
			HistoryManagement();
			break;
		}
		case 7:
		{
			work = false;
			break;
		}
		}
	}
}

void MenuClient(Account account)
{
	vector<BorrowOrder> cancelledOrders = LoadCancelledBorrowOrders(account.username);
	if (!cancelledOrders.empty())
	{
		for (int i = 0; i < cancelledOrders.size(); i++)
		{
			system("cls");
			cout << "Ваша заявка была отменена:" << endl;
			PrintCancelledBorrowOrder(cancelledOrders[i]);
			system("pause");
		}
		cancelledOrders.clear();
		SaveCancelledBorrowOrders(cancelledOrders, account.username);
	}

	bool work = true;
	while (work)
	{
		system("cls");
		cout << "1. Редактирование профиля" << endl;
		cout << "2. Просмотр доступных номеров" << endl;
		cout << "3. Оставление заявки на бронь" << endl;
		cout << "4. Просмотр текущих заявок о брони" << endl;
		cout << "5. Просмотр текущих записей о брони" << endl;
		cout << "6. История записей" << endl;
		cout << "7. Выход" << endl;

		switch (InputInt(1, 7))
		{
		case 1:
		{
			ManageProfile(account);
			break;
		}
		case 2:
		{
			ViewAvaliableNumbers();
			break;
		}
		case 3:
		{
			CreateOrder(account);
			break;
		}
		case 4:
		{
			ViewClientOrders(account);
			break;
		}
		case 5:
		{
			ViewClientRecords(account);
			break;
		}
		case 6:
		{
			ViewClientHistory(account);
			break;
		}
		case 7:
		{
			work = false;
			break;
		}
		}
	}
}

Account Authorization()
{
	vector<Account> accounts = LoadAccounts();

	Account account;
	account.isActive = true;
	account.role = ROLE_UNDEFINED;

	cout << "Введите логин:" << endl;
	account.username = InputStr();
	cout << "Введите пароль:" << endl;
	account.password = InputPassword();

	for (int i = 0; i < accounts.size(); ++i)
	{
		if (account.username == accounts[i].username && account.password == accounts[i].password)
		{
			return accounts[i];
		}
	}

	return account;
}

Account Registration()
{
	vector<Account> accounts = LoadAccounts();
	vector<ClientInfo> clientInfos = LoadClientInfos();

	Account account = InputAccount(ROLE_CLIENT, true);
	ClientInfo clientInfo = InputClientInfo(account.id);

	accounts.push_back(account);
	SaveAccounts(accounts);

	clientInfos.push_back(clientInfo);
	SaveClientInfos(clientInfos);

	return account;
}

void UserManagement()
{
	vector<Account> accounts = LoadAccounts();
	vector<Account> clients;
	for (int i = 0; i < accounts.size(); i++)
	{
		if (accounts[i].role == ROLE_UNDEFINED || accounts[i].role == ROLE_ADMIN)
			continue;

		clients.push_back(accounts[i]);
	}

	system("cls");
	if (clients.empty())
	{
		cout << "Пока что не зарегистрировано ни одного пользователя" << endl;
		system("pause");
		return;
	}

	bool work = true;
	while (work)
	{
		system("cls");
		PrintAccounts(clients);
		
		cout << "Введите номер пользователя для блокировки/разблокировки или 0 для выхода" << endl;
		int index = InputInt(0, clients.size());

		if (index == 0)
		{
			work = false;
			continue;
		}

		index--;
		clients[index].isActive = !clients[index].isActive;
	}

	for (int i = 0; i < accounts.size(); i++)
	{
		for (int j = 0; j < clients.size(); j++)
		{
			if (accounts[i].id == clients[j].id)
			{
				accounts[i].isActive = clients[j].isActive;
			}
		}
	}

	SaveAccounts(accounts);
}

void RoomTypeManagement()
{
	

	bool work = true;
	while (work)
	{
		vector<RoomType> roomTypes = LoadRoomTypes();
		system("cls");
		cout << "1.Просмотреть все типы номеров" << endl;
		cout << "2.Добавить тип номера" << endl;
		cout << "3.Редактировать тип номера" << endl;
		cout << "4.Удалить тип номера" << endl;
		cout << "5.Поиск типов номеров" << endl;
		cout << "6.Сортировка типов номеров" << endl;
		cout << "7.Назад" << endl;

		switch (InputInt(1,7))
		{
		case 1:
		{
			system("cls");
			PrintRoomTypes(roomTypes);
			system("pause");
			break;
		}
		case 2:
		{
			system("cls");
			roomTypes.push_back(InputRoomType());
			system("pause");
			break;
		}
		case 3:
		{
			system("cls");
			PrintRoomTypes(roomTypes);
			if (roomTypes.empty())
			{
				system("pause");
				break;
			}

			cout << "Введите номер типа комнаты для редактирования или 0 для отмены:" << endl;
			int index = InputInt(0, roomTypes.size());

			if (index == 0)
				break;

			index--;

			RoomType newValue = roomTypes[index];

			bool edit = true;
			while (edit)
			{
				system("cls");
				PrintRoomType(newValue);
				cout << endl;
				cout << "Выберите поле для редактирования:" << endl;
				cout << "1.Название" << endl;
				cout << "2.Описание" << endl;
				cout << "3.Стоимость проживания" << endl;
				cout << "4.Сохранить" << endl;
				switch (InputInt(1,4))
				{
				case 1:
				{
					cout << "Введите новое название:" << endl;
					newValue.name = InputLine();
					break;
				}
				case 2:
				{
					cout << "Введите новое описание:" << endl;
					newValue.description = InputLine();
					break;
				}
				case 3:
				{
					cout << "Введите новую стоимость проживания в день:" << endl;
					newValue.cost = InputDouble(0, 1000000);
					break;
				}
				case 4:
				{
					edit = false;
					break;
				}
				}
			}

			if (Confirmation("Вы уверены что хотите применить изменения?"))
				roomTypes[index] = newValue;

			break;
		}
		case 4:
		{
			system("cls");
			PrintRoomTypes(roomTypes);
			if (roomTypes.empty())
			{
				system("pause");
				break;
			}

			cout << "Введите номер типа комнаты для удаления или 0 для отмены:" << endl;
			int index = InputInt(0, roomTypes.size());

			if (index == 0)
				break;

			index--;

			if (!FindRoomByRoomTypeId(roomTypes[index].id).empty())
			{
				cout << "Есть номера данного типа. Удаление невозможно" << endl;
				break;
			}

			if (Confirmation("Вы уверены что хотите удалить запись?"))
				roomTypes.erase(roomTypes.begin() + index);

			break;
		}
		case 5:
		{
			system("cls");
			if (roomTypes.empty())
			{
				PrintRoomTypes(roomTypes);
				system("pause");
				break;
			}

			cout << "Введите строку или подстроку для поиска:" << endl;
			string search = InputLine();

			vector<RoomType> found;
			for (int i = 0; i < roomTypes.size(); i++)
			{
				if (roomTypes[i].name.find(search) != -1 || roomTypes[i].description.find(search) != -1)
					found.push_back(roomTypes[i]);
			}

			system("cls");

			if (found.empty())
				cout << "Ничего не найдено" << endl;
			else
				PrintRoomTypes(found);

			system("pause");
			break;
		}
		case 6:
		{
			system("cls");
			if (roomTypes.empty())
			{
				PrintRoomTypes(roomTypes);
				system("pause");
				break;
			}

			cout << "Выберите поле для сортировки:" << endl;
			cout << "1.Название" << endl;
			cout << "2.Строимость проживания" << endl;

			switch (InputInt(1,2))
			{
			case 1:
			{
				for (int i = 0; i < roomTypes.size(); i++)
				{
					for (int j = i + 1; j < roomTypes.size(); j++)
					{
						if (roomTypes[i].name.compare(roomTypes[j].name) == 1)
							swap(roomTypes[i], roomTypes[j]);
					}
				}
				break;
			}
			case 2:
			{
				for (int i = 0; i < roomTypes.size(); i++)
				{
					for (int j = i + 1; j < roomTypes.size(); j++)
					{
						if (roomTypes[i].cost > roomTypes[j].cost)
							swap(roomTypes[i], roomTypes[j]);
					}
				}
				break;
			}
			}

			system("cls");
			PrintRoomTypes(roomTypes);
			system("pause");

			break;
		}
		case 7:
		{
			work = false;
			break;
		}
		}
		SaveRoomTypes(roomTypes);
	}

	
}

void RoomManagement()
{
	bool work = true;
	while (work)
	{
		vector<Room> rooms = LoadRooms();
		system("cls");
		cout << "1.Просмотреть все номера" << endl;
		cout << "2.Добавить номер" << endl;
		cout << "3.Редактировать номер" << endl;
		cout << "4.Удалить номер" << endl;
		cout << "5.Поиск номеров" << endl;
		cout << "6.Сортировка номеров" << endl;
		cout << "7.Назад" << endl;

		switch (InputInt(1, 7))
		{
		case 1:
		{
			system("cls");
			PrintRooms(rooms);
			system("pause");
			break;
		}
		case 2:
		{
			system("cls");
			vector<RoomType> types = LoadRoomTypes();
			if (types.empty())
			{
				cout << "Список типов номеров пуст" << endl;
				system("pause");
				break;
			}

			rooms.push_back(InputRoom());
			break;
		}
		case 3:
		{
			system("cls");
			PrintRooms(rooms);
			if (rooms.empty())
			{
				system("pause");
				break;
			}

			cout << "Введите номер комнаты для редактирования или 0 для отмены:" << endl;
			int index = InputInt(0, rooms.size());

			if (index == 0)
				break;

			index--;

			Room newValue = rooms[index];

			bool edit = true;
			while (edit)
			{
				system("cls");
				PrintRoom(newValue);
				cout << endl;
				cout << "Выберите поле для редактирования:" << endl;
				cout << "1.Номер комнаты" << endl;
				cout << "2.Тип комнаты" << endl;
				cout << "3.Сохранить" << endl;
				switch (InputInt(1, 3))
				{
				case 1:
				{
					cout << "Введите новый номер:" << endl;
					newValue.number = InputLine();
					break;
				}
				case 2:
				{
					cout << "Введите новое описание:" << endl;
					newValue.roomTypeId = ChooseRoomType();
					break;
				}
				case 3:
				{
					edit = false;
					break;
				}
				}
			}

			if (Confirmation("Вы уверены что хотите применить изменения?"))
				rooms[index] = newValue;

			break;
		}
		case 4:
		{
			system("cls");
			PrintRooms(rooms);
			if (rooms.empty())
			{
				system("pause");
				break;
			}

			cout << "Введите номер записи для удаления или 0 для отмены:" << endl;
			int index = InputInt(0, rooms.size());

			if (index == 0)
				break;

			index--;

			if (!FindBorrowRecordByRoomId(rooms[index].id, true).empty())
			{
				cout << "Этот номер сейчас занят. Удаление невозможно" << endl;
				break;
			}

			if (Confirmation("Вы уверены что хотите удалить запись?"))
				rooms.erase(rooms.begin() + index);

			break;
		}
		case 5:
		{
			system("cls");
			if (rooms.empty())
			{
				PrintRooms(rooms);
				system("pause");
				break;
			}

			cout << "Введите строку или подстроку для поиска:" << endl;
			string search = InputLine();

			vector<Room> found;
			for (int i = 0; i < rooms.size(); i++)
			{
				if (rooms[i].number.find(search) != -1 || FindRoomTypeById(rooms[i].roomTypeId).name.find(search) != -1)
					found.push_back(rooms[i]);
			}

			system("cls");

			if (found.empty())
				cout << "Ничего не найдено" << endl;
			else
				PrintRooms(found);

			system("pause");
			break;
		}
		case 6:
		{
			system("cls");
			if (rooms.empty())
			{
				PrintRooms(rooms);
				system("pause");
				break;
			}

			cout << "Выберите поле для сортировки:" << endl;
			cout << "1.Номер комнаты" << endl;
			cout << "2.Тип номера" << endl;

			switch (InputInt(1, 2))
			{
			case 1:
			{
				for (int i = 0; i < rooms.size(); i++)
				{
					for (int j = i + 1; j < rooms.size(); j++)
					{
						if (rooms[i].number.compare(rooms[j].number) == 1)
							swap(rooms[i], rooms[j]);
					}
				}
				break;
			}
			case 2:
			{
				for (int i = 0; i < rooms.size(); i++)
				{
					for (int j = i + 1; j < rooms.size(); j++)
					{
						if (FindRoomTypeById(rooms[i].roomTypeId).name.compare(FindRoomTypeById(rooms[j].roomTypeId).name) == 1)
							swap(rooms[i], rooms[j]);
					}
				}
				break;
			}
			}

			system("cls");
			PrintRooms(rooms);
			system("pause");

			break;
		}
		case 7:
		{
			work = false;
			break;
		}
		}
		SaveRooms(rooms);
	}
}

void OrderManagement()
{
	vector<BorrowOrder> orders = LoadBorrowOrders();

	if (orders.empty())
	{
		system("cls");
		cout << "Пока что нет ни одной заявки" << endl;
		system("pause");
		return;
	}

	bool work = true;
	while (work)
	{
		orders = LoadBorrowOrders();

		system("cls");
		PrintBorrowOrders(orders);

		cout << endl;
		cout << "1.Одобрить заявку" << endl;
		cout << "2.Отклонить заявку" << endl;
		cout << "3.Поиск заявок" << endl;
		cout << "4.Сортировка заявок" << endl;
		cout << "5.Назад" << endl;

		switch (InputInt(1,5))
		{
		case 1:
		{
			cout << "Введите номер заявки для одобрения или 0 для отмены:" << endl;
			int index = InputInt(0, orders.size());

			if (index == 0)
				break;

			index--;

			vector<Room> avaliableRooms;

			vector<Room> rooms = LoadRooms();
			for (int i = 0; i < rooms.size(); i++)
			{
				if (rooms[i].roomTypeId == orders[index].roomTypeId && IsRoomFree(rooms[i].id))
					avaliableRooms.push_back(rooms[i]);
			}

			if (avaliableRooms.empty())
			{
				cout << "Нет ни одного свободного номера данного типа" << endl;
				system("pause");
			}

			PrintRooms(avaliableRooms);
			cout << "Введите номер комнаты для брони или 0 для отмены:" << endl;

			int roomIndex = InputInt(0, avaliableRooms.size());

			if (roomIndex == 0)
				break;

			roomIndex--;

			BorrowRecord record;
			record.clientInfoId = orders[index].clientInfoId;
			record.roomId = avaliableRooms[roomIndex].id;
			record.start = orders[index].start;
			record.end = orders[index].end;
			record.state = STATE_DEFAULT;

			vector<BorrowRecord> records = LoadBorrowRecords();
			records.push_back(record);
			SaveBorrowRecords(records);

			orders.erase(orders.begin() + index);
			break;
		}
		case 2:
		{
			cout << "Введите номер заявки для отклоения или 0 для отмены:" << endl;
			int index = InputInt(0, orders.size());

			if (index == 0)
				break;

			index--;

			cout << "Введите причину отклонения:" << endl;
			string reason = InputLine();

			if (!Confirmation("Вы уверены, что хотите отклонить заявку?"))
				break;

			orders[index].comment = reason;
			CancelOrder(orders[index]);
			orders.erase(orders.begin() + index);
			break;
		}
		case 3:
		{
			system("cls");
			cout << "Введите строку или подстроку для поиска:" << endl;
			string search = InputLine();

			vector<BorrowOrder> found;
			for (int i = 0; i < orders.size(); i++)
			{
				if (FindClientInfoById(orders[i].clientInfoId).credentials.firstname.find(search) != -1 ||
					FindRoomTypeById(orders[i].roomTypeId).name.find(search) != -1 ||
					orders[i].comment.find(search) != -1 ||
					DateToString(orders[i].ordered).find(search) != -1 ||
					DateToString(orders[i].start).find(search) != -1 ||
					DateToString(orders[i].end).find(search) != -1)
				{
					found.push_back(orders[i]);
				}
					
			}

			system("cls");

			if (found.empty())
				cout << "Ничего не найдено" << endl;
			else
				PrintBorrowOrders(found);

			system("pause");
			break;
		}
		case 4:
		{
			system("cls");
			cout << "Выберите поле для сортировки:" << endl;
			cout << "1.Фамилия клиента" << endl;
			cout << "2.Тип номера" << endl;
			cout << "3.Дата создания заявки" << endl;
			cout << "4.Дата начала брони" << endl;

			switch (InputInt(1, 4))
			{
			case 1:
			{
				for (int i = 0; i < orders.size(); i++)
				{
					for (int j = i + 1; j < orders.size(); j++)
					{
						if (FindClientInfoById(orders[i].clientInfoId).credentials.firstname.compare(FindClientInfoById(orders[j].clientInfoId).credentials.firstname) == 1)
							swap(orders[i], orders[j]);
					}
				}
				break;
			}
			case 2:
			{
				for (int i = 0; i < orders.size(); i++)
				{
					for (int j = i + 1; j < orders.size(); j++)
					{
						if (FindRoomTypeById(orders[i].roomTypeId).name.compare(FindRoomTypeById(orders[j].roomTypeId).name) == 1)
							swap(orders[i], orders[j]);
					}
				}
				break;
			}
			case 3:
			{
				for (int i = 0; i < orders.size(); i++)
				{
					for (int j = i + 1; j < orders.size(); j++)
					{
						if (CompareDate(orders[i].ordered, orders[j].ordered) == 1)
							swap(orders[i], orders[j]);
					}
				}
				break;
			}
			case 4:
			{
				for (int i = 0; i < orders.size(); i++)
				{
					for (int j = i + 1; j < orders.size(); j++)
					{
						if (CompareDate(orders[i].start, orders[j].start) == 1)
							swap(orders[i], orders[j]);
					}
				}
				break;
			}
			}
			break;
		}
		case 5:
		{
			work = false;
			break;
		}
		}

		if (orders.empty())
		{
			system("cls");
			cout << "Заявки закончились" << endl;
			system("pause");

			work = false;
		}
		SaveBorrowOrders(orders);
	}

	
}

void RecordManagement()
{
	vector<BorrowRecord> allRecords = LoadBorrowRecords();

	vector<BorrowRecord> records;
	for (int i = 0; i < allRecords.size(); i++)
	{
		if (IsActive(allRecords[i]))
			records.push_back(allRecords[i]);
	}

	if (records.empty())
	{
		system("cls");
		cout << "Пока что нет ни одной активной записи" << endl;
		system("pause");
		return;
	}

	bool work = true;
	while (work)
	{
		system("cls");
		cout << "1.Просмотр всех записей" << endl;
		cout << "2.Поиск записей" << endl;
		cout << "3.Сортировка записей" << endl;
		cout << "4.Фильтрация записей" << endl;
		cout << "5.Выход" << endl;

		switch (InputInt(1,5))
		{
		case 1:
		{
			system("cls");
			PrintBorrowRecords(records);
			system("pause");
			break;
		}
		case 2:
		{
			system("cls");
			cout << "Введите строку или подстроку для поиска:" << endl;
			string search = InputLine();

			vector<BorrowRecord> found;
			for (int i = 0; i < records.size(); i++)
			{
				if (CredentialsToStr(FindClientInfoById(records[i].clientInfoId).credentials, true).find(search) != -1 ||
					FindRoomById(records[i].roomId).number.find(search) != -1 ||
					DateToString(records[i].start).find(search) != -1 ||
					DateToString(records[i].end).find(search) != -1)
				{
					found.push_back(records[i]);
				}
			}

			system("cls");

			if (found.empty())
			{
				cout << "Ничего не найдено" << endl;
			}
			else
			{
				PrintBorrowRecords(found);
			}
			system("pause");

			break;
		}
		case 3:
		{
			system("cls");
			cout << "Выберите поле для сортировки:" << endl;
			cout << "1.ФИО клиента" << endl;
			cout << "2.Номер комнаты" << endl;
			cout << "3.Дата начала брони" << endl;
			cout << "4.Дата окончания брони" << endl;

			switch (InputInt(1,4))
			{
			case 1:
			{
				for (int i = 0; i < records.size(); i++)
				{
					for (int j = i + 1; j < records.size(); j++)
					{
						if (CredentialsToStr(FindClientInfoById(records[i].clientInfoId).credentials, true)
							.compare(CredentialsToStr(FindClientInfoById(records[j].clientInfoId).credentials, true)) == 1)
						{
							swap(records[i], records[j]);
						}
					}
				}
				break;
			}
			case 2:
			{
				for (int i = 0; i < records.size(); i++)
				{
					for (int j = i + 1; j < records.size(); j++)
					{
						if (FindRoomById(records[i].roomId).number.compare(FindRoomById(records[j].roomId).number) == 1)
						{
							swap(records[i], records[j]);
						}
					}
				}
				break;
			}
			case 3:
			{
				for (int i = 0; i < records.size(); i++)
				{
					for (int j = i + 1; j < records.size(); j++)
					{
						if (CompareDate(records[i].start, records[j].start) == 1)
						{
							swap(records[i], records[j]);
						}
					}
				}
				break;
			}
			case 4:
			{
				for (int i = 0; i < records.size(); i++)
				{
					for (int j = i + 1; j < records.size(); j++)
					{
						if (CompareDate(records[i].end, records[j].end) == 1)
						{
							swap(records[i], records[j]);
						}
					}
				}
				break;
			}
			}

			system("cls");
			PrintBorrowRecords(records);
			system("pause");

			break;
		}
		case 4:
		{
			system("cls");
			cout << "Выберите поле для фильтрации:" << endl;
			cout << "1.Дата начала брони" << endl;
			cout << "2.Дата окончания брони" << endl;

			vector<BorrowRecord> found;
			switch (InputInt(1, 2))
			{
			case 1:
			{
				cout << "Введите минимальную дату:" << endl;
				Date date1 = InputDate();
				cout << "Введите максимальную дату:" << endl;
				Date date2 = InputDate();
				for (int i = 0; i < records.size(); i++)
				{
					if (CompareDate(date2, records[i].start) >= 0 && CompareDate(date1, records[i].start) <= 0)
					{
						found.push_back(records[i]);
					}
				}
				break;
			}
			case 2:
			{
				cout << "Введите минимальную дату:" << endl;
				Date date1 = InputDate();
				cout << "Введите максимальную дату:" << endl;
				Date date2 = InputDate();
				for (int i = 0; i < records.size(); i++)
				{
					if (CompareDate(date2, records[i].end) >= 0 && CompareDate(date1, records[i].end) <= 0)
					{
						found.push_back(records[i]);
					}
				}
				break;
			}
			}
			system("cls");

			if (found.empty())
			{
				cout << "Ничего не найдено" << endl;
			}
			else
			{
				PrintBorrowRecords(found);
			}
			system("pause");

			break;
		}
		case 5:
		{
			work = false;
			break;
		}
		}
	}
}

void HistoryManagement()
{
	vector<BorrowRecord> allRecords = LoadBorrowRecords();

	vector<BorrowRecord> records;
	for (int i = 0; i < allRecords.size(); i++)
	{
		if (!IsActive(allRecords[i]))
			records.push_back(allRecords[i]);
	}

	if (records.empty())
	{
		system("cls");
		cout << "Пока что нет ни одной записи в истории" << endl;
		system("pause");
		return;
	}

	bool work = true;
	while (work)
	{
		system("cls");
		cout << "1.Просмотр всех записей" << endl;
		cout << "2.Поиск записей" << endl;
		cout << "3.Сортировка записей" << endl;
		cout << "4.Фильтрация записей" << endl;
		cout << "5.Выход" << endl;

		switch (InputInt(1, 5))
		{
		case 1:
		{
			system("cls");
			PrintBorrowRecords(records);
			system("pause");
			break;
		}
		case 2:
		{
			system("cls");
			cout << "Введите строку или подстроку для поиска:" << endl;
			string search = InputLine();

			vector<BorrowRecord> found;
			for (int i = 0; i < records.size(); i++)
			{
				if (CredentialsToStr(FindClientInfoById(records[i].clientInfoId).credentials, true).find(search) != -1 ||
					FindRoomById(records[i].roomId).number.find(search) != -1||
					DateToString(records[i].start).find(search) != -1||
					DateToString(records[i].end).find(search) != -1)
				{
					found.push_back(records[i]);
				}
			}

			system("cls");

			if (found.empty())
			{
				cout << "Ничего не найдено" << endl;
			}
			else
			{
				PrintBorrowRecords(found);
			}
			system("pause");

			break;
		}
		case 3:
		{
			system("cls");
			cout << "Выберите поле для сортировки:" << endl;
			cout << "1.ФИО клиента" << endl;
			cout << "2.Номер комнаты" << endl;
			cout << "3.Дата начала брони" << endl;
			cout << "4.Дата окончания брони" << endl;

			switch (InputInt(1, 4))
			{
			case 1:
			{
				for (int i = 0; i < records.size(); i++)
				{
					for (int j = i + 1; j < records.size(); j++)
					{
						if (CredentialsToStr(FindClientInfoById(records[i].clientInfoId).credentials, true)
							.compare(CredentialsToStr(FindClientInfoById(records[j].clientInfoId).credentials, true)) == 1)
						{
							swap(records[i], records[j]);
						}
					}
				}
				break;
			}
			case 2:
			{
				for (int i = 0; i < records.size(); i++)
				{
					for (int j = i + 1; j < records.size(); j++)
					{
						if (FindRoomById(records[i].roomId).number.compare(FindRoomById(records[j].roomId).number) == 1)
						{
							swap(records[i], records[j]);
						}
					}
				}
				break;
			}
			case 3:
			{
				for (int i = 0; i < records.size(); i++)
				{
					for (int j = i + 1; j < records.size(); j++)
					{
						if (CompareDate(records[i].start, records[j].start) == 1)
						{
							swap(records[i], records[j]);
						}
					}
				}
				break;
			}
			case 4:
			{
				for (int i = 0; i < records.size(); i++)
				{
					for (int j = i + 1; j < records.size(); j++)
					{
						if (CompareDate(records[i].end, records[j].end) == 1)
						{
							swap(records[i], records[j]);
						}
					}
				}
				break;
			}
			}

			system("cls");
			PrintBorrowRecords(records);
			system("pause");

			break;
		}
		case 4:
		{
			system("cls");
			cout << "Выберите поле для фильтрации:" << endl;
			cout << "1.Дата начала брони" << endl;
			cout << "2.Дата окончания брони" << endl;

			vector<BorrowRecord> found;
			switch (InputInt(1, 4))
			{
			case 1:
			{
				cout << "Введите минимальную дату:" << endl;
				Date date1 = InputDate();
				cout << "Введите максимальную дату:" << endl;
				Date date2 = InputDate();
				for (int i = 0; i < records.size(); i++)
				{
					if (CompareDate(date2, records[i].start) >= 0 && CompareDate(date1, records[i].start) <= 0)
					{
						found.push_back(records[i]);
					}
				}
				break;
			}
			case 2:
			{
				cout << "Введите минимальную дату:" << endl;
				Date date1 = InputDate();
				cout << "Введите максимальную дату:" << endl;
				Date date2 = InputDate();
				for (int i = 0; i < records.size(); i++)
				{
					if (CompareDate(date2, records[i].end) >= 0 && CompareDate(date1, records[i].end) <= 0)
					{
						found.push_back(records[i]);
					}
				}
				break;
			}
			}
			system("cls");

			if (found.empty())
			{
				cout << "Ничего не найдено" << endl;
			}
			else
			{
				PrintBorrowRecords(found);
			}
			system("pause");

			break;
		}
		case 5:
		{
			work = false;
			break;
		}
		}
	}
}

void ManageProfile(Account account)
{
	ClientInfo info = FindClientInfoByAccountId(account.id);

	bool work = true;
	while (work)
	{
		system("cls");
		PrintClientInfo(info);
		cout << endl;
		cout << "1.Редактировать ФИО" << endl;
		cout << "2.Редактировать телефон" << endl;
		cout << "3.Редактировать паспорт" << endl;
		cout << "4.Сохранить и выйти" << endl;
		cout << "5.Отмена" << endl;

		switch (InputInt(1,5))
		{
		case 1:
		{
			cout << "Введите новое ФИО:" << endl;
			info.credentials = InputCredentials();
			break;
		}
		case 2:
		{
			cout << "Введите новый номер телефона:" << endl;
			info.telephone = InputTelephone();
			break;
		}
		case 3:
		{
			cout << "Введите новый паспорт:" << endl;
			info.passport = InputPassport();
			break;
		}
		case 4:
		{
			UpdateClientInfo(info);
			work = false;
			break;
		}
		case 5:
		{
			work = false;
			break;
		}
		}
	}
}

void ViewAvaliableNumbers()
{
	vector<RoomType> roomTypes = LoadRoomTypes();

	vector<RoomType> avaliableRoomTypes;
	for (int i = 0; i < roomTypes.size(); i++)
	{
		if (GetCountOfFreeRooms(roomTypes[i].id) != 0)
			avaliableRoomTypes.push_back(roomTypes[i]);
	}

	system("cls");

	if (avaliableRoomTypes.empty())
		cout << "Нет ни одного свободного номера" << endl;
	else
		PrintRoomTypes(avaliableRoomTypes);

	system("pause");
}

void CreateOrder(Account account)
{
	ClientInfo info = FindClientInfoByAccountId(account.id);
	vector<RoomType> roomTypes = LoadRoomTypes();

	vector<RoomType> avaliableRoomTypes;
	for (int i = 0; i < roomTypes.size(); i++)
	{
		if (GetCountOfFreeRooms(roomTypes[i].id) != 0)
			avaliableRoomTypes.push_back(roomTypes[i]);
	}

	system("cls");

	if (avaliableRoomTypes.empty())
	{
		cout << "Нет ни одного свободного номера" << endl;
		system("pause");
		return;
	}

	PrintRoomTypes(avaliableRoomTypes);
	cout << "Выберите тип номера или 0 для отмены:" << endl;
	int index = InputInt(0, avaliableRoomTypes.size());
	
	if (index == 0)
		return;

	index--;
	BorrowOrder order = InputBorrowOrder(avaliableRoomTypes[index].id, info.id);

	vector<BorrowOrder> orders = LoadBorrowOrders();
	orders.push_back(order);
	SaveBorrowOrders(orders);

	system("cls");
	cout << "Ваша заявка отправлена на рассмотрение" << endl;
	system("pause");
}

void ViewClientOrders(Account account)
{
	ClientInfo info = FindClientInfoByAccountId(account.id);
	vector<BorrowOrder> orders = LoadBorrowOrders();

	vector<BorrowOrder> clientOrders;
	for (int i = 0; i < orders.size(); i++)
	{
		if (orders[i].clientInfoId == info.id)
			clientOrders.push_back(orders[i]);
	}

	if (orders.empty())
	{
		system("cls");
		cout << "Пока что у вас нет ни одной заявки" << endl;
		system("pause");
		return;
	}

	bool work = true;
	while (work)
	{
		system("cls");
		PrintBorrowOrders(clientOrders);
		cout << endl;
		cout << "Выберите номер заявки для отмены или 0 для выхода:" << endl;
		int index = InputInt(0, clientOrders.size());
		if (index == 0)
		{
			work = false;
			continue;
		}

		index--;
		if (!Confirmation("Вы уверены что хотите отменить заявку?"))
			continue;

		for (int i = 0; i < orders.size(); i++)
		{
			if (orders[i].clientInfoId == clientOrders[index].clientInfoId &&
				orders[i].roomTypeId == clientOrders[index].roomTypeId &&
				CompareDate(orders[i].ordered, clientOrders[index].ordered) == 0 &&
				CompareDate(orders[i].start, clientOrders[index].start) == 0 &&
				CompareDate(orders[i].end, clientOrders[index].end) == 0 &&
				orders[i].comment == clientOrders[index].comment)
			{
				orders.erase(orders.begin() + i);
				break;
			}
		}
	}

	SaveBorrowOrders(orders);
}

void ViewClientRecords(Account account)
{
	ClientInfo info = FindClientInfoByAccountId(account.id);
	vector<BorrowRecord> allRecords = LoadBorrowRecords();

	vector<BorrowRecord> records;
	for (int i = 0; i < allRecords.size(); i++)
	{
		if (IsActive(allRecords[i]) && allRecords[i].clientInfoId == info.id)
			records.push_back(allRecords[i]);
	}

	if (records.empty())
	{
		system("cls");
		cout << "Пока что нет ни одной активной записи" << endl;
		system("pause");
		return;
	}

	bool work = true;
	while (work)
	{
		system("cls");
		cout << "1.Просмотр всех записей" << endl;
		cout << "2.Поиск записей" << endl;
		cout << "3.Сортировка записей" << endl;
		cout << "4.Фильтрация записей" << endl;
		cout << "5.Выход" << endl;

		switch (InputInt(1, 5))
		{
		case 1:
		{
			system("cls");
			PrintBorrowRecords(records);
			system("pause");
			break;
		}
		case 2:
		{
			system("cls");
			cout << "Введите строку или подстроку для поиска:" << endl;
			string search = InputLine();

			vector<BorrowRecord> found;
			for (int i = 0; i < records.size(); i++)
			{
				if (FindRoomById(records[i].roomId).number.find(search) != -1 ||
					DateToString(records[i].start).find(search) != -1 ||
					DateToString(records[i].end).find(search) != -1)
				{
					found.push_back(records[i]);
				}
			}

			system("cls");

			if (found.empty())
			{
				cout << "Ничего не найдено" << endl;
			}
			else
			{
				PrintBorrowRecords(found);
			}
			system("pause");

			break;
		}
		case 3:
		{
			system("cls");
			cout << "Выберите поле для сортировки:" << endl;
			cout << "1.Номер комнаты" << endl;
			cout << "2.Дата начала брони" << endl;
			cout << "3.Дата окончания брони" << endl;

			switch (InputInt(1, 3))
			{
			case 1:
			{
				for (int i = 0; i < records.size(); i++)
				{
					for (int j = i + 1; j < records.size(); j++)
					{
						if (FindRoomById(records[i].roomId).number.compare(FindRoomById(records[j].roomId).number) == 1)
						{
							swap(records[i], records[j]);
						}
					}
				}
				break;
			}
			case 2:
			{
				for (int i = 0; i < records.size(); i++)
				{
					for (int j = i + 1; j < records.size(); j++)
					{
						if (CompareDate(records[i].start, records[j].start) == 1)
						{
							swap(records[i], records[j]);
						}
					}
				}
				break;
			}
			case 3:
			{
				for (int i = 0; i < records.size(); i++)
				{
					for (int j = i + 1; j < records.size(); j++)
					{
						if (CompareDate(records[i].end, records[j].end) == 1)
						{
							swap(records[i], records[j]);
						}
					}
				}
				break;
			}
			}

			system("cls");
			PrintBorrowRecords(records);
			system("pause");

			break;
		}
		case 4:
		{
			system("cls");
			cout << "Выберите поле для фильтрации:" << endl;
			cout << "1.Дата начала брони" << endl;
			cout << "2.Дата окончания брони" << endl;

			vector<BorrowRecord> found;
			switch (InputInt(1, 4))
			{
			case 1:
			{
				cout << "Введите минимальную дату:" << endl;
				Date date1 = InputDate();
				cout << "Введите максимальную дату:" << endl;
				Date date2 = InputDate();
				for (int i = 0; i < records.size(); i++)
				{
					if (CompareDate(date2, records[i].start) >= 0 && CompareDate(date1, records[i].start) <= 0)
					{
						found.push_back(records[i]);
					}
				}
				break;
			}
			case 2:
			{
				cout << "Введите минимальную дату:" << endl;
				Date date1 = InputDate();
				cout << "Введите максимальную дату:" << endl;
				Date date2 = InputDate();
				for (int i = 0; i < records.size(); i++)
				{
					if (CompareDate(date2, records[i].end) >= 0 && CompareDate(date1, records[i].end) <= 0)
					{
						found.push_back(records[i]);
					}
				}
				break;
			}
			}
			system("cls");

			if (found.empty())
			{
				cout << "Ничего не найдено" << endl;
			}
			else
			{
				PrintBorrowRecords(found);
			}
			system("pause");

			break;
		}
		case 5:
		{
			work = false;
			break;
		}
		}
	}
}

void ViewClientHistory(Account account)
{
	ClientInfo info = FindClientInfoByAccountId(account.id);
	vector<BorrowRecord> allRecords = LoadBorrowRecords();

	vector<BorrowRecord> records;
	for (int i = 0; i < allRecords.size(); i++)
	{
		if (!IsActive(allRecords[i]) && allRecords[i].clientInfoId == info.id)
			records.push_back(allRecords[i]);
	}

	if (records.empty())
	{
		system("cls");
		cout << "Пока что нет ни одной активной записи" << endl;
		system("pause");
		return;
	}

	bool work = true;
	while (work)
	{
		system("cls");
		cout << "1.Просмотр всех записей" << endl;
		cout << "2.Поиск записей" << endl;
		cout << "3.Сортировка записей" << endl;
		cout << "4.Фильтрация записей" << endl;
		cout << "5.Выход" << endl;

		switch (InputInt(1, 5))
		{
		case 1:
		{
			system("cls");
			PrintBorrowRecords(records);
			system("pause");
			break;
		}
		case 2:
		{
			system("cls");
			cout << "Введите строку или подстроку для поиска:" << endl;
			string search = InputLine();

			vector<BorrowRecord> found;
			for (int i = 0; i < records.size(); i++)
			{
				if (FindRoomById(records[i].roomId).number.find(search) != -1 ||
					DateToString(records[i].start).find(search) != -1 ||
					DateToString(records[i].end).find(search) != -1)
				{
					found.push_back(records[i]);
				}
			}

			system("cls");

			if (found.empty())
			{
				cout << "Ничего не найдено" << endl;
			}
			else
			{
				PrintBorrowRecords(found);
			}
			system("pause");

			break;
		}
		case 3:
		{
			system("cls");
			cout << "Выберите поле для сортировки:" << endl;
			cout << "1.Номер комнаты" << endl;
			cout << "2.Дата начала брони" << endl;
			cout << "3.Дата окончания брони" << endl;

			switch (InputInt(1, 3))
			{
			case 1:
			{
				for (int i = 0; i < records.size(); i++)
				{
					for (int j = i + 1; j < records.size(); j++)
					{
						if (FindRoomById(records[i].roomId).number.compare(FindRoomById(records[j].roomId).number) == 1)
						{
							swap(records[i], records[j]);
						}
					}
				}
				break;
			}
			case 2:
			{
				for (int i = 0; i < records.size(); i++)
				{
					for (int j = i + 1; j < records.size(); j++)
					{
						if (CompareDate(records[i].start, records[j].start) == 1)
						{
							swap(records[i], records[j]);
						}
					}
				}
				break;
			}
			case 3:
			{
				for (int i = 0; i < records.size(); i++)
				{
					for (int j = i + 1; j < records.size(); j++)
					{
						if (CompareDate(records[i].end, records[j].end) == 1)
						{
							swap(records[i], records[j]);
						}
					}
				}
				break;
			}
			}

			system("cls");
			PrintBorrowRecords(records);
			system("pause");

			break;
		}
		case 4:
		{
			system("cls");
			cout << "Выберите поле для фильтрации:" << endl;
			cout << "1.Дата начала брони" << endl;
			cout << "2.Дата окончания брони" << endl;

			vector<BorrowRecord> found;
			switch (InputInt(1, 4))
			{
			case 1:
			{
				cout << "Введите минимальную дату:" << endl;
				Date date1 = InputDate();
				cout << "Введите максимальную дату:" << endl;
				Date date2 = InputDate();
				for (int i = 0; i < records.size(); i++)
				{
					if (CompareDate(date2, records[i].start) >= 0 && CompareDate(date1, records[i].start) <= 0)
					{
						found.push_back(records[i]);
					}
				}
				break;
			}
			case 2:
			{
				cout << "Введите минимальную дату:" << endl;
				Date date1 = InputDate();
				cout << "Введите максимальную дату:" << endl;
				Date date2 = InputDate();
				for (int i = 0; i < records.size(); i++)
				{
					if (CompareDate(date2, records[i].end) >= 0 && CompareDate(date1, records[i].end) <= 0)
					{
						found.push_back(records[i]);
					}
				}
				break;
			}
			}
			system("cls");

			if (found.empty())
			{
				cout << "Ничего не найдено" << endl;
			}
			else
			{
				PrintBorrowRecords(found);
			}
			system("pause");

			break;
		}
		case 5:
		{
			work = false;
			break;
		}
		}
	}
}
