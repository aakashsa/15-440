package lib;

import java.io.File;
import java.lang.reflect.ParameterizedType;

import interfaces.InputFormat;

/**
 * A Utils class that has useful utils functions.
 */
public class Utils {

	/**
	 * A function that performs sanity checks on the configurations of a given job
	 * @param job - job to check
	 */
	public static void performJobSanityChecks(Job job) {
		Job newJob = new Job();

		// Check if all needed things are provided
		if (job.getJobName() == null)
			newJob.setJobName("job");
		else
			newJob.setJobName(job.getJobName());

		if (job.getMapperClass() == null)
			throw new IllegalArgumentException("No mapper class provided");
		else
			newJob.setMapperClass(job.getMapperClass());

		if (job.getReducerClass() == null)
			throw new IllegalArgumentException("No reducer class provided");
		else
			newJob.setReducerClass(job.getReducerClass());

		if (job.getFileInputFormatClass() == null)
			throw new IllegalArgumentException(
					"No file input format class provided");
		else
			newJob.setFileInputFormatClass(job.getFileInputFormatClass());

		if (job.getMapperOutputKeyClass() == null)
			throw new IllegalArgumentException(
					"No mapper output key class provided");
		else
			newJob.setMapperOutputKeyClass(job.getMapperOutputKeyClass());

		if (job.getMapperOutputValueClass() == null)
			throw new IllegalArgumentException(
					"No mapper output value class provided");
		else
			newJob.setMapperOutputValueClass(job.getMapperOutputValueClass());

		if (job.getReducerOutputKeyClass() == null)
			throw new IllegalArgumentException(
					"No reducer output key class provided");
		else
			newJob.setReducerOutputKeyClass(job.getReducerOutputKeyClass());

		if (job.getReducerOutputValueClass() == null)
			throw new IllegalArgumentException(
					"No reducer output value class provided");
		else
			newJob.setReducerOutputValueClass(job.getReducerOutputValueClass());

		// Get types of actual Mapper and Reducer
		ParameterizedType pt = (ParameterizedType) newJob.getMapperClass()
				.getGenericInterfaces()[0];

		String mapK1 = ((Class<?>) pt.getActualTypeArguments()[0]).getName();
		String mapV1 = ((Class<?>) pt.getActualTypeArguments()[1]).getName();
		String mapK2 = ((Class<?>) pt.getActualTypeArguments()[2]).getName();
		String mapV2 = ((Class<?>) pt.getActualTypeArguments()[3]).getName();

		pt = (ParameterizedType) newJob.getReducerClass()
				.getGenericInterfaces()[0];

		String reducerK1 = ((Class<?>) pt.getActualTypeArguments()[0]).getName();
		String reducerV1 = ((Class<?>) pt.getActualTypeArguments()[1]).getName();
		String reducerK2 = ((Class<?>) pt.getActualTypeArguments()[2]).getName();
		String reducerV2 = ((Class<?>) pt.getActualTypeArguments()[3]).getName();

		try {
			// Check if mapper input types match that required by file input
			// format
			InputFormat<?, ?> format = (InputFormat<?, ?>) job
					.getFileInputFormatClass().newInstance();
			if (!(format.getKeyType().equals(mapK1) && format.getValueType()
					.equals(mapV1))) {
				throw new IllegalArgumentException("File input format key value type (" + format.getKeyType() + ", " + format.getValueType() + ") don't match input key value types of mapper");
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		// Check if the types user specified are the same as the actual mapper
		// and reducer
		if (!(mapK2.equals(newJob.getMapperOutputKeyClass().getName()))) {
			throw new IllegalArgumentException(
					"Actual Mapper output key type and that provided in configuration don't match. Expected: "
							+ mapK2
							+ "; Actual: "
							+ "lib."
							+ newJob.getMapperOutputKeyClass().getName());
		}
		if (!(mapV2.equals(newJob.getMapperOutputValueClass().getName()))) {
			throw new IllegalArgumentException(
					"Actual Mapper output value type and that provided in configuration don't match");
		}
		if (!(reducerK2.equals(newJob.getReducerOutputKeyClass().getName()))) {
			throw new IllegalArgumentException(
					"Actual Reducer output key type and that provided in configuration don't match");
		}
		if (!(reducerV2.equals(newJob.getReducerOutputValueClass().getName()))) {
			throw new IllegalArgumentException(
					"Actual Reducer output value type and that provided in configuration don't match");
		}

		// Check if output types of mapper are same as input types of reducer
		if (!(mapK2.equals(reducerK1))) {
			throw new IllegalArgumentException(
					"Mapper output key type does not match reducer input key type");
		}
		if (!(mapV2.equals(reducerV1))) {
			throw new IllegalArgumentException(
					"Mapper output value type does not match reducer input value type");
		}

		// Check if anything else except mapper input key class is NullWritable
		if (mapK2.equals(NullWritable.class.getName())
				|| mapV2.equals(NullWritable.class.getName())
				|| reducerK2.equals(NullWritable.class.getName())
				|| reducerV2.equals(NullWritable.class.getName())) {
			throw new IllegalArgumentException(
					"Input/output key/value types (except Mapper input key type) cannot be NullWritable");
		}
	}

	/**
	 * A function to remove a directory and files in it.
	 * @param directory - directory to remove
	 * @return whether the directory was deleted successfully or not
	 */
	public static boolean removeDirectory(File directory) {

		// System.out.println("removeDirectory " + directory);

		if (directory == null)
			return false;
		if (!directory.exists())
			return true;
		if (!directory.isDirectory())
			return false;

		String[] list = directory.list();

		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				File entry = new File(directory, list[i]);

				if (entry.isDirectory()) {
					if (!removeDirectory(entry))
						return false;
				} else {
					if (!entry.delete())
						return false;
				}
			}
		}

		return directory.delete();
	}

}
