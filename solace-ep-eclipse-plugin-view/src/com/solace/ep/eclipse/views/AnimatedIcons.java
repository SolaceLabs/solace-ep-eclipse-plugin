package com.solace.ep.eclipse.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;

public class AnimatedIcons {

	enum AnimType {
		LOADING,
		;
	}
	
	public static final int ANIMATION_DELAY_MS = 200;

	private boolean inited = false;
	
	private static AnimatedIcons instance = new AnimatedIcons();
	
	public static AnimatedIcons getInstance() {
		return instance;
	}
	
	static void init(/* ResourceManager mgr */) {
		System.out.println("Loading animated imageRegistry...");
		instance.loadImages(/* mgr */);
	}
	
//	private ResourceManager mgr = null;
	private Map<AnimType, List<ImageDescriptor>> imageDes = new HashMap<>();
//	private Map<AnimType, List<Image>> imageRegistry = new HashMap<>();
	
	private String path(String filename) {
		return "/icons/animated/" + filename;
	}
	
	private void loadImages(/* ResourceManager mgr */) {
		try {
			imageDes.put(AnimType.LOADING, new ArrayList<>());
			for (int i=0; i<8; i++) {
				ImageDescriptor id = Icons.buildImageDescriptor(path("loading"+i+".png"));
				imageDes.get(AnimType.LOADING).add(id);
				EventPortalView.register(AnimType.LOADING.name()+i, id);
			}

//			for (AnimType key : imageDes.keySet()) {
//				imageRegistry.put(key, new ArrayList<>());
//				int count = 1;
//				for (ImageDescriptor id : imageDes.get(key)) {
//					Image image = mgr.create(id);
//					imageRegistry.get(key).add(image);
//					EventPortalView.register(AnimType.LOADING.name()+(count++), image);
//				}
//			}
			
			inited = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static int getIndexByTime(AnimType key) {
		int numFrames = instance.imageDes.get(key).size();
		return (int)((System.currentTimeMillis() / ANIMATION_DELAY_MS) % numFrames);
	}
	
//	Image getImage(AnimType key) {
//		if (!inited) return null;
//		return imageRegistry.get(key).get(getIndexByTime(key));
//	}
	
	static ImageDescriptor getImageDescriptor(AnimType key) {
		if (!instance.inited) return null;
		return instance.imageDes.get(key).get(getIndexByTime(key));
	}
	
//    void dispose() {
//    	for (AnimType key : imageRegistry.keySet()) {
//	    	for (Image image : imageRegistry.get(key)) {
//	    		image.dispose();
//	    	}
//    	}
//    }

}
