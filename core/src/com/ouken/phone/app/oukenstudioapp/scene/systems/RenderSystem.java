package com.ouken.phone.app.oukenstudioapp.scene.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.ouken.phone.app.oukenstudioapp.scene.Scene;
import com.ouken.phone.app.oukenstudioapp.scene.common.Families;
import com.ouken.phone.app.oukenstudioapp.scene.common.Mappers;
import com.ouken.phone.app.oukenstudioapp.scene.components.RenderComponent;
import com.ouken.phone.app.oukenstudioapp.scene.components.TransformComponent;
import com.ouken.phone.app.oukenstudioapp.scene.uitls.TransformComponentComparator;
import com.ouken.phone.app.oukenstudioapp.scene.uitls.TransformSortedIteratingSystem;

public class RenderSystem extends TransformSortedIteratingSystem{

	private static final Logger log = new Logger(RenderSystem.class.getName(), Logger.DEBUG);
	
	private Scene scene;
	private SpriteBatch batch;
	private Vector2 tmp = new Vector2();
	
	public RenderSystem(Scene scene) {
		super(Families.RENDER, new TransformComponentComparator());
		this.scene = scene;
		batch = scene.getBatch();
	}
	
	@Override
	public void update(float deltaTime) {
		batch.flush();
		batch.begin();
		//batch.enableBlending();
		super.update(deltaTime);
		//batch.disableBlending();
		batch.end();
		batch.flush();
	}


	
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TransformComponent transform = Mappers.TRANSFORM.get(entity);
		if(!transform.visible)return;
		RenderComponent render = Mappers.RENDER.get(entity);
		if(render == null)return;
		TextureRegion region = render.region;
		
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
		
		if(region != null) {
			render.draw(batch, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
			batch.flush();
		}
		
	}



	

	
}
