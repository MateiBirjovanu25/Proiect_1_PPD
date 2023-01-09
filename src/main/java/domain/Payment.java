package domain;

public class Payment extends Entity<Long>{
    private String date;
    private String cnp;
    private double amount;

    public Payment() {
    }

    public Payment(String date, String cnp, double amount) {
        this.date = date;
        this.cnp = cnp;
        this.amount = amount;
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
}
