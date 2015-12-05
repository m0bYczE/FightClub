package pw.kimbo.fightclub.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pw.kimbo.fightclub.lib.Fight;

public class FightStartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Fight fight;

    public FightStartEvent(Fight fight) {
        this.fight = fight;
    }

    public Fight getFight() {
        return fight;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
