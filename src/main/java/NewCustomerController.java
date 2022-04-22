import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.*;

/**
 * This Controller controls the New Customer window. It allows the window
 * to get the text that is entered in the fields and save it in the
 * database.
 */
public class NewCustomerController{

    private Connection conn;

    @FXML private Button addCustomerButton;

    @FXML private TextField newCustomerEmail;
    @FXML private TextField newCustomerFirstName;
    @FXML private TextField newCustomerLastName;
    @FXML private TextField newCustomerPhone;

    /**
     * Creates a customer based off of the text fields and adds it
     * to the database
     * @param event Event that triggered this method
     */
    @FXML
    void addCustomer(ActionEvent event) {
        String firstName = newCustomerFirstName.getText();
        String lastName = newCustomerLastName.getText();
        String phone = newCustomerPhone.getText();
        String email = newCustomerEmail.getText();

        PreparedStatement s = null;
        String sql = "INSERT INTO Customers (firstname, lastname, phone, email) VALUES (?, ?, ?, ?)";
        try
        {
            s = conn.prepareStatement(sql);
            s.setString(1, firstName);
            s.setString(2, lastName);
            s.setString(3, phone);
            s.setString(4, email);
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
        Stage window = (Stage) addCustomerButton.getScene().getWindow();
        window.close();
    }

    /**
     * Sets the database connection for this controller
     * @param conn Connection to set for this controller
     */
    public void setConnection(Connection conn) {
        this.conn = conn;
    }
}