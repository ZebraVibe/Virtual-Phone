package com.ouken.phone.screens;

import java.io.File;
import java.lang.annotation.Annotation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ouken.phone.Phone;
import com.ouken.phone.app.App;
import com.ouken.phone.app.AppInfo;
import com.ouken.phone.app.utils.AppScreenViewport;
import com.ouken.phone.assets.AssetDescriptors;
import com.ouken.phone.assets.RegionNames;
import com.ouken.phone.common.PhoneManager;
import com.ouken.phone.config.Config;
import com.ouken.phone.utils.GdxUtils;
import com.ouken.phone.utils.actor.ColoredButton;
import com.ouken.phone.utils.font.FontSize;
import com.ouken.phone.utils.font.Fonts;
import com.ouken.phone.utils.shader.Shaders;

public class HomeScreen extends ScreenAdapter {

	// -- constants --
	public static final HomeScreen INSTANCE = new HomeScreen();
	private static final Logger log = new Logger(HomeScreen.class.getName(), Logger.DEBUG);

	private static final float PAGE_WIDTH = Config.SCREEN_RECT_WIDTH, PAGE_HEIGHT = Config.SCREEN_RECT_HEIGHT;
	private static final float PAGE_GAP = Config.APP_ICON_WIDTH;

	private static final int MAX_APP_ICON_LABEL_LENGTH = 12;
	
	// -- attributes --
	private final Phone phone;
	private final SpriteBatch batch;
	private final AssetManager assets;
	private Viewport viewport;
	private Stage stage;
	private ShapeRenderer renderer;
	private boolean hasInit;

	private ShaderProgram appIconStencilMaskShader, appIconAlphaMaskShader;

	private TextureRegion appIconMaskRegion, appIconShadowRegion, pageIndicatorRegion, pageIndicatorSelectedRegion,
			pageNavArrowLeftRegion, pageNavArrowLeftSelectedRegion, pageNavArrowRightRegion,
			pageNavArrowRightSelectedRegion,
			pageNavBackgroundCircleLeft,pageNavBackgroundCircleRight, pageNavBackgroundCenter;
	private TextureRegionDrawable appIconShadowDrawable;
	private TextureAtlas appIconsAtlas;

	private ArrayMap<String, Texture> iconTextures = new ArrayMap<String, Texture>();

	
	private final PhoneManager manager = PhoneManager.INSTANCE;
	private Table allPagesContainer;
	private final ButtonGroup<Button> pageIndicatorsButtonGroup = new ButtonGroup<>();
	private Table pageIndicators;
	private Table pageNavArea;
	private Actor animationActor;
	private Vector2 tmp = new Vector2();
	private int totalPages;
	private int currentPage;

	// -- constructors --
	private HomeScreen() {
		this.phone = Phone.INSTANCE;
		batch = phone.getBatch();
		assets = phone.getAssets();
	}

	// -- init --
	@Override
	public void show() {
		if (!hasInit) {
			viewport = new AppScreenViewport();
			stage = new Stage(viewport, batch);
			renderer = new ShapeRenderer();

			init();
			hasInit = true;
		}
		Phone.INSTANCE.addProcessor(stage);
	}

	private void init() {
		initAssets();

		updatePages();
		
		createAnimationActor();
	}

	private void initAssets() {
		// app icon
		appIconsAtlas = assets.get(AssetDescriptors.APP_ICONS);
		TextureAtlas phoneAtlas = assets.get(AssetDescriptors.PHONE);
		
		appIconStencilMaskShader = Shaders.createDiscardTranparentPixelsShader();
		appIconAlphaMaskShader = Shaders.createAlphaMaskWithSecondTextureShader();
		appIconMaskRegion = phoneAtlas.findRegion(RegionNames.APP_MASK);
		appIconShadowRegion = phoneAtlas.findRegion(RegionNames.APP_SHADOW);
		appIconShadowDrawable = new TextureRegionDrawable(appIconShadowRegion);
		
		// page navigation
		TextureRegion pageNav = phoneAtlas.findRegion(RegionNames.PAGE_NAVIGATION);
		pageIndicatorSelectedRegion = new TextureRegion(pageNav, 0, 0, 12, 12);
		pageIndicatorRegion = new TextureRegion(pageNav, 12, 0, 12, 12);
		pageNavArrowLeftRegion = new TextureRegion(pageNav, 0, 16, 15, 26);
		pageNavArrowLeftSelectedRegion = new TextureRegion(pageNav, 16, 16, 19, 30);

		pageNavArrowRightRegion = new TextureRegion(pageNavArrowLeftRegion);
		pageNavArrowRightRegion.flip(true, false);
		pageNavArrowRightSelectedRegion = new TextureRegion(pageNavArrowLeftSelectedRegion);
		pageNavArrowRightSelectedRegion.flip(true, false);
		
		pageNavBackgroundCircleLeft = new TextureRegion(pageNav,0,48,15,30);
		pageNavBackgroundCenter = new TextureRegion(pageNav, 16, 48, 1, 30);
		pageNavBackgroundCircleRight = new TextureRegion(pageNav, 18, 48, 15, 30);
		
		
	
		
	}

	
	
	// -- public methdos --
	
	
	
	/** can be used to reinitilize the page contents */
	public void updatePages() {
		// init apps
		Array<Class<? extends App>> apps = PhoneManager.INSTANCE.getInstalledAppClasses();

		int maxAppsPerPage = Config.MAX_APPS_PER_PAGE;
		totalPages = apps.size == 0 ? 1 : MathUtils.ceil(apps.size / (float) maxAppsPerPage);

		// clear
		if(allPagesContainer == null) {
			allPagesContainer = new Table();
			stage.addActor(allPagesContainer);
		}
		
		allPagesContainer.clearChildren(false);
		allPagesContainer.defaults().top().left().padRight(PAGE_GAP);

		// place apps into their pages
		for (int i = 0; i < totalPages; i++) {
			
			float pad = (PAGE_WIDTH - Config.MAX_APP_ICONS_PER_ROW * Config.APP_ICON_WIDTH) / (float) (2 * Config.MAX_APP_ICONS_PER_ROW);

			Table page = new Table();
//			page.setDebug(true);
			page.defaults().pad(pad);
			page.left().top();
			page.setSize(PAGE_WIDTH, PAGE_HEIGHT);

			int col = 0;
			for (int k = i * maxAppsPerPage; k < apps.size && k < (i + 1) * maxAppsPerPage; k++) {
				Class<? extends App> app = apps.get(k);

				if (app == null)
					continue;

				Actor icon = createAppIcon(app);
				String name = getFormatedAppName(app);
				
				FontSize fontSize = FontSize.x12;
				float labelGap = 8;
				LabelStyle style = new LabelStyle(Fonts.getDefaultFontBySize(fontSize), Color.WHITE.cpy());
				Label nameLabel = new Label(name,style);
				
				Table table = new Table();
				table.add(icon).size(Config.APP_ICON_SIZE);
				table.pack();
				table.addActor(nameLabel);
				nameLabel.setPosition(table.getWidth() / 2f, - (labelGap + fontSize.toInt()), Align.bottom);
				
				page.add(table);

				
				col %= 3;
				col++;

				if (col == 3)
					page.row();
			}
			allPagesContainer.add(page);
		}
		allPagesContainer.pack();
		allPagesContainer.setPosition(0, viewport.getWorldHeight() - Config.FRAME_CORNER_INNER_RADIUS, Align.topLeft);
		
		updatePageNavigation();

	}
	
	public String getFormatedAppName(Class<? extends App> appClass, int maxLength) {
		AppInfo info = App.getAppInfoFrom(appClass);
		String name = info != null ? info.name() : "Unnamed";
		int max = maxLength;

		if(name.length() > max) {
			name = name.substring(0, max - 3) + "...";
		}
		return name;
	}
	
	/**Returns a String with the max length of {@link HomeScreen#MAX_APP_ICON_LABEL_LENGTH}*/
	public String getFormatedAppName(Class<? extends App> appClass) {
		return getFormatedAppName(appClass, MAX_APP_ICON_LABEL_LENGTH);
	}

	
	public Actor createAppIcon(Class<? extends App> appClass) {
		
		TextureRegion region = null;
		
		// even native apps are made sure that they have their own folders
		FileHandle iconPNG = PhoneManager.INSTANCE.getIconPNGFileFromAppFolderOf(appClass);
		
		if (iconPNG != null && iconPNG.exists()) {
			if (!iconTextures.containsKey(appClass.getSimpleName())) {
				Texture tex = new Texture(iconPNG);
				iconTextures.put(appClass.getSimpleName(), tex);
				region = new TextureRegion(tex);
				
			} else {
				region = new TextureRegion(iconTextures.get(appClass.getSimpleName()));
			}

		}else {
			log.debug("Loading app icon of: " + appClass.getSimpleName());
			region = getInternalAppIconIfAvailable(appClass);
			if(region == null)region = appIconMaskRegion;
		}
		
		
		Image icon = new Image(region) {

			TextureRegionDrawable mask = new TextureRegionDrawable(appIconMaskRegion);
			TextureRegionDrawable tex = (TextureRegionDrawable)getDrawable();
	
			
			@Override
			public void draw(Batch batch, float parentAlpha) {
				drawShadow(batch, parentAlpha);
				drawStencil(batch, parentAlpha);
//				drawBlend(batch, parentAlpha);

			}

			private void drawShadow(Batch batch, float parentAlpha) {
				batch.flush();
				float xOff = 3, yOff = 4;
				xOff *= getWidth() / Config.APP_ICON_WIDTH;
				yOff *= getHeight() / Config.APP_ICON_HEIGHT;
				float xRatio = appIconShadowRegion.getRegionWidth() / Config.APP_ICON_WIDTH;
				float yRatio = appIconShadowRegion.getRegionHeight() / Config.APP_ICON_HEIGHT;
				
				batch.draw(appIconShadowRegion, getX() - xOff, getY() - yOff, getWidth() * xRatio, getHeight() * yRatio);
			}

			private void drawStencil(Batch batch, float parentAlpha) {
				batch.flush();
				//stencil
				Gdx.gl.glEnable(GL20.GL_STENCIL_TEST);// tells gl to use our given stencil properties when									// rendering/flushing
				Gdx.gl.glStencilMask(0xFF); // allows writing to the buffer
				Gdx.gl.glStencilFunc(GL20.GL_ALWAYS, 1, 0xFF); // always - pass the test - & have refnumber = 1
				Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_REPLACE, GL20.GL_REPLACE); // when passing stencil test: replace buffer value with ref value = 1
				Gdx.gl.glColorMask(false, false, false, false);
		
				batch.setShader(appIconStencilMaskShader);
				setDrawable(mask);
				super.draw(batch, parentAlpha);
				batch.flush();


				Gdx.gl.glColorMask(true, true, true, true);
				Gdx.gl.glStencilMask(0x00); // denies writing to buffer
				Gdx.gl.glStencilFunc(GL20.GL_EQUAL, 1, 0xFF);// if buffer val equal to ref val 1 - pass the test (from above stencilOp: after passing we replace( we dont  write to buffer NOW but we draw the pixel))
				
				// draw icon
//				batch.setShader(appIconAlphaMaskShader);
//				setupShader();
				batch.setShader(null);
				setDrawable(tex);
				super.draw(batch, parentAlpha);
				batch.flush();
				
//				cleanUpShader();
				
				batch.setShader(null);
				Gdx.gl.glDisable(GL20.GL_STENCIL_TEST);

			}
			
			
			private void setupShader() {
				// shader must be bound for this to work:
				
				// unit 0 is actually set & overwritten in .draw()
				// enable receiving mask texture region coordinates
				ShaderProgram shader = batch.getShader();
//				shader.setVertexAttribute(
//						ShaderProgram.TEXCOORD_ATTRIBUTE + "1", 
//						2, GL20.GL_FLOAT, false, 0, 0);
//				
				
				//1.) activates texture unit(location value) first before binding, 
				//2.) binds this texture to currently active texture unit(=location value)
				tex.getRegion().getTexture().bind(0);
				mask.getRegion().getTexture().bind(1);
				//3.) assigning unit(=texture location value) to texture sampler 
				// (=telling opengl which texture location belongs to which shader sampler)
				shader.setUniformi(Shaders.U_TEXTURE, 0);
				shader.setUniformi(Shaders.U_MASK_TEXTURE, 1);
				
				// if the next render call has only one texture in its shader - the second texture unit cant get assigned and should be ignored
		
			}
			
 			private void cleanUpShader() {
				mask.getRegion().getTexture().bind(0);
				batch.getShader().setUniformi(Shaders.U_MASK_TEXTURE, 0);
			}
			
			
			private void drawBlend(Batch batch, float parentAlpha) {
				batch.flush();
				
				//blend
				Gdx.gl.glEnable(GL20.GL_BLEND);
				Gdx.gl.glColorMask(true, true, true, true);
//				Gdx.gl.glColorMask(false, false, false, true);
				Gdx.gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_SRC_ALPHA);// cf = cs * sf + cd * df
				
//				setDrawable(mask);
//				pack(); // size drawable to pref size (for larger drawables)
//				super.draw(batch, parentAlpha);
				batch.draw(mask.getRegion(), getX(), getY(), getWidth(), getHeight());
				batch.flush();
				
//				Gdx.gl.glColorMask(true, true, true, true);
//				Gdx.gl.glBlendFunc(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
////				Gdx.gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_ONE);
////				setDrawable(tex);
////				pack();// size drawable to pref size (for larger drawables)
////				super.draw(batch, parentAlpha);
//				batch.draw(tex.getRegion(), getX(), getY(), getWidth(), getHeight());
//				batch.flush();
				
				Gdx.gl.glColorMask(true, true, true, true);
				Gdx.gl.glDisable(GL20.GL_BLEND); 
			}
			
		};
		icon.addListener(createAppIconInputListener(icon, appClass));
		icon.setSize(Config.APP_ICON_WIDTH, Config.APP_ICON_HEIGHT);
		return icon;

	}
	
	public TextureRegion getInternalAppIconIfAvailable(Class<? extends App> appClass) {
		if(appClass == null) throw new IllegalArgumentException("appClass must not be null!");
		return appIconsAtlas.findRegion(appClass.getSimpleName());
	}
	
	
	
	
	// -- private methods --
	
	
	
	private InputListener createAppIconInputListener(Image icon, Class<? extends App> appClass) {
		return new InputListener() {
			

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

				createAppOpeningAnimation(icon, appClass);
				
			}

		};
	}
	
	private void createAnimationActor() {
		animationActor = new Actor() {
			Color color = Color.BLACK.cpy();
			
			@Override
			public void draw(Batch batch, float parentAlpha) {
				batch.flush();
				
				renderer.setProjectionMatrix(batch.getProjectionMatrix());
				Color oldColor = renderer.getColor();
				color.a = this.getColor().a;
				renderer.setColor(color);
				renderer.begin(ShapeType.Filled);
				renderer.rect(getX(), getY(), getWidth(), getHeight());
				renderer.end();
				renderer.setColor(oldColor);
			}
			
		};
		animationActor.setVisible(false);
		stage.addActor(animationActor);
	}
	
	private void createAppOpeningAnimation(Image icon, Class<? extends App> appClass) {
		tmp.setZero();
		icon.localToStageCoordinates(tmp);// the actors local coordsystem( 0,0 his left bottom corner)

		
		animationActor.setVisible(true);
		animationActor.setPosition(tmp.x, tmp.y);
		animationActor.setSize(icon.getWidth(), icon.getHeight());

		float toX = 0;
		float toY = 0;
		float toSizeX = viewport.getWorldWidth();
		float toSizeY = viewport.getWorldHeight();
		
		float duration = 0.25f;
		Interpolation interpolation = Interpolation.sineOut;
		
		animationActor.addAction(Actions.after(Actions.sequence(
				Actions.parallel(
					Actions.targeting(stage.getRoot(), Actions.touchable(Touchable.disabled)),
					Actions.moveTo(toX, toY, duration, interpolation),
					Actions.sizeTo(toSizeX, toSizeY, duration, interpolation)
				),
				Actions.run(() -> Phone.INSTANCE.queueAppScreen(appClass)),
				Actions.parallel(
					Actions.visible(false),
					Actions.targeting(stage.getRoot(), Actions.touchable(Touchable.enabled))
				))));
	}

	
	private void updatePageNavigation() {
		updatePageNavigationIndicators();
		updatePageNavigationArea();
	}
	
	private void updatePageNavigationIndicators() {
		if(pageIndicators == null) {
			pageIndicators = new Table();
		}
		
		// clear
		pageIndicatorsButtonGroup.clear();
		pageIndicators.clearChildren();

		pageIndicators.center();
		pageIndicators.defaults().pad(3);

		pageIndicatorsButtonGroup.setMaxCheckCount(1);
		pageIndicatorsButtonGroup.setMinCheckCount(1);
		pageIndicatorsButtonGroup.setUncheckLast(true);

		for (int i = 0; i < totalPages; i++) {
			int linkedPageNumber = i;

			ButtonStyle style = new ButtonStyle();
			style.up = new TextureRegionDrawable(pageIndicatorRegion);
			style.checked = new TextureRegionDrawable(pageIndicatorSelectedRegion);
			ColoredButton pageIndicator = new ColoredButton(style, Color.GRAY.cpy());

			pageIndicator.addListener(new InputListener() {

				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					return true;
				}
				
				@Override
				public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
					goToPage(linkedPageNumber);
				}

			});

			pageIndicatorsButtonGroup.add(pageIndicator);
			pageIndicators.add(pageIndicator);
		}
		pageIndicators.pack();

	}
	
	/**returns true if the specified page is reachable*/
	private boolean goToPage(int pageNumber) {
		if (pageNumber == currentPage || pageNumber > totalPages-1 || pageNumber < 0)return false;
		float amountX = (currentPage - pageNumber) * (PAGE_WIDTH + PAGE_GAP);
		float duration = 0.3f;
		allPagesContainer
				.addAction(Actions.after(Actions.moveBy(amountX, 0, duration, Interpolation.sineOut)));
		currentPage = pageNumber;
		return true;
	}

	
	private void updatePageNavigationArea() {
		if(pageNavArea == null) {
			pageNavArea = new Table();
			stage.addActor(pageNavArea);
		}
		
		pageNavArea.clearChildren();
		pageNavArea.center();

		
		ColoredButton leftButton = new ColoredButton(pageNavArrowLeftSelectedRegion, Color.GRAY.cpy(), 0.5f) {
			@Override
			public void act(float delta) {
				if(currentPage == 0)setDisabled(true);
				else setDisabled(false);
				super.act(delta);
			}
		};
	
		leftButton.addListener(new InputListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if(goToPage(currentPage -1)) {
					((Button)pageIndicators.getChild(currentPage)).setChecked(true);
					return true;
				}
				return false;
			}
		});
		
		ColoredButton rightButton = new ColoredButton(pageNavArrowRightSelectedRegion, Color.GRAY.cpy(), 0.5f) {
			@Override
			public void act(float delta) {
				if(currentPage == totalPages - 1)setDisabled(true);
				else setDisabled(false);
				super.act(delta);
			}
		};
		rightButton.addListener(new InputListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if(goToPage(currentPage +1)) {
					((Button)pageIndicators.getChild(currentPage)).setChecked(true);
					return true;
				}
				return false;
			}
		});
		
		float gap = 18;
		
		Image bgLeft = new Image(pageNavBackgroundCircleLeft);
		Image bgCenter = new Image(pageNavBackgroundCenter);
		Image bgRight = new Image(pageNavBackgroundCircleRight);
		
		Table bg = new Table();
		bg.setColor(1, 1, 1, Config.OVERLAY_BACKGROUND_ALPHA);
		bg.add(bgLeft);
		bg.add(bgCenter).width(pageIndicators.getWidth() + 2* gap + leftButton.getWidth() + rightButton.getWidth());
		bg.add(bgRight);
		bg.pack();
		
		Table fg = new Table();
		fg.add(leftButton);
		fg.add(pageIndicators).padRight(gap).padLeft(gap);
		fg.add(rightButton);
		fg.pack();
		
		pageNavArea.stack(bg,fg).center();
		pageNavArea.pack();
		pageNavArea.setPosition(PAGE_WIDTH / 2f, Config.PAGE_INDICATOR_Y, Align.bottom);
	}
	
	
	
	// -- render --

	@Override
	public void render(float delta) {
		GdxUtils.clearScreenStencil();
		GdxUtils.clearScreen(manager.homeScreenBackgroundColor);

		viewport.apply();
		stage.act();
		stage.draw();
		
	}


	// -- resize & dispose --

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}

	@Override
	public void hide() {
		// dispose(); // else change show() too
	}

	@Override
	public void dispose() {
		log.debug("diposing HOMESCREEN");
		stage.dispose(); // doesnt dispose batch if passed in
		appIconStencilMaskShader.dispose();
		appIconAlphaMaskShader.dispose();
		renderer.dispose();
		iconTextures.forEach( icon -> icon.value.dispose());
	}


}
