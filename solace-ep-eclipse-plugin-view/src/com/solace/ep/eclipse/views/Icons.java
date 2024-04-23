package com.solace.ep.eclipse.views;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.solace.ep.eclipse.Activator;
import com.solace.ep.eclipse.views.ColorUtils.ColorMode;

public class Icons {
	
	private static final Logger logger = LogManager.getLogger(Icons.class);

	enum Type {
		DOMAIN,
		APP,
		vAPP,
		vAPPnone,
		vAPPboth,
		vAPPpub,
		vAPPsub,
		EVENT,
		vEVENT,
		vEVENTboth,
		vEVENTpub,
		vEVENTsub,
		SCHEMA,
		vSCHEMA,
		vSCHEMAnone,
		vSCHEMAprim,
		API,
		vAPI,
		APIproduct,
		vAPIproduct,
		
		LOAD,
//		LOADING,
		REFRESH,
		GEAR,
		EXPAND,
		COLLAPSE,
		SORTtype,
		SORTname,
		FILTER,
		VISIBLE,
		
		PORTAL,
		LOGO,
		WATCH,
		BROWSER,
		MULE,
		SPRING,
		ASYNC,
		;
	}

	private boolean inited = false;
	private static Icons instance = new Icons();
	
//	public static Icons getInstance() {
//		return instance;
//	}


	
	static void init(/* ResourceManager mgr */) {
		System.out.println("Loading imageRegistry...");
		
//		this.mgr = mgr;
		instance.loadImages();
	}
	
//	private ResourceManager mgr = null;
	private Map<Type, ImageDescriptor> idsLight = new HashMap<>();
	private Map<Type, ImageDescriptor> idsDark = new HashMap<>();
//	private Map<Type, Image> imageRegistry = new HashMap<>();
	
	private String path(String filename) {
		return "/icons/" + filename;
	}

	
	private void loadImages() {
		try {
			add(Type.DOMAIN, "ep/domain-large");
			add(Type.APP, "ep/hex-large");
			add(Type.vAPP, "ep/hex-small");
			add(Type.vAPPnone, "ep/apps-noevent");
			add(Type.vAPPpub, "ep/hex-small-pub");
			add(Type.vAPPsub, "ep/hex-small-sub");
			add(Type.vAPPboth, "ep/hex-small-both");
			add(Type.EVENT, "ep/event-large");
			add(Type.vEVENT, "ep/event-small");
			add(Type.vEVENTpub, "ep/event-small-pub");
			add(Type.vEVENTsub, "ep/event-small-sub");
			add(Type.vEVENTboth, "ep/event-small-both");
			add(Type.SCHEMA, "ep/triangle-large");
			add(Type.vSCHEMA, "ep/triangle-small");
			add(Type.vSCHEMAnone, "ep/schema-none");
			add(Type.vSCHEMAprim, "ep/schema-primitive");
			add(Type.API, "ep/square-large");
			add(Type.vAPI, "ep/square-small");
			add(Type.APIproduct, "ep/diamond-large");
			add(Type.vAPIproduct, "ep/diamond-small");
	
			add(Type.EXPAND, "expandall");
			add(Type.COLLAPSE, "collapseall");
			add(Type.SORTname, "sorted");
			add(Type.SORTtype, "sortByType");
			add(Type.LOAD, "execute");
			add(Type.REFRESH, "refresh");
			add(Type.GEAR, "gearPlain");
			add(Type.FILTER, "filter");
			add(Type.VISIBLE, "toggleVisibility");
			
//			add(Type.PORTAL, "portal6");
			add(Type.PORTAL, "portalToolbar");
			add(Type.WATCH, "watch");
			add(Type.LOGO, "ep/EP_Logo_Green");
			add(Type.MULE, "MuleFlow-16x16");
			add(Type.SPRING, "spring");
			add(Type.BROWSER, "browser");
			add(Type.ASYNC, "asyncapi3");

			inited = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void add(Type key, String filename) {
		String lightPath = path(filename) + ".png";
		ImageDescriptor id = buildImageDescriptor(lightPath);
		if (id == null) {  // couldn't find any file
			logger.error("Couldn't load " + filename);
			return;
		}
		idsLight.put(key, id);
		EventPortalView.register(key.name() + "_LIGHT", id);
		String darkPath = path(filename) + "_dark.png";
		ImageDescriptor iddark = buildImageDescriptor(darkPath);
		if (iddark == null) {  // no dark version
//			logger.debug("Can't find dark version for : " + key.name() + ", " + darkPath);
			iddark = id;
		}
		idsDark.put(key, iddark);
		EventPortalView.register(key.name() + "_DARK", iddark);
	}

	static Image getImage(Type key) {
		if (!instance.inited) return null;
		String imageKey = key.name() + "_" + ColorUtils.getMode();
		return EventPortalView.getImage(imageKey);
	}

	static ImageDescriptor getImageDescriptor(Type key) {
		if (!instance.inited) return null;
		if (ColorUtils.getMode() == ColorMode.DARK) {
			return instance.idsDark.get(key);
		}
		return instance.idsLight.get(key);
	}
	
    static ImageDescriptor buildImageDescriptor(String path) {
    	return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, path);
    }
    
}
