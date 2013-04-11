package test2;

import java.util.Iterator;

import lib.Context;
import lib.TextWritable;
import interfaces.Reducer;

/**
 * Collate job reducer
 */
public class CollatingReducer implements
		Reducer<TextWritable, TextWritable, TextWritable, TextWritable> {

	/**
	 * Collate job reduce function. This function just collects all the strings and
	 * appends them comma separated
	 */
	@Override
	public void reduce(TextWritable key, Iterator<TextWritable> values,
			Context<TextWritable, TextWritable> context) {
		String sum = "";
		while (values.hasNext()) {
			sum = sum + ", " + values.next().getValue();
		}
		context.write(key, new TextWritable(sum));
	}
}
