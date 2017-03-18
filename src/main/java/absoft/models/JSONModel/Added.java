package absoft.models.JSONModel;

import java.util.HashMap;

public class Added {
    private String testName;
    private HashMap<String, String> entryPoint;
    private HashMap<String, String> annotation;

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public HashMap<String, String> getEntryPoint() {
        return entryPoint;
    }

    public void setEntryPoint(HashMap<String, String> entryPoint) {
        this.entryPoint = entryPoint;
    }

    public HashMap<String, String> getAnnotation() {
        return annotation;
    }

    public void setAnnotation(HashMap<String, String> annotation) {
        this.annotation = annotation;
    }

    public Added(String testName, HashMap<String, String> entryPoint, HashMap<String, String> annotation) {

        this.testName = testName;
        this.entryPoint = entryPoint;
        this.annotation = annotation;
    }
}
