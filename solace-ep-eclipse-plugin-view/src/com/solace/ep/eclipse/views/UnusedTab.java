package com.solace.ep.eclipse.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;


public class UnusedTab extends SuperTabView {
	
	public UnusedTab(EventPortalView view, Composite parent, int index, RefreshListener tabbedView) {
		super(view, parent, null, index, tabbedView, false);
		updateView();
	}
	
	private void updateView() {
		Composite comp = new Composite(this.parent, SWT.NONE);
		GridLayout gl = new GridLayout();
//		gl.marginHeight = 20;
//		gl.marginWidth = 10;
//		gl.verticalSpacing = 20;
		gl.numColumns = 1;
		comp.setLayout(gl);
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.CENTER;
		gd.verticalAlignment = SWT.CENTER;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		comp.setLayoutData(gd);

		Label logo = new Label(comp, SWT.NONE);
		logo.setImage(Icons.getImage(Icons.Type.LOGO));
		gd = new GridData();
		gd.horizontalAlignment = SWT.CENTER;
		gd.verticalAlignment = SWT.CENTER;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		logo.setLayoutData(gd);
		
		CLabel label = new CLabel(comp, SWT.NONE);
		label.setText("This tab coming soon..!");
		gd = new GridData();
		gd.horizontalAlignment = SWT.CENTER;
		gd.verticalAlignment = SWT.CENTER;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;
		label.setLayoutData(gd);

		control = comp;

	}

	@Override
	public void dataLoaded() {
		// nothing, ignore
	}

	@Override
	public void sortByAlpha() {
	}
}
