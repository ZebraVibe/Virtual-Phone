package com.ouken.phone.app.oukenstudioapp.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane.SplitPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.TreeStyle;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ouken.phone.app.oukenstudioapp.OukenStudioApp;
import com.ouken.phone.app.oukenstudioapp.assets.AssetDescriptors;
import com.ouken.phone.app.oukenstudioapp.assets.RegionNames;
import com.ouken.phone.app.oukenstudioapp.assets.SkinNames;
import com.ouken.phone.app.oukenstudioapp.common.EditorManager;
import com.ouken.phone.app.oukenstudioapp.editor.utils.Pane;
import com.ouken.phone.app.oukenstudioapp.editor.utils.PaneSpriteDrawable;
import com.ouken.phone.app.utils.AppScreenViewport;
import com.ouken.phone.utils.GdxUtils;
import com.ouken.phone.utils.font.FontSize;
import com.ouken.phone.utils.font.Fonts;

public class LoadingScreen extends ScreenAdapter{

	public static final int LOADING_BAR_WIDTH = 128, LOADING_BAR_HEIGHT = 32;
	
	private OukenStudioApp game;
	private AssetManager assets;
	private ShapeRenderer renderer;

	private Viewport viewport;
	private float waitAfterLoading = 1;//sec
	
	public LoadingScreen(OukenStudioApp game) {
		this.game = game;
		this.assets = game.getAssetManager();
	}
	
	@Override
	public void show() {
		renderer = new ShapeRenderer();
		viewport = new AppScreenViewport();
		AssetDescriptors.ALL.forEach(desc -> assets.load(desc));
		game.log().debug("Loading Assets...");
	}
	
	
	
	@Override
	public void render(float delta) {
		GdxUtils.clearScreen(0,0,0,1);
		
		renderer.setProjectionMatrix(viewport.getCamera().combined);
		
		float progress = assets.getProgress();
		renderer.begin(ShapeType.Filled);
	
		renderer.setColor(Color.WHITE);
		renderer.rect((viewport.getWorldWidth() - LOADING_BAR_WIDTH) /2f, (viewport.getWorldHeight() - LOADING_BAR_HEIGHT) / 2f, 
				LOADING_BAR_WIDTH * progress, LOADING_BAR_HEIGHT);
		renderer.end();
		
		if(assets.update()) {
			
			if(waitAfterLoading > 0) {
				waitAfterLoading -= delta;
				return;
			};
			
			game.log().debug("...Done Loading Assets");
			game.log().debug("Changing to " + ProjectSelectScreen.class.getName());
			
			initSkin();
			
			game.setScreen(new ProjectSelectScreen(game));
		}
	}
	private void initSkin() {
		TextureAtlas gameplayAtlas = assets.get(AssetDescriptors.GAMEPLAY);
		TextureAtlas uiAtlas = assets.get(AssetDescriptors.EDITOR_UI);
		
		TextureRegion paneRegion = uiAtlas.findRegion(RegionNames.PANE);
		TextureRegion pixelRegion = new TextureRegion(paneRegion, paneRegion.getRegionWidth() / 2, paneRegion.getRegionHeight() /2, 1, 1);
		TextureRegion knobRegion = uiAtlas.findRegion(RegionNames.SCROLL_KNOB);
		
		TextureRegion plusRegionWhite = new TextureRegion(uiAtlas.findRegion(RegionNames.ARROW_RIGHT_WHITE)); // native white
		TextureRegion minusRegionWhite = new TextureRegion(uiAtlas.findRegion(RegionNames.ARROW_DOWN_WHITE));// native white
		
		// pane Drawable
		PaneSpriteDrawable paneDrawableDarkestGray = new PaneSpriteDrawable(paneRegion, Pane.DARKEST_GRAY.cpy());
		PaneSpriteDrawable paneDrawableDarkestGrayHalfAlpha = new PaneSpriteDrawable(paneRegion, Pane.DARKEST_GRAY.cpy().sub(0, 0, 0, 0.5f));
		PaneSpriteDrawable paneDrawableDarkGray = new PaneSpriteDrawable(paneRegion, Pane.DARK_GRAY.cpy());
		PaneSpriteDrawable paneDrawableGray = new PaneSpriteDrawable(paneRegion, Pane.GRAY.cpy());
		PaneSpriteDrawable paneDrawableLightGray = new PaneSpriteDrawable(paneRegion, Pane.LIGHT_GRAY.cpy());
		PaneSpriteDrawable paneDrawableHalfLightGray = new PaneSpriteDrawable(paneRegion, Pane.GRAY.cpy().lerp(Pane.LIGHT_GRAY.cpy(), 0.5f));
		PaneSpriteDrawable paneDrawableClear = new PaneSpriteDrawable(paneRegion, Color.CLEAR.cpy());
		
		// scrollpane
		ScrollPaneStyle scrollPaneStyle = new ScrollPaneStyle();
		scrollPaneStyle.hScrollKnob = scrollPaneStyle.vScrollKnob = new PaneSpriteDrawable(knobRegion, Pane.LIGHT_GRAY.cpy());
		
		// splitpane
		SplitPaneStyle splitPaneStyle = new SplitPaneStyle();
		splitPaneStyle.handle = new PaneSpriteDrawable(pixelRegion, Pane.GRAY.cpy());
		
		// image text button
		ImageTextButtonStyle imageTextButtonStyleDarkGray10 = new ImageTextButtonStyle();
		imageTextButtonStyleDarkGray10.up = new PaneSpriteDrawable(paneRegion, Pane.DARK_GRAY.cpy());
		imageTextButtonStyleDarkGray10.checked = new PaneSpriteDrawable(paneRegion, Pane.LIGHT_GRAY.cpy());
		imageTextButtonStyleDarkGray10.down = new PaneSpriteDrawable(paneRegion, Pane.LIGHT_GRAY.cpy());
		imageTextButtonStyleDarkGray10.over = paneDrawableHalfLightGray;
		imageTextButtonStyleDarkGray10.fontColor = Pane.GREEN.cpy();
		imageTextButtonStyleDarkGray10.font = Fonts.getDefaultFontBySize(FontSize.x10);
		
		ImageTextButtonStyle imageTextButtonStyleDarkGray12 = new ImageTextButtonStyle(imageTextButtonStyleDarkGray10);
		imageTextButtonStyleDarkGray12.font = Fonts.getDefaultFontBySize(FontSize.x12);
		
		
		ImageTextButtonStyle imageTextButtonStyleGray10 = new ImageTextButtonStyle(imageTextButtonStyleDarkGray10);
		imageTextButtonStyleGray10.up = new PaneSpriteDrawable(paneRegion, Pane.GRAY.cpy());
		imageTextButtonStyleGray10.checked = new PaneSpriteDrawable(paneRegion, Pane.LIGHT_GRAY.cpy());
		
		ImageTextButtonStyle imageTextButtonStyleGray12 = new ImageTextButtonStyle(imageTextButtonStyleGray10);
		imageTextButtonStyleGray12.font = Fonts.getDefaultFontBySize(FontSize.x12);
		
		
		ImageTextButtonStyle imageTextButtonStyleDarkestGray10 = new ImageTextButtonStyle(imageTextButtonStyleDarkGray10);
		imageTextButtonStyleDarkestGray10.up = new PaneSpriteDrawable(paneRegion, Pane.DARKEST_GRAY.cpy());
		imageTextButtonStyleDarkestGray10.checked = new PaneSpriteDrawable(paneRegion, Pane.GRAY.cpy());
		
		ImageTextButtonStyle imageTextButtonStyleDarkestGray12 = new ImageTextButtonStyle(imageTextButtonStyleDarkestGray10);
		imageTextButtonStyleDarkestGray12.font = Fonts.getDefaultFontBySize(FontSize.x12);
		
		
		// label
		LabelStyle labelStyleX10 = new LabelStyle();
		labelStyleX10.font = Fonts.getDefaultFontBySize(FontSize.x10);
		labelStyleX10.fontColor = Pane.GREEN.cpy();
		
		LabelStyle labelStyleX12 = new LabelStyle();
		labelStyleX12.font = Fonts.getDefaultFontBySize(FontSize.x12);
		labelStyleX12.fontColor = Pane.GREEN.cpy();
		
		// textfield
		Sprite selectionSprite = new Sprite(pixelRegion);
		Color selectionSpriteColor = Pane.GREEN.cpy();
		selectionSpriteColor.a = 0.3f;
		selectionSprite.setColor(selectionSpriteColor);
		SpriteDrawable selectionDrawable = new SpriteDrawable(selectionSprite);
		Sprite cursorSprite = new Sprite(pixelRegion);
		SpriteDrawable cursorDrawable = new SpriteDrawable(cursorSprite);
		
		TextFieldStyle textFielsStyleX10 = new TextFieldStyle(
				Fonts.getDefaultFontBySize(FontSize.x10), 
				Pane.GREEN.cpy(), cursorDrawable, selectionDrawable, paneDrawableDarkestGray);
		
		TextFieldStyle textFielsStyleX12 = new TextFieldStyle(
				Fonts.getDefaultFontBySize(FontSize.x12), 
				Pane.GREEN.cpy(), cursorDrawable, selectionDrawable, paneDrawableDarkestGray);

		
		
		
		
		// plus minus button
		ButtonStyle plusMinusButtonStyleDarkGray = new ButtonStyle();
		plusMinusButtonStyleDarkGray.up = new SpriteDrawable(createSprite(plusRegionWhite, Pane.DARK_GRAY));
		plusMinusButtonStyleDarkGray.checked = new SpriteDrawable(createSprite(minusRegionWhite, Pane.DARK_GRAY));
		
		ButtonStyle plusMinusButtonStyleLightGray = new ButtonStyle();
		plusMinusButtonStyleLightGray.up = new SpriteDrawable(createSprite(plusRegionWhite, Pane.LIGHT_GRAY));
		plusMinusButtonStyleLightGray.checked = new SpriteDrawable(createSprite(minusRegionWhite, Pane.LIGHT_GRAY));
		
		ButtonStyle plusMinusButtonStyleGray = new ButtonStyle();
		plusMinusButtonStyleGray.up = new SpriteDrawable(createSprite(plusRegionWhite, Pane.GRAY));
		plusMinusButtonStyleGray.checked = new SpriteDrawable(createSprite(minusRegionWhite, Pane.GRAY));
		
		
		// ListStyle
		ListStyle listStyleDarkGray10 = new ListStyle();
		listStyleDarkGray10.selection = new PaneSpriteDrawable(paneRegion, Color.CLEAR); //
		listStyleDarkGray10.over = selectionDrawable;
		listStyleDarkGray10.background = paneDrawableDarkGray;
		listStyleDarkGray10.down = paneDrawableDarkestGray;
		listStyleDarkGray10.font = Fonts.getDefaultFontBySize(FontSize.x10);
		listStyleDarkGray10.fontColorSelected = listStyleDarkGray10.fontColorUnselected = Pane.GREEN.cpy();
		
		ListStyle listStyleDarkGray12 = new ListStyle(listStyleDarkGray10);
		listStyleDarkGray12.font = Fonts.getDefaultFontBySize(FontSize.x12);
		
		ListStyle listStyleGray10 = new ListStyle(listStyleDarkGray10);
		listStyleGray10.background = paneDrawableGray;
		listStyleGray10.down = paneDrawableDarkGray;
		
		ListStyle listStyleGray12 = new ListStyle(listStyleDarkGray12);
		listStyleGray12.background = paneDrawableGray;
		listStyleGray12.down = paneDrawableDarkGray;
		
		
		// select box
		SelectBoxStyle selectBoxStyleDarkGray10 = new SelectBoxStyle();
		selectBoxStyleDarkGray10.background = paneDrawableDarkGray;
		selectBoxStyleDarkGray10.font = Fonts.getDefaultFontBySize(FontSize.x10);
		selectBoxStyleDarkGray10.fontColor = Pane.GREEN.cpy();
		selectBoxStyleDarkGray10.scrollStyle = scrollPaneStyle;
		selectBoxStyleDarkGray10.listStyle = listStyleDarkGray10;
		
		
		SelectBoxStyle selectBoxStyleDarkGray12 = new SelectBoxStyle(selectBoxStyleDarkGray10);
		selectBoxStyleDarkGray12.font = Fonts.getDefaultFontBySize(FontSize.x12);
		selectBoxStyleDarkGray12.listStyle = listStyleDarkGray12;
		
		
		SelectBoxStyle selectBoxStyleGray10 = new SelectBoxStyle(selectBoxStyleDarkGray10);
		selectBoxStyleDarkGray10.background = paneDrawableGray;

		SelectBoxStyle selectBoxStyleGray12 = new SelectBoxStyle(selectBoxStyleDarkGray12);
		selectBoxStyleDarkGray10.background = paneDrawableGray;
		
		
		// selection button
		ButtonStyle selectionButtonStyleDarkestGray = new ButtonStyle();
		selectionButtonStyleDarkestGray.up = paneDrawableClear;
		selectionButtonStyleDarkestGray.checked = paneDrawableDarkestGray;
		selectionButtonStyleDarkestGray.over = paneDrawableDarkestGrayHalfAlpha;
		
		ButtonStyle pressedButtonStyleDarkestGray = new ButtonStyle();
		pressedButtonStyleDarkestGray.up = paneDrawableClear;
		pressedButtonStyleDarkestGray.down = paneDrawableDarkestGray;
		pressedButtonStyleDarkestGray.over = paneDrawableDarkestGrayHalfAlpha;
		
		
		// checkbox
		CheckBoxStyle checkBoxStyle10 = new CheckBoxStyle();
		checkBoxStyle10.checkboxOff = new TextureRegionDrawable(uiAtlas.findRegion(RegionNames.CHECK_BOX_UNCHECKED));
		checkBoxStyle10.checkboxOn = new TextureRegionDrawable(uiAtlas.findRegion(RegionNames.CHECK_BOX_CHECKED));
		checkBoxStyle10.font = Fonts.getDefaultFontBySize(FontSize.x10);
		checkBoxStyle10.fontColor = Pane.GREEN.cpy();
		
		CheckBoxStyle checkBoxStyle12 = new CheckBoxStyle(checkBoxStyle10);
		checkBoxStyle12.font = Fonts.getDefaultFontBySize(FontSize.x12);
		
		
		// tree style
		TreeStyle treeStyle = new TreeStyle();
		treeStyle.plus = new SpriteDrawable(createSprite(plusRegionWhite, Pane.GRAY));
		treeStyle.minus = new SpriteDrawable(createSprite(minusRegionWhite, Pane.GRAY));
		treeStyle.selection = paneDrawableDarkestGray;
		treeStyle.over = paneDrawableDarkestGrayHalfAlpha;
		
		
		Skin skin = game.getSkin();
		skin.addRegions(uiAtlas);
		skin.add(SkinNames.PIXEL_REGION, pixelRegion);
		
		skin.add(SkinNames.PANE_DRAWABLE_LIGHT_GRAY, paneDrawableLightGray);
		skin.add(SkinNames.PANE_DRAWABLE_GRAY, paneDrawableGray);
		skin.add(SkinNames.PANE_DRAWABLE_DARK_GRAY, paneDrawableDarkGray);
		skin.add(SkinNames.PANE_DRAWABLE_DARKEST_GRAY, paneDrawableDarkestGray);
		
		skin.add(SkinNames.SCROLL_PANE_STYLE, scrollPaneStyle);
		skin.add(SkinNames.SPLIT_PANE_STYLE, splitPaneStyle);
		
		
		skin.add(SkinNames.IMAGE_TEXT_BUTTON_STYLE_DARKEST_GRAY10, imageTextButtonStyleDarkestGray10);
		skin.add(SkinNames.IMAGE_TEXT_BUTTON_STYLE_DARKEST_GRAY12, imageTextButtonStyleDarkestGray12);
		skin.add(SkinNames.IMAGE_TEXT_BUTTON_STYLE_DARK_GRAY10, imageTextButtonStyleDarkGray10);
		skin.add(SkinNames.IMAGE_TEXT_BUTTON_STYLE_DARK_GRAY12, imageTextButtonStyleDarkGray12);
		skin.add(SkinNames.IMAGE_TEXT_BUTTON_STYLE_GRAY10, imageTextButtonStyleGray10);
		skin.add(SkinNames.IMAGE_TEXT_BUTTON_STYLE_GRAY12, imageTextButtonStyleGray12);
		
		skin.add(SkinNames.LABEL_STYLE_10, labelStyleX10);
		skin.add(SkinNames.LABEL_STYLE_12, labelStyleX12);
		
		skin.add(SkinNames.TEXT_FIELD_STYLE10, textFielsStyleX10);
		skin.add(SkinNames.TEXT_FIELD_STYLE12, textFielsStyleX12);
		
		skin.add(SkinNames.LIST_STYLE_GRAY_10, listStyleGray10);
		skin.add(SkinNames.LIST_STYLE_GRAY_12, listStyleGray12);
		skin.add(SkinNames.LIST_STYLE_DARK_GRAY_10, listStyleDarkGray10);
		skin.add(SkinNames.LIST_STYLE_DARK_GRAY_12, listStyleDarkGray12);
		
		
		skin.add(SkinNames.SELECT_BOX_STYLE_DARK_GRAY_10, selectBoxStyleDarkGray10);
		skin.add(SkinNames.SELECT_BOX_STYLE_DARK_GRAY_12, selectBoxStyleDarkGray12);
		skin.add(SkinNames.SELECT_BOX_STYLE_GRAY_10, selectBoxStyleGray10);
		skin.add(SkinNames.SELECT_BOX_STYLE_GRAY_12, selectBoxStyleGray12);
		
		
		skin.add(SkinNames.PLUS_MINUS_BUTTON_STYLE_GRAY, plusMinusButtonStyleGray);
		skin.add(SkinNames.PLUS_MINUS_BUTTON_STYLE_DARK_GRAY, plusMinusButtonStyleDarkGray);
		skin.add(SkinNames.PLUS_MINUS_BUTTON_STYLE_LIGHT_GRAY, plusMinusButtonStyleLightGray);
		
		skin.add(SkinNames.SELECTION_BUTTON_STYLE_DARKEST_GRAY, selectionButtonStyleDarkestGray);
		skin.add(SkinNames.PRESSED_BUTTON_STYLE_DARKEST_GRAY, pressedButtonStyleDarkestGray);
		
		skin.add(SkinNames.CHECK_BOX_STYLE, checkBoxStyle10);
		skin.add(SkinNames.TREE_STYLE, treeStyle);
		
	}
	
	private Sprite createSprite(TextureRegion region, Color color) {
		Sprite s = new Sprite(region);
		s.setColor(color);
		return s;
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}
	
	@Override
	public void hide() {
		dispose();
	}
	
	@Override
	public void dispose() {
		renderer.dispose();
	}
	
	
}
