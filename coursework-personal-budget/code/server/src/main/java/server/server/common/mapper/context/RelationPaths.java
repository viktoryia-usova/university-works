package server.server.common.mapper.context;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class RelationPaths {

    private final Set<String> paths;

    private RelationPaths(Set<String> paths) {
        this.paths = paths;
    }

    public static RelationPaths all() {
        return new RelationPaths(null);
    }

    public static RelationPaths none() {
        return new RelationPaths(new HashSet<>());
    }

    public static RelationPaths of(String... paths) {
        return new RelationPaths(new HashSet<>(Arrays.asList(paths)));
    }

    public boolean includes(String relation) {
        if (paths == null) return true;
        return paths.stream().anyMatch(p -> p.equals(relation) || p.startsWith(relation + "."));
    }

    public RelationPaths nested(String prefix) {
        if (paths == null) return this;
        Set<String> nestedPaths = paths.stream()
                .filter(p -> p.startsWith(prefix + "."))
                .map(p -> p.substring(prefix.length() + 1))
                .collect(Collectors.toSet());
        return new RelationPaths(nestedPaths);
    }
}
