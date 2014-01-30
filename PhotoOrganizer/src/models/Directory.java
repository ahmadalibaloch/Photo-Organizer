package models;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Stack;

import android.content.Context;

import providers.PhotosProvider;

public class Directory implements Comparable<Directory> {

	public File path;
	public String name;

	public Directory(String path) {
		File thisDir = new File(path);
		if (!thisDir.exists())
			thisDir.mkdirs();
		this.path = thisDir;
		this.name = this.path.getName();
	}
	public void delete(){
		deleteAll(this.path);
	}
	private void deleteAll(File dir) {

		Stack<File> stack = new Stack<File>();
		stack.push(dir);
		while (!stack.isEmpty()) {
			File child = stack.pop();
			if (child.isDirectory()) {

				for (File f : child.listFiles())
					stack.push(f);
			}
			child.delete();
		}
		while (dir.exists()) {
			deleteAll(dir);
			dir.delete();
		}
	}

	public Directory(File file) {
		this.path = file;
		if (!file.exists())
			file.mkdirs();
		this.name = this.path.getName();
	}

	public Directory(Directory parent, String name) {
		File thisDir = new File(parent.path, name);
		if (!thisDir.exists())
			thisDir.mkdirs();
		this.path = thisDir;
		this.name = this.path.getName();
	}

	public Directory(File parent, String name) {
		File thisDir = new File(parent, name);
		if (!thisDir.exists())
			thisDir.mkdirs();
		this.path = thisDir;
		this.name = this.path.getName();
	}

	public String getPath() {
		return path.getAbsolutePath();
	}

	public String getName() {
		return name;
	}

	public void setName(String str) {
		this.name = str;
	}

	public ArrayList<Directory> getSubDirectories() {
		ArrayList<Directory> directories = new ArrayList<Directory>();
		File[] files = new File(getPath()).listFiles();
		for (File aFile : files)
			if (aFile.isDirectory())
				directories.add(new Directory(aFile.getAbsolutePath()));
		return directories;
	}

	public ArrayList<File> getSubDirectoriesAsFiles() {
		ArrayList<File> directories = new ArrayList<File>();
		File[] files = new File(getPath()).listFiles();
		for (File aFile : files)
			if (aFile.isDirectory())
				directories.add(aFile);
		return directories;
	}

	public ArrayList<File> getAllFiles() {
		ArrayList<File> filesList = new ArrayList<File>();
		FileFilter filter = new FileFilter() {
			private String[] extension = { ".jpg", ".JPG", ".jpeg", ".bmp",
					".png", ".gif" };

			@Override
			public boolean accept(File pathname) {
				// if we are looking at a directory/file that's not hidden we
				// want to see it so return TRUE
				if ((pathname.isDirectory() || pathname.isFile())
						&& !pathname.isHidden()) {
					return true;
				}
				for (String ext : extension) {
					if (pathname.getName().toLowerCase(Locale.US).endsWith(ext)) {
						return true;
					}
				}
				return false;
			}
		};
		File[] files = new File(getPath()).listFiles(filter);
		for (File aFile : files)
			filesList.add(aFile);
		return filesList;
	}

	public ArrayList<Photo> getPhotos(Context context) {
		ArrayList<Photo> photos = new ArrayList<Photo>();
		FileFilter filter = new FileFilter() {
			private String[] extension = { ".jpg", ".JPG", ".jpeg", ".bmp",
					".png", ".gif" };

			@Override
			public boolean accept(File pathname) {
				// if we are looking at a directory/file that's not hidden we
				// want to see it so return TRUE
				if (pathname.isFile() && !pathname.isHidden()) {
					for (String ext : extension) {
						if (pathname.getName().toLowerCase(Locale.US)
								.endsWith(ext)) {
							return true;
						}
					}
				}

				return false;
			}
		};
		File[] files = new File(getPath()).listFiles(filter);
		for (File aFile : files) {
			Photo photo = PhotosProvider.getOne(context, aFile);
			if (photo != null)
				photos.add(photo);
		}
		return photos;
	}

	@Override
	public String toString() {
		return this.getPath();

	}

	public int getTotalFiles() {
		if (path.listFiles() != null)
			return path.listFiles().length;
		return -1;
	}

	@Override
	public int compareTo(Directory another) {
		if (this.getPath().equalsIgnoreCase(another.getPath()))
			return 0;
		return 1;
	}

	public boolean equals(Directory another) {
		if (this.getPath().equalsIgnoreCase(another.getPath()))
			return true;
		else
			return false;
	}
}
