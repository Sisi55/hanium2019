package com.example.kiosk_jnsy.model;

public class UserDTO {
    String personName;
    String personUUID;
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
// setter
    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public void setPersonUUID(String personUUID) {
        this.personUUID = personUUID;
    }
}
