package com.risk.business;

import java.util.List;

import com.risk.model.Map;
import com.risk.model.file.File;

/**
 * This interface is responsible for handling all Map related functionalities
 * like : Translating GUI Map into a File Map which can be parsed and stored in
 * a Map File. Translating File Map into a GUI Map which can be rendered on UI.
 * Map related validations like duplicate territories or continents and checking
 * discontinuities in the map.
 * 
 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
 * @version 0.0.1
 */
public interface IManageMap {

	/**
	 * This method is an abstraction for the process of retrieving A Map Object from
	 * the Map File to be saved/loaded.
	 * 
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 * @param fileName Fully Qualified Map File Name on local disk. This file will
	 *                 be rendered for Playing as well as for Editing Map.
	 * @return Entire Map Object which will be rendered for Playing.
	 */
	Map getFullMap(String fileName);

	/**
	 * This method checks the World Map Object for any duplicate territories.
	 * 
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 * @param map Object Representation of the Map File.
	 * @return Any Errors if they exist otherwise empty String.
	 */
	String checkDuplicateTerritory(Map map);

	/**
	 * This method checks the World Map Object for any discontinuities, i.e. any
	 * part(territories/set of territories) that are disconnected from the rest of
	 * the connected map.
	 * 
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 * @param map Object Representation of the Map File.
	 * @return Any Errors if they exist otherwise empty String.
	 */
	String checkDiscontinuity(Map map);

	/**
	 * This method checks the World Map Object for any territory being used as a
	 * neighbor, without actually being in existance.
	 * 
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 * @param map Object Representation of the Map File.
	 * @return Any Errors if they exist otherwise empty String.
	 */
	String checkInvalidNeighbour(Map map);

	/**
	 * This method receives the World Map Object from GUI and checks it for any
	 * discontinuities. If its valid then the map is converted to a FILE Object and
	 * forwarded to File Access Layer for saving to a physical MAP File. If its
	 * invalid then a boolean value False is returned to GUI.
	 * 
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 * @param map       GUI Object Representation of the World Map.
	 * @param file_name Name of the Map file to be stored in Resource Folder.
	 * @return GUI Map otherwise with a status update for valid or invalid save.
	 * @throws Exception input/output exception
	 */
	com.risk.model.gui.Map saveMap(com.risk.model.gui.Map map, String file_name) throws Exception;

	/**
	 * This method retrieves an existing World Map File from Resource Folder and
	 * returns a MAP Object that can be rendered to UI. If the loaded file is an
	 * invalid Map then it returns Null.
	 * 
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 * @param file_name Name of the Map file to be stored in Resource Folder.
	 * @return GUI Map object if its a valid map otherwise Null.
	 */
	com.risk.model.gui.Map fetchMap(String file_name);

	/**
	 * This method retrieves a list of existing World Map File from Resource Folder.
	 * User then selects a file from the given list.
	 * 
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 * @return List of existing World Map Files from Resource Folder.
	 */
	List<String> fetchMaps();

	/**
	 * This method parses the File Object and converts it to a Map Object.
	 * 
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 * @param file File Object Representing the Map File to be saved/loaded.
	 * @return Map map : Object Representation of the Map File.
	 */
	Map convertFileToMap(File file);
}