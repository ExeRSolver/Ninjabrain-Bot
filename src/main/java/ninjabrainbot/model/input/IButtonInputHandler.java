package ninjabrainbot.model.input;

import ninjabrainbot.model.datastate.common.StructurePosition;
import ninjabrainbot.model.datastate.divine.IDivinable;
import ninjabrainbot.model.datastate.endereye.IEnderEyeThrow;

public interface IButtonInputHandler {

	void onResetButtonPressed();

	void onUndoButtonPressed();

	void onRedoButtonPressed();

	void onRemoveDivineButtonPressed(IDivinable divineToRemove);

	void onRemoveThrowButtonPressed(IEnderEyeThrow throwToRemove);

	void onRemoveAllAdvancementsStructureButtonPressed(StructurePosition structurePosition);

}
