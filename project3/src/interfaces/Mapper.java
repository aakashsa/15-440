package interfaces;

import nodework.Context;

public interface Mapper<K1 extends Writable<?>, V1 extends Writable<?>, K2 extends Writable<?>, V2  extends Writable<?>> {
	
	Context<K2,V2> map(K1 key, V1 value, Context<K2,V2> context);
	
}