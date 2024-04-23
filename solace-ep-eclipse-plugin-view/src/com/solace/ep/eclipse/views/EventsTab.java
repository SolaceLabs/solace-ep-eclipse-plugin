package com.solace.ep.eclipse.views;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.solace.ep.eclipse.Activator;
import com.solace.ep.eclipse.prefs.PreferenceConstants;
import com.solace.ep.eclipse.views.Icons.Type;

import community.solace.ep.client.model.Application;
import community.solace.ep.client.model.ApplicationDomain;
import community.solace.ep.client.model.ApplicationVersion;
import community.solace.ep.client.model.Event;
import community.solace.ep.client.model.EventApi;
import community.solace.ep.client.model.EventApiVersion;
import community.solace.ep.client.model.EventVersion;
import community.solace.ep.client.model.SchemaObject;
import community.solace.ep.client.model.SchemaVersion;
import community.solace.ep.wrapper.EventPortalWrapper;
import community.solace.ep.wrapper.SupportedObjectType;
import dev.solace.aaron.useful.TimeUtils;
import dev.solace.aaron.useful.TimeUtils.TimeStringFormat;
import dev.solace.aaron.useful.WordUtils;


public class EventsTab extends SuperTabView {

	private static final Logger logger = LogManager.getLogger(EventsTab.class);
	
	public EventsTab(EventPortalView view, Composite parent, int index, RefreshListener tabbedView) {
		super(view, parent, SupportedObjectType.EVENT, index, tabbedView, false);
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
			String details = WordUtils.pluralize("Event", domain.getStats().getEventCount());
			TreeNode domNode = builder.build("Domain", domain.getName(), details, null, null, time);
//			TreeNode domNode = new TreeNode(type, domain.getId(), domain, iconType, "Domain", domain.getName(), details, null, null, time);
			newRoot.addChild(domNode);
			for (Event event : epw.getEventsForDomainId(domain.getId())) {
				builder = new TreeNode.Builder()
						.withEpType(SupportedObjectType.EVENT)
						.withId(event.getId())
						.withEpObject(event)
						.withIconType(Type.EVENT);
//				type = SupportedObjectType.APPLICATION;
//				iconType = AnimType.APP;
				String name = event.getName();
				time = TimeUtils.formatTime(event.getUpdatedTime(), TSF);
				String brokerType = WordUtils.capitalFirst(event.getBrokerType());
				details = String.format("%s Event",
						brokerType);
				details += ", " + WordUtils.pluralize("Version", event.getNumberOfVersions());
				TreeNode appNode = builder.build("Event", name, details, null, null, time);
//				TreeNode appNode =  new TreeNode(type, app.getId(), app, iconType, "App", app.getName(), details, null, null, time);
				domNode.addChild(appNode);
				for (EventVersion eventVer : epw.getEventVersionsForEventId(event.getId())) {
					builder = new TreeNode.Builder()
							.withEpType(SupportedObjectType.EVENT_VERSION)
							.withId(eventVer.getId())
							.withEpObject(eventVer)
							.withIconType(Type.vEVENT);

//					Application app = EventPortalWrapper.INSTANCE.getApplication(appVer.getApplicationId());
//					item.setImage(imageRegistry.get("vApp"));
					
					
					
					
					Set<String> pubApps = new HashSet<>(eventVer.getDeclaredProducingApplicationVersionIds());
					Set<String> subApps = new HashSet<>(eventVer.getDeclaredConsumingApplicationVersionIds());
					Set<String> bothApps = modifySetsCalcIntersection(pubApps, subApps);
					Set<String> pubApis = new HashSet<>(eventVer.getProducingEventApiVersionIds());
					Set<String> subApis = new HashSet<>(eventVer.getConsumingEventApiVersionIds());
					Set<String> bothApis = modifySetsCalcIntersection(pubApis, subApis);
//					if ((pubs.size() > 0 && subs.size() > 0) || both.size() > 0) builder.withIconType(Type.vAPPboth);
//					else if (pubs.size() > 0) builder.withIconType(Type.vAPPpub);
//					else if (subs.size() > 0) builder.withIconType(Type.vAPPsub);
//					else builder.withIconType(Type.vAPP);
					
					name = vName(eventVer.getVersion(), event.getName());
//					details = WordUtils.pluralize(brokerType + " Event", pubApps.size() + subApps.size() + bothApps.size()) + " referenced";
					details = WordUtils.pluralize(brokerType + " App", pubApps.size() + subApps.size() + bothApps.size()) + ", " + WordUtils.pluralize("Event API", pubApis.size() + subApis.size() + bothApis.size())   + " referenced";
					String state = EventPortalWrapper.INSTANCE.getState(eventVer.getStateId()).getName();
					String topic = "";
					String updated = TimeUtils.formatTime(eventVer.getUpdatedTime(), TSF);

					TreeNode eventVerNode = builder.build("vEvent", name, details, state, topic, updated, "View AsyncAPI");
//					TreeNode appVerNode =  new TreeNode(type, appVer.getId(), appVer, iconType, "vApp", name, details, state, topic, updated, "View AsyncAPI");
					appNode.addChild(eventVerNode);
					
					
					// schema time!
					if (eventVer.getSchemaVersionId() != null) {  // complex primitive
						SchemaVersion schemaVer = EventPortalWrapper.INSTANCE.getSchemaVersion(eventVer.getSchemaVersionId());
						SchemaObject schema = EventPortalWrapper.INSTANCE.getSchema(schemaVer.getSchemaId());
						builder = new TreeNode.Builder()
								.withEpType(SupportedObjectType.SCHEMA_VERSION)
								.withIconType(Icons.Type.vSCHEMA)
								.withEpObject(schemaVer)
								.withId(schemaVer.getId());
						String origDomainId = schema.getApplicationDomainId();
						if (!origDomainId.equals(event.getApplicationDomainId())) builder.withOrigDomainId(origDomainId);
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
//						name = String.format("v%s %s%s", schemaVer.getVersion(), schema.getName(), schema.getShared() ? "*" : "");
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
//						name = String.format("v%s %s%s", schemaVer.getVersion(), schema.getName(), schema.getShared() ? "*" : "");
						name = "";
						details = null;
						state = null;
						updated = null;
						TreeNode schemaNode = builder.build("No schema", name, details, state, null, updated);
						eventVerNode.addChild(schemaNode);
						
					}
					
					
				
					for (String appId : bothApps) {
						helperAddAppVersion(appId, eventVerNode, Dir.BOTH, brokerType, TSF);
					}
					for (String appId : pubApps) {
						helperAddAppVersion(appId, eventVerNode, Dir.PUB, brokerType, TSF);
					}
					for (String appId : subApps) {
						helperAddAppVersion(appId, eventVerNode, Dir.SUB, brokerType, TSF);
					}

					for (String apiId : bothApis) {
						helperAddEventApiVersion(apiId, eventVer, eventVerNode, Dir.BOTH, brokerType, TSF);
					}
					for (String apiId : pubApis) {
						helperAddEventApiVersion(apiId, eventVer, eventVerNode, Dir.PUB, brokerType, TSF);
					}
					for (String apiId : subApis) {
						helperAddEventApiVersion(apiId, eventVer, eventVerNode, Dir.SUB, brokerType, TSF);
					}
}
			}
		}
		return newRoot;
	}
	
	public enum Dir {
		PUB("Pub","Publishing","Pub"),
		SUB("Sub","Subscribing", "Sub"),
		BOTH("Pub/Sub", "Pub/Sub'ing", "Both"),
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

	static void helperAddAppVersion(String vAppId, TreeNode nodeParent, Dir pubSub, String brokerType, TimeStringFormat TSF) {
		TreeNode.Builder builder = new TreeNode.Builder()
				.withEpType(SupportedObjectType.APPLICATION_VERSION)
				.withId(vAppId);
		switch (pubSub) {
			case PUB: builder.withIconType(Type.vAPPpub); break;
			case SUB: builder.withIconType(Type.vAPPsub); break;
			case BOTH: builder.withIconType(Type.vAPPboth); break;
			default: builder.withIconType(Type.vAPP);
		}
		EventVersion eventVer = (EventVersion)nodeParent.epObject;
		Event event = (Event)nodeParent.getParent().epObject;
		ApplicationVersion appVer = EventPortalWrapper.INSTANCE.getApplicationVersion(vAppId);

		if (appVer == null) {  // haven't loaded yet
			TreeNode eventVerNode = builder.build("vApp", pubSub.arrows + ", ID: " + vAppId, pubSub.big + " " + brokerType + " Event", null, "topic/goes/here", null);
//			TreeNode eventVerNode =  new TreeNode(SupportedObjectType.EVENT_VERSION, vEventId, eventVer, iconType, "vEvent", pubSub.arrows + " ID: " + vEventId, pubSub.big + " " + brokerType + " Event", null, null, null, "View in Portal");
			nodeParent.addChild(eventVerNode);
		} else {
			builder.withEpObject(appVer);
			Application app = EventPortalWrapper.INSTANCE.getApplication(appVer.getApplicationId());
			String origDomainId = app.getApplicationDomainId();
			if (!origDomainId.equals(event.getApplicationDomainId())) builder.withOrigDomainId(origDomainId);
			String name = String.format("%sv%s %s",
					origDomainId.equals(event.getApplicationDomainId()) ? "" : "(EXT) ",
					appVer.getVersion(),
					app.getName());
//					event.getShared() ? "*" : "");
			String details = pubSub.big + " " /* + (event.getShared() ? "Shared " : "") */ + brokerType + " App";
			String state = EventPortalWrapper.INSTANCE.getState(eventVer.getStateId()).getName();
//			String topic = TopicUtils.buildTopic(eventVer.getDeliveryDescriptor());
			String updated = TimeUtils.formatTime(eventVer.getUpdatedTime(), TSF);
			TreeNode appVerNode = builder.build("vApp", name, details, state, null, updated);
//			TreeNode eventVerNode =  new TreeNode(SupportedObjectType.EVENT_VERSION, vEventId, eventVer, iconType, "vEvent", pubSub.arrows + " ID: " + vEventId, pubSub.big + " " + brokerType + " Event", null, null, null, "View in Portal");
			nodeParent.addChild(appVerNode);

			
		}
	}


	static void helperAddEventApiVersion(String vEventApiId, EventVersion eventVer, TreeNode nodeParent, Dir pubSub, String brokerType, TimeStringFormat TSF) {
		TreeNode.Builder builder = new TreeNode.Builder()
				.withEpType(SupportedObjectType.EVENT_API_VERSION)
				.withId(vEventApiId);
		switch (pubSub) {
			case PUB: builder.withIconType(Type.vAPI); break;
			case SUB: builder.withIconType(Type.vAPI); break;
			case BOTH: builder.withIconType(Type.vAPI); break;
			default: builder.withIconType(Type.vAPI);
		}
//		EventApiVersion apiVer = (EventApiVersion)nodeParent.epObject;
		EventApiVersion apiVer = EventPortalWrapper.INSTANCE.getEventApiVersion(vEventApiId);
//		EventApi api = (EventApi)nodeParent.getParent().epObject;
		EventApi api = EventPortalWrapper.INSTANCE.getEventApi(apiVer.getEventApiId());
//		EventVersion eventVer = EventPortalWrapper.INSTANCE.getEventVersion(vEventApiId);
		Event event = EventPortalWrapper.INSTANCE.getEvent(eventVer.getEventId());
		builder.withEpObject(apiVer);
		String origDomainId = event.getApplicationDomainId();
		if (!origDomainId.equals(api.getApplicationDomainId())) builder.withOrigDomainId(origDomainId);
		
		String name = vName(apiVer.getVersion(), api.getName());
//		String details = WordUtils.pluralize(brokerType + " Event", pubs.size() + subs.size() + both.size()) + " referenced";
		String state = EventPortalWrapper.INSTANCE.getState(apiVer.getStateId()).getName();
		String topic = "";
		String updated = TimeUtils.formatTime(apiVer.getUpdatedTime(), TSF);

		
//		String name = String.format("%sv%s %s%s",
//				origDomainId.equals(api.getApplicationDomainId()) ? "" : "(EXT) ",
//				eventVer.getVersion(),
//				event.getName(),
//				event.getShared() ? "*" : "");
		String details = pubSub.big + " " + (event.getShared() ? "Shared " : "") + brokerType + " Event API";
		if (eventVer.getSchemaPrimitiveType() == null && eventVer.getSchemaVersionId() == null) {
			details += ", NO schema";
		}
//		String state = EventPortalWrapper.INSTANCE.getState(apiVer.getStateId()).getName();
//		String topic = TopicUtils.buildTopic(eventVer.getDeliveryDescriptor());
//		String updated = TimeUtils.formatTime(apiVer.getUpdatedTime(), TSF);
		TreeNode eventVerNode = builder.build("vEventAPI", name, details, state, topic, updated);
//			TreeNode eventVerNode =  new TreeNode(SupportedObjectType.EVENT_VERSION, vEventId, eventVer, iconType, "vEvent", pubSub.arrows + " ID: " + vEventId, pubSub.big + " " + brokerType + " Event", null, null, null, "View in Portal");
		nodeParent.addChild(eventVerNode);
	}

	

	
}
