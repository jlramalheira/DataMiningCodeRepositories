/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import model.PreProcessingCommit;

/**
 *
 * @author Joao
 */
public class DaoPreProcessingCommit extends Dao<PreProcessingCommit>{

    public DaoPreProcessingCommit() {
        super(PreProcessingCommit.class);
    }

    public List<String> listDistinctAuthors() {
        return em.createNativeQuery("SELECT DISTINCT author FROM PREPROCESSINGCOMMIT").getResultList();
    }

    public List<Integer> listDistinctNumberClass() {
        return em.createNativeQuery("SELECT DISTINCT numberClass FROM PREPROCESSINGCOMMIT ORDER BY numberClass ASC").getResultList();
    }
    
    public double getMinAcc() {
        return (double) em.createNativeQuery("SELECT MIN(deltaAAC) FROM PREPROCESSINGCOMMIT").getResultList().get(0);
    }

    public double getMaxAcc() {
        return (double) em.createNativeQuery("SELECT MAX(deltaAAC) FROM PREPROCESSINGCOMMIT").getResultList().get(0);
    }

    public int getMinNom() {
        return (int) em.createNativeQuery("SELECT MIN(deltaNOM) FROM PREPROCESSINGCOMMIT").getResultList().get(0);
    }

    public int getMaxNom() {
        return (int) em.createNativeQuery("SELECT MAX(deltaNOM) FROM PREPROCESSINGCOMMIT").getResultList().get(0);
    }

    public int getMinLoc() {
        return (int) em.createNativeQuery("SELECT MIN(deltaLOC) FROM PREPROCESSINGCOMMIT").getResultList().get(0);
    }

    public int getMaxLoc() {
        return (int) em.createNativeQuery("SELECT MAX(deltaLOC) FROM PREPROCESSINGCOMMIT").getResultList().get(0);
    }

    public double getMinDam() {
        return (double) em.createNativeQuery("SELECT MIN(deltaDAM) FROM PREPROCESSINGCOMMIT").getResultList().get(0);
    }

    public double getMaxDam() {
        return (double) em.createNativeQuery("SELECT MAX(deltaDAM) FROM PREPROCESSINGCOMMIT").getResultList().get(0);
    }

    public int getMinCis() {
        return (int) em.createNativeQuery("SELECT MIN(deltaCIS) FROM PREPROCESSINGCOMMIT").getResultList().get(0);
    }

    public int getMaxCis() {
        return (int) em.createNativeQuery("SELECT MAX(deltaCIS) FROM PREPROCESSINGCOMMIT").getResultList().get(0);
    }

    public double getMingetAvgLOCNOM() {
        return (double) em.createNativeQuery("SELECT MIN(deltaAvgLOCNOM) FROM PREPROCESSINGCOMMIT").getResultList().get(0);
    }

    public double getMaxgetAvgLOCNOM() {
        return (double) em.createNativeQuery("SELECT MAX(deltaAvgLOCNOM) FROM PREPROCESSINGCOMMIT").getResultList().get(0);
    }

    public List<Double> listDistinctDeltaAcc() {
        return em.createNativeQuery("SELECT DISTINCT deltaAAC FROM PREPROCESSINGCOMMIT ORDER BY deltaAAC ASC").getResultList();
    }

    public List<Integer> listDistinctDeltaNom() {
        return em.createNativeQuery("SELECT DISTINCT deltaNOM FROM PREPROCESSINGCOMMIT ORDER BY deltaNOM ASC").getResultList();
    }

    public List<Integer> listDistinctDeltaLoc() {
        return em.createNativeQuery("SELECT DISTINCT deltaLOC FROM PREPROCESSINGCOMMIT ORDER BY deltaLOC ASC").getResultList();
    }

    public List<Double> listDistinctDeltaDam() {
        return em.createNativeQuery("SELECT DISTINCT deltaDAM FROM PREPROCESSINGCOMMIT ORDER BY deltaDAM ASC").getResultList();
    }

    public List<Integer> listDistinctDeltaCis() {
        return em.createNativeQuery("SELECT DISTINCT deltaCIS FROM PREPROCESSINGCOMMIT ORDER BY deltaCIS ASC").getResultList();
    }

    public List<Double> listDistinctDeltaAvgLocNom() {
        return em.createNativeQuery("SELECT DISTINCT deltaAvgLOCNOM FROM PREPROCESSINGCOMMIT ORDER BY deltaAvgLOCNOM ASC").getResultList();
    }

    public int getTotPreCommits() {
        return newCriteria().count().intValue();
    }

    public List<Double> listAACS() {
        return em.createQuery("SELECT p.deltaAAC from PreProcessingCommit p").getResultList();
    }
    
    public List<Integer> getNumberCommitGroupByAuthor(){
        return em.createNativeQuery("select count(id) as qnt from PREPROCESSINGCOMMIT group by author order by qnt desc limit 10").getResultList();
    }
    
    public List<String> getAuthorsOrderByCommits(){
        return em.createNativeQuery("select author from PREPROCESSINGCOMMIT group by author order by count(id) desc limit 10").getResultList();
    }
    
}
