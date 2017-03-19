package com.huofu.RestaurantOS.bean;

/***
 * 设置中各标题的类，包含图标名称、设置项名称、设置项提示
 * 举个例子：R.drawble.setting_client_enterprice、企业设置、默认9折
 * @author kunyashaw
 *
 */
public class SettingTitleComponent {

	public int set_for_what = 0;//设置什么
	public int icon_id = 0;//图标id
	public String title = "";//设置项标题
	public String title_hint = "";//设置项提示

	public boolean flagSelected = false;
}
