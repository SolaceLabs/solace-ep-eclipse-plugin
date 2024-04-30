package com.solace.ep.eclipse.views;

import java.awt.AWTError;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.eclipse.ui.wizards.IWizardRegistry;

import com.solace.ep.eclipse.Activator;
import com.solace.ep.eclipse.prefs.PreferenceConstants;
import com.solace.ep.eclipse.views.Icons.Type;
import com.solace.ep.eclipse.wizards.ImportAsyncAPIWizardHack;

import community.solace.ep.wrapper.EventPortalWrapper;





public class EventPortalView extends ViewPart implements RefreshListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.solace.ep.eclipse.views.EventPortalView";
//	private static final TimeStringFormat TSF = TimeStringFormat.RELATIVE;

	// the resource manager for loading imageRegistry
	ResourceManager resManager = null;
	List<SuperTabView> tabs = new ArrayList<SuperTabView>();
	CTabFolder tabFolder = null;

	@Inject IWorkbench workbench;
	
	private Action actionLoad;
	private Action actionPrefs;
	private Action actionAsyncAPI;

	Composite parent;
	private Set<EpDataListener> loadListeners = new HashSet<>();
	boolean runningInMule = false;
	 
		
	
	
	private static final Logger logger = LogManager.getLogger(EventPortalView.class);
	static ImageRegistry imageRegistry = null;
	static FontRegistry fonts = null;
	static ColorRegistry colors = null;
//
//	public static void register(String key, Image image) {
//		imageRegistry.put(key, image);
//	}
	
	public static void register(String key, ImageDescriptor id) {
		imageRegistry.put(key, id);
	}
	
	public static Image getImage(String key) {
		return imageRegistry.get(key);
	}
	
	public static void register(String key, FontData fd) {
		fonts.put(key, new FontData[] {fd});
	}
	
	public static void register(String key, RGB color) {
		colors.put(key, color);
	}
	

	
	
	@Override
	public void createPartControl(Composite parent) {
		logger.info("STARTING createPartControl() *******************");
		this.parent = parent;
//		ColorUtils.init(PlatformUI.getWorkbench().getThemeManager());
		ColorUtils.init();

		imageRegistry = new ImageRegistry(parent.getDisplay());
		fonts = new FontRegistry(parent.getDisplay());
		colors = new ColorRegistry(parent.getDisplay());
		
		// initialize some fonts
		FontData defaultFontData = fonts.defaultFont().getFontData()[0];
		FontData fontData = new FontData( "Consolas", defaultFontData.getHeight(), SWT.BOLD );
		fonts.put("topic", new FontData[] {fontData});
		
//		FontDescriptor des = fonts.defaultFontDescriptor();
		fontData = new FontData(defaultFontData.getName(), (int)(defaultFontData.getHeight()), SWT.BOLD);
		fonts.put("bold", new FontData[] {fontData});
		fontData = new FontData(defaultFontData.getName(), (int)Math.round(defaultFontData.getHeight() * 0.75), defaultFontData.getStyle());
		fonts.put("small", new FontData[] {fontData});
		fontData = new FontData(defaultFontData.getName(), (int)Math.round(defaultFontData.getHeight() * 1.15), defaultFontData.getStyle());
		fonts.put("big", new FontData[] {fontData});

		Icons.init();
		AnimatedIcons.init();
		
        try {
			IWizardRegistry wizards = PlatformUI.getWorkbench().getImportWizardRegistry();
			IWizardDescriptor wizard = wizards.findWizard("org.mule.tooling.ui.muleZipProjectImportWizard");
			if (wizard != null) {
				runningInMule = true;
			}
        } finally {
        	
        }
		
		Composite comp = new Composite(parent, SWT.BORDER);
		GridLayout gl = new GridLayout();
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
		gl.horizontalSpacing = 0;
		gl.marginTop = 0;
		gl.marginLeft = 0;
		gl.marginRight = 1;
		leftToolbar.setLayout(gl);
		
		leftToolbar.setBackgroundMode(SWT.INHERIT_NONE);  // no borders on buttons??
		
		Button b;
		b = new Button(leftToolbar, SWT.FLAT);
		b.setImage(Icons.getImage(Type.EXPAND));
//		b.setEnabled(false);
		gd = new GridData();
		gd.verticalIndent = 2;
		b.setLayoutData(gd);
		b.setToolTipText("Expand one level");
		b.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int tabIndex = tabFolder.getSelectionIndex();
				SuperTabView tab = tabs.get(tabIndex);
//				CTabItem item = tabFolder.getItem(tab);
//				Control c = item.getControl();
//				if (tab == 0) {
//					System.err.println(c.toString());
//				}
				tab.expandOne();
//				System.err.println("Expand by one on tab " + tabFolder.getSelectionIndex());
			}
			
			@Override public void widgetDefaultSelected(SelectionEvent e) {}
		});

		b = new Button(leftToolbar, SWT.FLAT);
		b.setImage(Icons.getImage(Type.COLLAPSE));
//		b.setEnabled(false);
		gd = new GridData();
		b.setLayoutData(gd);
		b.setToolTipText("Collapse all");
		b.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int tabIndex = tabFolder.getSelectionIndex();
				SuperTabView tab = tabs.get(tabIndex);
//				tab.co
				tab.collapseAll();
//				System.err.println("Collapse by one on tab " + tabFolder.getSelectionIndex());
			}
			
			@Override public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		b = new Button(leftToolbar, SWT.FLAT);  // sort by domain
		b.setImage(Icons.getImage(Type.SORTtype));
		b.setEnabled(false);
		b.setSelection(true);
		gd = new GridData();
		gd.verticalIndent = 10;
		b.setLayoutData(gd);
		b.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.err.println("Sort by domain on tab " + tabFolder.getSelectionIndex());
			}
			
			@Override public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		b = new Button(leftToolbar, SWT.FLAT);  // sort by name
		b.setImage(Icons.getImage(Type.SORTname));
		b.setEnabled(false);
		gd = new GridData();
		b.setLayoutData(gd);
		b.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.info("Sort by name on tab " + tabFolder.getSelectionIndex());
				tabs.get(tabFolder.getSelectionIndex()).sortByAlpha();
			}
			
			@Override public void widgetDefaultSelected(SelectionEvent e) {}
		});

		b = new Button(leftToolbar, SWT.FLAT);
		b.setImage(Icons.getImage(Type.FILTER));
		b.setEnabled(false);
		gd = new GridData();
		gd.verticalIndent = 10;
		b.setLayoutData(gd);
		b.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// filter
			}
			
			@Override public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		b = new Button(leftToolbar, SWT.FLAT);
		b.setImage(Icons.getImage(Type.VISIBLE));
		b.setEnabled(false);
		gd = new GridData();
		gd.verticalIndent = 10;
		b.setLayoutData(gd);
		
		tabFolder = new CTabFolder (comp, SWT.NONE);
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		tabFolder.setLayoutData(gd);

		final Type[] tabIcons = { Type.vAPP, Type.EVENT, Type.SCHEMA, Type.API, Type.APIproduct };
		final String[] tabTitles =  {
				"Applications", "Events", "Schemas", "Event APIs", "Event API Products" };

		for (int i=0; i<tabTitles.length; i++) {
			
			final String title = tabTitles[i];
			CTabItem tabbedItem = new CTabItem (tabFolder, SWT.NONE);
			tabbedItem.setImage(Icons.getImage(tabIcons[i]));
			tabbedItem.setText(title);
			if (i == 0) {
				AppsTab tab = new AppsTab(this, tabFolder, i, this);
				tabs.add(tab);
				loadListeners.add(tab);
				tabbedItem.setControl(tab.getControl());
			} else if (i == 1) {
				EventsTab tab = new EventsTab(this, tabFolder, i, this);
				tabs.add(tab);
				loadListeners.add(tab);
				tabbedItem.setControl(tab.getControl());
			} else if (i == 2) {
				SchemasTab tab = new SchemasTab(this, tabFolder, i, this);
				tabs.add(tab);
				loadListeners.add(tab);
				tabbedItem.setControl(tab.getControl());
			} else if (i == 3) {
				EventAPIsTab tab = new EventAPIsTab(this, tabFolder, i, this);
				tabs.add(tab);
				loadListeners.add(tab);
				tabbedItem.setControl(tab.getControl());
			} else {
				UnusedTab tab = new UnusedTab(this, tabFolder, i, this);
				loadListeners.add(tab);
				tabbedItem.setControl(tab.getControl());
				tabs.add(tab);
			}
		}
		tabFolder.pack ();
		tabFolder.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("WIDget selected " + e.toString());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				System.out.println("WIDget default selected " + e.toString());
			}
		});
		
//		tabFolder.addPaintListener(new PaintListener() {
//			@Override
//			public void paintControl(PaintEvent e) {
//				System.out.println("CTabFolder Paint event fired " + e.toString());
//			}
//		});

		tabFolder.setSelection(0);
		
		
		
		
		
		
		
		// test code   /////////////////////////////////////
		/*
		ITheme theme = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme();
//		PlatformUI.getWorkbench().getDecoratorManager().
//		SWT.COLOR_FO
		
		Set<String> tableCols = new HashSet<String>(Arrays.asList(new String[] {
				"org.eclipse.ui.workbench.FORM_HEADING_INFO_COLOR",
				"org.eclipse.ui.workbench.ACTIVE_TAB_BG_END",
				"org.eclipse.ui.workbench.ACTIVE_NOFOCUS_TAB_BG_END",
				"org.eclipse.egit.ui.IgnoredResourceBackgroundColor",
				"org.eclipse.egit.ui.UncommittedChangeBackgroundColor",
				"org.eclipse.ui.workbench.ACTIVE_NOFOCUS_TAB_BG_START",
				"org.eclipse.ui.workbench.ACTIVE_TAB_BG_START"}));
		
		logger.info(theme.getClass().getName());
		logger.info(theme.getId());
		logger.info(theme.keySet());
		logger.info("Color manager: " + theme.getColorRegistry().getClass().getName());
		logger.info("Color manager: " + theme.getColorRegistry().toString());
		
		for (String key : theme.getColorRegistry().getKeySet()) {
			if (key.toLowerCase().contains("fore")
					|| key.toLowerCase().contains("defau")
					|| key.toLowerCase().contains("text")) {
//				logger.info(key + ": " + theme.getColorRegistry().get(key).getRGB());
			}
			RGB col = theme.getColorRegistry().get(key).getRGB();
			String c = col.toString();
			if (c.matches(".*17., 17.*") || c.matches(".*16., 16.*")) {
				logger.info(key + " ******************* : " + theme.getColorRegistry().get(key).getRGB());
			}
			if (c.matches(".*4., 4., 4.*")) {
				logger.info(key + " *#################* : " + theme.getColorRegistry().get(key).getRGB());
			}
//			if (c.matches(".*0, 0, 0.*")) {
//				logger.info(key + " *%%%%%%%%%%%%%%%%* : " + theme.getColorRegistry().get(key).getRGB());
//			}
			if (tableCols.contains(key)) {
				logger.info(key + " @@@@@@@@@@@@@@ : " + theme.getColorRegistry().get(key).getRGB());
			}
		}
		Color c = Display.getDefault().getSystemColor(SWT.COLOR_LIST_FOREGROUND);
		logger.info("COLOR_LIST_FOREGROUND " + c.getRGB());
		c = Display.getDefault().getSystemColor(SWT.COLOR_INFO_FOREGROUND);
		logger.info("COLOR_INFO_FOREGROUND " + c.getRGB());
		c = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND);
		logger.info("COLOR_WIDGET_FOREGROUND " + c.getRGB());
		*/
		
		
        
        
        
		// Create the help context id for the treeViewer's control
//		workbench.getHelpSystem().setHelp(treeViewer.getControl(), "HelloWorld2.viewer");
		workbench.getHelpSystem().setHelp(tabFolder, "HelloWorld2.viewer");
//		getSite().setSelectionProvider(treeViewer);
		makeToolbarActions();
//		hookContextMenu();
//		hookDoubleClickAction();
		contributeToActionBars();
	}

//	@Override
//	public void dispose() {
////		Icons.dispose();
////		AnimatedIcons.dispose();
//		imageRegistry.dispose();
//		super.dispose();
//	}

	
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
//		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}



//	private void fillLocalPullDown(IMenuManager manager) {
//		manager.add(actionLoad);
//		manager.add(actionPrefs);
//		manager.add(new Separator());
//		logger.info("i'm in fillLocalPullDown()");
//	}

	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(actionLoad);
/*		manager.add(new ContributionItem("abc123") {
		       @Override
		        public void fill(Composite parent) {
		    	   Button b = new Button(parent, SWT.NONE);
		    	   b.setText("this is a button");
		        }

		        @Override
		        public boolean isDynamic() {
		            return true;
		        }
	        });
		manager.add(new Separator());
		Action asf = new Action() { };
		manager.add(new ActionContributionItem(asf));
		manager.add(new Separator());
*/
		
		manager.add(new ControlContribution("asdfsdf") {
			@Override
			protected Control createControl(Composite parent) {
				Combo c = new Combo(parent, SWT.READ_ONLY);
				String org = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.ORG.getId());
				org = String.format("%-10s", org);
				c.setItems(org);
				c.select(0);
				
				Activator.getDefault().getPreferenceStore().addPropertyChangeListener(event -> {
					if (!event.getProperty().toLowerCase().contains("token")) {
						logger.info(String.format("'%s' updated! %s -> %s", event.getProperty(), event.getOldValue(), event.getNewValue()));
					} else {
						logger.info(String.format("'%s' updated!", event.getProperty()));
					}
					if (PreferenceConstants.ORG.getId().equals(event.getProperty())) {
						c.setItem(0, event.getNewValue().toString());
					}
				});
				return c;
			}
		});
		manager.add(new Separator());
		manager.add(actionAsyncAPI);
		manager.add(actionPrefs);
		manager.add(new Separator());
//		drillDownAdapter.addNavigationActions(manager);
	}	
	
	
	
	@PostConstruct
	public void postConstruct() {
		logger.info("STARTING postConstruct() ********************\n\n>");
	}
	
	
	private AnimationFuture startAnimation(AnimationRunnable runnable) {
		ScheduledFuture<?> future = executorService.scheduleAtFixedRate(runnable, 0, AnimatedIcons.ANIMATION_DELAY_MS, TimeUnit.MILLISECONDS);
		return new AnimationFuture(runnable, future);
//		return future;
	}

	private ScheduledFuture<?> startAnimatedLoadIcon() {
		ScheduledFuture<?> future = executorService.scheduleAtFixedRate(() -> {
			Display.getDefault().syncExec(() -> {
				
				actionLoad.setImageDescriptor(AnimatedIcons.getImageDescriptor(AnimatedIcons.AnimType.LOADING));
			});
		}, 0, AnimatedIcons.ANIMATION_DELAY_MS, TimeUnit.MILLISECONDS);
		return future;
	}
	
	private void makeToolbarActions() {
		actionLoad = new Action() {
			public void run() {
				logger.info("Loading/Refresh will go here");
				actionLoad.setEnabled(false);
				final AnimationRunnable runnable = new AnimationRunnable(actionLoad, AnimatedIcons.AnimType.LOADING, Icons.Type.REFRESH);
//				final ScheduledFuture<?> future = startAnimatedLoadIcon();  // kick off the loading animation
				final AnimationFuture animFuture = startAnimation(runnable);
				executorService.submit(() -> {
					try {
						String token = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.TOKEN.getId());
//						Base64.Decoder decoder = Base64.getUrlDecoder();
//						String[] chunks = token.split("\\.");
//						for (int i=0; i<chunks.length; i++) {
//							String piece = new String(decoder.decode(chunks[i]));
//							System.err.println("chunk " + i + ": " + piece);
//						}
						EventPortalWrapper.INSTANCE.setToken(token);
						boolean success = EventPortalWrapper.INSTANCE.loadAll(executorService);  // blocking call until all data loaded
						if (!success) {
							logger.warn("Could not load all Event Portal data!  Ensure token has correct permissions.");
							logger.warn(EventPortalWrapper.INSTANCE.getLoadErrorString());
							StringBuilder sb = new StringBuilder("Could not load all Event Portal data!\n\nError Messages:\n");
							for (String msg : EventPortalWrapper.INSTANCE.getLoadErrorString()) {
								sb.append(msg).append('\n');
							}
							showMessageAsync(sb.toString());
							return;
						}
						for (EpDataListener l : loadListeners) {
							logger.info("calling dataloaded() on " + l);
							l.dataLoaded();
						}
					} catch (Exception e) {
						logger.error("Had issues loading Event Portal data", e);
						e.printStackTrace();
					} finally {
						animFuture.cancel();
//						future.cancel(true);
//						actionLoad.setImageDescriptor(Icons.getImageDescriptor(Icons.Type.REFRESH));
//						actionLoad.setEnabled(true);
						logger.info("End of refresh!  Returning..!");
					}
				});
				
			}
		};
		actionLoad.setText("Load EP Data");
		actionLoad.setToolTipText("Load Event Portal data");
		
		actionLoad.setImageDescriptor(Icons.getImageDescriptor(Icons.Type.LOAD));

		
		actionPrefs = new Action() {
			public void run() {
//				showMessage("Action 2 executed");
				PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(
						new Shell(), PreferenceConstants.PAGE_ID,  
						new String[] { PreferenceConstants.PAGE_ID }, null);
					dialog.open();
			}
		};
		actionPrefs.setText("Event Portal settings");
		actionPrefs.setToolTipText("Configured Event Portal settings");
		actionPrefs.setImageDescriptor(Icons.getImageDescriptor(Icons.Type.GEAR));
//		actionPrefs.setHoverImageDescriptor(Icons.getImageDescriptor(Icons.Type.COLLAPSE));
//		action2.setImageDescriptor(workbench.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		
		actionAsyncAPI = new Action() {
			public void run() {
//				showMessage("clicked on AsyncAPI import!");
				
//	            try {
	            	ImportAsyncAPIWizardHack wiz2 = new ImportAsyncAPIWizardHack();
//					IWizardRegistry wizards = PlatformUI.getWorkbench().getImportWizardRegistry();
//					IWizardDescriptor wizard = wizards.findWizard("com.solace.ep.eclipse.wizards.ImportAsyncAPIWizard");
//					if (wizard != null) {
//						final IWizard wiz = wizard.createWizard();  // could throw CoreException
						wiz2.init(PlatformUI.getWorkbench(), null);
						final WizardDialog wd = new WizardDialog(Display.getDefault().getActiveShell(), wiz2);
						wd.setTitle("AsyncAPI File Import Wizard");
						logger.info("Looks like we should be about to open a dialog");
						wd.open();
//					} else {
//		            	logger.info("Could not create new Mule Import dialog");
//					}
//	            } catch (CoreException e) {
//	            	logger.warn("Could not create new Import dialog", e);
//	            }
			}
		};
		actionAsyncAPI.setToolTipText("Build Project from AsyncAPI file");
		actionAsyncAPI.setImageDescriptor(Icons.getImageDescriptor(Icons.Type.ASYNC));
	
	}

	
	
/*	static class SlowRunnable implements Runnable {

		final int job;
		
		SlowRunnable(int job) {
			this.job = job;
		}
		
		@Override
		public void run() {
			logger.info("SlowRunnable starting job " + job);
			try {
				Thread.sleep(1000);
				logger.info("SlowRunnable finished job " + job);
			} catch (InterruptedException e) {
				logger.error("SlowRunnable job " + job + " got interrupted!");
			}
		}
	}*/
	
	
	static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(20);

	
	static void showMessageAsync(String message) {
		Display.getDefault().asyncExec(() -> {
			showMessage(message);
		});
	}
	
	public static void showMessage(String message) {
		MessageDialog.openInformation(
			Display.getDefault().getActiveShell(),
			"Solace Event Portal plugin",
			message);
	}

	static void showWarningAsync(String message) {
		Display.getDefault().asyncExec(() -> {
			showWarning(message);
		});
	}

	public static void showWarning(String message) {
		MessageDialog.openWarning(
			Display.getDefault().getActiveShell(),
			"Solace Event Portal plugin",
			message);
	}
	
	@Override
	public void setFocus() {
//		tabFolder.setFocus();
		tabs.get(tabFolder.getSelectionIndex()).getControl().setFocus();
//		treeViewer.getControl().setFocus();
	}

	@Override
	public void refreshTab(EpDataListener listener) {
		logger.info("Refresh called for tab " + listener);
		tabFolder.getItem(listener.getIndex()).setControl(listener.getControl());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	


	public static boolean copyStringToClipboard(String s) {
		try {
//			logger.info("System property java.awt.headless = " + System.getProperty("java.awt.headless"));
//			System.setProperty("java.awt.headless", "false");
			StringSelection stringSelection = new StringSelection(s);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, stringSelection);
			return true;
		} catch (HeadlessException | AWTError | IllegalStateException | SecurityException e) {
			logger.warn("Couldn't copy to clipboard!", e);
			return false;
		}
	}
	
}
