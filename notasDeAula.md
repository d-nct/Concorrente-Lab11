# MyPool

> A documentação do código esá no próprio.

Ideia é implementar um puul de threads (mesmo que posteriormente utilizaremos a 
implementação nativa).

O Pool existe quando está chegando demandas e o loading balance seja difícil.
Essa coisa de existir um grande vetor compartilhado e as threads irem pegando as tarefas sob demanda já é um loading balance dinâmico.

No Pool de Threads, criamos as threads e elas ficam esperando as tarefas chegarem.
Elas recebem a própria tarefa que irão executar.
O que precisar, ela executam e depois voltam a esperar.

Vale ressaltar que o número de threads pode ser dinâmico dentro do Pull.

É especialmente útil para servidores web para processar requisições.
Como a thread já está criada e depois ela não é destruída, o overhead é bem menor.

Para executar o arquivo, executar
```bash
javac MyPool.java
java MyPool
```

## Detalhes

- A thread executa sempre o método `run` do objeto (que ìmplements Runnable`).

- O método `shutdown` desliga, e não mata. I.e, as tarefas sendo executadas vão terminar.

- O `extends Thread` nos obriga a implementar o `run`.

# FuturePool

O Future abstrai o `join` no momento do retorno da thread.

Eu sei que vou precisar de um resultado lá na frente, que pode ser calculado em outro fluxo de execução.
O Futuro nos dá um objeto que permite recuperar esse resultado e espera se necessário.

É como se fosse um ticket, em que o método `get()` permite pegar o resultado.
Note que esse método é bloqueante, então se o resultado não estiver pronto, ele espera.

O `implements Callable<tipoDoRetorno>` é primo do `Runnable`, que permite haver retorno!
Nele, é preciso implementar `call()`.
O retorno é normal, via `return`.



