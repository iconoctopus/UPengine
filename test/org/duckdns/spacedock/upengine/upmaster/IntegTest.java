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
	SessionManager manager = SessionManager.getInstance();
	int fighter1 = manager.addFighter(1).getIndex();
	int fighter2 = manager.addFighter(2).getIndex();
	int fighter3 = manager.addFighter(3).getIndex();

	//test ND en esquive et sans
	Assert.assertEquals(10, manager.getFighterDefence(fighter1, 0, false));
	Assert.assertEquals(15, manager.getFighterDefence(fighter2, 2, true));
	manager.setFighterArmourPart(fighter2, 7, 0, 0, Inventaire.ZoneEmplacement.CORPS);//ajout d'une cuirasse pour modifier le ND notamment d'esquive
	Assert.assertEquals(10, manager.getFighterDefence(fighter2, 2, true));
	manager.delFighterArmourPart(fighter2, Inventaire.ZoneEmplacement.CORPS);

	Assert.assertEquals(25, manager.getFighterDefence(fighter3, 3, false));

	//test des libellés de persos
	Assert.assertEquals("PersoRM1", manager.getFighterName(fighter1));
	Assert.assertEquals("PersoRM2", manager.getFighterName(fighter2));
	Assert.assertEquals("PersoRM3", manager.getFighterName(fighter3));

	manager.setFighterName(fighter2, "le caca c'est délicieux");
	Assert.assertEquals("le caca c'est délicieux", manager.getFighterName(fighter2));

	//test cohérence de la liste
	manager.delFighter(fighter2);
	Assert.assertEquals("PersoRM1", manager.getFighterName(fighter2 - 1));
	Assert.assertEquals("PersoRM3", manager.getFighterName(fighter2 + 1));

	try
	{
	    manager.getFighterName(fighter2);
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
	manager.setFighterWeapon(fighter, 7, Arme.QualiteArme.superieure, Arme.EquilibrageArme.mauvais);
	Assert.assertEquals("rapière de qualité supérieure et équilibrage mauvais", manager.getFighterCurrentWeaponName(fighter));
	manager.delFighterWeapon(fighter);
	Assert.assertEquals("mains nues", manager.getFighterCurrentWeaponName(fighter));
	manager.setFighterWeapon(fighter, 14, Arme.QualiteArme.superieure, Arme.EquilibrageArme.mauvais);
	Assert.assertEquals("épée à deux mains de qualité supérieure et équilibrage mauvais", manager.getFighterCurrentWeaponName(fighter));
	manager.setFighterShield(fighter, 0, 0, 0);
	Assert.assertEquals("mains nues", manager.getFighterCurrentWeaponName(fighter));
	Assert.assertEquals("targe en métal", manager.getFighterShieldName(fighter));
	manager.setFighterWeapon(fighter, 14, Arme.QualiteArme.superieure, Arme.EquilibrageArme.mauvais);
	Assert.assertEquals("épée à deux mains de qualité supérieure et équilibrage mauvais", manager.getFighterCurrentWeaponName(fighter));
	Assert.assertEquals("aucun", manager.getFighterShieldName(fighter));
	manager.setFighterWeapon(fighter, 7, Arme.QualiteArme.superieure, Arme.EquilibrageArme.mauvais);
	Assert.assertEquals("rapière de qualité supérieure et équilibrage mauvais", manager.getFighterCurrentWeaponName(fighter));

	//test sur l'ajout-retrait d'armure
	manager.setFighterArmourPart(fighter, 0, 0, 0, Inventaire.ZoneEmplacement.TETE);
	Assert.assertEquals("casque complet en plates", manager.getFighterArmourPartName(fighter, Inventaire.ZoneEmplacement.TETE));
	manager.setFighterArmourPart(fighter, 7, 0, 0, Inventaire.ZoneEmplacement.CORPS);
	Assert.assertEquals("cuirasse en plates", manager.getFighterArmourPartName(fighter, Inventaire.ZoneEmplacement.CORPS));
	manager.setFighterArmourPart(fighter, 1, 0, 0, Inventaire.ZoneEmplacement.TETE);
	Assert.assertEquals("casque ouvert en plates", manager.getFighterArmourPartName(fighter, Inventaire.ZoneEmplacement.TETE));
	manager.delFighterArmourPart(fighter, Inventaire.ZoneEmplacement.CORPS);
	Assert.assertEquals("aucun", manager.getFighterArmourPartName(fighter, Inventaire.ZoneEmplacement.CORPS));
	manager.delFighterArmourPart(fighter, Inventaire.ZoneEmplacement.TETE);
	Assert.assertEquals("aucun", manager.getFighterArmourPartName(fighter, Inventaire.ZoneEmplacement.TETE));

	//cas d'erreur : ajout au mauvais emplacement
	try
	{
	    manager.setFighterArmourPart(fighter, 6, 0, 0, Inventaire.ZoneEmplacement.TETE);
	    fail();
	}
	catch (IllegalStateException e)
	{
	    Assert.assertEquals("emploi de la mauvaise méthode dans ce contexte:localisation incorrecte", e.getMessage());
	}

	//test sur le ND cible
	manager.setFighterTargetDefence(fighter, 23);
	Assert.assertEquals(23, manager.getFighterTargetDefence(fighter));

	manager.setFighterTargetDefence(fighter, 0);
	Assert.assertEquals(0, manager.getFighterTargetDefence(fighter));

	//test succinct sur les blessures
	SessionManager.HealthReport report = manager.hurtFighter(fighter, new Degats(1000, 4));
	Assert.assertEquals(true, report.isOut());
	Assert.assertEquals(true, report.isStunned());
    }
}
