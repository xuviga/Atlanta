package ru.imine.server.core.database;

import com.alibaba.fastjson.JSONObject;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.imine.server.core.AiMineCore;
import ru.imine.shared.AiMine;
import ru.imine.shared.util.Discord;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@SuppressWarnings({"unused"})
public class MySQL
{
    private static MySQL globalData;
    private static MySQL gameData;

    private File dataFolder;
    private Connection connection;

    private String user;
    private String database;
    private String password;
    private String port;
    private String hostname;
    private String dbLocation;

    private final Logger logger;

    static
    {
        try
        {
            JSONObject config = AiMineCore.getConfig();
            JSONObject object = config.getJSONObject("database").getJSONObject("gamedata");
            gameData = new MySQL(
                    object.getString("ip"),
                    object.getString("port"),
                    object.getString("database"),
                    object.getString("login"),
                    object.getString("password"));

            object = config.getJSONObject("database").getJSONObject("globaldata");
            globalData = new MySQL(
                    object.getString("ip"),
                    object.getString("port"),
                    object.getString("database"),
                    object.getString("login"),
                    object.getString("password"));
        }
        catch (Exception e)
        {
            AiMine.LOGGER.error("FATAL ERROR: Failed to read MySQL connection data from config/iMine/core.cfg. Shutdown!", e);
            Discord.instance.sendErrorLog("iMineCore", "FATAL ERROR: Failed to read MySQL connection data from config/iMine/core.cfg. Shutdown!", e);
            FMLCommonHandler.instance().exitJava(1, false);
        }
    }

    public static MySQL getGameDataSQL()
    {
        return gameData;
    }

    public static MySQL getGlobalDataSQL()
    {
        return globalData;
    }

    public MySQL(String hostname, String port, String database, String username, String password) throws SQLException, ClassNotFoundException
    {
        this.dataFolder = null;
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.user = username;
        this.password = password;
        this.logger = LogManager.getLogger("MySQL|" + this.user);
        openConnection();
        connection.prepareStatement("SET SESSION wait_timeout = 999999").execute();
    }

    public MySQL(File dataFolder, String dbLocation) throws SQLException, ClassNotFoundException
    {
        this.dataFolder = dataFolder;
        this.dbLocation = dbLocation;
        this.logger = LogManager.getLogger("MySQL|" + this.dbLocation);
        openConnection();
        connection.prepareStatement("SET SESSION wait_timeout = 999999").execute();
    }

    protected Connection openConnection() throws SQLException, ClassNotFoundException
    {
        if (connection != null && !connection.isClosed())
            return connection;
        if (dbLocation==null)
        {
            Class.forName("com.mysql.jdbc.Driver");
            if (AiMineCore.isTest())
                connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database+"?characterEncoding=utf-8&serverTimezone=GMT", this.user, null);
            else
                connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database+"?characterEncoding=utf-8&serverTimezone="+ TimeZone.getDefault().getID(), this.user, this.password);
            return connection;
        }
        else
        {
            if (!dataFolder.exists())
            {
                boolean ignored = dataFolder.mkdirs();
            }
            File file = new File(dataFolder, dbLocation+".db");
            if (!(file.exists()))
            {
                try
                {
                    boolean ignored = file.createNewFile();
                }
                catch (Exception e)
                {
                    logger.error("Failed to create DB file", e);
                }
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" +dataFolder.toPath().toString() + "/" + dbLocation+".db?autoReconnect=true&characterEncoding=utf-8");
            return connection;
        }
    }
    public Connection getConnection()
    {
        return connection;
    }

    public List<Row> querySQL(String str, Object... args) throws SQLException
    {
        PreparedStatement statement = prepare(str, args);
        ResultSet resultSet = statement.executeQuery();

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columns = metaData.getColumnCount();

        List<Row> result = new ArrayList<>();

        while (resultSet.next())
        {
            Row row = new Row();
            boolean skipRow = false;
            result.add(row);
            for(int i = 1; i <= columns; i++)
            {
                Object object = resultSet.getObject(i);
                if (metaData.getColumnName(i).equals("created_at") && object != null && object.toString().equals("0000-00-00 00:00:00")) {
                    skipRow = true;
                    break;
                }
                if (object!=null)
                {
                    row.data_int.put(i, object);
                    row.data.put(metaData.getColumnName(i), object);
                }
            }
            if (!skipRow) {
                result.add(row);
            }
        }
        statement.close();
        resultSet.close();
        return result;
    }

    public int updateSQL(String str, Object... args) throws SQLException
    {
        PreparedStatement statement = prepare(str, args);
        int result = statement.executeUpdate();
        statement.close();
        return result;
    }

    private PreparedStatement prepare(String str, Object... args) throws SQLException
    {
        PreparedStatement stat = connection.prepareStatement(str);
        stat.setQueryTimeout(3);
        int i = 1;
        for (Object arg : args)
        {
            stat.setObject(i, arg);
            i++;
        }
        return stat;
    }
}