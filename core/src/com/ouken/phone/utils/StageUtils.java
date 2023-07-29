package com.ouken.phone.utils;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class StageUtils {

	private StageUtils() {}
	
	public static void mark(float stageX, float stageY, LabelStyle style, Stage stage) {
		Label l = new Label("X", style);
		l.setTouchable(Touchable.disabled);
		l.setPosition(stageX, stageY);
		stage.addActor(l);
	}
	
}
