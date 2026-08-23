package hva.app.search;

import hva.Animal;
import hva.Habitat;
import hva.Hotel;
import hva.app.exceptions.UnknownHabitatKeyException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/** Apresenta os animais que residem num habitat, por ordem crescente de chave. */
class DoShowAnimalsInHabitat extends Command<Hotel> {

    DoShowAnimalsInHabitat(Hotel receiver) {
        super(Label.ANIMALS_IN_HABITAT, receiver);
        addStringField("id", hva.app.habitat.Prompt.habitatKey());
    }

    @Override
    protected void execute() throws CommandException {
        String id = stringField("id");
        Habitat habitat = _receiver.getHabitat(id);

        if (habitat == null)
            throw new UnknownHabitatKeyException(id);

        for (Animal animal : habitat.getAnimals())
            _display.addLine(animal);
        _display.display();
    }
}
