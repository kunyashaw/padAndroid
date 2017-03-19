package com.huofu.RestaurantOS.ui.pannel.clientMenu;

import android.content.Context;
import android.os.Handler;

import com.huofu.RestaurantOS.support.greenDao.MenuDetail;
import com.huofu.RestaurantOS.support.greenDao.MenuDetailDao;
import com.huofu.RestaurantOS.utils.CommonUtils;
import com.huofu.RestaurantOS.utils.HandlerUtils;

import java.util.Calendar;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/***
 * 
 * @author kunyashaw
 * 
 *         菜单的增改删查
 * 
 */
public class MenuOperate {

	public static String tag = "MenuOperate";

	/**
	 * 检查本周菜单是否存在
	 * 
	 * @return
	 */
	public static boolean checkMenuThisWeek(MenuDetailDao menuDetailDao,Handler handler, int menu_type) 
	{

		String thisWeek[] = CommonUtils.getThisWeekDate();
		boolean flag = false;
		for (int i = 0; i < thisWeek.length; i++) 
		{
			CommonUtils.LogWuwei(tag, "正在检查本周菜单是否存在");
			QueryBuilder qb = menuDetailDao.queryBuilder();
			NewFromHistoryMenuActivity.menu_id_now_choose = Long.parseLong(thisWeek[i]+ menu_type);
			qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(NewFromHistoryMenuActivity.menu_id_now_choose)));
			List listResult = qb.list();

			QueryBuilder qbTmp = menuDetailDao.queryBuilder();
			qbTmp.where(MenuDetailDao.Properties.MenuDateId.eq(Long.toString(NewFromHistoryMenuActivity.menu_id_now_choose + 20000)));
			List listResultTmp = qbTmp.list();

			if (listResult.size() > 0) 
			{
				CommonUtils.LogWuwei(tag, "本周菜单存在");
				flag = true;

				// show_menu_mobile(NewFromHistoryMenuActivity.menu_id_now_choose);
				CommonUtils.sendMsg(Long.toString(NewFromHistoryMenuActivity.menu_id_now_choose), NewFromHistoryMenuActivity.SHOW_MENU, handler);
				return flag;
			}

			if (listResultTmp.size() > 0) 
			{
				flag = true;
				CommonUtils.sendMsg(Long.toString(NewFromHistoryMenuActivity.menu_id_now_choose + 20000), NewFromHistoryMenuActivity.SHOW_MENU, handler);
				return flag;
			}

		}
		return flag;

	}

	
	/**
	 * 检查下周次菜单是否存在
	 * 
	 * @return
	 */
	public static boolean checkMenuNextWeek(MenuDetailDao menuDetailDao,Handler handler, int menu_type) 
	{

		String nextWeek[] = CommonUtils.getNextWeekDate();

		boolean flag = false;
		for (int i = 0; i < nextWeek.length; i++) {
			CommonUtils.LogWuwei(tag, "正在检查下周菜单是否存在");
			QueryBuilder qb = menuDetailDao.queryBuilder();

			NewFromHistoryMenuActivity.menu_id_now_choose = Long.parseLong(nextWeek[i]
					+ menu_type);

			qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long
					.toString(NewFromHistoryMenuActivity.menu_id_now_choose)));
			List listResult = qb.list();

			QueryBuilder qbTmp = menuDetailDao.queryBuilder();
			qbTmp.where(MenuDetailDao.Properties.MenuDateId.eq(Long
					.toString(NewFromHistoryMenuActivity.menu_id_now_choose + 20000)));
			List listResultTmp = qbTmp.list();

			if (listResult.size() > 0) {
				CommonUtils.LogWuwei(tag, "下周菜单存在");
				flag = true;
				CommonUtils.sendMsg(
						Long.toString(NewFromHistoryMenuActivity.menu_id_now_choose),
						NewFromHistoryMenuActivity.SHOW_MENU, handler);
				return flag;
			}

			if (listResultTmp.size() > 0) {
				flag = true;
				CommonUtils
						.sendMsg(
								Long.toString(NewFromHistoryMenuActivity.menu_id_now_choose + 20000),
								NewFromHistoryMenuActivity.SHOW_MENU, handler);
				return flag;
			}

		}
		return flag;
	}

	
	/**
	 * 寻找最近两个月来最近一次有效菜单是否存在，存在返回那一周的周一的日期，否则返回0
	 */
	public static Long checkRecentEfficientData(MenuDetailDao menuDetailDao) {
		CommonUtils.LogWuwei(tag, "寻找最近两个月来最近一次有效菜单是否存在，存在返回那一周的周一的日期，否则返回0");
		long dateTmp[] = CommonUtils.checkRecentEfficientDataFromToday();
		long date[] = new long[8];

		for (int i = 0; i < dateTmp.length; i++) {
			date[i] = dateTmp[i] + 2000;
		}

		for (int i = 0; i < 8; i++) {
			QueryBuilder qb_tmp = menuDetailDao.queryBuilder();
			qb_tmp.where(MenuDetailDao.Properties.MenuDateId.eq(Long
					.toString(dateTmp[i] * 10)));
			List listResultTmp = qb_tmp.list();
			CommonUtils.LogWuwei(tag,
					"listResultTmp size is " + listResultTmp.size());

			QueryBuilder qb = menuDetailDao.queryBuilder();
			qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long
					.toString(date[i] * 10)));
			List listResult = qb.list();
			CommonUtils
					.LogWuwei(tag, "listResult size is " + listResult.size());

			if (listResult.size() > 0) {
				CommonUtils.LogWuwei(tag, "+++++++++++临时菜单存在");
				listResultTmp = listResult;
				dateTmp = date;
			} else {
				CommonUtils.LogWuwei(tag, "-----------临时菜单不存在");
			}

			// CommonUtils.LogWuwei(tag,"dateTmp:"+dateTmp[i]+"有菜单");

			if (listResultTmp.size() > 0)// 如果存在有效数据
			{
				CommonUtils.LogWuwei(tag, "最近两月存在有效数据,那周周一是:" + dateTmp[i]);
				return dateTmp[i];
			}
		}
		CommonUtils.LogWuwei(tag, "最近两个月不存在有效数据");
		return (long) 0;
	}

	
	/**
	 * 得到上周菜单每天菜单是否存在
	 */
	public static Boolean[] checkLastWeekMenuExists(MenuDetailDao menuDetailDao) {
		CommonUtils.LogWuwei(tag, "checkLastWeekMenuExists 检查上周菜单是否存在");
		Boolean flag = true;
		// 得到今天是工作日还是周末
		int today = CommonUtils.getIntWeekOfDate();// 获取今天周几
		String baseDate = "";// 初始化上周一的日期
		switch (today)// //得到上周一的日期
		{
		case 1:
			baseDate = CommonUtils.getDate(-7);
			break;
		case 2:
			baseDate = CommonUtils.getDate(-8);
			break;
		case 3:
			baseDate = CommonUtils.getDate(-9);
			break;
		case 4:
			baseDate = CommonUtils.getDate(-10);
			break;
		case 5:
			baseDate = CommonUtils.getDate(-11);
			break;
		case 6:
			baseDate = CommonUtils.getDate(-12);
			break;
		case 7:
			baseDate = CommonUtils.getDate(-13);
			break;
		}

		Boolean flagExists[] = { false, false, false, false, false, false,
				false };

		// 查看上周每天对应的menuTable表是否存在
		for (int i = 0; i < 7; i++) {
			QueryBuilder qb = menuDetailDao.queryBuilder();
			Long date = Long.parseLong(baseDate) + i;
			String str_date = Long.toString(date);
			qb.where(MenuDetailDao.Properties.MenuDateId.eq(str_date + "1"));
			List listResult = qb.list();

			CommonUtils.LogWuwei(tag, "-------size is " + listResult.size());
			if (listResult.size() > 0) {
				flagExists[i] = true;
				flag = true;
			}
		}
		if (!flag) {
			CommonUtils.LogWuwei(tag, "上周没有可用菜单，请选择添加-->新建菜单来添加菜单");
		} else {
			CommonUtils.LogWuwei(tag, "上周有可用菜单");
		}
		return flagExists;
	}

	
	/**
	 * 得到本周具体周几菜单存在
	 * 
	 * @return
	 */
	public static Boolean[] checkThisWeekMenuExists(MenuDetailDao menuDetailDao) {
		Boolean flag = false;
		CommonUtils.LogWuwei(tag, "检查本周菜单，得到有那些天有菜单");
		// 得到今天是工作日还是周末
		int today = CommonUtils.getIntWeekOfDate();// 获取今天周几

		String baseDate = "";// 初始化本周一的日期
		int step = 0;

		switch (today)// //得到本周一的日期
		{
		case 1:
			baseDate = CommonUtils.getDate(0);
			step = 0;
			break;
		case 2:
			baseDate = CommonUtils.getDate(-1);
			step = -1;
			break;
		case 3:
			baseDate = CommonUtils.getDate(-2);
			step = -2;
			break;
		case 4:
			baseDate = CommonUtils.getDate(-3);
			step = -3;
			break;
		case 5:
			baseDate = CommonUtils.getDate(-4);
			step = -4;
			break;
		case 6:
			baseDate = CommonUtils.getDate(-5);
			step = -5;
			break;
		case 7:
			baseDate = CommonUtils.getDate(-6);
			step = -6;
			break;
		}

		CommonUtils.LogWuwei(tag, "检查本周菜单 得到本周周一日期" + baseDate);

		Boolean flagExists[] = { false, false, false, false, false, false,
				false };

		Calendar c = Calendar.getInstance();

		String[] thisWeek = CommonUtils.getThisWeekDate();

		// 查看本周每天对应的menuTable表是否存在
		for (int i = 0; i < 7; i++) {
			QueryBuilder qb = menuDetailDao.queryBuilder();

			String str_date = thisWeek[i];

			qb.where(MenuDetailDao.Properties.MenuDateId.eq(str_date + "1"));

			List listResult = qb.list();

			CommonUtils.LogWuwei(tag, "str_date is " + str_date
					+ "-------size is " + listResult.size());
			if (listResult.size() > 0) {
				CommonUtils.LogWuwei(tag, "本周" + i + 1 + "有菜单");
				flagExists[i] = true;
				flag = true;
			} else {
				CommonUtils.LogWuwei(tag, "本周" + i + 1 + "没有菜单");
			}
		}
		// if(!flag)
		{
			// CommonUtils.LogWuwei(tag, "本周没有可用菜单，请选择添加-->新建菜单来添加菜单");
			// finish();
		}
		return flagExists;
	}

	
	/**
	 * 根据检查本周菜单方法checkMenuThisWeek返回的结果对要显示的本周数据进行处理
	 * 如果flag为true，说明本周菜单已经存在，方法可以结束 如果flag为false，说明本周菜单不存在，接下来就去检查上周菜单是否存在，存在就
	 * 将上周菜单拷贝到本周，上周菜单不存在的话，会去检查从本周一前2个月（8周）是否存在
	 * 有效菜单，如果存在，就拷贝距离本周最近的一周菜单，否则创建空白菜单（只有主题）
	 * 
	 */
	public static void dataDealThisWeek(Context ctxt,MenuDetailDao menuDetailDao, Handler handler, boolean flag) {
		if (flag)// 如果菜单已经存在
		{
			CommonUtils.LogWuwei(tag, "dataDealThisWeek 菜单已经存在");
			return;
		} else// 如果本周菜单不存在
		{
			// 检查上周菜单是否存在，如果存在，拷贝过来；如果不存在，寻找最近一次有效数据并拷贝过来。如果两者都不存在，则创建空白菜单。
			Boolean flag_last[] = checkLastWeekMenuExists(menuDetailDao);
			if (flag_last[0])// 如果上周菜单存在
			{
				CommonUtils.LogWuwei(tag, "上周菜单已经存在");
				HandlerUtils.showToast(ctxt, "正在拷贝上周菜单到本周，请稍候");
				addToThisWeekAccordingLastWeek(menuDetailDao);
			} else// 寻找最近一次有效数据
			{
				CommonUtils.LogWuwei(tag, "上周菜单不存在，准备查询近两个月有没有可用菜单");
				long flag_recent = checkRecentEfficientData(menuDetailDao);
				if (flag_recent == 0)// 如果最近两个月都不存在有效数据
				{
					CommonUtils.LogWuwei(tag, "最近两个月都不存在有效数据");
					CommonUtils.sendMsg("0",
							NewFromHistoryMenuActivity.SHOW_DIALOG_LOCAL_NO_MENUS,
							handler);
				} else// 将有效数据拷贝到本周
				{
					CommonUtils.LogWuwei(tag, "将有效数据拷贝到本周");
					copyMenuFromRecent(menuDetailDao, flag_recent);
				}
			}
		}
	}

	
	/**
	 * 根据检查下周菜单方法checkMenuNextWeek返回的结果对要显示的下周数据进行处理。
	 * 如果flag为true，说明下周菜单已经存在，方法可以结束 如果flag为false，说明下周菜单不存在，接下来就去检查本周菜单是否存在，存在就
	 * 将本周菜单拷贝到下周，本周菜单不存在的话，会去检查从下周一前2个月（8周）是否存在
	 * 有效菜单，如果存在，就拷贝距离下周最近的一周菜单，否则创建空白菜单（只有主题）
	 * 
	 */
	public static void dataDealNextWeek(Context ctxt, MenuDetailDao menuDetailDao,Handler handler, boolean flag) 
	{
		if (flag)// 如果菜单已经存在
		{
			CommonUtils.LogWuwei(tag, "dataDealNextWeek 下周菜单已经存在");
			return;
		} else// 如果下周菜单不存在
		{
			// 检查本周菜单是否存在，如果存在，拷贝过来；如果不存在，寻找最近一次有效数据并拷贝过来。如果两者都不存在，则创建空白菜单。
			Boolean flag_this[] = checkThisWeekMenuExists(menuDetailDao);
			Boolean flag_this_week_exist = false;
			for (int i = 0; i < flag_this.length; i++) {
				if (flag_this[i]) {
					flag_this_week_exist = true;
					break;
				}

			}
			if (flag_this_week_exist)// 如果本周菜单存在
			{
				CommonUtils.LogWuwei(tag, "本周菜单已经存在");
				HandlerUtils.showToast(ctxt, "正在拷贝本周菜单到下周，请稍候");
				addToNextWeekAccordingThisWeek(menuDetailDao);
			} else// 寻找最近一次有效数据
			{
				CommonUtils.LogWuwei(tag, "本周菜单不存在");

				long flag_recent = checkRecentEfficientData(menuDetailDao);

				if (flag_recent == 0)// 如果最近两个月都不存在有效数据
				{
					CommonUtils.LogWuwei(tag, "最近两个月都没有菜单");
					CommonUtils.sendMsg("1",
							NewFromHistoryMenuActivity.SHOW_DIALOG_LOCAL_NO_MENUS,
							handler);
				} else// 将有效数据拷贝到本周
				{
					CommonUtils.LogWuwei(tag, "最近两个月有菜单，那一周的周一是" + flag_recent);
					copyMenuFromRecent(menuDetailDao, flag_recent);
				}
			}
		}
	}

	
	/**
	 * 根据flag（0 1）创建一周的空白菜单（上周 下周）
	 * 
	 * @param flag
	 */
	public static void createNullMenu(int flag, MenuDetailDao menuDetailDao) {

		// 最近2个月都没有菜单，提示是创建一周的空白菜单还是从模板加载

		long monday = (long) 0;
		if (flag == 0)// 创建本周所有的空白菜单
		{
			monday = CommonUtils.getThisMonday();
		} else if (flag == 1)// 创建下周所有的空白菜单
		{
			monday = CommonUtils.getNextMonday();
		}

		for (int i = 0; i < 7; i++) {
			for (int k = 0; k < 3; k++) {
				MenuDetail menu_detail_entity = new MenuDetail();
				menu_detail_entity.setMenuDateId(Long.toString(monday + i) + k);
				menu_detail_entity.setAddtiontal("");
				menu_detail_entity.setDistance((double) 0);
				menu_detail_entity.setBackgroundColor("0x0000");
				menu_detail_entity.setFontColor("#ffffffff");
				menu_detail_entity.setFontSize(45);
				menu_detail_entity.setPrice(0);
				menu_detail_entity.setX((double) 0);
				menu_detail_entity.setY((double) 0);
				menu_detail_entity.setWidgetId(0);
				menu_detail_entity.setName("五味,放心吃");
				menu_detail_entity.setType("theme");
				menu_detail_entity.setMenuId((long) 0);

				menuDetailDao.insertOrReplace(menu_detail_entity);
				CommonUtils.LogWuwei(tag, "创建空白菜单" + Long.toString(monday + i)
						+ k);
			}
		}

	}

	
	public static void loadModule(final int flag, final Handler handler) {
		if (flag == 0) {
			CommonUtils.LogWuwei(tag, "从模板加载数据到本周中....");
		} else {
			CommonUtils.LogWuwei(tag, "从模板加载数据到下周中....");
		}
		ClientMenu.whetherUpdateAll = true;
		ClientMenu.updateDayDate(flag);
		ClientMenu.flag_load_module_new_menu = true;
		

		new Thread() {
			public void run() {
				try {
					CommonUtils.sendMsg("1", NewFromHistoryMenuActivity.UPDATE_PROGRESS_BAR, handler);
					Thread.sleep(10);// 1

					ClientMenu.makeMondayBreakfastMenuJson(1);// 2
					CommonUtils.sendMsg("2", NewFromHistoryMenuActivity.UPDATE_PROGRESS_BAR, handler);
					Thread.sleep(10);

					ClientMenu.makeTuesdayBreakfastMenuJson(1);// 3
					CommonUtils.sendMsg("3", NewFromHistoryMenuActivity.UPDATE_PROGRESS_BAR, handler);
					Thread.sleep(10);

					ClientMenu.makeWednesdayBreakfastMenuJson(1);// 4
					CommonUtils.sendMsg("4", NewFromHistoryMenuActivity.UPDATE_PROGRESS_BAR, handler);
					Thread.sleep(10);

					ClientMenu.makeThursdayBreakfastMenuJson(1);// 5
					CommonUtils.sendMsg("5", NewFromHistoryMenuActivity.UPDATE_PROGRESS_BAR, handler);
					Thread.sleep(10);

					ClientMenu.makeFridayBreakfastMenuJson(1);// 6
					CommonUtils.sendMsg("6", NewFromHistoryMenuActivity.UPDATE_PROGRESS_BAR, handler);
					Thread.sleep(10);

					ClientMenu.makeLaunchMenuJson(1, 1);// 7
					CommonUtils.sendMsg("7", NewFromHistoryMenuActivity.UPDATE_PROGRESS_BAR, handler);
					Thread.sleep(10);

					ClientMenu.makeLaunchMenuJson(2, 1);// 8
					CommonUtils.sendMsg("8", NewFromHistoryMenuActivity.UPDATE_PROGRESS_BAR, handler);
					Thread.sleep(10);

					ClientMenu.makeLaunchMenuJson(3, 1);// 9
					CommonUtils.sendMsg("9", NewFromHistoryMenuActivity.UPDATE_PROGRESS_BAR, handler);
					Thread.sleep(10);

					ClientMenu.makeLaunchMenuJson(4, 1);// 10
					CommonUtils.sendMsg("10", NewFromHistoryMenuActivity.UPDATE_PROGRESS_BAR, handler);
					Thread.sleep(10);

					ClientMenu.makeLaunchMenuJson(5, 1);// 11
					CommonUtils.sendMsg("11", NewFromHistoryMenuActivity.UPDATE_PROGRESS_BAR, handler);
					Thread.sleep(10);

					ClientMenu.makeDinnerMenuJson(1, 1);// 12
					CommonUtils.sendMsg("12", NewFromHistoryMenuActivity.UPDATE_PROGRESS_BAR, handler);
					Thread.sleep(10);

					ClientMenu.makeDinnerMenuJson(2, 1);// 13
					CommonUtils.sendMsg("13", NewFromHistoryMenuActivity.UPDATE_PROGRESS_BAR, handler);
					Thread.sleep(10);

					ClientMenu.makeDinnerMenuJson(3, 1);// 14
					CommonUtils.sendMsg("14", NewFromHistoryMenuActivity.UPDATE_PROGRESS_BAR, handler);
					Thread.sleep(10);

					ClientMenu.makeDinnerMenuJson(4, 1);// 15
					CommonUtils.sendMsg("15", NewFromHistoryMenuActivity.UPDATE_PROGRESS_BAR, handler);
					Thread.sleep(10);

					ClientMenu.makeDinnerMenuJson(5, 1);// 16
					CommonUtils.sendMsg("16", NewFromHistoryMenuActivity.UPDATE_PROGRESS_BAR, handler);
					Thread.sleep(10);

					int step = CommonUtils.getIntWeekOfDate();
					if (flag == 0) {
						NewFromHistoryMenuActivity.menu_id_now_choose = Long
								.parseLong(CommonUtils.getDate(1 - step));
						CommonUtils.LogWuwei(tag, "本周一是"
								+ NewFromHistoryMenuActivity.menu_id_now_choose);
					} else if (flag == 1) {
						NewFromHistoryMenuActivity.menu_id_now_choose = Long
								.parseLong(CommonUtils.getDate(8 - step));
						CommonUtils.LogWuwei(tag, "下周一是"
								+ NewFromHistoryMenuActivity.menu_id_now_choose);
					}

					CommonUtils.sendMsg("",
							NewFromHistoryMenuActivity.UPDATE_MENU_MOBILE_DEFAULT,
							handler);
					CommonUtils.LogWuwei(tag, "完事了，准备显示周一早餐的菜单");

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ClientMenu.whetherUpdateAll = false;
			}
		}.start();

	}

	
	/**
	 * 根据得到的最近两个月内最近一个周一
	 */
	public static void copyMenuFromRecent(MenuDetailDao menuDetailDao,long monday) 
	{

		long weekDate[] = { monday, monday + 1, monday + 2, monday + 3,
				monday + 4, monday + 5, monday + 6 };
		String this_week_date[] = CommonUtils.getThisWeekDate();
		for (int i = 0; i < 7; i++) {
			for (int k = 0; k < 3; k++) {
				QueryBuilder qb = menuDetailDao.queryBuilder();
				qb.where(MenuDetailDao.Properties.MenuDateId.eq(Long
						.toString(weekDate[i]) + Integer.toString(k)));
				List listResult = qb.list();
				if (listResult.size() > 0)//
				{
					QueryBuilder new_qb = menuDetailDao.queryBuilder();
					new_qb.where(MenuDetailDao.Properties.MenuDateId
							.eq(this_week_date[i] + Integer.toString(k)));
					List newListResult = new_qb.list();
					CommonUtils.LogWuwei(tag, "拷贝" + this_week_date[i]
							+ "菜单中...");
					for (int p = 0; p < listResult.size(); p++) {
						MenuDetail menu_detail_entity = (MenuDetail) listResult
								.get(k);
						MenuDetail new_menu_detail_entity = menu_detail_entity;
						new_menu_detail_entity.setMenuDateId(this_week_date[i]
								+ Integer.toString(p));
						menuDetailDao.insert(new_menu_detail_entity);
						CommonUtils.LogWuwei(tag, "date is "
								+ this_week_date[i] + Integer.toString(p)
								+ "data inserting....");
					}
				}
			}
		}
	}

	
	/**
	 * 将上周有效的菜单拷贝到本周
	 */
	public static void copyDataToThisWeek(MenuDetailDao menuDetailDao,Boolean flag[]) {

		String last_week_date[] = CommonUtils.getLastWeekDate();
		String this_week_date[] = CommonUtils.getThisWeekDate();

		for (int i = 0; i < flag.length; i++)// 遍历一周
		{
			// if(flag[i] == true)
			{
				int j = i + 1;
				CommonUtils.LogWuwei(tag, "上周" + j + "的菜单存在，准备拷贝到本周" + j);
				for (int p = 0; p < 3; p++)// 遍历每天的菜单
				{
					// 查看对应的menuTable表是否存在
					QueryBuilder qb = menuDetailDao.queryBuilder();
					qb.where(MenuDetailDao.Properties.MenuDateId
							.eq(last_week_date[i] + Integer.toString(p)));
					List listResult = qb.list();

					QueryBuilder qbTmp = menuDetailDao.queryBuilder();
					qbTmp.where(MenuDetailDao.Properties.MenuDateId.eq(Long
							.toString(Long.parseLong(last_week_date[i]) * 10
									+ p + 20000)));
					List listResultTmp = qbTmp.list();

					if (listResultTmp.size() > 0) {
						listResult = listResultTmp;
					}

					if (listResult.size() > 0)// 如果上周菜单存在
					{
						QueryBuilder new_qb = menuDetailDao.queryBuilder();
						new_qb.where(MenuDetailDao.Properties.MenuDateId
								.eq(this_week_date[i] + Integer.toString(p)));
						List newListResult = new_qb.list();
						if (newListResult.size() == 0)// 如果对应的本周菜单不存在
						{
							CommonUtils.LogWuwei(tag, "拷贝" + this_week_date[i]
									+ "菜单中...");
							for (int k = 0; k < listResult.size(); k++) {

								MenuDetail menu_detail_entity = (MenuDetail) listResult
										.get(k);
								MenuDetail new_menu_detail_entity = menu_detail_entity;
								new_menu_detail_entity
										.setMenuDateId(this_week_date[i]
												+ Integer.toString(p));

								menuDetailDao.insert(new_menu_detail_entity);
								CommonUtils.LogWuwei(tag,
										"date is " + this_week_date[i]
												+ Integer.toString(p)
												+ "data inserting....");
							}
						} else {
							CommonUtils.LogWuwei(tag, "对应的本周" + j + "菜单已经存在");
						}

					}

				}
			}
		}
	}

	
	
	/**
	 * 将本周菜单拷贝到下周菜单
	 * 
	 * @param flag
	 */
	public static void copyDataToNextWeek(MenuDetailDao menuDetailDao,Boolean flag[]) {
		String this_week_date[] = CommonUtils.getThisWeekDate();
		String next_week_date[] = CommonUtils.getNextWeekDate();

		for (int i = 0; i < flag.length; i++)// 遍历一周
		{
			// if(flag[i] == true)
			{
				int j = i + 1;
				CommonUtils.LogWuwei(tag, "本周" + j + "的菜单存在，准备拷下到本周" + j);
				for (int p = 0; p < 3; p++)// 遍历每天的菜单
				{
					// 查看对应的menuTable表是否存在
					QueryBuilder qb = menuDetailDao.queryBuilder();
					qb.where(MenuDetailDao.Properties.MenuDateId
							.eq(this_week_date[i] + Integer.toString(p)));
					List listResult = qb.list();

					// 查看对应的menuTable表是否存在
					QueryBuilder qbTmp = menuDetailDao.queryBuilder();
					qbTmp.where(MenuDetailDao.Properties.MenuDateId.eq(Long
							.toString(Long.parseLong(this_week_date[i]) * 10
									+ p + 20000)));
					List listResultTmp = qbTmp.list();
					if (listResultTmp.size() > 0) {
						// CommonUtils.LogWuwei(tag,
						// "本周存在临时数据....tmpDate is "+(Long.toString(Long.parseLong(this_week_date[i])*10+p+20000)));
						listResult = listResultTmp;
					} else {
						// CommonUtils.LogWuwei(tag,
						// "本周不存在临时数据。。。。tmpDate is "+(Long.toString(Long.parseLong(this_week_date[i])*10+p+20000)));
					}

					if (listResult.size() > 0)// 如果本周菜单存在
					{
						QueryBuilder new_qb = menuDetailDao.queryBuilder();
						new_qb.where(MenuDetailDao.Properties.MenuDateId
								.eq(next_week_date[i] + Integer.toString(p)));
						List newListResult = new_qb.list();
						if (newListResult.size() == 0)// 如果对应的本周菜单不存在
						{
							CommonUtils.LogWuwei(tag,
									"————————————————————————————拷贝"
											+ next_week_date[i] + "菜单中...");
							for (int k = 0; k < listResult.size(); k++) {
								MenuDetail menu_detail_entity = (MenuDetail) listResult
										.get(k);
								MenuDetail new_menu_detail_entity = menu_detail_entity;
								new_menu_detail_entity
										.setMenuDateId(next_week_date[i]
												+ Integer.toString(p));
								menuDetailDao.insert(new_menu_detail_entity);
								CommonUtils.LogWuwei(tag,
										"date is " + this_week_date[i]
												+ Integer.toString(p)
												+ "data inserting....");
							}
						} else {
							CommonUtils.LogWuwei(tag,
									"——————————————————————————————对应的下周" + j
											+ "菜单已经存在，不用拷贝了");
						}

					}

				}
			}
		}
	}

	
	
	/**
	 * 将上周菜单拷贝到本周
	 */
	public static void addToThisWeekAccordingLastWeek(MenuDetailDao menuDetailDao) 
	{
		// 确定上周菜单存在，并对异常做处理
		CommonUtils.LogWuwei(tag, "确定上周菜单存在，并对异常做处理");
		Boolean flag[] = checkLastWeekMenuExists(menuDetailDao);

		// 将上周存在的菜单拷贝为本周对应的菜单
		copyDataToThisWeek(menuDetailDao, flag);
		CommonUtils.LogWuwei(tag, "将上周存在的菜单拷贝为本周对应的菜单");
		// 拷贝结束后，如果上周的今天有数据，默认显示出来；否则就显示有数据的最前面那一天
		// show_default_menu(flag,1);

	}

	
	/**
	 * 将本周菜单拷贝到下周
	 */
	public static void addToNextWeekAccordingThisWeek(MenuDetailDao menuDetailDao) 
	{

		// 确定本周菜单存在，并对异常做处理
		CommonUtils.LogWuwei(tag, "确定本周菜单存在，并对异常做处理");
		Boolean flag[] = checkThisWeekMenuExists(menuDetailDao);

		for (int i = 0; i < flag.length; i++) {
			CommonUtils.LogWuwei(tag, "flag[" + i + "] is " + flag[i]);
		}

		// 将本周存在的菜单拷贝为下周对应的菜单
		CommonUtils.LogWuwei(tag, "将本周存在的菜单拷贝为下周对应的菜单");
		copyDataToNextWeek(menuDetailDao, flag);

		// 拷贝结束后，如果上周的今天有数据，默认显示出来；否则就显示有数据的最前面那一天
		CommonUtils.LogWuwei(tag, "拷贝结束后，如果上周的今天有数据，默认显示出来；否则就显示有数据的最前面那一天");
		// show_default_menu(flag,0);
	}

	
	/**
	 * 显示默认菜单
	 * 
	 * @param flag
	 *            ：哪一天有菜单
	 * @param light
	 *            ：也是个标志位，为1则显示本周，为0显示下周
	 */
	public static void show_default_menu(Boolean flag[], int light,Handler handler) 
	{
		long menu_index = 0;
		if (light == 1)// 准备显示本周菜单
		{
			int day_of_week = CommonUtils.getIntWeekOfDate();// 得到今天是周几
			int show_day_of_week = 0;// 需要处理的本周有菜单的第一天

			CommonUtils.LogWuwei(tag, "day_of_week is " + day_of_week);

			if (flag[day_of_week - 1] == true)// 如果今天有菜单，默认显示今天的菜单
			{
				show_day_of_week = day_of_week;
			} else// 如果今天没有菜单可以显示，那么就从周一开始遍历，找到有菜单的为止
			{
				for (int i = 0; i < 7; i++)// 从周一到周日开始寻找
				{
					CommonUtils.LogWuwei(tag, "flag[" + i + "] is " + flag[i]);
					if (flag[i] == true) {
						show_day_of_week = i;
						CommonUtils.LogWuwei(tag, "show_day_of_week is " + i);
						break;
					}
				}
			}
			menu_index = Long.parseLong(CommonUtils.getDate(show_day_of_week
					- day_of_week)
					+ "1");
			CommonUtils.LogWuwei(tag, "menu_index is " + menu_index);
		} else// 准备显示下周菜单
		{
			String nextweek[] = CommonUtils.getNextWeekDate();
			menu_index = Long.parseLong(nextweek[0] + "1");
			CommonUtils.LogWuwei(tag, "menu_index is " + menu_index);
		}

		CommonUtils.sendMsg(Long.toString(menu_index),
				NewFromHistoryMenuActivity.SHOW_MENU, handler);
	}


}
