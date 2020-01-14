import java.util.HashMap;
import java.util.List;

public class Customer implements User {
    //    private HashMap<Integer, Double> offersMade;
    public final String userId;
    private String password;
    private HashMap<Integer, Double> remainingPayment;

    public Customer() {
        this("", "");
    }

    public Customer(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    @Override
    public boolean tryPwd(String password) {
        return this.password.equals(password);
    }

    public boolean makeOffer(CarService carService, int carId, double amount) {
        if (carService.placeOfferOnCar(userId, carId, amount)) {
//            offersMade.put(carId, amount);
            return true;
        }
        return false;
    }

    public void addPayment(int carId, double payment) {
        if (remainingPayment == null) {
            remainingPayment = new HashMap<>();
        }
        remainingPayment.put(carId, payment);
    }

    public double getPayment(int carId) {
        if (remainingPayment == null || remainingPayment.get(carId) == null) {
            return 0;
        }
        return remainingPayment.get(carId);
    }

    public double pay(UserService userService, int carId, double payment) {
        double remaining = remainingPayment.get(carId);
        remaining -= payment;
        if (remaining <= 0) {
            remainingPayment.remove(carId);
            if (remainingPayment.size() == 0) {
                remainingPayment = null;
            }
        } else {
            remainingPayment.replace(carId, remaining);
        }
        userService.addPaymentToHistory(new Payment(userId, carId, payment));
        return remaining;
    }

    public String getUserId() {
        return userId;
    }
}
