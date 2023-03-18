package ru.imine.shared.util;

public abstract class Discord
{
    public static Discord instance;

    public abstract void sendErrorLog(String header, String message, Throwable e);

    public abstract void sendErrorLog(String header, String message);

    public abstract void sendWarningLog(String header, String message, Throwable e);

    public abstract void sendWarningLog(String header, String message);

}
