package absoft.controllers;

import absoft.helpers.HelperSVN;
import absoft.helpers.ParseStream;
import absoft.models.JSONModel.JSONModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class MainController {
    @Autowired
    ParseStream parseStream;

    @RequestMapping(value = "/getLastRevision", method = RequestMethod.GET)
    @ResponseBody int getLastRevision(@RequestParam String branchText) throws SVNException {
        HelperSVN svnHelper = new HelperSVN(HelperSVN.LOGIN, HelperSVN.PASS, branchText);
        return svnHelper.getLastRevision().intValue();
    }

    @RequestMapping(value = "/difference", method = RequestMethod.GET)
    @ResponseBody List<JSONModel> difference(@RequestParam String start_revision, @RequestParam String end_revision, @RequestParam String branch) throws SVNException, ParseException {
        HelperSVN svnHelper = new HelperSVN(HelperSVN.LOGIN, HelperSVN.PASS, branch);
        int endRevision = end_revision.isEmpty() ? svnHelper.getLastRevision().intValue() : Integer.parseInt(end_revision);
        return new SaveToDBSendToEMail().saveToDBSendToEMail(branch, parseStream, Integer.parseInt(start_revision), endRevision);
    }

    @RequestMapping(value = "/difference_dates", method = RequestMethod.GET)
    @ResponseBody List<JSONModel> difference_dates(@RequestParam String start_date, @RequestParam String end_date, @RequestParam String branch) throws SVNException, ParseException {
        long end_revision, start_revision;
        HelperSVN svnHelper = new HelperSVN(HelperSVN.LOGIN, HelperSVN.PASS, branch);
        DateFormat df = new SimpleDateFormat("ddMMyyyyHHmm");
        start_revision = svnHelper.getDatedRevision(df.parse(start_date));
        int endRevision = end_date.isEmpty() ? svnHelper.getLastRevision().intValue() : Integer.parseInt(String.valueOf(svnHelper.getDatedRevision(df.parse(end_date))));
        return new SaveToDBSendToEMail().saveToDBSendToEMail(branch, parseStream, Integer.parseInt(String.valueOf(start_revision)), endRevision);
    }

}
