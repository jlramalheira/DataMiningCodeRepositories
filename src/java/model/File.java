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
import javax.persistence.ManyToOne;

/**
 *
 * @author joao
 */
@Entity
public class File implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double acc;
    private double cam;
    private int nom;
    private int loc;
    private double dam;
    private int dcc;
    private int cis;
    private double getAvgLOCNOM;
    private String path;
    @ManyToOne
    private Commit commit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getAcc() {
        return acc;
    }

    public void setAcc(double acc) {
        this.acc = acc;
    }

    public double getCam() {
        return cam;
    }

    public void setCam(double cam) {
        this.cam = cam;
    }

    public int getNom() {
        return nom;
    }

    public void setNom(int nom) {
        this.nom = nom;
    }

    public int getLoc() {
        return loc;
    }

    public void setLoc(int loc) {
        this.loc = loc;
    }

    public double getDam() {
        return dam;
    }

    public void setDam(double dam) {
        this.dam = dam;
    }

    public int getDcc() {
        return dcc;
    }

    public void setDcc(int dcc) {
        this.dcc = dcc;
    }

    public int getCis() {
        return cis;
    }

    public void setCis(int cis) {
        this.cis = cis;
    }

    public double getGetAvgLOCNOM() {
        return getAvgLOCNOM;
    }

    public void setGetAvgLOCNOM(double getAvgLOCNOM) {
        this.getAvgLOCNOM = getAvgLOCNOM;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
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
        if (!(object instanceof File)) {
            return false;
        }
        File other = (File) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.File[ id=" + id + " ]";
    }
    
}
