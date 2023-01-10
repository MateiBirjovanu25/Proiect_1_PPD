package server;

import repository.AppointmentRepository;
import repository.PaymentRepository;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static ExecutorService executorService;
    private static PaymentRepository paymentRepository;
    private static AppointmentRepository appointmentRepository;

    public static void main(String[] args) {
        int portNumber = Integer.parseInt(args[0]);
        boolean listening = true;
        executorService = Executors.newCachedThreadPool();

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
                executorService.submit(new ServerThread(serverSocket.accept(), appointmentRepository, paymentRepository));
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
}
