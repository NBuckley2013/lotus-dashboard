/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vehiclesalesapp;

/**
 *
 * @authors Neil Buckley, Steve Pye
 */
public class Sales {
    
    final private Integer QTR, Quantity;
    final private String Region, Vehicle, Year;
    
    public Sales(Integer QTR, Integer Quantity, String Year, String Region, String Vehicle) {
        this.QTR = QTR;
        this.Quantity = Quantity;
        this.Year = Year;
        this.Region = Region;
        this.Vehicle = Vehicle;
    }
   
    // for testing
    @Override
    public String toString() {
        return "Sales{" + "QTR=" + QTR + ", Quantity=" + Quantity + ", Year=" + Year + ", Region=" + Region + ", Vehicle=" + Vehicle + '}' + "\n";
    }
    
    public Integer getQTR() {
        return QTR;
    }

    public Integer getQuantity() {
        return Quantity;
    }

    public String getYear() {
        return Year;
    }

    public String getRegion() {
        return Region;
    }

    public String getVehicle() {
        return Vehicle;
    }
}