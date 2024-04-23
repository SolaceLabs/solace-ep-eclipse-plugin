package com.solace.ep.eclipse.views;

import java.util.concurrent.ScheduledFuture;

public class AnimationFuture {
	
	final AnimationRunnable runnable;
	final ScheduledFuture<?> future;
	
	public AnimationFuture(AnimationRunnable runnable, ScheduledFuture<?> future) {
		this.runnable = runnable;
		this.future = future;
	}
	
	public void cancel() {
		runnable.cancel();
		future.cancel(true);
		runnable.action.setImageDescriptor(Icons.getImageDescriptor(runnable.finishedIcon));
		runnable.action.setEnabled(true);
	}
}
