package com.solace.ep.eclipse.views;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import community.solace.ep.client.model.ApplicationDomain;
import community.solace.ep.wrapper.EventPortalWrapper;
import community.solace.ep.wrapper.SupportedObjectType;

class TreeNodeNameProvider extends ColumnLabelProvider implements IFontProvider {
	
	final int textFromColumn;
	final SupportedObjectType typeToBold;
	
	public TreeNodeNameProvider(int textFromColumn, SupportedObjectType typeToBold) {
		this.textFromColumn = textFromColumn;
		this.typeToBold = typeToBold;
	}

	@Override
	public String getToolTipText(Object obj) {
		TreeNode node = (TreeNode)obj;
		if (node.origDomainId != null) {
			ApplicationDomain domain = EventPortalWrapper.INSTANCE.getDomain(node.origDomainId);
			if (domain != null) {
				return node.content[textFromColumn] + "\nDomain: " + domain.getName();
			} else {
				return node.content[textFromColumn] + "\nDomain ID: " + node.origDomainId;
			}
		}
		return null;
	}
	
	@Override
	public String getText(Object obj) {
		return ((TreeNode)obj).content[textFromColumn];
	}
	

	@Override
	public Image getImage(Object obj) {
		return null;
	}

	@Override
	public Font getFont(Object element) {
		TreeNode node = (TreeNode)element;
		if (node.type == typeToBold) {
			return EventPortalView.fonts.get("bold");
		}
		return EventPortalView.fonts.defaultFont();
	}
}

