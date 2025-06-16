import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

/**
 * Classe Runnable para escrever um único número primo em um arquivo.
 */
public class Logger {
  private final String local;
  private File arq;
  private FileWriter fw;

  /**
   * Construtor.
   * @param local O nome do arquivo onde o primo será salvo.
   */
  public Logger(String local) {
    this.local = local;
    this.arq = new File(local);
    if (this.arq.exists()) arq.delete(); // Trunca

    // Abre o buffer
    try {
      fw = new FileWriter(local, true);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Escreve p no arquivo.
   *
   * @param p O número primo a ser escrito.
   */
  public void registra(Long p) {
    try {
      fw.write(p.toString() + "\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Fecha o arquivo.
   */
  public void destruidor() {
    try {
      fw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
