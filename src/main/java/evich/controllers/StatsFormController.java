package evich.controllers;

import evich.Procession;
import evich.components.AmplsMomentsTable;
import evich.components.ErrorsTable;
import evich.components.MeanVarTable;
import evich.model.Experiment;
import evich.model.LinearCoefficients;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StatsFormController implements Initializable
{
    private final MeanVarTable meanVarTable = new MeanVarTable();
    private final AmplsMomentsTable amplsMomentsTable = new AmplsMomentsTable();
    private final ErrorsTable errorsTable = new ErrorsTable();
    @FXML
    private VBox trendsTab;
    @FXML
    private VBox meanVarTab;
    @FXML
    private VBox momentsTab;
    @FXML
    private VBox errorsTab;
    @FXML
    private TableView<LinearCoefficients> trendsTable;
    @FXML
    private TableColumn<LinearCoefficients, String> generatorColumn;
    @FXML
    private TableColumn<LinearCoefficients, Number> aColumn;
    @FXML
    private TableColumn<LinearCoefficients, Number> bColumn;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        generatorColumn.setCellValueFactory(data -> {
            int index = trendsTable.getItems().indexOf(data.getValue());
            return new SimpleStringProperty("Генератор " + (index + 1));
        });
        
        aColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().a));
        bColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().b));
        
        //trendsTab.getChildren().add(trendsTable);
        meanVarTab.getChildren().add(meanVarTable);
        momentsTab.getChildren().add(amplsMomentsTable);
        errorsTab.getChildren().add(errorsTable);
    }
    
    public void setData(Experiment experiment) {
        updateTrendsTable(experiment);
        updateMeanVarTable(experiment);
        updateMomentsTable(experiment);
        updateErrorsTable(experiment);
    }
    
    private void updateTrendsTable(Experiment experiment) {
        trendsTable.getItems().clear();
        if (experiment == null) {
            return;
        }
        trendsTable.setItems(FXCollections.observableArrayList(experiment.getLinearCoefficients()));
    }
    
    private void updateMeanVarTable(Experiment experiment) {
        meanVarTable.getItems().clear();
        if (experiment == null) {
            return;
        }
        meanVarTable.setItems(FXCollections.observableArrayList(experiment.getMeanVars()));
    }
    
    private void updateMomentsTable(Experiment experiment) {
        amplsMomentsTable.getItems().clear();
        if (experiment == null) {
            return;
        }
        amplsMomentsTable.setData(experiment.getMoments(), experiment.getAmpls());
    }
    
    private void updateErrorsTable(Experiment experiment) {
        errorsTable.getItems().clear();
        if (experiment == null) {
            return;
        }
        double[] vars = new double[0];
        double[] means = new double[0];
        try {
            vars = Procession.vars(experiment.getAbsError());
            means = Procession.means(experiment.getAbsError());
        } catch (IOException e) {
            e.printStackTrace();
        }
        errorsTable.setData(means, vars);
    }
}
