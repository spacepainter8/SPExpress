/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CityOperations;

/**
 *
 * @author sofija
 */
public class bs200051_CityOperations implements CityOperations{

    private Connection conn = DB.getInstance().getConnection();
    
    @Override
    public int insertCity(String name, String postalCode) {
        String query = "select * from grad where upper(naziv)=upper(?) or postbr=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, name);
            ps.setString(2, postalCode);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()) {
                    System.out.println("Vec postoji grad sa imenom "+name+" ili postanskim brojem " + postalCode);
                    return -1;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        
        query = "insert into Grad values(?,?)";
        try(PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)){
            ps.setString(1, name);
            ps.setString(2, postalCode);
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if (rs.next()){
                    System.out.println("Grad uspesno dodat!");
                    return rs.getInt(1);
                } else {
                    System.out.println("Grad nije uspesno dodat!");
                    return -1;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        
    }

    @Override
    public int deleteCity(String... names) {
        int cnt = 0;
        String query = "delete from grad where upper(naziv)=upper(?)";
        for (String name:names){
            try(PreparedStatement ps = conn.prepareStatement(query)){
                ps.setString(1, name);
                cnt += ps.executeUpdate();
            } catch (SQLException ex) {
                if (ex.getErrorCode()==547){
                    System.out.println("Neki od gradova se koristi u drugim tabelama, obrisite prvo redove u tim tabelama");
                } else {
                    Logger.getLogger(bs200051_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
                return cnt;
            }
        }
        if (cnt == 0) System.out.println("Ne postoji nijedan grad sa nekim od tih naziva");
        else System.out.println("Uspesno obrisano "+cnt+" gradova");
        return cnt;
        
    }

    @Override
    public boolean deleteCity(int i) {
        String query = "delete from grad where idg=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, i);
            int cnt = ps.executeUpdate();
            if (cnt==0) {
                System.out.println("Ne postoji grad sa id " + i);
                return false;
            } else {
                System.out.println("Uspesno obrisan grad sa id "+i);
                return true;
            }
        } catch (SQLException ex) {
               if (ex.getErrorCode()==547){
                    System.out.println("Grad se koristi u drugim tabelama, obrisite prvo redove u tim tabelama");
                } else {
                    Logger.getLogger(bs200051_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
                return false;
        }
        
    }

    @Override
    public List<Integer> getAllCities() {
        List<Integer> ids = new ArrayList<>();
        String query = "select idg from grad";
        try(PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                ids.add(rs.getInt(1));
            }    
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
        return ids;
    }
    
   
    
}
