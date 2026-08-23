package hva.exceptions;


import java.io.Serial;

public class DuplicateHabitatKeyExceptionCore extends Exception {
    @Serial
    private static final long serialVersionUID = 202407081733L;
    private final String _key;

    public DuplicateHabitatKeyExceptionCore(String key) {
        _key = key;
    }

    public String getKey(){
        return _key;
    }
}
