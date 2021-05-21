package com.boots.entity;

public class Product {
    private String name;
    private String color;
    private String created_at;

    public Product() {

    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", created_at='" + created_at + '\'' +
                ", description='" + description + '\'' +
                ", secret='" + secret + '\'' +
                '}';
    }

    public Product(String name, String color, String created_at, String description, String secret) {
        this.name = name;
        this.color = color;
        this.created_at = created_at;
        this.description = description;
        this.secret = secret;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    private String description;
    private String secret;
}
