package repository;

import domain.Appointment;
import domain.Payment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PaymentRepository implements Repository<Long, Payment> {

    String url;
    String username;
    String password;

    public PaymentRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Iterable<Payment> findAll() {
        String sql = "select pl.pid as plpid, pl.cnp as plcnp, pl.data as pldata, pl.suma as suma, pr.pid as prpid, pr.cnp as prcnp, pr.data as prdata, pr.locatie as locatie, pr.tip_tratament as tip_tratament, pr.ora as ora, pr.nume as nume from plata pl inner join programare pr on pr.pid = pl.programare_id;";
        Set<Payment> payments = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            ResultSet resultSet = connection.createStatement().executeQuery(sql);
            while (resultSet.next()) {
                Payment payment = new Payment(resultSet.getString("pldata"),
                        resultSet.getString("plcnp"),
                        resultSet.getDouble("suma"));
                Appointment appointment = new Appointment(
                        resultSet.getLong("prpid"),
                        resultSet.getString("nume"),
                        resultSet.getString("prcnp"),
                        resultSet.getString("prdata"),
                        resultSet.getString("locatie"),
                        resultSet.getString("tip_tratament"),
                        resultSet.getString("ora"));
                payment.setAppointment(appointment);
                payment.setId(resultSet.getLong("plpid"));
                payments.add(payment);
            }
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
        return payments;
    }

    @Override
    public Optional<Payment> save(Payment entity) {
        String sqlString = "insert into Plata (cnp, suma, data, programare_id) values ('%s', '%s', '%s', %d);";
        String sql = String
                .format(sqlString, entity.getCnp(), entity.getAmount(), entity.getDate(), entity.getAppointment().getId());
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            connection.createStatement().executeQuery(sql);
        } catch (SQLException error) {
            if(error.getMessage().contains("No results"))
                return Optional.empty();
            else
                System.out.println(error.getMessage());
        }
        return Optional.of(entity);
    }

    private Payment findPayment(Long id) throws SQLException {
        String sql = String.format("select pl.pid as plpid, pl.cnp as plcnp, pl.data as pldata, pl.suma as suma, pr.pid as prpid, pr.cnp as prcnp, pr.data as prdata, pr.locatie as locatie, pr.tip_tratament as tip_tratament, pr.ora as ora, pr.nume as nume from plata pl inner join programare pr on pr.pid = pl.programare_id where pid = %d", id);
        Connection connection = DriverManager.getConnection(url, username, password);
        var resultSet = connection.createStatement().executeQuery(sql);
        if (resultSet.next())
        {
            Payment payment = new Payment(resultSet.getString("pldata"),
                    resultSet.getString("plcnp"),
                    resultSet.getDouble("suma"));
            Appointment appointment = new Appointment(
                    resultSet.getLong("prpid"),
                    resultSet.getString("nume"),
                    resultSet.getString("prcnp"),
                    resultSet.getString("prdata"),
                    resultSet.getString("locatie"),
                    resultSet.getString("tip_tratament"),
                    resultSet.getString("ora"));
            payment.setAppointment(appointment);
            payment.setId(resultSet.getLong("plpid"));
            return  payment;
        }
        return null;
    }

    @Override
    public Optional<Payment> delete(Long id) {
        Payment payment = null;
        try {
            if ((payment = findPayment(id)) == null)
                return Optional.empty();
        } catch (SQLException ignored) {
        }
        String sql = String.format("delete from Plata where pid = %d", id);
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            connection.createStatement().executeQuery(sql);
        } catch (SQLException ignored) {
        }
        return Optional.ofNullable(payment);
    }
}
