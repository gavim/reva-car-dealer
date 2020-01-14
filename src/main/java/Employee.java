public class Employee implements User {
    public final String userId;
    private String password;

    public Employee() {
        this("gavin", "123456");
    }

    public Employee(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    @Override
    public boolean tryPwd(String password) {
        return this.password.equals(password);
    }

    public void acceptOffer(UserService userService, CarService carService, String userId, Integer carId) {
        carService.acceptUserOfferOnTheCar(userService, userId, carId);
    }

    public void rejectOffer(CarService carService, String userId, Integer carId) {
        carService.rejectUserOfferOnTheCar(userId, carId);
    }

    public String getUserId() {
        return userId;
    }
}
