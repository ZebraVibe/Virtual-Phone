package com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item.group;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.MusicLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Music.OnCompletionListener;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Logger;
import com.ouken.phone.app.oukenstudioapp.assets.RegionNames;
import com.ouken.phone.app.oukenstudioapp.common.EditorManager;
import com.ouken.phone.app.oukenstudioapp.editor.ui.menu.ContextMenu;
import com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item.CreateMenuItem;
import com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item.DeleteMenuItem;
import com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item.DeleteNodeMenuItem;
import com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item.MenuItem;
import com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item.RenameMenuItem;
import com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item.RenameNodeMenuItem;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tabs.FileExplorer;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.FileHandleNode;
import com.ouken.phone.app.oukenstudioapp.editor.ui.window.TextFieldWindow;
import com.ouken.phone.app.oukenstudioapp.editor.utils.EditorUtils;
import com.ouken.phone.app.oukenstudioapp.editor.utils.ValueButton;
import com.ouken.phone.app.oukenstudioapp.editor.utils.ValueButton.ValueType;
import com.ouken.phone.app.oukenstudioapp.editor.ui.window.TextFieldWindow.TFValidatable;

public class EditFileBrowserMenuItemGroup extends MenuItemGroup<ValueButton>{
	
	private static final Logger log = new Logger(EditFileBrowserMenuItemGroup.class.getName(), Logger.DEBUG);
	
	private MenuItem<ValueButton> audioItem;
	
	public EditFileBrowserMenuItemGroup(FileExplorer explorer, Skin skin, ShapeRenderer renderer) {
		
		add(new CreateMenuItem<ValueButton>("Create Dir", skin.getRegion(RegionNames.FOLDER_SMALL), skin, renderer) {
			
			@Override
			public void onClick(ValueButton selected, TextFieldWindow window, TFValidatable validate,Consumer<TextField> onSuccess) {
				super.onClick(selected, window, tf -> {
					String text = tf.getText();
					if (text != null && !text.equals("")) {
						FileHandleNode selectedNode = explorer.getExplorerTree().getSelectedNode();
						if(selectedNode == null)return false;// a tree node must be selected to see the browser
						FileHandle browserFile = selected != null ? ((FileHandle) selected.getValue()).parent() : selectedNode.getValue();
						if(browserFile != null && EditorManager.INSTANCE.createFileHandle(browserFile, text + "/",false) != null) {
							return true;
						}
						return false;
					}
					return false;
				}, tf -> {
					FileHandleNode selectedNode = explorer.getExplorerTree().getSelectedNode();
					//FileHandleNode newNode = explorer.addNodeToSelectedOrAsRoot(selectedNode.getValue().child(tf.getText()), tf.getText());
					//explorer.setContentBrowserOf(explorer.getExplorerTree().getSelectedNode());
					explorer.addToContentBrowser(selectedNode.getValue().child(tf.getText()));
				});
			}
			
		});

		add(new RenameMenuItem<ValueButton>("Rename", skin, renderer) {
			@Override
			public void onWindowCreated(ValueButton selected, TextFieldWindow window, String header, String preText) {
				super.onWindowCreated(selected, window, header, getSelectedName(selected));
			}
			@Override
			public void onClick(ValueButton selected, TextFieldWindow window, TFValidatable validate, Consumer<TextField> onSuccess) {
				if(selected.hasFileHandleValue())
					super.onClick(selected, window, tf -> { 
						String text = tf.getText();
						if(text == null || text.equals(""))return false;
						FileHandle old = (FileHandle)selected.getValue();

						FileHandle dest = EditorManager.INSTANCE.renameFileHandle(old, text, false);
						if(dest != null) {
							FileHandleNode node = explorer.getExplorerTree().findNode(old);
							if(node != null)node.setValue(dest);
							selected.setValue(dest);
							return true;
						}
						return false;
						}, tf -> {
						FileHandle value = (FileHandle)selected.getValue();
						
						Label label = explorer.getBrowserItemLabel(selected);
						label.setText(tf.getText());
							
						if (value.isDirectory()) {
							FileHandleNode node = explorer.getExplorerTree().findNode(value);
							if (node != null)node.setLabelText(tf.getText());
						}
						//explorer.setContentBrowserOf(explorer.getExplorerTree().getSelectedNode()); // why update ?

					});
			}
			
		});
		
		add(new MenuItem<ValueButton>("Duplictae", skin) {
			@Override
			public void onClick(ValueButton selected) {
				
			}
		});
		
		add(new DeleteMenuItem<ValueButton>("Delete", skin, renderer) {

			@Override
			public void onDelete(ValueButton selected) {
				// logic in explorer.removeNode
				if(selected.hasFileHandleValue()) {
					explorer.removeFromContentBrowser(selected);
				}
			}
			
			@Override
			public String getDeletedObjectName(ValueButton selected) {
				return getSelectedName(selected);
			}
			
		});
		String s = "Open in Explorer"; // "Import";
		add(new MenuItem<ValueButton>(s, skin) {
			@Override
			public void onClick(ValueButton selected) {
				FileHandleNode selectedNode = null;
				String path = null;
				File file;
				
				if(selected != null && selected.getValue() instanceof FileHandle) {
					file = ((FileHandle)selected.getValue()).file();
					path = file.getPath();	
				}else {
					if((selectedNode = explorer.getExplorerTree().getSelectedNode()) == null) {
						path = EditorManager.INSTANCE.getCurrentProjectDir().file().getPath();
					}else {
						file = selectedNode.getValue().file();
						path = file.getPath();
					}
				}
				
				try {
					Runtime.getRuntime().exec("explorer.exe /select," + path);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		String play = "Play Audio", stop = "Stop Audio";
		add(new MenuItem<ValueButton>(play, skin.getRegion(RegionNames.ARROW_RIGHT) , skin) {
			
			@Override
			public void onClick(ValueButton selected) {
				if(!hasFileHandle(selected))return;
				FileHandle value = (FileHandle)selected.getValue();
				
				if(explorer.isAudio(value)) {
					Music m = getMusic(value);
					
					if(m == null)return;
					
					if(!m.isPlaying()) {
						m.play();
						getLabel().setText(stop);
					}else {
						getLabel().setText(play);
						m.stop();
					}
				}
			}
			
			@Override
			public void onMenuMadeVisible(ContextMenu<ValueButton> menu, ValueButton selected) {
				if(!hasFileHandle(selected)) {
					setHideItem(true);
//					log.debug("selected is unusable: is button NULL ?: " + 
//							(selected == null ? " selected button NULL." : ( selected.getValue() == null ? " value NULL." : "No filehandle? " +
//									(!(selected.getValue() instanceof FileHandle)) ) ));
					return;
				}
				
				FileHandle value = (FileHandle)selected.getValue();
				boolean isAudio = explorer.isAudio(value);
				
				if(explorer.isAudio(value)) {
					Music m = getMusic(value);
					if(m != null) {
					
						if(!m.isPlaying()) {
							getLabel().setText(play);
						}else {
							getLabel().setText(stop);
						}
						
					}
				}
				
				setHideItem(!isAudio);
//				log.debug("is audio " + (isAudio));	
			}
			
			@Override
			public boolean hideMenuOnClick() {
				return false;
			}
			private boolean hasFileHandle(ValueButton selected) {
				return !(selected == null || selected.getValue() == null || !(selected.getValue() instanceof FileHandle));
			}
			
			
			private Music getMusic(FileHandle value) {
				Object obj = explorer.getDisposables().get(value.path());
				Music m = null;
				
				if(obj == null) {
					m = Gdx.audio.newMusic(value);
					m.setOnCompletionListener(new OnCompletionListener() {
						
						@Override
						public void onCompletion(Music music) {
							getLabel().setText(play);
						}
						
					});
					explorer.getDisposables().put(value.path(), m);
				}else if(!(obj instanceof Music))return null;
				else m = (Music)obj;
				return m;
			}
			
			
		});
		
	}

	
	private String getSelectedName(ValueButton selected) {
		if(selected.hasFileHandleValue()) {
			return ((FileHandle) selected.getValue()).nameWithoutExtension();
		} else if(selected.hasEntityValue()) {
			Entity e = ((Entity) selected.getValue());
			return "<no name comp.>";
		}
		return "<???>";
	}
	
	
}
