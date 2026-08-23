package hva.exceptions;


import java.io.Serial;

public class DuplicateEmployeeKeyExceptionCore extends Exception {
    @Serial
    private static final long serialVersionUID = 202407081733L;

    private final String _key;

    public DuplicateEmployeeKeyExceptionCore(String key) {
        _key = key;
    }

    public String getKey(){
        return _key;
    }
}
