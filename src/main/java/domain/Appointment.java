package domain;

public class Appointment extends Entity<Long>{
    private String name;
    private String cnp;
    private String date;
    private String location;
    private String treatment_type;
    private String hour;

    public Appointment(String name, String cnp, String date, String location, String treatment_type, String hour) {
        this.name = name;
        this.cnp = cnp;
        this.date = date;
        this.location = location;
        this.treatment_type = treatment_type;
        this.hour = hour;
    }

    public Appointment(Long id, String name, String cnp, String date, String location, String treatment_type, String hour) {
        setId(id);
        this.name = name;
        this.cnp = cnp;
        this.date = date;
        this.location = location;
        this.treatment_type = treatment_type;
        this.hour = hour;
    }

    public String getName() {
        return name;
    }

    public String getCnp() {
        return cnp;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getTreatment_type() {
        return treatment_type;
    }

    public String getHour() {
        return hour;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "name='" + name + '\'' +
                ", cnp='" + cnp + '\'' +
                ", date='" + date + '\'' +
                ", location='" + location + '\'' +
                ", treatment_type='" + treatment_type + '\'' +
                ", hour='" + hour + '\'' +
                '}';
    }
}
