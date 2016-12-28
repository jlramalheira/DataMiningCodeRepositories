/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
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

    public int getTotCommit() {
        criteria = newCriteria();
        return criteria.count().intValue();
    }

    public void removeFiles(Commit commit) {
//        em.createNativeQuery("DELETE FROM FILE WHERE COMMIT_ID ="+commit.getId()).getResultList();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost";
            Connection connection = DriverManager.getConnection(url, "root", "root");
            Statement statement = connection.createStatement();
            statement.execute("use dm");
            statement.execute("DELETE FROM FILE WHERE COMMIT_ID ="+commit.getId());
            statement.close();
            connection.close();
            
        } catch (Exception ex) {
            System.out.println("erro");
            System.out.println(ex);
        }
    }
    
    
}
