/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import dao.DaoProject;
import java.util.ArrayList;
import java.util.List;
import model.Project;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 *
 * @author joao
 */
public class initDb {
    
    public static void initProjects(){
        List<Project> projects = new ArrayList<>();
        
        //projects.add(new Project("Apache Abdera", util.Util.path+"abdera", "git://git.apache.org/abdera.git"));
        //projects.add(new Project("Apache Accumulo", util.Util.path+"accumulo", "git://git.apache.org/accumulo.git"));
        //projects.add(new Project("Apache Ace", util.Util.path+"ace", "git://git.apache.org/ace.git"));
        //projects.add(new Project("Apache Ace", util.Util.path+"activemq-activeio", "git://git.apache.org/activemq-activeio.git"));
        //projects.add(new Project("Apache Airavata Sandbox", util.Util.path+"airavata-sandbox", "git://git.apache.org/airavata-sandbox.git"));
        //projects.add(new Project("Apache Airavata", util.Util.path+"airavata", "git://git.apache.org/airavata.git"));
        
        DaoProject daoProject = new DaoProject();
        
        for (Project project : projects) {
            daoProject.insert(project);
        }
    }
    
    public static boolean recreateDataBase(String dataBase, String user, String password) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost";
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement statement = connection.createStatement();
            statement.execute("DROP DATABASE IF EXISTS " + dataBase);
            statement.execute("CREATE DATABASE " + dataBase);
            statement.close();
            connection.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
}
