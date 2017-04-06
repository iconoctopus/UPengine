/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.duckdns.spacedock.upengine.upmaster;

import java.util.List;
import org.duckdns.spacedock.upengine.libupsystem.Perso;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
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
	    SessionManager.class, CharacterAssembly.class, Perso.Degats.class
	})
public class UnitStaticChronoTest
{

    @Test
    public void testChrono() throws Exception
    {
	CharacterAssembly assemblyMock = PowerMockito.mock(CharacterAssembly.class);
	whenNew(CharacterAssembly.class).withArguments(1).thenReturn(assemblyMock);

	SessionManager manager = SessionManager.getInstance();
	int fighter1 = manager.addFighter(1).getIndex();

	//test du cas ou le combattant est actif
	when(assemblyMock.isActive(2)).thenReturn(true);
	assertEquals(1, manager.getCurrentPhase());
	List<Integer> listActive = manager.advancePhase();
	assertEquals(1, listActive.size());
	assertEquals(0, (int) listActive.get(0));
	verify(assemblyMock).isActive(2);
	assertTrue(manager.isAnyoneActive());

	//test du cas où le combattant n'est pas actif
	when(assemblyMock.isActive(2)).thenReturn(false);
	assertEquals(2, manager.getCurrentPhase());
	listActive = manager.advancePhase();
	assertTrue(listActive.isEmpty());
	verify(assemblyMock).isActive(2);
	assertFalse(manager.isAnyoneActive());
	assertEquals(3, manager.getCurrentPhase());

	//test de l'enchaînement des phases et tours
	int turn = 1, phase = 3;
	boolean premierTour = true;

	for (int i = manager.getCurrentPhase(); i <= 100; ++i)
	{
	    if (premierTour && phase == 1)
	    {
		verify(assemblyMock).regenInit();
		premierTour = false;
	    }
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
