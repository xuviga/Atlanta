package ru.imine.server.util;

import org.apache.commons.lang3.exception.ExceptionUtils;
import ru.imine.server.core.AiMineCore;
import ru.imine.shared.AiMine;
import ru.imine.shared.util.Discord;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class DiscordAPI extends Discord
{
    private static final String USER_AGENT = "Mozilla/5.0";

    private static final String MC_ERROR_LOGS_HOOK = "784477526256386058/IQnKhr-Vu1v_35ffh6-2lI1g4lzFZ2agVSbDuKX38d1P8mc3c4T7EGCDHsb--rnOQdIT";
    private static final String MC_WARNING_LOGS_HOOK = "784477526256386058/IQnKhr-Vu1v_35ffh6-2lI1g4lzFZ2agVSbDuKX38d1P8mc3c4T7EGCDHsb--rnOQdIT";
    private static final String MC_DONATE_LOGS_HOOK = "784477526256386058/IQnKhr-Vu1v_35ffh6-2lI1g4lzFZ2agVSbDuKX38d1P8mc3c4T7EGCDHsb--rnOQdIT";

    public static DiscordAPI getInstance()
    {
        return (DiscordAPI) instance;
    }

    public static void sendWebhook(String endpoint, String username, String message)
    {
        if (AiMineCore.getServerName().toLowerCase().endsWith("pingas"))
            return;
        if (message.length() > 1955)
        {
            sendWebhook(endpoint, username, message.substring(0, 1950)+"```");
            sendWebhook(endpoint, username, "```"+message.substring(1950));
            return;
        }

        try
        {
            username = username.substring(0, Math.min(username.length(), 32));
            URL url = new URL("https://discordapp.com/api/webhooks/" + endpoint);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);

            // For POST only - START
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(String.format("content=%s&username=%s", message, username).getBytes());
            os.flush();
            os.close();
            // For POST only - END

            int responseCode = con.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_NO_CONTENT)
                AiMine.LOGGER.warn("Got bad response code from discord api: %d for message '%s'", responseCode, message.replace("`", ""));

        }
        catch (IOException ignored)
        {
            ignored.printStackTrace();
        }
    }

    public void sendErrorLog(String header, String message, Throwable e)
    {
        String completed = String.format("%s\n%s:```%s: %s```", header, new Date(), message, ExceptionUtils.getStackTrace(e));
        e.printStackTrace();
        sendWebhook(MC_ERROR_LOGS_HOOK, "HiTech: "+AiMineCore.getServerName(), completed);
    }

    public void sendErrorLog(String header, String message)
    {
        String completed = String.format("%s\n%s:```%s```", header, new Date(), message);
        sendWebhook(MC_ERROR_LOGS_HOOK, "HiTech: "+AiMineCore.getServerName(), completed);
    }

    public void sendWarningLog(String header, String message, Throwable e)
    {
        String completed = String.format("%s\n%s:```%s: %s```", header, new Date(), message, ExceptionUtils.getStackTrace(e));
        sendWebhook(MC_WARNING_LOGS_HOOK, "HiTech: "+AiMineCore.getServerName(), completed);
    }

    public void sendWarningLog(String header, String message)
    {
        String completed = String.format("%s\n%s:```%s```", header, new Date(), message);
        sendWebhook(MC_WARNING_LOGS_HOOK, "HiTech: "+AiMineCore.getServerName(), completed);
    }

    public void sendDonateLog(String message)
    {
        String completed = String.format("%s:\n%s", new Date(), message);
        sendWebhook(MC_DONATE_LOGS_HOOK, "HiTech: " + AiMineCore.getServerName(), completed);
    }

    public void sendDonateErrorLog(String message, Throwable e)
    {
        String completed = String.format("%s:```%s: %s```", new Date(), message, ExceptionUtils.getStackTrace(e));
        sendWebhook(MC_DONATE_LOGS_HOOK, "HiTech: "+AiMineCore.getServerName(), completed);
    }
}
