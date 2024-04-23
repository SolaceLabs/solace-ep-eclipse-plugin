package com.solace.ep.eclipse.views;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import com.solace.ep.eclipse.Activator;
import com.solace.ep.eclipse.prefs.PreferenceConstants;
import com.solace.ep.eclipse.views.Icons.Type;

import community.solace.ep.client.model.ApplicationDomain;
import community.solace.ep.client.model.Event;
import community.solace.ep.client.model.EventApi;
import community.solace.ep.client.model.EventApiVersion;
import community.solace.ep.client.model.EventVersion;
import community.solace.ep.client.model.SchemaObject;
import community.solace.ep.client.model.SchemaVersion;
import community.solace.ep.wrapper.EventPortalWrapper;
import community.solace.ep.wrapper.SupportedObjectType;
import community.solace.ep.wrapper.TopicUtils;
import dev.solace.aaron.useful.TimeUtils;
import dev.solace.aaron.useful.TimeUtils.TimeStringFormat;
import dev.solace.aaron.useful.WordUtils;


public class EventAPIsTab extends SuperTabView {

	private static final Logger logger = LogManager.getLogger(EventAPIsTab.class);
	
	public EventAPIsTab(EventPortalView view, Composite parent, int index, RefreshListener tabbedView) {
		super(view, parent, SupportedObjectType.EVENT_API, index, tabbedView, true);
	}
	

	@Override
	public void dataLoaded() {
		logger.info(this + " dataLoaded() called");
		try {
			root = initialize();
		} catch (Exception e) {
			logger.error("Broke during initilizze",e);
			return;
		}
		Display.getDefault().asyncExec(() -> {
			logger.info(this + " GUI dataLoaded() starting");
			if (treeViewer == null) {  // first time initializing
				buildTree();
				if (control != null && !control.isDisposed()) {
					control.dispose();  // get rid of the old!
				}
			} // else reuse the previous tree treeViewer
	        treeViewer.setInput(root);  // all done!
	        treeViewer.expandToLevel(2);
			control = treeViewer.getControl();  // overwrite
			
//			control.addPaintListener(new PaintListener() {
//				@Override
//				public void paintControl(PaintEvent e) {
//					logger.warn("######### we received a paint event!!!! " + e.toString());
//				}
//			});
			
			tabbedView.refreshTab(this);  // tell daddy we're done
		});
	}
	
	@Override
	public void sortByAlpha() {
		logger.info("Starting sort by alpha");
		TreeNode newRoot = TreeNode.createRootNode();
		newRoot.addChild(new TreeNode.Builder().withId("1").withIconType(Type.APIproduct).build("1","1","1","1","1","1","1","1"));
		newRoot.addChild(new TreeNode.Builder().withId("2").withIconType(Type.vAPIproduct).build("2","2","2","2","2","2","2","2"));
		newRoot.addChild(new TreeNode.Builder().withId("3").withIconType(Type.vAPI).build("3","3","3","3","3","3","3","3"));
		root = newRoot;
		treeViewer.setInput(root);
//		tabbedView.refreshTab(this);  // tell daddy we're done

	}


	private TreeNode initialize() {
		TreeNode newRoot = TreeNode.createRootNode();
		
		EventPortalWrapper epw = EventPortalWrapper.INSTANCE;
		
		TimeStringFormat TSF;
		switch (Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.TIME_FORMAT.getId())) {
			case "iso": TSF = TimeStringFormat.TIMESTAMP; break;
			case "normal": TSF = TimeStringFormat.CASUAL; break;
			default: TSF = TimeStringFormat.RELATIVE;
		}
		for (ApplicationDomain domain : epw.getDomains()) {
			TreeNode.Builder builder = new TreeNode.Builder()
					.withEpType(SupportedObjectType.DOMAIN)
					.withId(domain.getId())
					.withEpObject(domain)
					.withIconType(Type.DOMAIN);
			String time = TimeUtils.formatTime(domain.getUpdatedTime(), TSF);
			String details = WordUtils.pluralize("Event API", domain.getStats().getEventApiCount());
			TreeNode domNode = builder.build("Domain", domain.getName(), details, null, null, time);
//			TreeNode domNode = new TreeNode(type, domain.getId(), domain, iconType, "Domain", domain.getName(), details, null, null, time);
			newRoot.addChild(domNode);
			for (EventApi api : epw.getEventApisForDomainId(domain.getId())) {
				builder = new TreeNode.Builder()
						.withEpType(SupportedObjectType.EVENT_API)
						.withId(api.getId())
						.withEpObject(api)
						.withIconType(Type.API);
//				type = SupportedObjectType.APPLICATION;
//				iconType = AnimType.APP;
				String name = api.getName();
				time = TimeUtils.formatTime(api.getUpdatedTime(), TSF);
				String brokerType = WordUtils.capitalFirst(api.getBrokerType().getValue());
				details = String.format("%s Event API",
						brokerType);
				details += ", " + WordUtils.pluralize("Version", api.getNumberOfVersions());
				TreeNode appNode = builder.build("EventAPI", name, details, null, null, time);
//				TreeNode appNode =  new TreeNode(type, app.getId(), app, iconType, "App", app.getName(), details, null, null, time);
				domNode.addChild(appNode);
				for (EventApiVersion apiVer : epw.getEventApiVersionsForEventApiId(api.getId())) {
					builder = new TreeNode.Builder()
							.withEpType(SupportedObjectType.EVENT_API_VERSION)
							.withId(apiVer.getId())
							.withEpObject(apiVer)
							.withIconType(Type.vAPI);

//					Application app = EventPortalWrapper.INSTANCE.getApplication(appVer.getApplicationId());
//					item.setImage(imageRegistry.get("vApp"));
					
					Set<String> pubs = new HashSet<>(apiVer.getProducedEventVersionIds());
					Set<String> subs = new HashSet<>(apiVer.getConsumedEventVersionIds());
					Set<String> both = modifySetsCalcIntersection(pubs, subs);
//					if ((pubs.size() > 0 && subs.size() > 0) || both.size() > 0) builder.withIconType(Type.vAPPboth);
//					else if (pubs.size() > 0) builder.withIconType(Type.vAPPpub);
//					else if (subs.size() > 0) builder.withIconType(Type.vAPPsub);
//					else builder.withIconType(Type.vAPP);
					
					name = vName(apiVer.getVersion(), api.getName());
					details = WordUtils.pluralize(brokerType + " Event", pubs.size() + subs.size() + both.size()) + " referenced";
					String state = EventPortalWrapper.INSTANCE.getState(apiVer.getStateId()).getName();
					String topic = "";
					String updated = TimeUtils.formatTime(apiVer.getUpdatedTime(), TSF);

					TreeNode apiVerNode = builder.build("vEventAPI", name, details, state, topic, updated, "View AsyncAPI");
//					TreeNode appVerNode =  new TreeNode(type, appVer.getId(), appVer, iconType, "vApp", name, details, state, topic, updated, "View AsyncAPI");
					appNode.addChild(apiVerNode);
				
					for (String eventId : both) {
						helperAddEventVersion(eventId, apiVerNode, Dir.BOTH, brokerType, TSF);
					}
					for (String eventId : pubs) {
						helperAddEventVersion(eventId, apiVerNode, Dir.PUB, brokerType, TSF);
					}
					for (String eventId : subs) {
						helperAddEventVersion(eventId, apiVerNode, Dir.SUB, brokerType, TSF);
					}
				}
			}
		}
		return newRoot;
	}
	
	public enum Dir {
		PUB("Pub","Published","Pub"),
		SUB("Sub","Subscribed", "Sub"),
		BOTH("Pub/Sub", "Pub/Sub'ed", "Both"),
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
	
	static void helperAddEventVersion(String vEventId, TreeNode nodeParent, Dir pubSub, String brokerType, TimeStringFormat TSF) {
		TreeNode.Builder builder = new TreeNode.Builder()
				.withEpType(SupportedObjectType.EVENT_VERSION)
				.withId(vEventId);
		switch (pubSub) {
			case PUB: builder.withIconType(Type.vEVENTpub); break;
			case SUB: builder.withIconType(Type.vEVENTsub); break;
			case BOTH: builder.withIconType(Type.vEVENTboth); break;
			default: builder.withIconType(Type.vEVENT);
		}
		EventApiVersion apiVer = (EventApiVersion)nodeParent.epObject;
		EventApi api = (EventApi)nodeParent.getParent().epObject;
		EventVersion eventVer = EventPortalWrapper.INSTANCE.getEventVersion(vEventId);

		if (eventVer == null) {  // haven't loaded yet
			TreeNode eventVerNode = builder.build("vEvent", pubSub.arrows + ", ID: " + vEventId, pubSub.big + " " + brokerType + " Event", null, "topic/goes/here", null);
//			TreeNode eventVerNode =  new TreeNode(SupportedObjectType.EVENT_VERSION, vEventId, eventVer, iconType, "vEvent", pubSub.arrows + " ID: " + vEventId, pubSub.big + " " + brokerType + " Event", null, null, null, "View in Portal");
			nodeParent.addChild(eventVerNode);
		} else {
			builder.withEpObject(eventVer);
			Event event = EventPortalWrapper.INSTANCE.getEvent(eventVer.getEventId());
			String origDomainId = event.getApplicationDomainId();
			if (!origDomainId.equals(api.getApplicationDomainId())) builder.withOrigDomainId(origDomainId);
			String name = String.format("%sv%s %s%s",
					origDomainId.equals(api.getApplicationDomainId()) ? "" : "(EXT) ",
					eventVer.getVersion(),
					event.getName(),
					event.getShared() ? "*" : "");
			String details = pubSub.big + " " + (event.getShared() ? "Shared " : "") + brokerType + " Event";
			if (eventVer.getSchemaPrimitiveType() == null && eventVer.getSchemaVersionId() == null) {
				details += ", NO schema";
			}
			String state = EventPortalWrapper.INSTANCE.getState(apiVer.getStateId()).getName();
			String topic = TopicUtils.buildTopic(eventVer.getDeliveryDescriptor());
			String updated = TimeUtils.formatTime(apiVer.getUpdatedTime(), TSF);
			TreeNode eventVerNode = builder.build("vEvent", name, details, state, topic, updated);
//			TreeNode eventVerNode =  new TreeNode(SupportedObjectType.EVENT_VERSION, vEventId, eventVer, iconType, "vEvent", pubSub.arrows + " ID: " + vEventId, pubSub.big + " " + brokerType + " Event", null, null, null, "View in Portal");
			nodeParent.addChild(eventVerNode);

			if (eventVer.getSchemaVersionId() != null) {  // complex primitive
				SchemaVersion schemaVer = EventPortalWrapper.INSTANCE.getSchemaVersion(eventVer.getSchemaVersionId());
				SchemaObject schema = EventPortalWrapper.INSTANCE.getSchema(schemaVer.getSchemaId());
				builder = new TreeNode.Builder()
						.withEpType(SupportedObjectType.SCHEMA_VERSION)
						.withIconType(Icons.Type.vSCHEMA)
						.withEpObject(schemaVer)
						.withId(schemaVer.getId());
				origDomainId = schema.getApplicationDomainId();
				if (!origDomainId.equals(api.getApplicationDomainId())) builder.withOrigDomainId(origDomainId);
				name = String.format("v%s %s%s", schemaVer.getVersion(), schema.getName(), schema.getShared() ? "*" : "");
				details = (schema.getShared() ? "Shared " : "") + WordUtils.capitalFirst(schema.getSchemaType()) + " Schema";
				state = EventPortalWrapper.INSTANCE.getState(schemaVer.getStateId()).getName();;
				updated = TimeUtils.formatTime(schemaVer.getUpdatedTime(), TimeStringFormat.RELATIVE);
				TreeNode schemaNode = builder.build("vSchema", name, details, state, null, updated);
				eventVerNode.addChild(schemaNode);
				
/*				proSchemaVer.addDetail(String.format("%s Payload", WordyUtils.capitalFirst(schema.getContentType())));
				proSchemaVer.setLink(String.format(TopicUtils.SCHEMA_VER_URL, AppSettingsState.getInstance().baseUrl, domain.getId(), schema.getId(), schemaVersion.getId()));
				proSchemaVer.setIcon(MyIcons.SchemaSmall);
				if (schema.isShared()) proSchemaVer.addDetail("Shared");
				proSchemaVer.setState(EventPortalWrapper.INSTANCE.getState(schemaVersion.getStateId()).getName());
				proSchemaVer.setLastUpdatedTs(TimeUtils.parseTime(schemaVersion.getUpdatedTime()));
				proSchemaVer.setLastUpdatedByUser(schemaVersion.getChangedBy());
				proSchemaVer.setCreatedByUser(schemaVersion.getCreatedBy());
*/
			} else if (eventVer.getSchemaPrimitiveType() != null) {  // primitive
				builder = new TreeNode.Builder()
						.withEpType(SupportedObjectType.EVENT_VERSION)
						.withIconType(Icons.Type.vSCHEMAprim)
						.withEpObject(eventVer)
						.withId(eventVer.getId() + "_schema");
//				name = String.format("v%s %s%s", schemaVer.getVersion(), schema.getName(), schema.getShared() ? "*" : "");
				name = "";
				details = eventVer.getSchemaPrimitiveType() + " Primitive";
				state = null;
				updated = null;
				TreeNode schemaNode = builder.build("Schema", name, details, state, null, updated);
				eventVerNode.addChild(schemaNode);
			} else {  // none
				builder = new TreeNode.Builder()
						.withEpType(SupportedObjectType.EVENT_VERSION)
						.withIconType(Icons.Type.vSCHEMAnone)
						.withEpObject(eventVer)
						.withId(eventVer.getId() + "_schema");
//				name = String.format("v%s %s%s", schemaVer.getVersion(), schema.getName(), schema.getShared() ? "*" : "");
				name = "";
				details = null;
				state = null;
				updated = null;
				TreeNode schemaNode = builder.build("No schema", name, details, state, null, updated);
				eventVerNode.addChild(schemaNode);
				
			}
/*
			// current / old
			if (EventPortalWrapper.INSTANCE.getSchemaVersion(eventVer.getSchemaVersionId()) != null) {
				SchemaVersion schemaVersion = EventPortalWrapper.INSTANCE.getSchemaVersion(eventVer.getSchemaVersionId());
				SchemaObject schema = EventPortalWrapper.INSTANCE.getSchema(schemaVersion.getSchemaId());
//				eventVerPro.addNote(String.format("%s Payload", TopicUtils.capitalFirst(schema.getContentType())));
				eventVerPro.addChild(proSchemaVer);
			} else if (eventVer.getSchemaPrimitiveType() != null) {
//				eventVerPro.addNote(String.format("%s Payload", TopicUtils.capitalFirst(eventVer.getSchemaPrimitiveType().getValue())));
				PortalRowObjectTreeNode proSchemaVer = new PortalRowObjectTreeNode(EventPortalObjectType.SCHEMA_VERSION, null, "");
				proSchemaVer.setName(String.format("Primitive %s Payload", WordyUtils.capitalFirst(eventVer.getSchemaPrimitiveType().getValue())));
				proSchemaVer.setIcon(MyIcons.SchemaPrimitive);
				
				eventVerPro.addChild(proSchemaVer);
			} else {
//				eventVerPro.addNote("No Schema");
				PortalRowObjectTreeNode proSchemaVer = new PortalRowObjectTreeNode(EventPortalObjectType.SCHEMA_VERSION, null, "");
				proSchemaVer.setName("No Schema");
				proSchemaVer.setIcon(MyIcons.SchemaNone);
				eventVerPro.addChild(proSchemaVer);
			}
			*/
			
			
		}
	}

	

	
}
