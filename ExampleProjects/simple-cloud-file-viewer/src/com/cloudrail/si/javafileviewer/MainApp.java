package com.cloudrail.si.javafileviewer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cloudrail.si.interfaces.CloudStorage;
import com.cloudrail.si.javafileviewer.model.Services;
import com.cloudrail.si.types.CloudMetaData;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MainApp extends Application {
	
	/**
	 * This listener reacts on someone clicking the expand button in the tree view. It updates
	 * the file tree by receiving the children for the next layer.
	 */
	private final ChangeListener<Boolean> onExpand = new ChangeListener<Boolean>() {

		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			if(newValue) {
				BooleanProperty bb = (BooleanProperty) observable;
				
				@SuppressWarnings("unchecked")
				TreeItem<String> item = (TreeItem<String>) bb.getBean();
				String path = getPath(item);
				
				List<TreeItem<String>> children = item.getChildren();
				
				for (TreeItem<String> child : children) {
					String childPath = path + "/" + child.getValue();
					CloudMetaData childMetaData = cache.get(childPath);
					
					// If content of a folder has not been fetched before...
					if(childMetaData.getFolder() && child.getChildren().isEmpty()) {
						// ... get a list of children for this folder and insert it into the tree view.
						new InsertNewLayerTask(child, childPath).start();
					}
				}
			}
		}
		
		private String getPath(TreeItem<String> item) {
			TreeItem<String> parent = item.getParent();
			String path = item.getValue();
			
			while(parent != null) {
				path = parent.getValue() + "/" + path;
				parent = parent.getParent();
			}
			
			return path.substring(4);
		}
		
	};
	
	private Stage primaryStage;
    private BorderPane rootLayout;
    private HBox selected = null;
    
    @FXML
    private AnchorPane rightAnchor;
    
    private TreeView<String> treeView;
    
    private Map<String, CloudMetaData> cache;
    
    private CloudStorage cs;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
        this.primaryStage.setTitle("CloudRail File Viewer");
        
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout, 600, 280);
            scene.getStylesheets().add("com/cloudrail/si/javafileviewer/style.css");
            primaryStage.setScene(scene);
            primaryStage.show();
            
            // Load standard view
            loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/MainView.fxml"));
            AnchorPane standardView = (AnchorPane) loader.load();
            
            // Set standard view into the center of root layout.
            rootLayout.setCenter(standardView);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Services.getInstance().prepare();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	/**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    /**
     * Is executed when user selects one of the four services. Starts the authentication process
     * and receives the content of the root folder.
     * 
     * @param event The event that occurred.
     */
    @FXML
    public void onSelectService(MouseEvent event) {
    	HBox source = (HBox) event.getSource();
    	if(selected != null) {
    		selected.getStyleClass().remove("selected");
    	}
    	selected = source;
    	selected.getStyleClass().add("selected");
    	this.rightAnchor.getChildren().clear();
    	Label tv = new Label("Receiving file list...");
    	this.rightAnchor.getChildren().add(tv);
    	
    	cs = Services.getInstance().getService(source.getId());
    	cache = new HashMap<>();
    	
    	new SetUpTreeViewTask().start();
    }
    
    /**
     * Task that retrieves a list of children for a specific folder and then
     * inserts them into the tree view.
     * 
     * @author patrick
     */
    private class InsertNewLayerTask extends Thread {
    	
    	private TreeItem<String> rootItem;
    	private String path;
    	

		public InsertNewLayerTask(TreeItem<String> rootItem, String path) {
			this.rootItem = rootItem;
			this.path = path;
		}


		@Override
		public void run() {
			List<CloudMetaData> content = cs.getChildren(path);
			Platform.runLater(new Publish(content));
		}
		
		private class Publish implements Runnable {
			
			private List<CloudMetaData> content;

			public Publish(List<CloudMetaData> content) {
				this.content = content;
			}

			@Override
			public void run() {
				for (CloudMetaData cmd : content) {
					cache.put(cmd.getPath(), cmd);
		    		TreeItem<String> item = new TreeItem<>(cmd.getName());
		    		item.expandedProperty().addListener(onExpand);
		    		rootItem.getChildren().add(item);
		    	}
			}
			
		}
    	
    }
    /**
     * Task that initially creates the tree view after the authorization is completed.
     * 
     * @author patrick
     */
    private class SetUpTreeViewTask extends Thread {
    	
    	@Override
		public void run() {
    		List<CloudMetaData> rootContent = cs.getChildren("/");
    		Platform.runLater(new Publish(rootContent));
		}
    	
    	private class Publish implements Runnable {
    		
    		private List<CloudMetaData> data;

			public Publish(List<CloudMetaData> data) {
				this.data = data;
			}

			@Override
			public void run() {
				CloudMetaData rootDummy = new CloudMetaData();
				rootDummy.setName("root");
				
				TreeItem<String> rootItem = new TreeItem<String>(rootDummy.getName());
		    	rootItem.setExpanded(true);
		    	
		    	for (CloudMetaData cmd : data) {
		    		cache.put(cmd.getPath(), cmd);
		    		TreeItem<String> item = new TreeItem<>(cmd.getName());
		    		item.expandedProperty().addListener(onExpand);
		    		rootItem.getChildren().add(item);
		    		
		    		if(cmd.getFolder()) {
		    			new InsertNewLayerTask(item, cmd.getPath()).start();
		    		}
		    	}
		    	
		    	treeView = new TreeView<String>(rootItem);
		    	AnchorPane.setBottomAnchor(treeView, 0.0);
		    	AnchorPane.setTopAnchor(treeView, 0.0);
		    	AnchorPane.setLeftAnchor(treeView, 0.0);
		    	AnchorPane.setRightAnchor(treeView, 0.0);
		    	
		    	rightAnchor.getChildren().clear();
		    	rightAnchor.getChildren().add(treeView);
			}
    		
    	}
    	
    }
}
