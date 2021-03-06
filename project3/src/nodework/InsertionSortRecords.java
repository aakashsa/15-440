package nodework;

import interfaces.Writable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Class that performs insertion sort on a file with fixed structure.
 */
public class InsertionSortRecords {

	private Class<?> keyClass = null;
	private int recordSize;
	private String fileName;
	private String kvDelimiter;

	/**
	 * @param keyClass Class of Key's Written 
	 * @param recordSize  Record Size
	 * @param fileName Name of File to be Sorted
	 */
	public InsertionSortRecords(Class<?> keyClass, int recordSize, String kvDelimiter, String fileName) {
		this.keyClass = keyClass;
		this.recordSize = recordSize;
		this.fileName = fileName;
		this.kvDelimiter = kvDelimiter;
	}

	/**
	 * Function to Compare two K2,V2 record strings
	 * @param record1 Record number 1 
	 * @param record2 Record number 2 
	 */
	public int compareRecords(String record1, String record2) {
		
		// Splitting Records to Key Value
		String[] keyValue1 = record1.split(this.kvDelimiter);
		String[] keyValue2 = record2.split(this.kvDelimiter);
		Writable<?> key1 = null;
		Writable<?> key2 = null;
		try {
			key1 = (Writable<?>) keyClass.newInstance();
			key2 = (Writable<?>) keyClass.newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		// Parsing the Writable from the string
		key1.parseFromString(keyValue1[0]);
		key2.parseFromString(keyValue2[0]);

		// Comparing the two Writable Values
		return key1.compareTo(key2.getValue());
	}

	/**
	 * The sort function
	 */
	public void sort() {
		RandomAccessFile rin = null;
		try {
			rin = new RandomAccessFile(fileName, "rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		long i = 1, j = -1;
		int read = 0;
		while (true) {
			byte[] toInsert = new byte[recordSize];
			byte[] jRecord = new byte[recordSize];
			try {
				rin.seek(i * recordSize);
				read = rin.read(toInsert, 0, recordSize);

				// If reached the End of File Break Out
				if (read < 0  || read < recordSize ) {
					break;
				}
				// Start at i -1 record and swap until order restored
				j = i - 1;
				while (j >= 0) {
					rin.seek((j) * recordSize);
					rin.read(jRecord, 0, recordSize);
					// If i'th record is less than the j'th record then swap
					// else break
					if (compareRecords(new String(toInsert),new String(jRecord)) < 0) {
						rin.seek((j) * recordSize);
						rin.write((toInsert), 0, recordSize);
						rin.seek((j + 1) * recordSize);
						rin.write((jRecord), 0, recordSize);
						j--;
					} else
						break;
				}
				// Move on to the next Record
				i++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
