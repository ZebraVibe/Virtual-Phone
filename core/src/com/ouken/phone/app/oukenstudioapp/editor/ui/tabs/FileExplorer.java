package com.ouken.phone.app.oukenstudioapp.editor.ui.tabs;

import javax.management.remote.TargetedNotification;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Logger;
import com.ouken.phone.app.oukenstudioapp.assets.RegionNames;
import com.ouken.phone.app.oukenstudioapp.assets.SkinNames;
import com.ouken.phone.app.oukenstudioapp.common.EditorManager;
import com.ouken.phone.app.oukenstudioapp.config.EditorConfig;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.EntityNode;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.FileHandleNode;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.SceneNode;
import com.ouken.phone.app.oukenstudioapp.editor.utils.DroppableTarget;
import com.ouken.phone.app.oukenstudioapp.editor.utils.DynamicTable;
import com.ouken.phone.app.oukenstudioapp.editor.utils.EditorUtils;
import com.ouken.phone.app.oukenstudioapp.editor.utils.ValueButton;
import com.ouken.phone.app.oukenstudioapp.editor.utils.ValueButton.ValueType;
import com.ouken.phone.app.oukenstudioapp.scene.Scene;
import com.ouken.phone.app.oukenstudioapp.scene.common.Mappers;
import com.ouken.phone.app.oukenstudioapp.scene.components.RenderComponent;
import com.ouken.phone.app.oukenstudioapp.scene.components.TransformComponent;
import com.ouken.phone.app.oukenstudioapp.scene.entites.SceneEntity;
import com.ouken.phone.app.oukenstudioapp.scene.entites.EntityFactory.EntityType;
import com.ouken.phone.app.oukenstudioapp.screens.EditorScreen;

public class FileExplorer extends Explorer<FileHandleNode, FileHandle> implements Disposable{

	private static final Logger log = new Logger(FileExplorer.class.getName(), Logger.DEBUG);
	
	private static final float CONTENT_BUTTON_WIDTH = 48, CONTENT_BUTTON_HEIGHT = 64;
	private static final int MAX_FILE_NAME_LENGTH = 11;
	
	private Skin skin;
	private EditorScreen editor;
	private ArrayMap<FileHandle, ArrayMap<FileHandle, TextureRegion>> regionArrays = new ArrayMap<>();
	private final ArrayMap<String ,Disposable> disposables = new ArrayMap<>();
	private DragAndDrop dragAndDrop;
	private Target entityExplorerTarget;
	private boolean targetsInitialized = false;
	

	
	public FileExplorer(EditorScreen editor, Skin skin, ShapeRenderer renderer) {
		super(skin, renderer);
		this.editor = editor;
		this.skin = skin;
		init();
	}
	
	// -- init --
	
	private void init() {
		fillExplorerWithChildDirsFrom(EditorManager.INSTANCE.getCurrentProjectDir(), null);
		initDragAndDrop();
	}
	
	
	private void fillExplorerWithChildDirsFrom(FileHandle parent, FileHandleNode parentNode) {
		if(isExistingDir(parent))
			
			updateImageFilesOf(parent);
			
			for(FileHandle child : parent.list())
				if(isExistingDir(child)) {
					
					FileHandleNode node = createNode(child, child.nameWithoutExtension());
					if(parentNode == null)getExplorerTree().add(node);
					else parentNode.add(node);
					if(child.list().length > 0)fillExplorerWithChildDirsFrom(child, node);
				}
	}
	
	private void initDragAndDrop() {
		dragAndDrop = new DragAndDrop();
	}
	
	
	
	
	// -- public methods --
	
	public ArrayMap<String, Disposable> getDisposables() {
		return disposables;
	}
	
	/**removed node if it is one, deletes the dir and removes it from the browser*/
	public void removeFromContentBrowser(ValueButton actor) {
		if(!(actor.getValue() instanceof FileHandle))return;
		FileHandle value = (FileHandle)actor.getValue();
		if(!isExisting(value))return;
		
		if(value.isDirectory()) { // node
				FileHandleNode node = getExplorerTree().findNode(value);
				if(node == null)return;
				removeNodeFromExplorer(node, false);
		}
		value.deleteDirectory();
		removeFromParentTable(actor);
		
	}
	
	public void addToContentBrowser(FileHandle value) {
		if(!isExisting(value))return;
		
		if(value.isDirectory()) {
			FileHandleNode node = addNodeToSelectedOrAsRoot(value, value.nameWithoutExtension());
		}
		// shall auto correctly place actor in table
		getCurrentContentBrowserTable().add(createBrowserItem(value));
	}
	
	
	@Override
	public void removeNodeFromExplorer(FileHandleNode node, boolean reInitBrowser) {
		if(node == null)return;
		node.getValue().deleteDirectory();
		super.removeNodeFromExplorer(node, reInitBrowser);
	}

	
	private void removeFromParentTable(Actor a) {
		Actor p = a.getParent();
		if(p == null || !(p instanceof Table))return;
		Table parent = (Table)p;
		
		Cell<?> cell = parent.getCell(a);
		a.remove();
		parent.getCells().removeValue(cell, true);
		//parent.invalidate();
		parent.invalidateHierarchy();
	}
	
	@Override
	public FileHandleNode getNodeFactory() {
		return new FileHandleNode(skin);
	}
	
	
	@Override
	public Table createContentBrowserOf(FileHandleNode selected) {
		if(selected == null) {
			return new Table();
		}
		
		resetDragAndDrop();
		updateImageFilesOf(selected.getValue());
		
		Array<ValueButton> actors = new Array<ValueButton>();
		ButtonGroup<Button> buttonGroup = new ButtonGroup<>();
		buttonGroup.setUncheckLast(true);
		buttonGroup.setMaxCheckCount(1);
		
		// add dirs
		for(FileHandleNode child : selected.getChildren()) {
			if(child == null)continue;
			FileHandle file = child.getValue();
			if(isExisting(file))actors.add(createContentBrowserItemOfChildNode(child));
		}
		// add everything else
		for (FileHandle file : selected.getValue().list()) {
			if (!isExisting(file) || file.isDirectory())continue;
			ValueButton browserItem = createBrowserItem(file);
			addDragSource(browserItem);
			actors.add(browserItem);
		}

		// add default scenes if dirs name is "scenes"
		
		
		// add to table
		int maxCols = 4;
		float pad = 20;

		Table content = new Table() {

			@Override
			public <T extends Actor> Cell<T> add(T actor) {
				Cell<T> cell = super.add(actor);
				int size = this.getCells().size;
				if (size > 0 && (size) % maxCols == 0)
					cell.row();
				return cell;
			}
		};
		
//		Table content = new DynamicTable(maxCols);
		
		content.setTouchable(Touchable.enabled); // by default children only
		content.pad(pad).padTop(pad / 2);
		content.top().left();
		content.defaults().size(CONTENT_BUTTON_WIDTH, CONTENT_BUTTON_HEIGHT).left().padRight(pad).padBottom(pad);
		for (Button a : actors) {
			content.add(a);
			buttonGroup.add(a);
		}
		
		// uncheck buttons when clicken void
		content.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if(event.getTarget() == null || event.getTarget() == content)buttonGroup.uncheckAll();
			}
		});
		
		buttonGroup.uncheckAll();
		return content;
	}
	

	/**Call this when changing a node's value and its children values depend on the parent value.
	 * [Note]:node is only updated if it has a dir as value*/
	public void updateExplorerNode(FileHandleNode node) {
		if(node == null || node.getValue() == null || !node.getValue().isDirectory())return;

		// check if is in tree
		if(getExplorerTree().findNode(node.getValue()) == null)return;

		node.clearChildren();
		fillExplorerWithChildDirsFrom(node.getValue(), node);
		if(node == getExplorerTree().getSelectedNode())setContentBrowserOf(node);
	}
	


	
	// -- private methods --
	
	private ValueButton createContentBrowserItemOfChildNode(FileHandleNode childNode) {
		ValueButton button = createBrowserItem(childNode.getValue());
		//TODO: bad option: override remove and call node.remove(). problem: not actor.remove() ins not always used 
		button.addCaptureListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				//if(!button.isChecked())return;
				if(getTapCount() == 2 &&  isExistingDir(childNode.getValue()) && childNode != null) {
					chooseNode(childNode);
				}
			}
		});
		return button;
	}
	
	public Label getBrowserItemLabel(ValueButton b) {
		return b.findActor("l");
	}
	
	public ValueButton createBrowserItem(FileHandle file) {
		ValueButton button = new ValueButton(skin.get(SkinNames.SELECTION_BUTTON_STYLE_DARKEST_GRAY, ButtonStyle.class));
		button.setTouchable(Touchable.enabled);
		
		
		Image img = new Image(createIconOf(file));
		img.setTouchable(Touchable.disabled);
		
		Label label = new Label("", skin, SkinNames.LABEL_STYLE_10) {
			@Override
			public void setText(CharSequence newText) {
				if(newText == null)super.setText(null);
				String s = newText.toString();
				if(s.length() > MAX_FILE_NAME_LENGTH) {
					s = s.substring(0, MAX_FILE_NAME_LENGTH - 3) + "...";
				}
				super.setText(s);
			}
		};
		label.setText(file.nameWithoutExtension());
		label.setTouchable(Touchable.disabled);
		label.setName("l");
		
		// fix size
		float w = CONTENT_BUTTON_WIDTH;
		if(img.getWidth() > w) {
			float ratio = img.getHeight() / img.getWidth();
			img.setSize(w, w * ratio);
		}
		
		float h = CONTENT_BUTTON_HEIGHT - label.getHeight();
		if( img.getHeight() > h) {
			float ratio = img.getWidth() / img.getHeight();
			img.setSize(h * ratio, h);
		}
		

		button.add(img).size(img.getWidth(), img.getHeight()).expand().center().row(); //
		button.add(label);
		button.pack();
		
		button.setValue(file);
		
		// already check on touch down not up
		button.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				((Button)event.getTarget()).setChecked(true);
				return true;
			}
		});
		
		return button;
	}
	
	
	/***/
	private void resetDragAndDrop() {
		dragAndDrop.clear();
		if(!targetsInitialized) {
			createDefaultTargets();
			targetsInitialized = true;
		}
		dragAndDrop.addTarget(entityExplorerTarget);
	}
	
	private void createDefaultTargets() {
		entityExplorerTarget = new Target(editor.getEntityExplorer()) {
			
			Vector2 tmp = new Vector2();
			
			@Override
			public void drop(Source source, Payload payload, float x, float y, int pointer) {
				if(payload == null)return;
				log.debug("explorer target: ------ Starting drop...");
				tmp.set(x,y);
				getActor().localToStageCoordinates(tmp);
				Actor hit = getActor().getStage().hit(tmp.x, tmp.y, true);
				//scene
				if(hit == null) {
					log.debug("explorer target: ...target == scene...");
					
				// droppable target	
				}else if(hit != null && hit instanceof DroppableTarget) {
					log.debug("explorer target: ... target == actor...");
					try {
						DroppableTarget target = (DroppableTarget)hit;
						log.error("payload instance of texture region null ?" + (payload.getObject() instanceof TextureRegion));
						TextureRegion region = (TextureRegion)payload.getObject();
						
						String text = EntityExplorer.DROPPABLE_TEX_TARGET_PREFIX + EditorUtils.getNameOfRegion(region, EntityExplorer.TEX_FILE_NAME_MAX_LENGTH);
						target.drop(region, text);
						
						log.debug("explorer target: ...success!");
					} catch (Exception e) {
						log.debug("explorer target: ...failed!");
						log.error("explorer target:  couldnt drop payload!");
					}
				}
				log.debug("explorer target: ...drop end! ------");
				//dragAndDrop.removeSource(source);
			}
			
			@Override
			public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
				//log.debug("explorer target: dragging over target...");
				return true;
			}
			
			@Override
			public void reset(Source source, Payload payload) {
				//dragAndDrop.removeTarget(this);
				log.debug("explorer target: resetting drag!");
			}
		};
		
	}
	
	private void addDragSource(Actor source) {
		dragAndDrop.addSource(new Source(source) {
			
			@Override
			public Payload dragStart(InputEvent event, float x, float y, int pointer) {
				//editor.getStageFilledTable().setTouchable(Touchable.enabled);
				
				Payload payload = new Payload();
				ValueButton valueButton = (ValueButton)event.getTarget();
				Object value = valueButton.getValue();
				
				if(valueButton.hasValue(ValueType.FILE_HANDLE)) {
					FileHandle file = (FileHandle)value;
					
					ValueButton dragActor = createBrowserItem(file);
					dragActor.setDisabled(true);
					Label label = getBrowserItemLabel(dragActor);
					if(label != null)label.remove();
					dragActor.pack();
					dragActor.getColor().a *= 0.75f; 
					
					
					payload.setDragActor(dragActor);
					
					if(!isImage(file)) {
						log.error("source: no image paylaod - starting drag without payload!");
						return null;
					}
					TextureRegion region = createIconOf(file);
					payload.setObject(region);
					log.debug("source: starting drag with payload!");
					return payload;
					
				}
				log.error("source: starting drag but no payload!");
				return null;
			}
			
			@Override
			public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, Target target) {
				Actor hit = event.getStage().hit(event.getStageX(), event.getStageY(), true);
				log.debug("source: stopping drag...looking for alternative drop...");
				// drop into scene
				if(hit == null || hit == event.getStage().getRoot()) {
					log.debug("source: dropping into scene..");
					
					Scene scene = editor.getCurrentScene();
					Entity entityHit = scene.hitScreen(Gdx.input.getX(), Gdx.input.getY());
					RenderComponent render = null;
					
					
					// dropping on extsting entity with no render component or no region-> adding render component
					if(entityHit != null && ((render = Mappers.RENDER.get(entityHit)) == null || render.region == null) && !(entityHit instanceof SceneEntity)){
						TransformComponent transform = Mappers.TRANSFORM.get(entityHit);
						if(transform == null)throw new NullPointerException("Transform must not be null!");
						if(render == null)render = scene.addComponentTo(entityHit, RenderComponent.class);
						
						try {
							render.region = (TextureRegion)payload.getObject();
							render.setNativeSize(entityHit);
							editor.getEntityExplorer().addNewItemContainerToBrowser(entityHit, render);
						} catch (Exception e) {}
						
						
					
					}else {//dropping into void -> creating new sprite	
						
						try {
							TextureRegion region = (TextureRegion) payload.getObject();

							// sets the parent when added
							EntityNode parentNode = editor.getEntityExplorer().getParentNodeForNewDraggedInEntity();
							boolean asRootOnly = parentNode instanceof SceneNode;
							
							EntityNode newNode = editor.getEntityExplorer().addDraggedInEntityToTree(
									EntityType.SPRITE, 
									EditorUtils.getNameOfRegion(region), 
									asRootOnly); 
							
							entityHit = newNode.getValue();
							TransformComponent transform = Mappers.TRANSFORM.get(entityHit);
							transform.setName("" + newNode.getLabelText());
							render = Mappers.RENDER.get(entityHit);
							render.region = region;
							render.setNativeSize(entityHit);
							
							//editor.getEntityExplorer().chooseNode(newNode, true);
							transform.positionBelowMouse();
							
							log.debug("new sprite created");
						} catch (Exception e) {
							log.error("couldn create new sprite: " + e.toString());
						}
					}
				}
				
				log.debug("source: stopping drag!");
			}
			
			
		});
	}
	
	
	
	
	private TextureRegion createIconOf(FileHandle file) {
		if(file.isDirectory())return skin.getRegion(RegionNames.FOLDER_BIG);
		else if(isImage(file)) {
			TextureRegion region = getRegion(file);
			if(region != null)return region;
		}
		else if(isFont(file))return skin.getRegion(RegionNames.FONT_ICON);
		else if(isAudio(file))return skin.getRegion(RegionNames.AUDIO_ICON);
		else if(isScene(file))return skin.getRegion(RegionNames.SCENE_FILE_ICON);
		return skin.getRegion(RegionNames.FILE_ICON);// placeholder
	}
	
	
	public boolean isAsset(FileHandle file) {
		return isScene(file) || isFont(file) || isImage(file) || isAudio(file);
	}
	
	public boolean isScene(FileHandle file) {
		return hasExtension(file, EditorConfig.SCENE_FILE_EXTENSION);
	}
	
	public boolean isFont(FileHandle file) {
		return hasExtension(file, "ttf");
	}
	
	public boolean isImage(FileHandle file) {
		return hasExtension(file, "png","jpg","jpeg");
	}
	
	public boolean isAudio(FileHandle file) {
		return hasExtension(file, "mp3","wav","OGG");
	}
	
	
	
	private ArrayMap<FileHandle, TextureRegion> getRegionsOf(FileHandle parent){
		return regionArrays.get(parent);
	}
	
	private TextureRegion getRegion(FileHandle imageFile) {
		ArrayMap<FileHandle, TextureRegion> regions = getRegionsOf(imageFile.parent());
		return regions == null ? null : regions.get(imageFile);

	}
	
	
	private void updateImageFilesOf(FileHandle parent) {
		if(isExistingDir(parent)) {
			
			ArrayMap<FileHandle, TextureRegion> regions = getRegionsOf(parent);
			
			if(regions == null) {
				regions = new ArrayMap<FileHandle,TextureRegion>();
				regionArrays.put(parent, regions);
			}
			
			for(FileHandle child : parent.list()) {
				if(!isExisting(child) || !isImage(child))continue;
				
				if(!regions.containsKey(child)) {
					regions.put(child, new TextureRegion(new Texture(child)));
				}
			}
			
			// remove if is registered but doesnt exist	
		}else if(regionArrays.containsKey(parent)){
			regionArrays.removeKey(parent);
		}
	}
	
	

	
	
	

	/**returns true if the file extension equals one of the given extensions*/
	private boolean hasExtension(FileHandle file, String ...extensions) {
		String fileExtension = file.extension().toLowerCase();
		if(extensions != null&& file != null)
			for(String ext : extensions)
				if(ext != null && fileExtension.equals(ext))return true;
		return false;
	}

	private boolean isExisting(FileHandle file) {
		return file != null && file.exists();
	}
	
	private boolean isExistingDir(FileHandle file) {
		return isExisting(file) && file.isDirectory();
	}

	@Override
	public void dispose() {
		log.debug("disposing file explorer textures");
		regionArrays.forEach(a -> a.value.forEach(r -> {
		if(r.value != null)r.value.getTexture().dispose();	
		}));
		
		log.debug("disposing file explorer disposables");
		disposables.forEach(d -> {
			if(d!= null && d.value != null)d.value.dispose();
		});
	}
	
	
}
