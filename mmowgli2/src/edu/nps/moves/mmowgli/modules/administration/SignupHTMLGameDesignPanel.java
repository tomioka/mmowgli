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

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer;

import edu.nps.moves.mmowgli.components.WebContentDisplayer;
import edu.nps.moves.mmowgli.db.MovePhase;

/**
 * HeaderFooterGameDesignPanel.java
 * Created on Mar 28, 2013
 * Updated on Mar 12, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class SignupHTMLGameDesignPanel extends AbstractGameBuilderPanel implements MovePhaseChangeListener
{
  private static final long serialVersionUID = -5658127898958076712L;
  
  public static final String SIGNUPPAGEHEADERIMAGE_DEFAULT = "mmowgli_logo_final.png";

  //private MovePhase activePhase;
  private SignupImageTextFieldWithDefaultButton imgComponent;
  public SignupHTMLGameDesignPanel(MovePhase phase, AuxiliaryChangeListener auxLis, GameDesignGlobals globs)
  {
    super(false,globs);
    //activePhase = phase;
    addEditComponent("Signup page header image", "MovePhase.signupHeaderImage", imgComponent = new SignupImageTextFieldWithDefaultButton(phase,globs));
    
    EditLine edLine = addEditLine("Signup page HTML", "MovePhase.signupText", phase, phase.getId(), "SignupText");
    TextArea ta = (TextArea)edLine.ta;
    ta.setRows(25);
    edLine.auxListener = auxLis;
    
    addThirdColumnComponent(makeButton("Show signup HTML",new RenderListener(ta)));
  }
  
  private AbstractComponent makeButton(String s, ClickListener lis)
  {
    HorizontalLayout hl = new HorizontalLayout();
    hl.setWidth("100%");
    Button b;
    hl.addComponent(b=new Button(s,lis));
    hl.setComponentAlignment(b, Alignment.MIDDLE_CENTER);
    return hl;
  }
  
  class RenderListener implements ClickListener
  {
    private static final long serialVersionUID = 1L;
    TextArea ta;
    RenderListener(TextArea ta)
    {
      this.ta = ta;
    }
    @Override
    public void buttonClick(ClickEvent event)
    {
      new WebContentDisplayer(ta.getValue().toString()).show(SignupHTMLGameDesignPanel.this,"500px","400px","Signup HTML");
    }
  }
 
  @Override
  Embedded getImage()
  {
    return null;
  }

  @Override
  public void movePhaseChanged(MovePhase newPhase)
  {
    //activePhase = newPhase;
    okToUpdateDbFlag = false; 
    changeMovePhase(newPhase); 
    imgComponent.movePhaseChanged(newPhase);
    okToUpdateDbFlag = true; 
  }
  
  @Override
  protected int getColumn1PixelWidth()
  {
    return super.getColumn1PixelWidth() + 55; // default = 80
  }

  @Override
  protected int getColumn2PixelWidth()
  {
    return super.getColumn2PixelWidth() - 55; // default = 240
  }
  
  class SignupImageTextFieldWithDefaultButton extends HorizontalLayout implements ClickListener, ValueChangeListener
  {
    private static final long serialVersionUID = 1L;
    
    TextField tf;
    Button defaultButt;
    MovePhase phase;
    boolean nocommit = false;
    
    public SignupImageTextFieldWithDefaultButton(MovePhase phase, GameDesignGlobals globs)
    {
      this.phase = phase;
      setSpacing(true);
      setMargin(false);
      setSizeUndefined();

      addComponent(tf = new TextField());
      tf.setColumns(30);
      String val = phase.getSignupHeaderImage();
      if(val == null || val.trim().length()<=0)
        val = "";
      tf.setValue(val);
      tf.setReadOnly(globs.readOnlyCheck(false));
      tf.setDescription("Name of image in mmowgli repository which will be displayed at a size of 400 pixels wide by 114 pixels high.  Enter blank for no display.");
      tf.addValueChangeListener(this);
      addComponent(defaultButt=new Button("set to default",this));
      defaultButt.setDescription("Set the signup page header image to the default (mmowgli name and logo)");
      defaultButt.addStyleName(Reindeer.BUTTON_SMALL);
      setComponentAlignment(defaultButt, Alignment.MIDDLE_CENTER);
      defaultButt.setReadOnly(globs.readOnlyCheck(false));
      defaultButt.setEnabled(!defaultButt.isReadOnly());
      if(!defaultButt.isReadOnly())
        defaultButt.addClickListener((ClickListener)this);
    }
    
    @Override
    public void buttonClick(ClickEvent event)
    {
      boolean origRO = tf.isReadOnly();
      tf.setReadOnly(false);
      tf.setValue(SIGNUPPAGEHEADERIMAGE_DEFAULT);
      tf.setReadOnly(origRO);
    }
    
    public void movePhaseChanged(MovePhase phase)
    {
      this.phase = phase;
      nocommit=true;
      boolean origRO = tf.isReadOnly();
      tf.setReadOnly(false);
      String val = phase.getSignupHeaderImage();
      if(val == null || val.trim().length()<=0)
        val = "";
      tf.setValue(val);
      tf.setReadOnly(origRO);
      nocommit = false;
    }

    @Override
    public void valueChange(ValueChangeEvent event)
    {
      System.out.println("valuechange");
      if(!nocommit) {
        System.out.println("valuechange commit");
        String val = event.getProperty().toString();
        if(val != null && val.trim().length()<=0)
          val = null;
        phase = MovePhase.merge(phase);
        phase.setSignupHeaderImage(val);
        MovePhase.update(phase);
      }
    }
  }
}