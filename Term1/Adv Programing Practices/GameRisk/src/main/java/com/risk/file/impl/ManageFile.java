package com.risk.file.impl;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.risk.file.IManageFile;
import com.risk.model.file.Continent;
import com.risk.model.file.File;
import com.risk.model.file.Map;
import com.risk.model.file.Territory;

/**
 * This class is use to deal to with map traditional file read and write
 * operation.
 * 
 * @author <a href="mailto:mayankjariwala1994@gmail.com">MayankJariwala</a>
 * @version 0.0.1
 */
@Service
public class ManageFile implements IManageFile {

	private String file_name;
	private Map filelayer_map_object;
	private File file_object;
	// Indicate Active Section such as Territory,Continent,Map
	private FileSectionDivider current_section_in_file;
	private List<Continent> continent_object_list;
	private List<Territory> territory_object_list;

	// Indicates 3 Section in Map File
	enum FileSectionDivider {
		MAP, CONTINENT, TERRITORY;
	}

	public ManageFile() {
	}

	/**
	 * 
	 * @param file_name Name of the Map File
	 */
	public ManageFile(String file_name) {
		this.file_name = file_name;
	}

	/**
	 * @see com.risk.file.IManageFile#retreiveFileObject()
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">MayankJariwala</a>
	 */
	@Override
	public File retreiveFileObject() {
		removeBlankLinesFromFile(file_name);
		String line = "";
		file_object = new File();
		filelayer_map_object = null;
		current_section_in_file = null;
		continent_object_list = new ArrayList<Continent>();
		territory_object_list = new ArrayList<Territory>();
		try (BufferedReader map_file_object = new BufferedReader(
				new FileReader("src/main/resource/Maps/" + file_name))) {
			while ((line = map_file_object.readLine()) != null) {
				if (line.length() > 0) {
					if (line.equalsIgnoreCase("[Map]")) {
						filelayer_map_object = new Map();
						current_section_in_file = FileSectionDivider.MAP;
						line = map_file_object.readLine();
					} else if (line.equalsIgnoreCase("[Continents]")) {
						current_section_in_file = FileSectionDivider.CONTINENT;
						line = map_file_object.readLine();
					} else if (line.equalsIgnoreCase("[TERRITORIES]")) {
						current_section_in_file = FileSectionDivider.TERRITORY;
						line = map_file_object.readLine();
					}

					if (current_section_in_file == FileSectionDivider.MAP)
						setValuesToFileLayerMapObject(line);
					if (current_section_in_file == FileSectionDivider.CONTINENT)
						setValuesToFileLayerContientObject(line);
					if (current_section_in_file == FileSectionDivider.TERRITORY)
						setValuesToFileLayerTerritoryObject(line);
				} else if (current_section_in_file != FileSectionDivider.TERRITORY) {
					current_section_in_file = null;
				}
			}
			// Set Values to file Object
			file_object.setMap(filelayer_map_object);
			file_object.setContinents(continent_object_list);
			file_object.setTerritories(territory_object_list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file_object;
	}

	/**
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">MayankJariwala</a>
	 * @see com.risk.file.IManageFile#saveFileToDisk(File, String)
	 */
	@Override
	public Boolean saveFileToDisk(File file, String file_name) {
		boolean file_writer_message = false;
		file_name = file_name.endsWith(".map") ? file_name : file_name + ".map";
		try (PrintStream map_file_writer = new PrintStream(
				new BufferedOutputStream(new FileOutputStream("src/main/resource/Maps/" + file_name)))) {
			map_file_writer.println("[Map]");
			map_file_writer.println("author=" + file.getMap().getAuthor());
			map_file_writer.println("image=" + file.getMap().getImage());
			map_file_writer.println("wrap=" + file.getMap().getWrap());
			map_file_writer.println("scroll=" + file.getMap().getScroll());
			map_file_writer.println("warn=" + file.getMap().getWarn());
			map_file_writer.println();

			map_file_writer.println("[Continents]");
			for (Continent continent_info : file.getContinents()) {
				map_file_writer.println(continent_info.getName() + "=" + continent_info.getScore());
			}
			map_file_writer.println();

			map_file_writer.println("[Territories]");
			for (Territory territory_info : file.getTerritories()) {
				String each_territory_info = "";
				each_territory_info = StringUtils.join(new String[] { territory_info.getName(),
						String.valueOf(territory_info.getX_coordinate()),
						String.valueOf(territory_info.getY_coordinate()), territory_info.getPart_of_continent() }, ",");
				List<String> each_adj_territory = new ArrayList<>();
				for (String adj_territories : territory_info.getAdj_territories()) {
					each_adj_territory.add(adj_territories);
				}
				each_territory_info += "," + StringUtils.join(each_adj_territory, ",");
				map_file_writer.println(each_territory_info);
			}
			if (map_file_writer.checkError()) {
				file_writer_message = false;
			} else {
				file_writer_message = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file_writer_message;
	}

	/**
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">MayankJariwala</a>
	 * @see com.risk.file.IManageFile#fetchMapFilesFromResource()
	 */
	@Override
	public List<String> fetchMapFilesFromResource() {
		List<String> list_of_map_files = new ArrayList<>();
		java.io.File resource_folder = new java.io.File("src/main/resource/Maps");
		java.io.File[] listOfMapFiles = resource_folder.listFiles();
		if (listOfMapFiles.length > 0) {
			for (java.io.File file : listOfMapFiles) {
				if (file.isFile() && file.getName().endsWith(".map")) {
					list_of_map_files.add(file.getName());
				}
			}
		}
		return list_of_map_files;
	}

	/**
	 * This function is use to set value of territory model entities at file layer
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">MayankJariwala</a>
	 * @param territory_info Information Regarding Territory
	 */
	private void setValuesToFileLayerTerritoryObject(String territory_info) {
		if (territory_info.length() > 0) {
			List<String> adjacent_territories = new ArrayList<String>();
			Territory territory = new Territory();
			String[] key_value = territory_info.split(",");
			int length = key_value.length;
			territory.setName(key_value[0].trim());
			territory.setX_coordinate(Integer.parseInt(key_value[1]));
			territory.setY_coordinate(Integer.parseInt(key_value[2]));
			territory.setPart_of_continent(key_value[3].trim());
			for (int i = 4; i < length; i++) {
				adjacent_territories.add(key_value[i].trim());
			}
			territory.setAdj_territories(adjacent_territories);
			if (territory != null) {
				territory_object_list.add(territory);
			}
		}
	}

	/**
	 * This function is use to set value of continent model entities at file layer
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">MayankJariwala</a>
	 * @param continent_info Information Regarding Continent
	 */
	private void setValuesToFileLayerContientObject(String continent_info) {
		Continent continent = new Continent();
		String[] key_value = continent_info.split("=");
		continent.setName(key_value[0].trim());
		continent.setScore(Integer.parseInt(key_value[1].trim()));
		continent_object_list.add(continent);
	}

	/**
	 * This function is use to set value of map model entities at file layer
	 * 
	 * @author <a href="mailto:mayankjariwala1994@gmail.com">MayankJariwala</a>
	 * @param map_info The basic information about Map
	 */
	private void setValuesToFileLayerMapObject(String map_info) {
		String[] key_value = map_info.split("=");
		if (key_value[0].equalsIgnoreCase("author")) {
			filelayer_map_object.setAuthor(key_value[1].trim());
		} else if (key_value[0].equalsIgnoreCase("image")) {
			filelayer_map_object.setImage(key_value[1].trim());
		} else if (key_value[0].equalsIgnoreCase("wrap")) {
			filelayer_map_object.setWrap(key_value[1].trim());
		} else if (key_value[0].equalsIgnoreCase("scroll")) {
			filelayer_map_object.setScroll(key_value[1].trim());
		} else if (key_value[0].equalsIgnoreCase("warn")) {
			filelayer_map_object.setWarn(key_value[1].trim());
		}
	}

	/**
	 * This function is use to remove all blank lines from map file
	 * 
	 * @author <a href="mayankjariwala1994@gmail.com"> Mayank Jariwala </a>
	 * @author <a href="himansipatel1994@gmail.com"> Himansi Patel </a>
	 * @param file_name
	 */
	private void removeBlankLinesFromFile(String file_name) {
		file_name = file_name.endsWith(".map") ? file_name.split("\\.")[0] : file_name;
		String original_file = "src/main/resource/Maps/" + file_name + ".map";
		String file_content = "";
		String line = "";
		try (BufferedReader buffered_reader = new BufferedReader(new FileReader(original_file));) {
			while ((line = buffered_reader.readLine()) != null) {
				if (!line.isEmpty()) {
					file_content += line.trim() + System.getProperty("line.separator");
				}
			}
			BufferedWriter file_writer = new BufferedWriter(new FileWriter(original_file));
			file_writer.write(file_content);
			file_writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
