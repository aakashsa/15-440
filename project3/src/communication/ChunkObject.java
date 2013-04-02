package communication;

import java.io.Serializable;

/**
 * A class that encapsulates a chunk of the input file for each
 * map task.
 */
public class ChunkObject implements Serializable{
	
	// serial ID
	private static final long serialVersionUID = 265849063821656550L;

	// number of this chunk (like a chunk ID)
	private int chunkNumber;
	
	// record this chunk starts at
	private long startingRecord;
	
	// number of records in this chunk
	private long numRecordsChunk;
	
	// size of each record
	private int recordSize;
	
	// filename to read chunks from
	private String fileName;

	/**
	 * Constructor that makes private copies of arguments.
	 * @param chunkNumber
	 * @param l
	 * @param numRecordsPerChunk
	 * @param recordSize
	 * @param fileName
	 */
	public ChunkObject(int chunkNumber, long l,
			long numRecordsPerChunk, int recordSize, String fileName) {
		this.startingRecord = l;
		this.numRecordsChunk = numRecordsPerChunk;
		this.recordSize = recordSize;
		this.fileName = fileName;
		this.chunkNumber = chunkNumber;
	}

	/**
	 * Check if two chunks are equal
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof ChunkObject))
			return false;

		final ChunkObject other = (ChunkObject) obj;
		if (chunkNumber != other.getChunkNumber())
			return false;
		if (startingRecord != other.getStartingRecord())
			return false;
		if (numRecordsChunk != other.getNumRecordsChunk())
			return false;
		if (recordSize != other.getRecordSize())
			return false;
		if (!fileName.equals(other.getFileName()))
			return false;
		return true;
	}

	/**
	 * @return the startingRecord
	 */
	public long getStartingRecord() {
		return startingRecord;
	}

	/**
	 * @param startingRecord
	 */
	public void setStartingRecord(int startingRecord) {
		this.startingRecord = startingRecord;
	}

	/**
	 * @return the numRecordsChunk
	 */
	public long getNumRecordsChunk() {
		return numRecordsChunk;
	}

	/**
	 * @param numRecordsChunk
	 */
	public void setNumRecordsChunk(int numRecordsChunk) {
		this.numRecordsChunk = numRecordsChunk;
	}

	/**
	 * @return the recordSize
	 */
	public int getRecordSize() {
		return recordSize;
	}

	/**
	 * @param recordSize
	 */
	public void setRecordSize(int recordSize) {
		this.recordSize = recordSize;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the chunkNumber
	 */
	public int getChunkNumber() {
		return chunkNumber;
	}

	/**
	 * @param chunkNumber
	 */
	public void setChunkNumber(int chunkNumber) {
		this.chunkNumber = chunkNumber;
	}
}
