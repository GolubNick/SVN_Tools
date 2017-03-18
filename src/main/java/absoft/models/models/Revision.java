package absoft.models.models;

import java.util.Date;

public class Revision {

    private long id;

    private int revision;
    private String comment;
    private Date date;
    private String author;
    private Branch branch;

    public Revision(){

    }

    public Revision(int revision, String comment, Date date, String author, Branch branch) {
        this.revision = revision;
        this.comment = comment;
        this.date = date;
        this.author = author;
        this.branch = branch;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString(){
        final StringBuilder sb = new StringBuilder("Revision{");
        sb.append("id='").append(id).append('\'');
        sb.append(", revision='").append(revision).append('\'');
        sb.append(", comment='").append(comment).append('\'');
        sb.append(", date='").append(date).append('\'');
        sb.append(", author='").append(author).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
