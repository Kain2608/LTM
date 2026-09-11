package poker.ui;

import javafx.scene.image.Image;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import poker.model.Card;

public class AssetLoader {
    private static final String DEFAULT_BASE_PATH = "D:/OpenDecks-Public-Domain-and-CC0-Playing-Cards";
    private static final File BASE_DIRECTORY = resolveBaseDirectory();
    private static final Map<String, Image> cache = new HashMap<>();

    public static Image loadImage(String relativePath) {
        if (cache.containsKey(relativePath)) {
            return cache.get(relativePath);
        }

        String resourcePath = "/assets/" + relativePath.replace('\\', '/');
        URL bundledResource = AssetLoader.class.getResource(resourcePath);
        if (bundledResource != null) {
            Image image = new Image(bundledResource.toExternalForm());
            cache.put(relativePath, image);
            return image;
        }

        File file = new File(BASE_DIRECTORY, relativePath);
        if (!file.exists()) {
            cache.put(relativePath, null);
            return null;
        }

        try {
            String url = file.toURI().toString();
            Image img = new Image(url);
            cache.put(relativePath, img);
            return img;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static File resolveBaseDirectory() {
        String configuredPath = System.getProperty("poker.assets.path");
        if (configuredPath == null || configuredPath.isBlank()) {
            configuredPath = System.getenv("POKER_ASSETS_PATH");
        }
        return new File(configuredPath == null || configuredPath.isBlank()
                ? DEFAULT_BASE_PATH
                : configuredPath);
    }

    public static Image getTableImage() {
        return loadImage("table/poker-table.png");
    }

    public static Image getBackgroundImage() {
        return loadImage("backgrounds/casino-background.png");
    }

    public static Image getAuthBackgroundImage() {
        return loadImage("backgrounds/auth-background.png");
    }

    public static Image getLobbyBackgroundImage() {
        return loadImage("backgrounds/lobby-background.png");
    }

    public static Image getRoomListBackgroundImage() {
        Image img = loadImage("backgrounds/room-browser-background.png");
        if (img == null) {
            img = loadImage("rooms/img_new.png");
        }
        if (img == null) {
            img = loadImage("rooms/img (1).webp");
        }
        if (img == null) {
            img = loadImage("rooms/room_bg.png");
        }
        if (img == null) {
            img = loadImage("rooms/img.webp");
        }
        return img != null ? img : getLobbyBackgroundImage();
    }

    public static Image getLogoImage() {
        return loadImage("logo/poker-emblem.png");
    }

    public static Image getButtonFrame1Image() {
        return loadImage("button/btn_frame_1.png");
    }

    public static Image getLobbyRoomListButtonImage() {
        return loadImage("button/btn_icon_room_list.png");
    }

    public static Image getLobbyCreateRoomButtonImage() {
        return loadImage("button/btn_icon_create_room.png");
    }

    public static Image getLobbyLeaderboardButtonImage() {
        return loadImage("button/btn_icon_leaderboard.png");
    }

    public static Image getLobbyProfileButtonImage() {
        return loadImage("button/btn_icon_profile.png");
    }

    public static Image getButtonFrame2Image() {
        return loadImage("button/btn_frame_2.png");
    }

    public static Image getFormFrameImage() {
        return loadImage("button/form_frame.png");
    }

    public static Image getAvatarImage(String avatarFileName) {
        return loadImage("avatars/" + avatarFileName);
    }

    public static Image getAvatarFrameImage() {
        return loadImage("avatars/avatar_frame_gold.png");
    }

    public static Image getDealerAvatarImage() {
        return loadImage("avatars/avatar_dealer.png");
    }

    public static Image getChipFrameHudImage() {
        return loadImage("chips/chip_frame_hud_custom.png");
    }

    public static Image getCardImage(Card card) {
        if (card == null) return getCardBackImage("red");
        return loadImage(card.getImageRelativePath());
    }

    public static Image getCardBackImage(String color) {
        return loadImage("cards/card-back.png");
    }

    public static Image getChipImageForValue(int amount) {
        Image chip = null;
        if (amount >= 1000) chip = loadImage("chips/chip_orange_1000.png");
        else if (amount >= 500) chip = loadImage("chips/chip_purple_500.png");
        else if (amount >= 100) chip = loadImage("chips/chip_black_100.png");
        else if (amount >= 25) chip = loadImage("chips/chip_green_25.png");
        else if (amount >= 5) chip = loadImage("chips/chip_red_5.png");
        else if (amount >= 1) chip = loadImage("chips/chip_white_1.png");
        return chip != null ? chip : loadImage("chips/premium-chip.png");
    }

    public static Image getChipStackImage() {
        Image chipStack = loadImage("chips/chip_stack.png");
        return chipStack != null ? chipStack : loadImage("chips/premium-chip.png");
    }
}
