package absoft.helpers;

import absoft.models.JSONModel.Added;
import absoft.models.JSONModel.Deleted;
import absoft.models.JSONModel.JSONModel;
import absoft.models.JSONModel.Updated;
import absoft.models.models.Branch;
import absoft.models.models.Revision;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNException;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.util.*;

@Service
public class ParseStream {

    private final String REGEXPPARAMETERS = "^.*?\\((.*)\\)";
    private final String REGEXPTESTNAME = "^.*public void\\s(.*)\\(.*";

    String[] stream;
    JSONModel jsonModel;
    Revision revision;

    String testAnnotation, streamRow, testName, textPackage;
    List<String> oldGroups, newGroups, notChangedGroups;
    ArrayList<HashMap<String, String>> listParametersAdded, listParametersDeleted;
    HashMap<String, String> mapTestParam, mapAnnotation;
    boolean searchTestName, changeTest, properties, accountType, data;
    int fileString, startRevisionString, endRevisionString, rowIndex;


    public JSONModel saveTestMethodNameBD(ByteArrayOutputStream outputStream, Branch branch, Date date, Long endRevision, String comment, String author) throws ParseException {
        jsonModel = new JSONModel(endRevision.toString(),author,comment,date.toString());
        oldGroups = newGroups = notChangedGroups = new ArrayList<String>();
        mapTestParam = mapAnnotation = new HashMap<String, String>();
        listParametersAdded = listParametersDeleted = new ArrayList<HashMap<String, String>>();
        changeTest = properties = searchTestName = false;

        stream = outputStream.toString().replace("\r", "").split("\n");
        revision = new Revision(endRevision.intValue(), comment, date, author, branch);
        System.out.println("Revision: " + revision);
        //цикл прохода по строкам
        for (rowIndex = 0; rowIndex < stream.length - 1; rowIndex++) {
            //удаление пустых строк
            while (rowIndex < stream.length - 1 && stream[rowIndex].replace(" ", "").isEmpty()) {
                rowIndex++;}
            if(stream[rowIndex].contains("Index:") && !stream[rowIndex].contains(".java")){
                properties = true;}
            while (rowIndex < stream.length - 1 && properties) {
                if(stream[rowIndex].contains("Index:") && stream[rowIndex].contains(".java")){
                    properties = false;}
                rowIndex++;}


            streamRow = stream[rowIndex].contains("public void") ? stream[rowIndex] : stream[rowIndex].replace(" ", "");

            // '+' в строке
            if (streamRow.matches("^\\+[^+].*")){
                stringContainsPlus();}
            // '-' в строке
            else if (streamRow.matches("^\\-[^-].*")){
                stringContainsMinus();}
            // "public void" в строке
            else if (streamRow.contains("public void") && searchTestName) {
                stringContainsPublicVoid(streamRow);}
            // "@@" в строке
            else if (streamRow.contains("@@")) {
                stringContainsAtAt();}
            // "Index:" в строке
            else if(streamRow.contains("Index:")){
                textPackage = streamRow.substring(6, streamRow.length()-5);
                rowIndex +=3;}
            else if(searchTestName && streamRow.contains("@Test(")){
                putMapAnnotation(streamRow.replaceAll(REGEXPPARAMETERS, "$1").replace(" ", "").split(","));
            }
        }
        if(searchTestName){
            try {
                String[] file = new HelperSVN(HelperSVN.LOGIN, HelperSVN.PASS, revision.getBranch().getUrl()).getFile(textPackage + ".java", revision.getRevision()).toString().replace("\r", "").split("\n");
                while(fileString < file.length && file[fileString]!=null) {
                    if (file[fileString].contains("public void")){
                        testName = textPackage + "/" + file[fileString].replaceAll(REGEXPTESTNAME, "$1");
                        stringContainsPublicVoid(file[fileString]);
                        clearAll();
                        break;}
                    fileString++;
                }
            } catch (SVNException e) {
                e.printStackTrace();}
        }
        return jsonModel;
    }

    private String isChangedTest(String[] stream,int index){
        while ((stream[index].matches("^\\+[^+].*") || stream[index].matches("^\\-[^-].*")) && index < stream.length - 1){
            if(stream[index].replace(" ","").contains("+@Test("))
                return stream[index].replace(" ","");
            index++;}
        return "";
    }

    private String isChangedName(String[] stream,int index){
        while ((stream[index].matches("^\\+[^+].*") || stream[index].matches("^\\-[^-].*"))  && index < stream.length - 1){
            if(stream[index].matches("^\\+.*public\\s*void.*"))
                return stream[index].replaceAll("^.*public\\s*void\\s*(\\w*).*","$1");
            index++;}
        return "";
    }

    //Достает инфу о тесте с отступом из файла по ревизии и бранче
    private Pair getTestInfo(String testName, int revision, Branch branch, int numberRow){
        String[] file = new String[0];
        boolean dogTest = false;
        ArrayList<HashMap<String, String>> listParametrs = new ArrayList<HashMap<String, String>>();
        try {
            file = new HelperSVN(HelperSVN.LOGIN, HelperSVN.PASS, branch.getUrl()).getFile(textPackage + ".java", revision).toString().replace("\r", "").split("\n");
        } catch (SVNException e) {
            e.printStackTrace();}
        while (numberRow < file.length && !(file[numberRow].contains("public void") && file[numberRow].contains(testName))){
            numberRow++;}
        while(!(dogTest && file[numberRow].contains("public void")) && numberRow > 1){
            if (file[numberRow].contains("@Test(")){
                dogTest = true;
                putMapAnnotation(file[numberRow].replaceAll(REGEXPPARAMETERS, "$1").replace(" ","").split(","));
            }
            if (file[numberRow].contains("@TestCase(")){
                String[] arrayParam = file[numberRow].replaceAll(REGEXPPARAMETERS, "$1").replace(" ","").split(",");
                putTestParam(arrayParam);
                listParametrs.add(new HashMap<String, String>(mapTestParam));
            }
            numberRow--;
        }
        return new Pair(mapAnnotation, listParametrs);
    }

    public void stringContainsAtAt(){
        startRevisionString = Integer.parseInt(streamRow.replaceAll("^.*@@\\-(\\d*)\\,(\\d*)\\+(\\d*)\\,(\\d*)@@", "$1"));
        endRevisionString = Integer.parseInt(streamRow.replaceAll("^.*@@\\-(\\d*)\\,(\\d*)\\+(\\d*)\\,(\\d*)@@", "$3"));
//        fileString = Integer.parseInt(streamRow.replaceAll(CHANGE,"$3")) + Integer.parseInt(streamRow.replaceAll(CHANGE,"$4"));
        fileString = startRevisionString < endRevisionString ? startRevisionString : endRevisionString;
        if (searchTestName) {
            try {
                String[] file = new HelperSVN(HelperSVN.LOGIN, HelperSVN.PASS, revision.getBranch().getUrl()).getFile(textPackage + ".java", revision.getRevision()).toString().replace("\r", "").split("\n");
                while(fileString < file.length && file[fileString]!=null) {
                    if (file[fileString].contains("public void")){
                        stringContainsPublicVoid(file[fileString]);
                        break;}
                    fileString++;
                }
            } catch (SVNException e) {
                e.printStackTrace();}
        }
        else{
            rowIndex +=3;}
    }

    //запись
    public void stringContainsPublicVoid(String tempRow){
        testName = textPackage + "/" + tempRow.replaceAll(REGEXPTESTNAME, "$1");
        for (HashMap<String, String> param : listParametersAdded) {
            jsonModel.addAdded(new Added(testName, param, new HashMap<String, String>(mapAnnotation)));}
        for (HashMap<String, String> param : listParametersDeleted) {
            jsonModel.addDeleted(new Deleted(testName, param, new HashMap<String, String>(mapAnnotation)));}
        if (!mapAnnotation.isEmpty()){
            jsonModel.addUpdated(new Updated(testName, new HashMap<String, String>(mapAnnotation)));}
        clearAll();
    }

    public void stringContainsPlus(){
        //добавился Ентрипоинт
        if (streamRow.contains("@TestCase(")) {
            entryPoint(listParametersAdded);
        }
        //добавился абсолютно новый тест
        else if (streamRow.contains("@Test(")) {
            putMapAnnotation(streamRow.replaceAll(REGEXPPARAMETERS, "$1").replace(" ","").split(","));
            searchTestName = true;
        }
        //добавился абсолютно новый тест
        else if (streamRow.contains("public void") && searchTestName) {
            testName = textPackage + "/" + streamRow.replaceAll(REGEXPTESTNAME, "$1");
            //запись
            for (HashMap<String, String> param : listParametersAdded) {
                jsonModel.addAdded(new Added(testName, param, new HashMap<String, String>(mapAnnotation)));}
            //обнуление
            clearAll();
        }
    }

    public void stringContainsMinus(){
        //удаление Ентри поинта
        if (streamRow.contains("@TestCase(")) {
            entryPoint(listParametersDeleted);
        }
        //изменение тестовых аннотаций
        else if (streamRow.contains("@Test(")) {
            String changeDogTest = isChangedTest(stream, rowIndex);
            if (!changeDogTest.isEmpty()) {
                changeTest = true;
                getAnnotation(changeDogTest,"enabled");
                getAnnotation(changeDogTest,"dataProvider");
                oldGroups = new ArrayList(Arrays.asList(streamRow.replaceAll("^.*groups=\\{(.*)\\}.*", "$1").split(",")));
                newGroups = new ArrayList(Arrays.asList(changeDogTest.replaceAll("^.*groups=\\{(.*)\\}.*", "$1").split(",")));

                notChangedGroups.clear();
                notChangedGroups.addAll(oldGroups);
                oldGroups.removeAll(newGroups);
                newGroups.removeAll(notChangedGroups);
                mapAnnotation.put("groupsDeleted", oldGroups.toString());
                mapAnnotation.put("groupsAdded", newGroups.toString());
                rowIndex++;
                searchTestName = true;
            }
            else{
                putMapAnnotation(streamRow.replaceAll(REGEXPPARAMETERS, "$1").replace(" ","").split(","));
                searchTestName = true;}
        }
        // нашли название теста,записываем в вывод, чистим списки и флаги
        else if (streamRow.contains("public void")/* && searchTestName*/) {
            //изменилось название теста
            Pair pair;
            String tempTestName = isChangedName(stream, rowIndex);
            if (!tempTestName.isEmpty()){
                pair = getTestInfo(streamRow.replaceAll("^.*public\\s*void\\s*(\\w*).*","$1"), revision.getRevision() - 1, revision.getBranch(), fileString);
                for (HashMap<String, String> entryPoint : pair.getR())
                    jsonModel.addDeleted(new Deleted(streamRow.replaceAll("^.*public\\s*void\\s*(\\w*).*", "$1"), entryPoint, new HashMap<String, String>(pair.getL())));
                pair = getTestInfo(tempTestName, revision.getRevision(), revision.getBranch(), fileString);
                for (HashMap<String, String> entryPoint : pair.getR())
                    jsonModel.addAdded(new Added(tempTestName, entryPoint, new HashMap<String, String>(pair.getL())));
            } else {
                //удаление теста
                testName = textPackage + "/" + streamRow.replaceAll(REGEXPTESTNAME, "$1");
                for (HashMap<String, String> param : listParametersDeleted) {
                    jsonModel.addDeleted(new Deleted(testName, param, new HashMap<String, String>(mapAnnotation)));}
            }
            //обнуление
            clearAll();
        }
    }

    private void putMapAnnotation(String[] annotations){
        for (String annotation : annotations) {
            if (!annotation.contains("=")){
                mapAnnotation.put("groups", mapAnnotation.get("groups") + ", " + annotation);}
            else {
                mapAnnotation.put(annotation.split("=")[0], annotation.split("=")[1]);}
        }
    }

    private void putTestParam(String[] arrayParam){
        for (int countParam = 0; countParam < arrayParam.length; countParam++) {
            if (!arrayParam[countParam].contains("=")){
                if (arrayParam[countParam-1].contains("accountType") || accountType) {
                    mapTestParam.put("accountType", mapTestParam.get("accountType") + ", " + arrayParam[countParam]);
                    accountType = true;
                    data = false;}
                else if (arrayParam[countParam-1].contains("data") || data) {
                    mapTestParam.put("data", mapTestParam.get("data") + ", " + arrayParam[countParam]);
                    data = true;
                    accountType = false;}
            }
            else {
                mapTestParam.put(arrayParam[countParam].split("=")[0], arrayParam[countParam].split("=")[1].replace("\"", ""));
                accountType = false;
                data = false;}
        }
    }

    private void entryPoint(ArrayList<HashMap<String, String>> list){
        String[] arrayParam = streamRow.replaceAll(REGEXPPARAMETERS, "$1").replace(" ", "").split(",");
        putTestParam(arrayParam);
        list.add(new HashMap<String, String>(mapTestParam));
        mapTestParam.clear();
        searchTestName = true;
    }

    private void getAnnotation(String changeDogTest, String annotation){
        if (streamRow.contains(annotation) && !streamRow.replaceAll("^.*" + annotation + "\\=(.*?),.*", "$1").equals(changeDogTest.replaceAll("^.*" + annotation + "\\=(.*?),.*", "$1"))) {
            testAnnotation += "from " + streamRow.replaceAll("^.*" + annotation + "\\=(.*?),.*", "$1") + " to " + changeDogTest.replaceAll("^.*" + annotation + "\\=(.*?),.*", "$1") + "; ";
            mapAnnotation.put(annotation, testAnnotation);}
    }

    private void clearAll(){
        testName = "";
        searchTestName = false;
        mapTestParam.clear();
        mapAnnotation.clear();
        mapAnnotation.clear();
        listParametersAdded.clear();
        listParametersDeleted.clear();
        oldGroups.clear();
        newGroups.clear();
        notChangedGroups.clear();
    }
}
