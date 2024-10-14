package com.solace.ep.eclipse.views;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.eclipse.ui.wizards.IWizardRegistry;

import com.solace.ep.codegen.mule.eclipse.EclipseProjectGenerator;
import com.solace.ep.eclipse.Activator;
import com.solace.ep.eclipse.prefs.PreferenceConstants;

import community.solace.ep.client.model.Application;
import community.solace.ep.client.model.Application.BrokerTypeEnum;
import community.solace.ep.client.model.ApplicationDomain;
import community.solace.ep.client.model.ApplicationVersion;
import community.solace.ep.client.model.EventApi;
import community.solace.ep.client.model.EventApiVersion;
import community.solace.ep.client.model.EventVersion;
import community.solace.ep.client.model.SchemaVersion;
import community.solace.ep.wrapper.EventPortalWrapper;
import community.solace.ep.wrapper.PortalLinksUtils;
import community.solace.ep.wrapper.SupportedObjectType;
import dev.solace.aaron.useful.WordUtils;


public abstract class SuperTabView implements EpDataListener {

	private static final Logger logger = LogManager.getLogger(SuperTabView.class);

	protected final EventPortalView view;
	protected final RefreshListener tabbedView;
	protected final Composite parent;
	protected final SupportedObjectType epType;
	protected final int index;
	protected Control control;  // pointer to whatever is shown in the tab
	protected TreeNode root = null;
	protected TreeViewer treeViewer = null;


	private DrillDownAdapter drillDownAdapter;
	private Action doubleClickAction;
//	private Action actionBuildMuleProf;

	protected final boolean linkToAsyncApi;
	
	
	protected SuperTabView(EventPortalView view, Composite parent, SupportedObjectType epType, int index, RefreshListener tabbedView, boolean linkToAsyncApi) {
		this.view = view;
		this.parent = parent;
		this.epType = epType;
		this.index = index;
		this.tabbedView = tabbedView;
		this.linkToAsyncApi = linkToAsyncApi;

		
		String token = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.TOKEN.getId());
		if (token == null || token.isEmpty()) {
			control = buildSetPrefs();
		} else {
			control = drawLoadDataPane();
		}

/*		Button button = new Button(comp, SWT.FLAT);
		button.setText("Go load event portal data");
		button.setImage(Icons.getImage(Icons.Type.LOAD));
		button.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.err.println("Buton selected");
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				System.err.println("Buton default selected");
			}
		});
		gd = new GridData();
		gd.horizontalAlignment = SWT.CENTER;
		gd.verticalAlignment = SWT.CENTER;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;
		button.setLayoutData(gd);*/
	}

	protected Composite drawLoadDataPane() {
		// default view
		Composite comp = new Composite(this.parent, SWT.NONE);
		GridLayout gl = new GridLayout();
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
		label.setText("Go load Event Portal data");
		label.setFont(EventPortalView.fonts.get("big"));
//		label.setImage(Icons.getImage(Icons.Type.LOAD));
		label.setEnabled(false);
		gd = new GridData();
		gd.horizontalAlignment = SWT.CENTER;
		gd.verticalAlignment = SWT.CENTER;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;
		label.setLayoutData(gd);
		return comp;
	}

	protected Composite buildSetPrefs() {
		// default view
		Composite comp = new Composite(this.parent, SWT.NONE);
		GridLayout gl = new GridLayout();
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
		label.setText("Go configure Event Portal token in Preferences");
		label.setImage(Icons.getImage(Icons.Type.GEAR));
		label.setEnabled(false);
		gd = new GridData();
		gd.horizontalAlignment = SWT.CENTER;
		gd.verticalAlignment = SWT.CENTER;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;
		label.setLayoutData(gd);
		return comp;
	}
	
	
	private int expandLevel = 2;
	
	public void expandOne() {
		if (treeViewer == null) return;
        treeViewer.expandToLevel(++expandLevel);
	}

	public void collapseAll() {
		expandLevel = 1;
		if (treeViewer == null) return;
//        treeViewer.collapseToLevel(root, --expandLevel);
		treeViewer.collapseAll();
	}
	
	@Override
	public Control getControl() {
		logger.info("getControl() called on " + this.toString());
		return control;
	}
	
	
	@Override
	public String toString() {
		return (this.getClass().getSimpleName()) + " #" + index;
	}

	@Override
	public int getIndex() {
		return index;
	}
	
	
	protected static Set<String> modifySetsCalcIntersection(Set<String> pubs, Set<String> subs) {
		Set<String> both = new HashSet<>(pubs); both.retainAll(subs);
		pubs.removeAll(both);
		subs.removeAll(both);
		return both;
	}
	
	protected static String vName(String version, String name) {
		return new StringBuilder().append('v').append(version).append(' ').append(name).toString();
	}

	
	protected void buildTree() {
		
		TreeViewer viewer2 = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
        ColumnViewerToolTipSupport.enableFor(viewer2);
		viewer2.setContentProvider(new TreeNodeViewContentProvider());
		viewer2.getTree().setHeaderVisible(true);
		viewer2.getTree().setLinesVisible(true);

//        GridLayoutFactory.fillDefaults().generateLayout(parent);

        TreeViewerColumn viewerColumn = new TreeViewerColumn(viewer2, SWT.NONE);
        viewerColumn.getColumn().setWidth(160);
        viewerColumn.getColumn().setText("Type");
        viewerColumn.setLabelProvider(new TreeNodeTypeLabelProvider());

        viewerColumn = new TreeViewerColumn(viewer2, SWT.NONE);
        viewerColumn.getColumn().setWidth(200);
        viewerColumn.getColumn().setText("Name");
        viewerColumn.setLabelProvider(new TreeNodeNameProvider(1, SupportedObjectType.APPLICATION_VERSION));
        
        viewerColumn = new TreeViewerColumn(viewer2, SWT.NONE);
        viewerColumn.getColumn().setWidth(200);
        viewerColumn.getColumn().setText("Details");
        viewerColumn.setLabelProvider(new TreeNodeGenericProvider(2));

        viewerColumn = new TreeViewerColumn(viewer2, SWT.NONE);
        viewerColumn.getColumn().setWidth(75);
        viewerColumn.getColumn().setText("State");
        viewerColumn.setLabelProvider(new TreeNodeStateProvider());
        
        final TreeViewerColumn viewerColumnTopic = new TreeViewerColumn(viewer2, SWT.NONE);
        viewerColumnTopic.getColumn().setWidth(250);
        viewerColumnTopic.getColumn().setText("Topic");
        viewerColumnTopic.setLabelProvider(new TreeNodeTopicProvider(parent.getDisplay()));
        viewerColumnTopic.getColumn().addControlListener(new ControlListener() {
			@Override
			public void controlResized(ControlEvent e) {
//				logger.info("controlResized: " + e.toString() + ", " + viewerColumnTopic.getColumn().getWidth());
				// TODO save this in preferences
			}
			@Override public void controlMoved(ControlEvent e) { }  // don't care
		});
        
        viewerColumn = new TreeViewerColumn(viewer2, SWT.NONE);
        viewerColumn.getColumn().setWidth(150);
        viewerColumn.getColumn().setText("Last Updated");
        viewerColumn.setLabelProvider(new TreeNodeGenericProvider(5));
        
        treeViewer = viewer2;
        drillDownAdapter = new DrillDownAdapter(treeViewer);
        
//        treeViewer.getTree().addPaintListener(new PaintListener() {			
//			@Override
//			public void paintControl(PaintEvent e) {
//				System.out.println("TreeViewer " + epType + " Paint event fired " + e.toString());
//			}
//		});

        
        
        configTreeActions();
        hookContextMenu();
		hookDoubleClickAction();
//        return viewer2;
	}

	


	
	public static class LinkAction extends Action {
		String url;
		LinkAction(String text) {
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

	private void hookDoubleClickAction() {
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				System.out.println(event.getViewer().getSelection());
				logger.info(event.getViewer().getSelection());
				doubleClickAction.run();
				
			}
		});
	}


	private void fillContextMenu(IMenuManager manager) {
//		manager.add(actionBuildMuleProf);
//		manager.add(actionLoad);
//		manager.add(actionPrefs);
		manager.add(new Separator());
//		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		
	}

	
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager(toString() + " PopupMenu");  // text isn't shown
//		new MenuManager()
		menuMgr.setRemoveAllWhenShown(true);
		
		initRightClickMenu(menuMgr);
		
		
//		menuMgr.addMenuListener(new IMenuListener() {
//			public void menuAboutToShow(IMenuManager manager) {
//				System.err.println("menu for " + this.toString());
//				fillContextMenu(manager);
//			}
//		});
//		TreeViewer treeViewer = null;
		
//		tabFolder.getSelection().getControl();
		Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
//		Menu menu = menuMgr.createContextMenu(tabFolder.getSelection().getControl());
		treeViewer.getControl().setMenu(menu);
//		getSite().registerContextMenu(menuMgr, tabs.get(tabFolder.getSelectionIndex()));
		view.getSite().registerContextMenu(menuMgr, treeViewer);
	}
	
	public void setTab(int index, String id) {
		view.tabFolder.setSelection(index);

		view.tabFolder.setEnabled(false);
		view.tabFolder.setEnabled(true);
		
/*		Event ev = new Event();
		ev.doit = true;
		CTabItem item = view.tabFolder.getItem(index);
		Rectangle rect = item.getControl().getBounds();
		System.out.println(rect.x);
		System.out.println(rect.y);
		
		
//		ev.
		view.tabFolder.getItem(index).notifyListeners(SWT.Selection, ev);
		
		
		if (1 == 1) return;
*/
		
//		view.tabFolder.setSelection(view.tabFolder.getItem(index));
		SuperTabView tab = view.tabs.get(index);
		TreeViewer tv = tab.treeViewer;
		Tree tree = tv.getTree();
//		tree.redraw();
//		tree.update();
//		view.tabFolder.redraw();
//		view.tabFolder.update();
//		tree.requestLayout();
//		view.tabFolder.requestLayout();
//		view.parent.redraw();
//		view.parent.update();

		
		TreeItem[] domains = tree.getItems();
/*		for (int i=0; i< domains.length; i++) {
			TreeItem domain = domains[i];
			System.out.printf("%d) %s%n", i, domain.getData());
			TreeItem[] apps = domain.getItems();
			for (int j=0; j < apps.length; j++) {
				TreeItem app = apps[j];
				System.out.printf("%d.%d) %s%n", i, j, app.getData());
				TreeItem[] appVers = app.getItems();
				for (int k=0; k < appVers.length; k++) {
					TreeItem appVer = appVers[k];
					System.out.printf("%d.%d.%d) %s%n", i, j, k, appVer.getData());
					TreeNode node = (TreeNode)appVer.getData();
					if (node != null && node.id.equals(id)) {
						System.out.println("FOUND IT!");
//						tree.setSelection(appVer);
						tree.setTopItem(appVer);
						return;
					}
				}
			}
		}
		*/
		
		for (int i=0; i< domains.length; i++) {
			TreeItem level0 = domains[i];
			TreeNode node = (TreeNode)level0.getData();
			if (node.getChildren() != null) for (TreeNode level1 : node.getChildren()) {
				if (level1.id.equals(id)) {
					System.out.println("FOUND IT! " + level1);
//					tv.expandToLevel(node, 1);
					tv.expandToLevel(level1, 5);
					tab.makeTopSelection(level1);
					return;
				}
				if (level1.getChildren() != null) for (TreeNode level2 : level1.getChildren()) {
					if (level2.id.equals(id)) {
						System.out.println("FOUND IT! " + level2);
//						tv.expandToLevel(node, 1);
//						tv.expandToLevel(level1, 2);
						tv.expandToLevel(level2, 5);
						tab.makeTopSelection(level2);
						return;
					}
				}
			}
		}
			
//		System.out.println("Could not find my item " + id);
//		tab.treeViewer.getTree().setTopItem(null);
//		view.findVersion("abc123");
	}
	
	private void makeTopSelection(TreeNode node) {
		Tree tree = treeViewer.getTree();
		for (TreeItem lev0 : tree.getItems()) {
			for (TreeItem lev1 : lev0.getItems()) {
				if (lev1.getData() == node) {
					tree.setTopItem(lev1);
					tree.setSelection(lev1);
					System.out.println("Found it on lev 1");
					return;
				}
				for (TreeItem lev2 : lev1.getItems()) {
					if (lev2.getData() == node) {
						tree.setTopItem(lev2);
						tree.setSelection(lev2);
						System.out.println("Found it on lev 2");
						return;
					}
				}
			}
		}
		System.out.println("Couldn't find the item in teh tree");
	}
	
	
	
	
	
	private void configTreeActions() {
		
//		actionBuildMuleProf = new Action() {
//			public void run() {
//				EventPortalView.showMessage("I am now executing Dennis' code here dummy!!!");
//			}
//		};
//		actionBuildMuleProf.setText("Build Mule Project");
//		actionBuildMuleProf.setImageDescriptor(Icons.getImageDescriptor(Icons.Type.MULE));

		doubleClickAction = new Action() {
			public void run() {
				if (treeViewer == null) return;
				IStructuredSelection selection = treeViewer.getStructuredSelection();
				if (selection == null) return;
				Object obj = selection.getFirstElement();
				if (obj == null) return;
//				EventPortalView.showMessage("Double-click detected on "+obj.toString());
				treeViewer.expandToLevel(obj, 10);
			}
		};

	}

	
	private void initRightClickMenu(MenuManager mgr) {
		
		LinkAction actionView = new LinkAction("View in Event Portal");//  🡵");
		actionView.setImageDescriptor(Icons.getImageDescriptor(Icons.Type.PORTAL));


//		final MenuManager mgr = new MenuManager();
//		mgr.setRemoveAllWhenShown(true);
		
//		mgr.addMenuListener(null);  // 
		
		mgr.addMenuListener(manager -> {
			IStructuredSelection selection = treeViewer.getStructuredSelection();
			if (!selection.isEmpty()) {
//				logger.info(selection.toString());
				TreeNode node = (TreeNode)selection.getFirstElement();
				String baseUrl = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.WEB_URL.getId());
				if (node.epObject != null) {  // otherwise we don't know what it is!
					actionView.url = PortalLinksUtils.generateUrl(node.epObject, baseUrl);
					mgr.add(actionView);
				}
				if (node.type == SupportedObjectType.APPLICATION_VERSION) {
//					logger.info("I am here: my node is a vAPP");
					ApplicationVersion appVer = (ApplicationVersion)node.epObject;
					Application app = EventPortalWrapper.INSTANCE.getApplication(appVer.getApplicationId());
//					Application app = (Application)node.getParent().epObject;
//					ApplicationDomain domain = (ApplicationDomain)node.getParent().getParent().epObject;
					ApplicationDomain domain = EventPortalWrapper.INSTANCE.getDomain(app.getApplicationDomainId());
					if (epType == SupportedObjectType.APPLICATION) {
						final Action a2 = new Action("") {
							public void run() {
								try {
									if (app.getBrokerType() != BrokerTypeEnum.SOLACE) {
										StringBuilder sb = new StringBuilder();
										sb.append("Cannot create Mule Flow project.  This is not a Solace app.\n\n");
										sb.append(WordUtils.capitalFirst(app.getBrokerType().toString()) + " will be supported in a later version.");
										EventPortalView.showMessageAsync(sb.toString());
										return;
									}
									String asyncApi = EventPortalWrapper.INSTANCE.getAsyncApiForAppVerId(appVer.getId(), true);
									System.out.println("AsyncAPI spec is: ");
									System.out.println(asyncApi);
									
								    final String groupId = UsefulUtils.helperMakePackageName(domain.getName());
								    final String artifactId = UsefulUtils.helperStripNonChars(app.getName());
								    final String version = appVer.getVersion();
								    
							        String generatedArchive = "NADA";
							        EclipseProjectGenerator epg = new EclipseProjectGenerator();
							        try {
		//					            generatedArchive = epg.createMuleProject(groupId, artifactId, version, xmlString);
		//					        	String saveLocation = System.getProperty("user.home") + System.getProperty(baseUrl)
							        	String filename = "Mule-" + artifactId + "-" + version + ".jar";
							        	File saveFile = new File(System.getProperty("user.home"), "Downloads/" + filename);
							        	generatedArchive = saveFile.getAbsolutePath();
							            epg.generateEclipseArchiveForMuleFlowFromAsyncApi(groupId, artifactId, version, asyncApi, saveFile.getAbsolutePath());
	//						            if ("1".equals("1")) throw new NullPointerException();
							        } catch (Exception e) {
								    	logger.error("EclipseProjectGenerator.createMuleProject() failed", e);
								    	String err = String.format("Could not generate Mule project from AsyncAPI.%n%n%s%n%nCheck logs at %s for details.", e.toString(), PreferenceConstants.LOG_LOCATION.getDescription());
								    	EventPortalView.showWarning(err);
							            return;
							        }
		
							        // If we're here, we succeeded
							        Path generatedArchivePath = Paths.get(generatedArchive);						    
								    logger.info("### DONE!  " + generatedArchivePath.toString());
								    boolean success = EventPortalView.copyStringToClipboard(generatedArchivePath.toString());
								    if (success) {
	//							    	String message = String.format("Mule project built! %s.%s%n%nLocation saved to clipboard. Paste into Import \"Packaged mule application\" dialog.\"", groupId, artifactId);
								    	String message = String.format("Mule project built! %s.%s%n%nLocation saved to clipboard. Paste into following Import dialog.", groupId, artifactId);
								    	EventPortalView.showMessage(message);
								    } else {
								    	String message = String.format("Mule project built! %s.%s%n%nSaved to: %s%n%nLoad into following Import dialog.", groupId, artifactId, generatedArchivePath.toString());
								    	EventPortalView.showMessage(message);
	//							    	EventPortalView.showMessage("Mule project built! " + groupId + "." + artifactId +"\n\nLocation: " + generatedArchivePath.toString() + ". Paste into Import \"Packaged mule application\" dialog.");
								    }
						            try {
										IWizardRegistry wizards = PlatformUI.getWorkbench().getImportWizardRegistry();
										IWizardDescriptor wizard = wizards.findWizard("org.mule.tooling.ui.muleZipProjectImportWizard");
										if (wizard != null) {
											final IWizard wiz = wizard.createWizard();  // could throw CoreException
											final WizardDialog wd = new WizardDialog(Display.getDefault().getActiveShell(), wiz);
											wd.setTitle("Mule Import from Deployable Archive");
											wd.open();
										} else {
							            	logger.info("Could not create new Mule Import dialog");
										}
						            } catch (CoreException e) {
						            	logger.warn("Could not create new Import dialog", e);
						            }
								} catch (Exception e) {
					            	logger.warn("Caught trying to build plugin for Mule", e);
							    	String err = String.format("Could not generate Mule project from AsyncAPI.%n%n%s%n%nCheck logs at %s for details.", e.toString(), PreferenceConstants.LOG_LOCATION.getDescription());
							    	EventPortalView.showWarning(err.toString());
	//						    	EventPortalView.showWarning("Could not generate Mule project from AsyncAPI. Check logs for details.");
								}
							}
						};
						if (view.runningInMule) {
							a2.setText("Build New Mule Flow Project");
							a2.setImageDescriptor(Icons.getImageDescriptor(Icons.Type.MULE));
							mgr.add(a2);
						} else {  // running in Eclipse or STS or something
							Action aSpring = new Action("Build New Spring Cloud Stream Project (coming soon)") { };
							aSpring.setImageDescriptor(Icons.getImageDescriptor(Icons.Type.SPRING));
							aSpring.setEnabled(false);
							mgr.add(aSpring);
						}
	
					} else {  // on the wrong tab.  Provide a menu to jump to another tab
						System.out.println(parent.getClass().getName());
						final Action a2 = new Action("") {
							public void run() {
								setTab(0, appVer.getId());
							}
						};
						a2.setText("Jump to Applications Tab");
						a2.setImageDescriptor(Icons.getImageDescriptor(Icons.Type.vAPP));
						mgr.add(a2);
					}
				}
				if (node.type == SupportedObjectType.EVENT_VERSION && epType != SupportedObjectType.EVENT) {
					final EventVersion eventVer = (EventVersion)node.epObject;
					final Action a2 = new Action("") {
						public void run() {
							setTab(1, eventVer.getId());
						}
					};
					a2.setText("Jump to Events Tab");
					a2.setImageDescriptor(Icons.getImageDescriptor(Icons.Type.vEVENT));
					mgr.add(a2);
				}
				if (node.type == SupportedObjectType.SCHEMA_VERSION && epType != SupportedObjectType.SCHEMA) {
					final SchemaVersion schemaVer = (SchemaVersion)node.epObject;
					final Action a2 = new Action("") {
						public void run() {
							setTab(2, schemaVer.getId());
						}
					};
					a2.setText("Jump to Schemas Tab");
					a2.setImageDescriptor(Icons.getImageDescriptor(Icons.Type.vSCHEMA));
					mgr.add(a2);
				}
				// TODO SUPER hack, just copied code... better to refactor
				if (node.type == SupportedObjectType.EVENT_API_VERSION && linkToAsyncApi) {
//					logger.info("I am here: my node is a vAPP");
					EventApiVersion apiVer = (EventApiVersion)node.epObject;
//					EventApi api = (EventApi)node.getParent().epObject;
					EventApi api = EventPortalWrapper.INSTANCE.getEventApi(apiVer.getEventApiId());
//					ApplicationDomain domain = (ApplicationDomain)node.getParent().getParent().epObject;
					ApplicationDomain domain = EventPortalWrapper.INSTANCE.getDomain(api.getApplicationDomainId());
					final Action a2 = new Action("") {
						public void run() {
							try {
								if (api.getBrokerType() != community.solace.ep.client.model.EventApi.BrokerTypeEnum.SOLACE) {
									StringBuilder sb = new StringBuilder();
									sb.append("Cannot create Mule Flow project.  This is not a Solace API.\n\n");
									sb.append(WordUtils.capitalFirst(api.getBrokerType().toString()) + " will be supported in a later version.");
									EventPortalView.showMessageAsync(sb.toString());
									return;
								}
								String asyncApi = EventPortalWrapper.INSTANCE.getAsyncApiForEventApiVerId(apiVer.getId(), true);
								System.out.println("AsyncAPI spec is: ");
								System.out.println(asyncApi);
								
							    final String groupId = UsefulUtils.helperMakePackageName(domain.getName());
							    final String artifactId = UsefulUtils.helperStripNonChars(api.getName());
							    final String version = apiVer.getVersion();
							    
						        String generatedArchive = "NADA";
						        EclipseProjectGenerator epg = new EclipseProjectGenerator();
						        try {
	//					            generatedArchive = epg.createMuleProject(groupId, artifactId, version, xmlString);
	//					        	String saveLocation = System.getProperty("user.home") + System.getProperty(baseUrl)
						        	String filename = "Mule-" + artifactId + "-" + version + ".jar";
						        	File saveFile = new File(System.getProperty("user.home"), "Downloads/" + filename);
						        	generatedArchive = saveFile.getAbsolutePath();
						            epg.generateEclipseArchiveForMuleFlowFromAsyncApi(groupId, artifactId, version, asyncApi, saveFile.getAbsolutePath());
//						            if ("1".equals("1")) throw new NullPointerException();
						        } catch (Exception e) {
							    	logger.error("EclipseProjectGenerator.createMuleProject() failed", e);
							    	String err = String.format("Could not generate Mule project from AsyncAPI.%n%n%s%n%nCheck logs at %s for details.", e.toString(), PreferenceConstants.LOG_LOCATION.getDescription());
							    	EventPortalView.showWarning(err);
						            return;
						        }
	
						        // If we're here, we succeeded
						        Path generatedArchivePath = Paths.get(generatedArchive);						    
							    logger.info("### DONE!  " + generatedArchivePath.toString());
							    boolean success = EventPortalView.copyStringToClipboard(generatedArchivePath.toString());
							    if (success) {
//							    	String message = String.format("Mule project built! %s.%s%n%nLocation saved to clipboard. Paste into Import \"Packaged mule application\" dialog.\"", groupId, artifactId);
							    	String message = String.format("Mule project built! %s.%s%n%nLocation saved to clipboard. Paste into following Import dialog.", groupId, artifactId);
							    	EventPortalView.showMessage(message);
							    } else {
							    	String message = String.format("Mule project built! %s.%s%n%nSaved to: %s%n%nLoad into following Import dialog.", groupId, artifactId, generatedArchivePath.toString());
							    	EventPortalView.showMessage(message);
//							    	EventPortalView.showMessage("Mule project built! " + groupId + "." + artifactId +"\n\nLocation: " + generatedArchivePath.toString() + ". Paste into Import \"Packaged mule application\" dialog.");
							    }
					            try {
									IWizardRegistry wizards = PlatformUI.getWorkbench().getImportWizardRegistry();
									IWizardDescriptor wizard = wizards.findWizard("org.mule.tooling.ui.muleZipProjectImportWizard");
									if (wizard != null) {
										final IWizard wiz = wizard.createWizard();  // could throw CoreException
										final WizardDialog wd = new WizardDialog(Display.getDefault().getActiveShell(), wiz);
										wd.setTitle("Mule Import from Deployable Archive");
										wd.open();
									} else {
						            	logger.info("Could not create new Mule Import dialog");
									}
					            } catch (CoreException e) {
					            	logger.warn("Could not create new Import dialog", e);
					            }
							} catch (Exception e) {
				            	logger.warn("Caught trying to build plugin for Mule", e);
						    	String err = String.format("Could not generate Mule project from AsyncAPI.%n%n%s%n%nCheck logs at %s for details.", e.toString(), PreferenceConstants.LOG_LOCATION.getDescription());
						    	EventPortalView.showWarning(err.toString());
//						    	EventPortalView.showWarning("Could not generate Mule project from AsyncAPI. Check logs for details.");
							}
						}
					};
					if (view.runningInMule) {
						a2.setText("Build New Mule Flow Project");
						a2.setImageDescriptor(Icons.getImageDescriptor(Icons.Type.MULE));
						mgr.add(a2);
					}

//					Action aSpring = new Action("Build New Spring Cloud Stream Project") {
//					};
//					aSpring.setImageDescriptor(Icons.getImageDescriptor(Icons.Type.SPRING));
//					aSpring.setEnabled(false);
//					mgr.add(aSpring);
				}
				
				
				Action aCheck = new Action("Check for mule wizard") {
					public void run() {
						StringBuilder sb = new StringBuilder();
						try {
	//						showMessage(node.id);
							
	//						org.eclipse.ui.wizards.datatransfer.ImportOperation asdf
	//						ImportOperation.
	//						IWorkspace sdf = org.eclipse.core.resources.ResourcesPlugin.getWorkspace();
	//						System.out.println(sdf);
	//						sdf.getRoot().g
	//						IProjectDescription proj = sdf.newProjectDescription("test");
	//						proj.
	//						ResourcesPlugin sdf2 = org.eclipse.core.resources.ResourcesPlugin.getPlugin();
	//						System.out.println(sdf2);
	//						sdf2.
							IWizardRegistry wizards = PlatformUI.getWorkbench().getImportWizardRegistry();
							IWizardDescriptor wizard = wizards.findWizard("org.mule.tooling.ui.muleZipProjectImportWizard");
							if (wizard != null) {
								sb.append("ID: ").append(wizard.getId());
								sb.append("\nDesc: ").append(wizard.getDescription());
								sb.append("\nhelpHref: ").append(wizard.getHelpHref());
								sb.append("\nlabel: ").append(wizard.getLabel());
								sb.append("\ntags: ").append(Arrays.toString(wizard.getTags()));
								sb.append("\nhasPages: ").append(wizard.hasPages());
								sb.append("\ntoString: ").append(wizard.toString());
								EventPortalView.showMessage(sb.toString());

								try {
									sb = new StringBuilder();
									final IWizard wiz = wizard.createWizard();
									sb.append("Wiz class type: " + wiz.getClass().getSimpleName());
									wiz.addPages();
									
//									final WizardDialog wd = new WizardDialog(Display.getCurrent().getActiveShell(), wiz);
									final WizardDialog wd = new WizardDialog(Display.getDefault().getActiveShell(), wiz);
									sb.append("\n\nCreated wizdialgog: " + wd.getClass().getSimpleName());
									wd.setTitle("Mule Import from Deployable Archive by Aaron");
//									sb.append("\n\nTitle is: " + wd.setT getTitle());
									
									
									String myString = "This text will be copied into clipboard";
									StringSelection stringSelection = new StringSelection(myString);
									Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
									clipboard.setContents(stringSelection, null);
									
									wd.setBlockOnOpen(false);
									wd.open();

//									sb.append("\n\ntrying class... ");
//									Class<?> clazz = Class.forName("org.mule.tooling.ui.wizards.MuleZippedProjectImportPage");
//									sb.append("trying constructor... ");
//									Constructor<?> ctor = clazz.getConstructor(String.class);
//									sb.append("trying new instance... ");
//									Object object = ctor.newInstance(new Object[] { "muleProjectImportPage" });
//									if (object == null) {
//										sb.append("\n\nObject is null");
//										
//									} else {
//										sb.append("\n\nSuccess! object is " + object.getClass().getSimpleName());
//										
//									}

									Runnable r = new Runnable() {

										@Override
										public void run() {
											StringBuilder sb = new StringBuilder();
											try {
												java.lang.reflect.Field mainPageField = wiz.getClass().getDeclaredField("firstPage");
												sb.append("firstPage field is: " + mainPageField);
												mainPageField.setAccessible(true);
												Object importPage = mainPageField.get(wiz);
												if (importPage == null) {
													EventPortalView.showWarning(sb.toString());
													return;
												} else {
													sb.append("\n\nImport page class: " + importPage.getClass().getSimpleName());
												}
//												Class<?> concreteClass = Class.forName("org.mule.tooling.ui.wizards.MuleZippedProjectImportPage");
												Method[] publicMethods = importPage.getClass().getMethods();
												sb.append("\n\nMethods are: " + Arrays.toString(publicMethods));
												
												Method setFilePathMethod = importPage.getClass().getDeclaredMethod("setFilePath", String.class);
												sb.append("\n\nMy setFilePathMethod sig is: " + setFilePathMethod.toString());
												setFilePathMethod.invoke(importPage, "thisisapath");
												sb.append("\n\nMade it through!");
												
												Logger logger = LogManager.getLogger(EventPortalView.class);
												logger.info("Hello world");
											} catch (NoSuchFieldException e) {
												sb.append("\n\nHad an issue!! " + e.toString());
											} catch (SecurityException e) {
												sb.append("\n\nHad an issue!! " + e.toString());
											} catch (IllegalArgumentException e) {
												sb.append("\n\nHad an issue!! " + e.toString());
											} catch (IllegalAccessException e) {
												sb.append("\n\nHad an issue!! " + e.toString());
											} catch (NoSuchMethodException e) {
												sb.append("\n\nHad an issue!! " + e.toString());
											} catch (InvocationTargetException e) {
												sb.append("\n\nHad an issue!! " + e.toString());
											} finally {
												String myString = sb.toString();
												StringSelection stringSelection = new StringSelection(myString);
												Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
												clipboard.setContents(stringSelection, null);
												EventPortalView.showMessage(sb.toString());
											}
										}
									};
									EventPortalView.executorService.schedule(r, 500, TimeUnit.MILLISECONDS);
								} catch (Exception e) {
									sb.append("\n\nCouldn't instantiate the class!! " + e.toString());
									sb.append(e.getMessage());
									if (e.getCause() != null) {
										sb.append("\nCause: " + e.getCause().toString());
										sb.append(e.getCause().getMessage());
									}
									EventPortalView.showWarning(sb.toString());
								} finally {
//									showMessage(sb.toString());
								}
								
							} else {
								EventPortalView.showWarning("Couldn't find any wizard like that");
							}
						}
						catch (Throwable e) {
							sb.append("\nException: " + e.toString() + "\n");
							EventPortalView.showWarning(sb.toString());
						}
					}
				};
//				mgr.add(new Separator());
				mgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			}
			drillDownAdapter.addNavigationActions(mgr);
			// debug: copy id to clipboard
/*			if (!selection.isEmpty()) {
//				logger.info(selection.toString());
				TreeNode node = (TreeNode)selection.getFirstElement();
				if (node.epObject != null) {  // otherwise we don't know what it is!
					boolean success = EventPortalView.copyStringToClipboard(node.id);
					if (success) {
						mgr.add(new Separator());
						Action actionWatch = new Action("Copy ID to clipboard") {
							public void run() {
								EventPor
							}
						};
						
					}
					actionWatch.setImageDescriptor(Icons.getImageDescriptor(Icons.Type.WATCH));
					actionView.url = PortalLinksUtils.generateUrl(node.epObject, baseUrl);
					mgr.add(actionView);
				}
			}*/
		});
		treeViewer.getControl().setMenu(mgr.createContextMenu(treeViewer.getControl()));		
	}
	

	
	
	
	
}
