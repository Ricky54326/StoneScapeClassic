package core;

public class IOThread implements Runnable {

	@Override
	public void run() {
		int cycleTime = 30;
		long lastTicks = 0;
		long totalTimeSpentProcessing = 0;
		int waitFails = 0;
		int cycle = 0;
		while(!Server.shutdownServer) {
			Server.playerHandler.processIOClients();
			// taking into account the time spend in the processing code for more accurate timing
			long timeSpent = System.currentTimeMillis()-lastTicks;
			totalTimeSpentProcessing += timeSpent;
			if(timeSpent >= cycleTime) {
				timeSpent = cycleTime;
				if(++waitFails > 100) {
					Server.shutdownServer = true;
					Misc.println("[KERNEL]: machine is too slow to run this server!");
				}
			}

			try { Thread.sleep(cycleTime-timeSpent); } catch(java.lang.Exception _ex) { }
			lastTicks = System.currentTimeMillis();
			cycle++;
			if(cycle % 100 == 0) {
				@SuppressWarnings("unused")
				float time = ((float)totalTimeSpentProcessing)/cycle;
				//misc.println_debug("[KERNEL]: "+(time*100/cycleTime)+"% processing time");
			}
		}
	}

}
