// Trabalho 1 de SO, Escalonamento de processos.
// Aluno: Luiz Gabriel Favacho de Almeida - Matrícula: 22153921
// Você deve inserir o arquivo txt na pasta "nome pasta", como foi adicionado os arquivos de exemplo,
// e na hora de executar, você deve escrever de forma correta (considerando maiúsculas e minúsculas),
// o nome do arquivo.

package trabalhoescalonamento;
// Bibliotecas do java utilizadas no código.
import java.io.BufferedReader; 
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
// Inicio da classe
public class Escalonamento {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        //HashMap de algoritmos
        Map<Integer, Consumer<List<Processo>>> algoritmos = new HashMap<>();
        // Laço de repetição para executa quantas vezes quiser, até sair do algoritmo.
        while (true) {
            System.out.println("Escolha uma opção:");
            System.out.println("1. Abrir arquivo de entrada");
            System.out.println("2. Sair");

            int opcao = scanner.nextInt();

            if (opcao == 1) {
                System.out.println("Digite o nome do arquivo de entrada:");
                String nomeArquivo = scanner.next();
                List<Processo> processos = lerArquivoLista(nomeArquivo + ".txt");
                executarAlgoritmo(processos, algoritmos);
            } else if (opcao == 2) {
                scanner.close();
                System.exit(0);
            } else {
                System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }
    //Método que vai executar o algoritmo que for selecionado, passando a lista de processos e o algoritmo escolhido.
    public static void executarAlgoritmo(List<Processo> processos, Map<Integer, Consumer<List<Processo>>> algoritmos) {
        System.out.println("Escolha um algoritmo de escalonamento:");
        System.out.println("1. FCFS");
        System.out.println("2. SJF");
        System.out.println("3. PRIOc");
        System.out.println("4. RR");
        System.out.println("5. SRTF");
        System.out.println("6. PRIOp");

        Scanner scanner = new Scanner(System.in);
        int opcaoAlgoritmo = scanner.nextInt();
        // Opção para executar o FCFS.
        if (opcaoAlgoritmo == 1) {
        	System.out.println("--- FCFS ---");
        	Escalonamento.FirstComeFirstServed(processos);
        }
        // Opção para executar o SJF.
        else if (opcaoAlgoritmo == 2) {
        	System.out.println("--- SJF ---");
        	Escalonamento.ShortestJobFirst(processos);
        }
        // Opção para executar o PRIOc.
        else if (opcaoAlgoritmo == 3) {
        	System.out.println("--- PRIOc ---");
        	Escalonamento.PRIOc(processos);
        }
        // Opção para executar o RR.
        else if (opcaoAlgoritmo == 4) {
        	System.out.println("Informe o quantum: ");
        	// Defina aqui o valor do quantum desejado
        	int quantum = scanner.nextInt();
        	System.out.println("--- RR ---");
        	Escalonamento.RoundRobin(processos, quantum);
        }
        // Opção para executar o SRTF.
        else if (opcaoAlgoritmo == 5) {
        	System.out.println("--- SRTF ---");
        	Escalonamento.ShortRemainingTimeFirst(processos);
        }
        // Opção para executar o PRIOp.
        else if (opcaoAlgoritmo == 6) {
        	System.out.println("Informe o quantum: ");
        	// Defina aqui o valor do quantum desejado
        	int quantum = scanner.nextInt();
        	System.out.println("--- PRIOp ---");
        	Escalonamento.PRIOp(processos,quantum);
        }
        // Caso a opção seja inválida
        else {
	        System.out.println("Opção inválida.");
	    }
    }

 // Método que executa o Algoritmo Round Robin
    public static void RoundRobin(List<Processo> processos, int quantum) {
        Queue<Processo> filaProntos = new LinkedList<>(); // Cria uma LSE de processos prontos.
        int tempoAtual = 0; // Inicializa o tempo atual como 0.
        int tempoExecucaoTotal = 0; // Inicializa o tempo total de execução como 0.
        int tempoEsperaTotal = 0; // Inicializa o tempo total de espera como 0.
        int processosConcluidos = 0; // Inicializa o contador de processos concluídos como 0.

        // Verifica se as listas de processos e fila de prontos não estão vazias
        while (!processos.isEmpty() || !filaProntos.isEmpty()) {
            // Adiciona processos à fila de prontos quando chegam.
            while (!processos.isEmpty() && processos.get(0).tempoIngresso <= tempoAtual) {
                // O processo é removido da lista de processos e adicionado a fila de Processos prontos
                filaProntos.add(processos.remove(0));
            }

            if (!filaProntos.isEmpty()) {
                // Obtem o primeiro processo da fila de prontos
                Processo processoAtual = filaProntos.poll();

                // Mostra a ordem de execução dos processos
                System.out.print(processoAtual.PID + " ");
                if (processoAtual.duracao > quantum) {
                	// Calcula o tempo restante de execução do processo atual
                	processoAtual.duracao = processoAtual.duracao - quantum;
   
                	// Avança o tempo atual com base no quantum escolhido
                	tempoAtual += quantum;
                	filaProntos.add(processoAtual); // processo volta a fila de prontos
                }
                else if (processoAtual.duracao <= quantum){
                	tempoAtual += processoAtual.duracao;
                	processoAtual.duracao = 0;
                	// processo terminou sua execução
                	tempoExecucaoTotal += tempoAtual - processoAtual.tempoIngresso;
                    tempoEsperaTotal += tempoAtual - processoAtual.duracaoOriginal - processoAtual.tempoIngresso;
                    processosConcluidos++;
                }

            } else {
                // Se a fila de prontos estiver vazia, avança o tempo atual.
                tempoAtual++;
            }
        }
        // Verifica se há mais de 1 processo concluído e calcula os tempos médios de execução e espera.
        if (processosConcluidos > 0) {
            double tempoMedioExecucao = (double) tempoExecucaoTotal / processosConcluidos; // Calcula o tempo médio de execução.
            double tempoMedioEspera = (double) tempoEsperaTotal / processosConcluidos; // Calcula o tempo médio de espera.

            System.out.println();
            System.out.printf("Tempo Médio de Execução (RR): %.2f \n", tempoMedioExecucao);
            System.out.printf("Tempo Médio de Espera (RR): %.2f \n\n", tempoMedioEspera);
        }
    }
  
    
   
    // Método que executa o Algoritmo PRIOp
    public static void PRIOp(List<Processo> processos, int quantum) {
        Queue<Processo> filaProntos = new LinkedList<>(); // Cria uma LSE de processos prontos.
        int tempoAtual = 0; // Inicializa o tempo atual como 0.
        int tempoExecucaoTotal = 0; // Inicializa o tempo total de execução como 0.
        int tempoEsperaTotal = 0; // Inicializa o tempo total de espera como 0.
        int processosConcluidos = 0; // Inicializa o contador de processos concluídos como 0.
    
        //verifica se as listas processos e fila de prontos não estão vazias
        while (!processos.isEmpty() || !filaProntos.isEmpty()) {
            // Adiciona processos à fila de prontos quando chegam.
            while (!processos.isEmpty() && processos.get(0).tempoIngresso <= tempoAtual) {
          // O processo é removido da lista de processos e adicionado à fila de processos prontos
                filaProntos.add(processos.remove(0));
            }

            if (!filaProntos.isEmpty()) {
                // Encontra o processo com a menor duração na fila de prontos.
                Processo processoAtual = encontrarMaiorPrioridade(filaProntos);
                
             // Mostra a ordem de execução dos Processos
                System.out.print(processoAtual.PID + " " );
                
                // Remove o processo da fila de prontos.
                filaProntos.remove(processoAtual);
                if (processoAtual.duracao > quantum) {
                	// decrementa o quantum do tempo de duração do processo
                	processoAtual.duracao = processoAtual.duracao - quantum;
               
                	// avança o tempo conforme o quantum
                	tempoAtual+= quantum;
                	filaProntos.add(processoAtual); // Coloca o processo no final da fila de execução
                }
                else {
                	tempoAtual += processoAtual.duracao;
                	processoAtual.duracao = 0;
                	// processo terminou sua execução
                	tempoExecucaoTotal += tempoAtual - processoAtual.tempoIngresso;
                    tempoEsperaTotal += tempoAtual - processoAtual.duracaoOriginal - processoAtual.tempoIngresso;         
                 	processosConcluidos++;
                }
            }else {
                // Se a fila de prontos estiver vazia, avança o tempo atual.
                tempoAtual++;
            }
        }
        if (processosConcluidos > 0) {
        	double tempoMedioExecucao = (double) tempoExecucaoTotal / processosConcluidos; // Calcula o tempo médio de Execução.
            double tempoMedioEspera = (double) tempoEsperaTotal / processosConcluidos; // Calcula o tempo médio de espera.           
            System.out.println(); 
            System.out.printf("Tempo Médio de Execução (PRIOp): %.2f \n", tempoMedioExecucao);
            System.out.printf("Tempo Médio de Espera (PRIOp): %.2f \n\n", tempoMedioEspera );
        }          
         
    }

     
    // Método que executa o Algoritmo SRTF
    private static void ShortRemainingTimeFirst(List<Processo> processos) {
        Queue<Processo> filaProntos = new LinkedList<>(); // Cria uma LSE de processos prontos.
        int tempoAtual = 0; // Inicializa o tempo atual como 0.
        int tempoExecucaoTotal = 0; // Inicializa o tempo total de execução como 0.
        int tempoEsperaTotal = 0; // Inicializa o tempo total de espera como 0.
        int processosConcluidos = 0; // Inicializa o contador de processos concluídos como 0.
    
        //verifica se as listas processos e fila de prontos não estão vazias
        while (!processos.isEmpty() || !filaProntos.isEmpty()) {
            // Adiciona processos à fila de prontos quando chegam.
            while (!processos.isEmpty() && processos.get(0).tempoIngresso <= tempoAtual) {
          // O processo é removido da lista de processos e adicionado à fila de filaProntos
                filaProntos.add(processos.remove(0));
            }

            if (!filaProntos.isEmpty()) {
                // Encontra o processo com a menor duração na fila de prontos.
                Processo processoAtual = encontrarMenorDuracao(filaProntos);
                
             // Mostra a ordem de execução do Processos
                System.out.print(processoAtual.PID + " " );
                
                
                // Remove o processo da fila de prontos.
                filaProntos.remove(processoAtual);
                
                // decrementa uma unidade de tempo do tempo de duração do processo
                processoAtual.duracao = processoAtual.duracao - 1;
               
                // avança uma unidade de tempo por volta de laço
                tempoAtual++;

                // se o processo ainda não tiver finalizado sua execução
                if(processoAtual.duracao > 0 ){
                	filaProntos.add(processoAtual); // Coloca o processo no final da fila de execução
                }
                // se o processo já tiver finalizado sua execução
                else {
                	tempoExecucaoTotal += tempoAtual - processoAtual.tempoIngresso;
                    tempoEsperaTotal += tempoAtual - processoAtual.duracaoOriginal - processoAtual.tempoIngresso;         
                 	processosConcluidos++;
                }
            }else {
                // Se a fila de prontos estiver vazia, avança o tempo atual.
                tempoAtual++;
            }
        }  

        if (processosConcluidos > 0) {
        	
            double tempoMedioExecucao = (double) tempoExecucaoTotal / processosConcluidos; // Calcula o tempo médio de Execução.
            double tempoMedioEspera = (double) tempoEsperaTotal / processosConcluidos; // Calcula o tempo médio de espera.           
            System.out.println();
            System.out.printf("Tempo Médio de Execução (SRTF): %.2f \n", tempoMedioExecucao);
            System.out.printf("Tempo Médio de Espera (SRTF): %.2f \n\n", tempoMedioEspera );
        }
    }
    
    
    // Função auxiliar para encontrar o processo com a menor duração na fila de prontos.
    private static Processo encontrarMenorDuracao(Queue<Processo> filaProntos) {
        Processo menorDuracao = null;
        for (Processo processo : filaProntos) {
            if (menorDuracao == null || processo.duracao < menorDuracao.duracao) {
                menorDuracao = processo;
            }
        }
        return menorDuracao;
    }
  
  
    // Método que executa o algoritmo PRIOc
    private static void PRIOc(List<Processo> processos) {
        Queue<Processo> filaProntos = new LinkedList<>(); // Cria uma LSE de processos prontos.
        int tempoAtual = 0; // Inicializa o tempo atual como 0.
        int tempoExecucaoTotal = 0; // Inicializa o tempo total de execução como 0.
        int tempoEsperaTotal = 0; // Inicializa o tempo total de espera como 0.
        int processosConcluidos = 0; // Inicializa o contador de processos concluídos como 0.
    
        //verifica se as listas processos e fila de prontos não estão vazias
        while (!processos.isEmpty() || !filaProntos.isEmpty()) {
            // Adiciona processos à fila de prontos quando chegam.
            while (!processos.isEmpty() && processos.get(0).tempoIngresso <= tempoAtual) {
          // O processo é removido da lista de processos e adicionado à fila de filaProntos
                filaProntos.add(processos.remove(0));
            }

            if (!filaProntos.isEmpty()) {
                // Encontra o processo com a maior prioridade na fila de prontos.
                Processo processoAtual = encontrarMaiorPrioridade(filaProntos);
                
             // Mostra a ordem de execução do Processos
                System.out.print(processoAtual.PID + " " );

                // Remove o processo da fila de prontos.
                filaProntos.remove(processoAtual);
                
                tempoAtual += processoAtual.duracao;
                tempoEsperaTotal += tempoAtual - processoAtual.duracao - processoAtual.tempoIngresso;
                tempoExecucaoTotal += tempoAtual - processoAtual.tempoIngresso;
                processosConcluidos++;
            } 
        }

        if (processosConcluidos > 0) {
        	
            double tempoMedioExecucao = (double) tempoExecucaoTotal / processosConcluidos; // Calcula o tempo médio de Execução.
            double tempoMedioEspera = (double) tempoEsperaTotal / processosConcluidos; // Calcula o tempo médio de espera.
            System.out.println();
            System.out.printf("Tempo Médio de Execução (PRIOc): %.2f \n", tempoMedioExecucao);
            System.out.printf("Tempo Médio de Espera (PRIOc): %.2f \n\n", tempoMedioEspera );
        }
    }
    
  
    // Função auxiliar do SJF para encontrar o processo com a maior prioridade na fila de prontos.
    private static Processo encontrarMaiorPrioridade(Queue<Processo> filaProntos) {
        Processo maiorPrioridade = null;
        for (Processo processo : filaProntos) {
            if (maiorPrioridade == null || processo.prioridade > maiorPrioridade.prioridade) {
            	maiorPrioridade = processo;
            }
        }
        return maiorPrioridade;
    }
    

    // Método que executa o Algoritmo SJF
    private static void ShortestJobFirst(List<Processo> processos) {
        Queue<Processo> filaProntos = new LinkedList<>(); // Cria uma fila de processos prontos.
        int tempoAtual = 0; // Inicializa o tempo atual como 0.
        int tempoExecucaoTotal = 0; // Inicializa o tempo total de execução como 0.
        int tempoEsperaTotal = 0; // Inicializa o tempo total de espera como 0.
        int processosConcluidos = 0; // Inicializa o contador de processos concluídos como 0.
    
        //verifica se as listas processos e fila de prontos não estão vazias
        while (!processos.isEmpty() || !filaProntos.isEmpty()) {
            // Adiciona processos à fila de prontos quando chegam.
            while (!processos.isEmpty() && processos.get(0).tempoIngresso <= tempoAtual) {
          // O processo é removido da lista de processos e adicionado à fila de filaProntos
                filaProntos.add(processos.remove(0));
            }

            if (!filaProntos.isEmpty()) {
                // Encontra o processo com a menor duração na fila de prontos.
                Processo processoAtual = encontrarMenorDuracao(filaProntos);
                
             // Mostra a ordem de execução do Processos
                System.out.print(processoAtual.PID + " " );
               
                // Remove o processo da fila de prontos.
                filaProntos.remove(processoAtual);
                
                tempoAtual += processoAtual.duracao;
                tempoEsperaTotal += tempoAtual - processoAtual.duracao - processoAtual.tempoIngresso;
                tempoExecucaoTotal += tempoAtual - processoAtual.tempoIngresso;
                processosConcluidos++;
          
            } else {
                // Se a fila de prontos estiver vazia, avança o tempo atual.
                tempoAtual++;
            }
        }

        if (processosConcluidos > 0) {
        	
            double tempoMedioExecucao = (double) tempoExecucaoTotal / processosConcluidos; // Calcula o tempo médio de Execução.
            double tempoMedioEspera = (double) tempoEsperaTotal / processosConcluidos; // Calcula o tempo médio de espera.
            System.out.println();
            System.out.printf("Tempo Médio de Execução (SJF): %.2f \n", tempoMedioExecucao);
            System.out.printf("Tempo Médio de Espera (SJF): %.2f \n\n", tempoMedioEspera );
        }
    }

    
    // Método que executa o algoritmo FCFS
    private static void FirstComeFirstServed(List<Processo> processos) {
        int tempoExecucaoTotal = 0; // Inicializa a variável para rastrear o tempo total de execução.
        int tempoEsperaTotal = 0; // Inicializa a variável para rastrear o tempo total de espera.
        int tempoAtual = 0; // Inicializa a variável de tempo atual como 0.

        for (Processo processo : processos) { // Itera sobre a lista de processos.
            if (tempoAtual < processo.tempoIngresso) { // Verifica se o tempo atual é menor que o tempo de ingresso do processo.
                tempoAtual = processo.tempoIngresso; // Atualiza o tempo atual para o tempo de ingresso do processo.
            }
            // Mostra a ordem de execução do Processos
            System.out.print(processo.PID + " " );
       
            tempoEsperaTotal += tempoAtual - processo.tempoIngresso; // Calcula o tempo de espera e adiciona ao total.
            tempoExecucaoTotal += tempoAtual + processo.duracao - processo.tempoIngresso; // Calcula o tempo de execução e adiciona ao total.
            tempoAtual += processo.duracao; // Atualiza o tempo atual com base na duração do processo.
        }

        double tempoMedioExecucao = (double) tempoExecucaoTotal / processos.size(); // Calcula o tempo médio de execução.
        double tempoMedioEspera = (double) tempoEsperaTotal / processos.size(); // Calcula o tempo médio de espera.
        
        // Imprime os resultados formatados com duas casas decimais.
        System.out.println();
        System.out.printf("Tempo Médio de Execução (FCFS): %.2f \n", tempoMedioExecucao);
        System.out.printf("Tempo Médio de Espera (FCFS): %.2f \n\n", tempoMedioEspera);
    }
    
    
    // Método para ler o arquivo e preencher a fila de processos prontos, para as 
    public static void lerArquivoFila(String nomeArquivo, Queue<Processo> filaProntos) {
        try {
            // Abre um arquivo para leitura
            BufferedReader leitor = new BufferedReader(new FileReader(nomeArquivo));
            String linha;
            // Lê cada linha do arquivo até o final
            while ((linha = leitor.readLine()) != null) {
                // Divide a linha em tokens (separados por espaços em branco)
                String[] tokens = linha.split(" ");
                String PID = tokens[0]; // Nome do processo
                int tempoIngresso = Integer.parseInt(tokens[1]); // Tempo de ingresso na fila de prontos
                int duracao = Integer.parseInt(tokens[2]); // Duração do processo             
                int prioridade = Integer.parseInt(tokens[3]); //Prioridade do processo
                int tipo = Integer.parseInt(tokens[4]); // Tipo de processo (CPU bound, I/O bound, ambos)
                // Cria um objeto Processo com as informações extraídas
                Processo processo = new Processo(PID, tempoIngresso, duracao, prioridade, tipo, duracao);
                // Adiciona o processo à fila de processos prontos
                filaProntos.add(processo);
            }
            // Fecha o leitor após ler todo o arquivo
            leitor.close();
        } catch (IOException e) {
            // Trata exceções de leitura do arquivo, se ocorrerem
            e.printStackTrace();
        }
    }

    
    // Método para ler o arquivo e preencher a lista os processos
    private static List<Processo> lerArquivoLista(String nomeArquivo) {
        List<Processo> processos = new ArrayList<>(); // Cria uma lista para armazenar os processos lidos do arquivo.
        try (BufferedReader br = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) { // Lê cada linha do arquivo até o final.
                String[] partes = linha.split(" "); // Divide a linha em partes com base no espaço em branco.
                String PID = partes[0]; // Obtém o ID do processo da primeira parte.
                int tempoIngresso = Integer.parseInt(partes[1]); // Converte e obtém o tempo de ingresso.
                int duracao = Integer.parseInt(partes[2]); // Converte e obtém a duração do processo.
                int prioridade = Integer.parseInt(partes[3]); // Converte e obtém a prioridade.
                int tipo = Integer.parseInt(partes[4]); // Converte e obtém o tipo do processo.
                processos.add(new Processo(PID, tempoIngresso, duracao, prioridade, tipo, duracao)); // Cria um novo objeto Processo e adiciona à lista.
                
            }
        } catch (IOException e) {
            e.printStackTrace(); // Em caso de exceção de E/S (leitura do arquivo), imprime o erro.
        }
        return processos; // Retorna a lista de processos lidos do arquivo.
    }   
}