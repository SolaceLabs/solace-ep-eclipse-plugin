package com.solace.ep.eclipse.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

class TreeNodeViewContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getElements(Object parent) {
//		System.out.println("ViewContentProvider.getElements(obj) called");
//		if (parent.equals(getViewSite())) {  // the root I guess?
//			System.out.println(parent.toString());
//			if (root == null) initialize();  // initialize
//			return getChildren(root);
//		}
		return getChildren(parent);
	}
	
	@Override
	public void dispose() {
		System.err.println("ViewContentProvider.dispose() called");
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		System.err.println("ViewContentProvider.inputChanged() called, old: "+oldInput+ ", new:"+newInput);
	}
	
	@Override
	public Object getParent(Object child) {
		if (child == null) return null;
		return ((TreeNode)child).getParent();  // could be null for the very root element?
	}
	
	@Override
	public Object[] getChildren(Object parent) {
		return ((TreeNode)parent).getChildren();
	}
	
	@Override
	public boolean hasChildren(Object parent) {
		if (parent instanceof TreeNode) return ((TreeNode)parent).hasChildren();
		return false;
	}

}
