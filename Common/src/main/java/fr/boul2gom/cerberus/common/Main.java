package fr.boul2gom.cerberus.common;

import fr.boul2gom.cerberus.api.AppType;
import fr.boul2gom.cerberus.api.CerberusAPI;
import fr.boul2gom.cerberus.api.events.Event;
import fr.boul2gom.cerberus.api.player.IPlayer;
import fr.boul2gom.cerberus.api.protocol.Channel;
import fr.boul2gom.cerberus.api.protocol.Packet;
import fr.boul2gom.cerberus.api.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

public class Main {

    public static void main(String[] args) throws Exception {
        if (Float.parseFloat(System.getProperty("java.class.version")) < 62.0F) {
            System.err.println("*** ERROR *** Cerberus requires Java >= 18 to work!");
            return;
        }

        new CommonCerberusAPI(AppType.MASTER_STANDALONE, Paths.get("data"));

        Protocol.register(100, TestPacket.class);

        CerberusAPI.get().getProtocol().subscribe(Channel.GLOBAL, TestPacket.class, (channel, packet) -> {
            final Logger logger = LoggerFactory.getLogger(Main.class);

            logger.info("Received test packet: {}, from channel: {}", packet.getName(), channel);
        });
        CerberusAPI.get().getEventBus().subscribe(TestEvent.class, event -> {
            final Logger logger = LoggerFactory.getLogger(Main.class);

            logger.info("Received test event!");
        });

        CerberusAPI.get().getProtocol().publish(Channel.GLOBAL, new TestPacket("Protocol network test packet"));
        CerberusAPI.get().getEventBus().publish(new TestEvent());

        final IPlayer player = CerberusAPI.get().getPlayerManager().get("1d921596-f06f-442e-a299-5a22a0397235");
        player.setName("boul2gom");
        player.setLastIp("127.0.0.1");
        player.save();

        final Logger logger = LoggerFactory.getLogger(Main.class);
        logger.info("Player name: {}", player.getUniqueId());

        new CountDownLatch(1).await();
    }

    public static class TestPacket extends Packet {

        private final String name;

        public TestPacket(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    public static class TestEvent extends Event {}
}
