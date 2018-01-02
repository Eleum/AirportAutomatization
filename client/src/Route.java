import java.io.Serializable;

public class Route implements Serializable {
    public static final long serialVersionUID = 726616452;

    private String id;
    private String source;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private String days;
    private String planeType;
    private String status;
    private String gate;

    public Route(String id, String source, String departureTime, String days) {
        this.id = id;
        this.source = source;
        this.departureTime = departureTime;
        this.days = days;
    }

    public Route(String id, String source, String destination,
                 String departureTime, String arrivalTime, String days, String planeType, String gate) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.days = days;
        this.planeType = planeType;
        this.gate = gate;
    }

    public String getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getDays() {
        return days;
    }

    public String getPlaneType() {
        return planeType;
    }

    public String getStatus() {
        return status;
    }

    public String getGate() {
        return gate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public void setPlaneType(String planeType) {
        this.planeType = planeType;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setGate(String gate) {
        this.gate = gate;
    }
}