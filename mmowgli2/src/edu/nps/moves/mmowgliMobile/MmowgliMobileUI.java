/* 
 * Copyright 2009 IT Mill Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package edu.nps.moves.mmowgliMobile;

import com.vaadin.annotations.*;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

import edu.nps.moves.mmowgliMobile.ui.SigninPopover;
import edu.nps.moves.mmowgliMobile.ui.SmartphoneMainView;

//One or more of the following interferes with the session interceptor design of the hibernate container
//@Push(PushMode.AUTOMATIC)
//@PreserveOnRefresh

@SuppressWarnings("serial")
@Theme("mmowglimobile")
@Title("mmowgli")
@Widgetset("edu.nps.moves.mmowgliMobile.gwt.MmowgliMobileWidgetSet")

public class MmowgliMobileUI extends UI
{
  private SigninPopover signinPopover = new SigninPopover();
 
  public MmowgliMobileUI()
  {
  }

  @Override
  protected void init(VaadinRequest request)
  {
    System.out.println("mmowgli mobile UI init");
    setContent(new SmartphoneMainView());
    setImmediate(true);
 /* this adds signin:    
    if(signinPopover != null) {
      addWindow(signinPopover);
      signinPopover.center();
    } */
  }

}