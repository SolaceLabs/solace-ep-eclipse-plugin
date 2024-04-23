package com.solace.ep.eclipse.views;

import org.eclipse.jface.viewers.ColumnLabelProvider;

class TreeNodeGenericProvider extends ColumnLabelProvider {
	
	final int textFromColumn;
	
	public TreeNodeGenericProvider(int textFromColumn) {
		this.textFromColumn = textFromColumn;
	}

	@Override
	public String getText(Object obj) {
		TreeNode node = (TreeNode)obj;
		if (node == null || node.content == null || textFromColumn > node.content.length-1) {
			return "-";
		}
		return node.content[textFromColumn];
	}
}

