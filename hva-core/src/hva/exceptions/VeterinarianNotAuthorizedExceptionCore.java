package hva.exceptions;


import java.io.Serial;

public class VeterinarianNotAuthorizedExceptionCore extends Exception {
    @Serial
    private static final long serialVersionUID = 202407081733L;

    private final String _vetKey;
    private final String _speciesKey;

    public VeterinarianNotAuthorizedExceptionCore(String vetKey,String speciesKey) {
        _vetKey= vetKey;
        _speciesKey = speciesKey;
    }

    public String getVetKey(){
        return _vetKey;
    }

    public String getSpeciesKey(){
        return _speciesKey;
    }
}
