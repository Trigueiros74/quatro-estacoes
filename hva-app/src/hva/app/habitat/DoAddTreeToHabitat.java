package hva.app.habitat;

import hva.Deciduous;
import hva.Evergreen;
import hva.Hotel;
import hva.app.exceptions.DuplicateTreeKeyException;
import hva.app.exceptions.UnknownHabitatKeyException;
import hva.exceptions.DuplicateTreeKeyExceptionCore;
import hva.exceptions.UnknownHabitatKeyExceptionCore;
import hva.exceptions.UnrecognizedEntryException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/** Planta uma nova árvore num habitat e apresenta-a. */
class DoAddTreeToHabitat extends Command<Hotel> {

    DoAddTreeToHabitat(Hotel receiver) {
        super(Label.ADD_TREE_TO_HABITAT, receiver);
        addStringField("habitatId", Prompt.habitatKey());
        addStringField("treeId", Prompt.treeKey());
        addStringField("treeName", Prompt.treeName());
        addIntegerField("treeAge", Prompt.treeAge());
        addIntegerField("treeDifficulty", Prompt.treeDifficulty());
        addOptionField("treeType", Prompt.treeType(), Deciduous.TYPE, Evergreen.TYPE);
    }

    @Override
    protected void execute() throws CommandException {
        String habitatId = stringField("habitatId");
        String treeId = stringField("treeId");

        try {
            _display.addLine(_receiver.addTreeToHabitat(habitatId, treeId,
                stringField("treeName"), integerField("treeAge"),
                integerField("treeDifficulty"), optionField("treeType")));
            _display.display();

        } catch (DuplicateTreeKeyExceptionCore e) {
            throw new DuplicateTreeKeyException(treeId);

        } catch (UnknownHabitatKeyExceptionCore e) {
            throw new UnknownHabitatKeyException(habitatId);

        } catch (UnrecognizedEntryException e) {
            // Inalcançável: o campo de opção só aceita os tipos de árvore conhecidos.
            throw new IllegalStateException(e);
        }
    }
}
