package org.fightidiocy.CraftZ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.sql.Connection; 
import java.sql.DriverManager;  
import java.sql.PreparedStatement;
import java.sql.ResultSet;  
import java.sql.Statement; 
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

public class Regions {
	ArrayList<Region> regions;
	String _databaseFile;
	
	public Regions (String databaseFile, JavaPlugin jp)
	{
		regions = new ArrayList<Region>();
		Connection connection = null;  
        ResultSet resultSet = null;  
        Statement statement = null;
        _databaseFile = databaseFile;
        
        try {  
            Class.forName("org.sqlite.JDBC");  
            connection = DriverManager  
                    .getConnection("jdbc:sqlite:" + databaseFile);  
            statement = connection.createStatement();  
            resultSet = statement  
                    .executeQuery("SELECT * FROM Regions");  
            while (resultSet.next()) {  
            	Region rg = new Region(resultSet.getInt("Id"), resultSet.getString("RegionName"), resultSet.getInt("StartX"), resultSet.getInt("StartY"), resultSet.getInt("StartZ"), resultSet.getInt("EndX"), resultSet.getInt("EndY"), resultSet.getInt("EndZ"));
                
            	PreparedStatement metaState = connection.prepareStatement("SELECT * FROM RegionMetaData WHERE RegionId = ?");
            	metaState.setInt(1, rg.getId());
            	
            	ResultSet metadata = metaState.executeQuery();          	
            	
            	while (metadata.next()) {
            		rg.addMetaData(metadata.getString("MetaKey"), metadata.getString("MetaData"));
            	}
            	
            	regions.add(rg);
            }  
        } catch (Exception e) {  
             jp.getServer().broadcast(e.getStackTrace().toString(), Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
        } finally {  
            try {  
                resultSet.close();  
                statement.close();  
                connection.close();  
            } catch (Exception e) {  
                jp.getServer().broadcast(e.getStackTrace().toString(), Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
            }  
        }  
	}
    
    public Region getRegionByName(String RegionName)
    {
    	Region t = new Region(0, RegionName, 0, 0, 0, 0, 0, 0);
    	Region rtn = null;
    	
    	if (regions.contains(t))
    	{
    		rtn = regions.get(regions.indexOf(t));
    	}
    	
    	return rtn;
    }
    
    public String listRegions()
    {
    	String rtn = "Regions: ";
    	
    	for (int i = 0; i < regions.size(); i++)
    	{
    		rtn += regions.get(i).getRegionName() + " "; 
    	}
    	
    	return rtn;
    }
    
    public String listRegionsInside(Location l)
    {
    	String rtn = "Regions here: ";
    	
    	for (int i = 0; i < regions.size(); i++)
    	{
    		if (regions.get(i).isInside(l.getBlockX(), l.getBlockY(), l.getBlockZ()))
    			rtn += regions.get(i).getRegionName() + " "; 
    	}
    	
    	return rtn;    	
    }
    
    public String getRegionData(String RegionName)
    {
    	String rtn = RegionName + " (";
    	Region rg = getRegionByName(RegionName);
    	
    	if (rg != null)
    	{
    		rtn += rg._StartX + "," + rg._StartY + "," + rg._StartZ + "),(";
    		rtn += rg._EndX + "," + rg._EndY + "," + rg._EndZ + "): ";
    		
    		if (!rg._MetaData.isEmpty())
    		{
	    		for (Entry<String, String> meta : rg._MetaData.entrySet()) 
	    		{
	    			rtn += meta.getKey() + "=" + meta.getValue() + " ";
	    		}
    		}
    	}
    	else
    		rtn = "No region by that name exists.";
    	
    	return rtn;
    }
    
    public ArrayList<String> listMetaValues(Location l, String MetaKey)
    {
    	ArrayList<String> rtn = new ArrayList<String>();
    	
    	for (int i = 0; i < regions.size(); i++)
    	{
    		Region r = regions.get(i);
    		if (r.isInside(l.getBlockX(), l.getBlockY(), l.getBlockZ()))
    		{
    			String m = r.getMetaData(MetaKey);
    			if (m != null && m.trim() != "");
    				rtn.add(m);
    		}
    	}
    	
    	return rtn;    	
    }
    
    public String addRegion(String RegionName, Integer StartX, Integer StartY, Integer StartZ, Integer EndX, Integer EndY, Integer EndZ)
    {
    	String rtn = "Success!";
    	
    	if (getRegionByName(RegionName) == null)
    	{    		    				
    		Connection connection = null;  
            ResultSet resultSet = null;  
            PreparedStatement statement = null;
              
            try {  
                Class.forName("org.sqlite.JDBC");  
                connection = DriverManager  
                        .getConnection("jdbc:sqlite:" + _databaseFile); 
                
                statement = connection.prepareStatement("INSERT INTO Regions (RegionName, StartX, StartY, StartZ, EndX, EndY, EndZ) VALUES (?, ?, ?, ?, ?, ?, ?)");
                statement.setString(1, RegionName);
                statement.setInt(2, StartX);
                statement.setInt(3, StartY);
                statement.setInt(4, StartZ);
                statement.setInt(5, EndX);
                statement.setInt(6, EndY);
                statement.setInt(7, EndZ);
                
                statement.execute();
                
            	PreparedStatement ps = null;
                	
            	try 
            	{
                	ps = connection.prepareStatement("SELECT * FROM Regions Where RegionName LIKE ?");
	                ps.setString(1, RegionName);
                	
                	resultSet = ps.executeQuery();  
	                while (resultSet.next()) {  
	                	Region rg = new Region(resultSet.getInt("Id"), resultSet.getString("RegionName"), resultSet.getInt("StartX"), resultSet.getInt("StartY"), resultSet.getInt("StartZ"), resultSet.getInt("EndX"), resultSet.getInt("EndY"), resultSet.getInt("EndZ"));
	                                    	
	                	regions.add(rg);
	                }
            	}
            	catch (Exception e)
            	{
            		//rtn = "Error Receiving Region";
            		rtn = e.getMessage();
            	}
            	finally 
            	{
            		ps.close();
            	}                
                  
            } catch (Exception e) {  
                rtn = e.getMessage();
            } finally {  
                try {  
                	if (resultSet != null)
            			resultSet.close();  
                	if (statement != null)
                		statement.close();  
                	if (connection != null)
                		connection.close();  
                } catch (Exception e) {  
            		rtn = "Error Inserting Region";
            		e.printStackTrace();
                    //rtn = e.getMessage();
                }  
            }
    	}
    	else
    	{
    		rtn = "That region already exists!";
    	}
    	
    	return rtn;
    }
    
    public String removeRegion(String RegionName)
    {
    	String rtn = null;
    	Region rg = getRegionByName(RegionName);
    	
    	if (rg != null)
    	{    		
    		rtn = "Success!";
    				
    		Connection connection = null;  
            PreparedStatement statement = null;
              
            try {  
                Class.forName("org.sqlite.JDBC");  
                connection = DriverManager  
                        .getConnection("jdbc:sqlite:" + _databaseFile); 
                
                statement = connection.prepareStatement("DELETE FROM Regions WHERE ID = ?");
                statement.setInt(1, rg.getId());
                
                statement.execute();
                regions.remove(rg);
                  
            } catch (Exception e) {  
                rtn = e.getMessage();
            } finally {  
                try {   
                    statement.close();  
                    connection.close();  
                } catch (Exception e) {  
                    rtn = e.getMessage();
                }  
            }
    	}
    	else
    	{
    		rtn = "That region does not exist!";
    	}
    	
    	return rtn;
    }
    

    public String addRegionMetaData(String RegionName, String Key, String Value)
    { //Also Used to Modify
    	String rtn = null;
    	Region rg = getRegionByName(RegionName);
    	
    	if (rg != null)
    	{    		
    		rtn = "Success!";
    				
    		Connection connection = null;  
            PreparedStatement statement = null;
            
            try {  
                Class.forName("org.sqlite.JDBC");  
                connection = DriverManager  
                        .getConnection("jdbc:sqlite:" + _databaseFile); 
                Boolean isInsert = rg.getMetaData(Key) == null;
                
                if (isInsert) //INSERT
                {
                    statement = connection.prepareStatement("INSERT INTO RegionMetaData (MetaData, RegionId, MetaKey) VALUES (?, ?, ?)");
                }
                else //EDIT
                {
                    statement = connection.prepareStatement("UPDATE RegionMetaData SET MetaData = ? WHERE RegionId = ? AND MetaKey LIKE ?"); 
                }

                statement.setString(1, Value);
                statement.setInt(2, rg.getId());          	
                statement.setString(3, Key);
                
                statement.execute();
        		rg.addMetaData(Key, Value);
                
            } catch (Exception e) {  
                rtn = e.getMessage();
            } finally {  
                try {   
                    statement.close();  
                    connection.close();  
                } catch (Exception e) {  
                    rtn = e.getMessage();
                }  
            }
    	}
    	else
    	{
    		rtn = "That region does not exist!";
    	}
    	
    	return rtn;
    }
    
    public String removeRegionMetaData(String RegionName, String Key)
    {
    	String rtn = null;
    	Region rg = getRegionByName(RegionName);
    	
    	if (rg != null)
    	{    		
    		rtn = "Success!";
    				
    		if (rg.getMetaData(Key) != null)
    		{
	    		Connection connection = null;  
	            PreparedStatement statement = null;
	              
	            try {  
	                Class.forName("org.sqlite.JDBC");  
	                connection = DriverManager  
	                        .getConnection("jdbc:sqlite:" + _databaseFile); 
	                
	                statement = connection.prepareStatement("DELETE FROM RegionMetaData WHERE RegionId = ? AND MetaKey LIKE ?");
	                statement.setInt(1, rg.getId());
	                statement.setString(2, Key);
	                
	                statement.execute();
                	rg._MetaData.remove(Key);
	                  
	            } catch (Exception e) {  
	                rtn = e.getMessage();
	            } finally {  
	                try {   
	                    statement.close();  
	                    connection.close();  
	                } catch (Exception e) {  
	                    rtn = e.getMessage();
	                }  
	            }
    		}
    		else
    		{
    			rtn = "That Key does not exist!";
    		}
    	}
    	else
    	{
    		rtn = "That region does not exist!";
    	}
    	
    	return rtn;
    }

	class Region {
		private Integer _ID;
		private String _RegionName;
		private Integer _StartX;
		private Integer _StartY;
		private Integer _StartZ;
		private Integer _EndX;
		private Integer _EndY;
		private Integer _EndZ;
		private HashMap<String, String> _MetaData;
		
		private Region(Integer ID, String RegionName, Integer StartX, Integer StartY, Integer StartZ, Integer EndX, Integer EndY, Integer EndZ)
		{
			_ID = ID;
			_RegionName = RegionName;
			_StartX = StartX;
			_StartY = StartY;
			_StartZ = StartZ;
			_EndX = EndX;
			_EndY = EndY;
			_EndZ = EndZ;
			_MetaData = new HashMap<String, String>();
		}
		
		public Integer getId()
		{
			return _ID;
		}
		
		public void addMetaData (String Key, String Value)
		{
			_MetaData.put(Key.toLowerCase(), Value.toLowerCase());
		}
		
		public String getMetaData(String Key)
		{
			return _MetaData.get(Key);
		}
		
		public void removeMetaData(String Key)
		{
			_MetaData.remove(Key);
		}
		
		public String getRegionName()
		{
			return _RegionName;
		}
		
		public Boolean isInside(Integer X, Integer Y, Integer Z)
		{
			Boolean isX = _StartX > _EndX ? X >= _EndX && X <= _StartX : X >= _StartX && X <= _EndX;
			Boolean isY = _StartY > _EndY ? Y >= _EndY && Y <= _StartY : Y >= _StartY && Y <= _EndY;
			Boolean isZ = _StartZ > _EndZ ? Z >= _EndZ && Z <= _StartZ : Z >= _StartZ && Z <= _EndZ;
			
			return isX && isY && isZ;
		}
		
		@Override
		public boolean equals (Object o)
		{
			Boolean rtn = false;
			
			if (o instanceof Region)
			{
				Region r = (Region)o;
				rtn = r.getRegionName().equalsIgnoreCase(this.getRegionName());
				//System.out.println(rtn);
			}
			
			return rtn;
		}
	}
}