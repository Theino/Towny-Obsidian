import java.util.*;

@SuppressWarnings("hiding")
public class BlockQueue {
	private LinkedList<Object> queue = new LinkedList<Object>();
	private static volatile BlockQueue instance;
	private BlockThread thread;
	
	public synchronized void addWork(Object obj) {
		queue.addLast(obj);
		notify();
    }

    public synchronized Object getWork() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        return queue.removeFirst();
    }
	
	public static BlockQueue getInstance() {
		if (instance == null) {
			instance = new BlockQueue();
			instance.thread = new BlockThread(instance);
			instance.thread.start();
		}
		
		return instance;
	}
	
	public BlockThread getThread() {
		return thread;
	}
}

