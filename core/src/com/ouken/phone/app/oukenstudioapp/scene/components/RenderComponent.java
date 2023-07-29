package com.ouken.phone.app.oukenstudioapp.scene.components;

import java.util.function.Consumer;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.ouken.phone.Phone;
import com.ouken.phone.app.crawlapp.assets.RegionNames;
import com.ouken.phone.app.oukenstudioapp.assets.AssetDescriptors;
import com.ouken.phone.app.oukenstudioapp.scene.common.Mappers;
import com.ouken.phone.app.oukenstudioapp.scene.uitls.EntityConsumer;
import com.ouken.phone.app.oukenstudioapp.scene.uitls.Serializable;

public class RenderComponent implements Component, Poolable {
	
	private static final Logger log = new Logger(RenderComponent.class.getName(), Logger.DEBUG);
	
	public TextureRegion region;
	@Serializable
	private final EntityConsumer setNativeSize;
	

	public RenderComponent() {
		setNativeSize = entity -> {
			if(entity == null || region == null)return;
			
			TransformComponent t = Mappers.TRANSFORM.get(entity);
			
			if(t == null)return;
			
			t.width = region.getRegionWidth();
			t.height = region.getRegionHeight();
			
		};
	}
	
	public void setNativeSize(Entity entity) {
		setNativeSize.accept(entity);
	}
	
	public void draw (Batch batch, float x, float y, float originX, float originY, float width, float height,
			float scaleX, float scaleY, float rotation) {
		batch.draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
	}
	

	@Override
	public void reset() {
		region = null;
	}
	
	
}
