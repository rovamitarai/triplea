package games.strategy.engine.framework.startup.ui;

import java.util.Set;

import org.triplea.game.server.HeadlessGameServer;
import org.triplea.lobby.common.IRemoteHostUtils;

import games.strategy.engine.message.MessageContext;
import games.strategy.net.INode;
import games.strategy.net.IServerMessenger;

final class RemoteHostUtils implements IRemoteHostUtils {
  private final INode serverNode;
  private final IServerMessenger serverMessenger;

  RemoteHostUtils(final INode serverNode, final IServerMessenger gameServerMessenger) {
    this.serverNode = serverNode;
    serverMessenger = gameServerMessenger;
  }

  @Override
  public String getConnections() {
    if (!MessageContext.getSender().equals(serverNode)) {
      return "Not accepted!";
    }
    if (serverMessenger != null) {
      final StringBuilder sb = new StringBuilder("Connected: " + serverMessenger.isConnected() + "\n" + "Nodes: \n");
      final Set<INode> nodes = serverMessenger.getNodes();
      if (nodes == null) {
        sb.append("  null\n");
      } else {
        for (final INode node : nodes) {
          sb.append("  ").append(node).append("\n");
        }
      }
      return sb.toString();
    }
    return "Not a server.";
  }

  @Override
  public String getChatLogHeadlessHostBot(final String hashedPassword, final String salt) {
    if (!MessageContext.getSender().equals(serverNode)) {
      return "Not accepted!";
    }
    final HeadlessGameServer instance = HeadlessGameServer.getInstance();
    if (instance == null) {
      return "Not a headless host bot!";
    }
    return instance.remoteGetChatLog(hashedPassword, salt);
  }

  @Override
  public String mutePlayerHeadlessHostBot(final String playerNameToBeMuted, final int minutes,
      final String hashedPassword, final String salt) {
    if (!MessageContext.getSender().equals(serverNode)) {
      return "Not accepted!";
    }
    final HeadlessGameServer instance = HeadlessGameServer.getInstance();
    if (instance == null) {
      return "Not a headless host bot!";
    }
    return instance.remoteMutePlayer(playerNameToBeMuted, minutes, hashedPassword, salt);
  }

  @Override
  public String bootPlayerHeadlessHostBot(final String playerNameToBeBooted, final String hashedPassword,
      final String salt) {
    if (!MessageContext.getSender().equals(serverNode)) {
      return "Not accepted!";
    }
    final HeadlessGameServer instance = HeadlessGameServer.getInstance();
    if (instance == null) {
      return "Not a headless host bot!";
    }
    return instance.remoteBootPlayer(playerNameToBeBooted, hashedPassword, salt);
  }

  @Override
  public String banPlayerHeadlessHostBot(final String playerNameToBeBanned, final int hours,
      final String hashedPassword, final String salt) {
    if (!MessageContext.getSender().equals(serverNode)) {
      return "Not accepted!";
    }
    final HeadlessGameServer instance = HeadlessGameServer.getInstance();
    if (instance == null) {
      return "Not a headless host bot!";
    }
    return instance.remoteBanPlayer(playerNameToBeBanned, hours, hashedPassword, salt);
  }

  @Override
  public String stopGameHeadlessHostBot(final String hashedPassword, final String salt) {
    if (!MessageContext.getSender().equals(serverNode)) {
      return "Not accepted!";
    }
    final HeadlessGameServer instance = HeadlessGameServer.getInstance();
    if (instance == null) {
      return "Not a headless host bot!";
    }
    return instance.remoteStopGame(hashedPassword, salt);
  }

  @Override
  public String shutDownHeadlessHostBot(final String hashedPassword, final String salt) {
    if (!MessageContext.getSender().equals(serverNode)) {
      return "Not accepted!";
    }
    final HeadlessGameServer instance = HeadlessGameServer.getInstance();
    if (instance == null) {
      return "Not a headless host bot!";
    }
    return HeadlessGameServer.remoteShutdown(hashedPassword, salt);
  }

  @Override
  public String getSalt() {
    if (!MessageContext.getSender().equals(serverNode)) {
      return "Not accepted!";
    }
    final HeadlessGameServer instance = HeadlessGameServer.getInstance();
    if (instance == null) {
      return "Not a headless host bot!";
    }
    return HeadlessGameServer.getSalt();
  }
}
