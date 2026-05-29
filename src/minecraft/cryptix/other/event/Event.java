package cryptix.other.event;

import cryptix.Client;
import cryptix.module.ModuleManager;

public abstract class Event {
	protected boolean cancelled;
	
	public void call() {
		Client.instance.moduleManager.onEvent(this);
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
}