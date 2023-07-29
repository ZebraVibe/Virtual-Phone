package com.ouken.phone.app.oukenstudioapp.scene;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ouken.phone.app.oukenstudioapp.assets.AssetDescriptors;
import com.ouken.phone.app.oukenstudioapp.assets.RegionNames;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tabs.EntityExplorer;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.EntityNode;
import com.ouken.phone.app.oukenstudioapp.editor.utils.Pane;
import com.ouken.phone.app.oukenstudioapp.scene.common.Mappers;
import com.ouken.phone.app.oukenstudioapp.scene.common.SceneManager;
import com.ouken.phone.app.oukenstudioapp.scene.components.TransformComponent;
import com.ouken.phone.app.oukenstudioapp.scene.entites.EntityFactory;
import com.ouken.phone.app.oukenstudioapp.scene.entites.SceneEntity;
import com.ouken.phone.app.oukenstudioapp.scene.systems.RenderDebugSystem;
import com.ouken.phone.app.oukenstudioapp.scene.systems.RenderSystem;
import com.ouken.phone.app.oukenstudioapp.scene.systems.TransformSystem;
import com.ouken.phone.app.oukenstudioapp.screens.EditorScreen;


public abstract class Scene extends InputMultiplexer implements Disposable{
	
	private static final Logger log = new Logger(Scene.class.getName(), Logger.DEBUG);
	
	private static final int CANVAS_PIXEL_SCALE = 8;
	private static final float SCENE_CAMERA_START_X = 128, SCENE_CAMERA_START_Y = 96;
	
	// -- attributes --
	private final Array<Class<? extends Component>> components = new Array<Class<? extends Component>>();
	private final Array<Class<? extends EntitySystem>> systems = new Array<Class<? extends EntitySystem>>();
	
	private EditorScreen editor;
	private PooledEngine engine;
	private EntityFactory factory;
		private AssetManager assetManager;
	private Viewport viewport;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private ShapeRenderer renderer;
	private TextureAtlas uiAtlas;
	private Texture canvasTexture, canvasTexture2;
	private Sprite canvasSprite;
	
	private Vector2 tempVec = new Vector2();
	
	private SceneEntity sceneEntity;
	
	// -- constructors --
	

	/**creates a Scene with a Pooled engine*/
	public Scene(Viewport sceneViewport, EditorScreen editor) {
		this.editor = editor;
		viewport = sceneViewport;
		camera = (OrthographicCamera)viewport.getCamera();
		this.batch = editor.getBatch();
		this.assetManager = editor.getAssets();
		uiAtlas = assetManager.get(AssetDescriptors.EDITOR_UI);
		renderer = new ShapeRenderer();
		init();
	}
	
	// -- init --
	private void init() {
		initCanvasTexture();
		
		initEngine();
		initInternalClasses();
		initInputProcessors();
		
		factory = new EntityFactory(this);
		initInternalSystems();
		initInternalEntities();
	}


	private void initCanvasTexture() {
		// canvas1
		AtlasRegion region = uiAtlas.findRegion(RegionNames.CANVAS);
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
		
		//canvas2
		AtlasRegion region2 = uiAtlas.findRegion(RegionNames.CANVAS2);
		TextureData data2 = region2.getTexture().getTextureData();
		if (!data2.isPrepared())
			data2.prepare();
		Pixmap atlasPixmap2 = data2.consumePixmap();

		Pixmap newPixmap2 = new Pixmap(region2.getRegionWidth(), region2.getRegionHeight(), Pixmap.Format.RGBA8888);
		newPixmap2.drawPixmap(atlasPixmap2, 0, 0, region2.getRegionX(), region2.getRegionY(), region2.getRegionWidth(),
				region2.getRegionHeight());

		canvasTexture2 = new Texture(newPixmap2);
		canvasTexture2.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		atlasPixmap2.dispose();
		newPixmap2.dispose();
		
		initCanvasRegion(canvasTexture2);
	}

	
	private void initCanvasRegion(Texture texture) {
		canvasSprite = new Sprite(texture);
		canvasSprite.setSize(canvasSprite.getWidth() * CANVAS_PIXEL_SCALE, canvasSprite.getHeight() * CANVAS_PIXEL_SCALE);
	}
	
	private void initEngine() {
		engine = new PooledEngine();
		
		engine.addEntityListener(new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				TransformComponent transform = entity.getComponent(TransformComponent.class);
				transform.remove();
			}
			
			@Override
			public void entityAdded(Entity entity) {
				// logic inside EntityFactory#createEmpty()
				log.debug("EntityListener: entity added");
			}
		});

	}
	
	private void initInternalSystems() {
		engine.addSystem(new TransformSystem());
		
		initSystems(engine);
		
		engine.addSystem(new RenderSystem(this));
		engine.addSystem(new RenderDebugSystem(this));
	}
	

	private void initInternalEntities() {
		sceneEntity = factory.createSceneEntity();
		TransformComponent transform = Mappers.TRANSFORM.get(sceneEntity);
		transform.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
		
		initEntities(engine);
	}
	
	private void initInternalClasses() {
		components.addAll(SceneManager.getInternalComponents());
		systems.addAll(SceneManager.getInternalSystems());
	}

	private void initInputProcessors() {
		InputAdapter sceneCamInput = new InputAdapter() {
			Vector2 tmpZoom = new Vector2(), camStampZoom = new Vector2(), mouseZoom = new Vector2();
			Vector2 stampDragg = new Vector2(), camStampDragg = new Vector2(), tmpDragg = new Vector2();
			
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				//camera dragg
				if(button == Buttons.RIGHT) {
					stampDragg.set(screenX, screenY);
					camStampDragg.set(camera.position.x, camera.position.y);
					return true;
				}
				return false;
			}
			
			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				// camera dragg
				if(Gdx.input.isButtonPressed(Buttons.RIGHT)) {
					tmpDragg.set(screenX, screenY);
					tmpDragg.sub(stampDragg);
					float factor = camera.zoom;
					camera.position.set(camStampDragg,0).sub(tmpDragg.x * factor, -tmpDragg.y * factor, 0);
					return true;
				}
				return false;
			}
			
			@Override
			public boolean scrolled(float amountX, float amountY) {
				if(amountY == 0) return false;
				final float fifth = 0.25f;
				float factor = 0.2f;//fifth;
				
//				if(Float.compare(camera.zoom, fifth) <= 0 && amountY < 0 || 
//						Float.compare(camera.zoom, fifth) < 0 && amountY > 0)
//					factor *= (camera.zoom * (4 *fifth));
				
				if(camera.zoom < factor * 2 && amountY < 0) return false;
				
				mouseZoom.set(Gdx.input.getX(), Gdx.input.getY());
				viewport.project(camStampZoom.set(camera.position.x, camera.position.y));
				tmpZoom.set(mouseZoom).sub(camStampZoom);

				camera.zoom += factor * amountY;
				viewport.unproject(mouseZoom);
				camera.position.set(mouseZoom, 0).sub(tmpZoom.x * camera.zoom, -tmpZoom.y * camera.zoom, 0);
				
				return true;
			}
			
			
		};
		
		
		// -- entity --
		
		InputAdapter entityInput = new InputAdapter() {
			
			Vector2 stampEntityDragg = new Vector2(), tmp = new Vector2();
			Entity hit;
			EntityNode hitNode;
			boolean draggable;
			
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				viewport.unproject(tmp.set(screenX, screenY));
				float mouseX = tmp.x;
				float mouseY = tmp.y;
				
				hit = hitScreen(screenX, screenY);
				
				// entity hit
				if(button == Buttons.LEFT) {
					if(hit != null) {
						// select node
						EntityExplorer explorer = editor.getEntityExplorer();
						hitNode = explorer.getExplorerTree().findNode(hit);
						
						// dragg only if already selected
						if(hitNode == explorer.getSelectedNode()) {
							TransformComponent transform = Mappers.TRANSFORM.get(hit);
							stampEntityDragg.set(mouseX - transform.x, mouseY - transform.y);
							draggable = true;
						}
						return true;
					}
					
					draggable = false;
					// no entity hit -> deselect node seleciton
					editor.getEntityExplorer().chooseNode(null);
				}
				
				return false;
			}
			
			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				if(hitNode != null)editor.getEntityExplorer().chooseNode(hitNode, true);
				hit = null;
				hitNode = null;
				draggable = false;
				return false;
			}
			
			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				viewport.unproject(tmp.set(screenX, screenY));
				float mouseX = tmp.x;
				float mouseY = tmp.y;
				
				// entity dragg
				if(Gdx.input.isButtonPressed(Buttons.LEFT)) {
					if(hit != null && draggable) {
						float x = (int)(mouseX - stampEntityDragg.x);
						float y = (int)(mouseY - stampEntityDragg.y);
						
						TransformComponent transform = Mappers.TRANSFORM.get(hit);
						transform.x = x;
						transform.y = y;
					}
					return true;
				}
				return false;
			}
			
		};
		
		addProcessor(entityInput);
		addProcessor(sceneCamInput);

	}
	
	
	// -- abtsract public methods --
	
	/**use this add systems more properly to the engine*/
	public abstract void initSystems(PooledEngine engine);
	
	/**use this add entites more properly to the engine*/
	public abstract void initEntities(PooledEngine engine);
	
	
	// -- public methods --
	
	public SpriteBatch getBatch() {
		return batch;
	}
	
	
	public ShapeRenderer getRenderer() {
		return renderer;
	}
	
	public Viewport getViewport() {
		return viewport;
	}
	
	public EditorScreen getEditor() {
		return editor;
	}
	
	public SceneEntity getSceneEntity() {
		return sceneEntity;
	}
	

	public PooledEngine getEngine() {
		return engine;
	}
	
	public EntityFactory getFactory() {
		return factory;
	}
	
	
	
	
	/**returns the upper most entity*/
	public @Null Entity hitScreen(float screenX, float screenY) {
		return hit(screenX, screenY, true);
	}
	
	public @Null Entity hitWorld(float worldX, float worldY) {
		return hit(worldX, worldY, false);
	}
	
	/***
	 * 
	 * @param x screen or world coord
	 * @param y screen or world coord
	 * @param screenCoord if true uses x,y as screen coord, else as world coord
	 * @return the top most entity
	 */
	public @Null Entity hit(float x, float y, boolean screenCoord) {
		//engine.getEntitiesFor(Families.TRANSFORM); are unsorted
		ImmutableArray<Entity> entities = engine.getSystem(TransformSystem.class).getEntities(); // sorted
		Entity hit = null;
		for(int i = entities.size() -1; i >= 0; i--) {
			Entity e = entities.get(i);
			TransformComponent transform = Mappers.TRANSFORM.get(e);

			if(screenCoord)transform.screenToParentCoord(tempVec.set(x, y));
			else transform.worldToParentCoord(tempVec.set(x, y));
			
			if(transform.getBounds().contains(tempVec)) {
				hit = e;
				if(hit instanceof SceneEntity)return null;	
				break;
			}
		}
		return hit;
	}
	
	
	/**positions the scene camera to its default start position*/
	public void repositionSceneCamera() {
		//camera.position.set(SCENE_CAMERA_START_X, SCENE_CAMERA_START_Y, 0);
		camera.position.set(viewport.getWorldWidth() /2f, viewport.getWorldHeight() /2f, 0);
	}
	
	
	
	
	
	
	public Array<Class<? extends Component>> getAvailableComponents() {
		return components;
	}
	
	public Array<Class<? extends EntitySystem>> getAvailableSystems() {
		return systems;
	}
	
	public <T extends Component> T addComponentTo(Entity entity, Class<T> componentType) {
		T c = engine.createComponent(componentType);
		entity.add(c);
		return c;
	}
	
	

	


	public void render() {
		float delta = Gdx.graphics.getDeltaTime();
		update(delta);
		render(delta);
	}
	
	private void update(float delta) {
		camera.update();
		updateInput();
		camera.update();
	}
	
	private void updateInput() {
		if(Gdx.input.isKeyJustPressed(Keys.ENTER)) {
//			log.debug(camera.position.toString());
//			log.debug("systems: " + engine.getSystems().toString());
//			log.debug("entities: " + engine.getEntities().toString());
		}

		Stage stage = editor.getStage();
		Vector3 camPos = camera.position;
		float pxl = 4f * camera.zoom;
		
		if(stage.getKeyboardFocus() == null) {
			// move cam wasd
			if (Gdx.input.isKeyPressed(Keys.A)) {
				camPos.x -= pxl;
			}
			if (Gdx.input.isKeyPressed(Keys.D)) {
				camPos.x += pxl;
			}
			if (Gdx.input.isKeyPressed(Keys.W)) {
				camPos.y += pxl;
			}
			if (Gdx.input.isKeyPressed(Keys.S)) {
				camPos.y -= pxl;
			}
			
			
			// remove key focus or collapse bottom/top pane
			if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
				stage = editor.getStage();

				if (stage.getKeyboardFocus() == null) {
					editor.changeHidingOf(true, true);
				} else {
					stage.setKeyboardFocus(null);
				}
				editor.hideContextMenu();
			}
		}
		
	
		
		// to remove focus, remove menu, and dragged actor (?)
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			stage = editor.getStage();
			stage.setKeyboardFocus(null);
			//stage.setScrollFocus(null);
			editor.hideContextMenu();
		}
		
	}
	

	
	private void render(float delta) {
		batch.flush();
		
		renderSceneCanvas();
		engine.update(delta);
		renderAxes();
	}
	
	private void renderSceneCanvas() {
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		if(canvasSprite != null) {
			float scale = camera.zoom  < 1 ? 1 : camera.zoom;
			
			float viewportWidth = viewport.getWorldWidth() * scale; // remove scale to not scale with camera zoom
			float viewportHeight = viewport.getWorldHeight() * scale;
			
			
			int xMin = ((int)(camera.position.x - viewportWidth / 2f) - (int)canvasSprite.getWidth()) / (int)canvasSprite.getWidth() * (int)canvasSprite.getWidth();
			int yMin = ((int)(camera.position.y - viewportHeight / 2f) - (int)canvasSprite.getHeight()) / (int)canvasSprite.getHeight() * (int)canvasSprite.getHeight();
			
			
			int xMax = xMin + (int)Math.ceil(viewportWidth / (float)canvasSprite.getWidth()) * (int)canvasSprite.getWidth() + (int)canvasSprite.getWidth();
			int yMax = yMin + (int)Math.ceil(viewportHeight / (float)canvasSprite.getHeight()) * (int)canvasSprite.getHeight() + (int)canvasSprite.getHeight();
			
			for(int x = xMin; x <= xMax; x += canvasSprite.getWidth()) {
				for(int y = yMin; y <= yMax; y += canvasSprite.getHeight()) {
					canvasSprite.setPosition(x, y);
					canvasSprite.draw(batch);
				}
			}
		}
		batch.end();
	}
	
	private void renderAxes() {
		renderer.setProjectionMatrix(camera.combined);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		
		// x axes
		if(camera.frustum.pointInFrustum(0, camera.position.y, 0)) {
			renderer.setColor(Pane.GREEN);
			renderer.begin(ShapeType.Line);
			renderer.line( 
					0, 
					camera.position.y - viewport.getWorldHeight() * camera.zoom / 2f, 
					0, 
					camera.position.y + viewport.getWorldHeight() * camera.zoom / 2f);
			renderer.end();
		}
		
		// y axes
		if (camera.frustum.pointInFrustum(camera.position.x, 0, 0)) {
			renderer.setColor(Pane.GREEN);
			renderer.begin(ShapeType.Line);
			renderer.line(
					camera.position.x - viewport.getWorldWidth() * camera.zoom / 2f,
					0, 
					camera.position.x + viewport.getWorldWidth() * camera.zoom / 2f,
					0);
			renderer.end();
		}
		
		Gdx.gl.glDisable(GL20.GL_BLEND);
		
	}
	


	@Override
	public void dispose() {
		disposeSystems();
		renderer.dispose();
	}
	
	private void disposeSystems() {
		getEngine().getSystems().forEach(system -> {
			if(system instanceof Disposable)((Disposable)system).dispose();
		});
	}
	
	
	
}
