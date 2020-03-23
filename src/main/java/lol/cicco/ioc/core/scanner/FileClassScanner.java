package lol.cicco.ioc.core.scanner;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class FileClassScanner implements BeanScanner {

    private static final FileClassScanner scanner = new FileClassScanner();

    private FileClassScanner(){}

    @Override
    public List<ClassMeta> doScan(URL url) {
        return addClassesFromFilePath(url.getPath());
    }

    public static BeanScanner getInstance() {
        return scanner;
    }

    private List<ClassMeta> addClassesFromFilePath(String path) {
        List<ClassMeta> classMetas = new LinkedList<>();

        File file = new File(path);
        File[] childFiles = file.listFiles();
        if (childFiles == null) {
            return classMetas;
        }
        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                // 搜索子目录
                classMetas.addAll(addClassesFromFilePath(childFile.getPath()));
            } else {
                String childFilePath = childFile.getPath();

                if (childFilePath.endsWith(ScannerConstants.CLASS_FILE_SUFFIX)) {
                    int classesStartIdx = 0;

                    String classDir = File.separator + "classes" + File.separator;
                    String testClassDir = File.separator + "test-classes" + File.separator;

                    int classesIdx = childFilePath.indexOf(classDir);
                    if (classesIdx != -1) {
                        classesStartIdx = classesIdx + classDir.length();
                    } else {
                        int testClassesIdx = childFilePath.indexOf(testClassDir);
                        if (testClassesIdx != -1) {
                            classesStartIdx = testClassesIdx + testClassDir.length();
                        }
                    }

                    childFilePath = childFilePath.substring(classesStartIdx, childFilePath.lastIndexOf(ScannerConstants.CLASS_FILE_SUFFIX));
                    childFilePath = childFilePath.replace(File.separator, ".");
                    classMetas.add(ClassMeta.of(childFilePath));
                }
            }
        }
        return classMetas;
    }
}
