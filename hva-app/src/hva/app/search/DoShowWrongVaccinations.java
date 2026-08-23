package hva.app.search;

import hva.Hotel;
import hva.VaccinationRecord;
import pt.tecnico.uilib.menus.Command;

/** Apresenta as vacinações que provocaram problemas de saúde, por ordem de aplicação. */
class DoShowWrongVaccinations extends Command<Hotel> {

    DoShowWrongVaccinations(Hotel receiver) {
        super(Label.WRONG_VACCINATIONS, receiver);
    }

    @Override
    protected void execute() {
        for (VaccinationRecord record : _receiver.getWrongVaccinations())
            _display.addLine(record);
        _display.display();
    }
}
