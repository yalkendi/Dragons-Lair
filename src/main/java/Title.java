import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Title {

    private String title;
    private int price;
    private String notes;
    private int id;
    private BooleanProperty flagged;

    public Title(int id, String title, int price, String notes) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.notes = notes;
        this.flagged = new SimpleBooleanProperty(false);
    }

    public Title(int id, String title, int price, String notes, boolean flagged) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.notes = notes;
        this.flagged = new SimpleBooleanProperty(flagged);
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

    // Helps with showing the Title object name in the orders table
    @Override
    public String toString() {
        return this.getTitle();
    }

    public BooleanProperty flaggedProperty() {
        return flagged;
    }
  
    public boolean isFlagged() {
        return this.flagged.get();
    }

    public void setFlagged(boolean flagged) {
        this.flagged.set(flagged);
    }
}
