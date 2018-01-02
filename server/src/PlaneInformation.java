import java.io.Serializable;

public class PlaneInformation implements Serializable {
    public static final long serialVersionUID = 726616452;

    private String name;
    private int maxTakeOff;
    private int maxLanding;
    private int capacity;
    private int maxLoad;
    private int flyHeight;
    private int fuel;

    public PlaneInformation(String name, int maxTakeOff, int maxLanding, int capacity, int maxLoad, int flyHeight, int fuel) {
        this.name = name;
        this.maxTakeOff = maxTakeOff;
        this.maxLanding = maxLanding;
        this.capacity = capacity;
        this.maxLoad = maxLoad;
        this.flyHeight = flyHeight;
        this.fuel = fuel;
    }
}