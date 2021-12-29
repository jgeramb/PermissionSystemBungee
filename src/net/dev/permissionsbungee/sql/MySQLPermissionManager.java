package net.dev.permissionsbungee.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MySQLPermissionManager {

	private MySQL mysql;
	private HashMap<SQLProperty, Object> values = new HashMap<>();
	
	public MySQLPermissionManager(MySQL mysql) {
		this.mysql = mysql;
	}

	public void collectUserData() {
		if(mysql.isConnected()) {
			values.put(SQLProperty.USER_PERMISSIONS, new HashMap<String, List<String>>());
			
			try {
				ResultSet resultSet = mysql.getResult("SELECT * FROM PermissionUsers");
				
				while(resultSet.next()) {
					String uuid = resultSet.getString("uuid");
					List<String> permissions = new ArrayList<>();
					String permissionString = resultSet.getString("permissions");
					
					if(!(permissionString.equalsIgnoreCase("[]"))){
						for (String string : permissionString.replace("[", "").replace("]", "").split(", "))
							permissions.add(string.trim());
					}
					
					((HashMap<String, List<String>>) values.get(SQLProperty.USER_PERMISSIONS)).put(uuid, permissions);
				}
				
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public List<String> getPlayerPermissions(String uuid) {
		return (values.containsKey(SQLProperty.USER_PERMISSIONS) ? ((HashMap<String, List<String>>) values.get(SQLProperty.USER_PERMISSIONS)).get(uuid) : new ArrayList<>());
	}
	
	public void collectGroupData() {
		if(mysql.isConnected()) {
			values.put(SQLProperty.GROUP_PERMISSIONS, new HashMap<String, List<String>>());
			values.put(SQLProperty.GROUP_MEMBERS, new HashMap<String, List<String>>());
			values.put(SQLProperty.GROUP_PARENT, new HashMap<String, String>());
			
			try {
				ResultSet resultSet = mysql.getResult("SELECT * FROM PermissionGroups");
				List<String> groupNames = new ArrayList<>();
				
				while(resultSet.next()) {
					String groupName = resultSet.getString("name");
					List<String> permissions = new ArrayList<>();
					List<String> members = new ArrayList<>();
					String permissionsString = resultSet.getString("permissions");
					String membersString = resultSet.getString("members");
					String parent = resultSet.getString("parent");
					
					if(!(permissionsString.equalsIgnoreCase("[]"))) {
						for (String string : permissionsString.replace("[", "").replace("]", "").split(", "))
							permissions.add(string.trim());
					}
					
					if(!(membersString.equalsIgnoreCase("[]"))) {
						for (String string : membersString.replace("[", "").replace("]", "").split(", "))
							members.add(string.trim());
					}
					
					((HashMap<String, List<String>>) values.get(SQLProperty.GROUP_PERMISSIONS)).put(groupName, permissions);
					((HashMap<String, List<String>>) values.get(SQLProperty.GROUP_MEMBERS)).put(groupName, members);
					((HashMap<String, String>) values.get(SQLProperty.GROUP_PARENT)).put(groupName, parent);
					
					groupNames.add(groupName);
				}
				
				values.put(SQLProperty.GROUP_NAMES, groupNames);
				
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public List<String> getGroupNames() {
		return (values.containsKey(SQLProperty.GROUP_NAMES) ? ((List<String>) values.get(SQLProperty.GROUP_NAMES)) : new ArrayList<>());
	}

	public List<String> getGroupPermissions(String groupName) {
		return (values.containsKey(SQLProperty.GROUP_PERMISSIONS) ? ((HashMap<String, List<String>>) values.get(SQLProperty.GROUP_PERMISSIONS)).get(groupName) : new ArrayList<>());
	}
	
	public List<String> getGroupMembers(String groupName) {
		return (values.containsKey(SQLProperty.GROUP_MEMBERS) ? ((HashMap<String, List<String>>) values.get(SQLProperty.GROUP_MEMBERS)).get(groupName) : new ArrayList<>());
	}

	public String getGroupParent(String groupName) {
		return (values.containsKey(SQLProperty.GROUP_PARENT) ? ((HashMap<String, String>) values.get(SQLProperty.GROUP_PARENT)).get(groupName) : null);
	}

	public boolean isGroupRegistered(String groupName) {
		return getGroupNames().contains(groupName);
	}
	
	public void setValues(HashMap<SQLProperty, Object> values) {
		this.values = values;
	}

}