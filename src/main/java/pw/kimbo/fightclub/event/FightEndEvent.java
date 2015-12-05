package pw.kimbo.fightclub.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pw.kimbo.fightclub.lib.Fight;


public class FightEndEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Fight fight;

    public FightEndEvent(Fight fight) {
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
