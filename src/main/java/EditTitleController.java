import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.*;

/**
 * This Controller controls the Edit Title window. It allows the window
 * to get the text that is entered in the fields and save it in the
 * database.
 */
public class EditTitleController{

    private Connection conn;
    private Title title;

    @FXML private Button updateTitleButton;

    @FXML private TextField updateTitleTitle;
    @FXML private TextField updateTitlePrice;
    @FXML private TextField updateTitleNotes;

    @FXML private Text priceValidText;

    /**
     * Updates the title based on the text entered in the text fields.
     * @param event Event that triggered the method call
     */
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

    /**
     * Sets the connection for this controller
     * @param conn The connection to set for this controller
     */
    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    /**
     * Sets the Title for this controller
     * @param title The title to set for this controller
     */
    public void setTitle(Title title) {
        this.title = title;
        updateTitleTitle.setText(title.getTitle());
        updateTitlePrice.setText(title.getPriceDollars());
        updateTitleNotes.setText(title.getNotes());
    }

    /**
     * Checks to see if a price String is in the valid format
     * @param priceDollars The String to test
     * @return True if the String is a valid format, false otherwise
     */
    private boolean isValidPrice(String priceDollars) {

        if (priceDollars.equals("") || priceDollars.matches("^[0-9]{1,3}(?:,?[0-9]{3})*\\.[0-9]{2}$") ) {

            return true;
        } else {
            priceValidText.setVisible(true);
            return false;
        }
    }

    /**
     * Converts a string in the format of XXX,XXX.XX to an integer
     * @param priceDollars The price in dollars to be converted
     * @return An integer representing the number of cents
     */
    private String dollarsToCents(String priceDollars) {
        priceDollars = priceDollars.replace(".", "");
        priceDollars = priceDollars.replaceAll(",", "");
        System.out.println(priceDollars);
        return priceDollars;
    }
}