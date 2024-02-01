package com.solace.ep.eclipse.prefs;

/**
 * Constant definitions for plug-in preferences
 */
public enum PreferenceConstants implements CharSequence {

	
//	public static final String P_PATH = "pathPreference";
//
//	public static final String P_BOOLEAN = "booleanPreference";
//
//	public static final String P_CHOICE = "choicePreference";
//
//	public static final String P_STRING = "stringPreference";
//
//	public static final String P_TOKEN = "tokenPreference";

	
	TOKEN("tokenPref"),
	URL("urlPref"),
	COLOUR_SCHEME("colourPref"),
	;

	public static final String PAGE_ID = "eventPortal.preferences.PreferencePage";

	private final String tokenName;
	
	private PreferenceConstants(String token) {
		this.tokenName = token;
	}
	
	public String getToken() {
		return tokenName;
	}

	@Override
	public int length() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public char charAt(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}
}
