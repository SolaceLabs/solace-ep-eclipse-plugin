package com.solace.ep.eclipse.views;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

import com.solace.ep.eclipse.Activator;
import com.solace.ep.eclipse.prefs.PreferenceConstants;
import com.solace.ep.eclipse.views.Icons.Type;
import com.solace.ep.muleflow.eclipse.EclipseProjectGenerator;

import community.solace.ep.client.model.Application;
import community.solace.ep.client.model.Application.BrokerTypeEnum;
import community.solace.ep.client.model.ApplicationDomain;
import community.solace.ep.client.model.ApplicationVersion;
import community.solace.ep.client.model.EventVersion;
import community.solace.ep.wrapper.EventPortalWrapper;
import community.solace.ep.wrapper.PortalLinksUtils;
import community.solace.ep.wrapper.SupportedObjectType;
import dev.solace.aaron.useful.TimeUtils;
import dev.solace.aaron.useful.TimeUtils.TimeStringFormat;
import dev.solace.aaron.useful.WordUtils;





public class EventPortalView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.solace.ep.eclipse.views.SampleView";
	private static final TimeStringFormat TSF = TimeStringFormat.RELATIVE;

	// the resource manager for loading images
	ResourceManager resManager = null;
	CTabFolder tabFolder = null;

	@Inject IWorkbench workbench;
	
	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action actionLoad;
	private Action actionPrefs;
	private Action doubleClickAction;
	private Action actionBuildMuleProf;
	 
	
	private static Set<String> modifySetsCalcIntersection(Set<String> pubs, Set<String> subs) {
		Set<String> both = new HashSet<>(pubs); both.retainAll(subs);
		pubs.removeAll(both);
		subs.removeAll(both);
		return both;
	}
	
	private static String vName(String version, String name) {
		return new StringBuilder().append('v').append(version).append(' ').append(name).toString();
	}



	private static TreeNode root;

	private static void initialize() {
		root = TreeNode.createRootNode();

		
		String token = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.TOKEN.getToken());
		if (token == null || token.isEmpty()) {
			root.addChild(new TreeNode.Builder().withId("abc").build("Please configure your", "", "", "", "", ""));
			root.addChild(new TreeNode.Builder().withId("def").build("Event Portal access token", "", "", "", "", ""));
			root.addChild(new TreeNode.Builder().withId("xyz").build("in preferences", "", "", "", "", ""));
			return;
		}
		
		EventPortalWrapper epw = EventPortalWrapper.INSTANCE;
		// SE Demo
//		epw.setToken("eyJhbGciOiJSUzI1NiIsImtpZCI6Im1hYXNfcHJvZF8yMDIwMDMyNiIsInR5cCI6IkpXVCJ9.eyJvcmciOiJzZWFsbGRlbW8iLCJvcmdUeXBlIjoiRU5URVJQUklTRSIsInN1YiI6IjY3dHI4dGt1NDEiLCJwZXJtaXNzaW9ucyI6IkFBQUFBSUFQQUFBQWZ6Z0E0QUVBQUFBQUFBQUFBQUFBQUlDeHpvY2hJQWpnTC8vL2c1WGZCZDREV01NRDQ0ZS9NUT09IiwiYXBpVG9rZW5JZCI6IjZicnQ5ZDRqazhvIiwiaXNzIjoiU29sYWNlIENvcnBvcmF0aW9uIiwiaWF0IjoxNjg5Nzk3NTkyfQ.buYOJRYDDBxtS0UspYX9mjxyN5W5WPrkPWBJk5__ejZG5UhMaNXuPKlDAnHBWXaoFFNQPkGg81CDA7I-xPTmOEVeA_PmzGKnAQjDKoTn5ySw_tEWrNAwRSMshk6V7iQCUyFSuSveBnOArG9hLaFFX2jZemHtFFWc_159_BTbt198LxRX8rPx5a29shyYKcwyYOciGrNSzD7hm4A21OltpB9dJk-01GjwECrKhqVcNzz_kHzkNZ1ltWrmW6FFVnfBpa4bTf12psi7j11HK50uWqydiW30mikgij782uXJBFF4DuBsQf-Pvh3Of-S4kHIszeX2V958Pg__4Z1uKG1Nyg");
		epw.setToken(Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.TOKEN.getToken()));
		// CTO
//			epw.setToken("eyJhbGciOiJSUzI1NiIsImtpZCI6Im1hYXNfcHJvZF8yMDIwMDMyNiIsInR5cCI6IkpXVCJ9.eyJvcmciOiJzb2xhY2VjdG8iLCJvcmdUeXBlIjoiRU5URVJQUklTRSIsInN1YiI6IjY3dHI4dGt1NDEiLCJwZXJtaXNzaW9ucyI6IkFBQUFBSUFQQUFBQWZ6Z0E0QUVBQUFBQUFBQUFBQUFBQUlDeHpvY2hJQWpnTC8vL2c1WGZCZDREV01NRDQ0ZS9NUT09IiwiYXBpVG9rZW5JZCI6IjBra21xMnc0ZTF6IiwiaXNzIjoiU29sYWNlIENvcnBvcmF0aW9uIiwiaWF0IjoxNjg4MzkzNTU1fQ.I9tr6VolbXeGNNNyW3ASVtg-sa5yFNKgivSfIDslpA-e-Xd45DYSy_mhmZz7vfyFge7QRJF4NGQFd6x4R55mheRLPh1OU7Rai4rHchy6MKwTX9tNpWbhZZbHaya0qWN86WWtLg7_26di79Gm01D7wPuMnjMJjQAduzasbuQBOlT-nS4APwAuE-ny7FeRc8AoVgIJYTqTF954WS56iU4nKqh2eTbvlPwLODFgiVTPF0g-RYat7qo5eYZypRgPtfO3c24bx6Ycuq-0-uZH6RL522ZJ-IcMufAKxfAYqPw0FCP6nubNn1EHdIe46YeC2q-z2Ptm1FCTNgs49-tz-nGjyw");
		
		epw.loadDomains();
		epw.loadApplications();  // apps and versions
		epw.loadEvents(false);   // only events
		epw.loadStates();
//			epw.loadEvents();
//			epw.loadSchemas();
		
		for (ApplicationDomain domain : epw.getDomains()) {
			TreeNode.Builder builder = new TreeNode.Builder()
					.withEpType(SupportedObjectType.DOMAIN)
					.withId(domain.getId())
					.withEpObject(domain)
					.withIconType(Type.DOMAIN);
			String time = TimeUtils.formatTime(domain.getUpdatedTime(), TSF);
			String details = WordUtils.pluralize("Application", domain.getStats().getApplicationCount());
			TreeNode domNode = builder.build("Domain", domain.getName(), details, null, null, time);
//			TreeNode domNode = new TreeNode(type, domain.getId(), domain, iconType, "Domain", domain.getName(), details, null, null, time);
			root.addChild(domNode);
			for (Application app : epw.getApplicationsForDomainId(domain.getId())) {
				builder = new TreeNode.Builder()
						.withEpType(SupportedObjectType.APPLICATION)
						.withId(app.getId())
						.withEpObject(app)
						.withIconType(Type.APP);
//				type = SupportedObjectType.APPLICATION;
//				iconType = Type.APP;
				time = TimeUtils.formatTime(app.getUpdatedTime(), TSF);
				String brokerType = WordUtils.capitalFirst(app.getBrokerType().getValue());
				details = String.format("%s App, %s",
						brokerType,
		                WordUtils.capitalFirst(app.getApplicationType()));
				details += ", " + WordUtils.pluralize("Version", app.getNumberOfVersions());
				TreeNode appNode = builder.build("App", app.getName(), details, null, null, time);
//				TreeNode appNode =  new TreeNode(type, app.getId(), app, iconType, "App", app.getName(), details, null, null, time);
				domNode.addChild(appNode);
				for (ApplicationVersion appVer : epw.getApplicationVersionsForApplicationId(app.getId())) {
					builder = new TreeNode.Builder()
							.withEpType(SupportedObjectType.APPLICATION_VERSION)
							.withId(appVer.getId())
							.withEpObject(appVer)
							.withIconType(Type.vAPP);

//					Application app = EventPortalWrapper.INSTANCE.getApplication(appVer.getApplicationId());
//					item.setImage(images.get("vApp"));
					
					Set<String> pubs = new HashSet<>(appVer.getDeclaredProducedEventVersionIds());
					Set<String> subs = new HashSet<>(appVer.getDeclaredConsumedEventVersionIds());
					Set<String> both = modifySetsCalcIntersection(pubs, subs);
					
					String name = vName(appVer.getVersion(), app.getName());
					details = WordUtils.pluralize(brokerType + " Event", pubs.size() + subs.size() + both.size()) + " referenced";
					String state = EventPortalWrapper.INSTANCE.getState(appVer.getStateId()).getName();
					String topic = "";
					String updated = TimeUtils.formatTime(appVer.getUpdatedTime(), TSF);

					TreeNode appVerNode = builder.build("vApp", name, details, state, topic, updated, "View AsyncAPI");
//					TreeNode appVerNode =  new TreeNode(type, appVer.getId(), appVer, iconType, "vApp", name, details, state, topic, updated, "View AsyncAPI");
					appNode.addChild(appVerNode);
				
					for (String eventId : both) {
						helperAddEventVersion(eventId, appVerNode, Dir.BOTH, brokerType);
					}
					for (String eventId : pubs) {
						helperAddEventVersion(eventId, appVerNode, Dir.PUB, brokerType);
					}
					for (String eventId : subs) {
						helperAddEventVersion(eventId, appVerNode, Dir.SUB, brokerType);
					}
				}
			}
		}
	}
	
	public enum Dir {
		PUB("Pub","Published","Pub→"),
		SUB("Sub","Subscribed", "→Sub"),
		BOTH("Pub/Sub", "Pub'ed & Sub'ed", "→Both→"),
		;
		
		final String small;
		final String big;
		final String arrows;
		
		Dir(String s, String b, String a) {
			this.small = s;
			this.big = b;
			this.arrows = a;
		}
	}
	
	static void helperAddEventVersion(String vEventId, TreeNode nodeParent, Dir pubSub, String brokerType) {
		TreeNode.Builder builder = new TreeNode.Builder()
				.withEpType(SupportedObjectType.EVENT_VERSION)
				.withId(vEventId);
		switch (pubSub) {
			case PUB: builder.withIconType(Type.vEVENTpub); break;
			case SUB: builder.withIconType(Type.vEVENTsub); break;
			case BOTH: builder.withIconType(Type.vEVENTboth); break;
			default: builder.withIconType(Type.vEVENT);
		}
		EventVersion eventVer = EventPortalWrapper.INSTANCE.getEventVersion(vEventId);
		if (eventVer == null) {  // haven't loaded yet
			TreeNode eventVerNode = builder.build("vEvent", pubSub.arrows + " ID: " + vEventId, pubSub.big + " " + brokerType + " Event", null, null, null, "View in Portal");
//			TreeNode eventVerNode =  new TreeNode(SupportedObjectType.EVENT_VERSION, vEventId, eventVer, iconType, "vEvent", pubSub.arrows + " ID: " + vEventId, pubSub.big + " " + brokerType + " Event", null, null, null, "View in Portal");
			nodeParent.addChild(eventVerNode);
		} else {  // not implemented yet
		}
	}
	
	class ChildLabelProvider extends ColumnLabelProvider /* implements IStyledLabelProvider */ {
		
		final int col;
		
		public ChildLabelProvider(int col) {
			this.col = col;
		}

		@Override
		public String getText(Object obj) {
			TreeNode o = (TreeNode)obj;
			if (col > o.content.length-1) return "-";
			return o.content[col];
		}
		
		@Override
		public Image getImage(Object obj) {
			return null;
		}
	}



	@Override
	public void createPartControl(Composite parent) {
		
		// create the manager and bind to a widget
		resManager = new LocalResourceManager(JFaceResources.getResources(), parent);
//		System.out.println("Resource Manager is: " + resManager);
		Icons.getInstance().init(resManager);
		
		
		
		
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
		gl.horizontalSpacing = 0;
		gl.marginTop = 0;
		gl.marginLeft = 0;
		gl.marginRight = 1;
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
		
/*		b= new Button(leftToolbar, SWT.PUSH);
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
*/
		
		String token = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.TOKEN.getToken());
		System.out.printf("TOKEN IS: '%s'%n", token);


		b = new Button(leftToolbar, SWT.FLAT);
		b.setImage(Icons.getInstance().getImage(Type.EXPAND));
		gd = new GridData();
		gd.verticalIndent = 2;
		b.setLayoutData(gd);
		b.setToolTipText("Expand one level");

		b = new Button(leftToolbar, SWT.SMOOTH);
		b.setImage(Icons.getInstance().getImage(Type.COLLAPSE));
		gd = new GridData();
		b.setLayoutData(gd);
		b.setToolTipText("Collapse one level");
		
		b = new Button(leftToolbar, SWT.FLAT);  // sort by alpha
		b.setImage(Icons.getInstance().getImage(Type.SORTtype));
		b.setSelection(true);
		gd = new GridData();
		gd.verticalIndent = 10;
		b.setLayoutData(gd);
		
		b = new Button(leftToolbar, SWT.FLAT);  // sort by domain
		b.setImage(Icons.getInstance().getImage(Type.SORTname));
		gd = new GridData();
		b.setLayoutData(gd);

		b = new Button(leftToolbar, SWT.PUSH);
		b.setImage(Icons.getInstance().getImage(Type.FILTER));
		b.setEnabled(false);
		gd = new GridData();
		gd.verticalIndent = 10;
		b.setLayoutData(gd);
		
		b = new Button(leftToolbar, SWT.PUSH);
		b.setImage(Icons.getInstance().getImage(Type.VISIBLE));
		b.setEnabled(false);
		gd = new GridData();
		gd.verticalIndent = 10;
		b.setLayoutData(gd);
		
//		ProgressBar pb = new ProgressBar(leftToolbar,SWT.HORIZONTAL);
//        pb.setMinimum(0);
//        pb.setMaximum(10);
////        pb.setSize(30, 16);
//        pb.setBounds(0,0,30,16);
//		gd = new GridData();
//		gd.verticalIndent = 10;
//		pb.setLayoutData(gd);

		
		
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

		final Type[] tabIcons = { Type.APP, Type.EVENT, Type.SCHEMA, Type.API, Type.APIproduct };
		final String[] tabTitles =  {
				"Applications", "Events", "Schemas", "Event APIs", "Event API Products" };
//		final String[] tabIcons = new String[] {
//				"hex-large.png", "event-large.png", "triangle-large.png", "square-large.png", "diamond-large.png" };

		for (int i=0; i<tabTitles.length; i++) {
			
			final String title = tabTitles[i];
			CTabItem tabbedItem = new CTabItem (tabFolder, SWT.NONE);
//			gl = new GridLayout();
			
//			item.setImage(new Image(tabFolder.getDisplay(), getClass().getResourceAsStream("/icons/" + tabIcons[i])));
			tabbedItem.setImage(Icons.getInstance().getImage(tabIcons[i]));
			tabbedItem.setText(title);
			try {
				if (i == 0) {
					/*
					if (token == null || token.isEmpty()) {
						Label l = new Label(tabFolder, SWT.NONE);
//						Composite c = new Composite(tabFolder, SWT.NONE);
//						gl = new GridLayout();
//						gl.
//						c.setLayout(gl);
						l.setText("Please configure your Event Portal access token in preferences");
						GridData gd2 = new GridData();
						gd2.horizontalAlignment = SWT.CENTER;
						gd2.verticalAlignment = SWT.CENTER;
						l.setLayoutData(gd2);
						tabbedItem.setControl(l);
						
						
						
						continue;
					}
					*/
					
					
//					AppsTab tab = new AppsTab();
//					tab.buildAppsTree(tabFolder);
//					item.setControl(tab.viewer.getControl());
//					viewer = tab.viewer;
//					item.setControl(tab.viewer.getControl());
					
//					AaTree tree = new AaTree(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
//					item.setControl(tree.getTree());
					
					viewer = new TreeViewer(tabFolder, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
					drillDownAdapter = new DrillDownAdapter(viewer);
					
					viewer.setContentProvider(new TreeNodeViewContentProvider());
					viewer.getTree().setHeaderVisible(true);
					viewer.getTree().setLinesVisible(true);
//					viewer.setLabelProvider(new RootLevelLabelProvider());
					
//			        GridLayoutFactory.fillDefaults().generateLayout(parent);

			        TreeViewerColumn viewerColumn = new TreeViewerColumn(viewer, SWT.NONE);
			        viewerColumn.getColumn().setWidth(150);
			        viewerColumn.getColumn().setText("Type");
			        viewerColumn.setLabelProvider(new TreeNodeTypeLabelProvider());
//			        viewerColumn.setLabelProvider(new ViewLabelProvider());
			//
			        int col = 1;
			        viewerColumn = new TreeViewerColumn(viewer, SWT.NONE);
			        viewerColumn.getColumn().setWidth(200);
			        viewerColumn.getColumn().setText("Name");
			        viewerColumn.setLabelProvider(new ChildLabelProvider(col++));
			        
			        viewerColumn = new TreeViewerColumn(viewer, SWT.NONE);
			        viewerColumn.getColumn().setWidth(200);
			        viewerColumn.getColumn().setText("Details");
			        viewerColumn.setLabelProvider(new ChildLabelProvider(col++));

			        viewerColumn = new TreeViewerColumn(viewer, SWT.NONE);
			        viewerColumn.getColumn().setWidth(75);
			        viewerColumn.getColumn().setText("State");
			        viewerColumn.setLabelProvider(new TreeNodeStateProvider());
			        col++;
			        
			        viewerColumn = new TreeViewerColumn(viewer, SWT.NONE);
			        viewerColumn.getColumn().setWidth(250);
			        viewerColumn.getColumn().setText("Topic");
			        viewerColumn.setLabelProvider(new TreeNodeTopicProvider(viewer.getTree().getFont(), parent.getDisplay()));
			        col++;
			        
			        viewerColumn = new TreeViewerColumn(viewer, SWT.NONE);
			        viewerColumn.getColumn().setWidth(150);
			        viewerColumn.getColumn().setText("Last Updated");
			        viewerColumn.setLabelProvider(new ChildLabelProvider(col++));
			        
//			        viewerColumn = new TreeViewerColumn(viewer, SWT.NONE);
//			        viewerColumn.getColumn().setWidth(100);
//			        viewerColumn.getColumn().setText("Action");
//			        viewerColumn.setLabelProvider(new ChildLabelProvider(col++));

					tabbedItem.setControl(viewer.getControl());
//					tabbedItem.setControl(viewer.getTree());

			        initialize();
			        viewer.setInput(root);  // I guess this initializes the tree?
			        viewer.expandToLevel(2);
//					viewer.setInput(getViewSite());  // I guess this initializes the tree?

					
					
					
					
					
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
					tabbedItem.setControl(l);
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
				tabbedItem.setControl(l);
			}
		}
		tabFolder.pack ();
//		tabFolder.showItem(first);
		tabFolder.setSelection(0);
//		tabFolder.setVisible(true);
//		tabFolder.setEnabled(true);
		
		
		
		
		
		
		
		class LinkAction extends Action {
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
		final LinkAction a4 = new LinkAction("View in Event Portal 🡵");
		a4.setImageDescriptor(Icons.getInstance().getImageDescripor(Icons.Type.PORTAL));

		final MenuManager mgr = new MenuManager();
		mgr.setRemoveAllWhenShown(true);
		
		mgr.addMenuListener(manager -> {
			IStructuredSelection selection = viewer.getStructuredSelection();
			if (!selection.isEmpty()) {
				TreeNode node = (TreeNode)selection.getFirstElement();
				String baseUrl = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.URL.getToken());
				a4.url = PortalLinksUtils.generateUrl(node.epObject, baseUrl);
				mgr.add(a4);
				if (node.iconType == Type.vEVENT) {
					a4.setEnabled(false);  // disabled for now since we don't have the actual object, no event ID
				}
				
				if (node.iconType == Type.vAPP) {
					ApplicationVersion appVer = (ApplicationVersion)node.epObject;
					Application app = (Application)node.getParent().epObject;
					ApplicationDomain domain = (ApplicationDomain)node.getParent().getParent().epObject;
					final Action a2 = new Action("") {
						public void run() {
							String asyncApi = EventPortalWrapper.INSTANCE.getAsyncApiForAppVerId(appVer.getId(), true);
							System.out.println("AsyncAPI spec is: ");
							System.out.println(asyncApi);
							
						    final String
//				            groupId = "com.test.ep",
//				            artifactId = "sample-mule-project",
//				            version = "0.0.1";
						    groupId = helperMakePackageName("com." + domain.getName()),
				            artifactId = helperStripNonChars(app.getName()),
				            version = appVer.getVersion();
						    
//						    String xmlString = "";
//						    try {
//						    	xmlString = MuleFlowGenerator.getMuleDocXmlFromAsyncApiString(asyncApi);
//						    	System.out.println("The XML String is: " + xmlString);
//						    } catch (Exception e) {
//						    	System.err.println("MuleFlowGenerator.getMuleDocXmlFromAsyncApiString() failed");
//						    	e.printStackTrace();
//						    	return;
//						    }
					        String generatedArchive = "NADA";
					        EclipseProjectGenerator epg = new EclipseProjectGenerator();
					        try {
//					            generatedArchive = epg.createMuleProject(groupId, artifactId, version, xmlString);
//					        	String saveLocation = System.getProperty("user.home") + System.getProperty(baseUrl)
					        	String filename = artifactId + "-" + version + ".jar";
					        	File saveFile = new File(System.getProperty("user.home"), filename);
					        	generatedArchive = saveFile.getAbsolutePath();
					            epg.generateEclipseArchiveForMuleFlowFromAsyncApi(groupId, artifactId, version, asyncApi, saveFile.getAbsolutePath());
					        } catch (Exception e) {
						    	System.err.println("EclipseProjectGenerator.createMuleProject() failed");
						    	e.printStackTrace();
					            return;
					        }

					        // If we're here, we succeeded
					        Path generatedArchivePath = Paths.get(generatedArchive);						    
						    System.out.println("### DONE!  " + generatedArchivePath.toString());
							showMessage("Mule project built! " + groupId + "." + artifactId +"\nJAR saved at: " + generatedArchivePath.toString());
						}
					};
					a2.setText("Build New Mule Flow Project");
					a2.setImageDescriptor(Icons.getInstance().getImageDescripor(Icons.Type.MULE));
					mgr.add(a2);
					// this Mule build app thing is only available for Solace so far
//					Application app = (Application)node.getParent().epObject;
					if (app.getBrokerType() != BrokerTypeEnum.SOLACE) {
						a2.setEnabled(false);
					} else {
						
					}
					Action aSpring = new Action("Build New Spring Cloud Stream Project") {
					};
					aSpring.setImageDescriptor(Icons.getInstance().getImageDescripor(Icons.Type.SPRING));
					aSpring.setEnabled(false);
					mgr.add(aSpring);
				}
/*				
				mgr.add(new Action("Check for mule wizard") {
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
								showMessage(sb.toString());
								
								IWizard wiz = wizard.createWizard();
								WizardDialog wd = new WizardDialog(Display.getCurrent().getActiveShell(), wiz);
	//							wd.
								wd.setTitle(wiz.getWindowTitle());
								wd.open();
								
								
							} else {
								showMessage("Couldn't find any wizard like that");
							}
						}
						catch (Throwable e) {
							sb.append("\nException: " + e.toString() + "\n");
							showMessage(sb.toString());
						}
					}
				});
*/				
				mgr.add(new Separator());
				drillDownAdapter.addNavigationActions(mgr);
				mgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			}
		});
		viewer.getControl().setMenu(mgr.createContextMenu(viewer.getControl()));		
		
		
		
		
		
		
        
        
        
		// Create the help context id for the viewer's control
		workbench.getHelpSystem().setHelp(viewer.getControl(), "HelloWorld2.viewer");
		getSite().setSelectionProvider(viewer);
		makeActions();
//		hookContextMenu();
//		hookDoubleClickAction();
		contributeToActionBars();
	}

	@SuppressWarnings("unused")
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				EventPortalView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
//		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(actionLoad);
		manager.add(actionPrefs);
		manager.add(new Separator());
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(actionBuildMuleProf);
//		manager.add(actionLoad);
//		manager.add(actionPrefs);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(actionLoad);
		manager.add(actionPrefs);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}
	
	
	
	
	@PostConstruct
	public void hello() {
		System.out.println("This is post constrcuter hellooooooo");
//		ViewContentProvider.initialize();
	}
	
	private void makeActions() {
		actionLoad = new Action() {
			public void run() {
				showMessage("Loading/Refresh will go here");
			}
		};
		actionLoad.setText("Action 1");
		actionLoad.setToolTipText("Action 1 tooltip");
		
//		action1.setIm
		
		
//		action1.setImageDescriptor(createImageDescriptor("/icons/execute.png"));
//		action1.setImageDescriptor(createFromImage(execute));
		actionLoad.setImageDescriptor(Icons.getInstance().getImageDescripor(Icons.Type.EXECUTE));
//		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		
		actionBuildMuleProf = new Action() {
			public void run() {
				showMessage("I am now executing Dennis' code here");
			}
		};
		actionBuildMuleProf.setText("Build Mule Project");
		actionBuildMuleProf.setImageDescriptor(Icons.getInstance().getImageDescripor(Icons.Type.MULE));

		actionPrefs = new Action() {
			public void run() {
//				showMessage("Action 2 executed");
				PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(
						new Shell(), PreferenceConstants.PAGE_ID,  
						new String[] { PreferenceConstants.PAGE_ID }, null);
					dialog.open();
			}
		};
		actionPrefs.setText("Action 2");
		actionPrefs.setToolTipText("Action 2 tooltip");
		actionPrefs.setImageDescriptor(Icons.getInstance().getImageDescripor(Icons.Type.GEAR));
//		action2.setImageDescriptor(workbench.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		doubleClickAction = new Action() {
			public void run() {
				IStructuredSelection selection = viewer.getStructuredSelection();
				Object obj = selection.getFirstElement();
				showMessage("Double-click detected on "+obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				System.out.println(event.getViewer().getSelection());
				doubleClickAction.run();
			}
		});
	}

	// "Point of sale (POS) terminal" -> "PointOfSalePOSTerminal"
	private static String helperStripNonChars(String name) {
		StringBuilder sb = new StringBuilder();
		boolean capNext = false;
		for (int i=0; i<name.length(); i++) {
			char c = name.charAt(i);
			if (Character.isAlphabetic(c)) {
				sb.append(capNext ? Character.toUpperCase(c) : c);
				capNext = false;
			} else if (Character.isDigit(c)) {
				sb.append(c);
				capNext = true;
			} else {
				capNext = true;
			}
		}
		return sb.toString();
	}

	// "ACME Retail Supply Logistics" -> "com.acme.retail"
	private static String helperMakePackageName(String name) {
		StringBuilder sb = new StringBuilder();
		name = "com." + name.toLowerCase();
		boolean skipToNextAlpha = true;
		int depth = 0;
		for (int i=0; i<name.length(); i++) {
			char c = name.charAt(i);
			if (Character.isAlphabetic(c)) {
				sb.append(c);
				skipToNextAlpha = false;
			} else {
				if (!skipToNextAlpha) {
					skipToNextAlpha = true;
					depth++;
					if (depth == 3) return sb.toString();  // done!
					sb.append(".");
				}
			}
		}
		return sb.toString();
	}
	
	
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Solace Event Portal",
			message);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
