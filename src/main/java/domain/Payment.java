package domain;

public class Payment extends Entity<Long>{
    private String date;
    private String cnp;
    private double amount;
    private Appointment appointment;

    public Payment() {}

    public Payment(String date, String cnp, double amount) {
        this.date = date;
        this.cnp = cnp;
        this.amount = amount;
    }

    public Payment(String date, String cnp, double amount, Appointment appointment) {
        this.date = date;
        this.cnp = cnp;
        this.amount = amount;
        this.appointment = appointment;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCnp(String cnp) {
        this.cnp = cnp;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public String getDate() {
        return date;
    }

    public String getCnp() {
        return cnp;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "date='" + date + '\'' +
                ", cnp='" + cnp + '\'' +
                ", amount=" + amount +
                ", appointment=" + appointment +
                '}';
    }
}
