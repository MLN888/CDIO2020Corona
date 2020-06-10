

//this main class is for testing the userinterface class
public class Main {
    public static void main(String[] args) {
        UserInterface UI = new UserInterface(false);
        UI.failCheck();

        while(true)
        {
            UI.moveSug(7, 6, 2, 40);
        }
    }
}