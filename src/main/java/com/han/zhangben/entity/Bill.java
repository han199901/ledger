package com.han.zhangben.entity;


import java.io.Serializable;
import java.util.Date;

public class Bill implements Serializable {
    private Integer id;
    private String name;
    private Date date;
    private String from;
    private String to;
    private String model;
    private Integer price;
    private Integer status; //0 为未付款
    private Integer del;    //0 为已删除

    public Bill() {
        name = "未命名";
        date = new Date(System.currentTimeMillis());
        from ="未设置";
        to = "未设置";
        model = "未设置";
        price = 0;
        status = 1;

    }


    public Bill(String name, Date date, String from, String to, String model, int price, int status) {
        this.name = name;
        this.date = date;
        this.from = from;
        this.to = to;
        this.model = model;
        this.price = price;
        this.status = status;
    }

    public Bill(Integer id, String name, Date date, String from, String to, String model, Integer price, Integer status) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.from = from;
        this.to = to;
        this.model = model;
        this.price = price;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", model='" + model + '\'' +
                ", price=" + price +
                ", status=" + status +
                '}';
    }
}

