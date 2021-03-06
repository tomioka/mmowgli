/*
  Copyright (C) 2010-2014 Modeling Virtual Environments and Simulation
  (MOVES) Institute at the Naval Postgraduate School (NPS)
  http://www.MovesInstitute.org and http://www.nps.edu
 
  This file is part of Mmowgli.
  
  Mmowgli is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  Mmowgli is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with Mmowgli in the form of a file named COPYING.  If not,
  see <http://www.gnu.org/licenses/>
*/

package edu.nps.moves.mmowgli.modules.cards;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Vector;

import org.hibernate.Session;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliSessionGlobals;
import edu.nps.moves.mmowgli.components.CardSummary;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateClosed;
import edu.nps.moves.mmowgli.markers.HibernateOpened;
import edu.nps.moves.mmowgli.markers.MmowgliCodeEntry;

/**
 * HorizontalCardDisplay.java
 * Created on Feb 17, 2013
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class HorizontalCardDisplay extends VerticalLayout implements MmowgliComponent
{
  private static final long serialVersionUID = 9018189749369961998L;
  private int componentWidth;
  private int componentHeight;
  private int numVisible;
  private ArrayList<Object> cardIds = new ArrayList<Object>();
  private Vector<CardSummary> displayedCards = new Vector<CardSummary>();
  
  private ClickListener startLis,leftLis,rightLis,endLis;
  private User me;
  private boolean mockupOnly = false;
  private HorizontalLayout cardHL;
  private HorizontalLayout outerCardHL;
  int leftIndex = -1;
  int myWidth;
  private String PANELSTATEKEY;
  Button start, left, right, end;
  
  public HorizontalCardDisplay(Dimension componentSize, int numVisible, User me, boolean mockupOnly, String key)
  {
    componentWidth = componentSize.width;
    componentHeight = componentSize.height;
    
    outerCardHL = new HorizontalLayout();
    outerCardHL.setMargin(false);
    outerCardHL.setSpacing(false);
    
    this.numVisible = numVisible;
    this.me = me;
    this.mockupOnly = mockupOnly;
    setMargin(false);
    setSpacing(true);
    
    startLis = new ButtListener(ListType.START);
    leftLis  = new ButtListener(ListType.LEFT);
    rightLis = new ButtListener(ListType.RIGHT);
    endLis   = new ButtListener(ListType.END);
    
    PANELSTATEKEY = getClass().getName()+key;
  }

  @Override
  public void initGui()
  {
    myWidth = componentWidth*numVisible;
    cardHL = makeCardHL(""+componentHeight+"px",true,false);
    outerCardHL.addComponent(cardHL);
    addComponent(outerCardHL);
    setComponentAlignment(outerCardHL,Alignment.TOP_CENTER);
    ButtonBar bb = new ButtonBar();
    addComponent(bb);
    setComponentAlignment(bb,Alignment.TOP_CENTER);
    Object o = Mmowgli2UI.getGlobals().getPanelState(PANELSTATEKEY);
    if(o != null)
      leftIndex = (Integer)o;
  }
  
  @SuppressWarnings("serial")
  class ButtonBar extends HorizontalLayout
  {
    ButtonBar()
    {
      setWidth(""+(myWidth-20)+"px");
      setSpacing(true);
      Label sp;
      addComponent(sp=new Label());
      sp.setWidth("10px");
      
      Label lab;   
      addComponent(lab=new Label("earliest"));
      lab.setSizeUndefined();
      lab.addStyleName("m-playidea-help-text");
       
      addComponent(sp=new Label());
      sp.setWidth("1px");
      setExpandRatio(sp,.5f);
      
      start = new NativeButton(null,startLis);
      start.setImmediate(true);
      start.addStyleName("m-vcr-fonticon");
      start.setDescription("show earliest cards");
      start.setHtmlContentAllowed(true);
      start.setIcon(VaadinIcons.ANGLE_DOUBLE_LEFT);
      
      addComponent(start);
    
      left = new NativeButton(null,leftLis);
      left.setImmediate(true);
      left.addStyleName("m-vcr-fonticon");
      left.setDescription("show earlier cards");
      left.setHtmlContentAllowed(true);
      left.setIcon(VaadinIcons.ANGLE_LEFT);
      
      addComponent(left);
      
      addComponent(sp=new Label());
      sp.setWidth("35px");
      
      right = new NativeButton(null,rightLis);
      right.setImmediate(true);
      right.addStyleName("m-vcr-fonticon");
      right.setDescription("show newer cards");
      right.setHtmlContentAllowed(true);
      right.setIcon(VaadinIcons.ANGLE_RIGHT);
      addComponent(right);
      
      end = new NativeButton(null,endLis);
      end.setImmediate(true);
      end.addStyleName("m-vcr-fonticon");
      end.setDescription("show newest cards");
      end.setHtmlContentAllowed(true);
      end.setIcon(VaadinIcons.ANGLE_DOUBLE_RIGHT);
      addComponent(end);
      
      addComponent(sp=new Label());
      sp.setWidth("1px");
      setExpandRatio(sp,.5f); 
      
      addComponent(lab=new Label("newest"));
      lab.setSizeUndefined();
      lab.addStyleName("m-playidea-help-text");   
    }   
  }
  
  private HorizontalLayout makeCardHL(String height, boolean fromLeft, boolean animated)
  {
    HorizontalLayout hl = new HorizontalLayout();
    hl.setMargin(false);
    hl.setSpacing(false);
    hl.setHeight(height);
    if(animated) {
      hl.addStyleName("animated");
      hl.addStyleName(fromLeft?"fadeIn":"fadeIn");  // can be other styles, e.g., fadeInLeft, fadeInRight
    }
    return hl;
  } 
  
  public void loadWrappers(ArrayList<Object>cardIds)
  {
    this.cardIds = cardIds;
  }
   
  public void addScrollee(Object cardId)
  {
    cardIds.add(cardId);
    showHideVcrButtons();
  }

  public void show(Session sess)
  {
    if(leftIndex != -1)
      showLeftSideTL(leftIndex,sess,false);
    else
      showEnd(sess,false);      
  }
 
  public void showEnd(Session sess)
  {
    showEnd(sess,false);
  }
  private void showEnd(Session sess, boolean animated)
  {
    showRightSideTL(cardIds.size()-1, sess,animated); //0 based index, so -1       
  }
  public void showStart(Session sess)
  {
    showStart(sess,false);
  }
  private void showStart(Session sess, boolean animated)
  {
    showLeftSideTL(0, sess, animated);  
  }
  
  /*
   *  display 4 (default) cards with the specified one being as far right as possible (don't think that's right...recompute
   */
  private void showRightSideTL(int idx, Session sess, boolean animated)
  {
    CardSummary[] arr = new CardSummary[numVisible];
    int i =0;
    
    int start = Math.min(idx, cardIds.size()-numVisible); // make sure it's filled
    start = Math.max(0, start);  // don't go below 0
    MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    
    while(start < cardIds.size() && i < numVisible) {
      arr[i] = CardSummary.newCardSummary((Long)cardIds.get(start), sess ,me, mockupOnly);
      if(i++ == 0) {
        leftIndex = start;
        globs.setPanelState(PANELSTATEKEY,leftIndex);
      }
      start++;
    }
    fillDisplayTL(arr,false, animated);    
  }
  
  private void showLeftSideTL(int idx, Session sess, boolean animated)
  {
    CardSummary[] arr = new CardSummary[numVisible];
    int i=0;
    MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    
    while(idx < cardIds.size() && i < numVisible) {
      arr[i] = CardSummary.newCardSummary((Long)cardIds.get(idx), sess ,me, mockupOnly);
      if(i++ == 0) {
        leftIndex = idx;
        globs.setPanelState(PANELSTATEKEY, leftIndex);
      }
      idx++;
    }
    fillDisplayTL(arr,true,animated);
  }
  
  private void fillDisplayTL(CardSummary[] cards, boolean fromLeft, boolean animated)
  {
 //   for(CardSummary cs : displayedCards)
 //     cardHL.removeComponent(cs);
    
    displayedCards.clear();
    
    cardHL = makeCardHL(""+componentHeight+"px",fromLeft,animated);
    int start = 0;
    for(CardSummary cs : cards)
      if(cs != null) {
        cardHL.addComponent(cs, start++);
        cs.initGui();
        displayedCards.add(cs);
      }
    
    outerCardHL.removeAllComponents();
    outerCardHL.addComponent(cardHL);
    showHideVcrButtons();
   }
  
  private void showHideVcrButtons()
  {
    buttonsEnabledSet(true,true,true,true);

    if(cardIds.size() <= numVisible) {
      buttonsEnabledSet(false,false,false,false);
      return;
    }
    if(leftIndex <= 0)
      buttonsEnabledSet(false,false,null,null);
    if(leftIndex >= (cardIds.size()-numVisible))
      buttonsEnabledSet(null,null,false,false); 
  }
  
  private void buttonsEnabledSet(Boolean startb, Boolean leftb, Boolean rightb, Boolean endb)
  {
    if(startb != null) start.setEnabled(startb);
    if(leftb != null)  left.setEnabled(leftb);
    if(rightb != null) right.setEnabled(rightb);
    if(endb != null) end.setEnabled(endb);
  }
  
  enum ListType {START,LEFT,RIGHT,END};
  
  @SuppressWarnings("serial")
  class ButtListener implements ClickListener
  {
    private ListType typ;
    ButtListener(ListType typ)
    {
      this.typ = typ;
    }
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      
      Session sess = HSess.get();
      switch(typ) {
      case START:
        showStart(sess,true);
        break;
      case LEFT:
        showLeftSideTL(Math.max(leftIndex-numVisible,0),sess,true);
        break;
      case RIGHT:
        showRightSideTL(Math.min(leftIndex+numVisible, cardIds.size()-1),sess,true);
        break;
      case END:
        showEnd(sess,true);
        break;
      }
      HSess.close();
    }
  }
}
