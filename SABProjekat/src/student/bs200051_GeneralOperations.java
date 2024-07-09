/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import rs.etf.sab.operations.GeneralOperations;

/**
 *
 * @author sofija
 */
public class bs200051_GeneralOperations implements GeneralOperations {
    private Connection conn = DB.getInstance().getConnection();
    
    @Override
    public void eraseAll() {
        try(Statement s = conn.createStatement()){
            s.addBatch("delete from zahtevkurir");
            s.addBatch("delete from ponuda");
            s.addBatch("delete from prevoz");
            s.addBatch("delete from voznja");
            s.addBatch("delete from paket");
            s.addBatch("delete from kurir");
            s.addBatch("delete from vozilo");
            s.addBatch("delete from administrator");
            s.addBatch("delete from zahtevprevoz");
            s.addBatch("delete from korisnik");
            s.addBatch("delete from opstina");
            s.addBatch("delete from grad");
            s.executeBatch();
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
