package test;

import interfaces.JobConfiguration;
import lib.IntWritable;
import lib.Job;
import lib.TextInputFormat;
import lib.TextWritable;

/**
 * A setup class for the word count job
 */
public class JobSetupClass implements JobConfiguration{

	/**
	 * Setup function for wordcount job
	 */
	public Job setup() {
				
		Job job = new Job();
		job.setJobName("wordcount");
		
		job.setFileInputFormatClass(TextInputFormat.class);
		
		job.setMapperClassName("WordCountMapper");
		job.setReducerClassName("WordCountReducer");
		
		job.setMapperOutputKeyClass(TextWritable.class);
		job.setMapperOutputValueClass(IntWritable.class);
		job.setReducerOutputKeyClass(TextWritable.class);
		job.setReducerOutputValueClass(IntWritable.class);
		
		job.setRecordSize(17);
		job.setChunkSize(340);
		job.setMapperOutputRecordSize(40);
		job.setNumReducers(2);
		job.setMapTimeout(10000);
		job.setReduceTimeout(150000);
		
		return job;
	}
	
}
