package com.ouken.phone.utils.math;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ArrayMap;

/**
 * Usage 1:
 * <br><ul>
 * - manually start the timer<br>
 * - manually pause/stop the timer (without timers)<br>
 * - manually set the timer back to running after its paused/stopped or done<br></ul>
 * 
 * Usage 2:
 * <br><ul>
 * - manually start the timer
 * - manually add pausedFor / stoppedFor/ doneFor - timers which <br> 
 *   Automatically set running to true of the main timer<br> afterwards
 * 
 * */
public class Timer{
	

	
	// -- constants --
	private final Amount amount;
	private final ArrayMap<TimerState, Timer> timers;
	
	// -- attributes --
	private TimerState state = TimerState.STOPPED;
	private boolean loop;
	
	
	
	// -- constructors --
	
	/**The timer is initially set ot STOPPED. Call setRunning to start the timer*/
	public Timer(float duration) {
		amount = new Amount();
		amount.setCap(duration);
		
		timers = new ArrayMap<TimerState, Timer>();
	}
	
	/**{@link Timer#Timer(float)}*/
	public Timer(float duration, boolean loop) {
		this(duration);
		setLooping(loop);
	}
	
	/**{@link Timer#Timer(float)}*/
	public Timer(float duration, boolean loop, boolean setRunning) {
		this(duration, loop);
		if(setRunning)setRunning();
	}
	
	// -- public methods -- 
	public void setLooping(boolean loop) {
		this.loop = loop;
	}

	
	/**The time increments*/
	public float getTime() {
		return amount.get();
	}
	
	public float getTimeDown() {
		return amount.getCap() - amount.get();
	}
	
	public float getMaxTime() {
		return amount.getCap();
	}
	
	public float getRatio() {
		return amount.getRatio();
	}
	
	public float getRatioDown() {
		return 1 - amount.getRatio();
	}
	
	
	
	
	
	public boolean isRunning() {
		return state.isRunning();
	}
	
	public boolean isPaused() {
		return state.isPaused();
	}
	
	public boolean isStopped() {
		return state.isStopped();
	}
	
	public boolean isDone() {
		return state.isDone();
	}
	
	public boolean isLooping() {
		return loop;
	}
	
	
	
	
	
	
	/**sets the timer to running state. if the timer was prevously done or stopped the timer resets its time.br>*/
	public void setRunning() {
		if(isStopped() || isDone())reset(false);
		else state = TimerState.RUNNING;
	}
	
	/**(Immediately) sets the timer to pausing state only if the timer was prevously running.<br>
	 * @return true if the timer could successfully be paused*/
	public boolean setPaused() {
		if(isRunning()) {
			state = TimerState.PAUSED;
			return true;
		}
		return false;
	}
	
	/**(Immediately) sets the timer to a timed pausing state only if the main-timer was prevously running.<br>
	 *  Afterwards sets timer back to running.
	 *  @return the pause-timer or null if the the main-timer isnt running*/
	public Timer setPausedFor(float duration) {
		Timer timer = null;
		if(setPaused()) {
			
			Timer mainTimer = this;
			timer = new Timer(duration) {
				@Override
				public void done() {
					mainTimer.setRunning();
					timers.removeKey(TimerState.PAUSED);
				}
			};
			timer.setRunning();
			timers.put(TimerState.PAUSED, timer);
		}
		return timer;
	}
	
	/**{@link Timer#setPausedFor(float)} <br>
	 *  @return true if the timer could successfully be pausedFor*/
	public boolean setPauseFor(Timer timer) {
		if(setPaused()) {
			
			Timer mainTimer = this;
			Timer used = new Timer(timer.getMaxTime()) {
				
				@Override
				public void update(float delta) {
					timer.update(delta);
					super.update(delta);// done is called here
				}
				
				@Override
				public void done() {
					if(timer.isDone() && !timer.isLooping()) {
						mainTimer.setRunning();
						timers.removeKey(TimerState.PAUSED);
					}
				}
				
				
			};
			used.setRunning();
			timers.put(TimerState.PAUSED, used);
			return true;
		}
		return false;
	}
	
	
	/**Immediately  Sets the timer to stopped state if the timer was previuosly running. <br>
	 * The timer resets its time if the timer is setRunning afterwards again.
	 * @return true if the timer could successfully be stopped*/
	public boolean setStopped() {
		if(isRunning()) {
			state = TimerState.STOPPED;
			return true;
		}
		return false;
	}
	
	
	/**Immediately Sets the timer to a timed stopped state if the timer was previously running. <br>
	 * The timer resets its time if the timer is setRunning afterwards again. Afterwards sets timer back to running
	 * @return the stopp-timer or null if the main-timer isnt running*/
	public Timer setStoppedFor(float duration) {
		Timer timer = null;
		if(setStopped()) {

			Timer mainTimer = this;
			timer = new Timer(duration) {
				@Override
				public void done() {
					mainTimer.setRunning();
					timers.removeKey(TimerState.STOPPED);
				}
			};
			timer.setRunning();
			timers.put(TimerState.STOPPED, timer);
		}
		return timer;
	}

	
	 /** @return the done-timer (always)*/
	public Timer setDoneFor(float duration) {
		Timer timer = new Timer(duration) {
			@Override
			public void done() {}
		};
		timers.put(TimerState.DONE, timer);
		return timer;
	}

	
	
	
	
	
	
	public void reset(boolean clearSubTimer) {
		state = TimerState.RUNNING;
		amount.set(0);
		
		if(clearSubTimer)timers.clear();
		else timers.forEach(timer -> timer.value.reset(false));
	}

	
	
	/**[Note]: the timer increments (from 0 to cap)*/
	public void update(float delta) {
		if(isRunning()) {
			
			reached();
			
			amount.incrBy(delta, true);
			
			if(amount.isCap()) {
				
				if(loop) {

					if(timers.containsKey(TimerState.DONE)) {
						timers.get(TimerState.DONE).setRunning();
						setDone();
					}
					else {
						setDone();
						done();
						reset(false);
					}
				}
				else {
					setDone();
					if(timers.containsKey(TimerState.DONE))timers.get(TimerState.DONE).setRunning();
					else done();
				}
			}
		}	
		if(isPaused() && timers.containsKey(TimerState.PAUSED)) {
			timers.get(TimerState.PAUSED).update(delta);
			
		}else if(isStopped() && timers.containsKey(TimerState.STOPPED)) {
			timers.get(TimerState.STOPPED).update(delta);
			
		}else if(isDone() && timers.containsKey(TimerState.DONE)) {
			Timer doneTimer = timers.get(TimerState.DONE);
			doneTimer.update(delta);
			
			if(doneTimer.isDone()) {				
				if(isLooping()) {
					done();
					reset(false);
				}else {
					done();
					timers.removeKey(TimerState.DONE);
				}
			}
		}
		
		
	}
	
	
	
	// -- methods ment to be @Overridden --
	
	
	/**Override this to dynamically insert calls at specific times or ratios while isRunning via<br>
	 * calling one of the following helper methods inside this method: <br><ul>
	 * {@link Timer#reachedTime(float)} or <br>
	 * {@link Timer#reachedTimeDown(float)} or <br>
	 * {@link Timer#reachedRatio(float)} or <br>
	 * {@link Timer#reachedRatioDown(float)} <br>*/
	public void reached() {
		
	}
	

	public boolean reachedTime(float time) {
		if(time < 0)return false;
		return getTime() <= time && getTime() + delta() >= time;
	}
	

	public boolean rechedTimeDown(float time) {
		if(time < 0)return false;
		return getTimeDown() >= time && getTimeDown() - delta() <= time;
	}
	

	public boolean reachedRatio(float ratio) {
		if(ratio < 0)return false;
		return getRatio() <= ratio && (getTime() + delta()) / getMaxTime() >= ratio;
	}
	

	public boolean reachedRatioDown(float ratio) {
		if(ratio < 0)return false;
		return getRatioDown() >= ratio && (getTimeDown() - delta()) / getMaxTime() <= ratio;
	}
	

	/**Called once when the timer is done. If doneFor was called this is fired after the doneFor-timer runs out.<br>
	 * if isLooping - done() is called at every end of a loop*/
	public void done() {}
	 

	
	
	
	
	// -- private methods --
	
	private void setDone() {
		this.state = TimerState.DONE;
	}
	
	
	private float delta() {
		return Gdx.graphics.getDeltaTime();
	}
	

}
