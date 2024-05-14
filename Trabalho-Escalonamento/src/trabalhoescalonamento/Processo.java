// Classe que define um processo e seus atributos

package trabalhoescalonamento;

class Processo {
	// Atributos da classe Processo
	public String PID; // ID do processo
    public int tempoIngresso; // tempo que ingressou na fila de prontos
    public int duracao; // duração do processo
    public int prioridade; // prioridade do processo
    public int tipo; // Tipo de processo: CPU bound = 1, I/O bound = 2 e ambos = 3.
    public int duracaoOriginal; // duração original, essa duração será utilizada nos métodos da classe Escalonamento.
    

    // Construtor da classe, para passar os atributos.
    public Processo(String PID, int tempoIngresso, int duracao, int prioridade, int tipo, int duracaoOriginal) {
        this.PID = PID;
        this.tempoIngresso = tempoIngresso;
        this.duracao = duracao;      
        this.prioridade = prioridade;
        this.tipo = tipo;
        this.duracaoOriginal = duracaoOriginal; 
    }  
}