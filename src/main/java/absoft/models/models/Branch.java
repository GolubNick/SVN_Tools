package absoft.models.models;


public class Branch {

    private long id;
    private String url;
    private int revision;

    public Branch() { }

    public Branch(long id) {
        this.id = id;
    }

    public Branch(String url) { this.url = url; }

    public Branch(String url,int revision) {
        this.url = url;
        this.revision = revision;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    @Override
    public String toString(){
        final StringBuilder sb = new StringBuilder("Branch{");
        sb.append("id='").append(id).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", revision='").append(revision).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
