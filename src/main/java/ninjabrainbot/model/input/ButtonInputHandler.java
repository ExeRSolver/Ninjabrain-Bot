package ninjabrainbot.model.input;

import ninjabrainbot.model.actions.divine.RemoveDivineAction;
import ninjabrainbot.model.datastate.IDataState;
import ninjabrainbot.model.actions.IActionExecutor;
import ninjabrainbot.model.actions.alladvancements.RemoveStructureAction;
import ninjabrainbot.model.actions.common.ResetAction;
import ninjabrainbot.model.actions.endereye.RemoveEnderEyeThrowAction;
import ninjabrainbot.model.datastate.common.StructurePosition;
import ninjabrainbot.model.datastate.divine.IDivinable;
import ninjabrainbot.model.datastate.endereye.IEnderEyeThrow;
import ninjabrainbot.model.domainmodel.IDomainModel;

public class ButtonInputHandler implements IButtonInputHandler {

	private final IDomainModel domainModel;
	private final IDataState dataState;
	private final IActionExecutor actionExecutor;

	public ButtonInputHandler(IDomainModel domainModel, IDataState dataState, IActionExecutor actionExecutor) {
		this.domainModel = domainModel;
		this.dataState = dataState;
		this.actionExecutor = actionExecutor;
	}

	@Override
	public void onResetButtonPressed() {
		actionExecutor.executeImmediately(new ResetAction(domainModel));
	}

	@Override
	public void onUndoButtonPressed() {
		domainModel.undoUnderWriteLock();
	}

	@Override
	public void onRedoButtonPressed() {
		domainModel.redoUnderWriteLock();
	}

	@Override
	public void onRemoveDivineButtonPressed(IDivinable divineToRemove) {
		actionExecutor.executeImmediately(new RemoveDivineAction(dataState.getDivineContext(), divineToRemove));
	}

	@Override
	public void onRemoveThrowButtonPressed(IEnderEyeThrow throwToRemove) {
		actionExecutor.executeImmediately(new RemoveEnderEyeThrowAction(dataState, throwToRemove));
	}

	@Override
	public void onRemoveAllAdvancementsStructureButtonPressed(StructurePosition structurePosition) {
		actionExecutor.executeImmediately(new RemoveStructureAction(dataState.allAdvancementsDataState(), structurePosition));
	}

}
