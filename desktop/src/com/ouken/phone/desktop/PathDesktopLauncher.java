package com.ouken.phone.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.ouken.phone.tests.PathTest;

public class PathDesktopLauncher {
	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(PathTest.WIDTH, PathTest.HEIGHT);
		config.setBackBufferConfig(8, 8, 8, 8, 16, 8, 0);
		new Lwjgl3Application(new PathTest(), config);
		
	}
}
