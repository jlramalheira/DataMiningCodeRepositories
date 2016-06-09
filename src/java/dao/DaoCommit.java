/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import model.Commit;
import model.Project;

/**
 *
 * @author joao
 */
public class DaoCommit extends Dao<Commit> {

    public DaoCommit() {
        super(Commit.class);
    }

    public List<Commit> listByProject(Project project) {
        criteria = newCriteria();
        return criteria
                .andEquals("project", project)
                .orderByAsc("date")
                .getResultList();
    }
    
    
}
