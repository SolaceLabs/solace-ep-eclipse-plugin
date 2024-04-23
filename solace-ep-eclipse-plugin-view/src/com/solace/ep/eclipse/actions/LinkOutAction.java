package com.solace.ep.eclipse.actions;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

class LinkOutAction extends Action {
	
	String url;
	
	LinkOutAction(String text) {
		super(text);
	}
	
	@Override
	public void run() {
		try {
			System.out.println("### attempting to open: " + url);
			PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(url));
		} catch (PartInitException | MalformedURLException e ) {
			e.printStackTrace();
		}
	}
}
