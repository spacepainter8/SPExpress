/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.VehicleOperations;

/**
 *
 * @author sofija
 */
public class bs200051_VehicleOperations implements VehicleOperations {
    private Connection conn = DB.getInstance().getConnection();
    
    @Override
    public boolean insertVehicle(String licencePlateNumber, int fuelType, BigDecimal fuelConsumption) {
       
        
        
         // da li postoji vozilo sa istim reg brojem
        String query = "select * from vozilo where upper(regbr)=upper(?)";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, licencePlateNumber);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    System.out.println("Vec postoji vozilo sa registracionim brojem " + licencePlateNumber);
                    return false;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        // da li je tip goriva ispravan
        if (fuelType != 0 && fuelType != 1 && fuelType != 2){
            System.out.println("Tip goriva mora biti 0 - plin, 1 - dizel ili 2 - benzin");
            return false;
        }
        
        // sve ok dodaj vozilo
        // regbr, tipgoriva, potrosnja
        
        query = "insert into vozilo values(?,?,?)";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, licencePlateNumber);
            ps.setInt(2, fuelType);
            ps.setBigDecimal(3, fuelConsumption);
            int val = ps.executeUpdate();
            if (val==0){
                System.out.println("Vozilo nije dodato");
                return false;
            }
            else {
                System.out.println("Vozilo uspesno dodato");
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
//        return false;
    }

    @Override
    public int deleteVehicles(String... licencePlateNumbers) {
        int cnt = 0;
        String query = "delete from vozilo where upper(regbr)=upper(?)";
        for (String name:licencePlateNumbers){
            try(PreparedStatement ps = conn.prepareStatement(query)){
                ps.setString(1, name);
                cnt += ps.executeUpdate();
            } catch (SQLException ex) {
                    if (ex.getErrorCode()==547){
                    System.out.println("Vozilo se koristi u drugim tabelama, obrisite prvo redove u tim tabelama");
                } else {
                    Logger.getLogger(bs200051_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
                return cnt;
            }
        }
        if (cnt == 0) System.out.println("Ne postoji nijedno vozilo sa nekim od tih registracionih brojeva");
        else System.out.println("Uspesno obrisano "+cnt+" vozila");
        return cnt;
    
    }

    @Override
    public List<String> getAllVehichles() {
        List<String> licencePlates = new ArrayList<>();
        String query = "select regbr from vozilo";
        try(PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                licencePlates.add(rs.getString(1));
            }    
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
        return licencePlates;
    
    }

    @Override
    public boolean changeFuelType(String licencePlateNumber, int fuelType) {
    
        int idv = -1;
        // da li ima vozilo sa licenceplateom
        String query = "select idv from vozilo where upper(regbr)=upper(?)";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, licencePlateNumber);
            try(ResultSet rs = ps.executeQuery()){
                if (!rs.next()){
                    System.out.println("Ne postoji vozilo sa registracionim brojem " + licencePlateNumber);
                    return false;
                } else idv = rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        // da lii je ok fuel type
        if (fuelType != 0 && fuelType != 1 && fuelType != 2){
            System.out.println("Tip goriva mora biti 0 - plin, 1 - dizel ili 2 - benzin");
            return false;
        }
        
        query = "update vozilo set tipgoriva=? where idv=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, fuelType);
            ps.setInt(2, idv);
            int val = ps.executeUpdate();
            if (val==0){
                System.out.println("Nije promenjen tip goriva");
                return false;
            } else {
                System.out.println("Tip goriva uspesno promenjen");
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
//        return false;
    }

    @Override
    public boolean changeConsumption(String licencePlateNumber, BigDecimal fuelConsumption) {
        
        int idv = -1;
        // da li ima vozilo sa licenceplateom
        String query = "select idv from vozilo where upper(regbr)=upper(?)";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, licencePlateNumber);
            try(ResultSet rs = ps.executeQuery()){
                if (!rs.next()){
                    System.out.println("Ne postoji vozilo sa registracionim brojem " + licencePlateNumber);
                    return false;
                } else idv = rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        query = "update vozilo set potrosnja=? where idv=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setBigDecimal(1, fuelConsumption);
            ps.setInt(2, idv);
            int val = ps.executeUpdate();
            if (val==0){
                System.out.println("Nije promenjena potrosnja");
                return false;
            } else {
                System.out.println("Potrosnja uspesno promenjena");
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
//        return false;
    }
    
}
