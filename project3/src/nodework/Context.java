package nodework;

import java.util.ArrayList;

public class Context<K, V> {

	private ArrayList<KeyValue<K,V>> items;
	
	public Context() {
		items = new ArrayList<KeyValue<K,V>>();
	}
	
	public void write(K key, V value) {
		items.add(new KeyValue<K,V>(key, value));
	}
	
	public ArrayList<KeyValue<K,V>> getAll() {
		return items;
	}
	
	public void clear() {
		items.clear();
	}
}