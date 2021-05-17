package evich;

import javafx.fxml.Initializable;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ExperimentWindowController implements Initializable
{
    public ToggleButton generatorsButton;
    public ToggleButton stagesButton;
    public VBox generatorsBox;
    public VBox stagesBox;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup group = new ToggleGroup();
        group.getToggles().add(generatorsButton);
        group.getToggles().add(stagesButton);
        
        stagesButton.selectedProperty().bindBidirectional(stagesBox.visibleProperty());
        stagesButton.selectedProperty().bindBidirectional(stagesBox.managedProperty());
        generatorsButton.selectedProperty().bindBidirectional(generatorsBox.visibleProperty());
        generatorsButton.selectedProperty().bindBidirectional(generatorsBox.managedProperty());
    }
}
