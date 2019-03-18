package lanr.view.settings;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

/**
 * @author Nicolas Bruch
 *
 */
public class InfoButton extends Button {
	
	private static List<Tooltip> tooltips = new ArrayList<Tooltip>();
	
	private static final double SIZE = 18;
	
	public InfoButton(String message) {
		super("?");
		this.setId("infoButton");
		this.setMinWidth(SIZE);
		this.setMinHeight(SIZE);
		this.setMaxWidth(SIZE);
		this.setMaxHeight(SIZE);
		this.setStyle(
				String.format("-fx-font-size: %dpx;", (int)(0.5 * SIZE))
				+ "-fx-font-weight: bold;"	);
		Tooltip tt = new Tooltip();
		tt.setText(message);
		tooltips.add(tt);
		this.setOnAction(event ->{
			if(tt.isShowing()) {
				hideAllTooltips();
			}else {
				hideAllTooltips();
				tt.show(this,
						this.localToScreen(this.getBoundsInLocal()).getCenterX() + 30,
						this.localToScreen(this.getBoundsInLocal()).getCenterY() - (tt.getHeight() / 2));
			}			
		});		
	}
	
	public static void hideAllTooltips() {
		for(Tooltip tt : tooltips) {
			tt.hide();
		}
	}
}
