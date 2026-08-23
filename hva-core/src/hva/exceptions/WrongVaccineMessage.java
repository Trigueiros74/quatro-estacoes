package hva.exceptions;


import java.io.Serial;

public class WrongVaccineMessage extends Exception {
    @Serial
    private static final long serialVersionUID = 202407081733L;

    private final String _vaccineKey;
    private final String _animalKey;

    public WrongVaccineMessage(String vaccineKey, String animalKey) {
        _animalKey = animalKey;
        _vaccineKey = vaccineKey;
    }

    public String getVaccineKey(){
        return _vaccineKey;
    }

    public String getAnimalKey(){
        return _animalKey;
    }
}
