package ninjabrainbot.model.datastate.divine;

public enum DivineType {

    FOSSIL(false),
    FIRST_PORTAL(false),
    BURIED_TREASURE(true);

    private final boolean allowMultiple;

    DivineType(boolean allowMultiple) {
        this.allowMultiple = allowMultiple;
    }

    public boolean allowMultiple() {
        return this.allowMultiple;
    }

}
