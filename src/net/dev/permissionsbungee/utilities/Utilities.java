package net.dev.permissionsbungee.utilities;

import java.io.File;
import java.io.IOException;

import net.dev.permissionsbungee.PermissionSystemBungee;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.config.*;

public class Utilities {

	public File directory, file;
	
	public Utilities() {
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
		
		Configuration cfg = getConfiguration();
		addDefault(cfg, "MySQL.hostname", "localhost");
		addDefault(cfg, "MySQL.port", "3306");
		addDefault(cfg, "MySQL.database", "private");
		addDefault(cfg, "MySQL.username", "root");
		addDefault(cfg, "MySQL.password", "password");
		addDefault(cfg, "MySQL.password", "password");
		saveConfig(cfg);
	}
	
	public void saveConfig(Configuration configuration) {
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void addDefault(Configuration configuration, String path, Object value) {
		if(!(configuration.contains(path)))
			configuration.set(path, value);
	}

	public Configuration getConfiguration() {
		try {
			return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void sendConsole(String msg) {
		BungeeCord.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText("§8» §3System §7┃ " + msg));
	}

}
