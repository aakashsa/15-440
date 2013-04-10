package lib;

import interfaces.Writable;

import java.util.ArrayList;


/**
 * A context represents an object that the user writes its output of
 * key and value from mapper or reducer to. It's essentially a collector.
 * A write operation collects whatever the user wants to write. And 
 * after the map/reduce operation is writes things in the collection
 * to file for further processing
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public class Context<K extends Writable<?>, V extends Writable<?>> {

	/// list of items in the context
	private ArrayList<KeyValue<K,V>> items;
	
	/**
	 * Empty constructor to create a context
	 */
	public Context() {
		items = new ArrayList<KeyValue<K,V>>();
	}
	
	/**
	 * Write operation for a context
	 * @param key key to write
	 * @param value value to write
	 */
	public void write(K key, V value) {
		items.add(new KeyValue<K,V>(key, value));
	}
	
	/**
	 * Gets all the current key value pairs in the context
	 * @return An arraylist of all keys and values in the context
	 */
	public ArrayList<KeyValue<K,V>> getAll() {
		return items;
	}
	
	/**
	 * Clear the context
	 */
	public void clear() {
		items.clear();
	}
}