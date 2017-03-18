package absoft.models.JSONModel;

import java.util.HashMap;

public class Updated {
    private String testName;

    private HashMap<String, String> annotation;

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public HashMap<String, String> getAnnotation() {
        return annotation;
    }

    public void setAnnotation(HashMap<String, String> annotation) {
        this.annotation = annotation;
    }

    public Updated(String testName, HashMap<String, String> annotation) {

        this.testName = testName;
        this.annotation = annotation;
    }
}
