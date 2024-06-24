package nya.tuyw.musicpower.init;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.fml.loading.FMLPaths;
import nya.tuyw.musicpower.MusicPower;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ConfigHandler {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("MusicPower");
    private static final File CONFIG_FILE = CONFIG_PATH.resolve("Config-SoundsEffect.json").toFile();
    private static List<EffectConfig> effectConfigs;

    static {
        if (!CONFIG_FILE.exists()){
            try {
                Files.createDirectories(CONFIG_PATH);
                writeDefaultConfig();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else {
            loadConfig();
        }
    }

    private static void writeDefaultConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            effectConfigs = List.of(
                    new EffectConfig("minecraft:example.anvil.place","minecraft:strength",1,60,16,true,false,""),
                    new EffectConfig("musicpower:example.sound","musicpower:examplebuff",5,10,32,false,true,"I love this mod!")
            );
            String json = GSON.toJson(effectConfigs);
            writer.write(json);
        } catch (IOException e) {
            MusicPower.LOGGER.error("Failed to initialize config file", e);
        }
    }
    private static void loadConfig() {
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            Type listType = new TypeToken<List<EffectConfig>>() {}.getType();
            effectConfigs = GSON.fromJson(reader, listType);
        } catch (IOException e) {
            MusicPower.LOGGER.error("Failed to load config file", e);
            throw new RuntimeException(e);
        }
    }

    public static List<EffectConfig> getEffectConfigs() {
        return effectConfigs;
    }

    public static class EffectConfig {
        private String Sound;
        private String Buff;
        private int Strength;
        private int Duration;
        private int Distance;
        private boolean StrengthDecayWithDistance;
        private boolean DurationDecayWithDistance;
        private String SendMessage;
        private EffectConfig(String sound, String buff, int strength, int duration,int distance,boolean strengthDecayWithDistance,boolean durationDecayWithDistance,String sendMessage) {
            this.Sound = sound;
            this.Buff = buff;
            this.Strength = strength;
            this.Duration = duration;
            this.Distance = distance;
            this.StrengthDecayWithDistance = strengthDecayWithDistance;
            this.DurationDecayWithDistance = durationDecayWithDistance;
            this.SendMessage = sendMessage;
        }

        public String getSound() {
            return Sound;
        }
        public String getBuff() {
            return Buff;
        }
        public int getStrength() {
            return Strength;
        }
        public int getDuration() {
            return Duration;
        }
        public int getDistance(){
            return Distance;
        }
        public boolean getStrengthDecaywithDistance(){
            return StrengthDecayWithDistance;
        }
        public boolean getDurarionDecaywithDistance(){
            return DurationDecayWithDistance;
        }
        public String getSendMessage(){
            return SendMessage;
        }
    }
}
