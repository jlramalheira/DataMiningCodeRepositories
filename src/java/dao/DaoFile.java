/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import model.Commit;
import model.File;

/**
 *
 * @author joao
 */
public class DaoFile extends Dao<File> {

    private double acc;
    private int nom;
    private int loc;
    private double dam;
    private int cis;
    private double getAvgLOCNOM;

    public DaoFile() {
        super(File.class);
    }

    public double getMinAcc() {
        return (double) em.createNativeQuery("SELECT MIN(acc) FROM File").getResultList().get(0);
    }

    public double getMaxAcc() {
        return (double) em.createNativeQuery("SELECT MAX(acc) FROM File").getResultList().get(0);
    }

    public int getMinNom() {
        return (int) em.createNativeQuery("SELECT MIN(nom) FROM File").getResultList().get(0);
    }

    public int getMaxNom() {
        return (int) em.createNativeQuery("SELECT MAX(nom) FROM File").getResultList().get(0);
    }

    public int getMinLoc() {
        return (int) em.createNativeQuery("SELECT MIN(loc) FROM File").getResultList().get(0);
    }

    public int getMaxLoc() {
        return (int) em.createNativeQuery("SELECT MAX(loc) FROM File").getResultList().get(0);
    }

    public double getMinDam() {
        return (double) em.createNativeQuery("SELECT MIN(dam) FROM File").getResultList().get(0);
    }

    public double getMaxDam() {
        return (double) em.createNativeQuery("SELECT MAX(dam) FROM File").getResultList().get(0);
    }

    public int getMinCis() {
        return (int) em.createNativeQuery("SELECT MIN(cis) FROM File").getResultList().get(0);
    }

    public int getMaxCis() {
        return (int) em.createNativeQuery("SELECT MAX(cis) FROM File").getResultList().get(0);
    }

    public double getMingetAvgLOCNOM() {
        return (double) em.createNativeQuery("SELECT MIN(getAvgLOCNOM) FROM File").getResultList().get(0);
    }

    public double getMaxgetAvgLOCNOM() {
        return (double) em.createNativeQuery("SELECT MAX(getAvgLOCNOM) FROM File").getResultList().get(0);
    }

    public List<model.File> listByCommit(Commit commit) {
        criteria = newCriteria();
        return criteria
                .andEquals("commit", commit)
                .getResultList();
    }

    public File getOldFile(File file, Commit commit) {
        criteria = newCriteria();
        List<model.File> files = criteria
                .andEquals("commit", commit)
                .andEquals("path", file.getPath())
                .andLessThan("id", file.getId())
                .getResultList();
        
        if (!files.isEmpty()){
            return files.get(0);
        }
        return null;
    }
}
