package fr.insee.pearljam.batch.dto;

import java.util.List;

public class HabilitatedUsers {

    private List<HabilitatedUser> personnes;

    public HabilitatedUsers() {
        super();
    }

    public List<HabilitatedUser> getPersonnes() {
        return personnes;
    }

    public void setPersonnes(List<HabilitatedUser> personnes) {
        this.personnes = personnes;
    }

}
