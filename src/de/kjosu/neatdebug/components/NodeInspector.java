package de.kjosu.neatdebug.components;

import de.kjosu.jnstinct.activation.Squash;
import de.kjosu.jnstinct.core.ConnectionGene;
import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.NodeGene;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class NodeInspector extends GridPane {

    private Label idLabel = new Label("Identifier");
    private TextField idField = new TextField();

    private Label typeLabel = new Label("Node type");
    private TextField typeField = new TextField();

    private Label squashLabel = new Label("Squash");
    private ComboBox<Squash> squashBox = new ComboBox<>();

    private Label biasLabel = new Label("Bias");
    private NumericTextField biasField = new NumericTextField();

    private Label activationLabel = new Label("Activation");
    private NumericTextField activationField = new NumericTextField();

    private Label stateLabel = new Label("State");
    private NumericTextField stateField = new NumericTextField();

    private Label oldStateLabel = new Label("Old state");
    private NumericTextField oldStateField = new NumericTextField();

    private Label incomingLabel = new Label("Incoming connections");
    private ListView<ConnectionGene> incomingList = new ListView<>();

    private Label outgoingLabel = new Label("Outgoing connections");
    private ListView<ConnectionGene> outgoingList = new ListView<>();

    private Label selfLabel = new Label("Self connection");
    private Hyperlink selfLink = new Hyperlink();

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
        getStyleClass().add("node-inspector");

        setPadding(new Insets(5, 5, 5, 5));
        setHgap(5D);
        setVgap(5D);

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
        incomingList.setMaxHeight(100D);
        incomingList.setOnMousePressed(event -> {
            if (event.getClickCount() == 2) {
                incomingConnectionAction();
            }
        });

        outgoingLabel.getStyleClass().add("outgoing-connections-label");
        outgoingList.getStyleClass().add("outgoing-connections-list");
        outgoingList.setMaxHeight(100D);
        outgoingList.setOnMousePressed(event -> {
            if (event.getClickCount() == 2) {
                outgoingConnectionAction();
            }
        });

        selfLabel.getStyleClass().add("self-connection-label");
        selfLink.getStyleClass().add("self-connection-link");
        selfLink.setOnAction(event -> selfConnectionAction());

        add(idLabel, 0, 0);
        add(idField, 1, 0);
        add(typeLabel, 0, 1);
        add(typeField, 1, 1);
        add(squashLabel, 0, 2);
        add(new AnchorPane(squashBox), 1, 2);
        add(biasLabel, 0, 3);
        add(biasField, 1, 3);
        add(activationLabel, 0, 4);
        add(activationField, 1, 4);
        add(stateLabel, 0, 5);
        add(stateField, 1, 5);
        add(oldStateLabel, 0, 6);
        add(oldStateField, 1, 6);
        add(new AnchorPane(incomingLabel), 0, 7, 2, 1);
        add(incomingList, 0, 8, 2, 1);
        add(new AnchorPane(outgoingLabel), 0, 9, 2, 1);
        add(outgoingList, 0, 10, 2, 1);
        add(selfLabel, 0, 11, 2, 1);
        add(selfLink, 0, 12, 2, 1);

        AnchorPane.setLeftAnchor(squashBox, 0D);
        AnchorPane.setBottomAnchor(squashBox, 0D);
        AnchorPane.setRightAnchor(squashBox, 0D);
        AnchorPane.setTopAnchor(squashBox, 0D);

        AnchorPane.setLeftAnchor(incomingLabel, 0D);
        AnchorPane.setBottomAnchor(incomingLabel, 0D);
        AnchorPane.setRightAnchor(incomingLabel, 0D);
        AnchorPane.setTopAnchor(incomingLabel, 0D);

        AnchorPane.setLeftAnchor(outgoingLabel, 0D);
        AnchorPane.setBottomAnchor(outgoingLabel, 0D);
        AnchorPane.setRightAnchor(outgoingLabel, 0D);
        AnchorPane.setTopAnchor(outgoingLabel, 0D);

        set(null, null);
    }

    public void set(Genome<?> genome, NodeGene node) {
        this.genome = genome;
        this.node = node;

        update();
    }

    public void update() {
        Platform.runLater(() -> {
            boolean disable = node == null;

            incomingList.getItems().clear();
            outgoingList.getItems().clear();

            idField.setDisable(disable);
            typeField.setDisable(disable);
            squashBox.setDisable(disable);
            biasField.setDisable(disable);
            activationField.setDisable(disable);
            stateField.setDisable(disable);
            oldStateField.setDisable(disable);
            incomingList.setDisable(disable);
            outgoingList.setDisable(disable);
            selfLink.setDisable(true);
            selfLink.setText("undefined");

            if (disable) {
                idField.clear();
                typeField.clear();
                squashBox.getItems().clear();
                squashBox.getSelectionModel().clearSelection();
                biasField.clear();
                activationField.clear();
                stateField.clear();
                oldStateField.clear();
            } else {
                idField.setText(String.valueOf(node.getId()));
                typeField.setText(node.getType().name());
                squashBox.getItems().setAll(Squash.values());
                squashBox.getSelectionModel().select(node.getSquash());
                biasField.setText(String.valueOf(node.getBias()));
                activationField.setText(String.valueOf(node.getActivation()));
                stateField.setText(String.valueOf(node.getState()));
                oldStateField.setText(String.valueOf(node.getOld()));

                if (genome != null) {
                    for (int id : node.getIncoming()) {
                        incomingList.getItems().add(genome.getConnection(id));
                    }

                    for (int id : node.getOutgoing()) {
                        outgoingList.getItems().add(genome.getConnection(id));
                    }

                    if (node.getSelf() != -1) {
                        selfLink.setDisable(false);
                        selfLink.setText(String.format("Connection %s", node.getSelf()));
                    }
                }
            }
        });
    }

    private void squashAction() {
        node.setSquash(squashBox.getValue());
    }

    private void biasAction() {
        node.setBias(biasField.getValue());
    }

    private void activationAction() {
        node.setActivation(activationField.getValue());
    }

    private void stateAction() {
        node.setState(stateField.getValue());
    }

    private void oldStateAction() {
        node.setOld(oldStateField.getValue());
    }

    private void incomingConnectionAction() {
        ConnectionGene connection = incomingList.getSelectionModel().getSelectedItem();

        if (connection == null || connectionInspector == null) {
            return;
        }

        connectionInspector.set(genome, connection);
    }

    private void outgoingConnectionAction() {
        ConnectionGene connection = outgoingList.getSelectionModel().getSelectedItem();

        if (connection == null || connectionInspector == null) {
            return;
        }

        connectionInspector.set(genome, connection);
    }

    private void selfConnectionAction() {
        if (node == null || node.getSelf() == -1 || connectionInspector == null) {
            return;
        }

        ConnectionGene connection = genome.getSelf(node.getSelf());
        connectionInspector.set(genome, connection);
    }

    public void setConnectionInspector(ConnectionInspector connectionInspector) {
        this.connectionInspector = connectionInspector;
    }

    public ConnectionInspector getConnectionInspector() {
        return connectionInspector;
    }

    public NodeGene getNode() {
        return node;
    }
}
