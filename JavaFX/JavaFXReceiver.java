package com.cloudrail.si.javafileviewer.model;

import java.util.concurrent.atomic.AtomicReference;

import com.cloudrail.si.servicecode.commands.awaitCodeRedirect.RedirectReceiver;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class JavaFXReceiver implements RedirectReceiver {

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
							if(isAuthSuccessfull(webEngine.getLocation())) {
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
		        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

					@Override
					public void handle(WindowEvent event) {
						synchronized(ret) {
							ret.set("");
							stage.close();
							ret.notify();
						}
					}
		        	
		        });
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
		
		if(ret.get().equals("")) return null;
		
		return ret.get();
	}
	
	private boolean isAuthSuccessfull(String url) {
		boolean containsKey = url.contains("code=");
		
		if(!containsKey) {
			containsKey = url.contains("oauth_token=") && url.contains("oauth_verifier=");
		}
		
		return containsKey;
	}

}
