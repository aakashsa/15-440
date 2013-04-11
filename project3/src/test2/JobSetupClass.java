package test2;

import interfaces.JobConfiguration;
import lib.IntWritable;
import lib.Job;
import lib.KeyValueInputFormat;
import lib.TextWritable;

/*
 * Collating Job. Maps words to the Documents it belongs.
 */
public class JobSetupClass implements JobConfiguration{

	public Job[] setup() {

		Job[] jobs = new Job[1];

		Job job = new Job();
		job.setJobName("collating");

		job.setFileInputFormatClass(KeyValueInputFormat.class);
		job.setMapperClass(CollatingMapper.class);
		job.setReducerClass(CollatingReducer.class);

		job.setMapperOutputKeyClass(TextWritable.class);
		job.setMapperOutputValueClass(TextWritable.class);
		job.setReducerOutputKeyClass(TextWritable.class);
		job.setReducerOutputValueClass(TextWritable.class);
		jobs[0] = job;
		return jobs;
	}

}
