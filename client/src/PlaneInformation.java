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

    public String getName() {
        return name;
    }

    public int getMaxTakeOff() {
        return maxTakeOff;
    }

    public int getMaxLanding() {
        return maxLanding;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getMaxLoad() {
        return maxLoad;
    }

    public int getFlyHeight() {
        return flyHeight;
    }

    public int getFuel() {
        return fuel;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMaxTakeOff(int maxTakeOff) {
        this.maxTakeOff = maxTakeOff;
    }

    public void setMaxLanding(int maxLanding) {
        this.maxLanding = maxLanding;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setMaxLoad(int maxLoad) {
        this.maxLoad = maxLoad;
    }

    public void setFlyHeight(int flyHeight) {
        this.flyHeight = flyHeight;
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
    }
}
