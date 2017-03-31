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
public class UnitSessionManagerTest
{
//Mocker characterAssembly

    @BeforeClass
    public static void setUpClass()
    {
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
    }

    @Test
    public void testCreationReport()
    {

    }

    @Test
    public void testAttackReport()
    {

    }

    @Test
    public void testHealthReport()
    {

    }

    @Test
    public void testFighterOperations()
    {
	//ajout suppression, voir tests d'intégration
	//vérifier que les trous sont bien comblés

    }

    @Test
    public void testChrono()
    {//advancephase
	//public int getCurrentPhase()
	//public int getCurrentTurn()
	//IsAnyoneActive()
//voir integ

    }

    @Test
    public void testAttack()
    {
	//public AttackReport attack(int p_index)
	//vérifier si combattant n'est plus actif ou si toujours actif et réactions afférentes
    }

    @Test
    public void testHurt()
    {
	// public HealthReport hurt(int p_index, Degats p_damage)
	//bons appels du characterassembly et bon retour du healthreport

    }

    @Test
    public void testLibelles()
    {
	//public String getName(int p_index)
	//essayer de mettre en place un set aussi
    }

    @Test
    public void testWeapons()
    {
	//ublic void setWeapon(int p_index, int p_weaponId, Arme.QualiteArme p_quality, Arme.EquilibrageArme p_balance)
//public String getCurrentWeaponName(int p_index)
	// public void delWeapon(int p_index)
	//ajout/retrait, vérifier bons appels
    }

    @Test
    public void testGetActions()
    {
	//getFighterActions(p_index)
    }

    @Test
    public void testArmure()
    {

	//vérifier bons appels
//public String getPieceArmureName(int p_fighterIndex, Inventaire.ZoneEmplacement p_zone)
	//public void setPieceArmure(int p_fighterIndex, int p_index, int p_materiau, int p_type, Inventaire.ZoneEmplacement p_zone)
	//public void delPieceArmure(int p_fighterIndex, Inventaire.ZoneEmplacement p_zone)
    }

    @Test
    public void testBouclier()
    {
	//public String getBouclierName(int p_fighterIndex)
	//public void setBouclier(int p_fighterIndex, int p_index, int p_materiau, int p_type)
	//public void delBouclier(int p_fighterIndex)

	//vérifier bons appels
    }

    @Test
    public void testND()
    {
	//public void setTargetND(int p_index, int p_ND)
	//  public int getTargetND(int p_index)
	//tester cas d'erreur sur ND négatif
    }

    @Test
    public void testGetDefensePassive()
    {
//    public int getFighterND(int p_index, int p_weapType, boolean p_dodge)
    }

}
