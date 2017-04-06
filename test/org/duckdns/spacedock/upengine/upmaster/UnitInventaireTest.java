/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.duckdns.spacedock.upengine.upmaster;

import org.duckdns.spacedock.upengine.libupsystem.Arme;
import org.duckdns.spacedock.upengine.libupsystem.Armure;
import org.duckdns.spacedock.upengine.libupsystem.Bouclier;
import org.duckdns.spacedock.upengine.libupsystem.PieceArmure;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
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
	    Armure.class, Inventaire.class, Bouclier.class
	})
public class UnitInventaireTest
{

    private Inventaire inventaireTest;
    private Arme arme1MainMock;
    private Arme arme2MainsMock;
    private PieceArmure casqueMock;
    private PieceArmure ganteletMock;
    private Bouclier bouclierMock;

    @Before
    public void setUp()
    {
	inventaireTest = new Inventaire();
	arme1MainMock = PowerMockito.mock(Arme.class);
	when(arme1MainMock.isArme2Mains()).thenReturn(false);
	arme2MainsMock = PowerMockito.mock(Arme.class);
	when(arme2MainsMock.isArme2Mains()).thenReturn(true);

	casqueMock = PowerMockito.mock(PieceArmure.class);
	when(casqueMock.getLocalisation()).thenReturn(0);
	when(casqueMock.getType()).thenReturn(0);
	when(casqueMock.getNbPoints()).thenReturn(2);

	ganteletMock = PowerMockito.mock(PieceArmure.class);
	when(ganteletMock.getLocalisation()).thenReturn(3);
	when(ganteletMock.getType()).thenReturn(2);
	when(ganteletMock.getNbPoints()).thenReturn(5);

	bouclierMock = PowerMockito.mock(Bouclier.class);
	when(bouclierMock.getType()).thenReturn(1);
	when(bouclierMock.getNbPoints()).thenReturn(5);
    }

    @Test
    public void testArmeErreur()
    {
	//main droite initialement prise
	inventaireTest.addArme(arme1MainMock, Inventaire.Lateralisation.DROITE);
	verify(arme1MainMock).isArme2Mains();

	//erreur : on ne peut pas retirer quelque chose qui n'est pas là
	try
	{
	    inventaireTest.removeArme(Inventaire.Lateralisation.GAUCHE);
	    fail();
	}
	catch (IllegalStateException e)
	{
	    Assert.assertEquals("emploi de la mauvaise méthode dans ce contexte:l'emplacement n'est pas actuellement occupé", e.getMessage());
	}

	//cas d'erreur : emplacement occupé
	Arme arme1MainSupMock = PowerMockito.mock(Arme.class);//on crée un mock supplémentaire pour pouvoir vérifier les appels indépendament
	try
	{
	    inventaireTest.addArme(arme1MainSupMock, Inventaire.Lateralisation.DROITE);
	    fail();
	}
	catch (IllegalStateException e)
	{
	    verify(arme1MainSupMock, never()).isArme2Mains();
	    Assert.assertEquals("emploi de la mauvaise méthode dans ce contexte:l'emplacement n'est pas libre", e.getMessage());
	}

	//on ajoute du côté gauche une arme à deux mains sans pour autant libérer le côté droit
	try
	{
	    inventaireTest.addArme(arme2MainsMock, Inventaire.Lateralisation.GAUCHE);
	    verify(arme2MainsMock).isArme2Mains();
	    fail();
	}
	catch (IllegalStateException e)
	{
	    Assert.assertEquals("emploi de la mauvaise méthode dans ce contexte:arme à deux mains mais main secondaire occupée", e.getMessage());
	}
    }

    @Test
    public void testArmeNominal()
    {
	//Au début il n'y a rien
	Assert.assertEquals(null, inventaireTest.getArmeCourante());

	//ajout d'arme principale correct
	inventaireTest.addArme(arme1MainMock, Inventaire.Lateralisation.DROITE);
	Arme armeRetour = inventaireTest.getArmeCourante();
	Assert.assertEquals(arme1MainMock, armeRetour);

	//ajout d'arme côté secondaire, dans cette version sans combat à deux armes l'arme principale qui répond reste celle de l'autre côté, à terme ce test évoluera
	Arme arme1MainSupMock = PowerMockito.mock(Arme.class);
	when(arme1MainSupMock.isArme2Mains()).thenReturn(false);
	inventaireTest.addArme(arme1MainSupMock, Inventaire.Lateralisation.GAUCHE);
	armeRetour = inventaireTest.getArmeCourante();
	Assert.assertEquals(arme1MainMock, armeRetour);

	//passer le côté secondaire en côté principal et vérifier que le switch a bien été fait
	inventaireTest.setCotePrincipal(Inventaire.Lateralisation.GAUCHE);
	armeRetour = inventaireTest.getArmeCourante();
	Assert.assertEquals(arme1MainSupMock, armeRetour);

	//on libère le côté droit, on réaffecte et vérifie que le côté principal est toujours à gauche
	inventaireTest.removeArme(Inventaire.Lateralisation.DROITE);
	inventaireTest.addArme(arme1MainMock, Inventaire.Lateralisation.DROITE);
	armeRetour = inventaireTest.getArmeCourante();
	Assert.assertEquals(arme1MainSupMock, armeRetour);

	//on libère les dexu côtés et l'on ajoute une arme à deux mains à droite, aucune arme ne doit être considérée comme l'arme principale car le côté principal est toujours à gauche
	inventaireTest.removeArme(Inventaire.Lateralisation.DROITE);
	inventaireTest.removeArme(Inventaire.Lateralisation.GAUCHE);
	inventaireTest.addArme(arme2MainsMock, Inventaire.Lateralisation.DROITE);
	Assert.assertEquals(null, inventaireTest.getArmeCourante());

	//on repasse le côté principal à gauche et on vérifie que l'arme à deux mains est bien l'arme principale
	inventaireTest.setCotePrincipal(Inventaire.Lateralisation.DROITE);
	armeRetour = inventaireTest.getArmeCourante();
	Assert.assertEquals(arme2MainsMock, armeRetour);
    }

    @Test
    public void testPieceErreur()
    {
	//inventaire initialement occupé par un casque
	inventaireTest.addPieceArmure(casqueMock, Inventaire.PartieCorps.TETE);

	//Erreur : ajout sur zone occupée
	PieceArmure casqueMock2 = PowerMockito.mock(PieceArmure.class);//on crée un mock supplémentaire pour pouvoir vérifier les appels indépendament
	try
	{
	    inventaireTest.addPieceArmure(casqueMock2, Inventaire.PartieCorps.TETE);
	    fail();
	}

	catch (IllegalStateException e)
	{
	    verify(casqueMock2, never()).getLocalisation();
	    verify(casqueMock2, never()).getType();
	    verify(casqueMock2, never()).getNbPoints();
	    Assert.assertEquals("emploi de la mauvaise méthode dans ce contexte:l'emplacement n'est pas libre", e.getMessage());
	}

	//Erreur : retrait de zone libre
	try
	{
	    inventaireTest.removePieceArmure(Inventaire.PartieCorps.PIEDGAUCHE);
	    fail();
	}
	catch (IllegalStateException e)
	{
	    Assert.assertEquals("emploi de la mauvaise méthode dans ce contexte:l'emplacement n'est pas actuellement occupé", e.getMessage());
	}

	//Erreur : localisation incorrecte
	PieceArmure casqueMock3 = PowerMockito.mock(PieceArmure.class);//on crée un mock supplémentaire pour pouvoir vérifier les appels indépendament
	try
	{
	    inventaireTest.addPieceArmure(casqueMock3, Inventaire.PartieCorps.PIEDDROIT);
	    fail();
	}
	catch (IllegalStateException e)
	{
	    verify(casqueMock3).getLocalisation();//la localisation est vérifiée, toutes les méthodes suivantes ne sont pas testées car la pièce n'est pas scannée vu que l'erreur est lancée avant
	    verify(casqueMock3, never()).getType();
	    verify(casqueMock3, never()).getNbPoints();
	    Assert.assertEquals("emploi de la mauvaise méthode dans ce contexte:localisation incorrecte", e.getMessage());
	}
    }

    @Test
    public void testPieceNominal() throws Exception
    {
	//tester l'augmentation diminution du type avec l'ajout retrait
	//tester malus akternatifs

	//Ajout du casque et vérification de l'appel de toutes les méthodes idoines puis de l'effet
	inventaireTest.addPieceArmure(casqueMock, Inventaire.PartieCorps.TETE);
	//on vérifie que la pièce est intégralement scannée
	verify(casqueMock).getLocalisation();
	verify(casqueMock).getType();
	verify(casqueMock).getNbPoints();

	Assert.assertEquals(casqueMock, inventaireTest.getPieceArmure(Inventaire.PartieCorps.TETE));

	//On construit un mock d'armure pour vérifier les paramétres de construction de l'inventaire
	Armure armureMockAvecCasque = PowerMockito.mock(Armure.class);
	whenNew(Armure.class).withArguments(2, 0).thenReturn(armureMockAvecCasque);

	//On vérifie que l'armure renvoyée correspond au casque mis en place
	Armure armureRetour = inventaireTest.getArmure();
	Assert.assertEquals(armureMockAvecCasque, armureRetour);

	//On ajoute le gantelet, le type doit changer ainsi que le nombre de points et l'un des malus
	Armure armureMockAvecCasqueEtGantelet = PowerMockito.mock(Armure.class);
	whenNew(Armure.class).withArguments(7, 2).thenReturn(armureMockAvecCasqueEtGantelet);
	inventaireTest.addPieceArmure(ganteletMock, Inventaire.PartieCorps.MAINDROITE);

	//On vérifie que l'armure renvoyée correspond au casque mis en place
	armureRetour = inventaireTest.getArmure();
	Assert.assertEquals(armureMockAvecCasqueEtGantelet, armureRetour);

	//On enlève le gantelet : c'est le premier mock qui doit être renvoyé
	inventaireTest.removePieceArmure(Inventaire.PartieCorps.MAINDROITE);
	armureRetour = inventaireTest.getArmure();
	Assert.assertEquals(armureMockAvecCasque, armureRetour);
    }

    @Test
    public void testBouclierErreur()
    {
	//On commence par un bouclier présent à gauche
	inventaireTest.addBouclier(bouclierMock, Inventaire.Lateralisation.GAUCHE);
	//arme dans même main que bouclier
	try
	{
	    inventaireTest.addArme(arme1MainMock, Inventaire.Lateralisation.GAUCHE);
	    fail();
	}
	catch (IllegalStateException e)
	{
	    Assert.assertEquals("emploi de la mauvaise méthode dans ce contexte:l'emplacement n'est pas libre", e.getMessage());
	}

	//le bouclier toujours présent, on ajoute une arme à deux mains dans l'autre main
	try
	{
	    inventaireTest.addArme(arme2MainsMock, Inventaire.Lateralisation.DROITE);
	    fail();
	}
	catch (IllegalStateException e)
	{
	    Assert.assertEquals("emploi de la mauvaise méthode dans ce contexte:arme à deux mains mais main secondaire occupée", e.getMessage());
	}

	//on ajoute une arme à droite et l'on essaye d'ajouter un bouclier dans la même main
	inventaireTest.addArme(arme1MainMock, Inventaire.Lateralisation.DROITE);
	Bouclier bouclierMock2 = PowerMockito.mock(Bouclier.class);//on crée un mock supplémentaire pour pouvoir vérifier les appels indépendament
	try
	{
	    inventaireTest.addBouclier(bouclierMock2, Inventaire.Lateralisation.DROITE);
	    fail();
	}
	catch (IllegalStateException e)
	{
	    verify(bouclierMock2, never()).getType();
	    verify(bouclierMock2, never()).getNbPoints();
	    Assert.assertEquals("emploi de la mauvaise méthode dans ce contexte:l'emplacement n'est pas libre", e.getMessage());
	}

	//erreur : retrait du bouclier de la main libre de bouclier (mais portant une arme)
	try
	{
	    inventaireTest.removeBouclier(Inventaire.Lateralisation.DROITE);
	    fail();
	}
	catch (IllegalStateException e)
	{
	    Assert.assertEquals("emploi de la mauvaise méthode dans ce contexte:l'emplacement n'est pas actuellement occupé", e.getMessage());
	}
    }

    @Test
    public void testBouclierNominal()
    {
	//On commence par un bouclier présent à gauche et l'on vérifie que la pièce est intégralement scannée
	inventaireTest.addBouclier(bouclierMock, Inventaire.Lateralisation.GAUCHE);
	verify(bouclierMock).getType();
	verify(bouclierMock).getNbPoints();
    }

    @Test
    public void testLateralisation()
    {
	//ajout d'une arme dans la main droite, l'inventaire la reconnait comme arme principale car elle est à droite et c'est le côté par défaut
	inventaireTest.addArme(arme1MainMock, Inventaire.Lateralisation.DROITE);
	Assert.assertEquals(arme1MainMock, inventaireTest.getArmeCourante());

	//changement de latéralisation et ajout d'un bouclier à gauche le bouclier n'est pas une arme donc la méthode doit renvoyer null
	inventaireTest.addBouclier(bouclierMock, Inventaire.Lateralisation.GAUCHE);
	inventaireTest.setCotePrincipal(Inventaire.Lateralisation.GAUCHE);
	Assert.assertEquals(null, inventaireTest.getArmeCourante());
    }
}
