/*
    This is a helper class to create and display flagged titles appropriately.
 */

public class FlaggedTable {

    private String flaggedTitleName;
    private int flaggedIssueNumber;
    private int flaggedPriceDollars;
    private int flaggedQuantity;
    private int titleId;
    private int flaggedNumRequests;


    public FlaggedTable(int titleId, String title, int issue, int price, int quantity, int numRequests){
        this.titleId = titleId;
        this.flaggedTitleName = title;
        this.flaggedIssueNumber = issue;
        this.flaggedPriceDollars = price;
        this.flaggedQuantity = quantity;
        this.flaggedNumRequests = numRequests;
    }

    public int getTitleId(){ return this.titleId; }
    public String getFlaggedTitleName(){ return this.flaggedTitleName; }
    public int getFlaggedIssueNumber(){ return this.flaggedIssueNumber; }
    public int getFlaggedPriceCents() { return this.flaggedPriceDollars; }
    public String getFlaggedPriceDollars(){
        String total;
        int dollars = (flaggedPriceDollars / 100);
        int cents = (flaggedPriceDollars % 100);
        if ((cents / 10) == 0) {
            total = Integer.toString(dollars) + ".0" + Integer.toString(cents);
        }else{
            total = Integer.toString(dollars) + '.' + Integer.toString(cents);
        }

        return total;
    }
    public int getFlaggedQuantity(){ return this.flaggedQuantity; }
    public int getFlaggedNumRequests(){ return this.flaggedNumRequests; }
}
