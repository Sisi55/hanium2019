package com.example.kiosk_jnsy.model;
// 지연 : recycler view에서 쓸 모델
public class MenuItem {
    private String name; // 메뉴 이름
    private int price; // 메뉴 가격
    private String img; // 메뉴 이미지 url

    // 생성자
    public MenuItem(String name,int price,String img){
        this.name=name;
        this.price=price;
        this.img=img;
    }

    // getter()
    public String getName() {
        return name;
    }
    public int getPrice() {
        return price;
    }
    public String getImg(){ return img;}

    // setter()
    public void setName(String name) {
        this.name = name;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public void setImg(String img){this.img=img; }

}
