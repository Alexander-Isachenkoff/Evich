package evich.components;

import evich.model.MeanVar;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MeanVarTable extends TableView<MeanVar>
{
    public MeanVarTable() {
        TableColumn<MeanVar, String> generatorColumn = new TableColumn<>("Генератор");
        TableColumn<MeanVar, Number> beforeColumn = new TableColumn<>("Истинных рядов");
        TableColumn<MeanVar, Number> afterColumn = new TableColumn<>("После фильтрации");
        TableColumn<MeanVar, Number> beforeMColumn = new TableColumn<>("M");
        TableColumn<MeanVar, Number> beforeDColumn = new TableColumn<>("D");
        TableColumn<MeanVar, Number> afterMColumn = new TableColumn<>("M");
        TableColumn<MeanVar, Number> afterDColumn = new TableColumn<>("D");
        
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
