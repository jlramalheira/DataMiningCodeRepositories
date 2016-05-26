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
    
}
