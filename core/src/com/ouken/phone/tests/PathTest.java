package com.ouken.phone.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * https://gamefromscratch.com/libgdx-tutorial-10-basic-networking/
 * 
 * https://www.youtube.com/watch?v=OMbtBzmk650
 * @author sebas
 *
 */
public class PathTest extends ApplicationAdapter{
	
	// -- constants --
	public static final int WIDTH = 720, HEIGHT = 480; // pxl
	public static final float WORLD_WIDTH = WIDTH, WORLD_HEIGHT = HEIGHT; // world units 
	private static final Logger log = new Logger(PathTest.class.getName(), Logger.DEBUG);
	
	// -- attributes --
	private Viewport viewport;
	private Stage stage;
	private Skin skin;
	
	
	
	// -- init --
	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
		stage = new Stage(viewport);
		skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
		
		Gdx.input.setInputProcessor(stage);
		
		init();
	}
	
	/*
	 * Idea: 
	 * https://stackoverflow.com/questions/941754/how-to-get-a-path-to-a-resource-in-a-java-jar-file
	 * 
	 * [.txt]
	 * 1) choosing a (local based path = using localPath * "/folderXY/file_name) filepath and creating a txt file;
	 * 1.1) reading to the txt file from (local based path from above) path
	 * 1.2)delete the file
	 * 
	 * [.jar]
	 * 2) creating(= writing ?) a jar file (in the above choosen folder) on runtime
	 * 2.1)reading a jar file on runtime (OR read it on preinit in build.gradle.)
	 *     https://stackoverflow.com/questions/27187566/load-jar-dynamically-at-runtime
	 * 2.2) [BREAK THIS DOWN!] using classpathloader to search for specific classes(with help of annotations ?)
	 * 
	 * */
	private void init() {
		testFilePaths();
		testFile();
	}
	
	private void testFilePaths() {
		// -- used in project structure, NOT .jar file --
		
		// The install directory is typically the directory where the java app is started. The path
		// can be by found from a running java app as:
		String userDir = System.getProperty("user.dir");
		// in this case: C:\Users\sebas\Documents\Eclipse\forge\mc phone 2021 worpkspace\libgdx phone project\desktop
		log.debug("user.dir: " + userDir);
		
		
		//getting an absolut path of a file relative to app dir with File.absolutePath():
		String fileDir = new File("lib/dummy.exe").getAbsolutePath();//returns string and doesnt create a file (?)
		 //in this case: C:\Users\sebas\Documents\Eclipse\forge\mc phone 2021 worpkspace\libgdx phone project\desktop\lib\dummy.exe
		log.debug("fileDir: " + fileDir);
		
		
		
		// -- used (also?) in .jar file --
		
		// !!! since the directory strucutre changes after deploying the app, this is how you find a
		// file's local path INSIDE a jar file
		// [INFO]"/assets/pacman.png" is an absolute location whle "assets/pacman.png" is a relative location.
		// !! src/ and assets/ are already included
		String relativePath = "phone/phone.atlas"; // "com/ouken/phone"; // <- they all work
		File file = getInternalResource(relativePath);
		log.debug(file.getAbsolutePath());
		System.out.println(PathTest.class.getClassLoader().getResource(relativePath).getPath());
//		System.out.println(FileTest.class.getResource(relativePath).getPath()); // smh doesnt work
		
		
		
		// another jar path when read on runtime
		try {
			String jarPath = PathTest.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			log.debug("jarPath= " + jarPath);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
	}
	

	private File getInternalResource(String relativePath) {
		File resourceFile = null;
		URL location = this.getClass().getProtectionDomain().getCodeSource().getLocation();
		String codeLodaction = location.toString();

		try {
			if (codeLodaction.endsWith(".jar")) {
				// Call from jar
				Path path = Paths.get(location.toURI()).resolve("../classes/" + relativePath).normalize();
				resourceFile = path.toFile();
			} else {
				//Call from IDE
				resourceFile = new File(PathTest.class.getClassLoader().getResource(relativePath).getPath());
			}
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resourceFile;
	}

	
	// -- reading & writing to files --
	
	private void testFile() {
	
		
		File appsFolder = new File("apps");
		log.debug("apps folder path= " + appsFolder.getAbsolutePath());
		
	}
	
	
	
	
	// -- test app --

	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	private static @interface AppInfo{
		String name();
	}
	
	private static class AppStorage {
		private File basePath;
		
		public AppStorage(File basePath) {
			this.basePath = basePath;
		}
		
		public File getFile(String filePath) {
			if(!isCorrectPath(filePath))return null;
			File file = new File(basePath, filePath);
			return file;
		}
		
		private boolean isCorrectPath(String filePath) {
			if(filePath == null || filePath.contains(".."))return false;
			return true;
		}
	}
	
	private static class App{
		
		public AppInfo info;
		
		public App() {
			if(!this.getClass().isAnnotationPresent(AppInfo.class))return;	
		}
		
	}
	
	
	
	
	@AppInfo(name = MyApp.NAME)
	private static class MyApp extends App{
		protected static final String NAME = "MyApp";
	}
	
	

	
	
	// -- public methods --
	@Override
	public void render() {
		Color c = Color.GRAY;
		Gdx.gl.glClearColor(c.r,c.g,c.b,c.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//viewport.apply();
		stage.act();
		stage.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}
	
	@Override
	public void dispose() {
		log.debug("disposing");
		skin.dispose();
		stage.dispose();
	}
	
	
	
}
