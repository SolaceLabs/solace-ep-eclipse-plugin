package com.solace.ep.eclipse.views;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

class TreeNodeStateProvider extends ColumnLabelProvider implements IColorProvider {
	
	// LOL terrible way!
	private static final Color DEF = Display.getCurrent().getSystemColor(23);
	private static final Color REL = new Color(java.awt.Color.decode("#59A869").getRed(),java.awt.Color.decode("#59A869").getGreen(),java.awt.Color.decode("#59A869").getBlue());
	private static final Color DRAFT = new Color(java.awt.Color.decode("#389FD6").getRed(),java.awt.Color.decode("#389FD6").getGreen(),java.awt.Color.decode("#389FD6").getBlue());
	private static final Color DEP = new Color(java.awt.Color.decode("#EDA200").getRed(),java.awt.Color.decode("#EDA200").getGreen(),java.awt.Color.decode("#EDA200").getBlue());
	private static final Color RET = new Color(java.awt.Color.decode("#DB5860").getRed(),java.awt.Color.decode("#DB5860").getGreen(),java.awt.Color.decode("#DB5860").getBlue());

	public TreeNodeStateProvider() {
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
		String state = getText(element);
		if (state == null) return null;
		switch (getText(element)) {
	        case "Released":	return REL;
        case "Draft":			return DRAFT;
        case "Deprecated":		return DEP;
        case "Retired":			return RET;
        default: 				return DEF;
		}
	}

	@Override
	public Color getBackground(Object element) {
		return null;
	}
}

