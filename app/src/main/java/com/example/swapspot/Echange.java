package com.example.swapspot;

public class Echange {
    private int id;
    private String itemName;
    private String userName;
    private String imagePath;

    public Echange(int id, String itemName, String userName, String imagePath) {
        this.id = id;
        this.itemName = itemName;
        this.userName = userName;
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
