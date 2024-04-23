package com.solace.ep.eclipse.views;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Color;

import com.solace.ep.eclipse.Activator;
import com.solace.ep.eclipse.prefs.PreferenceConstants;

public class ColorUtils implements IPropertyChangeListener {

	private static ColorUtils instance = new ColorUtils();
//	private static final Logger logger = LogManager.getLogger(ColorUtils.class);

//	private IThemeManager themeManager = null;

	enum ColorMode {
		DARK,
		LIGHT,
		;
	}
	
	private ColorMode mode = ColorMode.DARK;
	private Color textDarkMode = new Color(170, 170, 170);
	private Color textLightMode = new Color(0, 0, 0);
	
	public static ColorMode getMode() {
		return instance.mode;
	}
	
	public static void setMode(ColorMode mode) {
		instance.mode = mode;
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
//		System.err.println("Updated!!: " + event.getProperty());
		if (PreferenceConstants.COLOUR_SCHEME.name().equals(event.getProperty())) updateMode();
		return;
/*		System.err.println("*********  propety change on theme manager on thread " + Thread.currentThread().getName());
		ITheme theme = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme();
		Color fg= theme.getColorRegistry().get(TABLE_FG);
		Color bg= theme.getColorRegistry().get(TABLE_BG);
		if (fg.getRGB().getHSB()[2] > bg.getRGB().getHSB()[2]) {  // compare brightness
			// dark mode
			if (mode != ColorMode.DARK) {
				logger.warn("ColorMode changing: {} to {}", mode, ColorMode.DARK);
			}
			mode = ColorMode.DARK;
		} else {
			if (mode != ColorMode.LIGHT) {
				logger.warn("ColorMode changing: {} to {}", mode, ColorMode.LIGHT);
			}
			mode = ColorMode.LIGHT;
		}*/
	}
	
//	private static final String TABLE_FG = "org.eclipse.ui.workbench.FORM_HEADING_INFO_COLOR";
//	private static final String TABLE_BG = "org.eclipse.egit.ui.UncommittedChangeBackgroundColor";

	static void init() {
//		instance.themeManager = themeManager;
//		instance.propertyChange(null);  // kick off to set the mode!
//		
//		PlatformUI.getWorkbench().getThemeManager().addPropertyChangeListener(instance);
	}
	
	private void updateMode() {
		mode = ColorMode.valueOf(Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.COLOUR_SCHEME.getId()).toUpperCase());
	}
	
	private ColorUtils() {
		System.err.println("Starting ColorUtils constructor");
		updateMode();
		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(this);
//		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(PreferenceConstants.PAGE_ID);
//		preferences.addPreferenceChangeListener(event -> {
//			System.err.println("1: " + event.toString());
//		});
//		IPreferenceStore preferences2 = Activator.getDefault().getPreferenceStore();
//		preferences2.addPropertyChangeListener(event -> {
//			System.err.println("2: " + event.toString());
//		});
//		System.err.println(preferences);
//		System.err.println(preferences2);
		
		
//		preferences.addPropertyChangeListener(new IPropertyChangeListener() {
//		        @Override
//		        public void propertyChange(PropertyChangeEvent event) {
//		            if (event.getProperty() == PreferenceConstants.COLOUR_SCHEME) {
//		        		mode = ColorMode.valueOf(Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.COLOUR_SCHEME.getId()).toUpperCase());
//		            }
//		        }
//		    });
	}
	
	public static Color decode(String hexCode) {
		java.awt.Color c = java.awt.Color.decode(hexCode);
		return new Color(c.getRed(), c.getGreen(), c.getBlue());
	}
	
	public static Color blend(Color base, Color accent, float amount) {
		assert amount >= 0 && amount <= 1;
		int r = Math.round(base.getRed() * (1-amount) + accent.getRed() * amount);
		int g = Math.round(base.getGreen() * (1-amount) + accent.getGreen() * amount);
		int b = Math.round(base.getBlue() * (1-amount) + accent.getBlue() * amount);
		return new Color(r,g,b);
	}
	
	
	
	public static Color getDefaultColor() {
		switch (instance.mode) {
		case DARK: return instance.textDarkMode;
		case LIGHT: return instance.textLightMode;
		default: return null;
		}
		
//		if (1 == 1) return PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry().get("org.eclipse.ui.workbench.FORM_HEADING_INFO_COLOR");
//		if (instance.themeManager == null) return Display.getDefault().getSystemColor(SWT.COLOR_LIST_FOREGROUND);
//		System.err.println(instance.themeManager.getCurrentTheme().getId());
//		return instance.themeManager.getCurrentTheme().getColorRegistry().get("org.eclipse.ui.workbench.FORM_HEADING_INFO_COLOR");
//		Color c = Display.getDefault().getSystemColor(SWT.COLOR_LIST_FOREGROUND);
//		return c;
	}
	
	
}
