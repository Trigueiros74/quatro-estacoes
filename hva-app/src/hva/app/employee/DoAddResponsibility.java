package hva.app.employee;

import hva.Hotel;
import hva.app.exceptions.NoResponsibilityException;
import hva.app.exceptions.UnknownEmployeeKeyException;
import hva.exceptions.UnknownEmployeeKeyExceptionCore;
import hva.exceptions.NoResponsibilityExceptionCore;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

class DoAddResponsibility extends Command<Hotel> {

    DoAddResponsibility(Hotel receiver) {
        super(Label.ADD_RESPONSABILITY, receiver);
        addStringField("employeeId", Prompt.employeeKey());
        addStringField("responsibilityId", Prompt.responsibilityKey());
    }

    @Override
    protected void execute() throws CommandException {
        String employeeId = stringField("employeeId");
        String responsibilityId = stringField("responsibilityId");

        try {
            _receiver.addResponsibility(employeeId, responsibilityId);
        } catch (UnknownEmployeeKeyExceptionCore e) {
            throw new UnknownEmployeeKeyException(employeeId);
        } catch (NoResponsibilityExceptionCore e) {
            throw new NoResponsibilityException(employeeId, responsibilityId);
        }
    }
}