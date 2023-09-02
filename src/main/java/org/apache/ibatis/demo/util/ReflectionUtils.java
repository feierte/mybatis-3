package org.apache.ibatis.demo.util;

import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public abstract class ReflectionUtils {

  /**
   * 加载指定 package 下的所有 classes
   * @param packageName 指定 package 的名称
   * @param classLoader 加载 classes 使用的类加载器
   * @return 返回 classes 列表
   */
  public static List<Class<?>> findClassesInPackage(String packageName, ClassLoader classLoader) throws IOException {

    packageName = packageName.replace(".", "/");
    URL resource = classLoader.getResource(packageName);

    List<Class<?>> result = new ArrayList<>();
    List<File> files = FileUtils.findFilesInDir(resource.getFile());
    if (CollectionUtils.isNotEmpty(files)) {
      PackageClassLoader packageClassLoader = new PackageClassLoader(classLoader, files);
      result.addAll(packageClassLoader.loadAllClass());
    }
    return result;
  }

  public static List<Class<?>> findClassesInPackage(String packageName) throws IOException {
    return findClassesInPackage(packageName, Thread.currentThread().getContextClassLoader());
  }

  private static class PackageClassLoader extends ClassLoader {
    private List<File> classFiles;

    public PackageClassLoader(ClassLoader parent, List<File> classFiles) {
      super(parent);
      this.classFiles = classFiles;
    }

    public List<Class<?>> loadAllClass() throws IOException {
      List<Class<?>> result = new ArrayList<>();
      List<File> classFiles = this.classFiles;
      if (CollectionUtils.isNotEmpty(classFiles)) {
        for (File classFile : classFiles) {
          byte[] bytes = Files.readAllBytes(classFile.toPath());
          result.add(defineClass(null, bytes, 0, bytes.length));
        }
      }
      return result;
    }
  }
}
