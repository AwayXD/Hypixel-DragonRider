package xyz.phantomac.dragonrider;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.phantomac.dragonrider.utils.ChatUtils;

@Mod(modid = DragonRider.MODID, name = DragonRider.NAME, version = DragonRider.VERSION)
public class DragonRider {

    public static final String MODID = "dragonrider";
    public static final String NAME = "Dragon Rider";
    public static final String VERSION = "1.7";

    private boolean enabled = true;
    private boolean circling = false;

    private EntityDragon entityDragon = null;
    private long dragonSpawnTime = 0L;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (!enabled) return;

        String cleanMessage = ChatUtils.getHypixelMessage(event.message);
        if (cleanMessage != null && cleanMessage.toLowerCase().contains("you won! want to play again? click here!")) {
            spawnDragon();
        }
    }
    private void spawnDragon() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;

        if (entityDragon == null) {
            entityDragon = new EntityDragon(mc.theWorld);
            mc.theWorld.addEntityToWorld(-1, entityDragon);
        }

        dragonSpawnTime = System.currentTimeMillis();
//        mc.thePlayer.addChatMessage(new ChatComponentText(
//                EnumChatFormatting.GREEN + "[DragonRider] " +
//                        EnumChatFormatting.AQUA + "Dragon spawned below you!"
//        ));
        // for debug purposes, can uncomment if you'd like - Away
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (entityDragon == null || mc.thePlayer == null || mc.theWorld == null) return;

        if (System.currentTimeMillis() - dragonSpawnTime > 5000) {
            mc.theWorld.removeEntityFromWorld(-1);
            entityDragon = null;
            return;
        }

        EntityPlayerSP player = mc.thePlayer;

        double x, y, z;
        if (circling) {
            double radius = 6.0;
            float angle = ((System.currentTimeMillis() - dragonSpawnTime) / 100F) % 360;
            double rad = Math.toRadians(angle);
            x = player.posX + radius * Math.cos(rad);
            z = player.posZ + radius * Math.sin(rad);
            y = player.posY - 3.0;
            entityDragon.rotationYaw = (float) Math.toDegrees(Math.atan2(player.posZ - z, player.posX - x)) - 90F;
        } else {
            x = player.posX;
            y = player.posY - 4.5;
            z = player.posZ;
            entityDragon.rotationYaw = player.rotationYawHead - 180f;
        }

        entityDragon.setPositionAndRotation(x, y, z, entityDragon.rotationYaw, player.rotationPitch);
    }

    @SubscribeEvent
    public void onAttack(net.minecraftforge.event.entity.player.AttackEntityEvent event) {
        if (entityDragon != null && event.entity == entityDragon) {
            event.setCanceled(true);
        }
    }
    @Mod.EventHandler
    public void onServerStarting(net.minecraftforge.fml.common.event.FMLServerStartingEvent event) {
        ClientCommandHandler.instance.registerCommand(new CommandDragonSpoof(this));    }

    public void toggle() {
        enabled = !enabled;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText(
                    EnumChatFormatting.GOLD + "[DragonRider] " +
                            EnumChatFormatting.GRAY + "Toggled " +
                            (enabled ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF")
            ));
        }
    }

    public void startCircling() {
        circling = true;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText(
                    EnumChatFormatting.LIGHT_PURPLE + "[DragonRider] " +
                            EnumChatFormatting.AQUA + "Dragon will circle below you!"
            ));
        }
    }

    public void stopCircling() {
        circling = false;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText(
                    EnumChatFormatting.LIGHT_PURPLE + "[DragonRider] " +
                            EnumChatFormatting.AQUA + "Dragon will hover below again."
            ));
        }
    }

    public boolean isEnabled() {
        return enabled;
    }
}
