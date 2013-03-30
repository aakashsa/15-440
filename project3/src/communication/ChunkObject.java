package communication;

import java.io.Serializable;


public class ChunkObject implements Serializable{
	private int chunkNumber;
	private int startingRecord;
	private int numRecordsChunk;
	private int recordSize;
	private String fileName;

	public ChunkObject(int chunkNumber, int startingRecord,
			int numRecordsChunk, int recordSize, String fileName) {
		this.startingRecord = startingRecord;
		this.numRecordsChunk = numRecordsChunk;
		this.recordSize = recordSize;
		this.fileName = fileName;
		this.chunkNumber = chunkNumber;
	}

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
	public int getStartingRecord() {
		return startingRecord;
	}

	/**
	 * @param startingRecord
	 *            the startingRecord to set
	 */
	public void setStartingRecord(int startingRecord) {
		this.startingRecord = startingRecord;
	}

	/**
	 * @return the numRecordsChunk
	 */
	public int getNumRecordsChunk() {
		return numRecordsChunk;
	}

	/**
	 * @param numRecordsChunk
	 *            the numRecordsChunk to set
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
	 *            the recordSize to set
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
	 *            the fileName to set
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
	 *            the chunkNumber to set
	 */
	public void setChunkNumber(int chunkNumber) {
		this.chunkNumber = chunkNumber;
	}
}
