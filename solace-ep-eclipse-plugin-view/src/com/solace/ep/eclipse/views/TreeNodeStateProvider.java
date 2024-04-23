package com.solace.ep.eclipse.views;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

class TreeNodeStateProvider extends ColumnLabelProvider implements IColorProvider {
	
	// LOL terrible way!
//	private static final Color DEF = Display.getCurrent().getSystemColor(23);
//	private static final Color REL = new Color(java.awt.Color.decode("#59A869").getRed(),java.awt.Color.decode("#59A869").getGreen(),java.awt.Color.decode("#59A869").getBlue());
//	private static final Color DRAFT = new Color(java.awt.Color.decode("#389FD6").getRed(),java.awt.Color.decode("#389FD6").getGreen(),java.awt.Color.decode("#389FD6").getBlue());
//	private static final Color DEP = new Color(java.awt.Color.decode("#EDA200").getRed(),java.awt.Color.decode("#EDA200").getGreen(),java.awt.Color.decode("#EDA200").getBlue());
//	private static final Color RET = new Color(java.awt.Color.decode("#DB5860").getRed(),java.awt.Color.decode("#DB5860").getGreen(),java.awt.Color.decode("#DB5860").getBlue());

//	private static final Color DEF = Display.getCurrent().getSystemColor(23);
	private static final Color REL = ColorUtils.decode("#59A869");
	private static final Color DRAFT = ColorUtils.decode("#389FD6");
	private static final Color DEP = ColorUtils.decode("#EDA200");
	private static final Color RET = ColorUtils.decode("#DB5860");

	public TreeNodeStateProvider() {
		
//		ColorDescriptor.createFrom(null)
//		RGB rgb = new RGB
		
	}

	@Override
	public String getText(Object obj) {
		return ((TreeNode)obj).content[3];
	}
	
	@Override
	public Image getImage(Object obj) {
		return null;
	}

	@Override
	public Color getForeground(Object element) {
//		if (0 == 0) return null;
//		if (1 == 1) return ColorUtils.getColor("234");
		String state = getText(element);
		if (state == null) return null;
		switch (getText(element)) {
	        case "Released":	return ColorUtils.blend(ColorUtils.getDefaultColor(), REL, 0.5f);
	        case "Draft":		return ColorUtils.blend(ColorUtils.getDefaultColor(), DRAFT, 0.5f);
	        case "Deprecated":	return ColorUtils.blend(ColorUtils.getDefaultColor(), DEP, 0.5f);
	        case "Retired":		return ColorUtils.blend(ColorUtils.getDefaultColor(), RET, 0.5f);
	        default: 			return ColorUtils.getDefaultColor();
		}
	}

	@Override
	public Color getBackground(Object element) {
		return null;
	}
}

