package lanr.view;

import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabDragPolicy;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class SettingsView extends Stage {
	
	public SettingsView() {
		TabPane root = createRootPane();
		this.setTitle("Settings");
		Scene scene = new Scene(root, 450, 450);
		this.setScene(scene);
		scene.getStylesheets().add(MainView.class.getResource("Main.css").toExternalForm());
	}
	
	private TabPane createRootPane() {
		TabPane pane = new TabPane();
		pane.setSide(Side.LEFT);
		pane.setTabDragPolicy(TabDragPolicy.FIXED);
		Tab performanceTab = new Tab("Performance");
		performanceTab.setClosable(false);
		performanceTab.setContent(createPerformanceView());
		Tab parametersTab = new Tab("Parameter");
		parametersTab.setClosable(false);
		performanceTab.setContent(createParamterView());
		
		pane.getTabs().add(performanceTab);
		pane.getTabs().add(parametersTab);
		return pane;
	}
	
	private GridPane createPerformanceView() {
		GridPane pane = new GridPane();
		return pane;
	}
	
	private GridPane createParamterView() {
		GridPane pane = new ParameterSettingsView();
		return pane;
	}
}
