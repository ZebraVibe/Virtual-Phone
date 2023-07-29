package com.ouken.phone.app.oukenstudioapp.scene.entites;

import com.badlogic.ashley.core.Entity;
import com.ouken.phone.app.oukenstudioapp.scene.Scene;

public class SceneEntity extends Entity {
	
	private Scene scene;
	
	/**Not ment to get components. This class exists for convinience*/
	public SceneEntity(Scene scene) {
		this.scene = scene;
	}
	
	public Scene getScene() {
		return scene;
	}
	
}
