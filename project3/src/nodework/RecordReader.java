package nodework;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import mapper.NaiveMapper;

public class RecordReader {

	private static RandomAccessFile rin;

	/*
	 * Reading a Particular Chunk from a particular file with a given path and
	 * recordSize
	 */
	public static void readChunk(int chunkNum, int chunkSize, int recordSize,
			String filePath) {
		try {
			rin = new RandomAccessFile(filePath, "r");
			byte[] b = new byte[chunkSize];
			int skip = rin.skipBytes(chunkNum*chunkSize);
			int readSize = rin.read(b,0,chunkSize);
			System.out.println("ReadSize = " + readSize);
			int j = 0;
			for (int i = 0; i < readSize; i += recordSize) {
				System.out.println("Record Number = " + j);
				byte[] recordBytes = Arrays.copyOfRange(b, i, i + recordSize);
				// Check for Encoding Characters
				String value = new String(recordBytes);
//				System.out.println("/*****************************************************************************/\n\n\n");
//				System.out.println(" Key = " + j);
//				System.out.println(" Value we got = \n" + value);
//				System.out.println("/*****************************************************************************/\n\n\n");
				NaiveMapper mapper = new NaiveMapper();
				mapper.map(j, value);
				j++;
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}