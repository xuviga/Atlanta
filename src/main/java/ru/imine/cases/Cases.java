package ru.imine.cases;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;
import ru.justagod.cutter.GradleSide;
import ru.justagod.cutter.GradleSideOnly;
import ru.justagod.cutter.invoke.Invoke;
import ru.imine.cases.network.CasesListPacket;
import ru.imine.cases.network.CasesMainPacket;
import ru.imine.cases.network.CasesViewPacket;
import ru.imine.cases.server.Comma;
import ru.imine.cases.server.JCases;

import java.io.File;

@Mod(
		modid = Cases.MOD_ID,
		name = Cases.MOD_NAME,
		version = Cases.VERSION
)
public class Cases {

	public static final String MOD_ID = "cases";
	public static final String MOD_NAME = "Cases";
	public static final String VERSION = "2.0";
	public static KeyBinding KeyTest;
	public static SimpleNetworkWrapper network;
	@Mod.Instance(MOD_ID)
	public static Cases INSTANCE;

	@Mod.EventHandler
	public void preinit(FMLPreInitializationEvent event) {
		network = new SimpleNetworkWrapper(MOD_ID);
		network.registerMessage(CasesMainPacket.Handler.class, CasesMainPacket.class, 0, Side.CLIENT);
		network.registerMessage(CasesListPacket.Handler.class, CasesListPacket.class, 1, Side.CLIENT);
		network.registerMessage(CasesViewPacket.Handler.class, CasesViewPacket.class, 2, Side.CLIENT);
		Invoke.server(() -> {
			JCases.confPath = new File(event.getModConfigurationDirectory(), "cases");
			JCases.confPath.mkdir();
			JCases.initCases();
			JCases.initPlayers();
		});

	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {

	}

	@Mod.EventHandler
	public void postinit(FMLPostInitializationEvent event) {
		if(event.getSide().isClient()){
			KeyTest = new KeyBinding("Открыть меню кейсов", Keyboard.KEY_F8, "Кейсы");
			ClientRegistry.registerKeyBinding(KeyTest);
		}
	}

	@GradleSideOnly(GradleSide.SERVER)
	@Mod.EventHandler
	public void initServer(FMLServerStartingEvent event) {
		event.registerServerCommand(new Comma());
	}
}
