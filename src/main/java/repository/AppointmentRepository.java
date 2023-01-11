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

public class AppointmentRepository implements Repository<Long, Appointment>{

    String url;
    String username;
    String password;

    public AppointmentRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Iterable<Appointment> findAll() {
        String sql = "select * from programare";
        Set<Appointment> appoinments = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            ResultSet resultSet = connection.createStatement().executeQuery(sql);
            while (resultSet.next())
            {
                Appointment appointment = new Appointment(
                        resultSet.getString("nume"),
                        resultSet.getString("cnp"),
                        resultSet.getString("data"),
                        resultSet.getString("locatie"),
                        resultSet.getString("tip_tratament"),
                        resultSet.getString("ora"));
                appointment.setId(resultSet.getLong("pid"));
                appoinments.add(appointment);
            }

        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
        return appoinments;
    }

    @Override
    public Optional<Appointment> save(Appointment entity) {
        String sqlString = "insert into programare(cnp,data,locatie,tip_tratament,ora,nume) " +
                "values ('%s', '%s', '%s', '%s', '%s', '%s') returning pid;";
        String sql = String
                .format(sqlString, entity.getCnp(), entity.getDate(), entity.getLocation(),
                        entity.getTreatment_type(), entity.getHour(), entity.getName());
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            var resultSet = connection.createStatement().executeQuery(sql);
            if(resultSet.next())
                entity.setId(resultSet.getLong("pid"));
        } catch (SQLException error) {
            if(error.getMessage().contains("No results"))
                return Optional.empty();
            else
                System.out.println(error.getMessage());
        }
        return Optional.of(entity);
    }

    private Appointment findAppointment(Long id) throws SQLException {
        String sql = String.format("select * from programare where pid = %d", id);
        Connection connection = DriverManager.getConnection(url, username, password);
        var resultSet = connection.createStatement().executeQuery(sql);
        if (resultSet.next())
        {
            Appointment appointment = new Appointment(resultSet.getString("nume"),
                    resultSet.getString("cnp"),
                    resultSet.getString("data"),
                    resultSet.getString("locatie"),
                    resultSet.getString("tip_tratament"),
                    resultSet.getString("ora"));
            appointment.setId(resultSet.getLong("pid"));
            return  appointment;
        }
        return null;
    }

    @Override
    public Optional<Appointment> delete(Long id) {
        Appointment appointment = null;
        try {
            if ((appointment = findAppointment(id)) == null)
                return Optional.empty();
        } catch (SQLException ignored) {
        }
        String sql = String.format("delete from programare where pid = %d", id);
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            connection.createStatement().executeQuery(sql);
        } catch (SQLException ignored) {
        }
        return Optional.ofNullable(appointment);
    }
}
