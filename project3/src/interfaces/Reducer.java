package interfaces;

import java.util.Iterator;

import lib.Context;


/**
 * The reducer interface.
 *
 * @param <K1> Input key type
 * @param <V1> Input value type
 * @param <K2> Output key type
 * @param <V2> Output value type
 */
public interface Reducer <K1 extends Writable<?>, V1 extends Writable<?>, K2 extends Writable<?>, V2  extends Writable<?>> {

	/**
	 * The reduce function
	 * @param key - key
	 * @param values - value iterator
	 * @param context - context to write results of reduce to
	 */
	public void reduce(K2 key, Iterator<V2> values, Context<K2, V2> context);

}
