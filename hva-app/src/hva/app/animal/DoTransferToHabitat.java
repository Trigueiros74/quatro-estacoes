package hva.app.animal;

import hva.Hotel;
import hva.app.exceptions.UnknownAnimalKeyException;
import hva.app.exceptions.UnknownHabitatKeyException;
import hva.exceptions.UnknownAnimalKeyExceptionCore;
import hva.exceptions.UnknownHabitatKeyExceptionCore;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/** Transfere um animal para outro habitat. */
class DoTransferToHabitat extends Command<Hotel> {

    DoTransferToHabitat(Hotel hotel) {
        super(Label.TRANSFER_ANIMAL_TO_HABITAT, hotel);
        addStringField("animalId", Prompt.animalKey());
        addStringField("habitatId", hva.app.habitat.Prompt.habitatKey());
    }

    @Override
    protected final void execute() throws CommandException {
        String animalId = stringField("animalId");
        String habitatId = stringField("habitatId");

        try {
            _receiver.transferAnimal(animalId, habitatId);

        } catch (UnknownAnimalKeyExceptionCore e) {
            throw new UnknownAnimalKeyException(animalId);

        } catch (UnknownHabitatKeyExceptionCore e) {
            throw new UnknownHabitatKeyException(habitatId);
        }
    }
}
