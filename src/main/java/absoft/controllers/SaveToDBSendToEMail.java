package absoft.controllers;

import absoft.helpers.*;
import absoft.models.JSONModel.JSONModel;
import absoft.models.models.Branch;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class SaveToDBSendToEMail {

    public List<JSONModel> saveToDBSendToEMail(String branchUrl, ParseStream parseStream, int startRevision, int endRevision) throws SVNException, ParseException {
        List<JSONModel> list = new ArrayList<JSONModel>();
        System.out.println("RUN " + branchUrl + " ...");
        ArrayList<Long> listRevisions = new ArrayList<Long>();
        ArrayList<Date> listRevisionsDate = new ArrayList<Date>();
        ArrayList<String> listRevisionsComment = new ArrayList<String>();
        ArrayList<String> listRevisionsAuthor = new ArrayList<String>();
        Branch branch = new Branch();
        branch.setUrl(branchUrl);
        HelperSVN svnHelper = new HelperSVN(HelperSVN.LOGIN, HelperSVN.PASS, branchUrl);
        int endSVNRevision = endRevision == 0 ? svnHelper.getLastRevision().intValue() : endRevision;
        System.out.println("ActualRevision = " + startRevision);
        System.out.println("EndRevision = " + endSVNRevision);

        if (startRevision < endSVNRevision) {
            System.out.println("Updating ...");
            Collection svnRevisions = svnHelper.getSelectedRevisionsList(startRevision, endSVNRevision);
            for (Object svnRevision : svnRevisions) {
                listRevisions.add(((SVNLogEntry) svnRevision).getRevision());
                listRevisionsDate.add(((SVNLogEntry) svnRevision).getDate());
                listRevisionsComment.add(((SVNLogEntry) svnRevision).getMessage());
                listRevisionsAuthor.add(((SVNLogEntry) svnRevision).getAuthor());
            }
                ByteArrayOutputStream outputStream = svnHelper.getDifferenceByRevisions(listRevisions.get(0) - 1, listRevisions.get(0));
                list.add(parseStream.saveTestMethodNameBD(outputStream, branch, listRevisionsDate.get(0), listRevisions.get(0), listRevisionsComment.get(0), listRevisionsAuthor.get(0)));
            if (listRevisions.size() != 1){
                for (int i = 0; i < listRevisions.size() - 1; i++) {
                    outputStream = svnHelper.getDifferenceByRevisions(listRevisions.get(i), listRevisions.get(i + 1));
                    list.add(parseStream.saveTestMethodNameBD(outputStream, branch, listRevisionsDate.get(i + 1), listRevisions.get(i + 1), listRevisionsComment.get(i + 1), listRevisionsAuthor.get(i + 1)));
                }
            }
            System.out.println("End Updating !!!");
        }
        return list;
    }
}