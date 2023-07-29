package com.ouken.phone.app.oukenstudioapp.scene.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.EntityNode;
import com.ouken.phone.app.oukenstudioapp.scene.Scene;
import com.ouken.phone.app.oukenstudioapp.scene.uitls.Invokable;
import com.ouken.phone.app.oukenstudioapp.scene.uitls.Serializable;
import com.ouken.phone.app.oukenstudioapp.scene.uitls.TransformSortedIteratingSystem;

public class TransformComponent implements Component, Poolable{
	
	private static final Logger log = new Logger(TransformComponent.class.getName(), Logger.DEBUG);
	
	private static final int INDEX_WITHOUT_PARENT = -1;
	
	private final SnapshotArray<TransformComponent> children = new SnapshotArray<TransformComponent>();
	private final Rectangle bounds = new Rectangle();
	private final Vector2 tmp = new Vector2();
	
	private Entity entity;
	private Engine engine;
	private Scene scene;
	
	private @Null TransformComponent parent;
	
	@Serializable
	@Invokable(name = "renamed") 
	private String name = "";

	@Serializable
	@Invokable(name = "sort")
	private int layer = 0;
	public boolean visible = true;
	public boolean transform = true; // unused

	public float x, y, width, height;
	public float originX, originY;
	public float scaleX = 1.0f, scaleY = 1.0f;
	public float rotation;
	
	
	
	
	// -- entity --
	
	/**This components entity is only set once until the component itself is resetted. 
	 * The entity is automatically set when the entity is added to engine*/
	public void setEntity(Entity entity) {
		if(this.entity != null) {
			log.error("Cant set entity, since entity already set!");
			return;
		}
		this.entity = entity;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	// -- engine --
	
	public Engine getEngine() {
		return engine;
	}
	
	/**This components scene is only set once until the component itself is resetted. 
	 * The scene is automatically set when the entity is added to engine*/
	public void setScene(Scene scene) {
		if(this.scene != null) {
			log.error("Cant set engine, since engine already set!");
			return;
		}
		this.scene = scene;
		this.engine = scene.getEngine();
	}
	

	
	// -- position --
	
	/**recalculates the bounds on each call.*/
	public Rectangle getBounds() {
		return bounds.set(x, y, width, height);
	}
	
	public void setBounds(float x, float y, float width, float height) {
		setPosition(x,y);
		setSize(width,height);
	}
	
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	/**resulting value might not be integer*/
	public void setPosition(float x, float y, int alignment) {
		setX(x, alignment);
		setY(y, alignment);
	}
	
	
	public Vector2 getPosition(Vector2 pos) {
		return pos.set(x,y);
	}
	
	public Vector2 getPosition(Vector2 pos, int alignment) {
		return pos.set(getX(alignment), getY(alignment));
	}
	
	
	/** {@link Align#bottomLeft} is always at (0,0), the bottom left corner of the Transforms coord system*/
	public Vector2 getLocalPosition(Vector2 pos, int alignment) {
		return pos.set(getX(alignment) - x, getY(alignment) - y);
	}
	
	
	/**resulting value might not be integer*/
	public void setX(float x, int alignment) {
		if(Align.isCenterVertical(alignment)) {
			this.x = x - width / 2f;
		}else if(Align.isRight(alignment)) {
			this.x = x + width;
		}
	}

	/**resulting value might not be integer*/
	public void setY(float y, int alignment) {
		if(Align.isCenterHorizontal(alignment)) {
			this.y = y - height / 2f;
		}else if(Align.isTop(alignment)) {
			this.y = y + height;
		}
	}
	
	/**Recalculates the x and y position to snap into a grid of the given size. Rounds down.<br>
	 * [Example]: gridSize = 1 : 1.9 -> 1 and 9.4 -> 9 */
	public void snapTo(int gridSize) {
		// rounds down;
		x = (x / gridSize) * gridSize;
		y = (y / gridSize) * gridSize;
	}
	

	public void setSize(float width, float height) {
		this.width = width;
		this.height = height;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	
	
	public float getX(int alignment) {
		float x = this.x;
		if(Align.isCenterVertical(alignment)) {
			x += width / 2f;
		}else if(Align.isRight(alignment)) {
			x += width;
		}
		return x;
	}
	
	public float getY(int alignment) {
		float y = this.y;
		if(Align.isCenterHorizontal(alignment)) {
			y += height / 2f;
		}else if(Align.isTop(alignment)) {
			y += height;
		}
		return y;
	}
	
	
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	
	public void positionBelowCamera() {
		Camera cam = scene.getViewport().getCamera();
		tmp.set(cam.position.x, cam.position.y); // already in world coord
		worldToParentCoord(tmp);
		x = (int)tmp.x - width / 2;
		y = (int)tmp.y - height / 2;
	}
	
	public void positionBelowMouse() {
		tmp.set(Gdx.input.getX(), Gdx.input.getY()); // screen coord
		screenToParentCoord(tmp);
		x = (int)tmp.x - width / 2;
		y = (int)tmp.y - height / 2;
	}
	

	// -- layer --
	
	public int getLayer() {
		return layer;
	}
	
	public void setLayer(int layer) {
		if(this.layer == layer)return;
		this.layer = layer;
		sort();
	}
	
	// -- name --
	public void setName(String name) {
		this.name = name;
		renamed();
	}
	
	public String getName() {
		return name;
	}
	
	
	
	// -- coord --
	
	public Vector2 worldToScreenCoord(Vector2 world) {
		Viewport viewport = scene.getViewport();
		return viewport.project(world);
	}
	
	public Vector2 screenToWorldCoord(Vector2 screen) {
		Viewport viewport = scene.getViewport();
		return viewport.unproject(screen);
	}
	
	
	
	public Vector2 screenToParentCoord(Vector2 screen) {
		tmp.set(screenToWorldCoord(screen));
		TransformComponent parent = this.parent;
		while(parent != null) {
			tmp.sub(parent.x, parent.y);
			parent = parent.parent;
		}
		return screen.set(tmp);
	}
	
	public Vector2 worldToLocalCoord(Vector2 world) {
		tmp.set(world);
		TransformComponent parent = this;
		while(parent != null) {
			tmp.sub(parent.x, parent.y);
			parent = parent.parent;
		}
		return world.set(tmp);
	}
	
	public Vector2 worldToParentCoord(Vector2 world) {
		tmp.set(world);
		TransformComponent parent = this.parent;
		while(parent != null) {
			tmp.sub(parent.x, parent.y);
			parent = parent.parent;
		}
		return world.set(tmp);
	}

	public Vector2 localToWorldCoord(Vector2 local) {
		tmp.set(local);
		TransformComponent parent = this;
		while(parent != null) {
			tmp.add(parent.x, parent.y);
			parent = parent.parent;
		}
		return local.set(tmp);
	}
	
	public Vector2 parentToWorldCoord(Vector2 local) {
		tmp.set(local);
		TransformComponent parent = this.parent;
		while(parent != null) {
			tmp.add(parent.x, parent.y);
			parent = parent.parent;
		}
		return local.set(tmp);
	}
	
	public Vector2 localToParentCoord(Vector2 local) {
		tmp.set(local).add(x, y);
		return local.set(tmp);
	}
	
	public Vector2 parentToLocalCoord(Vector2 parent) {
		tmp.set(parent).sub(x, y);
		return parent.set(tmp);
	}

	
	/***
	 * 
	 * @param x in local coord
	 * @param y in local coord
	 * @return
	 */
	public Entity hit(float x, float y) {
		localToWorldCoord(tmp.set(x, y));
		return scene.hitWorld(tmp.x, tmp.y);
	} 
	
	
	
	// -- parent transform --
	
	
	public @Null TransformComponent getParent() {
		return parent;
	}
	
	public boolean hasParent() {
		return parent != null;
	}
	
	/**repositions the transform so that the entity visually doesnt change*/
	public void setParent(@Null TransformComponent parent, boolean reposition) {
		if(!reposition) {
			setParent(parent);
			return;
		}
		localToWorldCoord(tmp.setZero());
		
		setParent(parent);

		worldToParentCoord(tmp);
		setPosition(tmp.x, tmp.y);
	}
	
	/**adds the child to its children*/
	public void setParent(@Null TransformComponent parent) {
		if(this.parent == parent)return;
		if(hasParent()) {
			this.parent.children.removeValue(this, true);
		}
		this.parent = parent;
		if(hasParent()) {
			this.parent.children.add(this);
		}
//		parentChanged();
		sort();
	}
	
	// -- children --
	
	/**[!!!] call {@link #sort()} after changing the children viea {@link #getChildren()} in some way*/
	public SnapshotArray<TransformComponent> getChildren() {
		return children;
	}
	
	public boolean hasChildren() {
		return children.size != 0;
	}
	
	
	public void addChild(TransformComponent child) {
		if(child == null)return;
		if(child.hasParent()) {
			child.parent.children.removeValue(child, true);
		}
		children.add(child);
		child.parent = this;
//		parentChanged();
		sort();
	}
	
	public void removeChild(TransformComponent child) {
		if(child == null)return;
		children.removeValue(child, true);
		child.parent = null;
//		parentChanged();
		sort();
	}
	
	/**removes this component from its parent but NOT from the engine.*/
	public void remove() {
		if(!hasParent())return;
		parent.children.removeValue(this, true);
		parent = null;
//		parentChanged();
		sort();
	}
	
	/**removes this component's entity and its children fromm the engine*/
	public void removeFromEgine() {
		log.debug("removing from engine: " + this);
		if(hasChildren()) {
			Object items[] = children.begin();
			// do not use i < children.size since it changes, store it in variable n, 
			// items.length might be bigger then children.size with nulls
			for (int i = 0, n = children.size; i < n; i++) {
				((TransformComponent) items[i]).removeFromEgine();
			}
			children.end();
			children.clear();// needed?
		}
		 // remove from parent
		remove();
		// remove from engine, since parent already parent.removeChild() is not called again
		engine.removeEntity(entity);
	}
	
	/**returns the child index if a perent is present. returns -1 if no parent is present.*/
	public int getZIndex() {
		if(hasParent())return parent.children.indexOf(this, true);
		return INDEX_WITHOUT_PARENT;
	}
	
	/**sets the child index if a parent is present*/
	public void setZIndex(int index) {
		if(!hasParent())return;
		int oldIndex = parent.children.indexOf(this, true);
		
		if(index <= oldIndex) {
			for(int i = oldIndex; i > index; i--)parent.children.swap(i, i-1);
		}else {
			for(int i = oldIndex; i < index; i++)parent.children.swap(i, i+1);	
		}
		log.debug("oldIndex= " + oldIndex + " newIndex= " + parent.children.indexOf(this, true) + " given index= " + index);
		sort();
	}
	
	
	/**returns the amount of ancestors(=parent/grand parent...).*/
	public int getAncestors() {
		TransformComponent parent = this.parent;
		int count = 0;
		while(parent != null) {
			count++;
			parent = parent.parent;
		}
		return count;
	}
	
	/**
	 * 
	 * @param transform
	 * @return weather or not this instance is an ancestor(=parent/grand parent...) of the given transform
	 */
	public boolean isAncestorOf(TransformComponent transform) {
		if(this == transform.parent)return true;
		for(TransformComponent child : children) {
			if(child.isAncestorOf(transform))return true;
		}
		return false;
	}
	
	public @Null TransformComponent getAncestor(int count) {
		TransformComponent ancestor = this;
		for(int i = count; i > 0 && ancestor != null; i--) {
			ancestor = ancestor.parent;
		}
		return ancestor;
	}
	
	
	
	// -- has changed and sort --
	
	/**notifies every {@link TransformSortedIteratingSystem} to sort*/
	public void sort() {
		log.debug("informing to sort entities of all " + TransformSortedIteratingSystem.class.getSimpleName() + "'s.");
		for(EntitySystem entitySystem : engine.getSystems()) {
			if(entitySystem instanceof TransformSortedIteratingSystem) {
				((TransformSortedIteratingSystem)entitySystem).forceSort();
			}
		}
	}
	

	// -- private methods --
	
	private void renamed() {
		EntityNode node = scene.getEditor().getEntityExplorer().getExplorerTree().findNode(entity);
		node.setLabelText(name);
	}
	
	
	// -- to string --
	
	@Override
	public String toString() {
		String s = "[TransformComponent]: name= " + name + ", parent= " + (parent != null ? parent.name : null) + ", zIndex= " + getZIndex() +
				" children= " + children.size;
//		if(hasChildren()) {
//			for(TransformComponent child : children) s+= "\n\t" + child + ",";
//		}else s += "none";
		return s;
	}
	
	
	

	@Override
	public void reset() {
		children.clear();
		bounds.set(0, 0, 0, 0);
		tmp.setZero();
		
		entity = null;
		engine = null;
		scene = null;

		parent = null;
		
		name = "";
		visible = true;
		transform = true;
		layer = 0;
		
		x = 0.0f;
		y = 0.0f;
		width = 0.0f;
		height = 0.0f;
		originX = 0.0f;
		originY = 0.0f;
		scaleX = 1.0f;
		scaleY = 1.0f;
		rotation = 0.0f;
	}
	

	
}
