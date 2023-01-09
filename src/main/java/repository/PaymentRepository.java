package repository;

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
        String sql = "select * from plata";
        Set<Payment> payments = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            ResultSet resultSet = connection.createStatement().executeQuery(sql);
            while (resultSet.next()) {
                Payment payment = new Payment(resultSet.getString("data"),
                        resultSet.getString("cnp"),
                        resultSet.getDouble("suma"));
                payment.setId(resultSet.getLong("pid"));
                payments.add(payment);
            }
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
        return payments;
    }

    @Override
    public Optional<Payment> save(Payment entity) {
        String sqlString = "insert into Plata(cnp, suma, data) values ('%s', '%s', '%s');";
        String sql = String
                .format(sqlString, entity.getCnp(), entity.getAmount(), entity.getDate());
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
        String sql = String.format("select * from Plata where pid = %d", id);
        Connection connection = DriverManager.getConnection(url, username, password);
        var resultSet = connection.createStatement().executeQuery(sql);
        if (resultSet.next())
        {
            Payment payment = new Payment(resultSet.getString("data"),
                    resultSet.getString("cnp"),
                    resultSet.getDouble("suma"));
            payment.setId(resultSet.getLong("pid"));
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
