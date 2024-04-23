package com.solace.ep.eclipse.views;

public class UsefulUtils {

	
	// "Point of sale (POS) terminal" -> "PointOfSalePOSTerminal"
	public static String helperStripNonChars(String name) {
		StringBuilder sb = new StringBuilder();
		boolean capNext = false;
		for (int i=0; i<name.length(); i++) {
			char c = name.charAt(i);
			if (Character.isAlphabetic(c)) {
				sb.append(capNext ? Character.toUpperCase(c) : c);
				capNext = false;
			} else if (Character.isDigit(c)) {
				sb.append(c);
				capNext = true;
			} else {
				capNext = true;
			}
		}
		return sb.toString();
	}

	// "ACME Retail Supply Logistics" -> "com.acme.retail"
	public static String helperMakePackageName(String name) {
		StringBuilder sb = new StringBuilder();
		name = "com." + name.toLowerCase();
		boolean skipToNextAlpha = true;
		int depth = 0;
		for (int i=0; i<name.length(); i++) {
			char c = name.charAt(i);
			if (Character.isAlphabetic(c)) {
				sb.append(c);
				skipToNextAlpha = false;
			} else {
				if (!skipToNextAlpha) {
					skipToNextAlpha = true;
					depth++;
					if (depth == 3) return sb.toString();  // done!
					sb.append(".");
				}
			}
		}
		return sb.toString();
	}

}
