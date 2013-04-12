package nodework;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import communication.ChunkObject;
import communication.MessageType;
import communication.ReduceTask;

/**
 *  A Class Encapsulating all the stuff that is common between a job Threads and its child threads 
 * 
 * 
 */
public class JobThreadSharedFields {

	private Object MAPCOUNTER_LOCK;	
	private int mapsDone;
	private ConcurrentLinkedQueue<ChunkObject> chunkQueue;
	private ConcurrentHashMap<ChunkObject, Integer> chunkWorkerMap;
	private ConcurrentHashMap<ReduceTask, Integer> reduceWorkerMap;
	private ConcurrentLinkedQueue<MessageType> reduceDoneMessages;
	
	public JobThreadSharedFields(ConcurrentLinkedQueue<ChunkObject> chunkQueue, ConcurrentHashMap<ChunkObject, Integer> chunkWorkerMap, ConcurrentHashMap<ReduceTask, Integer> reduceWorkerMap, ConcurrentLinkedQueue<MessageType> reduceDoneMessages){
		this.MAPCOUNTER_LOCK = new Object();
		this.mapsDone = 0;
		this.chunkQueue = chunkQueue;
		this.chunkWorkerMap = chunkWorkerMap;
		this.reduceWorkerMap = reduceWorkerMap;
		this.reduceDoneMessages = reduceDoneMessages;
	}
	/**
	 * 	Maps Done Counter For this Thread
	 */
	public int getMapCounter (){
		return mapsDone;
	}
	
	public void setMapCounter(int set){
		this.mapsDone = set;
	}
	/**
	 * 	Increment Maps Done Counter For this Thread
	 */
	public void incrementMapCounter(){
		this.mapsDone++;
	}
	
	/** Getting the Lock Object for the map counter
	 * 
	 * @return Counter Lock
	 */
	public Object getMapCounterLock(){
		return MAPCOUNTER_LOCK;
	}
	
	/** Chunk Queue For this Job
	 * 
	 * @return ConcurrentLinkedQueue
	 */
	public ConcurrentLinkedQueue<ChunkObject> getChunkQueue(){
		return chunkQueue;
	}

	/**
	 *  Chunk to Worker Map For this Job
	 * 
	 * @return chunkWorkerMap
	 */
	public ConcurrentHashMap<ChunkObject, Integer> getchunkWorkerMap(){
		return chunkWorkerMap;
	}

	/**
	 *  Reduce Task to Worker Map For this Job
	 * 
	 * @return chunkWorkerMap
	 */
	public ConcurrentHashMap<ReduceTask, Integer> getReduceWorkerMap(){
		return reduceWorkerMap;
	}
	
	/**
	 * Map For Recording the Reduce Task's Done
	 * @return reduceDone Linked Queue - Linked Queue Holding Reducers Acks's
	 */
	public ConcurrentLinkedQueue<MessageType> getReduceDoneMap(){
		return reduceDoneMessages;
	}
}
