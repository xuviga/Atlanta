package ru.imine.cases.server;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import ru.justagod.cutter.GradleSide;
import ru.justagod.cutter.GradleSideOnly;
import ru.imine.cases.Cases;
import ru.imine.cases.network.CasesListPacket;
import ru.imine.cases.network.CasesMainPacket;
import ru.imine.cases.network.CasesViewPacket;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static ru.imine.cases.server.JCases.decrementCases;

@GradleSideOnly(GradleSide.SERVER)
public class Comma extends CommandBase {
	String usage = "/jcases <arg>\n" +
			"args:\n" +
			" - list\n" +
			" - view\n" +
			" - roll\n" +
			" - add\n" +
			" - remove\n" +
			" - check\n" +
			" - reload";

	@Override
	public String getName() {
		return "jcases";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return usage;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, "list", "view", "roll", "add", "remove", "check", "reload");
		if (args.length == 2) {
			return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		}
		return super.getTabCompletions(server, sender, args, targetPos);
	}

	@Override
	public List<String> getAliases() {
		ArrayList<String> al = new ArrayList<>();
		al.add("jca");
		al.add("jc");
		return al;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (!sender.canUseCommand(1, "jcases.command.main")) {
			sender.sendMessage(new TextComponentString(TextFormatting.RED + "Нет прав"));
			return;
		}
		if (args.length == 0) {
			if (sender.canUseCommand(4, "jcases.command.op.help"))
				sender.sendMessage(new TextComponentString(usage));
			else
				sender.sendMessage(new TextComponentString(usage.substring(0, usage.indexOf(" - add\n"))));
			return;
		}
		if (sender.getName().equals("Server")) {
			sender.sendMessage(new TextComponentString("Only player."));
			return;
		}
		EntityPlayerMP player = (EntityPlayerMP) sender;
		switch (args[0]) {
			case "list":
				if (!sender.canUseCommand(1, "jcases.command.list")) {
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Нет прав"));
					return;
				}
				Cases.network.sendTo(new CasesMainPacket("Clear"), player);
				String out = Joiner.on(",").join(Iterables.transform((Iterable<JCase>) JCases.caseMap.values(), (Function<? super JCase, ?>) caseEntry -> {
					if (caseEntry != null) {
						return caseEntry.format(JCases.getAvailableCases(player, caseEntry.getName()));
					}
					return null;
				}));
				Cases.network.sendTo(new CasesListPacket(out), player);
				Cases.network.sendTo(new CasesMainPacket("Open"), player);
				break;
			case "view":
				if (!sender.canUseCommand(1, "jcases.command.view")) {
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Нет прав"));
					return;
				}
				if (args.length > 1) {
					String caseName = args[1];
					if (JCases.caseMap.containsKey(caseName)) {
						Cases.network.sendTo(new CasesViewPacket((JCases.caseMap.get(caseName).formatItems())), player);
						Cases.network.sendTo(new CasesMainPacket(String.format("%s,%s", "View", caseName)), player);
						return;
					}
					Cases.network.sendTo(new CasesMainPacket("CloseGui"), player);
					player.sendMessage(new TextComponentString(TextFormatting.RED + "Неизвестный кейс"));
				}
				break;
			case "roll":
				if (!sender.canUseCommand(1, "jcases.command.roll")) {
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Нет прав"));
					return;
				}
				if (args.length > 1) {
					String playerName = player.getName();
					String caseName = args[1];
					if (JCases.caseMap.containsKey(caseName)) {
						int available = JCases.getAvailableCases(player, caseName);
						if (available > 0) {
							decrementCases(playerName, caseName, 1);
							JCases.savePlayers();
							JCaseItem winItem = (JCases.caseMap.get(caseName)).getRandomItem();
							new Thread(() -> {
								if (!player.inventory.addItemStackToInventory(winItem.getItemStack())) {
									EntityItem item = new EntityItem(player.getEntityWorld(), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
									player.getEntityWorld().spawnEntity(item);
								}

								give(player, winItem);
								if (winItem.getRarity() > 2) {
									lucky(playerName, winItem);
								}
							}).start();
							Cases.network.sendTo(new CasesMainPacket(String.format("%s,%s", "SetWon", winItem.winFormat())), player);
							Cases.network.sendTo(new CasesMainPacket("RollCase"), player);
							return;
						}
						Cases.network.sendTo(new CasesMainPacket("CloseGui"), player);
						player.sendMessage(new TextComponentString(TextFormatting.RED + "У вас нет доступных для открытия кейсов"));
						break;
					}
					Cases.network.sendTo(new CasesMainPacket("CloseGui"), player);
					player.sendMessage(new TextComponentString(TextFormatting.RED + "Неизвестный кейс"));
				}
				break;
			case "add":
				if (!sender.canUseCommand(4, "jcases.command.add")) {
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Нет прав"));
					return;
				}
				if (args.length > 2) {
					String strPl = args[1];
					String caseName = args[2];
					if (!JCases.caseMap.containsKey(caseName)) {
						throw new CommandException("Неизвестный кейс");
					}
					int value = args.length > 3 ? Integer.parseInt(args[3]) : 1;

					JCases.incrementCases(strPl, caseName, value);
					JCases.savePlayers();
					sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Успешно выполнено"));
					return;
				}
				sender.sendMessage(new TextComponentString("/jcases add {Player} {Case} [Value]"));
				break;
			case "remove":
				if (!sender.canUseCommand(4, "jcases.command.remove")) {
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Нет прав"));
					return;
				}
				if (args.length > 2) {
					String strPl = args[1];
					String caseName = args[2];
					if (!JCases.caseMap.containsKey(caseName)) {
						throw new CommandException("Неизвестный кейс");
					}
					int value = args.length > 3 ? Integer.parseInt(args[3]) : 1;

					decrementCases(strPl, caseName, value);
					JCases.savePlayers();
					sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Успешно выполнено"));
					return;
				}
				sender.sendMessage(new TextComponentString("/jcases remove {Player} {Case} [Value]"));
				break;
			case "check":
				if (!sender.canUseCommand(4, "jcases.command.check")) {
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Нет прав"));
					return;
				}
				if (args.length > 2) {
					String strPl = args[1];
					String caseName = args[2];
					if (!JCases.caseMap.containsKey(caseName)) {
						throw new CommandException("Неизвестный кейс");
					}
					if ((!JCases.playersCases.containsKey(player.getName())) || (!(JCases.playersCases.get(player.getName())).containsKey(caseName))) {
						throw new CommandException("У игрока нет доступных кейсов");
					}
					sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Количество доступных кейсов: " + (JCases.playersCases.get(strPl)).get(caseName)));
					return;
				}
				sender.sendMessage(new TextComponentString("/jcases check {Player} {Case}"));
				break;
			case "reload":
				if (!sender.canUseCommand(4, "jcases.command.reload")) {
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Нет прав"));
					return;
				}
				JCases.initCases();
				JCases.initPlayers();
				sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Успешно выполнено"));
				break;
			default:
				if (sender.canUseCommand(4, "jcases.command.op.help"))
					sender.sendMessage(new TextComponentString(usage));
				else
					sender.sendMessage(new TextComponentString(usage.substring(0, usage.indexOf(" - add\n"))));
				break;
		}
	}

	private void lucky(final String playerName, final JCaseItem item) {
		FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers().forEach(entity -> {
			try {
				TextFormatting color = this.colorItem(item);
				if (!entity.getName().equals(playerName))
					entity.sendMessage(new TextComponentString(String.format("&r&bИгрок &6&l%s&r&b получил из кейса дроп &l%s&r&b.", playerName, color + "✦ " + item.getName() + color + " ✦").replace("&", "§")));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private void give(final EntityPlayerMP player, final JCaseItem item) {
		TextFormatting color = this.colorItem(item);
		player.sendMessage(new TextComponentString(String.format("&r&bВы получили" +
				" дроп &l%s&r&b из кейса", color + "✦ " + item.getName() + color + " ✦").replace("&", "§")));
	}

	private TextFormatting colorItem(final JCaseItem item) {
		TextFormatting color;
		switch (item.getRarity()) {
			case 2: {
				color = TextFormatting.DARK_PURPLE;
				break;
			}
			case 3: {
				color = TextFormatting.LIGHT_PURPLE;
				break;
			}
			case 4: {
				color = TextFormatting.RED;
				break;
			}
			case 5: {
				color = TextFormatting.GOLD;
				break;
			}
			case 1:
			default: {
				color = TextFormatting.BLUE;
				break;
			}
		}
		return color;
	}
}
