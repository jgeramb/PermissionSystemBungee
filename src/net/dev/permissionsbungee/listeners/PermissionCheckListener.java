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
	public void onPermissionCheck(PermissionCheckEvent e) {
		MySQLPermissionManager mysqlPermissionManager = PermissionSystemBungee.getInstance().getMySQLPermissionManager();
		
		if(e.getSender() instanceof ProxiedPlayer) {
			ProxiedPlayer p = (ProxiedPlayer) e.getSender();
			String uuid = p.getUniqueId().toString();
			String permission = e.getPermission().toLowerCase();
			List<String> playerPermissions = mysqlPermissionManager.getPlayerPermissions(uuid);
			
			if(playerPermissions != null) {
				for (String playerPermission : playerPermissions) {
					if(playerPermission.equalsIgnoreCase("-" + permission)) {
						e.setHasPermission(false);
						return;
					} else if(playerPermission.equalsIgnoreCase(permission) || playerPermission.equalsIgnoreCase("*"))
						e.setHasPermission(true);
				}
			}
			
			for (String groupName : mysqlPermissionManager.getGroupNames()) {
				if(mysqlPermissionManager.getGroupMembers(groupName).contains(uuid)) {
					String parent = groupName;
					
					while((parent = mysqlPermissionManager.getGroupParent(parent)) != null) {
						List<String> permissions = mysqlPermissionManager.getGroupPermissions(parent);
						
						if(permissions != null) {
							for (String groupPermission : permissions) {
								if(groupPermission.equalsIgnoreCase("-" + permission)) {
									e.setHasPermission(false);
									return;
								} else if(groupPermission.equalsIgnoreCase(permission) || groupPermission.equalsIgnoreCase("*"))
									e.setHasPermission(true);
							}
						} else
							break;
					}
					
					if(permission.equalsIgnoreCase("group." + groupName))
						e.setHasPermission(true);
					
					for (String groupPermission : mysqlPermissionManager.getGroupPermissions(groupName)) {
						if(groupPermission.equalsIgnoreCase("-" + permission)) {
							e.setHasPermission(false);
							return;
						} else if(groupPermission.equalsIgnoreCase(permission) || groupPermission.equalsIgnoreCase("*"))
							e.setHasPermission(true);
					}
				}
			}
		}
	}

}
