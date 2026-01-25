package dev.piyush.shyamduels.party;

import java.util.UUID;

public class PartyInvite {

    private final UUID partyId;
    private final UUID senderId;
    private final UUID targetId;
    private final long expirationTime;

    public PartyInvite(UUID partyId, UUID senderId, UUID targetId, long expirationSeconds) {
        this.partyId = partyId;
        this.senderId = senderId;
        this.targetId = targetId;
        this.expirationTime = System.currentTimeMillis() + (expirationSeconds * 1000);
    }

    public UUID getPartyId() {
        return partyId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public UUID getTargetId() {
        return targetId;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expirationTime;
    }

    public long getRemainingTime() {
        return Math.max(0, expirationTime - System.currentTimeMillis()) / 1000;
    }
}
