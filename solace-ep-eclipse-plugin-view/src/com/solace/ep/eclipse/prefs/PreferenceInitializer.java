package com.solace.ep.eclipse.prefs;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.solace.ep.eclipse.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
//		store.setDefault(PreferenceConstants.P_BOOLEAN, true);
//		store.setDefault(PreferenceConstants.P_CHOICE, "choice2");
//		store.setDefault(PreferenceConstants.P_STRING,
//				"Default value");
//		store.setDefault("prefEpDOmain", "api.solace.com");
		store.setDefault(PreferenceConstants.TOKEN.getToken(), "");
		store.setDefault(PreferenceConstants.URL.getToken(), "api.solace.cloud");
		store.setDefault(PreferenceConstants.COLOUR_SCHEME.getToken(), "dark");
	}

}
