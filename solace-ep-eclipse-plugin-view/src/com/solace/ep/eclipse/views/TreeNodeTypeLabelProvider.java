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
		return Icons.getInstance().getImage(tn.iconType);
//		switch (tn.type) {
//		case DOMAIN: return Icons.getInstance().getImage(Icons.Type.DOMAIN);
//		case APP: return Icons.getInstance().getImage(Icons.Type.APP);
//		case vAPP: return Icons.getInstance().getImage(Icons.Type.vAPP);
//		case EVENT: return Icons.getInstance().getImage(Icons.Type.EVENT);
//		case vAPP: return Icons.getInstance().getImage(Icons.Type.vAPP);
//		default:
//			return Icons.getInstance().getImage(Icons.Type.SCHEMA);
//		}
	}
}

