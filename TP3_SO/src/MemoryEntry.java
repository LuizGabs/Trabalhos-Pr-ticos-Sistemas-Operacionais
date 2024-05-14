//Classe para representar um objeto na memória
public class MemoryEntry {
	int process; // Processo
    int page; // Página
    boolean referenceBit; //bit de referência, só será utilizado no algoritmo Second Chance
    int lastUsed; // Atributo para identificar a página que foi menos utilizada, utilizado somente no algoritmo LRU

    // Construtor da classe
    public MemoryEntry(int process, int page) {
        this.process = process;
        this.page = page;
        this.referenceBit = true; // Bit de referência inicialmente definido como true
    }
}
