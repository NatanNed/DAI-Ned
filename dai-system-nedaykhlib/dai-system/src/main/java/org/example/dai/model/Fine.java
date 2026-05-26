package org.example.dai.model;

/**
 * Модель штрафу в інформаційній системі ДАІ.
 */
public class Fine {

    private int fineId;
    private int vehicleId;
    private int driverId;
    private String fineDate;
    private double amount;
    private String violationType;
    private boolean paid;
    private boolean accident;

    public Fine() {
    }

    public Fine(int fineId, int vehicleId, int driverId, String fineDate,
                double amount, String violationType, boolean paid, boolean accident) {
        this.fineId = fineId;
        this.vehicleId = vehicleId;
        this.driverId = driverId;
        this.fineDate = fineDate;
        this.amount = amount;
        this.violationType = violationType;
        this.paid = paid;
        this.accident = accident;
    }

    public int getFineId() {
        return fineId;
    }

    public void setFineId(int fineId) {
        this.fineId = fineId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getFineDate() {
        return fineDate;
    }

    public void setFineDate(String fineDate) {
        this.fineDate = fineDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getViolationType() {
        return violationType;
    }

    public void setViolationType(String violationType) {
        this.violationType = violationType;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public boolean isAccident() {
        return accident;
    }

    public void setAccident(boolean accident) {
        this.accident = accident;
    }
}
