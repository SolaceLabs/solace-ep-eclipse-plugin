package com.solace.ep.eclipse.wizards;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.eclipse.ui.wizards.IWizardRegistry;

import com.solace.ep.eclipse.prefs.PreferenceConstants;
import com.solace.ep.eclipse.views.EventPortalView;
import com.solace.ep.eclipse.views.UsefulUtils;
import com.solace.ep.muleflow.eclipse.EclipseProjectGenerator;

import dev.solace.aaron.useful.FileUtils;

public class ImportAsyncAPIWizardHack extends Wizard implements IImportWizard {
	
	private static final Logger logger = LogManager.getLogger(ImportAsyncAPIWizardHack.class);
	public ImportWizardPageHack firstPage;
	IWizardPage secondPage;
	IWizard muleWizard;
	boolean runCodegenYet = false;

	public ImportAsyncAPIWizardHack() {
		super();
		logger.info("ImportAsyncAPIWizardHack() constrcutro");
	}

	/** (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		logger.info("ImportAsyncAPIWizardHack() performFinish");
//		IFile file = firstPage.createNewFile();
//        if (file == null)
//            return false;
//        return true;
        
        return muleWizard.performFinish();
        
	}
	
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
	    IWizardPage nextPage = super.getNextPage(page);
		logger.info("ImportAsyncAPIWizardHack() getNextPage() " + page.getName());
	    logger.info("The value of the file dialgoe: " +  firstPage.editor.getStringValue());
	    String pathString = firstPage.editor.getStringValue();

	    if (nextPage == secondPage && (pathString != null && !pathString.isEmpty()) && !runCodegenYet) {
			logger.info("ImportAsyncAPIWizardHack() loading the Mule 2nd getNextPage()");
	    	runCodegenYet = true;
//	        ParsedData data = parse(page.getData());
//	        nextPage.initFields(data);
			
		    File file = new File(pathString);
	    	logger.info(file.toString());
	    	try {
	    		String asyncApi = FileUtils.loadTextFile(file);
	    		String artifactId = "DefaultAppName";
	    		String version = "0.0.1";
	    		String groupId = "DefaultDomain";
	    		Pattern titlePattern = Pattern.compile("\"?title\"? ?: ?\"([^\"]*)\"");
	    		Pattern versionPattern = Pattern.compile("[^-]\"?version\"? ?: ?\"([^\"]*)\"");
	    		Pattern domainPattern = Pattern.compile("\"?x-ep-application-domain-name\"? ?: ?\"([^\"]*)\"");
	    		
	    		Matcher m1 = titlePattern.matcher(asyncApi);
	    		if (m1.find()) {
	    			artifactId = UsefulUtils.helperStripNonChars(m1.group(1));
//	    			logger.info("Found a title match: " + m1.group());
	    		} else {
	    			logger.warn("Could not find a title match");
	    		}
	    		Matcher m2 = versionPattern.matcher(asyncApi);
	    		if (m2.find()) {
	    			version = m2.group(1);
//	    			logger.info("Found a version match: " + m2.group());
	    		} else {
	    			logger.warn("Could not find a version match");
	    		}
	    		Matcher m3 = domainPattern.matcher(asyncApi);
	    		if (m3.find()) {
	    			groupId = UsefulUtils.helperMakePackageName(m3.group(1));
//	    			logger.info("Found a domain match: " + m3.group());
	    		} else {
	    			logger.warn("Could not find a domain match");
	    		}
	    		
		        String generatedArchive = "NADA";
		        EclipseProjectGenerator epg = new EclipseProjectGenerator();
		        try {
//					            generatedArchive = epg.createMuleProject(groupId, artifactId, version, xmlString);
//					        	String saveLocation = System.getProperty("user.home") + System.getProperty(baseUrl)
		        	String filename = "Mule-" + artifactId + "-" + version + ".jar";
//		        	File saveFile = new File(System.getProperty("user.home"), "Downloads/" + filename);
		        	File saveFile = new File(file.getParent(), filename);
		        	generatedArchive = saveFile.getAbsolutePath();
		            epg.generateEclipseArchiveForMuleFlowFromAsyncApi(groupId, artifactId, version, asyncApi, saveFile.getAbsolutePath());

			        // If we're here, we succeeded
			        Path generatedArchivePath = Paths.get(generatedArchive);						    
				    logger.info("### DONE!  " + generatedArchivePath.toString());
/*				    boolean success = EventPortalView.copyStringToClipboard(generatedArchivePath.toString());
				    
				    if (success) {
//							    	String message = String.format("Mule project built! %s.%s%n%nLocation saved to clipboard. Paste into Import \"Packaged mule application\" dialog.\"", groupId, artifactId);
				    	String message = String.format("Mule project built! %s.%s%n%nLocation saved to clipboard. Paste into following Import dialog.", groupId, artifactId);
				    	EventPortalView.showMessage(message);
				    } else {
				    	String message = String.format("Mule project built! %s.%s%n%nSaved to: %s%n%nLoad into following Import dialog.", groupId, artifactId, generatedArchivePath.toString());
				    	EventPortalView.showMessage(message);
//							    	EventPortalView.showMessage("Mule project built! " + groupId + "." + artifactId +"\n\nLocation: " + generatedArchivePath.toString() + ". Paste into Import \"Packaged mule application\" dialog.");
				    }
*/

				    
//						java.lang.reflect.Field mainPageField = wiz.getClass().getDeclaredField("firstPage");
//						sb.append("firstPage field is: " + mainPageField);
//						mainPageField.setAccessible(true);
//						Object importPage = mainPageField.get(wiz);
//						if (importPage == null) {
//							EventPortalView.showWarning(sb.toString());
//							return;
//						} else {
//							sb.append("\n\nImport page class: " + importPage.getClass().getSimpleName());
//						}
//						Class<?> concreteClass = Class.forName("org.mule.tooling.ui.wizards.MuleZippedProjectImportPage");
					Method[] publicMethods = secondPage.getClass().getMethods();
					logger.info("Methods are: " + Arrays.toString(publicMethods));
					
					Method setFilePathMethod = secondPage.getClass().getDeclaredMethod("setFilePath", String.class);
					logger.info("My setFilePathMethod sig is: " + setFilePathMethod.toString());
					setFilePathMethod.invoke(secondPage, generatedArchivePath.toString());
					logger.info("Made it through!");

				    
				    
				    
				    
				    

		        } catch (Exception e) {
			    	logger.error("EclipseProjectGenerator.createMuleProject() failed", e);
			    	String err = String.format("Could not generate Mule project from AsyncAPI.%n%n%s%n%nCheck logs at %s for details.", e.toString(), PreferenceConstants.LOG_LOCATION.getDescription());
			    	EventPortalView.showWarning(err);
//		            return;
		        }

	    		
	    		
	    	} catch (FileNotFoundException e) {  // should be hard since we picked it out of a box
	    		
	    	}
	    }
	    return nextPage;
	}


	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		logger.info("ImportAsyncAPIWizardHack() init(), workbench={}, selection={}", workbench, selection);
		setWindowTitle("AsyncAPI File Import Wizard"); //NON-NLS-1
		setNeedsProgressMonitor(true);
		System.out.println(selection);
		firstPage = new ImportWizardPageHack("Import AsyncAPI File",selection); //NON-NLS-1
	}
	
	/* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#addPages()
     */
    public void addPages() {
        super.addPages(); 
		logger.info("ImportAsyncAPIWizardHack() addPages()");
		
        try {
			IWizardRegistry wizards = PlatformUI.getWorkbench().getImportWizardRegistry();
			IWizardDescriptor wizard = wizards.findWizard("org.mule.tooling.ui.muleZipProjectImportWizard");
			if (wizard != null) {
				final IWizard wiz = wizard.createWizard();  // could throw CoreException
//				final WizardDialog wd = new WizardDialog(Display.getDefault().getActiveShell(), wiz);
//				wd.setTitle("Mule Import from Deployable Archive");
//				wd.open();
				
				logger.info("ImportAsyncAPIWizardHack() wiz is not null");
				logger.info("There are {} pages in Mule dialog", wiz.getPageCount());
				wiz.addPages();  // builds the page
				logger.info("There are {} pages in Mule dialog", wiz.getPageCount());
				secondPage = wiz.getStartingPage();
		        addPage(firstPage);
				addPage(secondPage);
				muleWizard = wiz;
			} else {
            	logger.info("Could not create new Mule Import dialog");
			}
        } catch (CoreException e) {
        	logger.warn("Could not create new Import dialog", e);
        }

		
		
		
    }

}
