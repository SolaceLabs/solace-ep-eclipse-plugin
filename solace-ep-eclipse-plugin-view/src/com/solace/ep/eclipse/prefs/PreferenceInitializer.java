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
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.ORG.getId(), "Default");
		store.setDefault(PreferenceConstants.TOKEN.getId(), "");
		store.setDefault(PreferenceConstants.WEB_URL.getId(), "console.solace.cloud");
		store.setDefault(PreferenceConstants.API_URL.getId(), "api.solace.cloud");
		store.setDefault(PreferenceConstants.TIME_FORMAT.getId(), "relative");
		store.setDefault(PreferenceConstants.COLOUR_SCHEME.getId(), "light");
	}
}
