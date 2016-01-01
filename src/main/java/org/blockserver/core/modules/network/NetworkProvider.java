/*
 * This file is part of BlockServer.
 *
 * BlockServer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BlockServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BlockServer.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.blockserver.core.modules.network;

import lombok.Getter;
import lombok.Setter;
import org.blockserver.core.Server;
import org.blockserver.core.message.Message;
import org.blockserver.core.module.Module;
import org.blockserver.core.modules.player.Player;
import org.blockserver.core.modules.player.PlayerModule;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Written by Exerosis!
 */
public class NetworkProvider extends Module {
    //@Getter private final Set<RawPacket> packetOutQueue = Collections.unmodifiableSet(Collections.synchronizedSet(new HashSet<>()));
    //@Getter private final Set<Message> messageInQueue = Collections.unmodifiableSet(Collections.synchronizedSet(new HashSet<>()));
    @Getter private final Queue<RawPacket> packetOutQueue = Collections.asLifoQueue(new LinkedBlockingDeque<>());
    @Getter private final Queue<Message> messageInQueue = Collections.asLifoQueue(new LinkedBlockingDeque<>());
    @Getter @Setter private NetworkConverter converter;

    public NetworkProvider(Server server, NetworkConverter converter) {
        super(server);
        this.converter = converter;
    }

    public void queueInboundPackets(Collection<RawPacket> packets) {
        getServer().getExecutorService().execute(() -> messageInQueue.addAll(converter.toMessages(packets)));
    }

    public void queueOutboundMessages(Collection<Message> messages) {
        packetOutQueue.addAll(converter.toPackets(messages));
    }

    public void queueOutboundMessages(Message... messages) {
        queueOutboundMessages(Arrays.asList(messages));
    }

    public void queueInboundPackets(RawPacket... packets) {
        queueInboundPackets(Arrays.asList(packets));
    }

    public Collection<Message> receiveInboundMessages() {
        Set<Message> packets = new HashSet<>(messageInQueue);
        messageInQueue.clear();
        return packets;
    }

    public Collection<RawPacket> receiveOutboundPackets() {
        Set<RawPacket> packets = new HashSet<>(packetOutQueue);
        packetOutQueue.clear();
        return packets;
    }

    protected final void sessionOpened(InetSocketAddress address) {
        Player player = new Player(getServer(), address);
        getServer().getModule(PlayerModule.class).internalOpenSession(player);
    }
}