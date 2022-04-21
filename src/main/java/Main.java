import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    Stage window;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sample-reports.fxml"));
        Parent root = fxmlLoader.load();
        Controller controller = fxmlLoader.getController();
        window = primaryStage;

        Scene scene = new Scene(root, 1000, 700);

        window.setTitle("Pull List");
        //Add handler for closing the window with unsaved flags
        window.setOnCloseRequest(closeRequest -> {
            System.out.println(controller.isUnsaved());
            if (controller.isUnsaved()) {
                Alert unsavedAlert = new Alert(Alert.AlertType.CONFIRMATION, "You have unsaved changes to flags. " +
                        "Are you sure you want to exit?");
                unsavedAlert.showAndWait()
                        .filter(response -> response == ButtonType.CANCEL)
                        .ifPresent(response -> {
                            closeRequest.consume();
                        });;
            }
        });

        window.setScene(scene);
        window.setMinHeight(700);
        window.setMinWidth(1000);
        window.show();
    }

    private void closeProgram() {
        boolean confirmClose = ConfirmBox.display("Confirm Exit", "Are you sure you would like to exit?");
        if (confirmClose) {
            window.close();
        }
    }
}
