package communication;

import java.io.Serializable;

/**
 * A class that encapsulates a chunk of the input file for each
 * map task. This object is serializable.
 */
public class ChunkObject implements Serializable {
	
	/**
	 * serial ID
	 */
	private static final long serialVersionUID = 265849063821656550L;

	/**
	 * Number of this chunk (like a chunk ID)
	 */
	private int chunkNumber;
	
	/**
	 * Record this chunk starts at
	 */
	private long startingRecord;
	
	/**
	 * Number of records in this chunk
	 */
	private long numRecordsChunk;
	
	/**
	 * Size of each record
	 */
	private int recordSize;
	
	/**
	 * Filename to read chunks from
	 */
	private String fileName;

	/**
	 * Constructor that makes private copies of arguments.
	 * @param chunkNumber Chunk ID
	 * @param l Starting record number
	 * @param numRecordsPerChunk Number of records in this chunk
	 * @param recordSize Size of each record (in bytes)
	 * @param fileName File name
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
	 * @param obj Other chunk to compare to
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
	 * Get the starting record number
	 * @return the startingRecord
	 */
	public long getStartingRecord() {
		return startingRecord;
	}

	/**
	 * Get number of records in this chunk
	 * @return the numRecordsChunk
	 */
	public long getNumRecordsChunk() {
		return numRecordsChunk;
	}

	/**
	 * Get the record size
	 * @return the recordSize
	 */
	public int getRecordSize() {
		return recordSize;
	}

	/**
	 * Get the file name that this chunk belongs to
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Get the chunk number of this chunk
	 * @return the chunkNumber
	 */
	public int getChunkNumber() {
		return chunkNumber;
	}
}
