package com.ouken.phone.app.oukenstudioapp.editor.utils;

import java.util.PrimitiveIterator.OfDouble;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

public class ScrollablePane extends Pane {
	
	public static final float DEFAULT_PAD = 2;
	
	
	private final Color scrollColor = new Color(DARK_GRAY.cpy()), scrollBarColor = new Color(LIGHT_GRAY.cpy());
	private TextureRegion knobRegion, knobCenterRegion;
	private ScrollPane scroll;

	private boolean vertical, horizontal;
	
	// -- constructors --
	
	/** set the default widget padding to {@value ScrollablePane#DEFAULT_PAD} */
	public ScrollablePane(Actor widget, boolean vertical, boolean horizontal, TextureRegion knobRegion, ShapeRenderer renderer) {
		this(widget, DEFAULT_PAD, vertical, horizontal, knobRegion, renderer);
	}
	
	public ScrollablePane(Actor widget, float widgetPad, boolean vertical, boolean horizontal, TextureRegion knobRegion, ShapeRenderer renderer) {
		super(widgetPad, renderer);
		if(knobRegion == null)throw new IllegalArgumentException("knobRegion must not be null!");
		
		this.knobRegion = knobRegion;
		this.vertical = vertical;
		this.horizontal = horizontal;
		
		ScrollPaneStyle style = new ScrollPaneStyle();
		style.vScrollKnob = style.hScrollKnob = new PaneSpriteDrawable(knobRegion, scrollBarColor);
		style.hScroll = style.vScroll = new PaneSpriteDrawable(knobRegion, scrollColor);
		
		scroll = new ScrollPane(widget, style);
		scroll.setFlickScroll(false);
		scroll.setScrollbarsOnTop(true);
		scroll.setScrollingDisabled(!horizontal, !vertical);
	
		add(scroll).expand().fill();
		addScrollListener();
	}
	
	
	
	// -- public methods --
	
	
	/** usable when the actor has a stage*/
	public void setScrollFocus() {
		if(this.getStage() != null) {
			this.getStage().setScrollFocus(scroll);
		}
	}
	
	
	public void removeScrollFocus() {
		if(this.getStage() != null) {
			this.getStage().setScrollFocus(null);
		}
	}
	
	public ScrollPane getScrollPane() {
		return scroll;
	}
	
	public ScrollablePane setScrollBarPosition(boolean bottom, boolean right) {
		scroll.setScrollBarPositions(bottom, right);
		return this;
	}
	
	public float getMinScrollBarWidth() {
		return knobRegion.getRegionWidth();
	}
	
	public float getMinScrollBarHeight(){
		return knobRegion.getRegionHeight();
	}
	
	
	public ScrollablePane setScrollColor(Color c) {
		scrollColor.set(c);
		
		return this;
	}
	
	public ScrollablePane setScrollBarColor(Color c) {
		scrollBarColor.set(c);
		return this;
	}
	
	
	 // -- private methods --

	private SpriteDrawable createScrollBarSprite(Sprite sprite, Color c) {
		return new SpriteDrawable(sprite) {
			
			@Override
			public void draw(Batch batch, float x, float y, float width, float height) {
				// left / bottom
				
				Color old = batch.getColor();
				batch.setColor(c);
				
				batch.draw(knobRegion, 
						x, y, 
						knobRegion.getRegionWidth(), knobRegion.getRegionHeight());
				
				if (vertical) {

					// center
					batch.draw(knobCenterRegion, 
							x , y + knobRegion.getRegionHeight() / 2f,
							knobRegion.getRegionWidth(), height - knobRegion.getRegionHeight());
					// top
					batch.draw(knobRegion, 
							x, y + height - knobRegion.getRegionHeight(), 
							knobRegion.getRegionWidth(), knobRegion.getRegionHeight());
					
				}
				if (horizontal) { // horizontal
					
					// center
					batch.draw(knobCenterRegion, 
							x + knobRegion.getRegionWidth() / 2f, y,
							width - knobRegion.getRegionWidth(), knobRegion.getRegionHeight());
					// right
					batch.draw(knobRegion, 
							x + width - knobRegion.getRegionWidth(), y, 
							knobRegion.getRegionWidth(), knobRegion.getRegionHeight());
					
				}
				batch.flush();
				batch.setColor(old);

			}
			
		};
	}
	
	private void addScrollListener() {
		addListener(new InputListener() {
			
			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				setScrollFocus();
			}
			
			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				removeScrollFocus();
			}
			
		});
	}
	
}
