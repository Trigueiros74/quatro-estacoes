package hva.app.habitat;

import hva.Habitat;
import hva.Hotel;
import hva.Tree;
import hva.app.exceptions.UnknownHabitatKeyException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/** Apresenta as árvores de um habitat, por ordem crescente de chave. */
class DoShowAllTreesInHabitat extends Command<Hotel> {

    DoShowAllTreesInHabitat(Hotel receiver) {
        super(Label.SHOW_TREES_IN_HABITAT, receiver);
        addStringField("id", Prompt.habitatKey());
    }

    @Override
    protected void execute() throws CommandException {
        String id = stringField("id");
        Habitat habitat = _receiver.getHabitat(id);

        if (habitat == null)
            throw new UnknownHabitatKeyException(id);

        for (Tree tree : habitat.getTrees())
            _display.addLine(tree);
        _display.display();
    }
}
