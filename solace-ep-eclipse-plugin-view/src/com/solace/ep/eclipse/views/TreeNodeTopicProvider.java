package com.solace.ep.eclipse.views;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

class TreeNodeTopicProvider extends ColumnLabelProvider implements IFontProvider {
	
//	final Font font;
//	final FontData fd;
//	final Font fwFont;
//	final Font bFont;

	public TreeNodeTopicProvider(/* Font origFont, */ Display display) {
//		font = tree.getFont();
//		font = origFont;
//		FontData fd;
//		fd = font.getFontData()[0];
////		nFont = new Font(font.getDevice(), fd);
//		bFont = new Font(display, new FontData( fd.getName(), fd.getHeight(), SWT.BOLD ) );
//		fwFont = new Font(display, new FontData( "Consolas", fd.getHeight(), SWT.BOLD ) );
	}

	@Override
	public String getText(Object obj) {
		return ((TreeNode)obj).content[4];
	}
	
	@Override
	public Image getImage(Object obj) {
		return null;
	}

	@Override
	public Font getFont(Object element) {
		return EventPortalView.fonts.get("topic");
//		return fwFont;
		// TODO Auto-generated method stub
//		return super.getFont(element);
	}

	
	
}

