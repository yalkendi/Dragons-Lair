/*
    This is a helper class to creat and displaying orders appropriately.
 */

public class OrderTable {

    private String title;
    private int quantity;
    private int issue;
    private int customerId;
    private int titleId;


    public OrderTable(int customerId, int titleId, String title, int quantity, int issue) {
        this.customerId = customerId;
        this.titleId =titleId;
        this.title =title;
        this.quantity = quantity;
        this.issue = issue;
    }

    public int getCustomerId(){
        return this.customerId;
    }
    public int getTitleId() {
        return this.titleId;
    }
    public String getTitleName() {
        return this.title;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public int getIssue() {
        return this.issue;
    }

}
