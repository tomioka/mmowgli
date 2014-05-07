/*
* Copyright (c) 1995-2014 held by the author(s).  All rights reserved.
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

import javax.persistence.*;

import org.hibernate.Session;

import edu.nps.moves.mmowgli.hibernate.VHib;
import edu.nps.moves.mmowgli.hibernate.Sess;

/**
 * One game represents an interaction that may have several "turns". For example, a game about piracy in Somalia.
 * 
 * @author DMcG
 * 
 *         * Modified on Dec 16, 2010
 * 
 *         MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

@Entity
public class GameLinks implements Serializable
{
  private static final long serialVersionUID = -5376587367981087375L;

  long id; // Primary key

  String aboutLink;
  String actionPlanRequestLink;
  String blogLink;
  String creditsLink;
  String faqLink;
  String fixesLink;
  String fouoLink;
  String gameFromEmail;
  String gameFullLink;
  String gameHomeUrl;
  String glossaryLink;
  String howToPlayLink;
  String improveScoreLink;
  String informedConsentLink;
  String learnMoreLink;
  String mmowgliMapLink;
  String surveyConsentLink;
  String termsLink;
  String thanksForInterestLink;
  String thanksForPlayingLink;
  String troubleLink;
  String troubleMailto;
  String userAgreementLink;

  public static GameLinks get(Session sess)
  {
    return get(sess, 1L); // only one entry in current design
  }

  private static GameLinks get(Session sess, Serializable id)
  {
    return (GameLinks) sess.get(GameLinks.class, id);
  }

  public static GameLinks get()
  {
    return get(1L); // only one entry in current design
  }

  public static GameLinks get(Serializable id)
  {
    return get(VHib.getVHSession(), id);
  }

  public static void save(GameLinks gl)
  {
    VHib.getVHSession().save(gl);
  }

  public static void update(GameLinks gl)
  {
    Sess.sessUpdate(gl);
  }

  public static void update()
  {
    update(GameLinks.get());
  }

  public static GameLinks merge(GameLinks gl)
  {
    return (GameLinks) Sess.sessMerge(gl);
  }

  /**
   * Primary key
   * 
   * @return the id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  public long getId()
  {
    return id;
  }

  /**
   * Primary key
   * 
   * @param id
   */
  public void setId(long id)
  {
    this.id = id;
  }

  @Basic
  public String getGameHomeUrl()
  {
    return gameHomeUrl;
  }

  public void setGameHomeUrl(String s)
  {
    this.gameHomeUrl = s;
  }

  @Basic
  public String getLearnMoreLink()
  {
    return learnMoreLink;
  }

  public void setLearnMoreLink(String learnMoreLink)
  {
    this.learnMoreLink = learnMoreLink;
  }

  @Basic
  public String getCreditsLink()
  {
    return creditsLink;
  }

  public void setCreditsLink(String creditsLink)
  {
    this.creditsLink = creditsLink;
  }

  @Basic
  public String getAboutLink()
  {
    return aboutLink;
  }

  public void setAboutLink(String aboutLink)
  {
    this.aboutLink = aboutLink;
  }

  @Basic
  public String getTermsLink()
  {
    return termsLink;
  }

  public void setTermsLink(String termsLink)
  {
    this.termsLink = termsLink;
  }

  @Basic
  public String getFaqLink()
  {
    return faqLink;
  }

  public void setFaqLink(String faqLink)
  {
    this.faqLink = faqLink;
  }

  @Basic
  public String getTroubleLink()
  {
    return troubleLink;
  }

  public void setTroubleLink(String troubleLink)
  {
    this.troubleLink = troubleLink;
  }

  @Basic
  public String getBlogLink()
  {
    return blogLink;
  }

  public void setBlogLink(String blogLink)
  {
    this.blogLink = blogLink;
  }

  @Basic
  public String getImproveScoreLink()
  {
    return improveScoreLink;
  }

  public void setImproveScoreLink(String improvesScoreLink)
  {
    this.improveScoreLink = improvesScoreLink;
  }

  @Basic
  public String getActionPlanRequestLink()
  {
    return actionPlanRequestLink;
  }

  public void setActionPlanRequestLink(String actionPlanRequestLink)
  {
    this.actionPlanRequestLink = actionPlanRequestLink;
  }

  @Basic
  public String getThanksForPlayingLink()
  {
    return thanksForPlayingLink;
  }

  public void setThanksForPlayingLink(String thanksForPlayingLink)
  {
    this.thanksForPlayingLink = thanksForPlayingLink;
  }

  @Basic
  public String getGameFullLink()
  {
    return gameFullLink;
  }

  public void setGameFullLink(String gameFullLink)
  {
    this.gameFullLink = gameFullLink;
  }

  @Basic
  public String getThanksForInterestLink()
  {
    return thanksForInterestLink;
  }

  public void setThanksForInterestLink(String thanksForInterestLink)
  {
    this.thanksForInterestLink = thanksForInterestLink;
  }

  @Basic
  public String getMmowgliMapLink()
  {
    return mmowgliMapLink;
  }

  public void setMmowgliMapLink(String mmowgliMapLink)
  {
    this.mmowgliMapLink = mmowgliMapLink;
  }

  @Basic
  public String getInformedConsentLink()
  {
    return informedConsentLink;
  }

  public void setInformedConsentLink(String informedConsentLink)
  {
    this.informedConsentLink = informedConsentLink;
  }

  @Basic
  public String getUserAgreementLink()
  {
    return userAgreementLink;
  }

  public void setUserAgreementLink(String userAgreementLink)
  {
    this.userAgreementLink = userAgreementLink;
  }

  @Basic
  public String getSurveyConsentLink()
  {
    return surveyConsentLink;
  }

  public void setSurveyConsentLink(String surveyConsentLink)
  {
    this.surveyConsentLink = surveyConsentLink;
  }

  @Basic
  public String getFouoLink()
  {
    return fouoLink;
  }

  public void setFouoLink(String fouoLink)
  {
    this.fouoLink = fouoLink;
  }

  @Basic
  public String getFixesLink()
  {
    return fixesLink;
  }

  public void setFixesLink(String s)
  {
    fixesLink = s;
  }

  @Basic
  public String getGlossaryLink()
  {
    return glossaryLink;
  }

  public void setGlossaryLink(String s)
  {
    glossaryLink = s;
  }

  @Basic
  public String getGameFromEmail()
  {
    return gameFromEmail;
  }

  public void setGameFromEmail(String s)
  {
    gameFromEmail = s;
  }

  @Basic
  public String getTroubleMailto()
  {
    return troubleMailto;
  }

  public void setTroubleMailto(String s)
  {
    troubleMailto = s;
  }

  @Basic
  public String getHowToPlayLink()
  {
    return howToPlayLink;
  }

  public void setHowToPlayLink(String s)
  {
    howToPlayLink = s;
  }
}