package com.ouken.phone.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.graphics.Color;
import com.ouken.phone.Phone;
import com.ouken.phone.config.Config;
import com.ouken.phone.tests.NetworkingTest;

public class NewtworkingDesktopLauncher {
	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
//		config.stencil = 8;
//		config.width = (int) Config.WIDTH;
//		config.height = (int) Config.HEIGHT;
//		config.initialBackgroundColor = new Color(0, 0, 0, 0);
//		config.resizable = false;
//		config.undecorated = true;
		config.setWindowedMode(NetworkingTest.WIDTH, NetworkingTest.HEIGHT);
		config.setBackBufferConfig(8, 8, 8, 8, 16, 8, 0);
		new Lwjgl3Application(new NetworkingTest(), config);
		
	}
}
