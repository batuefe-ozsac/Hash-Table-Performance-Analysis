package assignment;

public interface InterfaceHashTable<K, V> {

	public V get(K key);
	
	// Returns the value of the point with the desired key value.
	
	public void add(K key, V value);
	
	// Adds the data to the map by assigning key and value parameters to it.
	
	public int doubleHashing(K key);
	
	// Tries to minimize the number of collisions by using a secondary hash function.
	
	public void rehash();
	
	// if the ratio of the table size to capacity is greater than the load factor, it performs the re-addition process by doubling the table's capacity.
	
	public int hash(K key);
	
	// Allows the codes coming from SSF or PAF methods to find the index to be placed in the table by taking the mode according to the capacity of the table.
	
	public int SSF(K key);
	
	// Collects the ASCII codes of all letters of the sent word and returns that value.
	
	public int PAF(K key);
	
	/* It uses Horner's Rule with a constant (z=33) to compute the hash efficiently.
	   Characters are mapped to values 1-26 (case-insensitive) to ensure better distribution.*/
	 
	
	public int getSize();
	
	// Returns the size of the table.
	
	public int getCapacity();
	
	// Returns the capacity of the table.
	
	public K getKeyAt(int index);
	
	// Returns the key of the sent index.
	
	public long getCollisinCount();
	
	// Returns the total collision count.
}
