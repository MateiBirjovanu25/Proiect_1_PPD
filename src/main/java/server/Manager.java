package server;

import domain.Appointment;
import domain.Payment;
import repository.AppointmentRepository;
import repository.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Manager {

    private volatile Integer users;
    private volatile Boolean check;
    private Repository<Long, Appointment> appointmentRepository;
    private Repository<Long, Payment> paymentRepository;
    private List<Double> listCosturi;
    private List<Double> listDurata;
    private Map<AbstractMap.SimpleEntry<Integer, Integer>, Integer> mapLocuriLibere;

    public Manager(Repository<Long, Appointment> appointmentRepositoryFile, Repository<Long, Payment> paymentRepositoryFile, List<Double> listCosturi, List<Double> listDurata, Map<AbstractMap.SimpleEntry<Integer, Integer>, Integer> mapLocuriLibere) {
        this.appointmentRepository = appointmentRepositoryFile;
        this.paymentRepository = paymentRepositoryFile;
        this.listCosturi = listCosturi;
        this.listDurata = listDurata;
        this.mapLocuriLibere = mapLocuriLibere;
        users = 0;
        check = Boolean.FALSE;
    }

    public synchronized void incrementUsers() {
        while (check) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        users++;
        this.notifyAll();
    }

    public synchronized void decrementUsers() {
        users--;
        this.notifyAll();
    }

    public synchronized String verify() {
        check = Boolean.TRUE;
        System.out.println("Started waiting.");
        while (users != 0) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Started checking.");
        String checkMessage = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + "\n";
        var payments = paymentRepository.findAll();
        var paymentsCollectedByLocation = StreamSupport.stream(payments.spliterator(), false).collect(Collectors.groupingBy(x -> x.getAppointment().getLocation()));
        var appointments = appointmentRepository.findAll();
        check = Boolean.FALSE;
        this.notifyAll();
        checkMessage += "---- Locations sold ----- \nLocation Name | Payed | All |\n";
        for(var locationPayments : paymentsCollectedByLocation.entrySet()) {
            var location = locationPayments.getKey();
            if(!locationPayments.getValue().isEmpty()) {
                var payed = locationPayments.getValue()
                                        .stream()
                                        .map(payment -> (int) payment.getAmount())
                                        .reduce(Integer::sum)
                                        .get();
                var canceled = locationPayments.getValue()
                                        .stream()
                                        .filter(x -> x.getAmount() < 0)
                                        .map(x -> x.getAppointment().getId())
                                        .toList();
                var shouldPay = locationPayments.getValue()
                                        .stream()
                                        .filter(x -> !canceled.contains(x.getAppointment().getId()))
                                        .map(x -> listCosturi.get(Integer.parseInt(x.getAppointment().getTreatment_type())).intValue())
                                        .reduce(Integer::sum)
                                        .get();
                checkMessage += location + " | " + payed + " | " + shouldPay + " |\n";
            }
        }
        checkMessage += " --- Not payed appointments ---\n";
        for(var app : appointments)
            if(StreamSupport.stream(payments.spliterator(), false).noneMatch(x -> x.getAppointment().getId().equals(app.getId())))
                checkMessage += app.getId() + " isn't payed.\n";
        return checkMessage;
    }
}