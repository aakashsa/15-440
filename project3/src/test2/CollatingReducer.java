package test2;

import java.util.Iterator;

import lib.Context;
import lib.TextWritable;
import interfaces.Reducer;

public class CollatingReducer implements
		Reducer<TextWritable, TextWritable, TextWritable, TextWritable> {

	@Override
	public void reduce(TextWritable key, Iterator<TextWritable> values,
			Context<TextWritable, TextWritable> context) {
		String sum = "";
		while (values.hasNext()) {
			sum = sum + " , " + values.next().getValue();
		}
		context.write(key, new TextWritable(sum));
	}
}
