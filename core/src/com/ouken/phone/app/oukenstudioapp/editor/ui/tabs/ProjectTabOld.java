package com.ouken.phone.app.oukenstudioapp.editor.ui.tabs;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.ouken.phone.app.oukenstudioapp.assets.RegionNames;
import com.ouken.phone.app.oukenstudioapp.assets.SkinNames;
import com.ouken.phone.app.oukenstudioapp.common.EditorManager;
import com.ouken.phone.app.oukenstudioapp.editor.utils.Pane;
import com.ouken.phone.app.oukenstudioapp.editor.utils.PaneSpriteDrawable;

public class ProjectTabOld extends Table{
	
	private static final Logger log = new Logger(ProjectTabOld.class.getName(), Logger.DEBUG);
	
	private static final float MIN_SPLIT_AMOUNT = 0.28f;
	private static final float MAX_SPLIT_AMOUNT = 0.5f;
	
	private static final String EXPAND_BUTTON_NAME = "expandButton";
	
	private ShapeRenderer renderer;
	private Skin skin;
	private TextureRegion folderSmall, folderOpenSmall, folderBig;
	private TextureRegionDrawable closedFolderDrawable, openedFolderDrawable;
	
	private ScrollPane fileContentScrollPane;
	private ButtonGroup<Button> fileExplorerButtonGroup = new ButtonGroup<>();
	private Button lastChecked;
	
	
	
	public ProjectTabOld(Skin skin, ShapeRenderer renderer) {
		this.renderer = renderer;
		this.skin = skin;

		
		folderSmall = skin.getRegion(RegionNames.FOLDER_SMALL);
		folderOpenSmall = skin.getRegion(RegionNames.FOLDER_OPEN_SMALL);
		folderBig = skin.getRegion(RegionNames.FOLDER_BIG);
		
		closedFolderDrawable = new TextureRegionDrawable(folderSmall);
		openedFolderDrawable = new TextureRegionDrawable(folderOpenSmall);
		
//		setBackground(skin.get(SkinNames.PANE_DRAWABLE_DARK_GRAY, PaneSpriteDrawable.class));
		initSplitPane();

		
	}
	


	private void initSplitPane() {
		PaneSpriteDrawable pane = new PaneSpriteDrawable(skin.getRegion(RegionNames.PANE), Pane.DARK_GRAY);
		
		Table firstWidget = createFileExplorerPane();
		firstWidget.setBackground(pane);
		ScrollPaneStyle scrollStyle = new ScrollPaneStyle(skin.get(SkinNames.SCROLL_PANE_STYLE, ScrollPaneStyle.class));
		fileContentScrollPane = new ScrollPane(new Table(), scrollStyle);
		fileContentScrollPane.setFlickScroll(false);
		scrollStyle.background = new PaneSpriteDrawable(skin.getRegion(RegionNames.PANE), Pane.DARK_GRAY);
		boolean vertical = false;
		
		SplitPane splitPane = new SplitPane(firstWidget, fileContentScrollPane, vertical, skin, SkinNames.SPLIT_PANE_STYLE);
		splitPane.addCaptureListener(new InputListener() {
			
			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				event.getStage().setScrollFocus(event.getTarget());
			}
			
			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				event.getStage().setScrollFocus(null);
			}
			
		});
		splitPane.setSplitAmount(MIN_SPLIT_AMOUNT);
		splitPane.setMinSplitAmount(MIN_SPLIT_AMOUNT);
		splitPane.setMaxSplitAmount(MAX_SPLIT_AMOUNT);
		
		add(splitPane).expand().fill();
	}
	
	private Table createFileExplorerPane() {
		Table container = new Table();
		
		// label
//		Pane labelTable = new Pane(renderer).smoothEdges(true, false, false, false);
//		labelTable.paneColor(Pane.GRAY);
//		labelTable.center();
//		String projectName = EditorManager.INSTANCE.getCurrentProjectName();
//		Label nameLabel = new Label(projectName, skin, SkinNames.LABEL_STYLE_10);
//		labelTable.add(nameLabel);
		
		
		// folders
		Table scrollContent = createFileExplorerContainer(EditorManager.INSTANCE.getCurrentProjectDir(), null);
		scrollContent.top().left();
		ScrollPane scrollPane = new ScrollPane(scrollContent, skin, SkinNames.SCROLL_PANE_STYLE);
		scrollPane.setFadeScrollBars(true);
		scrollPane.setScrollbarsOnTop(true);
		scrollPane.setFlickScroll(false);
		
//		scrollContent.setDebug(true);
		
//		container.add(labelTable).padTop(1).padLeft(1).expandX().fill().row();
		container.add(scrollPane).expand().fill();
		return container;
	}
	
	
	/**parentFileRow == null means has no parent file row*/
	private Table createFileExplorerContainer(FileHandle fileDir, Actor parentFileRow) {
		// check if not empty
		boolean hasDirs = false;
		if (fileDir != null)
			for (FileHandle file : fileDir.list())
				if (file != null && file.isDirectory() && file.exists())hasDirs = true;
		if (!hasDirs)return null; // termination

		
		
		ButtonGroup<Button> rowGroup = new ButtonGroup<>();
		rowGroup.setMaxCheckCount(1);
		rowGroup.setMinCheckCount(1);
		rowGroup.setUncheckLast(true);
		Table container = new Table();
		container.left();
		float space = 4;
		float pad = space + skin.getRegion(RegionNames.ARROW_RIGHT).getRegionWidth();

//		container.setDebug(true);
		
		for (FileHandle file : fileDir.list()) {
			if (file != null && file.exists() && file.isDirectory()) {
				
//				Button oldRow = findFileExplorerRow(file);
//				if(oldRow != null)continue;
				
				Button selectableRow = new Button(skin, SkinNames.SELECTION_BUTTON_STYLE_DARKEST_GRAY);
				selectableRow.setName(file.name());
				rowGroup.add(selectableRow);
//				markRow(selectableRow, file);
//				fileExplorerButtonGroup.add(selectableRow);
				
				Table subContent = new Table();
				Image folderImage = new Image(folderSmall);
				Label folderName = new Label(file.nameWithoutExtension(), skin, SkinNames.LABEL_STYLE_10);
				folderName.setTouchable(Touchable.disabled);
				Button expandButton = new Button(skin, SkinNames.PLUS_MINUS_BUTTON_STYLE_DARK_GRAY);
				expandButton.setName(EXPAND_BUTTON_NAME);
				expandButton.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						if(!hasSubDirs(file))return;
						
						if(subContent.getCells().notEmpty()) {// collapse
							folderImage.setDrawable(closedFolderDrawable); // change folder icon
							subContent.clearChildren(); // remove expanded children

						}else {// expand
							folderImage.setDrawable(openedFolderDrawable); // change folder icon
							subContent.add(createFileExplorerContainer(file, selectableRow)).expandX().fill(); // add expanded children
						}
					}
				});
				
				boolean hasSubDirs = hasSubDirs(file);
				if(!hasSubDirs)expandButton.setVisible(false);

				Table rowContent = new Table();
				rowContent.defaults().padLeft(space);
				rowContent.add(expandButton);
				rowContent.add(folderImage);
				rowContent.add(folderName).padLeft(space/2);
				rowContent.pack();

				selectableRow.addCaptureListener(new ClickListener(){
					@Override
					public void clicked(InputEvent event, float x, float y) { 
						if(event.getTarget() == expandButton) {// dont select if expand button is clicked
							selectableRow.setDisabled(true);
						}else {// when selected deselect the last button since multiple buttongroups
							setLastChecked(selectableRow);
							selectableRow.setDisabled(false);
						}
						
						
						
					}
				});
				selectableRow.addListener(new ChangeListener() { //used to be checked : created the file content
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						Actor target = event.getTarget();// add file content when checked
						if(target instanceof Button && target == selectableRow && ((Button)target).isChecked()) {
							fileContentScrollPane.setActor(createFileContentOf(file, subContent, selectableRow));
						}
					}
				});
				selectableRow.addListener(new ClickListener(){
					@Override
					public void clicked(InputEvent event, float x, float y) {
						if(getTapCount() == 2) {// expand on double click or if already selected
							expandButton.setChecked(!expandButton.isChecked());
						}
						
					}
					
				});
				
				selectableRow.left();
				selectableRow.add(rowContent).padLeft(pad * countParentDirsOf(file));
				float maxHeight = Math.max(folderName.getMaxHeight(), folderImage.getHeight());
				container.add(selectableRow).height(maxHeight + space).expandX().fillX().row();
				if(hasSubDirs)container.add(subContent).expandX().fillX().row();
			}
		}
		rowGroup.uncheckAll();
		return container;
	}
	
	private void setLastChecked(Button row) {
		if(lastChecked != null && lastChecked != row)lastChecked.getButtonGroup().uncheckAll();
		lastChecked = row;
	}
	
	
	private int countParentDirsOf(FileHandle file) {
		int count = 0;
		String projectDirPath = EditorManager.INSTANCE.getCurrentProjectDir().path();
		FileHandle parent = file.parent();
		while(!parent.path().equals(projectDirPath)) {
			parent = parent.parent();
			count++;
		}
		return count;
	}
	
	private boolean hasSubDirs(FileHandle file) {
		for(FileHandle dir : file.list())
			if(dir.isDirectory() && dir.exists()) {
				return true; 
			}
		return false;
	}
	
	
	
	private Table createFileContentOf(FileHandle dir, Table fileRowContainer, Actor parentFileRow) {
		
		Array<Actor> actors = new Array<Actor>();
		
		
		for(FileHandle file : dir.list()) {
			if(!file.exists())continue;
			String ex = file.extension().toLowerCase();
			// directory
			if(file.isDirectory()) {
				actors.add(createFileContentObjectButtonOf(file, fileRowContainer, parentFileRow, folderBig, null ));
			}
			// image: png / jpeg / jpg
			if(ex.equals("png") || ex.equals("jpg") || ex.equals("jpeg")) {}
			// font
			if(ex.equals("ttf")) {}
			// audio
			if(ex.equals("mp3") || ex.equals("wav") || ex.equals("ogg")) {}
		}
		
		// add to table
		float cellW = 48, cellH = 64;
		int maxCols = 4;
		float pad = 20;
		
		Table content = new Table() {
			
			@Override
			public <T extends Actor> Cell<T> add(T actor) {
				Cell<T> cell = super.add(actor);
				int size = this.getCells().size;
				if(size > 0 && (size) % maxCols == 0)cell.row();
				return cell;
			}
			
			
			
		};
		content.pad(pad).padTop(pad / 2);
		content.top().left();
		content.defaults().size(cellW, cellH).left().padRight(pad).padBottom(pad);
		for(Actor a : actors)content.add(a);
		
//		content.setDebug(true);

		
		
		return content;
	}
	
	

	
	
	private Button createFileContentObjectButtonOf(FileHandle file, Table fileRowContainer, Actor parentFileRow, TextureRegion icon, Runnable onUp) {
		ButtonStyle style = new ButtonStyle();
		TextureRegion paneRegion = skin.getRegion(RegionNames.PANE);
		style.up = new PaneSpriteDrawable(paneRegion, Color.CLEAR);
		style.down = new PaneSpriteDrawable(paneRegion, Pane.DARKEST_GRAY);
		Button button = new Button(style);
		
		Image img = new Image(icon);
		Label label = new Label(file.nameWithoutExtension(), skin, SkinNames.LABEL_STYLE_10);
		
		button.add(img).expand().center().row(); // dont fill
		button.add(label);
		
		button.addListener(new InputListener() {
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if(file.isDirectory() && file.exists()) {
					
					
					// has always a parent if a contentObjButton could be created
					Button parent = ((Button)parentFileRow);
					Button parentExpandButton = parent.findActor(EXPAND_BUTTON_NAME);
					parentExpandButton.setChecked(true); // subContent table actors of fileRow might get first initilized here
					parent.getButtonGroup().uncheckAll();

					Button fileRow = fileRowContainer.findActor(file.name());
					fileRow.setChecked(true);
					setLastChecked(fileRow);
					if (onUp != null)onUp.run();
	
				}
			}
			
		});
		return button;
	}
	
	/**returns a dir which is a direct child of the project dir in which the given file is in*/
	private FileHandle getRootDirOf(FileHandle file) {
		String projectDirPath = EditorManager.INSTANCE.getCurrentProjectDir().path();
		FileHandle dir = file;
		while(!dir.parent().path().equals(projectDirPath)) {
			dir = dir.parent();
		}
		return dir;
	}
	
//	private void markRow(Button row, FileHandle file) {
//		if(row != null && file != null)row.setName(file.path());
//	}
//	
//	private Button findFileExplorerRow(FileHandle file) {
//		if(file == null)return null;
//		for(Button b: fileExplorerButtonGroup.getButtons())if(b.getName().equals(file.path()))return b;
//		return null;
//	}
//	

	
	
}
  