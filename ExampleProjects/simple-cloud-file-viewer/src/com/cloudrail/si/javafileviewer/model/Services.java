package com.cloudrail.si.javafileviewer.model;

import java.util.concurrent.atomic.AtomicReference;

import com.cloudrail.si.interfaces.CloudStorage;
import com.cloudrail.si.servicecode.commands.awaitCodeRedirect.RedirectReceiver;
import com.cloudrail.si.services.Box;
import com.cloudrail.si.services.Dropbox;
import com.cloudrail.si.services.GoogleDrive;
import com.cloudrail.si.services.OneDrive;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Services {
	private static Services ourInstance = new Services();
	
	private static final String REDIRECT_URL = "https://www.cloudrailauth.com/auth";
	
	private RedirectReceiver receiver = new RedirectReceiver() {

		@Override
		public String openAndAwait(String url, String currentState) {
			final AtomicReference<String> ret = new AtomicReference<String>();
			
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					VBox root = new VBox();
					final Stage stage = new Stage();
			    	
			    	WebView webView = new WebView();
					WebEngine webEngine = webView.getEngine();
					webEngine.load(url);
					
					webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {

						@Override
						public void changed(ObservableValue<? extends State> observable, State oldValue,
								State newValue) {
							synchronized(ret) {
								if(webEngine.getLocation().contains("code=")) {
									ret.set(webEngine.getLocation());
									stage.close();
									ret.notify();
								}
							}
						}
						
					});
					
					ScrollPane scrollPane = new ScrollPane();
					scrollPane.setContent(webView);
					
					root.getChildren().addAll(scrollPane);
					
					stage.initModality(Modality.APPLICATION_MODAL);
			        stage.setTitle("Authenticate");
			        stage.setScene(new Scene(root));  
			        stage.show();
				}
				
			});
			
			synchronized(ret) {
				while(ret.get() == null) {
					try {
						ret.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			return ret.get();
		}
    	
    };
	
	private CloudStorage dropbox;
	private CloudStorage googledrive;
	private CloudStorage box;
	private CloudStorage onedrive;
	
	public static Services getInstance() {
		return ourInstance;
	};
	
	private Services() {}
	
	public void prepare() {
		this.initBox();
		this.initDropbox();
		this.initGoogleDrive();
		this.initOneDrive();
	}
	
	public CloudStorage getService(String service) {
		if(receiver == null) {
			throw new RuntimeException("Call 'prepare()' first.");
		}
		
		switch (service) {
			case "dropbox": return dropbox;
			case "googledrive": return googledrive;
			case "box": return box;
			case "onedrive": return onedrive;
		}
		
		throw new IllegalArgumentException("The service '" + service + "' does not exist!");
	}
	
	private void initDropbox() {
        dropbox = new Dropbox(receiver, "u4gevj9clhvdjug", "9ol49hdlk8by9v9", REDIRECT_URL, "Dropbox");
    }

    private void initBox() {
        box = new Box(receiver, "zqgl7zrzxei2c076ss5k9hxf2ivbppfa", "ueG5uWHUarWYQNgldCsCwUwGzvSWlR0Y", REDIRECT_URL, "Box");
    }

    private void initGoogleDrive() {
        googledrive = new GoogleDrive(receiver, "638240013795-k6cavk4npp6gtqkpb56icpm0hm4uo6aq.apps.googleusercontent.com", "hhJG6zCn4F7ObJUzllL3BXoL", REDIRECT_URL, "GoogleDrive");
    }

    private void initOneDrive() {
        onedrive = new OneDrive(receiver, "000000004018F12F", "lGQPubehDO6eklir1GQmIuCPFfzwihMo", REDIRECT_URL, "OneDrive");
    }
}
