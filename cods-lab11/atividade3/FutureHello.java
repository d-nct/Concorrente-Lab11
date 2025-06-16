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

//classe do método main
public class FutureHello  {
  private static final long N = 2000000;
  private static final int NTHREADS = 10;

  public static void main(String[] args) {
    //cria um pool de threads (NTHREADS)
    ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
    //cria uma lista para armazenar referencias de chamadas assincronas
    List<Future<Boolean>> list = new ArrayList<Future<Boolean>>();

    System.out.print("Enviando " + N + " tarefas para o pool de threads... ");
    for (long i = 1; i <= N; i++) {
      Callable<Boolean> worker = new Primo(i, 0); // Instancia os objetos que executam o processamento
      Future<Boolean> submit = executor.submit(worker); // Executa o processamento via pool de threads
      list.add(submit); // Coloca o ticket para o resultado na lista
    }
    System.out.println("Sucesso!");

    //recupera os resultados e faz o somatório final
    long sum = 0;
    long tarefasProcessadas = 0; // Para mostrar o progresso!
    boolean imprimiu25 = false;
    boolean imprimiu50 = false;
    boolean imprimiu75 = false;
    long umQuarto = N / 4;
    long doisQuartos = N / 2;
    long tresQuartos = N * 3 / 4;

    System.out.print("Progresso até 25%... ");
    for (Future<Boolean> future : list) {
      try {
        if (future.get()) sum++; //bloqueia se a computação nao tiver terminado
        
        // IMPLEMENTAR LÓGICA DE BARRA DE PROGRESSO: PRINT EM 25%, 50% e 75% de progresso 
        tarefasProcessadas++;
        if (!imprimiu25 && tarefasProcessadas >= umQuarto) {
          System.out.println("Ok!");
          System.out.print("Progresso até 50%... ");
          imprimiu25 = true;
        } 
        if (!imprimiu50 && tarefasProcessadas >= doisQuartos) {
          System.out.println("Ok!");
          System.out.print("Progresso até 75%... ");
          imprimiu50 = true;
        } 
        if (!imprimiu75 && tarefasProcessadas >= tresQuartos) {
          System.out.println("Ok!");
          System.out.print("Progresso até 100%... ");
          imprimiu75 = true;
        }
      } catch (InterruptedException e) {
        break;
      } catch (ExecutionException e) {
        e.printStackTrace();
      }
    }
    System.out.println("Ok!");

    // Interface com Usuário
    System.out.println("\nRESUMO");
    System.out.println("------");
    System.out.println("Verificados inteiros de 1 a " + N);
    System.out.println("Total de primos encontrados: " + sum);

    executor.shutdown();
  }
}
