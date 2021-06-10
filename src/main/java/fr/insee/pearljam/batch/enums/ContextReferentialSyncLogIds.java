package fr.insee.pearljam.batch.enums;

public enum ContextReferentialSyncLogIds {
	YES("YES"),
	NO("NO"),
	IN_SEPARATE_FILES("IN_SEPARATE_FILES");
	
	/**
	 * label of the BatchOption
	 */
	private String label;

	/**
	 * Defaut constructor for BatchOption
	 * @param label
	 */
	ContextReferentialSyncLogIds(String label) {
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
