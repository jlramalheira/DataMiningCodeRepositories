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
import weka.core.SelectedTag;

/**
 *
 * @author joao
 */
public class Util {

    public static int fileErros;

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
            fileErros++;
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

        //TEST
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
        commit.setNumberFileErros(fileErros);
        fileErros = 0;

        List<File> files = getFilesInFolder(new File(project.getAbsolutePath()));

        commit.setNumberFileChanges(files.size());
        daoCommit.insert(commit);
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
                commit.setNumberFileErros(fileErros);
                fileErros = 0;
                files = listFilesChanges(git, rev, project);
                commit.setNumberFileChanges(files.size());

                daoCommit.insert(commit);
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
                        fileNames.add(new File(project.getAbsolutePath() + "/" + diff.getNewPath()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileNames;
    }

    public static void assiciationRules(String pathFile, String fileResultPath) {
        try {
            Apriori apriori = new Apriori();
            BufferedReader reader = new BufferedReader(new FileReader(pathFile));
            Instances instances = new Instances(reader);

            apriori.setNumRules(20);
            apriori.setLowerBoundMinSupport(0.01);
            apriori.setMinMetric(0.09);
            //apriori.setMetricType(new SelectedTag(0, Apriori.TAGS_SELECTION));
            //apriori.setUpperBoundMinSupport(1);
            //apriori.setDelta(1);
            //apriori.setSignificanceLevel(-1);

            apriori.buildAssociations(instances);

            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileResultPath, false)));

            writer.println(apriori);

            writer.flush();

            writer.close();

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

    public static void generateARFFBucket(String filePath) throws IOException {
        DaoPreProcessingCommit daoPreProcessingCommit = new DaoPreProcessingCommit();

        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filePath, false)));
        writer.println("@relation DM\n");

        List<String> authors = daoPreProcessingCommit.listDistinctAuthors();
        List<Integer> numberFilesChange = daoPreProcessingCommit.listDistinctNumberClass();
        List<PreProcessingCommit> preProcessingCommits = daoPreProcessingCommit.list();

        double deltaMinAcc = daoPreProcessingCommit.getMinAcc();
        double deltaMaxAcc = daoPreProcessingCommit.getMaxAcc();
        int deltaMinNom = daoPreProcessingCommit.getMinNom();
        int deltaMaxNom = daoPreProcessingCommit.getMaxNom();
        int deltaMinLoc = daoPreProcessingCommit.getMinLoc();
        int deltaMaxLoc = daoPreProcessingCommit.getMaxLoc();
        double deltaMinDam = daoPreProcessingCommit.getMinDam();
        double deltaMaxDam = daoPreProcessingCommit.getMaxDam();
        int deltaMinCis = daoPreProcessingCommit.getMinCis();
        int deltaMaxCis = daoPreProcessingCommit.getMaxCis();
        double deltaMingetAvgLOCNOM = daoPreProcessingCommit.getMingetAvgLOCNOM();
        double deltaMaxgetAvgLOCNOM = daoPreProcessingCommit.getMaxgetAvgLOCNOM();

        double[] disAcc = discretizeDouble(deltaMinAcc, deltaMaxAcc, Define.SIZEBUCKET);
        int[] disNom = discretizeInteger(deltaMinNom, deltaMaxNom, Define.SIZEBUCKET);
        int[] disLoc = discretizeInteger(deltaMinLoc, deltaMaxLoc, Define.SIZEBUCKET);
        double[] disDam = discretizeDouble(deltaMinDam, deltaMaxDam, Define.SIZEBUCKET);
        int[] disCis = discretizeInteger(deltaMinCis, deltaMaxCis, Define.SIZEBUCKET);
        double[] disgetAvgLOCNOM = discretizeDouble(deltaMingetAvgLOCNOM, deltaMaxgetAvgLOCNOM, Define.SIZEBUCKET);

        writer.println("@attribute deltaacc {" + Arrays.toString(disAcc).replace("[", "").replace("]", "") + "}");
        /*writer.println("@attribute deltanom {" + Arrays.toString(disNom).replace("[", "").replace("]", "") + "}");
        writer.println("@attribute deltaloc {" + Arrays.toString(disLoc).replace("[", "").replace("]", "") + "}");
         writer.println("@attribute deltadam {" + Arrays.toString(disDam).replace("[", "").replace("]", "") + "}");
        writer.println("@attribute deltacis {" + Arrays.toString(disCis).replace("[", "").replace("]", "") + "}");
        */writer.println("@attribute deltagetAvgLOCNOM {" + Arrays.toString(disgetAvgLOCNOM).replace("[", "").replace("]", "") + "}");
         
        writer.print("@attribute authors {");
        writer.print("\"" + authors.get(0) + "\"");
        for (int i = 1; i < authors.size(); i++) {
            writer.print(",\"" + authors.get(i) + "\"");
        }
        writer.println("}");
        writer.print("@attribute numberClass {");
        writer.print(numberFilesChange.get(0));
        for (int i = 1; i < numberFilesChange.size(); i++) {
            writer.print("," + numberFilesChange.get(i));
        }
        writer.println("}");
        writer.println("@attribute type {" + Define.BUG + ", " + Define.OTHER + ", " + Define.REFACTOR + "}");

        writer.println();
        writer.flush();
        writer.println("@data");
        writer.flush();
        for (PreProcessingCommit preProcessingCommit : preProcessingCommits) {
            if (preProcessingCommit.getNumberClass() == 0) {
                continue;
            }
            writer.println(getValueDiscretized(preProcessingCommit.getDeltaAAC(), disAcc) + ","
                    /*+ getValueDiscretized(preProcessingCommit.getDeltaNOM(), disNom) + ","
                    + getValueDiscretized(preProcessingCommit.getDeltaLOC(), disLoc) + ","
                    + getValueDiscretized(preProcessingCommit.getDeltaDAM(), disDam) + ","
                    + getValueDiscretized(preProcessingCommit.getDeltaCIS(), disCis) + ","
                    */+ getValueDiscretized(preProcessingCommit.getDeltaAvgLOCNOM(), disgetAvgLOCNOM) + ","
                     
                    + "\"" + preProcessingCommit.getAuthor() + "\","
                    + preProcessingCommit.getNumberClass() + ","
                    + preProcessingCommit.getType());
        }

        writer.flush();

        writer.close();
    }

    public static void generateARFF(String filePath) throws IOException {
        DaoPreProcessingCommit daoPreProcessingCommit = new DaoPreProcessingCommit();

        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filePath, false)));
        writer.println("@relation DM\n");

        List<String> authors = daoPreProcessingCommit.listDistinctAuthors();
        List<Integer> numberFilesChange = daoPreProcessingCommit.listDistinctNumberClass();
        List<PreProcessingCommit> preProcessingCommits = daoPreProcessingCommit.list();
        List<Double> deltaAccs = daoPreProcessingCommit.listDistinctDeltaAcc();
        List<Integer> deltaNoms = daoPreProcessingCommit.listDistinctDeltaNom();
        List<Integer> deltaLocs = daoPreProcessingCommit.listDistinctDeltaLoc();
        List<Double> deltaDams = daoPreProcessingCommit.listDistinctDeltaDam();
        List<Integer> deltaCiss = daoPreProcessingCommit.listDistinctDeltaCis();
        List<Double> deltaAgvLocNoms = daoPreProcessingCommit.listDistinctDeltaAvgLocNom();

        writer.print("@attribute deltaaac {");
        writer.print(deltaAccs.get(0));
        for (int i = 1; i < deltaAccs.size(); i++) {
            writer.print("," + deltaAccs.get(i));
        }
        writer.println("}");

        writer.print("@attribute deltanom {");
        writer.print(deltaNoms.get(0));
        for (int i = 1; i < deltaNoms.size(); i++) {
            writer.print("," + deltaNoms.get(i));
        }
        writer.println("}");
        writer.print("@attribute deltaloc {");
        writer.print(deltaLocs.get(0));
        for (int i = 1; i < deltaLocs.size(); i++) {
            writer.print("," + deltaLocs.get(i));
        }
        writer.println("}");

        writer.print("@attribute deltadam {");
        writer.print(deltaDams.get(0));
        for (int i = 1; i < deltaDams.size(); i++) {
            writer.print("," + deltaDams.get(i));
        }
        writer.println("}");
        writer.print("@attribute deltacis {");
        writer.print(deltaCiss.get(0));
        for (int i = 1; i < deltaCiss.size(); i++) {
            writer.print("," + deltaCiss.get(i));
        }
        writer.println("}");
        writer.print("@attribute deltaAvgLocNom {");
        writer.print(deltaAgvLocNoms.get(0));
        for (int i = 1; i < deltaAgvLocNoms.size(); i++) {
            writer.print("," + deltaAgvLocNoms.get(i));
        }
        writer.println("}");

        writer.print("@attribute authors {");
        writer.print("\"" + authors.get(0) + "\"");
        for (int i = 1; i < authors.size(); i++) {
            writer.print(",\"" + authors.get(i) + "\"");
        }
        writer.println("}");
        writer.print("@attribute numberClass {");
        writer.print(numberFilesChange.get(0));
        for (int i = 1; i < numberFilesChange.size(); i++) {
            writer.print("," + numberFilesChange.get(i));
        }
        writer.println("}");
        writer.println("@attribute type {" + Define.BUG + ", " + Define.OTHER + ", " + Define.REFACTOR + "}");

        writer.println();
        writer.flush();
        writer.println("@data");
        writer.flush();
        for (PreProcessingCommit preProcessingCommit : preProcessingCommits) {
            if (!preProcessingCommit.isValid()) {
                continue;
            }

            writer.println(preProcessingCommit.getDeltaAAC() + ","
                    + preProcessingCommit.getDeltaNOM() + ","
                    + preProcessingCommit.getDeltaLOC() + ","
                    + preProcessingCommit.getDeltaDAM() + ","
                    + preProcessingCommit.getDeltaCIS() + ","
                    + preProcessingCommit.getDeltaAvgLOCNOM() + ","
                    + "\"" + preProcessingCommit.getAuthor() + "\","
                    + preProcessingCommit.getNumberClass() + ","
                    + preProcessingCommit.getType());
            writer.flush();
        }

        writer.close();
    }

    public static void generateArffDisc(String filePath) throws IOException {
        DaoPreProcessingCommit daoPreProcessingCommit = new DaoPreProcessingCommit();

        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filePath, false)));
        writer.println("@relation DM\n");

        List<String> authors = daoPreProcessingCommit.listDistinctAuthors();
        List<Integer> numberFilesChange = daoPreProcessingCommit.listDistinctNumberClass();
        List<PreProcessingCommit> preProcessingCommits = daoPreProcessingCommit.list();

        writer.println("@attribute deltaaac {Up, Down, Estable}");
        //writer.println("@attribute deltanom {Up, Down, Estable}");
        /*
        writer.println("@attribute deltaloc {Up, Down, Estable}");
        writer.println("@attribute deltadam {Up, Down, Estable}");
        writer.println("@attribute deltacis {Up, Down, Estable}");
        */
        writer.println("@attribute deltaAvgLocNom {Up, Down, Estable}");
        
        writer.print("@attribute authors {");
        writer.print("\"" + authors.get(0) + "\"");
        for (int i = 1; i < authors.size(); i++) {
            writer.print(",\"" + authors.get(i) + "\"");
        }
        writer.println("}");
        writer.print("@attribute numberClass {");
        writer.print(numberFilesChange.get(0));
        for (int i = 1; i < numberFilesChange.size(); i++) {
            writer.print("," + numberFilesChange.get(i));
        }
        writer.println("}");
        writer.println("@attribute type {" + Define.BUG + ", " + Define.OTHER + ", " + Define.REFACTOR + "}");

        writer.println();
        writer.flush();
        writer.println("@data");
        writer.flush();
        for (PreProcessingCommit preProcessingCommit : preProcessingCommits) {
            if (!preProcessingCommit.isValid()) {
                continue;
            }
            writer.println(preProcessingCommit.printDeltaAAC() + ","
                    //+ preProcessingCommit.printDeltaNOM() + ","
                    /*+ preProcessingCommit.printDeltaLOC() + ","
                    + preProcessingCommit.printDeltaDAM() + ","
                    + preProcessingCommit.printDeltaCIS() + ","
                    */+ preProcessingCommit.printDeltaAvgLOCNOM() + ","
                    + "\"" + preProcessingCommit.getAuthor() + "\","
                    + preProcessingCommit.getNumberClass() + ","
                    + preProcessingCommit.getType());
            writer.flush();
        }

        writer.close();
    }

    public static void associate(String pathArffFile) {
        //assiciationRules(pathArffFile);
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
            if (commit.getNumberFileChanges() > 0) {
                preProcessingCommit = preProcessingCommit(commit);

                daoPreProcessingCommit.insert(preProcessingCommit);
            }
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
        Project p = new Project("Apache TomCat", util.Util.path + "tomcat", "git://git.apache.org/tomcat.git");
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
 /*
        Project p = new DaoProject().get(new Long(1));
        System.out.println("preprocessing");
        //preProcessing(p);
        String filePath = path + p.getName() + "4.arff";
        System.out.println("Gerando arff..");
        generateARFF(filePath);
        System.out.println(filePath);
        assiciationRules(filePath);
         */
        //initDb.recreateDataBase("dm", "root", "root");

        List<Project> projects = new ArrayList<>();

        //projects.add(new Project("Apache Abdera", util.Util.path + "abdera", "git://git.apache.org/abdera.git"));
        projects.add(new Project("Apache Harmony", util.Util.path + "harmony", "git://git.apache.org/harmony.git"));
        //projects.add(new Project("Apache OFBiz", util.Util.path + "ofbiz", "git://git.apache.org/ofbiz.git"));
        //projects.add(new Project("Apache Ant", util.Util.path + "ant", "git://git.apache.org/ant.git"));
        //projects.add(new Project("Apache Maven", util.Util.path + "maven", "git://git.apache.org/maven.git"));
        //projects.add(new Project("Apache Tomcat", util.Util.path + "tomcat", "git://git.apache.org/tomcat.git")); //
        //projects.add(new Project("Apache Hadoop", util.Util.path + "hadoop", "git://git.apache.org/hadoop.git"));
        //projects.add(new Project("Apache CXF", util.Util.path + "cxf", "git://git.apache.org/cxf.git"));
        //projects.add(new Project("Apache Derby", util.Util.path + "derby", "git://git.apache.org/derby.git"));
        //projects.add(new Project("Apache Felix", util.Util.path + "felix", "git://git.apache.org/felix.git"));
        
        /*
        for (Project project : projects) {
            DaoProject daoProject = new DaoProject();
            daoProject.insert(project);
            System.out.println("\n\nClonnig..." + project.getName());
            cloneProject(project.getUri(), project.getAbsolutePath());
            try {
                Git git = buildGit(new File(project.getAbsolutePath()));
                System.out.println("Extract info");
                extractInformation(git, project);

            } catch (Exception e) {
                System.out.println(e);
            }

            System.out.println("preprocessing");
            preProcessing(project);

            String filePath = path + project.getName() + " Bucket.arff";
            String filePathResult = path + project.getName() + " - Result Bucket.txt";
            System.out.println("Gerando arff..");
            generateARFFBucket(filePath);
            System.out.println(filePath);
            assiciationRules(filePath, filePathResult);

//            initDb.recreateDataBase("dm", "root", "root");
        }
        */
        
        /*
        Project project = new DaoProject().get(new Long(1));
        preProcessing(project);
        */
        String filePath = path + "Apache CXF.arff";
        String filePathResult = path + "Apache CXF - Result.txt";
        System.out.println("Gerando arff..");
        //generateArffDisc(filePath);
        System.out.println(filePath);
        assiciationRules(filePath, filePathResult);
        /*
        
        Project p = new DaoProject().get(new Long(1+""));
        List<Commit> commits = new DaoCommit().listByProject(p);
        DaoFile daoFile = new DaoFile();
        DaoCommit daoCommit = new DaoCommit();
        for (Commit commit : commits) {
            daoCommit.removeFiles(commit);            
            daoCommit.remove(commit.getId());
        }
        new DaoProject().remove(new Long(1+""));
*/
    }

}
