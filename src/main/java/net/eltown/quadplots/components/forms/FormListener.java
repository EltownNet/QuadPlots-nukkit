package net.eltown.quadplots.components.forms;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Attribute;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.network.protocol.ModalFormRequestPacket;
import cn.nukkit.network.protocol.NetworkStackLatencyPacket;
import cn.nukkit.scheduler.Task;
import cn.nukkit.scheduler.TaskHandler;

import java.util.Random;

public class FormListener implements Listener {

    @EventHandler
    public void on(PlayerFormRespondedEvent event) {
        if (event.getWindow() instanceof FormWindowSimple) FormHandler.handleSimple(event.getPlayer(), (FormWindowSimple) event.getWindow());
        if (event.getWindow() instanceof FormWindowModal) FormHandler.handleModal(event.getPlayer(), (FormWindowModal) event.getWindow());
        if (event.getWindow() instanceof FormWindowCustom) FormHandler.handleCustom(event.getPlayer(), (FormWindowCustom) event.getWindow());
    }


    /*
    * Bilder Fix
    * */

    @EventHandler
    public void on(final DataPacketSendEvent event) {
        if (event.getPacket() instanceof ModalFormRequestPacket) {
            final Player player = event.getPlayer();
            Server.getInstance().getScheduler().scheduleDelayedTask(new Task() {
                final int timesToRequest = 5;
                @Override
                public void onRun( int ticks ) {
                    if ( player.isOnline() ) {
                        onPacketSend(player);
                        if ( player.isOnline() ) {
                            requestUpdate( player );
                                final int[] times = { timesToRequest - 1 };
                                final TaskHandler[] taskHandler = { null };

                                taskHandler[0] = Server.getInstance().getScheduler().scheduleRepeatingTask( new Task() {
                                    @Override
                                    public void onRun( int i ) {
                                        if ( --times[0] >= 0 && player.isOnline() ) {
                                            requestUpdate( player );
                                        } else {
                                            taskHandler[0].cancel();
                                            taskHandler[0] = null;
                                        }
                                    }
                                }, 5 );
                        }
                    }
                }
            }, 1 );
        }
    }


    private void onPacketSend(Player player) {
        long ts = new Random().nextLong();

        NetworkStackLatencyPacket packet = new NetworkStackLatencyPacket();
        packet.timestamp = ts;
        packet.unknownBool = true;
        player.dataPacket( packet );
    }

    private void requestUpdate( Player player ) {
        player.setAttribute( Attribute.getAttribute( Attribute.EXPERIENCE_LEVEL ) );
    }



}
