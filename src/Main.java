import models.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLOutput;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) {
        // Configurar el manejador de archivos para el logger
        FileHandler fileHandler = null;
        try {
            fileHandler = new FileHandler("batallas.log");
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al configurar el manejador de archivos de logs", e);
            System.exit(1);
        }

        //Variables
        int numBatallas = 1;
        List<Personaje> jugador1 = new ArrayList<>();
        List<Personaje> jugador2 = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            if (!Files.exists(Path.of("batallas.log"))) {
                try {
                    fileHandler = new FileHandler("batallas.log");
                    SimpleFormatter simpleFormatter = new SimpleFormatter();
                    fileHandler.setFormatter(simpleFormatter);
                    LOGGER.addHandler(fileHandler);
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Error al configurar el manejador de archivos de logs", e);
                    System.exit(1);
                }
            }
            // ========================
            // MENU + SWITCH PARA IR HACIA LAS OPCIONES
            // ========================
            System.out.println("Menú del Juego");
            System.out.println("1. Iniciar partida con personajes aleatorios");
            System.out.println("2. Iniciar partida y agregar personajes manualmente");
            System.out.println("3. Leer logs de partidas");
            System.out.println("4. Leer logs de partidas sin detalles, solo mensajes");
            System.out.println("5. Borrar archivo de logs");
            System.out.println("6. Salir");

            int opcion = -1;
            boolean entradaValida = false;

            while (!entradaValida) {
                do {
                    System.out.print("Seleccione una opción: ");
                    try {
                        opcion = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        opcion = -1;
                    }

                    if (opcion < 0 || opcion > 6){
                        System.out.println("Entrada no válida. Intente nuevamente.");
                    }
                }while (opcion < 0 || opcion > 6);
                entradaValida = true;
            }

            switch (opcion) {
                case 1:
                    // Generar los personajes aleatoriamente
                    generarMazoYRepartir(generarMazoAutomatico(), jugador1, jugador2);
                    LOGGER.info("Partida iniciada con personajes aleatorios.");
                    batalla(jugador1,jugador2, numBatallas);
                    numBatallas++;
                    break;
                case 2:
                    // Agregar personajes manualmente
                    generarMazoYRepartir(generarMazoManual(), jugador1, jugador2);
                    LOGGER.info("Partida iniciada con personajes manuales.");
                    batalla(jugador1,jugador2, numBatallas);
                    numBatallas++;
                    break;
                case 3:
                    // Leer logs de partidas
                    if (Files.exists(Path.of("batallas.log"))) {
                        mostrarLogs();
                    } else {
                        System.out.println("El archivo de logs no existe.");
                    }
                    break;
                case 4:
                    // Leer logs de partidas just info
                    if (Files.exists(Path.of("batallas.log"))) {
                        mostrarLogsJustInfo();
                    } else {
                        System.out.println("El archivo de logs no existe.");
                    }
                    break;
                case 5:
                    // Borrar archivo de logs
                    if (Files.exists(Path.of("batallas.log"))) {
                        borrarLogs(fileHandler);
                    } else {
                        System.out.println("El archivo de logs no existe.");
                    }
                    break;
                case 6:
                    // Salir del juego
                    System.out.println("Saliendo del juego.");
                    System.exit(0);
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }
            // ========================

            // ========================
            // Preguntar si el jugador quiere seguir jugando
            // ========================
            int seguirJugando;

            do {
                System.out.print("¿Desea volver al menu? (1: Sí, 2: No): ");
                try {
                    seguirJugando = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    seguirJugando = -1;
                }
                if(seguirJugando != 1 && seguirJugando != 2){
                    System.out.println("Entrada no válida. Intente nuevamente.");
                }
            } while (seguirJugando != 1 && seguirJugando != 2);

            if (seguirJugando == 2) {
                System.out.println("Saliendo del juego.");
                System.exit(0);
            }
            // ========================
        }
    }

    public static void generarMazoYRepartir(List<Personaje> mazo, List<Personaje> jugador1, List<Personaje> jugador2) {
        List<Personaje> copiaMazo = new ArrayList<>(mazo);
        //mazo : Es un mazo que se va a dar segun si las cartas son AUTOMATICAS o MANUALES
        for (int i = 1; i <= 2; i++) { // i es el numero del jugador
            List<Personaje> jugadorActual = (i == 1) ? jugador1 : jugador2;
            for (int j = 0; j < 3; j++) { //Esta es el numero de carta (i+1)
                if (!copiaMazo.isEmpty()) {
                    int indexCarta = new Random().nextInt(copiaMazo.size());
                        jugadorActual.add(copiaMazo.remove(indexCarta));
                }
            }
        }
    }

    public static void batalla(List<Personaje> jugador1, List<Personaje> jugador2, int numBatallas) {
        LOGGER.info("===========================================");
        LOGGER.info("<-------- Iniciando batalla N°" + numBatallas + " -------->");

        presentacionMazoJugador(jugador1, 1);
        LOGGER.info("Presentación del mazo del Jugador 1 realizada.");
        System.out.println();

        presentacionMazoJugador(jugador2, 2);
        LOGGER.info("Presentación del mazo del Jugador 2 realizada.");
        System.out.println();

        Random rand = new Random();
        boolean coinFlip = rand.nextBoolean(); // true: Jugador 1 ataca primero, false: Jugador 2 ataca primero

        while (!jugador1.isEmpty() && !jugador2.isEmpty()) {
            Personaje atacante;
            Personaje defensor;

            if (coinFlip) {
                atacante = jugador1.get(rand.nextInt(jugador1.size()));
                defensor = jugador2.get(rand.nextInt(jugador2.size()));
            } else {
                atacante = jugador2.get(rand.nextInt(jugador2.size()));
                defensor = jugador1.get(rand.nextInt(jugador1.size()));
            }

            for (int i = 0; i < 7; i++) {
                LOGGER.info("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-");
                LOGGER.info("Ronda " + (i + 1));
                LOGGER.info("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-");
                LOGGER.info("Atacante: " + atacante.getNombre() + " AKA " + atacante.getApodo() + " (Jugador " + (coinFlip ? "1" : "2") + ") - " + atacante.getSalud() + " HP");
                LOGGER.info("Defensor: " + defensor.getNombre() + " AKA " + defensor.getApodo() + " (Jugador " + (!coinFlip ? "1" : "2") + ") - " + defensor.getSalud() + " HP");

                double daniorealizado = atacante.ataque(defensor);
                defensor.actualizarSalud(daniorealizado);
                LOGGER.info(atacante.getNombre() + " AKA " + atacante.getApodo() + ": " + atacante.getSalud() + " HP, realizo " + daniorealizado + " daño");
                LOGGER.info(defensor.getNombre() + " AKA " + defensor.getApodo() + ": " + defensor.getSalud() + " HP");

                if (defensor.getSalud() <= 0) {
                    LOGGER.info(defensor.getNombre() + " AKA " + defensor.getApodo() + " ha sido eliminado. (Jugador " + (coinFlip ? "2" : "1") + ")");
                    double hpRandom = 1 + (rand.nextDouble() * (100 - 1));
                    atacante.setSalud(atacante.getSalud() + hpRandom);
                    LOGGER.info(atacante.getNombre() + " AKA " + atacante.getApodo() + " ha ganado, recibe +" + hpRandom + " HP. (Jugador " + (!coinFlip ? "2" : "1") + ")");
                    if (coinFlip) {
                        jugador2.remove(defensor);
                    } else {
                        jugador1.remove(defensor);
                    }
                    break;
                }

                // Intercambiar roles de atacante y defensor
                Personaje temp = atacante;
                atacante = defensor;
                defensor = temp;
                coinFlip = !coinFlip;
            }

            if (!jugador1.isEmpty() && !jugador2.isEmpty()) {
                LOGGER.info("--------------- Siguiente ronda ---------------");
                coinFlip = !coinFlip; // Cambiar el jugador que atacará primero en la siguiente ronda
            } else {
                break;
            }
        }
        LOGGER.info("===========================================");
        LOGGER.info("\nCantidad de personajes restantes en Jugador 1: " + jugador1.size());
        LOGGER.info("Cantidad de personajes restantes en Jugador 2: " + jugador2.size());

        LOGGER.info("\n===========================================");
        if (jugador1.isEmpty()) {
            LOGGER.info("¡Jugador 2 gana!");
        } else if (jugador2.isEmpty()) {
            LOGGER.info("¡Jugador 1 gana!");
        } else {
            LOGGER.info("¡Empate!"); //Esto no deberia de pasar nunca
        }

        LOGGER.info("===========================================");
        jugador1.clear(); //Limpio por si sigue jugando la persona
        jugador2.clear();
    }

    public static void presentacionMazoJugador(List<Personaje> mazo, int nroJugador){
        int contador = 1;
        LOGGER.info("=========================================");
        LOGGER.info("Las cartas del jugador " + nroJugador);
        LOGGER.info("=========================================");

        for (Personaje personaje : mazo) {
            LOGGER.info("---------------- CARTA " + contador + " ----------------");
            String txtPresentacion = personaje.toString();
            LOGGER.info(txtPresentacion);
            LOGGER.info("-------------------------------------------");
            contador++;
        }
    }

    public static List<Personaje> generarMazoAutomatico(){
        List<Personaje> mazoCartas = new ArrayList<>();
        Random rand = new Random();

        //Nombres
        List<String> nombresOrcos = List.of("Grom", "Thrall", "Garrosh", "Durotan", "Gul'dan");
        List<String> nombresHumanos = List.of("Arthur", "Lancelot", "Guinevere", "Merlin", "Morgana");
        List<String> nombresElfos = List.of("Legolas", "Arwen", "Glorfindel", "Galadriel", "Thranduil");

        // Listas de apodos y fechas de nacimiento
        List<String> apodos = List.of("Ace", "Blaze", "Crash", "Duke", "Echo", "Flame", "Ghost", "Hawk", "Ice", "Joker", "Kaiser", "Lion", "Maverick", "Ninja", "Omega");

        for (int i = 0; i < 9; i++) {
            String nombre;
            Raza raza = Raza.values()[rand.nextInt(Raza.values().length)];

            String apodo = apodos.get(rand.nextInt(apodos.size()));

            // Generar un número de fecha de nacimiento entre 1723 y 2023 para que tenga 300 o menos
            int anio = rand.nextInt(301) + 1723; // El rango es desde 1723 hasta (1723 + 300)
            int mes = rand.nextInt(12) + 1;
            int dia = rand.nextInt(28) + 1; //Use 28 para que no me de problemas ya que febrero tiene maximo 28, si se tuviera mucho en cuenta para unas cosas, deberia ser segun el num de mes
            String fechaDeNacimiento = String.format("%04d-%02d-%02d", anio, mes, dia);

            Personaje personaje = null;

            switch (raza) {
                case ORCO:
                    nombre = nombresOrcos.get(rand.nextInt(nombresOrcos.size()));
                    personaje = new Orco(nombre, apodo, raza, fechaDeNacimiento);
                    break;
                case HUMANO:
                    nombre = nombresHumanos.get(rand.nextInt(nombresHumanos.size()));
                    personaje = new Humano(nombre, apodo, raza, fechaDeNacimiento);
                    break;
                case ELFO:
                    nombre = nombresElfos.get(rand.nextInt(nombresElfos.size()));
                    personaje = new Elfo(nombre, apodo, raza, fechaDeNacimiento);
                    break;
            }

            //Añado la carta generada aleatoriamente
            mazoCartas.add(personaje);
        }

        return mazoCartas;
    }

    public static List<Personaje> generarMazoManual() {
        List<Personaje> mazoCartas = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Creación de cartas:");

        for (int i = 0; i < 9; i++) {
            System.out.println("Carta " + (i+1) + ":");

            String nombre = pedirInput("Nombre: ", "^[A-Za-z]+$");
            String apodo = pedirInput("Apodo: ", "^[A-Za-z]+$");

            Raza raza = null;
            boolean razaValida = false;
            do {
                System.out.print("Raza (ORCO, HUMANO, ELFO): ");
                String inputRaza = scanner.nextLine().toUpperCase();
                try {
                    raza = Raza.valueOf(inputRaza);
                    razaValida = true;
                } catch (IllegalArgumentException e) {
                    System.out.println("Raza no válida. Intente nuevamente.");
                }
            } while (!razaValida);

            String fechaNacimiento = pedirFechaNacimiento();

            Personaje personaje = null;
            switch (raza) {
                case ORCO:
                    personaje = new Orco(nombre, apodo, raza, fechaNacimiento);
                    break;
                case HUMANO:
                    personaje = new Humano(nombre, apodo, raza, fechaNacimiento);
                    break;
                case ELFO:
                    personaje = new Elfo(nombre, apodo, raza, fechaNacimiento);
                    break;
            }

            //Añado la carta generada manualmente
            mazoCartas.add(personaje);

            System.out.println("Carta creada y añadida al mazo.");
            System.out.println("-----------------------------");
        }
        return mazoCartas;
    }


    //Para pedir strings con un regex
    public static String pedirInput(String mensaje, String regex) {
        Scanner scanner = new Scanner(System.in);
        String input;
        Pattern pattern = Pattern.compile(regex);

        do {
            System.out.print(mensaje);
            input = scanner.nextLine();
            Matcher matcher = pattern.matcher(input);

            if (!matcher.matches()) {
                System.out.println("Entrada no válida. Intente nuevamente.");
            }
        } while (!input.matches(regex));

        return input;
    }

    //Pedir fecha con formato
    public static String pedirFechaNacimiento() {
        Scanner scanner = new Scanner(System.in);
        String fechaNacimiento;

        do {
            System.out.print("Fecha de nacimiento (AAAA-MM-DD): ");
            fechaNacimiento = scanner.nextLine();

            if (!fechaNacimiento.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                System.out.println("Formato de fecha incorrecto. Debe ser AAAA-MM-DD.");
                continue;
            }

            String[] partesFecha = fechaNacimiento.split("-");
            int anioNacimiento = Integer.parseInt(partesFecha[0]);
            int mesNacimiento = Integer.parseInt(partesFecha[1]);
            int diaNacimiento = Integer.parseInt(partesFecha[2]);

            Calendar calendario = Calendar.getInstance();
            int anioActual = calendario.get(Calendar.YEAR);

            if (anioNacimiento > anioActual || mesNacimiento < 1 || mesNacimiento > 12 || diaNacimiento < 1 || diaNacimiento > 31) {
                System.out.println("Fecha de nacimiento inválida.");
                continue;
            }

            break;
        } while (true);

        return fechaNacimiento;
    }

    public static void mostrarLogs() {
        try {
            String logs = Files.readString(Path.of("batallas.log"));
            System.out.println("Contenido del archivo de logs:");
            System.out.println(logs);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al leer el archivo de logs", e);
        }
    }

    public static void mostrarLogsJustInfo() {
        try {
            String logs = Files.readString(Path.of("batallas.log"));
            System.out.println("Contenido del archivo de logs:");

            String[] lines = logs.split(System.lineSeparator());

            for (String line : lines) {
                if (line.contains("INFO:")) {
                    String logEntry = line.replace("INFO: ", "");
                    System.out.println(logEntry);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al leer el archivo de logs", e);
        }
    }

    public static void borrarLogs(FileHandler fileHandler) {
        try {
            if (fileHandler != null) {
                fileHandler.close(); // Cierra el manejador de archivos si no es nulo
            }
            Files.deleteIfExists(Path.of("batallas.log"));
            System.out.println("Borrado exitoso.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al borrar el archivo de logs", e);
        }
    }
}