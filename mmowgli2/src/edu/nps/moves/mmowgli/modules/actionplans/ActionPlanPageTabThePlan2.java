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
package edu.nps.moves.mmowgli.modules.actionplans;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;
import static edu.nps.moves.mmowgli.modules.actionplans.ActionPlanPage2.SaveCancelPan.CANCEL_BUTTON;
import static edu.nps.moves.mmowgli.modules.actionplans.ActionPlanPage2.SaveCancelPan.SAVE_BUTTON;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.SortedSet;

import org.hibernate.Session;

import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.BaseTheme;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.*;
import edu.nps.moves.mmowgli.modules.actionplans.ActionPlanPage2.SaveCancelPan;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;
import edu.nps.moves.mmowgli.utility.*;
import edu.nps.moves.mmowgli.utility.HistoryDialog.DoneListener;

/**
 * IdeaDashboardTabTheCards.java
 * Created on Feb 8, 2011
 *`
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ActionPlanPageTabThePlan2 extends ActionPlanPageTabPanel //implements ValueChangeListener, TextChangeListener
{
  private static final long serialVersionUID = -2609460134128823467L;

  //private TextField subTitleTF;
  private TextArea  whatTA;
  private TextArea  whatWillItTakeTA;
  private TextArea  howWorkTA;
  private TextArea  howChangeTA;
  private TextArea  whoTA;

  private FieldListener fldLis = new FieldListener();
  private TextAreaGroup whoGroup, whatGroup, whatTakeGroup, howGroup, howChangeGroup;

  public boolean whoIsFocused,whatIsFocused,whatTakeFocused,howWorkFocused,howChangeFocused;
  static Class<?>[] STRCLSARR = new Class<?>[]{String.class};  // for reflection

  public ActionPlanPageTabThePlan2(ActionPlanPage2 mother, Object apId, boolean isMockup)
  {
    super(apId, isMockup);
    //this.mother = mother;
    //subTitleTF = new TextField();
    //subTitleTF.add((TextChangeListener)this);
    whatTA = new TextArea();           whatTA.addStyleName(ACTIONPLAN_TAB_THEPLAN_WHAT);
    whatWillItTakeTA = new TextArea(); whatTA.addStyleName(ACTIONPLAN_TAB_THEPLAN_TAKE);
    howWorkTA = new TextArea();        whatTA.addStyleName(ACTIONPLAN_TAB_THEPLAN_WORK);
    howChangeTA = new TextArea();      whatTA.addStyleName(ACTIONPLAN_TAB_THEPLAN_CHANGE);
    whoTA = new TextArea();            whatTA.addStyleName(ACTIONPLAN_TAB_THEPLAN_WHO);    
  }

  @Override
  public void initGui()
  {
    setSizeUndefined(); // let flow to right
    VerticalLayout leftLay = getLeftLayout();

    leftLay.setSpacing(false);
    leftLay.setMargin(false);
    
    VerticalLayout flowLay = new VerticalLayout();
    leftLay.addComponent(flowLay);
    flowLay.setSpacing(true);
    
    Label missionLab = new Label("Authors, this is your workspace."); //"Congratulations! You're an author!");
    flowLay.addComponent(missionLab);
    flowLay.setComponentAlignment(missionLab, Alignment.TOP_LEFT);
    missionLab.addStyleName("m-actionplan-mission-title-text");
    
    ActionPlan actPln = ActionPlan.get(apId);
    Label missionContentLab;
    if(!isMockup)
      missionContentLab= new HtmlLabel(actPln.getPlanInstructions());
    else {
      Game g = Game.get(1L);
      missionContentLab = new HtmlLabel(g.getDefaultActionPlanThePlanText());
    }
    flowLay.addComponent(missionContentLab);
    flowLay.setComponentAlignment(missionContentLab, Alignment.TOP_LEFT);
    flowLay.addStyleName("m-actionplan-mission-content-text");
    
    VerticalLayout rightLay = getRightLayout();
    rightLay.setMargin(false);
    rightLay.setSpacing(false);
    
    flowLay = new VerticalLayout();
    flowLay.setWidth("100%");
    rightLay.addComponent(flowLay);
    flowLay.setSpacing(true);
    flowLay.setStyleName("m-actionplan-plan-rightside");      // set the style name so the css's below can use it (e.g.: .m-actionplan-plan-rightside .m-actionplan-plan-headling { blah:blah;} )
  
    Label lab = new Label();
    lab.setHeight("20px");
    flowLay.addComponent(lab);
   
    flowLay.addComponent(whoGroup=buildTextAreaGroup("Who is involved?", whoTA, "Who-is-involved history", "Previous values", "Text", "getSubTitleEditHistory", "getSubTitle", "setSubTitleWithHistory"));
    whoGroup.setValue(actPln.getSubTitle()); // misnamed since we're calling it it who is involved instead of subtitle
   
    flowLay.addComponent(whatGroup=buildTextAreaGroup("What is it?", whatTA, "What-is-it history", "Previous values", "Text", "getWhatIsItEditHistory", "getWhatIsItText", "setWhatIsItTextWithHistory"));
    whatGroup.setValue(actPln.getWhatIsItText());
   
    flowLay.addComponent(whatTakeGroup=buildTextAreaGroup("What will it take?", whatWillItTakeTA, "What-will-it-take history", "Previous values", "Text", "getWhatTakeEditHistory","getWhatWillItTakeText","setWhatWillItTakeTextWithHistory"));
    whatTakeGroup.setValue(actPln.getWhatWillItTakeText());

    flowLay.addComponent(howGroup=buildTextAreaGroup("How will it work?", howWorkTA,"How-will-it-work history", "Previous values", "Text", "getHowWorkEditHistory","getHowWillItWorkText","setHowWillItWorkTextWithHistory"));
    howGroup.setValue(actPln.getHowWillItWorkText());

    flowLay.addComponent(howChangeGroup=buildTextAreaGroup("How will it change the situation?", howChangeTA, "How-will-it-change-things history", "Previous values", "Text", "getHowChangeEditHistory","getHowWillItChangeText","setHowWillItChangeTextWithHistory"));
    howChangeGroup.setValue(actPln.getHowWillItChangeText());
  }

  class FieldListener implements /*FocusListener,*/ ClickListener
  {
    private static final long serialVersionUID = 1L;
    
 /*   @Override
    public void focus(FocusEvent event)
    {
      String s = "";
      Object source = event.getSource();
      if(source == whoTA) {
        if(whoTA.isReadOnly())
          return; 
        s = "\"Who is involved?\"";
        whoGroup.setFocused(true);
      }
      else if(source == whatTA) {
        if(whatTA.isReadOnly())
          return;
        s = "\"What is it?\"";
        whatGroup.setFocused(true);
      }
      else if(source == whatWillItTakeTA) {
        if(whatWillItTakeTA.isReadOnly())
          return;
        s = "\"What will it take?\"";
        whatTakeGroup.setFocused(true);
      }
      else if(source == howWorkTA) {
        if(howWorkTA.isReadOnly())
          return;
        s = "\"How will it work?\"";
        howGroup.setFocused(true);
      }
      else if(source == howChangeTA) {
        if(howChangeTA.isReadOnly())
          return;
        s = "\"How will change the situation?\"";
        howChangeGroup.setFocused(true);
      }

     String substring = " is editing the "+s+" field.";
     sendStartEditMessage( DBGet.getUser(app.getUser()).getUserName()+substring);     
    }
 */   
    private boolean getSet(TextAreaGroup tag, String getMethodName, String setMethodName, ActionPlan ap, Integer whButt)
    {
      boolean wantUpdate = false;
      try {
        if(whButt == CANCEL_BUTTON) {
          Method getMethod = ActionPlan.class.getDeclaredMethod(getMethodName, (Class<?>[])null);
          setValueIfNonNull(tag.ta,(String)getMethod.invoke(ap, (Object[])null));
          tag.union.labelTop();
        }
        else {
          Method setMethod = ActionPlan.class.getDeclaredMethod(setMethodName, STRCLSARR);
          int len = tag.ta.getValue().toString().length();
          if(len >= 511) {
            Notification notif = new Notification("<center>Not so fast!</center>",
                "Suggest limiting text length to 500 characters (now "+len+"). <small>Click this message to continue.</small>",
                Notification.Type.WARNING_MESSAGE,true);
            notif.setDelayMsec(-1); // must click
            notif.show(Page.getCurrent());
          }
        /*  else */ {   // over limit does not kill anymore
            String s = nullOrString(tag.ta.getValue());
            setMethod.invoke(ap, s);//nullOrString(tag.ta.getValue()));
            tag.union.setLabelValue(s);
            wantUpdate=true;
            tag.union.labelTop();
          }
        }
      }
      catch(Exception ex) {
        System.err.println("Error with reflection in ActionPlanPageTabThePlan.getSet()");
      }
      tag.setFocused(false);
      return wantUpdate;
    }
    
    @Override
    public void buttonClick(ClickEvent event)
    {
      ActionPlan ap = ActionPlan.get(apId);
      Button o = event.getButton();
      Integer wh = null;
      boolean wantUpdate=false;
      String eventText="action plan field";
      
      if((wh=whoGroup.isMyButton(o))!=null) {
        wantUpdate = getSet(whoGroup,"getSubTitle","setSubTitleWithHistory",ap,wh);
        eventText = "who is involved";
      }     
      else if((wh=whatGroup.isMyButton(o))!=null) {
        wantUpdate = getSet(whatGroup,"getWhatIsItText","setWhatIsItTextWithHistory",ap,wh);
        eventText = "what is it";
      }
      else if((wh=whatTakeGroup.isMyButton(o))!=null) {
        wantUpdate = getSet(whatTakeGroup,"getWhatWillItTakeText","setWhatWillItTakeTextWithHistory",ap,wh);
        eventText = "what will it take";
      }
      else if((wh=howGroup.isMyButton(o))!=null) {
        wantUpdate = getSet(howGroup,"getHowWillItWorkText","setHowWillItWorkTextWithHistory",ap,wh);
        eventText = "how will it work";
      }
      else if((wh=howChangeGroup.isMyButton(o))!=null) {
        wantUpdate = getSet(howChangeGroup,"getHowWillItChangeText","setHowWillItChangeTextWithHistory",ap,wh);
        eventText = "how will it change things";
      }
            
      if(wh != null && wh == SAVE_BUTTON && wantUpdate) {
        ActionPlan.update(ap);
        User u = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID());
        GameEventLogger.logActionPlanUpdate(ap, eventText, u.getId()); //u.getUserName());
      }
    }
  }
  
  private TextAreaGroup buildTextAreaGroup(String labelText, TextArea ta, String windowTitle, String tableTitle, String columnTitle, String listGetterStr, String currentGetterStr, String currentSetterStr)
  {
    TextAreaGroup tag = new TextAreaGroup(labelText, ta, fldLis,  windowTitle, tableTitle, columnTitle, listGetterStr, currentGetterStr, currentSetterStr);
    return tag;
  }

  class TextAreaGroup extends VerticalLayout implements FocusListener
  {
    private static final long serialVersionUID = 1L;
    
    private SaveCancelPan scp;
    private TextArea ta;
    NativeButton historyButt;
    private TextAreaLabelUnion union;
    private boolean ro;
    
    public TextAreaGroup(String labelText, TextArea ta, ClickListener lis, String windowTitle, String tableTitle, String columnHeader,String listGetterStr, String currentGetterStr, String currentSetterStr)
    {
      this.ta = ta;
      setWidth("680px");
      
      setMargin(false);
      setSpacing(true);
      
      HorizontalLayout hl = new HorizontalLayout();
      hl.setMargin(false);
      hl.setSpacing(false);
      Label lab = new Label(labelText);
      lab.addStyleName("m-actionplan-plan-question");

      hl.addComponent(lab);
      hl.setComponentAlignment(lab, Alignment.MIDDLE_LEFT);
      hl.setExpandRatio(lab, 1.0f);
      scp = new SaveCancelPan();
      scp.setClickHearer(lis);
      hl.addComponent(scp);
      addComponent(hl);
      
      hl.setWidth("100%");
      ta.setSizeFull();
      
      scp.setVisible(false);
      
      VerticalLayout taVL = new VerticalLayout();
      taVL.setSpacing(false);
      taVL.setMargin(false);

      union = new TextAreaLabelUnion(ta,null,this);
      union.addStyleName("m-darkgreyborder");

      taVL.addComponent(union);
      union.setHeight("75px");
      union.setWidth("680px");
      union.initGui();
      addComponent(taVL);
      
     /* Label nuts;
      taVL.addComponent(nuts=new Label("test<br/><br/><br/>"));
      nuts.setContentMode(Label.CONTENT_XHTML);
      nuts.setSizeFull();
      nuts.addStyleName("m-actionplan-plan-answer");
      absL.addComponent(nuts,"top:0px;left:0px;");
      absL.addComponent(ta,"top:0px;left:0px;");
      ComponentPosition cp = absL.getPosition(ta);
      System.out.println("duh = "+ ++duh);
      cp.setZIndex(duh%2==0?0:1);
      cp = absL.getPosition(nuts);
      cp.setZIndex(duh%2==0?1:0);
      System.out.println(""+(duh%2)); */
      historyButt = new NativeButton();     
      historyButt.setCaption("history");
      historyButt.setStyleName(BaseTheme.BUTTON_LINK);
      historyButt.addStyleName("borderless");
      historyButt.addStyleName("m-actionplan-history-button");

      historyButt.addClickListener(new HistoryListener(windowTitle,tableTitle,columnHeader,listGetterStr,currentGetterStr,currentSetterStr));
      taVL.addComponent(historyButt);
      historyButt.setSizeUndefined();
      taVL.setComponentAlignment(historyButt,Alignment.TOP_RIGHT);
    }

    @Override
    public void focus(FocusEvent event)
    {
      setFocused(true);
      String substring = " is editing the "+ "<todo field names>"+" field.";
      sendStartEditMessage( DBGet.getUser(Mmowgli2UI.getGlobals().getUserID()).getUserName()+substring);     
    } 

    public void setValue(String s)
    {
      union.setValue(s);
    }
    
    public void setValueOob(String s, Session sess)
    {
      union.setValueOob(s, sess);
    }
    
    public String getValue()
    {
      return union.getValue();
    }
    
    public Button getHistoryButton()
    {
      return historyButt;
    }
    
    public void setFocused(boolean tf)
    {
      scp.setVisible(tf);
    }
    
    public boolean isFocused()
    {
      return scp.isVisible();
    }
    
    public Integer isMyButton(Button b)
    {
      if(b == scp.saveButt)
        return SAVE_BUTTON;
      if(b == scp.canButt)
        return CANCEL_BUTTON;
      return null;
    }
    
    class HistoryListener implements ClickListener, DoneListener
    {
      private static final long serialVersionUID = 1L;
      String windowTitle, tableTitle, columnTitle;
      Method historyListGetter;
      Method currentGetter;
      Method currentSetter;
      
      public HistoryListener(String windowTitle, String tableTitle, String columnTitle, String listGetterStr, String currentGetterStr, String currentSetterStr)
      {
        try {
          historyListGetter = ActionPlan.class.getDeclaredMethod(listGetterStr, new Class<?>[]{});
          currentGetter     = ActionPlan.class.getDeclaredMethod(currentGetterStr, new Class<?>[]{});
          currentSetter     = ActionPlan.class.getDeclaredMethod(currentSetterStr,  new Class<?>[]{String.class});
          this.windowTitle = windowTitle;
          this.tableTitle = tableTitle;
          this.columnTitle = columnTitle;
        }
        catch(Exception ex) {
          throw new RuntimeException("Programming error in ActionPlanPageTabThePlan2.HistoryListener: "+ex.getClass().getSimpleName()+", "+ex.getLocalizedMessage());
        }
      }
      
      @SuppressWarnings("unchecked")
      @Override
      public void buttonClick(ClickEvent event)
      { 
        try {
          SortedSet<Edits> set = (SortedSet<Edits>)historyListGetter.invoke(ActionPlan.get(apId), (Object[])null); 
          HistoryDialog dial = new HistoryDialog(set, windowTitle, tableTitle, columnTitle,  this);
          UI.getCurrent().addWindow(dial);
          dial.center();
        }
        catch(Exception ex) {
          System.out.println("bp");
        }
      } 
      
      public void done(String sel, int idx)
      {
        if (sel != null) {
          try {
            ActionPlan ap = ActionPlan.get(apId);
            String currentTitle = (String) currentGetter.invoke(ap, (Object[]) null);
            if (!sel.equals(currentTitle)) {
             // LinkedList<String> lis = (LinkedList<String>) historyListGetter.invoke(ap, (Object[]) null);
             // lis.remove(idx);
              currentSetter.invoke(ap, new Object[] {sel} ); // will push
              ActionPlan.update(ap);
            }
          }
          catch (Exception ex) {
            throw new RuntimeException("Programming error in ActionPlanPageTabThePlan2.done: " + ex.getClass().getSimpleName() + ", " + ex.getLocalizedMessage());
          }
        }
      }
    }

    public boolean isRo()
    {
      return ro;
    }

    public void setRo(boolean ro)
    {
      this.ro = ro;
      union.setRo(ro);
    }
  }

  @Override
  public boolean actionPlanUpdatedOob(SessionManager sessMgr, Serializable apId)
  {
    Session session = M.getSession(sessMgr);
    
    ActionPlan ap = (ActionPlan) session.get(ActionPlan.class, (Serializable) apId);

    boolean whoRO = whoGroup.isRo();
    boolean whatRO = whatGroup.isRo(); 
    boolean whatTakeRO = whatTakeGroup.isRo(); 
    boolean howRO = howGroup.isRo(); 
    boolean howChangeRO = howChangeGroup.isRo();

    whoGroup.setRo(false);
    whatGroup.setRo(false);
    whatTakeGroup.setRo(false);
    howGroup.setRo(false);
    howChangeGroup.setRo(false);

    boolean updateUI = false;
    String s = null;
    
    if (!whoGroup.isFocused()) {
      s = ap.getSubTitle();
      if (!whoGroup.getValue().equals(s)) {
        whoGroup.setValueOob(s,session);
        updateUI = true;
      }
    }
    if (!whatGroup.isFocused()) {
      s = ap.getWhatIsItText();
      if (!whatGroup.getValue().equals(s)) {
        whatGroup.setValueOob(s,session);
        updateUI = true;
      }
    }
    if (!whatTakeGroup.isFocused()) {
      s = ap.getWhatWillItTakeText();
      if (!whatTakeGroup.getValue().equals(s)) {
        whatTakeGroup.setValueOob(s,session);
        updateUI = true;
      }
    }

    if (!howGroup.isFocused()) {
      s = ap.getHowWillItWorkText();
      if (!howGroup.getValue().equals(s)) {
        howGroup.setValueOob(s,session);
        updateUI = true;
      }
    }
    if (!howChangeGroup.isFocused()) {
      s = ap.getHowWillItChangeText();
      if (!howChangeGroup.getValue().equals(s)) {
        howChangeGroup.setValueOob(s,session);
        updateUI = true;
      }
    }

    whoGroup.setRo(whoRO);
    whatGroup.setRo(whatRO);
    whatTakeGroup.setRo(whatTakeRO);
    howGroup.setRo(howRO);
    howChangeGroup.setRo(howChangeRO);

    return updateUI;
  }

  @Override
  public void setImAuthor(boolean yn)
  {
    //whoTA.setReadOnly(!yn);
    whoGroup.setRo(!yn);
    whoGroup.getHistoryButton().setVisible(yn);
    //whatTA.setReadOnly(!yn);
    whatGroup.setRo(!yn);
    whatGroup.getHistoryButton().setVisible(yn);
    //whatWillItTakeTA.setReadOnly(!yn);
    whatTakeGroup.setRo(!yn);
    whatTakeGroup.getHistoryButton().setVisible(yn);
    //howWorkTA.setReadOnly(!yn);
    howGroup.setRo(!yn);
    howGroup.getHistoryButton().setVisible(yn);
    //howChangeTA.setReadOnly(!yn); 
    howChangeGroup.setRo(!yn);
    howChangeGroup.getHistoryButton().setVisible(yn);
  }
}