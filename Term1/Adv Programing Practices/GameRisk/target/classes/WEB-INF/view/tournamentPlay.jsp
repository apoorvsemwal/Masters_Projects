<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript">
	$(document).ready(function() {
		
		var auditLogsDataTable = $('#auditLogs').DataTable({
	        "paging":   false,
	        "ordering": false,
	        "info":     false,
	        "searching": false
	    });
		
		$("#noOfGames").keydown(function (e) {
	        // Allow: backspace, delete, tab, escape, enter and .
	        if ($.inArray(e.keyCode, [46, 8, 9, 27, 13, 110]) !== -1 ||
	             // Allow: Ctrl+A, Command+A
	            (e.keyCode === 65 && (e.ctrlKey === true || e.metaKey === true)) || 
	             // Allow: home, end, left, right, down, up
	            (e.keyCode >= 35 && e.keyCode <= 40)) {
	                 // let it happen, don't do anything
	                 return;
	        }
	        // Ensure that it is a number and stop the keypress
	        if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
	            e.preventDefault();
	        }
	    });
		
		$("#maxTurns").keydown(function (e) {
	        // Allow: backspace, delete, tab, escape, enter and .
	        if ($.inArray(e.keyCode, [46, 8, 9, 27, 13, 110]) !== -1 ||
	             // Allow: Ctrl+A, Command+A
	            (e.keyCode === 65 && (e.ctrlKey === true || e.metaKey === true)) || 
	             // Allow: home, end, left, right, down, up
	            (e.keyCode >= 35 && e.keyCode <= 40)) {
	                 // let it happen, don't do anything
	                 return;
	        }
	        // Ensure that it is a number and stop the keypress
	        if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
	            e.preventDefault();
	        }
	    });

		$.ajax({
			type : "GET",
			url : "maps/getAvailableMaps",
			success : function(data) {
				$('#availableMapsName').find('option').remove();
				for (var i = 0; i < data.length; i++) {
					$('#availableMapsName').append($('<option>', {
						value : data[i],
						text : data[i]
					}));
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert("Failure fetching map");
			}
		});

		$('#choicesSelectModal').modal({
			backdrop : 'static',
			keyboard : false
		});
		
		function fetchTournamentChoices(){
			var tournametChoices={};
			var maps=[];
			$('#availableMapsName :selected').each(function(i, sel){ 
				 maps.push($(sel).val());
				});
			
			var strategies=[];
			$('#strategies :selected').each(function(i, sel){ 
				strategies.push($(sel).val());
				});
			
			var noOfGames = $('#noOfGames').val();
			var maxTurns = $('#maxTurns').val();
			if((maps == null || (maps != null && (maps.length > 5 || maps.length < 1))) || (strategies == null || (strategies != null && (strategies.length > 4 || strategies.length < 2))) || (noOfGames == null || (noOfGames != null && noOfGames < 1)) || (maxTurns == null || (maxTurns != null && maxTurns < 1))){
				return;
			}
			
			tournamentChoices = {
					mapNames:maps,
					multipleStrategies:strategies,
					noOfGamesToPlay:noOfGames,
					maxTurns:maxTurns
			}
			 
			return tournamentChoices;
		}
		
		function addMessagesToAuditLogs(data){
			var currentId = data.current_game_play_id;
			var tourStatus = data.status;
			var currStatus;
			for (var i=0;i<data.tournament.length;i++){
				if(currentId == data.tournament[i].game_play_id){
					currStatus = data.tournament[i].status;
				}
			}
			while(tourStatus != null && (tourStatus.includes("/n") || tourStatus.includes("\n"))){
				tourStatus = tourStatus.replace(/\n/g, "<br/>");
			}
			while(currStatus != null && (currStatus.includes("/n") || currStatus.includes("\n"))){
				currStatus = currStatus.replace(/\n/g, "<br/>");
			}
			$("#footer p").prepend(tourStatus);
			$("#footer p").prepend(currStatus);
			//auditLogsDataTable.row.add([ tourStatus,currStatus ])
			//.draw(false);			
		}
		
		function fillResults(data){
			for(var i=0; i < data.length; i++){
				auditLogsDataTable.row.add([ data[i].mapName,data[i].gameNo,data[i].winner ])
				.draw(false);
			}
		}
		
		function getResults(data){
			var a = JSON.stringify(data);
			$
			.ajax({
				type : "POST",
				url : "gamePlay/getTournamentResult",
				dataType : "json",
				data : a,
				contentType : "application/json",
				success : function(data) {
					stopLoading();
					fillResults(data.gameResult);					
				},
				error : function(XMLHttpRequest,
						textStatus, errorThrown) {
					stopLoading();
					alert("Startup Phase Failure");
				}
			});
		}
		
		function continueTournament(data){
			var a = JSON.stringify(data);
			$
			.ajax({
				type : "POST",
				url : "gamePlay/playTournament",
				dataType : "json",
				data : a,
				contentType : "application/json",
				success : function(data) {
					stopLoading();
					if(data.status != 'TOURNAMENT_OVER\n'){
						addMessagesToAuditLogs(data);
						setTimeout(function(){ continueTournament(data); }, 1);
					}else{
						var tourStatus = data.status.replace(/\n/g, "<br/>");
						$("#footer p").prepend(tourStatus);
						getResults(data);
					}
				},
				error : function(XMLHttpRequest,
						textStatus, errorThrown) {
					stopLoading();
					alert("Startup Phase Failure");
				}
			});			
		}
		
		function startTournament(){
			showLoading();
			var tournamentChoices = fetchTournamentChoices();
			if(typeof tournamentChoices == undefined || tournamentChoices == null){
				alert("Please select map(1-5), strategies(2-4) and atleast one no of games and max turns");
				stopLoading();
				return;
			}
			var a = JSON.stringify(tournamentChoices);
			$
			.ajax({
				type : "POST",
				url : "gamePlay/startTournament",
				dataType : "json",
				data : a,
				contentType : "application/json",
				success : function(data) {
					stopLoading();
					$('#choicesSelectModal').modal('toggle');
					if(typeof data.tournament == undefined || data.tournament ==null || (typeof data.tournament != undefined && data.tournament !=null && data.tournament.length == 0)){
						$("#footer p").prepend("<br/>"+data.status.replace(/\n/g, "<br/>"));
						return;
					}
					if(data.status != 'TOURNAMENT_OVER'){
						addMessagesToAuditLogs(data);
						continueTournament(data);
					}
				},
				error : function(XMLHttpRequest,
						textStatus, errorThrown) {
					stopLoading();
					alert("Startup Phase Failure");
				}
			});
		}
		
		$('#startTournament')
		.on(
				'click',
				function() {
					startTournament();					
				});
	});
</script>

</head>
<body>
	<table id="auditLogs" class="display" style="width: 100%">
		<thead>
			<tr>
				<th>Map Name</th>
				<th>Game no</th>
				<th>Winner</th>
			</tr>
		</thead>
		<tfoot>
			<tr>
				<th>Map Name</th>
				<th>Game no</th>
				<th>Winner</th>
			</tr>
		</tfoot>
	</table>
	<!-- Modal map selection -->
	<div class="modal fade" id="choicesSelectModal" tabindex="-1"
		role="dialog" aria-labelledby="choicesSelectModalTitle"
		aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="exampleModalLongTitle">Startup
						Phase</h5>

				</div>
				<div class="modal-body">
					<h6>Select Maps :</h6>
					<div class="form-group">
						<label for="availableMapsName">Map : </label> <select
							class="form-control form-control-sm" id="availableMapsName"
							multiple>
							<option></option>
						</select>
					</div>
					<div class="form-group">
						<label for="strategies">Select Strategies : </label> <select
							class="form-control form-control-sm" id="strategies" multiple>
							<option value="AGGRESSIVE">Aggressive</option>
							<option value="BENEVOLENT">Benevolent</option>
							<option value="RANDOM">Random</option>
							<option value="CHEATER">Cheater</option>
						</select>
					</div>
					<div>
						<label for="noOfGames">No of Games : </label> <input type="text"
							class="form-control" id="noOfGames">
					</div>
					<div>
						<label for="maxTurns">Max Turns : </label> <input type="text"
							class="form-control" id="maxTurns">
					</div>
				</div>

				<div class="modal-footer">
					<button id="startTournament" type="button" class="btn btn-primary"
						style="background-color: black; border-color: black"
						data-toggle="tooltip">Start Tournament</button>
				</div>
			</div>
		</div>
	</div>
	<div style="margin-bottom: 35%"></div>
</body>
</html>