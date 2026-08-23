package hva.app.employee;

import hva.Employee;
import hva.Hotel;
import hva.app.exceptions.UnknownEmployeeKeyException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/** Apresenta a satisfação de um funcionário, arredondada ao inteiro mais próximo. */
class DoShowSatisfactionOfEmployee extends Command<Hotel> {

    DoShowSatisfactionOfEmployee(Hotel receiver) {
        super(Label.SHOW_SATISFACTION_OF_EMPLOYEE, receiver);
        addStringField("id", Prompt.employeeKey());
    }

    @Override
    protected void execute() throws CommandException {
        String id = stringField("id");
        Employee employee = _receiver.getEmployee(id);

        if (employee == null)
            throw new UnknownEmployeeKeyException(id);

        _display.addLine(Math.round(employee.getSatisfaction()));
        _display.display();
    }
}
