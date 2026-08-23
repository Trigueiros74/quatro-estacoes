package hva.app.habitat;

import hva.Hotel;
import hva.Influence;
import hva.app.exceptions.UnknownHabitatKeyException;
import hva.app.exceptions.UnknownSpeciesKeyException;
import hva.exceptions.UnknownHabitatKeyExceptionCore;
import hva.exceptions.UnknownSpeciesKeyExceptionCore;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/** Altera a adequação de um habitat a uma espécie. */
class DoChangeHabitatInfluence extends Command<Hotel> {

    DoChangeHabitatInfluence(Hotel receiver) {
        super(Label.CHANGE_HABITAT_INFLUENCE, receiver);
        addStringField("habitatId", Prompt.habitatKey());
        addStringField("speciesId", hva.app.animal.Prompt.speciesKey());
        addOptionField("influence", Prompt.habitatInfluence(), "POS", "NEG", "NEU");
    }

    @Override
    protected void execute() throws CommandException {
        String habitatId = stringField("habitatId");
        String speciesId = stringField("speciesId");
        Influence influence = Influence.valueOf(optionField("influence"));

        try {
            _receiver.changeInfluence(habitatId, speciesId, influence);
        } catch (UnknownHabitatKeyExceptionCore e) {
            throw new UnknownHabitatKeyException(habitatId);
        } catch (UnknownSpeciesKeyExceptionCore e) {
            throw new UnknownSpeciesKeyException(speciesId);
        }
    }
}
