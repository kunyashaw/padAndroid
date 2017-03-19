package com.huofu.RestaurantOS.bean.storeOrder;


/**
 * 产品（是否打包、订单id、收费项目id、产品名称、产品单位、产品数量）
 * @author kunyashaw
 *
 */
public class MealProduct {

	public Long product_id ;//产品的id
	public String product_name = "";//产品的名称
	public String unit = "";//产品的单位
	public Boolean analysisFlag = false;//产品是否需要统计

	public Long getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Long product_id) {
		this.product_id = product_id;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Boolean getAnalysisFlag() {
		return analysisFlag;
	}

	public void setAnalysisFlag(Boolean analysisFlag) {
		this.analysisFlag = analysisFlag;
	}
}
