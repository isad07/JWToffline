package com.jwtoffline.ui;

import com.jwtoffline.core.ClaimInspector;
import com.jwtoffline.core.DecodedJwt;
import com.jwtoffline.core.JwtException;
import com.jwtoffline.workspace.SavedToken;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Workspace screen: saved tokens backed by the (mock) repository, with search, open, rename and delete. */
class WorkspaceView extends VBox {

    private static final DateTimeFormatter WHEN = DateTimeFormatter.ofPattern("MMM d, HH:mm").withZone(ZoneId.systemDefault());

    private final AppContext ctx;
    private final TableView<SavedToken> table = new TableView<>();
    private final TextField search = new TextField();
    private final Label status = Ui.status();

    WorkspaceView(AppContext ctx) {
        this.ctx = ctx;
        setSpacing(12);
        setPadding(new Insets(28));

        search.setPromptText("Search by label");
        search.textProperty().addListener((o, a, b) -> refresh());

        TableColumn<SavedToken, String> label = new TableColumn<>("Label");
        label.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().label()));
        label.setPrefWidth(260);
        TableColumn<SavedToken, String> alg = new TableColumn<>("Alg");
        alg.setCellValueFactory(c -> new SimpleStringProperty(peek(c.getValue()).split("\\|")[0]));
        alg.setPrefWidth(80);
        TableColumn<SavedToken, String> state = new TableColumn<>("Status");
        state.setCellValueFactory(c -> new SimpleStringProperty(peek(c.getValue()).split("\\|")[1]));
        state.setPrefWidth(140);
        TableColumn<SavedToken, String> saved = new TableColumn<>("Saved");
        saved.setCellValueFactory(c -> new SimpleStringProperty(WHEN.format(c.getValue().savedAt())));
        saved.setPrefWidth(140);
        table.getColumns().add(label);
        table.getColumns().add(alg);
        table.getColumns().add(state);
        table.getColumns().add(saved);
        table.setPlaceholder(new Label("No saved tokens yet. Use \"Save to workspace\" on the Decode screen."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                open();
            }
        });
        VBox.setVgrow(table, Priority.ALWAYS);

        Button open = new Button("Open in decoder");
        open.getStyleClass().add("primary");
        open.setOnAction(e -> open());
        Button rename = new Button("Rename");
        rename.setOnAction(e -> rename());
        Button delete = new Button("Delete");
        delete.getStyleClass().add("danger");
        delete.setOnAction(e -> delete());

        HBox top = new HBox(8, search);
        HBox.setHgrow(search, Priority.ALWAYS);

        getChildren().addAll(Ui.title("Workspace"),
                Ui.caption("Saved tokens stay on this computer. (Prototype: stored in memory with sample data.)"),
                top, table, new HBox(8, open, rename, delete), status);
    }

    void refresh() {
        table.getItems().setAll(ctx.repository.search(search.getText()));
    }

    private String peek(SavedToken saved) {
        try {
            DecodedJwt jwt = ctx.decoder.decode(saved.token());
            ClaimInspector.Status s = ctx.inspector.status(jwt.payload());
            return jwt.algorithmName() + "|" + s.name().replace('_', ' ').toLowerCase();
        } catch (JwtException ex) {
            return "?|unreadable";
        }
    }

    private SavedToken selected() {
        SavedToken s = table.getSelectionModel().getSelectedItem();
        if (s == null) {
            Ui.setStatus(status, "Select a token first", "warn");
        }
        return s;
    }

    private void open() {
        SavedToken s = selected();
        if (s != null) {
            ctx.navigator().openInDecoder(s.token());
        }
    }

    private void rename() {
        SavedToken s = selected();
        if (s == null) {
            return;
        }
        TextInputDialog dialog = new TextInputDialog(s.label());
        dialog.setTitle("Rename token");
        dialog.setHeaderText(null);
        dialog.setContentText("New label:");
        dialog.showAndWait().ifPresent(newLabel -> {
            if (ctx.repository.rename(s.id(), newLabel)) {
                refresh();
                Ui.setStatus(status, "Renamed", "ok");
            } else {
                Ui.setStatus(status, "Label cannot be empty", "warn");
            }
        });
    }

    private void delete() {
        SavedToken s = selected();
        if (s != null && ctx.repository.delete(s.id())) {
            refresh();
            Ui.setStatus(status, "Deleted \"" + s.label() + "\"", "ok");
        }
    }
}
