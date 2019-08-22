package com.example.kiosk_jnsy.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// 메뉴 아이템 모델
public class CafeItem implements Serializable { // 인텐트에 객체를 전달하기 위해 Serializable
    private String name; // 메뉴명
    private int price; // 메뉴가격
    private String body; // 메뉴 상세설명 : 나중에 추가
    private String photoUrl; // 안씀
    private String imageUrl; // 스토리지 download url

    public Map<String, Double> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Double> options) {
        this.options = options;
    }

    private Map<String,Double> options;// 세부옵션들과 그 값

    public CafeItem() {
    }

    public CafeItem(String name, int price, String imageUrl, String body,Map<String,Double> options) {
        this.price=price;
        this.name = name;
        // this.photoUrl = photoUrl;
        this.imageUrl = imageUrl;
        this.body=body;
        this.options=options;
    }
    public CafeItem(String name, int price, String imageUrl, String body) {
        this.price=price;
        this.name = name;
        // this.photoUrl = photoUrl;
        this.imageUrl = imageUrl;
        this.body=body;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CafeItem)) return false;
        CafeItem cafeItem = (CafeItem) o;
        return getPrice() == cafeItem.getPrice() &&
                Objects.equals(getName(), cafeItem.getName()) &&
                Objects.equals(getBody(), cafeItem.getBody()) &&
                Objects.equals(photoUrl, cafeItem.photoUrl) &&
                Objects.equals(getImageUrl(), cafeItem.getImageUrl()) &&
                Objects.equals(getOptions(), cafeItem.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getPrice(), getBody(), photoUrl, getImageUrl(), getOptions());
    }

    // 이름이 같으면 같은 객체로 인식하게끔 오버라이딩
/*
    @Override

    public int hashCode() {
        return name.hashCode();
    }



    @Override

    public boolean equals(Object obj) {
        if(!(obj instanceof CafeItem)) {
            return false;
        }
        CafeItem s = (CafeItem) obj;
        return name.equals(s.name);
    }
*/
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
}
