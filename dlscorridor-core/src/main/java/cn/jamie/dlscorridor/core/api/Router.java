package cn.jamie.dlscorridor.core.api;

import java.util.List;

/**
 * @author jamieLu
 * @create 2024-03-17
 */
public interface Router {
    <T> List<T> router(List<T> providers);

    Router Default = new Router() {
        @Override
        public <T> List<T> router(List<T> providers) {
            return providers;
        }
    };
}
