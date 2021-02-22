package net.dev.permissionsbungee.utils;

import java.io.File;
import java.io.IOException;

import net.dev.permissionsbungee.PermissionSystemBungee;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.config.*;

public class Utils {

	public File directory, file;
	
	public Utils() {
		PluginDescription desc = PermissionSystemBungee.getInstance().getDescription();
		
		directory = new File("plugins/" + desc.getName() + "/");
		file = new File(directory, "mysql.yml");
		
		if(!(directory.exists()))
			directory.mkdir();
		
		if(!(file.exists())) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		Configuration cfg = getConfig();
		addDefault(cfg, "MySQL.hostname", "localhost");
		addDefault(cfg, "MySQL.port", "3306");
		addDefault(cfg, "MySQL.database", "private");
		addDefault(cfg, "MySQL.username", "root");
		addDefault(cfg, "MySQL.password", "password");
		addDefault(cfg, "MySQL.password", "password");
		saveConfig(cfg);
	}
	
	public void saveConfig(Configuration cfg) {
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(cfg, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void addDefault(Configuration cfg, String path, Object value) {
		if(!(cfg.contains(path)))
			cfg.set(path, value);
	}

	public Configuration getConfig() {
		try {
			return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void sendConsole(String msg) {
		BungeeCord.getInstance().getConsole().sendMessage("§8[§3§lPermissionSystemBungee§8] " + msg);
	}

}
