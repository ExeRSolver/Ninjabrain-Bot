package ninjabrainbot.model.actions.divine;

import ninjabrainbot.model.actions.IAction;
import ninjabrainbot.model.datastate.divine.IDivinable;
import ninjabrainbot.model.datastate.divine.IDivineContext;

public class RemoveDivineAction implements IAction {

    private final IDivineContext divineContext;
    private final IDivinable divine;

    public RemoveDivineAction(IDivineContext divineContext, IDivinable divine) {
        this.divineContext = divineContext;
        this.divine = divine;
    }

    @Override
    public void execute() {
        divineContext.removeDivineObject(divine);
    }

}
