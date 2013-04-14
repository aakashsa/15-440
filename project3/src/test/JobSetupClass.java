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
		
		job.setMapperClass(WordCountMapper.class);
		job.setReducerClass(WordCountReducer.class);
		
		job.setMapperOutputKeyClass(TextWritable.class);
		job.setMapperOutputValueClass(IntWritable.class);
		job.setReducerOutputKeyClass(TextWritable.class);
		job.setReducerOutputValueClass(IntWritable.class);
		
		job.setRecordSize(22);
		job.setChunkSize(2200);
		job.setMapperOutputRecordSize(40);
		job.setNumReducers(2);
		
		return job;
	}
	
}
