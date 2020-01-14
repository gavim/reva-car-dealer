import java.io.*;
import java.util.Collection;
import java.util.HashMap;

public class UserService {
    HashMap<String, User> userPool;
    HashMap<String, Payment> paymentHistory;

    public UserService() {
        this(new HashMap<>());
    }

    public UserService(HashMap<String, User> userPool) {
        this.userPool = userPool;
    }

    public void initialize() {
        userPool.put("gavin", new Employee("gavin", "123456"));
    }

    public boolean registerCustomer(String userId, String password) {
        userPool.put(userId, new Customer(userId, password));
        return true;
    }

    public User loginUser(String userId, String password) {
        User user = userPool.get(userId);
        if (user == null) {
            return null;
        }
        if (user.tryPwd(password)) {
            return user;
        } else {
            return null;
        }
    }

    public void addPaymentToCustomer(String userId, int carId, double payment) {
        Customer customer = (Customer) userPool.get(userId);
        customer.addPayment(carId, payment);
    }

    public double getPayment(String userId, int carId) {
        Customer customer = (Customer) userPool.get(userId);
        return customer.getPayment(carId);
    }

    public void addPaymentToHistory(Payment payment) {
        if (paymentHistory == null) {
            paymentHistory = new HashMap<>();
        }
        paymentHistory.put(payment.getUserId(), payment);
    }

    public Collection<Payment> getPaymentHistory() {
        return paymentHistory.values();
    }

    public void saveToFile(String fileName) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {
            fos = new FileOutputStream(fileName);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(userPool);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static UserService readFromFile(String fileName) throws FileNotFoundException {
        HashMap<String, User> pool = null;

        try (FileInputStream fis = new FileInputStream(fileName);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            pool = (HashMap<String, User>) ois.readObject();
        } catch (FileNotFoundException | InvalidClassException e) {
            throw new FileNotFoundException(fileName + " doesn't not exist!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return new UserService(pool);
    }
}
