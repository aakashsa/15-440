package communication;

import java.io.Serializable;

public class MessageClass implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int chunkNumber;
	private int chunkSize;
	private int recordSize;
	private String fileName;

	public MessageClass(int chunkNumber, int chunkSize, int recordSize,
			String fileName) {
		this.chunkNumber = chunkNumber;
		this.chunkSize = chunkSize;
		this.recordSize = recordSize;
		this.fileName = fileName;
	}

	public int getChunkNumber() {
		return chunkNumber;
	}

	public int getChunkSize() {
		return chunkSize;
	}

	public int getRecordSize() {
		return recordSize;
	}

	public String getFileName() {
		return fileName;
	}

}
