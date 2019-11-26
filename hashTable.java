package test;
import java.util.Vector;

public class hashTable<V> 
{
	private static class HashEntry<V>
	{
		private String key;
		private Vector<V> value = new Vector<V>();
		private boolean isDeleted = false;

		private HashEntry(String insertKey, V insertValue)
		{
			key = insertKey;
			value.add(insertValue);
		}
	}
	
	private final int[] SIZES = { 1019, 2027, 4079, 8123, 16267, 32503, 65011,
		130027, 260111, 520279, 1040387, 2080763, 4161539, 8323151, 16646323 };
	
	private int sizeIdx = 0;
	private HashEntry<V>[] table;
	private int numEntries = 0;
	private int numFilledSlots = 0;
	private int numProbes = 0;


	@SuppressWarnings("unchecked")
	public hashTable()
	{
		table = new HashEntry[SIZES[sizeIdx]];
	}

	@SuppressWarnings("unchecked")
	private void increaseCapacity()
	{
		HashEntry<V>[] oldTable = table;
		
		table = new HashEntry[SIZES[++sizeIdx]];
		
		for (int i = 0; i < oldTable.length; ++i)
		{
			if (oldTable[i] != null && !oldTable[i].isDeleted)
			{
				for (V value : oldTable[i].value)
				{
					insert(oldTable[i].key, value);
				}
			}
		}
	}

	public boolean insert(String key, V value)
	{
		int size = SIZES[sizeIdx];
		int i;
		numProbes = 0;

		if (numFilledSlots > 0.75 * size)
		{
			increaseCapacity();
			size = SIZES[sizeIdx];
		}

		for (i = 0; i < size; ++i)
		{
			int index = probe(key, i, size);

			if (table[index] == null || table[index].isDeleted)
			{
				table[index] = new HashEntry<V>(key, value);
				++numEntries;
				++numFilledSlots;
				numProbes = i;

				return true;
			}
			
			else if (table[index].key.equals(key) && !table[index].isDeleted)
			{
				table[index].value.add(value);
				++numEntries;
				numProbes = i;

				return true;
			}
		}

		numProbes = i - 1;

		return false;
	}

	private int probe(String key, int i, int size)
	{
		return (hash(key) + ((int) (Math.pow(i, 2) + i) >> 2)) % size;
	}

	public int getNumProbes()
	{
		return numProbes;
	}

	public Vector<V> find(String key)
	{
		int size = SIZES[sizeIdx];

		for (int i = 0; i < size; ++i)
		{
			int index = probe(key, i, size);

			if (table[index] == null)
			{
				return null;
			}
			else if (table[index].key.equals(key) && !table[index].isDeleted)
			{
				return table[index].value;
			}
		}

		return null;
	}

	public boolean delete(String key)
	{
		int size = SIZES[sizeIdx];

		for (int i = 0; i < size; ++i)
		{
			int index = probe(key, i, size);

			if (table[index] == null)
			{
				return false;
			}
			
			else if (table[index].key.equals(key) && !table[index].isDeleted)
			{
				table[index].isDeleted = true;

				return true;
			}
		}

		return false;
	}

	// available to @Override
	public int hash(String key)
	{
		int hashValue = 0;

		for (int pos = 0; pos < key.length(); ++pos)
		{
			hashValue = (hashValue << 4) + key.charAt(pos);
			int highBits = hashValue & 0xF0000000;

			if (highBits != 0)
			{
				hashValue ^= highBits >> 24;
			}
			hashValue &= ~highBits;
		}

		return hashValue;
	}
	
	// available to @Override
	// compare two hashed keys (index in table) (type: int)
	public int compare(int index1, int index2)
	{
		if ((index1 - index2) > 0)
			return 1;
		
		else if ((index1 - index2) == 0)
			return 0;
	
		else
			return -1;
	}
}