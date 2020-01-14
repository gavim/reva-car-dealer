public class Payment {
    private final String userId;
    private final int carId;
    private final double amount;

    public Payment() {
        this("", 0, 0);
    }

    public Payment(String userId, int carId, double amount) {
        this.userId = userId;
        this.carId = carId;
        this.amount = amount;
    }

    public String getUserId() {
        return userId;
    }

    public int getCarId() {
        return carId;
    }

    public double getAmount() {
        return amount;
    }
}
