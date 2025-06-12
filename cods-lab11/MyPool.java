/* Disciplina: Programacao Concorrente */
/* Prof.: Silvana Rossetto */
/* Laboratório: 11 */
/* Codigo: Criando um pool de threads em Java */

import java.util.LinkedList;

//-------------------------------------------------------------------------------
//!!! Documentar essa classe !!!

/**
 * @brief É a classe que implementa o Pull de Threads.
 */
class FilaTarefas {
	private final int nThreads;
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
	//...completar implementacao, recebe um numero inteiro positivo e imprime se esse numero eh primo ou nao
	public void run() {}
}

//Classe da aplicação (método main)
class MyPool {
	private static final int NTHREADS = 10;

	public static void main (String[] args) {
		//--PASSO 2: cria o pool de threads
		FilaTarefas pool = new FilaTarefas(NTHREADS); 

		//--PASSO 3: dispara a execução dos objetos runnable usando o pool de threads
		for (int i = 0; i < 25; i++) {
			final String m = "Hello da tarefa " + i;
			Runnable hello = new Hello(m);
			pool.execute(hello);
			//Runnable primo = new Primo(i);
			//pool.execute(primo);
		}

		//--PASSO 4: esperar pelo termino das threads
		pool.shutdown();
		System.out.println("Terminou");
	}
}
