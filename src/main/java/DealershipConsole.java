import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.util.*;

public class DealershipConsole {
    private static final Logger logger = LogManager.getLogger(DealershipConsole.class);
    private static Scanner scan = new Scanner(System.in);
    private static UserService userService;
    private static CarService carService;

    public static void main(String[] args) {
        logger.trace("Program Starts");

        try {
            userService = UserService.readFromFile("users.dat");
        } catch (FileNotFoundException e) {
            userService = new UserService();
            userService.initialize();
        }
        try {
            carService = CarService.readFromFile("cars.dat");
        } catch (FileNotFoundException e) {
            carService = new CarService();
        }

        String option = "";

        do {
            System.out.println("Please type a number to select on of the following options:");
            System.out.println("[1] Register as Customer");
            System.out.println("[2] User Login");
            System.out.println("[0] Exit");
            option = scan.next();

            int intOption = Integer.parseInt(option);
            if (intOption == 1) {
                System.out.println("Please Enter a UserName and Password of Your Choice:");
                String userName = scan.next();
                String password = scan.next();
                if (userService.registerCustomer(userName, password)) {
                    System.out.println("Success");
                } else {
                    System.out.println("Registration Failed");
                }
            } else if (intOption == 2) {
                System.out.println("Please Enter Your UserName and Password:");
                String userName = scan.next();
                String password = scan.next();
                User user = userService.loginUser(userName, password);
                if (user == null) {
                    System.out.println("Login Failed");
                } else {
                    if (user instanceof Customer) {
                        System.out.println("Welcome " + user.getUserId());
                        customerFunctions((Customer) user);
                    } else if (user instanceof Employee) {
                        System.out.println("You are logged in as employee " + user.getUserId());
                        employeeFunctions((Employee) user);
                    }
                }
            }

        } while (!option.equals("0"));

        scan.close();
        userService.saveToFile("users.dat");
        carService.saveToFile("cars.dat");
    }

    public static void employeeFunctions(Employee employee) {
        int option;
        do {
            System.out.println("Please type a number to select one of the following options:");
            System.out.println("[1] View Cars on the Lot");
            System.out.println("[2] Add a Car to the Lot");
            System.out.println("[3] Remove a Car From the Lot");
            System.out.println("[4] Accept of Reject an Offer on a Car");
            System.out.println("[5] View Payment History");
            System.out.println("[0] Back");
            option = Integer.parseInt(scan.next());

            if (option == 1) {
                printAllCarsAndKeepAMap();
            } else if (option == 2) {
                scan.nextLine();
                System.out.println("Please type the following info for the car you want to add:");
                System.out.println("Make Year Model Miles (Separated by Space)");
                String input = scan.nextLine();
                String[] carInfo = input.split(" ");
                carService.addCarToLot(
                        carService.registerACar(carInfo[0], carInfo[1], carInfo[2], Double.parseDouble(carInfo[3])));
            } else if (option == 3) {
                HashMap<Integer, Integer> map = printAllCarsAndKeepAMap();
                System.out.println("Please Select the Car You Want to Remove by Typing in the Corresponding Index:");
                System.out.println("(Enter Any Other Numbers to Cancel)");
                int selection = scan.nextInt();
                if (map.get(selection) != null) {
                    carService.removeCar(map.get(selection));
                }
            } else if (option == 4) {
                HashMap<Integer, Integer> map = printAllCarsAndKeepAMap();
                System.out.println("Please Select a Car by Typing in the Corresponding Index:");
                System.out.println("(Enter Any Other Numbers to Cancel)");
                int selection = scan.nextInt();
                if (map.get(selection) != null) {
                    Set<Map.Entry<String, Double>> offers = carService.showOffersOnTheCar(map.get(selection));
                    if (offers == null || offers.size() <= 0) {
                        System.out.println("No offers yet.");
                    } else {
                        HashSet<String> offerredCustomers = new HashSet<>();
                        for (Map.Entry<String, Double> entry : offers) {
                            System.out.println("UserId: " + entry.getKey() + "; Offer: " + entry.getValue());
                            offerredCustomers.add(entry.getKey());
                        }
                        System.out.println("Please Select an Offer by Typing the Corresponding UserId");
                        String userId = scan.next();

                        if (offerredCustomers.contains(userId)) {
                            System.out.println("1: Accept; 0: Reject");
                            int offerOption = scan.nextInt();
                            if (offerOption == 1) {
                                employee.acceptOffer(userService, carService, userId, map.get(selection));
                            } else if (offerOption == 0) {
                                employee.rejectOffer(carService, userId, map.get(selection));
                            }
                        }
                    }
                }
            } else if (option == 5) {
                Collection<Payment> paymentHist = userService.getPaymentHistory();
                for (Payment payment : paymentHist) {
                    System.out.println(payment.getUserId() + " Made a Payment of $" + payment.getAmount() +
                            " for " + carService.getCar(payment.getCarId()).toString());
                }
            }
        } while (option != 0);
    }

    public static void customerFunctions(Customer customer) {
        int option;
        do {
            System.out.println("Please type a number to select one of the following options:");
            System.out.println("[1] View Cars on the Lot");
            System.out.println("[2] Make an Offer for a Car");
            System.out.println("[3] View My Cars");
            System.out.println("[4] Make a Payment");
            System.out.println("[0] Back");
            option = Integer.parseInt(scan.next());

            if (option == 1) {
                printAllCarsAndKeepAMap();
            } else if (option == 2) {
                HashMap<Integer, Integer> map = printAllCarsAndKeepAMap();
                System.out.println("Please Select the Car You Want by Typing in the Corresponding Index:");
                System.out.println("(Enter Any Other Numbers to Cancel)");
                int selection = scan.nextInt();
                if (map.get(selection) != null) {
                    System.out.println("Please Enter the Dollar Amount You Want to Offer:");
                    double amount = scan.nextDouble();

                    if (customer.makeOffer(carService, map.get(selection), amount)) {
                        System.out.println("Success");
                    } else {
                        System.out.println("You can't place offer on this car, as it's owned by another person.");
                    }
                }
                System.out.println();
            } else if (option == 3) {
                List<Car> myCars = carService.carsOwnedByUser(customer.userId);
                if (myCars.isEmpty()) {
                    System.out.println("You don't own any car right now.");
                } else {
                    int index = 0;
                    for (Car car : myCars) {
                        System.out.println("[" + index + "] " + car);
                        index++;
                    }
                }
            } else if (option == 4) {
                List<Car> myCars = carService.carsOwnedByUser(customer.userId);
                List<Integer> map = new ArrayList<>();
                int index = 0;
                for (Car car : myCars) {
                    if (userService.getPayment(customer.userId, car.id) == 0) {
                        continue;
                    }
                    map.add(index, car.id);
                    System.out.println("[" + index + "] " + car);
                    index++;
                }
                if (index == 0) {
                    System.out.println("You don't have any upcoming payment.");
                } else {
                    System.out.println("Please Select the Car on Which You Want to Make the Payment:");
                    int selection = scan.nextInt();
                    if (selection >= 0 && selection < map.size()) {
                        double remaining = userService.getPayment(customer.userId, map.get(selection));
                        System.out.println("You still need to pay $" + remaining);
                        System.out.println(
                                "Please enter the number of months over which period you want to make the payment:");
                        int numberOfMon = scan.nextInt();
                        System.out.println("You'll need to pay " + remaining / numberOfMon + " per month");
                        System.out.println("Please Enter the Amount You Want to Pay:");
                        double amount = scan.nextDouble();
                        double remainingAfterPayment = customer.pay(userService, map.get(selection), amount);
                        if (remainingAfterPayment < 0) {
                            remainingAfterPayment = 0;
                        }
                        System.out.println("Success. Your remaining payment due is $" + remainingAfterPayment);
                    }
                }
            }
        } while (option != 0);
    }

    private static HashMap<Integer, Integer> printAllCarsAndKeepAMap() {
        Collection<Car> allCars = carService.showAllCars();
        HashMap<Integer, Integer> map = new HashMap<>();
        int index = 0;
        for (Car car : allCars) {
            map.put(index, car.id);
            System.out.println("[" + index + "] " + car);
            index++;
        }
        return map;
    }
}