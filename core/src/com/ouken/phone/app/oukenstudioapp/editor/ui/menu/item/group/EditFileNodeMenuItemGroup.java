package com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item.group;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.utils.Logger;
import com.ouken.phone.app.oukenstudioapp.assets.RegionNames;
import com.ouken.phone.app.oukenstudioapp.common.EditorManager;
import com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item.MenuItem;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tabs.Explorer;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tabs.FileExplorer;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.FileHandleNode;
import com.ouken.phone.app.oukenstudioapp.editor.ui.window.TextFieldWindow;
import com.ouken.phone.app.oukenstudioapp.editor.ui.window.TextFieldWindow.TFValidatable;
import com.ouken.phone.app.oukenstudioapp.editor.utils.ValueButton;

public class EditFileNodeMenuItemGroup extends EditNodeMenuItemGroup<FileExplorer, FileHandleNode, FileHandle> {

	private static final Logger log = new Logger(EditFileBrowserMenuItemGroup.class.getName(), Logger.DEBUG);
	
	public EditFileNodeMenuItemGroup(FileExplorer explorer, Skin skin, ShapeRenderer renderer) {
		super(explorer, skin, renderer);
		
		String s = "Open in Explorer"; // "Import";
		add(new MenuItem<FileHandleNode>(s, skin) {
			@Override
			public void onClick(FileHandleNode selected) {
				FileHandleNode selectedNode = selected;
				String path = null;
				File file;
				
				if ((selectedNode = explorer.getExplorerTree().getSelectedNode()) == null) {
					path = EditorManager.INSTANCE.getCurrentProjectDir().file().getPath();
				} else {
					file = selectedNode.getValue().file();
					path = file.getPath();
				}

				try {
					Runtime.getRuntime().exec("explorer.exe /select," + path);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
	}


	@Override
	public void onCreateClick(FileHandleNode selected, TextFieldWindow window, TFValidatable validate,Consumer<TextField> onSuccess) {
		if(selected == null)window.closeWindow();
		super.onCreateClick(selected, window,tf -> {
			String text = tf.getText();
			if (text != null && !text.equals("")) {
				return EditorManager.INSTANCE.createFileHandle((FileHandle)selected.getValue() , text + "/", false) != null;
			}
			return false;
		}, tf -> {
			FileHandleNode node = getExplorer().addNodeToSelectedOrAsRoot(selected.getValue().child(tf.getText()),tf.getText());
			getExplorer().setContentBrowserOf(selected);
			if (selected != null && selected.getValue() != null)getTree().getSelection().fireChangeEvent();//changed(selected.getActor());
			node.expandTo();	
		});
	}
	
	
	@Override
	public void onRenameClick(FileHandleNode selected, TextFieldWindow window, TFValidatable validate,Consumer<TextField> onSuccess) {
		if(selected == null)window.closeWindow();
		super.onRenameClick(selected, window,tf -> {
			String text = tf.getText();
			if (text != null && !text.equals("") && selected != null) {

				if(selected.getValue() == null) {
					log.error("selected node value is null");
					return false;
				}
				
				FileHandle dest = EditorManager.INSTANCE.renameFileHandle(selected.getValue(), text, false);
				if(dest == null)return false;
				selected.setValue(dest);
				
				return true;
			}
			return false;
		}, tf -> {
			selected.setLabelText(tf.getText());
			getExplorer().updateExplorerNode(selected);
		});
	}
	
	@Override
	public void onDeleteClick(FileHandleNode selected) {
		// logic in explorer.removeNode
		//selected.getValue().deleteDirectory();
	}
	
	@Override
	public String getCreateIconRegionName() {
		return RegionNames.FOLDER_SMALL;
	}
	
	@Override
	public String getCreateMenuItemName() { 
		return "Create Dir"; 
	}
	

}
