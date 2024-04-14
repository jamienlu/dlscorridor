package cn.jamie.discorridor.spring.boot.autoconfigure.process;

import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.DISCORRIDOR_PREFIX;

/**
 * @author jamieLu
 * @create 2024-04-13
 */
@Data
@Slf4j
public class ApolloConfigChanged implements ApplicationContextAware {
    ApplicationContext applicationContext;
    @ApolloConfigChangeListener(value = {"application","provider.yml"}, interestedKeyPrefixes = DISCORRIDOR_PREFIX)
    private void onChange(ConfigChangeEvent changeEvent) {
        for (String key : changeEvent.changedKeys()) {
            ConfigChange change = changeEvent.getChange(key);
            log.info("found change - {}", change.toString());
        }
        this.applicationContext.publishEvent(new EnvironmentChangeEvent(changeEvent.changedKeys()));
    }

}
