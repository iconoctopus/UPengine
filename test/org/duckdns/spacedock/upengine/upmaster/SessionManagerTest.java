/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.duckdns.spacedock.upengine.upmaster;

import org.duckdns.spacedock.upengine.libupsystem.Arme;
import org.duckdns.spacedock.upengine.libupsystem.Arme.Degats;
import org.junit.Assert;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author iconoctopus
 */
public class SessionManagerTest
{

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

	//test ND en esquive et sans, comme il n'y a pas d'armure cela ne devrait pas avoir d'effet
	Assert.assertEquals(10, manager.getFighterND(fighter1, 0, false));
	Assert.assertEquals(15, manager.getFighterND(fighter2, 2, true));//TODO tester avec de l'armure
	Assert.assertEquals(25, manager.getFighterND(fighter3, 3, false));
	try
	{
	    manager.getFighterND(-1, -1, false);//le second -1 ne doit pas générer d'erreur car ça DOIT pêter avant
	    fail();
	}
	catch (IllegalArgumentException e)
	{
	    Assert.assertEquals("paramétre aberrant:indice:-1", e.getMessage());
	}

	//test des libellés de persos
	Assert.assertEquals("PersoRM1", manager.getName(fighter1));
	Assert.assertEquals("PersoRM2", manager.getName(fighter2));
	Assert.assertEquals("PersoRM3", manager.getName(fighter3));
	try
	{
	    manager.getName(-1);
	    fail();
	}
	catch (IllegalArgumentException e)
	{
	    Assert.assertEquals("paramétre aberrant:indice:-1", e.getMessage());
	}

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

	//test sur l'ajout-retrait d'arme
	manager.setWeapon(fighter, 7, Arme.QualiteArme.superieure, Arme.EquilibrageArme.mauvais);
	Assert.assertEquals("rapière de qualité supérieure et équilibrage mauvais", manager.getCurrentWeaponName(fighter));
	try
	{
	    manager.getCurrentWeaponName(-1);
	    fail();
	}
	catch (IllegalArgumentException e)
	{
	    Assert.assertEquals("paramétre aberrant:indice:-1", e.getMessage());
	}
	try
	{
	    manager.setWeapon(-1, -1, Arme.QualiteArme.maitre, Arme.EquilibrageArme.bon);//le second -1 ne doit pas générer d'erreur car ça DOIT pêter avant
	    fail();
	}
	catch (IllegalArgumentException e)
	{
	    Assert.assertEquals("paramétre aberrant:indice:-1", e.getMessage());
	}

	//TODO ajouter et tester fonctionalité pour rengainer
	//test sur le ND cible
	manager.setTargetND(fighter, 23);
	Assert.assertEquals(23, manager.getTargetND(fighter));
	try
	{
	    manager.getTargetND(-1);
	    fail();
	}
	catch (IllegalArgumentException e)
	{
	    Assert.assertEquals("paramétre aberrant:indice:-1", e.getMessage());
	}
	manager.setTargetND(fighter, 0);
	try
	{
	    manager.setTargetND(-1, 56);
	    fail();
	}
	catch (IllegalArgumentException e)
	{
	    Assert.assertEquals("paramétre aberrant:indice:-1 ND:56", e.getMessage());
	}
	try
	{
	    manager.setTargetND(fighter, -56);
	    fail();
	}
	catch (IllegalArgumentException e)
	{
	    Assert.assertEquals("paramétre aberrant:indice:" + fighter + " ND:-56", e.getMessage());
	}

	//test succinct sur les blessures
	SessionManager.HealthReport report = manager.hurt(fighter, new Degats(1000, 4));
	Assert.assertEquals(true, report.isOut());
	Assert.assertEquals(true, report.isStunned());
	try
	{
	    manager.hurt(-1, null);//ca doit pêter avant qu'il n'y ait le NullPointerException
	    fail();
	}
	catch (IllegalArgumentException e)
	{
	    Assert.assertEquals("paramétre aberrant:indice:-1", e.getMessage());
	}
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
