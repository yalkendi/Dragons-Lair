import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.sql.*;
import java.text.DateFormat;
import java.util.ResourceBundle;
import java.util.Date;

public class Controller implements Initializable {

    @FXML private TableView<Customer> customerTable;
    @FXML private TableView<Order> customerOrderTable;
    @FXML private TableView<Title> titleTable;
    @FXML private TableColumn<Customer, String> customerLastNameColumn;
    @FXML private TableColumn<Customer, String> customerFirstNameColumn;
    @FXML private TableColumn<Customer, String> customerPhoneColumn;
    @FXML private TableColumn<Customer, String> customerEmailColumn;

    @FXML private TableColumn<Title, Boolean> titleFlaggedColumn;
    @FXML private TableColumn<Title, String> titleTitleColumn;
    @FXML private TableColumn<Title, String> titlePriceColumn;
    @FXML private TableColumn<Title, String> titleNotesColumn;

    @FXML private TableColumn<Title, String> customerOrderReqItemsColumn;
    @FXML private TableColumn<Title, String> customerOrderQuantityColumn;
    @FXML private TableColumn<Title, String> customerOrderIssueColumn;

    @FXML private Text customerFirstNameText;
    @FXML private Text customerLastNameText;
    @FXML private Text customerPhoneText;
    @FXML private Text customerEmailText;

    @FXML private Text titleTitleText;
    @FXML private Text titlePriceText;
    @FXML private Text titleNotesText;

    private static Connection conn = null;

    //This is where we load from our database
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        createConnection();

        //Populate columns for Customer Table
        customerLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        customerFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        customerPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        customerEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        customerTable.getItems().setAll(getCustomers());

        //Populate columns for Orders Table
        customerOrderReqItemsColumn.setCellValueFactory(new PropertyValueFactory<>("titleId"));
        customerOrderQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        customerOrderIssueColumn.setCellValueFactory(new PropertyValueFactory<>("issue"));
        customerOrderTable.getItems().setAll(getOrders());

        //Populate columns for Title Table
        titleFlaggedColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Title, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Title, Boolean> flaggedCellDataFeatures) {
                return flaggedCellDataFeatures.getValue().flaggedProperty();
            }
        });
        titleFlaggedColumn.setCellFactory(tc -> new CheckBoxTableCell<>());
        titleTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titlePriceColumn.setCellValueFactory(new PropertyValueFactory<>("priceDollars"));
        titleNotesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        titleTable.getItems().setAll(getTitles());


        //Add Listener for Customer table
        customerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                customerFirstNameText.setText(newSelection.getFirstName());
                customerLastNameText.setText(newSelection.getLastName());
                customerPhoneText.setText(newSelection.getPhone());
                customerEmailText.setText(newSelection.getEmail());
            }
        });

        //Add Listener for Titles table
        titleTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                titleTitleText.setText(newSelection.getTitle());
                titlePriceText.setText(newSelection.getPriceDollars());
                titleNotesText.setText(newSelection.getNotes());
            }
        });

    }

    //Get all Customers
    public ObservableList<Customer> getCustomers() {

        ObservableList<Customer> customers = FXCollections.observableArrayList();

        Statement s = null;
        try
        {
            s = conn.createStatement();
            ResultSet results = s.executeQuery("select * from Customers");

            while(results.next())
            {
                int customerId = results.getInt(1);
                String firstName = results.getString(2);
                String lastName = results.getString(3);
                String phone = results.getString(4);
                String email = results.getString(5);
                customers.add(new Customer(customerId, firstName, lastName, phone, email));
            }
            results.close();
            s.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }

        return customers;
    }

    //Get all Titles
    public ObservableList<Title> getTitles() {

        ObservableList<Title> titles  = FXCollections.observableArrayList();

        Statement s = null;
        try
        {
            s = conn.createStatement();
            ResultSet results = s.executeQuery("select * from Titles");

            while(results.next())
            {
                int titleId = results.getInt(1);
                String title = results.getString(2);
                int price= results.getInt(3);
                String notes = results.getString(4);
                boolean flagged = results.getBoolean(5);
                titles.add(new Title(titleId, title, price, notes, flagged));
            }
            results.close();
            s.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }

        return titles;
    }

    //Get all Orders
    public ObservableList<Order> getOrders() {

        ObservableList<Order> orders  = FXCollections.observableArrayList();

        Statement s = null;
        try
        {
            s = conn.createStatement();
            ResultSet results = s.executeQuery("select * from Orders");

            while(results.next())
            {
                int customerId = results.getInt(1);
                int titleId = results.getInt(2);
                int issue = results.getInt(3);
                int quantity = results.getInt(4);
                orders.add(new Order(customerId, titleId, issue, quantity));
            }
            results.close();
            s.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }

        return orders;
    }

    private void createConnection() {
        try {
            conn = DriverManager.getConnection("jdbc:derby:/home/logan/School/Capstone/derbyDB;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleAddCustomer(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NewCustomerBox.fxml"));
            Parent root = fxmlLoader.load();

            NewCustomerController newCustomerController = fxmlLoader.getController();
            newCustomerController.setConnection(conn);

            Stage window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Add Customer");
            window.setResizable(false);
            window.setHeight(250);
            window.setWidth(400);
            window.setScene(new Scene(root));
            window.setOnHidden( e -> customerTable.getItems().setAll(getCustomers()));

            window.show();
        } catch (Exception e) {
            System.out.println("Error when opening window. This is probably a bug");
            e.printStackTrace();
        }
    }

    @FXML
    void handleAddTitle(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NewTitleBox.fxml"));
            Parent root = fxmlLoader.load();

            NewTitleController newTitleController = fxmlLoader.getController();
            newTitleController.setConnection(conn);

            Stage window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Add Title");
            window.setResizable(false);
            window.setHeight(250);
            window.setWidth(400);
            window.setScene(new Scene(root));
            window.setOnHidden( e -> titleTable.getItems().setAll(getTitles()));

            window.show();
        } catch (Exception e) {
            System.out.println("Error when opening window. This is probably a bug");
            e.printStackTrace();
        }
    }

    @FXML
    void handleDeleteCustomer(ActionEvent event) {
        String firstName = customerFirstNameText.getText();
        String lastName = customerLastNameText.getText();

        if (customerTable.getSelectionModel().getSelectedItem() == null) {
            AlertBox.display("Confirm Delete", "Please select a customer.");
        }
        else {
            int customerId = customerTable.getSelectionModel().getSelectedItem().getId();

            boolean confirmDelete = ConfirmBox.display(
                    "Confirm Delete",
                    "Are you sure you would like to delete customer " + firstName + " " + lastName + "?");
            if (confirmDelete) {
                PreparedStatement s = null;
                String sql = "DELETE FROM Customers WHERE customerId = ?";
                try {
                    s = conn.prepareStatement(sql);
                    s.setString(1, Integer.toString(customerId));
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
            }
            customerTable.getItems().setAll(getCustomers());
            customerFirstNameText.setText("");
            customerLastNameText.setText("");
            customerPhoneText.setText("");
            customerEmailText.setText("");
        }
    }

    @FXML
    void handleDeleteTitle(ActionEvent event) {
        String title = titleTitleText.getText();

        if (titleTable.getSelectionModel().getSelectedItem() == null) {
            AlertBox.display("Confirm Delete", "Please select a title.");
        }
        else {
            int titleId = titleTable.getSelectionModel().getSelectedItem().getId();

            boolean confirmDelete = ConfirmBox.display(
                    "Confirm Delete",
                    "Are you sure you would like to delete " + title + "?");
            if (confirmDelete) {
                PreparedStatement s = null;
                String sql = "DELETE FROM TITLES WHERE TITLEID = ?";
                try {
                    s = conn.prepareStatement(sql);
                    s.setString(1, Integer.toString(titleId));
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
            }
            titleTable.getItems().setAll(getTitles());
            titleTitleText.setText("");
            titlePriceText.setText("");
            titleNotesText.setText("");
        }
    }

    @FXML
    void handleEditCustomer(ActionEvent event) {
        if (customerTable.getSelectionModel().getSelectedItem() == null) {
            AlertBox.display("Confirm Edit", "Please select a customer.");
        }
        else {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EditCustomerBox.fxml"));
                Parent root = fxmlLoader.load();

                EditCustomerController editCustomerController = fxmlLoader.getController();
                editCustomerController.setConnection(conn);
                editCustomerController.setCustomer(customerTable.getSelectionModel().getSelectedItem());

                Stage window = new Stage();
                window.initModality(Modality.APPLICATION_MODAL);
                window.setTitle("Edit Customer");
                window.setResizable(false);
                window.setHeight(250);
                window.setWidth(400);
                window.setScene(new Scene(root));
                window.setOnHidden(e -> {
                    customerTable.getItems().setAll(getCustomers());
                    customerFirstNameText.setText("");
                    customerLastNameText.setText("");
                    customerPhoneText.setText("");
                    customerEmailText.setText("");
                });
                window.show();
            } catch (Exception e) {
                System.out.println("Error when opening window. This is probably a bug");
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleEditTitle(ActionEvent event) {
        if (titleTable.getSelectionModel().getSelectedItem() == null) {
            AlertBox.display("Confirm Edit", "Please select a title.");
        }
        else {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EditTitleBox.fxml"));
                Parent root = fxmlLoader.load();

                EditTitleController editTitleController = fxmlLoader.getController();
                editTitleController.setConnection(conn);
                editTitleController.setTitle(titleTable.getSelectionModel().getSelectedItem());

                Stage window = new Stage();
                window.initModality(Modality.APPLICATION_MODAL);
                window.setTitle("Edit Title");
                window.setResizable(false);
                window.setHeight(300);
                window.setWidth(307);
                window.setScene(new Scene(root));
                window.setOnHidden(e -> {
                    titleTable.getItems().setAll(getTitles());
                    titleTitleText.setText("");
                    titlePriceText.setText("");
                    titleNotesText.setText("");
                });
                window.show();
            } catch (Exception e) {
                System.out.println("Error when opening window. This is probably a bug");
                e.printStackTrace();
            }
        }
    }
    @FXML
    void saveFlags() {

        ObservableList<Title> titles = titleTable.getItems();
        Date today = new Date();

        for (int i = 0; i < titles.size(); i++) {
            PreparedStatement s = null;
            String sql = """
                    UPDATE Titles
                    SET FLAGGED = ?, DATE_FLAGGED = ?
                    WHERE TITLEID = ?
                    """;
            try {
                s = conn.prepareStatement(sql);
                s.setString(1, Boolean.toString(titles.get(i).isFlagged()));
                s.setString(2, DateFormat.getDateInstance().format(today));
                s.setString(3, Integer.toString(titles.get(i).getId()));
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
        }

        return;
    }
}

