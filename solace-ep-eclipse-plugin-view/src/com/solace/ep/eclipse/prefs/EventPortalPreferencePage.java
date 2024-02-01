package com.solace.ep.eclipse.prefs;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import com.solace.ep.eclipse.Activator;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class EventPortalPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public EventPortalPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Set your preferences for interfacing with the Solace PubSub+ Event Portal.");
		
		
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
/*		addField(new DirectoryFieldEditor(PreferenceConstants.P_PATH, 
				"&Directory preference:", getFieldEditorParent()));
		addField(
			new BooleanFieldEditor(
				PreferenceConstants.P_BOOLEAN,
				"&An example of a boolean preference",
				getFieldEditorParent()));

		addField(new RadioGroupFieldEditor(
				PreferenceConstants.P_CHOICE,
			"An example of a multiple-choice preference",
			1,
			new String[][] { { "&Choice 1", "choice1" }, {
				"C&hoice 2", "choice2" }
		}, getFieldEditorParent()));*/
//		addField(new StringFieldEditor(PreferenceConstants.P_STRING, "A &text preference:", getFieldEditorParent()));

//		addField(new StringFieldEditor(PreferenceConstants.P_STRING, "A &text preference2:", getFieldEditorParent()));
		StringFieldEditor password = new StringFieldEditor(PreferenceConstants.TOKEN.getToken(), "Event Portal token:", 30, getFieldEditorParent()) {

			@Override
			    protected void doFillIntoGrid(Composite parent, int numColumns) {
			        super.doFillIntoGrid(parent, numColumns);
			        getTextControl().setEchoChar('*');
			    }
			};
		addField(password);
		
		addField(new StringFieldEditor(PreferenceConstants.URL.getToken(), "URL for web access:", 30, getFieldEditorParent()));
		RadioGroupFieldEditor colour = new RadioGroupFieldEditor(
				PreferenceConstants.COLOUR_SCHEME.getToken(),
				"Colour Scheme:",
				2,
				new String[][] { { "&Dark Mode", "dark" }, {
					"&Light Mode", "light" }
		}, getFieldEditorParent());
		colour.setEnabled(false, getFieldEditorParent());
		addField(colour);
//		
//		PreferenceManager prefsManager = PlatformUI.getWorkbench().getPreferenceManager();
//		IPreferenceNode [] rootNodes = prefsManager.getRootSubNodes();
//		for (IPreferenceNode node : rootNodes) {
//			System.out.println("&&&&& " + node.getId());
//		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}