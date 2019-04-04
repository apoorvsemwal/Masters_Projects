package com.risk.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observer;

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

import com.risk.business.IManageGamePlay;
import com.risk.business.IManageMap;
import com.risk.business.IManagePlayer;
import com.risk.file.IManageGamePlayFile;
import com.risk.model.GamePlay;
import com.risk.model.Tournament;
import com.risk.model.gui.GameResult;
import com.risk.model.gui.PlayerDetails;
import com.risk.model.gui.TournamentChoices;

/**
 * GamePlay Controller is a part of MVC Controller which handle the actions and
 * events on GUI side.According to risk game, this controller allows to
 * initialize the startup phase (each phases) and render game play webpage to
 * players.
 * 
 * @author <a href="mailto:l_grew@encs.concordia.ca">Loveshant Grewal</a>
 * @version 0.0.1
 */
@Controller
@RequestMapping("/gamePlay")
public class GamePlayController {

	@Autowired
	IManageMap iManageMap;

	@Autowired
	IManagePlayer iManagePlayer;

	@Autowired
	IManageGamePlay iManageGamePlay;

	@Autowired
	IManageGamePlayFile iManageGamePlayFile;

	static GamePlay gamePlay;

	static Tournament tournament;

	/**
	 * This function renders the gamePlay.jsp file on which players can start
	 * playing their game.
	 * 
	 * @author <a href="mailto:l_grew@encs.concordia.ca">Loveshant Grewal</a>
	 * @param request  Request Payload
	 * @param response An object to assist a servlet in sending a response to the
	 *                 client
	 * @return Web Page of Game Play
	 * @throws Exception NullPointerException when model object is null
	 */
	@RequestMapping(value = "/getPlayView", method = RequestMethod.GET)
	public ModelAndView getGamePlayView(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView model = new ModelAndView("gamePlay");
		return model;
	}

	/**
	 * This function renders the gamePlay.jsp file on which players can start
	 * playing their game.
	 * 
	 * @author <a href="mailto:l_grew@encs.concordia.ca">Loveshant Grewal</a>
	 * @param request  Request Payload
	 * @param response An object to assist a servlet in sending a response to the
	 *                 client
	 * @return Web Page of Tournament Play
	 * @throws Exception NullPointerException when model object is null
	 */
	@RequestMapping(value = "/getTournamentView", method = RequestMethod.GET)
	public ModelAndView getTournamentView(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView model = new ModelAndView("tournamentPlay");
		return model;
	}

	/**
	 * This function initializes the startup phase once the player selects no. of
	 * players and game play map.
	 * 
	 * @author <a href="mailto:l_grew@encs.concordia.ca">Loveshant Grewal</a>
	 * @param request       Request Payload
	 * @param response      An object to assist a servlet in sending a response to
	 *                      the client
	 * @param playerDetails object with details about players who will be playing
	 *                      the game
	 * @return gamePlay
	 * @throws Exception NullPointerException when game state object is null
	 */
	@RequestMapping(value = "/initStartUpPhase", method = RequestMethod.POST)
	@ResponseBody
	public GamePlay initStartUpPhase(HttpServletRequest request, HttpServletResponse response,
			@RequestBody PlayerDetails playerDetails) throws Exception {
		gamePlay = iManagePlayer.createPlayer(playerDetails);
		gamePlay.addObserver((Observer) iManageGamePlay);
		return gamePlay;
	}

	/**
	 * This function will save the map state and return the map state with next
	 * phase and next player turn.
	 * 
	 * @author <a href="mailto:l_grew@encs.concordia.ca">Loveshant Grewal</a>
	 * @param request          Request Payload
	 * @param response         An object to assist a servlet in sending a response
	 *                         to the client
	 * @param gamePlayFromView game state to save
	 * @return game play state after update with next phase and player
	 * @throws Exception NullPointerException when game state object is null
	 */
	@RequestMapping(value = "/saveGameState", method = RequestMethod.POST)
	@ResponseBody
	public GamePlay submitGameState(HttpServletRequest request, HttpServletResponse response,
			@RequestBody GamePlay gamePlayFromView) throws Exception {
		// gamePlay = iManageGamePlay.savePhase(gamePlay);
		abstractView(gamePlayFromView);
		return gamePlay;
	}

	/**
	 * This function will persist the map state and return boolean true if success.
	 * 
	 * @author <a href="mailto:l_grew@encs.concordia.ca">Loveshant Grewal</a>
	 * @param request          Request Payload
	 * @param response         An object to assist a servlet in sending a response
	 *                         to the client
	 * @param gamePlayFromView game state to save
	 * @return game play true if success.
	 * @throws Exception NullPointerException when game state object is null
	 */
	@RequestMapping(value = "/persistGameState", method = RequestMethod.POST)
	@ResponseBody
	public Boolean persistGameState(HttpServletRequest request, HttpServletResponse response,
			@RequestBody GamePlay gamePlayFromView) throws Exception {
		Boolean gamePlay = iManageGamePlayFile.saveGameStateToDisk(gamePlayFromView);
		return gamePlay;
	}

	/**
	 * This function provides the abstract view for observer design pattern.
	 * 
	 * @author <a href="mailto:l_grew@encs.concordia.ca">Loveshant Grewal</a>
	 * @param gamePlay game state to save
	 */
	private void abstractView(GamePlay gamePlayFromView) {
		gamePlay.setMap(gamePlayFromView.getMap());
		gamePlay.setCard_trade(gamePlayFromView.getCard_trade());
		gamePlay.setCurrent_player(gamePlayFromView.getCurrent_player());
		gamePlay.setFile_name(gamePlayFromView.getFile_name());
		gamePlay.setFree_cards(gamePlayFromView.getFree_cards());
		gamePlay.setGame_phase(gamePlayFromView.getGame_phase());
		gamePlay.setStatus(gamePlayFromView.getStatus());
		gamePlay.setAttack(gamePlayFromView.getAttack());
		gamePlay.setFortification(gamePlayFromView.getFortification());
		gamePlay.setArmy_move(gamePlayFromView.getArmy_move());
		gamePlay.setGui_map(gamePlayFromView.getGui_map());
		gamePlay.setDomination(gamePlayFromView.getDomination());
		gamePlay.setGame_play_id(gamePlayFromView.getGame_play_id());
		gamePlay.setGame_play_turns(gamePlayFromView.getGame_play_turns());
		gamePlay.setWinner(gamePlayFromView.getWinner());
		List<com.risk.model.Player> players = setGameStateData(gamePlayFromView);
		gamePlay.setGame_state(players);
	}

	private com.risk.model.Player setCurrentPlayer(com.risk.model.Player player) {
		com.risk.model.Player player_new = null;
		for (com.risk.model.Player p : gamePlay.getGame_state()) {
			if (p.getId() == player.getId()) {
				p.setArmy_stock(player.getArmy_stock());
				p.setAny_territory_occupied(player.isAny_territory_occupied());
				p.setCard_list(player.getCard_list());
				p.setName(player.getName());
				p.setTerritory_list(player.getTerritory_list());
				p.setTrade_count(player.getTrade_count());
				p.setType(player.getType());
				p.setStrategy_name(player.getStrategy_name());
				player_new = p;
				break;
			}
		}
		return player_new;
	}

	private List<com.risk.model.Player> setGameStateData(GamePlay gamePlayFromView) {
		List<com.risk.model.Player> players = new ArrayList<>();
		for (com.risk.model.Player player : gamePlayFromView.getGame_state()) {
			players.add(setCurrentPlayer(player));
		}
		return players;
	}

	/**
	 * This function start the tournament by providing the necessary details as
	 * given by user.
	 * 
	 * @author <a href="mailto:l_grew@encs.concordia.ca">Loveshant Grewal</a>
	 * @param request           Request Payload
	 * @param response          An object to assist a servlet in sending a response
	 *                          to the client
	 * @param tournamentChoices object with details about tournament maps and
	 *                          strategies
	 * @return Tournament object with gamePlays object
	 * @throws Exception NullPointerException when game state object is null
	 */
	@RequestMapping(value = "/startTournament", method = RequestMethod.POST)
	@ResponseBody
	public Tournament startTournament(HttpServletRequest request, HttpServletResponse response,
			@RequestBody TournamentChoices tournamentChoices) throws Exception {
		tournament = iManageGamePlay.prepareTournamentGamePlay(tournamentChoices);
		return tournament;
	}

	/**
	 * This function will resume the tournament by providing the necessary details
	 * as given by user.
	 * 
	 * @author <a href="mailto:l_grew@encs.concordia.ca">Loveshant Grewal</a>
	 * @param request            Request Payload
	 * @param response           An object to assist a servlet in sending a response
	 *                           to the client
	 * @param tournamentFromView Tournament object with details about tournament
	 *                           maps and strategies
	 * @return Tournament object with gamePlays object
	 * @throws Exception NullPointerException when game state object is null
	 */
	@RequestMapping(value = "/playTournament", method = RequestMethod.POST)
	@ResponseBody
	public Tournament playTournament(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Tournament tournamentFromView) throws Exception {
		tournament = iManageGamePlay.playTournamentMode(tournament);
		return tournament;
	}

	/**
	 * This function will fetch tournament results.
	 * 
	 * @author <a href="mailto:l_grew@encs.concordia.ca">Loveshant Grewal</a>
	 * @param request            Request Payload
	 * @param response           An object to assist a servlet in sending a response
	 *                           to the client
	 * @param tournamentFromView Tournament object with details about tournament
	 *                           maps and strategies
	 * @return Tournament object with gamePlays object
	 * @throws Exception NullPointerException when game state object is null
	 */
	@RequestMapping(value = "/getTournamentResult", method = RequestMethod.POST)
	@ResponseBody
	public com.risk.model.gui.TournamentResults getTournamentResult(HttpServletRequest request,
			HttpServletResponse response, @RequestBody Tournament tournamentFromView) throws Exception {
		com.risk.model.gui.TournamentResults tournamentResult = gatherResultsFromTournament();
		return tournamentResult;
	}

	/**
	 * This function fetches all available saved games from resource folder
	 * 
	 * @param request  Request Payload
	 * @param response An object to assist a servlet in sending a response to the
	 *                 client
	 * @return List of Available Saved Games
	 * @throws Exception NullPointerException when list of player is empty
	 */
	@RequestMapping(value = "/getSavedGames", method = RequestMethod.GET)
	@ResponseBody
	public List<String> getSavedGames(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<String> availableGames = iManageGamePlay.fetchGamePlays();
		return availableGames;
	}

	/**
	 * This method is an abstraction for the process of retrieving a saved game
	 * state.
	 * 
	 * @author <a href="mailto:l_grew@encs.concordia.ca">Loveshant Grewal</a>
	 * @param request       Request Payload
	 * @param response      An object to assist a servlet in sending a response to
	 *                      the client
	 * @param savedGameName Saved Game Name
	 * @return GamePlay Object
	 * @throws Exception NullPointerException when map object is null
	 */
	@RequestMapping(value = "/fetchGame", method = RequestMethod.GET)
	@ResponseBody
	public GamePlay fetchGame(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "savedGameName", required = false) String savedGameName) throws Exception {
		gamePlay = iManageGamePlay.fetchGamePlay(savedGameName);
		gamePlay.addObserver((Observer) iManageGamePlay);
		return gamePlay;
	}

	public com.risk.model.gui.TournamentResults gatherResultsFromTournament() {
		com.risk.model.gui.TournamentResults results = new com.risk.model.gui.TournamentResults();
		List<GameResult> gameResult = new ArrayList<>();
		com.risk.model.TournamentResults tournamentResults = tournament.getTournament_results();
		Map<String, List<GamePlay>> each_map_result = tournamentResults.getEach_map_results();
		for (Map.Entry<String, List<GamePlay>> entry : each_map_result.entrySet()) {
			for (int i = 0; i < entry.getValue().size(); i++) {
				com.risk.model.gui.GameResult gr = new com.risk.model.gui.GameResult();
				gr.setWinner(entry.getValue().get(i).getWinner());
				gr.setGameNo((i + 1));
				gr.setMapName(entry.getKey());
				gameResult.add(gr);
			}
		}
		results.setGameResult(gameResult);
		return results;
	}
}