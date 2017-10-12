/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.duckdns.spacedock.upengine.upmaster;

import org.duckdns.spacedock.upengine.libupsystem.Arme;
import org.duckdns.spacedock.upengine.libupsystem.ArmeCaC;
import org.duckdns.spacedock.upengine.libupsystem.ArmeDist;
import org.duckdns.spacedock.upengine.libupsystem.Armure;
import org.duckdns.spacedock.upengine.libupsystem.Bouclier;
import org.duckdns.spacedock.upengine.libupsystem.Perso;
import org.duckdns.spacedock.upengine.libupsystem.PieceArmure;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author ykonoclast
 */
public class IntegEquipementTest
{

    CharacterAssembly assemblyRM3;

    @Before
    public void setUp()
    {
	assemblyRM3 = new CharacterAssembly(3);
    }

    @Test
    public void testAjoutIntegArmure() throws Exception
    {
	PieceArmure casque = new PieceArmure(0, 1, 0);
	PieceArmure cuirasse = new PieceArmure(7, 0, 0);
	PieceArmure jambiereGauche = new PieceArmure(5, 0, 0);
	PieceArmure jambiereDroite = new PieceArmure(5, 0, 0);
	PieceArmure brassiereGauche = new PieceArmure(4, 0, 0);
	PieceArmure brassiereDroite = new PieceArmure(4, 0, 0);
	PieceArmure botteGauche = new PieceArmure(6, 0, 0);
	PieceArmure botteDroite = new PieceArmure(6, 0, 0);
	PieceArmure ganteletGauche = new PieceArmure(3, 0, 2);
	PieceArmure ganteletDroit = new PieceArmure(3, 0, 2);

	//On ajoute un gantelet à un perso il n'y pas assez de points pour changer le ND et la réduction des dégâts
	assemblyRM3.setArmourPart(3, 0, 2, Inventaire.PartieCorps.MAINGAUCHE);
	Assert.assertEquals("gantelet en cuir clouté", assemblyRM3.getArmourPartName(Inventaire.PartieCorps.MAINGAUCHE));
	Assert.assertEquals(25, assemblyRM3.getFighterDefense(0));

	//vérification du cas nominal de l'ajout, pour toutes les zones
	assemblyRM3.setArmourPart(0, 1, 0, Inventaire.PartieCorps.TETE);
	Assert.assertEquals("casque complet moderne de facture très lourde", assemblyRM3.getArmourPartName(Inventaire.PartieCorps.TETE));
	assemblyRM3.setArmourPart(7, 0, 0, Inventaire.PartieCorps.CORPS);
	Assert.assertEquals("cuirasse en plates", assemblyRM3.getArmourPartName(Inventaire.PartieCorps.CORPS));
	assemblyRM3.setArmourPart(5, 0, 0, Inventaire.PartieCorps.JAMBEDROITE);
	Assert.assertEquals("jambière en plates", assemblyRM3.getArmourPartName(Inventaire.PartieCorps.JAMBEDROITE));
	assemblyRM3.setArmourPart(5, 0, 0, Inventaire.PartieCorps.JAMBEGAUCHE);
	Assert.assertEquals("jambière en plates", assemblyRM3.getArmourPartName(Inventaire.PartieCorps.JAMBEGAUCHE));
	assemblyRM3.setArmourPart(4, 0, 0, Inventaire.PartieCorps.BRASDROIT);
	Assert.assertEquals("brassière en plates", assemblyRM3.getArmourPartName(Inventaire.PartieCorps.BRASDROIT));
	assemblyRM3.setArmourPart(4, 0, 0, Inventaire.PartieCorps.BRASGAUCHE);
	Assert.assertEquals("brassière en plates", assemblyRM3.getArmourPartName(Inventaire.PartieCorps.BRASGAUCHE));
	assemblyRM3.setArmourPart(6, 0, 0, Inventaire.PartieCorps.PIEDDROIT);
	Assert.assertEquals("botte en plates", assemblyRM3.getArmourPartName(Inventaire.PartieCorps.PIEDDROIT));
	assemblyRM3.setArmourPart(6, 0, 0, Inventaire.PartieCorps.PIEDGAUCHE);
	Assert.assertEquals("botte en plates", assemblyRM3.getArmourPartName(Inventaire.PartieCorps.PIEDGAUCHE));
	assemblyRM3.setArmourPart(3, 0, 2, Inventaire.PartieCorps.MAINDROITE);
	Assert.assertEquals("gantelet en cuir clouté", assemblyRM3.getArmourPartName(Inventaire.PartieCorps.MAINDROITE));

	//récupération de l'armure totale
	Assert.assertEquals(34, assemblyRM3.getFighterDefense(1));
	Assert.assertEquals(31, assemblyRM3.getFighterDefense(2));

	//suppression de l'une des pièce d'armure et vérification qu'elle est effective
	assemblyRM3.delArmourPart(Inventaire.PartieCorps.PIEDGAUCHE);
	Assert.assertEquals("aucun", assemblyRM3.getArmourPartName(Inventaire.PartieCorps.PIEDGAUCHE));

	//récupération de l'armure totale (avec le trou dans la liste au niveau du pied gauche)
	Assert.assertEquals(33, assemblyRM3.getFighterDefense(1));
	Assert.assertEquals(35, assemblyRM3.getFighterDefense(0));
	Assert.assertEquals(31, assemblyRM3.getFighterDefense(2));

	//test de l'ajout d'un bouclier
	assemblyRM3.setShield(0, 2);
	Assert.assertEquals("targe avec blindage", assemblyRM3.getShieldName());
	Assert.assertEquals(34, assemblyRM3.getFighterDefense(2));//le bouclier doit avoir fait augmenter le type général de l'armure en plus de lui avoir fait passer un rang
	Assert.assertEquals(31, assemblyRM3.getFighterDefense(3));
	Assert.assertEquals(30, assemblyRM3.getFighterDefense(4));

	//on retire le bouclier, tout doit redevenir comme avant
	assemblyRM3.delShield();
	Assert.assertEquals(33, assemblyRM3.getFighterDefense(1));
	Assert.assertEquals(35, assemblyRM3.getFighterDefense(0));
	Assert.assertEquals(31, assemblyRM3.getFighterDefense(2));
    }

    @Test
    public void testIntegArmes()
    {
	assemblyRM3.setCurrentWeapon(0, Arme.QualiteArme.maitre, Arme.EquilibrageArme.mauvais);
	Assert.assertEquals("cimeterre de maître", assemblyRM3.getCurrentWeaponName());

	Arme arme2 = new ArmeCaC(2, Arme.QualiteArme.superieure, Arme.EquilibrageArme.mauvais);
	Assert.assertEquals("épée courte de qualité supérieure et équilibrage mauvais", arme2.toString());
	Arme arme3 = new ArmeCaC(1, Arme.QualiteArme.inferieure, Arme.EquilibrageArme.bon);
	Assert.assertEquals("coutelas de qualité inférieure et équilibrage bon", arme3.toString());
	Arme arme4 = new ArmeDist(42, Arme.QualiteArme.moyenne, Arme.EquilibrageArme.normal);
	Assert.assertEquals("pistolet lourd de qualité moyenne et équilibrage normal", arme4.toString());
    }
}
