package hva.app.search;

import hva.Animal;
import hva.Hotel;
import hva.VaccinationRecord;
import hva.app.exceptions.UnknownAnimalKeyException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/** Apresenta as vacinações de um animal, por ordem de aplicação. */
class DoShowMedicalActsOnAnimal extends Command<Hotel> {

    DoShowMedicalActsOnAnimal(Hotel receiver) {
        super(Label.MEDICAL_ACTS_ON_ANIMAL, receiver);
        addStringField("animalId", hva.app.animal.Prompt.animalKey());
    }

    @Override
    protected void execute() throws CommandException {
        String animalId = stringField("animalId");
        Animal animal = _receiver.getAnimal(animalId);

        if (animal == null)
            throw new UnknownAnimalKeyException(animalId);

        for (VaccinationRecord record : animal.getRecords())
            _display.addLine(record);
        _display.display();
    }
}
