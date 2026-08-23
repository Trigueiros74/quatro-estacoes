package hva.app.search;

import hva.Hotel;
import hva.VaccinationRecord;
import hva.Veterinarian;
import hva.app.exceptions.UnknownVeterinarianKeyException;
import hva.exceptions.UnknownVeterinarianKeyExceptionCore;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/** Apresenta as vacinações realizadas por um veterinário, por ordem de aplicação. */
class DoShowMedicalActsByVeterinarian extends Command<Hotel> {

    DoShowMedicalActsByVeterinarian(Hotel receiver) {
        super(Label.MEDICAL_ACTS_BY_VET, receiver);
        addStringField("veterinarianId", hva.app.employee.Prompt.employeeKey());
    }

    @Override
    protected void execute() throws CommandException {
        String veterinarianId = stringField("veterinarianId");

        try {
            Veterinarian veterinarian = _receiver.getVeterinarian(veterinarianId);
            for (VaccinationRecord record : veterinarian.getRecords())
                _display.addLine(record);
            _display.display();
        } catch (UnknownVeterinarianKeyExceptionCore e) {
            throw new UnknownVeterinarianKeyException(veterinarianId);
        }
    }
}
