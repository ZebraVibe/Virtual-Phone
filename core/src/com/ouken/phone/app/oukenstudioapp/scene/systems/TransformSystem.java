package com.ouken.phone.app.oukenstudioapp.scene.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.ouken.phone.app.oukenstudioapp.scene.common.Families;
import com.ouken.phone.app.oukenstudioapp.scene.common.Mappers;
import com.ouken.phone.app.oukenstudioapp.scene.components.TransformComponent;
import com.ouken.phone.app.oukenstudioapp.scene.uitls.TransformComponentComparator;
import com.ouken.phone.app.oukenstudioapp.scene.uitls.TransformSortedIteratingSystem;

public class TransformSystem extends TransformSortedIteratingSystem{
	
	private static final Logger log = new Logger(TransformSystem.class.getName(), Logger.DEBUG);
	
	public TransformSystem() {
		super(Families.TRANSFORM, new TransformComponentComparator());

	}
	
	

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
	}
	


	@Override
	protected void processEntity(Entity entity, float deltaTime) {
	}

}
