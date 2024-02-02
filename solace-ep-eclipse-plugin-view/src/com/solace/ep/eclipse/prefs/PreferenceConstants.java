package com.solace.ep.eclipse.prefs;

/**
 * Constant definitions for plug-in preferences
 */
public enum PreferenceConstants {
	
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
	
	;

	public static final String PAGE_ID = "eventPortal.preferences.PreferencePage";

	private final String tokenName;
	private final String description;
	
	private PreferenceConstants(String token, String description) {
		this.tokenName = token;
		this.description = description;
	}
	
	public String getToken() {
		return tokenName;
	}
	
	public String getDescription() {
		return description;
	}
}
