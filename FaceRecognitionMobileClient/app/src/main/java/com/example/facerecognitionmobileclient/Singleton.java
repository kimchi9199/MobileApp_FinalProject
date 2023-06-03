<<<<<<< Updated upstream
package com.example.facerecognitionmobileclient;

public class Singleton {
    private static Singleton instance;
    private User data;
    // Private constructor to prevent instantiation from outside the class
    private Singleton() {
        // Initialization code, if any
    }

    // Method to get the singleton instance
    public static Singleton getInstance() {
        if (instance == null) {
            // Create a new instance if it doesn't exist
            instance = new Singleton();
        }
        return instance;
    }

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }
}
=======
package com.example.facerecognitionmobileclient;

public class Singleton {
    private static Singleton instance;
    private User data;
    // Private constructor to prevent instantiation from outside the class
    private Singleton() {
        // Initialization code, if any
    }

    // Method to get the singleton instance
    public static Singleton getInstance() {
        if (instance == null) {
            // Create a new instance if it doesn't exist
            instance = new Singleton();
        }
        return instance;
    }

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }
}
>>>>>>> Stashed changes
