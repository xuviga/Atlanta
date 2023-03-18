package ru.imine.version.server.v1_12_2.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import ru.imine.server.core.player.AiMinePlayerMP;
import ru.imine.server.util.AiCommand;
import ru.imine.server.util.PermsUtil;
import ru.imine.shared.AiMine;
import ru.imine.shared.fancychat.chat.FancyLine;
import ru.imine.shared.util.Discord;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class MoneyCommand extends AiCommand
{
    @Override
    public String getName()
    {
        return "money";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/money help";
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("coins","mani","balance","bal","mone","mony","many","деньги");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender senderEnder, String[] args)
    {
        boolean moberator = PermsUtil.hasPermission(senderEnder,"imine.money");
        AiMinePlayerMP sender = senderEnder instanceof EntityPlayerMP ? AiMinePlayerMP.get(senderEnder) : null;
        if (args.length==0)
        {
            sender.sendMessage("Твой баланс: "+ sender.getMoney());
            return;
        }
        else if (args[0].equalsIgnoreCase("help"))
        {
            senderEnder.sendMessage(new TextComponentString("/money - проверить твой баланс\n" +
                    "/money help - вывод существующих команд\n" +
                    "/money give игрок сумма - перевести нику указанную сумму"));
            if (moberator)
                senderEnder.sendMessage(new TextComponentString("Админские команды:\n"+
                    "/money mod add игрок сумма - админская выдача денег \"из неоткуда\"\n" +
                    "/money mod top - топ 10 игроков по деньгам\n" +
                    "/money mod take игрок сумма - админский вычет денег у игрока\n" +
                    "/money mod delete игрок - админское полное очищение баланса игрока, установка значения на \"0\"\n" +
                    "/money mod set nick summ - админская установка нужного кол-ва денег игроку"));
        }
        else if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("pay"))
        {
            if (args.length != 3)
            {
                execute(server, senderEnder, new String[]{"help"});
                return;
            }

            long money = Long.parseLong(args[2]);
            AiMinePlayerMP target = AiMinePlayerMP.getOffline(args[1]);
            if (target == null)
            {
                senderEnder.sendMessage(new TextComponentString("Такой игрок не найден"));
                return;
            }

            try
            {
                sender.changeMoney(-money);
                target.changeMoney(money);
                AiMinePlayerMP targetEntity = AiMinePlayerMP.get(target.getName());
                if (targetEntity!=null)
                {
                    sender.sendMessage(new FancyLine().with("Успешно перевели "+money+" монеток игроку ").with(targetEntity.getDisplayName()));
                    targetEntity.sendMessage(new FancyLine().with(sender.getDisplayName()).with(" успешно перевел тебе " + money + " монеток"));
                }
                else
                    sender.sendMessage("Успешно перевели "+money+" монеток игроку "+ target.getName());
            }
            catch (SQLException e)
            {
                String message = String.format("Failed to transfer money between players: %s (%s) -%s-> %s %s",
                        sender.getName(), sender.getUUID(), money, target.getName(), target.getPlayerId());
                AiMine.LOGGER.error(message, e);
                Discord.instance.sendErrorLog("iMineCore", message, e);
            }
        }
        else if (args[0].equalsIgnoreCase("mod"))
        {
            if (!moberator)
            {
                senderEnder.sendMessage(new TextComponentString("§cНедостаточно прав!"));
                return;
            }
            if (args[1].equalsIgnoreCase("add"))
            {
                if (args.length != 4)
                {
                    execute(server, senderEnder, new String[]{"help"});
                    return;
                }

                long money = Long.parseLong(args[3]);
                AiMinePlayerMP target = AiMinePlayerMP.getOffline(args[2]);
                if (target == null)
                {
                    senderEnder.sendMessage(new TextComponentString("Такой игрок не найден"));
                    return;
                }

                try
                {
                    target.changeMoney(money);
                    senderEnder.sendMessage(new TextComponentString("Успешно добавили "+money+" монеток игроку "+ target.getName()));
                }
                catch (SQLException e)
                {
                    String message = String.format("Failed to admin add money to player: %s (%s) (%s)",
                            target.getName(), target.getPlayerId(), money);
                    AiMine.LOGGER.error(message, e);
                    Discord.instance.sendErrorLog("iMineCore", message, e);
                }
            }
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] wut, int index)
    {
        return index>1;
    }
}
