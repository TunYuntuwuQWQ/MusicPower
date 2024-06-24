package nya.tuyw.musicpower.events;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;
import nya.tuyw.musicpower.MusicPower;
import nya.tuyw.musicpower.init.ConfigHandler;

@Mod.EventBusSubscriber(modid = MusicPower.MODID)
public class PlaySoundEventHandler {
    @SubscribeEvent
    public static void onPlaySound(PlaySoundEvent event) {
        SoundInstance soundInstance = event.getSound();
        if (soundInstance == null) {
            MusicPower.LOGGER.error("SoundInstance is null.");
            return;
        }
        BlockPos soundPos = new BlockPos((int) soundInstance.getX(), (int) soundInstance.getY(), (int) soundInstance.getZ());
        ResourceLocation soundType = soundInstance.getLocation();

        for (ConfigHandler.EffectConfig effectConfig : ConfigHandler.getEffectConfigs()) {
            if (effectConfig.getSound().equals(soundType.toString())) {
                MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                if (server != null) {
                    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                        double distanceSq = player.blockPosition().distSqr(soundPos);
                        if (distanceSq < effectConfig.getDistance() * effectConfig.getDistance()) {
                            MobEffect mobEffect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(effectConfig.getBuff()));
                            if (mobEffect != null) {
                                int buffStrength = effectConfig.getStrength() - 1;
                                int buffDuration = effectConfig.getDuration() * 20;
                                if (effectConfig.getStrengthDecaywithDistance() || effectConfig.getDurarionDecaywithDistance()){
                                    double distance = Math.sqrt(distanceSq);
                                    double maxDistance = effectConfig.getDistance();
                                    double distanceF = (maxDistance - distance) / maxDistance;
                                    if (effectConfig.getStrengthDecaywithDistance()) {
                                        buffStrength = (int) ((buffStrength + 1) * distanceF);
                                    }
                                    if (effectConfig.getDurarionDecaywithDistance()) {
                                        buffDuration = (int) (buffDuration * distanceF);
                                    }
                                }
                                if (buffDuration > 0 && buffStrength >= 0) {
                                    player.addEffect(new MobEffectInstance(mobEffect, buffDuration, buffStrength));
                                    if (!effectConfig.getSendMessage().isEmpty()){
                                        player.sendSystemMessage(Component.literal(effectConfig.getSendMessage()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
