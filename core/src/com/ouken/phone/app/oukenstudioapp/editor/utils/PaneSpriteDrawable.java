package com.ouken.phone.app.oukenstudioapp.editor.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

public class PaneSpriteDrawable extends SpriteDrawable {

	private TextureRegion region, topLeft, topRight, bottomLeft, bottomRight, center;
	private Color tmp = new Color();
	private boolean disableCenterDraw;
	
	public PaneSpriteDrawable(TextureRegion shapeRegion) {
		this(shapeRegion, Color.WHITE);
	}
	
	public PaneSpriteDrawable(TextureRegion shapeRegion, Color color) {
		Sprite sprite = new Sprite(shapeRegion);
		sprite.setColor(color);
		setSprite(sprite);

		this.region = shapeRegion;
		
		int w = region.getRegionWidth();
		int halfW = w /2; // rounds down => 2* halfW <= w
		int h = region.getRegionHeight();
		int halfH = h/2; // rounds down => 2*halfH <= h
		
		// tex region coord system 0,0 is in top left corner
		bottomLeft = new TextureRegion(shapeRegion, 0, h - halfH, halfW, halfH);
		bottomRight = new TextureRegion(shapeRegion, w - halfW, h - halfH, halfW, halfH);
		topLeft = new TextureRegion(shapeRegion, 0, 0, halfW, halfH);
		topRight = new TextureRegion(shapeRegion, w - halfW, 0, halfW, halfH);
		
		center = new TextureRegion(shapeRegion, halfW, halfH, 1,1);
		
	}
	
	
	@Override
	public void draw(Batch batch, float x, float y, float width, float height) {
		batch.flush();

		tmp.set(batch.getColor());
		//batch.setColor(getSprite().getColor());
		Color c = getSprite().getColor();
		batch.setColor(c.r, c.g, c.b, c.a * tmp.a);
		
		// bottom left
		batch.draw(bottomLeft, x, y);
		// bottom right
		batch.draw(bottomRight, x + width - bottomRight.getRegionWidth(), y);
		// top left
		batch.draw(topLeft, x, y + height - topLeft.getRegionHeight());
		// top right
		batch.draw(topRight, x + width - topRight.getRegionWidth(), y + height - topRight.getRegionHeight());
		
		
		// left
		batch.draw(center, x,y + bottomLeft.getRegionHeight(), bottomLeft.getRegionWidth(),
				height - 2*bottomLeft.getRegionHeight());
		// top
		batch.draw(center, x + topLeft.getRegionWidth(), y + height - topLeft.getRegionHeight(), 
				width - 2*topLeft.getRegionWidth(),
				topLeft.getRegionHeight());
		// right
		batch.draw(center, x + width - bottomRight.getRegionWidth(), 
				y + bottomRight.getRegionHeight(), 
				bottomRight.getRegionWidth(),
				height - 2*bottomRight.getRegionHeight());
		// bottom
		batch.draw(center, x + bottomLeft.getRegionWidth(), y, width - 2*bottomLeft.getRegionWidth(),
				bottomLeft.getRegionHeight());
		
		// center
		if(!disableCenterDraw)batch.draw(center, x + bottomLeft.getRegionWidth(), y + bottomLeft.getRegionHeight(),
								width - 2*bottomLeft.getRegionWidth(), height - 2*bottomLeft.getRegionHeight());
		
		
		batch.flush();
		batch.setColor(tmp);
		
		
	}
	
	
	public void setColor(Color color) {
		getSprite().setColor(color);
	}
	
	public void disableCenterDrawing(boolean disable) {
		disableCenterDraw = disable;
	}
	

	
}
