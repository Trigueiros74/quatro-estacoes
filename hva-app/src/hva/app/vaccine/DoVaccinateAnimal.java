package hva.app.vaccine;

import hva.Hotel;
import hva.app.exceptions.UnknownAnimalKeyException;
import hva.app.exceptions.UnknownVaccineKeyException;
import hva.app.exceptions.UnknownVeterinarianKeyException;
import hva.app.exceptions.VeterinarianNotAuthorizedException;
import hva.exceptions.UnknownAnimalKeyExceptionCore;
import hva.exceptions.UnknownVaccineKeyExceptionCore;
import hva.exceptions.UnknownVeterinarianKeyExceptionCore;
import hva.exceptions.VeterinarianNotAuthorizedExceptionCore;
import hva.exceptions.WrongVaccineMessage;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/** Vacina um animal, avisando se a vacina não lhe for adequada. */
class DoVaccinateAnimal extends Command<Hotel> {

    DoVaccinateAnimal(Hotel receiver) {
        super(Label.VACCINATE_ANIMAL, receiver);
        addStringField("vaccineId", Prompt.vaccineKey());
        addStringField("vetId", Prompt.veterinarianKey());
        addStringField("animalId", hva.app.animal.Prompt.animalKey());
    }

    @Override
    protected final void execute() throws CommandException {
        String vaccineId = stringField("vaccineId");
        String vetId = stringField("vetId");
        String animalId = stringField("animalId");

        try {
            _receiver.vaccinateAnimal(vaccineId, vetId, animalId);

        } catch (UnknownVaccineKeyExceptionCore e) {
            throw new UnknownVaccineKeyException(vaccineId);

        } catch (UnknownVeterinarianKeyExceptionCore e) {
            throw new UnknownVeterinarianKeyException(vetId);

        } catch (UnknownAnimalKeyExceptionCore e) {
            throw new UnknownAnimalKeyException(animalId);

        } catch (VeterinarianNotAuthorizedExceptionCore e) {
            throw new VeterinarianNotAuthorizedException(e.getVetKey(), e.getSpeciesKey());

        } catch (WrongVaccineMessage e) {
            _display.addLine(Message.wrongVaccine(e.getVaccineKey(), e.getAnimalKey()));
            _display.display();
        }
    }
}
