package net.dev.permissionsbungee;

import java.util.*;

import net.dev.permissionsbungee.listeners.PermissionCheckListener;
import net.dev.permissionsbungee.sql.MySQL;
import net.dev.permissionsbungee.sql.MySQLPermissionManager;
import net.dev.permissionsbungee.utils.Utils;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

public class PermissionSystemBungee extends Plugin {

	public static PermissionSystemBungee instance;

	public static PermissionSystemBungee getInstance() {
		return instance;
	}
	
	public MySQL mysql;
	private Timer t;
	
	private Utils utils;
	private MySQLPermissionManager mysqlPermissionManager;
	
	@Override
	public void onEnable() {
		instance = this;
		
		utils = new Utils();
		
		Configuration cfg = utils.getConfig();
		
		mysql = new MySQL(cfg.getString("MySQL.hostname"), cfg.getString("MySQL.port"), cfg.getString("MySQL.database"), cfg.getString("MySQL.username"), cfg.getString("MySQL.password"));
		mysql.connect();
		
		mysqlPermissionManager = new MySQLPermissionManager(mysql);

		t = new Timer();
		t.schedule(new TimerTask() {
			
			@Override
			public void run() {
				mysqlPermissionManager.setValues(new HashMap<>());
				mysqlPermissionManager.collectUserData();
				mysqlPermissionManager.collectGroupData();
			}
		}, 0, 2500);
		
		BungeeCord.getInstance().getPluginManager().registerListener(this, new PermissionCheckListener());
		
		utils.sendConsole("§eThe system has been enabled§7!");
	}
	
	@Override
	public void onDisable() {
		t.cancel();
		mysql.disconnect();
		
		utils.sendConsole("§cThe system has been disabled§7!");
	}
	
	public Utils getUtils() {
		return utils;
	}
	
	public MySQLPermissionManager getMySQLPermissionManager() {
		return mysqlPermissionManager;
	}

}
