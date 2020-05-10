package lol.cicco.ioc.core;

import java.util.*;

class DependGraph {

    private static class DependPoint {
        private CiccoModule<?> module; // 端点信息
        private int inDegree; // 入度

        static DependPoint of(CiccoModule<?> module) {
            DependPoint point = new DependPoint();
            point.module = module;
            return point;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DependPoint that = (DependPoint) o;

            return Objects.equals(module, that.module);
        }

        @Override
        public int hashCode() {
            return module != null ? module.hashCode() : 0;
        }
    }

    private static class DependPath {
        private DependPoint endPoint;

        static DependPath of(DependPoint point) {
            DependPath path = new DependPath();
            path.endPoint = point;
            return path;
        }
    }

    // 保存有向图
    private final Map<DependPoint, List<DependPath>> dependGraph = new LinkedHashMap<>();

    private final Map<String, CiccoModule<?>> registerModules;

    public DependGraph(Map<String, CiccoModule<?>> registerModules) {
        this.registerModules = registerModules;

        createGraph();
    }

    // 拓扑排序
    public List<CiccoModule<?>> sort() {
        List<CiccoModule<?>> sortList = new LinkedList<>();
        do {
            boolean getPoint = false;
            for(DependPoint point : dependGraph.keySet()) {
                if(point.inDegree == 0) {
                    sortList.add(point.module);
                    // 从图中删除
                    List<DependPath> allPath = dependGraph.remove(point);
                    for(DependPath path : allPath) {
                        path.endPoint.inDegree --; // 删除边后 入度 - 1
                    }
                    getPoint = true;
                    break;
                }
            }
            if(!getPoint) {
                // 说明有环
                throw new CiccoModuleException("循环依赖..请检查依赖情况..");
            }
        } while (!dependGraph.isEmpty());

        return sortList;
    }

    /**
     * 生成图
     */
    private void createGraph() {
        for(String moduleName : registerModules.keySet()) {
            CiccoModule<?> module = getModuleByName(moduleName);
            if(module.dependModule() == null || module.dependModule().isEmpty()) {
                putPoint(module);
            } else {
                for(String depend : module.dependModule()) {
                    putPath(getModuleByName(depend), module);
                }
            }
            if(module.afterModule() != null && !module.afterModule().isEmpty()) {
                for(String after : module.afterModule()) {
                    putPath(module, getModuleByName(after));
                }
            }
        }
    }

    // 添加图端点
    private DependPoint putPoint(CiccoModule<?> module) {
        DependPoint point = getPointByModule(module);
        if(point == null) {
            point = DependPoint.of(module);
            dependGraph.put(point, new LinkedList<>());
        }
        return point;
    }

    // 添加图路径
    private void putPath(CiccoModule<?> start, CiccoModule<?> end) {
        DependPoint startPoint = putPoint(start);
        DependPoint endPoint = putPoint(end);

        // 增加一条边
        dependGraph.get(startPoint).add(DependPath.of(endPoint));
        // 增加入度 出度就是path数量
        endPoint.inDegree += 1;
    }

    // 获得端点
    private DependPoint getPointByModule(CiccoModule<?> module) {
        for(DependPoint point : dependGraph.keySet()) {
            if(point.module.equals(module)) {
                return point;
            }
        }
        return null;
    }

    private CiccoModule<?> getModuleByName(String moduleName) {
        CiccoModule<?> module = registerModules.get(moduleName);
        if(module == null) {
            throw new CiccoModuleException("未找到依赖模块, 请检查模块是否注册至Context....");
        }
        return module;
    }
}
