Aluno: Luiz Gabriel Favacho de Almeida
Matrícula: 22153921

Shell - Guia de Uso

Bem-vindo ao Shell, um shell simples implementado em Java. Este shell oferece uma variedade de comandos e recursos para interagir com o sistema de arquivos do seu ambiente Linux.

Comandos Disponíveis:
---------------------

1. `pwd`: Exibe o diretório de trabalho atual.

2. `cd <diretório>`: Altera o diretório de trabalho para o diretório especificado. Use `cd ..` para subir um diretório.

3. `ls`: Lista arquivos e diretórios no diretório atual.

4. `create <nome_do_arquivo> [> output.txt]`: Cria um novo arquivo com o nome especificado. Use `>` para redirecionar a saída para um arquivo.

5. `copy <arquivo_origem> <arquivo_destino>`: Copia um arquivo de origem para o destino.

6. `rename <nome_antigo> <nome_novo>`: Renomeia um arquivo de nome antigo para nome novo.

7. `remove <arquivo>`: Remove um arquivo especificado.

8. `mkdir <nome_do_diretório>`: Cria um novo diretório com o nome especificado.

9. `cat <nome_do_arquivo>`: Exibe o conteúdo de um arquivo.

10. `help`: Exibe uma lista de comandos disponíveis e suas descrições.

11. `exit`: Sai do MeuShell.

Recursos Avançados:
-------------------

- Você pode usar o operador `|` para criar pipelines de comandos. Por exemplo: `cat arquivo.txt | grep palavra-chave`.

- Para executar um comando em segundo plano, adicione `&` no final do comando. Por exemplo: `comando_longo &`.

- Todos os comandos e suas saídas são registrados em um arquivo de log para cada sessão.

Uso Básico:
-----------

1. Inicie o MeuShell.

2. Use os comandos listados acima para interagir com o sistema de arquivos e executar tarefas específicas.

3. Você pode usar redirecionamento de saída (`>`) para salvar a saída de um comando em um arquivo.

4. Use o comando `help` para obter uma lista de comandos disponíveis a qualquer momento.

5. Para sair do MeuShell, digite `exit`.

Exemplos de Comandos:
---------------------

- Para listar arquivos e diretórios no diretório atual:
ls


- Para criar um novo arquivo e redirecionar a saída:
create novo_arquivo.txt > saida.txt

- Para copiar um arquivo:
copy arquivo_origem.txt arquivo_destino.txt
