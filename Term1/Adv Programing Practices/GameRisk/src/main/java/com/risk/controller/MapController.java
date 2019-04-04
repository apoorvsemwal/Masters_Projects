package com.risk.controller;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.risk.business.IManageMap;
import com.risk.model.gui.Map;

/**
 * Map Controller is a part of MVC Controller which handle the actions and
 * events on GUI side.According to risk game, this controller calls appropriate
 * business logic for saving and loading map into a local system and updates map
 * as per the user action.
 * 
 * @author <a href="mailto:l_grew@encs.concordia.ca">Loveshant Grewal</a>
 * @version 0.0.1
 */
@Controller
@RequestMapping("/maps")
public class MapController {

	@Autowired
	IManageMap iManageMap;

	/**
	 * This function create and update map model and simply render web page of map
	 * in tabular form
	 * 
	 * @author <a href="mailto:l_grew@encs.concordia.ca">Loveshant Grewal</a>
	 * @param request  Request Payload
	 * @param response An object to assist a servlet in sending a response to the
	 *                 client
	 * @return Web Page of Map
	 * @throws Exception NullPointerException when model object is null
	 */
	@RequestMapping(value = "/getMapView", method = RequestMethod.GET)
	public ModelAndView getMapView(HttpServletRequest request, HttpServletResponse response) throws Exception {

		ModelAndView model = new ModelAndView("map");
		com.risk.model.Map map = createMapObject();
		model.addObject("map", map);
		return model;
	}

	/**
	 * This method is an abstraction for the process of retrieving A Map Object from
	 * the file name given as Input.
	 * 
	 * @author <a href="mailto:l_grew@encs.concordia.ca">Loveshant Grewal</a>
	 * @param request  Request Payload
	 * @param response An object to assist a servlet in sending a response to the
	 *                 client
	 * @param fileName Map File Name
	 * @return Map Object
	 * @throws Exception NullPointerException when map object is null
	 */
	@RequestMapping(value = "/map", method = RequestMethod.GET)
	@ResponseBody
	public Map getFullMap(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "fileName", required = false) String fileName) throws Exception {
		Map map = iManageMap.fetchMap(fileName);
		return map;
	}

	/**
	 * This function save current state of game to disk
	 * 
	 * @author <a href="mailto:l_grew@encs.concordia.ca">Loveshant Grewal</a>
	 * @param request  Request Payload
	 * @param response An object to assist a servlet in sending a response to the
	 *                 client
	 * @param map      Map Object
	 * @return Map Object
	 * @throws Exception NullPointerException when map object is null
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public Map submitMap(HttpServletRequest request, HttpServletResponse response, @RequestBody Map map)
			throws Exception {
		iManageMap.saveMap(map, map.getCurrentMap());
		return map;
	}

	/**
	 * This function fetches all available map files from resource folder
	 * 
	 * @param request  Request Payload
	 * @param response An object to assist a servlet in sending a response to the
	 *                 client
	 * @return List of Available Map Files
	 * @throws Exception NullPointerException when list of player is empty
	 */
	@RequestMapping(value = "/getAvailableMaps", method = RequestMethod.GET)
	@ResponseBody
	public List<String> getAvailableMaps(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<String> availableFiles = iManageMap.fetchMaps();
		return availableFiles;
	}

	/**
	 * @return Map Object
	 */
	private com.risk.model.Map createMapObject() {
		com.risk.model.Map map = new com.risk.model.Map();
		map.setContinents(new HashMap<>());
		return map;
	}

}