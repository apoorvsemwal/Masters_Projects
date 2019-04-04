#include <opencv2/opencv.hpp>
#include <iostream>
#include <cstdlib>
#include <vector>
#include <cfloat>
#include <queue>
#include <unordered_map> 

using namespace cv;
using namespace std;

//This structure here represents an edge in the graph.
//Pixel - The destination pixel where edge goes into
//Capacity - Weight on the edge
struct GraphEdge
{
	int    pixel;
	int    capacity;
};

//Function Declarations

/*
BFS implementation for finding a path from source to sink while running Ford Fulkerson.
Parameters in the same sequence:
vector<GraphEdge>* - Adjacency matrix representing the image.
int* - Predecessor array to trace the path obtained from BFS.
int - Source Pixel
int - Sink Pixel
int - Height of the image
int - Width of the image
*/
bool   bfs_find_augmenting_path(vector<GraphEdge>*, int*, int, int, int, int);

/*
Ford Fulkerson implementation for finding min cut.
Parameters in the same sequence:
vector<GraphEdge>* - Adjacency matrix representing the image.
int - Source Pixel
int - Sink Pixel
int - Height of the image
int - Width of the image
*/
vector<GraphEdge>* min_cut_compute(vector<GraphEdge>*, int, int, int, int);

/*
BFS implementation used after finding a min cut to prepare the list of all pixels reachable from the source.
Parameters in the same sequence:
vector<GraphEdge>* - Adjacency matrix representing the image.
int - Source Pixel
int - Height of the image
int - Width of the image
*/
bool*  bfs_find_pixel_split(vector<GraphEdge> *, int, int, int);

/*
Preparing the final output image.
Parameters in the same sequence:
vector<GraphEdge>* - Adjacency matrix representing the image.
Mat - OpenCV matrix containing the output image.
int - Height of the image
int - Width of the image
*/
Mat    prepareResultImage(vector<GraphEdge>*, Mat, int, int);


int main(int argc, char** argv)
{
	if (argc != 4) {
		cout << "Usage: ../seg input_image initialization_file output_mask" << endl;
		return -1;
	}

	Mat in_image;
	//Loading the image
	in_image = imread(argv[1]);

	if (!in_image.data)
	{
		cout << "Could not load input image!!!" << endl;
		return -1;
	}

	if (in_image.channels() != 3) {
		cout << "Image does not have 3 channels!!! " << in_image.depth() << endl;
		return -1;
	}

	ifstream seed_file_read(argv[2]);
	if (!seed_file_read)
	{
		cout << "Could not read seed pixels file." << endl;
		return -1;
	}
	else
	{
		// Calculate the height and width of the image.
		int height = in_image.rows;
		int width  = in_image.cols;
		int size = height * width;

		//Number of pixels given as seeds.
		int num_of_seed_pixels;
		seed_file_read >> num_of_seed_pixels;

		//Preparing a matrix for Seed Pixels.
		//Later used for creating links to Source and Sink.
		vector<vector<int> > seeds(num_of_seed_pixels, vector<int>(3));
		unordered_map<int, int> seed_id_list;
		int seed_id;

		for (int i = 0; i < num_of_seed_pixels; i++)
		{
			//Seed Pixel - x cordinate, y cordinate, label - 1 - Foreground, 0 - Background.
			seed_file_read >> seeds[i][0] >> seeds[i][1] >> seeds[i][2];

			//Check if the seed pixel is valid or not.
			if ((seeds[i][1] < 0 || seeds[i][1] >= height) || (seeds[i][0] < 0 || seeds[i][0] >= width)) {
				cout << "Invalid Seed Pixel" << endl;
				return -1;
			}
			seed_id = (seeds[i][1] * width) + seeds[i][0];
			seed_id_list[seed_id] = seeds[i][2];
		}

		//This structure will hold the full adjacency matrix of our graph.
		//It is an array of Vectors.
		//Each entry in this array represents a pixel in the image, and
		//Pixels are indexed from 0 to (height*width + 2). Extra 2 pixels in the end
		//represent our source and the sink respectively.
		//Each pixel is then linked to a vector of edges. Containing the outgoing edges from the pixel. 
		vector<GraphEdge > *pixel_links = new vector<GraphEdge>[height*width + 2];

		Vec3b pix1_intensity;
		Vec3b pix2_intensity;

		int   avg_intensity1, avg_intensity2;

		//This will hold the unique pixel number. Mapping 2d cordinates of a pixel into unique index number.
		int map_2d_to_1d_u, map_2d_to_1d_v_rt, map_2d_to_1d_v_down;

		vector<GraphEdge> adjacent_pixels_u;
		vector<GraphEdge> adjacent_pixels_v_rt;
		vector<GraphEdge> adjacent_pixels_v_down;

		GraphEdge edge;
		GraphEdge edge_rev;

		//Graph is being prepared here.
		for (int y = 0; y < height; y++)
		{

			for (int x = 0; x < width; x++)
			{
				adjacent_pixels_u.erase(adjacent_pixels_u.begin(), adjacent_pixels_u.end());
				adjacent_pixels_u.shrink_to_fit();

				adjacent_pixels_v_rt.erase(adjacent_pixels_v_rt.begin(), adjacent_pixels_v_rt.end());
				adjacent_pixels_v_rt.shrink_to_fit();

				adjacent_pixels_v_down.erase(adjacent_pixels_v_down.begin(), adjacent_pixels_v_down.end());
				adjacent_pixels_v_down.shrink_to_fit();

				//This is where we convert a pixel represented by 2 co-ordinates into one unique id of pixel.
				map_2d_to_1d_u = (y*width) + x;
				if (pixel_links[map_2d_to_1d_u].size() > 0)
				{
					adjacent_pixels_u = pixel_links[map_2d_to_1d_u];
				}

				if (y + 1 < height)
				{
					//Pixel Below
					map_2d_to_1d_v_down = ((y + 1)*width) + x;
					if (pixel_links[map_2d_to_1d_v_down].size() > 0)
					{
						adjacent_pixels_v_down = pixel_links[map_2d_to_1d_v_down];
					}
					pix1_intensity = in_image.at<Vec3b>(y, x);
					pix2_intensity = in_image.at<Vec3b>(y + 1, x);

					//Luminosity method is used to convert pixel intensity to grayscale.
					//Refer: https://www.johndcook.com/blog/2009/08/24/algorithms-convert-color-grayscale/
					avg_intensity1 = (int)((pix1_intensity.val[0] * 0.07) + (pix1_intensity.val[1] * 0.72) + (pix1_intensity.val[2] * 0.21));
					avg_intensity2 = (int)((pix2_intensity.val[0] * 0.07) + (pix2_intensity.val[1] * 0.72) + (pix2_intensity.val[2] * 0.21));

					//Rational behind putting edge weights is that if the intensities of 2 pixels
					//have low difference then the weight will be high.  
					//And as the difference increases we penalise it by reducing the weight on the edge. 
					//Different strategy has been used for vertical and horizontal weights to break the symmetry.
					edge.capacity = (int)floor(5 * exp(-(pow(avg_intensity1 - avg_intensity2, 2))));
					edge.pixel = map_2d_to_1d_v_down;
					adjacent_pixels_u.push_back(edge);

					//Creating a reverse edge.
					edge_rev.capacity = edge.capacity;
					edge_rev.pixel = map_2d_to_1d_u;
					adjacent_pixels_v_down.push_back(edge_rev);
				}

				if (x + 1 < width)
				{
					//Pixel Right
					map_2d_to_1d_v_rt = (y*width) + x + 1;
					if (pixel_links[map_2d_to_1d_v_rt].size() > 0)
					{
						adjacent_pixels_v_rt = pixel_links[map_2d_to_1d_v_rt];
					}
					pix1_intensity = in_image.at<Vec3b>(y, x);
					pix2_intensity = in_image.at<Vec3b>(y, x + 1);

					//Luminosity method is used to convert pixel intensity to grayscale.
					//Refer: https://www.johndcook.com/blog/2009/08/24/algorithms-convert-color-grayscale/
					avg_intensity1 = (int)((pix1_intensity.val[0] * 0.07) + (pix1_intensity.val[1] * 0.72) + (pix1_intensity.val[2] * 0.21));
					avg_intensity2 = (int)((pix2_intensity.val[0] * 0.07) + (pix2_intensity.val[1] * 0.72) + (pix2_intensity.val[2] * 0.21));

					//Rational behind putting edge weights is that if the intensities of 2 pixels
					//have low difference then the weight will be high.  
					//And as the difference increases we penalise it by reducing the weight on the edge.
					edge.capacity = (int)floor(40000 * exp(-(pow(avg_intensity1 - avg_intensity2, 1))));					

					edge.pixel = map_2d_to_1d_v_rt;
					adjacent_pixels_u.push_back(edge);

					//Creating a reverse edge.
					edge_rev.capacity = edge.capacity;
					edge_rev.pixel = map_2d_to_1d_u;
					adjacent_pixels_v_rt.push_back(edge_rev);
				}

				if (adjacent_pixels_u.size() > 0)
				{
					pixel_links[map_2d_to_1d_u] = adjacent_pixels_u;
				}
				if (adjacent_pixels_v_rt.size() > 0)
				{
					pixel_links[map_2d_to_1d_v_rt] = adjacent_pixels_v_rt;
				}
				if (adjacent_pixels_v_down.size() > 0)
				{
					pixel_links[map_2d_to_1d_v_down] = adjacent_pixels_v_down;
				}
			}
		}

		GraphEdge source_edge;
		GraphEdge sink_edge;

		adjacent_pixels_u.erase(adjacent_pixels_u.begin(), adjacent_pixels_u.end());
		adjacent_pixels_u.shrink_to_fit();

		//Vector represents edge weights between source and the seed pixels in foreground.
		vector<GraphEdge> source_links;

		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				map_2d_to_1d_u = (y*width) + x;

				if (pixel_links[map_2d_to_1d_u].size() > 0)
				{
					adjacent_pixels_u = pixel_links[map_2d_to_1d_u];
				}

				for (auto x : seed_id_list) {
					if (x.first == map_2d_to_1d_u && x.second == 1)
					{
						//Link between source and seed pixels in foreground.
						//Assumed to have an infinite capacity. Here taken as maximum integer value.
						source_edge.capacity = INT_MAX;
						source_edge.pixel = map_2d_to_1d_u;
						source_links.push_back(source_edge);
						break;
					}
					else if (x.first == map_2d_to_1d_u && x.second == 0)
					{
						//Link between sink and seed pixels in background.
						//Assumed to have an infinite capacity. Here taken as maximum integer value.
						sink_edge.capacity = INT_MAX;
						sink_edge.pixel = height * width + 1;
						adjacent_pixels_u.push_back(sink_edge);
						pixel_links[map_2d_to_1d_u] = adjacent_pixels_u;
						break;
					}
				}
			}
		}

		//Adding all source links in the final graph.
		pixel_links[height*width] = source_links;

		//Running Ford Fulkerson to find the min cut.
		//Check the above declarations for parameter descriptions.
		pixel_links = min_cut_compute(pixel_links, height*width, height*width + 1, height, width);

		//Preparing the output image.
		Mat out_image = prepareResultImage(pixel_links, in_image, height, width);

		imwrite(argv[3], out_image);
		namedWindow("Original image", WINDOW_AUTOSIZE);
		namedWindow("Show Marked Pixels", WINDOW_AUTOSIZE);

		imshow("Original image", in_image);
		imshow("Show Marked Pixels", out_image);
		waitKey(0);
		return 0;
	}
}

bool bfs_find_augmenting_path(vector<GraphEdge> *residual_graph, int *predecessor, int src, int dest, int height, int width) {

	queue <int> vertex_queue;

	//Creating a visited array for the vertex set with all vertices initially marked as false - Non-Visited
	bool *visited_vertices = new bool[height*width + 2];
	for (int i = 0; i < height*width + 2; i++)
	{
		visited_vertices[i] = false;
	}

	vertex_queue.push(src);

	//Vertex src will be the first vertex of the path there will not be any predecessor vertex before that.
	predecessor[src] = -1;

	vector<GraphEdge>::iterator i;

	bool dest_set = false;
	visited_vertices[src] = true;

	while (!vertex_queue.empty()) {

		int curr_vertex = vertex_queue.front();
		vertex_queue.pop();

		for (i = residual_graph[curr_vertex].begin(); i != residual_graph[curr_vertex].end(); i++)
		{
			//If the vertex has not been visited and there exists a direct path between the two vertices,
			//add it to the queue, set its predecessor on the path vector, mark it as visited.
			if (visited_vertices[i->pixel] == false && i->capacity > 0)
			{
				vertex_queue.push(i->pixel);
				predecessor[i->pixel] = curr_vertex;
				visited_vertices[i->pixel] = true;
				//If we have found a path till sink there is no point of running the bfs so we stop it.
				if (visited_vertices[dest] == true)
				{
					dest_set = true;
					break;
				}
			}
		}
		if (dest_set)
		{
			break;
		}
	}

	return visited_vertices[dest];
}

vector<GraphEdge>*  min_cut_compute(vector<GraphEdge> *residual_graph, int src, int sink, int height, int width) {

	int *predecessor = new int[height*width];
	for (int i = 0; i < height*width; i++)
	{
		predecessor[i] = -1;
	}

	vector<GraphEdge>::iterator j;
	vector<GraphEdge>::iterator k;

	while (bfs_find_augmenting_path(residual_graph, predecessor, src, sink, height, width))
	{

		int limiting_val_flow = INT_MAX;

		//Finding the minimum flow limiting value in the chosen path after running BFS.
		for (int i = sink; i != src && i != -1; i = predecessor[i])
		{
			for (j = residual_graph[predecessor[i]].begin(); j != residual_graph[predecessor[i]].end(); j++)
			{
				if (j->pixel == i && limiting_val_flow > j->capacity)
				{
					limiting_val_flow = j->capacity;
					break;
				}
			}
		}

		//Preparing the residual graph for next iteration.
		for (int i = sink; i != src && i != -1; i = predecessor[i])
		{
			for (j = residual_graph[predecessor[i]].begin(); j != residual_graph[predecessor[i]].end(); j++)
			{
				if (j->pixel == i)
				{
					j->capacity -= limiting_val_flow;
					for (k = residual_graph[j->pixel].begin(); k != residual_graph[j->pixel].end(); k++)
					{
						if (k->pixel == predecessor[i])
						{
							k->capacity += limiting_val_flow;
							break;
						}
					}
					break;
				}
			}
		}

	}

	//At this point we have achieved the maximum flow in the graph.
	//This implies that now all the pixels reachable from Source belong to foreground
	//and pixels not reachable from Source belong to background.
	return residual_graph;
}

//Finds and returns all the pixels reachable from source vertex after max flow has been achieved.
bool* bfs_find_pixel_split(vector<GraphEdge> *residual_graph, int src, int height, int width) {

	queue <int> vertex_queue;

	bool *visited_vertices = new bool[height*width + 2];
	for (int i = 0; i < height*width + 2; i++)
	{
		visited_vertices[i] = false;
	}

	vertex_queue.push(src);

	vector<GraphEdge>::iterator i;

	visited_vertices[src] = true;

	while (!vertex_queue.empty()) {

		int curr_vertex = vertex_queue.front();
		vertex_queue.pop();

		for (i = residual_graph[curr_vertex].begin(); i != residual_graph[curr_vertex].end(); i++)
		{
			if (visited_vertices[i->pixel] == false && i->capacity > 0)
			{
				vertex_queue.push(i->pixel);
				visited_vertices[i->pixel] = true;
			}
		}
	}

	//This holds a boolean entry for every pixel.
	//If the pixel is reachable from source - True
	//If the pixel is not reachable from source - False
	return visited_vertices;
}

//Preparing the output image.
Mat  prepareResultImage(vector<GraphEdge>* residual_graph, Mat in_image, int height, int width) {

	int src = height * width;
	bool *visited_vertices = bfs_find_pixel_split(residual_graph, src, height, width);

	int map_2d_to_1d;

	Mat out_image = in_image.clone();
	for (int y = 0; y < in_image.rows; y++)
	{
		for (int x = 0; x < in_image.cols; x++)
		{
			map_2d_to_1d = (y*width) + x;

			if (visited_vertices[map_2d_to_1d])
			{
				out_image.at<Vec3b>(y, x).val[0] = 255;
				out_image.at<Vec3b>(y, x).val[1] = 255;
				out_image.at<Vec3b>(y, x).val[2] = 255;
			}
			else
			{
				out_image.at<Vec3b>(y, x).val[0] = 0;
				out_image.at<Vec3b>(y, x).val[1] = 0;
				out_image.at<Vec3b>(y, x).val[2] = 0;
			}

		}
	}
	return out_image;
}