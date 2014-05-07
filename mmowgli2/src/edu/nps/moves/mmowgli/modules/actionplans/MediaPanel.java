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

import org.hibernate.Session;

import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.HtmlLabel;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.Media;
import edu.nps.moves.mmowgli.db.Media.MediaType;
import edu.nps.moves.mmowgli.hibernate.DBGet;
import edu.nps.moves.mmowgli.hibernate.SessionManager;
import edu.nps.moves.mmowgli.utility.M;
import edu.nps.moves.mmowgli.utility.MediaLocator;

/**
 * MediaPanel.java
 * Created on Mar 9, 2012
 * Updated on Mar 14, 2014
 * 
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class MediaPanel extends VerticalLayout implements MmowgliComponent
{
  private static final long serialVersionUID = -1414358432821912217L;
  
  //private String HEIGHT = "310px";
  //private String HEIGHTPLUS = "344px"; //with open save/cancel panel
  public static String WIDTH = "309px";
  private String PLAYER_HEIGHT = "207px"; 
  private int PLAYERINDEX_IN_LAYOUT = 1;
  
  NativeButton zoom;
  TextArea caption;
  TextField title;
  HorizontalLayout titleHL;
  Label indexLab;
  Media m;
  ClickListener scaler;
  Button canButt, saveButt;
  HorizontalLayout captionSavePan;
  Object apId;
  int idx = -1;
  Embedded image;
  Component mediaPlayer;
  Label placeHolder;

  private boolean titleFocused=false;
  private boolean captionFocused = false;
  
  MediaPanel(Media m, Object apId, int idx, ClickListener replaceListener)
  {
    this.idx = idx;
    this.m = m;
    
    zoom = new NativeButton();
    caption = new TextArea();
    caption.setInputPrompt("Description");
    title = new TextField();
    title.setInputPrompt("Title");
    titleHL = new HorizontalLayout();
    indexLab = new HtmlLabel("");
    
    FocusHandler fHandler = new FocusHandler();
    caption.addFocusListener((FocusListener)fHandler);
    //caption.addListener((BlurListener)fHandler);
    title.addFocusListener((FocusListener)fHandler);
    //title.addListener((BlurListener)fHandler);
     
    captionSavePan = new HorizontalLayout();
    captionSavePan.setSpacing(true);
    captionSavePan.setMargin(false);
    Label lab;
    captionSavePan.addComponent(lab = new Label());
    lab.setWidth("1px");
    captionSavePan.setExpandRatio(lab, 1.0f);
    canButt = new Button("Cancel");
    captionSavePan.addComponent(canButt);
    canButt.setStyleName(Reindeer.BUTTON_SMALL);
    canButt.addClickListener((ClickListener)fHandler);
    saveButt = new Button("Save");
    captionSavePan.addComponent(saveButt);
    saveButt.setStyleName(Reindeer.BUTTON_SMALL);
    saveButt.addStyleName("m-redbutton");
    saveButt.addClickListener((ClickListener)fHandler);
    captionSavePan.addComponent(lab=new Label());
    lab.setWidth("5px");
  }
  
  public void setIndex(int i)
  {
    indexLab.setValue("<b style='font-size:150%'>"+i+"</b>");    
  }
  
  class FocusHandler implements FocusListener,ClickListener //BlurListener,
  {
    private static final long serialVersionUID = -5412529699678903650L;

    @Override
    public void focus(FocusEvent event)
    {
      String s = "";
      if(event.getSource() == caption) {
        if(caption.isReadOnly())
          return; 
        caption.selectAll();
        s = "caption ";
        captionFocused=true;
      }
      else if(event.getSource() == title) {
        if(title.isReadOnly())
          return;
        title.selectAll();
        s = "title ";
        titleFocused=true;
      }
      captionSavePan.setVisible(true);
      String substring = m.getType()==MediaType.IMAGE?" is editing image "+s+"number ":" is editing video "+s+"number ";
      sendStartEditMessage( DBGet.getUser(Mmowgli2UI.getGlobals().getUserID()).getUserName()+substring+(idx+1));
    }
   
    @Override
    public void buttonClick(ClickEvent event)
    {
      if(event.getSource()  == canButt) {
        m = Media.get(m.getId());  // might have changed under us, and we don't update fields being edited
        setValueIfNonNull(caption,m.getDescription());
        setValueIfNonNull(title,m.getTitle());
      }
      else { // Save
        m.setDescription(nullOrString(caption.getValue()));
        m.setTitle(nullOrString(title.getValue()));
        Media.update(m);
      }
      captionSavePan.setVisible(false);
      titleFocused = false;
      captionFocused = false;
    }     
  }
  public void setIdx(int idx)
  {
    this.idx = idx;
  }
  private String nullOrString(Object o)
  {
    if(o == null)
      return null;
    return o.toString();
  }
  public void setReadOnly(boolean ro)
  {
    caption.setReadOnly(ro);
    title.setReadOnly(ro);
  }
  public void setMedia(Media m)
  {
    this.m = m;
    if(MediaType.IMAGE == m.getType())
      setImageMedia(m);
    else if(MediaType.YOUTUBE == m.getType())
      setVideoMedia(m);
  }
  
  public void mediaUpdatedOob(SessionManager sMgr)
  {
    Media oobM = getOobMedia(sMgr,null);
    if(oobM == null)  // may be null if image removed
      return;
    if(!titleFocused)
      setTitleVal(oobM);
    if(!captionFocused)
      setCaptionVal(oobM);
  }

  private Media getOobMedia(SessionManager sMgr, Media oobM)
  {
    if(oobM != null)
      return oobM;
    return (Media)M.getSession(sMgr).get(Media.class,m.getId()); // this can be null if the media was deleted
  }
  
  public Media getMedia()
  {
    return m;
  }

  private Component buildPlayer(Media m)
  {
    try {
      
      Embedded player = new Embedded();
      player.setType(Embedded.TYPE_OBJECT);
      player.setMimeType("application/x-shockwave-flash");
      player.setSource(new ExternalResource("http://www.youtube.com/v/"+m.getUrl()));
      //todo v7 check
/*        ytp.setVideoId(vidMedia.getUrl());
      ytp.setAllowFullscreen(true);
      ytp.setShowRelated(false); // newly added
*/
     player.setWidth(WIDTH);
     player.setHeight(PLAYER_HEIGHT);
     
     placeHolder = new Label("Mmowgli Video");
     placeHolder.setWidth(WIDTH);
     placeHolder.setHeight(PLAYER_HEIGHT);
     
     return player;
    }
    catch(Exception e) {
      return new Label("Wrong media type");
    }
  }
  
  private AbsoluteLayout buildImage(Media m)
  {
    MediaLocator mLoc = Mmowgli2UI.getGlobals().getMediaLocator();
    AbsoluteLayout imageStack = new AbsoluteLayout();
    imageStack.setWidth(WIDTH);
    imageStack.setHeight(PLAYER_HEIGHT);
    image = new Embedded();
    image.setSource(mLoc.locate(m));

    image.setWidth(WIDTH);
    image.setHeight(PLAYER_HEIGHT);
    imageStack.addComponent(image, "top:0px;left:0px");

    zoom.setIcon(mLoc.getActionPlanZoomButt());
    zoom.addStyleName("m-actionplan-zoom-button");
    zoom.addStyleName("borderless");
    imageStack.addComponent(zoom, "top:10px;left:10px");
    return imageStack;
  }
  
  private void setVideoMedia(Media m)
  {
    this.m = m;
    Component comp = buildPlayer(m);
    if(mediaPlayer != null)
      removeComponent(mediaPlayer);
    addComponent(mediaPlayer = comp, PLAYERINDEX_IN_LAYOUT);
    
    setCaptionAndTitle(m);
    
    mediaPlayer = comp;
  }

  private void setCaptionAndTitle(Media m)
  {
    setCaptionVal(m);
    setTitleVal(m);
  }
  
  private void setCaptionVal(Media m)
  {
    boolean isRo = caption.isReadOnly();
    caption.setReadOnly(false);
    setValueIfNonNull(caption,m.getDescription());// "caption" here is "description" in db   
    caption.setReadOnly(isRo);   
  }
  private void setTitleVal(Media m)
  {
    boolean isRo = title.isReadOnly();
    title.setReadOnly(false);
    setValueIfNonNull(title,m.getTitle());
    title.setReadOnly(isRo);
    
  }
  private void setImageMedia(Media m)
  {
    this.m = m;
    Component comp = buildImage(m);
    if(mediaPlayer != null)
      removeComponent(mediaPlayer);
    addComponent(mediaPlayer = comp, PLAYERINDEX_IN_LAYOUT);

    image.setSource(Mmowgli2UI.getGlobals().getMediaLocator().locate(m));
    if (scaler != null)
      zoom.removeClickListener(scaler);
    zoom.addClickListener(scaler = new Scaler(m));
    
    setCaptionAndTitle(m);
   
    mediaPlayer = comp;
  }

  private void setValueIfNonNull(AbstractTextField comp, String s)
  {
    if(s != null)
      comp.setValue(s);
  }
  
  @Override
  public void initGui()
  {
    setStyleName("m-actionplan-image-panel");
    setSizeUndefined();
    setWidth(WIDTH);
    
    setSpacing(false);
    titleHL.setMargin(false);
    titleHL.setSpacing(true);
    titleHL.setSizeUndefined();
    titleHL.setWidth("307px"); //WIDTH);
    Label sp;
    titleHL.addComponent(sp=new Label());
    sp.setWidth("3px");
    titleHL.addComponent(indexLab);
    titleHL.setComponentAlignment(indexLab, Alignment.MIDDLE_CENTER);
    titleHL.addComponent(title);
    titleHL.setExpandRatio(title, 1.0f);
    addComponent(titleHL);
    title.addStyleName("m-actionplan-image-title");
    title.setWidth("100%");
    
    setMedia(m);
    
    caption.setHeight("65px");// this: setRows(4); doesn't size the same on chrome and ff
    caption.setWidth("100%");
    addComponent(caption);
    caption.addStyleName("m-actionplan-images-caption");
    
    addComponent(captionSavePan);
    captionSavePan.setWidth("99%");
    captionSavePan.setVisible(false);
  }
 
  public  void sendStartEditMessage(String msg)
  {
    /* Have seen event flurries
    if(app.isAlive()) {
      ApplicationMaster master = app.globs().applicationMaster();
      master.sendLocalMessage(ACTIONPLAN_EDIT_BEGIN, ""+apId+MMESSAGE_DELIM+msg);
    }*/
  }
  
  public void enableVideo()
  {
    hideVideo(false);
  }
  
  public void disableVideo()
  {
    hideVideo(true);
  }
  
  private void hideVideo(boolean tf)
  {
    if(tf) {
      replaceComponent(mediaPlayer, placeHolder); //PLAYERINDEX_IN_LAYOUT
    }
    else {
      replaceComponent(placeHolder,mediaPlayer);
    }
  }
  class Scaler implements ClickListener
  {
    private static final long serialVersionUID = -6183261170803030233L;

    Media m;

    Scaler(Media m)
    {
      this.m = m;
    }
    
    Embedded embd;
    
    // We now skip trying to get the size of the image -- we were trying to do that to manage aspect ratio.
    // 1. Doing ImageIO.read() was failing because some URLs visible from the client (browser) were not visible from the server (JVM).
    // 2. Eliminates the need for ImageScaler plugin.
    // 3. Uses browser's ability to zoom an img element when window size changes.
    // 4. Downside, can't get client code to report size of rendered image.
    
    public void buttonClick(ClickEvent event)
    {
      Resource r = Mmowgli2UI.getGlobals().getMediaLocator().locate(m);

      final Window win = new Window();

      win.setModal(true);
      win.setWidth("640px");
      win.setHeight("480px");

      VerticalLayout layout = new VerticalLayout();
      layout.setSizeFull();
      layout.setMargin(true);
      win.setContent(layout);

      embd = new Embedded(null, r);
      layout.addComponent(embd);
      embd.setSizeFull();
      layout.setExpandRatio(embd, 1.0f);

      UI.getCurrent().addWindow(win);
      win.center();
    }

  }
  
  public void saveCaptionIfChanged(Session sess){} //todo remove
  
}