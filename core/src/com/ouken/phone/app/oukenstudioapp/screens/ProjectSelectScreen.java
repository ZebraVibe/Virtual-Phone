package com.ouken.phone.app.oukenstudioapp.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ouken.phone.app.oukenstudioapp.OukenStudioApp;
import com.ouken.phone.app.oukenstudioapp.assets.RegionNames;
import com.ouken.phone.app.oukenstudioapp.assets.SkinNames;
import com.ouken.phone.app.oukenstudioapp.common.EditorManager;
import com.ouken.phone.app.oukenstudioapp.config.EditorConfig;
import com.ouken.phone.app.oukenstudioapp.editor.ui.window.CreateProjectWindow;
import com.ouken.phone.app.oukenstudioapp.editor.utils.Pane;
import com.ouken.phone.app.oukenstudioapp.editor.utils.PaneSpriteDrawable;
import com.ouken.phone.app.utils.AppScreenViewport;
import com.ouken.phone.utils.GdxUtils;
import com.ouken.phone.utils.font.FontSize;
import com.ouken.phone.utils.font.Fonts;

public class ProjectSelectScreen extends ScreenAdapter {
	
	
	private static final int BUTTON_WIDTH = 48, BUTTON_HEIGHT = 64;
	
	private OukenStudioApp app;
	private Skin skin;
	private Viewport viewport;
	private Stage stage;
	private SpriteBatch batch;
	
	private Table projectsTable;
	private ShapeRenderer renderer;
	
	private TextureRegion folder, pane;
	
	public ProjectSelectScreen(OukenStudioApp app) {
		this.app = app;
		this.skin = app.getSkin();
		this.batch = app.getBatch();
	}
	
	@Override
	public void show() {
		renderer = new ShapeRenderer();
		viewport = new AppScreenViewport(true);
		stage = new Stage(viewport, batch);
		
		app.addProcessors(stage);
		
		init();
	}
	
	private void init() {
		
		projectsTable = new Table();
		projectsTable.defaults().spaceRight(8);
		projectsTable.center();
		
		folder = skin.getRegion(RegionNames.FOLDER_BIG);
		pane = skin.getRegion(RegionNames.PANE);
		ButtonStyle buttonStyle = new ButtonStyle();
		buttonStyle.up = new PaneSpriteDrawable(pane, Pane.DARK_GRAY);
		buttonStyle.down = new PaneSpriteDrawable(pane, Pane.DARKEST_GRAY);

		
		// create proj button
		Button b = new Button(buttonStyle);
		Label l = new Label("New Proj", skin.get(SkinNames.LABEL_STYLE_10, LabelStyle.class));
		b.add(l);
		b.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				projectsTable.setTouchable(Touchable.disabled);
				b.setDisabled(true);
				
				CreateProjectWindow window = new CreateProjectWindow(skin, renderer);
				window.addOnSuccValidation(tf -> {
					app.setScreen(new EditorScreen(app));
				});
				
				window.addWindowClosedRunnable(() -> {
					projectsTable.setTouchable(Touchable.enabled);
					b.setDisabled(false);
				});
				
				window.setPosition(stage.getWidth() / 2f, stage.getHeight() / 2f, Align.center);
				stage.addActor(window);
				
			}
		});
		projectsTable.add(b).size(BUTTON_WIDTH,BUTTON_HEIGHT);
		
		
		String demoProjectName = EditorConfig.DEMO_PROJECT_NAME;
		boolean hasDemoProject = false;
		
		// project buttons 
		for(FileHandle file: EditorManager.INSTANCE.getProjectsDir().list()) {
			if(file.nameWithoutExtension().equals(demoProjectName))hasDemoProject = true;
			addProjectButton(file, buttonStyle);
		}
		
		// add demo if absent
//		if(!hasDemoProject) {
//			FileHandle file = EditorManager.INSTANCE.createProject(demoProjectName);
//			addProjectButton(file, buttonStyle);
//		}
		
		
		projectsTable.pack();
		projectsTable.setPosition(stage.getWidth() / 2, stage.getHeight() /2, Align.center);
		
		// header
		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = Fonts.getDefaultFontBySize(FontSize.x14);
		labelStyle.fontColor = Pane.GREEN.cpy();
		Label header = new Label("Project Select", labelStyle);
		
		Table headerTable = new Table();
		headerTable.setFillParent(true);
		headerTable.top();
		headerTable.add(header);
		
		stage.addActor(headerTable);
		stage.addActor(projectsTable);
			
	}
	
	private void addProjectButton(FileHandle file, ButtonStyle style) {
		Button button = new Button(style);
		
		button.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				EditorManager.INSTANCE.setProjectCurrent(file);
				app.log().debug("changing to " + EditorScreen.class);
				app.getProcessors().clear();
				app.setScreen(new EditorScreen(app));
			}
			
		});
		
		
		Image img = new Image(folder);
		Label label = new Label(file.nameWithoutExtension(), skin.get(SkinNames.LABEL_STYLE_10, LabelStyle.class));
		
		button.center();
		button.add(img).expand().center().row();
		button.add(label).maxWidth(BUTTON_WIDTH);
		
		projectsTable.add(button).size(BUTTON_WIDTH,BUTTON_HEIGHT);
	}
	
	
	
	@Override
	public void render(float delta) {
		GdxUtils.clearScreen(Pane.GRAY);
		
		viewport.apply();
		stage.act();
		stage.draw();
		
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}
	
	@Override
	public void hide() {
		renderer.dispose();
		stage.dispose();
	}
	
	@Override
	public void dispose() {
		hide();
	}
	
}
