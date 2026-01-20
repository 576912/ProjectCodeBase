package com.maxi.analyser.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Product {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private double price;
    private String description;
    private String color;
    private String image;  // URL or relative path to image



    public Product() {
    }

    public Product(Long id, String color, String description, double price, String name, String image) {
        this.id = id;
        this.color = color;
        this.description = description;
        this.price = price;
        this.name = name;
        this.image=image;
    }
    // add getter & setter
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

