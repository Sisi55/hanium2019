package com.example.kiosk_jnsy.model;

import java.text.SimpleDateFormat;

public class RecomDTO {
    private String userName;
    private String itemName;
    private String time;

    public RecomDTO(String userName, String itemName){
        this.userName = userName;
        this.itemName = itemName;
        this.time = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
    }

    public String getUserName() {
        return userName;
    }

    public String getItemName() {
        return itemName;
    }

    public String getTime() {
        return time;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
