import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.*;

/**
 * This Controller controls the Edit Customer window. It allows the window
 * to get the text that is entered in the fields and save it in the
 * database.
 */
public class EditCustomerController{

    private Connection conn;
    private Customer customer;

    @FXML private Button updateCustomerButton;

    @FXML private TextField updateCustomerEmail;
    @FXML private TextField updateCustomerFirstName;
    @FXML private TextField updateCustomerLastName;
    @FXML private TextField updateCustomerPhone;

    /**
     * Updates the Customer that has been set for this Controller. Sets
     * the values that have been entered in the text fields for the
     * Customer in the database.
     * @param event
     */
    @FXML
    void updateCustomer(ActionEvent event) {
        String firstName = updateCustomerFirstName.getText();
        String lastName = updateCustomerLastName.getText();
        String phone = updateCustomerPhone.getText();
        String email = updateCustomerEmail.getText();

        PreparedStatement s = null;
        String sql = """
        UPDATE Customers
        SET firstname = ?, lastname = ?, phone = ?, email= ?
        WHERE CUSTOMERID = ?
        """;

        try
        {
            s = conn.prepareStatement(sql);
            s.setString(1, firstName);
            s.setString(2, lastName);
            s.setString(3, phone);
            s.setString(4, email);
            s.setString(5, Integer.toString(customer.getId()));
            int rowsAffected = s.executeUpdate();

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
        Stage window = (Stage) updateCustomerButton.getScene().getWindow();
        window.close();
    }

    /**
     * Sets the connection for this controller
     * @param conn The connection to set for this controller
     */
    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    /**
     * Sets the Customer to edit
     * @param customer The customer that will be edited
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
        updateCustomerFirstName.setText(customer.getFirstName());
        updateCustomerLastName.setText(customer.getLastName());
        updateCustomerPhone.setText(customer.getPhone());
        updateCustomerEmail.setText(customer.getEmail());
    }
}