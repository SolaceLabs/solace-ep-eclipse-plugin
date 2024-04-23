package com.solace.ep.eclipse.wizards;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import com.solace.ep.eclipse.views.SuperTabView;

public class ImportAsyncAPIWizard extends Wizard implements IImportWizard {
	
	private static final Logger logger = LogManager.getLogger(ImportAsyncAPIWizard.class);
	ImportWizardPage mainPage;

	public ImportAsyncAPIWizard() {
		super();
		logger.info("ImportAsyncAPIWizard() constrcutro");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
//		IFile file = firstPage.createNewFile();
//        if (file == null)
//            return false;
        return true;
	}
	 
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		logger.info("ImportAsyncAPIWizard() init()");
		setWindowTitle("AsyncAPI File Import Wizard"); //NON-NLS-1
		setNeedsProgressMonitor(true);
		System.out.println(selection);
		mainPage = new ImportWizardPage("Import AsyncAPI File",selection); //NON-NLS-1
	}
	
	/* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#addPages()
     */
    public void addPages() {
        super.addPages(); 
        addPage(mainPage);        
		logger.info("ImportAsyncAPIWizard() addPages()");
    }

}
