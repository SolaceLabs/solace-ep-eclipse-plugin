package com.solace.ep.eclipse.views;

import org.eclipse.swt.widgets.Control;

public interface EpDataListener {

	public void dataLoaded();
	public Control getControl();
	public int getIndex();
	public void sortByAlpha();
}
