/*
 * Copyright (c) 1995-2010 held by the author(s).  All rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *  
 *  * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer
 *       in the documentation and/or other materials provided with the
 *       distribution.
 *  * Neither the names of the Naval Postgraduate School (NPS)
 *       Modeling Virtual Environments and Simulation (MOVES) Institute
 *       (http://www.nps.edu and http://www.MovesInstitute.org)
 *       nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific
 *       prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package edu.nps.moves.mmowgli.export;

import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hibernate.Session;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.nps.moves.mmowgli.MmowgliConstants;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.db.Media.MediaType;
import edu.nps.moves.mmowgli.hibernate.SingleSessionManager;

/**
 * ActionPlanExporter.java Created on Nov 28, 2011
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ActionPlanExporter extends BaseExporter
{
  Thread thread;
  
  private String CDATA_ELEMENTS       = BRIEFING_TEXT_ELEM/*+" "+blah1+" "+blah2*/;
  
  public final String STYLESHEET_NAME = "ActionPlanList.xsl";
  public final String FILE_NAME       = "ActionPlanList";
  public final String THREAD_NAME     = "ActionPlanExporter";
  
  public final String ACTIONPLANID_KEY = "displaySingleActionPlanNumber";
  
  private static Map<String,String> parameters = null;
  
  Long apId = null;
  
  public ActionPlanExporter()
  { 
  }
  
  /**
   * Normal use: let base class manage.  Just store and return through following 2 methods.
   * return null first time if letting base class handle, else return map generated here.
   */
  public Map<String,String> getStaticTransformationParameters()
  {
    return parameters;
  }
  
  /**
   * Normal use: called by base class.  Save for subsequent returns through getStaticTransformationParameters().
   */
  public void setStaticTransformationParameters(Map<String,String> map)
  {
    parameters = map;
  }
  
  // Entry from menu item
  public void exportSinglePlanToBrowser(String title, Object apId)
  {
    this.apId = (Long)apId;
    parameters = new HashMap<String,String>();
    parameters.put(ACTIONPLANID_KEY,apId.toString());
    showXml = false;
  showXml=true; //test  
    exportToBrowser(title);    
  }
  
  // Entry from menu item
  public void exportAllPlansToBrowser(String title)
  {
    this.apId = null;
    parameters = new HashMap<String,String>();
    showXml = false;
 showXml = true; //test   
    exportToBrowser(title);      
  }

  public Document buildXmlDocument() throws Throwable
  {
    Document doc;
    SingleSessionManager ssm = null;
    Session sess = null;;
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder parser = factory.newDocumentBuilder();
      doc = parser.newDocument();
      doc.setXmlStandalone(true);
      // Can't get regex stuf in xsl 2.0 to work...
      // ProcessingInstruction pi = doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"ActionPlanList.xsl\"");
      // Use comment instead
      Comment comm = doc.createComment("<xml-stylesheet\", \"type=\"text/xsl\" href=\""+STYLESHEET_NAME+"\""); //ActionPlanList.xsl\"?>");
      Element root = doc.createElement("ActionPlanList");
      doc.appendChild(root);
      // doc.insertBefore(pi, root);
      doc.insertBefore(comm, root);

      // Skip schema for now (needs tweeking)
      // root.setAttribute("xmlns","http://edu.nps.moves.mmowgli.actionPlanList");
      // root.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
      // root.setAttribute("xsi:schemaLocation", "http://edu.nps.moves.mmowgli.actionPlanList ActionPlanList.xsd");

      root.setAttribute("exported", dateFmt.format(new Date()));
      
      ssm = new SingleSessionManager();
      sess = ssm.getSession();
      
      root.setAttribute("multipleMoves", Boolean.toString(isMultipleMoves(sess)));
      Game g = (Game) sess.get(Game.class, 1L);
      String s = g.getTitle();
      addElementWithText(root, "GameTitle", s.replace(' ','_'));  // better file name handling
      addElementWithText(root, "GameSecurity", g.isShowFouo()?"FOUO":"open");
      addElementWithText(root, "GameSummary", metaString);
      addCallToAction(root, sess);

      @SuppressWarnings("unchecked")
      List<ActionPlan> lis = sess.createCriteria(ActionPlan.class).list();     
      List<RankedActionPlan>rlis = sortByRank(lis);
      ssm.endSession();   
      ssm = null;

      for (ActionPlan ap : lis)  {
        ssm = new SingleSessionManager();
        sess = ssm.getSession();
        ap = ActionPlan.merge(ap,sess);
        addPlanToDocument(root, ap, getRank(ap,rlis));
        ssm.endSession();   
        ssm = null;
      }
     
      /* This orders the list...use instead of above if desired
      for (RankedActionPlan rap : rlis)
        addPlanToDocument(root, rap.plan, rap.roundRank); */
    }
    catch (Throwable t) {
      t.printStackTrace();
      if(sess != null)
        ssm.endSession();
      throw t; // rethrow
    }
    return doc;
  }
  
  private void addPlanToDocument(Element root, ActionPlan ap, int rank) throws Throwable
  {
    try {
      // System.out.println("exporting action plan "+ap.getId());
      Element apElem = createAppend(root, "ActionPlan");
      apElem.setAttribute("thumbs", twoPlaceDecimalFmt.format(ap.getAverageThumb()));
      apElem.setAttribute("sumThumbs", onePlaceDecimalFmt.format(ap.getSumThumbs()));
      apElem.setAttribute("numVoters", "" + ap.getUserThumbs().size());
      apElem.setAttribute("roundRanking", "" + rank);
      apElem.setAttribute("hidden", "" + ap.isHidden());
      apElem.setAttribute("superInteresting", "" + ap.isSuperInteresting());
      apElem.setAttribute("moveNumber", "" + ap.getCreatedInMove().getNumber());
      apElem.setAttribute("creationDate", dateFmt.format(ap.getCreationDate()));

      addElementWithText(apElem, "Title", toUtf8(nn(ap.getTitle())));
      addElementWithText(apElem, "ID", nn("" + ap.getId()));

      Card chainRoot = ap.getChainRoot();
      Element chain = addElementWithText(apElem, "CardChainRoot", toUtf8(chainRoot.getText()));
      chain.setAttribute("type", chainRoot.getCardType().getTitle());
      chain.setAttribute("author", chainRoot.getAuthorName());
      chain.setAttribute("ID", "" + chainRoot.getId());
      chain.setAttribute("date", dateFmt.format(chainRoot.getCreationDate()));

      addElementWithText(apElem, "WhoIsInvolved", toUtf8(nn(ap.getSubTitle().trim())));
      addElementWithText(apElem, "WhatIsIt", toUtf8(nn(ap.getWhatIsItText().trim())));
      addElementWithText(apElem, "WhatWillItTake", toUtf8(nn(ap.getWhatWillItTakeText().trim())));
      addElementWithText(apElem, "HowWillItWork", toUtf8(nn(ap.getHowWillItWorkText().trim())));
      addElementWithText(apElem, "HowWillItChangeThings", toUtf8(nn(ap.getHowWillItChangeText().trim())));

      // System.out.println("exp getting authors");
      Set<User> authors = ap.getAuthors();
      for (User u : authors) {
        // System.out.println("exp author "+u.getUserName());
        Element author = createAppend(apElem, "Author");
        addElementWithText(author, "GameName", toUtf8(nn(u.getUserName())));
        // do not emit... addElementWithText(author,"Location",toUtf8(nn(u.getLocation())));
      }

      // System.out.println("exp getting comments");
      SortedSet<Message> comments = ap.getComments();
      Element commentList = createAppend(apElem, "CommentList");

      for (Message m : comments) {
        // System.out.println("exp comment "+n++);
        Element message = addElementWithText(commentList, "Comment", toUtf8(nn(m.getText())));
        message.setAttribute("postTime", dateFmt.format(m.getDateTime()));
        message.setAttribute("from", toUtf8(nn(m.getFromUser().getUserName())));
        message.setAttribute("hidden", "" + m.isHidden());
        message.setAttribute("superInteresting", "" + m.isSuperInteresting());
        message.setAttribute("moveNumber", "" + m.getCreatedInMove().getNumber());
      }

      // System.out.println("exp getting media");
      List<Media> medlis = ap.getMedia();
      Element images = createAppend(apElem, "ImageList");

      for (Media med : medlis) {
        // System.out.println("exp image "+ n++);
        if (med.getType() == MediaType.IMAGE) {
          Element imageEl = createAppend(images, "Image");
          addElementWithText(imageEl, "Title", toUtf8(nn(med.getTitle())));
          addElementWithText(imageEl, "Caption", toUtf8(nn(med.getCaption())));
          addElementWithText(imageEl, "Description", toUtf8(nn(med.getDescription())));
          String url = nn(med.getUrl());
          String activeUrl = url;
          if (med.getSource() == Media.Source.USER_UPLOADS_REPOSITORY) {
            activeUrl = "file://" + MmowgliConstants.USER_IMAGES_FILESYSTEM_PATH + url;
            url = MmowgliConstants.REPORT_TO_IMAGE_URL_PREFIX + url;
          }
          addElementWithText(imageEl, "URL", url);
          ImageSize iSz = getImageSize(activeUrl);
          addAttribute(imageEl, "width", "" + iSz.size.width);
          addAttribute(imageEl, "height", "" + iSz.size.height);
          addAttribute(imageEl, "scaledWidth", "" + iSz.scaledSize.width);
          addAttribute(imageEl, "scaledHeight", "" + iSz.scaledSize.height);
          addImageContent(imageEl, med);
        }
      }

      Element videos = createAppend(apElem, "VideoList");

      for (Media med : medlis) {
        // System.out.println("exp video "+n++);
        if (med.getType() == MediaType.YOUTUBE) {
          Element vid = createAppend(videos, "Video");
          addElementWithText(vid, "Title", toUtf8(nn(med.getTitle())));
          addElementWithText(vid, "Caption", toUtf8(nn(med.getCaption())));
          addElementWithText(vid, "Description", toUtf8(nn(med.getDescription())));
          addElementWithText(vid, "URL", "https://www.youtube.com/watch?v=" + nn(med.getUrl()));
        }
      }

      // System.out.println("exp getting chat log");
      ChatLog chat = ap.getChatLog();
      SortedSet<Message> chats = chat.getMessages();
      Element chatLog = createAppend(apElem, "ChatLog");

      for (Message m : chats) {
        // System.out.println("exp chat "+n++);
        Element message = addElementWithText(chatLog, "Message", toUtf8(nn(m.getText())));
        message.setAttribute("postTime", dateFmt.format(m.getDateTime()));
        message.setAttribute("from", toUtf8(nn(m.getFromUser().getUserName())));
        message.setAttribute("hidden", "" + m.isHidden());
        message.setAttribute("superInteresting", "" + m.isSuperInteresting());
        message.setAttribute("moveNumber", "" + m.getCreatedInMove().getNumber());
      }
    }
    catch (Throwable t) {
      // Do something here if needed
      throw t; // rethrow
    }
  }
  
  class RankedActionPlan
  {
    public int roundRank=1; // one-based
    public ActionPlan plan;
    public RankedActionPlan(ActionPlan ap)
    {
      plan = ap;
    }
  }
  
  private List<RankedActionPlan> sortByRank(List<ActionPlan>lis)
  {
    ArrayList<RankedActionPlan> rlis = new ArrayList<RankedActionPlan>(lis.size());
    for(ActionPlan ap : lis)
      rlis.add(new RankedActionPlan(ap));
    
    Collections.sort(rlis,new RoundThumbComparator());
    
    int lastRound = -1;
    int rank = -1;
    for(RankedActionPlan rap : rlis) {
       if(rap.plan.getCreatedInMove().getNumber() != lastRound) {
         lastRound = rap.plan.getCreatedInMove().getNumber();
         rank = 1; // 1-based
       }
       rap.roundRank = rank++;
    }
    return rlis;
  }
  
  private int getRank(ActionPlan ap,List<RankedActionPlan> rlis)
  {
    for(RankedActionPlan rap : rlis)
      if(ap.getId() == rap.plan.getId())
        return rap.roundRank;
    return 0;
  }
  
  class RoundThumbComparator implements Comparator<RankedActionPlan>
  {
    @Override
    public int compare(RankedActionPlan ap1, RankedActionPlan ap2)
    {
      double round1 = ap1.plan.getCreatedInMove().getNumber() * (-1000.);
      double round2 = ap2.plan.getCreatedInMove().getNumber() * (-1000.);
      
      double val1 = round1 + ap1.plan.getAverageThumb();
      double val2 = round2 + ap2.plan.getAverageThumb();

      if(val1 < val2)  // want high to low
        return 1;
      if(val1 > val2)
        return -1;
      return 0;
    }    
  }
  
  @Override
  public String getCdataSections()
  {
    return CDATA_ELEMENTS;
  }
  @Override
  public String getStyleSheetName()
  {
    return STYLESHEET_NAME;
  }
  @Override
  public String getFileNamePrefix()
  {
    return FILE_NAME;
  }
  @Override
  public String getThreadName()
  {
    return THREAD_NAME;
  }

 }