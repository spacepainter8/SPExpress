/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CourierOperations;

/**
 *
 * @author sofija
 */
public class bs200051_CourierOperations implements CourierOperations {
    private Connection conn = DB.getInstance().getConnection();
    
    @Override
    public boolean insertCourier(String courierUserName, String licencePlateNumber) {
         
        
        
        // postoji li korisnik 
        int idk = -1;
        String query = "select idk from korisnik where upper(korime)=upper(?)";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, courierUserName);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    idk = rs.getInt(1);
                } else {
                    System.out.println("Korisnik sa korisnickim imenom "+courierUserName + " ne postoji");
                    return false;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        // ne sme vec da bude kurir
        query = "select * from kurir where idk=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, idk);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    System.out.println("Korisnik sa korisnickim imenom " + courierUserName + " je vec kurir");
                    return false;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        // postoji li auto uopste
        int idv = -1;
        query = "select idv from vozilo where upper(regbr)=upper(?)";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, licencePlateNumber);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    idv = rs.getInt(1);
                } else {
                    System.out.println("Vozilo sa registracijom " + licencePlateNumber + " ne postoji");
                    return false;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        // sme samo slobodan auto 
        query = "select * from kurir where idv=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, idv);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    System.out.println("Neki kurir vec koristi vozilo sa registracijom " + licencePlateNumber);
                    return false;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        // dodaj kurira
        // idk, idv, 0, 0, 0
        query = "insert into kurir values(?,?,0,0,0)";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, idk);
            ps.setInt(2, idv);
            int val = ps.executeUpdate();
            if (val==0){
                System.out.println("Nepoznata greska prilikom dodavanja kurir");
                return false;
            } else {
                System.out.println("Kurir uspesno dodat");
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        
    }

    @Override
    public boolean deleteCourier(String courierUserName) {
       
        
        
         // da li je korisnik
        int idk = -1;
        String query = "select idk from korisnik where upper(korime)=upper(?)";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, courierUserName);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    idk = rs.getInt(1);
                } else {
                    System.out.println("Korisnik " + courierUserName + " ne postoji");
                    return false;
                }
            }
        } catch (SQLException ex) {
               if (ex.getErrorCode()==547){
                    System.out.println("Kurir se koristi u drugim tabelama, obrisite prvo redove u tim tabelama");
                } else {
                    Logger.getLogger(bs200051_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
                return false;
        }
        
        // da li je kurir
        int status = -1;
        query = "select status from kurir where idk = ?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, idk);
            try(ResultSet rs = ps.executeQuery()){
                if (!rs.next()){
                    System.out.println("Korisnik " + courierUserName + " nije kurir");
                    return false;
                } else {
                    status = rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        // napravi da ne moze da se obrise ako trenutno vozi
        // onda mu obrisi voznje sve prvo pa onda
        
        if (status == 1){
            System.out.println("Kurir trenutno vozi. Kada se zavrsi voznja moze da se obrise");
            return false;
        }
        
        List<Integer> idVs = new ArrayList<>();
        query = "select idvzn from voznja where idk=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, idk);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()) idVs.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        query = "delete from voznja where idvzn=?";
        for (int idv:idVs){
            try(PreparedStatement ps = conn.prepareStatement(query)){
                ps.setInt(1, idv);
                ps.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(bs200051_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
  
        
        
        query = "delete from kurir where idk=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, idk);
            int val = ps.executeUpdate();
            if (val==0){
                System.out.println("Nepoznata greska prilikom brisanja kurira");
                return false;
            } else {
                System.out.println("Uspesno obrisan kurir " + courierUserName);
                return true;
            }
        } catch (SQLException ex) {
                if (ex.getErrorCode()==547){
                    System.out.println("Kurir se koristi u drugim tabelama, obrisite prvo redove u tim tabelama");
                } else {
                    Logger.getLogger(bs200051_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
                return false;
        }
        
        
    }   

    @Override
    public List<String> getCouriersWithStatus(int statusOfCourier) {
        // status moze biti 0 ili 1
        List<String> userNames = new ArrayList<>();
        if (statusOfCourier  != 0 && statusOfCourier != 1){
            System.out.println("Status kurira moze biti 0 ili 1");
            return userNames;
        }
        
        String query = "select korime from kurir k1 join korisnik k on (k.idk=k1.idk) where k1.status=?";
        try(PreparedStatement ps = conn.prepareStatement(query);){
            ps.setInt(1, statusOfCourier);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()) userNames.add(rs.getString(1));
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
        
        return userNames;
    }

    @Override
    public List<String> getAllCouriers() {
        List<String> userNames = new ArrayList<>();
        String query = "select korime from kurir k1 join korisnik k on (k1.idk=k.idk)\n" +
"order by profit desc";
        try(PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()){
            while(rs.next()) userNames.add(rs.getString(1));
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
        
        return userNames;
    }

    @Override
    public BigDecimal getAverageCourierProfit(int numberOfDeliveries) {
        BigDecimal val = new BigDecimal(0);
        String query = "select coalesce(avg(profit),0)\n" +
                "from kurir\n" +
                "where brisppak>=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, numberOfDeliveries);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    val = rs.getBigDecimal(1);
                } else {
                    System.out.println("Doslo je do neocekivane greske");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        
        return val;
    }
    
}
