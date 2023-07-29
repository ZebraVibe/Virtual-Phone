package com.ouken.phone.app.oukenstudioapp.scene.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ouken.phone.app.oukenstudioapp.editor.utils.Pane;
import com.ouken.phone.app.oukenstudioapp.scene.Scene;
import com.ouken.phone.app.oukenstudioapp.scene.common.Families;
import com.ouken.phone.app.oukenstudioapp.scene.common.Mappers;
import com.ouken.phone.app.oukenstudioapp.scene.components.TransformComponent;
import com.ouken.phone.app.oukenstudioapp.scene.entites.SceneEntity;

public class RenderDebugSystem extends IteratingSystem{
	
	private static final Logger log = new Logger(RenderDebugSystem.class.getName(), Logger.DEBUG);
	
	private Scene scene;
	private ShapeRenderer renderer;
	private Viewport viewport;
	
	private Vector2 tmp = new Vector2();
	private Entity currentSelected;
	
	public final Color debugColor, highlightedColor, tmpColor = new Color();
	
	public boolean debug = true;
	public boolean debugWorldBounds = false;
	
	
	
	public RenderDebugSystem(Scene scene) {
		super(Families.TRANSFORM);
		this.scene = scene;
		this.renderer = scene.getRenderer();
		this.viewport = scene.getViewport();
		
		debugColor = Pane.DARKEST_GRAY.cpy();
//		debugColor = Pane.GREEN.cpy();
//		debugColor.a = 0.5f;
		highlightedColor = Pane.GREEN.cpy();
		
		
		log.debug("creating " + getClass().getSimpleName());
	}
	
	@Override
	public void update(float deltaTime) {
		if(!debug)return;
		
		currentSelected = scene.getEditor().getEntityExplorer().getSelectedValue();
		
		
		renderer.setColor(debugColor);
		renderer.setProjectionMatrix(viewport.getCamera().combined);
		renderer.begin(ShapeType.Line);
		super.update(deltaTime);
		renderer.end();
	}
	

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		if(entity == null)return;
		TransformComponent transform = Mappers.TRANSFORM.get(entity);
		
		if(!debugWorldBounds && entity instanceof SceneEntity)return;
		
		transform.localToWorldCoord(tmp.setZero());
		float x = tmp.x;//transform.x;//+ parentX;
		float y = tmp.y;//transform.y; // + parentY;
		float originX = transform.originX;
		float originY = transform.originY;
		float width = transform.width;
		float height = transform.height;
		float scaleX = transform.scaleX;
		float scaleY = transform.scaleY;
		float rotation = transform.rotation;
		
		
		boolean selected = isSelected(entity);
		
		tmpColor.set(renderer.getColor());
		if(selected) {
			renderer.end();
			renderer.setColor(highlightedColor);
			renderer.begin(ShapeType.Line);
		}
		renderer.rect(x, y, originX, originY, width, height, scaleX, scaleY, rotation);
		if(selected) {
			renderer.end();
			renderer.setColor(tmpColor);
			renderer.begin(ShapeType.Line);
		}
		
	}
	

	
	private boolean isSelected(Entity entity) {
		return currentSelected != null && currentSelected == entity;
	}


	
	
	
}
