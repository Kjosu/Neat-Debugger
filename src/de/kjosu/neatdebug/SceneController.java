package de.kjosu.neatdebug;

import de.kjosu.jnstinct.core.Genome;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class SceneController {

    @FXML
    private ListView<Genome<?>> populationListView;

    @FXML
    private Label populationHeader;

    @FXML
    private Label visualizerHeader;

    @FXML
    private Label mutationsHeader;

    @FXML
    private Label nodeInspectorHeader;

    @FXML
    private Label connectionInspectorHeader;

    @FXML
    public void initialize() {

    }
}
