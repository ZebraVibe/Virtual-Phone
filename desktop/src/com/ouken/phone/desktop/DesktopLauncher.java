package com.ouken.phone.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.ouken.phone.Phone;
import com.ouken.phone.config.Config;

public class DesktopLauncher {
	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setDecorated(true);
		config.setResizable(false); // !!
		
//		config.setTransparentFramebuffer(true);
//		config.setInitialBackgroundColor(new Color(0, 0, 0, 0));
		config.setPreferencesConfig(Config.PHONE_FOLDER_PATH, FileType.External);
		config.setWindowedMode((int)Config.WIDTH, (int)Config.HEIGHT);
		config.setBackBufferConfig(8, 8, 8, 8, 16, 8, 0);
		new Lwjgl3Application(Phone.INSTANCE, config);
	}
}
