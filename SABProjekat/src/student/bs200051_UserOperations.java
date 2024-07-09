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
import rs.etf.sab.operations.UserOperations;

/**
 *
 * @author sofija
 */
public class bs200051_UserOperations implements UserOperations {
    
    private Connection conn = DB.getInstance().getConnection();

    @Override
    public boolean insertUser(String userName, String firstName, String lastName, String password) {
        
       
        
        
        // proveri da li vec ima korisnik sa istim userName-om
        String query = "select * from korisnik where upper(korime)=upper(?)";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, userName);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    System.out.println("Vec postoji korisnik sa korisnickim imenom " + userName);
                    return false;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
         // firstName mora da pocinje velikim slovom
         if (!Character.isUpperCase(firstName.charAt(0))){
             System.out.println("Ime korisnika mora pocinjati velikim slovom");
             return false;
         }
         
         // lastName mora da pocinje velikim slovom
        if (!Character.isUpperCase(lastName.charAt(0))){
             System.out.println("Prezime korisnika mora pocinjati velikim slovom");
             return false;
        }
        
        // password - duza od 8 karaktera, barem jedno slovo i barem jedan broj
        if (password.length() < 8){
            System.out.println("Lozinka mora biti duza od 8 karaktera");
            return false;
        }
        
        int letterCount = 0;
        int numberCount = 0;
        for (Character c:password.toCharArray()){
            if (Character.isAlphabetic(c)) letterCount++;
            if (Character.isDigit(c)) numberCount++;
        }
        
        if (letterCount==0){
            System.out.println("Lozinka mora imati barem jedno slovo");
            return false;
        }
        
        if (numberCount==0){
            System.out.println("Lozinka mora imati barem jedan broj");
            return false;
        }
        
        
        // ime,prezime,korime,lozinka,brposlpaketa(0)
        query = "insert into korisnik values(?,?,?,?,0)";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, userName);
            ps.setString(4, password);
            int cnt = ps.executeUpdate();
            if (cnt!=0) {
                System.out.println("Novi korisnik uspesno dodat");
                return true;
            } else {
                System.out.println("Korisnik nije dodat");
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
//            return false;
        }
        
        return false;
    }

    @Override
    public int declareAdmin(String userName) {
       
     
        
         // da li postoji korisnik
        String query = "select idk from korisnik where upper(korIme)=upper(?)";
        int idk = -1;
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, userName);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    idk = rs.getInt(1);
                } else {
                    System.out.println("Ne postoji korisnik sa korisnickim imenom " + userName);
                    return 2;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            return 3;
        }
        
        // da li je vec admin
        query = "select * from administrator where idk=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, idk);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    System.out.println("Korisnik " + userName + " je vec admin");
                    return 1;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            return 3;
        }
        
        query = "insert into administrator values(?)";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, idk);
            int cnt = ps.executeUpdate();
            if (cnt==0){
                System.out.println("Neuspesno dodavanje admina");
                return 3;
            } else {
                System.out.println("Korisnik " + userName + " je postao admin");
                return 0;
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            return 3;
        }
        
//        return 3;
        
    }

    @Override
    public Integer getSentPackages(String... userNames) {
        // ako ne postoji nijedan od korisnika vrati null
        // inace saberi brposlatih paketa za sve korisnike iz argsa
        int numOfSentPackages = 0;
        int numOfExistingUsers = 0;
        
        String query = "select brposlpak from korisnik where upper(korime)=upper(?)";
        for (String userName:userNames){
            try(PreparedStatement ps = conn.prepareStatement(query)){
                ps.setString(1, userName);
                try(ResultSet rs = ps.executeQuery()){
                    if (rs.next()){
                        numOfExistingUsers++;
                        numOfSentPackages += rs.getInt(1);
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(bs200051_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
        
        if (numOfExistingUsers == 0) return null;
        else return numOfSentPackages;
    }

    @Override
    public int deleteUsers(String... userNames) {
        int cnt = 0;
        String query = "delete from korisnik where upper(korime)=upper(?)";
        for (String name:userNames){
            try(PreparedStatement ps = conn.prepareStatement(query)){
                ps.setString(1, name);
                cnt += ps.executeUpdate();
            } catch (SQLException ex) {    if (ex.getErrorCode()==547){
                    System.out.println("Korisnik se koristi u drugim tabelama, obrisite prvo redove u tim tabelama");
                } else {
                    Logger.getLogger(bs200051_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
                return cnt;
            }
        }
        if (cnt == 0) System.out.println("Ne postoji nijedan korisnik sa nekim od tih korisnickih imena");
        else System.out.println("Uspesno obrisano "+cnt+" korisnika");
        return cnt;
    
    }

    @Override
    public List<String> getAllUsers() {
        List<String> usernames = new ArrayList<>();
        String query = "select korime from korisnik";
        try(PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                usernames.add(rs.getString(1));
            }    
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
        return usernames;
    }
    
}
