package org.duckdns.spacedock.upengine.upmaster;

import org.duckdns.spacedock.upengine.libupsystem.Arme;
import org.duckdns.spacedock.upengine.libupsystem.Arme.Degats;
import org.duckdns.spacedock.upengine.libupsystem.ArmeCaC;
import org.duckdns.spacedock.upengine.libupsystem.Perso;
import org.duckdns.spacedock.upengine.libupsystem.RollUtils.RollResult;
import org.duckdns.spacedock.upengine.upmaster.SessionManager.AttackReport;

/**
 * Created by iconoctopus on 6/6/16. Cette classe représente un personnage et
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
    private int m_targetND;

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
	m_targetND = p_ND;
    }

    /**
     * @return le nom du perso tel que connu par le Perso UP!System
     */
    String getLibellePerso()
    {
	return m_perso.toString();
    }

    /**
     * Permet de spécifier une arme
     *
     * @param p_rolled dés lancés
     * @param p_kept dés gardés
     */
    void setCurrentWeapon(int p_index, Arme.QualiteArme p_quality, Arme.EquilibrageArme p_balance)//TODO:nécessite mise à jour de l'application, pour l'instant ne permet que d'ajouter une arme et de la sélectionner, à terme il faut distinguer ces opérations en ajout dans l'inventaire et sélection de l'arme actuelle sans oublier lapossiblité de retirer une arme
    {
	m_perso.getListArmes().add(new ArmeCaC(p_index, p_quality, p_balance));
	m_perso.setArmeCourante(m_perso.getListArmes().size() - 1);//après avoir ajouté une arme en queue, on définit l'arme courante comme étant la dernière a avoir été ajoutée
    }

    /**
     *
     * @return le nom de l'arme actuellement brandie
     */
    String getCurrentWeaponName()
    {
	return m_perso.getArmeCourante().toString();
    }

    /**
     * configure le ND de la cible
     *
     * @param p_ND
     */
    void setTargetND(int p_ND)
    {
	m_targetND = p_ND;
    }

    /**
     * @return le ND de la cible actuelle
     */
    int getTargetND()
    {
	return (m_targetND);
    }

    /**
     *
     * @return le ND propre du combattant
     */
    int getFighterND(int p_weapType, boolean p_dodge)
    {
	return (m_perso.getNDPassif(p_weapType, m_perso.getArmeCourante().getCategorie(), p_dodge));
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
     * @return si le personnage doit être éliminé
     */
    boolean isOut()
    {
	return m_perso.isInconscient();//dans cette version seule l'inconscience vaut élimination
    }

    /**
     *
     * @return si le personnage est sonné
     */
    boolean isStunned()
    {
	return m_perso.isSonne();
    }

    /**
     *
     * @return nombre de blessures légères reçues
     */
    int getNbFleshWounds()
    {
	return m_perso.getBlessuresLegeres();
    }

    /**
     *
     * @return nombre de blessures graves reçues
     */
    int getNbDramaWounds()
    {
	return m_perso.getBlessuresGraves();
    }

    /**
     * fait attaquer le personnage
     *
     * @param p_currentPhase phase actuelle car la libupsystem vérifie si
     * l'action est possible
     * @return les dégâts infligés
     */
    AttackReport attack(int p_currentPhase)
    {
	Degats finalResult = new Degats(0, 0);
	RollResult technicalResult = m_perso.attaquerCaC(p_currentPhase, m_targetND);

	if (technicalResult.isJetReussi())//extraction des dégâts
	{
	    finalResult = m_perso.genererDegats(technicalResult.getNbIncrements());
	}
	return new AttackReport(finalResult, technicalResult.isJetReussi(), isActive(p_currentPhase));
    }

    /**
     * inflige des dégâts à ce personnage
     *
     * @param p_damage les dommages a infliger
     */
    void hurt(Degats p_degats)
    {
	m_perso.etreBlesse(p_degats);
    }

    /**
     * force un personnage à retirer son initiative
     */
    void regenInit()
    {
	m_perso.genInit();
    }
}
