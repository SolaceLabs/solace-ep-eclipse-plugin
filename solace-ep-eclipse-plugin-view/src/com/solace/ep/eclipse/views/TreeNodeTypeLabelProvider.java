package com.solace.ep.eclipse.views;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

class TreeNodeTypeLabelProvider extends ColumnLabelProvider /* implements IStyledLabelProvider */ {
	
	public TreeNodeTypeLabelProvider() {
	}

	@Override
	public String getText(Object obj) {
		return ((TreeNode)obj).content[0];
	}
	
	@Override
	public Image getImage(Object obj) {
		TreeNode tn = (TreeNode)obj;
		if (tn.iconType == null) return null;
		return Icons.getImage(tn.iconType);
	}
}

