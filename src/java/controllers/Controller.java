/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import javancss.Javancss;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.DepthWalk.RevWalk;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

/**
 *
 * @author joao
 */
@WebServlet(name = "Controller", urlPatterns = {"/Controller"})
public class Controller extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {

            String dir = "/repositories/";

            String path = getServletContext().getRealPath(dir);

            File repositoriesDir = new File(path.substring(0, path.indexOf("build")).concat(dir));

            File files[] = repositoriesDir.listFiles();

            if (files != null) {
                for (File file : files) {

                    FileRepositoryBuilder builder = new FileRepositoryBuilder();
                    Repository repo = builder.setGitDir(new File(file.getAbsolutePath() + "/.git")).setMustExist(true).build();
                    Git git = new Git(repo);
                    Iterable<RevCommit> log = git.log().call();
                    int counter = 0;
                    for (Iterator<RevCommit> iterator = log.iterator(); iterator.hasNext();) {
                        RevCommit rev = iterator.next();
                        System.out.println(rev.getAuthorIdent());
                        counter++;
                    }
                    System.out.printf("Have %d commits\n\n", counter);
                }
            }
            
            File f = new File("/home/joao/NetBeansProjects/DataMining/SimpleSubjectResolver.java");
            System.out.println(f.getAbsolutePath());
            System.out.println(f.toString());
            Javancss javancss = new Javancss(f);
            System.out.println("Linhas: " + javancss.getLOC());
            System.out.println("Funcs: " + javancss.getFunctions().size());
            String out = javancss.printFunctionNcss();
            String s = out.substring(out.lastIndexOf("CCN:")+4);
            s = s.substring(0,s.indexOf('\n'));
            System.out.println(s);
            double d = Double.parseDouble(s);
            System.out.println(d);

        } catch (Exception e) {
            System.out.println("Deu PAU! " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
