package interfaces;

public interface Mapper<K1, V1, K2, V2> {
	void map(K1 key, V1 value);
}