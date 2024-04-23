package com.solace.ep.eclipse.views;

import org.eclipse.jface.action.Action;

class AnimationRunnable implements Runnable {

	final Action action;
	final AnimatedIcons.AnimType iconType;
	final Icons.Type finishedIcon;
	private volatile boolean isCancelled = false;

	public AnimationRunnable(Action action, AnimatedIcons.AnimType animatedIcon, Icons.Type finishedIcon) {
		this.action = action;
		this.iconType = animatedIcon;
		this.finishedIcon = finishedIcon;
	}
	
	public void cancel() {
		isCancelled = true;
	}

	@Override
	public void run() {
		if (!isCancelled) action.setImageDescriptor(AnimatedIcons.getImageDescriptor(iconType));
	}
}

