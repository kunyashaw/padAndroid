package com.huofu.RestaurantOS.bean.test;

import com.huofu.RestaurantOS.bean.storeOrder.StoreMealCheckout;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/12/5.
 */
public class Group {

    private Long       id;
    private String     name;
    private List<User> users = new ArrayList<User>();

    public List<StoreMealCheckout> getStore_meal_checkout() {
        return store_meal_checkout;
    }

    public void setStore_meal_checkout(List<StoreMealCheckout> store_meal_checkout) {
        this.store_meal_checkout = store_meal_checkout;
    }

    public List<StoreMealCheckout> store_meal_checkout;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void addUser(User user) {
        users.add(user);
    }
}
