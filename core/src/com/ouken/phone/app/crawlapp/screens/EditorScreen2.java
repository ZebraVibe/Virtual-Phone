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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ouken.phone.app.crawlapp.CrawlApp;
import com.ouken.phone.app.crawlapp.assets.AssetDescriptors;
import com.ouken.phone.app.crawlapp.assets.RegionNames;
import com.ouken.phone.app.crawlapp.editor.Panel;
import com.ouken.phone.app.utils.AppScreenViewport;
import com.ouken.phone.utils.GdxUtils;
import com.ouken.phone.utils.actor.ColoredButton;
import com.ouken.phone.utils.color.ColorConverter;
import com.ouken.phone.utils.font.FontSize;
import com.ouken.phone.utils.font.Fonts;

public class EditorScreen2 extends ScreenAdapter {

	private static final Color BACKGROUND_COLOR = ColorConverter.hexToRGBA8888(0x454545);// 0x555555 , 0x3b3b3b
	private static final Color BUTTON_BG_COLOR = ColorConverter.hexToRGBA8888(0x454545);
	private static final Color BUTTON_BORDER_COLOR = ColorConverter.hexToRGBA8888(0x454545);

	private static final int EDITOR_PANEL_WIDTH = 400, EDITOR_PANEL_HEIGHT = 700;

	// -- attributes --
	private CrawlApp game;
	private AssetManager assets;
	private SpriteBatch batch;
	private ShapeRenderer renderer;
	private TextureAtlas gameplayAtlas, uiAtlas;
	private Texture canvasTexture;

	private Viewport viewportUnderlay, viewportCanvas, viewportOverlay;
	private Stage stageUnderlay, stageCanvas, stageOverlay;

	// -- constructor --

	public EditorScreen2(CrawlApp game) {
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

	private void init() {
		this.renderer = new ShapeRenderer();
		gameplayAtlas = assets.get(AssetDescriptors.GAMEPLAY);
		uiAtlas = assets.get(AssetDescriptors.EDITOR_UI);

		initStages();
		initCanvasTexture();
		initCanvas();

		initGameObjectPanel();

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

	private void initStages() {
		Vector2 tmp = new Vector2();

		viewportUnderlay = new AppScreenViewport(true);
		stageUnderlay = new Stage(viewportUnderlay, batch);
		Panel panel = new Panel(renderer).innerShadow(true);
		panel.setSize(EDITOR_PANEL_WIDTH, EDITOR_PANEL_HEIGHT);
		panel.setPosition(viewportUnderlay.getWorldWidth() / 2f, viewportUnderlay.getWorldHeight() / 2f, Align.center);
		panel.clip();
		stageUnderlay.setRoot(panel);

		viewportCanvas = new FitViewport(EDITOR_PANEL_WIDTH, EDITOR_PANEL_HEIGHT);// new AppScreenViewport(true);
		tmp.setZero();
		panel.localToScreenCoordinates(tmp);
		System.out.println(tmp);
		int screenX = (int) tmp.x;
		int screenY = (int) tmp.y;
//		viewportCanvas.setScreenBounds(screenX, screenY, EDITOR_PANEL_WIDTH / 2, EDITOR_PANEL_HEIGHT / 2);
		viewportCanvas.setScreenPosition((int) tmp.x, (int) tmp.y);
		stageCanvas = new Stage(viewportCanvas, batch);
		Table t = new Table();
		t.setSize(EDITOR_PANEL_WIDTH, EDITOR_PANEL_HEIGHT);
		stageCanvas.addActor(t);
//		stageCanvas.setDebugAll(true);

		viewportOverlay = new AppScreenViewport(true);
		stageOverlay = new Stage(viewportOverlay, batch);
		Table table = new Table();
		table.setSize(EDITOR_PANEL_WIDTH, EDITOR_PANEL_HEIGHT);
		table.setPosition(viewportOverlay.getWorldWidth() / 2f, viewportOverlay.getWorldHeight() / 2f, Align.center);
		table.clip();
		stageOverlay.setRoot(table);
		stageOverlay.setDebugAll(true);

		game.addProcessors(stageOverlay, stageCanvas);

	}

	private void initCanvas() {

		float sizeMultiplyer = 16;
		TextureRegion r = new TextureRegion(canvasTexture, 0, 0, canvasTexture.getWidth() * sizeMultiplyer,
				canvasTexture.getHeight() * sizeMultiplyer);
		Image img = new Image(r);
		float sizeScale = 4;
		img.setSize(img.getWidth() * sizeScale, img.getHeight() * sizeScale);
		img.setPosition(viewportCanvas.getWorldWidth() / 2f, viewportCanvas.getWorldHeight() / 2f, Align.center);
		stageCanvas.addActor(img);

	};

	private void initGameObjectPanel() {
		float panelWidth = 128 * 3;
		float panelHeight = 128;

		Panel panel = new Panel(renderer).bg(BACKGROUND_COLOR.cpy());
		panel.setTransform(true);
		panel.clip();

		float categoriesWidth = panelWidth / 3f;
		Table categories = new Table();
		categories.clip();
		categories.add(createCategories()).center();

		Panel gameObjects = new Panel(renderer).bg(null);
		gameObjects.clip();

		panel.add(categories).size(categoriesWidth, panelHeight);
		panel.add(gameObjects).size(panelWidth - categoriesWidth, panelHeight);
		panel.pack();

		float gap = (stageOverlay.getRoot().getWidth() - panel.getWidth()) / 2f;
		panel.setPosition(stageOverlay.getRoot().getWidth() / 2f, gap, Align.bottom);

		stageOverlay.addActor(panel);
	}

	private Actor createCategories() {
		Table content = new Table();
		content.setFillParent(true);
		content.defaults().space(5);

		// create categories
		Array<String> names = new Array<String>();
		for (AtlasRegion r : gameplayAtlas.getRegions()) {
			String sub = r.name.contains("_") ? r.name.split("_")[0] : r.name;
			if (!names.contains(sub, false)) {
				names.add(sub);
			}
		}

		// add categories and sort in regions
		for (String name : names) {
			Array<TextureRegion> regions = new Array<TextureRegion>();

			for (AtlasRegion r : gameplayAtlas.getRegions()) {
				if (r.name.contains(name))
					regions.add(r);
			}

			Panel category = new Panel(renderer).bg(BUTTON_BG_COLOR).border(BUTTON_BORDER_COLOR);
			category.setName(name);
			float width = 32, height = 16;
			category.setSize(width, height);
			category.center();

			Image img = new Image(regions.first());

			LabelStyle style = new LabelStyle();
			style.font = Fonts.getDefaultFontBySize(FontSize.x12);
			Label label = new Label(name, style);

			category.add(img).size(12).spaceRight(3);
			category.add(label);
			content.add(category).row();
		}
		content.pack();

		ScrollPane scroll = new ScrollPane(content);
		scroll.setFillParent(true);
		scroll.setFlickScroll(false);
		return scroll;
	}

	// -- render --

	@Override
	public void render(float delta) {
		GdxUtils.clearScreen(BACKGROUND_COLOR);

		updateDebugInput();

		viewportUnderlay.apply();
		stageUnderlay.act();
		stageUnderlay.draw();

		viewportCanvas.apply();
		stageCanvas.act();
		stageCanvas.draw();

		viewportOverlay.apply();
		stageOverlay.act();
		stageOverlay.draw();
	}

	// -- resize and dispose --

	private void updateDebugInput() {
		OrthographicCamera cam = (OrthographicCamera) stageCanvas.getCamera();
		float zoomBy = 0.125f;
		float camZoom = cam.zoom;
		float minZoom = 0.125f;
		float maxZoom = 4;
		if (Gdx.input.isKeyJustPressed(Keys.P)) {
			if (camZoom < maxZoom) {
				cam.zoom += zoomBy;
				cam.update();
			}
		} else if (Gdx.input.isKeyJustPressed(Keys.M)) {
			if (camZoom > minZoom) {
				cam.zoom -= zoomBy;
				cam.update();
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		viewportUnderlay.update(width, height, true);
		viewportCanvas.update(width, height, true);
		viewportOverlay.update(width, height, true);
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		stageUnderlay.dispose();
		stageCanvas.dispose();
		stageOverlay.dispose();
		canvasTexture.dispose();
		renderer.dispose();
	}

}
