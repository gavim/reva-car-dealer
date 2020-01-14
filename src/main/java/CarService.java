import java.io.*;
import java.util.*;

public class CarService implements Serializable {
    private HashMap<Integer, Car> lot;
    private int curCarId = 100;

    public CarService() {
        this(new HashMap<>());
    }

    public CarService(HashMap<Integer, Car> lot) {
        this.lot = lot;
    }

    public Car registerACar(String make, String year, String model, Double miles) {
        return new Car(curCarId++, make, year, model, miles);
    }

    public boolean addCarToLot(Car car) {
        lot.put(car.id, car);
        return true;
    }

    public Car getCar(int carId) {
        return lot.get(carId);
    }

    public Car removeCar(int carId) {
        return lot.remove(carId);
    }

    public Collection<Car> showAllCars() {
        return lot.values();
    }

    public Set<Map.Entry<String, Double>> showOffersOnTheCar(int carId) {
        HashMap<String, Double> offers = lot.get(carId).getOffers();
        return offers == null ? null : offers.entrySet();
    }

    public void acceptUserOfferOnTheCar(UserService userService, String userId, Integer carId) {
        Car car = lot.get(carId);
        userService.addPaymentToCustomer(userId, carId, car.getOffers().get(userId));
        car.setOwner(userId);
        car.clearAllOffers();
    }

    public void rejectUserOfferOnTheCar(String userId, Integer carId) {
        Car car = lot.get(carId);
        car.removeOffer(userId);
    }

    public boolean placeOfferOnCar(String userId, int carId, double amount) {
        Car car = lot.get(carId);
        if (car.getOwner().equals("")) {
            car.addOffer(userId, amount);
            return true;
        } else {
            return false;
        }
    }

    public List<Car> carsOwnedByUser(String userId) {
        ArrayList<Car> cars = new ArrayList<>();
        for (Car car : lot.values()) {
            if (car.getOwner().equals(userId)) {
                cars.add(car);
            }
        }
        return cars;
    }

    public void saveToFile(String fileName) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {
            fos = new FileOutputStream(fileName);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
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
    public static CarService readFromFile(String fileName) throws FileNotFoundException {
        CarService carService = null;
        try (FileInputStream fis = new FileInputStream(fileName);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            carService = (CarService) ois.readObject();
        } catch (FileNotFoundException | InvalidClassException e) {
            throw new FileNotFoundException(fileName + " doesn't not exist!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return carService;
    }
}
