/*
This is a helper class to create and display customer requests appropriately
 */

public class RequestTable {
    private String requestLastName;
    private String requestFirstName;
    private int requestQuantity;

    public RequestTable(String requestLastName, String requestFirstName, int requestQuantity){
        this.requestLastName = requestLastName;
        this.requestFirstName = requestFirstName;
        this.requestQuantity = requestQuantity;
    }

    public String getRequestLastName(){ return this.requestLastName; }
    public String getRequestFirstName(){ return this.requestFirstName; }
    public String getRequestQuantity(){ return String.valueOf(this.requestQuantity); }
}
