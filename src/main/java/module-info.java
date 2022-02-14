module evich {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    
    
    opens evich to javafx.fxml;
    exports evich;
    opens evich.controllers to javafx.fxml;
    exports evich.controllers;
}