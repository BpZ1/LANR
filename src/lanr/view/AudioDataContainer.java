package lanr.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalTime;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import lanr.controller.AudioController;
import lanr.logic.model.AudioStream;
import lanr.logic.model.AudioData;
import lanr.logic.model.Noise;

/**
 * Container for an audio file and its channel.
 * 
 * @author Nicolas Bruch
 *
 */
public class AudioDataContainer extends TitledPane {

	private static final String AUDIO_CONTAINER_CSS_ID = "audioContainer";
	private static final String LABEL_CSS_ID = "labelText";
	private static final String PLACEHOLDER_CSS_ID = "placeHolder";
	private static final String DATA_BOX_CSS_ID = "dataBox";
	private static boolean showVisualization = true;
	
	private final AudioController controller;
	private final AudioData data;
	private VBox content;
	private Button analyzeButton;
	private Text placeHolderText;
	private AudioVisualisation audioVisualisation;
	private TitledPane noiseData;
	private Circle statusCircle;
	
	public AudioDataContainer(AudioData data, AudioController controller) {
		this.controller = controller;
		this.data = data;
		this.setId(AUDIO_CONTAINER_CSS_ID);
		this.setText(data.getPath() + " - Not analyzed");
		data.addChangeListener(createDataChangeListener());
		createNodeElements();
		placeHolderText = new Text("In Progress..."); 
		placeHolderText.setId("placeHolderText");
		content.getChildren().add(placeHolderText);
		placeHolderText.setVisible(false);
	}
	
	private void createNodeElements() {
		this.content = createContent();
		this.setContent(content);	
		//Creating the state circle
		this.statusCircle = new Circle(6, Color.WHITE);	
		this.statusCircle.setStroke(Color.BLACK);
		this.setGraphic(statusCircle);
		this.placeHolderText = new Text("In Progress..."); 
		this.placeHolderText.setId(PLACEHOLDER_CSS_ID);
	}

	/**
	 * Creates the info block for the meta data.
	 * @return
	 */
	private VBox createContent() {
		VBox content = new VBox();
		content.setSpacing(4);
		GridPane infoBox = new GridPane();
		infoBox.setId(DATA_BOX_CSS_ID);
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
		Text channelNumberText = new Text(String.valueOf(data.getStreams().size()));	
		
		infoBox.add(channelLabelText, 0, row);
		infoBox.add(channelNumberText, 1, row);	
		row++;
		
		for(AudioStream channel : data.getStreams()) {
			Text durationLabelText = new Text("Duration:");
			durationLabelText.setId(LABEL_CSS_ID);
			Text durationNumberText = new Text(getDurationString(channel.getLength()));
			
			infoBox.add(durationLabelText, 0, row);
			infoBox.add(durationNumberText, 1, row);	
			row++;
		}
		
		Text bitLabelText = new Text("Bit rate:");
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
		content.getChildren().add(createAnalyzeButton());
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
			controller.analyze(data);
		});
		return analyzeButton;
	}
	
	/**
	 * Gets called when the analyzing is finished.
	 * @return
	 */
	private PropertyChangeListener createDataChangeListener() {
		PropertyChangeListener listener = new  PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				
				Platform.runLater(()->{
					if(evt.getPropertyName().equals(AudioData.DATA_ANALYSIS_STARTED)){
						if(showVisualization) {
							//Remove old data if it exists
							if(audioVisualisation != null) {
								content.getChildren().remove(audioVisualisation);								
							}
							analyzeButton.setVisible(false);
							placeHolderText.setVisible(true);
							setText(data.getPath() + " - Is being analyzed...");
							audioVisualisation = new AudioVisualisation(200, 600, data);
							content.getChildren().add(audioVisualisation);
						}
						//Remove old data if it exists
						if(noiseData != null) {
							content.getChildren().remove(noiseData);
						}
						//When the file has finished analyzing the table with the found noise will be added
					}else if(evt.getPropertyName().equals(AudioData.DATA_ANALYZED_PROPERTY)) {
						analyzeButton.setVisible(true);
						placeHolderText.setVisible(false);
						//Add the visualisation
						content.getChildren().remove(placeHolderText);
						//Add the table
						noiseData = new TitledPane();
						noiseData.setText("Found Problems");
						noiseData.setContent(createNoiseTable());
						content.getChildren().add(noiseData);
						statusCircle.setFill(getSeverityColor(data.getSeverity()));
						setText(data.getPath() + " - Severity: " + data.getSeverity());
					}
				});
				
			}			
		};
		return listener;
	}

	private TableView<Noise> createNoiseTable() {
		TableView<Noise> noiseTable = new TableView<Noise>();
		noiseTable.setPlaceholder(new Text("No noise found"));
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
			return new SimpleStringProperty(
					getDurationString(param.getValue().getLocation() / data.getSampleRate()));
		});
		noiseTable.getColumns().add(positionColumn);
		
		// Create the column for the position in which the noise was located
		TableColumn<Noise, String> lengthColumn = new TableColumn<Noise, String>();
		lengthColumn.setText("Length");
		lengthColumn.setCellValueFactory(param -> {
			return new SimpleStringProperty(
					getDurationString(param.getValue().getLength() / data.getSampleRate()));
		});
		noiseTable.getColumns().add(lengthColumn);

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
		for(AudioStream c : data.getStreams()) {
			noiseList.addAll(c.getFoundNoise());
		}
		noiseTable.setItems(noiseList);
		return noiseTable;
	}
	
	private Color getSeverityColor(double severity) {
		if(severity < 3) {
			return Color.GREEN;
		}
		if(severity < 6) {
			return Color.YELLOW;
		}
		return Color.RED;
	}
	
	private String getDurationString(long durationInSeconds) {
		LocalTime timeOfDay = LocalTime.ofSecondOfDay(durationInSeconds);
		String time = timeOfDay.toString();
		return time;
	}

	public static boolean isShowVisualization() {
		return showVisualization;
	}

	public static void setShowVisualization(boolean showVisualization) {
		AudioDataContainer.showVisualization = showVisualization;
	}

}
