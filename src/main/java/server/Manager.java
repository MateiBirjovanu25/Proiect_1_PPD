package server;

import domain.Appointment;
import domain.Payment;
import repository.AppointmentRepository;
import repository.Repository;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

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
        while (users != 0) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}