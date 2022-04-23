import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.*;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private boolean unsaved = false;

    @FXML private TableView<Customer> customerTable;
    @FXML private TableView<Order> customerOrderTable;
    @FXML private TableView<Title> titleTable;
    @FXML private TableView<FlaggedTable> flaggedTable;                                         //Jack
    @FXML private TableView<RequestTable> requestsTable;
    @FXML private TableColumn<Customer, String> customerLastNameColumn;
    @FXML private TableColumn<Customer, String> customerFirstNameColumn;
    @FXML private TableColumn<Customer, String> customerPhoneColumn;
    @FXML private TableColumn<Customer, String> customerEmailColumn;

    @FXML private TableColumn<Title, Boolean> titleFlaggedColumn;
    @FXML private TableColumn<Title, String> titleTitleColumn;
    @FXML private TableColumn<Title, String> titlePriceColumn;
    @FXML private TableColumn<Title, String> titleNotesColumn;

    @FXML private TableColumn<Order, String> customerOrderReqItemsColumn;
    @FXML private TableColumn<Order, String> customerOrderQuantityColumn;
    @FXML private TableColumn<Order, String> customerOrderIssueColumn;

    @FXML private TableColumn<FlaggedTable, String> flaggedTitleColumn;             //Jack
    @FXML private TableColumn<FlaggedTable, String> flaggedIssueColumn;             //Jack
    @FXML private TableColumn<FlaggedTable, String> flaggedPriceColumn;             //Jack
    @FXML private TableColumn<FlaggedTable, String> flaggedQuantityColumn;          //Jack
    @FXML private TableColumn<FlaggedTable, String> flaggedNumRequestsColumn;

    @FXML private TableColumn<RequestTable, String> requestLastNameColumn;
    @FXML private TableColumn<RequestTable, String> requestFirstNameColumn;
    @FXML private TableColumn<RequestTable, Integer> requestQuantityColumn;

    @FXML private Text customerFirstNameText;
    @FXML private Text customerLastNameText;
    @FXML private Text customerPhoneText;
    @FXML private Text customerEmailText;

    @FXML private Text titleTitleText;
    @FXML private Text titlePriceText;
    @FXML private Text titleNotesText;
    @FXML private Text titleDateFlagged;
    @FXML private Text titleDateFlaggedNoticeText;
    @FXML private Text titleNumberRequestsText;

    //for the summary info in "new week pulls" tab in "reports" tab
    @FXML private Text FlaggedTitlesTotalText;
    @FXML private Text FlaggedTitlesTotalCustomersText;
    @FXML private Text FlaggedIssueNumbersText;
    @FXML private Text FlaggedNoRequestsText;

    //for the summary info on a particular flagged title, when clicked
    @FXML private Text RequestTitleText;
    @FXML private Text RequestQuantityText;
    @FXML private Text RequestNumCustomersText;

    private static Connection conn = null;

    /**
     * Initiializes the state of the application. Creates a connection to the database,
     * loads all Customer, Title, and Order data, populates all tables, and creates
     * listeners.
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        createConnection();

        //Populate columns for Customer Table
        customerLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        customerFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        customerPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        customerEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        customerTable.getItems().setAll(this.getCustomers());

        //Populate columns for Orders Table
        customerOrderReqItemsColumn.setCellValueFactory(new PropertyValueFactory<>("TitleName"));
        customerOrderQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        customerOrderIssueColumn.setCellValueFactory(cell -> {
            if (cell.getValue().getIssue() > 0) {
                return new SimpleStringProperty(Integer.toString(cell.getValue().getIssue()));
            } else {
                return new SimpleStringProperty("");
            }
        });

        //Populate columns for Title Table
        titleFlaggedColumn.setCellValueFactory(c -> c.getValue().flaggedProperty());
        titleFlaggedColumn.setCellFactory(tc -> new CheckBoxTableCell<>());
        titleTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titlePriceColumn.setCellValueFactory(new PropertyValueFactory<>("priceDollars"));
        titlePriceColumn.setCellValueFactory(cell -> {
            if (cell.getValue().getPrice() > 0) {
                return new SimpleStringProperty(cell.getValue().getPriceDollars());
            } else {
                return new SimpleStringProperty("");
            }
        });
        titleNotesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        titleTable.getItems().setAll(this.getTitles());

        //Populate columns for flagged titles table in New Week Pulls Tab
        flaggedTitleColumn.setCellValueFactory(new PropertyValueFactory<>("flaggedTitleName"));
        flaggedIssueColumn.setCellValueFactory(new PropertyValueFactory<>("flaggedIssueNumber"));
        flaggedPriceColumn.setCellValueFactory(new PropertyValueFactory<>("flaggedPriceDollars"));
        flaggedQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("flaggedQuantity"));
        flaggedNumRequestsColumn.setCellValueFactory(new PropertyValueFactory<>("flaggedNumRequests"));
        flaggedTable.getItems().setAll(this.getFlaggedTitles());

        //for requests table
        requestLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("RequestLastName"));
        requestFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("RequestFirstName"));
        requestQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("RequestQuantity"));

        FlaggedTitlesTotalText.setText(Integer.toString(this.getNumTitlesCurrentlyFlagged()));
        //FlaggedTitlesTotalCustomersText.setText(Integer.toString(getNumCustomers()));
        FlaggedTitlesTotalCustomersText.setText("TODO");
        FlaggedIssueNumbersText.setText(Integer.toString(this.getNumFlaggedWithIssueNumbers()));
        //FlaggedIssueNumbersText.setText("");
        //FlaggedNoRequestsText.setText(Integer.toString(getNumTitlesNoRequests()));
        FlaggedNoRequestsText.setText(Integer.toString(getNumTitlesNoRequests()));

        //Add Listener for selected Customer
        customerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                customerFirstNameText.setText(newSelection.getFirstName());
                customerLastNameText.setText(newSelection.getLastName());
                customerPhoneText.setText(newSelection.getPhone());
                customerEmailText.setText(newSelection.getEmail());
                updateOrdersTable(newSelection);
            }
        });

        //Add Listener for Titles table
        titleTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                titleTitleText.setText(newSelection.getTitle());
                titlePriceText.setText(newSelection.getPriceDollars());
                titleNotesText.setText(newSelection.getNotes());
                String numberRequests = String.format("This Title Currently has %s Customer Requests", getNumberRequests(newSelection));
                LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
                if (newSelection.getDateFlagged() != null) {
                    titleDateFlagged.setText(newSelection.getDateFlagged().toString());
                    if (newSelection.getDateFlagged().isBefore(sixMonthsAgo)) {
                        titleDateFlaggedNoticeText.setVisible(true);
                    }
                    else {
                        titleDateFlaggedNoticeText.setVisible(false);
                    }
                }
                else {
                    titleDateFlagged.setText("Never");
                    titleDateFlaggedNoticeText.setVisible(true);
                }
                titleNumberRequestsText.setText(numberRequests);
            }
        });

        //add listener for selected flagged title

        flaggedTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {

                //first the summary info for the flagged title is set
                RequestTitleText.setText(newSelection.getFlaggedTitleName());
                RequestQuantityText.setText(Integer.toString(newSelection.getFlaggedQuantity()));
                RequestNumCustomersText.setText(Integer.toString(getNumRequests(newSelection.getTitleId())));

                //TODO: next load the table of customers who have requested the selected title

                //updateRequestsTable(newSelection.getTitleId());
                requestsTable.getItems().setAll(this.getRequests(newSelection.getTitleId()));
            }
        });
    }

    /**
     * Gets a list representing all Customers in the database
     * @return An ObservableList of Customer objects
     */
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


    /**
     * Gets a list representing all Orders in the database.
     * @return An ObservableList of Order objects
     */
    public ObservableList<Order> getOrderTable() {
        ObservableList<Order> orders = FXCollections.observableArrayList();

        Statement s = null;
        try
        {
            s = conn.createStatement();
            ResultSet results = s.executeQuery("SELECT ORDERS.CUSTOMERID, ORDERS.TITLEID, TITLES.title, ORDERS.QUANTITY, ORDERS.ISSUE FROM TITLES" +
                    " INNER JOIN ORDERS ON Orders.titleID=TITLES.TitleId");

            while(results.next())
            {
                int customerId = results.getInt(1);
                int titleId = results.getInt(2);
                String title = results.getString(3);
                int quantity = results.getInt(4);
                int issue = results.getInt(5);

                orders.add(new Order(customerId, titleId, title, quantity, issue));
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

    /**
     * Gets a list representing all Titles in the database
     * @return An ObeservableList of all Title objects
     */
    public ObservableList<Title> getTitles() {

        ObservableList<Title> titles  = FXCollections.observableArrayList();

        Statement s = null;
        try
        {
            s = conn.createStatement();
            ResultSet results = s.executeQuery("select * from Titles order by TITLE");

            while(results.next())
            {
                int titleId = results.getInt(1);
                String title = results.getString(2);
                int price= results.getInt(3);
                String notes = results.getString(4);
                boolean flagged = results.getBoolean(5);
                Date dateFlagged = results.getDate(6);
                if (dateFlagged != null) {
                    titles.add(new Title(titleId, title, price, notes, flagged, dateFlagged.toLocalDate()));
                }
                else {
                    titles.add(new Title(titleId, title, price, notes, flagged, null));
                }
            }
            results.close();
            s.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }

        for (Title t : titles) {
            t.flaggedProperty().addListener((obs, wasFlagged, isFlagged) -> {
                this.unsaved = true;
            });
        }

        return titles;
    }

    public ObservableList<FlaggedTable> getFlaggedTitles() {

        ObservableList<FlaggedTable> flaggedTitles = FXCollections.observableArrayList();

        Statement s = null;
        try
        {
            s = conn.createStatement();
            //ResultSet results = s.executeQuery("SELECT TITLES.TITLEID, TITLES.TITLE, ORDERS.ISSUE, TITLES.PRICE, ORDERS.QUANTITY from TITLES" +
            //        " INNER JOIN ORDERS ON ORDERS.TITLEID = TITLES.TITLEID AND TITLES.FLAGGED=true" );


            ResultSet results = s.executeQuery("""
            SELECT TITLEID, TITLE, ISSUE, PRICE, SUM(QUANTITY) AS QUANTITY FROM (
                                                  SELECT TITLES.TITLEID, TITLES.TITLE, ORDERS.ISSUE, TITLES.PRICE, ORDERS.QUANTITY
                                                  from TITLES
                                                      INNER JOIN ORDERS ON ORDERS.TITLEID = TITLES.TITLEID
                                                  WHERE TITLES.FLAGGED = true
                                              ) AS FLAGGED_ORDERS
                                GROUP BY TITLEID, TITLE, ISSUE, PRICE
            """);
            
            while(results.next())
            {
                int titleId = results.getInt(1);
                String title = results.getString(2);
                int issue = results.getInt(3);
                int price= results.getInt(4);
                int quantity = results.getInt(5);
                int numRequests = getNumRequests(titleId);


                flaggedTitles.add(new FlaggedTable( titleId, title, issue, price, quantity, numRequests));

            }
            results.close();
            s.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }

        return flaggedTitles;
    }

    /*
    TODO
     */
    public ObservableList<RequestTable> getRequests(int titleId){

        ObservableList<RequestTable> requestsTable = FXCollections.observableArrayList();

        Statement s = null;
        try
        {


            String sql = String.format("""
                    SELECT CUSTOMERS.LASTNAME, CUSTOMERS.FIRSTNAME, ORDERS.QUANTITY FROM CUSTOMERS
                    INNER JOIN ORDERS ON ORDERS.CUSTOMERID=CUSTOMERS.CUSTOMERID
                    WHERE ORDERS.TITLEID=%s
                    """, titleId);

            //ResultSet results = s.executeQuery("SELECT CUSTOMERS.LASTNAME, CUSTOMERS.FIRSTNAME, ORDERS.QUANTITY FROM CUSTOMERS " +
            //        "INNER JOIN ORDERS ON ORDERS.TITLEID=ORDERS.TITLEID AND TITLEID=" + titleId );
            s = conn.createStatement();

            ResultSet results = s.executeQuery(sql);

            while(results.next())
            {
                //int titleID = results.getInt(1);
                String lastName = results.getString(1);
                String firstName = results.getString(2);
                int quantity = results.getInt(3);
                requestsTable.add(new RequestTable( lastName, firstName, quantity));

            }
            results.close();
            s.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }

        return requestsTable;
    }



    /*
    void updateRequestsTable(int flaggedTitleID) {
        ObservableList<RequestTable> allRequests = getRequests(flaggedTitleID);
        ObservableList<RequestTable> titleRequests = FXCollections.observableArrayList();
        for (int i = 0; i < allRequests.size(); i++){
            if (allRequests.get(i).getTitleID() == flaggedTitleID){
                titleRequests.add(allRequests.get(i));
            }
        }
        requestsTable.getItems().setAll(titleRequests);
    }

    */

    /**
     * Creates a connection to the database and sets the global conn variable.
     */
    private void createConnection() {
        try {

            conn = DriverManager.getConnection("jdbc:derby:/Users/jackreefe/Desktop/SCHOOL/2022 Spring/capstone/derbyDB;");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    private helper method to count the number of requests for a title
     */
    private int getNumRequests(int titleId) {
        int numRequests = 0;
        ResultSet result;
        Statement s = null;
        try
        {
            String sql = String.format("""
                    SELECT SUM(QUANTITY) FROM ORDERS
                    WHERE ORDERS.titleID = %s
                    """, titleId);

            s = conn.createStatement();
            //ResultSet results = s.executeQuery("SELECT SUM(QUANTITY) FROM ORDERS WHERE ORDERS.TITLEID=" + titleId);

            result = s.executeQuery(sql);
            while (result.next()){
                numRequests = result.getInt(1);
            }

            //results.next();
            //numRequests = results.getInt(1);

            result.close();
            s.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }

        return numRequests;
    }


    /*
    i think this works

    helper method to get the first piece of summary info on "new week pulls" tab: the total # of flagged titles
     */
    private int getNumTitlesCurrentlyFlagged() {

        int numTitlesCurrentlyFlagged = 0;

        Statement s = null;
        try
        {
            s = conn.createStatement();
            ResultSet results = s.executeQuery("SELECT COUNT(TITLES.FLAGGED) AS FlagCount FROM TITLES WHERE FLAGGED=TRUE");

            results.next();
            numTitlesCurrentlyFlagged = results.getInt("FlagCount");

            results.close();
            s.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }

        return numTitlesCurrentlyFlagged;
    }


    /*
    TODO: doesn't work

    helper method to get the second piece of summary info on "new week pulls" tab: the number of customers the titles are flagged for
     */
    private int getNumCustomers(){
        int numCustomers = 0;

        Statement s = null;
        try
        {
            s = conn.createStatement();
            ResultSet results = s.executeQuery("");

            numCustomers = results.getInt(1);

            results.close();
            s.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }

        return numCustomers;
    }


    /*
    possibly works

    helper method to get the third piece of summary info on "new week pulls" tab: the number of titles that have triggered issue #'s
     */
    private int getNumFlaggedWithIssueNumbers(){
        int numTitlesWithIssueNumbers = 0;






        Statement s = null;
        try
        {
            s = conn.createStatement();
            ResultSet results = s.executeQuery("SELECT COUNT(*) FROM ORDERS WHERE ISSUE IS NOT NULL");

            results.next();
            numTitlesWithIssueNumbers = results.getInt(1);

            results.close();
            s.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }



        return numTitlesWithIssueNumbers;
    }


    /*
    TODO: doesn't work

    helper method to get the fourth piece of summary info on "new week pulls" tab: the number of titles with no customer requests
     */
    private int getNumTitlesNoRequests(){


        int numTitlesNoRequests = 0;

        /*
        for (int i = 0; i < getFlaggedTitles().size(); i++) {
            if (getFlaggedTitles().get(i).getFlaggedNumRequests() < 1){
                numTitlesNoRequests++;
            }
        }
        */


        numTitlesNoRequests = getNumTitlesCurrentlyFlagged() - getFlaggedTitles().size();

        /*
        Statement s = null;
        try
        {

            s = conn.createStatement();
            ResultSet results = s.executeQuery("");

            numTitlesNoRequests = results.getInt(1);

            results.close();
            s.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
        */

        return numTitlesNoRequests;
    }

    /**
     * Runs when the Add Customer button is pressed. Creates a new window for
     * the user to enter information and create a customer. Re-renders the
     * Customer table on window close.
     * @param event Event that triggered the method call.
     */
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

    /**
     * Runs when the Add Title button is pressed. Creates a new window for
     * the user to enter information and create a title. Re-renders the
     * Title table on window close.
     * @param event Event that triggered the method call.
     */
    @FXML
    void handleAddTitle(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NewTitleBox.fxml"));
            Parent root = fxmlLoader.load();

            NewTitleController newTitleController = fxmlLoader.getController();
            newTitleController.setConnection(this.conn);

            Stage window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Add Title");
            window.setResizable(false);
            window.setHeight(285);
            window.setWidth(400);
            window.setScene(new Scene(root));
            window.setOnHidden( e -> titleTable.getItems().setAll(getTitles()));

            window.show();
        } catch (Exception e) {
            System.out.println("Error when opening window. This is probably a bug");
            e.printStackTrace();
        }
    }

    /**
     * Runs when the Delete Customer button is pressed. Creates a dialog for the
     * user to confirm deletion of the selected Customer. Re-renders the Customer
     * table on window close.
     * @param event Event that triggered the method call.
     */
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

    /**
     * Runs when the Delete Title button is pressed. Creates a dialog for the
     * user to confirm deletion of the selected Title. Re-renders the Title
     * table on window close.
     * @param event Event that triggered the method call.
     */
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

    /**
     * Runs when the Edit Customer button is pressed. Creates a new window for
     * the user to enter information and edit a Customer. Re-renders the
     * Customer table on window close.
     * @param event Event that triggered the method call.
     */
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

    /**
     * Runs when the Edit Title button is pressed. Creates a new window for
     * the user to enter information and edit a title. Re-renders the
     * Title table on window close.
     * @param event Event that triggered the method call.
     */
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

                window.setHeight(285);
                window.setWidth(400);

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

    /**
     * Runs when the Add Request button is pressed. Creates a new window for
     * the user to enter information and create an Order. Re-renders the
     * Orders table on window close.
     * @param event Event that triggered the method call.
     */
    @FXML
    void handleNewOrder(ActionEvent event) {
        if (customerTable.getSelectionModel().getSelectedItem() == null) {
            AlertBox.display("New Order", "Please select a customer.");
        }
        else {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddOrderBox.fxml"));
                Parent root = fxmlLoader.load();

                NewOrderController newOrderController = fxmlLoader.getController();
                newOrderController.setConnection(conn);
                newOrderController.setCustomerID(customerTable.getSelectionModel().getSelectedItem().getId());
                newOrderController.populate(this.getTitles());
                newOrderController.setNewOrder();

                Stage window = new Stage();
                window.initModality(Modality.APPLICATION_MODAL);
                window.setTitle("New Order");
                window.setResizable(false);
                window.setHeight(250);
                window.setWidth(400);
                window.setScene(new Scene(root));
                window.setOnHidden(e -> updateOrdersTable(customerTable.getSelectionModel().getSelectedItem()));
                window.show();
            } catch (Exception e) {
                System.out.println("Error when opening window. This is probably a bug");
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is called when the singleTitleReportButton button is
     * clicked.
     */
    @FXML
    void handleExportSingleTitle(ActionEvent event) {

        Title title = titleTable.getSelectionModel().getSelectedItem();

        if (title == null) {
            Alert selectedAlert = new Alert(Alert.AlertType.INFORMATION, "Please select a title.", ButtonType.OK);
            selectedAlert.setTitle("Confirm Export");
            selectedAlert.setHeaderText("");
            selectedAlert.show();
        } else {
            LocalDate today = LocalDate.now();
            String fileName = title.getTitle() + " Requests " + today;

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Location");
            fileChooser.setInitialFileName(fileName);
            File file = fileChooser.showSaveDialog(((Node) event.getTarget()).getScene().getWindow());

            if (file != null) {
                try {
                    try (PrintWriter writer = new PrintWriter(file.getAbsolutePath() + ".csv")) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Customer,");
                        sb.append("Issue,");
                        sb.append("Quantity");
                        sb.append('\n');

                        ResultSet result;
                        Statement s = null;
                        try
                        {
                            String sql = String.format("""
                                SELECT FIRSTNAME, LASTNAME, ISSUE, QUANTITY FROM ORDERS
                                LEFT JOIN CUSTOMERS C on C.CUSTOMERID = ORDERS.CUSTOMERID
                                WHERE TITLEID = %s
                                ORDER BY LASTNAME
                                """, title.getId());

                                s = conn.createStatement();
                                result = s.executeQuery(sql);
                                while(result.next()) {
                                   sb.append(result.getString("LASTNAME"));
                                   sb.append(" ");
                                   sb.append(result.getString("FIRSTNAME"));
                                   sb.append(",");
                                   Object issue = result.getObject("ISSUE");
                                   if (issue != null) {
                                       sb.append(issue);
                                   }
                                   sb.append(",");
                                   sb.append(result.getInt("QUANTITY"));
                                   sb.append("\n");
                                }
                                result.close();
                                s.close();
                        }
                        catch (SQLException sqlExcept)
                        {
                            sqlExcept.printStackTrace();
                        }

                        writer.write(sb.toString());

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Adds all orders for a given Customer to the Orders table.
     * @param customer The Customer to update the Order Table for
     */
    void updateOrdersTable(Customer customer){
        ObservableList<Order> allOrders = getOrderTable();
        ObservableList<Order> customerOrders = FXCollections.observableArrayList();
        for(int i=0; i < allOrders.size(); i++) {
            if (allOrders.get(i).getCustomerId() == customer.getId())
                customerOrders.add(allOrders.get(i));
        }
        customerOrderTable.getItems().setAll(customerOrders);
    }

    /**
     * Runs when the Delete Request button is pressed. Creates a dialog for the
     * user to confirm deletion of the selected Order. Re-renders the Order
     * table on window close.
     * @param event Event that triggered the method call.
     */
    @FXML
    void handleDeleteOrder(ActionEvent event) {
        String title = titleTitleText.getText();

        if (customerOrderTable.getSelectionModel().getSelectedItem() == null) {
            AlertBox.display("Confirm Delete", "Please select an order.");
        } else {
            int customerId = customerOrderTable.getSelectionModel().getSelectedItem().getCustomerId();
            int titleId = customerOrderTable.getSelectionModel().getSelectedItem().getTitleId();
            int quantity = customerOrderTable.getSelectionModel().getSelectedItem().getQuantity();
            int issue = customerOrderTable.getSelectionModel().getSelectedItem().getIssue();


            boolean confirmDelete = ConfirmBox.display(
                    "Confirm Delete",
                    "Are you sure you would like to delete " + title + "?");
            if (confirmDelete) {
                PreparedStatement s = null;
                String sql = "DELETE FROM ORDERS WHERE CUSTOMERID = ? AND TITLEID = ? AND QUANTITY = ? AND ISSUE = ?";
                try {
                    s = conn.prepareStatement(sql);
                    s.setString(1, Integer.toString(customerId));
                    s.setString(2, Integer.toString(titleId));
                    s.setString(3, Integer.toString(quantity));
                    s.setString(4, Integer.toString(issue));


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
            updateOrdersTable(customerTable.getSelectionModel().getSelectedItem());

        }
    }

    /**
     * Saves the current state and date of all New Release Flags to the database
     */
    @FXML
    void saveFlags() {

        ObservableList<Title> titles = titleTable.getItems();
        ZonedDateTime startOfToday = LocalDate.now().atStartOfDay(ZoneId.systemDefault());
        long todayMillis = startOfToday.toEpochSecond() * 1000;
        Date today = new Date(todayMillis);

        Alert savingAlert = new Alert(Alert.AlertType.INFORMATION, "Saving New Release Flags...", ButtonType.OK);

        savingAlert.setTitle("Saving");
        savingAlert.setHeaderText("");
        savingAlert.setContentText("Saving New Release Flags...");
        savingAlert.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
        savingAlert.getDialogPane().getScene().getWindow().setOnCloseRequest(Event::consume);
        savingAlert.show();

        for (int i = 0; i < titles.size(); i++) {
            PreparedStatement s = null;
            if (titles.get(i).isFlagged()) {
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
                    s.executeUpdate();
                    s.close();
                } catch (SQLException sqlExcept) {
                    sqlExcept.printStackTrace();
                }
            }
            else {
                String sql = """
                    UPDATE Titles
                    SET FLAGGED = ?
                    WHERE TITLEID = ?
                    """;
                try {
                    s = conn.prepareStatement(sql);
                    s.setString(1, Boolean.toString(titles.get(i).isFlagged()));
                    s.setString(2, Integer.toString(titles.get(i).getId()));
                    s.executeUpdate();
                    s.close();
                } catch (SQLException sqlExcept) {
                    sqlExcept.printStackTrace();
                }
            }

        }

        savingAlert.close();

        Alert savedAlert = new Alert(Alert.AlertType.INFORMATION, "Saved Flags!", ButtonType.OK);
        savedAlert.setHeaderText("");
        savedAlert.show();
        this.unsaved = false;
        titleTable.getItems().setAll(getTitles());
    }

    /**
     * Sets the Flagged attribute of all Titles to false
     */
    @FXML
    void resetFlags() {
        Alert resetAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to reset all flags?" +
                " This cannot be undone.");
        resetAlert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(response -> {
                    PreparedStatement s = null;
                    String sql = """
                                UPDATE Titles
                                SET FLAGGED = FALSE
                                """;
                    try {
                        s = conn.prepareStatement(sql);
                        s.executeUpdate();
                        s.close();
                    } catch (SQLException sqlExcept) {
                        sqlExcept.printStackTrace();
                    }
                    titleTable.getItems().setAll(getTitles());
                });
        this.unsaved = false;
    }

    /**
     * Gets the number of Orders for a specified Title
     * @param title The title to count orders for
     * @return The number of orders
     */
    private int getNumberRequests(Title title) {
        int ordersCount = 0;
        ResultSet result;
        Statement s = null;
        try
        {
            String sql = String.format("""
                    SELECT COUNT(*) FROM ORDERS
                    WHERE titleID = %s
                    """, title.getId());

            s = conn.createStatement();
            result = s.executeQuery(sql);
            while(result.next()) {
                ordersCount = result.getInt(1);
            }
            result.close();
            s.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }

        return ordersCount;
    }

    /**
     * Returns true or false based on if there are unsaved changes to New
     * Release Flags or not.
     * @return A boolean for whether or not there are unsaved changes
     */
    public boolean isUnsaved() {
        return unsaved;

    }
}

