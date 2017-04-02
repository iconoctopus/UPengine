/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.duckdns.spacedock.upengine.upmaster;

import org.duckdns.spacedock.upengine.libupsystem.Arme;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
	SessionManager.CreationReport report1 = new SessionManager.CreationReport(0, true);
	SessionManager.CreationReport report2 = new SessionManager.CreationReport(7, false);

	assertTrue(report1.assess());
	assertFalse(report2.assess());

	assertEquals(0, report1.getIndex());
	assertEquals(7, report2.getIndex());
    }

    @Test
    public void testAttackReport()
    {
	Arme.Degats degatsMock = PowerMockito.mock(Arme.Degats.class);

	SessionManager.AttackReport report1 = new SessionManager.AttackReport(degatsMock, false, true);
	SessionManager.AttackReport report2 = new SessionManager.AttackReport(degatsMock, true, false);

	assertTrue(report1.isStillActive());
	assertFalse(report2.isStillActive());
	assertFalse(report1.assess());
	assertTrue(report2.assess());

	assertEquals(degatsMock, report1.getDamage());
	assertEquals(degatsMock, report2.getDamage());

    }

    @Test
    public void testHealthReport()
    {
	SessionManager.HealthReport report1 = new SessionManager.HealthReport(0, 0, true, false);
	SessionManager.HealthReport report2 = new SessionManager.HealthReport(17, 25, false, true);

	assertEquals(0, report1.getDramaWounds());
	assertEquals(25, report2.getDramaWounds());

	assertEquals(0, report1.getFleshWounds());
	assertEquals(17, report2.getFleshWounds());

	assertTrue(report1.isStunned());
	assertFalse(report1.isOut());

	assertFalse(report1.isOut());
	assertTrue(report1.isStunned());
    }
}
