/* Disciplina: Programacao Concorrente */
/* Prof.: Silvana Rossetto */
/* Laboratório: 11 */
/* Codigo: Exemplo de uso de futures */
/* -------------------------------------------------------------------*/

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

//classe do método main
public class FutureHello  {
  private static final long N = 1000000000;
  private static final int NTHREADS = 10;
  private static final int LIST_CAPACITY = NTHREADS * 10;

  public static void main(String[] args) {
    //cria um pool de threads (NTHREADS)
    ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
    //cria uma lista para armazenar referencias de chamadas assincronas
    List<Future<Boolean>> list = new ArrayList<Future<Boolean>>();

    // Variáveis de controle para a lista de tamanho fixo
    long tarefasSubmetidas = 0;
    long primosEncontrados = 0;

    System.out.println("Iniciando checagem de primos até " + N + " com " + NTHREADS + "threads via pull de threads...");

    while (tarefasSubmetidas < N) {
      // Submete as tarefas até encher a lista
      while (list.size() < LIST_CAPACITY && tarefasSubmetidas < N) {
        Callable<Boolean> worker = new Primo(tarefasSubmetidas++, 0); // Instancia os objetos que executam o processamento
        Future<Boolean> submit = executor.submit(worker); // Executa o processamento via pool de threads
        list.add(submit); // Coloca o ticket para o resultado na lista
      }

      // Processa a lista
      Iterator<Future<Boolean>> iterator = list.iterator();
      while (iterator.hasNext()) {
        Future<Boolean> future = iterator.next();
        if (future.isDone()) { // não bloqueante
          try {
            if (future.get()) { // dentro do bloco não bloqueante, a func bloqueante não bloqueia
              primosEncontrados++;
            }
          } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
          }
          iterator.remove(); // Pulo do gato: desaloca a tarefa da lista
        }
      }
    }

    // Processa as tarefas sobressalentes
    System.out.println("Tarefas submetidas. Aguardando as " + list.size() + " últimas terminarem...");
    for (Future<Boolean> future : list) {
      try {
        if (future.get()) { // bloqueante
          primosEncontrados++;
        }
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }
    System.out.println("Terminei!");

    // Interface com Usuário
    System.out.println("\nRESUMO");
    System.out.println("======");
    System.out.println("Verificados inteiros de 1 a " + N);
    System.out.println("Número de Threads: " + NTHREADS);
    System.out.println("Capacidade da lista de futuros: " + LIST_CAPACITY);
    System.out.println("---");
    System.out.println("Total de primos encontrados: " + primosEncontrados);

    executor.shutdown();
  }
}
