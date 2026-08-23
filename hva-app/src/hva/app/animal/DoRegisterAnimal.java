package hva.app.animal;

import hva.Hotel;
import hva.app.exceptions.DuplicateAnimalKeyException;
import hva.app.exceptions.UnknownHabitatKeyException;
import hva.app.exceptions.UnknownSpeciesKeyException;
import hva.exceptions.DuplicateAnimalKeyExceptionCore;
import hva.exceptions.UnknownHabitatKeyExceptionCore;
import hva.exceptions.UnknownSpeciesKeyExceptionCore;
import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Regista um novo animal. Se a espécie indicada ainda não existir, pede o seu
 * nome e regista-a antes de alojar o animal.
 */
class DoRegisterAnimal extends Command<Hotel> {

    DoRegisterAnimal(Hotel receiver) {
        super(Label.REGISTER_ANIMAL, receiver);
        addStringField("id", Prompt.animalKey());
        addStringField("name", Prompt.animalName());
        addStringField("speciesId", Prompt.speciesKey());
        addStringField("habitatId", hva.app.habitat.Prompt.habitatKey());
    }

    @Override
    protected final void execute() throws CommandException {
        String id = stringField("id");
        String name = stringField("name");
        String speciesId = stringField("speciesId");
        String habitatId = stringField("habitatId");

        try {
            try {
                _receiver.registerAnimal(id, name, speciesId, habitatId);
            } catch (UnknownSpeciesKeyExceptionCore e) {
                _receiver.registerSpecies(speciesId, Form.requestString(Prompt.speciesName()));
                _receiver.registerAnimal(id, name, speciesId, habitatId);
            }

        } catch (DuplicateAnimalKeyExceptionCore e) {
            throw new DuplicateAnimalKeyException(id);

        } catch (UnknownHabitatKeyExceptionCore e) {
            throw new UnknownHabitatKeyException(habitatId);

        } catch (UnknownSpeciesKeyExceptionCore e) {
            // Inalcançável: a espécie acabou de ser registada com esta chave.
            throw new UnknownSpeciesKeyException(speciesId);
        }
    }
}
