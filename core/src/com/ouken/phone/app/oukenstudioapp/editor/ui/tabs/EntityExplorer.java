package com.ouken.phone.app.oukenstudioapp.editor.ui.tabs;

import java.util.function.Consumer;

import javax.security.auth.Subject;

import org.reflections.Reflections;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.bullet.collision.btStaticPlaneShape;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.TreeStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.reflect.Annotation;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.ouken.phone.app.oukenstudioapp.assets.RegionNames;
import com.ouken.phone.app.oukenstudioapp.assets.SkinNames;
import com.ouken.phone.app.oukenstudioapp.common.EditorManager;
import com.ouken.phone.app.oukenstudioapp.config.EditorConfig;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.EntityNode;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.SceneNode;
import com.ouken.phone.app.oukenstudioapp.editor.utils.DroppableTarget;
import com.ouken.phone.app.oukenstudioapp.editor.utils.DynamicTable;
import com.ouken.phone.app.oukenstudioapp.editor.utils.EditorUtils;
import com.ouken.phone.app.oukenstudioapp.editor.utils.Pane;
import com.ouken.phone.app.oukenstudioapp.editor.utils.PaneSpriteDrawable;
import com.ouken.phone.app.oukenstudioapp.scene.Scene;
import com.ouken.phone.app.oukenstudioapp.scene.common.Mappers;
import com.ouken.phone.app.oukenstudioapp.scene.components.RenderComponent;
import com.ouken.phone.app.oukenstudioapp.scene.components.SceneComponent;
import com.ouken.phone.app.oukenstudioapp.scene.components.TransformComponent;
import com.ouken.phone.app.oukenstudioapp.scene.entites.EntityFactory.EntityType;
import com.ouken.phone.app.oukenstudioapp.scene.entites.SceneEntity;
import com.ouken.phone.app.oukenstudioapp.scene.uitls.Invokable;
import com.ouken.phone.app.oukenstudioapp.scene.uitls.Serializable;
import com.ouken.phone.app.oukenstudioapp.scene.uitls.EntityConsumer;

public class EntityExplorer extends Explorer<EntityNode, Entity>{

	private static final Logger log = new Logger(EntityExplorer.class.getName(), Logger.DEBUG);
	
	public static final float ITEM_CONTAINER_HEIGHT = 52;
	public static final float TF_MIN_WIDTH = 55, TF_MIN_HEIGHT = 14;
	public static final int TEX_FILE_NAME_MAX_LENGTH = 14;
	public static final String DROPPABLE_TEX_TARGET_PREFIX = "tex :: ";
	public static final String ADD_BUTTON_NAME = "add";
	
	private Skin skin;
	private ShapeRenderer renderer;
	private boolean isFirstNode = true;
	private Scene scene;
	private SceneNode sceneNode;
	private Vector2 tmp = new Vector2();
	
	private Array<Field> tmpFields = new Array<Field>();
	private final ArrayMap<EntityNode, Table> browsers = new ArrayMap<>();
	
	public EntityExplorer(Scene scene, Skin skin, ShapeRenderer renderer) {
		super(skin, renderer);
		this.skin = skin;
		this.renderer = renderer;
		this.scene = scene;
		init();
	}


	private void init() {
		// first node is added as scene node
		initSceneRootNode();
		initTreeListener();
	}
	
	private void initTreeListener() {
		// center camrea over entity of over node
		getExplorerTree().addListener(new ClickListener() {
			Vector2 tmp = new Vector2();
			EntityNode lastNode;
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(getSelectedNode() == null)return;
				if(lastNode != getSelectedNode()) {
					lastNode = getSelectedNode();
					setTapCount(0);
				}
				if(getTapCount() == 2) {
					Entity over = null;
					if((over = getExplorerTree().getOverValue()) != null) {
						TransformComponent transform = Mappers.TRANSFORM.get(over);
						// position camera over hit
						transform.parentToWorldCoord(transform.getPosition(tmp, Align.center));
						Camera camera = scene.getViewport().getCamera();
						camera.position.set(tmp, 0);
					}
				}
			}
		});
	}


	private void initSceneRootNode() {
		// first added not is of SceneNode
		sceneNode = (SceneNode)addEntityToSelectedOrAsRootNode(getSceneEntity(), "Scene", true);
	}
	

	
	// -- public --
	
	public Scene getScene() {
		return scene;
	}
	
	public SceneNode getSceneNode() {
		return sceneNode;
	}
	
	public SceneEntity getSceneEntity() {
		return scene.getSceneEntity();
	}
	
	public EntityNode getSelectedNode() {
		return getExplorerTree().getSelectedNode();
	}
	
	public Entity getSelectedValue() {
		return getExplorerTree().getSelectedValue();
	}
	
	/**creates an empty entity
	 * @throws Exception */
	public EntityNode addEntityToSelectedOrAsRootNode(String label){
		return addEntityToSelectedOrAsRootNode(label, false);
	}
	
	/**creates an empty entity
	 * @throws Exception */
	public EntityNode addEntityToSelectedOrAsRootNode(String label, boolean asRootOnly){
		return addEntityToSelectedOrAsRootNode(scene.getFactory().createEmpty(), label, asRootOnly);
	}
	
	public EntityNode addEntityToSelectedOrAsRootNode(EntityType type, String label, boolean asRootOnly){
		return addEntityToSelectedOrAsRootNode(scene.getFactory().create(type), label, asRootOnly);
	}
	
	public EntityNode addEntityToSelectedOrAsRootNode(Entity entity, String label, boolean asRootOnly){
		EntityNode parentNode = null;
		if(!(entity instanceof SceneEntity)) {
			parentNode = getSelectedNode();
			if(parentNode == null)parentNode = getSceneNode();
		}
		return addEntityToNodeOrAsRootNode(parentNode, entity, label, asRootOnly);
	}
	
	
	

	
	public EntityNode addEntityToNodeOrAsRootNode(EntityNode parentNode, EntityType type, String label, boolean asRootOnly){
		return addEntityToNodeOrAsRootNode(parentNode, scene.getFactory().create(type), label, asRootOnly);
	}
	
	public EntityNode addEntityToNodeOrAsRootNode(EntityNode parentNode, Entity entity, String label, boolean asRootOnly){
		if(!(entity instanceof SceneEntity) && parentNode != null) {
			TransformComponent transform = Mappers.TRANSFORM.get(entity);
			Entity parent = parentNode.getValue();
			
			TransformComponent parentTransform = Mappers.TRANSFORM.get(parent);
			if(parentTransform != null)transform.setParent(parentTransform);
		}

		return addNodeToOrAsRoot(parentNode, entity, label, asRootOnly);
	}
	

	
	
	
	public EntityNode addDraggedInEntityToTree(EntityType type, String label, boolean asRootOnly){
		return addDraggedInEntityToTree(scene.getFactory().create(type), label, asRootOnly);
	}
	
	public EntityNode addDraggedInEntityToTree(Entity entity, String label, boolean asRootOnly){
		EntityNode parentNode = null;
		if(!(entity instanceof SceneEntity)) {
			// set parent
			parentNode = getParentNodeForNewDraggedInEntity();
			Entity parent = parentNode.getValue();
			TransformComponent transform = Mappers.TRANSFORM.get(entity);
			TransformComponent parentTransform = Mappers.TRANSFORM.get(parent);
			if(parentTransform != null)transform.setParent(parentTransform);
			
		}
		return addNodeToOrAsRoot(parentNode, entity, label, asRootOnly);
	}
	
	@Override
	public EntityNode addNodeToOrAsRoot(EntityNode parentNode, Entity value, String label, boolean asRootOnly) {
		EntityNode node = super.addNodeToOrAsRoot(parentNode, value, label, asRootOnly);
		// format after node has its parent
		if(node != null)formatNameBasedOnNodeSiblings(node);
		return node;
	}
	
	
	/**returns the parent of the current selected node. if null returns the scene node*/
	public EntityNode getParentNodeForNewDraggedInEntity() {
		EntityNode sceneNode = getSceneNode();
		EntityNode parentNode = getExplorerTree().getSelectedNode();
		parentNode = parentNode == null ? null : parentNode.getParent();
		
		return parentNode != null ? parentNode : sceneNode;
	}
	
	/**parent node null, if parent is tree*/
	public void formatNameBasedOnNodeSiblings(EntityNode node) {
		if(node == null)return;
//		TransformComponent transform = Mappers.TRANSFORM.get(node.getValue());
		String name = node.getLabelText();//transform.name;
		
		Array<EntityNode> siblings;
		
		if(node.getParent() == null)siblings = node.getTree().getRootNodes();
		else siblings = node.getParent().getChildren();

			// check if equal sibling exists
			boolean hasEqual = false;
			for(EntityNode sibling : siblings) {
				if(sibling == node)continue;
//				TransformComponent sibTransform = Mappers.TRANSFORM.get(sibling.getValue());
//				if(sibTransform.name.equals(name)) {
//					hasEqual = true;
//					break;
//				}
//				log.debug("Formatting node name: coomparing: " + sibling.getLabelText() + " with " + name);
				if(sibling.getLabelText().equals(name)) {
					hasEqual = true;
					break;
				}
			}
			
			
			if(!hasEqual) {
				log.error("NOT setting new formatted name");
				return;
			}
			
			int lastIndex= 0;
			for(EntityNode sibling : siblings) {
				if(sibling == node)continue;
//				TransformComponent sibTransform = Mappers.TRANSFORM.get(sibling.getValue());
				String sibName = sibling.getLabelText();//sibTransform.name;
				if(sibName.length() < name.length())continue;
				String sub = sibName.substring(0, name.length());
				if(sub.equals(name)) {
					
					// we want the "x" from name(x)
					String sIndex = sibName.substring(sibName.length() - 2, sibName.length() -1);
					try {
						int i = Integer.parseInt(sIndex);
						if(i > lastIndex)lastIndex = i;
					} catch (Exception e) {
						// no int available -> so start with (0)
					}
					
				}
				
			}
			log.debug("setting new formatted name");
			name += "(" + (lastIndex + 1) + ")";
			
			node.setLabelText(name);
		}
	
	
	
	@Override
	protected boolean insertDraggedNodeAt(EntityNode node, EntityNode parent, EntityNode prevNode, boolean add) {
		if((parent == null && prevNode == null) || parent instanceof SceneNode)return false; // tries to insert it before scene node
		if(super.insertDraggedNodeAt(node, parent, prevNode, add)){
			TransformComponent transform = Mappers.TRANSFORM.get(node.getValue());
			TransformComponent parentTransform = null;
			
			
			
			int index = 0;
			if(node.getParent() == null) {//tree
				index = getExplorerTree().getRootNodes().indexOf(node, true) - 1; // minus the scene entity
				parentTransform = Mappers.TRANSFORM.get(getSceneEntity());
			}else{
				index = node.getParent().getChildren().indexOf(node, true);
				parentTransform = Mappers.TRANSFORM.get(node.getParent().getValue());
			}

		
			transform.setParent(parentTransform, true); // only adds the child for right Z order we have to insert
			transform.setZIndex(index);
			log.debug("inserted : " + transform);
			return true;
		}
		return false;
	}
	
	@Override
	public void removeNodeFromExplorer(EntityNode node, boolean reInitBrowser) {
		if (node == null || node instanceof SceneNode)return;
		super.removeNodeFromExplorer(node, reInitBrowser);
		Entity entity = node.getValue();
		TransformComponent transform;
		if((transform = Mappers.TRANSFORM.get(entity)) == null)return;
		transform.removeFromEgine();

	}
	
	/**null if the component couldnt be added*/
	public Component addComponentToCurrentSelected(Class<? extends Component> cls) {
		if(cls == null) {
			log.error("class is null");
			return null;
		}
		Entity entity = getExplorerTree().getSelectedValue();
		if(entity == null) {
			log.error("Selected EntityNode Entity-value not found!");
			return null;
		}
		Class<? extends Component> componentClass = cls;
		if(entity.getComponent(componentClass) != null) {
			log.debug("Component " + componentClass.getSimpleName() + " already added.");
			return null;
		}
		
		try {
			Component c = scene.getEngine().createComponent(componentClass);
			entity.add(c);
			log.debug("New Component of type" + cls.getSimpleName() + " created!");
			return c;
		} catch (Exception e) {
			log.error("Couldnt create component." + e.toString());
			return null;
		}
	}
	
	/**null if the system couldnt be added*/
	public EntitySystem addSystemToScene(Class<? extends EntitySystem> cls) {
		if(cls == null) {
			log.error("class is null");
			return null;
		}
		Class<? extends EntitySystem> systemClass = cls;
		EntitySystem engineSystem = scene.getEngine().getSystem(systemClass);
		if(engineSystem != null) {
			log.debug("System " + engineSystem + " already in engine.");
			return null;
		}
		
		try {
			EntitySystem newSystem = null;
			Constructor[] constructors = ClassReflection.getConstructors(systemClass);
			
			for(Constructor c : constructors) {
				Class<?>[] params = c.getParameterTypes();
				//log.debug("parameter types" + c.getParameterTypes().toString());
				if(params == null || params.length == 0) {
					newSystem = (EntitySystem) c.newInstance();
				} else {
					// tries to pass a scene object, might throw exception which will be catched
					newSystem = (EntitySystem)c.newInstance(scene);
				}
			}
			
			
			//EntitySystem newSystem = ClassReflection.newInstance(systemClass);
			
			scene.getEngine().addSystem(newSystem);
			log.debug("New System of type" + systemClass.getSimpleName() + " created!");
			return newSystem;
		} catch (Exception e) {
			log.error("[!] Couldnt create new system instance. Maybe needs parameters instead of only empty constructor? " + e.toString());
			return null;
		}
	}
	
	
	
	
	
	// -- overridden --
	
	
	@Override
	public EntityNode getNodeFactory() {
		if(isFirstNode) {
			isFirstNode = false;
			SceneNode sceneNode = new SceneNode(skin);
			sceneNode.setIcon(new TextureRegionDrawable(skin.getRegion(RegionNames.SCENE_ICON)));
			return sceneNode;
		}
		return new EntityNode(skin);
	}
	
	@Override
	public Table createContentBrowserOf(EntityNode selected) {
		Table browser = findContentBrowserOf(selected);
		// browser already exists
		if(browser != null) {
			return browser;
		}
		
		Table content = new Table();
		content.top().left();
		content.pad(1);
		content.defaults().spaceBottom(1);
		
		Engine engine = scene.getEngine();
		
		if(selected instanceof SceneNode) {
			if(engine == null)return content;
			for(EntitySystem s : engine.getSystems()) {
				content.add(createBrowserItemContainer(engine,s)).expandX().fillX().row();
			}
			Button button = createButtonForAdding("Add Systems");
			button.setName(ADD_BUTTON_NAME);
			button.addListener(createAddButtonClickListener(false, button));
			content.add(button).row();
			
		}else {
			Entity entity = selected.getValue();
			if(entity == null)return content;
			
			for(Component c : entity.getComponents()) {
				content.add(createBrowserItemContainer(entity, c)).expandX().fillX().row();
			}
			Button button = createButtonForAdding("Add Components");
			button.setName(ADD_BUTTON_NAME);
			button.addListener(createAddButtonClickListener(true, button));
			content.add(button).row();
		}
		
		browsers.put(selected, content);
		
		return content;
	}
	
	
	
	
	
	
	
	// -- private --
	
	private Table createBrowserItemContainer(Object objHolder, Object compOrSys) {
		Table container = new Table();
		container.setBackground(skin.get(SkinNames.PANE_DRAWABLE_GRAY, PaneSpriteDrawable.class));
		//container.pad(1).padTop(0);
		
		Button plusMinButton = new Button(skin.get(SkinNames.PLUS_MINUS_BUTTON_STYLE_LIGHT_GRAY, ButtonStyle.class));
		
		TextButton button = createTextButtonGray(getFormattedName(compOrSys));// 
		button.clearChildren();
		button.add(plusMinButton).padLeft(Pane.PAD_4);
		button.add(button.getLabel()).height(EditorConfig.WINDOW_HEADER_HEIGHT).padLeft(Pane.PAD_4).expandX().left().row();
		container.add(button).expandX().fill().row();
		
		ScrollPane paneContent = createFieldsContainer(objHolder, compOrSys);
		Pane pane = new Pane(renderer).paneColor(Pane.DARK_GRAY).smoothEdges(false, false, true, true);
		pane.add(paneContent).expand().fill();
		
		
		
		
		
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				plusMinButton.toggle();
			}
			
		});
		
		
		plusMinButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(plusMinButton.isChecked()) {
					container.add(pane).colspan(2).pad(1).padTop(0).height(ITEM_CONTAINER_HEIGHT).expandX().fill().row();
					
				}else {
					Cell<?> cell = container.getCell(pane);
					pane.remove();
					container.getCells().removeValue(cell, true);
					container.invalidate();
				}
			}
		});
		
		plusMinButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				plusMinButton.toggle();
			}
		});
		
	
		
		
		return container;
	}


	// add button
	
	private Button createButtonForAdding(String text) {
		TextButton b = createTextButtonGray(text);
		b.pad(Pane.PAD_4);
		b.pack();
		return b;
	}
	
	private TextButton createTextButtonGray(String text) {
		TextButtonStyle style = new TextButtonStyle(skin.get(SkinNames.IMAGE_TEXT_BUTTON_STYLE_GRAY10, ImageTextButtonStyle.class));
		style.down = style.checked;
		style.checked = null;
		TextButton b = new TextButton(text ,style);
		b.getLabel().setTouchable(Touchable.disabled);
		return b;
	}
	
	private TextButton createTextButtonDarkestGray(String text) {
		TextButtonStyle style = new TextButtonStyle(skin.get(SkinNames.IMAGE_TEXT_BUTTON_STYLE_DARKEST_GRAY10, ImageTextButtonStyle.class));
		style.down = style.checked;
		style.checked = null;
		TextButton b = new TextButton(text ,style);
		b.getLabel().setTouchable(Touchable.disabled);
		return b;
	}
	
	

	private ClickListener createAddButtonClickListener(boolean isComponent, Button button) {
		Vector2 tmp = new Vector2();
		Pane pane = new Pane(renderer).paneColor(Pane.GRAY.cpy()).addShadow();
		ListStyle listStyle = new ListStyle(skin.get(SkinNames.LIST_STYLE_GRAY_12, ListStyle.class));
		listStyle.background = null;
		List<String> list = new List<>(listStyle);
		
		ScrollPaneStyle scrollStyle = new ScrollPaneStyle(skin.get(SkinNames.SCROLL_PANE_STYLE, ScrollPaneStyle.class));
		scrollStyle.background = null;
		
		ScrollPane scroll = new ScrollPane(list, scrollStyle);
		scroll.setFadeScrollBars(true);
		scroll.setScrollbarsOnTop(true);
		scroll.setFlickScroll(false);
		scroll.setScrollingDisabled(true, false);
		
		
		ArrayMap<String, Class<? extends EntitySystem>> systems = new ArrayMap<>();
		ArrayMap<String, Class<? extends Component>> components = new ArrayMap<>();
		
		if(isComponent) {
			scene.getAvailableComponents().forEach(c -> components.put(c.getSimpleName(), c));
			components.forEach(cls -> list.getItems().add(cls.key));
		}else {
			scene.getAvailableSystems().forEach(s -> systems.put(s.getSimpleName(), s));
			systems.forEach(cls -> list.getItems().add(cls.key));
		}
		
		
		list.setItems(list.getItems());
		list.getSelection().clear(); // else by default one selected and hover is not rendered
		pane.pad(Pane.PAD_4);
		pane.add(scroll).maxHeight(64);
		pane.pack();
		
		list.addListener(new ClickListener() {
			
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				String itemClassName = list.getOverItem();
				log.debug("item name: " + itemClassName);
				if(itemClassName == null) {
					log.error("over-list-item is null!");
					return;
				}
				
				Object itemHolder;
				Object item;

				
				// returns if item couldn be added
				if(isComponent) {
					log.debug("adding component...");
					if((item = addComponentToCurrentSelected(components.get(itemClassName))) == null) {
						list.getSelection().clear(); // clears current selected list item
						return;
					}
					itemHolder = getExplorerTree().getSelectedValue();
				}
				else {
					log.debug("adding system...");
					if((item = addSystemToScene(systems.get(itemClassName))) == null) {
						list.getSelection().clear(); // clears current selected list item
						return;
					}
					itemHolder = scene.getEngine();
				}
				
				
				addNewItemContainerToBrowser(itemHolder, item);
				
				pane.remove();
			};
			
		});
		
		ClickListener stageCaptureListener = new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Actor target = event.getTarget();
				if(target == button || target != list) {
					pane.remove();
					event.getStage().removeCaptureListener(this);
				}
			};
			
			

		};
		
		return new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				log.debug("clicking...");
				if(pane.getStage() != null) {
					pane.remove();
					event.getStage().removeCaptureListener(stageCaptureListener);
					log.debug("removing");
					
				}else {
					log.debug("adding");
					if(event.getTarget() instanceof Button) {
						tmp.set(button.getWidth() / 2, 0);
						button.localToStageCoordinates(tmp);
						//stageToLocalCoordinates(tmp);
						pane.setPosition(tmp.x, tmp.y, Align.top);
						event.getStage().addActor(pane);
						
						// add stage listener
						event.getStage().addCaptureListener(stageCaptureListener);
						
					}
				}
			}
		};
	}

	public Table findContentBrowserOf(EntityNode node) {
		return browsers.get(node);
	}
	
	public Table findContentBrowserOf(Entity entity) {
		if(entity == null)return null;
		return findContentBrowserOf(getExplorerTree().findNode(entity));
	}
	

	
	
	/**
	 * Adds  new item to the browser ot the current selected node
	 * @param objHolder i.e. Entity or Engine
	 * @param obj i.e. Component or System
	 */
	public void addNewItemContainerToBrowser(Object objHolder, Object obj) {
		if(objHolder == null || obj == null || 
				(objHolder instanceof Entity && !(obj instanceof Component)) ||
				(objHolder instanceof Engine && !(obj instanceof System)))return;
		
		Table browser = null;
		
		if(objHolder instanceof Entity) {
			// not currently selected
			Entity entity = (Entity)objHolder;
			if(getSelectedValue() != entity) {
				EntityNode node = getExplorerTree().findNode(entity);
				if(node == null) {
					log.error("EntityNode node does not exist yet!");
					return;
				}
				browser = findContentBrowserOf(node);
				if(browser == null) browser = createContentBrowserOf(node);
				
			}else {
				browser = getCurrentContentBrowserTable();
			}
		}
		
		
		Actor browserItem = createBrowserItemContainer(objHolder, obj);
		if(browser == null) {
			log.error("current Content browser table is null!");
			return;
		}
		Button button = browser.findActor(ADD_BUTTON_NAME); 
		browser.getCell(button).setActor(browserItem).expandX().fill().row();
		browser.add(button).row();
	}
	
	
	
	// component content fields
	
	private ScrollPane createFieldsContainer(Object objHolder, Object obj) {
		Class cls = obj.getClass();
		Table container = createDynamicTable(2);
		
		log.debug("creating fields table of class: " + cls.getSimpleName());
		
		for(Field field : getFieldsOf(cls)) {
			if(field == null)continue;
			Table item = null;
			try {
				item = createFieldActor(objHolder, obj, field);
			} catch (Exception e) {
				log.error("[!]Couldnt create Field item.");
				e.printStackTrace();
			}
			container.add(item);
		}
		
		ScrollPaneStyle scrollStyle = new ScrollPaneStyle(skin.get(SkinNames.SCROLL_PANE_STYLE, ScrollPaneStyle.class));
		ScrollPane scroll = new ScrollPane(container, scrollStyle);
		scroll.setFlickScroll(false);
		
		return scroll;
	}
	
	
	private Table createDynamicTable(int maxCols /*field -> labels and checkboxes/texfields*/) {
		Table table = new DynamicTable().maxCols(2); // new Table(); //
		table.pad(Pane.PAD_4);
		table.defaults().left().expandX().fillX().spaceBottom(1).spaceLeft(16).maxWidth(128);
		//table.setDebug(true);
		return table;
	}
	
	
	
	private Table createFieldActor(Object objHolder, Object obj, Field field ) throws Exception {
		if(obj == null || field == null)return null;
		Class<?> type = field.getType();
		//log.debug("Object : " + obj);
		if(type == int.class || type == float.class || type == double.class || type == long.class || type.isAssignableFrom(String.class)) {
			return createTextFielfOfField(objHolder, obj, field, field.getType());
			
		}else if(type == boolean.class) {
			return createCheckBoxOfField(objHolder, obj, field);
			
		}else if(type instanceof Object) {
			
			if(type == EntityConsumer.class && objHolder instanceof Entity)return createTextButtonOfField((Entity)objHolder, obj, field);
			if(type.isAssignableFrom(TextureRegion.class))return createSelectOrDragInBox((Entity)objHolder, obj, field);
			
		}
		return null;
	}

	
	// field actors
	
	private <T> Table createTextFielfOfField(Object objHolder, Object obj, Field field, T valueType) {
		Table container = new Table();
		container.defaults();//.spaceRight(Pane.PAD_4);
		try {
			LabelStyle labelStyle = new LabelStyle(skin.get(SkinNames.LABEL_STYLE_10, LabelStyle.class));
			Label label = new Label(field.getName(), labelStyle);
			
			TextFieldStyle tfStyle = new TextFieldStyle(skin.get(SkinNames.TEXT_FIELD_STYLE10, TextFieldStyle.class));
			TextField tf = new TextField("" + getFieldValue(field, obj), tfStyle) {
				
				Object oldValue = getFieldValue(field, obj);
				
				@Override
				public void act(float delta) {
					try {
						// upate displayed value on external field change
						if(!hasKeyboardFocus() && !oldValue.equals(getFieldValue(field, obj))) {
							oldValue = getFieldValue(field, obj);
							setText("" + getFieldValue(field, obj));
						}
					} catch (Exception e) {}
					super.act(delta);
				}
			};
			
			// update field and value text on unfocus
			tf.addCaptureListener(new FocusListener() {
				@Override
				public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
					
					if(event.isFocused())return;
					
					try {
						setFieldValue(field, obj, parsePrimitiveType(tf.getText(), field.getType()));
						log.debug("Changing field value on focus lost");
					} catch (Exception e) {
						log.error("[!!] Resetting value since couldnt set field value");
						
						try {// reset value
							tf.setText("" + getFieldValue(field, obj));
						} catch (Exception e1) {
							log.error("Couldnt reset value!");
							e1.printStackTrace();
						}
						e.printStackTrace();
					}
					
				}
			
			});
			
			// update field
			tf.addListener(new InputListener() {
				
				@Override
				public boolean keyTyped(InputEvent event, char character) {
					try {
						setFieldValue(field, obj, parsePrimitiveType(tf.getText(), field.getType()));
						log.debug("Changing field value");
					} catch (Exception e) {
						log.error("Couldnt set value!");
						e.printStackTrace();
					}
					return true;
				}

				
			});
			
			
			container.add(label).expandX().fillX();
			container.add(tf).size(TF_MIN_WIDTH, TF_MIN_HEIGHT);
			//container.pack();
			
		} catch (Exception e) {
			log.error("Couldnt create field instance and therefore no textfield.");
			e.printStackTrace();
			return container;
		}
		
		return container;
	}
	
	private Table createCheckBoxOfField(Object objHolder, Object obj, Field field) {
		CheckBoxStyle boxStyle = new CheckBoxStyle(skin.get(SkinNames.CHECK_BOX_STYLE, CheckBoxStyle.class));
		CheckBox box = new CheckBox("" + field.getName(), boxStyle) {
			@Override
			public void act(float delta) {
//				try { 
//					// update displayed value if field value has changed (from external call)
//					boolean value = (boolean)getValue(field, obj);
//					if(!equalByType(isChecked(), value, boolean.class, true)) {
//						log.debug("Changing button checked state");
//						setChecked(value);
//					}
//					
//					// update displayed name if field name has changed (from external call)
//					if(!field.getName().equals(getText())) {
//						log.debug("Changing label name");
//						getLabel().setText(field.getName());
//					}
//					
//					
//				} catch (ReflectionException e) {}
				
				super.act(delta);
			}
		};
		box.clearChildren();
		Label label = box.getLabel();
		Image img = box.getImage();
		box.add(label).expandX().fillX();
		box.add(img).expandX();
		//box.pack();
		
		try {
			box.setChecked((boolean)getFieldValue(field, obj));
		} catch (Exception e) {
			log.error("[!!] couldnt change checked state of checkbox");
			e.printStackTrace();
		}
		
		// update field
		box.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				try {
					setFieldValue(field, obj, box.isChecked());
					log.debug("Changing field value");
				} catch (Exception e) {
					log.error("[!!] couldnt change checked state of checkbox on click");
					e.printStackTrace();
				}
			}
		});
		return box;
	}
	
	private Button createTextButtonOfField(Entity entity, Object obj, Field field) {
		Button button = createTextButtonGray(field.getName());

		button.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				
				log.debug("clicking..");
				
				if(field.getType() == EntityConsumer.class) {
					log.debug("...field is of type " + EntityConsumer.class + "...");
					try {
						EntityConsumer value = (EntityConsumer)getFieldValue(field, obj);
						value.accept(entity);
						log.debug("...accepted!");
					} catch (Exception e) {
					}
				}
			}
			
		});
		
		
		return button;
	}
	
	private Table createSelectOrDragInBox(Entity entity, Object obj, Field field) {
		TextButtonStyle style = new TextButtonStyle(skin.get(SkinNames.IMAGE_TEXT_BUTTON_STYLE_DARKEST_GRAY10, ImageTextButtonStyle.class));
		style.down = style.checked;
		style.checked = null;
		String text = DROPPABLE_TEX_TARGET_PREFIX +"none";
		DroppableTarget dropTarget = new DroppableTarget(text ,style) {
			@Override
			public void drop(Object dropObj) {
				super.drop(dropObj);
				if(dropObj instanceof TextureRegion) {
					RenderComponent render = Mappers.RENDER.get(entity);
					render.region = (TextureRegion)dropObj;
				}
			}
		};
		
		
		LabelStyle labelStyle = new LabelStyle(skin.get(SkinNames.LABEL_STYLE_10, LabelStyle.class));
		Label label = new Label(field.getName(), labelStyle);
		
		Table container = new Table();
		
		try {
			// in case the region is already initialized
			TextureRegion region = (TextureRegion)getFieldValue(field, obj);
			if(region != null) {
				String name = "unknown";
				TextureData data = region.getTexture().getTextureData();
				if(data instanceof FileTextureData) {
					name = EditorUtils.getNameOfRegion(region, TEX_FILE_NAME_MAX_LENGTH);
				}
				dropTarget.drop(region, DROPPABLE_TEX_TARGET_PREFIX + name);
			}

		} catch (Exception e) {
			log.error("couldnt create field instance therefore no selectOrDragInBox");
		}
		container.add(label).expandX().fillX();
		container.add(dropTarget).height(TF_MIN_HEIGHT).minWidth(TF_MIN_WIDTH);

		return container;
	}
	
	
	
	// -- writing / reading fields and annotation --
	
	
	/**a
	 * 
	 * @param cls
	 * @return all fields that are public or serilizable
	 */
	private Field[] getFieldsOf(Class<?> cls) {
		Field[] fields= ClassReflection.getDeclaredFields(cls); // all fields
		tmpFields.clear();
		for(Field field : fields) {
			// isAccess is for sth else and canAccess is not included in badlogic field class
			if((field.isPublic() && !field.isFinal()) || field.getDeclaredAnnotation(Serializable.class) != null) {
				tmpFields.add(field);
			}
		}
		Field[] fs = tmpFields.toArray(Field.class);
		tmpFields.clear();
		return fs;
//		return ClassReflection.getFields(cls); // only accessable public ones
	}
	
	/**handles the accessebility of private fields*/
	private Object getFieldValue(Field field, Object obj) {
		Object value = null;
		// isAccess is for sth else and canAccess is not included in badlogic field class
		boolean notAccessible = !field.isPublic(); 
		if(notAccessible)field.setAccessible(true);
		try {
			value = field.get(obj);
		} catch (ReflectionException e) {
			log.error("couldnt get value of field " + field);
			e.printStackTrace();
		}
		if(notAccessible)field.setAccessible(false);
		return value;
	}
	
	/**handles the accessebility of private fields*/
	private void setFieldValue(Field field, Object obj, Object newValue) {
		if(field.isFinal())return;
		// isAccess is for sth else and canAccess is not included in badlogic field class
		boolean notAccessible = !field.isPublic();
		if(notAccessible)field.setAccessible(true);
		try {
			field.set(obj, newValue);
			handleAnnotation(obj, field);
		} catch (ReflectionException e) {
			log.error("couldnt set value of field " + field);
			e.printStackTrace();
		}
		if(notAccessible)field.setAccessible(false);
	}
	
	private void handleAnnotation(Object obj, Field field) {
		Annotation anno = field.getDeclaredAnnotation(Invokable.class);
		if(anno == null)return;
		Invokable call = anno.getAnnotation(Invokable.class);
		String methodName = call.name();
		try {
			Method methodField = ClassReflection.getDeclaredMethod(obj.getClass(), methodName);
			boolean notAccessible = !methodField.isPublic();
			if(notAccessible)methodField.setAccessible(true);
			methodField.invoke(obj);
			if(notAccessible)methodField.setAccessible(false);
		} catch (Exception e) {
			log.error("Could not find/call the method " + methodName + " provided in annotation " + Invokable.class.getSimpleName() + ". Error: " + e.toString());
		}
		
	}
	
	

	// -- util --
	
	
	
	private boolean isComponent(Object comp) {
		return comp instanceof Component;
	}
	
	private boolean isSystem(Object sys) {
		return sys instanceof EntitySystem;
	}
	
	private String getFormattedName(Object compOrSys) {
		
		String name = "";
		String className = "";
		if(compOrSys instanceof String)className = (String)compOrSys;
		else className = compOrSys.getClass().getSimpleName();
		
		if(className.equals("")) {// if the underlying class is anonymous
			className =  isComponent(compOrSys)? "Component" : ( isSystem(compOrSys) ? "EntitySytem" : "Object"); 
		}
		
		String[] split = className.split("(?=\\p{Upper})");
		for(String s : split)name += s + " ";
		//log.debug("className: " + className + " newName " + name);
		return name;
	}
	
	
	/**
	 * 
	 * @param <T>
	 * @param a
	 * @param b
	 * @param type the type to check the equality
	 * @param identity if true == will be used else a.equals(b)
	 * @return
	 */
	private <T> boolean equalByType(Object a, Object b, T type, boolean identity) {
		if(a == null || b == null)return false;
		return  a.getClass() == type && b.getClass() == type && ( identity ? (T)a ==(T)b : ((T)a).equals((T)b));
	}
	
	/**doesnt include objects /classes/enums/interfaces*/
	private @Null Object parsePrimitiveType(String value, Class<?> type) throws Exception{
		if(type == int.class)return Integer.parseInt(value);
		else if(type == float.class)return Float.parseFloat(value);
		else if(type == double.class)return Double.parseDouble(value);
		else if(type == long.class)return Long.parseLong(value);
		else if(type.isAssignableFrom(String.class))return value;
		else if(type == boolean.class)return Boolean.parseBoolean(value);
		//else if(castType instanceof Object) {}
		return null;
	}

	
	
}
  