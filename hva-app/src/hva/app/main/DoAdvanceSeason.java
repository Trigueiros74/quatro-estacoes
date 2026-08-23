package hva.app.main;

import hva.HotelManager;
import pt.tecnico.uilib.menus.Command;

/** Avança a estação do ano e apresenta o código da nova estação. */
class DoAdvanceSeason extends Command<HotelManager> {

    DoAdvanceSeason(HotelManager receiver) {
        super(Label.ADVANCE_SEASON, receiver);
    }

    @Override
    protected final void execute() {
        _display.addLine(_receiver.getHotel().nextSeason());
        _display.display();
    }
}
