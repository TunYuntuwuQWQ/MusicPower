package nya.tuyw.musicpower.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import nya.tuyw.musicpower.MusicPower;
import nya.tuyw.musicpower.server.ServerSoundEventHandler;

import java.util.Objects;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = MusicPower.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class PlaySoundEventHandler {
    @SubscribeEvent
    public static void onPlaySound(PlaySoundEvent event) {
        SoundInstance soundInstance = event.getSound();
        if (soundInstance == null) {
            MusicPower.LOGGER.error("SoundInstance is null.");
            return;
        }

        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            sendSoundEvent(soundInstance);
        }
    }
    private static void sendSoundEvent(SoundInstance soundInstance) {
        BlockPos soundPos = new BlockPos((int) soundInstance.getX(), (int) soundInstance.getY(), (int) soundInstance.getZ());
        ResourceLocation soundType = soundInstance.getLocation();

        MusicPower.NETWORK.send(PacketDistributor.SERVER.noArg(), new SoundEventMessage(soundPos, soundType));
    }

    public static class SoundEventMessage {
        private final BlockPos soundPos;
        private final ResourceLocation soundType;

        public SoundEventMessage(BlockPos soundPos, ResourceLocation soundType) {
            this.soundPos = soundPos;
            this.soundType = soundType;
        }

        public SoundEventMessage(FriendlyByteBuf buf) {
            this.soundPos = buf.readBlockPos();
            this.soundType = buf.readResourceLocation();
        }

        public void toBytes(FriendlyByteBuf buf) {
            buf.writeBlockPos(soundPos);
            buf.writeResourceLocation(soundType);
        }

        public static void handle(SoundEventMessage message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                MinecraftServer server = Objects.requireNonNull(ctx.get().getSender()).getServer();
                ServerSoundEventHandler.handleSoundEvent(server, message.soundPos, message.soundType);
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
