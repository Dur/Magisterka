package pl.dur.client.view;

import pl.dur.client.controllers.ConnectionPopupController;

public class ConnectionPopupView extends View{

	private static final String FILENAME_STRING = "ConnectionPopup.fxml";
	@Override
	protected String getViewFileName() {
		return FILENAME_STRING;
	}

	@Override
	protected void doExtraWorkAfterInitalisation() {
	}

	@Override
	public void onReload() {
		((ConnectionPopupController) getController()).reload();
	}

}
