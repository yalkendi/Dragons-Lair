/**
 * An Order relating a Title and a customer. Every order has a customer
 * that is requesting it and a title that is to be requested. Every
 * order must also have a specified quantity and issue #.
 */
public class Order {

    private int customerId;
    private int titleId;
    private int quantity;
    private int issue;

    public Order(int customerId, int titleId, int quantity, int issue) {
        this.customerId = customerId;
        this.titleId =titleId;
        this.quantity = quantity;
        this.issue = issue;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getTitleId() {
        return titleId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getIssue() {
        return issue;
    }

    public void setIssue(int issue) {
        this.issue = issue;
    }

}
