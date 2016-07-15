/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import dao.DaoCommit;
import dao.DaoFile;
import dao.DaoPreProcessingCommit;
import dao.DaoProject;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javancss.Javancss;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Project;
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

    RequestDispatcher rd;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        switch (action){
            case "results":
                List<Project> projects = new DaoProject().list();
                for (Project project : projects) {
                    System.out.println(project.getName());
                }
                DaoFile daoFile = new DaoFile();
                DaoPreProcessingCommit daoPreProcessingCommit = new DaoPreProcessingCommit();
                if (!projects.isEmpty()){
                    Project project = projects.get(0);
                    int totCommits = new DaoCommit().getTotCommit();
                    int totFiles = daoFile.getTotFiles();
                    int totPreProcessing = daoPreProcessingCommit.getTotPreCommits();
                    List<Double> aacs = daoPreProcessingCommit.listAACS();                  
                    List<Integer> qtsCommit = daoPreProcessingCommit.getNumberCommitGroupByAuthor();                  
                    List<String> authors = daoPreProcessingCommit.getAuthorsOrderByCommits();                  

                    request.setAttribute("project", project);
                    request.setAttribute("totCommits", totCommits);
                    request.setAttribute("totPreProcessing", totPreProcessing);
                    request.setAttribute("totFiles", totFiles);
                    request.setAttribute("aacs", aacs);
                    request.setAttribute("qtsCommit", qtsCommit);
                    request.setAttribute("authors", authors);
                    
                    rd = request.getRequestDispatcher("results.jsp");
                    rd.forward(request, response);
                } else {
                    response.sendRedirect("index.html");
                }
                
                break;
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
