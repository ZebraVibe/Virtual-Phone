package com.ouken.phone.app.oukenstudioapp.scene.uitls;

import java.util.Comparator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Null;
import com.ouken.phone.app.oukenstudioapp.scene.common.Mappers;
import com.ouken.phone.app.oukenstudioapp.scene.components.TransformComponent;
import com.ouken.phone.app.oukenstudioapp.scene.entites.SceneEntity;

/***
 * Compares in the following order:  ancestor, layer, child index
 */
public class TransformComponentComparator implements Comparator<Entity>{
	
	@Override
	public int compare(Entity o1, Entity o2) {
		TransformComponent t1 = Mappers.TRANSFORM.get(o1);
		TransformComponent t2 = Mappers.TRANSFORM.get(o2);
		if(t1 == null || t2 == null)return 0;
		
		// check if SceneEntity
		if(o1 instanceof SceneEntity)return -1;
		else if(o2 instanceof SceneEntity)return 1;
		
		
		// -1 to ignore sceneEntity as Ancestor
		int a1 = t1.getAncestors() - 1; 
		int a2 = t2.getAncestors() -1;
		
		TransformComponent ancestor1 = null, ancestor2 = null;
		
		// get ancestors in same level
		if(a1 > a2) {
			ancestor1 = t1.getAncestor(a1-a2);
			ancestor2 = t2;
			if(ancestor1 == ancestor2)return 1;
		}else if(a1 < a2) {
			ancestor1 = t1;
			ancestor2 = t2.getAncestor(a2 - a1);
			if(ancestor1 == ancestor2)return -1;
		}else {
			ancestor1 = t1;
			ancestor2 = t2;
		}
		
		// check layer first of same level ancestors
		int l1 = ancestor1.getLayer();
		int l2 = ancestor2.getLayer();
		if(l1 != l2)return l1 -l2;
		
		// check Z index of same level ancestors;
		int i1 = ancestor1.getZIndex();
		int i2 = ancestor2.getZIndex();
		
		return i1-i2;
	}
	
	
	

	

}



