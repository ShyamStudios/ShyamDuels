package dev.piyush.shyamduels.party;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Party {

    private final UUID partyId;
    private UUID owner;
    private final Set<UUID> members;
    private PartyMode mode;
    private PartyState state;
    private boolean chatEnabled;

    public Party(UUID owner) {
        this.partyId = UUID.randomUUID();
        this.owner = owner;
        this.members = ConcurrentHashMap.newKeySet();
        this.members.add(owner);
        this.mode = PartyMode.PRIVATE;
        this.state = PartyState.LOBBY;
        this.chatEnabled = false;
    }

    public UUID getPartyId() {
        return partyId;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public Set<UUID> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    public List<UUID> getMemberList() {
        return new ArrayList<>(members);
    }

    public int getSize() {
        return members.size();
    }

    public boolean addMember(UUID uuid) {
        return members.add(uuid);
    }

    public boolean removeMember(UUID uuid) {
        return members.remove(uuid);
    }

    public boolean isMember(UUID uuid) {
        return members.contains(uuid);
    }

    public boolean isOwner(UUID uuid) {
        return owner.equals(uuid);
    }

    public PartyMode getMode() {
        return mode;
    }

    public void setMode(PartyMode mode) {
        this.mode = mode;
    }

    public boolean isPublic() {
        return mode == PartyMode.PUBLIC;
    }

    public PartyState getState() {
        return state;
    }

    public void setState(PartyState state) {
        this.state = state;
    }

    public boolean isInLobby() {
        return state == PartyState.LOBBY;
    }

    public boolean isInQueue() {
        return state == PartyState.IN_QUEUE;
    }

    public boolean isInDuel() {
        return state == PartyState.IN_DUEL;
    }

    public boolean isChatEnabled() {
        return chatEnabled;
    }

    public void setChatEnabled(boolean chatEnabled) {
        this.chatEnabled = chatEnabled;
    }

    public void toggleChat() {
        this.chatEnabled = !this.chatEnabled;
    }

    public Player getOwnerPlayer() {
        return Bukkit.getPlayer(owner);
    }

    public List<Player> getOnlineMembers() {
        List<Player> online = new ArrayList<>();
        for (UUID uuid : members) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                online.add(player);
            }
        }
        return online;
    }

    public void broadcast(net.kyori.adventure.text.Component message) {
        for (Player player : getOnlineMembers()) {
            player.sendMessage(message);
        }
    }

    public void broadcast(String legacyMessage) {
        for (Player player : getOnlineMembers()) {
            player.sendMessage(legacyMessage);
        }
    }

    public UUID getNextOwnerCandidate() {
        for (UUID uuid : members) {
            if (!uuid.equals(owner)) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null && player.isOnline()) {
                    return uuid;
                }
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Party party = (Party) o;
        return Objects.equals(partyId, party.partyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partyId);
    }
}
