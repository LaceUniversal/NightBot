package pw.lace;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("nightbot")
public interface NightBotPluginConfig extends Config {
	@ConfigItem(
			keyName = "filePath",
			name = "File Path",
			description = "Path to the text file"
	)
	default String filePath() {
		return "C:/Users/USER/Documents/Nightbot/current_song.txt";
	}

	@ConfigItem(
			keyName = "xCoordinate",
			name = "X Coordinate",
			description = "X position of the overlay"
	)
	default int getXCoordinate() {
		return 20;
	}

	@ConfigItem(
			keyName = "yCoordinate",
			name = "Y Coordinate",
			description = "Y position of the overlay"
	)
	default int getYCoordinate() {
		return 550;
	}

	@ConfigItem(
			keyName = "roundedBackground",
			name = "Rounded Background",
			description = "Enable rounded background for the overlay"
	)
	default boolean isRoundedBackground() {
		return false;
	}
}
