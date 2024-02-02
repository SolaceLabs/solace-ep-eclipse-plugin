package com.solace.ep.eclipse.views;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class ResourceUsingLabelProvider extends LabelProvider {

    private ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());

    @Override
    public Image getImage(Object element) {
//        if (element instanceof Task) {
//            Bundle bundle = FrameworkUtil.getBundle(this.getClass());
//            // use the org.eclipse.core.runtime.Path as import
//            WEB_URL url = FileLocator.find(bundle, new Path("icons/task.png"), null);
//            ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(url);
//
//            // return the image being created by the resourceManager
//            return resourceManager.createImage(imageDescriptor);
//        }

        return super.getImage(element);
    }

    @Override
    public void dispose() {
        super.dispose();

        // dispose the ResourceManager yourself
        resourceManager.dispose();
    }
}