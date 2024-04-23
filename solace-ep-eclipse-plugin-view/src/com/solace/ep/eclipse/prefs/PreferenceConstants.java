package com.solace.ep.eclipse.prefs;

import java.io.File;

/**
 * Constant definitions for plug-in preferences
 */
public enum PreferenceConstants {
	
	ORG("orgPref", "Token name:"),
	TOKEN("tokenPref", "&Event Portal token:"),
	WEB_URL("urlPref", "&URL for web Portal:"),
	API_URL("apiUrlPref", "&API base URL:"),
	TIME_FORMAT("timeFormatPref", "&Time Format:"),
	TIME_FORMAT_RELATIVE("relative", "Relative"),
	TIME_FORMAT_ISO("iso", "ISO"),
	TIME_FORMAT_NORMAL("normal", "Normal"),
	COLOUR_SCHEME("colourPref", "Colour Scheme:"),
	COLOUR_SCHEME_DARK("dark", "&Dark Mode"),
	COLOUR_SCHEME_LIGHT("light", "&Light Mode"),
	LOG_LOCATION("logDir", System.getProperty("user.home") + File.separatorChar + "solace-ep-plugin-log"),
	;

	public static final String PAGE_ID = "eventPortal.preferences.PreferencePage";

	private final String id;
	private final String description;
	
	private PreferenceConstants(String id, String description) {
		this.id = id;
		this.description = description;
	}
	
	public String getId() {
		return id;
	}
	
	public String getDescription() {
		return description;
	}
}
