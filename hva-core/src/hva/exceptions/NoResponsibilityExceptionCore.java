package hva.exceptions;


import java.io.Serial;

public class NoResponsibilityExceptionCore extends Exception {
    @Serial
    private static final long serialVersionUID = 202407081733L;

    private final String _employeeKey;
    private final String _responsibilityKey;

    public NoResponsibilityExceptionCore(String employeeKey, String responsibilityKey) {
        _employeeKey = employeeKey;
        _responsibilityKey = responsibilityKey;
    }

    public String getEmployeeKey(){
        return _employeeKey;
    }

    public String getResponsibilityKey(){
        return _responsibilityKey;
    }
}