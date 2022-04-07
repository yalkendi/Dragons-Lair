import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.*;

public class EditTitleController{

    private Connection conn;
    private Title title;

    @FXML private Button updateTitleButton;

    @FXML private TextField updateTitleTitle;
    @FXML private TextField updateTitlePrice;
    @FXML private TextField updateTitleNotes;

    @FXML private Text priceValidText;

    @FXML
    void updateTitle(ActionEvent event) {
        String titleText = updateTitleTitle.getText();
        String notes = updateTitleNotes.getText();

        if(isValidPrice(updateTitlePrice.getText())) {
            String price = updateTitlePrice.getText();

            PreparedStatement s = null;
            String sql = """
            UPDATE TITLES
            SET TITLE = ?, PRICE = ?, NOTES = ?
            WHERE TITLEID = ?
            """;

            try
            {
                s = conn.prepareStatement(sql);
                s.setString(1, titleText);
                s.setString(2, dollarsToCents(price));
                s.setString(3, notes);
                s.setString(4, Integer.toString(title.getId()));
                int rowsAffected = s.executeUpdate();
                System.out.println(rowsAffected);
                if (rowsAffected == 0) {
                    //TODO: Throw an error
                }
                else if (rowsAffected > 1) {
                    //TODO: Throw an error
                }
                s.close();
            }
            catch (SQLException sqlExcept)
            {
                sqlExcept.printStackTrace();
            }
            Stage window = (Stage) updateTitleButton.getScene().getWindow();
            window.close();
        }
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    public void setTitle(Title title) {
        this.title = title;
        updateTitleTitle.setText(title.getTitle());
        updateTitlePrice.setText(title.getPriceDollars());
        updateTitleNotes.setText(title.getNotes());
    }

    private boolean isValidPrice(String priceDollars) {

        if (priceDollars.equals("") || priceDollars.matches("^[0-9]{1,3}(?:,?[0-9]{3})*\\.[0-9]{2}$") ) {

            return true;
        } else {
            priceValidText.setVisible(true);
            return false;
        }
    }

    private String dollarsToCents(String priceDollars) {
        priceDollars = priceDollars.replace(".", "");
        priceDollars = priceDollars.replaceAll(",", "");
        System.out.println(priceDollars);
        return priceDollars;
    }
}