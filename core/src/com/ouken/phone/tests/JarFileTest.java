package com.ouken.phone.tests;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DataInput;
import com.badlogic.gdx.utils.DataOutput;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * 
 *[send files/bytes to server]
 *https://www.youtube.com/watch?v=GLrlwwyd1gY
 *
 *[send image to server]
 *https://www.youtube.com/watch?v=5YaRhoJlcpY
 *
 *[http requests code example]
 *https://gist.github.com/MobiDevelop/6215682
 *
 *[loading jar at runtime]
 *https://stackoverflow.com/questions/194698/how-to-load-a-jar-file-at-runtime
 *
 *[reading inputstreams]
 *https://stackoverflow.com/questions/10473873/java-inputstream-too-slow-to-read-huge-files
 *
 */
public class JarFileTest extends ApplicationAdapter{
	
	// -- constants --
	public static final int WIDTH = 720, HEIGHT = 480; // pxl
	public static final float WORLD_WIDTH = WIDTH, WORLD_HEIGHT = HEIGHT; // world units 
	private static final Logger log = new Logger(JarFileTest.class.getName(), Logger.DEBUG);
	
	// -- attributes --
	private Viewport viewport;
	private Stage stage;
	private Skin skin;

	Table jarsContainer; 
	File file;
	Array<File> subFiles = new Array<File>();
	
	Thread serverThread;
	ServerSocket serverSocket;
	
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

	private void init() {
		String path = "testjars/";
		file = new File(path);
//		file.createNewFile(); // no folder but openable file
		if(!file.exists())file.mkdirs();

		
		Table container = new Table(skin);
		container.setFillParent(true);
		container.center();
		
		Table downloadContainer = createDownloadContainer();
	
			
		jarsContainer = new Table(skin);
		jarsContainer.defaults().center();//.row();
		updateJarButtons();
		jarsContainer.setDebug(true);
		
		container.add(downloadContainer).padRight(20);
		container.add(jarsContainer);
		container.pack();
		
		stage.addActor(container);
	}
	
	private Table createDownloadContainer() {
		Table container = new Table();
		container.center();
		
		// -- on client --
		Array<String> adresses = new Array<String>();
		
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			for(NetworkInterface ni : Collections.list(interfaces)) {
				
				//InetAdress represents IP adress (we loop since we have IPv4 and IPv6 and more (?) for example)
				for(InetAddress adress : Collections.list(ni.getInetAddresses())) {
					
					if(adress instanceof Inet4Address) {
						// adding host ip adress in string format
						adresses.add(adress.getHostAddress());
					}
				}
				
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		String ipAdress = "";
		
		for(String s : adresses) {
			ipAdress = ipAdress + s + "\n";
		}
		
		
		Label clientDetails = new Label(ipAdress, skin);
		TextField targetIp = new TextField("Target IP",skin);
		Label msg = new Label("Waiting...", skin);
		TextButton downloadButton = new TextButton("Download Jar", skin);
		downloadButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(targetIp.getText() == null || targetIp.getText().isEmpty()) {
					log.error("targetIp empty");
					return;
				}
				

				File file = new File("C:\\Users\\sebas\\Documents\\Eclipse\\libgdx\\lwjgl3test workspace\\"
						+ "lwjgl3test project\\desktop\\build\\libs/desktop-1.0.jar");
				
				SocketHints hints = new SocketHints();
				// socket will time out after 10 sec
				hints.connectTimeout = 10000;

				Socket clientSocket = null;
				try {
					clientSocket = Gdx.net.newClientSocket(Protocol.TCP, targetIp.getText(), 1337, hints);
				}	catch(GdxRuntimeException e) {
					log.error("[Error] Wrong IP!");
					return;
				}
				
//				BufferedOutputStream out = new BufferedOutputStream(clientSocket.getOutputStream());
				DataOutput out = new DataOutput(clientSocket.getOutputStream());
				
				
				try {
//					clientSocket.getOutputStream().write(toBeSentMsg.getBytes());
//					clientSocket.getOutputStream().write(bytes);
					log.debug("[Client] Preparing a file to send...");
					long start = System.currentTimeMillis();
					
					
//					JarFile jar = new JarFile(file);
//					Manifest man = jar.getManifest();
//					JarOutputStream jarOut = new JarOutputStream(out);
//					long byteCount = Files.copy(file.toPath(), jarOut); // copies all bytes from the file to thge outputstream
//					jarOut.close(); // !!!!! else when reading the outputstream never -1 is returned
					
//					long byteCount = Files.copy(file.toPath(), out); // copies all bytes from the file to thge outputstream
//					out.close(); // !!!!! else when reading the outputstream never -1 is returned
//	
					
					String fileName = file.getName();
					byte[] fileNameBytes = fileName.getBytes();
					byte[] fyleContentBytes = new byte[(int)file.length()];
					
					out.writeInt(fileNameBytes.length);
					out.write(fileNameBytes);
					
					out.writeLong(fyleContentBytes.length);
					out.write(fyleContentBytes);
					
					out.flush();
					out.close();
					
					long end = System.currentTimeMillis() - start;
					log.debug("[Client]...done! "+ end + " ms to send " + file.length() + " bytes!");
					
				} catch (IOException e) {
					log.error("[Client - Error] Couldn't write to OutputStream!");
					try {
						log.error("[Client - Error] Outputstream is getting closed!");
						clientSocket.getOutputStream().close();
					} catch (IOException e1) {
						log.error("[Client - Error] Couldnt close outputstream!");
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
			}
		});
		
		// -- on server --
		
		serverThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				ServerSocketHints hints = new ServerSocketHints();
				// 0 means no time out, waits infinitly, else  means how long to wait during server.accept(). here 2sec
				hints.acceptTimeout = 0;
				
				serverSocket = Gdx.net.newServerSocket(Protocol.TCP, 1337, hints);
				
				// loop for continously checking for incoming messages
				while(true) {
					// accepts a new incoming connection from the client socket
					Socket socket = serverSocket.accept(null);
					log.debug("[Server] client socket accepted");
					
					// read data from socket into a buffer reader
//					BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//					BufferedInputStream bufferIn = new BufferedInputStream(socket.getInputStream()); // reads many bites at a time
//					DataInput in = new DataInput(bufferIn);
					DataInput in = new DataInput(socket.getInputStream());
					

					try {
						// read a a line of a text. a line must have "\n" to be seen as a line and read by readLine()
//						sentMessageLabel.setText(buffer.readLine());
						

						
						int size = file.listFiles().length;
						String name = "my-jar-copy.jar";
						File newFile = new File(file.getPath() + "/" + name +"(" + (size == 0 ? 0 : size + 1) + ")");
						long startTime = System.currentTimeMillis();
						log.debug("[Server] Receiving bytes...");
						
//						JarInputStream jarIn = new JarInputStream(in);
						
//						int fileNameLength = in.rea
						

						int count = 0;
						int read = 0;
						byte[] byteBuffer = new byte[1000];
						while((count = in.read(byteBuffer)) != -1) {
							read+= count;
							msg.setText("Downloaded " + read + " bytes [" + count +"]");
						}
						in.close();
						log.debug("[Server] Writing to File...");
						
						
						Files.write(newFile.toPath(), byteBuffer);
						long end = (System.currentTimeMillis() - startTime);
						log.debug("[Server]...done! " + end +" ms to receive ~" + read + " bytes!");
//						
						
						updateJarButtons();
					} catch (IOException e) {
						log.error("[Server-Error] Couldn't copy file!");
						try {
							socket.getInputStream().close();
							log.error("[Server-Error] Inputstream closed!");
						} catch (IOException e1) {
							e1.printStackTrace();
							log.error("[Server-Error] Couldn't close inputstream!");
						}
						e.printStackTrace();
					}
					socket.dispose();
				}
				
			}
			
		});
		serverThread.start();
		
		
		
		container.add(clientDetails).row();
		container.add(targetIp).row();
		container.add(msg).row();
		container.add(downloadButton).colspan(2).row();
//		container.add(sentMessageLabel);
	
		container.pack();
		return container;
	}
	
	
	
	private void updateJarButtons() {
		File[] files = file.listFiles();//listFiles(File::isFile);
		if(files == null)return;
		
		for (File f : files) {

			if (f != null && !subFiles.contains(f, true) && !containsFileName(f.getName())/*&& f.getName().contains(".jar")*/) {
				subFiles.add(f);
				TextButton jarButton = new TextButton(f.getName(), skin);
				jarButton.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						// f. // load jar
						log.debug("opening jar  " + f.getName() + "...");
					}
				});
				jarsContainer.add(jarButton).row();
			}
		}
		jarsContainer.pack();
	}
	
	private boolean containsFileName(String fileName) {
		for(File f : subFiles) {
			if(f.getName().equals(fileName))return true;
		}
		return false;
	}
	
	// -- other --
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
		serverSocket.dispose();
	}
	
	
	
}
