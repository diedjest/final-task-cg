module ru.vsu.cs.finaltaskcg {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;

    opens ru.vsu.cs.finaltaskcg to javafx.fxml;
    exports ru.vsu.cs.finaltaskcg;
}