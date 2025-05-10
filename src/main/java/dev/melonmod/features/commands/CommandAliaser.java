package dev.melonmod.features.commands;

import dev.melonmod.features.Feature;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;

public class CommandAliaser extends Feature {
    public CommandAliaser() {
        init("commandaliaser", "Command Autoaliaser", "Manages command aliasing via packet manipulation");
    }

    @Override
    public void handlePacket(Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof CommandTreeS2CPacket commandTreeS2CPacket) {
            //Mod.log(commandTreeS2CPacket.getCommandTree());
        }
    }

    @Override
    public void sendPacket(Packet<?> packet, CallbackInfo ci) {
    }
}
