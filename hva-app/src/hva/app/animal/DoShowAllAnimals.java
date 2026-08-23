package hva.app.animal;

import hva.Animal;
import hva.Hotel;
import pt.tecnico.uilib.menus.Command;

/** Apresenta todos os animais do hotel, por ordem crescente de chave. */
class DoShowAllAnimals extends Command<Hotel> {

    DoShowAllAnimals(Hotel receiver) {
        super(Label.SHOW_ALL_ANIMALS, receiver);
    }

    @Override
    protected final void execute() {
        for (Animal animal : _receiver.getAnimals())
            _display.addLine(animal);
        _display.display();
    }
}
