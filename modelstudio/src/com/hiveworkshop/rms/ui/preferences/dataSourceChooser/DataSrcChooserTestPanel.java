package com.hiveworkshop.rms.ui.preferences.dataSourceChooser;

import com.jtattoo.plaf.acryl.AcrylLookAndFeel;
import com.jtattoo.plaf.aluminium.AluminiumLookAndFeel;
import com.jtattoo.plaf.hifi.HiFiLookAndFeel;
import com.jtattoo.plaf.noire.NoireLookAndFeel;

import javax.swing.*;
import java.util.Properties;

public class DataSrcChooserTestPanel {


	public static void main(final String[] args) {
		setupLookAndFeel("Aluminium");
		final JFrame dataSourceChooserFrame = new JFrame("DataSourceChooserPanel");
		dataSourceChooserFrame.setContentPane(new DataSourceChooserPanel(null));
		dataSourceChooserFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		dataSourceChooserFrame.pack();
		dataSourceChooserFrame.setLocationRelativeTo(null);
		dataSourceChooserFrame.setVisible(true);
	}

	public static void setupLookAndFeel(final String jtattooTheme) {

		// setup the look and feel properties
		final Properties props = new Properties();
		// props.put("windowDecoration", "false");
		//
		props.put("logoString", "RMS");
		// props.put("licenseKey", "INSERT YOUR LICENSE KEY HERE");
		//
		// props.put("selectionBackgroundColor", "180 240 197");
		// props.put("menuSelectionBackgroundColor", "180 240 197");
		//
		// props.put("controlColor", "218 254 230");
		// props.put("controlColorLight", "218 254 230");
		// props.put("controlColorDark", "180 240 197");
		//
		// props.put("buttonColor", "218 230 254");
		// props.put("buttonColorLight", "255 255 255");
		// props.put("buttonColorDark", "244 242 232");
		//
		// props.put("rolloverColor", "218 254 230");
		// props.put("rolloverColorLight", "218 254 230");
		// props.put("rolloverColorDark", "180 240 197");
		//
		// props.put("windowTitleForegroundColor", "0 0 0");
		// props.put("windowTitleBackgroundColor", "180 240 197");
		// props.put("windowTitleColorLight", "218 254 230");
		// props.put("windowTitleColorDark", "180 240 197");
		// props.put("windowBorderColor", "218 254 230");

		// set your theme
		switch (jtattooTheme) {
			case "Noire" -> NoireLookAndFeel.setCurrentTheme(props);
			case "HiFi" -> HiFiLookAndFeel.setCurrentTheme(props);
			case "Acryl" -> AcrylLookAndFeel.setCurrentTheme(props);
			case "Aluminium" -> AluminiumLookAndFeel.setCurrentTheme(props);
		}
		// select the Look and Feel
		try {
			UIManager.setLookAndFeel("com.jtattoo.plaf." + jtattooTheme.toLowerCase() + "." + jtattooTheme + "LookAndFeel");
		} catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
			throw new RuntimeException(e);
		}
	}
}
