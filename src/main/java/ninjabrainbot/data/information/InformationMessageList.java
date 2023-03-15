package ninjabrainbot.data.information;

import java.util.HashMap;

import ninjabrainbot.event.ObservableList;
import ninjabrainbot.event.SubscriptionHandler;

public class InformationMessageList extends ObservableList<InformationMessage> {

	HashMap<InformationMessageProvider, InformationMessage> informationMessages;

	SubscriptionHandler sh = new SubscriptionHandler();

	public InformationMessageList() {
		informationMessages = new HashMap<>();
	}

	public void AddInformationMessageProvider(InformationMessageProvider informationMessageProvider) {
		sh.add(informationMessageProvider.subscribe(message -> whenInformationMessageChanged(informationMessageProvider, message)));
	}

	private void whenInformationMessageChanged(InformationMessageProvider provider, InformationMessage message) {
		if (informationMessages.containsKey(provider)) {
			remove(informationMessages.get(provider));
			informationMessages.remove(provider);
		}
		if (message != null) {
			add(message);
			informationMessages.put(provider, message);
		}
	}

}