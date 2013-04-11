package interfaces;

import lib.Context;

/**
 * Mapper interface
 *
 * @param <K1> Input key type
 * @param <V1> Input value type
 * @param <K2> Output key type
 * @param <V2> Output value type
 */
public interface Mapper<K1 extends Writable<?>, V1 extends Writable<?>, K2 extends Writable<?>, V2  extends Writable<?>> {
	
	/**
	 * The map function
	 * @param key Key
	 * @param value Value 
	 * @param context A context to write the mapped key,value pairs to
	 */
	void map(K1 key, V1 value, Context<K2,V2> context);
	
	/**
	 * An init function for convenience. This is called at the start of each map task.
	 */
	void init();
	
}