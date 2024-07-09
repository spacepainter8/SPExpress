/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import java.util.List;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CourierRequestOperation;

/**
 *
 * @author sofija
 */
public class bs200051_CourierRequestOperations implements CourierRequestOperation {

    private Connection conn = DB.getInstance().getConnection();
    
    @Override
    public boolean insertCourierRequest(String userName, String licencePlateNumber) {
        
       
        
       
        
        // da li korisnik postoji
        int idk = -1;
        String query = "select idk from korisnik where upper(korime)=upper(?)";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, userName);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    idk = rs.getInt(1);
                } else {
                    System.out.println("Ne postoji korisnik sa korisnickim imenom " + userName);
                    return false;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_CourierRequestOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
         // da li je vec kurir
        query = "select * from kurir where idk=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
             ps.setInt(1, idk);
             try(ResultSet rs = ps.executeQuery()){
                 if (rs.next()){
                     System.out.println("Korisnik sa korisnickim imenom " + userName + " je vec kurir");
                     return false;
                 }
             }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_CourierRequestOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        // da li postoji automobil
        int idv = -1;
        query = "select idv from vozilo where upper(regbr)=upper(?)";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, licencePlateNumber);
            try(ResultSet rs = ps.executeQuery()){
                if (!rs.next()){
                    System.out.println("Ne postoji vozilo sa registracionim brojem " + licencePlateNumber);
                    return false;
                } else {
                    idv = rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_CourierRequestOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        // da li vec ima zahtev
        query = "select * from zahtevkurir where idk=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, idk);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    System.out.println("Korisnik sa korisnickim imenom " + userName + " vec ima zahtev za kurira");
                    return false;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_CourierRequestOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        // idk, idv
        query = "insert into zahtevkurir values(?,?)";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, idk);
            ps.setInt(2, idv);
            int val = ps.executeUpdate();
            if (val==0){
                System.out.println("Nije bilo moguce dodati zahtev za kurira");
                return false;
            } else {
                System.out.println("Zahtev za kurira uspesno dodat");
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_CourierRequestOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    
        
    }

    @Override
    public boolean deleteCourierRequest(String userName) {
        
        // da li postoji korisnik sa ovim usernameom
        int idk = -1;
        String query = "select idk from korisnik where upper(korime)=upper(?)";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, userName);
            try(ResultSet rs = ps.executeQuery()){
                if (!rs.next()){
                    System.out.println("Ne postoji korisnik sa korisnickim imenom " + userName);
                    return false;
                } else {
                    idk = rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_CourierRequestOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        query = "delete from zahtevkurir where idk=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, idk);
            int val = ps.executeUpdate();
            if (val==0) {
                System.out.println("Korisnik nije imao zahteva za kurira");
                return false;
            } else {
                System.out.println("Zahtev uspesno obrisan");
                return true;
            }
        } catch (SQLException ex) {
                if (ex.getErrorCode()==547){
                    System.out.println("Zahtev kurir se koristi u drugim tabelama, obrisite prvo redove u tim tabelama");
                } else {
                    Logger.getLogger(bs200051_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
                return false;
        }
        
        
    }

    @Override
    public boolean changeVehicleInCourierRequest(String userName, String licencePlateNumber) {
        
       
        // da li postoji korisnik
        String query = "select idk from korisnik where upper(korime)=upper(?)";
        int idk = -1;
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, userName);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    idk = rs.getInt(1);
                } else {
                    System.out.println("Ne postoji korisnik sa korisnickim imenom " + userName);
                    return false;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_CourierRequestOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        
        // da li postoji zahtev
        query = "select * from zahtevkurir where idk=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, idk);
            try(ResultSet rs = ps.executeQuery()){
                if (!rs.next()){
                    System.out.println("Korisnik " + userName + " nema zahtev za kurira");
                    return false;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_CourierRequestOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        // da li postoji auto
        query = "select idv from vozilo where upper(regbr)=upper(?)";
        int idv = -1;
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, licencePlateNumber);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    idv = rs.getInt(1);
                } else {
                    System.out.println("Ne postoji vozilo sa registracionim brojem " + licencePlateNumber);
                    return false;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_CourierRequestOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        query = "update zahtevkurir set idv=? where idk=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, idv);
            ps.setInt(2, idk);
            int val = ps.executeUpdate();
            if (val==0){
                System.out.println("Dogodila se greska prilikom promene vozila");
                return false;
            } else {
                System.out.println("Uspesno ste promenili vozilo");
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_CourierRequestOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
//        return false;
    }

    @Override
    public List<String> getAllCourierRequests() {
        List<String> userNames = new ArrayList<>();
        String query = "select korime from zahtevkurir z join korisnik k on (z.idk=k.idk)";
        try(PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                userNames.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_CourierRequestOperations.class.getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
        return userNames;
    }

    @Override
    public boolean grantRequest(String userName) {
        // kroz proceduru
        // postoji li korisnik  -1
        // neko ne sme vec da bude kurir -2
        // da li ima zahtev za kurira -3
        // sme samo slobodan auto -4
        // obrisi zahtev 
        // dodaj kurira
        
        // uspeh 0
        
//        String query = "{ call SPBrojRadnikaSaImenom (?,?) }";
//        try ( CallableStatement cs = conn.prepareCall(query)) {
//            cs.setString(1, ime);
//            cs.registerOutParameter(2, java.sql.Types.INTEGER);
//            cs.execute();
//            return cs.getInt(2);
//        } catch (SQLException ex) {
//            Logger.getLogger(JDBC_vezbe.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        String query = "{call spGrantRequest (?,?)}";
        try(CallableStatement cs = conn.prepareCall(query)){
            cs.setString(1, userName);
            cs.registerOutParameter(2, java.sql.Types.INTEGER);
            cs.execute();
            int val = cs.getInt(2);
            switch (val) {
                case 0:{System.out.println("Uspesno odobren zahtev za kurira korisniku " + userName);return true;}
                case -1: {System.out.println("Korisnik sa korisnickim imenom " + userName + " ne postoji"); return false;}
                case -2:{System.out.println("Korisnik sa korisnickim imenom " + userName + " je vec kurir"); return false;}
                case -3:{System.out.println("Korisnik sa korisnickim imenom " + userName + " nema zahtev za kurira"); return false;}
                case -4:{System.out.println("Vozilo iz zahteva korisnika " + userName + " trenutno nije slobodno"); return false; }
                case -5:{System.out.println("Vozilo iz zahteva vise ne postoji u sistemu");return false;}
                default:
                    System.out.println("Nepoznata greska pri odobravanju zahteva za kurira"); return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_CourierRequestOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
//        System.out.println("Nepoznata greska pri odobravanju zahteva za kurira?");
//        return false;
        
    }
    
}
