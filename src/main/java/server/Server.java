package server;

import domain.Appointment;
import domain.Payment;
import repository.AppointmentRepository;
import repository.PaymentRepository;

import java.io.*;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static ExecutorService executorService;
    private static PaymentRepository paymentRepository;
    private static AppointmentRepository appointmentRepository;
    private static List<Double> listCosturi;
    private static List<Double> listDurata;
    private static Map<AbstractMap.SimpleEntry<Integer, Integer>, Integer> mapLocuriLibere;
    private static int p = 10;
    private static int nrClienti = 10;
    private static int nrTratamente = 5;
    private static int nrLocatii = 5;

    public static void main(String[] args) throws FileNotFoundException {

        int portNumber = Integer.parseInt(args[0]);
        boolean listening = true;
        executorService = Executors.newCachedThreadPool();

        loadTratamente();
//        listCosturi.forEach(System.out::println);
//        listDurata.forEach(System.out::println);
//        mapLocuriLibere.values().forEach(System.out::println);

        var properties = new Properties();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream("src/main/resources/config.properties");
            properties.load(fileInputStream);
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
        paymentRepository = new PaymentRepository(properties.getProperty("url"), properties.getProperty("username"), properties.getProperty("password"));
        appointmentRepository = new AppointmentRepository(properties.getProperty("url"), properties.getProperty("username"), properties.getProperty("password"));

        System.out.println("waiting for clients");
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {
                executorService.submit(new ServerThread(serverSocket.accept(), appointmentRepository, paymentRepository, listCosturi, listDurata, mapLocuriLibere));
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }

    private static void loadTratamente() throws FileNotFoundException {
        listCosturi = new ArrayList<>();
        listCosturi.add(0.0);
        listDurata = new ArrayList<>();
        listDurata.add(0.0);
        mapLocuriLibere = new HashMap<>();

        Scanner scanner = new Scanner(new File("src/main/resources/date_tratemente"));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String type = line.split(":")[0];
            String[] elements = line.split(":")[1].split(",");
            if (type.equals("costuri")) {
                for (String elem : elements) {
                    listCosturi.add(Double.parseDouble(elem));
                }
            } else if (type.equals("durate")) {
                for (String elem : elements) {
                    listDurata.add(Double.parseDouble(elem));
                }
            } else {
                int currentIndex = 1;
                for (String elem : elements) {
                    mapLocuriLibere.put(new AbstractMap.SimpleEntry<>(1, currentIndex), Integer.parseInt(elem));
                    currentIndex++;
                }
                for (int i = 2; i <= nrLocatii; i++) {
                    for (int j = 1; j <= nrTratamente; j++) {
                        int newValue = mapLocuriLibere.get(new AbstractMap.SimpleEntry<>(1, j)) * (i-1);
                        mapLocuriLibere.put(new AbstractMap.SimpleEntry<>(i, j), newValue);
                    }
                }
            }
        }
    }
}
