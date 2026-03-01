package assignment;

public class HashTable<K, V> implements InterfaceHashTable<K, V>{
	
	private Entry<K, V>[] hashTable;
	private int tableSize;
	private int tableCapacity;
	
	private boolean isSSF; // true: SSF / false: PAF
	private boolean isDH; // true: Double Hashing / false: Linear Probing
	private double maxLoadFactor;
	private static final int z = 33; // paf constant mersin :))
	
	private static final int DEFAULT_CAPACITY = 15;
	private static final double DEFAULT_LOAD_FACTOR = 0.5;
	private long collisionCount = 0;
	
	public HashTable() {
		this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR, true, false);
	}
	
	@SuppressWarnings("unchecked")
	public HashTable(int initialCapacity, double loadFactor, boolean isSSF, boolean isDH) {
		tableCapacity = goNextPrime(initialCapacity);
		
		this.maxLoadFactor = loadFactor;
		this.isSSF = isSSF;
		this.isDH = isDH;
		this.hashTable = (Entry<K, V>[]) new Entry[tableCapacity];
		this.tableSize = 0;
	}
	
	public V get(K key) {
		int index = hash(key);
		
		int next = 1;
		if(isDH){
			next = doubleHashing(key);
		}
		
		int noLoop = 0;
		while(hashTable[index] != null && noLoop < tableCapacity) {
			if(hashTable[index].getKey().equals(key)) {
				return hashTable[index].getValue();
			}
			
			
			index = (index + next) % tableCapacity;
			noLoop++;
		}
		
		return null; 
	}
	
	public int doubleHashing(K key) {
		int q = goPreviousPrime(tableCapacity);
		int k = 0;
		
		if(isSSF) {
			k = SSF(key);
			
			if(tableCapacity > 10000) {
				k = k * 31459;
			}
		} else {
			k = PAF(key);
		}
		
		return q - (k % q) == 0 ? 1 : q - (k % q);
	}
	public void add(K key, V value) {
		if((double) tableSize / tableCapacity >= maxLoadFactor) {
			rehash();
		}
		
		int index = hash(key);
		
		int next = 1; // FOR linear probing
		if(isDH) {
			next = doubleHashing(key); // for double hashing
		}
		
		int noLoop = 0;
		while(hashTable[index] != null && noLoop < tableCapacity) {
			if(hashTable[index].getKey().equals(key)) {
				hashTable[index].setValue(value);
				return;
			} else {
				collisionCount++;
			}
			
			index = (index + next) % tableCapacity;
			noLoop++;
		}
		
		hashTable[index] = new Entry<K, V>(key, value);
		tableSize++;
	}
	
	@SuppressWarnings("unchecked")
	public void rehash() {
		int oldCapacity = tableCapacity;
		
		Entry<K, V>[] oldTable = hashTable;
		
		tableCapacity = goNextPrime(oldCapacity * 2);
		
		hashTable = (Entry<K, V>[]) new Entry[tableCapacity];
		tableSize = 0;
		
		for(int i = 0; i < oldCapacity; i++) {
			if(oldTable[i] != null) {
				add(oldTable[i].getKey(), oldTable[i].getValue());
			}
		}
	}
	
	public int hash(K key) {
		int hashCode = 0;
		
		if(isSSF) {
			hashCode = SSF(key);
			
			if(tableCapacity > 10000) {
				hashCode = hashCode * 31459;
			}
		} else {
			hashCode = PAF(key);
		}
		
		hashCode = hashCode % tableCapacity; // for security !!!
		
		if(hashCode < 0) {
			hashCode += tableCapacity;
		}
		
		return hashCode;
	}
	
	public int SSF(K key) {
		String word = key.toString();
		
		int sum = 0;
		for(int i = 0; i < word.length(); i++) {
			sum += word.charAt(i);
		}
		
		return Math.abs(sum);
	}
	
	/*public int PAF(K key) { // maybe use this ?
		String word = key.toString();
		
		long sum = 0;
		
		for(int i = 0; i < word.length(); i++) {
			sum = (sum * z) + word.charAt(i); 
		}
		
		return Math.abs((int)(sum % Integer.MAX_VALUE));
	}*/

	public int PAF(K key) {
		String word = key.toString().toLowerCase();
		
		long sum = 0;
		for(int i = 0; i < word.length(); i++) {
			int value = 0;
			
			// case insensitive
			if(word.charAt(i) >= 'a' && word.charAt(i) <= 'z') {
				value = word.charAt(i) - 96;
			}
			sum = ((sum * z) + value) % Integer.MAX_VALUE;
		}
		
		return (int) Math.abs(sum);
	}
	
	private int goPreviousPrime(int x) {
		for(int i = x - 1; i >=2 ; i--) {
			if(isPrime(i)) return i;
		}
		
		return 2; // min prime number....:)))
	}
	
	private int goNextPrime(int x) {
		while(!isPrime(x)) {
			x++;
		}
		return x;
	}
	
	private boolean isPrime(int x) {
		if(x == 2) return true;
		if(x < 2) return false;
		if(x % 2 == 0) return false;
		
		for(int i = 3; i * i <= x; i += 2) {
			if(x % i == 0) return false;
		}
		
		return true;
	}
	
	public int getSize() {
		return tableSize;
	}
	
	public int getCapacity() {
		return tableCapacity;
	}
	
	public K getKeyAt(int index) {
		if(hashTable[index] != null) {
			return hashTable[index].getKey();
		}
		
		return null;
	}
	
	public long getCollisinCount() {
		return collisionCount;
	}
}
