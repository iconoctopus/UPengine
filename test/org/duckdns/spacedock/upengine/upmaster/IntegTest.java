/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.duckdns.spacedock.upengine.upmaster;

import org.duckdns.spacedock.upengine.libupsystem.Arme;
import org.duckdns.spacedock.upengine.libupsystem.Arme.Degats;
import org.duckdns.spacedock.upengine.libupsystem.Inventaire;
import org.junit.Assert;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author ykonoclast
 */
public class IntegTest
{//TODO tests d'intégration : faire regénérer init du perso et faire un tour de combat "à blanc"

    @Test
    public void testFighterAddDel()
    {
	//test création correcte
	SessionManager manager = SessionManager.getInstance();
	int fighter1 = manager.addFighter(1).getIndex();
	int fighter2 = manager.addFighter(2).getIndex();
	Assert.assertEquals(fighter1 + 1, fighter2);
	int fighter3 = manager.addFighter(3).getIndex();
	Assert.assertEquals(fighter2 + 1, fighter3);
	try
	{
	    manager.addFighter(0);
	    fail();
	}
	catch (IllegalArgumentException e)
	{
	    Assert.assertEquals("paramétre aberrant:RM doit être compris entre 1 et 5 inclus", e.getMessage());
	}
	try
	{
	    manager.addFighter(-1);
	    fail();
	}
	catch (IllegalArgumentException e)
	{
	    Assert.assertEquals("paramétre aberrant:RM doit être compris entre 1 et 5 inclus", e.getMessage());
	}
	try
	{
	    manager.addFighter(6);
	    fail();
	}
	catch (IllegalArgumentException e)
	{
	    Assert.assertEquals("paramétre aberrant:RM doit être compris entre 1 et 5 inclus", e.getMessage());
	}

	//test ND en esquive et sans
	Assert.assertEquals(10, manager.getFighterND(fighter1, 0, false));
	Assert.assertEquals(15, manager.getFighterND(fighter2, 2, true));
	manager.setPieceArmure(fighter2, 7, 0, 0, Inventaire.ZoneEmplacement.CORPS);//ajout d'une cuirasse pour modifier le ND notamment d'esquive
	Assert.assertEquals(10, manager.getFighterND(fighter2, 2, true));
	manager.delPieceArmure(fighter2, Inventaire.ZoneEmplacement.CORPS);

	Assert.assertEquals(25, manager.getFighterND(fighter3, 3, false));

	//test des libellés de persos
	Assert.assertEquals("PersoRM1", manager.getName(fighter1));
	Assert.assertEquals("PersoRM2", manager.getName(fighter2));
	Assert.assertEquals("PersoRM3", manager.getName(fighter3));

	//test cohérence de la liste
	manager.delFighter(fighter2);
	Assert.assertEquals("PersoRM1", manager.getName(fighter2 - 1));
	Assert.assertEquals("PersoRM3", manager.getName(fighter2 + 1));

	try
	{
	    manager.getName(fighter2);
	    fail();
	}
	catch (NullPointerException e)
	{

	}
    }

    @Test
    public void testFighterOperations()
    {
	SessionManager manager = SessionManager.getInstance();
	int fighter = manager.addFighter(3).getIndex();

	//test sur l'ajout-retrait d'arme et de boucliers (notamment sur l'impossibilité d'avoir arme à deux mains et bouclier en même temps)
	manager.setWeapon(fighter, 7, Arme.QualiteArme.superieure, Arme.EquilibrageArme.mauvais);
	Assert.assertEquals("rapière de qualité supérieure et équilibrage mauvais", manager.getCurrentWeaponName(fighter));
	manager.delWeapon(fighter);
	Assert.assertEquals("mains nues", manager.getCurrentWeaponName(fighter));
	manager.setWeapon(fighter, 14, Arme.QualiteArme.superieure, Arme.EquilibrageArme.mauvais);
	Assert.assertEquals("épée à deux mains de qualité supérieure et équilibrage mauvais", manager.getCurrentWeaponName(fighter));
	manager.setBouclier(fighter, 0, 0, 0);
	Assert.assertEquals("mains nues", manager.getCurrentWeaponName(fighter));
	Assert.assertEquals("targe en métal", manager.getBouclierName(fighter));
	manager.setWeapon(fighter, 14, Arme.QualiteArme.superieure, Arme.EquilibrageArme.mauvais);
	Assert.assertEquals("épée à deux mains de qualité supérieure et équilibrage mauvais", manager.getCurrentWeaponName(fighter));
	Assert.assertEquals("aucun", manager.getBouclierName(fighter));
	manager.setWeapon(fighter, 7, Arme.QualiteArme.superieure, Arme.EquilibrageArme.mauvais);
	Assert.assertEquals("rapière de qualité supérieure et équilibrage mauvais", manager.getCurrentWeaponName(fighter));

	//test sur l'ajout-retrait d'armure
	manager.setPieceArmure(fighter, 0, 0, 0, Inventaire.ZoneEmplacement.TETE);
	Assert.assertEquals("casque complet en plates", manager.getPieceArmureName(fighter, Inventaire.ZoneEmplacement.TETE));
	manager.setPieceArmure(fighter, 7, 0, 0, Inventaire.ZoneEmplacement.CORPS);
	Assert.assertEquals("cuirasse en plates", manager.getPieceArmureName(fighter, Inventaire.ZoneEmplacement.CORPS));
	manager.setPieceArmure(fighter, 1, 0, 0, Inventaire.ZoneEmplacement.TETE);
	Assert.assertEquals("casque ouvert en plates", manager.getPieceArmureName(fighter, Inventaire.ZoneEmplacement.TETE));
	manager.delPieceArmure(fighter, Inventaire.ZoneEmplacement.CORPS);
	Assert.assertEquals("aucun", manager.getPieceArmureName(fighter, Inventaire.ZoneEmplacement.CORPS));
	manager.delPieceArmure(fighter, Inventaire.ZoneEmplacement.TETE);
	Assert.assertEquals("aucun", manager.getPieceArmureName(fighter, Inventaire.ZoneEmplacement.TETE));

	//cas d'erreur : ajout au mauvais emplacement
	try
	{
	    manager.setPieceArmure(fighter, 6, 0, 0, Inventaire.ZoneEmplacement.TETE);
	    fail();
	}
	catch (IllegalStateException e)
	{
	    Assert.assertEquals("emploi de la mauvaise méthode dans ce contexte:localisation incorrecte", e.getMessage());
	}

	//test sur le ND cible
	manager.setTargetND(fighter, 23);
	Assert.assertEquals(23, manager.getTargetND(fighter));

	manager.setTargetND(fighter, 0);
	Assert.assertEquals(0, manager.getTargetND(fighter));

	try
	{
	    manager.setTargetND(fighter, -56);
	    fail();
	}
	catch (IllegalArgumentException e)
	{
	    Assert.assertEquals("paramétre aberrant:ND:-56", e.getMessage());
	}

	//test succinct sur les blessures
	SessionManager.HealthReport report = manager.hurt(fighter, new Degats(1000, 4));
	Assert.assertEquals(true, report.isOut());
	Assert.assertEquals(true, report.isStunned());
    }

    @Test
    public void testChronoPhaseTurn()
    {
	SessionManager manager = SessionManager.getInstance();

	int turn, phase;

	for (int i = 1; i <= 100; ++i)
	{
	    turn = manager.getCurrentTurn();
	    phase = manager.getCurrentPhase();

	    if (phase == 10)
	    {
		phase = 1;
		++turn;
	    }
	    else
	    {
		++phase;
	    }
	    manager.advancePhase();
	    Assert.assertEquals(phase, manager.getCurrentPhase());
	    Assert.assertEquals(turn, manager.getCurrentTurn());
	}
    }
}
