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

    public List<model.File> listByCommit(Commit commit) {
        criteria = newCriteria();
        return criteria
                .andEquals("commit", commit)
                .getResultList();
    }

    public File getOldFile(File file, Commit commit) {
        criteria = newCriteria();
        List<model.File> files = criteria
                .andEquals("path", file.getPath())
                .andLessThan("id", file.getId())
                .orderByDesc("id")
                .getResultList();
        
        if (!files.isEmpty()){
            return files.get(0);
        }
        return null;
    }
}
