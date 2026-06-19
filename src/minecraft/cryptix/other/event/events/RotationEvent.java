package cryptix.other.event.events;

import cryptix.other.event.Event;

public class RotationEvent extends Event{
	private float yaw, pitch;

	public RotationEvent innit(float yaw, float pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
		return this;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
}
