import java.io.Serializable;
import java.util.HashMap;

public class Car implements Serializable {
    private HashMap<String, Double> offers;
    public final int id;
    private String owner = "";

    private String make;
    private String year;
    private String model;
    private Double miles;

    public Car() {
        this(0);
    }

    public Car(int id) {
        this.id = id;
    }

    public Car(int id, String make, String year, String model, Double miles) {
        this.id = id;
        this.make = make;
        this.year = year;
        this.model = model;
        this.miles = miles;
    }

    public void addOffer(String userId, double amount) {
        if (offers == null) {
            offers = new HashMap<>();
        }
        offers.put(userId, amount);
    }

    public void removeOffer(String userId) {
        offers.remove(userId);
    }

    public HashMap<String, Double> getOffers() {
        return offers;
    }

    public void clearAllOffers() {
        offers = null;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "Car{" +
                "make='" + make + '\'' +
                ", year='" + year + '\'' +
                ", model='" + model + '\'' +
                ", miles=" + miles +
                '}';
    }
}
