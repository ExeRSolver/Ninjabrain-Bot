package ninjabrainbot.data;

import ninjabrainbot.data.datalock.IModificationLock;
import ninjabrainbot.data.divine.BuriedTreasure;
import ninjabrainbot.data.divine.FirstPortal;
import ninjabrainbot.data.divine.Fossil;
import ninjabrainbot.data.endereye.IThrow;
import ninjabrainbot.event.ISubscribable;

public interface IDataStateHandler {

	IDataState getDataState();

	IModificationLock getModificationLock();

	void reset();

	void resetIfNotLocked();

	void undo();

	void undoIfNotLocked();

	void removeThrow(IThrow t);

	void resetDivineContext();

	void changeLastAngleIfNotLocked(double delta);

	void toggleAltStdOnLastThrowIfNotLocked();

	public ISubscribable<IDataState> whenDataStateModified();

	public void addThrowStream(ISubscribable<IThrow> stream);

	public void addFirstPortalStream(ISubscribable<FirstPortal> stream);

	public void addBuriedTreasureStream(ISubscribable<BuriedTreasure> stream);

	public void addFossilStream(ISubscribable<Fossil> stream);

}
