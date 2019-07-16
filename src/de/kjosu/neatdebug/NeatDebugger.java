package de.kjosu.neatdebug;

import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NeatDebugger<T extends Genome<T>> {

    private final Stage stage;
    private final Scene scene;

    private final SceneController<T> controller;

    private Neat<T> neat;

    public NeatDebugger(Stage stage) throws IOException {
        if (stage == null) {
            throw new IllegalArgumentException("Stage can't be null");
        }

        this.stage = stage;

        ClassLoader classLoader = getClass().getClassLoader();

        FXMLLoader loader = new FXMLLoader(classLoader.getResource("fxml/app.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        scene = new Scene(root);
        scene.getStylesheets().add(classLoader.getResource("css/style.css").toExternalForm());

        stage.setScene(scene);
    }

    public Stage getStage() {
        return stage;
    }

    public Scene getScene() {
        return scene;
    }

    public SceneController<T> getController() {
        return controller;
    }

    public Neat<T> getNeat() {
        return neat;
    }

    public void setNeat(Neat<T> neat) {
        this.neat = neat;
        controller.setNeat(neat);
    }
}
