/* Disciplina: Programacao Concorrente */
/* Prof.: Silvana Rossetto */
/* Laboratório: 11 */
/* Codigo: Criando um pool de threads em Java */

import java.util.LinkedList;
import java.util.Random;


//-------------------------------------------------------------------------------
//!!! Documentar essa classe !!!

/**
 * @brief É a classe que implementa o Pull de Threads.
 */
class FilaTarefas {
	private final int nThreads;               // Número de Threads
	private final MyPoolThreads[] threads;    // Vetor de threads
	private final LinkedList<Runnable> queue; // Essa é a lista de tarefas
	private boolean shutdown;                 // Se é para pegar a próxima tarefa

	/**
	 * @brief Construtor da classe. Responsável por criar a lista e dar o kickstart nas threads.
	 */
	public FilaTarefas(int nThreads) {
		this.shutdown = false;
		this.nThreads = nThreads;
		queue = new LinkedList<Runnable>();
		threads = new MyPoolThreads[nThreads];
		for (int i=0; i<nThreads; i++) {
			threads[i] = new MyPoolThreads();
			threads[i].start();
		} 
	}

	/**
	 * @brief Executa qualquer coisa que implemente runnable a partir do método run.
	 *				Uma tarefa = um notify
	 */
	public void execute(Runnable r) {
		synchronized(queue) { // Garante operações atômicas na fila
													// Ou seja, não supõe lista thread safe
			if (this.shutdown) return;
			queue.addLast(r);
			queue.notify();
		}
	}

	/**
	 * @brief Desliga as threads.
	 */
	public void shutdown() {
		synchronized(queue) { 
			this.shutdown=true;
			queue.notifyAll();
		}
		for (int i=0; i<nThreads; i++) {
			try { threads[i].join(); } catch (InterruptedException e) { return; }
		}
	}

	/**
	 * @brief Esse é o run central do que as threads fazem.
	 */
	private class MyPoolThreads extends Thread {
		public void run() {
			Runnable r;
			while (true) {
				synchronized(queue) { // Nesse bloco a thread pega a tarefa da lista
					while (queue.isEmpty() && (!shutdown)) {
						try { queue.wait(); }
						catch (InterruptedException ignored){}
					}
					if (queue.isEmpty()) return;   
					r = (Runnable) queue.removeFirst();
				}
				try { r.run(); }
				catch (RuntimeException e) {}
			} 
		} 
	} 
}
//-------------------------------------------------------------------------------

//--PASSO 1: cria uma classe que implementa a interface Runnable 
class Hello implements Runnable {
	String msg;
	public Hello(String m) { msg = m; }

	//--metodo executado pela thread
	public void run() {
		System.out.println(msg);
	}
}

class Primo implements Runnable {
  private long p;        // Número a ser testado como primo
  private int numIter;   // Número de iterações para o teste
  private Random rand = new Random(); // Objeto randômico
  private boolean soPrimos; // Booleano para ignorar alguns prints

  /**
   * Construtor.
   */
  public Primo(long p, boolean soPrimos) {
    this.p = p;
    this.numIter = 10; // Justificado pelo Teo de Rabin (em testeDeMillerRabin)
    this.soPrimos = soPrimos; // Se true, ignora o print quando p é composto
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
   * @print se p é composto
   *        se p é primo (provavelmente)
   */
  @Override
	public void run() {
    long b; // Base para o teste

    // Casos Triviais
    if (this.p <= 1) {
      if (!this.soPrimos) System.out.println(" > Não Primo: " + this.p);
      return;
    }
    if (this.p <= 3) { // p é 2 ou 3
      System.out.println(" > Primo:     " + this.p);
      return;
    }
    if (this.p % 2 == 0) {
      if (!this.soPrimos) System.out.println(" > Não Primo: " + this.p);
      return;
    }

    // Executa o teste
    for (int i = 0; i < this.numIter; i++) {
      // Gera uma base b != 0 em Z_p
      b = Math.abs(rand.nextLong()) % (this.p - 1) + 1;
      if (!this.testeDeMiller(b)) {
        if (!this.soPrimos) System.out.println(" > Não Primo: " + this.p);
        return;
      }
    }

    // Se nenhum teste falhou, a prob de não ser composto é \frac{1}{4^numIter}
    System.out.println(" > Primo:     " + this.p);
  }
}

//Classe da aplicação (método main)
class MyPool {
	private static final int NTHREADS = 10;

	public static void main (String[] args) {
		//--PASSO 2: cria o pool de threads
		FilaTarefas pool = new FilaTarefas(NTHREADS); 

		//--PASSO 3: dispara a execução dos objetos runnable usando o pool de threads
		for (long i = 0; i < 10000; i++) {
			final String m = "Hello da tarefa " + i;
			Runnable primo = new Primo(i, false); // Imprime primos e não primos
			pool.execute(primo);
		}

		//--PASSO 4: esperar pelo termino das threads
		pool.shutdown();
		System.out.println("Terminou");
	}
}
