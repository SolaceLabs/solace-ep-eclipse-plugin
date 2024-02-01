package com.solace.ep.eclipse.views;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.graphics.Image;

import com.solace.ep.eclipse.Activator;

public class Icons {

	enum Type {
		DOMAIN,
		APP,
		vAPP,
		EVENT,
		vEVENT,
		vEVENTboth,
		vEVENTpub,
		vEVENTsub,
		SCHEMA,
		vSCHEMA,
		API,
		vAPI,
		APIproduct,
		vAPIproduct,
		
		EXECUTE,
		GEAR,
		EXPAND,
		COLLAPSE,
		SORTtype,
		SORTname,
		FILTER,
		VISIBLE,
		
		PORTAL,
		BROWSER,
		MULE,
		SPRING,
		;
	}

	private boolean inited = false;
	
	private static Icons instance = new Icons();
	
	public static Icons getInstance() {
		return instance;
	}
	
	private Icons() {
		// hide the constructor
	}
	
	void init(ResourceManager mgr) {
		System.out.println("Loading images...");
//		this.mgr = mgr;
		loadImages(mgr);
	}
	
//	private ResourceManager mgr = null;
	private Map<Type, ImageDescriptor> imageDes = new HashMap<>();
	private Map<Type, Image> images = new HashMap<>();
	
	
	private void loadImages(ResourceManager mgr) {
		try {
			imageDes.put(Type.DOMAIN, createImageDescriptor("/icons/ep/domain-large.png"));
			imageDes.put(Type.APP, createImageDescriptor("/icons/ep/hex-large.png"));
			imageDes.put(Type.vAPP, createImageDescriptor("/icons/ep/hex-small-pub.png"));
			imageDes.put(Type.EVENT, createImageDescriptor("/icons/ep/event-large.png"));
			imageDes.put(Type.vEVENT, createImageDescriptor("/icons/ep/event-small.png"));
			imageDes.put(Type.vEVENTpub, createImageDescriptor("/icons/ep/event-small-pub.png"));
			imageDes.put(Type.vEVENTsub, createImageDescriptor("/icons/ep/event-small-sub.png"));
			imageDes.put(Type.vEVENTboth, createImageDescriptor("/icons/ep/event-small-both.png"));
			imageDes.put(Type.SCHEMA, createImageDescriptor("/icons/ep/triangle-large.png"));
			imageDes.put(Type.vSCHEMA, createImageDescriptor("/icons/ep/triangle-small.png"));
			imageDes.put(Type.API, createImageDescriptor("/icons/ep/square-large.png"));
	//		imageDes.put(Type.vAPI, createImageDescriptor("/icons/ep/suare-small.png"));
			imageDes.put(Type.APIproduct, createImageDescriptor("/icons/ep/diamond-large.png"));
	//		imageDes.put(Type.vAPIproduct, createImageDescriptor("/icons/ep/di-small.png"));
	
			imageDes.put(Type.EXPAND, createImageDescriptor("/icons/expandall.png"));
			imageDes.put(Type.COLLAPSE, createImageDescriptor("/icons/collapseall.png"));
			imageDes.put(Type.SORTname, createImageDescriptor("/icons/sorted.png"));
			imageDes.put(Type.SORTtype, createImageDescriptor("/icons/sortByType.png"));
			
			imageDes.put(Type.EXECUTE, createImageDescriptor("/icons/execute.png"));
			imageDes.put(Type.GEAR, createImageDescriptor("/icons/gearPlain.png"));
			imageDes.put(Type.FILTER, createImageDescriptor("/icons/filter.png"));
			imageDes.put(Type.VISIBLE, createImageDescriptor("/icons/toggleVisibility.png"));
			
			
			imageDes.put(Type.PORTAL, createImageDescriptor("/icons/portal6.png"));
			imageDes.put(Type.MULE, createImageDescriptor("/icons/MuleFlow-16x16.png"));
			imageDes.put(Type.SPRING, createImageDescriptor("/icons/spring.png"));
			imageDes.put(Type.BROWSER, createImageDescriptor("/icons/browser.png"));

			for (Type key : imageDes.keySet()) {
				images.put(key, mgr.create(imageDes.get(key)));
			}
			inited = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	Image getImage(Type key) {
		if (!inited) return null;
		return images.get(key);
	}
	
	ImageDescriptor getImageDescripor(Type key) {
		if (!inited) return null;
		return imageDes.get(key);
	}
	
//    public static ImageDescriptor createImageDescriptor(String filename) {
//        Bundle bundle = FrameworkUtil.getBundle(Icons.class);
//        URL url = FileLocator.find(bundle, new Path("/resources/icons/"+filename), null);
//        return ImageDescriptor.createFromURL(url);
//    }
	
    private static ImageDescriptor createImageDescriptor(String path) {
    	return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, path);
//        Bundle bundle = FrameworkUtil.getBundle(this.getClass());
//        URL url = FileLocator.find(bundle, new Path(path), null);
//        return ImageDescriptor.createFromURL(url);
    }
    
}
