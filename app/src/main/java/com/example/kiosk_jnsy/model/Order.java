// order.java
package com.example.kiosk_jnsy.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class Order implements Serializable {
    private Map<String,Double> emotion; // 감정
    private Map<String,Double> weather; // 날씨
    ArrayList<CafeItem> items; // 선택한 메뉴들
    String today; // 주문 날짜
    String guest; // 손님 이름
    Boolean check; // 사장님 체크 여부
    String orderToString; // 메뉴+옵션을 문자열로 반환


    public Order(){

    }
    public Order(Map<String,Double> emotion, Map<String, Double> weather, ArrayList<CafeItem> items, String today, String guest,String orderToString){
        this.emotion=emotion;
        this.weather=weather;
        this.items=items;
        this.today=today;
        this.guest=guest;
        this.check=false;// 디폴트는 false
        this.orderToString=orderToString;
    }


    // getter setter
    public Map<String, Double> getEmotion() {
        return emotion;
    }

    public void setEmotion(Map<String, Double> emotion) {
        this.emotion = emotion;
    }

    public Map<String, Double> getWeather() {
        return weather;
    }

    public void setWeather(Map<String, Double> weather) {
        this.weather = weather;
    }

    public ArrayList<CafeItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<CafeItem> items) {
        this.items = items;
    }

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

    public String getGuest() {
        return guest;
    }

    public void setGuest(String guest) {
        this.guest = guest;
    }


    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

}