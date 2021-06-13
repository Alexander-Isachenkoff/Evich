package evich.components;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ErrorsTable extends TableView<Integer>
{
    private final TableColumn<Integer, Number> rowColumn = new TableColumn<>("№ ряда");
    private final TableColumn<Integer, Number> meanColumn = new TableColumn<>("M");
    private final TableColumn<Integer, Number> varColumn = new TableColumn<>("D");
    
    private double[] means;
    private double[] vars;
    
    public ErrorsTable() {
        getColumns().add(rowColumn);
        getColumns().add(meanColumn);
        getColumns().add(varColumn);
        
        rowColumn.setCellValueFactory(data -> {
            int value = getItems().indexOf(data.getValue()) + 1;
            return new SimpleIntegerProperty(value);
        });
        
        meanColumn.setCellValueFactory(data -> {
            int value = getItems().indexOf(data.getValue());
            return new SimpleDoubleProperty(means[value]);
        });
        
        varColumn.setCellValueFactory(data -> {
            int value = getItems().indexOf(data.getValue());
            return new SimpleDoubleProperty(vars[value]);
        });
    }
    
    public void setData(double[] means, double[] vars) {
        this.means = means;
        this.vars = vars;
        
        getItems().clear();
        for (int i = 0; i < means.length; i++) {
            getItems().add(i);
        }
    }
}
