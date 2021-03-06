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

package edu.nps.moves.mmowgli.utility;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliEvent;


/**
 * @author Mike Bailey, jmbailey@nps.edu
 *
 * @version	$Id$
 * @since $Date$
 * @copyright	Copyright (C) 2011
 */

public class IDButton extends Button implements IDButtonIF, ClickListener
{
  private static final long serialVersionUID = -3676056694116020140L;
  
  MmowgliEvent mEv;
  Object param;
  private boolean locallyEnabled=true;
  
  public IDButton(String label, MmowgliEvent mEv, Object param)
  {
    super(label);
    this.mEv = mEv;
    this.param = param;
  }
  
  public IDButton(String label, MmowgliEvent mEv)
  {
    this(label,mEv,null);
  }
  
  @Override
  public MmowgliEvent getEvent()
  {
    return mEv;
  }
  
  @Override
  public Object getParam()
  {
    return param;
  }
  
  @Override
  public void setParam(Object param)
  {
    this.param = param;
  }

  @Override
  public void setEvent(MmowgliEvent mEv)
  {
	  this.mEv = mEv;	  
  }
  
  @Override
  public void attach()
  {
    super.attach();
    addClickListener((ClickListener)this);
  }

  @Override
  public void buttonClick(ClickEvent event)
  {
    if(locallyEnabled) 
      Mmowgli2UI.getGlobals().getController().buttonClick(event);
  }
  
  public void enableAction(boolean yn)
  {
    locallyEnabled = yn;
  }  
}