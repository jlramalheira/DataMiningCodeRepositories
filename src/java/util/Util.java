/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import dao.DaoCommit;
import dao.DaoFile;
import dao.DaoPreProcessingCommit;
import dao.DaoProject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javancss.FunctionMetric;
import javancss.Javancss;
import model.Commit;
import model.PreProcessingCommit;
import model.Project;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.eclipse.persistence.exceptions.DatabaseException;
import weka.associations.Apriori;
import weka.core.Instances;

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

    //Métricas de Acesso a Dados
    public static double getDAM(File file) {
        Javancss javancss = new Javancss(file);
        try {
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
        } catch (Exception e) {
            System.out.println("Problem read file: " + file.getName());
        }
        return -1;
    }

    //Tamanho da Interface da Classe
    public static int getCIS(File file) throws IOException {
        Javancss javancss = new Javancss(file);
        try {

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
        } catch (Exception e) {
            System.out.println("Problem read file: " + file.getName());
        }
        return -1;
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

    public static void extractInformation(Git git, Project project) throws IOException, DatabaseException {

        DaoCommit daoCommit = new DaoCommit();

        List<RevCommit> revs = empilharRevs(git, project);
        RevCommit rev = revs.remove(0);

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
        while (!revs.isEmpty()) {
            rev = revs.remove(0);
            try {
                git.checkout().setName(rev.getName()).call();
                System.out.println("Checkout... " + con++);

                commit = new Commit();
                commit.setAuthor(rev.getAuthorIdent().getName());
                commit.setDate(rev.getAuthorIdent().getWhen());
                commit.setMessage(rev.getShortMessage());
                commit.setProject(project);
                commit.setSha(rev.getName());

                daoCommit.insert(commit);

                files = listFilesChanges(git, rev, project);
                for (File file : files) {
                    extractAllMetrics(file, commit);
                }
            } catch (Exception e) {
                e.printStackTrace();
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

    public static List<RevCommit> empilharRevs(Git git, Project project) {
        List<RevCommit> revs = new ArrayList<>();
        try {
            Iterable<RevCommit> log = git.log().call();
            Iterator<RevCommit> iterator = log.iterator();
            RevCommit rev = iterator.next();
            revs.add(0, rev);
            rev = iterator.next();
            revs.add(0, rev);
            for (; iterator.hasNext();) {
                rev = iterator.next();
                revs.add(0, rev);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return revs;
    }

    public static List<File> listFilesChanges(Git git, RevCommit rev, Project project) {
        List<File> fileNames = new ArrayList<>();
        try {
            if (rev.getParentCount() > 0) {
                DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
                RevCommit parent = rev.getParent(0);
                Repository repository = git.getRepository();
                df.setRepository(repository);
                df.setDiffComparator(RawTextComparator.DEFAULT);
                df.setDetectRenames(true);
                List<DiffEntry> diffs = df.scan(parent.getTree(), rev.getTree());
                for (DiffEntry diff : diffs) {
                    if (!diff.getChangeType().name().equalsIgnoreCase("DELETE") && diff.getNewPath().endsWith(".java")) {
                        fileNames.add(new File(project.getAbsolutePath() + diff.getNewPath()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileNames;
    }

    public static void assiciationRules(String pathFile) {
        try {
            Apriori apriori = new Apriori();
            BufferedReader reader = new BufferedReader(new FileReader(pathFile));
            Instances instances = new Instances(reader);
            instances.setClassIndex(instances.numAttributes() - 1);

            apriori.buildAssociations(instances);

            System.out.println(apriori.getAssociationRules().getNumRules());

            System.out.println(apriori);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static int[] discretizeInteger(int min, int max, int size) {
        int[] result = new int[size];

        int dif = max - min;
        int step = dif / size;

        for (int i = 0; i < size; i++) {
            result[i] = min + step * i;
        }

        result[size - 1] = max;

        return result;
    }

    public static double[] discretizeDouble(double min, double max, int size) {
        double[] result = new double[size];

        double dif = max - min;
        double step = dif / size;

        for (int i = 0; i < size; i++) {
            result[i] = min + step * i;
        }

        result[size - 1] = max;

        return result;
    }

    public static double getValueDiscretized(double input, double[] possibilites) {
        for (int i = 0; i < possibilites.length - 1; i++) {
            if (possibilites[i] < input && possibilites[i + 1] > input) {
                return possibilites[i];
            }
        }
        return possibilites[possibilites.length - 1];
    }

    public static int getValueDiscretized(int input, int[] possibilites) {
        for (int i = 0; i < possibilites.length - 1; i++) {
            if (possibilites[i] < input && possibilites[i + 1] > input) {
                return possibilites[i];
            }
        }
        return possibilites[possibilites.length - 1];
    }

    public static void generateARFF() throws IOException {
        DaoFile daoFile = new DaoFile();
        List<model.File> files = daoFile.list();
        List<Commit> commits = new DaoCommit().list();

        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\Joao\\Documents\\NetBeansProjects\\DataMiningCodeRepositories\\harmony.arff", false)));
        writer.println("@relation Teste\n");

        double minAcc = daoFile.getMinAcc();
        double maxAcc = daoFile.getMaxAcc();
        int minNom = daoFile.getMinNom();
        int maxNom = daoFile.getMaxNom();
        int minLoc = daoFile.getMinLoc();
        int maxLoc = daoFile.getMaxLoc();
        double minDam = daoFile.getMinDam();
        double maxDam = daoFile.getMaxDam();
        int minCis = daoFile.getMinCis();
        int maxCis = daoFile.getMaxCis();
        double mingetAvgLOCNOM = daoFile.getMingetAvgLOCNOM();
        double maxgetAvgLOCNOM = daoFile.getMaxgetAvgLOCNOM();

        double[] disAcc = discretizeDouble(minAcc, maxAcc, 10);
        int[] disNom = discretizeInteger(minNom, maxNom, 10);
        int[] disLoc = discretizeInteger(minLoc, maxLoc, 10);
        double[] disDam = discretizeDouble(minDam, maxDam, 10);
        int[] disCis = discretizeInteger(minCis, maxCis, 10);
        double[] disgetAvgLOCNOM = discretizeDouble(mingetAvgLOCNOM, maxgetAvgLOCNOM, 10);

        writer.println("@attribute acc {" + Arrays.toString(disAcc).replace("[", "").replace("]", "") + "}");
        writer.println("@attribute nom {" + Arrays.toString(disNom).replace("[", "").replace("]", "") + "}");
        writer.println("@attribute loc {" + Arrays.toString(disLoc).replace("[", "").replace("]", "") + "}");
        writer.println("@attribute dam {" + Arrays.toString(disDam).replace("[", "").replace("]", "") + "}");
        writer.println("@attribute cis {" + Arrays.toString(disCis).replace("[", "").replace("]", "") + "}");
        writer.println("@attribute getAvgLOCNOM {" + Arrays.toString(disgetAvgLOCNOM).replace("[", "").replace("]", "") + "}");
        writer.print("@attribute commit {");
        writer.print(commits.get(0).getId());
        for (int i = 1; i < commits.size(); i++) {
            writer.print("," + commits.get(i).getId());
        }
        writer.println("}\n");

        writer.flush();
        writer.println("@data");
        writer.flush();
        for (model.File file : files) {
            if (!file.hasNegativeValue()) {
                continue;
            }
            writer.println(getValueDiscretized(file.getAcc(), disAcc) + ","
                    + getValueDiscretized(file.getNom(), disNom) + ","
                    + getValueDiscretized(file.getLoc(), disLoc) + ","
                    + getValueDiscretized(file.getDam(), disDam) + ","
                    + getValueDiscretized(file.getCis(), disCis) + ","
                    + getValueDiscretized(file.getGetAvgLOCNOM(), disgetAvgLOCNOM) + ","
                    + file.getCommit().getId());
        }

        writer.flush();

        writer.close();
    }

    public static void associate() {
        assiciationRules("C:\\Users\\Joao\\Documents\\NetBeansProjects\\DataMiningCodeRepositories\\harmony.arff");
    }

    public static double[] getDeltas(Commit commit) {
        double[] deltas = new double[6];
        for (int i = 0; i < deltas.length; i++) {
            deltas[i] = 0;
        }

        List<model.File> files = new DaoFile().listByCommit(commit);
        for (model.File file : files) {
            model.File oldFile = new DaoFile().getOldFile(file, commit);
            if (oldFile != null) {
                System.out.println("???????");
                deltas[0] += file.getAcc() - oldFile.getAcc();
                deltas[1] += file.getNom() - oldFile.getNom();
                deltas[2] += file.getLoc() - oldFile.getLoc();
                deltas[3] += file.getDam() - oldFile.getDam();
                deltas[4] += file.getCis() - oldFile.getCis();
                deltas[5] += file.getGetAvgLOCNOM() - oldFile.getGetAvgLOCNOM();
            } else {
                deltas[0] += file.getAcc();
                deltas[1] += file.getNom();
                deltas[2] += file.getLoc();
                deltas[3] += file.getDam();
                deltas[4] += file.getCis();
                deltas[5] += file.getGetAvgLOCNOM();
            }
        }
        return deltas;
    }

    public static PreProcessingCommit preProcessingCommit(Commit commit) {
        PreProcessingCommit preProcessingCommit = new PreProcessingCommit();
        List<model.File> files = new DaoFile().listByCommit(commit);

        preProcessingCommit.setAuthor(commit.getAuthor());
        preProcessingCommit.setNumberClass(files.size());
        preProcessingCommit.setType(commit.getMessage());

        double[] deltas = getDeltas(commit);
        preProcessingCommit.setDeltaAAC(deltas[0]);
        preProcessingCommit.setDeltaNOM((int) deltas[1]);
        preProcessingCommit.setDeltaLOC((int) deltas[2]);
        preProcessingCommit.setDeltaDAM(deltas[3]);
        preProcessingCommit.setDeltaCIS((int) deltas[4]);
        preProcessingCommit.setDeltaAvgLOCNOM(deltas[5]);

        return preProcessingCommit;
    }

    public static void preProcessing(Project project) {
        List<Commit> commits = new DaoCommit().listByProject(project);

        PreProcessingCommit preProcessingCommit;
        DaoPreProcessingCommit daoPreProcessingCommit = new DaoPreProcessingCommit();

        preProcessingCommit = preProcessingCommit(commits.get(0));
        daoPreProcessingCommit.insert(preProcessingCommit);
         
        for (int i = 1; i < commits.size(); i++) {
            Commit commit = commits.get(i);

            preProcessingCommit = preProcessingCommit(commit);

            daoPreProcessingCommit.insert(preProcessingCommit);
        }
        
    }

    public static void main(String[] args) throws IOException, GitAPIException {
        /*
        System.out.println("Initializing...");
        //initDb.initProjects();

        DaoProject daoProject = new DaoProject();
        List<Project> projects = daoProject.list();

        System.out.println("\n\nClonnig...");
        
        clearFolder(new File(path));
        for (Project project : projects) {
            System.out.println("Clone :" + project.getName());
            cloneProject(project.getUri(), project.getAbsolutePath());
        }
        //
         */
 /*
        DaoProject daoProject = new DaoProject();
        Project p = new Project("Apache Harmony", util.Util.path + "harmony", "git://git.apache.org/harmony.git");
        daoProject.insert(p);
        cloneProject(p.getUri(), p.getAbsolutePath());

        try {
            Git git = buildGit(new File(p.getAbsolutePath()));
            System.out.println("Extract info");
            extractInformation(git, p);

        } catch (Exception e) {
            System.out.println(e);
        }
         */

        Project p = new DaoProject().get(new Long(1));
        preProcessing(p);
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
