package net.dev.permissionsbungee;

import java.util.*;

import net.dev.permissionsbungee.listeners.PermissionCheckListener;
import net.dev.permissionsbungee.sql.MySQL;
import net.dev.permissionsbungee.sql.MySQLPermissionManager;
import net.dev.permissionsbungee.utilities.Utilities;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

public class PermissionSystemBungee extends Plugin {

	public static PermissionSystemBungee instance;

	public static PermissionSystemBungee getInstance() {
		return instance;
	}
	
	public MySQL mysql;
	private Timer timer;
	
	private Utilities utilities;
	private MySQLPermissionManager mysqlPermissionManager;
	
	@Override
	public void onEnable() {
		instance = this;
		
		utilities = new Utilities();
		
		Configuration configuration = utilities.getConfiguration();
		
		mysql = new MySQL(configuration.getString("MySQL.hostname"), configuration.getString("MySQL.port"), configuration.getString("MySQL.database"), configuration.getString("MySQL.username"), configuration.getString("MySQL.password"));
		mysql.connect();
		
		mysqlPermissionManager = new MySQLPermissionManager(mysql);

		timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				mysqlPermissionManager.setValues(new HashMap<>());
				mysqlPermissionManager.collectUserData();
				mysqlPermissionManager.collectGroupData();
			}
		}, 0, 2500);
		
		BungeeCord.getInstance().getPluginManager().registerListener(this, new PermissionCheckListener());
		
		utilities.sendConsole("§eThe system has been enabled§7!");
	}
	
	@Override
	public void onDisable() {
		timer.cancel();
		mysql.disconnect();
		
		utilities.sendConsole("§cThe system has been disabled§7!");
	}
	
	public Utilities getUtils() {
		return utilities;
	}
	
	public MySQLPermissionManager getMySQLPermissionManager() {
		return mysqlPermissionManager;
	}

}
