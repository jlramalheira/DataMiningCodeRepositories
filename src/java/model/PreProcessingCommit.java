/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Joao
 */
@Entity
public class PreProcessingCommit implements Serializable {
    
    public final String REFACTOR = "refactor";
    public final String BUG = "bug";
    public final String OTHER = "other";

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String author;
    private String type;
    private int numberClass;
    private double deltaAAC;
    private int deltaNOM;
    private int deltaLOC;
    private double deltaDAM;
    private int deltaCIS;
    private double deltaAvgLOCNOM;

    public PreProcessingCommit() {
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getType() {
        return type;
    }

    public void setType(String message) {
        if (message.contains("")){
            this.type = this.REFACTOR;
        } else if (message.contains("")){
            this.type = this.BUG;
        } else {
            this.type = this.OTHER;
        }
    }

    public int getNumberClass() {
        return numberClass;
    }

    public void setNumberClass(int numberClass) {
        this.numberClass = numberClass;
    }

    public double getDeltaAAC() {
        return deltaAAC;
    }

    public void setDeltaAAC(double deltaAAC) {
        this.deltaAAC = deltaAAC;
    }

    public int getDeltaNOM() {
        return deltaNOM;
    }

    public void setDeltaNOM(int deltaNOM) {
        this.deltaNOM = deltaNOM;
    }

    public int getDeltaLOC() {
        return deltaLOC;
    }

    public void setDeltaLOC(int deltaLOC) {
        this.deltaLOC = deltaLOC;
    }

    public double getDeltaDAM() {
        return deltaDAM;
    }

    public void setDeltaDAM(double deltaDAM) {
        this.deltaDAM = deltaDAM;
    }

    public int getDeltaCIS() {
        return deltaCIS;
    }

    public void setDeltaCIS(int deltaCIS) {
        this.deltaCIS = deltaCIS;
    }

    public double getDeltaAvgLOCNOM() {
        return deltaAvgLOCNOM;
    }

    public void setDeltaAvgLOCNOM(double deltaAvgLOCNOM) {
        this.deltaAvgLOCNOM = deltaAvgLOCNOM;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PreProcessingCommit)) {
            return false;
        }
        PreProcessingCommit other = (PreProcessingCommit) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.PreProcessingCommit[ id=" + id + " ]";
    }
    
}
