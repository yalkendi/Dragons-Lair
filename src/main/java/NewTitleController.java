import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.*;

public class NewTitleController{

    private Connection conn;

    @FXML private Button addTitleButton;

    @FXML private TextField newTitleTitle;
    @FXML private TextField newTitlePrice;
    @FXML private TextField newTitleNotes;


    @FXML
    void addTitle(ActionEvent event) {
        String title = newTitleTitle.getText();
        String price = newTitlePrice.getText();
        String notes = newTitleNotes.getText();

        PreparedStatement s = null;
        String sql = "INSERT INTO Titles (TITLE, PRICE, NOTES) VALUES (?, ?, ?)";
        try
        {
            s = conn.prepareStatement(sql);
            s.setString(1, title);
            s.setString(2, price);
            s.setString(3, notes);
            int rowsAffected = s.executeUpdate();

            if (rowsAffected == 0) {
                //TODO: Throw an error
            }
            else if (rowsAffected > 1) {
                //TODO: Throw and error
            }
            s.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
        Stage window = (Stage) addTitleButton.getScene().getWindow();
        window.close();
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }
}