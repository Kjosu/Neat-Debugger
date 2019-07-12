package de.kjosu.neatdebug.components;

import de.kjosu.jnstinct.activation.Squash;
import de.kjosu.jnstinct.core.ConnectionGene;
import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;
import de.kjosu.jnstinct.core.NodeGene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class NodeInspector extends AnchorPane {

    private GridPane contentPane = new GridPane();

    private Label idLabel = new Label("Identifier");
    private TextField idField = new TextField();

    private Label typeLabel = new Label("Node type");
    private TextField typeField = new TextField();

    private Label squashLabel = new Label("Squash");
    private ComboBox<Squash> squashBox = new ComboBox<>();

    private Label biasLabel = new Label("Bias");
    private TextField biasField = new TextField();

    private Label activationLabel = new Label("Activation");
    private TextField activationField = new TextField();

    private Label stateLabel = new Label("State");
    private TextField stateField = new TextField();

    private Label oldStateLabel = new Label("Old state");
    private TextField oldStateField = new TextField();

    private Label incomingLabel = new Label("Incoming connections");
    private ListView<ConnectionGene> incomingList = new ListView<>();

    private Label outgoingLabel = new Label("Outgoing connections");
    private ListView<ConnectionGene> outgoingList = new ListView<>();

    private Label selfLabel = new Label("Self connection");
    private Button selfButton = new Button("Inspect");

    private ConnectionInspector connectionInspector;

    private Genome<?> genome;
    private NodeGene node;

    public NodeInspector() {
        initComponents();
    }

    public NodeInspector(ConnectionInspector connectionInspector) {
        this.connectionInspector = connectionInspector;

        initComponents();
    }

    private void initComponents() {
        contentPane.getStyleClass().add("content-pane");

        idLabel.getStyleClass().add("id-label");
        idField.getStyleClass().add("id-field");
        idField.setEditable(false);

        typeLabel.getStyleClass().add("type-label");
        typeField.getStyleClass().add("type-field");
        typeField.setEditable(false);

        squashLabel.getStyleClass().add("squash-label");
        squashBox.getStyleClass().add("squash-box");
        squashBox.setOnAction(event -> squashAction());

        biasLabel.getStyleClass().add("bias-label");
        biasField.getStyleClass().add("bias-field");
        biasField.setOnAction(event -> biasAction());

        activationLabel.getStyleClass().add("activation-label");
        activationField.getStyleClass().add("activation-field");
        activationField.setOnAction(event -> activationAction());

        stateLabel.getStyleClass().add("state-label");
        stateField.getStyleClass().add("state-field");
        stateField.setOnAction(event -> stateAction());

        oldStateLabel.getStyleClass().add("old-state-label");
        oldStateField.getStyleClass().add("old-state-field");
        oldStateField.setOnAction(event -> oldStateAction());

        incomingLabel.getStyleClass().add("incoming-connections-label");
        incomingList.getStyleClass().add("incoming-connections-list");
        incomingList.setOnMousePressed(event -> {
            if (event.getClickCount() == 2) {
                incomingConnectionAction();
            }
        });

        outgoingLabel.getStyleClass().add("outgoing-connections-label");
        outgoingList.getStyleClass().add("outgoing-connections-list");
        outgoingList.setOnMousePressed(event -> {
            if (event.getClickCount() == 2) {
                outgoingConnectionAction();
            }
        });

        selfLabel.getStyleClass().add("self-connection-label");
        selfButton.getStyleClass().add("self-connection-button");
        selfButton.setOnAction(event -> selfConnectionAction());

        contentPane.add(idLabel, 0, 0);
        contentPane.add(idField, 1, 0);
        contentPane.add(typeLabel, 0, 1);
        contentPane.add(typeField, 1, 1);
        contentPane.add(squashLabel, 0, 2);
        contentPane.add(squashBox, 1, 2);
        contentPane.add(biasLabel, 0, 3);
        contentPane.add(biasField, 1, 3);
        contentPane.add(activationLabel, 0, 4);
        contentPane.add(activationField, 1, 4);
        contentPane.add(stateLabel, 0, 5);
        contentPane.add(stateField, 1, 5);
        contentPane.add(oldStateLabel, 0, 6);
        contentPane.add(oldStateField, 1, 6);
        contentPane.add(incomingLabel, 0, 7, 2, 0);
        contentPane.add(incomingList, 0, 8, 2, 0);
        contentPane.add(outgoingLabel, 0, 9, 2, 0);
        contentPane.add(outgoingList, 0, 10, 2, 0);
        contentPane.add(selfLabel, 0, 11, 2, 0);
        contentPane.add(selfButton, 0, 12, 2, 0);

        getChildren().add(contentPane);
    }

    public void setConnectionInspector(ConnectionInspector connectionInspector) {
        this.connectionInspector = connectionInspector;
    }

    public ConnectionInspector getConnectionInspector() {
        return connectionInspector;
    }

    public void set(Genome<?> genome, NodeGene node) {
        this.node = node;

        incomingList.getItems().clear();
        outgoingList.getItems().clear();

        if (node == null) {
            idField.clear();
            typeField.clear();
            squashBox.getItems().clear();
            squashBox.getSelectionModel().clearSelection();
            biasField.clear();
            activationField.clear();
            stateField.clear();
            oldStateField.clear();
            selfButton.setDisable(true);
        } else {
            idField.setText(String.valueOf(node.getId()));
            typeField.setText(node.getType().name());
            squashBox.getItems().addAll(Squash.values());
            squashBox.getSelectionModel().select(node.getSquash());
            biasField.setText(String.valueOf(node.getBias()));
            activationField.setText(String.valueOf(node.getActivation()));
            stateField.setText(String.valueOf(node.getState()));
            oldStateField.setText(String.valueOf(node.getOld()));

            for (int id : node.getIncoming()) {
                incomingList.getItems().add(genome.getConnection(id));
            }

            for (int id : node.getOutgoing()) {
                outgoingList.getItems().add(genome.getConnection(id));
            }

            selfButton.setDisable(false);
        }
    }

    public NodeGene getNode() {
        return node;
    }

    private void squashAction() {
        node.setSquash(squashBox.getValue());
    }

    private void biasAction() {
        // TODO change to numeric text fields
    }

    private void activationAction() {

    }

    private void stateAction() {

    }

    private void oldStateAction() {

    }

    private void incomingConnectionAction() {

    }

    private void outgoingConnectionAction() {

    }

    private void selfConnectionAction() {

    }
}
