package server;

import domain.Appointment;
import domain.Payment;
import repository.AppointmentRepository;
import repository.PaymentRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread implements Runnable {
    private Socket socket;
    AppointmentRepository appointmentRepository;
    PaymentRepository paymentRepository;

    public ServerThread(Socket socket, AppointmentRepository appointmentRepository, PaymentRepository paymentRepository) {
        this.socket = socket;
        this.appointmentRepository = appointmentRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public void run() {
        System.out.println("client connected");
        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
        ) {
            String inputLine, outputLine;

            while ((inputLine = in.readLine()) != null) {
                if (inputLine.split("\\|")[0].equals("programare")) {
                    System.out.println("programare");
                    handleProgramare(inputLine);
                } else if (inputLine.split("\\|")[0].equals("plata")) {
                    System.out.println("plata");
                    handlePlata(inputLine);
                } else {
                    System.out.println("unknown command");
                }
                outputLine = inputLine;
                out.println(outputLine);
                if (outputLine.equals("bye"))
                    break;
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleProgramare(String input) {
        String[] elements = input.split("\\|");
        String nume = elements[1];
        String cnp = elements[2];
        String data = elements[3];
        String locatie = elements[4];
        String tipTratament = elements[5];
        String ora = elements[6];
        appointmentRepository.save(new Appointment(nume, cnp, data, locatie, tipTratament, ora));
    }

    private void handlePlata(String input) {
        String[] elements = input.split("\\|");
        String data = elements[1];
        String cnp = elements[2];
        double suma = Double.parseDouble(elements[3]);
        paymentRepository.save(new Payment(data, cnp, suma));
    }
}
