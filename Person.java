package oop2;

/**
 * The {@code Person} class represents a person with a name, age, and email.
 * It provides basic methods to get and set personal information and display details.
 */
public class Person {
    private String Name;
    private int Age;
    private String Email;

    /**
     * Constructs a new {@code Person} with the specified name, age, and email.
     *
     * @param Name  the person's name
     * @param Age   the person's age
     * @param Email the person's email
     */
    public Person(String Name, int Age, String Email) {
        this.Name = Name;
        this.Age = Age;
        this.Email = Email;
    }

    /**
     * Returns the name of the person.
     *
     * @return the name
     */
    public String getName() {
        return Name;
    }

    /**
     * Sets the name of the person.
     *
     * @param Name the name to set
     */
    public void setName(String Name) {
        this.Name = Name;
    }

    /**
     * Returns the age of the person.
     *
     * @return the age
     */
    public int getAge() {
        return Age;
    }

    /**
     * Sets the age of the person.
     *
     * @param Age the age to set
     */
    public void setAge(int Age) {
        this.Age = Age;
    }

    /**
     * Returns the email of the person.
     *
     * @return the email
     */
    public String getEmail() {
        return Email;
    }

    /**
     * Sets the email of the person.
     *
     * @param Email the email to set
     */
    public void setEmail(String Email) {
        this.Email = Email;
    }

    /**
     * Displays all details of the person: name, age, and email.
     */
    public void displayDetails() {
        System.out.println("person Details:");
        System.out.println("Name:" + Name);
        System.out.println("Age:" + Age);
        System.out.println("Email:" + Email);
    }

    /**
     * Displays only the name of the person.
     *
     * @param Name the name to display
     */
    public void displayDetails(String Name) {
        System.out.println("Person details:");
        System.out.println("Name:" + Name);
    }

    /**
     * Displays the name and age of the person.
     *
     * @param Name the name to display
     * @param Age  the age to display
     */
    public void displayDetails(String Name, int Age) {
        System.out.println("Person details:");
        System.out.println("Name:" + Name);
        System.out.println("Age:" + Age);
    }
}
