package com.ouken.phone.utils.sprite;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.ouken.phone.utils.input.InputListener;
import com.ouken.phone.utils.input.InputState;

public class SpriteGroup extends Sprite {

	// -- constants --
	private static final Logger log = new Logger(SpriteGroup.class.getSimpleName(), Logger.DEBUG);
	private static final Color DEBUG_0 = Color.BLACK, DEBUG_1 = Color.RED, DEBUG_2 = Color.GREEN, DEBUG_3 = Color.YELLOW;
	private static final Color DEBUG_PACK = Color.BLUE;
	private static final boolean DEBUG_INPUT = false;

	// -- attributes --
	private SpriteGroup parent;
	private final Array<SpriteGroup> children = new Array<SpriteGroup>();
	private final Array<InputListener> listeners = new Array<InputListener>();
	private TextureRegion region;
	private final Vector2 tmp = new Vector2();
	private final Vector2 position = new Vector2();
	private final Rectangle packedBounds = new Rectangle();
	private final Array<InputState> inputsReceivedByChildren = new Array<InputState>(InputState.values());
	private Listenable listenable = Listenable.enabled;
	private boolean visible = true;
	private boolean isDrawDebugPackedBounds;
	private boolean isDrawDebug;
	
	

	// -- constructors --
	public SpriteGroup(TextureRegion region) {
		super(region);
		this.region = region;
	}

	public SpriteGroup() {
	}

	// -- public mnethods --
	
	/**enables the debug rendering of this instance not its children*/
	public void setDebug(boolean debug) {
		this.isDrawDebug = debug;
	}
	
	/**enables the debug rendering of this instance including all of its CURRENT children.*/
	public void debugAll() {
		setDebug(true);
		for(SpriteGroup c : children)c.debugAll();
	}
	
	
	/** Enables the debug drawing of packed bounds, which embrace the top most parent and all<br>
	 *  of his children. may be drawn exactly below the top most parent's bounds  */ 
	public void setDrawDebugPackedBounds(boolean debug) {
		isDrawDebugPackedBounds = debug;
	}
	
	
	/** [Important]: re-calculated each call. Not to be used to store information.
	 * @return an rectangle which embraces this instance and all of its children */
	public Rectangle getPackedBounds() {
		// set bounding rectangle
		packedBounds.set(getBoundingRectangle());
		if (hasChildren()) {
			// translate to children coordinate system
			packedBounds.setPosition(0,0);
			
			for (SpriteGroup child : children) {
				Rectangle rect = child.getPackedBounds();
				
				// left bottom
				if(rect.x < packedBounds.x) {
					packedBounds.width += packedBounds.x - rect.x;
					packedBounds.x = rect.x;
				}
				
				// left top
				if(rect.y < packedBounds.y) {
					packedBounds.height += packedBounds.y - rect.y;
					packedBounds.y = rect.y;
				}
				
				// right bottom
				if(rect.x + rect.width > packedBounds.x + packedBounds.width) {
					packedBounds.width += rect.x + rect.width - (packedBounds.x + packedBounds.width); 
				}
				
				// right top
				if(rect.y + rect.height > packedBounds.y + packedBounds.height) {
					packedBounds.height += rect.y + rect.height - (packedBounds.y + packedBounds.height); 
				}
				
			}
			
			// translate back to own coordinate sytsem
			packedBounds.x += getX();
			packedBounds.y += getY();
		}
		return packedBounds;
	}

	/*
	 * true by default. If set to false no inputs are received from this instance
	 * and his children
	 **/
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/*
	 * true by default. If false no inputs are received from this instance and his
	 * children
	 **/
	public boolean isVisible() {
		return visible;
	}

	
	
	
	
	
	/** [Important]: re-calculated each call. Not to be used to store information */
	public Vector2 getPosition() {
		return position.set(getX(), getY());
	}

	/** [Important]: re-calculated each call. Not to be used to store information */
	public Vector2 getCenterPosition() {
		return position.set(getX() + getWidth() / 2f, getY() + getHeight() / 2f);
	}
	
	/** [Important]: re-calculated each call. Not to be used to store information */
	public Vector2 getScreenPosition() {
		return localToScreenCoord(getPosition());
	}

	/** is set to enabled by default */
	public void setListenable(Listenable listenable) {
		if (listenable != null)
			this.listenable = listenable;
	}

	/** is set to enabled by default */
	public Listenable getListenable() {
		return listenable;
	}

	
	
	
	
	
	
	/** by default all inputs are already received by children */
	public void setInputReceivedByChildren(InputState state) {
		if (state != null)
			inputsReceivedByChildren.add(state);
	}

	/** All input states are received by the children */
	public void enableAllInputsReceivedByChildren() {
		inputsReceivedByChildren.clear();
		inputsReceivedByChildren.addAll(InputState.values());
	}

	/**
	 * No input state is received by the children. Does the same as clearing all the
	 * inputs
	 */
	public void disableAllInputsReceivedByChildren() {
		clearInputsReceivedByChildren();
	}

	/** by default all inputs are received by children */
	public boolean isInputReceivedByChildren(InputState state) {
		return inputsReceivedByChildren.contains(state, true);
	}

	public boolean hasInputsReceivedByChildren() {
		return !inputsReceivedByChildren.isEmpty();
	}

	public void removeInputReceivedByChildren(InputState state) {
		inputsReceivedByChildren.removeValue(state, true);
	}

	public void clearInputsReceivedByChildren() {
		inputsReceivedByChildren.clear();
	}

	
	
	
	
	public boolean hasListener() {
		return !listeners.isEmpty();
	}

	public void addListener(InputListener listener) {
		listeners.add(listener);
	}

	public void removeListener(int index) {
		listeners.removeIndex(index);
	}

	public boolean removeListener(InputListener listener) {
		return listeners.removeValue(listener, true);
	}

	public void clearListeners() {
		listeners.clear();
	}

	
	
	
	
	
	public boolean hasParent() {
		return parent != null;
	}

	public void setParent(SpriteGroup parent) {
		this.parent = parent;
	}

	public SpriteGroup getParent() {
		return parent;
	}

	public int getAncestorCount() {// vorfahre
		int z = 0;
		SpriteGroup current = this;
		while (current.hasParent()) {
			z++;
			current = current.getParent();
		}
		return z;
	}

	public Color getAncestorBasedColor() {
		switch (getAncestorCount()) {
		case 0:
			return DEBUG_0;
		case 1:
			return DEBUG_1;
		case 2:
			return DEBUG_2;
		default:// >= 2 ancestors
			return DEBUG_3;
		}
	}

	
	
	
	
	
	
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	public Array<SpriteGroup> getChildren() {
		return children;
	}

	public void addChild(SpriteGroup sprite) {
		children.add(sprite);
		sprite.setParent(this);
		childAdded(sprite);
	}

	public void addAllChildren(Array<SpriteGroup> children) {
		addAllChildren(children.toArray(SpriteGroup.class));
	}

	public void addAllChildren(SpriteGroup... children) {
		if (children != null)
			for (SpriteGroup child : children)
				if (child != null)
					addChild(child);
	}
	
	
	/**Fired after a child was added. Can be overridden*/
	public void childAdded(SpriteGroup child) {}
	
	/**Fired after a child was removed. Can be overridden*/
	public void childRemoved(SpriteGroup child) {}
	
	public void removeChild(int index) {
		SpriteGroup child = children.removeIndex(index);
		childRemoved(child);
	}

	public boolean removeChild(SpriteGroup child) {
		boolean removed = children.removeValue(child, true);
		childRemoved(child);
		return removed;
	}

	public void removeAllChildren(SpriteGroup... children) {
		if (children != null)
			for (SpriteGroup child : children)
				removeChild(child);
	}

	public void removeAllChildren(Array<SpriteGroup> children) {
		removeAllChildren(children.toArray(SpriteGroup.class));
	}
	
	/**does not fire a childRemoved event*/
	public void clearChildren() {
		this.children.clear();
	}

	
	
	
	
	
	
	/** translates the given local coordinates into screen coordinates */
	public Vector2 localToScreenCoord(Vector2 localCoord) {
		if (localCoord != null) {
			SpriteGroup current = this;
			while (current.hasParent()) {
				current = current.getParent();
				localCoord.x += current.getX();
				localCoord.y += current.getY();
			}
			return localCoord;
		}
		return null;
	}

	/** translates the given screen coordinates into local coordinates */
	public Vector2 screenToLocalCoord(Vector2 screenCoord) {
		if (screenCoord != null) {
			SpriteGroup current = this;
			while (current.hasParent()) {
				current = current.getParent();
				screenCoord.x -= current.getX();
				screenCoord.y -= current.getY();
			}
			return screenCoord;
		}
		return null;
	}

	/** translates the given local coordinates into parent coordinates */
	public Vector2 localToParentCoord(Vector2 localCoord) {
		if (localCoord != null) {
			if (hasParent()) {
				localCoord.x += getParent().getX();
				localCoord.y += getParent().getY();
			}
			return localCoord;
		}
		return null;
	}

	
	
	
	
	
	public boolean overlaps(SpriteGroup other, boolean checkChildrenToo) {
		// translate my coord into screen coord
		Rectangle myRect = getBoundingRectangle().setPosition(getScreenPosition());

		// translate other's coord into screen coord
		Rectangle otherRect = other.getBoundingRectangle().setPosition(other.getScreenPosition());
		if (myRect.overlaps(otherRect))
			return true;
		if (checkChildrenToo)
			for (SpriteGroup child : children) {
				if (child.overlaps(other, checkChildrenToo))
					return true;
			}
		return false;
	}

	/**
	 * @param local weather the given coordinates are locally or screenwise
	 */
	public boolean contains(Vector2 pos, boolean local, boolean checkChildrenToo) {
		return pos == null ? false : contains(pos.x, pos.y, local, checkChildrenToo);
	}

	/**
	 * @param local weather the given coordinates are locally or screenwise
	 */
	public boolean contains(float x, float y, boolean local, boolean checkChildrenToo) {
		// translate my coord into screen coord
		Rectangle myRect = getBoundingRectangle().setPosition(getScreenPosition());

		float screenX = local ? x + myRect.x : x;
		float screenY = local ? y + myRect.y : y;

		if (myRect.contains(screenX, screenY))
			return true;
		if (checkChildrenToo)
			for (SpriteGroup child : children) {
				if (child != null && child.contains(screenX, screenY, false, checkChildrenToo))
					return true;
			}
		return false;
	}

	/**
	 * 
	 * @param x                local or screen position
	 * @param y                local or screen position
	 * @param local            weather ot not thie givven coordinates are locally or
	 *                         screenwise
	 * @param checkChildrenToo
	 * @return if(checkChildreenToo) the top most child (or main), else main or null
	 */
	public SpriteGroup hit(Vector2 pos, boolean local, boolean checkChildrenToo) {
		return pos == null ? null : hit(pos.x, pos.y, local, checkChildrenToo);
	}

	/** {@link SpriteGroup#hit(float, float, boolean, boolean)} */
	public SpriteGroup hit(float x, float y, boolean local, boolean checkChildrenToo) {
		Rectangle bounds = getBoundingRectangle().setPosition(getScreenPosition());

		float screenX = local ? x + bounds.x : x;
		float screenY = local ? y + bounds.y : y;

		if (checkChildrenToo && hasChildren()) {
			SpriteGroup[] children = this.children.toArray(SpriteGroup.class);
			for (int i = children.length - 1; i >= 0; i--) {
				SpriteGroup child = children[i].hit(screenX, screenY, false, checkChildrenToo);
				if (child != null)
					return child;
			}
		}
		return bounds.contains(screenX, screenY) ? this : null;
	}

	
	
	
	private ShaderProgram shader;
	private boolean isShaderEnabled;
	
	public void setShader(ShaderProgram shader) {
		this.shader = shader;
		isShaderEnabled = true;
	}
	
	public ShaderProgram getShader() {
		return shader;
	}
	
	public boolean isShaderEnabled() {
		return isShaderEnabled;
	}
	
	public void setEnableShader(boolean enable) {
		this.isShaderEnabled = enable;
	}
	
	
	
	@Override
	public void draw(Batch batch) {
		if(!isVisible())return;
		
		ShaderProgram oldShader = batch.getShader();
		if(isShaderEnabled)batch.setShader(shader);
		
		drawMain(batch);//flushes
		if (hasChildren())drawChildren(batch); // flushes
		
		if(isShaderEnabled)batch.setShader(oldShader);
		
		
	}
	
	public void drawMain(Batch batch) {
		float x = this.getX();
		float y = this.getY();
		float originX = this.getOriginX();
		float originY = this.getOriginY();
		float width = this.getWidth();
		float height = this.getHeight();
		float scaleX = this.getScaleX();
		float scaleY = this.getScaleY();
		float rotation = this.getRotation();
		Color oldColor = batch.getColor().cpy();
		Color color = this.getColor().cpy();
		

		// parent position
		SpriteGroup currentSprite = this;
		while (currentSprite.hasParent()) {
			SpriteGroup currentParent = currentSprite.getParent();

			x += currentParent.getX();
			y += currentParent.getY();
			scaleX *= currentParent.getScaleX(); // scaled origin of parent may produes problems !!
			scaleY *= currentParent.getScaleY(); //
//			originX = currentParent.getScaleX()* 
			rotation += currentParent.getRotation();
			color.mul(currentParent.getColor());

			currentSprite = currentParent;
		}
		
		batch.setColor(color);
		
		//draw
		if (region != null)
			batch.draw(region, 
					x, y, 
					originX, originY, 
					width, height, 
					scaleX, scaleY, 
					rotation);
		
		//flush
		batch.flush();
		batch.setColor(oldColor);
		
	}
	
	
	public void drawChildren(Batch batch) {
		for (SpriteGroup child : children)child.draw(batch);
	}
	

	public void drawDebug(ShapeRenderer renderer) {
		if(!isDrawDebug)return;
		float x = this.getX();
		float y = this.getY();
		float originX = this.getOriginX();
		float originY = this.getOriginY();
		float width = this.getWidth();
		float height = this.getHeight();
		float scaleX = this.getScaleX();
		float scaleY = this.getScaleY();
		float rotation = this.getRotation();
		Color oldColor = renderer.getColor().cpy();
		Color color = getAncestorBasedColor();
		
		//debug packed bounds
		if(isDrawDebugPackedBounds) {
			Rectangle rect = getPackedBounds();
			tmp.set(rect.x, rect.y);
			localToScreenCoord(tmp);
			renderer.setColor(DEBUG_PACK);
			renderer.rect(rect.x, rect.y, originX, originY, rect.width, rect.height, scaleX, scaleY, rotation);
			renderer.setColor(oldColor);
		}
		
		
		// calculate parent position
		SpriteGroup currentSprite = this;
		while (currentSprite.hasParent()) {
			SpriteGroup currentParent = currentSprite.getParent();

			x += currentParent.getX();
			y += currentParent.getY();
			scaleX *= currentParent.getScaleX();
			scaleY *= currentParent.getScaleY();
			rotation += currentParent.getRotation();

			currentSprite = currentParent;
		}
		renderer.setColor(color);
		renderer.rect(x, y, originX, originY, width, height, scaleX, scaleY, rotation);
		renderer.setColor(oldColor);

		if (hasChildren())for (SpriteGroup child : children)child.drawDebug(renderer);
		
		
		

	}

	
	
	
	
	
	/** Fires an event excluding children */
	public void justTouchedDown(float screenX, float screenY, Entity entity) {
		if (hasListener()) {
			InputListener[] listeners = this.listeners.toArray(InputListener.class);
			for (int i = listeners.length - 1; i >= 0; i--) {
				// own
				InputListener listener = listeners[i];
				tmp.set(screenX, screenY);
				screenToLocalCoord(tmp);
				listener.justTouchedDown(tmp.x, tmp.y, this, entity);

				// children first to last
//				if (isInputReceivedByChildren(InputState.JUST_TOUCHED_DOWN) && hasChildren()) {
//					SpriteGroup[] children = this.children.toArray(SpriteGroup.class);
//					for (int k = children.length - 1; k >= 0; k--) {
//						SpriteGroup child = children[k];
//						if(child.contains(screenX, screenY, false, child.isInputReceivedByChildren(InputState.JUST_TOUCHED_DOWN)))
//							child.justTouchedDown(screenX, screenY, entity);
//					}
//				}
			}
		}
		if (DEBUG_INPUT)
			log.debug("[just down] descendant: " + getAncestorCount());
	}

	/** Fires an event excluding children */
	public void touchDown(float screenX, float screenY, Entity entity) {
		if (hasListener()) {
			InputListener[] listeners = this.listeners.toArray(InputListener.class);
			for (int i = listeners.length - 1; i >= 0; i--) {
				// own
				InputListener listener = listeners[i];
				tmp.set(screenX, screenY);
				screenToLocalCoord(tmp);
				listener.touchDown(tmp.x, tmp.y, this, entity);

				// children first to last
//				if (isInputReceivedByChildren(InputState.TOUCHED_DOWN) && hasChildren()) {
//					SpriteGroup[] children = this.children.toArray(SpriteGroup.class);
//					for (int k = children.length - 1; k >= 0; k--) {
//						SpriteGroup child = children[k];
//						if(child.contains(screenX, screenY, false, child.isInputReceivedByChildren(InputState.TOUCHED_DOWN)))
//							child.touchDown(screenX, screenY, entity);
//					}
//				}
			}
		}
	}

	/** Fires an event excluding children */
	public void touchUp(float screenX, float screenY, Entity entity) {
		if (hasListener()) {
			InputListener[] listeners = this.listeners.toArray(InputListener.class);
			for (int i = listeners.length - 1; i >= 0; i--) {
				// own
				InputListener listener = listeners[i];
				tmp.set(screenX, screenY);
				screenToLocalCoord(tmp);
				listener.touchUp(tmp.x, tmp.y, this, entity);

				// children first to last
//				if (isInputReceivedByChildren(InputState.TOUCHED_UP) && hasChildren()) {
//					SpriteGroup[] children = this.children.toArray(SpriteGroup.class);
//					for (int k = children.length - 1; k >= 0; k--) {
//						SpriteGroup child = children[k];
//						if(child.contains(screenX, screenY, false, child.isInputReceivedByChildren(InputState.TOUCHED_UP)))
//							child.touchUp(screenX, screenY, entity);
//					}
//				}
			}
		}
		if (DEBUG_INPUT)
			log.debug("[touch up] descendant: " + getAncestorCount());
	}

	/** Fires an event excluding children */
	public void entered(float screenX, float screenY, Entity entity) {
		if (hasListener()) {
			InputListener[] listeners = this.listeners.toArray(InputListener.class);
			for (int i = listeners.length - 1; i >= 0; i--) {
				// own
				InputListener listener = listeners[i];
				tmp.set(screenX, screenY);
				screenToLocalCoord(tmp);
				listener.entered(tmp.x, tmp.y, this, entity);

				// children first to last
//				if (isInputReceivedByChildren(InputState.ENTERED) && hasChildren()) {
//					SpriteGroup[] children = this.children.toArray(SpriteGroup.class);
//					for (int k = children.length - 1; k >= 0; k--) {
//						SpriteGroup child = children[k];
//						if(child.contains(screenX, screenY, false, child.isInputReceivedByChildren(InputState.ENTERED)))
//							child.entered(screenX, screenY, entity);
//					}
//				}
			}
		}
		if (DEBUG_INPUT)
			log.debug("[entered] descendant: " + getAncestorCount());
	}

	/** Fires an event excluding children */
	public void exited(float screenX, float screenY, Entity entity) {
		if (hasListener()) {
			InputListener[] listeners = this.listeners.toArray(InputListener.class);
			for (int i = listeners.length - 1; i >= 0; i--) {
				// own
				InputListener listener = listeners[i];
				tmp.set(screenX, screenY);
				screenToLocalCoord(tmp);
				listener.exited(tmp.x, tmp.y, this, entity);

				// children first to last
//				if (isInputReceivedByChildren(InputState.EXITED) && hasChildren()) {
//					SpriteGroup[] children = this.children.toArray(SpriteGroup.class);
//					for (int k = children.length - 1; k >= 0; k--) {
//						SpriteGroup child = children[k];
//						if(child.contains(screenX, screenY, false, child.isInputReceivedByChildren(InputState.EXITED)))
//							child.exited(screenX, screenY, entity);
//					}
//				}
			}
		}
		if (DEBUG_INPUT)
			log.debug("[exited] descendant: " + getAncestorCount());
	}

	/** Fires an event excluding children */
	public void mouseOver(float screenX, float screenY, Entity entity) {
		if (hasListener()) {
			InputListener[] listeners = this.listeners.toArray(InputListener.class);
			for (int i = listeners.length - 1; i >= 0; i--) {
				// own
				InputListener listener = listeners[i];
				tmp.set(screenX, screenY);
				screenToLocalCoord(tmp);
				listener.mouseOver(tmp.x, tmp.y, this, entity);

				// children first to last
//				if (isInputReceivedByChildren(InputState.MOUSE_OVER) && hasChildren()) {
//					SpriteGroup[] children = this.children.toArray(SpriteGroup.class);
//					for (int k = children.length - 1; k >= 0; k--) {
//						SpriteGroup child = children[k];
//						if(child.contains(screenX, screenY, false, child.isInputReceivedByChildren(InputState.MOUSE_OVER)))
//							child.mouseOver(screenX, screenY, entity);
//					}
//				}
			}
		}
	}

	private boolean lastTouched;
	private boolean lastEntered;

	public void queryInput(float screenX, float screenY, Entity entity) {
		if (listenable.isDisabled() || !isVisible())
			return;

		// this sprite
		if (listenable.isEnabled()) {
			if (contains(screenX, screenY, false, false)) { // mouse over entity

				if (hasParentAndReceivesInput(InputState.MOUSE_OVER))
					mouseOver(screenX, screenY, entity);
				else if (!hasParent())
					mouseOver(screenX, screenY, entity);

				if (!lastEntered) { // entered
					lastEntered = true;
					if (hasParentAndReceivesInput(InputState.ENTERED))
						entered(screenX, screenY, entity);
					else if (!hasParent())
						entered(screenX, screenY, entity);

				}

				if (isTouchDown()) { // down
					if (!lastTouched) { // just down
						lastTouched = true;
						if (hasParentAndReceivesInput(InputState.JUST_TOUCHED_DOWN))
							justTouchedDown(screenX, screenY, entity);
						else if (!hasParent())
							justTouchedDown(screenX, screenY, entity);

					}
					if (hasParentAndReceivesInput(InputState.TOUCHED_DOWN))
						touchDown(screenX, screenY, entity);
					else if (!hasParent())
						touchDown(screenX, screenY, entity);

				} else if (lastTouched) { // up if over
					lastTouched = false;
					if (hasParentAndReceivesInput(InputState.TOUCHED_UP))
						touchUp(screenX, screenY, entity);
					else if (!hasParent())
						touchUp(screenX, screenY, entity);

				}

			} else if (lastEntered) { // exited
				lastEntered = false;
				if (hasParentAndReceivesInput(InputState.EXITED))
					exited(screenX, screenY, entity);
				else if (!hasParent())
					exited(screenX, screenY, entity);

			} else if (lastTouched) { // autom. up if not over anymore
				lastTouched = false;
				if (hasParentAndReceivesInput(InputState.TOUCHED_UP))
					touchUp(screenX, screenY, entity);
				else if (!hasParent())
					touchUp(screenX, screenY, entity);
			}
		}

		// children
		if ((listenable.isEnabled() || listenable.isChildrenOnly()) && hasChildren() && hasInputsReceivedByChildren()) {
			getChildren().forEach(child -> child.queryInput(screenX, screenY, entity));
		}

	}

	public boolean isMouseOver() {
		return lastEntered;
	}

	// -- private methods --

	private boolean isTouchDown() {
		return Gdx.input.isTouched();
	}

	private boolean hasParentAndReceivesInput(InputState state) {
		return hasParent() && getParent().isInputReceivedByChildren(state);
	}

}
