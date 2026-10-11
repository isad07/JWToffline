package com.jwtoffline.ui;

import com.jwtoffline.core.ClaimInspector;
import com.jwtoffline.core.DecodedJwt;
import com.jwtoffline.core.JwtException;
import com.jwtoffline.workspace.SavedToken;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.List;
import java.util.function.Function;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

class WorkspaceView extends VBox {

    private static final DateTimeFormatter WHEN = DateTimeFormatter.ofPattern("MMM d, HH:mm").withZone(ZoneId.systemDefault());

    private final AppContext ctx;
    private final TableView<TokenRow> table = new TableView<>();
    private final TextField search = new TextField();
    private final Label status = Ui.status();

    WorkspaceView(AppContext ctx) {
        this.ctx = ctx;
        Ui.setupScreen(this, 12);

        search.setPromptText("Search by label");
        search.textProperty().addListener((o, a, b) -> refresh());

        table.getColumns().setAll(List.of(
                column("Label", 260, row -> row.token().label()),
                column("Alg", 80, TokenRow::algorithm),
                column("Status", 140, TokenRow::status),
                column("Saved", 140, row -> WHEN.format(row.token().savedAt()))));
        table.setPlaceholder(new Label("No saved tokens yet. Use \"Save to workspace\" on the Decode screen."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                open();
            }
        });
        VBox.setVgrow(table, Priority.ALWAYS);

        Button open = Ui.button("Open in decoder", this::open, "primary");
        Button rename = Ui.button("Rename", this::rename);
        Button delete = Ui.button("Delete", this::delete, "danger");

        HBox top = new HBox(8, search);
        HBox.setHgrow(search, Priority.ALWAYS);

        getChildren().addAll(Ui.title("Workspace"),
                top, table, new HBox(8, open, rename, delete), status);
    }

    private static TableColumn<TokenRow, String> column(String title, double width, Function<TokenRow, String> value) {
        TableColumn<TokenRow, String> column = new TableColumn<>(title);
        column.setCellValueFactory(c -> new SimpleStringProperty(value.apply(c.getValue())));
        column.setPrefWidth(width);
        return column;
    }

    void refresh() {
        table.getItems().setAll(ctx.repository.search(search.getText()).stream().map(this::toRow).toList());
    }

    private TokenRow toRow(SavedToken saved) {
        try {
            DecodedJwt jwt = ctx.decoder.decode(saved.token());
            ClaimInspector.Status timeStatus = ctx.inspector.inspect(jwt.payload()).status();
            String state = timeStatus == ClaimInspector.Status.VALID ? "not expired"
                    : timeStatus.name().replace('_', ' ').toLowerCase(Locale.ROOT);
            return new TokenRow(saved, jwt.algorithmName(), state);
        } catch (JwtException ex) {
            return new TokenRow(saved, "?", "unreadable");
        }
    }

    private record TokenRow(SavedToken token, String algorithm, String status) {
    }

    private SavedToken selected() {
        TokenRow row = table.getSelectionModel().getSelectedItem();
        if (row == null) {
            Ui.setStatus(status, "Select a token first", Ui.StatusKind.WARN);
        }
        return row == null ? null : row.token();
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
                Ui.setStatus(status, "Renamed", Ui.StatusKind.OK);
            } else {
                Ui.setStatus(status, "Label cannot be empty", Ui.StatusKind.WARN);
            }
        });
    }

    private void delete() {
        SavedToken s = selected();
        if (s != null && ctx.repository.delete(s.id())) {
            refresh();
            Ui.setStatus(status, "Deleted \"" + s.label() + "\"", Ui.StatusKind.OK);
        }
    }
}
