/*
This is a helper class to create and display customer requests appropriately
 */

public class RequestTable {
    private int titleID;
    private String requestLastName;
    private String requestFirstName;
    private int requestQuantity;

    public RequestTable(int titleID, String requestLastName, String requestFirstName, int requestQuantity){
        this.titleID = titleID;
        this.requestLastName = requestLastName;
        this.requestFirstName = requestFirstName;
        this.requestQuantity = requestQuantity;
    }

    public int getTitleID(){ return this.titleID; }
    public String getRequestLastName(){ return this.requestLastName; }
    public String getRequestFirstName(){ return this.requestFirstName; }
    public String getRequestQuantity(){ return String.valueOf(this.requestQuantity); }
}
