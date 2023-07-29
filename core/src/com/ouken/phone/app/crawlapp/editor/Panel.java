package com.ouken.phone.app.crawlapp.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.ouken.phone.utils.color.ColorConverter;
import com.ouken.phone.utils.font.FontSize;
import com.ouken.phone.utils.font.Fonts;

public class Panel extends Table {
	
	protected static final Color defaultBackgroundColor = ColorConverter.hexToRGBA8888(0x303030);
	protected static final Color defaultBorderColor = Color.BLACK.cpy();
	
	// -- attributes --
	protected final ShapeRenderer renderer;
	protected Color tmp = new Color();
	protected Color backgroundColor = new Color(defaultBackgroundColor);
	protected Color topBorderColor = new Color(defaultBorderColor);
	protected Color bottomBorderColor = new Color(defaultBorderColor);
	protected Color leftBorderColor = new Color(defaultBorderColor);
	protected Color rightBorderColor = new Color(defaultBorderColor);

	private boolean hasShadow;
	private boolean hasInnerShadow;
	private boolean enableBorder = true;
	private float shadowAlpha = 0.27f;
	
	public Panel(ShapeRenderer renderer) {
		this.renderer = renderer;
	}
	
	
	
	// -- public methods --
	
	
	/**@param backgroundColor is not drawn if null*/
	public Panel bg(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}
	
	
	// -- border --
	
	/**@param enable set to true by default*/
	public Panel enableBorder(boolean enable) {
		this.enableBorder = enable;
		return this;
	}
	
	/**@param borderColor is not drawn if null*/
	public Panel border(Color borderColor) {
		return topBorder(borderColor).bottomBorder(borderColor).leftBorder(borderColor).rightBorder(borderColor);
	}
	
	/**@param borderColor is not drawn if null*/
	public Panel topBorder(Color borderColor) {
		this.topBorderColor = borderColor;
		return this;
	}
	
	/**@param borderColor is not drawn if null*/
	public Panel bottomBorder(Color borderColor) {
		this.bottomBorderColor = borderColor;
		return this;
	}
	
	/**@param borderColor is not drawn if null*/
	public Panel leftBorder(Color borderColor) {
		this.leftBorderColor = borderColor;
		return this;
	}
	
	/**@param borderColor is not drawn if null*/
	public Panel rightBorder(Color borderColor) {
		this.rightBorderColor = borderColor;
		return this;
	}
	
	
	// -- shadow --
	
	public Panel shadow(boolean enable) {
		hasShadow = enable;
		return this;
	}
	
	
	public Panel innerShadow(boolean enable) {
		hasInnerShadow = enable;
		return this;
	}
	
	
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		if(batch.isDrawing())batch.end();
		Color old = renderer.getColor();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		renderer.setAutoShapeType(true);
		renderer.setProjectionMatrix(getStage().getViewport().getCamera().combined);
		renderer.begin(ShapeType.Filled);
		applyTransform(renderer, computeTransform());
		
		// shadow
		if(hasShadow)drawShadow();
		
		// background
		drawBg();
		
		renderer.end();
		renderer.setColor(old);
		renderer.setAutoShapeType(false);
		resetTransform(renderer);
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
		super.draw(batch, parentAlpha);
		batch.end();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		renderer.setAutoShapeType(true);
		renderer.setProjectionMatrix(getStage().getViewport().getCamera().combined);
		renderer.begin(ShapeType.Filled);
		applyTransform(renderer, computeTransform());
		
		// inner shadow
		if(hasInnerShadow)drawInnerShadow();
		
		// border
		drawBorder();
		
		renderer.end();
		renderer.setColor(old);
		renderer.setAutoShapeType(false);
		resetTransform(renderer);
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
	}
	
	// -- private methods --
	
	private void drawBg() {
		if(backgroundColor == null)return;
		renderer.set(ShapeType.Filled);
		renderer.setColor(backgroundColor);
		rect(0, 0, getWidth(), getHeight());
		renderer.flush();
	}
	
	private void drawBorder() {
		if(!enableBorder)return;
		renderer.set(ShapeType.Filled);
		// top
		if (topBorderColor != null) {
			renderer.setColor(topBorderColor);
			rect(0, getHeight(), getWidth(), 1);
		}
		// bottom
		if (bottomBorderColor != null) {
			renderer.setColor(bottomBorderColor);
			rect(0, -1, getWidth(),  1);
		}
		// left
		if (leftBorderColor != null) {
			renderer.setColor(leftBorderColor);
			rect(-1, 0, 1, getHeight());
		}
		// right
		if (rightBorderColor != null) {
			renderer.setColor(rightBorderColor);
			rect(getWidth() , 0, 1, getHeight());
		}
		renderer.flush();
	}
	
	private void drawShadow() {
		renderer.set(ShapeType.Filled);
		renderer.setColor(0, 0, 0, shadowAlpha);
		
		float xDiff = 2;
		float yDiff = 2;
		
		// bottom
		float x = xDiff;
		float y = -yDiff;
		float w = getWidth() - 1;
		float h = 2;
		
		rect(x, y, w, h);
		// right
		x = getWidth();
		y = -yDiff + 1;
		w = 2;
		h = getHeight() -1;
		rect(x, y, w, h);
		renderer.flush();
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
	private void drawInnerShadow() {
		renderer.set(ShapeType.Line);
		renderer.setColor(0, 0, 0, shadowAlpha);
		rect( 1 , 1, getWidth() -1, getHeight() -1);
		renderer.flush();
	}
	
	private void rect(float x, float y, float width, float height) {
		renderer.rect(x, y, getOriginX(), getOriginY(), width, height, 1, 1, getRotation());
	}
	
	
}
