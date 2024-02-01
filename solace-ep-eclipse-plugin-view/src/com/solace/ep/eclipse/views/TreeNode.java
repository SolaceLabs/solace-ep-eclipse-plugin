package com.solace.ep.eclipse.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;

import community.solace.ep.wrapper.SupportedObjectType;

class TreeNode implements IAdaptable  {
	
	final String id;
	String[] content = new String[0];
	final Object epObject;
	final Icons.Type iconType;
	final SupportedObjectType type;
	private TreeNode parent = null;
//	private ArrayList<TreeNode> children = null;
	private Map<String,TreeNode> children = null;

	public static TreeNode createRootNode() {
		return new TreeNode(null, null, null, null, "root");
	}
	
	public static class Builder {
		private String id = null;
//		private String[] content = new String[0];
		private Object epObject;
		private Icons.Type iconType;
		private SupportedObjectType type;

		public Builder withEpType(SupportedObjectType type) {	this.type = type; return this; }
		public Builder withId(String id) {						this.id = id; return this; }
		public Builder withEpObject(Object epObject) {			this.epObject = epObject; return this; }
		public Builder withIconType(Icons.Type iconType) {		this.iconType = iconType; return this; }
		public TreeNode build(String... content) {
			if (id == null) throw new IllegalArgumentException("Builder not finished, id is null!");
			return new TreeNode(type, id, epObject, iconType, content);
		}
	}
	
	
	
	private TreeNode(SupportedObjectType type, String id, Object epObject, Icons.Type iconType, String... cols) {
		this.type = type;
		this.epObject = epObject;
		this.iconType = iconType;
		this.id = id;
		this.content = cols;
	}

//	public TreeNode(TreeNode parent, String... cols) {
//		this.parent = parent;
//		this.content = cols;
//	}

	public void addChild(TreeNode child) {
		if (children == null) {  // lazy init
			children = new LinkedHashMap<>();
		}
		children.put(child.id, child);
		child.parent = this;
	}

	public TreeNode getParent() {
		return parent;
	}
	
	/** Could be null */
	public TreeNode[] getChildren() {
		if (children == null ) {
			return null;//new TreeNode[0];
		}
		return children.values().toArray(new TreeNode[children.size()]);
//		return children.toArray(new TreeNode[children.size()]);
	}

	public boolean hasChildren() {
		if (children == null) return false;
		return children.size() > 0;
	}

	@Override
	public String toString() {
		return "toString(): " + Arrays.toString(content);
//		return getName().toString();
	}
	

	
	@Override
	public <T> T getAdapter(Class<T> key) {
		return null;
	}
}
