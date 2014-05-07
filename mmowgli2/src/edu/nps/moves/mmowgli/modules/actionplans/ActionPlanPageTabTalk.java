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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import org.hibernate.Session;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.themes.BaseTheme;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliSessionGlobals;
import edu.nps.moves.mmowgli.cache.MCacheManager.QuickUser;
import edu.nps.moves.mmowgli.components.*;
import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgli.hibernate.*;
import edu.nps.moves.mmowgli.messaging.WantsChatLogUpdates;
import edu.nps.moves.mmowgli.modules.cards.EditCardTextWindow;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;
import edu.nps.moves.mmowgli.utility.M;
import edu.nps.moves.mmowgli.utility.MmowgliLinkInserter;

/**
 * ActionPlanPageTabTalk.java
 * Created on Feb 8, 2011
 * Updated on Mar 14, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ActionPlanPageTabTalk extends ActionPlanPageTabPanel implements/* ClickListener, */WantsChatLogUpdates
{
  private static final long serialVersionUID = 3549944451620639594L;
  
  private NativeButton submitButt;
  private NativeButton discardButt;
  private ToggleLinkButton viewAllButt;
  private Panel chatScroller;
  private TextArea chatTextField;
  private SimpleDateFormat dateFormatter;
  private Component chatEntryComponent;
  private NativeButton chatSubmitButt;
  private Label nonAuthorLabel;
  private boolean showingHiddenMsgs = false;
  private boolean isGameMasterOrAdmin = false;
  private boolean gameRO = false;
  
  public ActionPlanPageTabTalk(Object apId, boolean isMockup)
  {
    super(apId, isMockup);

    submitButt = new NativeButton();
    discardButt = new NativeButton();
    dateFormatter = new SimpleDateFormat("MM/dd HH:mm z");
    chatEntryComponent=createChatEntryField();    
    User me = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID());
    
    isGameMasterOrAdmin = me.isAdministrator() || me.isGameMaster();
  }
  
  @Override
  public void setVisible(boolean visible)
  {
    super.setVisible(visible);
    if(visible)
      chatSubmitButt.setClickShortcut(KeyCode.ENTER);
    else
      chatSubmitButt.removeClickShortcut();
  }
  
  @Override
  public void initGui()
  {
    setSizeUndefined();
    VerticalLayout leftVL = this.getLeftLayout();

    leftVL.setSpacing(true);

    Label missionLab = new Label("Authors, this is your team space.");
    leftVL.addComponent(missionLab);
    leftVL.setComponentAlignment(missionLab, Alignment.TOP_LEFT);
    missionLab.addStyleName("m-actionplan-mission-title-text");
    
    ActionPlan ap = ActionPlan.get(apId);

    Label missionContentLab;
    Game g = Game.get(1L);
    if(!isMockup)
      missionContentLab = new HtmlLabel(ap.getTalkItOverInstructions());
    else {
      missionContentLab = new HtmlLabel(g.getDefaultActionPlanTalkText());
    }
    gameRO = g.isReadonly();
    
    leftVL.addComponent(missionContentLab);
    leftVL.setComponentAlignment(missionContentLab, Alignment.TOP_LEFT);
    leftVL.addStyleName("m-actionplan-mission-content-text");
    
    Label sp;
    leftVL.addComponent(sp=new Label());
    sp.setHeight("1px");
    leftVL.setExpandRatio(sp, 1.0f);
   
    VerticalLayout rightVL = getRightLayout();
    rightVL.setSpacing(true);
  //  rightVL.setWidth("99%");
  //  rightVL.setHeight("99%");
    Label lab;
    rightVL.addComponent(lab=new Label());
    lab.setHeight("15px");
    
    rightVL.addComponent(nonAuthorLabel = new Label("This is a space for plan authors to communicate."));
    nonAuthorLabel.setVisible(false);
    
    rightVL.addComponent(chatEntryComponent);
    chatEntryComponent.setWidth("100%");
    
    if (isGameMasterOrAdmin) {
      HorizontalLayout hl = new HorizontalLayout();
      rightVL.addComponent(hl);
      hl.setWidth("100%");

      hl.addComponent(sp=new Label());
      sp.setWidth("1px");
      hl.setExpandRatio(sp, 1.0f);
      
      viewAllButt = new ToggleLinkButton("View all", "View unhidden only", "m-actionplan-comment-text");
      viewAllButt.setToolTips("Temporarily show all messages, including those marked \"hidden\" (gm)", "Temporarily hide messages marked \"hidden\" (gm)");
      viewAllButt.addStyleName("m-actionplan-comments-button");
      viewAllButt.addOnListener(new ViewAllListener());
      viewAllButt.addOffListener(new ViewUnhiddenOnlyListener());
      hl.addComponent(viewAllButt);
      
      hl.addComponent(sp=new Label());
      sp.setWidth("8px");
    }
    Component comp=createChatScroller(rightVL);
    comp.setWidth("99%"); //"100%");
    rightVL.setExpandRatio(comp, 1.0f);
    comp.setHeight("99%");
    fillChatScroller();
  }
  
  @SuppressWarnings("serial")
  class ViewAllListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      showingHiddenMsgs = true;
      fillChatScroller();
    }   
  }
  
  @SuppressWarnings("serial")
  class ViewUnhiddenOnlyListener implements ClickListener
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      showingHiddenMsgs = false; 
      fillChatScroller();
    }    
  }
  
  private void fillChatScroller()
  {
    ((VerticalLayout)chatScroller.getContent()).removeAllComponents();
    
    ActionPlan ap = ActionPlan.get(apId);
    ChatLog log = ap.getChatLog();
    if (log != null) {// shouldn't have to do this, both log and messages should be set up to have the sets build by default
      SortedSet<Message> msgs = log.getMessages();
      if (msgs != null) // ditto
        for (Message m : msgs) {
          addMessageToScroller(m);
        }
    }
  }
  
  private void addMessageToScroller(Message m)
  {
    ChatMsg cm = new ChatMsg(m);
    ((VerticalLayout)chatScroller.getContent()).addComponent(cm);
    cm.setWidth("98%");
    handleVisible(cm);
  }
  
  private void handleVisible(ChatMsg cm)
  {
    boolean vis = showingHiddenMsgs || !cm.getMessage().isHidden();
    cm.setVisible(vis);
  }
  
  private void addMessageToScrollerTop(Message m, Session sess)
  {
    addMessageToScrollerAtPosition(m,0,sess);
  }
  
  private void addMessageToScrollerAtPosition(Message m, int pos, Session sess)
  {
    ChatMsg cm = new ChatMsg(m,sess);
    ((VerticalLayout)chatScroller.getContent()).addComponent(cm, pos); 
    cm.setWidth("98%");
    handleVisible(cm);
  }
  
  @SuppressWarnings("serial")
  class ChatMsg extends VerticalLayout
  {
    private Message msg;
    private CheckBox superInterestingCB;
    private Label textLabel;
    
    ChatMsg(Message m)
    {
      this(m,null);
    }
    ChatMsg(Message m, Session sess)
    {
      if(sess == null)
        sess = VHib.getVHSession();
      msg = m;
      setSpacing(true);
      setMargin(false);
      
      Object myId = Mmowgli2UI.getGlobals().getUserID();
      if (myId.equals(m.getFromUser().getId()))
        addStyleName("m-chatmessage-mine");
      else
        addStyleName("m-chatmessage");

      HorizontalLayout hl = new HorizontalLayout();
      hl.setWidth("99%");
      addComponent(hl);
      Label lab;
      hl.setMargin(false);
      hl.setSpacing(true);

      hl.addComponent(lab=new HtmlLabel("<b>"+m.getFromUser().getUserName()+"</b>"));
      lab.setSizeUndefined();
      hl.addComponent(lab=new HtmlLabel("<b>"+dateFormatter.format(m.getDateTime())+"</b>"));
      lab.setSizeUndefined();

      if (isGameMasterOrAdmin) {
        Label sp;
        hl.addComponent(sp=new Label());
        sp.setWidth("1px");
        hl.setExpandRatio(sp, 1.0f);

        hl.addComponent(superInterestingCB = new CheckBox("super-interesting"));
        superInterestingCB.setValue(msg.isSuperInteresting());
        superInterestingCB.addStyleName("m-actionplan-comment-superinteresting");
        superInterestingCB.setImmediate(true);
        superInterestingCB.addValueChangeListener(new SuperInterestingCheckBoxListener());
        superInterestingCB.setEnabled(!gameRO);
        hl.setComponentAlignment(superInterestingCB, Alignment.TOP_CENTER);
     
        final ToggleLinkButton tlb;
        hl.addComponent(tlb = new ToggleLinkButton("hide", "show", "m-actionplan-comment-text"));
        hl.setComponentAlignment(tlb, Alignment.MIDDLE_CENTER);
        tlb.setInitialState(!msg.isHidden());
        tlb.setEnabled(!gameRO);
        tlb.addOnListener(new Button.ClickListener()
        {         
          @Override
          public void buttonClick(ClickEvent event)
          {
            msg.setHidden(true);
            Message.update(msg);
            if(!showingHiddenMsgs)
              ChatMsg.this.setVisible(false); // hide
          }
        });
        tlb.addOffListener(new Button.ClickListener()
        {
          
          @Override
          public void buttonClick(ClickEvent event)
          {
            msg.setHidden(false);
            Message.update(msg);
            ChatMsg.this.setVisible(true); // show            
          }
        });
        tlb.setToolTips("Hide this message in this list", "Show this message in this list");
                
        NativeButton editButt = new NativeButton();
        editButt.setCaption("edit");
        editButt.setStyleName(BaseTheme.BUTTON_LINK);
        editButt.addStyleName("borderless");
        editButt.addStyleName("m-actionplan-comment-text");
        editButt.setDescription("Edit this text (game masters only)");

        editButt.addClickListener(new EditListener());
        editButt.setEnabled(!gameRO);

        editButt.setSizeUndefined();
        hl.addComponent(editButt);
        hl.setComponentAlignment(editButt, Alignment.MIDDLE_CENTER);       
      }
     
      Game g = (Game)sess.get(Game.class, 1L);
      String linkifiedString = MmowgliLinkInserter.insertLinksOob(m.getText(), g, sess);
      addComponent(textLabel = new HtmlLabel(linkifiedString));
    }
    
    class EditListener implements ClickListener
    {
      EditCardTextWindow w;
      @Override
      public void buttonClick(ClickEvent event)
      {
        EditCardTextWindow  w = new EditCardTextWindow(msg.getText(),Integer.MAX_VALUE);
        w.setCaption("Edit Chat Message");
        w.addCloseListener(new SaveTextListener());       
      }
      
      class SaveTextListener implements CloseListener
      {
        @Override
        public void windowClose(CloseEvent e)
        {         
          EditCardTextWindow w = (EditCardTextWindow)e.getWindow();
          if(w.results != null) {                   
            textLabel.setValue(MmowgliLinkInserter.insertLinks(w.results,null));
            msg.setText(w.results);
            Message.update(msg);
            User me = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID());
            GameEventLogger.chatTextEditted(me.getUserName(),apId,msg);
          }
        }
      }
    }

    public Message getMessage()
    {
      return msg;
    }
  
    class SuperInterestingCheckBoxListener implements ValueChangeListener
    {
      @Override
      public void valueChange(ValueChangeEvent event)
      {
        Boolean supInt = (Boolean) superInterestingCB.getValue();
        msg.setSuperInteresting(supInt);
        Message.update(msg);
        User me = DBGet.getUser(Mmowgli2UI.getGlobals());
        GameEventLogger.commentMarkedSuperInteresting(me.getUserName(),apId,msg,supInt);
      }
    }
  }

  private Component createChatScroller(VerticalLayout cont)
  {
    chatScroller = new Panel();
    cont.addComponent(chatScroller);
    VerticalLayout vl=new VerticalLayout();
    chatScroller.setContent(vl);
    vl.addStyleName("m-padding2");
    vl.setWidth("99%");  // the padding screws up the panel's width calc
    return chatScroller;
  }
  
  private Component createChatEntryField()
  {
    MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    boolean gameRO = globs.isGameReadOnly() || globs.isViewOnlyUser();
    
    HorizontalLayout hl = new HorizontalLayout();
    chatTextField = new TextArea();
    chatTextField.setRows(3);
    chatTextField.setWordwrap(true);
    chatTextField.setInputPrompt("Type here to chat, RETURN submits");
    hl.addComponent(chatTextField);
    chatTextField.setWidth("99%");
    hl.setExpandRatio(chatTextField, 1.0f);
    chatTextField.setReadOnly(gameRO);
    
    chatSubmitButt = new NativeButton();
    chatSubmitButt.setStyleName("m-submitButton");
    hl.addComponent(chatSubmitButt);
    chatSubmitButt.addClickListener(new ChatButtonListener());
    chatSubmitButt.setEnabled(!gameRO);
    return hl;
  }
  
  @SuppressWarnings("serial")
  class ChatButtonListener implements ClickListener
  {
    Message msg;

    @Override
    public void buttonClick(ClickEvent event)
    {
      String s = chatTextField.getValue().toString();
      if(s == null || s.length()<=0)
        return;
      User me = DBGet.getUser(Mmowgli2UI.getGlobals().getUserID());
      msg = new Message(s);
      
      if(!me.isGameMaster())
        buttonClick2(me);
      else
        getProxyAuthor(me,this,event.getButton());
    }
    
    private void buttonClick2(User proxAuth)
    {
      msg.setFromUser(proxAuth);
      Message.save(msg);  // persist
      ActionPlan ap = ActionPlan.get(apId);
      ChatLog log = ap.getChatLog();
      log.getMessages().add(msg);
      ChatLog.update(log); // persist
      
      addMessageToScrollerTop(msg,VHib.getVHSession());
      chatTextField.setValue("");
      chatTextField.focus();     
    }
  }

  @SuppressWarnings("serial")
  private void getProxyAuthor(final User me, final ChatButtonListener cbLis, Button butt)
  {
    ArrayList<User> meLis = new ArrayList<User>(1);
    meLis.add(me);
    
    final AddAuthorDialog dial = new AddAuthorDialog(meLis, true);
    dial.infoLabel.setValue("As administrator, you may choose another player to be comment author.");
    dial.setCaption("Select Proxy Author");
    dial.setMultiSelect(false);
    dial.cancelButt.setCaption("Use myself");
    dial.addButt.setCaption("Use selected");
    dial.selectItemAt(0);
    dial.addListener(new CloseListener()
    {
      @Override
      public void windowClose(CloseEvent e)
      {
        User u = me;
        if (dial.addClicked) {
          Object o = dial.getSelected();
          if (o instanceof User) {
            u = (User) o;
          }
          else if (o instanceof QuickUser) {
            QuickUser qu = (QuickUser) o;
            u = DBGet.getUserFresh(qu.id);
          }
        }
        cbLis.buttonClick2(u);
      }
    });

    UI.getCurrent().addWindow(dial);
    dial.center();
  }
  
  /* return true if need gui update */
  @Override
  public boolean logUpdated_oob(SingleSessionManager mgr, Serializable chatLogId)
  {
    Session session = M.getSession(mgr);
    // This comes in on MY updates too, should do nothing since my just-entered msg is already displayed
    ActionPlan ap = (ActionPlan)session.get(ActionPlan.class,(Serializable)apId);
    ChatLog log = ap.getChatLog();
    if(chatLogId.equals(log.getId())) {

      // somebody else updated this log.
      // try not to load everything.  All lists should be in order
      SortedSet<Message> msgs = log.getMessages();
      if(msgs.size()<=0)
        return false;
      VerticalLayout lay = (VerticalLayout)chatScroller.getContent();
           
      Message topMsg = null;
      if(lay.getComponentCount() > 0)
        topMsg = ((ChatMsg)lay.getComponent(0)).getMessage();
      
      int pos=0;
      for(Iterator<Message> itr=msgs.iterator();itr.hasNext();) {
        Message m = itr.next();
        if(topMsg != null && m.getId() == topMsg.getId())
          break;
        addMessageToScrollerAtPosition(m,pos++,session);
      } 
      return true;
    }
    return false;
  }

  @Override
  public boolean actionPlanUpdatedOob(SessionManager sessMgr, Serializable apId)
  { 
    return false;
  }
  
  boolean author = false;
  boolean gm = false;
  
  public void setICanChat(boolean yn)
  {
    submitButt.setEnabled(yn);
    discardButt.setEnabled(yn);
    chatEntryComponent.setVisible(yn);
    chatScroller.setVisible(yn || isGameMasterOrAdmin);
    nonAuthorLabel.setVisible(!yn);
    if(viewAllButt != null)
      viewAllButt.setVisible(yn);
  }
  
  @Override
  public void setImAuthor(boolean yn)
  {
    author = yn;
  }
  
  public void setImGM(boolean yn)
  {
    gm = yn;
  }  
}