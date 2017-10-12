/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.duckdns.spacedock.upengine.upmaster;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.duckdns.spacedock.commonutils.ErrorHandler;
import org.duckdns.spacedock.commonutils.PropertiesHandler;
import org.duckdns.spacedock.upengine.libupsystem.Arme;
import org.duckdns.spacedock.upengine.libupsystem.Arme.Degats;
import org.duckdns.spacedock.upengine.libupsystem.EnsembleJauges;

/**
 * Contrôleur gérant la session à la fois comme singleton de configuration mais
 * aussi avec des méthodes de contrôle
 *
 * @author ykonoclast
 */
public class SessionManager
{

    /**
     * premier indice libre à la fin de la liste des combattants (il peut y a
     * voir des trous dans la liste)
     */
    private int m_currentIndex;

    /**
     * phase actuelle de la session de jeu
     */
    private int m_currentPhase;

    /**
     * tour actuel de la session de jeu
     */
    private int m_currentTurn;

    /**
     * liste des personnages
     */
    private final ArrayList<CharacterAssembly> m_listFighters = new ArrayList<>();

    /**
     * liste des indices libres dans la liste des combattants
     */
    private final LinkedList<Integer> m_listFreeIndex = new LinkedList<>();
    private final ListIterator<Integer> m_indexIterator = m_listFreeIndex.listIterator();

    /**
     * liste des personnages actifs dans la phase en cours
     */
    private final LinkedList<Integer> m_listActiveFighters = new LinkedList<>();
    private ListIterator<Integer> m_activeFightersIterator = m_listActiveFighters.listIterator();

    /**
     * instance unique du SessionMnager
     */
    private static SessionManager m_instance = null;//doit être statique pour être appelé par getInstance en contexte statique

    /**
     * pseudo-constructeur public garantissant un et un seul SessionManager par
     * lancement de l'application
     *
     * @return le SessionManager actuel si il a été créé, un nouveau sinon
     */
    public static SessionManager getInstance()//doit être statique pour être appelé hors instanciation d'un objet
    {
	if (m_instance == null)
	{
	    m_instance = new SessionManager();
	}
	return (m_instance);
    }

    /**
     * véritable constructeur, appelé seulement si le SessionManager n'a pas
     * encore été créé
     */
    private SessionManager()
    {
	m_currentPhase = 1;
	m_currentTurn = 1;
	m_currentIndex = 0;
    }

    /**
     * Crée un nouveau combattant côté controleur
     *
     * @param p_rm le RM du combattant à créer
     * @return l'indice qui a été affecté au nouveau combattant et un booleen
     * valant vrai si le personnage sera actif dans la phase en cours
     */
    public CreationReport addFighter(int p_rm)
    {
	int newIndex = 0;
	boolean isActive = false;
	ArrayList<Integer> listActions = new ArrayList<>();
	if (p_rm > 0)
	{
	    CharacterAssembly newFighter;
	    if (m_indexIterator.hasNext())//il y a eu des libérations on renvoie donc la première case libre
	    {
		newIndex = m_indexIterator.next();
		m_indexIterator.previous();//on a récupéré une valeur, il faut donc la supprimer de la liste car cet indice va désormais être occupé par un combattant
		m_indexIterator.remove();
		newFighter = new CharacterAssembly(p_rm);
		m_listFighters.set(newIndex, newFighter);//on ajoute le nouveau combattant à la liste à la place de celui qu'il remplace
	    }
	    else//pas de case libre, la première case libre est donc l'indice, on l'incrémente après l'avoir récupéré
	    {
		newIndex = m_currentIndex++;
		newFighter = new CharacterAssembly(p_rm);
		m_listFighters.add(newFighter);//le nouveau combattant est ajouté en queue
	    }

	    isActive = newFighter.isActive(m_currentPhase);
	    listActions = newFighter.getActions();

	    if (isActive)//si le combattant est actif dans la phase courante, on ajoute son indice à la liste idoine
	    {
		m_activeFightersIterator.add(newIndex);
	    }
	}
	else
	{
	    ErrorHandler.paramAberrant(PropertiesHandler.getInstance("upmaster").getErrorMessage("erreurRM"));
	}
	return (new CreationReport(newIndex, isActive, listActions));
    }

    /**
     * Supprime un combattant côté contrôleur
     *
     * @param p_index indice du combattant à supprimer
     */
    public void delFighter(int p_index)
    {
	m_indexIterator.add(p_index);
	m_indexIterator.previous();//replace le curseur au cran d'avant afin que hasNext() puisse répondre true lors de sa prochaine interrogation
	m_listFighters.set(p_index, null);//le combattant est supprimé de la liste (avec set() et pas remove() afin que sa case reste libre pour ne pas bordéliser les indices des autres
	m_listActiveFighters.remove(Integer.valueOf(p_index));//on enlève le combattant de la liste des combatants actifs
	m_activeFightersIterator = m_listActiveFighters.listIterator();//reset de l'itérateur associé
    }

    /**
     * passe à la phase suivante voire au tour suivant
     *
     * @return la liste des indices des combattants actifs
     */
    public List<Integer> advancePhase()
    {
	++m_currentPhase;
	m_listActiveFighters.clear();//la list des combattants actifs est vidée pour être reconstruite
	m_activeFightersIterator = m_listActiveFighters.listIterator();//on reset l'itérateur
	boolean newTurn = false;

	if (m_currentPhase > 10)//on doit passer au tour suivant
	{
	    newTurn = true;
	    ++m_currentTurn;
	    m_currentPhase = 1;
	}

	for (int index = 0; index < m_listFighters.size(); ++index)//parcours de la liste de tous les combattants
	{
	    CharacterAssembly currentfighter = m_listFighters.get(index);

	    if (currentfighter != null)//currentFighter peut très bien être null car on remplace juste les combattants retirés par des null pour conserver les indices
	    {
		if (newTurn)
		{
		    currentfighter.regenInit();//nouveau tour, les combattants voient leur initiative régénérée pour le nouveau tour
		}
		if (currentfighter.isActive(m_currentPhase))
		{
		    m_activeFightersIterator.add(index);//chaque personnage actif dans la phase courante est ajouté à la liste idoine
		}
	    }
	}
	return m_listActiveFighters;
    }

    /**
     * fait attaquer un personnage contre le ND de sa cible actuelle
     *
     * @param p_index l'indice du personnage
     * @return une structure contenant les dégâts infligés, un booléen indiquant
     * si l'attaque est un succès et un booléen indiquant si le combattant reste
     * actif après attaque
     */
    public AttackReport makeFighterAttack(int p_index)
    {
	AttackReport result = new AttackReport(new Degats(0, 0), true, true, new ArrayList<Integer>());

	CharacterAssembly attacker = m_listFighters.get(p_index);
	if (attacker.isActive(m_currentPhase))
	{
	    result = attacker.attack(m_currentPhase);
	    if (!result.isStillActive())//si le combattant n'est plus actif on le retire de la liste de combattans actifs
	    {
		m_listActiveFighters.remove(Integer.valueOf(p_index));
		m_activeFightersIterator = m_listActiveFighters.listIterator();
	    }
	}
	else
	{
	    ErrorHandler.paramAberrant(PropertiesHandler.getInstance("upmaster").getErrorMessage("persoInactif"));
	}

	return result;
    }

    /**
     * Inflige des dégâts au personnage indiqué
     *
     * @param p_index indice du personnage ciblé
     * @param p_damage dégâts à infliger
     * @return un rapport complet sur l'état vital du personnage
     */
    public EnsembleJauges.EtatVital hurtFighter(int p_index, Degats p_damage)
    {
	CharacterAssembly victim = m_listFighters.get(p_index);
	victim.hurt(p_damage);

	return victim.getEtatVital();
    }

    /**
     * renvoie le nom d'un combattant
     *
     * @param p_index son indice dans les listes
     * @return la chaine représentant son nom, issue de la couche libupsystem
     */
    public String getFighterName(int p_index)
    {
	return m_listFighters.get(p_index).getCharName();
    }

    public void setFighterName(int p_index, String p_name)
    {
	m_listFighters.get(p_index).setCharName(p_name);
    }

    /**
     * affecte l'arme passée en paramétre au combattant d'indice passé en
     * paramétre
     *
     * @param p_index
     * @param p_weaponId
     * @param p_quality
     * @param p_balance
     */
    public void setFighterWeapon(int p_index, int p_weaponId, Arme.QualiteArme p_quality, Arme.EquilibrageArme p_balance)
    {
	m_listFighters.get(p_index).setCurrentWeapon(p_weaponId, p_quality, p_balance);
    }

    /**
     * passe le combattant en question à mains nues
     *
     * @param p_index
     */
    public void delFighterWeapon(int p_index)
    {
	m_listFighters.get(p_index).delWeapon();
    }

    /**
     *
     * @param p_fighterIndex
     * @param p_zone
     * @return
     */
    public String getFighterArmourPartName(int p_fighterIndex, Inventaire.PartieCorps p_zone)
    {
	return m_listFighters.get(p_fighterIndex).getArmourPartName(p_zone);
    }

    /**
     *
     * @param p_fighterIndex
     * @param p_index
     * @param p_materiau
     * @param p_type
     * @param p_zone
     */
    public void setFighterArmourPart(int p_fighterIndex, int p_index, int p_materiau, int p_type, Inventaire.PartieCorps p_zone)
    {
	m_listFighters.get(p_fighterIndex).setArmourPart(p_index, p_materiau, p_type, p_zone);
    }

    /**
     *
     * @param p_fighterIndex
     * @param p_zone
     */
    public void delFighterArmourPart(int p_fighterIndex, Inventaire.PartieCorps p_zone)
    {
	m_listFighters.get(p_fighterIndex).delArmourPart(p_zone);
    }

    /**
     *
     * @param p_fighterIndex
     * @return
     */
    public String getFighterShieldName(int p_fighterIndex)
    {
	return m_listFighters.get(p_fighterIndex).getShieldName();
    }

    /**
     *
     * @param p_fighterIndex
     * @param p_index
     * @param p_type
     */
    public void setFighterShield(int p_fighterIndex, int p_index, int p_type)
    {
	m_listFighters.get(p_fighterIndex).setShield(p_index, p_type);
    }

    /**
     *
     * @param p_fighterIndex
     */
    public void delFighterShield(int p_fighterIndex)
    {
	m_listFighters.get(p_fighterIndex).delShield();
    }

    /**
     * @param p_index indice du combattant
     * @return le nom de l'arme portée par le combattant visé
     */
    public String getFighterCurrentWeaponName(int p_index)
    {
	return m_listFighters.get(p_index).getCurrentWeaponName();
    }

    /**
     * @param p_index indice du combattant
     * @param p_ND le ND de la cible
     */
    public void setFighterTargetDefence(int p_index, int p_ND)
    {
	if (p_ND >= 0)
	{
	    m_listFighters.get(p_index).setTargetDefence(p_ND);
	}
	else
	{
	    ErrorHandler.paramAberrant(PropertiesHandler.getInstance("upmaster").getString("defense") + ":" + p_ND);
	}
    }

    /**
     * @param p_index indice du combattant
     * @return le ND de la cible
     */
    public int getFighterTargetDefence(int p_index)
    {
	return m_listFighters.get(p_index).getTargetDefence();
    }

    /**
     *
     * @param p_index
     * @param p_weapType
     * @param p_dodge
     * @return
     */
    public int getFighterDefence(int p_index, int p_weapType)
    {
	return m_listFighters.get(p_index).getFighterDefense(p_weapType);
    }

    /**
     *
     * @param p_index
     * @return les actions restantes dans ce tour au combattant indiqué
     */
    public ArrayList<Integer> getFighterActions(int p_index)
    {
	return m_listFighters.get(p_index).getActions();
    }

    /**
     * @return la phase courante
     */
    public int getCurrentPhase()
    {
	return m_currentPhase;
    }

    /**
     * @return le tour courant
     */
    public int getCurrentTurn()
    {
	return m_currentTurn;
    }

    /**
     * @return un booléen indiquant si un personnage est actif dans la phase en
     * cours
     */
    public boolean isAnyoneActive()
    {
	boolean notEmpty = !m_listActiveFighters.isEmpty();
	return (notEmpty);
    }

    /**
     * classe représentant le résultat d'une action du SessionManager
     */
    public static class CreationReport
    {

	private final int m_index;
	private final boolean m_assessment;
	private final ArrayList<Integer> m_listActions;

	public CreationReport(int p_index, boolean p_isActive, ArrayList<Integer> p_ActionsLeft)
	{
	    m_index = p_index;
	    m_assessment = p_isActive;
	    m_listActions = p_ActionsLeft;
	}

	public int getIndex()
	{
	    return m_index;
	}

	public boolean isActive()
	{
	    return (m_assessment);
	}

	public ArrayList<Integer> getActionsLeft()
	{
	    return m_listActions;
	}
    }

    /**
     * classe représentant le résultat d'une attaque
     */
    public static class AttackReport
    {

	private final Degats m_damage;
	private final boolean m_assessment;
	private final boolean m_stillActive;
	private final ArrayList<Integer> m_listActions;

	public AttackReport(Degats p_damage, boolean p_assessment, boolean p_stillActive, ArrayList<Integer> p_ActionsLeft)
	{
	    m_damage = p_damage;
	    m_assessment = p_assessment;
	    m_stillActive = p_stillActive;
	    m_listActions = p_ActionsLeft;
	}

	public Degats getDamage()
	{
	    return m_damage;
	}

	public boolean assess()
	{
	    return m_assessment;
	}

	public boolean isStillActive()
	{
	    return m_stillActive;
	}

	public ArrayList<Integer> getActionsLeft()
	{
	    return m_listActions;
	}
    }
}
