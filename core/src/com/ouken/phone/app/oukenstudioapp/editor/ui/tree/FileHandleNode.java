package com.ouken.phone.app.oukenstudioapp.editor.ui.tree;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.ouken.phone.app.oukenstudioapp.assets.RegionNames;

public class FileHandleNode extends CustomNode<FileHandleNode, FileHandle> {
	
	private TextureRegionDrawable folder, folderOpen;
	
	
	public FileHandleNode(Skin skin) {
		super(skin, skin.getRegion(RegionNames.FOLDER_SMALL));
		folder = new TextureRegionDrawable(getSkin().getRegion(RegionNames.FOLDER_SMALL));
		folderOpen = new TextureRegionDrawable(getSkin().getRegion(RegionNames.FOLDER_OPEN_SMALL));
	}

	@Override
	public void setExpanded(boolean expanded) {
		setIcon(expanded && hasChildren()? folderOpen : folder);
		super.setExpanded(expanded);
	}
	


}
