package com.huofu.RestaurantOS.utils;


public class StringSort  implements Comparable<StringSort>
{
	public String s;//包装String
	 
	public StringSort(String s) 
	{
		this.s = s;
	}

 @Override
 	public int compareTo(StringSort o) 
 {
  if(o==null||o.s==null) return 1;
  if(s.length()>o.s.length()) return 1;
  else if(s.length()<o.s.length()) return -1;
  return s.compareTo(o.s);
 }
}