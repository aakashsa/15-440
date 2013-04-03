package nodework;

public class KeyValue<K,V> {

	private K key;
	private V value;
	
	public KeyValue(K key, V value) {
		this.key = key;
		this.value = value;
	}
	
	K getKey() {
		return key;
	}
	
	V getValue() {
		return value;
	}
	
}
