package hva.app.habitat;

import hva.Hotel;
import hva.app.exceptions.UnknownHabitatKeyException;
import hva.exceptions.UnknownHabitatKeyExceptionCore;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

class DoChangeHabitatArea extends Command<Hotel> {

    DoChangeHabitatArea(Hotel receiver) {
        super(Label.CHANGE_HABITAT_AREA, receiver);
        addStringField("id", Prompt.habitatKey());
        addIntegerField("newArea", Prompt.habitatArea());
    }

    @Override
    protected void execute() throws CommandException {
        String id = stringField("id");
        Integer newArea = integerField("newArea"); 
        
        try {
            _receiver.changeHabitatArea(id, newArea);
        } catch (UnknownHabitatKeyExceptionCore e) {
            throw new UnknownHabitatKeyException(id); 
        }
    }
}



