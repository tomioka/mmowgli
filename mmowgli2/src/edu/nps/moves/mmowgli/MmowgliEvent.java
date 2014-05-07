package edu.nps.moves.mmowgli;

/**
 * MmowgliEvent.java
 * Created on Mar 18, 2014
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public enum MmowgliEvent
{ 
  LEADERBOARDCLICK,
  TAKEACTIONCLICK,
  PLAYIDEACLICK,
  LEARNMORECLICK,
  CALLTOACTIONCLICK,
  MAPCLICK,
  SEARCHCLICK,
  INBOXCLICK,
  BLOGFEEDCLICK,
  SIGNOUTCLICK,
  POSTTROUBLECLICK,
  FOUOCLICK,

  HOWTOPLAYCLICK,

  // Menus
  MENUHOMECLICK,
  MENUGAMEMASTEREDITCLICK,
  MENUGAMESETUPCLICK,
  MENUGAMEMASTERACTIONDASHBOARDCLICK,
  MENUGAMEMASTERLOGOUTCLICK,
  MENUGAMEMASTERUSERPROFILE,
  MENUGAMEMASTERCREATEACTIONPLAN,
  MENUGAMEMASTERMONITOREVENTS,
  MENUGAMEMASTERUSERADMIN,
  MENUGAMEADMINLOGINLIMIT,
  MENUGAMEMASTERACTIVECOUNTCLICK,
  MENUGAMEMASTERACTIVECOUNTBYSERVERCLICK,
  MENUGAMEMASTERBROADCAST,
  MENUGAMEMASTERCARDCOUNTCLICK,
  MENUGAMEMASTERBROADCASTTOGMS,
  MENUGAMEMASTERPOSTCOMMENT,
  MENUGAMEADMINTESTCLICK,
  MENUGAMEMASTERINVITEAUTHORSCLICK,
  MENUGAMEMASTERUNLOCKEDITSCLICK,
  MENUGAMEMASTERBLOGHEADLINE,
  MENUGAMEMASTERTOTALREGISTEREDUSERS,
  MENUGAMEMASTERUSERPOLLINGCLICK,
  MENUGAMEMASTEROPENREPORTSPAGE,
  MENUGAMEADMINDUMPEMAILS,
  MENUGAMEADMINDUMPSIGNUPS,
  MENUGAMEADMINDUMPGAMEMASTERS,
  MENUGAMEADMINCLEANINVITEES,
  MENUGAMEADMINSETGAMEREADONLY,
  MENUGAMEADMINSETGAMEREADWRITE,
  MENUGAMEADMINMANAGESIGNUPS,
  MENUGAMEADMINSETSIGNUPRESTRICTED,
  MENUGAMEADMINSETSIGNUPOPEN,
  MENUGAMEADMINPOSTGAMERECALCULATION,

  MENUGAMEADMINSETSIGNUPINTERVALRESTRICTED,
  MENUGAMEADMINSETSIGNUPINTERVALOPEN,
  MENUGAMEADMINSETNONEWSIGNUPS,
  MENUGAMEADMINSETALLOWNEWSIGNUPS,
  MENUGAMEADMIN_QUERIES_ENABLED,
  MENUGAMEADMIN_QUERIES_DISABLED,

  // not used MENUGAMEADMINZEROONLINEBITS,
  MENUGAMEADMINSETCARDSREADONLY,
  MENUGAMEADMINSETCARDSREADWRITE,
  MENUGAMEADMINTESTEXCEPTION,
  MENUGAMEADMINTESTOOBEXCEPTION,
  MENUGAMEADMINSETTOPCARDSREADONLY,
  MENUGAMEADMINSETTOPCARDSREADWRITE,
  MENUGAMEADMINEXPORTACTIONPLANS,
  MENUGAMEADMINEXPORTCARDS,
  MENUGAMEADMINPUBLISHREPORTS,
  MENUGAMEADMINSETLOGINS,
  MENUGAMEMASTER_EXPORT_SELECTED_ACTIONPLAN,
  MENUGAMEMASTER_EXPORT_SELECTED_CARD,

  MENUGAMEADMIN_START_CARD_DB_TEST,
  MENUGAMEADMIN_END_CARD_DB_TEST,
  MENUGAMEADMIN_START_USER_DB_TEST,
  MENUGAMEADMIN_END_USER_DB_TEST,

  MENUGAMEADMIN_START_EMAILCONFIRMATION,
  MENUGAMEADMIN_END_EMAILCONFIRMATION,
  MENUGAMEADMIN_BUILDGAMECLICK,
  MENUGAMEADMIN_BUILDGAMECLICK_READONLY, // next
                                                                       // 142
  MENUGAMEADMIN_EXPORTGAMESETTINGS,

  MENUGAMEADMIN_ADMIN_LOGIN_ONLY,
  MENUGAMEADMIN_ADMIN_LOGIN_ONLY_REMOVE,

  GAMEADMIN_SHOW_WELCOME_MOCKUP,
  GAMEADMIN_SHOW_CALLTOACTION_MOCKUP,
  GAMEADMIN_SHOW_TOPCARDS_MOCKUP,
  GAMEADMIN_SHOW_SUBCARDS_MOCKUP,
  GAMEADMIN_SHOW_ACTIONPLAN_MOCKUP,

  MENUGAMEMASTERADDTOVIPLIST,
  MENUGAMEMASTERVIEWVIPLIST,

  HANDLE_LOGIN_STARTUP, // next = 143
  
  
  
  GAMESETUPEDITMOVECLICK,
  // Action Plans
  RFECLICK,
  // Cards
  IDEADASHBOARDCLICK,
  CARDCLICK,
  CARDAUTHORCLICK,
  CARDCHAINPOPUPCLICK,
  CARDCREATEACTIONPLANCLICK,
  // User profile
  IMPROVESCORECLICK,
  SHOWUSERPROFILECLICK,
  
  // Action dashboard
  ACTIONPLANSHOWCLICK,
  HOWTOWINACTIONCLICK,
  ACTIONPLANREQUESTCLICK;
  
  public static final MmowgliEvent values[] = values();
}