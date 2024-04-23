package com.solace.ep.eclipse.wizards;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


//public class ImportWizardPage extends WizardNewFileCreationPage {
public class ImportWizardPageHack extends WizardPage {
	
	protected FileFieldEditor editor;
	private static final Logger logger = LogManager.getLogger(ImportWizardPageHack.class);

	public ImportWizardPageHack(String pageName, IStructuredSelection selection) {
		super(pageName);
		setTitle(pageName);
		setDescription("Import a project from a local AsyncAPI file. Supports version 2.5.0.");
		logger.info("ImportWizardPageHack() constructor");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#createAdvancedControls(org.eclipse.swt.widgets.Composite)
	 */	
	protected void createAdvancedControls(Composite parent) {
/*		Composite fileSelectionArea = new Composite(parent, SWT.NONE);
		GridData fileSelectionData = new GridData(GridData.GRAB_HORIZONTAL
				| GridData.FILL_HORIZONTAL);
		fileSelectionArea.setLayoutData(fileSelectionData);

		GridLayout fileSelectionLayout = new GridLayout();
		fileSelectionLayout.numColumns = 3;
		fileSelectionLayout.makeColumnsEqualWidth = false;
		fileSelectionLayout.marginWidth = 0;
		fileSelectionLayout.marginHeight = 0;
		fileSelectionArea.setLayout(fileSelectionLayout);
		System.out.println("I AM HERE ER!!!");
		
		editor = new FileFieldEditor("fileSelect","Select File: ",fileSelectionArea); //NON-NLS-1 //NON-NLS-2
		editor.getTextControl(fileSelectionArea).addModifyListener(e -> {
			IPath path = new Path(ImportWizardPageHack.this.editor.getStringValue());
//			setFileName(path.lastSegment());
		});
		String[] extensions = new String[] { "*.json;*.yaml" }; //NON-NLS-1
		editor.setFileExtensions(extensions);
		fileSelectionArea.moveAbove(null);
		
		*/
		
		
		logger.info("ImportWizardPageHack() createAdvancedControls");

	}
	
	 /* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#createLinkTarget()
	 */
	protected void createLinkTarget() {
		logger.info("ImportWizardPageHack() createLinkTarget");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#getInitialContents()
	 */
	protected InputStream getInitialContents() {
		logger.info("ImportWizardPageHack() getInitialContents");
		try {
			return new FileInputStream(new File(editor.getStringValue()));
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#getNewFileLabel()
	 */
	protected String getNewFileLabel() {
		logger.info("ImportWizardPageHack() getNewFileLabel");
		return "New File Name:";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#validateLinkedResource()
	 */
	protected IStatus validateLinkedResource() {
		logger.info("ImportWizardPageHack() validateLinkedResource");
		return Status.OK_STATUS;
	}
	
	
	
	

	@Override
	public void createControl(Composite parent) {
		logger.info("ImportWizardPageHack() createControl");
		
		Composite fileSelectionArea = new Composite(parent, SWT.NONE);
		GridData fileSelectionData = new GridData(GridData.GRAB_HORIZONTAL
				| GridData.FILL_HORIZONTAL);
		fileSelectionArea.setLayoutData(fileSelectionData);

		GridLayout fileSelectionLayout = new GridLayout();
		fileSelectionLayout.numColumns = 3;
		fileSelectionLayout.makeColumnsEqualWidth = false;
		fileSelectionLayout.marginWidth = 0;
		fileSelectionLayout.marginHeight = 0;
		fileSelectionArea.setLayout(fileSelectionLayout);
		
		editor = new FileFieldEditor("fileSelect","Select File: ",fileSelectionArea); //NON-NLS-1 //NON-NLS-2
		editor.getTextControl(fileSelectionArea).addModifyListener(e -> {
			IPath path = new Path(ImportWizardPageHack.this.editor.getStringValue());
//			setFileName(path.lastSegment());
		});
		String[] extensions = new String[] { "*.json" }; //NON-NLS-1
		editor.setFileExtensions(extensions);
		fileSelectionArea.moveAbove(null);
		
		setControl(fileSelectionArea);

	}
}
