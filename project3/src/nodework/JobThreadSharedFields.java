package nodework;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import communication.ChunkObject;

// A Class Encapsulating all the stuff that is common between a job Threads and its child threads 
public class JobThreadSharedFields {

	private Object MAPCOUNTER_LOCK;	
	private int mapsDone;
	private ConcurrentLinkedQueue<ChunkObject> chunkQueue;
	private ConcurrentHashMap<ChunkObject, Integer> chunkWorkerMap;
	
	public JobThreadSharedFields(ConcurrentLinkedQueue<ChunkObject> chunkQueue, ConcurrentHashMap<ChunkObject, Integer> chunkWorkerMap){
		this.MAPCOUNTER_LOCK = new Object();
		this.mapsDone = 0;
		this.chunkQueue = chunkQueue;
		this.chunkWorkerMap = chunkWorkerMap;
	}
	// Maps Done Counter For this Thread
	public int getMapCounter (){
		return mapsDone;
	}
	
	public void setMapCounter(int set){
		this.mapsDone = set;
	}
	//Increment Maps Done Counter For this Thread
	public void incrementMapCounter(){
		this.mapsDone++;
	}
	
	// Getting the Lock Object for the map counter
	public Object getMapCounterLock(){
		return MAPCOUNTER_LOCK;
	}
	// Chunk Queue For this Job
	public ConcurrentLinkedQueue<ChunkObject> getChunkQueue(){
		return chunkQueue;
	}
	
	// Chunk to Worker Map For this Job
	public ConcurrentHashMap<ChunkObject, Integer> getchunkWorkerMap(){
		return chunkWorkerMap;
	}

}
