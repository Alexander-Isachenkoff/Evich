package evich;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MeanVarTable extends TableView<MeanVar>
{
    private final TableColumn<MeanVar, String> generatorColumn = new TableColumn<>("Генератор");
    private final TableColumn<MeanVar, Number> beforeColumn = new TableColumn<>("До наложения скачков");
    private final TableColumn<MeanVar, Number> afterColumn = new TableColumn<>("После фильтрации");
    private final TableColumn<MeanVar, Number> beforeMColumn = new TableColumn<>("M");
    private final TableColumn<MeanVar, Number> beforeDColumn = new TableColumn<>("D");
    private final TableColumn<MeanVar, Number> afterMColumn = new TableColumn<>("M");
    private final TableColumn<MeanVar, Number> afterDColumn = new TableColumn<>("D");
    
    MeanVarTable() {
        getColumns().add(generatorColumn);
        
        getColumns().add(beforeColumn);
        getColumns().add(afterColumn);
        
        beforeColumn.getColumns().add(beforeMColumn);
        beforeColumn.getColumns().add(beforeDColumn);
        
        afterColumn.getColumns().add(afterMColumn);
        afterColumn.getColumns().add(afterDColumn);
        
        generatorColumn.setCellValueFactory(data -> {
            int index = getItems().indexOf(data.getValue());
            return new SimpleStringProperty("Генератор " + (index + 1));
        });
        beforeMColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().beforeM));
        beforeDColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().beforeD));
        afterMColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().afterM));
        afterDColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().afterD));
    }
}
