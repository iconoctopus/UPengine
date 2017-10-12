/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.duckdns.spacedock.upengine.upmaster;

import java.util.List;
import org.duckdns.spacedock.upengine.libupsystem.Arme;
import org.duckdns.spacedock.upengine.libupsystem.Arme.Degats;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.times;
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
public class UnitStaticAttackTest
{

    @Test
    public void testAttack() throws Exception
    {
	CharacterAssembly assemblyMock = PowerMockito.mock(CharacterAssembly.class);
	whenNew(CharacterAssembly.class).withArguments(1).thenReturn(assemblyMock);
	when(assemblyMock.isActive(1)).thenReturn(true);

	SessionManager manager = SessionManager.getInstance();
	manager.addFighter(1);

	Degats degatsMock = PowerMockito.mock(Degats.class);

	//test du cas ou le combattant est actif et possède une seconde action derrière
	SessionManager.AttackReport report1 = new SessionManager.AttackReport(degatsMock, true, true, null);
	when(assemblyMock.attack(1)).thenReturn(report1);

	assertEquals(report1, manager.makeFighterAttack(0));//on vérifie que le bon rapport est renvoyé
	verify(assemblyMock, times(2)).isActive(1);

	when(assemblyMock.isActive(2)).thenReturn(true);
	List<Integer> listActive = manager.advancePhase();
	assertEquals(1, listActive.size());//on vérifie que la liste des persos actif en contient un
	assertEquals(0, (int) listActive.get(0));//et que c'est le combattant ajouté précédemment

	//test du cas ou le combattant est actif mais ne l'est plus à l'issue de l'attaque
	SessionManager.AttackReport report2 = new SessionManager.AttackReport(degatsMock, true, false, null);
	when(assemblyMock.attack(2)).thenReturn(report2);

	assertEquals(report2, manager.makeFighterAttack(0));
	assertFalse(manager.isAnyoneActive());

	//test du cas ou le combattant n'est pas actif du tout
	when(assemblyMock.isActive(2)).thenReturn(false);
	try
	{
	    manager.makeFighterAttack(0);
	}
	catch (IllegalArgumentException e)
	{
	    assertEquals("paramétre aberrant:perso inactif", e.getMessage());
	}
    }
}
