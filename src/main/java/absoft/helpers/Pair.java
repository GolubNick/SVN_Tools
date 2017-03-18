package absoft.helpers;


import java.util.ArrayList;
import java.util.HashMap;

public class Pair {
    private HashMap<String, String> l;
    private ArrayList<HashMap<String, String>> r;
    public Pair(HashMap<String, String> l, ArrayList<HashMap<String, String>> r){
        this.l = l;
        this.r = r;
    }
    public HashMap<String, String> getL(){ return l; }
    public ArrayList<HashMap<String, String>> getR(){ return r; }
    public void setL(HashMap<String, String> l){ this.l = l; }
    public void setR(ArrayList<HashMap<String, String>> r){ this.r = r; }
}
