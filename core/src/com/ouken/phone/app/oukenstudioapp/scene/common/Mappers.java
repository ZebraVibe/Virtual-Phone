package com.ouken.phone.app.oukenstudioapp.scene.common;

import com.badlogic.ashley.core.ComponentMapper;
import com.ouken.phone.app.oukenstudioapp.scene.components.RenderComponent;
import com.ouken.phone.app.oukenstudioapp.scene.components.TransformComponent;

public class Mappers {
	
	private Mappers() {}
	public static final ComponentMapper<TransformComponent> TRANSFORM = ComponentMapper.getFor(TransformComponent.class);
	public static final ComponentMapper<RenderComponent> RENDER = ComponentMapper.getFor(RenderComponent.class);
	
}
