import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.*;

/**
 * This Controller controls the New Title window. It allows the window
 * to get the text that is entered in the fields and save it in the
 * database.
 */
public class NewTitleController{

    private Connection conn;

    @FXML private Button addTitleButton;

    @FXML private TextField newTitleTitle;
    @FXML private TextField newTitlePrice;
    @FXML private TextField newTitleNotes;

    @FXML private Text priceValidText;

    /**
     * Creates a title based off of the text fields and adds it
     * to the database
     * @param event Event that triggered this method
     */
    @FXML
    void addTitle(ActionEvent event) {
        String title = newTitleTitle.getText();
        String notes = newTitleNotes.getText();

        if(isValidPrice(newTitlePrice.getText())) {
            String price = newTitlePrice.getText();

            PreparedStatement s = null;
            String sql = "INSERT INTO Titles (TITLE, PRICE, NOTES) VALUES (?, ?, ?)";
            try {
                s = conn.prepareStatement(sql);
                s.setString(1, title);
                s.setObject(2, dollarsToCents(price), Types.INTEGER);
                s.setString(3, notes);
                int rowsAffected = s.executeUpdate();

                if (rowsAffected == 0) {
                    //TODO: Throw an error
                } else if (rowsAffected > 1) {
                    //TODO: Throw and error
                }
                s.close();
            } catch (SQLException sqlExcept) {
                sqlExcept.printStackTrace();
            }
            Stage window = (Stage) addTitleButton.getScene().getWindow();
            window.close();
        }
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
        if (priceDollars == "") {
            return null;
        }
        priceDollars = priceDollars.replace(".", "");
        priceDollars = priceDollars.replaceAll(",", "");
        System.out.println(priceDollars);
        return priceDollars;
    }

    /**
     * Sets this database connection for this controller
     * @param conn Connection to set for this controller
     */
    public void setConnection(Connection conn) {
        this.conn = conn;
    }
}