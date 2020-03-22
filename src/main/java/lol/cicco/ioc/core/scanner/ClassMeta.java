package lol.cicco.ioc.core.scanner;

import lombok.Data;

@Data
public class ClassMeta {
    /**
     * 类名称
     */
    private String className;

    public static ClassMeta of(String className) {
        ClassMeta meta = new ClassMeta();
        meta.className = className;
        return meta;
    }
}
