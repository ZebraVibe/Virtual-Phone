package com.ouken.phone.app.oukenstudioapp.scene.entites;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ouken.phone.app.oukenstudioapp.assets.AssetDescriptors;
import com.ouken.phone.app.oukenstudioapp.assets.RegionNames;
import com.ouken.phone.app.oukenstudioapp.scene.Scene;
import com.ouken.phone.app.oukenstudioapp.scene.common.Mappers;
import com.ouken.phone.app.oukenstudioapp.scene.components.RenderComponent;
import com.ouken.phone.app.oukenstudioapp.scene.components.SceneComponent;
import com.ouken.phone.app.oukenstudioapp.scene.components.TransformComponent;

/**For a new entity type listed in the context menu simply add a createEntityXYZ() method, create a corresponding enum
 * and add it to the {@link #create(EntityType)} method*/
public class EntityFactory {
	
	private static final float DEFAULT_SIZE = 32;
	
	private Scene scene;
	private PooledEngine engine;
	
	public EntityFactory(Scene scene) {
		this.engine = scene.getEngine();
		this.scene = scene;
	}
	
	
	/**Use this as base for every other createEntityXYZ method to ensure it has a {@link TransformComponent}!!*/
	private Entity createEmpty(Entity entity) {
		engine.addEntity(entity);// (ATM NOT) adding transform component when added to engine
		
		TransformComponent transform = entity.getComponent(TransformComponent.class);
		if(transform == null) {
			transform = engine.createComponent(TransformComponent.class);
			entity.add(transform);
		}

		transform.setEntity(entity);
		transform.setScene(scene);
		
		if(entity instanceof SceneEntity)return entity; // entityexplorer is not created yet at sceneEntity adding time
		
		// by default only adds the sceneNode as parent
		TransformComponent parentTransform = Mappers.TRANSFORM.get(scene.getSceneEntity());
		if(parentTransform != null)transform.setParent(parentTransform);
		
		transform.width = DEFAULT_SIZE;
		transform.height = DEFAULT_SIZE;
		return entity;
	}
	
	/**creates an empty entity*/
	public Entity createEmpty() {
		return createEmpty(engine.createEntity());
	}
	
	public SceneEntity createSceneEntity() {
		SceneEntity e = (SceneEntity)createEmpty(new SceneEntity(scene));
		e.add(new SceneComponent());
		return e;
	}
	
	
	/**creates an entity with and Render Component added to it*/
	public Entity createSprite() {
		Entity entity = createEmpty();
		RenderComponent render = engine.createComponent(RenderComponent.class);
		render.region = scene.getEditor().getAssets().get(AssetDescriptors.EDITOR_UI).findRegion(RegionNames.PANE);
		return entity.add(render);
	}
	
	public static enum EntityType {
		EMPTY,SPRITE;
	}
	
	public Entity create(EntityType type) {
		switch (type) {
		case SPRITE:
			return createSprite();
		default:
			return createEmpty();
		}
	}

	
}
