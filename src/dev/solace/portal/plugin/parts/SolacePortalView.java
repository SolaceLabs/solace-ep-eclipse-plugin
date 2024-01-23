package dev.solace.portal.plugin.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import community.solace.ep.wrapper.EventPortalWrapper;

public class SolacePortalView {
//	private Label myLabelInView;
//	private Group myGroup;
	CTabFolder tabFolder = null;

	
	private static String pad(String s) {
		return s;
//		return new StringBuilder(s).append("  ").toString();
	}
	

	@PostConstruct
	public void createPartControl(Composite parent) {
		
		
		EventPortalWrapper epw = EventPortalWrapper.INSTANCE;
		System.out.println("Enter in SampleE4View postConstruct");
		
/*	    Bundle bundle = FrameworkUtil.getBundle(BootstrapTheme3x.class);
	    BundleContext context = bundle.getBundleContext();
	    ServiceReference<IThemeManager> ref = context
	        .getServiceReference(IThemeManager.class);
	    IThemeManager mgr = context.getService(ref);
	    final IThemeEngine engine = mgr.getEngineForDisplay(parent.getDisplay());
	    
	    for (ITheme theme : engine.getThemes()) {
	    	System.out.println(theme.getLabel());
	    }
	    WidgetElement.setCSSClass(parent, "MyComposite");
*/
	    
		
//		IThemeEngine engine2 = PlatformUI.getWorkbench().getService(IThemeEngine.class);
//		if (engine != null) {
//		    ITheme activeTheme = engine.getActiveTheme();
//		    if (activeTheme != null) {
//		        // The theme id
//		        String themeId = activeTheme.getId();
//		        // The display label
//		        String label = activeTheme.getLabel();
//		     
//		        ...
//		    }
//		}
		
		
		
		

		Composite comp = new Composite(parent, SWT.BORDER);
		GridLayout gl = new GridLayout();
//		gl.marginLeft = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.horizontalSpacing = 0;
		gl.numColumns = 2;
		comp.setLayout(gl);
		
		GridData gd = new GridData();
		
		Composite leftToolbar = new Composite(comp, SWT.NONE);
		gd.grabExcessVerticalSpace = true;
		gd.verticalAlignment = SWT.TOP;
		leftToolbar.setLayoutData(gd);
		gl = new GridLayout();
		gl.verticalSpacing = 0;
		gl.marginTop = 0;
		leftToolbar.setLayout(gl);
		
//		Label label = new Label(leftToolbar, SWT.NONE);
//		label.setText("🔃");
//		System.out.println("***** I am about to set an image");
//		System.out.println("Are we in dark mode? " + Display.isSystemDarkTheme());
//		System.out.println(SWT.COLOR_WIDGET_BACKGROUND);
//		Color bg = parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
//		System.out.println(bg);
//		bg = parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
//		System.out.println(bg);
		
//        gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		
		leftToolbar.setBackgroundMode(SWT.INHERIT_NONE);  // no borders on buttons??
		
		Button b;
		
		b= new Button(leftToolbar, SWT.PUSH);
		b.setImage(new Image(parent.getDisplay(), getClass().getResourceAsStream("/icons/gearPlain.png")));
		gd = new GridData();
		b.setLayoutData(gd);
		
		if ("a".equals("b")) {  // 
		Label l = new Label(leftToolbar, SWT.NONE);
		l.setImage(new Image(parent.getDisplay(), getClass().getResourceAsStream("/icons/gearPlain.png")));
		gd = new GridData();
		gd.horizontalAlignment = SWT.CENTER;
		l.setLayoutData(gd);
		l.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				System.out.println("MOUSE UP ON GEAR LABEL");
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				System.out.println("MOUSE DOWN ON GEAR LABEL");
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				System.out.println("MOUSE DBLECLICK ON GEAR LABEL");
			}
		});
		}
		
		b= new Button(leftToolbar, SWT.NONE);
		b.setImage(new Image(parent.getDisplay(), getClass().getResourceAsStream("/icons/execute.png")));
		gd = new GridData();
		gd.verticalIndent = 10;
		b.setLayoutData(gd);
		b.setToolTipText("Load Event Portal Data");
		b.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				// let's load some event portal data..?
				

			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		});

		b = new Button(leftToolbar, SWT.FLAT);
		b.setImage(new Image(parent.getDisplay(), getClass().getResourceAsStream("/icons/expandall.png")));
		gd = new GridData();
		gd.verticalIndent = 20;
		b.setLayoutData(gd);
		b.setToolTipText("Expand one level");

		b = new Button(leftToolbar, SWT.SMOOTH);
		b.setImage(new Image(parent.getDisplay(), getClass().getResourceAsStream("/icons/collapseall.png")));
		gd = new GridData();
		b.setLayoutData(gd);
		b.setToolTipText("Collapse one level");
		
		b = new Button(leftToolbar, SWT.FLAT);  // sort by alpha
		b.setImage(new Image(parent.getDisplay(), getClass().getResourceAsStream("/icons/sortByType.png")));
		b.setSelection(true);
		gd = new GridData();
		gd.verticalIndent = 10;
		b.setLayoutData(gd);
		
		b = new Button(leftToolbar, SWT.FLAT);  // sort by domain
		b.setImage(new Image(parent.getDisplay(), getClass().getResourceAsStream("/icons/sorted.png")));
		gd = new GridData();
		b.setLayoutData(gd);

		b = new Button(leftToolbar, SWT.PUSH);
		b.setImage(new Image(parent.getDisplay(), getClass().getResourceAsStream("/icons/filter.png")));
		b.setEnabled(false);
		gd = new GridData();
		gd.verticalIndent = 10;
		b.setLayoutData(gd);
		
		b = new Button(leftToolbar, SWT.PUSH);
		b.setImage(new Image(parent.getDisplay(), getClass().getResourceAsStream("/icons/toggleVisibility.png")));
		b.setEnabled(false);
		gd = new GridData();
		gd.verticalIndent = 10;
		b.setLayoutData(gd);
		
		
//		gd = new GridData();
//		gd.horizontalIndent = 5;
//		gd.verticalIndent = 5;
//		gd.grabExcessVerticalSpace = false;
//		label.setLayoutData(gd);
//		label.setLayoutData(new GridData(GridData.);
		
		tabFolder = new CTabFolder (comp, SWT.NONE);
//		tabFolder.set
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		tabFolder.setLayoutData(gd);
//		tabFolder.
//		Rectangle clientArea = shell.getClientArea ();
//		tabFolder.setLocation (clientArea.x, clientArea.y);
		
		final String[] tabTitles = new String[] {
				"Applications", "Events", "Schemas", "Event APIs", "Event API Products" };
		final String[] tabIcons = new String[] {
				"hex-large.png", "event-large.png", "triangle-large.png", "square-large.png", "diamond-large.png" };

		for (int i=0; i<tabTitles.length; i++) {
			
			final String title = tabTitles[i];
			CTabItem item = new CTabItem (tabFolder, SWT.NONE);
//			gl = new GridLayout();
			
			item.setImage(new Image(tabFolder.getDisplay(), getClass().getResourceAsStream("/icons/" + tabIcons[i])));
			item.setText(pad(title));
			try {
				if (i == 0) {
					AaTree tree = new AaTree(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
					item.setControl(tree.getTree());
				} else {
					Label l = new Label(tabFolder, SWT.NONE);
//					Composite c = new Composite(tabFolder, SWT.NONE);
//					gl = new GridLayout();
//					gl.
//					c.setLayout(gl);
					l.setText("Not implemented yet");
					GridData gd2 = new GridData();
					gd2.horizontalAlignment = SWT.CENTER;
					gd2.verticalAlignment = SWT.CENTER;
					l.setLayoutData(gd2);
					item.setControl(l);
				}
				
	/*			Button button = new Button (tabFolder, SWT.PUSH);
				button.setText ("Page " + title);
				button.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent arg0) {
						System.out.println("### button "+title+" selected..!");
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent arg0) {
						System.out.println("### default button "+title+" default selected..!");
					}
				});
				item.setControl (button);
	*/
			} catch (RuntimeException e) {
				e.printStackTrace();
				Label l = new Label(tabFolder, SWT.NONE);
				l.setText("Runtime exception encountered: " + e.toString());
				item.setControl(l);
			}
		}
		tabFolder.pack ();
//		tabFolder.setSelection(0);
		
//		myGroup = new Group(parent, SWT.BORDER);
//		myGroup.setText("This is a heading");
//		myGroup.setLayout(new GridLayout());
////		myGroup.
//		myLabelInView = new Label(myGroup, SWT.BORDER);
//		myLabelInView.setText("This is a Solace sample E4 view.");

	}

	@Focus
	public void setFocus() {
//		myLabelInView.setFocus();
		tabFolder.setFocus();

	}

	/**
	 * This method is kept for E3 compatiblity. You can remove it if you do not
	 * mix E3 and E4 code. <br/>
	 * With E4 code you will set directly the selection in ESelectionService and
	 * you do not receive a ISelection
	 * 
	 * @param s
	 *            the selection received from JFace (E3 mode)
	 */
	@Inject
	@Optional
	public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) ISelection s) {
		if (s==null || s.isEmpty())
			return;

		if (s instanceof IStructuredSelection) {
			IStructuredSelection iss = (IStructuredSelection) s;
			if (iss.size() == 1)
				setSelection(iss.getFirstElement());
			else
				setSelection(iss.toArray());
		}
	}

	/**
	 * This method manages the selection of your current object. In this example
	 * we listen to a single Object (even the ISelection already captured in E3
	 * mode). <br/>
	 * You should change the parameter type of your received Object to manage
	 * your specific selection
	 * 
	 * @param o
	 *            : the current object received
	 */
	@Inject
	@Optional
	public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) Object o) {

		// Remove the 2 following lines in pure E4 mode, keep them in mixed mode
		if (o instanceof ISelection) // Already captured
			return;
		if (o == null) {
			System.out.println("*** Current single selection class is null?");
		} else {
			System.out.println("*** Current single selection class is : " + o.getClass());
		}
		// Test if label exists (inject methods are called before PostConstruct)
//		if (myLabelInView != null) {
//			if (o == null) {
//				myLabelInView.setText("Current single selection class is null?");
//			} else {
//				myLabelInView.setText("Current single selection class is : " + o.getClass());
//			}
//		}
	}

	/**
	 * This method manages the multiple selection of your current objects. <br/>
	 * You should change the parameter type of your array of Objects to manage
	 * your specific selection
	 * 
	 * @param o
	 *            : the current array of objects received in case of multiple selection
	 */
	@Inject
	@Optional
	public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) Object[] selectedObjects) {

		if (selectedObjects == null) {
			System.out.println("*** This is a multiple selection of null bjects");
		} else {
			System.out.println("*** This is a multiple selection of " + selectedObjects.length + " objects");
		}
		
		
		// Test if label exists (inject methods are called before PostConstruct)
//		if (myLabelInView != null) {
//			if (selectedObjects == null) {
//				myLabelInView.setText("This is a multiple selection of null bjects");
//			} else {
//				myLabelInView.setText("This is a multiple selection of " + selectedObjects.length + " objects");
//			}
//		}
	}
}
