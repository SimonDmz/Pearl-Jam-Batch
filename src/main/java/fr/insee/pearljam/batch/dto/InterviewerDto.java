package fr.insee.pearljam.batch.dto;

public class InterviewerDto {
	
	private String idep;
	private Long idSirh;
	private String nom;
	private String prenom;
	private String sexe;
	private String mailInsee;
	private String telInsee;
	private String telAutre;
	private String poleGestionCourant;
	
	public InterviewerDto() {
		super();
	}
	
	public String getIdep() {
		return idep;
	}
	public void setIdep(String idep) {
		this.idep = idep;
	}
	public Long getIdSirh() {
		return idSirh;
	}
	public void setIdSirh(Long idSirh) {
		this.idSirh = idSirh;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getPrenom() {
		return prenom;
	}
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
	public String getSexe() {
		return sexe;
	}
	public void setSexe(String sexe) {
		this.sexe = sexe;
	}
	public String getMailInsee() {
		return mailInsee;
	}
	public void setMailInsee(String mailInsee) {
		this.mailInsee = mailInsee;
	}
	public String getTelInsee() {
		return telInsee;
	}
	public void setTelInsee(String telInsee) {
		this.telInsee = telInsee;
	}
	public String getTelAutre() {
		return telAutre;
	}
	public void setTelAutre(String telAutre) {
		this.telAutre = telAutre;
	}
	public String getPoleGestionCourant() {
		return poleGestionCourant;
	}
	public void setPoleGestionCourant(String poleGestionCourant) {
		this.poleGestionCourant = poleGestionCourant;
	}
	
}
