import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;


import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainTest extends ApplicationTest {

    private Controller controller;

    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = fxmlLoader.load();
        stage.setScene(new Scene(root));
        stage.show();
        stage.toFront();

        controller = fxmlLoader.getController();
    }

    @Before
    public void setUp () throws Exception {
    }

    @After
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Test
    public void testAddingCustomer () {
        int before = controller.getCustomers().size();

        clickOn("Customers");
        clickOn("#addCustomerButtonMain");
        clickOn("#newCustomerFirstName");
        write("TestFirst");
        clickOn("#newCustomerLastName");
        write("TestLast");
        clickOn("#newCustomerPhone");
        write("911-911-9911");
        clickOn("#newCustomerEmail");
        write("Test@dumpy.net");

        clickOn("#addCustomerButton");


        int after = controller.getCustomers().size();

        assertEquals(before+1, after);
    }

    @Test
    public void testAddingTitle () {
        int before = controller.getTitles().size();

        clickOn("Titles");
        clickOn("#addTitleButtonMain");
        clickOn("#newTitleTitle");
        write("The test");
        clickOn("#newTitlePrice");
        write("5.00");
        clickOn("#newTitleNotes");
        write("Just testing");

        clickOn("#addTitleButton");

        int after = controller.getTitles().size();

        assertEquals(before+1, after);
    }

    @Test
    public void testDeletingTitle () {
        int before = controller.getTitles().size();

        clickOn("Titles");
        clickOn("The test");
        clickOn("#deleteTitleButton");
        clickOn("#yesButton");

        int after = controller.getTitles().size();

        assertEquals(before - 1, after);
    }

    @Test
    public void testDeletingCustomer () {
        int before = controller.getCustomers().size();

        clickOn("Customers");
        clickOn("TestLast");
        clickOn("#deleteCustomerButton");
        clickOn("#yesButton");

        int after = controller.getCustomers().size();

        assertEquals(before - 1, after);
    }
}