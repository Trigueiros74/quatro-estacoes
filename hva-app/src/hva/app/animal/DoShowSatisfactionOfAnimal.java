package hva.app.animal;

import hva.Animal;
import hva.Hotel;
import hva.app.exceptions.UnknownAnimalKeyException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/** Apresenta a satisfação de um animal, arredondada ao inteiro mais próximo. */
class DoShowSatisfactionOfAnimal extends Command<Hotel> {

    DoShowSatisfactionOfAnimal(Hotel receiver) {
        super(Label.SHOW_SATISFACTION_OF_ANIMAL, receiver);
        addStringField("id", Prompt.animalKey());
    }

    @Override
    protected final void execute() throws CommandException {
        String id = stringField("id");
        Animal animal = _receiver.getAnimal(id);

        if (animal == null)
            throw new UnknownAnimalKeyException(id);

        _display.addLine(Math.round(animal.getSatisfaction()));
        _display.display();
    }
}
