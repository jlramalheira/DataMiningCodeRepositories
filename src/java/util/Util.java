/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.sun.org.apache.bcel.internal.generic.MethodGen;
import dao.DaoCommit;
import dao.DaoFile;
import dao.DaoProject;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javancss.FunctionMetric;
import javancss.Javancss;
import javancss.parser.JavaParser;
import model.Commit;
import model.Project;
import org.eclipse.jgit.api.CheckoutResult;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.persistence.exceptions.DatabaseException;

/**
 *
 * @author joao
 */
public class Util {

    static String dir = "/repositories/";
    //String path = getServletContext().getRealPath(dir);
    //static String path = "/home/joao/NetBeansProjects/DataMiningCodeRepositories/build/web/repositories/";
    static String path = "/home/joao/NetBeansProjects/DataMiningCodeRepositories/repositories/";

    public static Git cloneProject(String uri, String folderPath) throws GitAPIException {
        return Git.cloneRepository().setURI(uri).setDirectory(new File(folderPath)).call();

    }

    public static List<File> getRepositoriesFilesInFolder(String folderPath) {
        File repositoriesDir = new File(folderPath.substring(0, folderPath.indexOf("build")).concat(dir));

        return Arrays.asList(repositoriesDir.listFiles());
    }

    public static Git buildGit(File repositorieFile) throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.setGitDir(new File(repositorieFile.getAbsolutePath() + "/.git")).setMustExist(true).build();

        return new Git(repository);
    }

    public static List<String> getLogAuthores(Git git) throws GitAPIException {
        List<String> logs = new ArrayList<>();
        Iterable<RevCommit> log = git.log().call();

        for (Iterator<RevCommit> iterator = log.iterator(); iterator.hasNext();) {
            RevCommit rev = iterator.next();
            logs.add(rev.getAuthorIdent().getName());
        }

        System.out.println(logs.size());
        return logs;
    }

    public static List<File> getFilesInFolder(File file) {
        List<File> files = new ArrayList<File>();
        File dirf = new File(file.getAbsolutePath());
        if (dirf.isDirectory()) {
            List<String> subDirectores = new ArrayList<>();
            subDirectores.add(file.getAbsolutePath());
            while (!subDirectores.isEmpty()) {
                dirf = new File(subDirectores.remove(0));
                File[] sub = dirf.listFiles();
                for (File f : sub) {
                    if (f.isDirectory()) {
                        subDirectores.add(f.getAbsolutePath() + "/");
                    } else if (f.getName().endsWith(".java")) {
                        files.add(f);
                    }
                }
            }
        }

        return files;

    }

    public static int getLOC(File file) {
        Javancss javancss = new Javancss(file);
        return javancss.getLOC();
    }

    public static int getNOM(File file) {
        Javancss javancss = new Javancss(file);
        return javancss.getFunctions().size();
    }

    public static double getACC(File file) {
        Javancss javancss = new Javancss(file);
        String out = javancss.printFunctionNcss();
        String ccn = out.substring(out.lastIndexOf("CCN:") + 4);
        ccn = ccn.substring(0, ccn.indexOf('\n'));
        return Double.parseDouble(ccn);
    }

    //Coesão entre Métodos da Classe
    public static void getCAM(File file) {
        //BCEL

    }

    //Métricas de Acesso a Dados
    public static double getDAM(File file) throws IOException {
        Javancss javancss = new Javancss(file);

        List<String> lines = Files.readAllLines(file.toPath());

        List<FunctionMetric> functions = javancss.getFunctions();

        double privates = 0;
        for (FunctionMetric function : functions) {
            try {
                String fullName = function.name;
                String name = fullName.substring(fullName.lastIndexOf(".") + 1, fullName.indexOf("("));
                for (String line : lines) {
                    if (line.contains(name) && line.contains("private") && line.contains("(")) {
                        privates++;
                        lines.remove(line);
                        break;
                    }
                }
            } catch (StringIndexOutOfBoundsException e) {

            }
        }
        if (functions.size() > 0) {
            return privates / functions.size();
        }
        return -1;
    }

    //Acoplamento Direto de Classes
    public static void getDCC(File file) {
        //BCEL
        //MethodGen gen = new MethodGen
    }

    //Tamanho da Interface da Classe
    public static int getCIS(File file) throws IOException {
        Javancss javancss = new Javancss(file);

        List<String> lines = Files.readAllLines(file.toPath());

        List<FunctionMetric> functions = javancss.getFunctions();

        int publics = 0;
        for (FunctionMetric function : functions) {
            try {
                String fullName = function.name;
                String name = fullName.substring(fullName.lastIndexOf(".") + 1, fullName.indexOf("("));
                for (String line : lines) {
                    if (line.contains(name) && line.contains("public") && line.contains("(")) {
                        publics++;
                        lines.remove(line);
                        break;
                    }
                }
            } catch (StringIndexOutOfBoundsException e) {

            }
        }

        return publics;
    }

    public static double getAvgLOCNOM(File file) {
        if (getNOM(file) > 0) {
            return ((double) getLOC(file)) / getNOM(file);
        }
        return -1;
    }

    public static void extractAllMetrics(File file, Commit commit) throws IOException, DatabaseException {

        DaoFile daoFile = new DaoFile();

        model.File f = new model.File();

        f.setAcc(getACC(file));
        //f.setCam();
        f.setCis(getCIS(file));
        f.setCommit(commit);
        f.setDam(getDAM(file));
        //f.setDcc();
        f.setGetAvgLOCNOM(getAvgLOCNOM(file));
        f.setLoc(getLOC(file));
        f.setNom(getNOM(file));
        f.setPath(file.getAbsolutePath());

        daoFile.insert(f);
    }

    public static void extractInformation(Git git, Project project) throws GitAPIException, IOException, DatabaseException {

        DaoCommit daoCommit = new DaoCommit();

        Iterable<RevCommit> log = git.log().call();
        Iterator<RevCommit> iterator = log.iterator();
        RevCommit rev = iterator.next();
        rev = iterator.next();

        Commit commit = new Commit();
        commit.setAuthor(rev.getAuthorIdent().getName());
        commit.setDate(rev.getAuthorIdent().getWhen());
        commit.setMessage(rev.getShortMessage());
        commit.setProject(project);
        commit.setSha(rev.getName());

        daoCommit.insert(commit);

        List<File> files = getFilesInFolder(new File(project.getAbsolutePath()));
        for (File file : files) {
            extractAllMetrics(file, commit);
        }
        int con = 0;
        for (; iterator.hasNext();) {
            rev = iterator.next();
            git.checkout().setName(rev.getName()).call();
            System.out.println("Checkout... "+ con++);

            commit = new Commit();
            commit.setAuthor(rev.getAuthorIdent().getName());
            commit.setDate(rev.getAuthorIdent().getWhen());
            commit.setMessage(rev.getShortMessage());
            commit.setProject(project);
            commit.setSha(rev.getName());

            daoCommit.insert(commit);
            
            files = getFilesInFolder(new File(project.getAbsolutePath()));
            for (File file : files) {
                extractAllMetrics(file, commit);
            }

        }
    }

    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    public static void clearFolder(File folder) {
        deleteFolder(folder);
        folder.mkdir();
    }

    public static void main(String[] args) throws IOException, GitAPIException {

        System.out.println("Initializing...");
        //initDb.initProjects();

        DaoProject daoProject = new DaoProject();
        List<Project> projects = daoProject.list();

        System.out.println("\n\nClonnig...");
        /*
        clearFolder(new File(path));
        for (Project project : projects) {
            System.out.println("Clone :" + project.getName());
            cloneProject(project.getUri(), project.getAbsolutePath());
        }
        */

        Project p = new Project("Apache Commons BCEL", util.Util.path+"commons-bcel", "git://git.apache.org/commons-bcel.git");
        daoProject.insert(p);        
        cloneProject(p.getUri(), p.getAbsolutePath());

        try {
            Git git = buildGit(new File(p.getAbsolutePath()));
            System.out.println("Extract info");
            extractInformation(git, p);
        } catch (Exception e) {
            System.out.println(e);
        }

        /*
        Git git = buildGit(new File(path+"/abdera"));
        Iterable<RevCommit> log = git.log().call();

        System.out.println(git.branchList().call());


        getLogAuthores(git);
        Iterator<RevCommit> iterator = log.iterator();
        RevCommit rev = iterator.next();
        rev = iterator.next();
        int c = 0;
        /*
        for (Iterator<RevCommit> iterator = log.iterator(); iterator.hasNext();) {
            rev = iterator.next();

            PersonIdent per = rev.getAuthorIdent();
            Date date = per.getWhen();

            c++;
            //System.out.println(date);
        } 
        System.out.println(rev);
        PersonIdent per = rev.getAuthorIdent();
        Date date = per.getWhen();
        System.out.println(date);
        //git.checkout().setName(rev.getName()).call();

        getLogAuthores(git);
         */
    }

}
