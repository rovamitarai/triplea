package games.strategy.net.nio;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.logging.Level;

import games.strategy.net.AbstractServerMessenger;
import games.strategy.net.ILoginValidator;
import games.strategy.net.MessageHeader;
import games.strategy.net.Node;
import lombok.extern.java.Log;

/**
 * Server-side implementation of {@link QuarantineConversation}.
 */
@Log
public class ServerQuarantineConversation extends QuarantineConversation {
  /*
   * Communication sequence
   * 1) server reads client name
   * 2) server sends challenge (or null if no challenge is to be made)
   * 3) server reads response (or null if no challenge)
   * 4) server send null then client name and node info on success, or an error message if there is an error
   * 5) if the client reads an error message, the client sends an acknowledgment (we need to make sur the client gets
   * the message before
   * closing the socket).
   */

  private enum Step {
    READ_NAME, READ_MAC, CHALLENGE, ACK_ERROR
  }

  private final ILoginValidator validator;
  private final SocketChannel channel;
  private final NioSocket socket;
  private Step step = Step.READ_NAME;
  private String remoteName;
  private String remoteMac;
  private Map<String, String> challenge;
  private final AbstractServerMessenger serverMessenger;

  public ServerQuarantineConversation(final ILoginValidator validator, final SocketChannel channel,
      final NioSocket socket, final AbstractServerMessenger serverMessenger) {
    this.validator = validator;
    this.socket = socket;
    this.channel = channel;
    this.serverMessenger = serverMessenger;
  }

  public String getRemoteName() {
    return remoteName;
  }

  @Override
  public Action message(final Object o) {
    try {
      switch (step) {
        case READ_NAME:
          // read name, send challenge
          remoteName = (String) o;
          step = Step.READ_MAC;
          return Action.NONE;
        case READ_MAC:
          // read name, send challenge
          remoteMac = (String) o;
          if (validator != null) {
            challenge = validator.getChallengeProperties(remoteName);
          }
          send((Serializable) challenge);
          step = Step.CHALLENGE;
          return Action.NONE;
        case CHALLENGE:
          @SuppressWarnings("unchecked")
          final Map<String, String> response = (Map<String, String>) o;
          if (validator != null) {
            final String error = validator.verifyConnection(challenge, response, remoteName, remoteMac,
                channel.socket().getRemoteSocketAddress());
            send(error);
            if (error != null) {
              step = Step.ACK_ERROR;
              return Action.NONE;
            }
          } else {
            send(null);
          }
          // get a unique name
          remoteName = serverMessenger.getUniqueName(remoteName);
          // send the node its name and our name
          send(new String[] {remoteName, serverMessenger.getLocalNode().getName()});
          // send the node its and our address as we see it
          send(new InetSocketAddress[] {(InetSocketAddress) channel.socket().getRemoteSocketAddress(),
              serverMessenger.getLocalNode().getSocketAddress()});
          // Login succeeded, so notify the ServerMessenger about the login with the name, mac, etc.
          serverMessenger.notifyPlayerLogin(remoteName, remoteMac);
          // We are good
          return Action.UNQUARANTINE;
        case ACK_ERROR:
          return Action.TERMINATE;
        default:
          throw new IllegalStateException("Invalid state");
      }
    } catch (final Throwable t) {
      log.log(Level.SEVERE, "Error with connection", t);
      return Action.TERMINATE;
    }
  }

  private void send(final Serializable object) {
    // this messenger is quarantined, so to and from don't matter
    final MessageHeader header = new MessageHeader(Node.NULL_NODE, Node.NULL_NODE, object);
    socket.send(channel, header);
  }

  @Override
  public void close() {}
}
