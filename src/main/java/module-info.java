module com.tvz.java {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires java.sql;


    opens com.tvz.java to javafx.fxml;
    opens com.tvz.java.controllers to javafx.fxml;
    exports com.tvz.java;
    exports com.tvz.java.entities;
    exports com.tvz.java.controllers;
}