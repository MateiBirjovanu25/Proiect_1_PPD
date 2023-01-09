import domain.Appointment;
import domain.Payment;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import repository.AppointmentRepository;
import repository.PaymentRepository;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

public class TestDB {

    private static PaymentRepository paymentRepository;
    private static AppointmentRepository appointmentRepository;

    @BeforeAll
    static void setUp() {
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
    }

    @Test
    void testPaymentRepository() {
        int initialSize = 0;
        Iterator<Payment> payments = paymentRepository.findAll().iterator();
        while (payments.hasNext()) {
            initialSize++;
            payments.next();
        }

        paymentRepository.save(new Payment("a", "a", 2));

        int newSize = 0;
        payments = paymentRepository.findAll().iterator();
        while (payments.hasNext()) {
            newSize++;
            payments.next();
        }

        assert (initialSize == newSize - 1);
    }

    @Test
    void testAppointmentRepository() {
        int initialSize = 0;
        Iterator<Appointment> appointments = appointmentRepository.findAll().iterator();
        while (appointments.hasNext()) {
            initialSize++;
            appointments.next();
        }

        appointmentRepository.save(new Appointment("a", "a", "a","a","a","a"));

        int newSize = 0;
        appointments = appointmentRepository.findAll().iterator();
        while (appointments.hasNext()) {
            newSize++;
            appointments.next();
        }

        assert (initialSize == newSize - 1);
    }
}
