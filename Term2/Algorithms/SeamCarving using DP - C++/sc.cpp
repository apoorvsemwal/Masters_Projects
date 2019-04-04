#include <opencv2/opencv.hpp>
#include <algorithm>
#include <climits>
#include "sc.h"

using namespace cv;
using namespace std;

/*
Rational - Energy of a pixel is a quantifiable measure of its importance in image. 
Higher the energy higher is its importance.
Therefore the algorithm selects those seams to cut which have pixels with lower energies or we can say are
less important.
*/

/*
Method takes the input image and returns a new image Matrix which represents the Energy Grid.
It uses the OpenCV Laplacian operator to calculate the second order gradient image.
The returned image is of type CV_64F. Floating point value for better precision with 64 as Bit Depth.
*/
Mat prepare_energy_grid(Mat in_img);

/*
Method takes the Energy Grid and returns a cumulative energy matrix.
It makes use of Dynamic Programing to find a cumulative energy matrix.
*/
Mat find_cumulative_energy_vert(Mat energy_grid_org, int height, int width);
Mat find_cumulative_energy_hori(Mat energy_grid_org, int height, int width);

/*
Methods takes the cumulative energy matrix and returns a min seam.
Min Seam is the one where - Sum of energies of all the pixels along that seam is the lowest with 
respect to any other seam.
*/
int* find_seam_vert(Mat cumul_energy_grid, int height, int width);
int* find_seam_hori(Mat cumul_energy_grid, int height, int width);

/*
Methods takes the min seam and based on that removes a set of pixels from the original image.
Each Horizontal Seam Removal - Reduces the Height of the image by 1.
Each Vertical Seam Removal - Reduces the Width of the image by 1.
*/
Mat remove_seam_vert(Mat in_img, int* min_seam);
Mat remove_seam_hori(Mat in_img, int* min_seam);

bool seam_carving(Mat& in_image, int new_width, int new_height, Mat& out_image) {
	
	//Checks for valid input parameters.
	if (new_width > in_image.cols) {
		cout << "Invalid request!!! new_width has to be smaller than the current size!" << endl;
		return false;
	}
	if (new_height > in_image.rows) {
		cout << "Invalid request!!! new_height has to be smaller than the current size!" << endl;
		return false;
	}

	if (new_width <= 0) {
		cout << "Invalid request!!! new_width has to be positive!" << endl;
		return false;
	}

	if (new_height <= 0) {
		cout << "Invalid request!!! new_height has to be positive!" << endl;
		return false;
	}

	Mat in_img_clone = in_image.clone();

	//Number of vertical seams to cut
	int vertical_seams = in_image.cols - new_width;
	
	//Number of horizontal seams to cut
	int horizontal_seams = in_image.rows - new_height;
	
	//Cutting Vertical Seams
	for (int i = 1; i <= vertical_seams; i++)
	{
		Mat energy_grid = prepare_energy_grid(in_img_clone);
		Mat cumul_energy_grid = find_cumulative_energy_vert(energy_grid, in_img_clone.rows, in_img_clone.cols);
		int*  min_seam = find_seam_vert(cumul_energy_grid, in_img_clone.rows, in_img_clone.cols);
		remove_seam_vert(in_img_clone, min_seam).copyTo(in_img_clone);
		delete[] min_seam;
	}

	//Cutting Horizontal Seams
	for (int i = 1; i <= horizontal_seams; i++)
	{
		Mat energy_grid = prepare_energy_grid(in_img_clone);
		Mat cumul_energy_grid = find_cumulative_energy_hori(energy_grid, in_img_clone.rows, in_img_clone.cols);
		int*  min_seam = find_seam_hori(cumul_energy_grid, in_img_clone.rows, in_img_clone.cols);
		remove_seam_hori(in_img_clone, min_seam).copyTo(in_img_clone);
		delete[] min_seam;
	}

	out_image = in_img_clone;
	return true;
}

/*
Check the description above for details.
*/
Mat prepare_energy_grid(Mat in_img)
{

	Mat out_img = Mat::zeros(in_img.size(), CV_32FC1);

	Mat img_gauss, img_gray;

	int scale = 1;
	int delta = 0;
	int ddepth = CV_16S;
	Mat grad_img;
	Mat abs_grad_img;

	//*Note : The following code to obtain EnergyGrid has been refferred from the official OpenCV Documentation of
	//Laplacian operator. It calulates the second order derivative and can be used to detect edges. 
	//In fact, since the Laplacian uses the gradient of images, it calls internally 
	//the Sobel operator to perform its computation.
	//https://docs.opencv.org/3.4/d5/db5/tutorial_laplace_operator.html

	//To remove any noises
	GaussianBlur(in_img, img_gauss, Size(3, 3), 0, 0, BORDER_DEFAULT);

	//Converting it to a grayscale image
	cvtColor(img_gauss, img_gray, COLOR_BGR2GRAY);

	//Applying Laplacian operator
	Laplacian(img_gray, grad_img, ddepth, 3, scale, delta, BORDER_DEFAULT);

	//Convert results to Type CV_8U - 8 bit unsigned (0-255)
	convertScaleAbs(grad_img, abs_grad_img);

	//Convert results to float for better precision.
	abs_grad_img.convertTo(out_img, CV_64F, 1.0 / 255.0);

	return out_img;
}

/*
Check the description above for details.
*/
Mat find_cumulative_energy_vert(Mat energy_grid_org, int height, int width)
{
	Mat cumul_energy_grid = Mat::zeros(energy_grid_org.size(), energy_grid_org.type());

	int left_pix_idx, right_pix_idx;

	/*
	For the first row cumulative energy is same as the original energy.
	As there is no row before that.
	*/
	for (int i = 0; i < width; i++)
	{
		cumul_energy_grid.at<double>(0, i) = energy_grid_org.at<double>(0, i);
	}

	for (int y = 1; y < height; y++)
	{
		for (int x = 0; x < width; x++)
		{
			/*
			Each Non Boundary pixel's energy is based on 3 pixels on the previous row.
			Diagonally Left - Up 
			Adjacent - Up
			Diagonally Right - Up
			We consider the min of those three to calculate cumulative energy.
			*/
			left_pix_idx = max(0, x - 1);
			right_pix_idx = min(width - 1, x + 1);
			cumul_energy_grid.at<double>(y, x) = energy_grid_org.at<double>(y, x);
			cumul_energy_grid.at<double>(y, x) += min(min(cumul_energy_grid.at<double>(y - 1, left_pix_idx), cumul_energy_grid.at<double>(y - 1, x)), cumul_energy_grid.at<double>(y - 1, right_pix_idx));
		}
	}

	return cumul_energy_grid;
}

/*
Check the description above for details.
*/
Mat find_cumulative_energy_hori(Mat energy_grid_org, int height, int width)
{
	Mat cumul_energy_grid = Mat::zeros(energy_grid_org.size(), energy_grid_org.type());
	int up_pix_idx, down_pix_idx;

	/*
	For the first column cumulative energy is same as the original energy.
	As there is no column before that.
	*/
	for (int i = 0; i < height; i++)
	{
		cumul_energy_grid.at<double>(i, 0) = energy_grid_org.at<double>(i, 0);
	}

	for (int y = 1; y < width; y++)
	{
		for (int x = 0; x < height; x++)
		{
			/*
			Each Non Boundary pixel's energy is based on 3 pixels on the previous Column.
			Diagonally Left - Up 
			Adjacent - Left
			Diagonally Left - Down 
			We consider the min of those three to calculate cumulative energy.
			*/
			up_pix_idx = max(0, x - 1);
			down_pix_idx = min(height - 1, x + 1);
			cumul_energy_grid.at<double>(x, y) = energy_grid_org.at<double>(x, y);
			cumul_energy_grid.at<double>(x, y) += min(min(cumul_energy_grid.at<double>(up_pix_idx, y - 1), cumul_energy_grid.at<double>(x, y - 1)), cumul_energy_grid.at<double>(down_pix_idx, y - 1));
		}
	}
	return cumul_energy_grid;
}


int* find_seam_vert(Mat cumul_energy_grid, int height, int width) {

	int*  min_seam = new int[height];

	double min_energy = DBL_MAX;
	int min_idx = -1;

	for (int i = 0; i < width; i++)
	{
		if (min_energy > cumul_energy_grid.at<double>(height - 1, i))
		{
			min_energy = cumul_energy_grid.at<double>(height - 1, i);
			min_idx = i;
		}
	}

	if (min_idx != -1)
	{
		int curr_pix_idx = min_idx;
		int small_pix_idx = min_idx;
		min_seam[height - 1] = curr_pix_idx;

		int bottom_up = height - 2;

		while (bottom_up >= 0)
		{
			int left_pix_idx = curr_pix_idx;
			int right_pix_idx = curr_pix_idx;

			if (curr_pix_idx > 0)
			{
				left_pix_idx--;
			}
			if (curr_pix_idx < width - 1)
			{
				right_pix_idx++;
			}

			/*
			Bottom Up Traversal to build seam (From Down to Up). 
			*/
			if (cumul_energy_grid.at<double>(bottom_up, left_pix_idx) < cumul_energy_grid.at<double>(bottom_up, curr_pix_idx))
			{
				small_pix_idx = left_pix_idx;
			}
			if (cumul_energy_grid.at<double>(bottom_up, right_pix_idx) < cumul_energy_grid.at<double>(bottom_up, small_pix_idx))
			{
				small_pix_idx = right_pix_idx;
			}
			curr_pix_idx = small_pix_idx;
			min_seam[bottom_up] = curr_pix_idx;
			bottom_up--;
		}
	}

	return min_seam;
}

int* find_seam_hori(Mat cumul_energy_grid, int height, int width) {

	int*  min_seam = new int[width];

	double min_energy = DBL_MAX;
	int min_idx = -1;

	for (int i = 0; i < height; i++)
	{
		if (min_energy > cumul_energy_grid.at<double>(i, width - 1))
		{
			min_energy = cumul_energy_grid.at<double>(i, width - 1);
			min_idx = i;
		}
	}

	if (min_idx != -1)
	{
		int curr_pix_idx = min_idx;
		int small_pix_idx = min_idx;
		min_seam[width - 1] = curr_pix_idx;

		int bottom_up = width - 2;

		while (bottom_up >= 0)
		{
			int up_pix_idx = curr_pix_idx;
			int down_pix_idx = curr_pix_idx;

			if (curr_pix_idx > 0)
			{
				up_pix_idx--;
			}
			if (curr_pix_idx < height - 1)
			{
				down_pix_idx++;
			}
			/*
			Bottom Up Traversal to build seam (From Right to Left).
			*/
			if (cumul_energy_grid.at<double>(up_pix_idx, bottom_up) < cumul_energy_grid.at<double>(curr_pix_idx, bottom_up))
			{
				small_pix_idx = up_pix_idx;
			}
			if (cumul_energy_grid.at<double>(down_pix_idx, bottom_up) < cumul_energy_grid.at<double>(small_pix_idx, bottom_up))
			{
				small_pix_idx = down_pix_idx;
			}
			curr_pix_idx = small_pix_idx;
			min_seam[bottom_up] = curr_pix_idx;
			bottom_up--;
		}
	}

	return min_seam;
}

Mat remove_seam_vert(Mat in_img, int* min_seam)
{
	Mat out_img = Mat::zeros(in_img.rows, in_img.cols - 1, in_img.type());

	int height = in_img.rows;
	int width = in_img.cols;

	for (int y = 0; y < height; y++)
	{
		bool seam_found = false;

		for (int x = 0; x < width - 1; x++)
		{
			if (x == min_seam[y])
			{
				/*
				The moment a pixel belonging to minimum seam is found we skip copying that and start over-writing
				the subsequent pixels from there on. 
				*/
				out_img.at<Vec3b>(y, x) = in_img.at<Vec3b>(y, x + 1);
				seam_found = true;
			}
			else if (seam_found == false && x != min_seam[y])
			{
				out_img.at<Vec3b>(y, x) = in_img.at<Vec3b>(y, x);
			}
			else if (seam_found == true && x != min_seam[y])
			{
				out_img.at<Vec3b>(y, x) = in_img.at<Vec3b>(y, x + 1);
			}
		}
	}
	return out_img;
}

Mat remove_seam_hori(Mat in_img, int* min_seam)
{
	Mat out_img = Mat::zeros(in_img.rows - 1, in_img.cols, in_img.type());

	int height = in_img.rows;
	int width = in_img.cols;

	for (int y = 0; y < width; y++)
	{
		bool seam_found = false;

		for (int x = 0; x < height - 1; x++)
		{
			if (x == min_seam[y])
			{
				/*
				The moment a pixel belonging to minimum seam is found we skip copying that and start over-writing
				the subsequent pixels from there on.
				*/
				out_img.at<Vec3b>(x, y) = in_img.at<Vec3b>(x + 1, y);
				seam_found = true;
			}
			else if (seam_found == false && x != min_seam[y])
			{
				out_img.at<Vec3b>(x, y) = in_img.at<Vec3b>(x, y);
			}
			else if (seam_found == true && x != min_seam[y])
			{
				out_img.at<Vec3b>(x, y) = in_img.at<Vec3b>(x + 1, y);
			}
		}
	}
	return out_img;
}