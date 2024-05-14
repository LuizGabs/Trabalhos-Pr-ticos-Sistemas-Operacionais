import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

public class TerminalShell {
	private static Path currentDirectory = Paths.get(System.getProperty("user.dir"));
	private static PrintWriter logWriter;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input;
        
     // Abre um arquivo de log para esta sessão
        String logFileName = "shell_log_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt";
        try {
            logWriter = new PrintWriter(new FileWriter(logFileName), true);
        } catch (IOException e) {
            System.err.println("Error opening log file: " + e.getMessage());
        }

        while (true) {
            System.out.print("Shell> ");
            input = scanner.nextLine();
            
            logCommand(input); // Registra o comando no arquivo de log

            // Verifique se o comando é "exit" e saia do loop se for.
            if (input.equals("exit")) {
                System.out.println("Exiting MyShell...");
                break;
            }else if (input.startsWith("cd")) {
                changeDirectory(input);
            }else if (input.equals("pwd")) {
                printWorkingDirectory();
            }else if (input.startsWith("copy")) {
                copyFile(input);
            }else if (input.startsWith("rename")) {
                renameFile(input);
            }else if (input.startsWith("remove")) {
                removeFile(input);
            }else if (input.startsWith("create")) {
                createNewFile(input);
            }else if (input.startsWith("mkdir")) {
                createNewDirectory(input);
            }else if (input.startsWith("cat")) {
                printFileContent(input);
            }else if (input.equals("ls")) {
                listFilesAndDirectories();
            }else if (input.startsWith("help")) {
                displayHelp();
            }else if (input.endsWith("&")) {
                executeInBackground(input);
            }else if (input.contains("|")) {
                executeCommandWithPipe(input);
            }else {
                executeExternalCommand(input);
            }

        }

        scanner.close();
    }

    private static void changeDirectory(String input) {
        String[] tokens = input.split(" ");
        if (tokens.length != 2) {
            System.out.println("Usage: cd <directory> [> output.txt]");
            return;
        }

        String newDirectory = tokens[1];
        Path newPath = currentDirectory.resolve(newDirectory);

        // Verifique se há redirecionamento de saída
        if (tokens.length > 2 && tokens[2].equals(">")) {
            String outputFileName = tokens[3];
            File outputFile = new File(outputFileName);
            try (PrintStream out = new PrintStream(new FileOutputStream(outputFile, true))) {
                System.setOut(out);
            } catch (IOException e) {
                System.err.println("Error redirecting output: " + e.getMessage());
                return;
            }
        }

        if (Files.isDirectory(newPath)) {
            currentDirectory = newPath;
            System.setProperty("user.dir", currentDirectory.toString());
        } else {
            System.out.println("Directory not found: " + newDirectory);
        }
    }

    private static void printWorkingDirectory() {
        System.out.println("Current Directory: " + currentDirectory.toString());
    }

    private static void copyFile(String input) {

        String[] tokens = input.split(" ");
        if (tokens.length != 3) {
            System.out.println("Usage: copy <sourceFile> <destinationFile> [> output.txt]");
            return;
        }

        String sourceFileName = tokens[1];
        String destinationFileName = tokens[2];

        Path sourcePath = currentDirectory.resolve(sourceFileName);
        Path destinationPath = currentDirectory.resolve(destinationFileName);

        try {
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

            // Verifique se há redirecionamento de saída
            if (tokens.length > 3 && tokens[3].equals(">")) {
                String outputFileName = tokens[4];
                File outputFile = new File(outputFileName);
                System.setOut(new PrintStream(new FileOutputStream(outputFile, true)));
            }

            System.out.println("File copied successfully.");
        } catch (IOException e) {
            System.err.println("Error copying file: " + e.getMessage());
        }
    }



    private static void renameFile(String input) {
        String[] tokens = input.split(" ");
        if (tokens.length != 3) {
            System.out.println("Usage: rename <sourceFile> <destinationFile> [> output.txt]");
            return;
        }

        String sourceFileName = tokens[1];
        String destinationFileName = tokens[2];

        // Verifique se há redirecionamento de saída
        if (tokens.length > 3 && tokens[3].equals(">")) {
            String outputFileName = tokens[4];
            File outputFile = new File(outputFileName);
            try (PrintStream out = new PrintStream(new FileOutputStream(outputFile, true))) {
                System.setOut(out);
            } catch (IOException e) {
                System.err.println("Error redirecting output: " + e.getMessage());
                return;
            }
        }

        Path sourcePath = currentDirectory.resolve(sourceFileName);
        Path destinationPath = currentDirectory.resolve(destinationFileName);

        try {
            Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File renamed successfully.");
        } catch (IOException e) {
            System.err.println("Error renaming file: " + e.getMessage());
        }
    }
    
    private static void removeFile(String input) {
        String[] tokens = input.split(" ");
        if (tokens.length != 2) {
            System.out.println("Usage: remove <fileName> [> output.txt]");
            return;
        }

        String fileName = tokens[1];
        Path filePath = currentDirectory.resolve(fileName);

        try {
            // Verifique se há redirecionamento de saída
            if (tokens.length > 2 && tokens[2].equals(">")) {
                String outputFileName = tokens[3];
                File outputFile = new File(outputFileName);
                System.setOut(new PrintStream(new FileOutputStream(outputFile, true)));
            }

            Files.delete(filePath);
            System.out.println("File removed successfully.");
        } catch (IOException e) {
            System.err.println("Error removing file: " + e.getMessage());
        }
    }
    
    private static void executeInBackground(String input) {
        String[] tokens = input.split(" ");
        if (tokens.length < 2) {
            System.out.println("Usage: command &");
            return;
        }

        String[] commandWithArgs = Arrays.copyOf(tokens, tokens.length - 1);
        String command = commandWithArgs[0];
        String[] commandArgs = Arrays.copyOfRange(commandWithArgs, 1, commandWithArgs.length);

        Runnable backgroundTask = () -> {
            ProcessBuilder processBuilder = new ProcessBuilder(commandWithArgs);
            processBuilder.inheritIO();

            try {
                Process process = processBuilder.start();
                int exitCode = process.waitFor();
                System.out.println("Background process completed with exit code: " + exitCode);
            } catch (IOException | InterruptedException e) {
                System.err.println("Error executing background command: " + e.getMessage());
            }
        };

        Thread backgroundThread = new Thread(backgroundTask);
        backgroundThread.start();
    }
    
    private static void executeCommandWithPipe(String input) {
        try {
            String[] commands = input.split("\\|");

            ProcessBuilder[] builders = new ProcessBuilder[commands.length];
            Process[] processes = new Process[commands.length];

            for (int i = 0; i < commands.length; i++) {
                builders[i] = new ProcessBuilder(commands[i].trim().split(" "));
                builders[i].redirectErrorStream(true);

                if (i == 0) {
                    builders[i].directory(currentDirectory.toFile());
                } else {
                    builders[i].directory(null); // Remove o diretório base
                    builders[i].redirectInput(ProcessBuilder.Redirect.INHERIT); // Redireciona a entrada do processo anterior
                }

                if (i == commands.length - 1) {
                    builders[i].inheritIO(); // A última saída é redirecionada para a saída do shell
                }

                processes[i] = builders[i].start();
            }

            for (Process process : processes) {
                process.waitFor();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error executing command with pipes: " + e.getMessage());
        }
    }

    private static void executeExternalCommand(String input) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(input.split(" "));
            processBuilder.inheritIO();

            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            System.out.println("Command executed with exit code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            System.err.println("Error executing external command: " + e.getMessage());
        }
    }
    private static void logCommand(String command) {
        if (logWriter != null) {
            logWriter.println("Command: " + command);
        }
    }

    private static void createNewFile(String input) {
        String[] tokens = input.split(" ");
        if (tokens.length != 2) {
            System.out.println("Usage: create <fileName> [> output.txt]");
            return;
        }

        String fileName = tokens[1];
        Path filePath = currentDirectory.resolve(fileName);

        try {
            Files.createFile(filePath);

            // Verifique se há redirecionamento de saída
            if (tokens.length > 2 && tokens[2].equals(">")) {
                String outputFileName = tokens[3];
                File outputFile = new File(outputFileName);
                try (PrintStream out = new PrintStream(new FileOutputStream(outputFile, true))) {
                    System.setOut(out);
                }
            }

            System.out.println("File created successfully: " + fileName);
        } catch (IOException e) {
            System.err.println("Error creating file: " + e.getMessage());
        }
    }
    private static void createNewDirectory(String input) {
        String[] tokens = input.split(" ");
        if (tokens.length != 2) {
            System.out.println("Usage: mkdir <directoryName>");
            return;
        }

        String directoryName = tokens[1];
        Path directoryPath = currentDirectory.resolve(directoryName);

        try {
            Files.createDirectories(directoryPath);
            System.out.println("Directory created successfully: " + directoryName);
        } catch (IOException e) {
            System.err.println("Error creating directory: " + e.getMessage());
        }
    }
    
    private static void printFileContent(String input) {
        String[] tokens = input.split(" ");
        if (tokens.length != 2) {
            System.out.println("Usage: cat <fileName>");
            return;
        }

        String fileName = tokens[1];
        Path filePath = currentDirectory.resolve(fileName);

        try {
            BufferedReader reader = Files.newBufferedReader(filePath);
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
    private static void listFilesAndDirectories() {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(currentDirectory)) {
            for (Path entry : directoryStream) {
                if (Files.isDirectory(entry)) {
                    System.out.println("Directory: " + entry.getFileName());
                } else {
                    System.out.println("File: " + entry.getFileName());
                }
            }
        } catch (IOException e) {
            System.err.println("Error listing files and directories: " + e.getMessage());
        }
    }

    private static void displayHelp() {
        System.out.println("MyShell - Comandos Disponíveis:");
        System.out.println("  pwd - Mostra o diretório atual.");
        System.out.println("  cd <diretório> - Muda o diretório atual.");
        System.out.println("  copy <arquivo_origem> <arquivo_destino> - Copia um arquivo.");
        System.out.println("  rename <nome_antigo> <nome_novo> - Renomeia um arquivo.");
        System.out.println("  remove <arquivo> - Remove um arquivo.");
        System.out.println("  create <nome_do_arquivo> - Cria um novo arquivo.");
        System.out.println("  mkdir <nome_do_diretório> - Cria um novo diretório.");
        System.out.println("  cat <nome_do_arquivo> - Mostra o conteúdo de um arquivo.");
        System.out.println("  ls - Lista arquivos e diretórios no diretório atual.");
        System.out.println("  help - Exibe esta ajuda.");
        System.out.println("  exit - Sai do MyShell.");
    }
}
