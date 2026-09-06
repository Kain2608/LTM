package poker.ui;

import javafx.scene.image.Image;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import poker.model.Card;

public class AssetLoader {
    private static final String BASE_PATH = "D:/OpenDecks-Public-Domain-and-CC0-Playing-Cards/";
    private static final Map<String, Image> cache = new HashMap<>();

    public static Image loadImage(String relativePath) {
        if (cache.containsKey(relativePath)) {
            return cache.get(relativePath);
        }

        File file = new File(BASE_PATH + relativePath);
        if (!file.exists()) {
            System.err.println("Asset not found: " + file.getAbsolutePath());
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

    public static Image getTableImage() {
        return loadImage("table/poker_table.png");
    }

    public static Image getBackgroundImage() {
        return loadImage("backgrounds/casino_background.png");
    }

    public static Image getAuthBackgroundImage() {
        return loadImage("backgrounds/f2ed63e6-642d-4c75-acfc-1eff951d33bf.jpg");
    }

    public static Image getRoomListBackgroundImage() {
        Image img = loadImage("rooms/img_new.png");
        if (img == null) {
            img = loadImage("rooms/img (1).webp");
        }
        if (img == null) {
            img = loadImage("rooms/room_bg.png");
        }
        if (img == null) {
            img = loadImage("rooms/img.webp");
        }
        return img;
    }

    public static Image getLogoImage() {
        return loadImage("logo/logo_transparent.png");
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
        String col = (color == null || color.equalsIgnoreCase("blue")) ? "blue" : "red";
        return loadImage("png cards/card backs/card back " + col + ".png");
    }

    public static Image getChipImageForValue(int amount) {
        if (amount >= 1000) return loadImage("chips/chip_orange_1000.png");
        if (amount >= 500) return loadImage("chips/chip_purple_500.png");
        if (amount >= 100) return loadImage("chips/chip_black_100.png");
        if (amount >= 25) return loadImage("chips/chip_green_25.png");
        if (amount >= 5) return loadImage("chips/chip_red_5.png");
        if (amount >= 1) return loadImage("chips/chip_white_1.png");
        return loadImage("chips/chip_poker.png");
    }

    public static Image getChipStackImage() {
        return loadImage("chips/chip_stack.png");
    }
}
