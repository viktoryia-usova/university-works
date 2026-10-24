#include<iostream>
#include<functional>

using namespace std;

template<class T>
class Vector
{
public:

	class iterator
	{
	private:

		T* element;

	public:

		iterator()
		{
			this->element = NULL;
		}

		iterator(T* element)
		{
			this->element = element;
		}

		T& operator *()
		{
			return *(element);
		}

		iterator operator + (int offset) const
		{
			return iterator(element + offset);
		}

		iterator operator - (int offset)const
		{
			return iterator(element - offset);
		}

		T* operator ->()
		{
			return element;
		}

		void operator = (const iterator& other)
		{
			this->element = other.element;
		}

		void operator += (int offset)
		{
			this->element += offset;
		}

		bool operator != (const iterator& other) const
		{
			return this->element != other.element;
		}

		bool operator == (const iterator& other) const
		{
			return this->element == other.element;
		}

		void operator ++ ()
		{
			this->element++;
		}

		void operator ++(int a)
		{
			this->element++;
		}

	};

public:
	Vector()
	{
		_size = 0;
		capacity = 10;
		arr = new T[capacity];
	}
	Vector(size_t size)
	{
		this->capacity = size * 1.5;
		this->_size = size;
		arr = new T[capacity];
	}
	Vector(size_t size, const T& cont) : Vector(size)
	{
		for (size_t i = 0; i < _size; i++)
		{
			arr[i] = cont;
		}
	}
	Vector(const Vector& other)
	{
		_size = other._size;
		capacity = other.capacity;
		arr = new T[capacity];
		for (size_t i = 0; i < _size; i++)
		{
			arr[i] = other.arr[i];
		}
	}

public:

	~Vector()
	{
		if (arr)
		{
			delete[] arr;
			arr = NULL;
		}
	}

public:

	void push_back(const T& value)
	{
		if (capacity > _size)
		{
			arr[_size++] = value;
		}
		else
		{
			capacity += 10;
			T* newMas = new T[capacity];
			for (int i = 0; i < _size; i++)
			{
				newMas[i] = arr[i];
			}
			delete[] arr;
			newMas[_size++] = value;
			arr = newMas;
		}
	}
	T pop_back()
	{
		if (_size != 0)
		{
			if (capacity / _size == 2)
			{
				shrink_to_fit();
			}
			return arr[--_size];
		}
	}

	void shrink_to_fit()
	{
		T* newMas = new T[_size];
		for (int i = 0; i < _size; i++)
		{
			newMas[i] = arr[i];
		}
		delete[] arr;
		capacity = _size;
		arr = newMas;
	}

	void push_front(const T& value)
	{
		if (_size == capacity)
		{
			push_back(arr[_size - 1]);
		}
		for (size_t i = _size - 1; i > 0; i--)
		{
			arr[i] = arr[i - 1];
		}
		arr[0] = value;
	}
	T pop_front()
	{
		T buf = arr[0];
		for (int i = 0; i < _size - 1; i++)
		{
			arr[i] = arr[i + 1];
		}
		_size--;
		return buf;
	}

	void clear()
	{
		capacity = 0;
		_size = 0;
		if (arr)
		{
			//delete[] arr;
			arr = NULL;
		}
	}

	void resize(const int& newSize)
	{
		if (newSize == this->_size)
		{
			return;
		}

		T* newMas = new T[newSize];
		if (newSize < _size)
		{
			for (size_t i = 0; i < newSize; i++)
			{
				newMas[i] = arr[i];
			}
		}
		else
		{
			for (size_t i = 0; i < _size; i++)
			{
				newMas[i] = arr[i];
			}
		}
		_size = newSize;
		delete[] arr;
		arr = newMas;
	}

	size_t size() const
	{
		return _size;
	}
	bool empty() const
	{
		return _size == 0;
	}

	void erase(int index)
	{
		T* newMas = new T[_size - 1];
		int j = 0;
		for (size_t i = 0; i < _size; i++)
		{
			if (i != index)
			{
				newMas[j++] = arr[i];
			}
		}
		if (arr)
		{
			//delete[]arr;
			//arr = NULL;
		}
		arr = newMas;
		_size--;
	}
	void erase(const Vector<T>::iterator& iterator)
	{
		T* newMas = new T[_size - 1];
		auto it = begin();
		int j = 0;
		for (size_t i = 0; i < _size; i++)
		{
			if (iterator != it + i)
			{
				newMas[j++] = arr[i];
			}
		}
		delete[]arr;
		arr = newMas;
		_size--;
	}

	Vector<T>::iterator begin()
	{
		return Vector<T>::iterator(arr);
	}
	Vector<T>::iterator end()
	{
		return Vector<T>::iterator(arr + _size);
	}

	T& operator [](const int& index)
	{
		return arr[index];
	}

	bool operator == (const Vector<T>& other)
	{
		if (_size != other._size)
		{
			return false;
		}
		for (size_t i = 0; i < _size; i++)
		{
			if (arr[i] != other.arr[i])
			{
				return false;
			}
		}
		return true;
	}
	void operator +=(const Vector<T>& other)
	{
		resize(_size + other._size);
		for (size_t i = _size; i < _size + other._size; i++)
		{
			arr[i] = other[i];
		}
	}
	Vector<T> operator + (const Vector<T>& other)
	{
		Vector<T> vec;
		for (size_t i = 0; i < this->_size; i++)
		{
			vec.push_back(arr[i]);
		}
		for (size_t i = 0; i < other._size(); i++)
		{
			vec.push_back(other[i]);
		}
		return vec;
	}
	void operator = (const Vector<T>& other)
	{
		_size = other._size;
		capacity = other.capacity;
		arr = new T[capacity];
		for (size_t i = 0; i < _size; i++)
		{
			arr[i] = other.arr[i];
		}
	}

	int find(const T& obj)
	{
		int index = -1;
		int i = 0;
		for (auto iter : arr)
		{
			if (obj == iter)index = i;
			i++;
		}
		return index;
	}
	friend void sort(Vector<T>& vec, function<bool(T t, T t2)> f)
	{
		int i, j, step;
		T tmp;
		for (step = vec.size() / 2; step > 0; step /= 2)
			for (i = step; i < vec.size(); i++)
			{
				tmp = vec[i];
				for (j = i; j >= step; j -= step)
				{
					if (f(tmp, vec[j - step]))
						vec[j] = vec[j - step];
					else
						break;
				}
				vec[j] = tmp;
			}
	}

private:

	int _size;
	int capacity;
	T* arr;
};