package com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item.group;

import java.util.function.Consumer;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ouken.phone.app.oukenstudioapp.assets.RegionNames;
import com.ouken.phone.app.oukenstudioapp.editor.ui.menu.EntityExplorerTreeContextMenu;
import com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item.CreateMenuItem;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tabs.EntityExplorer;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.EntityNode;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.SceneNode;
import com.ouken.phone.app.oukenstudioapp.editor.ui.window.TextFieldWindow;
import com.ouken.phone.app.oukenstudioapp.editor.ui.window.TextFieldWindow.TFValidatable;
import com.ouken.phone.app.oukenstudioapp.scene.common.Mappers;
import com.ouken.phone.app.oukenstudioapp.scene.components.TransformComponent;
import com.ouken.phone.app.oukenstudioapp.scene.entites.EntityFactory.EntityType;

public class EditEntityNodeMenuItemGroup extends EditNodeMenuItemGroup<EntityExplorer, EntityNode, Entity> {

	private Skin skin;
	private ShapeRenderer renderer;
	
	public EditEntityNodeMenuItemGroup(EntityExplorer explorer, Skin skin, ShapeRenderer renderer) {
		super(explorer, skin, renderer);
		this.skin = skin;
		this.renderer = renderer;
		init();
	}


	private void init() {
		//remove old create entity menu item
		getItems().removeIndex(0);
		
		// insert other create entity items;
		int startIndex = 0;
		EntityType[] values = EntityType.values();
		
		for(int i = startIndex; i < values.length; i++) {
			EntityType type = values[i];
//			if(type == EntityType.EMPTY)continue;
			
			String name = type.toString().toLowerCase();
			name = name.substring(0, 1).toUpperCase() + (name.length() > 1 ? name.substring(1, name.length()).toLowerCase() : "");
			
			insert(startIndex++, new CreateMenuItem<EntityNode>("Create " + name, skin.getRegion(getCreateIconRegionName()), skin, renderer) {
				@Override
				public void onClick(EntityNode selected, TextFieldWindow window) {
					onCreateClick(selected, window,tf -> {
						String text = tf.getText();
						if (text != null && !text.equals(""))
							return true;
						return false;
					}, tf -> {
						boolean asRootOnly = selected == null || selected instanceof SceneNode;
						// adds an entity of a type
						EntityNode node = getExplorer().addEntityToSelectedOrAsRootNode(type, tf.getText(), asRootOnly);
						if (selected != null && selected.getValue() != null)getTree().getSelection().fireChangeEvent();// changed(selected.getActor());
						node.expandTo();
						
						setupEntity(node.getValue());
						
						getExplorer().chooseNode(node);
					});
					
				}
				
				private void setupEntity(Entity child) {
					if(child == null)return;
					TransformComponent transform = Mappers.TRANSFORM.get(child);
					if(transform != null)transform.positionBelowCamera();
					
				}
			});
		}
		
	}

	
	// create empty entity (standard)

//	@Override
//	public void onCreateClick(EntityNode selected, TextFieldWindow window, TFValidatable validate,Consumer<TextField> onSuccess) {
//		super.onCreateClick(selected, window, tf -> {
//			String text = tf.getText();
//			if (text != null && !text.equals(""))
//				return true;
//			return false;
//		}, tf -> {
//			boolean asRootOnly = selected == null || selected instanceof SceneNode;
//			// adds an empty entity
//			EntityNode node = getExplorer().addEntityToSelectedOrAsRootNode(tf.getText(), asRootOnly);
//			if (selected != null && selected.getValue() != null)getTree().getSelection().fireChangeEvent();// changed(selected.getActor());
//			node.expandTo();
//		});
//	}
	
	@Override
	public void onRenameClick(EntityNode selected, TextFieldWindow window, TFValidatable validate,Consumer<TextField> onSuccess) {
		super.onRenameClick(selected, window, tf -> {
			String text = tf.getText();
			if (selected != null && text != null && !text.equals("")) {
				return true;
			}
			return false;
		}, tf -> {
			selected.setLabelText(tf.getText());
			TransformComponent transform = Mappers.TRANSFORM.get(selected.getValue());
			if(transform != null)transform.setName(tf.getText());
		});
	}
	
	
	@Override
	public void onDeleteClick(EntityNode selected) {
		// logic already inside overriden explorer.removeNode
		
		
//		if (selected == null || selected instanceof SceneNode)return;
//		Entity entity = selected.getValue();
//		TransformComponent transform;
//		if((transform = Mappers.TRANSFORM.get(entity)) == null)return;
//		transform.removeFromEgine();
	}
	
	
	@Override
	public void onDupliateClick(EntityNode selected) {
		if (selected == null || selected instanceof SceneNode)return;
	}
	
	
	@Override
	public boolean openDeleteWindowOn(EntityNode selected) {
		return selected != null && !(selected instanceof SceneNode);
	}
	
	
	@Override
	public String getCreateIconRegionName() {
		return RegionNames.ENTITY_ICON;
	}
	
	@Override
	public String getCreateMenuItemName() { 
		return "Create Empty"; 
	}
	
}
