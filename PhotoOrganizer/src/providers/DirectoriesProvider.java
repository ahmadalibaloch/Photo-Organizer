package providers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Stack;

import models.Directory;
import models.Photo;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DirectoriesProvider {

	public static final String APP_FOLDER = "organized";
	public static final String DEFAULT_SUB_DIR = "all";
	public static boolean internalStorage = true;

	public static Directory getAppDirectory() {
		internalStorage = checkExternalStorage() == 1 ? false : true;
		File path;
		//
		// default location will be in dcim/picprgonizer
		if (!internalStorage) {
			path = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
			return new Directory(path, APP_FOLDER);
		} else {
			path = Environment.getExternalStorageDirectory();
			// add app name folder
			return new Directory(path, APP_FOLDER);
		}

	}

	public static ArrayList<Directory> getPhotoDirectories(Context c,
			ArrayList<Directory> directories) {
		ArrayList<Directory> gallery = new ArrayList<Directory>();
		for (Directory dir : directories) {
			if (dir.getPhotos(c).size() > 0)
				gallery.add(dir);
		}
		return gallery;
	}

	public static ArrayList<Directory> getDirectoryTree(File dir) {
		ArrayList<Directory> directories = new ArrayList<Directory>();
		Stack<File> stack = new Stack<File>();
		stack.push(dir);
		while (!stack.isEmpty()) {
			File child = stack.pop();
			if (child.isDirectory()) {
				directories.add(new Directory(child));
				for (File f : child.listFiles())
					stack.push(f);

			}
		}
		return directories;
	}

	// method from android official documentation for loading thumnails
	public static Bitmap getThumb(String path, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, options);
	}

	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	public static Directory getDefaultPictureDirectory() {
		return new Directory(getAppDirectory(), "all");

	}

	public static Directory getRootDirectory() {
		return new Directory(getAppDirectory().path.getParentFile()
				.getParentFile().getParentFile());

	}

	public static String getDefaultImageName() {
		return new SimpleDateFormat("yyyyMMdd_hhmmss", Locale.US)
				.format(new Date()) + ".jpg";

	}

	public static String getDateString(Date date) {
		return new SimpleDateFormat("yyyyMMdd_hhmmss", Locale.US)
				.format(new Date());
	}

	public static File getDefaultImageFile() {
		return new File(getDefaultPictureDirectory() + "/"
				+ getDefaultImageName());
	}

	public static String getBaseFileName(String nameWithExt) {
		return nameWithExt.lastIndexOf(".") > 0 ? nameWithExt.substring(0,
				nameWithExt.lastIndexOf(".")) : nameWithExt;
	}

	static int checkExternalStorage() {
		// check media storage availablitiy
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		if (mExternalStorageAvailable && mExternalStorageWriteable)
			return 1;
		else if (mExternalStorageAvailable)
			return 0;
		else
			return -1;
	}

	public static boolean moveFile(File sourceFile, File destinationFilePath) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel in = null;
		FileChannel out = null;

		try {
			destinationFilePath.createNewFile();

			fis = new FileInputStream(sourceFile);
			fos = new FileOutputStream(destinationFilePath);
			in = fis.getChannel();
			out = fos.getChannel();

			long size = in.size();
			if (size == in.transferTo(0, size, out)) {
				 sourceFile.delete();
				return true;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (Throwable ignore) {
			}

			try {
				if (fos != null)
					fos.close();
			} catch (Throwable ignore) {
			}

			try {
				if (in != null && in.isOpen())
					in.close();
			} catch (Throwable ignore) {
			}

			try {
				if (out != null && out.isOpen())
					out.close();
			} catch (Throwable ignore) {
			}
		}
		return false;
	}

}
