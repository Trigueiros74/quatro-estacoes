package hva.app.main;

import java.io.IOException;

import hva.HotelManager;
import hva.exceptions.MissingFileAssociationException;
import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;

/**
 * Guarda o estado da aplicação no ficheiro que lhe está associado, pedindo um
 * nome se ela ainda for anónima. Nada faz se não houver alterações por guardar.
 */
class DoSaveFile extends Command<HotelManager> {

    DoSaveFile(HotelManager receiver) {
        super(Label.SAVE_FILE, receiver, r -> r.getHotel() != null);
    }

    @Override
    protected final void execute() {
        if (!_receiver.changed())
            return;

        try {
            try {
                _receiver.save();
            } catch (MissingFileAssociationException e) {
                saveAs();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Pede um nome de ficheiro, repetindo o pedido enquanto for vazio. */
    private void saveAs() throws IOException {
        try {
            _receiver.saveAs(Form.requestString(Prompt.newSaveAs()));
        } catch (MissingFileAssociationException e) {
            saveAs();
        }
    }
}
