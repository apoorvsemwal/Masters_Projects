<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">



<script type="text/javascript">
	$(document).ready(
			function() {
				
				//$("#updateCountry").attr("disabled", "disabled");
				
				var currentLoadedMap;
				
				$("#score").keydown(function (e) {
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

				var continentDataTable = $('#continentsDesc').DataTable();
				var countryDataTable = $('#countriesDesc').DataTable();
				
				//css change
				$("#countriesDesc_next").css( "color","black" );
				$("#countriesDesc_previous").css( "color","black" );
				$("#continentsDesc_previous").css( "color","black" );
				$("#continentsDesc_next").css( "color","black" );

				function parseMapData(data) {
					if(typeof(data) == undefined || data == null){
						alert("Invalid Map");
						return;
					}

					continentDataTable.clear().draw();
					for (var i = 0; i < data.continents.length; i++) {
						addRowInContinentDataTable(data.continents[i].name,
								data.continents[i].score);
					}

					countryDataTable.clear().draw();
					for (var i = 0; i < data.territories.length; i++) {
						addRowInCountryDataTable(data.territories[i].name,
								data.territories[i].continentName,
								data.territories[i].neighbours);
					}

				}

				function addRowInContinentDataTable(column1, column2) {
					continentDataTable.row.add([ column1, column2 ])
							.draw(false);
				}

				function addRowInCountryDataTable(column1, column2, column3) {
					countryDataTable.row.add([ column1, column2, column3 ])
							.draw(false);
				}
				
				function populateContinentForm(row){
					$('#continentName').val(row[0]);
					$('#score').val(row[1]);
				}
				
				$('#continentsDesc tbody').on( 'click', 'tr', function () {
				    var index = continentDataTable.row( this ).index();
				    //continentDataTable.row(index).remove().draw( false );
					var row = continentDataTable.row( this ).data();
					populateContinentForm(row);
					continentDataTable.row(index).remove().draw( false );
				    alert("Selecting this row will delete row from table and details of this row will got populated in country form. Please add the row if you want to cancel the update");
				    window.scrollTo(0, 100);
				} );
				
				function populateCountryForm(row){
					$('#countryName').val(row[0]);
					$('#continent').val(row[1]);
					$('#neighbors').val(row[2]);
				}
				
				$('#countriesDesc tbody').on( 'click', 'tr', function () {
				    var index = countryDataTable.row( this ).index();
				    var row = countryDataTable.row( this ).data();
				    populateCountryForm(row);
				    //$("#updateCountry").removeAttr("disabled");
				    //$("#addCountry").attr("disabled", "disabled");
				    countryDataTable.row(index).remove().draw( false );
				    alert("Selecting this row will delete row from table and details of this row will got populated in country form. Please add the row if you want to cancel the update");
				    window.scrollTo(0, 100);
				} );;

				$('#getFullMap').on(
						'click',
						function() {
							showLoading();
							var selectedMap=$( "#availableMapsName option:selected" ).text();
							//set current map in global variable
							//currentLoadedMap = selectedMap;
							if(selectedMap == ''){
								return;																
							}
							$.ajax({
								type : "GET",
								data: $.param({ fileName: selectedMap }),
								url : "maps/map",
								success : function(data) {debugger;
									if(data !=null){
										if(data.status == null || data.status ==''){
											parseMapData(data);
										}
										if(data.status !=null && data.status != ''){
											alert(data.status);
										}
									}else{
										alert("Error while loading map");
									}
									$('#loadMapModal').modal('toggle');
									stopLoading();
								},
								error : function(XMLHttpRequest, textStatus,
										errorThrown) {
									stopLoading();
									$('#loadMapModal').modal('toggle');
									alert("Failure loading map");
								}
							});
						});
				
				$('#getMapName').on(
						'click',
						function() {
							showLoading();
							$.ajax({
								type : "GET",
								url : "maps/getAvailableMaps",
								success : function(data) {
									$('#availableMapsName')
								    .find('option')
								    .remove();
									for(var i=0; i< data.length;i++){
										$('#availableMapsName').append($('<option>', {
										    value: data[i],
										    text: data[i]
										}));
									}
									stopLoading();
								},
								error : function(XMLHttpRequest, textStatus,
										errorThrown) {
									stopLoading();
									alert("Failure fetching map");
								}
							});
						});
				
				function checkIfDuplicateContinent(continentToBeChecked){
					var existingContinent = continentDataTable.columns( 0 ).data()[0];
					for(var i=0; i<existingContinent.length; i++){
						if(existingContinent[i].toUpperCase() === continentToBeChecked.toUpperCase()){
							return true;
						}
					}
					return false;
				}

				$('#addContinent').on('click', function() {
										var cont = $('#continentName').val();
										var sco = $('#score').val();
										
										if(checkIfDuplicateContinent(cont)){
											alert("Contient already present with name : " + cont);
											return;
										}
										
										
										if(typeof(cont)!=undefined && typeof(sco)!=undefined && cont != '' && sco != ''){
												addRowInContinentDataTable(cont, sco);
												$('#continentName').val("");
												$('#score').val("");
											}					
				});
				
				function checkIfContinentPresent(continentToBeChecked){
					var existingContinent = continentDataTable.columns( 0 ).data()[0];
					for(var i=0; i<existingContinent.length; i++){
						if(existingContinent[i].toUpperCase() === continentToBeChecked.toUpperCase()){
							return true;
						}
					}
					return false;
				}
				
				function checkIfDuplicateCountry(countryToBeChecked){
					var existingCountry = countryDataTable.columns( 0 ).data()[0];
					for(var i=0; i<existingCountry.length; i++){
						if(existingCountry[i].toUpperCase() === countryToBeChecked.toUpperCase()){
							return true;
						}
					}
					return false;
				}
				
				function checkIfNeighboursPresent(neighboursToBeChecked){
					for(var i=0; i<neighboursToBeChecked.length; i++){
						if(neighboursToBeChecked[i] != '' && !checkIfDuplicateCountry(neighboursToBeChecked[i])){
							return false;
						}
					}
					return true;
				}
				
				$('#addCountry').on('click', function() {
				var con = $('#countryName').val();
				var conti = $('#continent').val();
				var neigh = $('#neighbors').val();
				if(!checkIfContinentPresent(conti)){
					alert("Please add continent : " + conti+" in continent table");
					return;
				}
				
				if(checkIfDuplicateCountry(con)){
					alert("Country already present with name : " + con);
					return;
				}
				
				if(!checkIfNeighboursPresent(neigh.split(";"))){
					alert("One or more neighbours countries not present");
					return;
				}
				
				if(typeof(con)!=undefined && typeof(conti)!=undefined && con != '' && conti != ''){
					addRowInCountryDataTable(con, conti, neigh);
						$('#countryName').val("");
						$('#continent').val("");
						$('#neighbors').val("");
					}
				});
				
				function formArraylistOfContinentToSave(data){
					var continentArray=[];
					for(var i = 0; i<data.length;i++){
						continentArray.push({name: data[i][0], score : data[i][1]});						
					}
					return continentArray;
				}
				
				function formArraylistOfCountryToSave(data){
					var countryArray=[];
					for(var i = 0; i<data.length;i++){
						countryArray.push({name: data[i][0], continentName : data[i][1], neighbours : data[i][2]});						
					}
					return countryArray;
				}
				
				$('#save').on('click', function() {
					$('#saveMapModal').modal('toggle');
					var loadedMapName = $( "#availableMapsName option:selected" ).text();
					$("#saveNameForMap").val(loadedMapName);
				});
				
				$('#saveMap').on('click', function() {
					$('#saveMapModal').modal('toggle');
					var data = continentDataTable.rows().data();
					var continentArray = formArraylistOfContinentToSave(data);
					data = countryDataTable.rows().data();
					var countryAray = formArraylistOfCountryToSave(data);
					var name = $("#saveNameForMap").val();
					var map = {currentMap: name,continents: continentArray, territories: countryAray};
					var a = JSON.stringify(map);
					$.ajax({
						type : "POST",
						url : "maps/save",
						dataType: "json",
					    data: a,
					    contentType: "application/json",
						success : function(data) {
							if(data !=null){
								if(data.status == null || data.status ==''){
									alert("success saving map");
								}
								if(data.status !=null && data.status != ''){
									alert(data.status);
								}
							}else{
								alert("Error while saving map");
							}
						},
						error : function(XMLHttpRequest, textStatus,
								errorThrown) {
							alert("Invalid Map. Please check");
						}
					});
					
					});
			
			});
</script>

</head>
<body>
	<div class="row">
		<div class="col-sm-6">
			<h2>Add Continent</h2>
			<div class="form-group">
				<label for="continentName">Continent Name</label> <input type="text"
					class="form-control" id="continentName">
			</div>
			<div class="form-group">
				<label for="score">Score</label> <input type="text"
					class="form-control" id="score" placeholder="e.g: 1 or 2 etc">
			</div>
			<button id="addContinent" class="btn btn-primary col-sm-3"
				style="background-color: black; border-color: black">Add</button>
		</div>
		<div class="col-sm-6">
			<h2>Add Country</h2>
			<div class="form-group">
				<label for="countryName">Country Name</label> <input type="text"
					class="form-control" id="countryName">
			</div>
			<div class="form-group">
				<label for="continent">Continent</label> <input type="text"
					class="form-control" id="continent">
			</div>
			<div class="form-group">
				<label for="score">Neighbors</label> <input type="text"
					class="form-control" id="neighbors" placeholder="country1;country2">
			</div>
			<button id="addCountry" class="btn btn-primary col-sm-3"
				style="background-color: black; border-color: black">Add</button>
		</div>
	</div>

	<div style="margin-top: 2%">
		<h2>Continents</h2>
		<p>Please select row to update</p>
		<div>
			<table id="continentsDesc" class="display" style="width: 100%">
				<thead>
					<tr>
						<th>Name</th>
						<th>Score</th>
					</tr>
				</thead>
				<tfoot>
					<tr>
						<th>Name</th>
						<th>Score</th>
					</tr>
				</tfoot>
			</table>
		</div>
	</div>

	<div style="margin-top: 2%">
		<h2>Territories</h2>
		<p>Please select row to update</p>
		<div>
			<table id="countriesDesc" class="display" style="width: 100%">
				<thead>
					<tr>
						<th>Name</th>
						<th>Continent</th>
						<th>Neighbors</th>
					</tr>
				</thead>
				<tfoot>
					<tr>
						<th>Name</th>
						<th>Continent</th>
						<th>Neighbors</th>
					</tr>
				</tfoot>
			</table>
		</div>
	</div>

	<button id="save" class="btn btn-secondary"
		style="background-color: black; border-color: black">Save</button>
	<button id="getMapName" class="btn btn-primary"
		style="background-color: black; border-color: black"
		data-toggle="modal" data-target="#loadMapModal">Load Map</button>



	<!-- Modal -->
	<div class="modal fade" id="loadMapModal" tabindex="-1" role="dialog"
		aria-labelledby="loadMapModalTitle" aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="exampleModalLongTitle">Select Map
						to load</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<select class="form-control form-control-sm" id="availableMapsName">
						<option></option>
					</select>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary"
						style="background-color: black; border-color: black"
						data-dismiss="modal">Close</button>
					<button id="getFullMap" type="button" class="btn btn-primary"
						style="background-color: black; border-color: black">Load</button>
				</div>
			</div>
		</div>
	</div>

	<!-- Modal -->
	<div class="modal fade" id="saveMapModal" tabindex="-1" role="dialog"
		aria-labelledby="loadMapModalTitle" aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="exampleModalLongTitle">Confirm
						name</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<div class="form-group">
						<p>Provide a name to save/update the map file.(Use the same
							name to update the currently loaded map.)</p>
						<label for="saveNameForMap">Map Name</label> <input type="text"
							class="form-control" id="saveNameForMap">
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary"
						style="background-color: black; border-color: black"
						data-dismiss="modal">Close</button>
					<button id="saveMap" type="button" class="btn btn-primary"
						style="background-color: black; border-color: black">Save</button>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
</html>