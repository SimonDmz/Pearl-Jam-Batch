package fr.insee.pearljam.batch.enums;

public enum BatchOption {
	LOADCAMPAIGN("LOADCAMPAIGN"),
	DELETECAMPAIGN("DELETECAMPAIGN"),
	LOADCONTEXT("LOADCONTEXT"),
	DAILYUPDATE("DAILYUPDATE"),
	SYNCHRONIZE("SYNCHRONIZE");
	
	/**
	 * label of the BatchOption
	 */
	private String label;

	/**
	 * Defaut constructor for BatchOption
	 * @param label
	 */
	BatchOption(String label) {
		this.label = label;
	}

	/**
	 * Get the label for BatchOption
	 * @return label
	 */
	public String getLabel() {
		return label;
	}
}
