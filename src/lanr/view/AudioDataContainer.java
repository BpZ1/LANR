package lanr.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalTime;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lanr.controller.AudioController;
import lanr.logic.model.AudioChannel;
import lanr.logic.model.AudioData;
import lanr.logic.model.Noise;

public class AudioDataContainer extends TitledPane {

	private static final String AUDIO_CONTAINER_CSS_ID = "audioContainer";
	private static final String LABEL_CSS_ID = "labelText";
	
	private final AudioController controller;
	private final AudioData data;
	private VBox content;
	private Button analyzeButton;
	private Text placeHolderText;
	private ScrollPane visualisationContainer;
	
	public AudioDataContainer(AudioData data, AudioController controller) {
		this.controller = controller;
		this.data = data;
		this.setId(AUDIO_CONTAINER_CSS_ID);
		this.setText(data.getPath());
		data.addChangeListener(createChangeListener());
		GridPane pane = new GridPane();
		this.content = createContent();
		pane.getChildren().add(content);
		this.setContent(pane);
		this.visualisationContainer = createAudioVisual();
	}

	/**
	 * Creates the info block for the meta data.
	 * @return
	 */
	private VBox createContent() {
		VBox content = new VBox();
		GridPane infoBox = new GridPane();
		infoBox.setPadding(new Insets(2,2,2,2));
		infoBox.setVgap(4);
		infoBox.setHgap(10);

		int row = 0;
		//Set the file information
		Text nameLabelText = new Text("Name:");
		nameLabelText.setId(LABEL_CSS_ID);
		Text nameText = new Text(data.getName());
		
		infoBox.add(nameLabelText, 0, row);
		infoBox.add(nameText, 1, row);
		row++;
		
		Text pathLabelText = new Text("Path:");
		pathLabelText.setId(LABEL_CSS_ID);
		Text pathText = new Text(data.getPath());
		
		infoBox.add(pathLabelText, 0, row);
		infoBox.add(pathText, 1, row);			
		row++;
		
		Text channelLabelText = new Text("Channel:");
		channelLabelText.setId(LABEL_CSS_ID);
		Text channelNumberText = new Text(String.valueOf(data.getAllChannel().size()));	
		
		infoBox.add(channelLabelText, 0, row);
		infoBox.add(channelNumberText, 1, row);	
		row++;
		
		for(AudioChannel channel : data.getAllChannel()) {
			Text durationLabelText = new Text("Duration:");
			durationLabelText.setId(LABEL_CSS_ID);
			Text durationNumberText = new Text(getDurationString(channel.getLength()));
			
			infoBox.add(durationLabelText, 0, row);
			infoBox.add(durationNumberText, 1, row);	
			row++;
		}
		
		Text bitLabelText = new Text("Bit depth:");
		bitLabelText.setId(LABEL_CSS_ID);
		Text bitDepthText = new Text(String.valueOf(data.getBitDepth()));
	
		infoBox.add(bitLabelText, 0, row);
		infoBox.add(bitDepthText, 1, row);	
		row++;
		
		Text sampleLabelText = new Text("Sample rate:");
		sampleLabelText.setId(LABEL_CSS_ID);
		Text sampleRateText = new Text(String.valueOf(data.getSampleRate()));
		
		infoBox.add(sampleLabelText, 0, row);
		infoBox.add(sampleRateText, 1, row);	
		row++;

		content.getChildren().add(infoBox);
		if(data.isAnalyzed()) {
			content.getChildren().add(visualisationContainer);			
		}else {
			content.getChildren().add(createAnalyzeButton());
		}
		
		return content;
	}
	
	/**
	 * Creates the button for analyzing the data.
	 * @return
	 */
	private Button createAnalyzeButton() {
		analyzeButton = new Button();
		analyzeButton.setText("Analyze");
		analyzeButton.setOnAction(event ->{
			content.getChildren().remove(analyzeButton);
			placeHolderText = new Text("In Progress..."); 
			placeHolderText.setId("placeHolderText");
			content.getChildren().add(placeHolderText);
			controller.analyze(data);
		});
		return analyzeButton;
	}
	
	private PropertyChangeListener createChangeListener() {
		PropertyChangeListener listener = new  PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				//When the file has finished analyzing the table with the found noise will be added
				Platform.runLater(()->{
					//Add the visualisation
					content.getChildren().remove(placeHolderText);
					content.getChildren().add(visualisationContainer);
					//Add the table
					TitledPane noiseData = new TitledPane();
					noiseData.setText("Found Problems");
					TableView<Noise> noiseTable = createNoiseTable();
					noiseData.setContent(noiseTable);
					content.getChildren().add(noiseData);
				});
				
			}			
		};
		return listener;
	}

	private ScrollPane createAudioVisual() {
		ScrollPane pane = new ScrollPane();
		AudioVisualisation canvasContainer = new AudioVisualisation(600, 200, data);
		pane.setContent(canvasContainer);
		return pane;
	}

	private TableView<Noise> createNoiseTable() {
		TableView<Noise> noiseTable = new TableView<Noise>();
		noiseTable.setPrefWidth(Region.USE_COMPUTED_SIZE);

		// Create the column for the type of noise
		TableColumn<Noise, String> typeColumn = new TableColumn<Noise, String>();
		typeColumn.setText("Type");
		typeColumn.setCellValueFactory(param -> {
			return new SimpleStringProperty(param.getValue().getType().toString());
		});
		noiseTable.getColumns().add(typeColumn);

		// Create the column for the position in which the noise was located
		TableColumn<Noise, String> positionColumn = new TableColumn<Noise, String>();
		positionColumn.setText("Position");
		positionColumn.setCellValueFactory(param -> {
			return new SimpleStringProperty(String.valueOf(param.getValue().getLocation()));
		});
		noiseTable.getColumns().add(positionColumn);

		// Create the column for the severity of the noise
		TableColumn<Noise, String> severityColumn = new TableColumn<Noise, String>();
		severityColumn.setText("Severity");
		severityColumn.setCellValueFactory(param -> {
			return new SimpleStringProperty(String.valueOf(param.getValue().getSeverity()));
		});
		noiseTable.getColumns().add(severityColumn);
		
		TableColumn<Noise, String> channelColumn = new TableColumn<Noise, String>();
		channelColumn.setText("Channel");
		channelColumn.setCellValueFactory(param -> {
			return new SimpleStringProperty(String.valueOf(param.getValue().getChannel()));
		});
		noiseTable.getColumns().add(channelColumn);

		noiseTable.setEditable(false);

		ObservableList<Noise> noiseList = FXCollections.observableArrayList();
		for(AudioChannel c : data.getAllChannel()) {
			noiseList.addAll(c.getFoundNoise());
		}
		noiseTable.setItems(noiseList);
		return noiseTable;
	}
	
	private String getDurationString(long durationInSeconds) {
		LocalTime timeOfDay = LocalTime.ofSecondOfDay(durationInSeconds);
		String time = timeOfDay.toString();
		return time;
	}

}
