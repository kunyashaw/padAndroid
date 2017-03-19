package com.huofu.RestaurantOS.bean.user;

/*"delivery_staff": {
    "name": "eric",
    "user_id": 1000008,
    "staff_id": 1000005,
    "head": "http://wx.qlogo.cn/mmopen/PiajxSqBRaEKBxYbw9kL5Sh7PHibNDHXiaKFIhbm9ZsfESib0hQxNkU6pZOCpLg7cDoNibTXiaBBCJtJ9N8lVhIFUg4Q/0",
    "merchant_id": 26
},*/

public class staff {
	
	public String name = "";
	public String head = "";
	public String alias_name = "";
	
	public int meachant_id = -1;
	
	public long user_id = -1;
	public int staff_id = 1;
	
	public boolean flagStaffChoosen = false;
	
}
