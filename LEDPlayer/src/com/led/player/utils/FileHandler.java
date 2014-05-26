package com.led.player.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.util.Log;

/**
 * 文件的操作类。包括单个文件的复制和 文件夹的复制，文件夹删除。
 * 
 * @author 1231
 * @since 2013年12月11日 17:41:45
 */
public class FileHandler {
	private static boolean DEBUG = false;

	/**
	 * 参数1是拷贝源目录或文件，参数2是拷贝目的文件夹
	 * 
	 * @param sourceDir
	 * @param targetDir
	 * @throws IOException
	 */
	public static void copyDirectiory(String sourceDir, String targetDir)
			throws IOException {
		File sfile = new File(sourceDir);
		File tfile = new File(targetDir);
		// 如果目标位置不是根目录，那么就说明参数1 是个文件夹路径，先删除指定目录下的所有文件。
		if (tfile.exists() && !targetDir.equalsIgnoreCase("/mnt/sdcard")) {
			try {
				deletefile(targetDir);
				if (DEBUG)
					Log.i("LedProjectActivity", "%%%%%%%%%%delete file ");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (sfile.isDirectory() && (tfile.mkdirs())) {// || tfile.isDirectory()
														// 这个去掉 mark
			File[] files = (new File(sourceDir)).listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					File sourceFile = files[i];
					File targetFile = new File(
							new File(targetDir).getAbsolutePath()
									+ File.separator + files[i].getName());
					copyFile(sourceFile, targetFile);
					if (DEBUG)
						Log.i("LedService",
								"&&&&&&&&& isDirectory coping......");
				} else if (files[i].isDirectory()) {
					copyDirectiory(sourceDir + "/" + files[i].getName(),
							targetDir + "/" + files[i].getName());
				}
			}

		} else if (sfile.isFile()) {
			File sourceFile = sfile;
			File targetFile = new File(targetDir + "/" + sfile.getName());
			copyFile(sourceFile, targetFile);
			if (DEBUG)
				Log.i("LedService", "&&&&&&&&& isFile coping......");
		}
	}

	/**
	 * 复制 源文件 到 目标文件
	 * 
	 * @param sourceFile
	 * @param targetFile
	 * @throws IOException
	 */
	public static void copyFile(File sourceFile, File targetFile)
			throws IOException {
		FileInputStream input = new FileInputStream(sourceFile);
		BufferedInputStream inBuff = new BufferedInputStream(input);
		//
		FileOutputStream output = new FileOutputStream(targetFile);
		BufferedOutputStream outBuff = new BufferedOutputStream(output);
		//
		byte[] b = new byte[1024];
		int len;
		while ((len = inBuff.read(b)) != -1) {
			outBuff.write(b, 0, len);
		}
		//
		outBuff.flush();
		//
		inBuff.close();
		outBuff.close();
		output.close();
		input.close();

	}

	/**
	 * 删除指定目录下的所有文件。或者是删除一个文件
	 * 
	 * @param delpath
	 *            全路劲目录
	 * @return 删除结果
	 * @throws Exception
	 */
	public static boolean deletefile(String delpath) throws Exception {
		try {
			File file = new File(delpath);
			if (!file.isDirectory()) {
				file.delete();
			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File delfile = new File(delpath + File.separator
							+ filelist[i]);
					if (!delfile.isDirectory()) {
						delfile.delete();
					} else if (delfile.isDirectory()) {
						deletefile(delpath + File.separator + filelist[i]);
					}
				}
				file.delete();// 删除自己这个目录
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 *在另外一个线程中删除指定文件夹或文件 删除已经拷贝的文件，内部拷贝发生了错误，必须删除已经存在的。
	 * @param filePath  删除的目录
	 */
	public static void deleteFailFiles(final String filePath) {
		new Thread("deleteFailFileThread"){
			public void run() {
				casecadeDeleteDir(filePath);
			};
		}.start();
	}
	private static void casecadeDeleteDir(String filePath){
		File f = new File(filePath);
		if (f.isDirectory()==false) {
			f.delete();
			return;
		}
		File[] files = f.listFiles();
		if (files==null) {
			f.delete();
		}else {
			for (int i = 0; i < files.length; i++) {
				File tmp_file = files[i];
				if (tmp_file.isDirectory()) {
					casecadeDeleteDir(tmp_file.getAbsolutePath());
				}else {
					tmp_file.delete();
				}
			}
			f.delete();
		}
	}
}
