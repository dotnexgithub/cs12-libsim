import java.util.Scanner;

public class Input
{
    private static Scanner scn = new Scanner(System.in);

    // private constructor, to prevent objects of type 'Input' from being created
    private Input() {}

    // static fields and methods belong to the class, not any particular instance of that class.
    // Therefore, call this method by writing 'Input.getUserString("Type your text: ")'
    public static String getUserString(String prompt)
    {
        System.out.print(prompt);
        return scn.nextLine();
    }

    // Loops until a user submits an int.
    public static int getUserInt(String prompt)
    {
        String userStr;
        int userInt = 0; // meaningless initialization

        boolean validInt = false;
        while (!validInt)
        {
            userStr = getUserString(prompt);

            try
            {
                userInt = Integer.parseInt(userStr);
                validInt = true; // we only set 'validInt = true' if userStr was successfully parsed as an int
            }
            catch (NumberFormatException e)
            {
                System.out.println("'" + userStr + "' is not a valid integer. Try again.");
            }
        }

        return userInt;
    }

    // Loops until the user submits a double.
    public static double getUserDouble(String prompt)
    {
        String userStr;
        double userDouble = 0.0; // meaningless initialization

        boolean validDouble = false;
        while (!validDouble)
        {
            userStr = getUserString(prompt);

            try
            {
                userDouble = Double.parseDouble(userStr);
                validDouble = true; // we only set 'validDouble = true' if userStr was successfully parsed as a double
            }
            catch (NumberFormatException e)
            {
                System.out.println("'" + userStr + "' is not a valid number. Try again.");
            }
        }

        return userDouble;
    }

    // Same logic as getUserDouble, but returns a float instead of a double.
    public static float getUserFloat(String prompt)
    {
        return (float)getUserDouble(prompt);
    }

    // Waits for user to press Enter before continuing.
    public static void waitForUserToPressEnter(String prompt)
    {
        System.out.print(prompt);
        scn.nextLine();
    }
}
