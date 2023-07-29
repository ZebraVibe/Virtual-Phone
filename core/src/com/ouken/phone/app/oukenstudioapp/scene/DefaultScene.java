package com.ouken.phone.app.oukenstudioapp.scene;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ouken.phone.app.oukenstudioapp.screens.EditorScreen;

public class DefaultScene extends Scene {

	public DefaultScene(Viewport sceneViewport, EditorScreen editor) {
		super(sceneViewport, editor);
	}

	@Override
	public void initSystems(PooledEngine engine) {
	}

	@Override
	public void initEntities(PooledEngine engine) {
	}

}
