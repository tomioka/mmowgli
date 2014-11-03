package edu.nps.moves.mmowgliMobile.unused;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgliMobile.data.*;

/**
 * MessageListRenderer.java Created on Feb 26, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public abstract class ListEntryRenderer
{
  public final String NORMAL_STYLE_NAME = "m-messageList-normal";
  public final String ITALIC_STYLE_NAME = "m-messageList-italics";
  public final String BOLD_STYLE_NAME = "m-messageList-bold";
  public final String ITALIC_BOLD_STYLE_NAME = "m-messageList-italic-bold";
  
  abstract public void renderEntry(ListEntry msg, ListView messageList, CssLayout layout);

  protected SimpleDateFormat formatter = new SimpleDateFormat("M/d/yy hh:mm");
  protected StringBuilder sb = new StringBuilder();
  
  protected Serializable getPojoId(ListEntry msg)
  {
    if (msg instanceof CardListEntry)
      return ((CardListEntry) msg).getCard().getId();
    if (msg instanceof UserListEntry)
      return ((UserListEntry) msg).getUser().getId();
    if (msg instanceof ActionPlanListEntry)
      return ((ActionPlanListEntry) msg).getActionPlan().getId();
    return null;
  }

  private void _text(String styleName, StringBuilder sb, Object ... strs)
  {
    sb.append("<span class='");
    sb.append(styleName);
    sb.append("'>");
    for(Object s : strs)
      sb.append(s.toString());
    sb.append("</span>");   
  }
  
  protected void styledText(String style, StringBuilder sb, Object ... strs)
  {
    _text(style,sb,strs);
  }
  protected void italicText(StringBuilder sb, Object ... strs)
  {
    _text(ITALIC_STYLE_NAME,sb,strs);
  }
  protected void italicBoldText(StringBuilder sb, Object ... strs)
  {
    _text(ITALIC_BOLD_STYLE_NAME,sb,strs);
  }
  protected void normalText(StringBuilder sb, Object ... strs)
  {
    _text(NORMAL_STYLE_NAME,sb,strs);
  }
  protected void boldText(StringBuilder sb, Object ... strs)
  {
    _text(BOLD_STYLE_NAME,sb,strs);
  }
  protected void text(StringBuilder sb, Object ...strs)
  {
    normalText(sb,strs);
  }
  private static UserListEntryRenderer u_rend = null;
  private static ActionPlanListEntryRenderer ap_rend = null;
  private static CardListEntryRenderer c_rend = null;

  public static UserListEntryRenderer u()
  {
    if (u_rend == null)
      u_rend = new UserListEntryRenderer();
    return u_rend;
  }
  public static ActionPlanListEntryRenderer ap()
  {
    if(ap_rend == null)
      ap_rend = new ActionPlanListEntryRenderer();
    return ap_rend;
  }
  public static CardListEntryRenderer c()
  {
    if(c_rend == null)
      c_rend = new CardListEntryRenderer();
    return c_rend;
  }
  
  public static class CardListEntryRenderer extends ListEntryRenderer
  {
    private CardListEntryRenderer(){}
    @Override
    public void renderEntry(ListEntry message, ListView messageList, CssLayout layout)
    {
      CardListEntry wc = (CardListEntry) message;
      Card c = wc.getCard();
      layout.removeAllComponents();      
      sb.setLength(0);
      
      boldText(sb,c.getId());
      sb.append(". ");
      normalText(sb,c.getText());
      sb.append(" ");
      italicBoldText(sb,c.getAuthorName());
      
      layout.addComponent(new Label(sb.toString(),ContentMode.HTML));
    }
  }

  public static class ActionPlanListEntryRenderer extends ListEntryRenderer
  {
    private ActionPlanListEntryRenderer(){}
    @Override
    public void renderEntry(ListEntry entry, ListView messageList, CssLayout layout)
    {
      ActionPlanListEntry wap = (ActionPlanListEntry) entry;
      ActionPlan ap = wap.getActionPlan();
      layout.removeAllComponents();      
      sb.setLength(0);
      
      boldText(sb,ap.getId());
      sb.append(". ");
      normalText(sb,formatter.format(ap.getCreationDate()));
      sb.append(", ");
      String s = ap.getQuickAuthorList().replaceAll(",",", "); // add space
      styledText("m-actionplan-text-authors",sb,s);// italicBoldText(sb,s);
      sb.append(". ");
      normalText(sb,ap.getTitle());
      
      layout.addComponent(new Label(sb.toString(),ContentMode.HTML));
    }
  }

  public static class UserListEntryRenderer extends ListEntryRenderer
  {
    private UserListEntryRenderer(){}
    @Override
    public void renderEntry(ListEntry msg, ListView messageList, CssLayout layout)
    {
      UserListEntry wu = (UserListEntry) msg;
      User u = wu.getUser();
      layout.removeAllComponents();      
      sb.setLength(0);
      
      italicBoldText(sb,u.getUserName());
      sb.append(", ");
      boldText(sb,u.getId());
      sb.append(". ");
      
      normalText(sb,u.getAffiliation());
      sb.append(", ");
      italicText(sb,u.getLocation());
      sb.append(", ");
      boldText(sb,u.getBasicScore());
      sb.append("/");
      boldText(sb,u.getInnovationScore());
      
      layout.addComponent(new Label(sb.toString(),ContentMode.HTML));
    }
  }
}