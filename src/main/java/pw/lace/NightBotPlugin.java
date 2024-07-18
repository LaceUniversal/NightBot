package pw.lace;

import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
		name = "NightBot Integration",
		description = "Display 'Now Playing' from NightBot file",
		tags = {"nightbot", "overlay", "text"}
)
public class NightBotPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private NightBotPluginOverlay overlay;

	@Provides
	NightBotPluginConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(NightBotPluginConfig.class);
	}

	@Override
	protected void startUp() throws Exception {
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception {
		overlayManager.remove(overlay);
	}
}
