package interfaces;

import java.io.Serializable;

import nodework.Context;

public interface Mapper<K1 extends Serializable, V1 extends Serializable, K2 extends Serializable, V2  extends Serializable> {
	
	Context<K2,V2> map(K1 key, V1 value, Context<K2,V2> context);
	
}