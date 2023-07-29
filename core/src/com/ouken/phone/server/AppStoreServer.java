package com.ouken.phone.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Enumeration;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.DataOutput;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ouken.phone.utils.GdxUtils;


public class AppStoreServer extends Game{
	
	private static final Logger log = new Logger(AppStoreServer.class.getName(), Logger.DEBUG);
	
	public static final int WIDTH = 720, HEIGHT = 480;
	public static final float WORLD_WIDTH = WIDTH, WORLD_HEIGHT = HEIGHT;
	private static final float ICON_SIZE = 64;
	
	public static final int PORT = 1338;
	public static final String IP = "localhost";
	
	private SpriteBatch batch;
	private Viewport viewport;
	private Stage stage;
	
	private Skin skin;
	private Thread serverThread;
	private ServerSocket serverSocket;
	
	
	private final Array<FileHandle> appFiles = new Array<FileHandle>();
	private final ArrayMap<String, FileHandle> iconFiles = new ArrayMap<String, FileHandle>();
	private Array<Texture> iconTextures = new Array<Texture>();
	
	// -- init --
	
	@Override
	public void create() {
		batch = new SpriteBatch();
		viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
		stage = new Stage(viewport, batch);
		
		skin = new Skin(Gdx.files.internal("default/uiskin.json"));
		
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		initServerContent();
		initServer();
	}

	private void initServerContent() {
		// init foler
		FileHandle folder = Gdx.files.external(".oukenphone_appstore_server/");
		if(!folder.exists())folder.mkdirs();
		appFiles.addAll(folder.list());
		log.debug("--- App files detected: " +appFiles.size + " ---");
		
		float padBottom = 10;
		Table container = new Table(skin);
		container.setFillParent(true);
		container.defaults().padBottom(padBottom);
		container.center();
		
		for(FileHandle file : appFiles) {
			Table table = new Table();
			Label label = new Label(file.name(), skin);
			
			table.add(label).row();
			
			log.debug("files in app file: " + file.list().length);
			
			for(FileHandle sub : file.list()) {
				String ex = sub.extension();
				if(ex.equals("png") || ex.equals("jpg") || ex.equals("jpeg")) {
					
					// image file
					iconFiles.put(file.name(), sub);
					Texture tex = new Texture(sub);
					iconTextures.add(tex);
					
					ImageButton iconImage = new ImageButton(new TextureRegionDrawable(tex));
					table.add(iconImage).size(ICON_SIZE).row();
					
					break;
				}
			}
			container.add(table);
			container.row();
		}

		container.pack();
		container.setDebug(true);
		
		ScrollPane scroll = new ScrollPane(container, skin);
		scroll.setFillParent(true);
		scroll.setFlickScroll(false);
	
		stage.addActor(scroll);
		stage.setScrollFocus(scroll);
	}
	
	
	private void initServer() {
		ServerSocketHints hints = new ServerSocketHints();
		hints.acceptTimeout = 0;
		
		serverSocket = Gdx.net.newServerSocket(Protocol.TCP, PORT, hints);
		
		
		serverThread = new Thread( new Runnable() {
			
			@Override
			public void run() {
				debugServer("Starting Server Thread to look for incomming connections...");
				while(true) {
					
					if(serverThread.isInterrupted()) return;
					
					debugServer("Waiting for incoming connections...");
					Socket socket = serverSocket.accept(null);
					debugServer("client socket accepted...");
					
					BufferedInputStream fromClient = new BufferedInputStream(socket.getInputStream());
					
					DataOutputStream toClientData = new DataOutputStream(socket.getOutputStream());
					
					
					
					// sending app size
					try {
						toClientData.writeInt(iconFiles.size);
					} catch (IOException e3) {
						errorServer("Couldnt write app size to stream");
						
						try {
							socket.getInputStream().close();
							socket.getOutputStream().close();
						} catch (IOException e2) {
							e2.printStackTrace();
						}
						e3.printStackTrace();
						return;
					}
					
					
					// sending app name and icon 
					for(Entry<String, FileHandle> e: iconFiles) {
						
						byte[] byteArrayAppName = (e.key + "\n").getBytes();
						byte[] byteArrayContent = e.value.readBytes();
						
						debugServer("App: " + e.key + " -> Sending byteArray of length " + byteArrayContent.length);
						
						try {
							toClientData.writeInt(byteArrayAppName.length);
							toClientData.write(byteArrayAppName);
							
							toClientData.writeInt(byteArrayContent.length);
							toClientData.write(byteArrayContent);
							toClientData.close();

						} catch (IOException e1) {
							errorServer("Not able to write to the stream");
							
							try {
								socket.getInputStream().close();
								socket.getOutputStream().close();
							} catch (IOException e2) {
								e2.printStackTrace();
							}
							
							
							e1.printStackTrace();
						}
						
					}

					socket.dispose();
				}
				
			}
		});
		serverThread.start();
			
		
	}
	
	
	
	
	
	// -- render --
	
	@Override
	public void render() {
		GdxUtils.clearScreen(Color.WHITE);
		
		stage.act();
		stage.draw();
		
		super.render();
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		super.resize(width, height);
	}
	
	@Override
	public void dispose() {
		stage.dispose();
		iconTextures.forEach(icon -> icon.dispose());
		serverSocket.dispose();
		serverThread.interrupt();
		super.dispose();
	}
	
	// -- private --
	
	private void debugServer(String message) {
		log.debug("[Server]: " + message);
	}
	
	private void errorServer(String message) {
		log.error("[Server]: " + message);
	}
	
}
