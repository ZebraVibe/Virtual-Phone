package com.ouken.phone.utils.input;

import com.badlogic.ashley.core.Entity;
import com.ouken.phone.utils.sprite.SpriteGroup;

public class InputListener {
	
	public void justTouchedDown(float localX, float localY, SpriteGroup sprite, Entity entity) {};
	
	public void touchDown(float localX, float localY,SpriteGroup sprite, Entity entity) {};
	
	public void touchUp(float localX, float localY, SpriteGroup sprite, Entity entity) {};

	public void entered(float localX, float localY, SpriteGroup sprite, Entity entity) {};
	
	public void exited(float localX, float localY, SpriteGroup sprite, Entity entity) {};
	
	public void mouseOver(float localX, float localY, SpriteGroup sprite, Entity entity) {};
	
}
