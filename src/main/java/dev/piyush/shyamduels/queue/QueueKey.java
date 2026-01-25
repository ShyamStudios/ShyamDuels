package dev.piyush.shyamduels.queue;

import java.util.Objects;

public final class QueueKey {
    private final String kitName;
    private final QueueMode mode;

    public QueueKey(String kitName, QueueMode mode) {
        this.kitName = kitName;
        this.mode = mode;
    }

    public String getKitName() {
        return kitName;
    }

    public QueueMode getMode() {
        return mode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        QueueKey queueKey = (QueueKey) o;
        return Objects.equals(kitName, queueKey.kitName) && mode == queueKey.mode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(kitName, mode);
    }
}
