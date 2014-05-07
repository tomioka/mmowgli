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
package edu.nps.moves.mmowgli.modules.administration;

import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer;

import edu.nps.moves.mmowgli.db.Move;
import edu.nps.moves.mmowgli.db.MovePhase;

/**
 * NewMovePhaseDialog.java Created on Apr 2, 2013
 * Updated on Mar 12, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class NewMovePhaseDialog extends Window
{
  private static final long serialVersionUID = -3543980329987811294L;
  
  private Move moveBeingEdited;
  private ComboBox descriptionCB;
  private String[] strings = { "PREPARATION", "PLAY", "PUBLISH" };
  private GridLayout existingPhasesGLay;
  Button saveButt,cancelButt;
  
  @SuppressWarnings("serial")
  public NewMovePhaseDialog(Move move)
  {
    super("Make a new Phase for " + move.getName());
    this.moveBeingEdited = Move.get(move.getId());
    setSizeUndefined();
    setWidth("390px");
    VerticalLayout vLay;
    setContent(vLay = new VerticalLayout());
    vLay.setSpacing(true);
    vLay.setMargin(true);
    vLay.addComponent(new Label("Choose a descriptive name for this phase.  Suggested names are shown in the " +
    		"drop down list, but any text is permitted."));

    descriptionCB = new ComboBox("Phase description");
    vLay.addComponent(descriptionCB);
    fillCombo(descriptionCB);
    
    descriptionCB.setInputPrompt("Choose suggested description, or enter text");
    descriptionCB.setWidth("350px");

    descriptionCB.setImmediate(true);
    descriptionCB.setNullSelectionAllowed(false);
    descriptionCB.setTextInputAllowed(true);
    descriptionCB.addValueChangeListener(new ValueChangeListener()
    {
      @Override
      public void valueChange(final ValueChangeEvent event)
      {
        // final String valueString = String.valueOf(event.getProperty().getValue());
        // Notification.show("Value changed:", valueString,
        // Type.TRAY_NOTIFICATION);
      }
    });
    vLay.addComponent(existingPhasesGLay = new GridLayout());
    existingPhasesGLay.setColumns(3);
    existingPhasesGLay.setRows(1); // to start
    existingPhasesGLay.setSpacing(true);
    existingPhasesGLay.addStyleName("m-greyborder");
    existingPhasesGLay.setCaption("Existing Phases");
    fillExistingPhases();
    
    HorizontalLayout buttHLay;
    vLay.addComponent(buttHLay= new HorizontalLayout());
    buttHLay.setWidth("100%");
    buttHLay.setSpacing(true);
    Label lab;
    buttHLay.addComponent(lab=new Label());
    lab.setWidth("1px");
    buttHLay.setExpandRatio(lab, 1.0f);
    buttHLay.addComponent(cancelButt = new Button("Cancel",saveCancelListener));
    buttHLay.addComponent(saveButt = new Button("Save",saveCancelListener));
  }
  
  class PhaseDeleteButt extends Button
  {
    private static final long serialVersionUID = 1L;
    public MovePhase movePhase;
    public PhaseDeleteButt(MovePhase mp)
    {
      super("delete",deleteListener);
      movePhase = mp;
    }
  }
  
  @SuppressWarnings("serial")
  ClickListener saveCancelListener = new ClickListener()
  {
    @Override
    public void buttonClick(ClickEvent event)
    { 
      if(event.getButton() == saveButt) {
        Object obj = descriptionCB.getValue();
        if(obj == null || obj.toString().length()<=0) {
          Notification.show("Error","You must enter a phase description", Notification.Type.ERROR_MESSAGE);
          return;         
        }
        Move thisMove = Move.get(moveBeingEdited.getId());
        MovePhase existing = thisMove.getCurrentMovePhase();
        MovePhase mp = new MovePhase();
        mp.cloneFrom(existing);
        mp.setDescription(obj.toString());
        MovePhase.save(mp);
        
        List<MovePhase> lis = thisMove.getMovePhases();
        lis.add(mp);
        Move.update(thisMove);
      }
      
      UI.getCurrent().removeWindow(NewMovePhaseDialog.this);
    }   
  };
  
  @SuppressWarnings("serial")
  ClickListener deleteListener = new ClickListener()
  {
    private MovePhase mp;
    private Move thisMove;
    @Override
    public void buttonClick(ClickEvent event)
    {
      thisMove = Move.get(moveBeingEdited.getId());
      int numPhases = thisMove.getMovePhases().size();
      if (numPhases <= 1) {
        Notification.show("Error","A round must contain at least one phase", Notification.Type.ERROR_MESSAGE);
        return;
      }

      mp = ((PhaseDeleteButt) event.getButton()).movePhase;
      if (thisMove.getCurrentMovePhase().getId() == mp.getId()) {
        Notification.show("Error","You cannot delete the active phase of a round", Notification.Type.ERROR_MESSAGE);
        return;
      }

      ConfirmDialog.show(UI.getCurrent(), "Please confirm phase delete:", "Are you really sure?", "Yes", "Never mind", confLis);
    }

    ConfirmDialog.Listener confLis = new ConfirmDialog.Listener()
    {
      public void onClose(ConfirmDialog dialog)
      {
        if (!dialog.isConfirmed())
          return;
        thisMove = Move.merge(thisMove);
        mp = MovePhase.merge(mp);
        thisMove.getMovePhases().remove(mp);
        Move.update(thisMove);
        MovePhase.delete(mp);
        fillExistingPhases();
      }
    };
  };
  
  private void fillExistingPhases()
  {
    existingPhasesGLay.removeAllComponents();
    List<MovePhase> lis = moveBeingEdited.getMovePhases();
    int i = 1;
    for(MovePhase mp : lis) {
      existingPhasesGLay.addComponent(new Label(""+i++));
      existingPhasesGLay.addComponent(new Label(mp.getDescription()));
      Button butt;
      existingPhasesGLay.addComponent(butt=new PhaseDeleteButt(mp));
      butt.addStyleName(Reindeer.BUTTON_SMALL);
    }   
  }
  private void fillCombo(ComboBox box)
  {
    for (String s : strings)
      box.addItem(s);
  }

  public void showDialog()
  {
    UI.getCurrent().addWindow(this);
    center();
  }
}