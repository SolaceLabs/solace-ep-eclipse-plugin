package dev.solace.portal.plugin.parts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import community.solace.ep.client.model.Application;
import community.solace.ep.client.model.ApplicationDomain;
import community.solace.ep.client.model.ApplicationVersion;
import community.solace.ep.client.model.Event;
import community.solace.ep.client.model.EventVersion;
import community.solace.ep.client.model.SchemaObject;
import community.solace.ep.client.model.SchemaVersion;
import community.solace.ep.wrapper.EventPortalWrapper;
import community.solace.ep.wrapper.TopicUtils;
import dev.solace.aaron.useful.TimeUtils;
import dev.solace.aaron.useful.TimeUtils.Format;
import dev.solace.aaron.useful.WordUtils;

public class AaTree {

	
	public static int INDENT_SIZE = 4;
	
	final Tree tree;
	Map<String, TreeItem> rows = new HashMap<>();
	final Font font;
	final FontData fd;
	final Font fwFont;
	final Font bFont;
	
	public Tree getTree() {
		return tree;
	}
	
	static Map<String,Image> images = null;
	
	
	public AaTree(Composite parent, int style) {
		tree = new Tree(parent, style);
		font = tree.getFont();
		fd = font.getFontData()[0];
//		nFont = new Font(font.getDevice(), fd);
		bFont = new Font(parent.getDisplay(), new FontData( fd.getName(), fd.getHeight(), SWT.BOLD ) );
		fwFont = new Font(parent.getDisplay(), new FontData( "Consolas", fd.getHeight(), SWT.BOLD ) );
		if (images == null) initImages(parent.getDisplay());
		init(0);
//		bFont.dispose();
//		fwFont.dispose();
		
		// for tooltips in tree: https://stackoverflow.com/questions/22659055/settooltiptext-for-treeitem-not-defined
		TreeViewer tv = new TreeViewer(parent);
		ColumnViewerToolTipSupport.enableFor(tv);
	}

	private void init(int indent) {
		tree.setHeaderVisible(true);
		TreeColumn column1 = new TreeColumn(tree, SWT.LEFT);
		column1.setText("Type");
		column1.setWidth(200);
		TreeColumn column2 = new TreeColumn(tree, SWT.LEFT);
		column2.setText("Name");
		column2.setWidth(300);
		TreeColumn column3 = new TreeColumn(tree, SWT.LEFT);
		column3.setText("Details");
		column3.setWidth(200);
		TreeColumn columnNext;
		columnNext = new TreeColumn(tree, SWT.LEFT);
		columnNext.setText("State");
		columnNext.setWidth(100);
		columnNext = new TreeColumn(tree, SWT.LEFT);
		columnNext.setText("Topic");
		columnNext.setWidth(200);
		columnNext = new TreeColumn(tree, SWT.LEFT);
		columnNext.setText("Last Updated");
		columnNext.setWidth(150);
		columnNext = new TreeColumn(tree, SWT.LEFT);
		columnNext.setText("Action");
		columnNext.setWidth(100);
		
		EventPortalWrapper epw = EventPortalWrapper.INSTANCE;
		// SE Demo
		epw.setToken("eyJhbGciOiJSUzI1NiIsImtpZCI6Im1hYXNfcHJvZF8yMDIwMDMyNiIsInR5cCI6IkpXVCJ9.eyJvcmciOiJzZWFsbGRlbW8iLCJvcmdUeXBlIjoiRU5URVJQUklTRSIsInN1YiI6IjY3dHI4dGt1NDEiLCJwZXJtaXNzaW9ucyI6IkFBQUFBSUFQQUFBQWZ6Z0E0QUVBQUFBQUFBQUFBQUFBQUlDeHpvY2hJQWpnTC8vL2c1WGZCZDREV01NRDQ0ZS9NUT09IiwiYXBpVG9rZW5JZCI6IjZicnQ5ZDRqazhvIiwiaXNzIjoiU29sYWNlIENvcnBvcmF0aW9uIiwiaWF0IjoxNjg5Nzk3NTkyfQ.buYOJRYDDBxtS0UspYX9mjxyN5W5WPrkPWBJk5__ejZG5UhMaNXuPKlDAnHBWXaoFFNQPkGg81CDA7I-xPTmOEVeA_PmzGKnAQjDKoTn5ySw_tEWrNAwRSMshk6V7iQCUyFSuSveBnOArG9hLaFFX2jZemHtFFWc_159_BTbt198LxRX8rPx5a29shyYKcwyYOciGrNSzD7hm4A21OltpB9dJk-01GjwECrKhqVcNzz_kHzkNZ1ltWrmW6FFVnfBpa4bTf12psi7j11HK50uWqydiW30mikgij782uXJBFF4DuBsQf-Pvh3Of-S4kHIszeX2V958Pg__4Z1uKG1Nyg");
		// CTO
//		epw.setToken("eyJhbGciOiJSUzI1NiIsImtpZCI6Im1hYXNfcHJvZF8yMDIwMDMyNiIsInR5cCI6IkpXVCJ9.eyJvcmciOiJzb2xhY2VjdG8iLCJvcmdUeXBlIjoiRU5URVJQUklTRSIsInN1YiI6IjY3dHI4dGt1NDEiLCJwZXJtaXNzaW9ucyI6IkFBQUFBSUFQQUFBQWZ6Z0E0QUVBQUFBQUFBQUFBQUFBQUlDeHpvY2hJQWpnTC8vL2c1WGZCZDREV01NRDQ0ZS9NUT09IiwiYXBpVG9rZW5JZCI6IjBra21xMnc0ZTF6IiwiaXNzIjoiU29sYWNlIENvcnBvcmF0aW9uIiwiaWF0IjoxNjg4MzkzNTU1fQ.I9tr6VolbXeGNNNyW3ASVtg-sa5yFNKgivSfIDslpA-e-Xd45DYSy_mhmZz7vfyFge7QRJF4NGQFd6x4R55mheRLPh1OU7Rai4rHchy6MKwTX9tNpWbhZZbHaya0qWN86WWtLg7_26di79Gm01D7wPuMnjMJjQAduzasbuQBOlT-nS4APwAuE-ny7FeRc8AoVgIJYTqTF954WS56iU4nKqh2eTbvlPwLODFgiVTPF0g-RYat7qo5eYZypRgPtfO3c24bx6Ycuq-0-uZH6RL522ZJ-IcMufAKxfAYqPw0FCP6nubNn1EHdIe46YeC2q-z2Ptm1FCTNgs49-tz-nGjyw");
		
		epw.loadDomains();
		epw.loadApplications();
		epw.loadStates();
//		epw.loadEvents();
//		epw.loadSchemas();
		
		for (ApplicationDomain domain : epw.getDomains()) {
			addDomain(domain, newRow(domain.getId()), indent);
		}
		tree.pack();
	}	

	private void initImages(Display display) {
		images = new HashMap<>();
		images.put("domain", new Image(display, getClass().getResourceAsStream("/icons/domain-large.png")));
		images.put("app", new Image(display, getClass().getResourceAsStream("/icons/hex-large.png")));
		images.put("vApp", new Image(display, getClass().getResourceAsStream("/icons/hex-small-pub.png")));
		images.put("event", new Image(display, getClass().getResourceAsStream("/icons/event-large.png")));
		images.put("vEvent", new Image(display, getClass().getResourceAsStream("/icons/event-small.png")));
		images.put("vEventPub", new Image(display, getClass().getResourceAsStream("/icons/event-small-pub.png")));
		images.put("vEventSub", new Image(display, getClass().getResourceAsStream("/icons/event-small-sub.png")));
		images.put("vEventBoth", new Image(display, getClass().getResourceAsStream("/icons/event-small-both.png")));
		images.put("vSchema", new Image(display, getClass().getResourceAsStream("/icons/triangle-small.png")));
	}

	// LOL terrible way!
	private static final Color DEF = Display.getCurrent().getSystemColor(23);
	private static final Color REL = new Color(java.awt.Color.decode("#59A869").getRed(),java.awt.Color.decode("#59A869").getGreen(),java.awt.Color.decode("#59A869").getBlue());
	private static final Color DRAFT = new Color(java.awt.Color.decode("#389FD6").getRed(),java.awt.Color.decode("#389FD6").getGreen(),java.awt.Color.decode("#389FD6").getBlue());
	private static final Color DEP = new Color(java.awt.Color.decode("#EDA200").getRed(),java.awt.Color.decode("#EDA200").getGreen(),java.awt.Color.decode("#EDA200").getBlue());
	private static final Color RET = new Color(java.awt.Color.decode("#DB5860").getRed(),java.awt.Color.decode("#DB5860").getGreen(),java.awt.Color.decode("#DB5860").getBlue());
	
	private static Color getStateColor(String stateId) {
		if (EventPortalWrapper.INSTANCE.getState(stateId) == null) return DEF;  // default
		String state = EventPortalWrapper.INSTANCE.getState(stateId).getName();
        switch (state) {
        case "Released":
        		return REL;
//                cellComponent.setForeground(blend(table.getForeground(), Color.decode("#59A869"), 0.3333f));
        case "Draft":
        	return DRAFT;
//            cellComponent.setForeground(blend(table.getForeground(), java.awt.Color.decode("#389FD6"), 0.3333f));
//            break;
        case "Deprecated":
        	return DEP;
//                cellComponent.setForeground(blend(table.getForeground(), Color.decode("#EDA200"), 0.3333f));
//                break;
        case "Retired":
        	return RET;
//                cellComponent.setForeground(blend(table.getForeground(), Color.decode("#DB5860"), 0.3333f));
//                break;
        default:
        	return DEF;
        }
	}
	

//	private TreeItem newRow(String id, Object parent) {
//		if (parent instanceof TreeItem) {  // usual
//			TreeItem item = new TreeItem((TreeItem)parent, SWT.NONE);
//			rows.put(id, item);
//			return item;
//		} else if (parent instanceof Tree) {  // root level (usually domains)
//			TreeItem item = new TreeItem((Tree)parent, SWT.NONE);
//			rows.put(id, item);
//			return item;
//		}
//		else throw new IllegalArgumentException("Passed in a non-Tree or TreeItem parent: " + parent.getClass());
//	}

	private Set<String> modifySetsCalcIntersection(Set<String> pubs, Set<String> subs) {
		Set<String> both = new HashSet<>(pubs); both.retainAll(subs);
		pubs.removeAll(both);
		subs.removeAll(both);
		return both;
	}

	private TreeItem newRow(String id) {
		TreeItem item = new TreeItem(tree, SWT.NONE);
		rows.put(id, item);
		return item;
	}
	
	private TreeItem newRow(String id, TreeItem parent) {
		TreeItem item = new TreeItem(parent, SWT.NONE);
		rows.put(id, item);
		return item;
	}

	private void addDomain(ApplicationDomain domain, TreeItem item, int indent) {
		String time = domain.getUpdatedTime();
		time = TimeUtils.formatTime(domain.getUpdatedTime(), Format.RELATIVE);
		String details = WordUtils.pluralize("Applications", domain.getStats().getApplicationCount());
		item.setText(new String[] { "Domain", domain.getName(), details, "", "", time, "View in Portal" });
		item.setImage(images.get("domain"));
//		Color c = new Color( new RGBA(154, 167, 144, 2));
//		Color c = new Color(154, 167, 144);
//		Color c2 = new Color( c.getRGB(), 100);  // can't do partial transparency
		// https://eclipse.dev/eclipse/news/4.5/M5/#transparent-bg
//		item.setBackground(c2);
		for (Application app : EventPortalWrapper.INSTANCE.getApplicationsForDomainId(domain.getId())) {
			addApp(app, newRow(app.getId(), item), indent + INDENT_SIZE);
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

	private void addApp(Application app, TreeItem item, int indent) {
		String type = "Application";
		String name = WordUtils.indent(app.getName(), indent);
		String updated = TimeUtils.formatTime(app.getUpdatedTime(), Format.RELATIVE);
		String details = String.format("%s App, %s",
				WordUtils.capitalFirst(app.getBrokerType().getValue()),
                WordUtils.capitalFirst(app.getApplicationType()));
		details += ", " + WordUtils.pluralize("Version", app.getNumberOfVersions());

		item.setText(new String[] { type, name, details, "", "", updated, "View in Portal" });
		item.setFont(1, bFont);  // bold the app name
		item.setImage(images.get("app"));
		for (ApplicationVersion appVer : EventPortalWrapper.INSTANCE.getApplicationVersionsForApplicationId(app.getId())) {
			addAppVer(appVer, newRow(appVer.getId(), item), indent + INDENT_SIZE);
		}
	}
	
	private void addAppVer(ApplicationVersion appVer, TreeItem item, int indent) {
		Application app = EventPortalWrapper.INSTANCE.getApplication(appVer.getApplicationId());
		item.setImage(images.get("vApp"));
		
		Set<String> pubs = new HashSet<>(appVer.getDeclaredProducedEventVersionIds());
		Set<String> subs = new HashSet<>(appVer.getDeclaredConsumedEventVersionIds());
		Set<String> both = modifySetsCalcIntersection(pubs, subs);
		
		String type = "App Ver";
		String name = WordUtils.indent(vName(appVer.getVersion(), app.getName()), indent);
		String details = WordUtils.pluralize("Event", pubs.size() + subs.size() + both.size());
		String state = EventPortalWrapper.INSTANCE.getState(appVer.getStateId()).getName();
		String topic = "";
		String updated = TimeUtils.formatTime(appVer.getUpdatedTime(), Format.RELATIVE);

		item.setText(new String[] { type, name, details, state, topic, updated, "View in Portal" });
//		item.setFont(1, bFont);  // bold the app name
		item.setForeground(3, getStateColor(appVer.getStateId()));
		
		for (String eventId : both) {
			addEventVersion(eventId, newRow(eventId, item), indent + INDENT_SIZE, Dir.BOTH);
		}
		for (String eventId : pubs) {
			addEventVersion(eventId, newRow(eventId, item), indent + INDENT_SIZE, Dir.PUB);
		}
		for (String eventId : subs) {
			addEventVersion(eventId, newRow(eventId, item), indent + INDENT_SIZE, Dir.SUB);
		}
	}
	
	private static String vName(String version, String name) {
		return new StringBuilder().append('v').append(version).append(' ').append(name).toString();
	}
	
	private void addEventVersion(String vEventId, TreeItem item, int indent, Dir pubSub) {
		switch (pubSub) {
		case PUB:
			item.setImage(images.get("vEventPub")); break;
		case SUB:
			item.setImage(images.get("vEventSub")); break;
		case BOTH:
			item.setImage(images.get("vEventBoth")); break;
		default:
			item.setImage(images.get("vEvent"));
		}
		// first, try to see if this exists, or is loaded?
		EventVersion vEvent = EventPortalWrapper.INSTANCE.getEventVersion(vEventId);
		if (vEvent == null) {  // haven't loaded yet
			item.setText(new String[] { "vEvent", WordUtils.indent(pubSub.arrows + " ID: " + vEventId, indent), pubSub.big + " Event", "-", "-", "", "View in Portal" });
			item.setForeground(3, getStateColor("-1"));
		} else {
			Event parent = EventPortalWrapper.INSTANCE.getEvent(vEvent.getEventId());
			String type = parent.getShared() ? "vEvent*" : "vEvent";
			String name = WordUtils.indent(vName(vEvent.getVersion(), parent.getName()), indent);
			String details = WordUtils.capitalFirst(pubSub.big + " " + parent.getBrokerType() + " event");
			String state = EventPortalWrapper.INSTANCE.getState(vEvent.getStateId()).getName();
			String updated = TimeUtils.formatTime(vEvent.getUpdatedTime(), Format.RELATIVE);
			String topic = TopicUtils.buildTopic(vEvent.getDeliveryDescriptor());
			item.setText(new String[] { type, name, details, state, topic, updated, "View in Portal" });
			item.setForeground(3, getStateColor(vEvent.getStateId()));
			item.setFont(4, fwFont);  // fixed-width for the topic
			// commented out for now, will finish later
//			if (vEvent.getSchemaVersionId() != null) {
//				if (EventPortalWrapper.INSTANCE.getSchemaVersion(vEvent.getSchemaVersionId()) != null) {
//					SchemaVersion vSchema = EventPortalWrapper.INSTANCE.getSchemaVersion(vEvent.getSchemaVersionId());
//					addSchemaVersion(vSchema, newRow(vSchema.getId(), item), indent + INDENT_SIZE);
//				}
//			} else if (vEvent.getSchemaPrimitiveType() != null) {
//				addPrimSchema(vEvent.getSchemaPrimitiveType(), newRow(), null, item, indent)
//			}
		}
//		fd.setHeight(fd.getHeight() * 2);
	}

/*	
	private void addPrimSchema(SchemaPrimitiveTypeEnum prim, SchemaObject parent, TreeItem item, int indent) {
//		SchemaObject parent = EventPortalWrapper.INSTANCE.getSchema(vSchema.getSchemaId());
		String type = parent.getShared() ? "vSchema*" : "vSchema";
		String name = WordUtils.indent(vName("0.0.0", parent.getName()), indent);
		String details = "";
//		String state = EventPortalWrapper.INSTANCE.getState(vSchema.getStateId()).getName();
		String state = "";
		details += ", " + parent.getSchemaType().toUpperCase() + " schema";
//		details += ", " + vSchema.getContent().length() + " chars";

		String updated = TimeUtils.formatTime(vSchema.getUpdatedTime(), Format.RELATIVE);

		item.setText(new String[] { type, name, details, state, "", updated, "View in Portal" });
		item.setForeground(3, getStateColor(vSchema.getStateId()));

//		fd.setHeight(fd.getHeight() * 2);
		item.setFont(3, fwFont);  // fixed-width for the topic
	}
*/

	private void addSchemaVersion(SchemaVersion vSchema, TreeItem item, int indent) {
		SchemaObject parent = EventPortalWrapper.INSTANCE.getSchema(vSchema.getSchemaId());
		String type = parent.getShared() ? "vSchema*" : "vSchema";
		String name = WordUtils.indent(vName(vSchema.getVersion(), parent.getName()), indent);
		String details = "";
		String state = EventPortalWrapper.INSTANCE.getState(vSchema.getStateId()).getName();
		details += ", " + parent.getSchemaType().toUpperCase() + " schema";
		details += ", " + vSchema.getContent().length() + " chars";

		String updated = TimeUtils.formatTime(vSchema.getUpdatedTime(), Format.RELATIVE);

		item.setText(new String[] { type, name, details, state, "", updated, "View in Portal" });
		item.setForeground(3, getStateColor(vSchema.getStateId()));

//		fd.setHeight(fd.getHeight() * 2);
		item.setFont(3, fwFont);  // fixed-width for the topic
	}
}
