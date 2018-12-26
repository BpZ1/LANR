package lanr.view;

import java.util.Optional;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabDragPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lanr.model.Settings;

public class SettingsView extends Stage {
	
	private SimpleBooleanProperty changed = new SimpleBooleanProperty(false);
	private ParameterSettingsView parameterView;
	private PerformanceSettingsView performanceView;
	
	public SettingsView() {
		Pane root = createRootPane();
		this.setTitle("Settings");
		Scene scene = new Scene(root, 450, 450);
		this.setScene(scene);
		scene.getStylesheets().add(MainView.class.getResource("Main.css").toExternalForm());
	}
	
	private Pane createRootPane() {
		BorderPane mainPane = new BorderPane();
		TabPane pane = new TabPane();
		pane.setSide(Side.LEFT);
		pane.setTabDragPolicy(TabDragPolicy.FIXED);
		//Performance tab
		Tab performanceTab = new Tab("Performance");
		performanceTab.setClosable(false);
		performanceView = new PerformanceSettingsView(changed);
		performanceTab.setContent(performanceView);		
		//Parameter tab
		Tab parametersTab = new Tab("Parameter");
		parametersTab.setClosable(false);
		this.parameterView = new ParameterSettingsView(changed);
		parametersTab.setContent(parameterView);
		
		pane.getTabs().add(performanceTab);
		pane.getTabs().add(parametersTab);
		//Buttons
		Button applyButton = new Button("Apply");
		applyButton.setOnAction(event ->{
			saveSettings();
		});
		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(event ->{
			if(changed.getValue()) {
				if(confirmationDialog()) {
					this.close();
				}
			}else {
				this.close();
			}
		});
		GridPane buttonPane = new GridPane();
		buttonPane.setAlignment(Pos.CENTER_RIGHT);
		buttonPane.setHgap(10);
		buttonPane.setPadding(new Insets(4, 4, 4, 4));
		buttonPane.add(applyButton, 0, 0);
		buttonPane.add(cancelButton, 1, 0);
		mainPane.setCenter(pane);
		mainPane.setBottom(buttonPane);
		return mainPane;
	}
	
	private boolean confirmationDialog() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Unsaved changes");
		alert.setHeaderText("Do you still want to close?");
		alert.setContentText("If yes the changes will be reverted.");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
		    return true;
		} else {
		    return false;
		}
	}
	
	private void saveSettings() {
		Settings s = Settings.getInstance();
		s.setConversionMethod(parameterView.getConversionMethod());
		s.setUsingWindowFunction(parameterView.getWindowFunctionSetting());
		s.setWindowSize(parameterView.getWindowSize());
		
		s.save();
	}
}
