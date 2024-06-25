package nya.tuyw.musicpower;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import nya.tuyw.musicpower.client.PlaySoundEventHandler;
import org.slf4j.Logger;

@Mod(MusicPower.MODID)
public class MusicPower {

    public static final String MODID = "musicpower";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public MusicPower() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        NETWORK.registerMessage(0, PlaySoundEventHandler.SoundEventMessage.class,
                PlaySoundEventHandler.SoundEventMessage::toBytes,
                PlaySoundEventHandler.SoundEventMessage::new,
                PlaySoundEventHandler.SoundEventMessage::handle
        );
    }
}