package com.ouken.phone.app.appstoreapp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.PixmapIO.PNG;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ouken.phone.Phone;
import com.ouken.phone.app.App;
import com.ouken.phone.app.AppInfo;
import com.ouken.phone.app.utils.AppScreenViewport;
import com.ouken.phone.assets.AssetDescriptors;
import com.ouken.phone.assets.RegionNames;
import com.ouken.phone.config.Config;
import com.ouken.phone.server.AppStoreServer;
import com.ouken.phone.utils.GdxUtils;
import com.ouken.phone.utils.font.FontSize;
import com.ouken.phone.utils.font.Fonts;

@AppInfo(name = "App Store", authors = "ouken11", version = "0.0")
public class AppStoreApp extends App{
	
	private static final Logger log = new Logger(AppStoreApp.class.getName(), Logger.DEBUG);
	
	// -- constants --
	private static Color BG_COLOR = Color.LIGHT_GRAY.cpy();
	
	// -- attributes --
	private Viewport viewport;
	private Stage stage;
	private Skin skin;
	
	private ArrayMap<String, FileHandle> iconFiles = new ArrayMap<String, FileHandle>();
	private Array<Texture> iconTextures = new Array<Texture>();
	
	// -- constructors --
	public AppStoreApp(SpriteBatch batch) {
		super(batch);
	}
	
	
	// -- init --
	@Override
	public void create() {
		viewport = new AppScreenViewport();
		stage = new Stage(viewport, getBatch());
	
		addProcessors(stage);
		
		skin = Phone.INSTANCE.getAssets().get(AssetDescriptors.DEFAULT_SKIN);
	
		
//		initTest3();
//		init();
		
	}
	
	private void init() {
		SocketHints hints = new SocketHints();
		hints.connectTimeout = 10000;
		
		Socket clientSocket = null;
		
		try {
		clientSocket = Gdx.net.newClientSocket(Protocol.TCP, 
				AppStoreServer.IP, AppStoreServer.PORT, hints);
		} catch(GdxRuntimeException e) {
			errorClient("Connection Failed: Wrong IP!");
			return;
		}
		
		BufferedOutputStream toServer = new BufferedOutputStream(clientSocket.getOutputStream());
		 
		DataInputStream fromServerData = new DataInputStream(clientSocket.getInputStream());
			
		
		try {

			int appCount = fromServerData.readInt();
			debugClient("Expected Apps to be loaded: " + appCount);
			for (int i = 0; i < appCount; i++) {

				// sapp name bytes
				int bytes0Length = fromServerData.readInt();
				byte[] bytes0 = new byte[bytes0Length];
				fromServerData.readFully(bytes0); // readFully reads exactly up to b length bytes, read() up to b length() maybe less, depends if available in the inputstream
				String name = new String(bytes0);
				
				debugClient("App loaded: " + name);

				// texture bytes
				int byteArrayLength = fromServerData.readInt();
				byte[] bytes = new byte[byteArrayLength];
				while (fromServerData.read(bytes, 0, bytes.length) != -1);
				
				debugClient("Done reading byteArray of length: " + bytes.length);
				
				// image file
				
				FileHandle tmp = FileHandle.tempFile("tempapp" + i);
				tmp.writeBytes(bytes, false);
				
				iconFiles.put(name, tmp);
				

			}
			fromServerData.close();

		} catch (IOException e) {
			errorClient("Couldn't read server bytes!");
			e.printStackTrace();

			try {
				clientSocket.getOutputStream().close();
				clientSocket.getInputStream().close();
			} catch (Exception e1) {
				errorClient("Couldn't close streams. Maybe already closed(?)");
				e1.printStackTrace();
			}

		}
		
		clientSocket.dispose();
		
		
		

		Table table = new Table();
		float pad = 10;
		table.defaults().pad(pad).top().center();
		table.setFillParent(true);
		
		for (Entry<String, FileHandle> e : iconFiles) {
			Texture tex = new Texture(e.value);
			iconTextures.add(tex);

			Table t = new Table();
			t.defaults().padBottom(5);
			t.pad(5);
			
			Image img = new Image(tex);
			LabelStyle style = new LabelStyle();
			style.font = Fonts.getDefaultFontBySize(FontSize.x12);
			Label label = new Label(e.key, style);
			
			t.add(img).size(Config.APP_ICON_HEIGHT).row();
			t.add(label);
			
			t.pack();
			table.add(t);

		}
		table.pack();
		ScrollPane scroll = new ScrollPane(table);
		scroll.setFillParent(true);
		scroll.setFlickScroll(false);
		stage.setScrollFocus(scroll);
		stage.addActor(table);
		
		
	}

	
	private void initTest3() {
		FileHandle fh = Gdx.files.internal("appicons/app_mask.png");
		Texture t = new Texture(fh);
		TextureData data = t.getTextureData();
		if(!data.isPrepared())data.prepare();
		Pixmap pm = data.consumePixmap();
		ByteBuffer bb = pm.getPixels();
		byte[] bytes0 = new byte[bb.remaining()];
		bb.get(bytes0);
		
		pm.dispose();
		t.dispose();
		
		
		// stream bytes to client
	
		
		Pixmap pixmap = new Pixmap((int)viewport.getWorldWidth(), (int)viewport.getWorldHeight(), Pixmap.Format.RGBA8888);
		ByteBuffer byteBuffer = pixmap.getPixels();
		byteBuffer.clear();
		byteBuffer.put(bytes0, 0, bytes0.length);
		
		
		Texture tex = new Texture(pixmap);
		Image img = new Image(tex);
		pixmap.dispose();
		
		stage.addActor(img);
		iconTextures.add(tex);
	}
	
	

	
	@Override
	public void pause() {
		
	}
	
	@Override
	public void resume() {
		
	}
	
	
	// -- render --
	
	@Override
	public void render() {
		GdxUtils.clearScreen(BG_COLOR);
		
		viewport.apply();
		stage.act();
		stage.draw();
	}
	
	// -- logic --
	
	
	
	// -- other --
	@Override
	public void resize(int width, int height) { 
		viewport.update(width, height, true);
	}
	
	@Override
	public void dispose() {	
		stage.dispose();
		iconTextures.forEach(icon -> icon.dispose() );
		iconFiles.forEach(e -> e.value.delete());
	}
	
	private void debugClient(String message) {
		log.debug("[Client]: " + message);
	}
	
	private void errorClient(String message) {
		log.error("[Client]: " + message);
	}
	
}
