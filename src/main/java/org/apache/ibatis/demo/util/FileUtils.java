package org.apache.ibatis.demo.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class FileUtils {

  public static List<File> findFilesInDir(String dirName) {
    File dir = new File(dirName);
    if (!dir.isDirectory()) {
      throw new IllegalArgumentException(dirName + " is not a directory.");
    }

    List<File> fileList = new ArrayList<>();

    File[] files = dir.listFiles();
    for (File file : files) {
      if (file.isDirectory()) {
        fileList.addAll(findFilesInDir(file.getAbsolutePath()));
      } else {
        fileList.add(file);
      }
    }
    return fileList;
  }

//  public static List<File> findFilesInDir(String dirName) {
//    File dir = new File(dirName);
//    if (!dir.isDirectory()) {
//      throw new IllegalArgumentException(dirName + " is not a directory.");
//    }
//
//    List<File> fileList = new ArrayList<>();
//    File[] files = dir.listFiles(file -> {
//      if (file.isDirectory()) {
//        findFilesInDir(file.getAbsolutePath());
//        return false;
//      }
//      return true;
//    });
//    if (files != null) {
//      fileList.addAll(List.of(files));
//    }
//    return fileList;
//  }
}
