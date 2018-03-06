package com.sekorm.util;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;

public class ZipAndRarUtils {

	/**
	 * 解压zip格式压缩包 对应的是ant.jar
	 */
	@SuppressWarnings("unused")
	private static void unZip(String sourceZip, String destDir)
			throws Exception {
		try {
			Project p = new Project();
			Expand e = new Expand();
			e.setProject(p);
			e.setSrc(new File(sourceZip));
			e.setOverwrite(false);
			e.setDest(new File(destDir));
			/*
			 * ant下的zip工具默认压缩编码为UTF-8编码， 而winRAR软件压缩是用的windows默认的GBK或者GB2312编码
			 * 所以解压缩时要制定编码格式
			 */
			e.setEncoding("gbk");
			e.execute();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 解压rar格式压缩包。
	 */
	private static void unRar(String srcRarPath, String dstDirectoryPath) {
		if (!srcRarPath.toLowerCase().endsWith(".rar")) {
			System.out.println("非rar文件！");
			return;
		}
		File dstDiretory = new File(dstDirectoryPath);
		if (!dstDiretory.exists()) {// 目标目录不存在时，创建该文件夹
			dstDiretory.mkdirs();
		}
		Archive a = null;
		try {
			a = new Archive(new File(srcRarPath));
			if (a != null) {
				FileHeader fh = a.nextFileHeader();
				while (fh != null) {
					// 防止文件名中文乱码问题的处理
					String fileName = fh.getFileNameW().isEmpty() ? fh
							.getFileNameString() : fh.getFileNameW();
					if (fh.isDirectory()) { // 文件夹
						File fol = new File(dstDirectoryPath + File.separator
								+ fileName);
						fol.mkdirs();
					} else { // 文件
						File out = new File(dstDirectoryPath + File.separator
								+ fileName.trim());
						try {
							if (!out.exists()) {
								if (!out.getParentFile().exists()) {// 相对路径可能多级，可能需要创建父目录.
									out.getParentFile().mkdirs();
								}
								out.createNewFile();
							}
							FileOutputStream os = new FileOutputStream(out);
							a.extractFile(fh, os);
							os.close();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
					fh = a.nextFileHeader();
				}
				a.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除文件
	 */
	private static void delete(String sourceFile) {
		File file = new File(sourceFile);
		file.delete();
	}

	/**
	 * 压缩 zip
	 * 
	 * @param srcPathname
	 *            压缩文件路径
	 * @param zipFilepath
	 *            压缩包路径
	 * @throws BuildException
	 * @throws RuntimeException
	 */
	private static void zip(String zipFilepath, String srcPathName) {
		File srcdir = new File(srcPathName);
		File zipFile = new File(zipFilepath);
		if (!srcdir.exists()) {
			throw new RuntimeException(srcPathName + "不存在！");
		}
		Project prj = new Project();
		Zip zip = new Zip();
		zip.setProject(prj);
		zip.setDestFile(zipFile);
		FileSet fileSet = new FileSet();
		fileSet.setProject(prj);
		fileSet.setDir(srcdir);
		zip.addFileset(fileSet);
		zip.execute();
	}

	/**
	 * 删除所有文件
	 * 
	 * @param path
	 * @return
	 */
	public static boolean delAllFile(String path) {
		File file = new File(path);
		if (!file.isDirectory()) {
			file.delete();
		} else if (file.isDirectory()) {
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				File delfile = fileList[i];
				if (!delfile.isDirectory()) {
					delfile.delete();
				} else if (delfile.isDirectory()) {
					delAllFile(fileList[i].getPath());
				}
			}
			file.delete();
		}
		return true;
	}

	/**
	 * 解密压缩包
	 */
	public static void decryptionZipAndRar(String sourceFile, String destDir)
			throws Exception {
		destDir=destDir+File.separator+
				sourceFile.substring(sourceFile.lastIndexOf(File.separator)+1,
						sourceFile.lastIndexOf("."))+
						File.separator;
		// 保证文件夹路径最后是"/"或者"\"
		char lastChar = destDir.charAt(destDir.length() - 1);
		if (lastChar != '/' && lastChar != '\\') {
			destDir += File.separator;
		}
		String type = sourceFile.substring(sourceFile.lastIndexOf(".") + 1);
		if (type.equals("zip")) {
			ZipAndRarUtils.unZip(sourceFile, destDir);
		} else if (type.equals("rar")) {
			ZipAndRarUtils.unRar(sourceFile, destDir);
		} else {
			throw new Exception("只支持zip和rar格式的压缩包！");
		}
		// 删除旧的压缩包
		delete(sourceFile);
		//线程休眠2s等待解密
		Thread.sleep(2000);
		//压缩所有文件
		zip(sourceFile.substring(0, sourceFile.lastIndexOf(".")+1)+"zip", destDir);
		//删除文件夹
		delAllFile(destDir);
	}
	
	/*public static void main(String[] args) throws Exception {
		decryptionZipAndRar("c:\\Outlook_a\\memcached全面剖析.rar", "c:\\Outlook_a\\");
	}*/
}
