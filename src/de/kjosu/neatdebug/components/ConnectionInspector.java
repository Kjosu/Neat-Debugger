package de.kjosu.neatdebug.components;

import de.kjosu.jnstinct.core.ConnectionGene;
import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.NodeGene;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class ConnectionInspector extends GridPane {

    private Label idLabel = new Label("Identifier");
    private TextField idField = new TextField();

    private Label fromNodeLabel = new Label("From");
    private Hyperlink fromNodeLink = new Hyperlink();

    private Label toNodeLabel = new Label("To");
    private Hyperlink toNodeLink = new Hyperlink();

    private Label gainLabel = new Label("Gain");
    private NumericTextField gainField = new NumericTextField();

    private Label weightLabel = new Label("Weight");
    private NumericTextField weightField = new NumericTextField();

    private Label enabledLabel = new Label("Enabled");
    private CheckBox enabledBox = new CheckBox();

    private Label gaterLabel = new Label("Gater");
    private Hyperlink gaterLink = new Hyperlink();

    private NodeInspector nodeInspector;

    private Genome<?> genome;
    private ConnectionGene connection;

    public ConnectionInspector() {
        initComponents();
    }

    public ConnectionInspector(NodeInspector nodeInspector) {
        this.nodeInspector = nodeInspector;

        initComponents();
    }

    private void initComponents() {
        getStyleClass().add("connection-inspector");

        setPadding(new Insets(5, 5, 5, 5));
        setHgap(5D);
        setVgap(5D);

        idLabel.getStyleClass().add("id-label");
        idField.getStyleClass().add("id-field");
        idField.setEditable(false);

        fromNodeLabel.getStyleClass().add("from-node-label");
        fromNodeLink.getStyleClass().add("from-node-link");
        fromNodeLink.setOnAction(event -> fromNodeAction());

        toNodeLabel.getStyleClass().add("to-node-label");
        toNodeLink.getStyleClass().add("to-node-link");
        toNodeLink.setOnAction(event -> toNodeAction());

        gainLabel.getStyleClass().add("gain-label");
        gainField.getStyleClass().add("gain-field");
        gainField.setOnAction(event -> gainAction());

        weightLabel.getStyleClass().add("weight-label");
        weightField.getStyleClass().add("weight-field");
        weightField.setOnAction(event -> weightAction());

        enabledLabel.getStyleClass().add("enabled-label");
        enabledBox.getStyleClass().add("enabled-check-box");
        enabledBox.setOnAction(event -> enabledAction());

        gaterLabel.getStyleClass().add("gater-label");
        gaterLink.getStyleClass().add("gater-link");
        gaterLink.setOnAction(event -> gaterAction());

        add(idLabel, 0, 0);
        add(idField, 1, 0);
        add(fromNodeLabel, 0, 1);
        add(fromNodeLink, 1, 1);
        add(toNodeLabel, 0, 2);
        add(toNodeLink, 1, 2);
        add(gainLabel, 0, 3);
        add(gainField, 1, 3);
        add(weightLabel, 0, 4);
        add(weightField, 1, 4);
        add(enabledLabel, 0, 5);
        add(enabledBox, 1, 5);
        add(gaterLabel, 0, 6);
        add(gaterLink, 1, 6);

        set(null, null);
    }

    public void set(Genome<?> genome, ConnectionGene connection) {
        this.genome = genome;
        this.connection = connection;

        update();
    }

    public void update() {
        Platform.runLater(() -> {
            boolean disable = connection == null;

            idField.setDisable(disable);
            fromNodeLink.setDisable(disable);
            toNodeLink.setDisable(disable);
            gainField.setDisable(disable);
            weightField.setDisable(disable);
            enabledBox.setDisable(disable);
            gaterLink.setDisable(true);
            gaterLink.setText("undefined");

            if (disable) {
                idField.clear();
                fromNodeLink.setText("undefined");
                toNodeLink.setText("undefined");
                gainField.clear();
                weightField.clear();
                enabledBox.setSelected(false);
            } else {
                idField.setText(String.valueOf(connection.getId()));
                fromNodeLink.setText(String.format("Node %s", connection.getFromNode()));
                toNodeLink.setText(String.format("Node %s", connection.getToNode()));
                gainField.setText(String.valueOf(connection.getGain()));
                weightField.setText(String.valueOf(connection.getWeight()));
                enabledBox.setSelected(connection.isEnabled());

                if (connection.getGaterNode() != -1 && genome != null) {
                    gaterLink.setDisable(false);
                    gaterLink.setText(String.format("Node %s", connection.getGaterNode()));
                }
            }
        });
    }

    private void fromNodeAction() {
        if (connection == null || genome == null || nodeInspector == null) {
            return;
        }

        NodeGene node = genome.getNode(connection.getFromNode());

        if (node == null) {
            return;
        }

        nodeInspector.set(genome, node);
    }

    private void toNodeAction() {
        if (connection == null || genome == null || nodeInspector == null) {
            return;
        }

        NodeGene node = genome.getNode(connection.getToNode());

        if (node == null) {
            return;
        }

        nodeInspector.set(genome, node);
    }

    private void gainAction() {
        connection.setGain(gainField.getValue());
    }

    private void weightAction() {
        connection.setWeight(weightField.getValue());
    }

    private void enabledAction() {
        connection.setEnabled(enabledBox.isSelected());
    }

    private void gaterAction() {
        if (connection == null || genome == null || nodeInspector == null) {
            return;
        }

        NodeGene node = genome.getNode(connection.getGaterNode());

        if (node == null) {
            return;
        }

        nodeInspector.set(genome, node);
    }

    public void setNodeInspector(NodeInspector nodeInspector) {
        this.nodeInspector = nodeInspector;
    }

    public NodeInspector getNodeInspector() {
        return nodeInspector;
    }

    public ConnectionGene getConnection() {
        return connection;
    }
}
