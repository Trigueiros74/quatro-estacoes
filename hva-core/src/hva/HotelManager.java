package hva;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import hva.exceptions.ImportFileException;
import hva.exceptions.MissingFileAssociationException;
import hva.exceptions.UnavailableFileException;
import hva.exceptions.UnrecognizedEntryException;

/**
 * Gere a aplicação: o hotel em memória e o ficheiro onde o seu estado é
 * preservado.
 */
public class HotelManager {

  /** O hotel corrente. */
  private Hotel _hotel = new Hotel();

  /** Ficheiro associado ao hotel corrente; vazio enquanto for anónimo. */
  private String _filename = "";

  /** @return o hotel corrente. */
  public Hotel getHotel() {
    return _hotel;
  }

  /** @return se existem alterações por guardar. */
  public boolean changed() {
    return _hotel.hasChanged();
  }

  /** @return a satisfação global do hotel corrente. */
  public long getGlobalSatisfaction() {
    return _hotel.getGlobalSatisfaction();
  }

  /** @return o nome do ficheiro associado ao hotel corrente. */
  public String getFilename() {
    return _filename;
  }

  /** Substitui o hotel corrente por um hotel vazio e anónimo. */
  public void reset() {
    _hotel = new Hotel();
    _filename = "";
  }

  /**
   * Guarda o estado da aplicação no ficheiro que lhe está associado.
   *
   * @throws FileNotFoundException           se o ficheiro não puder ser criado ou aberto
   * @throws MissingFileAssociationException se não existir ficheiro associado
   * @throws IOException                     se ocorrer um erro na serialização
   */
  public void save() throws IOException, FileNotFoundException, MissingFileAssociationException {
    if (_filename == null || _filename.isBlank())
      throw new MissingFileAssociationException();

    try (ObjectOutputStream out =
        new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(_filename)))) {
      out.writeObject(_hotel);
      _hotel.setChanged(false);
    }
  }

  /**
   * Associa a aplicação ao ficheiro indicado e guarda nele o seu estado.
   *
   * @param filename nome do ficheiro a utilizar
   * @throws FileNotFoundException           se o ficheiro não puder ser criado ou aberto
   * @throws MissingFileAssociationException se o nome indicado for vazio
   * @throws IOException                     se ocorrer um erro na serialização
   */
  public void saveAs(String filename)
      throws MissingFileAssociationException, FileNotFoundException, IOException {
    _filename = filename;
    save();
  }

  /**
   * Recupera o estado da aplicação a partir de um ficheiro previamente guardado,
   * que fica associado à aplicação.
   *
   * @param filename nome do ficheiro a abrir
   * @throws UnavailableFileException se o ficheiro não existir ou não puder ser processado
   */
  public void load(String filename) throws UnavailableFileException {
    try (ObjectInputStream in =
        new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
      _hotel = (Hotel) in.readObject();
      _hotel.setChanged(false);
      _filename = filename;
    } catch (IOException | ClassNotFoundException e) {
      throw new UnavailableFileException(filename);
    }
  }

  /**
   * Povoa a aplicação a partir de um ficheiro de dados textuais.
   *
   * @param textFile nome do ficheiro de texto a importar
   * @throws ImportFileException se o ficheiro não puder ser lido ou processado
   */
  public void importFile(String textFile) throws ImportFileException {
    try {
      _hotel.importFile(textFile);
    } catch (IOException | UnrecognizedEntryException e) {
      throw new ImportFileException(textFile);
    }
  }
}
