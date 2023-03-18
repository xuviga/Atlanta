package ru.imine.server.webapi;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import ru.imine.shared.util.Discord;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class HttpListener
{

    public HttpListener(int port)
    {
        try
        {
            InetSocketAddress address = new InetSocketAddress(port);
            HttpServer httpServer = HttpServer.create(address, 0);
            httpServer.createContext("/", new HttpHandler());
            httpServer.setExecutor(null);
            httpServer.start();

        }
        catch (Exception exception)
        {
            WebAPI.LOGGER.error("Failed to create HTTP server on port " + port, exception);
            Discord.instance.sendErrorLog("WebAPI", "Failed to create HTTP server on port " + port, exception);
            FMLCommonHandler.instance().exitJava(1, false);
        }
    }


    private class HttpHandler implements com.sun.net.httpserver.HttpHandler
    {
        @Override
        public void handle(HttpExchange t)
        {
            byte[] bytes = new byte[Integer.parseInt(t.getRequestHeaders().get("Content-length").get(0))];
            try
            {
                new DataInputStream(t.getRequestBody()).readFully(bytes);
            }
            catch (IOException e)
            {
                WebAPI.LOGGER.error("Failed to read bytes of request from site.", e);
                Discord.instance.sendWarningLog("WebAPI", "Failed to read bytes of request from site.", e);
                return;
            }
            String receivedData = new String(bytes);

            Response response = ResponseHandler.parseAndExecute(receivedData);
            String dataToSend = response.data.toString();

            try
            {
                t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                t.sendResponseHeaders(response.code, dataToSend.length());
                OutputStream os = t.getResponseBody();
                os.write(dataToSend.getBytes());
                os.flush();
                os.close();
            }
            catch (IOException e)
            {
                WebAPI.LOGGER.error("Failed to send response to site.\n Request=" + receivedData + "\nResponse=" + dataToSend, e);
                Discord.instance.sendWarningLog("WebAPI", "Failed to send response to site.\n Request=" + receivedData + "\nResponse=" + dataToSend, e);
                return;
            }
        }
    }
}