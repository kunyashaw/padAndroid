package com.huofu.RestaurantOS.Enum;

/**
 * author: Created by zzl on 15/10/14.
 */
public enum TemplateAlignmentEnum {

    PrinterTextAlignmentLeft(0),
    PrinterTextAlignmentCente(1),
    PrinterTextAlignmentRight(2);

    private  int value;
    private TemplateAlignmentEnum(int value)
    {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
