package com.ouken.phone.app.oukenstudioapp.scene.uitls;

import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;

public abstract class TransformSortedIteratingSystem extends SortedIteratingSystem{

	public TransformSortedIteratingSystem(Family family, TransformComponentComparator comparator) {
		super(family, comparator);
	}

}
