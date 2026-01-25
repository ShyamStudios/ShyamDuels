package dev.piyush.shyamduels.party;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PartySplitManager {

    private final Map<UUID, PartySplit> splits = new ConcurrentHashMap<>();

    public void createSplit(Party party) {
        PartySplit split = new PartySplit(party);
        splits.put(party.getPartyId(), split);
    }

    public PartySplit getSplit(Party party) {
        return splits.get(party.getPartyId());
    }

    public void removeSplit(Party party) {
        splits.remove(party.getPartyId());
    }

    public boolean hasSplit(Party party) {
        return splits.containsKey(party.getPartyId());
    }

    public static class PartySplit {
        private final Party party;
        private final Set<UUID> teamA = new LinkedHashSet<>();
        private final Set<UUID> teamB = new LinkedHashSet<>();

        public PartySplit(Party party) {
            this.party = party;
        }

        public Party getParty() {
            return party;
        }

        public Set<UUID> getTeamA() {
            return teamA;
        }

        public Set<UUID> getTeamB() {
            return teamB;
        }

        public void addToTeamA(UUID uuid) {
            teamB.remove(uuid);
            teamA.add(uuid);
        }

        public void addToTeamB(UUID uuid) {
            teamA.remove(uuid);
            teamB.add(uuid);
        }

        public void removeFromTeams(UUID uuid) {
            teamA.remove(uuid);
            teamB.remove(uuid);
        }

        public boolean isInTeamA(UUID uuid) {
            return teamA.contains(uuid);
        }

        public boolean isInTeamB(UUID uuid) {
            return teamB.contains(uuid);
        }

        public boolean isAssigned(UUID uuid) {
            return teamA.contains(uuid) || teamB.contains(uuid);
        }

        public boolean isValid() {
            int totalAssigned = teamA.size() + teamB.size();
            return totalAssigned >= 2 && teamA.size() == teamB.size();
        }

        public void autoSplit() {
            teamA.clear();
            teamB.clear();
            List<UUID> members = new ArrayList<>(party.getMembers());
            Collections.shuffle(members);
            int half = members.size() / 2;
            for (int i = 0; i < members.size(); i++) {
                if (i < half) {
                    teamA.add(members.get(i));
                } else {
                    teamB.add(members.get(i));
                }
            }
        }

        public void clear() {
            teamA.clear();
            teamB.clear();
        }
    }
}
