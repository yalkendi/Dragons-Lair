public class Title {

    private String title;
    private int price;
    private String notes;
    private int id;

    public Title(int id, String title, int price, String notes) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public String getPriceDollars() {
        String total;
        int dollars = (price / 100);
        int cents = (price % 100);
        if ((cents / 10) == 0) {
            total = Integer.toString(dollars) + ".0" + Integer.toString(cents);
        }
        else {
            total = Integer.toString(dollars) + '.' + Integer.toString(cents);
        }
        return total;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
