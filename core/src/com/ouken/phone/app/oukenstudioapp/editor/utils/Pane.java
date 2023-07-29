package com.ouken.phone.app.oukenstudioapp.editor.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Align;
import com.ouken.phone.utils.color.ColorConverter;

/** A Pane containts an input listener actor with setFillParent(true) to
 * look for input events*/
public class Pane extends Table{
	
	// -- constants --
	public static final Color LIGHT_GRAY = ColorConverter.hexToRGBA8888(0x505050);
	public static final Color GRAY = ColorConverter.hexToRGBA8888(0x3c3c3c);
	public static final Color DARK_GRAY = ColorConverter.hexToRGBA8888(0x282828);
	public static final Color DARKEST_GRAY = ColorConverter.hexToRGBA8888(0x191919);
	public static final Color GREEN = ColorConverter.hexToRGBA8888(0x65ff00);
	
	public static final int wunits = 2;
	
	public static final int DEFAULT_BORDER_SIZE = 1;
	public static final int DEFAULT_SPLIT_GAP = 3;
	public static final int DEFAULT_SPLIT_LINE_THIKNESS = 1;
	public static final int PAD_2 = 2;
	public static final int PAD_4 = 4;
	
	// -- atributes --
	private ShapeRenderer renderer;
	private final Rectangle BOUNDS = new Rectangle();
	private Color paneColor = new Color(GRAY), tmpColor = new Color(), splitLineColor = new Color(DARK_GRAY);
	private Pane borderPane, shadowPane;
	
	public boolean smoothTopLeft, smoothTopRight, smoothBottomLeft, smoothBottomRight;
	private boolean disableShapeDrawing;
	private boolean enableBorder;
	private boolean isHSplit, isVSplit;
	
	private float xSplitPos, ySplitPos, xSplitGap, ySplitGap, splitLineThikness;
	private float borderSize;
	
	private Widget inputListenerActor;
	
	// -- constructors --
	public Pane(ShapeRenderer renderer) {
		this.renderer = renderer;
		smoothEdges();
		setTransform(true);
		
		inputListenerActor = new Widget();
		inputListenerActor.setFillParent(true);
		
		addActor(inputListenerActor);
		setTouchable(Touchable.enabled);
		
	}
	
	public Pane(float pad, ShapeRenderer renderer) {
		this(renderer);
		pad(pad);
	}
	
	// -- public methods --
	
	public ShapeRenderer getShapeRenderer() {
		return renderer;
	}
	
	public Widget getInputListenerActor() {
		return inputListenerActor;
	}

	public Pane paneColor(Color c) {
		if(c == null)return this;
		else paneColor.set(c);
		return this;
	}
	
	
	// -- smooth edegs 
	
	public Pane smoothEdges(boolean topLeft, boolean topRight, boolean bottomLeft, boolean bottomRight) {
		smoothTopLeft = topLeft;
		smoothTopRight = topRight;
		smoothBottomLeft = bottomLeft;
		smoothBottomRight = bottomRight;
		return this;
	}
	
	/**smoothes all edges*/
	public Pane smoothEdges() {
		smoothEdges(true);
		return this;
	}
	
	/**smoothes all edges*/
	public Pane smoothEdges(boolean smooth) {
		smoothEdges(smooth, smooth, smooth, smooth);
		return this;
	}
	
	
	/**Set to false by default*/
	public Pane disableShapeDrawing(boolean disable) {
		this.disableShapeDrawing = disable;
		return this;
	}
	
	// -- shadow --
	
	public Pane addShadow() {
		Color c = Color.BLACK.cpy();
		c.a = 0.02f;
		shadowPane = new Pane(renderer).paneColor(c);
		shadowPane.setVisible(false);
		addActor(shadowPane);
		return this;
	}
	
	
	// -- border --
	
	
	public Pane enableBorder(boolean enable, float borderSize, Color color) {
		this.enableBorder = enable;
		
		if (enable) {

			if (borderPane == null) {
				Pane p = Pane.this;
				borderPane = new Pane(renderer) {

					@Override
					public void act(float delta) {

						super.act(delta);
					}
					
					@Override
					public void draw(Batch batch, float parentAlpha) {
						smoothEdges(p.smoothTopLeft, p.smoothTopRight, p.smoothBottomLeft, p.smoothBottomRight);
//						borderPane.setOrigin(Align.center);
//						borderPane.setScale((p.getWidth() + 2* borderSize) / p.getWidth());

						// parent has set transform(true)
						setBounds(-borderSize, -borderSize, p.getWidth() + borderSize * 2,
								p.getHeight() + borderSize * 2);
						setOrigin(p.getOriginX(), p.getOriginY());
						setScale(p.getScaleX(), p.getScaleY());
						setRotation(p.getRotation());

						super.draw(batch, parentAlpha);
					}
					
				};

			}

//			borderPane.setFillParent(true);
			borderPane.setVisible(false);
			addActor(borderPane);

			borderPane.paneColor(color);
			this.borderSize = borderSize;
			
		}else {
			if(borderPane != null)borderPane.remove();
		}
		
		return this;
	}
	
	/**enables border drawing width a default size of {@value Pane#DEFAULT_BORDER_SIZE} and default color Green*/
	public Pane enableBorder(boolean enable) {
		return enableBorder(enable, DEFAULT_BORDER_SIZE, GREEN);
	}
	
	
	public Pane borderColor(Color color) {
		borderPane.paneColor(color);
		return this;
	}
	
	
	
	// -- split line --
	
	
	public Pane splitLineColor(Color color) {
		splitLineColor.set(color);
		return this;
	}
	
	public Pane splitLineThickness(float thickness) {
		splitLineThikness = thickness;
		return this;
	}
	
	
	/**creates a splitting line at the given position with a default split 
	 * gab of {@value Pane#DEFAULT_SPLIT_GAP}, thikness of {@value Pane#DEFAULT_SPLIT_LINE_THIKNESS}, 
	 * and the default split line color*/
	public Pane hSplitLine(float y) {
		return hSplitLine(DEFAULT_SPLIT_GAP, y);
	}
	
	/**creates a splitting line at the given position with a thikness 
	 * of {@value Pane#DEFAULT_SPLIT_LINE_THIKNESS}, 
	 * and the default split line color*/
	public Pane hSplitLine(float xGap, float y) {
		return hSplitLine(xGap, y, DEFAULT_SPLIT_LINE_THIKNESS, splitLineColor);
	}	
	
	public Pane hSplitLine(float xGap, float y, float thickness, Color color ) {
		isHSplit = true;
		xSplitGap = xGap;
		ySplitPos = y;
		splitLineThikness = thickness;
		splitLineColor.set(color); 
		return this;
	}
	
	
	
	/**creates a splitting line at the given position with a default split 
	 * gab of {@value Pane#DEFAULT_SPLIT_GAP}, thikness of {@value Pane#DEFAULT_SPLIT_LINE_THIKNESS}, 
	 * and the default split line color*/
	public Pane vSplitLine(float x) {
		return vSplitLine(DEFAULT_SPLIT_GAP, x);
	}
	
	/**creates a splitting line at the given position with a thikness 
	 * of {@value Pane#DEFAULT_SPLIT_LINE_THIKNESS}, 
	 * and the default split line color*/
	public Pane vSplitLine(float yGap, float x) {
		return vSplitLine(yGap, x, DEFAULT_SPLIT_LINE_THIKNESS, splitLineColor);
	}
	
	
	
	public Pane vSplitLine(float yGap, float x, float thickness, Color color) {
		isVSplit = true;
		ySplitGap = yGap;
		xSplitPos = x;
		splitLineThikness = thickness;
		splitLineColor.set(color); 
		return this;
	}
	

	
	// -- helper --
	
	public Rectangle getBoundingRectangle() {
		return BOUNDS.set(getX(), getY(), getWidth(), getHeight());
	}
	
	
	// -- draw --
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		applyTransform(batch, computeTransform());
		if(enableBorder)drawBorder(batch, parentAlpha);
		if(shadowPane != null)drawShadow(batch, parentAlpha);
		resetTransform(batch);
		
		if(batch.isDrawing())batch.end();
		tmpColor.set(renderer.getColor());
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		renderer.setAutoShapeType(true);
		renderer.setProjectionMatrix(getStage().getViewport().getCamera().combined);
		renderer.begin(ShapeType.Filled);
		applyTransform(renderer, computeTransform());
		
		
		if(!disableShapeDrawing)drawShape();
		if(isHSplit || isVSplit)drawSplit();
		
		renderer.end();
		renderer.setColor(tmpColor);
		renderer.setAutoShapeType(false);
		resetTransform(renderer);
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
		super.draw(batch, parentAlpha);

	}
	


	private void drawShape() {
		renderer.setColor(paneColor);
		renderer.set(ShapeType.Filled);
		
		// top
		rect(wunits, getHeight() - wunits, getWidth() - 2*wunits, wunits);
		// bottom
		rect(wunits, 0, getWidth() - 2*wunits, wunits);
		// left
		rect(0, wunits, wunits, getHeight() - 2*wunits);
		// right
		rect(getWidth() - wunits, wunits, wunits, getHeight() - 2* wunits);
		
		// center
		rect(wunits / 2, wunits / 2, getWidth() - wunits, getHeight() - wunits);

		if (!smoothTopLeft)rect(0, getHeight() - wunits, wunits, wunits);
		if (!smoothTopRight)rect(getWidth() - wunits, getHeight() - wunits, wunits, wunits);
		if (!smoothBottomLeft)rect(0, 0, wunits, wunits);
		if (!smoothBottomRight)rect(getWidth() - wunits, 0, wunits, wunits);
			
		renderer.flush();
	}
	
	
	private void drawBorder(Batch batch, float parentAlpha) {
		borderPane.draw(batch, parentAlpha);
	}
	
	
	private void drawSplit() {
		renderer.setColor(splitLineColor);
		renderer.set(ShapeType.Filled);
		if(isHSplit) {
			rect(xSplitGap, ySplitPos - splitLineThikness / 2f, getWidth() - 2* xSplitGap, splitLineThikness);
		}
		
		if(isVSplit) {
			rect(xSplitPos - splitLineThikness / 2f, splitLineThikness, ySplitGap,getHeight() - 2* ySplitGap);
		}
		renderer.flush();
	}
	
	
	private void rect(float x, float y, float width, float height) {
		renderer.rect(x, y, getOriginX(), getOriginY(), width, height, 1, 1, getRotation());
	}
	
	
	private void drawShadow(Batch batch, float parentAlpha) {
		int layers = 3;
		for(int i = layers; i > 0; i--) {
			float gap = 3*i;
			shadowPane.smoothEdges(smoothTopLeft, smoothTopRight, smoothBottomLeft, smoothBottomRight);
			shadowPane.setBounds(-gap / 2f, -gap, getWidth() + gap, getHeight() + 1.5f*gap);
			shadowPane.draw(batch, parentAlpha);
			batch.flush();
		}
	}
	
	
}
