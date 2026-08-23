package hva.app.vaccine;

import hva.Hotel;
import hva.Vaccine;
import pt.tecnico.uilib.menus.Command;

/** Apresenta todas as vacinas do hotel, por ordem crescente de chave. */
class DoShowAllVaccines extends Command<Hotel> {

    DoShowAllVaccines(Hotel receiver) {
        super(Label.SHOW_ALL_VACCINES, receiver);
    }

    @Override
    protected final void execute() {
        for (Vaccine vaccine : _receiver.getVaccines())
            _display.addLine(vaccine);
        _display.display();
    }
}
