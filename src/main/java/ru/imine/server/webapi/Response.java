package ru.imine.server.webapi;

import com.google.gson.JsonObject;

public class Response
{
    public JsonObject data;
    public int code;

    public Response(JsonObject data, int code)
    {
        this.data = data;
        this.code = code;
    }
}
