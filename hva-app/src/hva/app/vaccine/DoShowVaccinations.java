package hva.app.vaccine;

import hva.Hotel;
import hva.VaccinationRecord;
import pt.tecnico.uilib.menus.Command;

/** Apresenta todas as vacinações realizadas, por ordem de aplicação. */
class DoShowVaccinations extends Command<Hotel> {

    DoShowVaccinations(Hotel receiver) {
        super(Label.SHOW_VACCINATIONS, receiver);
    }

    @Override
    protected final void execute() {
        for (VaccinationRecord record : _receiver.getRecords())
            _display.addLine(record);
        _display.display();
    }
}
