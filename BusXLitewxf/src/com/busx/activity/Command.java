package com.busx.activity;

import java.util.ArrayList;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import com.busx.R;

/**
 * 定义图片按钮和菜单按钮 添加菜单
 * 
 * @author 
 * 
 */
public class Command {
	// fields
	public int cmdID = CMD_ID_NONE;
	public int cmdIcon = CMD_ICON_NONE;
	public String cmdName = "";

	// commands list
	private static ArrayList<Command> commands = new ArrayList<Command>();

	// commands
	public final static int CMD_ID_NONE = 0;
	public final static int CMD_ICON_NONE = 0;

	public final static int MENU_HOME = 1;
	public final static int MENU_ABOUT = 3;
	public final static int MENU_MORE = 5;
	public final static int MENU_CLEAR = 6;
	public final static int MENU_EXIT = 9;
	public final static int MENU_FAVOR = 10;
	public final static int MENU_VIEWMAP = 11;
	public final static int MENU_APPUPDATE = 15;
	public final static int MENU_WEATHER = 16;
	public final static int MENU_CHANGECITY = 17;
	public final static int MENU_TRANSFERINFO = 18;

	public final static int MENU_SEARCH = 20;
	public final static int MENU_ROUTE = 21;
	public final static int MENU_NEARBY = 22;
	public final static int MENU_LAYER = 23;
	

	// methods
	private Command(Context context, int cmdID, int idResName, int cmdIcon) {
		this.cmdID = cmdID;
		this.cmdName = context.getString(idResName);
		this.cmdIcon = cmdIcon;
	}

	public static void init(Context context) {
		//搜索
        commands.add(new Command(context, Command.MENU_SEARCH, R.string.menu_search, R.drawable.ic_menu_app_search ));
        //线路
        commands.add(new Command(context, Command.MENU_ROUTE, R.string.menu_route, R.drawable.ic_menu_app_nav ));
        //周边
        commands.add(new Command(context, Command.MENU_NEARBY, R.string.menu_nearby, R.drawable.icon_result_nearsearch ));
        //出行信息
        commands.add(new Command(context, Command.MENU_TRANSFERINFO, R.string.menu_traveltips, R.drawable.menu_help ));
        //清空
        commands.add(new Command(context, Command.MENU_CLEAR, R.string.menu_clear, R.drawable.ic_menu_app_del_rst ));
        
        /****更多*****/
        //检查更新
        commands.add(new Command(context, Command.MENU_APPUPDATE, R.string.menu_appupdate, R.drawable.ic_menu_appupdate ));
        //天气
        commands.add(new Command(context, Command.MENU_WEATHER, R.string.menu_weather, R.drawable.menu_help));
        //切换城市
        commands.add(new Command(context, Command.MENU_CHANGECITY, R.string.menu_changecity, R.drawable.menu_help ));
        //收藏
        commands.add(new Command(context, Command.MENU_FAVOR, R.string.menu_favor, R.drawable.menu_favor ));
        //图层
        commands.add(new Command(context, Command.MENU_LAYER, R.string.menu_layer, R.drawable.ic_menu_app_layers ));
        //关于我们
        commands.add(new Command(context, Command.MENU_ABOUT, R.string.menu_about, R.drawable.menu_about ));
        //退出
        commands.add(new Command(context, Command.MENU_EXIT, R.string.menu_exit, R.drawable.menu_exit ));
        
        

	}

	public static Command get(int cmd_id) {
		for (int i = 0; i < commands.size(); i++) {
			Command command = commands.get(i);
			if (command != null)
				if (command.cmdID == cmd_id)
					return command;
		}
		return null;
	}

	/**
	 * 获取名称
	 * 
	 * @param cmd_id
	 * @return
	 */
	public static String getName(int cmd_id) {
		for (int i = 0; i < commands.size(); i++) {
			Command command = commands.get(i);
			if (command != null)
				if (command.cmdID == cmd_id)
					return command.cmdName;
		}
		return "";
	}

	/**
	 * 获取图片
	 * 
	 * @param cmd_id
	 * @return
	 */
	public static int getIcon(int cmd_id) {
		for (int i = 0; i < commands.size(); i++) {
			Command command = commands.get(i);
			if (command != null)
				if (command.cmdID == cmd_id)
					return command.cmdIcon;
		}
		return CMD_ICON_NONE;
	}

	/**
	 * 添加菜单
	 * 
	 * @param menu
	 * @param cm_id
	 * @return
	 */
	public static MenuItem addMenuItem(Menu menu, int cm_id) {
		MenuItem item = menu.add(0, cm_id, Menu.NONE, Command.getName(cm_id));
		int cmd_icon = Command.getIcon(cm_id);
		if (item != null && cmd_icon != CMD_ICON_NONE)
			item.setIcon(cmd_icon);
		return item;

	}

	/**
	 * 添加子菜单
	 * 
	 * @param menu
	 * @param cm_id
	 * @param icon_res_id
	 * @return
	 */
	public static SubMenu addSubMenuItem(Menu menu, int cm_id, int icon_res_id) {
		SubMenu item = menu.addSubMenu(0, cm_id, Menu.NONE, Command
				.getName(cm_id));
		if (item != null)
			item.setIcon(icon_res_id);
		return item;
	}

}
