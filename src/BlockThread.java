import java.util.*;
import java.util.logging.Logger;

public class BlockThread extends Thread {
	protected static final Logger log = Logger.getLogger("Minecraft");
	private BlockQueue blockQueue;
	public static final Object NO_MORE_WORK = new Object();
	public static final Object END_JOB = new Object();
	
	private boolean running;
	
	private Job currentJob;
	private int blocks, skipped;
	
	public BlockThread(BlockQueue blockQueue) {
        this.blockQueue = blockQueue;
		setRunning(true);
	}
	
	public synchronized void setRunning(boolean running) {
		this.running = running;
	}
	
	public void run() {
		blocks = 0;
		skipped = 0;
		
		try {
			while (running) {
				Object obj = blockQueue.getWork();
				
				if (obj == NO_MORE_WORK)
					break;
				
				if (obj == END_JOB)
					currentJob.done(blocks, skipped);
				
				if (obj instanceof Block) {
					try { buildBlock((Block)obj); } catch (Exception e) { skipped++; };
					blocks++;
				}
				
				if (obj instanceof Job) {
					currentJob = (Job)obj;
					blocks = 0;
					skipped = 0;
				}
			}
		} catch (InterruptedException e) { };
		
		log.info("[Blocker] BlockQueue Thread stoped.");
		blockQueue = null;
	} 
	
	public void buildBlock(Block block) {
		try { sleep(25); } catch (InterruptedException e) {}
		
		if (block.getType() == etc.getServer().getBlockIdAt(block.getX(), block.getY(), block.getZ()))
			return;
			
		boolean result = etc.getServer().setBlock(block);
		
		/*
		int tries = 0;
		while (tries < 3) {
			try { sleep(50); } catch (InterruptedException e) { break; }
			
			if (block.getType() == etc.getServer().getBlockIdAt(block.getX(), block.getY(), block.getZ()))
				break;
			
			tries++;	
			try { sleep(50); } catch (InterruptedException e) { break; }
		}
		if (tries == 3)
			skipped++;
		*/
	}
}

class Job {
	String boss;
	boolean notify;
	
	public Job(String boss) {
		this(boss, true);
	}
	
	public Job(String boss, boolean notify) {
		this.boss = boss;
		this.notify = notify;
	}
	
	public void done(int blocks, int skipped) {
		if (notify) {
			Player player = etc.getServer().matchPlayer(boss);
			player.sendMessage("Generated: "+blocks+" Blocks");
			if (skipped > 0)
				player.sendMessage("Skipped: "+blocks+" Blocks");
		}
	}
}