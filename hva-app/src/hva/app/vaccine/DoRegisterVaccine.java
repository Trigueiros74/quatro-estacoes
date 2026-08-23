package hva.app.vaccine;

import java.util.Arrays;
import java.util.List;

import hva.Hotel;
import hva.app.exceptions.DuplicateVaccineKeyException;
import hva.app.exceptions.UnknownSpeciesKeyException;
import hva.exceptions.DuplicateVaccineKeyExceptionCore;
import hva.exceptions.UnknownSpeciesKeyExceptionCore;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/** Regista uma nova vacina e as espécies a que se destina. */
class DoRegisterVaccine extends Command<Hotel> {

    DoRegisterVaccine(Hotel receiver) {
        super(Label.REGISTER_VACCINE, receiver);
        addStringField("vaccineId", Prompt.vaccineKey());
        addStringField("vaccineName", Prompt.vaccineName());
        addStringField("speciesKeys", Prompt.listOfSpeciesKeys());
    }

    @Override
    protected final void execute() throws CommandException {
        String vaccineId = stringField("vaccineId");
        String vaccineName = stringField("vaccineName");

        try {
            _receiver.registerVaccine(vaccineId, vaccineName, parseKeys(stringField("speciesKeys")));
        } catch (DuplicateVaccineKeyExceptionCore e) {
            throw new DuplicateVaccineKeyException(vaccineId);
        } catch (UnknownSpeciesKeyExceptionCore e) {
            throw new UnknownSpeciesKeyException(e.getKey());
        }
    }

    /**
     * @param keys lista de chaves separadas por vírgulas; os espaços são irrelevantes
     * @return as chaves indicadas, ou uma lista vazia se nenhuma o for
     */
    private static List<String> parseKeys(String keys) {
        if (keys.isBlank())
            return List.of();
        return Arrays.stream(keys.split(",")).map(String::trim).filter(key -> !key.isEmpty()).toList();
    }
}
