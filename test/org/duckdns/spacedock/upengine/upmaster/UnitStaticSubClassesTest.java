/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.duckdns.spacedock.upengine.upmaster;

import java.util.ArrayList;
import org.duckdns.spacedock.upengine.libupsystem.Arme;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 * @author ykonoclast
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(//pour les méthodes statiques c'est la classe appelante qui doit apparaître ici, pour les classes final c'est la classe appelée (donc UPReferenceSysteme n'apparaît ici que pour son caractère final et pas pour sa méthode getInstance()

	{//les classes final, appelant du statique et les classes subissant un whennew
	    SessionManager.class, CharacterAssembly.class, Arme.Degats.class
	})
public class UnitStaticSubClassesTest
{

    @Test
    public void testCreationReport()
    {
	ArrayList<Integer> listActions = new ArrayList<>();
	listActions.add(5);
	listActions.add(2);

	SessionManager.CreationReport report1 = new SessionManager.CreationReport(0, true, listActions);
	SessionManager.CreationReport report2 = new SessionManager.CreationReport(7, false, listActions);

	assertTrue(report1.isActive());
	assertFalse(report2.isActive());

	assertEquals(0, report1.getIndex());
	assertEquals(7, report2.getIndex());

	assertEquals(listActions, report1.getActionsLeft());
	assertEquals(listActions, report2.getActionsLeft());
    }

    @Test
    public void testAttackReport()
    {
	Arme.Degats degatsMock = PowerMockito.mock(Arme.Degats.class);

	ArrayList<Integer> listActions = new ArrayList<>();
	listActions.add(5);

	SessionManager.AttackReport report1 = new SessionManager.AttackReport(degatsMock, false, true, listActions);
	SessionManager.AttackReport report2 = new SessionManager.AttackReport(degatsMock, true, false, listActions);

	assertTrue(report1.isStillActive());
	assertFalse(report2.isStillActive());
	assertFalse(report1.assess());
	assertTrue(report2.assess());

	assertEquals(degatsMock, report1.getDamage());
	assertEquals(degatsMock, report2.getDamage());

	assertEquals(listActions, report1.getActionsLeft());
	assertEquals(listActions, report2.getActionsLeft());
    }
}
