/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.duckdns.spacedock.upengine.upmaster;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ykonoclast
 */
public class UnitCharacterAssemblyTest
{

    public UnitCharacterAssemblyTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
    }

    @Before
    public void setUp()
    {

	//mock : perso(tostring, setlibelle), inventaire(getbouclier,), arme CaC et Dist(nbmains), UPReference(getModArme)
    }

    @Test
    public void testCreation()
    {
//tester ND 25 par défaut

	/*CharacterAssembly(int p_rm
	)
    CharacterAssembly(int p_rm
	, int p_ND




    )*/
    }

    @Test
    public void testLibelle()
    {
	//tester get, créer set

	/*String getLibellePerso




    ()*/
    }

    @Test
    public void testWeapon()
    {
	//si pas d'arme, mains nues est remonté dans get
	//tester cac et dist car création différenciée
	//tester une et deux mains
	//tester retrait auto du bouclier si ajout arme à deux mains
	//retrait d'une arme à deux mains si ajout du bouclier
	//tester retrait auto d'une arme si nouvelle arme mise


	/*void setCurrentWeapon
	(int p_index, Arme
	.QualiteArme p_quality, Arme
	.EquilibrageArme p_balance
	)

    void delWeapon
	()
    String getCurrentWeaponName




    ()*/
    }

    @Test
    public void testArmureEtND()
    {

	//message si pas d'armure à l'emplacement (ajouter, tester, retirer et retester
	//présence effective à l'emplacement
	//retrait d'ancienne pièce si nouvelle ajoutée
	//get du bouclier avec message si absent (mettre, tester puis retirer et retester)
	//ND : appel de la bonne méthode (vraisemblalbmeent mains nues ici)
	/*String getPieceArmureName
	(Inventaire.ZoneEmplacement p_zone
	)
    void setPieceArmure
	(int p_index, int p_materiau, int p_type, Inventaire
	.ZoneEmplacement p_zone
	)




    void delPieceArmure
	(Inventaire.ZoneEmplacement p_zone
	)




    String getBouclierName
	()




    void setBouclier
	(int p_index, int p_materiau, int p_type
	)




    void delBouclier




    ()*/
    }

    @Test
    public void testTarget()
    {

	//get et set
	/*void setTargetND
	(int p_ND
	)




    int getTargetND




    ()*/
    }

    @Test
    public void testInit()
    {
	//ajouter un getinit du perso si il n'existe pas, faire un isactive des phases ou il agit et n'agit pas et faire un coup de regen avec vérif du bon appel

	/*void regenInit
	()
    boolean isActive
	(int p_phase




    )*/
    }

    @Test
    public void testEtat()
    {
	//simple verif des bons appels

	/*
	boolean isOut
	()



    boolean isStunned
	()




    int getNbFleshWounds
	()




    int getNbDramaWounds


    ()*/
    }

    @Test
    public void testAttaque()

    {
//test échec (avec mock de rollresult) et réussite
	/*  AttackReport attack
	(int p_currentPhase


    )*/
    }

    @Test
    public void testDegats()

    {
	//bon appel de la méthode

	/*
    void hurt
	(Degats p_degats


)*/
    }

}
