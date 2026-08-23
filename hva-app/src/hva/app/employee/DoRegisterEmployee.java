package hva.app.employee;

import hva.Caretaker;
import hva.Hotel;
import hva.Veterinarian;
import hva.app.exceptions.DuplicateEmployeeKeyException;
import hva.exceptions.DuplicateEmployeeKeyExceptionCore;
import hva.exceptions.UnrecognizedEntryException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/** Regista um novo funcionário, sem responsabilidades atribuídas. */
class DoRegisterEmployee extends Command<Hotel> {

    DoRegisterEmployee(Hotel receiver) {
        super(Label.REGISTER_EMPLOYEE, receiver);
        addStringField("id", Prompt.employeeKey());
        addStringField("name", Prompt.employeeName());
        addOptionField("type", Prompt.employeeType(), Veterinarian.TYPE, Caretaker.TYPE);
    }

    @Override
    protected void execute() throws CommandException {
        String id = stringField("id");

        try {
            _receiver.registerEmployee(id, stringField("name"), optionField("type"));

        } catch (DuplicateEmployeeKeyExceptionCore e) {
            throw new DuplicateEmployeeKeyException(id);

        } catch (UnrecognizedEntryException e) {
            // Inalcançável: o campo de opção só aceita os tipos de funcionário conhecidos.
            throw new IllegalStateException(e);
        }
    }
}
