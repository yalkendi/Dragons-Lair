import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * This Controller controls the Edit Title window. It allows the window
 * to get the text that is entered in the fields and save it in the
 * database.
 */
public class EditOrderController {

    private Connection conn;
    private Order order;
    private int customerId;

    @FXML private Button updateOrderButton;

    @FXML private ComboBox<String> setTitle;
    @FXML private TextField setQuantity;
    @FXML private TextField setIssue;

    @FXML private Text orderTitleErrorText;
    @FXML private Text orderQuantityErrorText;

    private String prevCustomerId;
    private String prevTitle;
    private String prevIssue;
    private String prevQuantity;

    private ObservableList<Title> titles  = FXCollections.observableArrayList();
    private ObservableList<String> titlesStr  = FXCollections.observableArrayList();

    /**
     * Updates the title based on the text entered in the text fields.
     * @param event Event that triggered the method call
     */
    @FXML
    void updateOrder(ActionEvent event) {

        PreparedStatement s = null;
        String sql = """
            UPDATE Orders
            SET customerId = ?, titleId = ?, quantity = ?, issue = ?
            WHERE customerId = ? AND titleId = ? AND quantity = ? AND issue = ?
            """;

        orderQuantityErrorText.setVisible(false);
        orderTitleErrorText.setVisible(false);

        if (getChoice(setTitle) == -1) {
            orderTitleErrorText.setVisible(true);
            return;
        }
        else if (setQuantity.getText().equals("")) {
            orderQuantityErrorText.setVisible(true);
            return;
        }
        else {
            int titleID = getChoice(setTitle);
            String issue = setIssue.getText();
            if (issue == "") {
                issue = null;
            }
            String quantity = setQuantity.getText();
            int customerId = this.customerId;

            try {
                s = conn.prepareStatement(sql);
                s.setString(1, Integer.toString(customerId));
                s.setString(2, Integer.toString(titleID));
                s.setString(3, quantity);
                s.setObject(4, issue, Types.INTEGER);

                s.setString(5, prevCustomerId);
                s.setString(6, prevTitle);
                s.setString(7, prevQuantity);
                s.setObject(8, prevIssue, Types.INTEGER);


                int rowsAffected = s.executeUpdate();

                if (rowsAffected == 0) {
                    //TODO: Throw an error
                } else if (rowsAffected > 1) {
                    //TODO: Throw an error
                }
                s.close();
            } catch (SQLException sqlExcept) {
                sqlExcept.printStackTrace();
            }
        }
        Stage window = (Stage) updateOrderButton.getScene().getWindow();
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
     * Sets the Title for this controller
     * @param order The title to set for this controller
     */
    public void setOrder(Order order) {
        this.order = order;
        this.customerId = order.getCustomerId();

        this.prevCustomerId = String.valueOf(order.getCustomerId());
        this. prevTitle =  String.valueOf(order.getTitleId());
        this.prevIssue = String.valueOf(order.getIssue());
        this.prevQuantity = String.valueOf(order.getQuantity());

        setTitle.setItems(this.titlesStr);
        setQuantity.setText(String.valueOf(order.getQuantity()));
        setIssue.setText(String.valueOf(order.getIssue()));

        setTitle.getSelectionModel().select(order.getTitleName());
        setTitle.setEditable(true);
        FxUtilTest.autoCompleteComboBoxPlus(setTitle, (typedText, itemToCompare) -> itemToCompare.toLowerCase().contains(typedText.toLowerCase()) || itemToCompare.equals(typedText));
    }

    public void populate(ObservableList<Title> getTitles){
        this.titles = getTitles;
        for(int i=0; i < titles.size(); i++){
            titlesStr.add(titles.get(i).getTitle());
        }
    }

    public int getChoice(ComboBox<String> setTitle ){
        String name = FxUtilTest.getComboBoxValue(setTitle);

        for(int i=0; i < titles.size(); i++){
            if (titles.get(i).getTitle().equals(name)){
                return titles.get(i).getId();
            }
        }
        return -1;
    }

}