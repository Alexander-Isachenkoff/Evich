package evich.components;

import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TextFieldGrid<T> extends GridPane
{
    private final List<List<TextField>> textFields = new ArrayList<>();
    private final Function<String, T> converter;
    private int rowsCount;
    private int columnsCount;
    
    public TextFieldGrid(Function<String, T> converter) {
        setHgap(5);
        setVgap(5);
        this.converter = converter;
    }
    
    public int getRowsCount() {
        return rowsCount;
    }
    
    public void setRowsCount(int n) {
        while (textFields.size() > n) {
            textFields.remove(n);
        }
        while (textFields.size() < n) {
            List<TextField> newList = new ArrayList<>();
            for (int i = 0; i < getColumnsCount(); i++) {
                newList.add(new TextField());
            }
            textFields.add(newList);
        }
        rowsCount = n;
        updateGrid();
    }
    
    public int getColumnsCount() {
        return columnsCount;
    }
    
    public void setColumnsCount(int n) {
        if (getColumnsCount() > n) {
            for (int i = 0; i < textFields.size(); i++) {
                textFields.set(i, textFields.get(i).subList(0, n));
            }
        }
        if (getColumnsCount() < n) {
            for (List<TextField> list : textFields) {
                while (list.size() < n) {
                    list.add(new TextField());
                }
            }
        }
        
        columnsCount = n;
        updateGrid();
    }
    
    private void updateGrid() {
        getChildren().clear();
        for (int i = 0; i < textFields.size(); i++) {
            for (int j = 0; j < textFields.get(i).size(); j++) {
                add(textFields.get(i).get(j), j, i);
            }
        }
    }
    
    public List<List<T>> getInfoAsList() {
        List<List<T>> info = new ArrayList<>();
        for (int i = 0; i < getRowsCount(); i++) {
            List<T> row = new ArrayList<>();
            info.add(row);
            for (int j = 0; j < getColumnsCount(); j++) {
                row.add(converter.apply(textFields.get(i).get(j).getText()));
            }
        }
        return info;
    }
    
    public void incRows() {
        setRowsCount(getRowsCount() + 1);
    }
    
    public void decRows() {
        if (getRowsCount() == 0) return;
        setRowsCount(getRowsCount() - 1);
    }
}
