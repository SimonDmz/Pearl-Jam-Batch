package fr.insee.pearljam.batch.dto;

public class KeycloakResponseDto {
	private String access_token;

	public KeycloakResponseDto() {
		super();
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	
}
