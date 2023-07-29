package com.ouken.phone.app.crawlapp.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ouken.phone.app.crawlapp.CrawlApp;
import com.ouken.phone.app.crawlapp.assets.AssetDescriptors;
import com.ouken.phone.app.crawlapp.assets.RegionNames;
import com.ouken.phone.app.crawlapp.editor.Panel;
import com.ouken.phone.app.utils.AppScreenViewport;
import com.ouken.phone.utils.GdxUtils;
import com.ouken.phone.utils.actor.ColoredButton;
import com.ouken.phone.utils.color.ColorConverter;

public class EditorScreen extends ScreenAdapter {

	private static final Color BACKGROUND_COLOR = ColorConverter.hexToRGBA8888(0x454545);

	private static final float EDITOR_PANEL_WIDTH = 400, EDITOR_PANEL_HEIGHT = 700;

	// -- attributes --
	private CrawlApp game;
	private AssetManager assets;
	private SpriteBatch batch;
	private ShapeRenderer renderer;
	private TextureAtlas gameplayAtlas, uiAtlas;
	private Texture canvasTexture;

	private Viewport viewport;
	private Stage stage;

	private Table canvas;

	// -- constructor --

	public EditorScreen(CrawlApp game) {
		this.game = game;
		this.assets = game.getAssets();
		this.batch = game.getBatch();
	}

	// -- init --
	@Override
	public void show() {
		game.log().debug("show EditorScreen");
		init();
	}

	// -- render --

	private void init() {
		this.renderer = new ShapeRenderer();
		gameplayAtlas = assets.get(AssetDescriptors.GAMEPLAY);
		uiAtlas = assets.get(AssetDescriptors.EDITOR_UI);

		viewport = new AppScreenViewport(true);
		stage = new Stage(viewport, batch);

		game.addProcessors(stage);

		initCanvasTexture();

		// ----
		initEditorPanel();
		initCanvas();

	}

	private void initCanvasTexture() {
		AtlasRegion region = uiAtlas.findRegion(RegionNames.EDITOR_BACKGROUND_UI);
		TextureData data = region.getTexture().getTextureData();
		if (!data.isPrepared())
			data.prepare();
		Pixmap atlasPixmap = data.consumePixmap();

		Pixmap newPixmap = new Pixmap(region.getRegionWidth(), region.getRegionHeight(), Pixmap.Format.RGBA8888);
		newPixmap.drawPixmap(atlasPixmap, 0, 0, region.getRegionX(), region.getRegionY(), region.getRegionWidth(),
				region.getRegionHeight());

		canvasTexture = new Texture(newPixmap);
		canvasTexture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		atlasPixmap.dispose();
		newPixmap.dispose();
	}

	private void initEditorPanel() {
		// create stage with panel as root
		Panel editorPanel = new Panel(renderer).innerShadow(true);
		float w = EDITOR_PANEL_WIDTH;
		float h =  EDITOR_PANEL_HEIGHT;
		editorPanel.setSize(w, h);
		editorPanel.setPosition(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 2f, Align.center);
		stage.setRoot(editorPanel);
	}

	private void initCanvas() {
		float w = stage.getRoot().getWidth();
		float h =  stage.getRoot().getHeight();
		int reps = 16; // repetitions
		float scale = 4;

		TextureRegion r = new TextureRegion(canvasTexture, 0, 0, canvasTexture.getWidth() * reps,
				canvasTexture.getHeight() * reps);
		Image img = new Image(r);
		Image img2 = new Image(r);
//		img.setSize(img.getWidth() * scale, img.getHeight() * scale);
		
		canvas = new Panel(renderer).bg(Color.RED.cpy()).shadow(true);
		canvas.setTransform(true);
		canvas.defaults().size(5);
		canvas.add(img).size(img.getWidth() * scale, img.getHeight() * scale).row();
		canvas.add(img2).size(img.getWidth() * scale, img.getHeight() * scale);
		canvas.pack();
		canvas.setPosition(w / 2f, h / 2f, Align.center);

		initCanvasListeners();

		stage.addActor(canvas);
		((Table) stage.getRoot()).clip();
//		stage.setDebugAll(true);

	}

	private void initCanvasListeners() {
		// canvas move
		canvas.addListener(new InputListener() {
			
			Vector2 fixed = new Vector2(), tmp = new Vector2();
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return Gdx.input.isKeyPressed(Keys.SPACE);
			}
			
			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {
				boolean isSpaceDown = Gdx.input.isKeyPressed(Keys.SPACE);
				boolean isSpaceJustDown = Gdx.input.isKeyJustPressed(Keys.SPACE);

				System.out.println("x= " + x + "y= " +y);
			
				
				if (!isSpaceDown)
					return;
				else if (isSpaceJustDown) {
					fixed.set(x, y); // local
				}     

			}

		});
		
		
		// canvas zoom
		canvas.addListener(new InputListener() {

			Vector2 tmp = new Vector2();

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				stage.setScrollFocus(canvas);
			}

			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				stage.setScrollFocus(null);
			}

			@Override
			public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
				if (event.getTarget() != canvas)
					return false;
				boolean isCTRLPressed = Gdx.input.isKeyPressed(Keys.CONTROL_LEFT);

				float zoom = 0.25f;
				float maxZoom = 5;
				float minZoom = 0.25f;

				if (isCTRLPressed) {

					Actor target = event.getTarget();
					float oldX = target.getOriginX(), oldY = target.getOriginY();
					target.setOrigin(x, y);

					// zoom in
					if (amountY < 0) {
						if (target.getScaleX() >= 1)
							target.scaleBy(-zoom);
						else if (target.getScaleX() > minZoom)
							target.scaleBy(-zoom / 2f);
					}
					// zoom out
					else if (amountY > 0 && target.getScaleX() <= maxZoom) {
						if (target.getScaleX() >= 1)
							target.scaleBy(zoom);
						else if (target.getScaleX() < 1)
							target.scaleBy(zoom / 2f);
					}
					

				}
				return true;
			}

		});

	}
	
	

	// -- render --

	@Override
	public void render(float delta) {
		GdxUtils.clearScreen(BACKGROUND_COLOR);

		updateDebugInput();
		updateInput();

		viewport.apply();
		stage.act();
		stage.draw();
	}

	// -- private methods --
	
	private void updateInput() {
		// canvas dragg
		if(Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			stage.touchDown(Gdx.input.getX(), Gdx.input.getY(), 0, Buttons.LEFT);
		}
		if(Gdx.input.isKeyPressed(Keys.SPACE)) {
			stage.touchDragged(Gdx.input.getX(), Gdx.input.getY(), 0);
		}
	}

	// -- debug --

	private void updateDebugInput() {

	}

	// -- resize and dispose --

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		renderer.dispose();
		stage.dispose();
		canvasTexture.dispose();
	}

	// -- old --

	private void initEntitySelectBar() {
		Panel container = new Panel(renderer).shadow(true);

		float containerWidth = 256;
		float containerHeight = 64;
		container.setTouchable(Touchable.childrenOnly);
		container.setSize(containerWidth, containerHeight);

		Table content = new Table();
//		content.setDebug(true);
		content.defaults().padLeft(5).padRight(5).maxHeight(containerHeight);

		for (AtlasRegion region : gameplayAtlas.getRegions()) {
			Actor a = pickEntityButton(region);
			content.add(a);
		}
		content.pack();

		ScrollPane pane = new ScrollPane(content);
		pane.setFillParent(true);
		pane.setFlickScroll(false);
		pane.setSmoothScrolling(false);
		pane.addListener(new InputListener() {

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//				overlayStage.setScrollFocus(pane);
			}

			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
//				overlayStage.setScrollFocus(null);
			}
		});

		container.add(pane);
		container.setPosition(viewport.getWorldWidth() / 2f, viewport.getWorldHeight(), Align.top);
//		overlayStage.addActor(container);

//		Actor a = new TabPanel(renderer);
////		a.setDebug(true);
//		a.setPosition(overlayViewport.getWorldWidth() / 2f, overlayViewport.getWorldHeight() / 2f, Align.center);
//		overlayStage.addActor(a);
	}

	private Array<Actor> sortEntitiesByName() {
		Array<Actor> tmp = new Array<>();
		String last = "";

		for (AtlasRegion region : gameplayAtlas.getRegions()) {

			String stmp = "" + region.name;
			stmp = stmp.contains("_") ? stmp.split("_")[0] : stmp;

			if (!stmp.equals(last)) {
				last = stmp;
			}

			Actor a = pickEntityButton(region);
			tmp.add(a);
		}

		return tmp;
	}

	private Actor pickEntityButton(TextureRegion region) {
		ColoredButton button = new ColoredButton(region, Color.DARK_GRAY.cpy());

		button.addListener(new InputListener() {

		});

		return button;
	}

}
