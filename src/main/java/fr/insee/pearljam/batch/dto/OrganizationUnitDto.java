package fr.insee.pearljam.batch.dto;

public class OrganizationUnitDto {
	
	private Long idEtab;
	private String timbreEtab;
	private String codeEtab;
	private String typeEtab;
	private String rges;
	private String origineEtab;
	private String nomEtab;
	private String libelleCourt;
	private Float latitudeMinimum;
	private Float latitudeMaximum;
	private Float longitudeMinimum;
	private Float longitudeMaximum;

	public OrganizationUnitDto() {
		super();
	}
	
	public Long getIdEtab() {
		return idEtab;
	}


	public void setIdEtab(Long idEtab) {
		this.idEtab = idEtab;
	}


	public String getTimbreEtab() {
		return timbreEtab;
	}


	public void setTimbreEtab(String timbreEtab) {
		this.timbreEtab = timbreEtab;
	}


	public String getCodeEtab() {
		return codeEtab;
	}


	public void setCodeEtab(String codeEtab) {
		this.codeEtab = codeEtab;
	}


	public String getTypeEtab() {
		return typeEtab;
	}


	public void setTypeEtab(String typeEtab) {
		this.typeEtab = typeEtab;
	}


	public String getRges() {
		return rges;
	}


	public void setRges(String rges) {
		this.rges = rges;
	}


	public String getOrigineEtab() {
		return origineEtab;
	}


	public void setOrigineEtab(String origineEtab) {
		this.origineEtab = origineEtab;
	}


	public String getNomEtab() {
		return nomEtab;
	}


	public void setNomEtab(String nomEtab) {
		this.nomEtab = nomEtab;
	}


	public String getLibelleCourt() {
		return libelleCourt;
	}


	public void setLibelleCourt(String libelleCourt) {
		this.libelleCourt = libelleCourt;
	}


	public Float getLatitudeMinimum() {
		return latitudeMinimum;
	}


	public void setLatitudeMinimum(Float latitudeMinimum) {
		this.latitudeMinimum = latitudeMinimum;
	}


	public Float getLatitudeMaximum() {
		return latitudeMaximum;
	}


	public void setLatitudeMaximum(Float latitudeMaximum) {
		this.latitudeMaximum = latitudeMaximum;
	}


	public Float getLongitudeMinimum() {
		return longitudeMinimum;
	}


	public void setLongitudeMinimum(Float longitudeMinimum) {
		this.longitudeMinimum = longitudeMinimum;
	}


	public Float getLongitudeMaximum() {
		return longitudeMaximum;
	}


	public void setLongitudeMaximum(Float longitudeMaximum) {
		this.longitudeMaximum = longitudeMaximum;
	}


	
}
