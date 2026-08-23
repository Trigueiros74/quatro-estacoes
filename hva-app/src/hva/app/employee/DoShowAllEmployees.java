package hva.app.employee;

import hva.Employee;
import hva.Hotel;
import pt.tecnico.uilib.menus.Command;

/** Apresenta todos os funcionários do hotel, por ordem crescente de chave. */
class DoShowAllEmployees extends Command<Hotel> {

    DoShowAllEmployees(Hotel receiver) {
        super(Label.SHOW_ALL_EMPLOYEES, receiver);
    }

    @Override
    protected void execute() {
        for (Employee employee : _receiver.getEmployees())
            _display.addLine(employee);
        _display.display();
    }
}
