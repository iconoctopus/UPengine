package org.duckdns.spacedock.upengine.upmaster;

import java.util.ArrayList;
import org.duckdns.spacedock.commonutils.PropertiesHandler;
import org.duckdns.spacedock.upengine.libupsystem.Arme;
import org.duckdns.spacedock.upengine.libupsystem.Arme.Degats;
import org.duckdns.spacedock.upengine.libupsystem.ArmeCaC;
import org.duckdns.spacedock.upengine.libupsystem.ArmeDist;
import org.duckdns.spacedock.upengine.libupsystem.Bouclier;
import org.duckdns.spacedock.upengine.libupsystem.EnsembleJauges.EtatVital;
import org.duckdns.spacedock.upengine.libupsystem.Perso;
import org.duckdns.spacedock.upengine.libupsystem.PieceArmure;
import org.duckdns.spacedock.upengine.libupsystem.RollUtils.RollResult;
import org.duckdns.spacedock.upengine.libupsystem.UPReferenceArmes;
import org.duckdns.spacedock.upengine.upmaster.SessionManager.AttackReport;

/**
 * Created by ykonoclast on 6/6/16. Cette classe représente un personnage et
 * l'environnement avec lequel il interragit (notamment le ND de sa cible) c'est
 * utile pour ne pas avoir à gérer plusieurs listes (les persos et leurs cibles)
 * avec risque de décorrélation. Un autre but est d'encapsuler les évolutions de
 * la libupsystem dans cette classe
 */
class CharacterAssembly
{

    /**
     * le perso issu de libupsystem
     */
    private final Perso m_perso;
    /**
     * ND de la cible courante
     */
    private int m_targetDefense;
    /**
     * l'inventaire du personnage de l'assembly
     */
    private Inventaire m_inventaire;

    /**
     * constructeur avec ND cible de 25 par défaut
     *
     * @param p_rm rm du personnage
     */
    CharacterAssembly(int p_rm)
    {
	this(p_rm, 25);//ND cible de 25 par défaut
    }

    /**
     * constructeur sans ND par défaut
     *
     * @param p_rm rm du personnage
     * @param p_ND ND de la cible
     */
    CharacterAssembly(int p_rm, int p_ND)
    {
	m_perso = new Perso(p_rm);
	m_targetDefense = p_ND;
	m_inventaire = new Inventaire();
    }

    /**
     * @return le nom du perso tel que connu par le Perso UP!System
     */
    String getCharName()
    {
	return m_perso.toString();
    }

    /**
     *
     * @param p_name
     * @return
     */
    void setCharName(String p_name)
    {
	m_perso.setLibellePerso(p_name);
    }

    /**
     * Permet de spécifier une arme. Tolère les erreurs : placer une épée à deux
     * mains ici alors que le bouclier est équipé va simplement retirer ce
     * dernier
     *
     * @param p_rolled dés lancés
     * @param p_kept dés gardés
     */
    void setCurrentWeapon(int p_index, Arme.QualiteArme p_quality, Arme.EquilibrageArme p_balance)
    {//TODO pour l'instant on ne s'occupe que du port d'une seule arme, forcément à droite

	Arme arme;
	if (UPReferenceArmes.getInstance().getModArme(p_index) == 0)
	{
	    arme = new ArmeCaC(p_index, p_quality, p_balance);
	}
	else
	{
	    arme = new ArmeDist(p_index, p_quality, p_balance);
	}

	if (arme.isArme2Mains())
	{
	    if (m_inventaire.getBouclier(Inventaire.Lateralisation.GAUCHE) != null)//TODO le bouclier est forcément à gauche pour l'instant
	    {
		m_inventaire.removeBouclier(Inventaire.Lateralisation.GAUCHE);
	    }
	}
	if (m_inventaire.getArmeCourante() != null)
	{
	    m_inventaire.removeArme(Inventaire.Lateralisation.DROITE);
	}
	m_inventaire.addArme(arme, Inventaire.Lateralisation.DROITE);
    }

    /**
     * supprime l'arme principale du personnage. Tolère les erreurs : si aucune
     * arme n'est équipée, l'inventaire rest ainsi
     */
    void delWeapon()
    {
	if (m_inventaire.getArmeCourante() != null)
	{
	    m_inventaire.removeArme(Inventaire.Lateralisation.DROITE);
	}
    }

    /**
     *
     * @return le nom de l'arme actuellement brandie (mains nues si rien)
     */
    String getCurrentWeaponName()
    {
	Arme currentWeapon = m_inventaire.getArmeCourante();

	String weapName = UPReferenceArmes.getInstance().getListCatArmeCaC().get(0);//on renvoie mains nues par défaut
	if (currentWeapon != null)
	{
	    weapName = currentWeapon.toString();
	}
	return weapName;
    }

    /**
     * renvoie le nom de la pièce d'armure portée à l'emplacement indiqué
     *
     * @param p_zone
     * @return
     */
    String getArmourPartName(Inventaire.PartieCorps p_zone)
    {
	String res = PropertiesHandler.getInstance("upmaster").getString("aucun");
	PieceArmure piece = m_inventaire.getPieceArmure(p_zone);
	if (piece != null)
	{
	    res = piece.toString();
	}
	return res;
    }

    /**
     * insère une pièce d'armure à l'emplacement indiqué. Tolérant aux erreurs :
     * remplace si occuoé
     *
     * @param p_index
     * @param p_zone
     * @return
     */
    void setArmourPart(int p_index, int p_material, int p_type, Inventaire.PartieCorps p_zone)
    {
	if (m_inventaire.getPieceArmure(p_zone) != null)
	{
	    m_inventaire.removePieceArmure(p_zone);
	}
	m_inventaire.addPieceArmure(new PieceArmure(p_index, p_material, p_type), p_zone);
    }

    /**
     * supprime une pièce d'armure
     *
     * @param p_zone
     */
    void delArmourPart(Inventaire.PartieCorps p_zone)
    {
	if (m_inventaire.getPieceArmure(p_zone) != null)
	{
	    m_inventaire.removePieceArmure(p_zone);
	}
    }

    /**
     * renvoie le nom du bouclier si il y en a un ou "aucun" si il n'y en a pas
     *
     * @return
     */
    String getShieldName()
    {//TODO en l'absence de localisation le bouclier est pour l'instant forcément à gauche
	String res = PropertiesHandler.getInstance("upmaster").getString("aucun");
	Bouclier bouclier = m_inventaire.getBouclier(Inventaire.Lateralisation.GAUCHE);
	if (bouclier != null)
	{
	    res = bouclier.toString();
	}
	return res;
    }

    /**
     * met un bouclier en place. Résistant aux erreurs : si une arme à deux
     * mains est installée elle est retirée pour mettre le bouclier
     *
     * @param p_index
     * @param p_materiau
     * @param p_type
     */
    void setShield(int p_index, int p_type)
    {
	Arme armeCourante = m_inventaire.getArmeCourante();
	if (armeCourante != null)
	{
	    if (armeCourante.isArme2Mains())//l'arme courante utilie deux mains, on la supprime donc pour installer le bouclier
	    {
		delWeapon();
	    }
	}
	m_inventaire.addBouclier(new Bouclier(p_index, p_type), Inventaire.Lateralisation.GAUCHE);//TODO le bouclier est forcément à gauche
    }

    /**
     * supprime le bouclier porté, résiste aux erreurs : si aucun bouclier, il
     * ne se passe juste rien
     */
    void delShield()
    {
	if (m_inventaire.getBouclier(Inventaire.Lateralisation.GAUCHE) != null)//TODO le bouclier est forcément à gauche
	{
	    m_inventaire.removeBouclier(Inventaire.Lateralisation.GAUCHE);
	}
    }

    /**
     * configure le ND de la cible
     *
     * @param p_Defense
     */
    void setTargetDefence(int p_Defense)
    {
	m_targetDefense = p_Defense;
    }

    /**
     * @return le ND de la cible actuelle
     */
    int getTargetDefence()
    {
	return (m_targetDefense);
    }

    /**
     *
     * @return le ND propre du combattant
     */
    int getFighterDefense(int p_weapType)
    {
	return (m_perso.getDefense(p_weapType, 0, m_inventaire.getArmure()));//TODO pour l'instant on ne gère pas les adversaires supplémentaires
    }

    /**
     *
     * @param p_phase la phase courante, ce n'est pas une méthode
     * d'interrogation générale : la méthode a un comportement indéfini si on
     * demande pour une phase ultérieure
     * @return vrai si le perso a au moins une action dans la phase en cours
     */
    boolean isActive(int p_phase)
    {
	return m_perso.isActif(p_phase);
    }

    /**
     *
     * @return les actions du personnages dans le tour en cours
     */
    ArrayList<Integer> getActions()
    {
	return m_perso.getActions();
    }

    EtatVital getEtatVital()
    {
	return m_perso.getEtatVital();
    }

    /**
     * fait attaquer le personnage
     *
     * @param p_currentPhase phase actuelle car la libupsystem vérifie si
     * l'action est possible
     * @return les dégâts infligés
     */
    AttackReport attack(int p_currentPhase)//TODO pour l'instant ne gère que les armes de corps à corps
    {
	Degats finalResult = new Degats(0, 0);
	RollResult technicalResult = m_perso.attaquerCaC(p_currentPhase, m_targetDefense, (ArmeCaC) m_inventaire.getArmeCourante());

	if (technicalResult.isJetReussi())//extraction des dégâts
	{
	    finalResult = m_perso.genererDegats(technicalResult.getNbIncrements(), m_inventaire.getArmeCourante());
	}
	return new AttackReport(finalResult, technicalResult.isJetReussi(), isActive(p_currentPhase), m_perso.getActions());
    }

    /**
     * inflige des dégâts à ce personnage
     *
     * @param p_damage les dommages a infliger
     */
    void hurt(Degats p_degats)
    {
	m_perso.etreBlesse(p_degats, m_inventaire.getArmure());
    }

    /**
     * force un personnage à retirer son initiative
     */
    void regenInit()
    {
	m_perso.genInit();
    }
}
