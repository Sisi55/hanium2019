package com.example.kiosk_jnsy.model;

import java.util.HashMap;
import java.util.Map;
// com.example.kiosk_jnsy.model.UserDTO
public class UserDTO {
    String personName;
    String personUUID;
    Map<String,Integer> itemPreference = new HashMap<>();

    UserDTO(){}
    public UserDTO(String personName, String personUUID){
        this.personName=personName;  this.personUUID=personUUID;
    }
// getter
    public String getPersonName() {
        return personName;
    }

    public String getPersonUUID() {
        return personUUID;
    }

    public Map<String, Integer> getItemPreference() {
        return itemPreference;
    }

    // setter
    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public void setPersonUUID(String personUUID) {
        this.personUUID = personUUID;
    }

    public void setItemPreference(Map<String, Integer> itemPreference) {
        this.itemPreference = itemPreference;
    }
}
