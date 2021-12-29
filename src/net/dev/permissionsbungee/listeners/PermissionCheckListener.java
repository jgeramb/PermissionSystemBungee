package net.dev.permissionsbungee.listeners;

import java.util.List;

import net.dev.permissionsbungee.PermissionSystemBungee;
import net.dev.permissionsbungee.sql.MySQLPermissionManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PermissionCheckListener implements Listener {

	@EventHandler
	public void onPermissionCheck(PermissionCheckEvent event) {
		MySQLPermissionManager mysqlPermissionManager = PermissionSystemBungee.getInstance().getMySQLPermissionManager();
		
		if(event.getSender() instanceof ProxiedPlayer) {
			ProxiedPlayer proxiedPlayer = (ProxiedPlayer) event.getSender();
			String uniqueId = proxiedPlayer.getUniqueId().toString();
			String permission = event.getPermission().toLowerCase();
			List<String> playerPermissions = mysqlPermissionManager.getPlayerPermissions(uniqueId);
			
			if(playerPermissions != null) {
				for (String playerPermission : playerPermissions) {
					if(playerPermission.equalsIgnoreCase("-" + permission)) {
						event.setHasPermission(false);
						return;
					} else if(playerPermission.equalsIgnoreCase(permission) || playerPermission.equalsIgnoreCase("*"))
						event.setHasPermission(true);
				}
			}
			
			for (String groupName : mysqlPermissionManager.getGroupNames()) {
				if(mysqlPermissionManager.getGroupMembers(groupName).contains(uniqueId)) {
					String parent = groupName;
					
					while((parent = mysqlPermissionManager.getGroupParent(parent)) != null) {
						List<String> permissions = mysqlPermissionManager.getGroupPermissions(parent);
						
						if(permissions != null) {
							for (String groupPermission : permissions) {
								if(groupPermission.equalsIgnoreCase("-" + permission)) {
									event.setHasPermission(false);
									return;
								} else if(groupPermission.equalsIgnoreCase(permission) || groupPermission.equalsIgnoreCase("*"))
									event.setHasPermission(true);
							}
						} else
							break;
					}
					
					if(permission.equalsIgnoreCase("group." + groupName))
						event.setHasPermission(true);
					
					for (String groupPermission : mysqlPermissionManager.getGroupPermissions(groupName)) {
						if(groupPermission.equalsIgnoreCase("-" + permission)) {
							event.setHasPermission(false);
							return;
						} else if(groupPermission.equalsIgnoreCase(permission) || groupPermission.equalsIgnoreCase("*"))
							event.setHasPermission(true);
					}
				}
			}
		}
	}

}
