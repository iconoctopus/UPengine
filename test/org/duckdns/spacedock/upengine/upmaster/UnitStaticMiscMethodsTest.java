/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.duckdns.spacedock.upengine.upmaster;

import org.duckdns.spacedock.upengine.libupsystem.Arme;
import org.duckdns.spacedock.upengine.libupsystem.Inventaire;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.verify;
import org.powermock.api.mockito.PowerMockito;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
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
public class UnitStaticMiscMethodsTest
{

    private int currentFighter;
    private CharacterAssembly assemblyMock;
    private final SessionManager manager = SessionManager.getInstance();

    @Before
    public void setUp() throws Exception
    {
	assemblyMock = PowerMockito.mock(CharacterAssembly.class);
	whenNew(CharacterAssembly.class).withArguments(1).thenReturn(assemblyMock);
	currentFighter = manager.addFighter(1).getIndex();
    }

    @Test
    public void testHurt() throws Exception
    {
	when(assemblyMock.getNbDramaWounds()).thenReturn(2);
	when(assemblyMock.getNbFleshWounds()).thenReturn(5);
	when(assemblyMock.isStunned()).thenReturn(true);
	when(assemblyMock.isOut()).thenReturn(false);

	Arme.Degats degatsMock = PowerMockito.mock(Arme.Degats.class);

	SessionManager.HealthReport report = manager.hurtFighter(currentFighter, degatsMock);

	verify(assemblyMock).hurt(degatsMock);//on  vérifie que la bonne méthode est appelée dans le manager

	assertEquals(2, report.getDramaWounds());
	assertEquals(5, report.getFleshWounds());
	assertFalse(report.isOut());
	assertTrue(report.isStunned());
    }

    @Test
    public void testLibelles() throws Exception
    {
	manager.getFighterName(currentFighter);
	verify(assemblyMock).getCharName();
	manager.setFighterName(currentFighter, "le caca c'est délicieux");
	verify(assemblyMock).setCharName("le caca c'est délicieux");
    }

    @Test
    public void testWeapons()
    {
	manager.setFighterWeapon(currentFighter, 1, Arme.QualiteArme.maitre, Arme.EquilibrageArme.bon);
	verify(assemblyMock).setCurrentWeapon(1, Arme.QualiteArme.maitre, Arme.EquilibrageArme.bon);

	manager.getFighterCurrentWeaponName(currentFighter);
	verify(assemblyMock).getCurrentWeaponName();

	manager.delFighterWeapon(currentFighter);
	verify(assemblyMock).delWeapon();
    }

    @Test
    public void testArmure()
    {
	manager.setFighterArmourPart(currentFighter, 1, 2, 3, Inventaire.ZoneEmplacement.TETE);
	verify(assemblyMock).setArmourPart(1, 2, 3, Inventaire.ZoneEmplacement.TETE);

	manager.getFighterArmourPartName(currentFighter, Inventaire.ZoneEmplacement.TETE);
	verify(assemblyMock).getArmourPartName(Inventaire.ZoneEmplacement.TETE);

	manager.delFighterArmourPart(currentFighter, Inventaire.ZoneEmplacement.TETE);
	verify(assemblyMock).delArmourPart(Inventaire.ZoneEmplacement.TETE);
    }

    @Test
    public void testBouclier()
    {
	manager.setFighterShield(currentFighter, 1, 2, 3);
	verify(assemblyMock).setShield(1, 2, 3);

	manager.getFighterShieldName(currentFighter);
	verify(assemblyMock).getShieldName();

	manager.delFighterShield(currentFighter);
	verify(assemblyMock).delShield();
    }

    @Test
    public void testND()
    {
	//cas nominal
	manager.getFighterTargetDefence(currentFighter);
	verify(assemblyMock).getTargetDefence();

	manager.setFighterTargetDefence(currentFighter, 23);
	verify(assemblyMock).setTargetDefence(23);

	//cas d'erreur sur défense négative
	try
	{
	    manager.setFighterTargetDefence(currentFighter, -1);
	    fail();
	}
	catch (IllegalArgumentException e)
	{
	    assertEquals("paramétre aberrant:défense:-1", e.getMessage());
	}
    }

    @Test
    public void testGetDefensePassive()
    {
	manager.getFighterDefence(currentFighter, 3, true);
	verify(assemblyMock).getFighterDefense(3, true);
    }
}
