package lib;

/**
 * A type that reprsents a tuple for a key and a value
 * 
 * @param <K> Key type
 * @param <V> Value type
 */
public class KeyValue<K,V> {

	private K key;
	private V value;
	
	/**
	 * Constructor for a key value pair
	 * @param key Key
	 * @param value Value
	 */
	public KeyValue(K key, V value) {
		this.key = key;
		this.value = value;
	}
	/**
	 * Get key
	 * @return Key
	 */
	public K getKey() {
		return key;
	}
	
	/**
	 * Get value
	 * @return Value
	 */
	public V getValue() {
		return value;
	}
	
}
