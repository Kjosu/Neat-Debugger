package de.kjosu.neatdebug;

import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;
import de.kjosu.jnstinct.util.GenomeLoader;
import de.kjosu.neatdebug.util.FontAwesome;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class SceneController<T extends Genome<T>> {

    @FXML
    private ListView<T> populationListView;

    @FXML
    private Label populationHeader;

    @FXML
    private Button populationAddButton;

    @FXML
    private Button populationSubButton;

    @FXML
    private Button crossoverButton;

    @FXML
    private Button populationLoadButton;

    @FXML
    private Button populationSaveButton;

    @FXML
    private Label visualizerHeader;

    @FXML
    private Label mutationsHeader;

    @FXML
    private Label nodeInspectorHeader;

    @FXML
    private Label connectionInspectorHeader;

    private Stage stage;

    private Neat<T> neat;

    private final FileChooser populationFileChooser = new FileChooser();

    public void setup(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        try {
            FontAwesome.init();
        } catch (IOException e) {
            e.printStackTrace();
        }

        populationFileChooser.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("GenomeFile", "*.genome")
        );

        populationListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        populationAddButton.setText(FontAwesome.Plus);
        populationSubButton.setText(FontAwesome.Minus);
        crossoverButton.setText(FontAwesome.Dna);
        populationLoadButton.setText(FontAwesome.File);
        populationSaveButton.setText(FontAwesome.Save);
    }

    private void initializeNeat() {
        if (neat == null) {
            populationListView.getItems().clear();
            populationHeader.setText("Population");
        } else {
            populationHeader.setText(String.format("Population (%s/%s)", neat.getPopulation().size(), neat.getPopulationSize()));
            populationListView.getItems().setAll(neat.getPopulation());
        }
    }

    @FXML
    private void populationAddAction() {
        if (neat == null) {
            return;
        }

        if (neat.getPopulation().size() < neat.getPopulationSize()) {
            T genome = neat.createGenome(neat, neat.getInputSize(), neat.getOutputSize(), true);
            neat.getPopulation().add(genome);

            initializeNeat();
        }
    }

    @FXML
    private void populationSubAction() {
        if (neat == null) {
            return;
        }

        ObservableList<T> selected = populationListView.getSelectionModel().getSelectedItems();

        if (selected == null || selected.isEmpty()) {
            return;
        }

        for (T genome : selected) {
            neat.getPopulation().remove(genome);
        }

        initializeNeat();
    }

    @FXML
    private void crossoverAction() {
        if (neat == null) {
            return;
        }

        ObservableList<T> selected = populationListView.getSelectionModel().getSelectedItems();

        if (selected.size() != 2) {
            return;
        }

        T genome = neat.createGenome(neat, selected.get(0), selected.get(1), false);
        neat.getPopulation().add(genome);

        initializeNeat();

        populationListView.getSelectionModel().select(genome);
    }

    @FXML
    private void populationLoadAction() {
        if (neat == null) {
            return;
        }

        if (neat.getPopulation().size() >= neat.getPopulationSize()) {
            return;
        }

        populationFileChooser.setTitle("Load Genome");
        File file = populationFileChooser.showOpenDialog(stage);

        if (file == null) {
            return;
        }

        try {
            T genome = GenomeLoader.load(neat, file);

            if (genome == null) {
                return;
            }

            neat.getPopulation().add(genome);
            initializeNeat();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void populationSaveAction() {
        if (neat == null) {
            return;
        }

        Genome genome = populationListView.getSelectionModel().getSelectedItem();

        if (genome == null) {
            return;
        }

        populationFileChooser.setTitle("Save Genome");
        File file = populationFileChooser.showSaveDialog(stage);

        if (file == null) {
            return;
        }

        try {
            GenomeLoader.save(genome, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Neat<T> getNeat() {
        return neat;
    }

    public void setNeat(Neat<T> neat) {
        this.neat = neat;

        initializeNeat();
    }
}
