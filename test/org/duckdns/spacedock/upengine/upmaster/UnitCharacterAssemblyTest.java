/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.duckdns.spacedock.upengine.upmaster;

import org.duckdns.spacedock.upengine.libupsystem.Arme;
import org.duckdns.spacedock.upengine.libupsystem.Arme.Degats;
import org.duckdns.spacedock.upengine.libupsystem.ArmeCaC;
import org.duckdns.spacedock.upengine.libupsystem.ArmeDist;
import org.duckdns.spacedock.upengine.libupsystem.Inventaire;
import org.duckdns.spacedock.upengine.libupsystem.Perso;
import org.duckdns.spacedock.upengine.libupsystem.PieceArmure;
import org.duckdns.spacedock.upengine.libupsystem.RollUtils;
import org.duckdns.spacedock.upengine.libupsystem.RollUtils.RollResult;
import org.duckdns.spacedock.upengine.libupsystem.UPReference;
import org.duckdns.spacedock.upengine.upmaster.SessionManager.AttackReport;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.never;
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
	    Perso.class, CharacterAssembly.class, UPReference.class, RollResult.class, Inventaire.class, Degats.class
	})
public class UnitCharacterAssemblyTest
{

    private Perso persoMock;
    private Inventaire inventaireMock;
    private UPReference referenceMock;
    private CharacterAssembly assemblyTest;
    private PieceArmure pieceMock1;
    private PieceArmure pieceMock2;

    @Before
    public void setUp() throws Exception
    {
	//Création de mocks utilisés dans les tests
	persoMock = PowerMockito.mock(Perso.class);
	whenNew(Perso.class).withAnyArguments().thenReturn(persoMock);

	inventaireMock = PowerMockito.mock(Inventaire.class);
	when(persoMock.getInventaire()).thenReturn(inventaireMock);//on retourne le mock d'inventaire quand demandé

	referenceMock = PowerMockito.mock(UPReference.class);
	PowerMockito.mockStatic(UPReference.class);//nécessaire quand on mocke une classe statique
	when(UPReference.getInstance()).thenReturn(referenceMock);

	pieceMock1 = PowerMockito.mock(PieceArmure.class);
	pieceMock2 = PowerMockito.mock(PieceArmure.class);

	when(pieceMock1.toString()).thenReturn("piece mock 1");
	when(pieceMock2.toString()).thenReturn("piece mock 2");

	assemblyTest = new CharacterAssembly(3);
	//mock : perso(tostring, setlibelle), inventaire(getbouclier,), arme CaC et Dist(nbmains), UPReference(getModArme)
    }

    @Test
    public void testName()
    {
	//on mocke la réponse du perso
	when(persoMock.toString()).thenReturn("test unit");

	Assert.assertEquals("test unit", assemblyTest.getCharName());
	assemblyTest.setCharName("param unit");
	verify(persoMock).setLibellePerso("param unit");
    }

    @Test
    public void testWeaponAndShield() throws Exception
    {//TODO séparer cette méthode en plusieurs cas de test, probablement trop complexe à maintenir en l'état

	//pas d'arme : la méthode renvoie le libellé de mains nues
	when(referenceMock.getLblCatArmeCaC(0)).thenReturn("reponse mains nues");

	Assert.assertEquals("reponse mains nues", assemblyTest.getCurrentWeaponName());//on a correctement réagi à l'absence d'arme courante
	verify(persoMock).getInventaire();//appel pour récupérer l'inventaire
	verify(inventaireMock).getArmeCourante();//appel pour vérifier quelle est l'arme courante
	verify(referenceMock).getLblCatArmeCaC(0);//appel pour vérifier le nom de la cat d'arme mains nues

	//création de mocks pour les armes qui vont être ajoutées : les objets sont créés par la méthode setCurrentWeapon et donc leurs constructeurs et méthodes doivent être interceptés ici
	ArmeCaC arme1mainMock = PowerMockito.mock(ArmeCaC.class);
	whenNew(ArmeCaC.class).withArguments(3, Arme.QualiteArme.maitre, Arme.EquilibrageArme.bon).thenReturn(arme1mainMock);
	when(arme1mainMock.getNbMainsArme()).thenReturn(1);
	ArmeCaC arme2mainsMock = PowerMockito.mock(ArmeCaC.class);
	whenNew(ArmeCaC.class).withArguments(77, Arme.QualiteArme.inferieure, Arme.EquilibrageArme.mauvais).thenReturn(arme2mainsMock);
	when(arme2mainsMock.getNbMainsArme()).thenReturn(2);
	ArmeDist armeDistMock = PowerMockito.mock(ArmeDist.class);
	whenNew(ArmeDist.class).withArguments(2, Arme.QualiteArme.superieure, Arme.EquilibrageArme.normal).thenReturn(armeDistMock);

	//Ajout d'une arme de corp à corps
	assemblyTest.setCurrentWeapon(3, Arme.QualiteArme.maitre, Arme.EquilibrageArme.bon);
	verify(arme1mainMock).getNbMainsArme();//l'assembly regarde le nombre de mains
	verify(inventaireMock, never()).getBouclier(Inventaire.Lateralisation.GAUCHE);//on ne regarde pas le bouclier car l'arme est à une main
	verify(inventaireMock, never()).removeBouclier(Inventaire.Lateralisation.GAUCHE);//on ne le retire pas non plus
	verify(inventaireMock).addArme(arme1mainMock, Inventaire.Lateralisation.DROITE);//test du set
	when(inventaireMock.getArmeCourante()).thenReturn(arme1mainMock);//il faut mocker un set réussi côté inventaire
	when(arme1mainMock.toString()).thenReturn("test arme une main");
	Assert.assertEquals("test arme une main", assemblyTest.getCurrentWeaponName());//le get passe correctement à l'interrogation de l'arme

	//test du cas où il n'y a pas encore de bouclier
	Assert.assertEquals("aucun", assemblyTest.getShieldName());//exception aux tests unitaires, on interroge directement le propertiesHandler

	//ajout d'un bouclier, l'arme est toujours là
	PieceArmure bouclierMock = PowerMockito.mock(PieceArmure.class);
	whenNew(PieceArmure.class).withArguments(0, 0, 0, true).thenReturn(bouclierMock);
	when(bouclierMock.toString()).thenReturn("bouclier mock");

	assemblyTest.setShield(0, 0, 0);
	verify(inventaireMock, never()).removeArme(Inventaire.Lateralisation.DROITE);//pas besoin d'aller retirer le bouclier
	verify(inventaireMock).addBouclier(bouclierMock, Inventaire.Lateralisation.GAUCHE);//le bouclier est bien ajouté

	when(inventaireMock.getBouclier(Inventaire.Lateralisation.GAUCHE)).thenReturn(bouclierMock);//simulation du set réussi
	Assert.assertEquals("bouclier mock", assemblyTest.getShieldName());
	Assert.assertEquals("test arme une main", assemblyTest.getCurrentWeaponName());//l'arme à une main est toujours là

	//ajout d'une amre à deux mains, l'ancienne arme est retirée, le bouclier aussi
	when(arme1mainMock.toString()).thenReturn("test arme à deux mains");
	assemblyTest.setCurrentWeapon(77, Arme.QualiteArme.inferieure, Arme.EquilibrageArme.mauvais);
	verify(inventaireMock).removeArme(Inventaire.Lateralisation.DROITE);//l'arme précédente est virée
	verify(inventaireMock).removeBouclier(Inventaire.Lateralisation.GAUCHE);//le bouclier aussi
	verify(inventaireMock).addArme(arme2mainsMock, Inventaire.Lateralisation.DROITE);//on ajoute bien l'arme

	//rajout d'un bouclier : l'arme à deux mains est retirée
	when(inventaireMock.getArmeCourante()).thenReturn(arme2mainsMock);
	assemblyTest.setShield(0, 0, 0);
	verify(inventaireMock, times(2)).removeArme(Inventaire.Lateralisation.DROITE);

	//retrait volontaire du bouclier
	assemblyTest.delShield();
	verify(inventaireMock, times(2)).removeBouclier(Inventaire.Lateralisation.GAUCHE);
    }

    @Test
    public void testArmourPart() throws Exception
    {
	//pour tous les emplacements initiaux cela doit répondre que la zone est vide sauf pour un où l'on place une pièce d'armure
	when(inventaireMock.getPieceArmure(Inventaire.ZoneEmplacement.TETE)).thenReturn(pieceMock1);
	for (Inventaire.ZoneEmplacement z : Inventaire.ZoneEmplacement.values())
	{
	    if (z == Inventaire.ZoneEmplacement.TETE)
	    {
		Assert.assertEquals("piece mock 1", assemblyTest.getArmourPartName(z));
	    }
	    else
	    {
		Assert.assertEquals("aucun", assemblyTest.getArmourPartName(z));
	    }
	}
	//Placer une piece là où il y en a déjà une ne pose pas de problème et enlève la précédente
	whenNew(PieceArmure.class).withArguments(0, 0, 0, false).thenReturn(pieceMock2);
	assemblyTest.setArmourPart(0, 0, 0, Inventaire.ZoneEmplacement.TETE);
	verify(inventaireMock).removePieceArmure(Inventaire.ZoneEmplacement.TETE);
	verify(inventaireMock).addPieceArmure(pieceMock2, Inventaire.ZoneEmplacement.TETE);
    }

    @Test
    public void testTarget()
    {
	//le constructeur appelé juste avec un RM va appeler l'autre en lui passant 25 par défaut
	Assert.assertEquals(25, assemblyTest.getTargetDefense());

	//on modifie le ND cible et on vérifie
	assemblyTest.setTargetDefense(37);
	Assert.assertEquals(37, assemblyTest.getTargetDefense());

	//on remet à 25 pour les autres tests
	assemblyTest.setTargetDefense(25);
    }

    @Test
    public void testInit()
    {
	assemblyTest.getActions();
	verify(persoMock).getActions();

	assemblyTest.regenInit();
	verify(persoMock).genInit();
    }

    @Test
    public void testEtat()
    {
	//simple verif des bons appels
	assemblyTest.isOut();
	verify(persoMock).isElimine();
	verify(persoMock).isInconscient();

	assemblyTest.isStunned();
	verify(persoMock).isSonne();

	assemblyTest.getNbFleshWounds();
	verify(persoMock).getBlessuresLegeres();

	assemblyTest.getNbDramaWounds();
	verify(persoMock).getBlessuresGraves();
    }

    @Test
    public void testAttaque() throws Exception

    {
	//Cas d'échec
	RollUtils.RollResult rollResultMock = PowerMockito.mock(RollResult.class);
	Arme.Degats degatMock = PowerMockito.mock(Degats.class);
	whenNew(Degats.class).withArguments(0, 0).thenReturn(degatMock);
	when(rollResultMock.isJetReussi()).thenReturn(false);
	when(persoMock.attaquerCaC(0, 25)).thenReturn(rollResultMock);
	when(persoMock.isActif(0)).thenReturn(true);

	AttackReport result = assemblyTest.attack(0);
	verify(rollResultMock, times(2)).isJetReussi();
	verify(persoMock).attaquerCaC(0, 25);
	assertFalse(result.assess());
	assertEquals(degatMock, result.getDamage());
	assertTrue(result.isStillAtive());

	//cas de réussite
	when(rollResultMock.isJetReussi()).thenReturn(true);
	when(rollResultMock.getNbIncrements()).thenReturn(2);
	when(persoMock.genererDegats(2)).thenReturn(degatMock);

	result = assemblyTest.attack(0);
	verify(persoMock).genererDegats(2);
	assertTrue(result.assess());
	assertEquals(degatMock, result.getDamage());
	assertTrue(result.isStillAtive());
    }

    @Test
    public void testDegats()

    {
	Degats degatMock = PowerMockito.mock(Degats.class);
	assemblyTest.hurt(degatMock);
	verify(persoMock).etreBlesse(degatMock);

    }

}
