/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.PackageOperations;
import java.util.Random;

/**
 *
 * @author sofija
 */
public class bs200051_PackageOperations implements PackageOperations {
  
    
    private Connection conn = DB.getInstance().getConnection();
 
    @Override
    public int insertPackage(int districtFrom, int districtTo, String userName, int packageType, BigDecimal weight) {
       
 
        // postoji li districtFrom
        String query = "select * from opstina where ido=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, districtFrom);
            try(ResultSet rs = ps.executeQuery()){
                if (!rs.next()){
                    System.out.println("Ne postoji opstina sa id " + districtFrom);
                    return -1;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        
        // postoji li districtTo
        query = "select * from opstina where ido=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, districtTo);
            try(ResultSet rs = ps.executeQuery()){
                if (!rs.next()){
                    System.out.println("Ne postoji opstina sa id " + districtTo);
                    return -1;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        
        // postoji li korisnik
        int idk = -1;
        query = "select idk from korisnik where upper(korime)=upper(?)";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    idk = rs.getInt(1);
                } else {
                    System.out.println("Ne postoji korisnik " + userName);
                    return -1;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        
         // da li je packageType odgovarajuci
         // moze biti 0,1,2
         if (packageType != 0 && packageType != 1 && packageType != 2){
             System.out.println("Tip paketa mora biti 0 - pismo ili 1 - standardno ili 2 - lomljivo");
             return -1;
         }
         
        // da li je weight broj veci od nule 
        if (weight.doubleValue() <=0)   {
            System.out.println("Tezina paketa mora biti veca od 0");
            return -1;
        }
        
        // ubaci paket
        // 0 null null null
        query = "insert into paket values (0,null,null,null)";
        int idp = -1;
        try(PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)){
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if (rs.next()){
                    idp = rs.getInt(1);
                } else {
                    System.out.println("Doslo je do neocekivane greske prilikom dodavanja paketa");
                    return -1;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        
        // ubaci zahtevprevoz
        // idp idk districtFrom districtTo packageType weight
        query = "insert into zahtevprevoz values (?,?,?,?,?,?)";
        try(PreparedStatement ps = conn.prepareStatement(query)){
             ps.setInt(1, idk);ps.setInt(2, districtFrom); 
             ps.setInt(3, districtTo);
            ps.setInt(4, packageType); ps.setBigDecimal(5, weight);ps.setInt(6, idp);
            int val = ps.executeUpdate();
            if (val==0){
                System.out.println("Doslo je do neocekivane greske pilikom dodavanja paketa");
                return -1;
            } else {
                System.out.println("Uspesno ste dodali paket");
                
                return idp;
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        
        
    }

    @Override
    public int insertTransportOffer(String couriersUserName, int packageId, BigDecimal pricePercentage) {
       
       
        // postoji li korisnik
        int idk = -1;
        String query = "select idk from korisnik where upper(korime)=upper(?)";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, couriersUserName);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    idk = rs.getInt(1);
                } else {
                    System.out.println("Korisnik " + couriersUserName + " ne postoji");
                    return -1;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        
        // da li je kurir
        query = "select * from kurir where idk=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, idk);
            try(ResultSet rs = ps.executeQuery()){
                if (!rs.next()){
                    System.out.println("Korisnik " + couriersUserName + " nije kurir");
                    return -1;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        
        // kurir mora biti u statusu 0 - ne vozi
        query = "select status from kurir where idk=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, idk);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    if (rs.getInt(1) != 0) {
                        System.out.println("Kurir " + couriersUserName + " trenutno vozi");
                        return -1;
                    }
                } else {
                    System.out.println("Doslo je do neocekivane greske prilikom dodavanja ponude");
                    return -1;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        
        // postoji li paket
        // paket takodje mora biti u statusu 0
        query = "select statusisp from paket where idp=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, packageId);
            try(ResultSet rs = ps.executeQuery()){
                if (!rs.next()){
                    System.out.println("Ne postoji paket sa id " + packageId);
                    return -1;
                } else {
                    int val = rs.getInt(1);
                    if (val != 0) {
                        System.out.println("Paket mora imati status 0 - kreiran");
                        return -1;
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        
        // pricePercentage mozda je null onda treba rand
        if (pricePercentage == null ){
            // treba da se  postavi neka rendom vrednost
            // izmedju -10 i +10
            Random rand = new Random();
            double perc = (rand.nextDouble() * 20.000) - 10.000;
            pricePercentage = new BigDecimal(perc);
        }
        
        // ubaci ponudu
        // idk procenat idpaketa
        query = "insert into ponuda values (?,?,?)";
        try(PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)){
            ps.setInt(1, idk);
            ps.setBigDecimal(2, pricePercentage);
            ps.setInt(3, packageId);
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if (rs.next()){
                    System.out.println("Uspesno ste dodali ponudu");
                    return rs.getInt(1);
                    
                } else {
                    System.out.println("Doslo je do neocekivane greske prilikom dodavanja ponude");
                    return -1;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        
//        return -1;
    }

    @Override
    public boolean acceptAnOffer(int offerId) {

        // postoji li ponuda 
        // idkurira procenat idpaketa
        int idKurir = -1;
        BigDecimal perc = null;
        int idPaket = -1;
        
        String query = "select idk, procenat, idp from ponuda where idpon=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, offerId);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    idKurir = rs.getInt(1);
                    perc = rs.getBigDecimal(2);
                    idPaket = rs.getInt(3);
                } else {
                    System.out.println("Ne postoji ponuda sa id " + offerId);
                    return false;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        // apdejtuj paket -> ovo bi trebalo da triggeruje brisanje drugih ponuda
        
        // treba sracunati cenu
        //  treba mi tip paketa, tezina paketa, koordinate obe opstine
        int tip = -1; BigDecimal tezina = null; int x1 = -1; int y1 = -1; int x2 = -1; int y2 = -1;
        query = "select zp.TipPaketa as tip, zp.TezinaPaketa as tezina, o1.x as x1, o1.y as y1, o2.x as x2, o2.y as y2 \n" +
        "from ZahtevPrevoz zp join Opstina o1 on (zp.idopre=o1.ido) join Opstina o2 on (zp.idodos=o2.ido)\n" +
        "where zp.idp=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1,idPaket);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    tip = rs.getInt("tip"); tezina = rs.getBigDecimal("tezina");
                    x1 = rs.getInt("x1"); y1 = rs.getInt("y1");
                    x2 = rs.getInt("x2"); y2 = rs.getInt("y2");
                }
            }  
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        BigDecimal osnovnaCena = null;
        BigDecimal tezinskiFaktor = null;
        BigDecimal cenaPoKG = null;
        
        switch (tip) {
            case 0:{
                osnovnaCena = new BigDecimal(10.0);
                tezinskiFaktor = new BigDecimal(0.0);
                cenaPoKG = new BigDecimal(0.0);
                break;
            }
            case 1: {
                osnovnaCena = new BigDecimal(25.0);
                tezinskiFaktor = new BigDecimal(1.0);
                cenaPoKG = new BigDecimal(100.0);
                break;
            }
            case 2: {
                osnovnaCena = new BigDecimal(75.0);
                tezinskiFaktor = new BigDecimal(2.0);
                cenaPoKG = new BigDecimal(300.0);
                break;
            }
            default:
                System.out.println("Nepoznat tip paketa");
        }
        
        perc = new BigDecimal(perc.doubleValue()/100.0);
        int ysquared = (y2-y1)*(y2-y1);
        int xsquared = (x2-x1)*(x2-x1);
        double res = Math.sqrt(ysquared+xsquared);
        BigDecimal euclid = new BigDecimal(res);
        BigDecimal cena = new BigDecimal((osnovnaCena.doubleValue()+(tezinskiFaktor.doubleValue()*tezina.doubleValue())*cenaPoKG.doubleValue())*euclid.doubleValue());
        cena = new BigDecimal(cena.doubleValue() + cena.doubleValue()*perc.doubleValue());
        
        
        // status cena vreme kurir
        query = "update paket set statusisp=1, cena=?, vremeprihv=getdate(),idk=? where idp=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setBigDecimal(1, cena);
            ps.setInt(2, idKurir); ps.setInt(3, idPaket);
            int val = ps.executeUpdate();
            if (val==0){
                System.out.println("Doslo je do neocekivane greske prilikom prihvatanja ponude");
                return false;
            } else {
                System.out.println("Ponuda uspesno prihvacena");
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
//        return false;
    }

    @Override
    public List<Integer> getAllOffers() {
        List<Integer> idPons = new ArrayList<>();
        String query = "select idpon from ponuda";
        try(PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()){
            while(rs.next()) idPons.add(rs.getInt(1));
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
        return idPons;
    }

    @Override
    public List<Pair<Integer, BigDecimal>> getAllOffersForPackage(int packageId) {
        List<Pair<Integer, BigDecimal>> offers = new ArrayList<>();
        
        String query = "select * from paket where idp=?";
        try(PreparedStatement ps  = conn.prepareStatement(query)){
            ps.setInt(1, packageId);
            try(ResultSet rs = ps.executeQuery()){
                if (!rs.next()){
                    System.out.println("Ne postoji paket sa id " + packageId);
                    return offers;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
        
        query = "select idpon, procenat from ponuda where idp=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, packageId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    int idpon = rs.getInt(1);
                    BigDecimal perc = rs.getBigDecimal(2);
                    Pair<Integer, BigDecimal> pair = new bs200051_PackageOperationsPair<>(idpon, perc);
                    offers.add(pair);
                }
            
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
        
        return offers;
    }

    @Override
    public boolean deletePackage(int packageId) {
        int status = -1;
        String query = "select statusisp from paket where idp = ?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, packageId);
            try(ResultSet rs = ps.executeQuery()){
                if (!rs.next()) {
                    System.out.println("Ne postoji paket sa id " + packageId);
                    return false;
                } else status = rs.getInt(1);
            }
        } catch (SQLException ex) {
                if (ex.getErrorCode()==547){
                    System.out.println("Paket se koristi u drugim tabelama, obrisite prvo redove u tim tabelama");
                } else {
                    Logger.getLogger(bs200051_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
                return false;
        }
        
        if (status == 2 || status == 3){
            System.out.println("Paket je vec pokupljen ili odvezen");
            return false;
        }
        
        query = "delete from zahtevprevoz where idp=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, packageId);
            int val = ps.executeUpdate();
            if(val==0){
                System.out.println("Doslo je do neocekivane greske prilikom brisanja paketa");
                return false;
            } 
        } catch (SQLException ex) {
                if (ex.getErrorCode()==547){
                    System.out.println("Paket se koristi u drugim tabelama, obrisite prvo redove u tim tabelama");
                } else {
                    Logger.getLogger(bs200051_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
                return false;
        }
        
        query = "delete from paket where idp=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, packageId);
            int val = ps.executeUpdate();
            if(val==0){
                System.out.println("Doslo je do neocekivane greske prilikom brisanja paketa");
                return false;
            } else {
                System.out.println("Paket uspesno obrisan");
                return true;
            }
        } catch (SQLException ex) {
                if (ex.getErrorCode()==547){
                    System.out.println("Paket se koristi u drugim tabelama, obrisite prvo redove u tim tabelama");
                } else {
                    Logger.getLogger(bs200051_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
                return false;
        }
        
        
    }

    @Override
    public boolean changeWeight(int packageId, BigDecimal newWeight) {
        // postoji li paket
        // da li je vec prihvacena ponuda za njega
        // da li je tezina vecina od 0
        String query = "select statusisp from paket where idp=?";
        int status = -1;
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, packageId);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    status = rs.getInt(1); 
                } else {
                    System.out.println("Paket sa id " + packageId + " ne postoji");
                    return false;
                }
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        if (status != 0 ){
            System.out.println("Ponuda za paket " + packageId + " je vec prihvacena. Nema smisla menjati tezinu.");
            return false;
        }
        
        if (newWeight.doubleValue() <=0){
            System.out.println("Nova tezina mora biti veca od 0");
            return false;
        }
        
        query = "update zahtevprevoz set tezinapaketa=? where idp=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setBigDecimal(1, newWeight);
            ps.setInt(2, packageId);
            int val = ps.executeUpdate();
            if (val==0){
                System.out.println("Doslo je do greske prilikom promene tezine paketa");
                return false;
            } else {
                System.out.println("Tezina paketa uspesno promenjena");
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean changeType(int packageId, int packageType) {
        String query = "select statusisp from paket where idp=?";
        int status = -1;
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, packageId);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    status = rs.getInt(1); 
                } else {
                    System.out.println("Paket sa id " + packageId + " ne postoji");
                    return false;
                }
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        if (status != 0 ){
            System.out.println("Ponuda za paket " + packageId + " je vec prihvacena. Nema smisla menjati tezinu.");
            return false;
        }
        
        if (packageType != 0 && packageType != 1 && packageType != 2){
             System.out.println("Tip paketa mora biti 0 - pismo ili 1 - standardno ili 2 - lomljivo");
             return false;
         }
        
        query = "update zahtevprevoz set tippaketa=? where idp=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, packageType);
            ps.setInt(2, packageId);
            int val = ps.executeUpdate();
            if (val==0){
                System.out.println("Doslo je do greske prilikom promene tipa paketa");
                return false;
            } else {
                System.out.println("Tip paketa uspesno promenjen");
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public Integer getDeliveryStatus(int packageId) {
        // ako nema paketa vrati null inace status
        String query = "select statusisp from paket where idp=?";
        Integer status = null;
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1,packageId);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    status = rs.getInt(1);
                    return status;
                } else {
                    System.out.println("Ne postoji paket sa id " + packageId);
                    return null;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
//        return null;
    }

    @Override
    public BigDecimal getPriceOfDelivery(int packageId) {
        String query = "select cena from paket where idp=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, packageId);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return rs.getBigDecimal(1);
                } else {
                    System.out.println("Ne postoji paket sa id " + packageId + " ili cena nije izracunata");
                    return null;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Date getAcceptanceTime(int packageId) {
        String query = "select vremeprihv from paket where idp=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, packageId);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return rs.getDate(1);
                } else {
                    System.out.println("Ne postoji paket sa id " + packageId + " ili cena nije izracunata");
                    return null;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public List<Integer> getAllPackagesWithSpecificType(int packageType) {
        if (packageType != 0 && packageType != 1 && packageType != 2){
            System.out.println("Tip paketa mora biti 0 - pismo ili 1 - standardno ili 2 - lomljivo");
            return new ArrayList<>();
        }
        
        List<Integer> idps = new ArrayList<>();
        String query = "select idp from zahtevprevoz where tippaketa=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, packageType);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    idps.add(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
         
        return idps;
    }

    @Override
    public List<Integer> getAllPackages() {
        List<Integer> idps = new ArrayList<>();
        String query = "select idp from paket";
        try(PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()){
            while(rs.next()) idps.add(rs.getInt(1));
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
        
        return idps;
    }

    @Override
    public List<Integer> getDrive(String courierUsername) {
        
      
        
        
        // postoji li koristnik
        int idk = -1;
        String query = "select idk from korisnik where upper(korime)=upper(?)";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, courierUsername);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    idk = rs.getInt(1);
                } else {
                    System.out.println("Korisnik " + courierUsername + " ne postoji");
                    return null;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        // da li je kurir
        int status = -1;
        query = "select status from kurir where idk=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, idk);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    status = rs.getInt(1);
                } else {
                    System.out.println("Korisnik " + courierUsername + " nije kurir");
                    return null;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        // ako je u statusu ne vozi vrati null
        
        if (status==0){
            // ne vozi
            return null;
        } else {
            // vozi
            //vrati idove paketa sa statusom 2 iz trenutne voznje
            query = "select p.idp\n" +
                    "from voznja v join prevoz p on (v.idvzn=p.idvzn) join paket pak on (p.idp=pak.idp)\n" +
                    "where v.idk=? and v.profit is null and pak.StatusIsp=2";
            List<Integer> idps = new ArrayList<>();
            try(PreparedStatement ps = conn.prepareStatement(query)){
                ps.setInt(1, idk);
                try(ResultSet rs = ps.executeQuery()){
                    while(rs.next()) idps.add(rs.getInt(1));
                }
            } catch (SQLException ex) {
                Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
            return idps;
        }
//        return null;
       
    }

    @Override
    public int driveNextPackage(String courierUserName) {
        
        // postoji li korisnik
        int idk = -1;
        String query = "select idk from korisnik where upper(korime)=upper(?)";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, courierUserName);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    idk = rs.getInt(1);
                } else {
                    System.out.println("Korisik " + courierUserName + " ne postoji");
                    return -2;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -2;
        }
        
        // da li je kurir
        int kurirStatus = -1;
        query = "select status from kurir where idk=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, idk);
            try(ResultSet rs = ps.executeQuery()){
                if (!rs.next()){
                    System.out.println("Korisnik " + courierUserName + " nije kurir");
                    return -2;
                } else {
                    kurirStatus = rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -2;
        }
        
        List<Integer> idps = new ArrayList<>();
        int chosenIdp = -1;
        int idvzn = -1;
        
        if (kurirStatus==0){
            // ne vozi
            
            // moze da zapocne voznju
            // dohvati sve pakete sa statusom 1, a za koje je idk=idk
                // orderuj pakete po idp ili po datum prihvatanja?
            query = "select idp\n" +
                    "from paket\n" +
                    "where idk=? and statusisp=1\n" +
                    "order by vremeprihv";
            try(PreparedStatement ps = conn.prepareStatement(query)){
                ps.setInt(1, idk);
                try(ResultSet rs = ps.executeQuery()){
                    while(rs.next()) idps.add(rs.getInt(1));
                }
            } catch (SQLException ex) {
                Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
                return -2;
            }
            // proveri da li je lista prazna
            if (idps.isEmpty()) return -1;
            
            // ti paketi sada cine jednu voznju, kreiraj voznju
            // NULL, idk
            query = "insert into voznja values(null,?)";
            
            try(PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)){
                ps.setInt(1, idk);
                int val = ps.executeUpdate();
                if (val==0){
                  System.out.println("puklo");
                  return -2;
                } 
                try (ResultSet rs = ps.getGeneratedKeys()){
                    if (rs.next()) idvzn = rs.getInt(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
                return -2;
            }
            // za sve te pakete dodaj red VOZNJA PAKET u prevoz tabelu
            // idvzn idp
            query = "insert into prevoz values(?,?)";
            for (int idp:idps){
                try(PreparedStatement ps = conn.prepareStatement(query)){
                    ps.setInt(1, idvzn);
                    ps.setInt(2, idp);
                    int val = ps.executeUpdate();
                    if (val==0){
                        System.out.println("puklo");
                        return -2;
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
                    return -2;
                }
            }
            // za sve te pakete postavi status na 2
            query = "update paket set statusisp=2 where idp=?";
            for (int idp:idps){
                try(PreparedStatement ps = conn.prepareStatement(query)){
                    ps.setInt(1, idp);
                    int val = ps.executeUpdate();
                    if (val==0) {System.out.println("puklo"); return -2;}
                } catch (SQLException ex) {
                    Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
                    return -2;
                }
            }
            // za svaki paket uvecaj njegovom korisniku brposlpak za 1
            query = "update korisnik\n" +
                    "set BrPoslPak = BrPoslPak+1\n" +
                    "where idk = (select zp.idk\n" +
                    "	from ZahtevPrevoz zp\n" +
                    "	where zp.idp=?\n" +
                    ")";
            for (int idp:idps){
                try(PreparedStatement ps = conn.prepareStatement(query)){
                    ps.setInt(1, idp);
                    int val = ps.executeUpdate();
                    if (val==0){
                        System.out.println("Puklo");
                        return -2;
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
                    return -2;
                }
            }
            
            // postavi status kurira na 1
            query = "update kurir set status=1 where idk=?";
            try(PreparedStatement ps = conn.prepareStatement(query)){
                ps.setInt(1, idk);
                int val = ps.executeUpdate();
                if (val==0) {System.out.println("puklo"); return -2;}
            } catch (SQLException ex) {
                Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
                return -2;
            }
            // dohvati prvi paketa koji treba da se vozi
            chosenIdp = idps.get(0);
            
        } else {
            // vozi
            // dohvati paket koji treba da se vozi
            // dohvati voznju ovog kurira kojoj je profit na null
            
            query = "select idvzn from voznja where idk=? and profit is null";
       
            try(PreparedStatement ps = conn.prepareStatement(query)){
                ps.setInt(1, idk);
                try(ResultSet rs = ps.executeQuery()){
                    if (rs.next()) idvzn = rs.getInt(1);
                    else return -2;
                }
            } catch (SQLException ex) {
                Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
                return -2;
            }
            
            //  odatle nadji sve pakete koji su u statusu 2, orderuj ih po idp
          
            query = "select pr.idp\n" +
                    "from prevoz pr join paket p on (pr.idp=p.idp)\n" +
                    "where pr.idvzn=? and p.statusisp=2\n" +
                    "order by p.vremeprihv";
            idps = new ArrayList<>();
            try(PreparedStatement ps = conn.prepareStatement(query)){
                ps.setInt(1, idvzn);
                try(ResultSet rs = ps.executeQuery()){
                    while(rs.next()) idps.add(rs.getInt(1));
                }
            } catch (SQLException ex) {
                Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            chosenIdp = idps.get(0);
            
        }
        
        // treba da se prvi paket

        // postavi status paketa na 3
        query = "update paket set statusisp=3 where idp=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, chosenIdp);
            int val = ps.executeUpdate();
            if (val==0){
                System.out.println("puklo");
                return -2;
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -2;
        }
        
        // uvcaj broj isporucenih paketa u kuriru
        query = "update kurir set brisppak = brisppak+1 where idk=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setInt(1, idk);
            int val = ps.executeUpdate();
            if (val==0){
                System.out.println("puklo");
                return -2;
            }
        } catch (SQLException ex) {
            Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // da li je ovo bio poslednji paket
            //  ako jeste sracunaj profit voznje i dodaj profit u kurira
            //  postavi status kurira na ne vozi

        if (idps.size() == 1){
            // ovo je bio poslednji paket
            
            // postavi kurira da ne vozi
            query = "update kurir set status=0 where idk=?";
            try(PreparedStatement ps = conn.prepareStatement(query)){
                ps.setInt(1, idk);
                int val = ps.executeUpdate();
                if (val==0) {
                    System.out.println("puklo");
                    return -2;
                }
            } catch (SQLException ex) {
                Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
                return -2;
            }
            
            // sracunaj prvo cenu svih 
            BigDecimal ukupnaCena = new BigDecimal(0);
            BigDecimal ukupnaDuzina = new BigDecimal(0);
            List<Pair<Integer, Integer>> coordPairs = new ArrayList<>();
            query = "select p.cena as cena, o1.x as x1, o1.y as y1, o2.x as x2, o2.y as y2\n" +
                    "from prevoz pr join paket p on (pr.idp=p.idp) join ZahtevPrevoz zp on (p.idp=zp.idp)\n" +
                    "	join opstina o1 on (zp.IdOPre=o1.ido) join opstina o2 on (zp.idodos=o2.ido)\n" +
                    "where pr.idvzn=?";
            try(PreparedStatement ps = conn.prepareStatement(query)){
                ps.setInt(1, idvzn);
                try(ResultSet rs = ps.executeQuery()){
                    while (rs.next()){
                        ukupnaCena = new BigDecimal(ukupnaCena.doubleValue() + rs.getBigDecimal("cena").doubleValue());
                        System.out.println(ukupnaCena.doubleValue());
                        Pair<Integer, Integer> pairA = new bs200051_PackageOperationsPair<>(rs.getInt("x1"), rs.getInt("y1"));
                        Pair<Integer, Integer> pairB = new bs200051_PackageOperationsPair<>(rs.getInt("x2"), rs.getInt("y2"));
                        coordPairs.add(pairA); coordPairs.add(pairB);
                    }
                    
                }
            
            }   catch (SQLException ex) {
                Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
                return -2;
            }
            
            System.out.println(coordPairs);
        
            for(int i=1;i<coordPairs.size();i++){
                int x1 = coordPairs.get(i-1).getFirstParam(); int y1 = coordPairs.get(i-1).getSecondParam();
                int x2 = coordPairs.get(i).getFirstParam(); int y2 = coordPairs.get(i).getSecondParam();
                int ysquared = (y2-y1)*(y2-y1);
                int xsquared = (x2-x1)*(x2-x1);
                double res = Math.sqrt(ysquared+xsquared);
                BigDecimal euclid = new BigDecimal(res);
             
                ukupnaDuzina = new BigDecimal(ukupnaDuzina.doubleValue()+euclid.doubleValue());
                System.out.println(euclid + " ===== " + ukupnaDuzina);
            }
        
            // iz vozila mi treba tipgoriva i potrosnja
            query = "select TipGoriva, Potrosnja from kurir k join vozilo v on (k.idv=v.idv)\n" +
                    "where k.idk=?";
            int tipGoriva = -1; 
            BigDecimal potrosnja = null;
            try(PreparedStatement ps = conn.prepareStatement(query)){
                ps.setInt(1, idk);
                try(ResultSet rs = ps.executeQuery()){
                    if (rs.next()){
                        tipGoriva = rs.getInt(1);
                        potrosnja = rs.getBigDecimal(2);
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
                return -2;
            }
            
            double jedinicnaCenaGoriva = -1;
            switch (tipGoriva) {
                case 0: jedinicnaCenaGoriva = 15; break;
                case 1: jedinicnaCenaGoriva = 32; break;
                case 2: jedinicnaCenaGoriva = 36; break;
                default:
                    System.out.println("Greska");
            }
            
            BigDecimal deficit = new BigDecimal(potrosnja.doubleValue() * ukupnaDuzina.doubleValue() * jedinicnaCenaGoriva);
            BigDecimal profit = new BigDecimal(ukupnaCena.doubleValue() - deficit.doubleValue());
            
            System.out.println(potrosnja);
            System.out.println(jedinicnaCenaGoriva);
            System.out.println(ukupnaDuzina);
            System.out.println(ukupnaCena);
            System.out.println(deficit);
            System.out.println(profit);
            
            query = "update voznja set profit=? where idvzn=?";
            try(PreparedStatement ps = conn.prepareStatement(query)){
                ps.setBigDecimal(1, profit);
                ps.setInt(2, idvzn);
                int val = ps.executeUpdate();
                if (val==0){
                    System.out.println("Puklo");
                    return -2;
                }
            } catch (SQLException ex) {
                Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
                return -2;
            }
            
            query = "update kurir set profit=profit+? where idk=?";
            try(PreparedStatement ps = conn.prepareStatement(query)){
                ps.setBigDecimal(1, profit);
                ps.setInt(2, idk);
                int val = ps.executeUpdate();
                if (val==0){
                    System.out.println("Puklo");
                    return -2;
                }
            } catch (SQLException ex) {
                Logger.getLogger(bs200051_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
                return -2;
            }
            
            
        }
    
        return chosenIdp;
    }
}
