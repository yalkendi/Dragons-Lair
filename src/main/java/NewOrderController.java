import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NewOrderController {

    private Connection conn;
    private Order order;
    private int customerId;

    @FXML private Button addOrderButton;


    //@FXML private TextField setTitle;
    @FXML private ComboBox<String> setTitle;
    @FXML private TextField setQuantity;
    @FXML private TextField setIssue;

    @FXML
    void newOrder(ActionEvent event) {
        String Title = String.valueOf(setTitle.getItems());
        String Issue = setIssue.getText();
        String Quantity = setQuantity.getText();
        int customerId = this.customerId;



        PreparedStatement s = null;
        String sql = "INSERT INTO Orders (customerId, titleId, quantity, issue) VALUES (?, ?, ?, ?)";


        try
        {
            s = conn.prepareStatement(sql);
            s.setString(1, Integer.toString(customerId));
            s.setString(2, Title);
            s.setString(3, Quantity);
            s.setString(4, Issue);
//            s.setString(5, Integer.toString(customer.getId()));
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
        Stage window = (Stage) addOrderButton.getScene().getWindow();
        window.close();
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    public void setCustomerID(int id) {
        this.customerId = id;
    }


}