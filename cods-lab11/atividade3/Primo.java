import java.util.concurrent.Callable;
import java.util.Random;


public class Primo implements Callable<Long> {
  private long p;        // Número a ser testado como primo
  private int numIter;   // Número de iterações para o teste
  private Random rand = new Random(); // Objeto randômico
  private int verb;      // Nível de verbosidade

  /**
   * Construtor.
   *
   * @param p é o número a ser testado
   * @param verb é o nível de verbosidade da execução
   *             se 0, então SEM prints
   *             se 1, então imprime apenas os primos
   *             se 2, então imprime primos ou não primos
   */
  public Primo(long p, int verb) {
    this.p = p;
    this.numIter = 10; // Justificado pelo Teo de Rabin (em testeDeMillerRabin)
    this.verb = verb; 
  }

  /**
   * Potência com Square & Multiply.
   *
   * @return forma reduzida de a^b (mod n)
   */
  public static long powMod(long a, long b, long n) {
    long res = 1;

    a = a % n;
    while (b > 0) {
      if (b % 2 == 1) {
        res = (res * a) % n;
      }
      b = b >> 1;
      a = (a * a) % n;
    }
    return res;
  }

  /**
   * Roda o teste de Miller em  p  na base  b.
   *
   * @param b é a base do teste
   * @return false se p é composto
   *         true  se teste inconclusivo
   */
  private boolean testeDeMiller(long b) {
    // Calcula q_k tq p-1 = 2^k * q, com q ímpar
    long q = this.p - 1;
    long k = 0;
    while (q % 2 == 0) {
      q /= 2;
      k++;
    }

    // Realiza o chute
    long chute = this.powMod(b, q, this.p);
    if (chute == 1 || chute == this.p-1) return true; // Passa no teste

    // Executa o teste para as potências de 2
    for (int i = 1; i < k; i++) {
      chute = this.powMod(chute, 2, this.p);
      if (chute == 1)   return false; // Existe ordem no anel do Z_p -> composto
      if (chute == this.p-1) return true; // Passou no teste
    } 

    // Se chegou até aqui, definitivamente é composto
    return false;
  }

  /**
   * Testa  p  com Miller-Rabin.
   *
   * Teorema de Rabin: Teste de Miller acerta em 3/4 das bases entre 2 e p-2.
   * Então, com 10 iterações, prob de erro é \frac{1}{4^{10}} > 1e-6.
   *
   * @return 0 se p é composto
   *         p se p é primo (provavelmente)
   * @print se p é composto
   *        se p é primo (provavelmente)
   */
	private Long testeDeMillerRabin() {
    long b; // Base para o teste

    // Casos Triviais
    if (this.p <= 1) {
      if (this.verb == 2) System.out.println(" > Não Primo: " + this.p);
      return 0L;
    }
    if (this.p <= 3) { // p é 2 ou 3
      if (this.verb >= 1) System.out.println(" > Primo:     " + this.p);
      return this.p;
    }
    if (this.p % 2 == 0) {
      if (this.verb == 2) System.out.println(" > Não Primo: " + this.p);
      return 0L;
    }

    // Executa o teste
    for (int i = 0; i < this.numIter; i++) {
      // Gera uma base b != 0 em Z_p
      b = Math.abs(rand.nextLong()) % (this.p - 1) + 1;
      if (!this.testeDeMiller(b)) {
        if (this.verb == 2) System.out.println(" > Não Primo: " + this.p);
        return 0L;
      }
    }

    // Se nenhum teste falhou, a prob de não ser composto é \frac{1}{4^numIter}
    if (this.verb >= 1) System.out.println(" > Primo:     " + this.p);
    return this.p;
  }

  // Separei o método apenas para deixar mais didático
  public Long call() throws Exception {
    return this.testeDeMillerRabin();
  }
}

