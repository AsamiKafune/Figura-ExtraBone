package moe.kfn.figuraextrabone;

import moe.kfn.figuraextrabone.imp.WizardEntryEx;
import moe.kfn.figuraextrabone.lua.plugins.ExtraBonePlugin;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FiguraExtraBone implements ModInitializer {
	public static final String MODID = "figuraextrabone";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
        new ExtraBonePlugin();
        WizardEntryEx.init();
		LOGGER.info("Hello Fabric world!");
	}
}