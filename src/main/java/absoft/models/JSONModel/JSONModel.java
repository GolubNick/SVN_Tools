package absoft.models.JSONModel;

import java.util.ArrayList;

public class JSONModel {
    private String revision;
    private String author;
    private String comment;
    private String date;
    private ArrayList<Added> added;
    private ArrayList<Deleted> deleted;
    private ArrayList<Updated> updated;

    public JSONModel(String revision, String author, String comment, String date) {
        this.revision = revision;
        this.author = author;
        this.comment = comment;
        this.date = date;
        this.added = new  ArrayList<Added>();
        this.deleted = new  ArrayList<Deleted>();
        this.updated = new  ArrayList<Updated>();
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<Added> getAdded() {
        return added;
    }

    public void setAdded(ArrayList<Added> added) {
        this.added = added;
    }

    public ArrayList<Deleted> getDeleted() {
        return deleted;
    }

    public void setDeleted(ArrayList<Deleted> deleted) {
        this.deleted = deleted;
    }

    public ArrayList<Updated> getUpdated() {
        return updated;
    }

    public void setUpdated(ArrayList<Updated> updated) {
        this.updated = updated;
    }
    public void addAdded(Added added){
        this.added.add(added);
    }
    public void addDeleted(Deleted deleted){
        this.deleted.add(deleted);
    }
    public void addUpdated(Updated updated){
        this.updated.add(updated);
    }
}
