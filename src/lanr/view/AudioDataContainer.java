package lanr.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lanr.logic.model.AudioData;
import lanr.logic.model.Noise;

public class AudioDataContainer extends TitledPane {

	public AudioDataContainer(AudioData data) {
		this.setText(data.getPath());
		GridPane pane = new GridPane();
		pane.getChildren().add(createContent(data));
		this.setContent(pane);
	}

	private VBox createContent(AudioData data) {
		VBox content = new VBox();
		
		//Set the file information
		Text nameText = new Text();
		nameText.setText(new StringBuilder()
				.append("Name:")
				.append("\t")
				.append(data.getName())
				.toString());
		
		content.getChildren().add(nameText);
		
		Text pathText = new Text();
		pathText.setText(new StringBuilder()
				.append("Path:")
				.append("\t")
				.append(data.getPath())
				.toString());
		
		content.getChildren().add(pathText);
		
		Text bitDepthText = new Text();
		bitDepthText.setText(new StringBuilder()
				.append("Bit depth:")
				.append("\t")
				.append(data.getBitDepth())
				.toString());
		
		content.getChildren().add(bitDepthText);
		
		Text sampleRateText = new Text();
		sampleRateText.setText(new StringBuilder()
				.append("Sample rate:")
				.append("\t")
				.append(data.getSampleRate())
				.toString());
		
		content.getChildren().add(sampleRateText);

		if (data.isAnalyzed()) {
			content.getChildren().add(createAudioVisual(data));
			TitledPane noiseData = new TitledPane();
			noiseData.setText("Found Problems");
			TableView<Noise> noiseTable = createNoiseTable();
			noiseData.setContent(noiseTable);
			content.getChildren().add(noiseData);
		}
		return content;
	}

	private ScrollPane createAudioVisual(AudioData data) {
		ScrollPane pane = new ScrollPane();
		pane.setContent(new AudioVisualisation(data));
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

		noiseTable.setEditable(false);

		return noiseTable;
	}

}
