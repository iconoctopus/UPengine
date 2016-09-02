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

/**
 * Contrôleur gérant la session à la fois comme singleton de configuration mais
 * aussi avec des méthodes de contrôle
 *
 * @author iconoctopus
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
	if (p_rm > 0 && p_rm < 6)
	{
	    CharacterAssembly newFighter;
	    if (m_indexIterator.hasNext())//il y a eu des libérations on renvoie donc la première case libre
	    {
		newIndex = (m_indexIterator.next()).intValue();
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

	    if (isActive)//si le combattant est actif dans la phase courante, on ajoute son indice à la liste idoine
	    {
		m_activeFightersIterator.add(newIndex);
	    }
	}
	else
	{
	    ErrorHandler.paramAberrant(PropertiesHandler.getInstance("upmaster").getErrorMessage("erreurRM"));
	}
	return (new CreationReport(newIndex, isActive));
    }

    /**
     * Supprime un combattant côté contrôleur
     *
     * @param p_index indice du combattant à supprimer
     */
    public void delFighter(int p_index)
    {
	if (p_index >= 0)
	{
	    m_indexIterator.add(p_index);
	    m_indexIterator.previous();//replace le curseur au cran d'avant afin que hasNext() puisse répondre true lors de sa prochaine interrogation
	    m_listFighters.set(p_index, null);//le combattant est supprimé de la liste (avec set() et pas remove() afin que sa case reste libre pour ne pas bordéliser les indices des autres
	    m_listActiveFighters.remove(Integer.valueOf(p_index));//on enlève le combattant de la liste des combatants actifs
	    m_activeFightersIterator = m_listActiveFighters.listIterator();//reset de l'itérateur associé
	}
	else
	{
	    ErrorHandler.paramAberrant(PropertiesHandler.getInstance("commonutils").getString("indice") + ":" + p_index);
	}
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
    public AttackReport attack(int p_index)
    {
	AttackReport result = new AttackReport(new Degats(0, 0), true, true);
	if (p_index >= 0)
	{

	    CharacterAssembly attacker = m_listFighters.get(p_index);
	    if (attacker.isActive(m_currentPhase))
	    {
		result = attacker.attack(m_currentPhase);
		if (!result.isStillAtive())//si le combattant n'est plus actif on le retire de la liste de combattans actifs
		{
		    m_listActiveFighters.remove(Integer.valueOf(p_index));
		    m_activeFightersIterator = m_listActiveFighters.listIterator();
		}
	    }
	    else
	    {
		ErrorHandler.paramAberrant(PropertiesHandler.getInstance("upmaster").getErrorMessage("persoInactif"));
	    }
	}
	else
	{
	    ErrorHandler.paramAberrant(PropertiesHandler.getInstance("commonutils").getString("indice") + ":" + p_index);
	}
	return result;
    }

    /**
     * Inflige des dégâts au personnage indiqué
     *
     * @param p_index indice du personnage ciblé
     * @param p_damage dégâts à infliger
     * @return une structure contenant le nombres de blessures légères et graves
     * ainsi que le statut sonné/inconscient du personnage
     */
    public HealthReport hurt(int p_index, Degats p_damage)
    {
	int nbFlesh = 0;
	int nbDrama = 0;
	boolean isStunned = false;
	boolean isOut = false;

	if (p_index >= 0)
	{
	    CharacterAssembly victim = m_listFighters.get(p_index);
	    victim.hurt(p_damage);

	    if (victim.isOut())//si la victime est éliminée on ne va pas plus loin dans l'évaluation de son statut
	    {
		isOut = true;
	    }
	    else
	    {
		isOut = false;
		nbFlesh = victim.getNbFleshWounds();
		nbDrama = victim.getNbDramaWounds();
		isStunned = victim.isStunned();
	    }
	}
	else
	{
	    ErrorHandler.paramAberrant(PropertiesHandler.getInstance("commonutils").getString("indice") + ":" + p_index + " " + PropertiesHandler.getInstance("upmaster").getString("damage") + ":" + p_damage);
	}
	return (new HealthReport(nbFlesh, nbDrama, isStunned, isOut));
    }

    /**
     * renvoie le nom d'un combattant
     *
     * @param p_index son indice dans les listes
     * @return la chaine représentant son nom, issue de la couche libupsystem
     */
    public String getName(int p_index)
    {
	String result = "";
	if (p_index >= 0)
	{
	    result = m_listFighters.get(p_index).getLibellePerso();
	}
	else
	{
	    ErrorHandler.paramAberrant(PropertiesHandler.getInstance("commonutils").getString("indice") + ":" + p_index);
	}
	return result;
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
    public void setCurrentWeapon(int p_index, int p_weaponId, Arme.QualiteArme p_quality, Arme.EquilibrageArme p_balance)
    {
	if (p_weaponId >= 0)
	{
	    m_listFighters.get(p_index).setCurrentWeapon(p_weaponId, p_quality, p_balance);
	}
	else
	{
	    ErrorHandler.paramAberrant(PropertiesHandler.getInstance("commonutils").getString("indice") + ":" + p_index);
	}
    }

    /**
     * @param p_index indice du combattant
     * @return le nom de l'arme portée par le combattant visé
     */
    public String getCurrentWeaponName(int p_index)
    {
	String result = "";
	if (p_index >= 0)
	{
	    result = m_listFighters.get(p_index).getCurrentWeaponName();
	}
	else
	{
	    ErrorHandler.paramAberrant(PropertiesHandler.getInstance("commonutils").getString("indice") + ":" + p_index);
	}
	return result;
    }

    /**
     * @param p_index indice du combattant
     * @param p_ND le ND de la cible
     */
    public void setTargetND(int p_index, int p_ND)
    {
	if (p_index >= 0 && p_ND >= 0)
	{
	    m_listFighters.get(p_index).setTargetND(p_ND);
	}
	else
	{
	    ErrorHandler.paramAberrant(PropertiesHandler.getInstance("commonutils").getString("indice") + ":" + p_index + " " + PropertiesHandler.getInstance("upmaster").getString("ND") + ":" + p_ND);
	}
    }

    /**
     * @param p_index indice du combattant
     * @return le ND de la cible
     */
    public int getTargetND(int p_index)
    {
	int result = 0;
	if (p_index >= 0)
	{
	    result = m_listFighters.get(p_index).getTargetND();
	}
	else
	{
	    ErrorHandler.paramAberrant(PropertiesHandler.getInstance("commonutils").getString("indice") + ":" + p_index);
	}
	return result;
    }

    /**
     *
     * @param p_index
     * @param p_weapType
     * @param p_dodge
     * @return
     */
    public int getFighterND(int p_index, int p_weapType, boolean p_dodge)
    {
	int result = 0;
	if (p_index >= 0)
	{
	    result = m_listFighters.get(p_index).getFighterND(p_weapType, p_dodge);
	}
	else
	{
	    ErrorHandler.paramAberrant(PropertiesHandler.getInstance("commonutils").getString("indice") + ":" + p_index);
	}
	return result;
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

	public CreationReport(int p_index, boolean p_assessment)
	{
	    m_index = p_index;
	    m_assessment = p_assessment;
	}

	public int getIndex()
	{
	    return m_index;
	}

	public boolean assess()
	{
	    return (m_assessment);
	}
    }

    /**
     * classe représentant le résultat d'une attaque
     */
    public static class AttackReport
    {

	private final Degats m_damage;
	boolean m_assessment;
	private final boolean m_stillActive;

	public AttackReport(Degats p_damage, boolean p_assessment, boolean p_stillActive)
	{
	    m_damage = p_damage;
	    m_assessment = p_assessment;
	    m_stillActive = p_stillActive;
	}

	public Degats getDamage()
	{
	    return m_damage;
	}

	public boolean assess()
	{
	    return m_assessment;
	}

	public boolean isStillAtive()
	{
	    return m_stillActive;
	}
    }

    /**
     * classe permettant de représenter l'état de santé d'un personnage
     */
    public static class HealthReport
    {

	private final int m_fleshNumber;
	private final int m_dramaNumber;
	private final boolean m_isStunned;
	private final boolean m_isOut;

	public HealthReport(int p_flesh, int p_drama, boolean p_stunned, boolean p_out)
	{
	    m_fleshNumber = p_flesh;
	    m_dramaNumber = p_drama;
	    m_isStunned = p_stunned;
	    m_isOut = p_out;
	}

	public boolean isOut()
	{
	    return (m_isOut);
	}

	public boolean isStunned()
	{
	    return (m_isStunned);
	}

	public int getFleshWounds()
	{
	    return (m_fleshNumber);
	}

	public int getDramaWounds()
	{
	    return (m_dramaNumber);
	}
    }
}
