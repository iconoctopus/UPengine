/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.duckdns.spacedock.upengine.upmaster;

import org.duckdns.spacedock.upengine.libupsystem.Perso;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
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
public class UnitStaticAddDelFighterTest
{

    @Test
    public void testFighterAddDelErreur()
    {
	SessionManager manager = SessionManager.getInstance();

	try
	{
	    manager.addFighter(0);
	    fail();
	}
	catch (IllegalArgumentException e)
	{
	    Assert.assertEquals("paramétre aberrant:RM doit être strictement positif", e.getMessage());
	}
	try
	{
	    manager.addFighter(-1);
	    fail();
	}
	catch (IllegalArgumentException e)
	{
	    Assert.assertEquals("paramétre aberrant:RM doit être strictement positif", e.getMessage());
	}
    }

    @Test
    public void testFighterAddDelNominal() throws Exception
    {
	//On mocke la création du CharacterAssembly
	CharacterAssembly assemblyMock = PowerMockito.mock(CharacterAssembly.class);
	whenNew(CharacterAssembly.class).withArguments(1).thenReturn(assemblyMock);
	whenNew(CharacterAssembly.class).withArguments(2).thenReturn(assemblyMock);
	whenNew(CharacterAssembly.class).withArguments(3).thenReturn(assemblyMock);

	SessionManager manager = SessionManager.getInstance();
	int fighter1 = manager.addFighter(1).getIndex();
	int fighter2 = manager.addFighter(2).getIndex();

	//on vérifie que les combattants sont ajoutés en ordre linéaire
	Assert.assertEquals(fighter1 + 1, fighter2);
	int fighter3 = manager.addFighter(3).getIndex();
	Assert.assertEquals(fighter2 + 1, fighter3);

	//on supprime celui du milieu et l'on vérifie que c'est bien effectué
	manager.delFighter(fighter2);
	try
	{
	    manager.getFighterName(fighter2);
	    fail();
	}
	catch (NullPointerException e)
	{
	}

	//l'ajout d'un combattant ira au premier indice libre, donc celui du milieu dans ce cas
	Assert.assertEquals(fighter2, manager.addFighter(1).getIndex());
    }
}
