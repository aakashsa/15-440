package test2;

import interfaces.JobConfiguration;
import lib.Job;
import lib.KeyValueInputFormat;
import lib.TextWritable;

/**
 * Collating Job. Maps documents to words in the document.
 */
public class JobSetupClass implements JobConfiguration{

	/**
	 * Collate job setup function
	 */
	public Job setup() {

		Job job = new Job();
		job.setJobName("collating");

		job.setFileInputFormatClass(KeyValueInputFormat.class);

		job.setMapperClass(CollatingMapper.class);
		job.setReducerClass(CollatingReducer.class);

		job.setMapperOutputKeyClass(TextWritable.class);
		job.setMapperOutputValueClass(TextWritable.class);
		job.setReducerOutputKeyClass(TextWritable.class);
		job.setReducerOutputValueClass(TextWritable.class);

		job.setRecordSize(10);
		job.setChunkSize(200);
		job.setMapperOutputRecordSize(30);
		job.setNumReducers(2);
		job.setMapTimeout(5000);
		job.setReduceTimeout(150000);
		
		return job;
	}
}
