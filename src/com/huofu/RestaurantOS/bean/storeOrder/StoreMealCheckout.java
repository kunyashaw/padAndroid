package com.huofu.RestaurantOS.bean.storeOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Created by zzl on 15/12/5.
 */
public class StoreMealCheckout {

    public long create_time;
    public List<ChargItem> meal_charges = new ArrayList<ChargItem>();
    public long merchant_id;
    public String order_id;
    public int packaged;
    public int packaged_seq;
    public int  port_count;
    public int port_id;
    public String port_letter;
    public String repast_date;
    public long store_id;
    public int take_mode;
    public int take_serial_number;
    public int take_serial_seq;
    public long time_bucket_id;

    public String getPort_letter() {
        return port_letter;
    }

    public void setPort_letter(String port_letter) {
        this.port_letter = port_letter;
    }

    public Long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Long create_time) {
        this.create_time = create_time;
    }

   public List<ChargItem> getMeal_charges() {
        return meal_charges;
    }

    public void setMeal_charges(List<ChargItem> meal_charges) {
        this.meal_charges = meal_charges;
    }

    public Long getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(Long merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public Integer getPackaged() {
        return packaged;
    }

    public void setPackaged(Integer packaged) {
        this.packaged = packaged;
    }

    public Integer getPackaged_seq() {
        return packaged_seq;
    }

    public void setPackaged_seq(Integer packaged_seq) {
        this.packaged_seq = packaged_seq;
    }

    public Integer getPort_count() {
        return port_count;
    }

    public void setPort_count(Integer port_count) {
        this.port_count = port_count;
    }

    public Integer getPort_id() {
        return port_id;
    }

    public void setPort_id(Integer port_id) {
        this.port_id = port_id;
    }

    public String getRepast_date() {
        return repast_date;
    }

    public void setRepast_date(String repast_date) {
        this.repast_date = repast_date;
    }

    public Long getStore_id() {
        return store_id;
    }

    public void setStore_id(Long store_id) {
        this.store_id = store_id;
    }

    public int getTake_mode() {
        return take_mode;
    }

    public void setTake_mode(int take_mode) {
        this.take_mode = take_mode;
    }

    public Integer getTake_serial_number() {
        return take_serial_number;
    }

    public void setTake_serial_number(Integer take_serial_number) {
        this.take_serial_number = take_serial_number;
    }

    public Integer getTake_serial_seq() {
        return take_serial_seq;
    }

    public void setTake_serial_seq(Integer take_serial_seq) {
        this.take_serial_seq = take_serial_seq;
    }

    public Long getTime_bucket_id() {
        return time_bucket_id;
    }

    public void setTime_bucket_id(Long time_bucket_id) {
        this.time_bucket_id = time_bucket_id;
    }
}
