package evich.components;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AmplsMomentsTable extends TableView<Integer>
{
    public void setData(double[][] moments, double[][] ampls) {
        getColumns().clear();
        
        for (int i = 0; i < moments.length; i++) {
            TableColumn<Integer, Number> generatorColumn = new TableColumn<>("Генератор " + (i + 1));
            TableColumn<Integer, Number> mColumn = new TableColumn<>("M");
            TableColumn<Integer, Number> aColumn = new TableColumn<>("A");
            
            final int mk = i;
            mColumn.setCellValueFactory(data -> {
                double value = moments[mk][getItems().indexOf(data.getValue())];
                return new SimpleIntegerProperty((int) value);
            });
            
            final int ak = i;
            aColumn.setCellValueFactory(data -> {
                double value = ampls[ak][getItems().indexOf(data.getValue())];
                return new SimpleDoubleProperty(Math.round(value * 100) / 100.0);
            });
            
            generatorColumn.getColumns().add(mColumn);
            generatorColumn.getColumns().add(aColumn);
            getColumns().add(generatorColumn);
        }
        
        getItems().clear();
        if (moments.length > 0) {
            for (int i = 0; i < moments[0].length; i++) {
                getItems().add(i);
            }
        }
    }
}
