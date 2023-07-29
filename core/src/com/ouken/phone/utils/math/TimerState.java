package com.ouken.phone.utils.math;

public enum TimerState {
	RUNNING,
	PAUSED, 
	STOPPED,
	DONE;
	
	
	
	public boolean isRunning() { return this == RUNNING; }
	
	public boolean isPaused() { return this == PAUSED; }
	
	public boolean isStopped() { return this == STOPPED; }
	
	public boolean isDone() { return this == DONE; }
	
}
