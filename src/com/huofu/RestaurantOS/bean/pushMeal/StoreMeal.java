package com.huofu.RestaurantOS.bean.pushMeal;

import com.huofu.RestaurantOS.bean.storeOrder.ChargItem;
import com.huofu.RestaurantOS.bean.storeOrder.StoreOrder;

import java.util.List;

/**
 * author: Created by zzl on 15/12/5.
 */
public class StoreMeal {

    public int time_bucket_id;
    public int packaged_seq;
    public long create_time;
    public int take_serial_seq;
    public int take_serial_number;
    public int count;
    public StoreOrder store_order;
    public List<ChargItem> meal_charges;

    public String getPort_letter() {
        return port_letter;
    }

    public void setPort_letter(String port_letter) {
        this.port_letter = port_letter;
    }

    public String port_letter;


    public StoreOrder getStore_order() {
        return store_order;
    }

    public void setStore_order(StoreOrder store_order) {
        this.store_order = store_order;
    }

    public String getRepast_date() {
        return repast_date;
    }

    public void setRepast_date(String repast_date) {
        this.repast_date = repast_date;
    }

    public String repast_date;
    public int port_id;
    public String order_id;
    public int packaged;

    public int getTake_mode() {
        return take_mode;
    }

    public void setTake_mode(int take_mode) {
        this.take_mode = take_mode;
    }

    public int port_count;
    public int take_mode;

    public int getTime_bucket_id() {
        return time_bucket_id;
    }

    public void setTime_bucket_id(int time_bucket_id) {
        this.time_bucket_id = time_bucket_id;
    }

    public int getPackaged_seq() {
        return packaged_seq;
    }

    public void setPackaged_seq(int packaged_seq) {
        this.packaged_seq = packaged_seq;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public int getTake_serial_seq() {
        return take_serial_seq;
    }

    public void setTake_serial_seq(int take_serial_seq) {
        this.take_serial_seq = take_serial_seq;
    }

    public int getTake_serial_number() {
        return take_serial_number;
    }

    public void setTake_serial_number(int take_serial_number) {
        this.take_serial_number = take_serial_number;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    public List<ChargItem> getMeal_charges() {
        return meal_charges;
    }

    public void setMeal_charges(List<ChargItem> meal_charges) {
        this.meal_charges = meal_charges;
    }


    public int getPort_id() {
        return port_id;
    }

    public void setPort_id(int port_id) {
        this.port_id = port_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public int getPackaged() {
        return packaged;
    }

    public void setPackaged(int packaged) {
        this.packaged = packaged;
    }

    public int getPort_count() {
        return port_count;
    }

    public void setPort_count(int port_count) {
        this.port_count = port_count;
    }
}
