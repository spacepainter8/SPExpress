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
import rs.etf.sab.operations.DistrictOperations;

/**
 *
 * @author sofija
 */
public class bs200051_DistrictOperations implements DistrictOperations {
    private Connection conn = DB.getInstance().getConnection();
    
    @Override
    public int insertDistrict(String name, int cityId, int xCord, int yCord) {
       
        
        // da li postoji grad
        String query = "select * from grad where idg=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, cityId);
            try(ResultSet rs = ps.executeQuery()){
                if (!rs.next()){
                    System.out.println("Grad sa id " + cityId + " ne postoji");
                    return -1;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_DistrictOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        
        // da li postoji opstina sa istim koordinatama
        query = "select * from opstina where x=? and y=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, xCord);
            ps.setInt(2, yCord);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    System.out.println("Vec postoji opstina sa tim koordinatama");
                    return -1;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_DistrictOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        
        // da li postoji opstina u datom gradu sa istim imenom
        query = "select * from opstina where idg=? and upper(naziv)=upper(?)";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, cityId);
            ps.setString(2, name);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    System.out.println("Vec postoji opstina u gradu sa id " + cityId + " sa imenom "+name);
                    return -1;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_DistrictOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        
        // dodaj opstinu (naziv, x, y, idg)
        query = "insert into opstina values(?,?,?,?)";
        try(PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)){
            ps.setString(1, name);
            ps.setInt(2, xCord);
            ps.setInt(3, yCord);
            ps.setInt(4, cityId);
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if (rs.next()){
                    System.out.println("Opstina uspesno dodata!");
                    return rs.getInt(1);
                } else {
                    System.out.println("Opstina nije dodata!");
                    return -1;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_DistrictOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        
        
//        return -1;
    }

    @Override
    public int deleteDistricts(String... names) {
        int cnt = 0;
        String query = "delete from opstina where upper(naziv)=upper(?)";
        for (String name:names){
            try(PreparedStatement ps = conn.prepareStatement(query)){
                ps.setString(1, name);
                cnt += ps.executeUpdate();
            } catch (SQLException ex) {
                    if (ex.getErrorCode()==547){
                    System.out.println("Opstina se koristi u drugim tabelama, obrisite prvo redove u tim tabelama");
                } else {
                    Logger.getLogger(bs200051_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
                return cnt;
            }
        }
        if (cnt == 0) System.out.println("Ne postoji nijedna opstina sa nekim od tih naziva");
        else System.out.println("Uspesno obrisano "+cnt+" opstina");
        return cnt;
    }

    @Override
    public boolean deleteDistrict(int i) {
        String query = "delete from opstina where ido=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, i);
            int cnt = ps.executeUpdate();
            if (cnt==0) {
                System.out.println("Ne postoji opstina sa id " + i);
                return false;
            } else {
                System.out.println("Uspesno obrisana opstina sa id "+i);
                return true;
            }
        } catch (SQLException ex) {
                if (ex.getErrorCode()==547){
                    System.out.println("Opstina se koristi u drugim tabelama, obrisite prvo redove u tim tabelama");
                } else {
                    Logger.getLogger(bs200051_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
                return false;
        }
//        return false;
    }

    @Override
    public int deleteAllDistrictsFromCity(String nameOfTheCity) {
        
        
        int cnt = 0;
        int idg = -1;
        
        // da li grad postoji
        // ako postoji usput dohvati njegov id
        String query = "select idg from grad where upper(naziv)=upper(?)";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, nameOfTheCity);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    idg = rs.getInt(1);
                } else {
                    System.out.println("Ne postoji grad sa imenom " + nameOfTheCity);
                    return cnt;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_DistrictOperations.class.getName()).log(Level.SEVERE, null, ex);
            return cnt;
        }
        
        
        // obrisi sve opstine sa idg = id grada
        query = "delete from opstina where idg=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, idg);
            cnt +=ps.executeUpdate();
        } catch (SQLException ex) {
                if (ex.getErrorCode()==547){
                    System.out.println("Kurir se koristi u drugim tabelama, obrisite prvo redove u tim tabelama");
                } else {
                    Logger.getLogger(bs200051_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
                return cnt;
        }
        
        if (cnt == 0) System.out.println("Ne postoji nijedna opstina u gradu koji se zove " + nameOfTheCity);
        else System.out.println("Uspesno obrisano "+cnt+" opstina");
        return cnt;
    }

    @Override
    public List<Integer> getAllDistrictsFromCity(int idCity) {
        // proveri ima li grad sa idCity, ako nema vrati null
        String query = "select * from grad where idg=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, idCity);
            try(ResultSet rs = ps.executeQuery()){
                if (!rs.next()){
                    System.out.println("Ne postoji grad sa id "+idCity);
                    return null;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_DistrictOperations.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        List<Integer> idOs = new ArrayList<>();
        query = "select ido from opstina where idg=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, idCity);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    idOs.add(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_DistrictOperations.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        return idOs;
    }

    @Override
    public List<Integer> getAllDistricts() {
        List<Integer> ids = new ArrayList<>();
        String query = "select ido from opstina";
        try(PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                ids.add(rs.getInt(1));
            }    
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_DistrictOperations.class.getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
        return ids;
    }
    
}
