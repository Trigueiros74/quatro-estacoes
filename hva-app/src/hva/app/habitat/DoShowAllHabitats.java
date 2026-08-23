package hva.app.habitat;

import hva.Habitat;
import hva.Hotel;
import hva.Tree;
import pt.tecnico.uilib.menus.Command;

/** Apresenta todos os habitats e as suas árvores, por ordem crescente de chave. */
class DoShowAllHabitats extends Command<Hotel> {

    DoShowAllHabitats(Hotel receiver) {
        super(Label.SHOW_ALL_HABITATS, receiver);
    }

    @Override
    protected final void execute() {
        for (Habitat habitat : _receiver.getHabitats()) {
            _display.addLine(habitat);
            for (Tree tree : habitat.getTrees())
                _display.addLine(tree);
        }
        _display.display();
    }
}
