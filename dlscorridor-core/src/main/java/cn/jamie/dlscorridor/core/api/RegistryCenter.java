package cn.jamie.dlscorridor.core.api;

import java.util.List;

/**
 * @author jamieLu
 * @create 2024-03-17
 */
public interface RegistryCenter {
    void start();
    void stop();
    // provicer
    void register(String service,String instance);
    void unregister(String service,String instance);
    //consumer
    List<String> fectchAll(String service);
    void subscribe();

    class StaticRegistrtCenter implements RegistryCenter {
        List<String> providers;

        public StaticRegistrtCenter(List<String> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(String service, String instance) {

        }

        @Override
        public void unregister(String service, String instance) {

        }

        @Override
        public List<String> fectchAll(String service) {
            return providers;
        }

        @Override
        public void subscribe() {

        }
    }
}
