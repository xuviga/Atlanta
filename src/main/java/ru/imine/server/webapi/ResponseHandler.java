package ru.imine.server.webapi;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ru.imine.server.core.AiMineCore;
import ru.imine.server.core.database.MySQL;
import ru.imine.server.core.database.Storage;
import ru.imine.server.core.player.AiMinePlayerMP;
import ru.imine.server.util.DiscordAPI;
import ru.imine.server.util.PermsUtil;

import java.io.StringReader;
import java.sql.SQLException;

//todo не тот жсон
public class ResponseHandler
{
    public static Response parseAndExecute(String data)
    {
        try
        {
            JsonReader reader = new JsonReader(new StringReader(data));
            reader.setLenient(true);
            JsonObject request = new JsonParser().parse(reader).getAsJsonObject();

            String method = request.get("method").getAsString();
            JsonObject args;
            if (request.has("args"))
            {
                args = request.getAsJsonObject("args");
            }
            else
                args = new JsonObject();
            if (method.equalsIgnoreCase("server/get_online_players"))
            {
                JsonObject object = new JsonObject();
                PlayerList playerList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
                object.addProperty("players", playerList==null ? 0 : playerList.getCurrentPlayerCount());
                object.addProperty("slots", playerList==null ? 0 : playerList.getMaxPlayers());
                return new Response(object, 200);
            }
            if (method.equalsIgnoreCase("player/get_chat_color") || method.equalsIgnoreCase("player/get_nick_color"))
            {
                Long playerId = null;
                try
                {
                    playerId = args.get("player").getAsLong();
                }
                catch (Exception e)
                {
                    JsonObject object = new JsonObject();
                    object.addProperty("message", "Неправильные аргументы");
                    object.addProperty("player", playerId.toString());
                    WebAPI.LOGGER.error(method + " - неправильные аргументы: " + object, e);
                    DiscordAPI.getInstance().sendDonateErrorLog(method + " - неправильные аргументы: " + object, e);
                    return new Response(object, 400);
                }
                try
                {
                    AiMinePlayerMP player = AiMinePlayerMP.getOffline(playerId);
                    JsonObject object = new JsonObject();
                    object.addProperty("color",
                            method.equalsIgnoreCase("player/get_chat_color") ? player.getChatColor() : player.getNickColor());
                    return new Response(object, 200);
                }
                catch (Exception e)
                {
                    WebAPI.LOGGER.error("Неудача "+method, e);
                    DiscordAPI.getInstance().sendWarningLog("WebAPI", "Неудача "+method, e);
                    JsonObject object = new JsonObject();
                    object.addProperty("trace", ExceptionUtils.getStackTrace(e));
                    return new Response(object, 500);
                }
            }
            if (method.equalsIgnoreCase("player/set_chat_color") || method.equalsIgnoreCase("player/set_nick_color"))
            {
                Long playerId = null;
                int color = -1;
                try
                {
                    playerId = args.get("player").getAsLong();
                    color = args.get("color").getAsInt();
                }
                catch (Exception e)
                {
                    JsonObject object = new JsonObject();
                    object.addProperty("message", "Неправильные аргументы");
                    object.addProperty("player", playerId.toString());
                    object.addProperty("color", color);
                    WebAPI.LOGGER.error(method+" - неправильные аргументы: " + object, e);
                    DiscordAPI.getInstance().sendDonateErrorLog(method+" - неправильные аргументы: " + object, e);
                    return new Response(object, 400);
                }
                try
                {
                    String logMessage = String.format("Игрок %s устанавливает %s на %05X",AiMinePlayerMP.getOffline(playerId),method,color);
                    DiscordAPI.getInstance().sendDonateLog(logMessage);
                    WebAPI.LOGGER.info(logMessage);
                    if (method.equalsIgnoreCase("player/set_chat_color"))
                        AiMinePlayerMP.get(playerId).setChatColor(color);
                    else
                        AiMinePlayerMP.get(playerId).setNickColor(color);
                }
                catch (Exception e)
                {
                    return failure(e);
                }
                return success(new JsonObject());
            }
            if (method.equalsIgnoreCase("player/update_prefix"))
            {
                Long playerId = null;
                try
                {
                    playerId = args.get("player").getAsLong();
                }
                catch (Exception e)
                {
                    JsonObject object = new JsonObject();
                    object.addProperty("message", "Неправильные аргументы");
                    object.addProperty("player", playerId.toString());
                    WebAPI.LOGGER.error(method+" - неправильные аргументы: " + object, e);
                    DiscordAPI.getInstance().sendDonateErrorLog(method+" - неправильные аргументы: " + object, e);
                    return new Response(object, 400);
                }

                try
                {
                    AiMinePlayerMP player = AiMinePlayerMP.get(playerId);
                    if (player!=null)
                        AiMineCore.updatePlayerInfo(player);
                }
                catch (Exception e)
                {
                    WebAPI.LOGGER.error("Exception while trying to update player's new group", e);
                    DiscordAPI.getInstance().sendWarningLog("WebAPI", "Exception while trying to update player's new group", e);
                }

                return new Response(new JsonObject(), 200);
            }
            if (method.equalsIgnoreCase("player/get_coins"))
            {
                Long playerId = null;
                try
                {
                    playerId = args.get("player").getAsLong();
                }
                catch (Exception e)
                {
                    JsonObject object = new JsonObject();
                    object.addProperty("message", "Неправильные аргументы");
                    object.addProperty("player", playerId.toString());
                    WebAPI.LOGGER.error(method+" - неправильные аргументы: " + object, e);
                    DiscordAPI.getInstance().sendDonateErrorLog(method+" - неправильные аргументы: " + object, e);
                    return new Response(object, 400);
                }
                JsonObject object = new JsonObject();
                object.addProperty("amount", AiMinePlayerMP.getOffline(playerId).getMoney());
                return new Response(object, 200);
            }
            else if (method.equalsIgnoreCase("player/change_coins"))
            {
                Long playerId = null;
                int amount = 0;
                int mlpr;
                try
                {
                    playerId = args.get("player").getAsLong();
                    amount = args.get("amount").getAsInt();
                    mlpr = args.get("action").getAsString().equalsIgnoreCase("add") ? 1 : -1;
                }
                catch (Exception e)
                {
                    JsonObject object = new JsonObject();
                    object.addProperty("message", "Неправильные аргументы");
                    object.addProperty("player", playerId.toString());
                    object.add("action", args.has("action") ? args.get("action") : JsonNull.INSTANCE);
                    object.addProperty("amount", amount);
                    WebAPI.LOGGER.error(method+" - неправильные аргументы: " + object, e);
                    DiscordAPI.getInstance().sendDonateErrorLog(method+" - неправильные аргументы: " + object, e);
                    return new Response(object, 400);
                }
                try
                {
                    String logMessage = String.format("Изменяем игроку %s кол-во монет на %s", AiMinePlayerMP.getOffline(playerId), amount * mlpr);
                    DiscordAPI.getInstance().sendDonateLog(logMessage);
                    WebAPI.LOGGER.info(logMessage);
                    AiMinePlayerMP.getOffline(playerId).changeMoney(amount * mlpr);
                }
                catch (Exception e)
                {
                    return failure(e);
                }
                return success(new JsonObject());
            }
            else if (method.equalsIgnoreCase("player/can_activate_group"))
            {
                Long playerId = null;
                String group;
                try
                {
                    playerId = args.get("player").getAsLong();
                    group = args.get("group").getAsString();
                }
                catch (Exception e)
                {
                    JsonObject object = new JsonObject();
                    object.addProperty("message", "Неправильные аргументы");
                    object.addProperty("player", playerId.toString());
                    object.add("group", JsonNull.INSTANCE);
                    WebAPI.LOGGER.error(method+" - неправильные аргументы: " + object, e);
                    DiscordAPI.getInstance().sendDonateErrorLog(method+" - неправильные аргументы: " + object, e);
                    return new Response(object, 400);
                }
                JsonObject object = new JsonObject();
                boolean hasGroup = PermsUtil.inheritsGroup(playerId, group);
                //todo За 2 дня отвечать has_group = false
                object.addProperty("can_activate", !hasGroup);
                return new Response(object, 200);
            }
            else if (method.equalsIgnoreCase("player/get_groups"))
            {
                Long playerId = null;
                try
                {
                    playerId = args.get("player").getAsLong();
                }
                catch (Exception e)
                {
                    JsonObject object = new JsonObject();
                    object.addProperty("message", "Неправильные аргументы");
                    object.addProperty("player", playerId.toString());
                    WebAPI.LOGGER.error(method + " - неправильные аргументы: " + object, e);
                    DiscordAPI.getInstance().sendDonateErrorLog(method + " - неправильные аргументы: " + object, e);
                    return new Response(object, 400);
                }
                JsonObject object = new JsonObject();
                JsonArray groups = new JsonArray();
                for (String s : PermsUtil.getGroups(playerId)) {
                    groups.add(new JsonPrimitive(s));
                }
                object.add("groups", groups);
                return new Response(object, 200);
            }
            else if (method.equalsIgnoreCase("player/buy_group"))
            {
                Long playerId = null;
                String group = null;
                Long duration;
                try
                {
                    playerId = args.get("player").getAsLong();
                    group = args.get("group").getAsString();
                    duration = args.get("duration").getAsLong();
                }
                catch (Exception e)
                {
                    JsonObject object = new JsonObject();
                    object.addProperty("message", "Неправильные аргументы");
                    object.addProperty("player", playerId.toString());
                    object.addProperty("group", group);
                    object.add("duration", JsonNull.INSTANCE);
                    WebAPI.LOGGER.error(method+" - неправильные аргументы: " + object, e);
                    DiscordAPI.getInstance().sendDonateErrorLog(method+" - неправильные аргументы: " + object, e);
                    return new Response(object, 400);
                }
                try
                {
                    String logMessage = String.format("Покупаем игроку %s группу %s на %s минут", AiMinePlayerMP.getOffline(playerId), group, duration);
                    DiscordAPI.getInstance().sendDonateLog(logMessage);
                    WebAPI.LOGGER.info(logMessage);
                    if (PermsUtil.inheritsGroup(playerId, group))
                    {
                        DiscordAPI.getInstance().sendDonateErrorLog("У этого игрока уже есть эта группа или выше!",new Exception());
                        WebAPI.LOGGER.error("У этого игрока уже есть эта группа или выше!");
                        JsonObject object = new JsonObject();
                        JsonArray groups = new JsonArray();
                        for (String s : PermsUtil.getGroups(playerId)) {
                            groups.add(new JsonPrimitive(s));
                        }
                        object.add("groups", groups);
                        return new Response(object, 412);
                    }
                    else
                    {
                        PermsUtil.provideGroup(playerId, group, duration * 60);
                        JsonObject object = new JsonObject();
                        JsonArray groups = new JsonArray();
                        for (String s : PermsUtil.getGroups(playerId)) {
                            groups.add(new JsonPrimitive(s));
                        }
                        int meta = 0;
                        try
                        {
                            if (group.equalsIgnoreCase("platinum") || group.equalsIgnoreCase("diamond"))
                                meta = 1;
                            else if (group.equalsIgnoreCase("emerald"))
                                meta = 2;

                            if (meta>Storage.getStorage("donate").getInteger(playerId.toString(),"got_smile_case",-1))
                            {
                                String bonusCase = "{\"id\":\"imine:smileCase\",\"damage\":" + meta + "}";
                                String item_request = "INSERT INTO `bought_items` (`Player`, `Server`, `ItemStack`) VALUES (?, ?, ?)";
                                MySQL.getGameDataSQL().updateSQL(item_request, playerId.toString(), AiMineCore.getServerName(), bonusCase);

                                Storage.getStorage("donate").set(playerId.toString(), "got_smile_case", meta);
                                logMessage = "Успешно выдали подарочный кейс смайликов уровня " + meta;
                                DiscordAPI.getInstance().sendDonateLog(logMessage);
                                WebAPI.LOGGER.info(logMessage);
                            }
                        }
                        catch (SQLException e)
                        {
                            WebAPI.LOGGER.error("SQL exception while trying to add present smile case for buying group=" + group, e);
                            DiscordAPI.getInstance().sendDonateErrorLog("Не удалось выдать подарочный кейс смайликов уровня "+meta,e);
                        }

                        try
                        {
                            AiMinePlayerMP player = AiMinePlayerMP.get(playerId);
                            if (player!=null)
                                AiMineCore.updatePlayerInfo(player);
                        }
                        catch (Exception e)
                        {
                            WebAPI.LOGGER.error("Exception while trying to update player's new group", e);
                            DiscordAPI.getInstance().sendDonateErrorLog("Exception while trying to update player's new group", e);
                        }
                        return success(object);
                    }
                }
                catch (Exception e)
                {
                    return failure(e);
                }
            }
            else if (method.equalsIgnoreCase("player/buy_item"))
            {
                Long playerId = null;
                int amount = 0;
                JsonObject nbt;
                try
                {
                    playerId = args.get("player").getAsLong();
                    nbt = args.getAsJsonObject("nbt");
                    amount = args.get("amount").getAsInt();
                }
                catch (Exception e)
                {
                    JsonObject object = new JsonObject();
                    object.addProperty("message", "Неправильные аргументы");
                    object.addProperty("player", playerId.toString());
                    object.add("nbt", JsonNull.INSTANCE);
                    object.addProperty("amount", amount);
                    WebAPI.LOGGER.error(method+" - неправильные аргументы: " + object, e);
                    DiscordAPI.getInstance().sendDonateErrorLog(method+" - неправильные аргументы: " + object, e);
                    return new Response(object, 400);
                }
                try
                {
                    String logMessage = String.format("Игрок %s покупает %d пачек предмета %s", AiMinePlayerMP.getOffline(playerId), amount, nbt);
                    DiscordAPI.getInstance().sendDonateLog(logMessage);
                    WebAPI.LOGGER.info(logMessage);
                    for (int i = 0; i < amount; i++)
                    {
                        String item_request = "INSERT INTO `bought_items` (`Player`, `Server`, `ItemStack`) VALUES (?, ?, ?)";
                        MySQL.getGameDataSQL().updateSQL(item_request, playerId.toString(), AiMineCore.getServerName(), nbt.toString());
                    }
                }
                catch (Exception e)
                {
                    return failure(e);
                }
                return success(new JsonObject());
            }
        }
        catch (Throwable e)
        {
            WebAPI.LOGGER.error("Необработанная ошибка", e);
            DiscordAPI.getInstance().sendDonateErrorLog("Необработанная ошибка", e);
            JsonObject object = new JsonObject();
            object.addProperty("trace", ExceptionUtils.getStackTrace(e));
            return new Response(object, 500);
        }
        JsonObject object = new JsonObject();
        object.addProperty("message", "Method not found");
        return new Response(object, 404);
    }

    private static Response failure(Exception e)
    {
        WebAPI.LOGGER.error("Неудача!", e);
        DiscordAPI.getInstance().sendDonateErrorLog("Неудача!", e);
        JsonObject object = new JsonObject();
        object.addProperty("trace", ExceptionUtils.getStackTrace(e));
        return new Response(object, 500);
    }

    private static Response success(JsonObject jsonObject)
    {
        WebAPI.LOGGER.error("Успешно");
        DiscordAPI.getInstance().sendDonateLog("Успешно");
        return new Response(jsonObject, 200);
    }
}
