package interfaces;

import java.util.Iterator;

import nodework.Context;

public interface Reducer <K1 extends Writable<?>, V1 extends Writable<?>, K2 extends Writable<?>, V2  extends Writable<?>> {

	public void reduce(K2 key, Iterator<V2> values, Context<K2, V2> context);

}
