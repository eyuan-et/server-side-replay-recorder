package com.thecolonel63.serversidereplayrecorder.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketCallbacks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.thecolonel63.serversidereplayrecorder.server.ServerSideReplayRecorderServer.connectionPlayerThreadRecorderMap;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(method = "send(Lnet/minecraft/network/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At("TAIL"))
    private void sendPacketToClient(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo ci) {

        //Get the recorder instance dedicated to this connection and give it the packet to record.
        //If there is no recorder instance for this connection, don't do anything.
        if (!connectionPlayerThreadRecorderMap.containsKey((ClientConnection) (Object) this)) {
            return;
        }
        connectionPlayerThreadRecorderMap.get((ClientConnection) (Object) this).onPacket(packet);
    }

    @Inject(method = "handleDisconnection", at = @At("HEAD"))
    private void handleDisconnectionOfRecorder(CallbackInfo ci) {
        //Tell the recorder to handle a disconnect, if there *is* a recorder.
        if (!connectionPlayerThreadRecorderMap.containsKey((ClientConnection) (Object) this)) {
            return;
        }
        connectionPlayerThreadRecorderMap.get((ClientConnection) (Object) this).handleDisconnect();
    }

}
