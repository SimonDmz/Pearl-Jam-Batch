package fr.insee.pearljam.batch.dto;

public class HabilitationActionResponseDto {

    private String action;
    private String erreur;

    public HabilitationActionResponseDto() {
        super();
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getErreur() {
        return erreur;
    }

    public void setErreur(String erreur) {
        this.erreur = erreur;
    }

}
