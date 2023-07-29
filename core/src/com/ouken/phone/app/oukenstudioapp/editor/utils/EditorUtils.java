package com.ouken.phone.app.oukenstudioapp.editor.utils;

import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.ouken.phone.app.oukenstudioapp.assets.SkinNames;
import com.ouken.phone.utils.StageUtils;

public class EditorUtils {
	
	private static final Logger log = new Logger(EditorUtils.class.getName(), Logger.DEBUG);
	
	private EditorUtils() {}
	
	public static void mark(float stageX, float stageY, Stage stage, Skin skin) {
		StageUtils.mark(stageX, stageY, skin.get(SkinNames.LABEL_STYLE_12, LabelStyle.class), stage);
	}
	
	/***
	 * 
	 * @param name
	 * @param maxLength negatvie to indicate no max length
	 * @return
	 */
	public static String getFormattedName(String name, int maxLength) {
		if(name == null || maxLength < 0 || name.length() <= maxLength)return name;
		else {
			String tmp = "";
			tmp = name.substring(0, maxLength - 3);
			tmp += "...";
			return tmp;
		}
	}
	
	/**
	 * 
	 * @param region
	 * @param maxLength negative to indicate no max length
	 * @return
	 */
	public static String getNameOfRegion(TextureRegion region, int maxLength) {
		TextureData data = region.getTexture().getTextureData();
		if(data instanceof FileTextureData) {
			FileTextureData fileData = (FileTextureData)data;
			String name = fileData.getFileHandle().nameWithoutExtension();
			name = EditorUtils.getFormattedName(name, maxLength);
			return name;
		}
		log.error("unknown region name when tried to create name from texture");
		return "unknown";
	}
	
	public static String getNameOfRegion(TextureRegion region) {
		return getNameOfRegion(region, -1);
	}
	
	
}
