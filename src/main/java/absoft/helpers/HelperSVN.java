package absoft.helpers;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.*;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class HelperSVN {

    private String url;
    private SVNRepository repository;
    public static final String LOGIN = "";
    public static final String PASS = "";

    public HelperSVN(String login, String password, String url) throws SVNException {
        this.url = url;
        repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(login, password);
        repository.setAuthenticationManager( authManager );
    }

    public Collection getRevisionsList() throws SVNException {
        return repository.log( new String[] { "" } , null , 0 , -1 , true , false );
    }

    public Long getLastRevision() throws SVNException {
        return repository.info(".", -1).getRevision();
    }

    public Long getFirstRevision() throws SVNException {
        return repository.info(".", 0).getRevision();
    }

    public Long getDatedRevision(Date date) throws SVNException {
        return repository.getDatedRevision(date);
    }

    public Collection getSelectedRevisionsList(long startRevision, long endRevision) throws SVNException {
        return repository.log( new String[] { "" } , null , startRevision , endRevision , true , false );
    }

    public ByteArrayOutputStream getFile(String nameFile, long revision) throws SVNException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        repository.getFile(nameFile , revision , new SVNProperties(), baos );
        return baos;
    }

    public ByteArrayOutputStream getDifferenceByRevisions(long startRevision, long endRevision) throws SVNException {
        SVNClientManager clientManager = SVNClientManager.newInstance();
        SVNDiffClient diffClient = clientManager.getDiffClient();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        diffClient.doDiff(SVNURL.parseURIEncoded(url), SVNRevision.create(startRevision), SVNURL.parseURIEncoded(url), SVNRevision.create(endRevision), SVNDepth.INFINITY, true, outputStream);
        return outputStream;
    }
}
