package com.ouken.phone.app.oukenstudioapp.scene.common;

import com.badlogic.ashley.core.Family;
import com.ouken.phone.app.oukenstudioapp.scene.components.RenderComponent;
import com.ouken.phone.app.oukenstudioapp.scene.components.SceneComponent;
import com.ouken.phone.app.oukenstudioapp.scene.components.TransformComponent;

public class Families {

	public static final Family TRANSFORM = Family.all(TransformComponent.class).get();
	public static final Family RENDER = Family.all(TransformComponent.class, RenderComponent.class).get();
	public static final Family SCENE = Family.all(SceneComponent.class, TransformComponent.class).get();
	
	private Families() {}
	
}
