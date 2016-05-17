/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javancss.FunctionMetric;
import javancss.Javancss;
import javancss.parser.JavaParser;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/**
 *
 * @author joao
 */
public class Teste {

    static String dir = "/repositories/";
    //String path = getServletContext().getRealPath(dir);
    static String path = "/home/joao/NetBeansProjects/DataMiningCodeRepositories/build/web/repositories/";

    public static List<File> gerRepositoriesFilesInFolder(String folderPath) {
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
                    } else {
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
            String fullName = function.name;
            String name = fullName.substring(fullName.lastIndexOf(".") + 1, fullName.indexOf("("));
            for (String line : lines) {
                if (line.contains(name) && line.contains("private") && line.contains("(")) {
                    privates++;
                    lines.remove(line);
                    break;
                }
            }
        }
        
        System.out.println("Priv: "+privates);
        
        return privates / functions.size();
    }

    //Acoplamento Direto de Classes
    public static void getDCC(File file) {
        //BCEL
    }

    //Tamanho da Interface da Classe
    public static double getCIS(File file) throws IOException {
        Javancss javancss = new Javancss(file);

        List<String> lines = Files.readAllLines(file.toPath());

        List<FunctionMetric> functions = javancss.getFunctions();

        double publics = 0;
        for (FunctionMetric function : functions) {
            String fullName = function.name;
            String name = fullName.substring(fullName.lastIndexOf(".") + 1, fullName.indexOf("("));
            for (String line : lines) {
                if (line.contains(name) && line.contains("public") && line.contains("(")) {
                    publics++;
                    lines.remove(line);
                    break;
                }
            }
        }
        
        return publics / functions.size();

    }

    public static void main(String[] args) throws IOException {
         System.out.println(getDAM(new File("/home/joao/NetBeansProjects/DataMiningCodeRepositories/repositories/abdera/core/src/main/java/org/apache/abdera/Abdera.java")));
         System.out.println(getCIS(new File("/home/joao/NetBeansProjects/DataMiningCodeRepositories/repositories/abdera/core/src/main/java/org/apache/abdera/Abdera.java")));
        System.out.println(getACC(new File("/home/joao/NetBeansProjects/DataMiningCodeRepositories/repositories/abdera/core/src/main/java/org/apache/abdera/Abdera.java")));
    }

}
