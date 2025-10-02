module incuat.kg.svetoofor {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.net.http;
    requires org.java_websocket;

    opens incuat.kg.svetoofor to javafx.fxml;
    exports incuat.kg.svetoofor;
}