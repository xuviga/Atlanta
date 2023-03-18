package ru.imine.server.webapi;

import ru.imine.server.core.AiMineCore;
import ru.imine.server.core.database.MySQL;
import ru.imine.server.core.database.Row;
import ru.imine.shared.util.Discord;

import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class MySQLListener
{
    public MySQLListener()
    {
        MySQL mySQL = MySQL.getGlobalDataSQL();
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    for (Row row : mySQL.querySQL("SELECT * FROM `API_query` WHERE `server` = ? AND (`status` = 'new' OR `status` = 'error')", AiMineCore.getServerName()))
                    {
                        Response response = ResponseHandler.parseAndExecute(row.getString("query"));
                        mySQL.updateSQL("UPDATE `API_query` SET `status` = ?, `resp_code` = ?, `resp_message` =  ?, `updated_at` = CURRENT_TIMESTAMP WHERE `id` = ?",
                                response.code == 200 ? "done" : "error", response.code, response.data.toString(), row.getInteger("id"));
                    }
                }
                catch (SQLException e)
                {
                    WebAPI.LOGGER.error("Failed query to MySQL connection to WebSite!", e);
                    Discord.instance.sendWarningLog("WebAPI", "Failed query to MySQL connection to WebSite!", e);
                }
            }
        }, 10000, 10000);
    }
}