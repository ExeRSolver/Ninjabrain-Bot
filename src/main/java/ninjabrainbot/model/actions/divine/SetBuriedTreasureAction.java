package ninjabrainbot.model.actions.divine;

import ninjabrainbot.model.actions.IAction;
import ninjabrainbot.model.datastate.divine.BuriedTreasure;
import ninjabrainbot.model.datastate.divine.IDivineContext;

public class SetBuriedTreasureAction implements IAction {

	private final IDivineContext divineContext;
	private final BuriedTreasure buriedTreasure;

	public SetBuriedTreasureAction(IDivineContext divineContext, BuriedTreasure buriedTreasure) {
		this.divineContext = divineContext;
		this.buriedTreasure = buriedTreasure;
	}

	@Override
	public void execute() {
		if (!divineContext.buriedTreasures().contains(buriedTreasure))
			divineContext.buriedTreasures().add(buriedTreasure);
	}

}
