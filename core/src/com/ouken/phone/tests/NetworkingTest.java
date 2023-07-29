package com.ouken.phone.tests;

import java.io.BufferedReader;
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
 * [socket example]
 * https://gamefromscratch.com/libgdx-tutorial-10-basic-networking/
 * 
 * [sockets example yt]
 * https://www.youtube.com/watch?v=OMbtBzmk650
 * @author sebas
 *
 *[sockets example forum]
 *https://coderanch.com/t/556838/java/Transferring-file-file-data-socket
 *
 */
public class NetworkingTest extends ApplicationAdapter{
	
	// -- constants --
	public static final int WIDTH = 720, HEIGHT = 480; // pxl
	public static final float WORLD_WIDTH = WIDTH, WORLD_HEIGHT = HEIGHT; // world units 
	private static final Logger log = new Logger(NetworkingTest.class.getName(), Logger.DEBUG);
	
	// -- attributes --
	private Viewport viewport;
	private Stage stage;
	private Skin skin;
	
	
	private Thread serverThread;
	private ServerSocket serverSocket;
	
	// -- init --
	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
		stage = new Stage(viewport);
		skin = new Skin(Gdx.files.internal("default/uiskin.json"));
		
		Gdx.input.setInputProcessor(stage);
		
		init();
	}
	
	/*
	 * Idea: client writes msg to server. Server continously loops through all interfaces 
	 * & displays a message on screen when receiving one. (server not headless since server is my laptop which ahs a monitor)
	 * */
	private void init() {
		Table container = new Table();
		container.center();
		
		// -- on client --
		Array<String> adresses = new Array<String>();
		
		// We loop through all interfaces found on this machine / device, since
		// this device can have multiple interfaces: one per NIC (pro lan adapter), one per wireless, 
		// one loopback (should be (mostly )always available -> used to allow communication between entities on this machine)
		// In Network settings one can see that this device has IPv6 and IPv4 ip-adresses - since IPv4 is often used we search fo ipv4 adresses
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
		TextField msg = new TextField("msg sent to server", skin);
		TextButton send = new TextButton("Send", skin);
		Label sentMessageLabel = new Label("Waiting for message...", skin);
		
		
		// -- on server --
		
		serverThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				ServerSocketHints hints = new ServerSocketHints();
				// 0 means no time out, waits infinitly, else  means how long to wait during server.accept(). here 2sec
				hints.acceptTimeout = 0;
				
				// creating socket server using tcp protocl and listening to 1337 port. ip is like a street adress and port the apartment number
				 // Only one app(applicationlistener) can listen to a port at a time, keep in mind many ports are reserved
                // especially in the lower numbers ( like 21, 80, etc ). Port values go from 1 to 65,536 (but several are reserved, but mostly >100)
				// i.e. 80 for HTTP and 21 for FTP
				// My machine can have multiple adresses, 1 per internet adapter and possibly even more, especially when rnning a VM wirtualizationmachine
				// The value 127.0.0.1 is the loop back adress / loopback adapter, that points back at itself
				serverSocket = Gdx.net.newServerSocket(Protocol.TCP, 1337, hints);
				
				// loop for continously checking for incoming messages
				while(true) {
					// accepts a new incoming connection from the client socket
					Socket socket = serverSocket.accept(null);
					
					
					// read data from socket into a buffer reader
					BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					
					
					try {
						// read a a line of a text. a line must have "\n" to be seen as a line and read by readLine()
						sentMessageLabel.setText(buffer.readLine());
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					socket.dispose();
				}
				
			}
			
		});
		serverThread.start();
		
		// -- on client --

		send.addListener(new ClickListener() {
	
			@Override
			public void clicked(InputEvent event, float x, float y) {
				 // +"\n" important for using .readLine() properly 
				String toBeSentMsg = msg.getText().length() == 0 ? "Hello from Client\n" : msg.getText() + "\n"; // !! "+\n"
				
				
				//ip = internet protocol, tcp = transmission control protocol, http = hypter text transfer protocol
				SocketHints hints = new SocketHints();
				// socket will time out after 10 sec
				hints.connectTimeout = 10000;
				
				// creates a socket using tco protol and uses port 1337 on the target ip,
				Socket clientSocket = Gdx.net.newClientSocket(Protocol.TCP, targetIp.getText(), 1337, hints);
				
				// TODO: streaming a texture
//				Texture tex = null;
//				TextureData data = tex.getTextureData();
//				if (!data.isPrepared()) data.prepare();
//				Pixmap map = data.consumePixmap();
//				//map.getPixel(x, y) //  for regions
//				ByteBuffer byteBuffer = map.getPixels();
//				byte[] bytes = byteBuffer.array();
//				map.dispose();
//				
				
				// write entered message to the stream
				try {
					clientSocket.getOutputStream().write(toBeSentMsg.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		
		
		container.add(clientDetails).row();
		container.add(targetIp).row();
		container.add(msg).row();
		container.add(send).colspan(2).row();
		container.add(sentMessageLabel);
		
		container.setFillParent(true);
		container.pack();
		stage.addActor(container);
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
		serverSocket.dispose();
	}
	
	
	
}
