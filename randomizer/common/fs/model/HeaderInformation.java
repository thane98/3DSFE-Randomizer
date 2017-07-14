package randomizer.common.fs.model;

class HeaderInformation
{
	private boolean hasSubheader;
	private boolean isRoutine = false;
	private boolean unknownCheck = false;
	private int eventType;

	HeaderInformation() {}

	int getEventType() {
		return eventType;
	}

	void setEventType(int eventType) {
		this.eventType = eventType;
	}

	boolean hasSubheader() {
		return hasSubheader;
	}

	void setHasSubheader(boolean hasSubheader) {
		this.hasSubheader = hasSubheader;
	}

	boolean isRoutine() {
		return isRoutine;
	}

	void setRoutine() {
		this.isRoutine = true;
	}

	boolean unknownCheck() {
		return unknownCheck;
	}

	void setUnknownCheck(boolean unknownCheck) {
		this.unknownCheck = unknownCheck;
	}
}
