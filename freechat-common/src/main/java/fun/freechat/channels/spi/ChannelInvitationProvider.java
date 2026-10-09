package fun.freechat.channels.spi;

public interface ChannelInvitationProvider {
    String username(String instanceId);

    String invitationLink(String instanceId);
}
