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
import java.util.*;
import java.util.stream.StreamSupport;

public class ServerThread implements Runnable {
    private final List<Double> costList;
    private final List<Double> durationList;
    private Map<AbstractMap.SimpleEntry<Integer, Integer>, Integer> mapLocuriLibere;
    private Socket socket;
    AppointmentRepository appointmentRepository;
    PaymentRepository paymentRepository;

    public ServerThread(Socket socket, AppointmentRepository appointmentRepository, PaymentRepository paymentRepository, List<Double> costList, List<Double> durationList, Map<AbstractMap.SimpleEntry<Integer, Integer>, Integer> mapLocuriLibere) {
        this.socket = socket;
        this.appointmentRepository = appointmentRepository;
        this.paymentRepository = paymentRepository;
        this.costList = costList;
        this.durationList = durationList;
        this.mapLocuriLibere = mapLocuriLibere;
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
            String inputLine, outputLine="";

            while ((inputLine = in.readLine()) != null) {
                if (inputLine.split("\\|")[0].equals("programare")) {
                    System.out.println("programare");
                    outputLine = handleProgramare(inputLine);
                } else if (inputLine.split("\\|")[0].equals("plata")) {
                    System.out.println("plata");
                    outputLine = handlePlata(inputLine);
                } else if (inputLine.split("\\|")[0].equals("anulare")) {
                    System.out.println("anulare");
                    outputLine = handleCancel(inputLine);
                } else {
                    System.out.println("unknown command");
                }
                out.println(outputLine);
                if (outputLine.equals("bye"))
                    break;
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String handleProgramare(String input) {
        String[] elements = input.split("\\|");
        String nume = elements[1];
        String cnp = elements[2];
        String data = elements[3];
        String locatie = elements[4];
        String tipTratament = elements[5];
        String oraStart = elements[6];
        String oraFinish = elements[7];

        String checkResult =
                checkInterval(Integer.parseInt(oraStart), Integer.parseInt(oraFinish),locatie, tipTratament,
                        (int)(double)durationList.get(Integer.parseInt(tipTratament)),
                        mapLocuriLibere.get(new AbstractMap.SimpleEntry<>(Integer.parseInt(locatie), Integer.parseInt(tipTratament))));

        if (!Objects.equals(checkResult, "")) {
            var entity = appointmentRepository.save(new Appointment(nume, cnp, data, locatie, tipTratament, checkResult));
            return entity.get().getId().toString();
        } else {
            return "-1";
        }
    }

    private String handlePlata(String input) {
        String[] elements = input.split("\\|");
        String data = elements[1];
        String cnp = elements[2];
        double suma = Double.parseDouble(elements[3]);
        paymentRepository.save(new Payment(data, cnp, suma));
        return "success";
    }

    private String handleCancel(String input) {
        // input == "appointment_id"
        Long appointment_id = Long.valueOf(input.strip().split("\\|")[1]);
        Appointment appointment =
                StreamSupport
                .stream(appointmentRepository.findAll().spliterator(), false)
                .filter(x -> x.getId().equals(appointment_id))
                .findFirst()
                .get();
        paymentRepository.save(new Payment(appointment.getDate(), appointment.getCnp(), costList.get(Integer.parseInt(appointment.getTreatment_type()))));
        appointmentRepository.delete(appointment_id);
        return "success";
    }

    String checkInterval(int startingHour, int finishingHour, String location, String treatmentType, int duration, int max) {
        List<Appointment> appointments = getAppointmentsForLocationAndTreatment(location, treatmentType);
        if (appointments.size() == max)
            return "";
        for (int i = startingHour; i < finishingHour; i++) {
            for (int j = 0; j <= 50; j += 10) {
                int currentStartMinutes = i * 60 + j;
                int currentFinishMinutes = currentStartMinutes + duration;
                boolean accepted = true;
                for (Appointment appointment : appointments) {
                    int appointmentStartMinutes = timeToMinutes(appointment.getHour());
                    int appointmentFinishMinutes = appointmentStartMinutes + duration;
                    if (currentFinishMinutes > finishingHour * 60) {
                        accepted = false;
                        break;
                    }
                    if (!((currentStartMinutes >= appointmentFinishMinutes) || (currentFinishMinutes <= appointmentStartMinutes))) {
                        accepted = false;
                        break;
                    }
                }
                if (accepted) {
                    return i + ":" + j;
                }
            }
        }
        return "";
    }

    private int timeToMinutes(String time) {
        return Integer.parseInt(time.split(":")[0]) * 60 + Integer.parseInt(time.split(":")[1]);
    }

    private List<Appointment> getAppointmentsForLocationAndTreatment(String location, String treatmentType) {
        List<Appointment> appointments = new ArrayList<>();
        for (Appointment app : appointmentRepository.findAll()) {
            if (app.getLocation().equals(location) && app.getTreatment_type().equals(treatmentType))
                appointments.add(app);
        }
        return appointments;
    }
}
