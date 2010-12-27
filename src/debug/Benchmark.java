package debug;

public class Benchmark {
	private long m_StartTime = 0;
	private long m_FinishTime = 0;
	
	public void start() {
		m_StartTime = System.currentTimeMillis();
	}
	
	public void stop() {
		m_FinishTime = System.currentTimeMillis() - m_StartTime;
	}
	
	public long getTime() {
		return m_FinishTime;
	}
	
	public String getCompiledTime() {
		long ms = (int) (getTime() % 1000);
	    int s = (int) ((getTime() / 1000) % 60);
	    int m = (int) ((getTime() / 60000) % 60);
	    int h = (int) ((getTime() / 3600000) % 24);
	    
		return String.format("%02d:%02d:%02d.%03d", h, m, s, ms);
	}
}
