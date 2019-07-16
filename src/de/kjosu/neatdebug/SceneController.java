package de.kjosu.neatdebug;

import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;
import de.kjosu.jnstinct.core.NodeGene;
import de.kjosu.jnstinct.util.GenomeLoader;
import de.kjosu.jnstinct.util.MapUtils;
import de.kjosu.neatdebug.components.ConnectionInspector;
import de.kjosu.neatdebug.components.GenomeVisualizer;
import de.kjosu.neatdebug.components.NodeInspector;
import de.kjosu.neatdebug.util.FontAwesome;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
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
    private GenomeVisualizer genomeVisualizer;

    @FXML
    private Label mutationsHeader;

    @FXML
    private Label nodeInspectorHeader;

    @FXML
    private NodeInspector nodeInspector;

    @FXML
    private Label connectionInspectorHeader;

    @FXML
    private ConnectionInspector connectionInspector;

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

        genomeVisualizer.setNodeInspector(nodeInspector);
        genomeVisualizer.setConnectionInspector(connectionInspector);

        nodeInspector.setConnectionInspector(connectionInspector);
        connectionInspector.setNodeInspector(nodeInspector);

        genomeVisualizer.start();
    }

    public void updatePopulationList() {
        String headerText;

        if (neat == null) {
            populationListView.getItems().clear();
            headerText = "Population";
        } else {
            populationListView.getItems().setAll(neat.getPopulation());
            headerText = String.format("Population (%s/%s)", populationListView.getItems().size(), neat.getPopulationSize());
        }

        populationHeader.setText(headerText);
    }

    @FXML
    private void onPopulationListPressed(MouseEvent e) {
        if (e.getClickCount() != 2) {
            return;
        }

        Genome<?> genome = populationListView.getSelectionModel().getSelectedItem();
        genomeVisualizer.setGenome(genome);
    }

    @FXML
    private void populationAddAction() {
        if (neat == null) {
            return;
        }

        if (neat.getPopulation().size() >= neat.getPopulationSize()) {
            return;
        }

        T genome = neat.createGenome(neat, neat.getInputSize(), neat.getOutputSize(), true);
        neat.getPopulation().add(genome);

        updatePopulationList();
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

        updatePopulationList();
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

        updatePopulationList();

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
            updatePopulationList();
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

        genomeVisualizer.setGenome(null);

        updatePopulationList();

        nodeInspector.update();
        connectionInspector.update();
    }

    public void dispose() {
        genomeVisualizer.stop();
    }
}
