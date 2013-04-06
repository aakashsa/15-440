package test;

import lib.IntWritable;
import lib.Job;
import lib.TextInputFormat;
import lib.TextWritable;

public class JobConfiguration {

	public Job[] setup() {
		
		Job[] jobs = new Job[1];
		
		Job job = new Job();
		job.setJobName("wordcount");
		
		job.setFileInputFormatClass(TextInputFormat.class);
		
		job.setMapperClass(WordCountMapper.class);
		job.setReducerClass(WordCountReducer.class);
		
		job.setMapperOutputKeyClass(TextWritable.class);
		job.setMapperOutputValueClass(IntWritable.class);
		job.setReducerOutputKeyClass(TextWritable.class);
		job.setReducerOutputValueClass(IntWritable.class);
		
		jobs[0] = job;
		
		return jobs;
	}
	
}
