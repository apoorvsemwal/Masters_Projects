package com.risk.business;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.risk.business.impl.ManageMap;

/**
 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
 * @version 0.0.1
 */
public class ManageMapTest {

	private static IManageMap manageMap;

	@Before
	public void initMapManager() {
		manageMap = new ManageMap();
	}

	/**
	 * Test to check if validation for checking discontinuity in map is working.
	 * One Disconnected Territory.
	 * One Territory not having any neighbors and is not a neighbor of any other territory.
	 * 
	 * @see com.risk.business.IManageMap#checkDiscontinuity(com.risk.model.Map)
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 */
	@Test
	public void testMapDiscontinuityOneDisconnect() {

		com.risk.model.Map       map_model	= manageMap.getFullMap("Switzerland - Invalid - Disconnected Geneva.map");		
		String message             			= manageMap.checkDiscontinuity(map_model);
		assertEquals("Geneva : Territory is disconnected from the rest of the Map.",message);

	}

	/**
	 * Test to check if validation for checking discontinuity in map is working.
	 * Many Territories interlinked but are not neighbors of any other territories in map.
	 * Group of Disconnected Territories.
	 * 
	 * @see com.risk.business.IManageMap#checkDiscontinuity(com.risk.model.Map)
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 */
	@Test
	public void testMapDiscontinuityMultiDisconnect() {
		
		com.risk.model.Map map_model  = manageMap.getFullMap("USA - Disconnected Continent.map");		
		String message                = manageMap.checkDiscontinuity(map_model);
		assertEquals("Disconnected Territories: Iowa-Texas-Minnisota-Missouri-South Dakota-Louisiana-Kansas-North Dakota-Arkansas-Nebraska within Continent: Midwest U.S.",message);
	}

	/**
	 * Test to check if a valid map is being read without any errors.
	 * 
	 * @see com.risk.business.IManageMap#checkDiscontinuity(com.risk.model.Map)
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 */
	@Test
	public void testValidMap() {

		com.risk.model.Map map_model = manageMap.getFullMap("Asia.map");		
		String message               = manageMap.checkDiscontinuity(map_model);
		assertEquals(message, "");

	}
	
	/**
	 * Test to check if validation for checking invalid neighbor is working.
	 * Some Territory mentioned in neighbors list but is not created yet.
	 * 
	 * @see com.risk.business.IManageMap#checkInvalidNeighbour(com.risk.model.Map)
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 */
	@Test
	public void testCheckInvalidNeighbour() {

		com.risk.model.Map map_model = manageMap.getFullMap("Asia - Invalid - NonExistantCountry - Yemen.map");		
		String message             	 = manageMap.checkInvalidNeighbour(map_model);
		assertEquals(message, "Invalid Territories used as a neighbor: yemen");		

	}	

	/**
	 * Test to check if validation for duplicate territories is working. 
	 * Territory Used twice in the Map.
	 * @see com.risk.business.IManageMap#checkDuplicateTerritory(com.risk.model.Map)
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 */
	@Test
	public void testCheckDuplicateTerritory() {

		com.risk.model.Map map_model 		= manageMap.getFullMap("World (small) - Invalid-DuplicateTerritory.map");		
		String message             			= manageMap.checkDuplicateTerritory(map_model);
		assertEquals(message, "Egypt : Duplicate territory detected in the Map.");

	}

}