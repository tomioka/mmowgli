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
package edu.nps.moves.mmowgli.db;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import org.hibernate.Session;

//import com.vaadin.data.hbnutil.HbnContainer;

//import edu.nps.moves.mmowgli.hibernate.VHib;
import edu.nps.moves.mmowgli.hibernate.Sess;
import static edu.nps.moves.mmowgli.hibernate.DbUtils.*;

/**
 * GameEvent.java Created on May 3, 2010
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * This is a database table, listing private message between users
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@Entity
public class GameEvent implements Serializable
{
  private static final long serialVersionUID = 5043474627706981217L;

  public static int DESCRIPTION_FIELD_LENGTH = 511;
  
  static public enum EventType {
    UNSPECIFIED,USERLOGIN,USERUPDATE,USERLOGOUT,USERTIMEOUT,
    IDEACARDPLAYED,CHILDCARDPLAYED,CARDMARKED,SCORECHANGE,
    MESSAGEBROADCAST,MESSAGEBROADCASTGM, LOGINLIMITCHANGE,
    GAMEMASTERNOTE,APPLICATIONSTARTUP,USERLOCKOUT,USERUNLOCKOUT,USERNEW,
    CARDSREADONLY,CARDSREADWRITE,GAMEREADONLY,GAMEREADWRITE,
    ACTIONPLANUPDATED,SESSIONEND,GAMESIGNUPRESTRICTED,GAMESIGNUPOPEN,
    GAMESIGNUPINTERVALRESTRICTED, GAMESIGNUPINTERVALOPEN,
    BLOGHEADLINEPOST,ACTIONPLANHELPWANTED,TOPCARDSREADONLY,TOPCARDSREADWRITE,
    NEWUSERSRESTRICTED, NEWUSERSUNRESTRICTED, REGISTRATIONATTEMPT, AUTOREPORTGENERATION,
    GAMEEMAILCONFIRMATIONSTART,GAMEEMAILCONFIRMATIONEND,
    CARDTEXTEDITED,GAMEDESIGNEDITED,COMMENTSUPERINTERESTING,
    USERGAMENAMECHANGED,COMMENTEDITED,CHATEDITED,USERPASSWORDCHANGED,
    GAMELOGINBUTTONSTATUS;
    
    public String description()
    {
      switch(this) {
        case UNSPECIFIED: return "";
        case IDEACARDPLAYED: return "Strategy card played ";
        case CHILDCARDPLAYED: return "Card played on a parent card ";
        case CARDMARKED: return "Card marked by gamemaster ";
        case SCORECHANGE: return "User score changed ";
        case USERLOGIN: return "User logged in ";
        case USERUPDATE: return "User data updated ";
        case USERLOGOUT: return "User logged out ";
        case USERTIMEOUT: return "User time-out ";
        case USERLOCKOUT: return "User lock-out ";
        case USERUNLOCKOUT: return "User un-lock-out ";
        case USERNEW: return "New user registered ";
        case MESSAGEBROADCAST: return "Message broadcast";
        case MESSAGEBROADCASTGM: return "Game master message broadcast";
        case LOGINLIMITCHANGE: return "User login limit change";
        case GAMEMASTERNOTE: return "Game master comment";
        case APPLICATIONSTARTUP: return "Application startup";
        case CARDSREADONLY: return "Cards set to read-only";
        case CARDSREADWRITE: return "Cards set to read-write";
        case GAMEREADONLY: return "Game set to read-only";
        case GAMEREADWRITE: return "Game set to read-write";
        case ACTIONPLANUPDATED: return "Action plan updated";
        case SESSIONEND: return "Explicit logout or timeout";
        case GAMESIGNUPRESTRICTED: return "Set new user sign-up restricted to Query entries";
        case GAMESIGNUPOPEN: return "Removed new user sign-up restriction to Query entries";
        case GAMESIGNUPINTERVALRESTRICTED: return "Set new user sign-up restricted to Query database markers";
        case GAMESIGNUPINTERVALOPEN: return "Removed new user sign-up restriction to Query database markers";
        case BLOGHEADLINEPOST: return "Blog headline updated";
        case ACTIONPLANHELPWANTED: return "Action plan help-wanted";
        case TOPCARDSREADONLY: return "Top cards set to read-only";
        case TOPCARDSREADWRITE: return "Top cards set to read-write";
        case NEWUSERSRESTRICTED: return "Game locking out new users";
        case NEWUSERSUNRESTRICTED: return "Game opening up to new users";
        case REGISTRATIONATTEMPT: return "Registration attempted and refused";
        case AUTOREPORTGENERATION: return "Report generation";
        case GAMEEMAILCONFIRMATIONSTART: return "Game now requires email confirmation for new users";
        case GAMEEMAILCONFIRMATIONEND: return "Game now does NOT require email confirmation for new users";
        case CARDTEXTEDITED: return "Card text edited by gamemaster";
        case GAMEDESIGNEDITED: return "Game design edited";
        case COMMENTSUPERINTERESTING: return "Comment super-interesting";
        case USERGAMENAMECHANGED: return "User game name changed";
        case COMMENTEDITED: return "Action plan comment edited";
        case CHATEDITED: return "Action plan chat edited";
        case USERPASSWORDCHANGED: return "User password changed";
        case GAMELOGINBUTTONSTATUS: return "Game login button status";
      }
      throw new AssertionError("Unknown op: " + this);
    }
  };
   
  long    id;         // Primary key, auto-generated.
  Date    dateTime;
  EventType eventtype = EventType.UNSPECIFIED;
  String  description = "";
  long    parameter = -1;
  
  public GameEvent()
  {
    setDateTime(new Date());   
  }

  public GameEvent(EventType typ, String desc)
  {
    this();
    setEventtype(typ);
    setDescription(desc);   
  }
  
  public GameEvent(EventType typ, String desc, long parameter)
  {
    this(typ,desc);
    setParameter(parameter);    
  }
  
/*  @SuppressWarnings("unchecked")
  public static HbnContainer<GameEvent> getContainer()
  {
    return (HbnContainer<GameEvent>) HibernateContainers.getContainer(GameEvent.class);
  }
*/
  public static void save(GameEvent m)
  {
    Sess.sessSave(m);
  }
  
  public static GameEvent get(Serializable id, Session sess)
  {
    return (GameEvent)sess.get(GameEvent.class, id);
  }
  
  public void clone(GameEvent ge)
  {
    setDateTime(ge.getDateTime());
    setDescription(ge.getDescription());
    setEventtype(ge.getEventtype());
    setId(ge.getId());
    setParameter(ge.getParameter());
  }
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  @Basic
  public Date getDateTime()
  {
    return dateTime;
  }

  public void setDateTime(Date dateTime)
  {
    this.dateTime = dateTime;
  }
  @Basic
  public EventType getEventtype()
  {
    return eventtype;
  }

  public void setEventtype(EventType eType)
  {
    this.eventtype = eType;
  }

  @Lob
  public String getDescription()
  {
    return description;
  }
  
  public void setDescription(String descr)
  {
    this.description = len511(descr);
  }

  @Basic
  public long getParameter()
  {
    return parameter;
  }

  public void setParameter(long parameter)
  {
    this.parameter = parameter;
  }
  
 }
